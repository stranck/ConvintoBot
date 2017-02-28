package reloaded.convintobot;

import java.util.ArrayList;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

public class Phrase {
	private ArrayList<String> videoPhrase = new ArrayList<String>(), livePhrase = new ArrayList<String>(), upcomingPhrase = new ArrayList<String>(), terminedPhrase = new ArrayList<String>();
	
	public boolean initialize(){
		//System.out.println(!FileO.exist("phrases.json"));
		if(!FileO.exist("phrases.json")){
			Main.logger("\"phrases.json\" not foud. If you wanna use your own phrases create this file in the same directory of the .jar and write in it in this method:\n"
					+ "{\n "
					+ "   \"video\" : [\n"
					+ "        \"phrase for new video 1\",\n"
					+ "        \"phrase for new video 2\",\n"
					+ "        \"...\"\n"
					+ "    ],\n"
					+ "   \"live\" : [\n"
					+ "        \"phrase for new live 1\",\n"
					+ "        \"phrase for new live 2\",\n"
					+ "        \"...\"\n"
					+ "    ],\n"
					+ "   \"upcoming\" : [\n"
					+ "        \"phrase for new upcoming live 1\",\n"
					+ "        \"phrase for new upcoming live 2\",\n"
					+ "        \"...\"\n"
					+ "    ],\n"
					+ "   \"termined\" : [\n"
					+ "        \"phrase when a live is termined 1\",\n"
					+ "        \"phrase when a live is termined 2\",\n"
					+ "        \"...\"\n"
					+ "    ]\n"
					+ "}");
			return false;
		}
		
		Main.loggerL("Initialazing phrase... ");
		
		try { //bot.execute(new EditMessageText(st.getChatId(), l.get(liveIndex).getMessageId(),
			String json = FileO.allLine("phrases.json");
			
			JSONArray videoPhrases = new JSONObject(json).getJSONArray("video");
			JSONArray livePhrases = new JSONObject(json).getJSONArray("live");
			JSONArray upcomingPhrases = new JSONObject(json).getJSONArray("upcoming");
			JSONArray terminedPhrases = new JSONObject(json).getJSONArray("termined");
			
			for(int i = 0; i < videoPhrases.length(); i++) videoPhrase.add(videoPhrases.getString(i));
			for(int i = 0; i < livePhrases.length(); i++) livePhrase.add(livePhrases.getString(i));
			for(int i = 0; i < upcomingPhrases.length(); i++) upcomingPhrase.add(upcomingPhrases.getString(i));
			for(int i = 0; i < terminedPhrases.length(); i++) terminedPhrase.add(terminedPhrases.getString(i));
			
			Main.logger("Done!");
			return true;
			
		} catch (Exception e) {
			Main.logger("Error while loading \"phrases.json\": ");
			Main.ea.alert(e);
		}
		return false;
	}
	
	public ArrayList<String> getAllPhrases(int type){
		try{
			switch(type){
				case 0: return videoPhrase;
				case 1: return livePhrase;
				case 2: return upcomingPhrase;
				case 3: return terminedPhrase;
			}
		}catch(Exception e){
			Main.logger("Error while getting phrases:");
			Main.ea.alert(e);
		}
		return null;
	}
	public String getSinglePhrases(int type, Settings s){
		if(s.getPhraseStatus()) {
			switch(type){
				case 0:	return "Nuovo video:";
				case 1:	return "In live ora:";
				case 2: return "[Live programmata]";
				case 3: return "[Live terminata]";
			}
		} else {
			Random r = new Random();
			switch(type){
				case 0: return videoPhrase.get(r.nextInt(videoPhrase.size()));
				case 1:	return livePhrase.get(r.nextInt(livePhrase.size()));
				case 2: return upcomingPhrase.get(r.nextInt(upcomingPhrase.size()));
				case 3: return terminedPhrase.get(r.nextInt(terminedPhrase.size()));
			}
		}
		return null;
	}
}
