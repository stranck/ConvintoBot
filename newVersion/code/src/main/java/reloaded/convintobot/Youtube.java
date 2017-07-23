package reloaded.convintobot;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class Youtube {
	private ArrayList<String> oldVideoIds = new ArrayList<String>();
	private boolean forceNewVideo = false, forceNoVideo = false;
	
	public void initialize(Settings s){
		Main.LOGGER.config("Inizialazing youtube object");
		
		while(true){
    		try{
    			JSONObject obj = new JSONObject(Download.dwn(s.getGoogleApiFullUrl(16)));
	    		JSONArray arr = obj.getJSONArray("items");
	    		for(int i = 0; i < arr.length(); i++) oldVideoIds.add(arr.getJSONObject(i).getJSONObject("id").getString("videoId"));
	    		break;
    		}catch(Exception e){
    			Main.ea.alert(e);
    			Main.wait(5000);
    		}
		}
		
		if(!FileO.exist("last.ini")) {
			FileO.newFile("last.ini");
			FileO.writer("thisisatest@S0vzZBxXRB4@0", "last.ini");
		}
	}
	
	//vado a magnà
	
	public void forceNoUpdate(boolean b){
		forceNoVideo = b;
	}
	public void forceVideoUpdate(boolean b){
		forceNewVideo = b;
	}
	
	public boolean newVideo(String id){
		
		if(forceNewVideo) {
			forceNewVideo = false;
			return true;
		}
		
		if(!oldVideoIds.contains(id)){
			oldVideoIds.remove(oldVideoIds.size() - 1);
			oldVideoIds.add(0, id);
			
			if(!forceNoVideo) return true;
			forceNoVideo = false;
		}
		return false;
	}

	public ArrayList<String> getOldVideoIds(){
		return oldVideoIds;
	}
}
