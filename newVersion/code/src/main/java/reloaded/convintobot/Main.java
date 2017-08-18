package reloaded.convintobot;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.GetMe;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.PinChatMessage;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetUpdatesResponse;

import reloaded.convintobot.Main;
import reloaded.convintobot.tResponse.Commands;

public class Main {
	
	
	public static final Logger LOGGER = Logger.getLogger(Main.class.getName());
	public static final String version = "official2.3.08092017"; //MMddYYYY
	public static Settings st = new Settings();
	public static ExceptionAlert ea;
	
	public static void main(String[] args) throws SecurityException, IOException{
		
		Handler handler = new ConsoleHandler();
    	handler.setLevel(Level.FINE);
    	handler.setFormatter(new Format());
    	LOGGER.addHandler(handler);
    	LOGGER.setLevel(Level.FINE);
    	LOGGER.setUseParentHandlers(false);
    	LOGGER.config("Starting up");
    	FileHandler fh = new FileHandler("log.txt", true);
		fh.setFormatter(new Format());
		LOGGER.addHandler(fh);
		
		long startTime = System.currentTimeMillis();
		
		if(!FileO.pathExist("command")) FileO.newPath("commands");
		
		Youtube yt = new Youtube();
		Phrase f = new Phrase();
		Info i = new Info();
		ArrayList<Live> l = new ArrayList<Live>();
		
		byte checkUpdate = 0x01, maxCheckUpdate = 0x11;//, switchLiveVideo; //0 = none, 1 = videoLive, 2 = videoUpcoming, 3 = live, 4 = upcoming
		boolean switchLiveVideo = true, logAllUserInfo = false;
		int liveIndex = 0, offset = 0, millsDelay = 500;
		
		if(!st.loadSettings(startTime)) {
			LOGGER.severe("Error while loading settings file");
			return;
		}
		ArrayList<Commands> c = initializeCmds();
		TelegramBot bot = TelegramBotAdapter.build(st.getTelegramToken());
		Twitch t = new Twitch(st);
		ea = new ExceptionAlert(bot, c, f);
		yt.initialize(st);
		st.setPhraseStatus(f.initialize());
		st.setUser(bot.execute(new GetMe()).user().username());
		if(st.getWhatBotName()) st.setBotName(bot.execute(new GetMe()).user().firstName());
		
		for (int n = 0; n < args.length; n++) {
        	switch(args[n]){
        		case"-d": {st.removeDirectory(); break;}
        		case"-dg":{maxCheckUpdate = Byte.valueOf(args[++n]); break;}
        		case"-dt":{millsDelay = Integer.parseInt(args[++n]); break;}
        		case"-au":{logAllUserInfo = true; break;}
        	}
    	}
        	
		bot.execute(new SendMessage("-1001063772015" , "*bot is again online on " + st.getChatsId() + ".*\n"
    		+ "Youtube ID: " + st.getChannelId() + "\n_[version: " + version + "]_\n").parseMode(ParseMode.Markdown));
		
		LOGGER.config("Startup done!");
		
		
		while(true){
			try{
				if(st.getIfYoutube() && --checkUpdate == 0x00){
					//youtube
					LOGGER.finer("Chiecking youtube video... ");
					if(switchLiveVideo){
						
						//check video update
						i.update(0, st);
						if(yt.newVideo(i.getVideoId())){
							
							//new video founded
							LOGGER.info("New update found! " + i.toString());
							lessSpam(bot, st.getChats());
							int type = convertType(i.getVideoType()); //stuff & get if any phrase is programmed
							String mText = f.getSinglePhrases(type, st, t, true);
							
							if(checkYtLiveOrVideoEnabled(st, type)) {
								int msId[] = sendYoutubeMessage(bot, st.getChats(), mText, i);
								pinMessage(bot, st.getChats(), msId, true);
							
								if(type != 0) 
									l.add(new Live(i.getVideoName(), i.getVideoId(), mText, type, msId)); //if it is a live add a live to the list
										else FileO.writer(FileO.toHtml(i.getVideoName()) + "@" + i.getVideoId() + "@" + msIdConverter(msId) + "@" + mText, "last.ini");
							
								LOGGER.fine("Phrase used: " + mText);
							}
						}
						if(l.size() > 0) switchLiveVideo = false;
						
					} else {
						
						//check live status
						LOGGER.finer("Checking: " + l.get(liveIndex).toString());
						int status = convertType(i.getVideoType(l.get(liveIndex).getId(), st));
						int[] msId = l.get(liveIndex).getMessageId();
						String id = l.get(liveIndex).getId(), title = l.get(liveIndex).getTitle();
						
						if(status == 1 && l.get(liveIndex).getType() == 2){
							//live changed his status from upcoming to live
							LOGGER.info("Changed from upcoming to live: " + l.get(liveIndex).toString());
							l.get(liveIndex).setType(status);
							String text = f.getSinglePhrases(1, st, t, true);
							if(!st.getMantainPhrase()) sendUpcomingToLiveMessage(text, l.get(liveIndex), bot, msId, id , title, st.getChats());
							sendLiveNotification(bot, l.get(liveIndex).getTitle(), st.getChats(), text, 0, true);
							
						} else if(status == 1) {
							//live still online
							l.get(liveIndex).setLiveOffline(System.currentTimeMillis());
							if(System.currentTimeMillis() - l.get(liveIndex).getNotificationCycle() > st.getRepeatDelay()){
								sendLiveNotification(bot, l.get(liveIndex).getTitle(), st.getChats(), f.getSinglePhrases(4, st, t, false), 1, true);
								l.get(liveIndex).setNotificationCycle(System.currentTimeMillis());
							}
						} else if(status == 0){
							//live offline
							if(System.currentTimeMillis() - l.get(liveIndex).getLiveOffline() > st.getOfflineDelay()) {
								//live dead
								LOGGER.info("Live stopped: " + l.get(liveIndex).toString());
								if(!st.getMantainPhrase()) sendLiveToStopMessage(st.getChats(), bot, msId, id, title, l.get(liveIndex).getMText(), f.getSinglePhrases(3, st, t, true));
								sendLiveNotification(bot, l.get(liveIndex).getTitle(), st.getChats(), f.getSinglePhrases(5, st, t, true), 2, true);
								l.remove(liveIndex);
							}
						} //live still upcoming
						
						if(++liveIndex >= l.size()) liveIndex = 0;
						switchLiveVideo = true;
					}
					checkUpdate = maxCheckUpdate;
				}
				
				if(st.getIfTwitch()){
					//twitch
					LOGGER.finer("Checking twitch... ");
					switch(t.checkLive(st)){
						case 3: {
							//gone online
							String mText = f.getSinglePhrases(6, st, t, true);
							LOGGER.info("Twitch stream is now online!");
							
							t.setMessageId(sendTwitchMessage(bot, st.getChats(), mText, st.getTwitchClickableTitle(t.getTitle())));
							t.setMText(mText);
							pinMessage(bot, st.getChats(), t.getMessageId(), false);
							//if(st.getIfNotificationOnUpcoming()) sendLiveNotification(bot, f, t.getTitle(), 6, t);
							break;
						}
						case 2: {
							//online
							if(System.currentTimeMillis() - t.getNotificationCycle() > st.getRepeatDelay()){
									sendLiveNotification(bot, t.getTitle(), st.getChats(), f.getSinglePhrases(6, st, t, false), 1, false);
									t.setNotificationCycle(System.currentTimeMillis());
							}
							break;
						}
						case 1: {
							//gone offline
							LOGGER.info("Twitch stream is now offline");
							String text = f.getSinglePhrases(7, st, t, true);
							sendTwitchOfflineMessage(st.getChats(), bot, t, text);
							sendLiveNotification(bot, t.getTitle(), st.getChats(), text, 2, false);
							break;
						}
					}
				}
				
				//telegram
				
		    	LOGGER.finest("Checking Telegram...");
				GetUpdatesResponse updatesResponse = bot.execute(new GetUpdates().offset(offset));
    			List<Update> updates = updatesResponse.updates();
    			for(Update update : updates) {
    				
    				offset = update.updateId() + 1;
    				String text = null;
    				try{
    					text = update.message().text();
    					String log = update.message().chat().id().toString();
    					if(logAllUserInfo) {
    						System.out.println("TEST");
    						log = update.message().toString();
    					}
    					LOGGER.info("[" + log + "] " + text);
    				}catch(Exception e) {}
    				
    				for(Commands cmd : c){
    					if(text != null && cmd.isThisCommand(text, st.getUser())){
    						boolean admin = st.getAdmins().contains(update.message().from().id().toString());
    						int response = cmd.commandExecute(text, bot, update, st, i, t, f, admin);
    						
    						if(admin){
    							String[] sp = text.split("\\s+");
    							
    							switch(response){
    								case 0: break;
    								
    								case 1: { //force
    									if(sp.length > 2) switch(sp[2]){
    									
    										case"reboot": {
    											LOGGER.warning("Forced reboot from ad admin");
    											bot.execute(new GetUpdates().offset(offset));
    											return;
    										}
    										case"vUpdate": {yt.forceVideoUpdate(true); break;}
    										case"noUpdate":{yt.forceNoUpdate(Boolean.parseBoolean(sp[3])); break;}
    										default: bot.execute(new SendMessage(update.message().chat().id().toString(), "Method not found"));
    										
    									} else bot.execute(new SendMessage(update.message().chat().id().toString(), "Too few argouments"));
    									break;
    								}
    								case 2: { //reload
    									
    									switch(sp[2]){
    									
    										case"Settings": {st.loadSettings(startTime); break;}
    										case"Phrases": 	{st.setPhraseStatus(f.initialize()); break;}
    										case"Commands": {c = initializeCmds(); break;}
    										default: bot.execute(new SendMessage(update.message().chat().id().toString(), "Method not found"));
    										
    									}
    									break;
    								}
    								case 3: { //file
    									
    									String all = "";
										for(int n = 4; n < sp.length; n++) all += sp[n] + " ";
										try{
											all = all.substring(0, all.length() - 1);
										}catch(Exception e){}
										
    									if(sp.length > 3) switch(sp[2]){
    										
    										case"newFile": {FileO.newFile(sp[3]); break;}
    										case"newPath": {FileO.newPath(sp[3]); break;}
    										case"edit": {FileO.writer(sp[3], all); break;}
    										case"addLine": {FileO.addWrite(all, sp[3]); break;}
    										case"read": {bot.execute(new SendMessage(update.message().chat().id().toString(), FileO.allLine(sp[3])).parseMode(ParseMode.HTML)); break;}
    										case"delete": {FileO.delater(sp[3]); break;}
    										case"cod": {FileO.toHtml(sp[3]); break;}
    										case"decod": {FileO.fromHtml(sp[3]); break;}
    										case"ls": {bot.execute(new SendMessage(update.message().chat().id().toString(), FileO.ls(sp[3])).parseMode(ParseMode.HTML)); break;}
    										default: bot.execute(new SendMessage(update.message().chat().id().toString(), "Method not found"));
    									
    									} else bot.execute(new SendMessage(update.message().chat().id().toString(), "Too few argouments"));
    									break;
    								}
    								case 4: {//program
    									
    									if(sp.length > 2){
    										String program = "";
    										for(int n = 2; n < sp.length; n++) program += sp[n] + " ";
    										program = program.substring(0, program.length() - 1);
    										
    										if(!FileO.exist("programmed.ini"))
    											FileO.newFile("programmed.ini"); else
    												bot.execute(new SendMessage(update.message().chat().id().toString(), "Delating old programmed phrase (" + FileO.allLine("programmed.ini") + ")").parseMode(ParseMode.HTML));
    										FileO.writer(program, "programmed.ini");
    									} else if(FileO.exist("programmed.ini")){
    										bot.execute(new SendMessage(update.message().chat().id().toString(), "Delating programmed phrase (" + FileO.allLine("programmed.ini") + ")").parseMode(ParseMode.HTML));
    										FileO.delater("programmed.ini");
    									}
    									break;
    									
    								}	
    								case 5: { //html
    									
    									String all = "";
										for(int n = 3; n < sp.length; n++) all += sp[n] + " ";
										try{
											all = all.substring(0, all.length() - 1);
										}catch(Exception e){}
										
										if(sp.length > 2) switch(sp[2]){
    										case"to": {
    											bot.execute(new SendMessage(update.message().chat().id().toString(), FileO.toHtml(all)));
    											break;
    										}
    										case"from": {
    											try{
    												bot.execute(new SendMessage(update.message().chat().id().toString(), FileO.fromHtml(all)));
    											}catch(Exception e){
    												bot.execute(new SendMessage(update.message().chat().title().toString(), "Error: " + e));
    											}
    											break;
    										}
    										default: bot.execute(new SendMessage(update.message().chat().id().toString(), "Method not found"));
    									} else bot.execute(new SendMessage(update.message().chat().id().toString(), "Too few argouments"));
    									
    								}
    							}
    							bot.execute(new SendMessage(update.message().chat().id().toString(), "Done"));
    						}
    						break;
    					}
    				}
    			}
				
			}catch(Exception e){ea.alert(e);}	
			wait(millsDelay);
		}
	}
	
	public static String getNameFromPath(String path){
		String[] file = path.split("\\\\");
		String[] name = file[file.length - 1].split("\\.");
		if(name.length==1) return name[0];
		String ret = "";
		for(int i = 0; i < name.length - 1; i++) ret += name[i] + ".";
		ret = ret.substring(0, ret.length() - 1);
		return ret;
    }
	
	public static String convertToLink(String id, String title){
		return "<a href=\"https://youtu.be/" + id + "\">" + title + "</a>";
	}
	
	public static byte convertType(String type){
		switch(type){
			case"live":     return 1;
			case"upcoming": return 2;
		}
		return 0;
	}
	
	public static ArrayList<Commands> initializeCmds(){
		ArrayList<Commands> c = new ArrayList<Commands>();
		File[] listOfFiles = new File("commands" + File.separator).listFiles();
		for(File f : listOfFiles) c.add(new Commands(getNameFromPath(f.getName())));
		return c;
	}
	
	public static void wait(int ms){
		try{
		    Thread.sleep(ms);
		} catch(Exception ex){
		    Thread.currentThread().interrupt();
		}
	}
	
	public static String time(String format) {
		SimpleDateFormat sdfDate = new SimpleDateFormat(format);
		Date now = new Date();
		String strDate = sdfDate.format(now);
		return strDate;	
	}
	
	public static void lessSpam(TelegramBot bot, ArrayList<Chats> c) throws IOException{
		
		String oldMessageData[] = FileO.reader("last.ini").split("@"); //edit previous message for less spam in chat
		String[] msgIds = oldMessageData[2].split("-");
		for(int i = 0; i < c.size(); i++){
			String option = c.get(i).getLessSpamMethod();
			if(!option.equalsIgnoreCase("none")) {
		
				if(option.equalsIgnoreCase("COMPRESS")) bot.execute(new EditMessageText(c.get(i).getChatId(), Integer.parseInt(msgIds[i]),
						convertToLink(oldMessageData[1], oldMessageData[0])).parseMode(ParseMode.HTML).disableWebPagePreview(true));
		
				else if(option.equalsIgnoreCase("NOWEBPREVIEW")) bot.execute(new EditMessageText(c.get(i).getChatId(), Integer.parseInt(msgIds[i]), 
						oldMessageData[3] + "\n" + convertToLink(oldMessageData[1], oldMessageData[0])).parseMode(ParseMode.HTML).disableWebPagePreview(true));
		
				else if(option.equalsIgnoreCase("DELETE")) bot.execute(new DeleteMessage(c.get(i).getChatId(), Integer.parseInt(msgIds[i])));
			}
		}
	}
	

	public static void sendLiveNotification(TelegramBot bot, String title,  ArrayList<Chats> c, String s, int nType, boolean ytOrTw){
		Main.LOGGER.info("Sending live notification (" + nType + ") with phrase: " + s);
		for(Chats ch : c)
			if(checkNotificationSettings(ch, nType) && checkYtOrTwitchEnabled(ch, ytOrTw)) new Thread(new LiveNotification(bot, s, title, ch.getChatId())).start();
	}
	public static boolean checkNotificationSettings(Chats c, int type){
		switch(type){
			case 0: return c.getIfNotificationOnUpcoming();
			case 1: return c.getIfRepeat();
			case 2: return c.getIfNotificationOnStop();
		}
		return false;
	}
	public static boolean checkYtLiveOrVideoEnabled(Settings s, int type){
		return (type == 0 && s.getIfYoutubeVideo()) || (type > 0 && s.getIfYoutubeLive());
	}
	public static boolean checkYtOrTwitchEnabled(Chats c, boolean b){
		return (b && c.getIfYoutube()) || (!b && c.getIfTwitch());
		//if(b) return c.getIfYoutube();
		//return c.getIfTwitch();
	}
	public static void sendTwitchOfflineMessage(ArrayList<Chats> c, TelegramBot bot, Twitch t, String mText){
		LOGGER.fine("Phrase used: " + mText);
		for(int i = 0; i < c.size(); i++){
			if(c.get(i).getIfTwitch())
				if(c.get(i).getLessSpamMethod().equalsIgnoreCase("COMPRESS")) bot.execute(new EditMessageText(c.get(i).getChatId(), t.getMessageId()[i], mText + "\n" + st.getTwitchClickableTitle(t.getTitle())).parseMode(ParseMode.HTML).disableWebPagePreview(true));
				else if(c.get(i).getLessSpamMethod().equalsIgnoreCase("DELETE")) bot.execute(new DeleteMessage(c.get(i).getChatId(), t.getMessageId()[i]));
				else if(c.get(i).getLessSpamMethod().equalsIgnoreCase("NOWEBPREVIEW")) bot.execute(new EditMessageText(c.get(i).getChatId(), t.getMessageId()[i], t.getMText() + "\n" + st.getTwitchClickableTitle(t.getTitle())).parseMode(ParseMode.HTML).disableWebPagePreview(true));
		}
	}
	public static void sendLiveToStopMessage(ArrayList<Chats> c, TelegramBot bot, int[] msId, String id, String title, String oldMText, String newMText) throws IOException{
		LOGGER.fine("Phrase used: " + newMText);
		for(int i = 0; i < msId.length; i++)
			if(c.get(i).getIfYoutube())
				if(c.get(i).getLessSpamMethod().equalsIgnoreCase("COMPRESS")) bot.execute(new EditMessageText(c.get(i).getChatId(), msId[i], newMText + "\n" + convertToLink(id, FileO.toHtml(title))).parseMode(ParseMode.HTML).disableWebPagePreview(true));
				else if(c.get(i).getLessSpamMethod().equalsIgnoreCase("DELETE")) bot.execute(new DeleteMessage(c.get(i).getChatId(), msId[i]));
				else if(c.get(i).getLessSpamMethod().equalsIgnoreCase("NOWEBPREVIEW"))  bot.execute(new EditMessageText(c.get(i).getChatId(), msId[i], oldMText + "\n" + convertToLink(id, FileO.toHtml(title))).parseMode(ParseMode.HTML).disableWebPagePreview(true));
	}
	public static void sendUpcomingToLiveMessage(String mText, Live l, TelegramBot bot, int[] msId, String id, String title, ArrayList<Chats> c) throws IOException{
		LOGGER.fine("Phrase used: " + mText);
		for(int i = 0; i < msId.length; i++)
			if(c.get(i).getIfYoutube())
				bot.execute(new EditMessageText(c.get(i).getChatId(), msId[i], mText + "\n" + convertToLink(id, FileO.toHtml(title))).parseMode(ParseMode.HTML));
	}
	public static void pinMessage(TelegramBot bot, ArrayList<Chats> c, int[] messageId, boolean twOrYt){
		for(int i = 0; i < messageId.length; i++)
			if(c.get(i).getIfPin() && checkYtOrTwitchEnabled(c.get(i), twOrYt))
				bot.execute(new PinChatMessage(c.get(i).getChatId(), messageId[i]).disableNotification(c.get(i).getPinNotification()));
	}
	public static int[] sendTwitchMessage(TelegramBot bot, ArrayList<Chats> c, String mText, String link){
		int[] msgIds = new int[c.size()];
		LOGGER.fine("Phrase used: " + mText);
		for(int i = 0; i < msgIds.length; i++)
			if(c.get(i).getIfTwitch())
				msgIds[i] = bot.execute(new SendMessage(c.get(i).getChatId(), mText + "\n" + link).parseMode(ParseMode.HTML)).message().messageId();
			else msgIds[i] = 0;
		return msgIds;
	}
	public static int[] sendYoutubeMessage(TelegramBot bot, ArrayList<Chats> c, String mText, Info i){
		int[] msgIds = new int[c.size()];
		for(int n = 0; n < msgIds.length; n++) 
			if(c.get(n).getIfYoutube())
				msgIds[n] = bot.execute(new SendMessage(c.get(n).getChatId(), mText + "\n" + //send message
				convertToLink(i.getVideoId(), FileO.toHtml(i.getVideoName()))).parseMode(ParseMode.HTML)).message().messageId();
			else msgIds[n] = 0;
		return msgIds;
	}
	public static String msIdConverter(int[] msid){
		String ret = "";
		for(int i : msid) ret += i + "-";
		return ret.substring(0, ret.length() - 1);
	}
}
