package it.TetrisReich.bot.TestBot;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
/*import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;*/
import java.net.ConnectException;


public class Startup {
	 public static boolean startup() throws IOException {
	    	App.logger("Starting up");
	    	if(apiToken()&&apiKey()&&url()&&chat()&&botName()) {/*Questa cosa orribile.*/} else return false;
	    	admin(); all(); cLast(); idd(); kronos();
	    	if(!App.skipDefaultDirectory) App.dir = "/home/TelegramBot/YoutubeBot/" + App.channel + "/";
	    	App.logger("\nTelegram channel: " + App.channel + "\n Telegram api: " + App.api);
	    	File f = new File("text");
		    if(f.exists() && !f.isDirectory()) { 
		    	App.logger("File \"text\" loaded successfully.");
		    } else {
		    	App.logger("File \"text\" not found.");
		    	App.textEsist = false;
		    }   
	    	if(App.textEsist) Chan.chanSP();
	    	App.logger("TextEsist: " + App.textEsist);
	    	return true;
	    }
	    public static void idd(){
	    	File f = new File("id");
	    	if(f.exists() && !f.isDirectory()) { 
	    		FileO.writer(App.getInfo(0), "id");
	    		App.logger("File \"id\" loaded successfully.");
	    	} else {
	    		App.logger("File \"id\" not found. Creating it.");
	    		FileO.newFile("id");
	    		App.logger("File \"id\" created and loaded successfully.");
	    	}
	    }
	    public static void all(){
	    	File f = new File("all");
	    	if(f.exists() && !f.isDirectory()) {
	    		String s = "";
	    		do{
	    			s = App.getInfo(1);
	    		}while(s==null);
	    		FileO.writer(s, "all");
	    		App.logger("File \"all\" loaded successfully.");
	    	} else {
	    		App.logger("File \"all\" not found. Creating it.");
	    		FileO.newFile("all");
	    		FileO.writer(App.getInfo(1), "all");
	    		App.logger("File \"all\" created and loaded successfully.");
	    	} 	
	    }
	    public static boolean url() throws IOException{
	    	File f = new File("channelID");
	    	if(f.exists() && !f.isDirectory()) { 
	    		App.api = "https://www.googleapis.com/youtube/v3/search?part=snippet&channelId="+
	        		(FileO.reader("channelID"))+
	    			"&order=date&key=" + App.key + "&maxResults=";
	    		App.logger("File \"channelID\" loaded successfully.");
	    	} else {
	    		App.logger("Fail to load the file \"channelID\"");
	    		return false;
	    	}
	    	return true;
	    }
	    public static boolean chat() throws IOException{
	    	File f = new File("chat");
	    	if(f.exists() && !f.isDirectory()) { 
	    		App.channel = FileO.reader("chat");
	    		App.logger("File \"chat\" loaded successfully.");
	    	} else {
	    		App.logger("Fail to load the file \"chat\"");
	    		return false;
	    	}
	    	return true;
	    }
	    public static void cLast(){
	    	File f = new File("Last");
	    	if(f.exists() && !f.isDirectory()) { 
	    		App.logger("File \"Last\" loaded successfully.");
	    	} else {
	    		App.logger("File \"Last\" not found. Creating it.");
	    		FileO.newFile("Last");
	    		FileO.writer("---;123", "Last");
	    		App.logger("File \"Last\" created and loaded successfully.");
	    	} 	
	    }
	    public static boolean apiToken() throws IOException{
	    	File f = new File("token");
	    	if(f.exists() && !f.isDirectory()) { 
	    		//String s = FileO.reader("token");
	    		App.token = FileO.reader("token");
	    		App.logger("File \"token\" loaded successfully.");
	    	} else {
	    		App.logger("Fail to load the file \"token\"");
	    		return false;
	    	}
	    	return true;
	    }
	    public static boolean apiKey() throws IOException{
	    	File f = new File("key");
	    	if(f.exists() && !f.isDirectory()) { 
	    		App.key = FileO.reader("key");
	    		App.logger("File \"key\" loaded successfully.");
	    	} else {
	    		App.logger("Fail to load the file \"key\"");
	    		return false;
	    	}
	    	return true;
	    }
	    public static boolean botName() throws IOException{
	    	File f = new File("name");
	    	if(f.exists() && !f.isDirectory()) { 
	    		App.botName = FileO.reader("name");
	    		App.logger("File \"name\" loaded successfully.");
	    	} else {
	    		App.logger("Fail to load the file \"name\"");
	    		return false;
	    	}
	    	return true;
	    }
	    public static void kronos(){
	    	File f = new File("kronos");
	    	if(f.exists() && !f.isDirectory()) { 
	    		App.logger("File \"kronos\" loaded successfully.");
	    	} else {
	    		App.logger("File \"kronos\" not found. Creating it.");
	    		FileO.newFile("kronos");
	    		App.logger("File \"kronos\" created and loaded successfully.");
	    	}
	    }
	    public static void admin(){
	    	File f = new File("admin");
	    	if(f.exists() && !f.isDirectory()) { 
	    		App.logger("File \"admin\" loaded successfully.");
	    	} else {
	    		App.logger("File \"admin\" not found. Creating it.");
	    		FileO.newFile("admin");
	    		App.logger("File \"admin\" created and loaded successfully.");
	    	}
	    }
	    public static boolean check() throws ConnectException, InvocationTargetException {
	    	App.loggerL("Check if this chat is allowed: ");
	    	App.secret = true;
	    	String[] result = Download.dwn("http://stranckutilies.altervista.org/allowed").split(";");
	    	App.secret = false;
	    	for (int x=0; x<result.length; x++) if(result[x].equalsIgnoreCase(App.channel)){
	    		App.logger("TRUE");
	    		return false;
	    	}
    		App.logger("FALSE");
	    	return true;
	    }
}
