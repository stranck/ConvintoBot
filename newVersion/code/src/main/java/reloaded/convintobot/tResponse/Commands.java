package reloaded.convintobot.tResponse;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendAudio;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;

import reloaded.convintobot.FileO;
import reloaded.convintobot.Info;
import reloaded.convintobot.Main;
import reloaded.convintobot.Phrase;
import reloaded.convintobot.Settings;
import reloaded.convintobot.Twitch;


public class Commands {
	private int commandType; //0 = bot operation; 1 = text; 2 = pic; 3 = song; 4 inline = keyboard;
	private boolean randomResponse, webPagePreview, onlyAdmin;
	private String command;
	private Response response = new Response(); // /test a
	ArrayList<ArrayList<Inline>> inline = new ArrayList<ArrayList<Inline>>();
	
	public Commands(String cmd){
		try{
			Main.LOGGER.config("Loading command: " + cmd);
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
			
			if(commandType == 4) {
				JSONArray il = json.getJSONArray("inline");
				for(int i = 0; i < il.length(); i++) inline.add(getArrayList(il.getJSONArray(i)));
			}
			
		}catch(Exception e){
			Main.LOGGER.warning("Error while loading command " + cmd);
			e.printStackTrace();
		}
	}
	
	public boolean isThisCommand(String cmd, String botUser){
		try{
			String[] sp = cmd.split("\\s+");
			if(command.equalsIgnoreCase("/start") && sp.length > 1) return false;
			return sp[0].equalsIgnoreCase(command) || (sp[0].equalsIgnoreCase("/start") && sp[1].equalsIgnoreCase(command.substring(1, command.length()))) || sp[0].equalsIgnoreCase(command + "@" + botUser);
		}catch(Exception e){}
		return false;
	}
	public int commandExecute(String cmd, TelegramBot bot, Update update, Settings st, Info i, Twitch t, Phrase f, boolean admin){
		try{
			String send = getString(cmd, st, i, t, f);

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
					case 4: {
						String sp[] = cmd.split("\\s+");
						int n;
						if(sp.length > 1) n = response.getArgs().getInt(sp[1]); else n = 0;
						ArrayList<InlineKeyboardButton> ikb = new ArrayList<InlineKeyboardButton>();
						for(Inline in : inline.get(n)) ikb.add(in.getButton());
						bot.execute(new SendMessage(update.message().chat().id().toString(), send).replyMarkup(new InlineKeyboardMarkup(ikb.toArray(new InlineKeyboardButton[ikb.size()]))).parseMode(ParseMode.HTML).disableWebPagePreview(webPagePreview));
					}
			}
		}catch(Exception e){Main.ea.alert(e);}
		return 0;
	}
	
	public String getString(String cmd, Settings st, Info i, Twitch t, Phrase f){
		String sp[] = cmd.split("\\s+");
		Random r = new Random();
		String send;

		if(!randomResponse) 
			if(sp.length > 1 && !sp[0].equalsIgnoreCase("/start")) send = response.getResponse().get(response.getArgs().getInt(sp[1]));
				else send = response.getResponse().get(0);
		else send = response.getResponse().get(r.nextInt(response.getResponse().size()));
		return send
				.replaceAll("%phraseStatus%", Arrays.toString(st.getPhraseStatus()))
				.replaceAll("%gToken%", st.getGoogleToken())
				.replaceAll("%tToken%", st.getTelegramToken())
				.replaceAll("%id%", st.getChannelId())
				.replaceAll("%chat%", st.getChatId())
				.replaceAll("%botName%", st.getBotName())
				.replaceAll("%dir%", st.getDefaultDirectory())
				.replaceAll("%uptime%", st.getUpTime())
				.replaceAll("%lastvideo%", last(i, f, st, t))
				.replaceAll("%twitch%", twitch(t, st, f))
				.replaceAll("%version%", Main.version)
				.replaceAll("%programmed%", String.valueOf(FileO.exist("programmed.ini")));
	}
	
	private String twitch(Twitch t, Settings st, Phrase f){
		int i = 7;
		if(t.getIfInLive()) i--;
		return f.getSinglePhrases(i, st, t) + FileO.toHtml(t.getGame()) + "\n" + st.getTwitchClickableTitle(t.getTitle());
	}
	
	private String last(Info i, Phrase f, Settings st, Twitch t){
		return f.getSinglePhrases(Main.convertType(i.getVideoId()), st, t) + "\n" + Main.convertToLink(i.getVideoId(), i.getVideoName());
	}
	
	private ArrayList<Inline> getArrayList(JSONArray il){
		ArrayList<Inline> ret = new ArrayList<Inline>();
		for(int i = 0; i < il.length(); i++)
			ret.add(new Inline(il.getJSONObject(i).getString("text"), il.getJSONObject(i).getString("data"), il.getJSONObject(i).getInt("type")));
		return ret;
	}
}
