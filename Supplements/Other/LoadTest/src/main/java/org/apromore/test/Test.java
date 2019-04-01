package org.apromore.test;

import java.util.logging.Logger;

import org.apromore.test.helper.TestLogger;
import org.apromore.test.helper.TestSetting;

public class Test {
	
	protected TestSetting testSetting;

	public void init(String logName, String loggerName, int nConcurrentUsers, int wait_sec)
	{
		System.setProperty("webdriver.chrome.driver", 
				"./ChromeDriver/chromedriver.exe");
		
		testSetting = new TestSetting(logName, loggerName, nConcurrentUsers, wait_sec);
	}
	
	protected void printFinalMessage()
	{
		System.out.println("All tests finished!");
		System.out.println("# of users successfully logged in (attempted the main operation) was = " + testSetting.nUsersLoggedIn);
	}
}
