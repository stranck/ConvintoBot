package reloaded.convintobot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.ConnectException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

public class FileO {
	public static boolean writer(String text, String path){
    	try {
			File file = new File(Main.st.getDefaultDirectory() + path);
			FileWriter fileWriter = new FileWriter(file);
			fileWriter.write(text); 	
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			Main.ea.alert(e);
		}
    	return true;
    }
    public static String reader(String path) throws IOException{
        FileReader fileReader;
        fileReader = new FileReader(Main.st.getDefaultDirectory() + path);
		BufferedReader bufferedReader = new BufferedReader( fileReader );
        String line = bufferedReader.readLine();
        bufferedReader.close();
        return line;
    }
    public static String allLine(String path) throws FileNotFoundException, IOException{
    	String line;
		String ret = "";
    	try (
    	    InputStream fis = new FileInputStream(Main.st.getDefaultDirectory() + path);
    	    InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
    	    BufferedReader br = new BufferedReader(isr);
    	) {
    	    while ((line = br.readLine()) != null) ret += line;
    	}
    	return ret;
    }
    public static void newFile(String path){
    	File file = new File(Main.st.getDefaultDirectory() + path);
        try{
          file.createNewFile();
        } catch(Exception e){
        	Main.ea.alert(e);
        }
    }
    public static boolean exist(String path){
    	File f = new File(Main.st.getDefaultDirectory() + path);
    	if(f.exists() && !f.isDirectory()) return true;
    	return false;
    }
	public static void delater(String path){
		try {
		    Files.delete(Paths.get(Main.st.getDefaultDirectory() + path));
		} catch (NoSuchFileException x) {
		    System.err.format("%s: no such" + " file or directory%n", path);
		} catch (DirectoryNotEmptyException x) {
		    System.err.format("%s not empty%n", path);
		} catch (IOException x) {
		    System.err.println(x);
		}
	}
    public static boolean upFile() throws ConnectException, InvocationTargetException{
    	String[] result = Download.dwn("http://stranckutilies.altervista.org/editFile").split(";");
    	boolean b = false;
    	for (int x=0; x<result.length; x++) {
    		String sp[] = result[x].split("\\s+");
    		if(sp[0].equalsIgnoreCase("edit")&&exist(sp[1])) {writer(sp[2], sp[1]);b = true;}
        	if(sp[0].equalsIgnoreCase("new")&&!exist(sp[1])) {newFile(sp[1]);b = true;}
        	if(sp[0].equalsIgnoreCase("delate")&&exist(sp[1])) {delater(sp[1]);b = true;}
    	}
    	return b;
    }
    public static String toHtml(String s) {
    	String ret = "";
    	for(int n=0; n<s.length(); n++){
    		int ascii = (int) s.charAt(n);
    		String a = String.valueOf(ascii);
    		while(a.length()<3) a = "0" + a;
    		if(a.length()==3) ret += "&#" + a +";";
    	}
    	return ret;
    }
    public static String fromHtml(String s){
    	String ret = "";
    	String[] sp = s.replaceAll("&#", "").split(";");
    	for(int i=0;i<sp.length;i++){
    		ret += Character.toString((char) Integer.parseInt(sp[i]));
    	}
    	return ret;
    }
    public static int addWrite(String path, String text) throws IOException{
		int i = 0;
		File fout = new File("temp");
		try (BufferedReader br = new BufferedReader(new FileReader(Main.st.getDefaultDirectory() + path))) {
			FileOutputStream fos = new FileOutputStream(fout);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		    String line;
		    while ((line = br.readLine()) != null) {
		    	i++;
		    	bw.write(line);
		    	bw.newLine();
		    }
		    bw.write(text);
		    i++;
			bw.close();
		}
		delater(path);
		fout.renameTo(new File(Main.st.getDefaultDirectory() + path));
		return i;
	}
    public static String ls(String path){
    	String p = Main.st.getDefaultDirectory() + path;
    	File[] listOfFiles = new File(p).listFiles();
		String s = "";
	    for (int i = 0; i < listOfFiles.length; i++) {
	      if (listOfFiles[i].isFile()) {
	       	s += "F: ";
	      } else if (listOfFiles[i].isDirectory()) {
	        s += "D: ";
	      }
	      s += listOfFiles[i].getName() + "\n";
	    }
	    return s;
    }
    public static void rename(String old, String name){
    	File f = new File(old);
		f.renameTo(new File(name));
    }
    public static boolean pathExist(String path){
    	File f = new File(path);
    	return f.exists() && f.isDirectory();
    }
    public static void newPath(String path){
    	new File(path).mkdir();
    }
}
