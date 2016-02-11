package com.lazrcowboy.serverpolling.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {
	
	/** General File utility class for all LazrCowboy plugins
	*
	* @author Kurt (LazrCowboy)
	* @version 1.0.0
	* @since 2-3-16
	*/
	
	public static boolean writeFile(File file, String[] string){
		
		boolean success = true;
		
		for(String s: string){
			success = writeFile(file, s);
		}
		
		return success;
	}
	
	public static boolean writeFile(File file, String string){
		
		boolean success = true;
		
		try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file, true));
            out.write(string);
            out.close();
        } catch (IOException e) {
        	success = false;
            e.printStackTrace();
        }
		
		return success;
	}
}
