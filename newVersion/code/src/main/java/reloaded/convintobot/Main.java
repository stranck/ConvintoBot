package reloaded.convintobot;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.ConnectException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.GetMe;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.KickChatMember;
import com.pengrad.telegrambot.request.PinChatMessage;
import com.pengrad.telegrambot.request.RestrictChatMember;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetUpdatesResponse;

import reloaded.convintobot.Main;
import reloaded.convintobot.tResponse.Commands;

public class Main {
	
	
	public static final Logger LOGGER = Logger.getLogger(Main.class.getName());
	public static final String version = "official2.3.08222017"; //MMddYYYY
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
		ArrayList<GroupUser> gus = null;
		
		byte checkUpdate = 0x01, maxCheckUpdate = 0x11;//, switchLiveVideo; //0 = none, 1 = videoLive, 2 = videoUpcoming, 3 = live, 4 = upcoming
		boolean switchLiveVideo = true, logAllUserInfo = false;
		int liveIndex = 0, offset = 0, millsDelay = 500;
		
		if(!st.loadSettings(startTime)) {
			LOGGER.severe("Error while loading settings file");
			return;
		}
		ArrayList<Commands> c = initializeCmds();
		if(st.getIfManageGroup()) gus = initializeGroupUser();
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
				
				if(st.getIfManageGroup()){
					//manage group
					for(int n = 0; n < gus.size(); n++){
						if(gus.get(n).checkIfTimedOut(st)){
							LOGGER.info(gus.get(n).getId() + ": Kicking from the group");
							getCommand("/sub", c, st).commandExecute("/sub kicked", bot, gus.get(n).getId(), st, i, t, f, false);
							bot.execute(new KickChatMember(st.getManageGroupId(), gus.get(n).getNumericId()));
							gus.get(n).delete();
							gus.remove(n);
						} else if(gus.get(n).checkIfExpired()){
							LOGGER.info(gus.get(n).getId() + ": Sub expired");
							getCommand("/sub", c, st).commandExecute("/sub expired", bot, gus.get(n).getId(), st, i, t, f, false);
							gus.get(n).setIfExpiredIsNotified(true);
							gus.get(n).setIfAlreadyNotified(true);
							gus.get(n).save();
						} else if(gus.get(n).checkIfNotify(st)){
							LOGGER.info(gus.get(n).getId() + ": Notifying for imminent sub expire");
							getCommand("/sub", c, st).commandExecute("/sub notify", bot, gus.get(n).getId(), st, i, t, f, false);
							gus.get(n).setIfAlreadyNotified(true);
							gus.get(n).save();
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
    					
        				if(st.getIfManageGroup() && update.message().chat().id().toString().equals(st.getManageGroupId()) && !FileO.exist("sub" + File.separator + update.message().from().id())){	
        					LOGGER.info(update.message().from().id() + ": New unregistred user");
        					gus.add(new GroupUser(update.message().from().id().toString(), System.currentTimeMillis()));
        					gus.get(gus.size() - 1).setIfAlreadyNotified(true);
        					gus.get(gus.size() - 1).setIfExpiredIsNotified(true);
        					gus.get(gus.size() - 1).save();
        					bot.execute(new RestrictChatMember(st.getManageGroupId(), update.message().from().id()).canSendMessages(!st.getIfMuteUntilLogin()));
        					getCommand("/sub", c, st).commandExecute("/sub new", bot, gus.get(gus.size() - 1).getId(), st, i, t, f, false);
        				}
    					
    				}catch(Exception e) {}
    				
    				for(Commands cmd : c){
    					if(text != null && cmd.isThisCommand(text, st.getUser(), st)){
    						boolean admin = st.getAdmins().contains(update.message().from().id().toString());
    						int response = cmd.commandExecute(text, bot, update.message().chat().id().toString(), st, i, t, f, admin);
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
        							bot.execute(new SendMessage(update.message().chat().id().toString(), "Done"));
    								break;
    							}
    							case 2: { //reload
    								
    								switch(sp[2]){
    								
    									case"Settings": {st.loadSettings(startTime); break;}
    									case"Phrases": 	{st.setPhraseStatus(f.initialize()); break;}
    									case"Commands": {c = initializeCmds(); break;}
    									default: bot.execute(new SendMessage(update.message().chat().id().toString(), "Method not found"));
    									
    								}
        							bot.execute(new SendMessage(update.message().chat().id().toString(), "Done"));
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
        							bot.execute(new SendMessage(update.message().chat().id().toString(), "Done"));
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
        							bot.execute(new SendMessage(update.message().chat().id().toString(), "Done"));
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
	    							bot.execute(new SendMessage(update.message().chat().id().toString(), "Done"));
	    							break;
    							}
    							case 6: { //twverify
    								Commands twCommand = getCommand("/subMessages", c, st);
    								twCommand.commandExecute("/subMessages loading", bot, update.message().chat().id().toString(), st, i, t, f, admin);
    								
    								try{
    									String token = sp[1].substring(1, sp[1].length());
    									long expireDate = getUserSub(token, getTwitchUserFromOauth(token), st);
    									if(expireDate > 0) {
    										//expireDate += 3024000000L;
    										
    										if(!FileO.exist("sub" + File.separator + update.message().from().id())) {
    											FileO.newFile("sub" + File.separator + update.message().from().id());
    										} else gus.remove(getGroupUserIndexById(update.message().from().id().toString(), gus));
    										
    										gus.add(new GroupUser(update.message().from().id().toString(), expireDate));
    			        					bot.execute(new RestrictChatMember(st.getManageGroupId(), update.message().from().id())
    			        							.canSendMessages(true)
    			        							.canSendMediaMessages(true)
    			        							.canSendOtherMessages(true)
    			        							.canAddWebPagePreviews(true));
    										twCommand.commandExecute("/subMessages done", bot, update.message().chat().id().toString(), st, i, t, f, admin);
    										bot.execute(new SendMessage(update.message().from().id().toString(), "v              Link              v")
    												.replyMarkup(new InlineKeyboardMarkup(new InlineKeyboardButton[]{new InlineKeyboardButton("Click me >.<").url(st.getGroupLink())})));
    										LOGGER.info(update.message().from().id() + ": User registrated. Subbed until " + expireDate);
    									} else {
    			        					LOGGER.info(update.message().from().id() + ": Error while registrating");
    										twCommand.commandExecute("/subMessages error", bot, update.message().chat().id().toString(), st, i, t, f, admin);
    									}
    								}catch(Exception e){
    									bot.execute(new SendMessage(update.message().chat().id(), "En error occurred:\n" + e));
    									ea.alert(e);
    								}
    							}
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
	
	public static ArrayList<GroupUser> initializeGroupUser(){
		ArrayList<GroupUser> gu = new ArrayList<GroupUser>();
		File[] listOfFiles = new File("sub" + File.separator).listFiles();
		for(File f : listOfFiles) {
			String id = getNameFromPath(f.getName());
			LOGGER.config("Loading sub: " + id);
			try{ gu.add(new GroupUser(id, FileO.reader("sub" + File.separator + id))); } catch (Exception e) {LOGGER.warning("Error: " + e);}
		}
		return gu;
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
	
	public static String getTwitchUserFromOauth(String token) throws Exception{
		JSONObject response = new JSONObject(Download.dwn("https://api.twitch.tv/kraken/user?oauth_token=" + token));
		return response.getString("name");
	}
	
	public static long getUserSub(String token, String user, Settings s) throws ConnectException, JSONException, InvocationTargetException{
		JSONObject response;
		try{
			response = new JSONObject(Download.dwn("https://api.twitch.tv/kraken/users/" + user + "/subscriptions/" + s.getTwitchChannel() + "?oauth_token=" + token));
		}catch(Exception e){
			return 0;
		}
		long expireDate = Instant.parse(response.getString("created_at")).toEpochMilli() + 2937600000L;
		while(System.currentTimeMillis() > expireDate) expireDate += 2592000000L;
		return expireDate;
	}
	
	public static int getGroupUserIndexById(String id, ArrayList<GroupUser> gus){
		int i = 0;
		for(GroupUser gu : gus) {
			if(gu.getId().equals(id)) break;
			i++;
		}
		return i;
	}
	
	public static Commands getCommand(String command, ArrayList<Commands> c, Settings s){
		Commands ret = null;
		for(Commands cmd : c){
			if(cmd.isThisCommand(command, st.getUser(), s)) {
				ret = cmd;
				break;
			}
		}
		return ret;
	}
	
	public static String replaceRuntimeData(String str, Info i, Phrase f, Twitch t, Settings s, String id) throws IOException{
		return str
			.replaceAll("%phraseStatus%", Arrays.toString(s.getPhraseStatus()))
			.replaceAll("%gToken%", s.getGoogleToken())
			.replaceAll("%tToken%", s.getTelegramToken())
			.replaceAll("%twToken%", st.getTwitchToken())
			.replaceAll("%id%", s.getChannelId())
			.replaceAll("%chat%", s.getChatsId().replace("]", "").replace("[", ""))
			.replaceAll("%botName%", s.getBotName())
			.replaceAll("%botUsername%", s.getUser())
			.replaceAll("%dir%", s.getDefaultDirectory())
			.replaceAll("%uptime%", s.getUpTime())
			.replaceAll("%twitchUser%", s.getTwitchChannel())
			.replaceAll("%lastvideo%", last(i, f, s, t))
			.replaceAll("%twitch%", twitch(t, s, f))
			.replaceAll("%version%", version)
			.replaceAll("%sub%", getSubStatus(id))
			.replaceAll("%lang%", st.getLoginPageLang())
			.replaceAll("%programmed%", String.valueOf(FileO.exist("programmed.ini")));
	}
	
	public static String twitch(Twitch t, Settings s, Phrase f) throws IOException{
		int i = 7;
		if(t.getIfInLive()) i--;
		return f.getSinglePhrases(i, s, t, false) + "\n" + s.getTwitchClickableTitle(t.getTitle());
	}
	
	public static String last(Info i, Phrase f, Settings s, Twitch t) throws IOException{
		return f.getSinglePhrases(convertType(i.getVideoId()), s, t, false) + "\n" + convertToLink(i.getVideoId(), i.getVideoName());
	}
	public static String getSubStatus(String id) throws IOException{
		File[] listOfFiles = new File("sub" + File.separator).listFiles();
		for(File f : listOfFiles) {
			String user = getNameFromPath(f.getName());
			if(user.equals(id)) {
				long expireDate = new GroupUser(user, FileO.reader("sub" + File.separator + user)).getExpireDate();
				if(System.currentTimeMillis() < expireDate)
					return "Abbonato - Scade il " + new SimpleDateFormat("dd/MM/yy HH:mm").format(new Date(expireDate));
				return "Non abbonato";
			}
		}
		return "-";
	}
	public static String remainTime(long ms){
		long estimatedTime = (System.currentTimeMillis() - ms) / 1000;
		int hours = (int) estimatedTime / 3600;
	    int secs = (int) estimatedTime - hours * 3600;
	    int mins = secs / 60;
	    secs = secs - mins * 60;
	    return hours + ":" + mins + ":" + secs;
	}
}
