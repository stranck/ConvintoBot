package reloaded.convintobot.tResponse;

import java.util.ArrayList;

import org.json.JSONObject;

public class Response {
	
	private ArrayList<String> response = new ArrayList<String>();
	private JSONObject args;
	
	public void addResponse(String r){
		response.add(r);
	}
	
	public void setResponse(ArrayList<String> r){
		response = r;
	}
	public void setArgs(JSONObject a){
		args = a;
	}
	
	public ArrayList<String> getResponse(){
		return response;
	}
	public JSONObject getArgs(){
		return args;
	}
	
}
