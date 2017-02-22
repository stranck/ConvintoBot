package it.TetrisReich.bot.TestBot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;

import it.TetrisReich.bot.TestBot.Download;
import it.TetrisReich.bot.TestBot.MyRunnable;
import it.TetrisReich.bot.TestBot.Chan;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.ConnectException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.json.*;

/**
 * Hello world!
 *
 */
public class App {
	//Gson g = new Gson();
	public static String token;
	public static boolean inLive = false;
	public static String fileCn;
	public static boolean log = false;
	public static String name;
	public static boolean textEsist = true;
	public static String channel;
	public static boolean crash = false;
	public static String api;
	public static String videoid;
	public static byte liveFinish = 0;
	public static int mesasge_id;
    public static String threadst1;
    public static String threadst2 = "";
    public static boolean link = false;
    public static boolean comp = false;
    public static boolean stat = false;
    public static String liveEnd;
    public static int lastId = 0;
    public static boolean cristoEVenuto = false;
    public static int lf = 0;
    public static int vf = 0;
    public static String key;
    public static boolean secret = false;
    public static long nTotalCheck = 0;
    public static long nVCheck = 0;
    public static long nLCheck = 0;
	public static long nVUpdate = 0;
	public static long nLUpdate = 0;
	public static boolean s = false;
	public static int altervistamerda = 0;
    public static final long startTime = System.currentTimeMillis();
    public static boolean tempLine = true;
    public static boolean mainT = false;
    public static boolean secondT = false;
    public static String dir = "";
    public static boolean skipDefaultDirectory = false;
    public static boolean skipOnlineCheck = false;
    public static String[] ag;
    public static String botName;
    public static String[] all;
    public static String last = "";
    public static final String version = "pre1.0.02092016";
    public static String threadst(){
    	return "https://www.googleapis.com/youtube/v3/videos?part=snippet&id="+videoid+"&maxResults=1&key="+key; 
    }
    public static boolean checKill() throws ConnectException, InvocationTargetException{
    	secret = true;
    	String data = Download.dwn("http://stranckutilies.altervista.org/kill");
    	if(data.equalsIgnoreCase(channel)){System.out.println("admin is turning of this bot. bye bye :D");
    		return true;}
    	secret = false;
    	crash = true;
    	return false;
    }
	public static void mainOld(String[] args) throws IOException, InvocationTargetException {
    	if(args.length>=1) log = true;	
		for (String s: args) {
        	switch(s){
        		case("-l"): link = true;
        		case("-c"):	comp = true;
        		case("-s"): stat = true;
        		case("-d"): skipDefaultDirectory = true;
        		case("-ml"):moveLog();
        		case("-o"): skipOnlineCheck = true;
        		case("-a"): {
            		stat = true;
            		comp = true;
            		link = true;
            	}
        		default: break;
        	}
    	}
    	ag = args;
    	if(!Startup.startup()) {logger("Fail to loading file"); return;}
    	TelegramBot bot = TelegramBotAdapter.build(token);
    	System.out.print(token);
    	bot.execute(new SendMessage("-1001063772015" , "*bot is again online on "+channel+".*\n"
    			+ "_[version: "+version+"]_\n")
    			.parseMode(ParseMode.Markdown).disableWebPagePreview(true));
    	/*if(Startup.check()){
    		bot.execute(new SendMessage(channel, "*This bot is not allowed in this chat.*\n"
    				+ "Please, pm me on my [youtube channel](https://www.youtube.com/user/STRANCKluchetto/about).")
    				.parseMode(ParseMode.Markdown).disableWebPagePreview(true));
    		return;
    	}*/
    	MyRunnable myRunnable = new MyRunnable(10);
        Thread t = new Thread(myRunnable);
        t.start();
        logger("VF: "+vf+" LF: "+lf);
        byte tesThread = 0;
    	while(true){
    		mainT = true;
    		try{
    		nTotalCheck++;
    		altervistamerda++;	
    		if(tesThread==4){if(!secondT){
    					System.out.println("Detect crash of second thread. Restarting the bot.");
    					return; 
    			}else {tesThread = 0; secondT = false;}}
    		if(skipOnlineCheck) altervistamerda = 0;
    		if(altervistamerda==60) {if(FileO.upFile()){
    			System.out.println("Updating file");
    	    	bot.execute(new SendMessage("-1001063772015" , "Updating file\n" +
    	    			Download.dwn("http://stranckutilies.altervista.org/editFile")
    	    			+ "\n_[version: "+version+"]_\n")
    	    			.parseMode(ParseMode.Markdown).disableWebPagePreview(true));
    		}
			if(checKill()||Jar.update()) {
    			String text;
    			if(!crash) text = channel + "> Terminated by admin."; else text = channel + "> Updating bot.";
    	    	bot.execute(new SendMessage("-1001063772015" , text
    	    			+ "\n_[version: "+version+"]_\n")
    	    			.parseMode(ParseMode.Markdown).disableWebPagePreview(true));
    			s = true;
    			return;
    		} else altervistamerda = 0;}
    		if(stat){
    			logger("\nTotal number of check: " + nTotalCheck);
    			logger("Total number of check of video update: " + nVCheck);
    			logger("Total number of check of live update: " + nLCheck);
    			logger("Total number of video update: " + nVUpdate);
    			logger("Total number of live update: " + nLUpdate + "\n");
    		} if(s) return;
    		String info = getInfo(0);
    		if(info!=null){
    		logger("Actual id: " + info + "\nId saved: " + Arrays.toString(all));
    		if(liveFinish<3){
    			//System.out.println("min");
    			nVCheck++;
    			loggerL("\nChecking for new video: ");
    			if(!oldId(info)){
    				nVUpdate++;
    				String mText = Chan.chan()+"\n<a href=\""+info+"\">"+name+"</a>";
    				SendResponse sendResponse = bot.execute(new SendMessage(channel, mText)
    						.parseMode(ParseMode.HTML));
    				//System.out.println(channel + mText);
    				Message message = sendResponse.message();
    				FileO.addWrite("kronos","["+time("yyyy-MM-dd HH:mm:ss.SSS")+"] "+mText+"_"+message.messageId());	
    				FileO.eLast(false);
    				bot.execute(new EditMessageText(channel, lastId, FileO.eLast(true))
    						.parseMode(ParseMode.HTML)
    						.disableWebPagePreview(true));
    				FileO.writer(mText + "@" + message.messageId(), "Last");
    				all = push(all, info);
    				//FileO.writer(FileO.reader("all") + ";" + info, "all");
    				logger("true\n");
    				if(inLive) {
    					logger("Live founded!");
    					threadst1 = threadst();
    					liveFinish = 2;
    					mesasge_id = message.messageId();
    					logger("New id value: " + mesasge_id);
    					nVUpdate++;
    					inLive = false;
    					threadst2 = info;
    					if(Clive.checkUpcoming()){loggerL("Editing programmed live message " + mesasge_id
    							+ " in channel: " + channel + "...");
    						bot.execute(new EditMessageText(channel, mesasge_id,
    							"[Live programmata]\n<a href=\"" + info + "\">" + name + "</a>")
    							.parseMode(ParseMode.HTML)); logger("Done!");}
    				}
    			} else {if(comp)logger("false");}
    			if(liveFinish==1) {
    				loggerL("Editing ending live message " + mesasge_id + " in channel: " + channel +
    						" with: " + liveEnd + "...");
    				bot.execute(new EditMessageText(channel, mesasge_id, "[Live terminata]\n"
    						+liveEnd).parseMode(ParseMode.HTML).disableWebPagePreview(true));
					//logger(bs.toString());
    				logger("Done!");
					liveFinish = 0;
					}
    			if(liveFinish>0) liveFinish = 3;
    			if(cristoEVenuto){
    				inLive = true;
    				bot.execute(new EditMessageText(channel, mesasge_id,
							Chan.chan()+"\n<a href=\"" + threadst2 + "\">" + liveEnd + "</a>")
							.parseMode(ParseMode.HTML));
    				inLive = false;
    				cristoEVenuto = false;
    			}
    		} else {nLCheck++; Clive.checkInLive();}}
    		inLive = false;
    		} catch(IOException e) {e.printStackTrace();}
    		try{
    		    Thread.sleep(10000);
    		} catch(InterruptedException ex){
    		    Thread.currentThread().interrupt();
    		} tesThread++;	
    	}
    }
    public static String getInfo(int n) throws JSONException{
    	int i = n + 1;
    	String result = "";
    	try{
    		last = Download.dwn(api + i);
    		JSONObject obj = new JSONObject(last);
    		JSONArray arr = obj.getJSONArray("items");
    		obj = arr.getJSONObject(n);
    		result = obj.getJSONObject("id").getString("videoId");
    		name = FileO.toHtml(obj.getJSONObject("snippet").getString("title"));
    		if(
    				obj.getJSONObject("snippet").getString("liveBroadcastContent").equals("live")||
    				obj.getJSONObject("snippet").getString("liveBroadcastContent").equals("upcoming")
    			) inLive = true;
    		videoid = result;
    	}catch (Exception e) {
			e.printStackTrace();
			inLive = false;
			try{
    		    Thread.sleep(5000);
    		} catch(InterruptedException ex){
    		    Thread.currentThread().interrupt();
    		}
			return null;
    	}
        return "https://youtu.be/" + result;
    }
    public static void moveLog(){
    	int i = 0;
    	loggerL("Moving log from CB_old.txt to log/CB");
    	while(true){
    		if(!FileO.exist("log/CB" + i + ".txt")){
    			FileO.rename("CB_old.txt", "log/CB" + i + ".txt");
    			logger(i + ".txt");
    			return;
    		} i++;
    	}
    }
    public static boolean oldId(String id) throws FileNotFoundException, IOException{
    	for(int i=0; i<all.length; i++) if(id.equals(all[i])) return true;
    	return false;
    }
    public static String[] push(String[] array, String push) {
        String[] longer = new String[array.length + 1];
        for (int i = 0; i < array.length; i++) longer[i] = array[i];
        longer[array.length] = push;
        return longer;
    }
    public static String[] remove(String[] array, String remove) {
        String[] shorter = new String[array.length - 1];int n = 0;
        for (int i = 0; i < array.length; i++) if(!array[i].equals(remove)){shorter[n] = array[i]; n++;}
        return shorter;
    }
    public static boolean checkAdm(Long id) throws IOException{
    	String[] s = FileO.aL("admin", false).split(";");
    	for(int i=0;i<s.length;i++){if(s[i].equalsIgnoreCase(id.toString())) return true;}
    	return false;
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
    	if(log) System.out.println(s + testo);
    	tempLine = true;
    }
    public static void loggerL(String testo){
    	String s = "";
    	if(tempLine) s = "[" + time("dd-HH:mm:ss") + "] ";
    	if(log) System.out.print(s + testo);
    	tempLine = false;
    }
}