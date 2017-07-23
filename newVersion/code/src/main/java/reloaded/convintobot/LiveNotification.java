package reloaded.convintobot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;

public class LiveNotification implements Runnable {
	
	private TelegramBot bot;
	private String title, chat, f;
	
	public LiveNotification(TelegramBot b, String p, String t, String c){
		bot = b;
		f = p;
		title = t;
		chat = c;
	}
	
	public void run() {
		try {			
			SendResponse sr = bot.execute(new SendMessage(chat, f + "\n" + title));
			Main.wait(1500);
			bot.execute(new DeleteMessage(chat, 
					sr.message().messageId()));
			
		} catch (Exception e) {
			e.printStackTrace();
			Main.ea.alert(e);
		}
	}
}
