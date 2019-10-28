package reloaded.convintobot;

import org.json.JSONArray;
import org.json.JSONObject;

public class Twitch {
	private boolean goneOnline = true, forceClipUpdate;
	private int[] messageId;
	private long liveOffline, notificationCycle;
	private String title = "", game = "", mText = "";
	
	public Twitch(){}
	
	public Twitch(Settings st){
		Main.LOGGER.config("Initializing Twitch object");
		try{
			if(st.getIfTwitch()){
				st.loadTwitchUserId();
				JSONObject stream = Download.twitchOld("channels/" + st.getTwitchUserID()); //FARE IN MODO CHE SI PRECARICHI VECCHI TITLE E GAME
				//System.out.println(stream.toString());
				title = stream.getString("status");
				game = stream.getString("game");
			}
		} catch(Exception e) {e.printStackTrace();}
	}
	
	public int checkLive(Settings st) throws Exception{
		JSONArray stream = Download.twitch(st.getTwitchUrl(0)).getJSONArray("data");
		
		int ret = 0; //0 = offline; 1 = gone offline; 2 = online; 3 = gone online;
		
		if(stream.length() > 0 && st.checkTwtichLiveType(stream.getJSONObject(0).getString("type"))) {
			if(goneOnline) {
				JSONObject data = stream.getJSONObject(0);
				notificationCycle = System.currentTimeMillis();
				goneOnline = false;
				game = getNameFromId(data.getString("game_id"));
				title = data.getString("title");
				ret = 3;
			} else ret = 2;
			liveOffline = System.currentTimeMillis();
		} else if(!goneOnline && System.currentTimeMillis() - liveOffline > st.getOfflineDelay()) {
			goneOnline = true;
			ret = 1;
		}
		
		return ret;
	}
	private String getNameFromId(String id) throws Exception{
		return Download.twitch("games?id=" + id).getString("name");
	}
	
	public String getMText(){
		return mText;
	}
	public String getGame(){
		return game;
	}
	public String getTitle(){
		return title;
	}
	public int[] getMessageId(){
		return messageId;
	}
	public long getNotificationCycle(){
		return notificationCycle;
	}
	public boolean getIfInLive(){
		return !goneOnline;
	}
	public boolean getClipUpdate(){
		return forceClipUpdate;
	}
	
	public void setClipUpdate(boolean b){
		forceClipUpdate = b;
	}
	public void setMText(String s){
		mText = s;
	}
	public void setMessageId(int[] n){
		messageId = n;
	}
	public void setNotificationCycle(long n){
		notificationCycle = n;
	}
}
