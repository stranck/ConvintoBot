package reloaded.convintobot;

public class Chats {
	private boolean pin, pinNotification, twitch, youtube, onUpcoming, onStop, repeat;
	private String chat, lessSpamMethod;
	
	public Chats(String c, String lsm, boolean p, boolean ou, boolean os, boolean r){
		chat = c;
		lessSpamMethod = lsm;
		pin = p;
		onUpcoming = ou;
		onStop = os;
		repeat = r;
	}
	
	public void setIfYoutube(boolean b){
		youtube = b;
	}
	public void setIfTwitch(boolean b){
		twitch = b;
	}
	public void setPiNotification(boolean b){
		pinNotification = b;
	}
	
	public String getChatId(){
		return chat;
	}
	public String getLessSpamMethod(){
		return lessSpamMethod;
	}
	public boolean getIfRepeat(){
		return repeat;
	}
	public boolean getIfNotificationOnUpcoming(){
		return onUpcoming;
	}
	public boolean getIfNotificationOnStop(){
		return onStop;
	}
	public boolean getIfYoutube(){
		return youtube;
	}
	public boolean getIfTwitch(){
		return twitch;
	}
	public boolean getIfPin(){
		return pin;
	}
	public boolean getPinNotification(){
		return pinNotification;
	}
}
