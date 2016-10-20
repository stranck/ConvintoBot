package it.TetrisReich.bot.TestBot;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;

public class Jar {
	static Jar thisInstance;
	public static String url;
	public static boolean update() throws ConnectException {
		System.out.println("Checking for version update: ");
		String[] res = Download.dwn("http://stranckutilies.altervista.org/version").split(";");;
		if(!res[0].equalsIgnoreCase(App.version)) {
			App.logger("true");
			url = "http://www.mediafire.com/download/"+res[1]+"/YoutubeBot.jar";
			new Jar();
			System.out.println("\nRESTARTING FOR BOT UPDATE!!!");
			return true;
		}
		App.logger("false");
		return false;
    }
    public  Jar() 
{
        otherthread();
    }
    public void otherthread()
    {
        navigate(url,"downloadedFromMediafire");
//      navigate("http://www.mediafire.com/download/j7e4wh6hbdhdj84/Minecraft+1.5.2-+C.H.T.zip","mediafire");
    }
    private void navigate(String url,String sufix)
    {
        try 
        {
            String downloadLink = fetchDownloadLink(getUrlSource(url));
            saveUrl(downloadLink,sufix);
        } catch (Exception e) 
        {
            e.printStackTrace();
        }

    }
    public void saveUrl(final String urlString,String sufix) throws Exception 
    {
    	App.loggerL("Downloading...");
        String filename = urlString.substring(urlString.lastIndexOf("/")+1, urlString.lastIndexOf("."))+urlString.substring(urlString.lastIndexOf("."), urlString.length());
        BufferedInputStream in = null;
        FileOutputStream fout = null;
        try {
            in = new BufferedInputStream(new URL(urlString).openStream());
            fout = new FileOutputStream(filename);

            final byte data[] = new byte[1024];
            int count;
            while ((count = in.read(data, 0, 1024)) != -1) 
            {
                fout.write(data, 0, count);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (fout != null) {
                fout.close();
            }
        }
        App.logger("Success!");
    }
    private static String getUrlSource(String url) throws IOException 
    {
        App.loggerL("Connecting...");
        URL yahoo = new URL(url);
        URLConnection yc = yahoo.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                yc.getInputStream(), "UTF-8"));
        String inputLine;
        String total="";
        while ((inputLine = in.readLine()) != null)
           total+=inputLine;
        in.close();

        return total;
    }
    private static String fetchDownloadLink(String str)
    {
    	App.logger("Fetching download link");
        try {
            String regex = "(?=\\<)|(?<=\\>)";
            String data[] = str.split(regex);
            String found = "NOTFOUND";
            for (String dat : data) {
                if (dat.contains("DLP_mOnDownload(this)")) {
                    found = dat;
                    break;
                }
            }
            String wentthru = found.substring(found.indexOf("href=\"") + 6);
            wentthru = wentthru.substring(0, wentthru.indexOf("\""));
            return wentthru;
        } catch (Exception e) 
        {
            e.printStackTrace();
            return "ERROR";
        }
    
	}
}
