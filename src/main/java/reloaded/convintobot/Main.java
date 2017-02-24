package reloaded.convintobot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;

public class Main {
	
	public static final String version = "official2.0.02222017"; //MMddYYYY
	public static boolean
			link = false,
			stat = false,
			skipDefaultDirectory = false,
			skipOnlineCheck = false,
			tempLine = true;
	public static Settings st = new Settings();
	
	public static void main(String[] args){
		
		Youtube yt = new Youtube();
		Phrase f = new Phrase();
		Info i = new Info();
		ArrayList<Live> l = new ArrayList<Live>();
		
		byte checkUpdate = 0x11;//, switchLiveVideo; //0 = none, 1 = videoLive, 2 = videoUpcoming, 3 = live, 4 = upcoming
		boolean switchLiveVideo = true;
		int liveIndex = 0;
		
		if(!st.loadSettings()) {
			logger("Error while loading settings file");
			return;
		}
		yt.initialize(st);
		f.initialize();
		TelegramBot bot = TelegramBotAdapter.build(st.getTelegramToken());
		
		for (String s: args) {
        	switch(s){
        		case("-l"): {link = true; break;}
        		case("-s"): {stat = true; break;}
        		case("-d"): {st.removeDirectory(); break;}
        		case("-ml"):{moveLog(); break;}
        		case("-o"): {skipOnlineCheck = true; break;}
        	}
    	}
		
		bot.execute(new SendMessage("-1001063772015" , "*bot is again online on " + st.getChatId() + ".*\n" + "Youtube ID: " + st.getChannelId() + "\n_[version: " + version + "]_\n").parseMode(ParseMode.Markdown));
		
		logger("Startup done!");
		
		
		while(true){
			try{
				if(--checkUpdate == 0x00){
					//youtube
					loggerL("Checking youtube ");
					if(switchLiveVideo || l.size() == 0){
						
						//check video update
						loggerL("video... ");
						i.update(0, st);
						if(yt.newVideo(i.getVideoId())){
							
							//new video founded
							loggerL("NEW ");
							String oldMessageData[] = FileO.reader("last.ini").split("@"); //edit previous message for less spam in chat
							bot.execute(new EditMessageText(st.getChatId(), Integer.parseInt(oldMessageData[2]), convertToLink(oldMessageData[1], oldMessageData[0])).parseMode(ParseMode.HTML).disableWebPagePreview(true));
							
							int type = convertType(i.getVideoType()); //stuff & get if any phrase is programmed
							String mText = f.getSinglePhrases(type, st);
							if(FileO.exist("programmed.ini")) {
								mText = FileO.toHtml(FileO.reader("programmed.ini"));
								FileO.delater("programmed.ini");
							}
							
							int msId = bot.execute(new SendMessage(st.getChatId(), mText + "\n" + convertToLink(i.getVideoId(), i.getVideoName())).parseMode(ParseMode.HTML)).message().messageId(); //send message and get message id
							
							if(type != 0) {
								l.add(new Live(i.getVideoName(), i.getVideoId(), type, msId)); //if it is a live add a live to the list
								loggerL("LIVE ");
							} else FileO.writer(FileO.toHtml(i.getVideoName() + "@" + i.getVideoId() + "@" + msId), "last.ini");
							
							loggerL(i.getVideoId() + "\nPhrase used: " + mText);
						}
						logger("");
						
					} else {
						
						//check live status
						loggerL("live (" + l.get(liveIndex).getId() + "... ");
						boolean b = false;
						int status = convertType(i.getVideoType(l.get(liveIndex).getId(), st)), msId = l.get(liveIndex).getMessageId();
						String mText = "", id = l.get(liveIndex).getId(), title = l.get(liveIndex).getTitle();
						
						if(status==1&&l.get(liveIndex).getType()==2){
							//live changed his status from upcoming to live
							mText = f.getSinglePhrases(1, st);
							loggerL("Changed from upcoming to live.\nPhrase used: " + mText);
							l.get(liveIndex).setType(status);
							b = true;
						} else if(status==0){
							//live stopped
							mText = f.getSinglePhrases(3, st);
							loggerL("stopped.\nPhrase used: " + mText);
							l.remove(liveIndex);
							b = true;
						}
						
						if(b) bot.execute(new EditMessageText(st.getChatId(), msId, mText + "\n" + convertToLink(id, title)));
						
						if(++liveIndex >= l.size()) liveIndex = 0;
						if(l.size() == 0) switchLiveVideo = true; else switchLiveVideo = !switchLiveVideo;
						logger("");
					}
					checkUpdate = 0x11;
				}
				//telegram
			}catch(Exception e){e.printStackTrace();}	
			wait(500);
		}
	}
	
	public static String convertToLink(String id, String title){
		return "<a href=\"https://youtu.be/" + id + "\">" + FileO.toHtml(title) + "</a>";
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
