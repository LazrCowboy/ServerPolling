package com.lazrcowboy.serverpolling;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public class Poll {
	
	/**Poll Object for creating server polls
	*
	* @author Kurt (LazrCowboy)
	* @version 1.0.0
	* @since 2-3-16
	*/
	
	String _name;
	boolean _active;
	Server _server;
	ArrayList<Integer> _votes;
	ArrayList<String> _choices;
	ArrayList<Player> _votingPlayers;
	
	public Poll(String name, Server server){
		_name = name;
		_active = false;
		_server = server;
		_votes = new ArrayList<Integer>();
		_choices = new ArrayList<String>();
		_votingPlayers = new ArrayList<Player>();
		
		_votes.add(0);
		_votes.add(0);
		_votes.add(0);
		_votes.add(0);
		_votes.add(0);
		_votes.add(0);
	}
	
	public String[] toStringArray(){
		
		String[] array = new String[_choices.size()];
		
		for (int i = 0; i < _choices.size(); i++){
			array[i] = _choices.get(i);
		}
		
		return array;
	}
	
	public boolean isActive() {
		return _active;
	}

	public boolean validateSize(int i) {
		
		boolean validate = false;
		int testSize = 0;
		
		for (String s: _choices){
			if(s != null){
				testSize++;
			}
		}
		
		if (testSize >= i){
			validate = true;
		}
		
		return validate;
	}

	public Server getServer() {
		return _server;
	}
	
	public int getChoiceSize() {
		return _choices.size();
	}

	public void addChoice(String string) {
		_choices.add(string);
	}

	public ArrayList<Player> getVotingPlayers() {
		return _votingPlayers;
	}

	public void addVote(Player player, int i) {
		
		_votingPlayers.add(player);
		
		_votes.set(i, _votes.get(i)+1);
	}

	public String[] getResultsArray() {
		String[] array = toStringArray();
		
		for(int i = 0; i < array.length; i++){
			array[i] = ChatColor.BLUE + String.valueOf(i + 1) + ChatColor.DARK_BLUE + ")  - " + ChatColor.AQUA + _votes.get(i) + ChatColor.DARK_BLUE + " -   " + ChatColor.GOLD + array[i];
		}
		
		return array;
	}
	
	public String[] getArrayToChat() {
		String[] array = toStringArray();
		
		for(int i = 0; i < array.length; i++){
			array[i] = ChatColor.BLUE + String.valueOf(i + 1) + ChatColor.DARK_BLUE + ") - " + ChatColor.GOLD + array[i];
		}
		
		return array;
	}

	public void setActive() {
		_active = true;
	}

	public String getName() {
		return _name;
	}

}
