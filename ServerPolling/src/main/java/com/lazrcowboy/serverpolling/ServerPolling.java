package com.lazrcowboy.serverpolling;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.bukkit.plugin.java.JavaPlugin;

import com.lazrcowboy.serverpolling.commands.PollCommand;
import com.lazrcowboy.serverpolling.util.MessageUtil;

public final class ServerPolling extends JavaPlugin {
	
	/**ServerPolling allows staff (Or people with the specific permission) to Poll the entire server
	* Also saves the poll to a file for future reference (If needed)
	*
	* @author Kurt (LazrCowboy)
	* @version 1.0.3
	* @since 2-3-16
	*/
	
	@Override
	public void onEnable(){
		
		MessageUtil.logMessage("Plugin Activated");
		MessageUtil.logMessage("Coded by LazrCowboy");
		
		this.getCommand("poll").setExecutor(new PollCommand());
		
		if (!setupRecordsFile()){
			MessageUtil.logMessage("Records File not set up");
		}
	}
	
	@Override
	public void onDisable(){
		MessageUtil.logMessage("Plugin Deactivated");
	}
	
	private boolean setupRecordsFile(){
		
		boolean successfulSetup = true;
		
		File file = new File("plugins/ServerPolling/poll/records.txt");
        String fileStart = "[ServerPolling] Records for all polls. /n";
        
		if (!file.exists()){
            try {
                BufferedWriter out = new BufferedWriter(new FileWriter("plugins/ServerPolling/poll/records.txt"));
                out.write(fileStart);
                out.close();
            } catch (IOException e) {
            	successfulSetup = false;
                e.printStackTrace();
            }
        }
		
		return successfulSetup;
	}

}
