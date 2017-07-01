package reloaded.convintobot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class Format extends Formatter{
    
	public Format() {
    	super();
    }

    @Override 
    public String format(final LogRecord record){
    	return "[" + time("dd-HH:mm:ss") + "][" + record.getLevel() + "]\t" + record.getMessage().replaceAll("\n", "") + "\n";
    }
    
	private String time(String format) {
		SimpleDateFormat sdfDate = new SimpleDateFormat(format);
		Date now = new Date();
		String strDate = sdfDate.format(now);
		return strDate;	
	}
}