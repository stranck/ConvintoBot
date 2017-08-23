package reloaded.convintobot;

import org.json.JSONObject;

public class Twitch {
	private boolean goneOnline = true;
	private int[] messageId;
	private long liveOffline, notificationCycle;
	private String title = "", game = "", mText = "";
	
	public Twitch(){}
	
	public Twitch(Settings st){
		Main.LOGGER.config("Initializing Twitch object");
		try{
			if(st.getIfTwitch()){
				JSONObject stream = new JSONObject(Download.dwn(st.getTwitchUrl(1))); //FARE IN MODO CHE SI PRECARICHI VECCHI TITLE E GAME
				title = stream.getString("status");
				game = stream.getString("game");
			}
		} catch(Exception e) {e.printStackTrace();}
	}
	
	public int checkLive(Settings st) throws Exception{
		JSONObject stream = new JSONObject(Download.dwn(st.getTwitchUrl(0)));
		
		int ret = 0; //0 = offline; 1 = gone offline; 2 = online; 3 = gone online;
		
		if(!stream.isNull("stream")) {
			if(goneOnline) {
				notificationCycle = System.currentTimeMillis();
				goneOnline = false;
				game = stream.getJSONObject("stream").getString("game");
				title = stream.getJSONObject("stream").getJSONObject("channel").getString("status");
				ret = 3;
			} else ret = 2;
			liveOffline = System.currentTimeMillis();
		} else if(!goneOnline && System.currentTimeMillis() - liveOffline > st.getOfflineDelay()) {
			goneOnline = true;
			ret = 1;
		}
		
		return ret;
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
