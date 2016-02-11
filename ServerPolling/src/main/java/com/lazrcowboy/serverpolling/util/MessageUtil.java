package com.lazrcowboy.serverpolling.util;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageUtil {
	
	/** A general class for any LazrCowboy plugin for chat related functions
	*
	* @author Kurt (LazrCowboy)
	* @version 1.0.0 ServerPolling
	* @since 2-3-16
	*/
	
	private static String pluginString = ChatColor.DARK_GREEN + "[" + ChatColor.AQUA + "POLL" + ChatColor.DARK_GREEN + "]"  + " • ";
    
//*********************************************SERVER LOGGING**************************************************
	public static void logMessage(String message){
		log(message, Level.INFO);
	}
	
	public static void logError(String message){
		log(message, Level.SEVERE);
	}
	
	public static void log(String message, Level level){
		Bukkit.getLogger().log(level, message);
	}
	
//*********************************************INDIVIDUAL PLAYER MESSAGING**************************************
	public static void send(String message, CommandSender sender){
		sender.sendMessage(pluginString + ChatColor.GOLD + message);
	}
	
	public static void send(String[] message, CommandSender sender){
		for(String s: message){
			send(s, sender);
		}
	}
	
	public static void sendError(String message, CommandSender sender){
		sender.sendMessage(pluginString + ChatColor.DARK_RED + "ERR: " + ChatColor.RED + message);
	}

	public static void sendInvalidCommand(String[] args, CommandSender sender) {
		String command = "/poll ";
		
		for(String s: args){
			command += s + " ";
		}
		
		sendError(command + "is not a Valid Command", sender);
	}

	public static String getPluginMessage() {
		return pluginString;
	}

//*****************************************************SEND WHOLE SERVER*****************************************************
	public static void sendServer(String string, Server server) {
		for (Player sendPlayer : server.getOnlinePlayers()){
            sendPlayer.sendMessage(string);
        }
	}

	public static void sendServer(String[] stringArray, Server server) {
		for(String s: stringArray){
			sendServer(s, server);
		}
	}
	
	public static void sendServerRaw(String string, Server server) {
		for (Player sendPlayer : server.getOnlinePlayers()){
            sendPlayer.sendMessage(string);
        }
	}

	public static void sendServerRaw(String[] stringArray, Server server) {
		for(String s: stringArray){
			sendServerRaw(s, server);
		}
	}
	
//*************************************************PLUGIN SPECIFIC*********************************************************
	public static void sendConfirmVote(int i, CommandSender sender) {
		sender.sendMessage(pluginString + ChatColor.RED + "You have voted for choice " + ChatColor.BOLD + i);
	}

	public static void sendDenyVote(CommandSender sender) {
		sender.sendMessage(pluginString + ChatColor.RED + "You have already voted in this poll");
	}
}
