package it.TetrisReich.ZombieHunterBot;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.SendAudio;
import com.pengrad.telegrambot.request.SendPhoto;
import com.pengrad.telegrambot.response.GetUpdatesResponse;

public class App{
	public static int[] oldId = new int[128];
	public static final String[] url = reader("urls.txt").split(";");
	public static final String[] aud = reader("audi.txt").split(";");
    public static void main(String[] args){
    	empty();
    	TelegramBot bot = TelegramBotAdapter.build(args[0]);
    	boolean b = true;
    	Random generator = new Random();
    	System.out.println("Bot started. " + args[0]);
    	while(true){try{
    		GetUpdatesResponse updatesResponse = bot.execute(new GetUpdates());
			List<Update> updates = updatesResponse.updates();
			for(Update update : updates) {
				Message msg = update.message();
				if(!isIn(update.updateId())){
					if(b) {empty(); b = false;}
					add(update.updateId());
					if(msg.text().equalsIgnoreCase("/armi")||msg.text().equalsIgnoreCase("/start armi")){
						int r = generator.nextInt(url.length);
						bot.execute(new SendPhoto(msg.chat().id(), url[r]));
						System.out.println("Request weapon: " + url[r]);
					}
					if(msg.text().equalsIgnoreCase("/song")||msg.text().equalsIgnoreCase("/start song")){
						int r = generator.nextInt(aud.length);
						bot.execute(new SendAudio(msg.chat().id(), aud[r]));
						System.out.println("Request song:   " + aud[r]);
					}
				} else b = true; 
				System.out.println("[" + update.updateId() + "]" + msg.chat().id().toString() + "> "+ msg.text());
			}
			try{
    		    Thread.sleep(300);
    		} catch(InterruptedException ex){
    		    Thread.currentThread().interrupt();
    		}}catch(NullPointerException e) {e.printStackTrace();}
    	}
    }	
    public static void add(int n){
    	for(int i=0;i<oldId.length;i++) if(oldId[i]==0) {oldId[i] = n; return;}
    }
    public static void empty(){
    	for(int i=0;i<oldId.length;i++) oldId[i] = 0;
    }
	public static boolean isIn(int n){
		for(int i=0;i<oldId.length;i++) if(oldId[i]==n) return true;
		return false;
	}
    public static String reader(String path) {
        try {
        	String ret = "";
        	try (BufferedReader br = new BufferedReader(new FileReader(path))) {
        	    String line;
        	    while ((line = br.readLine()) != null) {
        	       ret += line;
        	    }
        	}
        	return ret;
		} catch (IOException e) {
			e.printStackTrace();
		}
        return null;
    }
}