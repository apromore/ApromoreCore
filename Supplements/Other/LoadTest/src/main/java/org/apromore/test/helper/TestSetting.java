package org.apromore.test.helper;

import java.util.logging.Logger;

public class TestSetting 
{

	public String logName;
	public Logger logger;
	public int nConcurrentUsers;
	public int wait_sec;
	public int nUsersLoggedIn = 0;
	
	public TestSetting(String logName, String loggerName, int nConcurrentUsers, int wait_sec)
	{
		this.logName = logName;
		this.logger = TestLogger.createLogger(loggerName + "_" + logName + "_" + nConcurrentUsers);
		this.nConcurrentUsers = nConcurrentUsers;
		this.wait_sec = wait_sec;
	}
}
