package reloaded.convintobot;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;

import reloaded.convintobot.tResponse.Commands;

public class ExceptionAlert {
	private Info i = new Info();
	private TelegramBot bot;
	private Commands stat;
	private Twitch t;
	private Phrase f;
	
	public ExceptionAlert(TelegramBot b, ArrayList<Commands> c){
		i.update(0, Main.st);
		bot = b;
		for(Commands cmd : c){
			if(cmd.isThisCommand("/stat", Main.st.getUser())) stat = cmd;
		}
	}
	
	public void alert(Exception e){
		
		try{
			e.printStackTrace();
			bot.execute(new SendMessage("-169611331", "An exception occurred\n\n" + stat.getString("/stat low", Main.st, i, t, f) + "\nException data:\n" + getThrow(e)));
		}catch(Exception sfiga){ //qui sarebbe il classisco "unexpected error occured while displaying an unexpected error"
			Main.wait(5000);
			sfiga.printStackTrace();
			alert(e);
		}
		
	}
	
	private String getThrow(Exception e){
		
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
		
	}
}
