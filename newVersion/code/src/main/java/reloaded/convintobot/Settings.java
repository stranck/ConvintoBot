package reloaded.convintobot;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;

public class Settings {
	private long startTime, liveOfflineDelay, repeatDelay = Long.MAX_VALUE;
	private boolean youtube, ytLive, ytVideo, twitch, mantainPhrase, useBotName = false;
	private String gToken, tToken, wToken, yId, tId, botName, user, dir = "";
	private ArrayList<String> admins = new ArrayList<String>();
	private ArrayList<Chats> chats = new ArrayList<Chats>();
	private boolean phraseStatus[] = new boolean[8];
	
	public boolean loadSettings(long sTime){
		
		for(int i = 0; i < phraseStatus.length; i++) phraseStatus[i] = false;
		
		try {
			if(!FileO.exist("config.json")){
				Main.LOGGER.severe("File \"config.json\" not found.");
				FileO.newFile("config.json");
				FileO.writer("{", "config.json");
				FileO.addWrite("config.json", "    \"tToken\" : \"INSERIT YOUR TELEGRAM TOKEN HERE\",");
				FileO.addWrite("config.json", "    \"botName\" : \"INSERIT THE NAME OF YOUR BOT HERE\",");
				FileO.addWrite("config.json", "    \"liveOfflineDelay\" : HOW MANY MS PASS UNTIL A LIVE IS DECLEARED OFFLINE (prevent the disconnection),");
				FileO.addWrite("config.json", "    \"notificationRepeatDelay\" : DELAY IN MS,");
				FileO.addWrite("config.json", "    \"mantainPhrase\" : true / false");
				FileO.addWrite("config.json", "    \"youtube\" : {");
				FileO.addWrite("config.json", "        \"enable\" : true / false,");
				FileO.addWrite("config.json", "        \"gToken\" : \"INSERIT YOUR GOOGLE APIS TOKEN HERE\",");
				FileO.addWrite("config.json", "        \"id\" : \"INSERIT YOUR CHANNEL ID HERE\",");
				FileO.addWrite("config.json", "        \"enableVideo\" : true / false,");
				FileO.addWrite("config.json", "        \"enableLive\" : true / false");
				FileO.addWrite("config.json", "    },");
				FileO.addWrite("config.json", "    \"twitch\" : {");
				FileO.addWrite("config.json", "        \"enable\" : true / false,");
				FileO.addWrite("config.json", "        \"token\" : \"INSERIT YOUR TWITCH CLIENT ID HERE\",");
				FileO.addWrite("config.json", "        \"id\" : \"INSERIT YOUR CHANNEL ID HERE\"");
				FileO.addWrite("config.json", "    },");
				FileO.addWrite("config.json", "    \"chats\" : [");
				FileO.addWrite("config.json", "        {");
				FileO.addWrite("config.json", "            \"chat\" : \"INSERIT THE CHAT ID HERE\",");
				FileO.addWrite("config.json", "            \"lessSpamMethod\" : \"DELETE / COMPRESS / NONE\",");
				FileO.addWrite("config.json", "            \"pinMessage\" : true / false,");
				FileO.addWrite("config.json", "            \"disablePinNotification\" : true / false,");
				FileO.addWrite("config.json", "            \"twitch\" : true / false,");
				FileO.addWrite("config.json", "            \"youtube\" : true / false,");
				FileO.addWrite("config.json", "            \"liveNotification\" : {");
				FileO.addWrite("config.json", "                \"onUpcoming\" : true / false,");
				FileO.addWrite("config.json", "                \"onStop\" : true / false,");
				FileO.addWrite("config.json", "                \"repeat\" : true / false");
				FileO.addWrite("config.json", "            }");
				FileO.addWrite("config.json", "        }");
				FileO.addWrite("config.json", "    ],");
				FileO.addWrite("config.json", "    \"admins\" : [");
				FileO.addWrite("config.json", "        \"INSERIT THE TELEGRAM ACCOUNT ID OF YOUR FIRST ADMIN HERE\",");
				FileO.addWrite("config.json", "        \"INSERIT THE TELEGRAM ACCOUNT ID OF YOUR SECOND ADMIN HERE\",");
				FileO.addWrite("config.json", "        \"...\"");
				FileO.addWrite("config.json", "    ]");
				FileO.addWrite("config.json", "}");
				Main.LOGGER.severe("File \"config.json\" created. Edit your settings and then turn on again the bot.");
				return false;
			}
			
			Main.LOGGER.config("Loading configs");
			JSONObject config = new JSONObject(FileO.allLine("config.json"));	
			tToken = config.getString("tToken");
			liveOfflineDelay = config.getLong("liveOfflineDelay");
			mantainPhrase = config.getBoolean("mantainPhrase");
			
			JSONObject ytObj = config.getJSONObject("youtube"); 
			if(ytObj.getBoolean("enable")){
				youtube = true;
				gToken = ytObj.getString("gToken");
				yId = ytObj.getString("id");
				ytVideo = ytObj.getBoolean("enableVideo");
				ytLive = ytObj.getBoolean("enableLive");
			}
			JSONObject twObj = config.getJSONObject("twitch"); 
			if(twObj.getBoolean("enable")){
				twitch = true;
				wToken = twObj.getString("token");
				tId = twObj.getString("id");
			}

			if(config.has("botName")) botName = config.getString("botName"); else {
				useBotName = true;
				Main.LOGGER.config("Using telegram bot name");
			}
			startTime = sTime;
			
			dir = System.getProperty("user.dir") + File.separator;
			
			JSONArray admin = config.getJSONArray("admins");
			admins.clear();
			for(int i = 0; i < admin.length(); i++) admins.add(admin.getString(i));
			
			boolean rd = false;
			chats.clear();
			JSONArray arr = config.getJSONArray("chats");
			for(int i = 0; i < arr.length(); i++){
				Main.LOGGER.config("Loading settings for chat [" + i + "]");
				JSONObject chat = arr.getJSONObject(i);
				
				String lessSpamMethod = chat.getString("lessSpamMethod");
				if(!lessSpamMethod.equalsIgnoreCase("DELETE") && !lessSpamMethod.equalsIgnoreCase("COMPRESS") && !lessSpamMethod.equalsIgnoreCase("NONE") && !lessSpamMethod.equalsIgnoreCase("NOWEBPREVIEW")) {
					Main.LOGGER.severe(lessSpamMethod + ": lessSpamMethod not valid.");
					return false;
				}
				JSONObject liveNotification = chat.getJSONObject("liveNotification");
				Chats c = new Chats(chat.getString("chat"), lessSpamMethod, chat.getBoolean("pinMessage"), liveNotification.getBoolean("onUpcoming"), liveNotification.getBoolean("onStop"), liveNotification.getBoolean("repeat"));
				if(twitch) c.setIfTwitch(chat.getBoolean("twitch")); 
				if(youtube) c.setIfYoutube(chat.getBoolean("youtube"));
				
				if(c.getIfPin()) c.setPiNotification(chat.getBoolean("disablePinNotification"));
				if(c.getIfRepeat()) rd = true;
				chats.add(c);
			}
			if(rd) repeatDelay = config.getLong("notificationRepeatDelay");
			
		} catch (Exception e) {
			e.printStackTrace();
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
	public String getChannelId(){
		return yId;
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
	public String getTwitchToken(){
		return wToken;
	}
	public String getTwitchChannel(){
		return tId;
	}
	public boolean getPhraseStatus(int type){
		return phraseStatus[type];
	}
	public boolean[] getPhraseStatus(){
		return phraseStatus;
	}
	public boolean getWhatBotName(){
		return useBotName;
	}
	public boolean getIfYoutube(){
		return youtube;
	}
	public boolean getIfTwitch(){
		return twitch;
	}
	public boolean getMantainPhrase(){
		return mantainPhrase;
	}
	public boolean getIfYoutubeVideo(){
		return ytVideo;
	}
	public boolean getIfYoutubeLive(){
		return ytLive;
	}
	public long getOfflineDelay(){
		return liveOfflineDelay;
	}
	public long getRepeatDelay(){
		return repeatDelay;
	}
	public ArrayList<Chats> getChats(){
		return chats;
	}
	public void setPhraseStatus(boolean[] status){
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
		return "https://www.googleapis.com/youtube/v3/search?part=snippet&channelId=" + yId + "&order=date&type=video&key=" + gToken + "&maxResults=" + n;
	}
	public String getUrlVideoType(String id){
    	return "https://www.googleapis.com/youtube/v3/videos?part=snippet&id=" + id + "&maxResults=1&key=" + gToken; 
    }
	public String getTwitchUrl(int option){
		String method = "";
		switch(option){
			case 0: {method = "streams"; break;}
			case 1: {method = "channels"; break;}
		}
		return "https://api.twitch.tv/kraken/" + method + "/" + tId + "?client_id=" + wToken;
	}
	public String getTwitchClickableTitle(String title){
		return "<a href=\"https://www.twitch.tv/" + tId + "\">" + FileO.toHtml(title) + "</a>";
	}
	public String getUpTime(){
		long estimatedTime = (System.currentTimeMillis() - startTime) / 1000;
		int hours = (int) estimatedTime / 3600;
	    int secs = (int) estimatedTime - hours * 3600;
	    int mins = secs / 60;
	    secs = secs - mins * 60;
	    return hours + ":" + mins + ":" + secs;
	}
	public String getChatsId(){
		String[] s = new String[chats.size()];
		for(int i = 0; i < s.length; i++) s[i] = chats.get(i).getChatId();
		return Arrays.toString(s);
	}
}
