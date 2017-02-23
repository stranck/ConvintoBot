package reloaded.convintobot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.model.request.ParseMode;
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
		
		bot.execute(new SendMessage("-1001063772015" , "*bot is again online on " + st.getChatId() + ".*\n"
    		+ "Youtube ID: " + st.getChannelId() + "\n_[version: " + version + "]_\n").parseMode(ParseMode.Markdown));
		
		logger("Startup done!");
		
		
		while(true){
			if(--checkUpdate == 0x00){
				//youtube
				if(switchLiveVideo || l.size() == 0){
					//check video update
					i.update(0, st);
					
				} else {
					int status = convertType(i.getVideoType(l.get(liveIndex).getId(), st));
					if(status==1&&l.get(liveIndex).getType()==2){
						//live changed his status from upcoming to live
						l.get(liveIndex).setType(status);
					} else if(status==0){
						//live stopped
						l.remove(liveIndex);
					}
					
					if(++liveIndex >= l.size()) liveIndex = 0;
					if(l.size() == 0) switchLiveVideo = true; else switchLiveVideo = !switchLiveVideo;
				}
				checkUpdate = 0x11;
			}
			//telegram
			wait(500);
		}
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
