package reloaded.convintobot;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;

public class Settings {
	private long startTime, liveOfflineDelay, notifyBeforeDelay, keepInGroupDelay, repeatDelay = Long.MAX_VALUE;
	private boolean youtube, ytLive, ytVideo, twitch, checkTwitch, mantainPhrase, manageGroup, notifyExpire, notifyBefore, keepInGroup, keepInGroup4e, muteUntilLogin, clipTrending, twitchClips, useBotName = false;
	private String gToken, tToken, wToken, yId, tLogin, redirect, secret, tUserId, botName, user, manageGroupId, link, lang, clipInterval, clipDay, dir = "";
	private ArrayList<String> admins = new ArrayList<String>();
	private ArrayList<String> allowedTwitchTypes = new ArrayList<String>();
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
				FileO.addWrite("config.json", "        \"checkTwitch\" : true / false,");
				FileO.addWrite("config.json", "        \"token\" : \"INSERIT YOUR TWITCH CLIENT ID HERE\",");
				FileO.addWrite("config.json", "        \"id\" : \"INSERIT YOUR CHANNEL ID HERE\"");
				FileO.addWrite("config.json", "        \"manageGroup\" : true / false,");
				FileO.addWrite("config.json", "        \"manageGroupSettings\" : {");
				FileO.addWrite("config.json", "        	   \"secret\" : \"INSERIT YOUR TWITCH SECRET HERE\",");
				FileO.addWrite("config.json", "            \"redirect\" : \"INSERIT REDIRECT LINK HERE\",");
				FileO.addWrite("config.json", "            \"group\" : \"INSERIT GROUP CHAT ID HERE\",");
				FileO.addWrite("config.json", "            \"link\" : INSERIT THE LINK TO THE GROUP HERE,");
				FileO.addWrite("config.json", "            \"muteUntilLogin\" : true / false,");
				FileO.addWrite("config.json", "            \"lang\" : INSERIT THE LANGUAGE CODE HERE,");
				FileO.addWrite("config.json", "            \"notifyExpire\" : true / false,");
				FileO.addWrite("config.json", "            \"notifyBefore\" : true / false,");
				FileO.addWrite("config.json", "            \"notifyBeforeDelay\" : INSERIT TIME IN MS,");
				FileO.addWrite("config.json", "            \"keepInGroupForever\" : true / false,");
				FileO.addWrite("config.json", "            \"keepInGroup\" : true / false,");
				FileO.addWrite("config.json", "            \"keepInGroupDelay\" : INSERIT TIME IN MS");
				FileO.addWrite("config.json", "        },");
				FileO.addWrite("config.json", "        \"shareClips\" : true / false,");
				FileO.addWrite("config.json", "        \"shareClipsSettings\" : {");
				FileO.addWrite("config.json", "            \"interval\" : \"day / week / month\",");
				FileO.addWrite("config.json", "            \"day\" : INSERIT HOUR/DAY TIME,");
				FileO.addWrite("config.json", "            \"trending\" : true / false,");
				FileO.addWrite("config.json", "        }");
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
				tLogin = twObj.getString("id");
				//tUserId = getTwitchUserId();
				checkTwitch = twObj.getBoolean("checkTwitch");
				if(twObj.has("allowedTypes")){
					JSONArray types = twObj.getJSONArray("allowedTypes");
					for(int i = 0; i < types.length(); i++)
						allowedTwitchTypes.add(types.getString(i));
				}
				manageGroup = twObj.getBoolean("manageGroup");
				if(manageGroup) {
					JSONObject mgObj = twObj.getJSONObject("manageGroupSettings");
					secret = mgObj.getString("secret");
					redirect = mgObj.getString("redirect");
					manageGroupId = mgObj.getString("group");
					link = mgObj.getString("link");
					muteUntilLogin = mgObj.getBoolean("muteUntilLogin");
					lang = mgObj.getString("lang");
					notifyExpire = mgObj.getBoolean("notifyExpire");
					notifyBefore = mgObj.getBoolean("notifyBefore");
					if(notifyBefore) notifyBeforeDelay = mgObj.getLong("notifyBeforeDelay");
					keepInGroup4e = mgObj.getBoolean("keepInGroupForever");
					if(!keepInGroup4e){
						keepInGroup = mgObj.getBoolean("keepInGroup");
						if(keepInGroup) keepInGroupDelay = mgObj.getLong("keepInGroupDelay");
					}
				}
				twitchClips = twObj.getBoolean("shareClips");
				if(twitchClips){
					JSONObject clipObj = twObj.getJSONObject("shareClipsSettings");
					clipInterval = clipObj.getString("interval");
					if(!clipInterval.equalsIgnoreCase("day") && !clipInterval.equalsIgnoreCase("week") && !clipInterval.equalsIgnoreCase("month")) {
						Main.LOGGER.severe(clipObj.getString("interval") + ": Interval not valid");
						return false;
					}
					clipDay = clipObj.getString("day");
					clipTrending = clipObj.getBoolean("trending");
				}
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
		return tLogin;
	}
	public String getTwitchUserID(){
		return tUserId;
	}
	public String getManageGroupId(){
		return manageGroupId;
	}
	public String getGroupLink(){
		return link;
	}
	public String getLoginPageLang(){
		return lang;
	}
	public String getClipInterval(){
		return clipInterval;
	}
	public String getClipDay(){
		return clipDay;
	}
	public String getTSecret(){
		return secret;
	}
	public String getRedirectURL(){
		return redirect + "acceptSub";
	}
	public String getLoginURL(){
		return redirect;
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
	public boolean getIfManageGroup(){
		return manageGroup;
	}
	public boolean getIfNotifyExpire(){
		return notifyExpire;
	}
	public boolean getIfNotifyBefore(){
		return notifyBefore;
	}
	public boolean getIfKeepInGroup(){
		return keepInGroup;
	}
	public boolean getIfKeepInGroupForever(){
		return keepInGroup4e;
	}
	public boolean getIfMuteUntilLogin(){
		return muteUntilLogin;
	}
	public boolean getIfCheckTwitch(){
		return checkTwitch;
	}
	public boolean getIfShareClips(){
		return twitchClips;
	}
	public boolean getIfClipTrending(){
		return clipTrending;
	}
	public long getOfflineDelay(){
		return liveOfflineDelay;
	}
	public long getRepeatDelay(){
		return repeatDelay;
	}
	public long getNotifyBeforeDelay(){
		return notifyBeforeDelay;
	}
	public long getKeepInGroupDelay(){
		return keepInGroupDelay;
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
	public boolean checkTwtichLiveType(String type){
		if(allowedTwitchTypes.size() == 0) return true;
		for(String s : allowedTwitchTypes)
			if(type.equals(s))
				return true;
		return false;
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
		return method + "?user_login=" + tLogin;
	}
	public String getTwitchClickableTitle(String title){
		return "<a href=\"https://www.twitch.tv/" + tLogin + "\">" + FileO.toHtml(title) + "</a>";
	}
	public String getUpTime(){
		return Main.remainTime(startTime);
	}
	public String getChatsId(){
		String[] s = new String[chats.size()];
		for(int i = 0; i < s.length; i++) s[i] = chats.get(i).getChatId();
		return Arrays.toString(s);
	}
	public void loadTwitchUserId() throws Exception{
		tUserId = Download.twitch("users?login=" + getTwitchChannel())
				.getJSONArray("data")
				.getJSONObject(0)
				.getString("id");
	}
}
