package reloaded.convintobot;

public class Live {
	private String id;
	private int type;
	public Live(String liveId, int videoType){
		id = liveId;
		type = videoType;
		//if(type==2) upcoming = true;
	}
	public void setType(int videoType){
		type = videoType;
	}
	public int getType(){
		return type;
	}
	public String getId(){
		return id;
	}
	//public boolean stillupcoming(int type){ //0 = live stopped, 2 = still live, 3 = switch from upcoming to live, 4 = still upcoming 
	////}
}
