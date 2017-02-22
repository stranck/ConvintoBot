package reloaded.convintobot;

import java.text.SimpleDateFormat;
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
		
		byte checkUpdate = 0x11, switchLiveVideo = 0x00;
		
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
				if()
				checkUpdate = 0x11;
			}
			//telegram
			wait(500);
		}
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
