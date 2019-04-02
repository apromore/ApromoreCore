package org.apromore.test.helper;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.apromore.test.config.TestConfig;

public class TestLogger {
	
	public static Logger createLogger(String name)
	{
		Logger logger = Logger.getLogger(name);
		
		try {
			Formatter recordFormatter = new Formatter() {
				
				@Override
				public String format(LogRecord record) {
					
					
					return record.getSequenceNumber() + "," +
							record.getThreadID() + "," + 
							record.getLevel() + "," + 
							record.getMessage() + "," + 
							record.getParameters()[0] + "\n";
				}
			};
			Handler fileHandler  = new FileHandler(TestConfig.logPath + name + ".csv", false);
			fileHandler.setFormatter(recordFormatter);
			logger.addHandler(fileHandler);
			fileHandler.setLevel(Level.ALL);
			logger.setLevel(Level.ALL);	
			
			
			
			
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return logger;
	}
	
	public static void logTimeout(Logger logger, String message, long wait_sec)
	{
		logger.log(Level.SEVERE, message + " within(sec)", wait_sec);
	}
	
	public static void logDuration(Logger logger, String message, long duration)
	{
		logger.log(Level.INFO, message + " took(sec)", duration);
	}
	
	public static void close(Logger logger)
	{
		Handler[] handlers = logger.getHandlers();
		if(handlers != null && handlers.length > 0)
		{
			handlers[0].close();
		}
	}
}
