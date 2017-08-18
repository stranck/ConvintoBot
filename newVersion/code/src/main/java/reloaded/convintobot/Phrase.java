package reloaded.convintobot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

public class Phrase {
	private ArrayList<String> videoPhrase = new ArrayList<String>(), 
							  livePhrase = new ArrayList<String>(), 
							  upcomingPhrase = new ArrayList<String>(), 
							  terminedPhrase = new ArrayList<String>(), 
							  upcomingToLivePhrase = new ArrayList<String>(), 
							  liveToNonePhrase = new ArrayList<String>(), 
							  twitchOnPhrase = new ArrayList<String>(), 
							  twitchOffPhrase = new ArrayList<String>();
	
	public boolean[] initialize(){
		//System.out.println(!FileO.exist("phrases.json"));
		videoPhrase.clear();
		livePhrase.clear();
		upcomingPhrase.clear();
		terminedPhrase.clear();
		upcomingToLivePhrase.clear();
		liveToNonePhrase.clear();
		twitchOnPhrase.clear();
		twitchOffPhrase.clear();
		
		boolean ret[] = new boolean[8];
		for(int i = 0; i < ret.length; i++) ret[i] = false;
		
		if(!FileO.exist("phrases.json")){
			Main.LOGGER.severe("\"phrases.json\" not foud. If you wanna use your own phrases create this file in the same directory of the .jar and write in it in this method:");
			System.out.println(
					  "{\n "
					+ "   \"video\" : [\n"
					+ "        \"phrase for new youtube video 1\",\n"
					+ "        \"phrase for new youtube video 2\",\n"
					+ "        \"...\"\n"
					+ "    ],\n"
					+ "   \"live\" : [\n"
					+ "        \"phrase for new youtube live 1\",\n"
					+ "        \"phrase for new youtube live 2\",\n"
					+ "        \"...\"\n"
					+ "    ],\n"
					+ "   \"upcoming\" : [\n"
					+ "        \"phrase for new upcoming youtube live 1\",\n"
					+ "        \"phrase for new upcoming youtube live 2\",\n"
					+ "        \"...\"\n"
					+ "    ],\n"
					+ "   \"upcomingToLive\" : [\n"
					+ "        \"notification's text when a youtube programmed live become on live 1\",\n"
					+ "        \"notification's text when a youtube programmed live become on live 2\",\n"
					+ "        \"...\"\n"
					+ "    ],\n"
					+ "   \"termined\" : [\n"
					+ "        \"phrase when a youtube live is termined 1\",\n"
					+ "        \"phrase when a youtube live is termined 2\",\n"
					+ "        \"...\"\n"
					+ "    ],\n"
					+ "   \"liveToNone\" : [\n"
					+ "        \"notification's text when a youtube live become offline 1\",\n"
					+ "        \"notification's text when a youtube live become offline 2\",\n"
					+ "        \"...\"\n"
					+ "    ],\n"
					+ "   \"twitchOnline\" : [\n"
					+ "        \"phrase when twitch stream become online 1\",\n"
					+ "        \"phrase when twitch stream become online 2\",\n"
					+ "        \"...\"\n"
					+ "    ],\n"
					+ "   \"twitchOffline\" : [\n"
					+ "        \"phrase when twitch stream become offline 1\",\n"
					+ "        \"phrase when twitch stream become offline 2\",\n"
					+ "        \"...\"\n"
					+ "    ]\n"
					+ "}");
		} else {
		
			Main.LOGGER.config("Initialazing phrase");
		
			try { //bot.execute(new EditMessageText(st.getChatId(), l.get(liveIndex).getMessageId(),
				JSONObject json = new JSONObject(FileO.allLine("phrases.json"));
				int i;
				
				if(json.has("video")){
					Main.LOGGER.config("Loading video phrases");
					JSONArray videoPhrases = json.getJSONArray("video");
					for(i = 0; i < videoPhrases.length(); i++) videoPhrase.add(videoPhrases.getString(i));
					ret[0] = true;
				}
				if(json.has("live")){
					Main.LOGGER.config("Loading live phrases");
					JSONArray livePhrases = json.getJSONArray("live");
					for(i = 0; i < livePhrases.length(); i++) livePhrase.add(livePhrases.getString(i));
					ret[1] = true;
				}
				if(json.has("upcoming")){
					Main.LOGGER.config("Loading upcoming phrases");
					JSONArray upcomingPhrases = json.getJSONArray("upcoming");
					for(i = 0; i < upcomingPhrases.length(); i++) upcomingPhrase.add(upcomingPhrases.getString(i));
					ret[2] = true;
				}
				if(json.has("termined")){
					Main.LOGGER.config("Loading termined phrases");
					JSONArray terminedPhrases = json.getJSONArray("termined");
					for(i = 0; i < terminedPhrases.length(); i++) terminedPhrase.add(terminedPhrases.getString(i));
					ret[3] = true;
				}
				if(json.has("upcomingToLive")){
					Main.LOGGER.config("Loading upcomingToLive phrases");
					JSONArray upcomingToLivePhrases = json.getJSONArray("upcomingToLive");
					for(i = 0; i < upcomingToLivePhrases.length(); i++) upcomingToLivePhrase.add(upcomingToLivePhrases.getString(i));
					ret[4] = true;
				}
				if(json.has("liveToNone")){
					Main.LOGGER.config("Loading liveToNone phrases");
					JSONArray liveToNonePhrases = json.getJSONArray("liveToNone");
					for(i = 0; i < liveToNonePhrases.length(); i++) liveToNonePhrase.add(liveToNonePhrases.getString(i));
					ret[5] = true;
				}
				if(json.has("twitchOnline")){
					Main.LOGGER.config("Loading twitchOnline phrases");
					JSONArray twitchOnPhrases = json.getJSONArray("twitchOnline");
					for(i = 0; i < twitchOnPhrases.length(); i++) twitchOnPhrase.add(twitchOnPhrases.getString(i));
					ret[5] = true;
				}
				if(json.has("twitchOffline")){
					Main.LOGGER.config("Loading twitchOffline phrases");
					JSONArray twitchOffPhrasePhrases = json.getJSONArray("twitchOffline");
					for(i = 0; i < twitchOffPhrasePhrases.length(); i++) twitchOffPhrase.add(twitchOffPhrasePhrases.getString(i));
					ret[5] = true;
				}
				
			} catch (Exception e) {
				Main.LOGGER.warning("Error while loading phrases:");
				Main.ea.alert(e);
			}
		}
		return ret;
	}
	
	public ArrayList<String> getAllPhrases(int type){
		try{
			switch(type){
				case 0: return videoPhrase;
				case 1: return livePhrase;
				case 2: return upcomingPhrase;
				case 3: return terminedPhrase;
				case 4: return upcomingToLivePhrase;
				case 5: return liveToNonePhrase;
				case 6: return twitchOnPhrase;
				case 7: return twitchOffPhrase;
			}
		}catch(Exception e){
			Main.LOGGER.warning("Error while getting phrases:");
			Main.ea.alert(e);
		}
		return null;
	}
	public String getSinglePhrases(int type, Settings s, Twitch t, boolean enableProgrammed) throws IOException{
		if(enableProgrammed && FileO.exist("programmed.ini")) {
			String prg = FileO.reader("programmed.ini");
			FileO.delater("programmed.ini");
			return prg.replaceAll("%game%", FileO.toHtml(t.getGame()));
		}
		if(!s.getPhraseStatus(type)) {
			switch(type){
				case 0:	return "Nuovo video:";
				case 1:	return "In live ora:";
				case 2: return "[Live programmata]";
				case 3: return "[Live terminata]";
				case 4: return "La live è iniziata!";
				case 5: return "Live finita";
				case 6: return "Sta giocando a " + t.getGame();
				case 7: return "Stava giocando a " + t.getGame();
			}
		} else {
			Random r = new Random();
			switch(type){
				case 0: return videoPhrase.get(r.nextInt(videoPhrase.size()));
				case 1:	return livePhrase.get(r.nextInt(livePhrase.size()));
				case 2: return upcomingPhrase.get(r.nextInt(upcomingPhrase.size()));
				case 3: return terminedPhrase.get(r.nextInt(terminedPhrase.size()));
				case 4: return upcomingToLivePhrase.get(r.nextInt(upcomingToLivePhrase.size()));
				case 5: return liveToNonePhrase.get(r.nextInt(liveToNonePhrase.size()));
				case 6: return twitchOnPhrase.get(r.nextInt(twitchOnPhrase.size())).replaceAll("%game%", FileO.toHtml(t.getGame()));
				case 7: return twitchOffPhrase.get(r.nextInt(twitchOffPhrase.size())).replaceAll("%game%", FileO.toHtml(t.getGame()));
			}
		}
		return null;
	}
}
