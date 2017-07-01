package reloaded.convintobot;

public class Live {
	private String id, title;
	private int type, messageId;
	private long notificationCycle, liveOffline;
	
	public Live(String name, String liveId, int videoType, int mId){
		title = name;
		id = liveId;
		type = videoType;
		messageId = mId;
		//if(type==2) upcoming = true;
	}

	public void setType(int videoType){
		type = videoType;
	}
	public void setNotificationCycle(long n){
		notificationCycle = n;
	}
	public void setLiveOffline(long n){
		liveOffline = n;
	}
	
	public int getMessageId(){
		return messageId; 
	}
	public int getType(){
		return type;
	}
	public long getNotificationCycle(){
		return notificationCycle;
	}
	public long getLiveOffline(){
		return liveOffline;
	}
	public String getId(){
		return id;
	}
	public String getTitle(){
		return title;
	}
	
	public String toString(){
    	return "Live{"
				+ "id=" + id + ", "
				+ "title='" + title + "', "
				+ "type='" + type + "', "
				+ "messageId='" + messageId + "',"
				+ "offlineFrom='" + liveOffline + "',"
				+ "notificationCycle='" + notificationCycle + "'"
			+ "}";
	}
	//public boolean stillupcoming(int type){ //0 = live stopped, 2 = still live, 3 = switch from upcoming to live, 4 = still upcoming 
	////}
}
