package reloaded.convintobot.tResponse;

import java.io.File;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendAudio;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;

import reloaded.convintobot.FileO;
import reloaded.convintobot.Info;
import reloaded.convintobot.Main;
import reloaded.convintobot.Settings;


public class Commands {
	private int commandType; //0 = bot operation; 1 = text; 2 = pic; 3 = song; 4 inline = keyboard;
	private boolean randomResponse, webPagePreview, onlyAdmin;
	private String command;
	private Response response = new Response(); // /test a
	public Commands(String cmd){
		try{
			JSONObject data = new JSONObject(FileO.allLine("commands" + File.separator + cmd + ".json"));
		
			commandType = data.getInt("commandType");
			randomResponse = data.getBoolean("randomResponse");
			webPagePreview = data.getBoolean("DisableWebPagePreview");
			onlyAdmin = data.getBoolean("admin");
			command = "/" + cmd;
		
			JSONObject json = data.getJSONObject("responseData");
			JSONArray rsp = json.getJSONArray("response");	
			for(int i = 0; i < rsp.length(); i++) response.addResponse(rsp.getString(i));
			response.setArgs(json.getJSONObject("args"));
			
		}catch(Exception e){
			Main.logger("Error while loading command " + cmd);
			e.printStackTrace();
		}
	}
	
	public boolean isThisCommand(String cmd){
		try{
			return cmd.split("\\s+")[0].equalsIgnoreCase(command) || ("/start" + cmd.split("\\s+")[1]).equalsIgnoreCase(command);
		}catch(Exception e){}
		return false;
	}
	public int commandExecute(String cmd, TelegramBot bot, Update update, Settings st, Info i, boolean admin){
		try{
			String sp[] = cmd.split("\\s+");
			Random r = new Random();
			String send;
		
			if(!randomResponse) 
				if(sp.length > 1) send = response.getResponse().get(response.getArgs().getInt(sp[1]));
					else send = response.getResponse().get(0);
			else send = response.getResponse().get(r.nextInt(response.getResponse().size()));
			send = send
					.replaceAll("%phraseStatus%", String.valueOf(st.getPhraseStatus()))
					.replaceAll("%gToken%", st.getGoogleToken())
					.replaceAll("%tToken%", st.getTelegramToken())
					.replaceAll("%id%", st.getChannelId())
					.replaceAll("%chat%", st.getChatId())
					.replaceAll("%botName%", st.getBotName())
					.replaceAll("%dir%", st.getDefaultDirectory())
					.replaceAll("%uptime%", st.getUpTime())
					.replaceAll("%lastvideo%", last(i))
					.replaceAll("%version%", Main.version);
			
			if((onlyAdmin==admin)||admin)
			switch(commandType){
				case 0: {
					return Integer.parseInt(send);
				}
				case 1: {
					bot.execute(new SendMessage(update.message().chat().id().toString(), send).parseMode(ParseMode.HTML).disableWebPagePreview(webPagePreview));
					break;
				}
				case 2: {
					bot.execute(new SendPhoto(update.message().chat().id().toString(), send));
					break;
				}
				case 3: {
					bot.execute(new SendAudio(update.message().chat().id().toString(), send));
					break;
				}
			}
		}catch(Exception e){Main.logger("" + e);}
		return 0;
	}
	private String last(Info i){
		String ret;
		switch(i.getVideoType()){
			case"live":     ret = "In live ora:";
			case"upcoming": ret = "Live programmata:";
			default: 		ret = "Video:";
		}
		return ret + "\n" + Main.convertToLink(i.getVideoId(), i.getVideoName());
	}
}
