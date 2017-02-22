package reloaded.convintobot;

import org.json.JSONArray;
import org.json.JSONObject;

public class Youtube {
	private String[] oldVideoIds;
	
	public void initialize(Settings s){
		Main.loggerL("Inizialazing youtube object... ");
		JSONArray arr;
		while(true){
    		try{
    			JSONObject obj = new JSONObject(Download.dwn(s.getGoogleApiFullUrl(16)));
	    		arr = obj.getJSONArray("items");
	    		break;
    		}catch(Exception e){
    			Main.wait(5000);
    		}
		}
		String[] localIds = new String[arr.length()];
		for(int i = 0; i < localIds.length; i++) localIds[i] = arr.getString(i);
		oldVideoIds = localIds;
		
		if(!FileO.exist("last.temp")) {
			FileO.newFile("last.temp");
			FileO.writer("---;123", "last.temp");
		}
		Main.logger("Done!");
	}
	
	//vado a magnà
	
	public String[] getOldVideoIds(){
		return oldVideoIds;
	}
}
