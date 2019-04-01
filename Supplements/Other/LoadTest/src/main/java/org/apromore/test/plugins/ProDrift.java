package org.apromore.test.plugins;

import org.apromore.test.config.TestConfig;
import org.apromore.test.helper.TestLogger;
import org.apromore.test.helper.TestSetting;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ProDrift extends Plugin
{

	public ProDrift(TestSetting testSetting) throws Exception
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
        
        WebElement analyzeMenu = driver.findElement(By.xpath("//span[contains(text(), 'Analyze')]"));
        action.moveToElement(analyzeMenu).click().perform();  
        
        WebElement driftItem = (new WebDriverWait(driver,
        		testSetting.wait_sec)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//span[contains(text(), 'Detect Process Drifts')]"))).get(1);    
        
        action.moveToElement(driftItem).click().perform(); 
        
        (new WebDriverWait(driver,
        		testSetting.wait_sec)).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(text(), 'ProDrift: Set Parameters.')]")));
        
        WebElement okButton = driver.findElement(By.xpath("//button[contains(text(), 'OK')]"));
    	action.moveToElement(okButton).perform();
	}

	public String call(){
        
        try {        	  	
        	
        	long t1;        	
        	long remainingTime = testSetting.wait_sec;
        	long duration = 0;        	   	
        	long stTime;     
        	
        	action.click().perform();  
        	
        	t1 = stTime = System.currentTimeMillis();
           (new WebDriverWait(driver,
        		   remainingTime, TestConfig.pollingInMillis)).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(., 'ProDrift: Drift Detection Result.')]")));       
           remainingTime -= (System.currentTimeMillis() - stTime) / 1000;
           
           duration += (System.currentTimeMillis() - t1);       	              
           TestLogger.logDuration(testSetting.logger, "Drift detection" , duration / 1000);
           
        }catch(TimeoutException ex)
		{
        	logUnsuccessfulOperation("Could not finish drift detection");
		}finally
		{
			closeBrowser(driver);
		}
		
		return null;
	}
	

}
