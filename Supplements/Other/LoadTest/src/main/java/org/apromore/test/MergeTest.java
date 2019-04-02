package org.apromore.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import org.apromore.test.config.TestConfig;
import org.apromore.test.helper.TestLogger;
import org.apromore.test.plugins.Compare;
import org.apromore.test.plugins.Merge;
import org.apromore.test.plugins.ProDrift;
import org.apromore.test.plugins.ProcessDiscoverer;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;


public class MergeTest extends org.apromore.test.Test {

	public MergeTest()
	{
		super.init("MergeFolder", 
				getClass().getName(),				
				TestConfig.nConcurrentUsers,
				TestConfig.wait_sec);
	}
	
	@Test
	public void test()
	{					
		ExecutorService executorService = Executors.newFixedThreadPool(testSetting.nConcurrentUsers);
		
		List<Merge> pluginList = new ArrayList<Merge>(testSetting.nConcurrentUsers);
		for(int i = 0; i < testSetting.nConcurrentUsers; i++)
		{
			try {
				pluginList.add(new Merge(testSetting, "BPIC2012_Model", "BPIC2012_Model2"));
			}catch(Exception ex)
			{
				
			}
			
		}		
				
		try {
			executorService.invokeAll(pluginList);
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		printFinalMessage();
		
		TestLogger.close(testSetting.logger);
	}
	
	
	
}


