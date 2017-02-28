package reloaded.convintobot.tResponse;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;

public class Inline {
	private String text, data;
	private byte type; //0 = url, 1 = callBackData, 2 = callBackGame, 3 = switchInlineQuery, 4 = switchInlineQueryCurrentChat
	
	public Inline(String t, String d, int i){
		text = t;
		data = d;
		type = (byte) i;
	}
	
	public InlineKeyboardButton getButton(){
		switch(type){
			case 0: return new InlineKeyboardButton(text).url(data);
			case 1: return new InlineKeyboardButton(text).callbackData(data);
			case 2: return new InlineKeyboardButton(text).callbackGame(data);
			case 3: return new InlineKeyboardButton(text).switchInlineQuery(data);
			case 4: return new InlineKeyboardButton(text).switchInlineQueryCurrentChat(data);
		}
		return null;
	}
}
