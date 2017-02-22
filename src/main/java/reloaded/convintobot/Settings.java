package reloaded.convintobot;

import org.json.JSONArray;
import org.json.JSONObject;

public class Settings {
	private boolean phraseStatus;
	private String dir, gToken, tToken, id, chat, botName;
	private String[] admins;
	public boolean loadSettings(){
		try {
			if(!FileO.exist("config.json")){
				Main.logger("File \"config.json\" not found.");
				FileO.newFile("config.json");
				FileO.writer("{", "config.json");
				FileO.addWrite("config.json", "    \"gToken\" : \"INSERIT YOUR GOOGLE APIS TOKEN HERE\",");
				FileO.addWrite("config.json", "    \"tToken\" : \"INSERIT YOUR TELEGRAM TOKEN HERE\",");
				FileO.addWrite("config.json", "    \"id\" : \"INSERIT YOUR CHANNEL ID HERE\",");
				FileO.addWrite("config.json", "    \"chat\" : \"INSERIT THE CHAT ID HERE\",");
				FileO.addWrite("config.json", "    \"botName\" : \"INSERIT THE NAME OF YOUR BOT HERE\",");
				FileO.addWrite("config.json", "    \"admins\" : [");
				FileO.addWrite("config.json", "        \"INSERIT THE TELEGRAM ACCOUNT ID OF YOUR FIRST ADMIN HERE\",");
				FileO.addWrite("config.json", "        \"INSERIT THE TELEGRAM ACCOUNT ID OF YOUR SECOND ADMIN HERE\",");
				FileO.addWrite("config.json", "        \"...\",");
				FileO.addWrite("config.json", "    ]");
				FileO.addWrite("config.json", "}");
				Main.logger("File \"config.json\" created. Edit your settings and then turn on again the bot.");
				return false;
			}
			
			Main.loggerL("Loading configs... ");
			JSONObject config = new JSONObject(FileO.reader("config.json"));
			gToken = config.getString("gToken");
			tToken = config.getString("tToken");
			id = config.getString("id");
			chat = config.getString("chat");
			botName = config.getString("botName");
			Main.logger("done");
			
			
			dir = System.getProperty("user.dir");
			
			JSONArray admin = config.getJSONArray("admins");
			String[] localAdmins = new String[admin.length()];
			for(int i = 0; i < localAdmins.length; i++) localAdmins[i] = admin.getJSONObject(i).toString();
			admins = localAdmins;
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public String[] getAdmins(){
		return admins;
	}
	public String getBotName(){
		return botName;
	}
	public String getChatId(){
		return chat;
	}
	public String getChannelId(){
		return id;
	}
	public String getTelegramToken(){
		return tToken;
	}
	public String getGoogleToken(){
		return gToken;
	}
	public String getDefaultDirectory(){
		return dir;
	}
	public boolean getPhraseStatus(){
		return phraseStatus;
	}
	public void setPhraseStatus(boolean status){
		phraseStatus = status;
	}
	public void removeDirectory(){
		dir = "";
	}
	public String getGoogleApiFullUrl(int n){
		return "https://www.googleapis.com/youtube/v3/search?part=snippet&channelId=" + id + "&order=date&type=video&key=" + gToken + "&maxResults=" + n;
	}
}
