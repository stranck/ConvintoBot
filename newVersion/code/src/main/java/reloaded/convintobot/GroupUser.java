package reloaded.convintobot;

import java.io.File;

public class GroupUser {
	private String id;
	private long expireDate;
	private boolean alreadyNotified = false, expiredNotified = false, banned = false;
	
	public GroupUser(String userId, String data){
		id = userId;
		if(data.length() > 0){
			String[] sp = data.split(";");
			expireDate = Long.valueOf(sp[0]);
			alreadyNotified = Boolean.valueOf(sp[1]);
			expiredNotified = Boolean.valueOf(sp[2]);
		} else {
			banned = true;
		}
	}
	
	public GroupUser(String userId, long ms){
		id = userId;
		expireDate = ms;
		save();
	}
	
	public boolean checkIfNotify(Settings st){
		return st.getIfNotifyBefore() && (System.currentTimeMillis() > expireDate - st.getNotifyBeforeDelay()) && !alreadyNotified;
	}
	public boolean checkIfExpired(){
		return (System.currentTimeMillis() > expireDate) && !expiredNotified;
	}
	public boolean checkIfTimedOut(Settings st){
		return (System.currentTimeMillis() > (expireDate + st.getKeepInGroupDelay())) || !st.getIfKeepInGroup();
	}
	
	public void save(){
		FileO.writer(String.valueOf(expireDate) + ";" + alreadyNotified + ";" + expiredNotified, "sub" + File.separator + id);
	}
	public void deletea(){
		FileO.delater("sub" + File.separator + id);
	}
	
	public String getId(){
		return id;
	}
	public int getNumericId(){
		return Integer.parseInt(id);
	}
	public long getExpireDate(){
		return expireDate;
	}
	public boolean isBanned(){
		return banned;
	}
	public void setId(String s){
		id = s;
	}
	public void setExpireDate(long l){
		expireDate = l;
	}
	public void setIfAlreadyNotified(boolean b){
		alreadyNotified = b;
	}
	public void setIfExpiredIsNotified(boolean b){
		expiredNotified = b;
	}
	public void ban(){
		banned = true;
		FileO.writer("", "sub" + File.separator + id);
	}
}
