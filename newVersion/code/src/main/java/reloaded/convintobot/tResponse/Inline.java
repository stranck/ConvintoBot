package reloaded.convintobot.tResponse;

import java.io.IOException;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;

import reloaded.convintobot.Info;
import reloaded.convintobot.Main;
import reloaded.convintobot.Phrase;
import reloaded.convintobot.Settings;
import reloaded.convintobot.Twitch;

public class Inline {
	private String text, data;
	private byte type; //0 = url, 1 = callBackData, 2 = callBackGame, 3 = switchInlineQuery, 4 = switchInlineQueryCurrentChat
	
	public Inline(String t, String d, int i){
		text = t;
		data = d;
		type = (byte) i;
	}
	
	public InlineKeyboardButton getButton(Info i, Phrase f, Twitch t, Settings s, String id) throws IOException{
		String nText = Main.replaceRuntimeData(text, i, f, t, s, id), nData = Main.replaceRuntimeData(data, i, f, t, s, id);
		switch(type){
			case 0: return new InlineKeyboardButton(nText).url(nData);
			case 1: return new InlineKeyboardButton(nText).callbackData(nData);
			case 2: return new InlineKeyboardButton(nText).callbackGame(nData);
			case 3: return new InlineKeyboardButton(nText).switchInlineQuery(nData);
			case 4: return new InlineKeyboardButton(nText).switchInlineQueryCurrentChat(nData);
		}
		return null;
	}
}
