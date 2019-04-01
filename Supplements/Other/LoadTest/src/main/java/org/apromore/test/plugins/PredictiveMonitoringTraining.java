package org.apromore.test.plugins;

import org.apromore.test.config.TestConfig;
import org.apromore.test.helper.TestLogger;
import org.apromore.test.helper.TestSetting;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class PredictiveMonitoringTraining extends Plugin
{

	public PredictiveMonitoringTraining(TestSetting testSetting) throws Exception
	{
		super(testSetting);
		if(log != null)
		{	
			try {
				init();
				testSetting.nUsersLoggedIn++;
			}catch(Exception ex)
			{
				closeBrowser(driver);	
				throw ex;
			}			
		}else
		{
			closeBrowser(driver);
		}
	}
	
	private void init()
	{
		action.moveToElement(log).click().perform();  
        
        WebElement analyzeMenu = driver.findElement(By.xpath("//span[contains(text(), 'Monitor')]"));
        action.moveToElement(analyzeMenu).click().perform();  
        
        WebElement trainingItem = (new WebDriverWait(driver,
        		testSetting.wait_sec)).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[contains(text(), 'Train Predictor with Log')]")));    
        
        action.moveToElement(trainingItem).click().perform(); 
    	
        WebElement trainModelButton = (new WebDriverWait(driver,
        		testSetting.wait_sec)).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[contains(text(), 'Train model')]")));       
        
 		action.moveToElement(trainModelButton).perform();
	}

	public String call() {
		
		if(log != null)
		{
			try {
	            
	            long t1;        	
	        	long remainingTime = testSetting.wait_sec;
	        	long duration = 0;
	        	
	        	action.click().perform(); 
	        	   	
	        	long stTime = t1 = System.currentTimeMillis();
	    		(new WebDriverWait(driver,
	        			remainingTime, TestConfig.pollingInMillis)).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(., 'RUNNING')]")));    
	    		remainingTime -= (System.currentTimeMillis() - stTime) / 1000;
	    		
	    		System.out.println("RUNNING observed");
	    		
	    		duration += (System.currentTimeMillis() - t1); 
	    		
	        	t1 = stTime = System.currentTimeMillis();     
	        	(new WebDriverWait(driver,
	        			remainingTime, TestConfig.pollingInMillis)).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(., 'COMPLETED')]")));    
	        	remainingTime -= (System.currentTimeMillis() - stTime) / 1000;
	        	
	        	duration += (System.currentTimeMillis() - t1);       	              
	            TestLogger.logDuration(testSetting.logger, "Training prediction model" , duration / 1000);
	           
	        }catch(TimeoutException ex)
			{
	        	logUnsuccessfulOperation("Could not finish training prediction model");
			}finally
			{
				closeBrowser(driver);
			}
		}
        
		
		return null;
	}
	

}
