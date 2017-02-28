package reloaded.convintobot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;

public class Download {
	public static String dwn(String apii) throws ConnectException, InvocationTargetException{
	boolean first = false;
	String check = null;
	URL url;
    InputStream is = null;
    BufferedReader br;
    String line;
    try {
        url = new URL(apii);
        is = url.openStream();
        br = new BufferedReader(new InputStreamReader(is));
        while ((line = br.readLine()) != null) {
        	if(!first){
        		check = line;
        		first = true;
        	} else check = check + line;
        }
    } catch (MalformedURLException mue) {
         Main.ea.alert(mue);
    } catch (IOException ioe) {
    	Main.ea.alert(ioe);
    } finally {
        try {
            if (is != null) is.close();
        } catch (IOException ioe) {}
    }
    if(Main.link){
        Main.logger("\n\nCheck:  " + apii);
        Main.logger(check + "\n");
    }
    return check;
	}
}