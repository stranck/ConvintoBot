package reloaded.convintobot;

import java.io.File;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class Settings {
	private long startTime;
	private boolean phraseStatus, useBotName = false;
	private String gToken, tToken, id, chat, botName, user, dir = "";
	private ArrayList<String> admins = new ArrayList<String>();
	public boolean loadSettings(long sTime){
		try {
			if(!FileO.exist("config.json")){
				Main.logger("File \"config.json\" not found.");
				FileO.newFile("config.json");
				FileO.writer("{", "config.json");
				FileO.addWrite("config.json", "    \"gToken\" : \"INSERIT YOUR GOOGLE APIS TOKEN HERE\",");
				FileO.addWrite("config.json", "    \"tToken\" : \"INSERIT YOUR TELEGRAM TOKEN HERE\",");
				FileO.addWrite("config.json", "    \"id\" : \"INSERIT YOUR CHANNEL ID HERE\",");
				FileO.addWrite("config.json", "    \"chat\" : \"INSERIT THE CHAT ID HERE\",");
				FileO.addWrite("config.json", "    \"botName\" : \"INSERIT THE NAME OF YOUR BOT HERE\",");
				FileO.addWrite("config.json", "    \"admins\" : [");
				FileO.addWrite("config.json", "        \"INSERIT THE TELEGRAM ACCOUNT ID OF YOUR FIRST ADMIN HERE\",");
				FileO.addWrite("config.json", "        \"INSERIT THE TELEGRAM ACCOUNT ID OF YOUR SECOND ADMIN HERE\",");
				FileO.addWrite("config.json", "        \"...\"");
				FileO.addWrite("config.json", "    ]");
				FileO.addWrite("config.json", "}");
				Main.logger("File \"config.json\" created. Edit your settings and then turn on again the bot.");
				return false;
			}
			
			Main.loggerL("Loading configs... ");
			JSONObject config = new JSONObject(FileO.allLine("config.json"));
			gToken = config.getString("gToken");
			tToken = config.getString("tToken");
			id = config.getString("id");
			chat = config.getString("chat");
			if(config.has("botName")) botName = config.getString("botName"); else {
				useBotName = true;
				Main.loggerL("Using telegram bot name. ");
			}
			startTime = sTime;
			Main.logger("done");
			
			dir = System.getProperty("user.dir") + File.separator;
			
			JSONArray admin = config.getJSONArray("admins");
			for(int i = 0; i < admin.length(); i++) admins.add(admin.getString(i));
			
		} catch (Exception e) {
			Main.ea.alert(e);
			return false;
		}
		return true;
	}
	public ArrayList<String> getAdmins(){
		return admins;
	}
	public String getBotName(){
		return botName;
	}
	public String getChatId(){
		return chat;
	}
	public String getChannelId(){
		return id;
	}
	public String getTelegramToken(){
		return tToken;
	}
	public String getGoogleToken(){
		return gToken;
	}
	public String getDefaultDirectory(){
		return dir;
	}
	public String getUser(){
		return user;
	}
	public boolean getPhraseStatus(){
		return phraseStatus;
	}
	public boolean getWhatBotName(){
		return useBotName;
	}
	public void setPhraseStatus(boolean status){
		phraseStatus = status;
	}
	public void removeDirectory(){
		dir = "";
	}
	public void setUser(String usr){
		user = usr;
	}
	public void setBotName(String name){
		botName = name;
	}
	public String getGoogleApiFullUrl(int n){
		return "https://www.googleapis.com/youtube/v3/search?part=snippet&channelId=" + id + "&order=date&type=video&key=" + gToken + "&maxResults=" + n;
	}
	public String getUrlVideoType(String id){
    	return "https://www.googleapis.com/youtube/v3/videos?part=snippet&id=" + id + "&maxResults=1&key=" + gToken; 
    }
	public String getUpTime(){
		long estimatedTime = (System.currentTimeMillis() - startTime) / 1000;
		int hours = (int) estimatedTime / 3600;
	    int secs = (int) estimatedTime - hours * 3600;
	    int mins = secs / 60;
	    secs = secs - mins * 60;
	    return hours + ":" + mins + ":" + secs;
	}
}
