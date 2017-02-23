package reloaded.convintobot;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class Youtube {
	private ArrayList<String> oldVideoIds = new ArrayList<String>();
	
	public void initialize(Settings s){
		Main.loggerL("Inizialazing youtube object... ");
		Info info = new Info();
		for(int i = 0; i < 16; i++){
			info.update(i, s);
			oldVideoIds.add(info.getVideoId());
		}
		
		if(!FileO.exist("last.ini")) {
			FileO.newFile("last.ini");
			FileO.writer("---;123", "last.ini");
		}
		Main.logger("Done!");
	}
	
	//vado a magnà
	
	public boolean newVideo(String id){
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
