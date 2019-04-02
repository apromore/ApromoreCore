package org.apromore.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apromore.test.config.TestConfig;
import org.apromore.test.helper.TestLogger;
import org.apromore.test.plugins.Measure;
import org.junit.Test;


public class MeasureTest extends org.apromore.test.Test {

	public MeasureTest()
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
		
		List<Measure> pluginList = new ArrayList<Measure>(testSetting.nConcurrentUsers);
		for(int i = 0; i < testSetting.nConcurrentUsers; i++)
		{
			try {
				pluginList.add(new Measure(testSetting));
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


