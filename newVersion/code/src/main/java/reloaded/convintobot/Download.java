package reloaded.convintobot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

/*import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;*/

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class Download {
	
	public static final int TIMEOUT = 4000;
	private static HttpClient client = HttpClients.custom().build();
	private static final RequestConfig PARAMS = RequestConfig.custom()
				.setConnectionRequestTimeout(TIMEOUT)
				.setConnectTimeout(TIMEOUT)
				.setSocketTimeout(TIMEOUT).build();
	/*public static String dwn(String apii) throws Exception{
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
	}*/
	public static String dwn(String apii) throws Exception{
		//Main.LOGGER.finest("Check:  " + apii);
		String s = dwn(apii, null);
		//Main.LOGGER.finest(s);
		//System.out.println(s);
		return s;
	}
	
	/*public static void asd(String api){
		final HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
		httpClient = new DefaultHttpClient(httpParams);
		HttpResponse httpResponse = httpClient.execute(httpGet);
	}*/
	public static String post(String url, ArrayList<NameValuePair> params) throws Exception{
		HttpPost httppost = new HttpPost(url);
		httppost.setConfig(PARAMS);
		httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		HttpResponse response = client.execute(httppost);
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			String s = EntityUtils.toString(entity);
			Main.LOGGER.finest(s);
			return s;
		}

		return null;
	}
	
	public static String dwn(String api, HashMap<String, String> headers) throws Exception{
		//System.out.println("FREE: " + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024));
		Main.LOGGER.finest("GET:  " + api);
		HttpGet get = new HttpGet(api);
		//if(headerName != null && headerValue != null)
		//	get.setHeader(headerName, headerValue);
		if(headers != null)
			for (Entry<String, String> h : headers.entrySet()) {
				get.setHeader(h.getKey(), h.getValue());
			}
		get.setConfig(PARAMS);
		HttpResponse responseGet = client.execute(get);  
		HttpEntity resEntityGet = responseGet.getEntity();
		Main.LOGGER.finest("downloaded");
		if (resEntityGet != null) {  
			//do something with the response
			String s = EntityUtils.toString(resEntityGet);
			get.releaseConnection();
			EntityUtils.consume(resEntityGet);
			Main.LOGGER.finest(s);
		    return s;
		}
		return null;
	}
	
	public static JSONObject twitchOAuth(String method, String oauth) throws Exception{
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Authorization", "Bearer " + oauth);
		return new JSONObject(dwn("https://api.twitch.tv/helix/" + method, headers));
	}
	public static JSONObject twitchOldOauth(String method, String oauth) throws Exception{
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Client-ID", Main.st.getTwitchToken());
		headers.put("Accept", "application/vnd.twitchtv.v5+json");
		headers.put("Authorization", "OAuth " + oauth);
		return new JSONObject(dwn("https://api.twitch.tv/kraken/" + method, headers));
	}
	public static JSONObject twitchOld(String method) throws Exception{
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Client-ID", Main.st.getTwitchToken());
		headers.put("Accept", "application/vnd.twitchtv.v5+json");
		return new JSONObject(dwn("https://api.twitch.tv/kraken/" + method, headers));
	}
	public static JSONObject twitch(String method) throws Exception{
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Client-ID", Main.st.getTwitchToken());
		return new JSONObject(dwn("https://api.twitch.tv/helix/" + method, headers));
	}
}