package reloaded.convintobot;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class Youtube {
	private ArrayList<String> oldVideoIds = new ArrayList<String>();
	private boolean forceNewVideo = false;
	
	public void initialize(Settings s){
		Main.loggerL("Inizialazing youtube object... ");
		
		while(true){
    		try{
	    		//System.out.println("START");
    			JSONObject obj = new JSONObject(Download.dwn(s.getGoogleApiFullUrl(16)));
	    		//System.out.println("END");
	    		JSONArray arr = obj.getJSONArray("items");
	    		for(int i = 0; i < arr.length(); i++) oldVideoIds.add(arr.getJSONObject(i).getJSONObject("id").getString("videoId"));
	    		//System.out.println("DONE");
	    		break;
    		}catch(Exception e){
    			e.printStackTrace();
    			Main.wait(5000);
    		}
		}
		
		if(!FileO.exist("last.ini")) {
			FileO.newFile("last.ini");
			FileO.writer("thisisatest@uItXIawwf8k@0", "last.ini");
		}
		Main.logger("Done!");
	}
	
	//vado a magnà
	
	public void forceVideoUpdate(boolean b){
		forceNewVideo = b;
	}
	
	public boolean newVideo(String id){
		if(forceNewVideo) return true;
		if(!oldVideoIds.contains(id)){
			oldVideoIds.remove(0);
			oldVideoIds.add(id);
			return true;
		}
		return false;
	}

	public ArrayList<String> getOldVideoIds(){
		return oldVideoIds;
	}
}
