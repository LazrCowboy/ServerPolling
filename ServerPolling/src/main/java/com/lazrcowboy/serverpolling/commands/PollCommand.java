package com.lazrcowboy.serverpolling.commands;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lazrcowboy.serverpolling.Poll;
import com.lazrcowboy.serverpolling.util.FileUtil;
import com.lazrcowboy.serverpolling.util.MessageUtil;

public class PollCommand implements CommandExecutor
{

    /**Poll Command allows for staff to create polls for the server and for the server to vote on the poll
     * Also saves the poll to a file for future reference (If needed)
     *
     * @author Kurt (LazrCowboy)
     * @version 1.0.0
     * @since 2-3-16
     */
	
	//Help String arrays
    String[] staffHelp = {
        ChatColor.DARK_RED + " * * * * * STAFF HELP SCREEN * * * * *" ,
        ChatColor.BLUE      + " | /poll create [topic of poll]  Used to create a poll",
        ChatColor.DARK_AQUA + " | /poll add [choice].  Adds a choice to the poll.  Max 6",
        ChatColor.BLUE      + " | /poll start [time]. Sends the poll to players.  Time is in seconds",
        ChatColor.DARK_AQUA + " | /poll reset.  Resets the current poll before it is started.",
        ChatColor.BLUE      + " | /poll help.  Access the help screen",
        ChatColor.DARK_AQUA + " | Staff are not permitted to vote on polls to preserve unskewed results"
    };
    String[] userHelp = {
        ChatColor.DARK_RED + " * * * * * PLAYER HELP SCREEN * * * * *" ,
        ChatColor.BLUE      + " | /poll {1,2,3,4,5,6}.  Vote on a poll choice.  You can only vote once.",
        ChatColor.DARK_AQUA + " | /poll help.  Access the help screen",
        ChatColor.BLUE      + " | All polls are completely anonymous.  No player names are ever recorded",
        //ChatColor.DARK_BLUE + " | ",
        //ChatColor.BLUE + " | ",
        //ChatColor.DARK_BLUE + " | "
    };

    Poll poll = null;
    
    public PollCommand(){
    	poll = null;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        
        if (!(sender instanceof Player)){
        	MessageUtil.send("Poll can only be started by an in game player", sender);
            return false;
        } else {
        	
        	final Player player = (Player) sender;
        	
            if(player.hasPermission("serverpolling.createpoll")){
            	
                //Commands for staff*************************************************************************************
                if(args.length == 0){

                    if(poll == null){
                        MessageUtil.send(staffHelp, player);
                    } else {
                    	if (poll.isActive()){
                    		MessageUtil.send(poll.toStringArray(), player);
                    	} else {
                    		MessageUtil.send(staffHelp, player);
                    	}
                    }
                    
                } else if (args.length == 1){
                    switch (args[0]) {
                            /*Brings up the help message for staff
                             1) create, start and add are not valid commands without more arguments
                             2) reset calls the reset method and resets the poll
                             3) staff cannot vote in polls to prevent skewed data
                             4) /poll [number] does not add choices to a poll
                             5) default tells poll creator that the command is not valid
                             */
                        case "help" : MessageUtil.send(staffHelp, player);
                            break;
                        case "create" : MessageUtil.sendError("Poll must be created with a name", player);
                            break;
                        case "start" : MessageUtil.sendError("Poll must be started with a time limit", player);
                            break;
                        case "reset" : poll = null;
                            MessageUtil.send("Poll has been reset", player);
                            break;
                        case "add" : case "addchoice" : MessageUtil.sendError("Adding a choice needs a choice name", player);
                            break;
                        case "1" : case "2" : case "3" : case "4" : case "5" : case "6" :
                        	if (poll != null) {
                        		 if (poll.isActive() == true){
                        			 MessageUtil.sendError("You are not permitted to vote in a poll", player);
                        		 } else {
                        			 MessageUtil.sendError("Use /poll add to add choices instead", player);
                        		 }
                        	} else {
                        		MessageUtil.sendError("Poll not created yet", player);
                        	}
                           
                            break;
                        default : MessageUtil.sendInvalidCommand(args, player);
                            break;
                    }
                } else if (args.length == 2){
                    switch (args[0]){
                        case "1" : case "2" : case "3" : case "4" : case "5" : case "6" :
                            MessageUtil.send("Use /poll add to add choices instead", player);
                            break;
                        case "create" : 
                        	if (poll != null){
                                MessageUtil.send("A poll was already created.  Use /poll reset to reset the poll", player);
                            } else {
                            	String name = "";
                            	Server server = player.getServer();
                            	for(int i = 1; i < args.length; i++){
                            		name += args[i];
                            	}
                            	
                                poll = new Poll(name, server);
                                
                                MessageUtil.send("A poll has been created with name \"" + name + "\"", player);
                            }
                            break;
                        case "start" : 
                        	if (poll.isActive()){
                                MessageUtil.sendError("There is already an active poll.", player);
                            } else {
                                if (poll.validateSize(2)){
                                    try {
                                    	poll.setActive();
                                    	
                                        int time = Integer.parseInt(args[1]) * 1000;
                                        
                                        if (time < 60) {
                                        	MessageUtil.sendError("Time must be between 60 and 240", sender);
                                        } else {
                                        	MessageUtil.sendServerRaw(ChatColor.DARK_RED + "••••••••••" + ChatColor.RED + "ATTENTION: A POLL IS STARTING" + ChatColor.DARK_RED + "••••••••••", poll.getServer());
                                            MessageUtil.sendServerRaw(MessageUtil.getPluginMessage() + ChatColor.AQUA + poll.getName(), poll.getServer());
                                            MessageUtil.sendServerRaw(poll.getArrayToChat(), poll.getServer());
                                            
                                            Timer t = new Timer();
                                            t.schedule(new TimerTask() {
                                                    @Override
                                                    public void run() {
                                                        
                                                        MessageUtil.sendServerRaw(MessageUtil.getPluginMessage() + ChatColor.YELLOW + "•••••••••<" + ChatColor.GOLD + "RESULTS TO THE POLL" + ChatColor.YELLOW + ">•••••••••", poll.getServer());
                                                        MessageUtil.sendServerRaw(MessageUtil.getPluginMessage() + ChatColor.AQUA + poll.getName(), poll.getServer());
                                                        MessageUtil.sendServerRaw(poll.getResultsArray(), poll.getServer());
                                                        
                                                        Date now = new Date();
                                                        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                                        String fileWrite = "/n/n" + "[" + format.format(now) + "]";
                                                        
                                                        File file = new File("plugins/ServerPolling/poll/records.txt");
                                                        
                                                        FileUtil.writeFile(file, fileWrite);
                                                        FileUtil.writeFile(file, poll.getResultsArray());
                                                        
                                                        poll = null;
                                                    }
                                                }, time);
                                        }
                                    } catch (NumberFormatException ex){
                                        MessageUtil.sendError(args[1] + " is not a proper time", player);
                                    }
                                } else {
                                    //Tells the user they need more choices
                                    MessageUtil.sendError("A poll must have more than 2 choices", player);
                                }
                            }
                            break;
                        case "add" : case "addchoice" : 
                            if (poll != null){
                                
                                if (poll.isActive() == false){
                                    
                                    if (poll.getChoiceSize() >= 6){
                                        MessageUtil.send("Poll already has 6 choices", player);
                                    } else {
                                        poll.addChoice(args[1]);
                                        MessageUtil.send("\"" + args[1] + "\" was added to poll", player);
                                    }
                                } else {
                                    MessageUtil.sendError("You can not add choices after a plugin is started", player);
                                }
                            } else {
                                MessageUtil.sendError("You can not add choices when a poll is not created", player);
                            }
                            break;
                        default : MessageUtil.sendInvalidCommand(args, player);
                            break;
                    }
                } else if (args.length > 2){
                	switch (args[0]){
                    	case "create" : 
                    		if (poll != null){
                    			MessageUtil.send("A poll was already created.  Use /poll reset to reset the poll", player);
                        	} else {
                        		String name = "";
                        		Server server = player.getServer();
                        		for(int i = 1; i < args.length; i++){
                        			name += args[i] + " ";
                        		}
                        	
                        		poll = new Poll(name, server);
                            
                        		MessageUtil.send("A poll has been created with name \"" + name + "\"", player);
                        	}
                    		break;
                    	case "add" : case "addchoice" : 
                            if (poll != null){
                                if (poll.isActive() == false){
                                    if (poll.getChoiceSize() > 6){
                                        MessageUtil.send("Poll already has 6 choices", player);
                                    } else {
                                    	String name = "";
                                    	for(int i = 1; i < args.length; i++){
                                			name += args[i] + " ";
                                		}
                                    	
                                        poll.addChoice(name);
                                        MessageUtil.send("\"" + name + "\" was added to poll", player);
                                    }
                                } else {
                                    MessageUtil.sendError("You can not add choices after a plugin is started", player);
                                }
                            } else {
                                MessageUtil.sendError("You can not add choices when a poll is not created", player);
                            }
                            break;
                    	default: MessageUtil.sendInvalidCommand(args, player);
                	}
                }
             } else {
                //Commands for server users *************************************************************************************
                if (args.length == 0){
                    if (poll != null){
                    	if (poll.isActive() == true){
                    		MessageUtil.send(poll.toString(), player);
                    	} else {
                    		MessageUtil.send(userHelp, player);
                    	}
                    } else {
                    	MessageUtil.send(userHelp, player);
                    }
               } else if (args.length == 1){
            	   
            	   if (poll != null){
            		   if(poll.isActive() == true){

                             /*If the poll is active, if a player types /poll [number] it will...
                             1) Check if the player already voted
                             2) If the vote is a valid vote (not outside the range of choices)
                             3) Then add the player to the players voted list and add their vote
                             */

                            switch (args[0]){
                                case "1" : checkPlayerVote(player, 1);
                                    break;
                                case "2" : checkPlayerVote(player, 2);
                                    break;
                                case "3" : checkPlayerVote(player, 3);
                                    break;
                                case "4" : checkPlayerVote(player, 4);
                                    break;
                                case "5" : checkPlayerVote(player, 5);
                                    break;
                                case "6" : checkPlayerVote(player, 6);
                                    break;
                                case "help" : MessageUtil.send(userHelp, player);
                                    break;
                                default : MessageUtil.sendInvalidCommand(args, player);
                                    break;
                            }
            		   }
            	   }else {
                        switch(args[0]){
                            case "1" : case "2" : case "3" : case "4" : case "5" : case "6" :
                                MessageUtil.send("There is not active poll right now", player);
                                break;
                            case "help" : MessageUtil.send(userHelp, player);
                                break;
                            default : MessageUtil.sendInvalidCommand(args, player);
                        }
                    }
                } else if (args.length > 1){
                    MessageUtil.sendInvalidCommand(args, player);
                }
            }
        }
        return true;
    }
  
    public void checkPlayerVote(Player player, int i){
    	if(!(poll.getVotingPlayers().contains(player))){
            if(poll.getChoiceSize() > i){
                MessageUtil.sendConfirmVote(i, player);
                poll.addVote(player, i - 1);
            } else {
                MessageUtil.sendError(i + " is not a valid vote", player);
            }
        } else {
        	MessageUtil.sendDenyVote(player);
        }
    }
}