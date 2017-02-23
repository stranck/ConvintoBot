package reloaded.convintobot;

import java.util.ArrayList;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

public class Phrase {
	private ArrayList<String> videoPhrase = new ArrayList<String>(), livePhrase = new ArrayList<String>(), upcomingPhrase = new ArrayList<String>();
	
	public boolean initialize(){
		if(!FileO.exist("phrases.json")){
			Main.logger("\"phrase.json\" not foud. If you wanna use your own phrases create this file in the same directory of the .jar and write in it in this method:\n"
					+ "{\n "
					+ "   \"video\" : [\n"
					+ "        \"phrase for new video 1\",\n"
					+ "        \"phrase for new video 2\",\n"
					+ "        \"...\"\n"
					+ "    ],\n"
					+ "   \"live\" : ["
					+ "        \"phrase for new live 1\",\n"
					+ "        \"phrase for new live 2\",\n"
					+ "        \"...\"\n"
					+ "    ],\n"
					+ "   \"upcoming\" : ["
					+ "        \"phrase for new upcoming live 1\",\n"
					+ "        \"phrase for new upcoming live 2\",\n"
					+ "        \"...\"\n"
					+ "    ]\n"
					+ "}");
			return false;
		}
		
		Main.loggerL("Initialazing phrase... ");
		
		try {
			{
				JSONArray videoPhrases = new JSONObject(FileO.reader("phrases.json")).getJSONArray("video");
				for(int i = 0; i < videoPhrases.length(); i++) videoPhrase.add(videoPhrases.getString(i));
			}
			{
				JSONArray livePhrases = new JSONObject(FileO.reader("phrases.json")).getJSONArray("live");
				for(int i = 0; i < livePhrases.length(); i++) livePhrase.add(livePhrases.getString(i));
			}
			{
				JSONArray upcomingPhrases = new JSONObject(FileO.reader("phrases.json")).getJSONArray("upcoming");
				for(int i = 0; i < upcomingPhrases.length(); i++) upcomingPhrase.add(upcomingPhrases.getString(i));
			}
			Main.logger("Done!");
			return true;
			
		} catch (Exception e) {
			Main.logger("Error while loading \"phrases.json\": ");
			e.printStackTrace();
		}
		return false;
	}
	
	public ArrayList<String> getAllPhrases(String type){
		try{
			switch(type){
				case"video":    return videoPhrase;
				case"live":     return livePhrase;
				case"upcoming": return upcomingPhrase;
			}
		}catch(Exception e){
			Main.logger("Error while getting phrases:");
			e.printStackTrace();
		}
		return null;
	}
	public String getSinglePhrases(String type, Settings s){
		if(s.getPhraseStatus()) {
			switch(type){
				case"video":	return "Nuovo video:";
				case"live":		return "In live ora:";
				case"upcoming": return "[Live programmata]";
			}
		} else {
			Random r = new Random();
			switch(type){
				case"video":    return videoPhrase.get(r.nextInt(videoPhrase.size()));
				case"live":		return livePhrase.get(r.nextInt(livePhrase.size()));
				case"upcoming": return upcomingPhrase.get(r.nextInt(upcomingPhrase.size()));
			}
		}
		return null;
	}
}
