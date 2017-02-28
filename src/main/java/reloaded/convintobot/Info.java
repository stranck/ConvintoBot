package reloaded.convintobot;

import org.json.JSONObject;

public class Info {
	private String id, type, name;
	
	public void update(int n, Settings s){
		while(true){ //this gibe me cancer
			try {
				JSONObject data = new JSONObject(Download.dwn(s.getGoogleApiFullUrl(n + 1))).getJSONArray("items").getJSONObject(n);
				JSONObject snippet = data.getJSONObject("snippet");
				id = data.getJSONObject("id").getString("videoId");
				type = snippet.getString("liveBroadcastContent");
				name = snippet.getString("title");
				return;
			} catch (Exception e) {
				Main.ea.alert(e);
				Main.wait(5000);
			}
		}
	}
	
	public String getVideoType(String id, Settings s){
		while(true){
			try{
				return new JSONObject(Download.dwn(s.getUrlVideoType(id))).getJSONArray("items").getJSONObject(0).getJSONObject("snippet").getString("liveBroadcastContent");
			}catch(Exception e){
				Main.ea.alert(e);
				Main.wait(5000);
			}
		}
	}
	
	public String getVideoId(){
		return id;
	}
	public String getVideoType(){
		return type;
	}
	public String getVideoName(){
		return name;
	}
}
