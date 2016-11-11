package it.TetrisReich.bot.TestBot;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

//import org.apache.commons.lang3.StringEscapeUtils;

import it.TetrisReich.bot.TestBot.App;

public class Chan {
	public static String chan() throws IOException{
		if(!FileO.exist("programmed")){
		if(!App.textEsist) if(App.inLive) return "In live ora:"; else  return "Nuovo video:";
		Random generator = new Random();
		int rand;
		if(App.inLive) rand = generator.nextInt(App.lf); else rand = generator.nextInt(App.vf);
		boolean toke = false;
		try(BufferedReader br = new BufferedReader(new FileReader("text"))) {
			String line;
		    while ((line = br.readLine()) != null) {
		    	String esc = line;
		    	if(App.inLive){
		    		if(toke) if(rand==0) return esc; else rand--;
		    		if(line.equalsIgnoreCase("_")) toke = true;
		    	} else {
		    		if(rand==0) return esc;
		    		rand--;
		    	}
		    }
		}} else {String s = FileO.aL("programmed", false); FileO.delater("programmed"); return s;}
		return null;
	}
	public static void chanSP() throws IOException{
		boolean toke = false;
		try(BufferedReader br = new BufferedReader(new FileReader("text"))) {
		    for(String line; (line = br.readLine()) != null; ) {
		    	if(line.equalsIgnoreCase("_")) toke = true; else {if(toke) App.lf++; else App.vf++;}
		    }
		}
	}
}