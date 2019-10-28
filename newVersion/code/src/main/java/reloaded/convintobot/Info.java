package reloaded.convintobot;

import org.json.JSONArray;
import org.json.JSONObject;

public class Info {
	private String id = "", type = "", name = "";
	
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
				e.printStackTrace();
				Main.ea.alert(e);
				Main.wait(5000);
			}
		}
	}
	
	public String getVideoType(String id, Settings s){
		String ret = "";
		while(ret.equals("")){
			try{
				JSONArray items = new JSONObject(Download.dwn(s.getUrlVideoType(id))).getJSONArray("items");
				if(items.length() > 0)
					ret = items.getJSONObject(0).getJSONObject("snippet").getString("liveBroadcastContent");
				else
					ret = "none";
			}catch(Exception e){
				Main.ea.alert(e);
				Main.wait(5000);
			}
		}
		return ret;
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
	
	public void set(String i, String t, String n){
		id = i;
		name = n;
		type = t;
	}
	
	public String toString(){
    	return "Info{"
				+ "id=" + id + ", "
				+ "title='" + name + "', "
				+ "type='" + type + "'"
			+ "}";
	}
}
