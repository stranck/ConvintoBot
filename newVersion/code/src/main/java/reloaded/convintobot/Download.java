package reloaded.convintobot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class Download {
	public static String dwn(String apii) throws Exception{
    	Main.LOGGER.finest("Check:  " + apii);
		String check = "";
		InputStream is = null;
    	BufferedReader br;
    	String line;
    	is = new URL(apii).openStream();
    	br = new BufferedReader(new InputStreamReader(is));
        while ((line = br.readLine()) != null) {
        	//if(line.isEmpty()) break;
        	check += line;
        }
    	try {
    		if (is != null) is.close();
    	} catch (IOException ioe) {}
    	Main.LOGGER.finest(check);
    	return check;
	}
}