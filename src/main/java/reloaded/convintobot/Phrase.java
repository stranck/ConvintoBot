package reloaded.convintobot;

import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

public class Phrase {
	private String[] videoPhrase, livePhrase, upcomingPhrase;
	
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
				String[] localVideoPhrases = new String[videoPhrases.length()];
				for(int i = 0; i < localVideoPhrases.length; i++) localVideoPhrases[i] = videoPhrases.getJSONObject(i).toString();
				videoPhrase = localVideoPhrases;
			}
			{
				JSONArray livePhrases = new JSONObject(FileO.reader("phrases.json")).getJSONArray("live");
				String[] localLivePhrases = new String[livePhrases.length()];
				for(int i = 0; i < localLivePhrases.length; i++) localLivePhrases[i] = livePhrases.getJSONObject(i).toString();
				livePhrase = localLivePhrases;
			}
			{
				JSONArray upcomingPhrases = new JSONObject(FileO.reader("phrases.json")).getJSONArray("upcoming");
				String[] localUpcomingPhrases = new String[upcomingPhrases.length()];
				for(int i = 0; i < localUpcomingPhrases.length; i++) localUpcomingPhrases[i] = upcomingPhrases.getJSONObject(i).toString();
				videoPhrase = localUpcomingPhrases;
			}
			Main.logger("Done!");
			return true;
			
		} catch (Exception e) {
			Main.logger("Error while loading \"phrases.json\": ");
			e.printStackTrace();
		}
		return false;
	}
	
	public String[] getAllPhrases(String type){
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
				case"video":    return videoPhrase[r.nextInt(videoPhrase.length)];
				case"live":		return livePhrase[r.nextInt(livePhrase.length)];
				case"upcoming": return upcomingPhrase[r.nextInt(upcomingPhrase.length)];
			}
		}
		return null;
	}
}
