package org.apromore.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import org.apromore.test.config.TestConfig;
import org.apromore.test.helper.TestLogger;
import org.apromore.test.plugins.MineProcessStage;
import org.apromore.test.plugins.MineStagedProcessPerformance;
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


public class MineStagedProcessPerformanceTest extends org.apromore.test.Test {

	public MineStagedProcessPerformanceTest()
	{
		super.init(TestConfig.logName, 
				getClass().getName(),				
				TestConfig.nConcurrentUsers,
				TestConfig.wait_sec);
	}
	
	@Test
	public void test()
	{					
		ExecutorService executorService = Executors.newFixedThreadPool(testSetting.nConcurrentUsers);
		
		List<MineStagedProcessPerformance> pluginList = new ArrayList<MineStagedProcessPerformance>(testSetting.nConcurrentUsers);
		for(int i = 0; i < testSetting.nConcurrentUsers; i++)
		{
			try {
				pluginList.add(new MineStagedProcessPerformance(testSetting));
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


