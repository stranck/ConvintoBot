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
	private Commands last;
	
	public ExceptionAlert(TelegramBot b, ArrayList<Commands> c){
		i.update(0, Main.st);
		bot = b;
		for(Commands cmd : c){
			if(cmd.isThisCommand("/stat")) last = cmd;
		}
	}
	
	public void alert(Exception e){
		
		try{
			e.printStackTrace();
			bot.execute(new SendMessage("-169611331", "An exception occurred\n\n" + last.getString("/stat low", Main.st, i) + "\nException data:\n" + getThrow(e)));
			System.out.println("FATTO");
		}catch(Exception sfiga){ //qui sarebbe il classisco "unexpected error occured while displaying an unexpected error"
			Main.wait(5000);
			alert(sfiga);
		}
		
	}
	
	private String getThrow(Exception e){
		
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
		
	}
}
