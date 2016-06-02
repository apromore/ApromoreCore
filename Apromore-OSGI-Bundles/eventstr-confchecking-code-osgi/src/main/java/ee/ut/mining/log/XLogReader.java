package ee.ut.mining.log;

import java.io.File;

import org.deckfour.xes.in.XMxmlGZIPParser;
import org.deckfour.xes.in.XMxmlParser;
import org.deckfour.xes.in.XesXmlGZIPParser;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XLog;

public class XLogReader {
	public static XLog openLog(String inputLogFileName) throws Exception {
		XLog log = null;

		if(inputLogFileName.toLowerCase().contains("mxml.gz")){
			XMxmlGZIPParser parser = new XMxmlGZIPParser();
			if(parser.canParse(new File(inputLogFileName))){
				try {
					log = parser.parse(new File(inputLogFileName)).get(0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}else if(inputLogFileName.toLowerCase().contains("mxml") || 
				inputLogFileName.toLowerCase().contains("xml")){
			XMxmlParser parser = new XMxmlParser();
			if(parser.canParse(new File(inputLogFileName))){
				try {
					log = parser.parse(new File(inputLogFileName)).get(0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		if(inputLogFileName.toLowerCase().contains("xes.gz")){
			XesXmlGZIPParser parser = new XesXmlGZIPParser();
			if(parser.canParse(new File(inputLogFileName))){
				try {
					log = parser.parse(new File(inputLogFileName)).get(0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}else if(inputLogFileName.toLowerCase().contains("xes")){
			XesXmlParser parser = new XesXmlParser();
			if(parser.canParse(new File(inputLogFileName))){
				try {
					log = parser.parse(new File(inputLogFileName)).get(0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}


		if(log == null)
			throw new Exception("Oops ...");
		
		return log;
	}
}
