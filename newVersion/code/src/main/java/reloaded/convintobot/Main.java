package reloaded.convintobot;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.GetMe;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetUpdatesResponse;

import reloaded.convintobot.tResponse.Commands;

public class Main {
	
	public static final String version = "official2.1.03222017"; //MMddYYYY
	public static boolean link = false, tempLine = true;
	public static Settings st = new Settings();
	public static ExceptionAlert ea;
	
	public static void main(String[] args){
		
		long startTime = System.currentTimeMillis();
		
		if(!FileO.pathExist("command")) FileO.newPath("commands");
		
		Youtube yt = new Youtube();
		Phrase f = new Phrase();
		Info i = new Info();
		ArrayList<Live> l = new ArrayList<Live>();
		
		byte checkUpdate, maxCheckUpdate = 0x11;//, switchLiveVideo; //0 = none, 1 = videoLive, 2 = videoUpcoming, 3 = live, 4 = upcoming
		boolean switchLiveVideo = true;
		int liveIndex = 0, offset = 0, millsDelay = 500;
		
		if(!st.loadSettings(startTime)) {
			logger("Error while loading settings file");
			return;
		}
		ArrayList<Commands> c = initializeCmds();
		yt.initialize(st);
		f.initialize();
		TelegramBot bot = TelegramBotAdapter.build(st.getTelegramToken());
		ea = new ExceptionAlert(bot, c);
		st.setUser(bot.execute(new GetMe()).user().username());
		if(st.getWhatBotName()) st.setBotName(bot.execute(new GetMe()).user().firstName());
		
		for (int n = 0; n < args.length; n++) {
        	switch(args[n]){
        		case"-l": {link = true; break;}
        		case"-d": {st.removeDirectory(); break;}
        		case"-ml":{moveLog(); break;}
        		case"-dg":{maxCheckUpdate = Byte.valueOf(args[++n]); break;}
        		case"-dt":{millsDelay = Integer.parseInt(args[++n]); break;}
        	}
    	}
        checkUpdate = maxCheckUpdate;
        	
		bot.execute(new SendMessage("-1001063772015" , "*bot is again online on " + st.getChatId() + ".*\n"
    		+ "Youtube ID: " + st.getChannelId() + "\n_[version: " + version + "]_\n").parseMode(ParseMode.Markdown));
		
		logger("Startup done!");
		
		
		while(true){
			try{
				if(--checkUpdate == 0x00){
					//youtube
					loggerL("Checking youtube ");
					if(switchLiveVideo){
						
						//check video update
						loggerL("video... ");
						i.update(0, st);
						if(yt.newVideo(i.getVideoId())){
							
							//new video founded
							loggerL("NEW ");
							String oldMessageData[] = FileO.reader("last.ini").split("@"); //edit previous message for less spam in chat
							bot.execute(new EditMessageText(st.getChatId(), Integer.parseInt(oldMessageData[2]),
									convertToLink(oldMessageData[1], oldMessageData[0])).parseMode(ParseMode.HTML).disableWebPagePreview(true));
							
							int type = convertType(i.getVideoType()); //stuff & get if any phrase is programmed
							String mText = f.getSinglePhrases(type, st);
							if(FileO.exist("programmed.ini")) {
								mText = FileO.reader("programmed.ini");
								FileO.delater("programmed.ini");
							}
							
							int msId = bot.execute(new SendMessage(st.getChatId(), mText + "\n" + //send message
									convertToLink(i.getVideoId(), FileO.toHtml(i.getVideoName()))).parseMode(ParseMode.HTML)).message().messageId();
							
							if(type != 0) {
								l.add(new Live(i.getVideoName(), i.getVideoId(), type, msId)); //if it is a live add a live to the list
								loggerL("LIVE ");
							} else FileO.writer(FileO.toHtml(i.getVideoName()) + "@" + i.getVideoId() + "@" + msId, "last.ini");
							
							loggerL(i.getVideoId() + "\nPhrase used: " + mText);
						}
						if(l.size() > 0) switchLiveVideo = false;
						logger("");
						
					} else {
						
						//check live status
						loggerL("live (" + l.get(liveIndex).getId() + ")... ");
						int status = convertType(i.getVideoType(l.get(liveIndex).getId(), st)), msId = l.get(liveIndex).getMessageId();
						String mText = "", id = l.get(liveIndex).getId(), title = l.get(liveIndex).getTitle();
						
						if(FileO.exist("programmed.ini")) {
							mText = FileO.reader("programmed.ini");
							FileO.delater("programmed.ini");
						}
						
						if(status==1&&l.get(liveIndex).getType()==2){
							//live changed his status from upcoming to live
							mText = f.getSinglePhrases(1, st);
							loggerL("Changed from upcoming to live.\nPhrase used: " + mText);
							l.get(liveIndex).setType(status);
							bot.execute(new EditMessageText(st.getChatId(), msId, mText + "\n" + convertToLink(id, FileO.toHtml(title))).parseMode(ParseMode.HTML));
						} else if(status==0){
							//live stopped
							mText = f.getSinglePhrases(3, st);
							loggerL("stopped.\nPhrase used: " + mText);
							l.remove(liveIndex);
							bot.execute(new EditMessageText(st.getChatId(), msId, mText + "\n" + convertToLink(id, FileO.toHtml(title))).parseMode(ParseMode.HTML).disableWebPagePreview(true));
						}
						
						if(++liveIndex >= l.size()) liveIndex = 0;
						switchLiveVideo = true;
						logger("");
					}
					checkUpdate = maxCheckUpdate;
				}
				//telegram
				
				GetUpdatesResponse updatesResponse = bot.execute(new GetUpdates().offset(offset));
    			List<Update> updates = updatesResponse.updates();
    			for(Update update : updates) {
    				
    				offset = update.updateId() + 1;
    				String text = null;
    				try{
    					text = update.message().text();
    					logger(update.message().chat().id() + ">" + text);
    				}catch(Exception e) {}
    				
    				for(Commands cmd : c){
    					if(text != null && cmd.isThisCommand(text, st.getUser())){
    						boolean admin = st.getAdmins().contains(update.message().from().id().toString());
    						int response = cmd.commandExecute(text, bot, update, st, i, admin);
    						
    						if(admin){
    							String[] sp = text.split("\\s+");
    							
    							switch(response){
    								case 0: break;
    								
    								case 1: { //force
    									switch(sp[2]){
    									
    										case"reboot": {
    											logger("Forced reboot from ad admin");
    											return;
    										}
    										case"vUpdate":{yt.forceVideoUpdate(true); break;}
    										
    									}
    									break;
    								}
    								case 2: { //reload
    									
    									switch(sp[2]){
    									
    										case"Settings": {st.loadSettings(startTime); break;}
    										case"Phrases": 	{f.initialize(); break;}
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
										
    									switch(sp[2]){
    										
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
    									
    									}
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
										
    									switch(sp[2]){
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
    									}
    									
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
	
	public static void moveLog(){
    	int i = 0;
    	Main.loggerL("Moving log from CB_old.txt to log/CB");
    	while(true){
    		if(!FileO.exist("log/CB" + i + ".txt")){
    			FileO.rename("CB_old.txt", "log/CB" + i + ".txt");
    			logger(i + ".txt");
    			return;
    		} i++;
    	}
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
	
    public static void logger(String testo){
    	String s = "";
    	if(tempLine) s = "[" + time("dd-HH:mm:ss") + "] ";
    	System.out.println(s + testo);
    	tempLine = true;
    }
    public static void loggerL(String testo){
    	String s = "";
    	if(tempLine) s = "[" + time("dd-HH:mm:ss") + "] ";
    	System.out.print(s + testo);
    	tempLine = false;
    }
}
