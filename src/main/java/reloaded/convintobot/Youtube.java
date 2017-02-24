package reloaded.convintobot;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class Youtube {
	private ArrayList<String> oldVideoIds = new ArrayList<String>();
	private boolean forceNewVideo = false;
	
	public void initialize(Settings s){
		Main.loggerL("Inizialazing youtube object... ");
		Info info = new Info();
		for(int i = 0; i < 16; i++){
			info.update(i, s);
			oldVideoIds.add(info.getVideoId());
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
