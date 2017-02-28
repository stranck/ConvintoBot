package it.TetrisReich.bot.TestBot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetUpdatesResponse;

public class MyRunnable implements Runnable {

    private int var;

    public MyRunnable(int var) {
        this.var = var;
    }

    public void run() {
    	boolean b = false;
    	if(b) System.out.println(var); //solo per togliere il warn
    	int offset = 0;
    	TelegramBot bot = TelegramBotAdapter.build(App.token);
    	long command = 0;
    	long cUpdates = 0;
    	long nUpdates = 0;
    	byte tesThread = 0;
    	boolean parse = false;
    	boolean crash = false;
    	while(true){
			App.secondT = true;
			try{
    		    Thread.sleep(1000);
    		} catch(InterruptedException ex){
    		    Thread.currentThread().interrupt();
    		}
    		try{//try{
    			GetUpdatesResponse updatesResponse = bot.execute(new GetUpdates().offset(offset));
    			List<Update> updates = updatesResponse.updates();
    			for(Update update : updates) {
    				nUpdates++;
    				offset = update.updateId() + 1;
    				Message message = update.message();
    				App.logger(message.chat().id().toString() + "> " + message.text());
    				if(message.text().equalsIgnoreCase("/start")||message.text().equalsIgnoreCase("/help")){
    					App.logger(message.chat().id().toString() + "> Request start/help page");
    					bot.execute(new SendMessage(message.chat().id().toString(),
    							"Salve! Benvenuto in " + App.botName + " :D\n"
    							+ "Questo bot ha il compito di condividere tutti i nuovi video di chivuoitu "
    							+ "sul canale telegram sempre di chivuoitu, in questo caso "+App.channel+".\n"
    							+ "\nEcco qui una piccola lista di comandi:\n"
    							+ "/last per ottenere l'ultimo mio video.\n"
    							+ "/github per ottenere il link di github del mio codice.\n"
    							+ "/start o /help per visualizzare questo messaggio.\n"
    							+ "/news per ricevere le notizie su questo ed altri bot del mio creatore.\n	"
    							+ "/stat per vedere le statistiche della sessione corrente del bot.\n\n"
    							+ "Per bug report, maggiori informazioni o se vuoi usarmi per il "
    							+ "tuo canale youtube contattami per email qui: public.stranck@gmail.com"
    							).parseMode(ParseMode.Markdown).disableWebPagePreview(true));
    					if(message.chat().id().toString().equals("50731050")||App.checkAdm(message.chat().id())){
    						bot.execute(new SendMessage(message.chat().id().toString(),
    								"Admin commands:\n"
    								+ "/program [text]: Set a specific description for the next video update\n"
    								+ "/ping: pong.\n"
    								+ "/force\n"
    								+ "    reboot: Terminate reboot for restart\n"
    								+ "    vUpdate: Forcing a video update modifying the file \"id\"\n"
    								+ "    startup: Redo the startup metod\n"
    								+ "    version: check for kill the bot, version, edit file\n"
    								+ "    crash: crash the bot with ArrayIndexOutOfBoundsException\n"
    								+ "/file\n"
    								+ "    new [file name]: create a file with certain name\n"
    								+ "    edit [file name] [text]: overwrite the file content "
    									+ "with specified text\n"
    								+ "    addLine [file name] [text]: add at the last line "
    									+ "of the file the text specified\n"
    								+ "    read [file name]: read the content of the file\n"
    								+ "    delate [file name]: delate the specified file\n"
    								+ "    html [on|off]: activate/deactivate the html parsing for /file read\n"
    								+ "    cod [file name]: encode the file contnent in html\n"
    								+ "    decod [file name]: decode the file contnent from html\n"
    								+ "    ls [path]: get list of all files in the select path. Use '.' for default"
    								));
    					}
    					command++;
    				}
    				if(message.text().equalsIgnoreCase("/github")){
    					bot.execute(new SendMessage(message.chat().id().toString(),
    							"Vuoi vedere quale convinzione si cela tra i miei codici?\n"
    							+"Bene, [premi qui!](https://github.com/stranck/ConvintoBot)")
    							.parseMode(ParseMode.Markdown));
    					App.logger(message.chat().id().toString()+"> Request github link");
        				command++;
    				}
    				if(message.text().equalsIgnoreCase("/ping")){
    					bot.execute(new SendMessage(message.chat().id().toString(), "Pong"));
    					App.logger(message.chat().id().toString()+"> Request ping");
        				command++;
    				}
    				if(message.text().equalsIgnoreCase("/news")){
    					String news = "http://telegram.me/MultyChatNews";
            	    	bot.execute(new SendMessage(message.chat().id().toString(), 
            	    			"Qui sotto il canale con tutte le news!").replyMarkup(new InlineKeyboardMarkup(
            	    					new InlineKeyboardButton[]{
            	    							new InlineKeyboardButton("Click me >.<").url(news),
            	    					})));
            	    	App.logger(message.chat().id().toString()+"> Request news channel");
    				}
    				if(message.text().equalsIgnoreCase("/stat")){
    					command++;
    					long estimatedTime = System.currentTimeMillis() - App.startTime;
    					estimatedTime = estimatedTime/1000;
    					int hours = (int) estimatedTime / 3600;
    				    int remainder = (int) estimatedTime - hours * 3600;
    				    int mins = remainder / 60;
    				    remainder = remainder - mins * 60;
    				    int secs = remainder;
    				    int n = 60 - App.altervistamerda;
    				    int nTime = n * 5;
    					bot.execute(new SendMessage(message.chat().id().toString(),
    							"ALL STAT ARE ONLY OF THIS SESSION!!!\n" +
    							"\n\nStat of main thread:\n--------\n" +
    							"Total number of check: "+ App.nTotalCheck + "\n"+
    							"Total number of check of video update: " + App.nVCheck + "\n"+
    							"Total number of check of live update: " + App.nLCheck + "\n" +
    							"Total number of video update: " + App.nVUpdate + "\n" +
    							"Total number of live update: " + App.nLUpdate + "\n" +
    							"\n\nStat of second thread:\n--------\n" +
    							"Actual offset: " + offset + "\n" +
    							"Actual messageId: " + message.messageId() + "\n" +
    							"Total number of checkUpdates: " + cUpdates + "\n" +
    							"Total number of Updates: " + nUpdates + "\n" +
    							"Total numeber of know command: " + command + "\n" +
    							"\n\nGeneral\n--------\n" + 
    							"Bot online for channel: " + App.channel + "\n" +
    							"Bot version: " + App.version + "\n" +
    							"Next check for a new version: " + n + " (About " + nTime + "sec+-5)\n" +
    							"Location: " + App.dir + "\n" +
    							"Startup argouments: " + Arrays.toString(App.ag) + "\n" +
    							"Name: " + App.botName + "\n" +
    							"Uptime: "+hours+":"+mins+":"+secs+"(From "+App.startTime+")\n\n"+
    							"Programmed by <a href=\"www.youtube.com/channel/UCmMWUz0QZ7WhIBx-1Dz-IGg\">Stranck</a>"
						).parseMode(ParseMode.HTML).disableWebPagePreview(true));
    					App.logger(message.chat().id().toString()+"> Request stat page");
    				}
    				if(message.text().equalsIgnoreCase("/last")){
    					try{
    			    		JSONObject obj = new JSONObject(App.last);
    			    		JSONArray arr = obj.getJSONArray("items");
    			    		obj = arr.getJSONObject(0);
    			    		String type = "Video:";
    			    		switch(obj.getJSONObject("snippet").getString("liveBroadcastContent")){
    			    			case "live": {type = "In live ora:"; break;}
    			    			case "upcoming": {type = "Live programmata:"; break;}
    			    			default: break;
    			    		}
    			    		System.out.println(type +  "\n<a href=\"https://youtu.be/"
    			    				+ obj.getJSONObject("id").getString("videoId") + "\">"
    			    				+ FileO.toHtml(obj.getJSONObject("snippet").getString("title"))
    			    				+ "</a>");
    			    		bot.execute(new SendMessage(message.chat().id().toString(),
    			    				type + "\n<a href=\"https://youtu.be/"
    			    				+ obj.getJSONObject("id").getString("videoId") + "\">"
    			    				+ FileO.toHtml(obj.getJSONObject("snippet").getString("title"))
    			    				+ "</a>").parseMode(ParseMode.HTML));
    			    		//System.out.println(br.description());
    			    	} catch (NullPointerException | JSONException e) {
    			    		e.printStackTrace();
    			    		bot.execute(new SendMessage("An error occurred, please retry later.",
    			    				message.chat().id().toString()));
    			    	}
    					App.logger(message.chat().id().toString()+"> Request last video");
    					command++;
    				}
    				try{
    				if(message.chat().id().toString().equals("50731050")||App.checkAdm(message.chat().id())){
    					String[] sp = message.text().split("\\s+");
    					if(sp[0].equalsIgnoreCase("/program")){
    						if(sp.length==1){
    							if(FileO.exist("programmed")) FileO.delater("programmed");
    							App.logger(message.chat().id().toString() + "> Delating programmed message");
    							bot.execute(new SendMessage(message.chat().id().toString(),
    									"Programmed message delated"));
    						} else{
    							FileO.newFile("programmed");
    							String text = "";
    							for(int i=1;i<sp.length;i++) text += FileO.toHtml(sp[i] + " ");
    							FileO.writer(text, "programmed");
    							bot.execute(new SendMessage(message.chat().id().toString(), "Message programmed"));
    							App.logger(message.chat().id().toString() + "> Programming message:\n" + text);
    							command++;
    						}
    					}
    					if(sp[0].equalsIgnoreCase("/force")){
    						App.loggerL(message.chat().id().toString() + "> Forcing ");
    						if(sp[1].equalsIgnoreCase("reboot")) {
    							App.loggerL("reboot.");
    							bot.execute(new SendMessage(message.chat().id().toString(), "Forcing reboot."));
    							bot.execute(new SendMessage("-1001063772015", "Reboot forced by an admin."
    									+ "\n_[version: "+App.version+"]_\n").parseMode(ParseMode.Markdown));
    							int o = offset;
    							Download.dwn("https://api.telegram.org/bot"+App.token+"/getUpdates?offset="+o+
    									"&limit=1");
    							App.s = true; return;
    						}
    						if(sp[1].equalsIgnoreCase("vUpdate")) {
    							App.loggerL("video update.");
    							String s = "";
    			    			do{
    				    			s = App.getInfo(0);//	App.loggerL("DUIASODASISDISA.");
    				    		}while(s==null);
    			    			//System.out.println(Arrays.toString(App.all));
    			    			//App.loggerL("ASDDDDDDDDDDD");
    			    			App.all = App.remove(App.all, s);
    			    			//System.out.println(Arrays.toString(App.all));
    			    			//App.loggerL("DIOCANEEEEEEEEEEEEEEEEEEEE");
    							bot.execute(new SendMessage(message.chat().id().toString(),
    									"Forcing video update."));
    						}
    						if(sp[1].equalsIgnoreCase("startup")) {
    							System.out.print("startup with");
    							String s = "";
    							if(Startup.startup()) s = " no";
    							App.loggerL(s + " error/s.");
    							bot.execute(new SendMessage(message.chat().id().toString(),
    									"Forced startup with" + s + " error/s."));
    						}
    						if(sp[1].equalsIgnoreCase("version")){
    							App.loggerL("version update.");
    							App.altervistamerda = 59;
    							bot.execute(new SendMessage(message.chat().id().toString(),
    									"Forcing version update."));
    						}
    						if(sp[1].equalsIgnoreCase("crash")){
    							App.loggerL("crash.");
    							bot.execute(new SendMessage(message.chat().id().toString(),
    									"Forcing crash."));
    							int o = offset;
    							Download.dwn("https://api.telegram.org/bot"+App.token+"/getUpdates?offset="+o+
    									"&limit=1");
    							crash = true;
    						}
    						App.logger("");
    						command++;
    					}
    					if(sp[0].equalsIgnoreCase("/file")){
    						App.loggerL(message.chat().id().toString() + "> File ");
    						if(sp[1].equalsIgnoreCase("read")){
    							App.loggerL("read " + sp[2]);
    							if(parse){
            						bot.execute(new SendMessage(message.chat().id().toString(),FileO.aL(sp[2],true))
            								.parseMode(ParseMode.HTML).disableWebPagePreview(true));
    							} else {
        						bot.execute(new SendMessage(message.chat().id().toString(), FileO.aL(sp[2],true))
        								.disableWebPagePreview(true));}
    						}
    						if(sp[1].equalsIgnoreCase("delate")){
    							App.loggerL("delate " + sp[2]);
    							FileO.delater(sp[2]);
    						}
    						if(sp[1].equalsIgnoreCase("new")){
    							App.loggerL("new " + sp[2]);
    							FileO.newFile(sp[2]);
    						}
    						if(sp[1].equalsIgnoreCase("edit")){
    							App.loggerL("edit " + sp[2] + " with " + sp[3]);
    							FileO.writer(sp[3], sp[2]);
    						}
    						if(sp[1].equalsIgnoreCase("addLine")){
    							App.loggerL("Adding line " + sp[3] + " to " + sp[2]);
    							FileO.addWrite(sp[2], sp[3]);
    						}
    						if(sp[1].equalsIgnoreCase("ls")){
    						    bot.execute(new SendMessage(message.chat().id().toString(), FileO.ls(sp[2])));
    						}
    						if(sp[1].equalsIgnoreCase("html")){
    							App.loggerL("parsing mode: ");
    							if(sp[2].equalsIgnoreCase("on")){ parse = true; App.loggerL("on");}
    							if(sp[2].equalsIgnoreCase("off")){ parse = false; App.loggerL("off");}
    						}
    						if(sp[1].equalsIgnoreCase("cod")){
    							File fout = new File("temp");
    							try (BufferedReader br = new BufferedReader(new FileReader(App.dir+sp[2]))) {
    								FileOutputStream fos = new FileOutputStream(fout);
    								BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
    							    String line;
    							    while ((line = br.readLine()) != null) {
    							    	bw.write(FileO.toHtml(line));
    							    	bw.newLine();
    							    }
    							    bw.close();
    							}
    							FileO.delater(sp[2]);
    							fout.renameTo(new File(App.dir+sp[2]));
    						}
    						if(sp[1].equalsIgnoreCase("decod")){
    							File fout = new File("temp");
    							try (BufferedReader br = new BufferedReader(new FileReader(App.dir+sp[2]))) {
    								FileOutputStream fos = new FileOutputStream(fout);
    								BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
    							    String line;
    							    while ((line = br.readLine()) != null) {
    							    	bw.write(FileO.fromHtml(line));
    							    	bw.newLine();
    							    }
    							    bw.close();
    							}
    							FileO.delater(sp[2]);
    							fout.renameTo(new File(App.dir+sp[2]));
    						}
    						App.logger("");
    						bot.execute(new SendMessage(message.chat().id().toString(), "Done!"));
    						command++;
    					}
    				}
    				}catch(ArrayIndexOutOfBoundsException e){} catch (IOException e){} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    			}
    			cUpdates++;
    		}catch(SocketTimeoutException | NullPointerException e){e.printStackTrace();}
    		catch(RuntimeException | IOException e){e.printStackTrace();}
    		
    		tesThread++;
			if(App.s) return; if(tesThread==32){if(!App.mainT){
						System.out.println("Detect crash of main thread. Restarting the bot.");
						return;
					}else{tesThread=0;App.mainT=false;}}
	    	if(crash) crash();
    	}
    }
    public static void crash(){
		int i[] = new int[1];
		System.out.println(i[8]);
    }
}