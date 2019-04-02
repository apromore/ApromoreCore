package org.apromore.test.plugins;

import org.apromore.test.config.TestConfig;
import org.apromore.test.helper.TestLogger;
import org.apromore.test.helper.TestSetting;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class MineProcessStage extends Plugin
{

	public MineProcessStage(TestSetting testSetting) throws Exception
	{
		super(testSetting);

		try {
			init();
			testSetting.nUsersLoggedIn++;
		}catch(Exception ex)
		{
			closeBrowser(driver);	
			throw ex;
		}
	}
	
	private void init()
	{
		action.moveToElement(log).click().perform();  
        
        WebElement analyzeMenu = driver.findElement(By.xpath("//span[contains(text(), 'Discover')]"));
        action.moveToElement(analyzeMenu).click().perform();  
        
        WebElement stageItem = (new WebDriverWait(driver,
        		testSetting.wait_sec)).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[contains(text(), 'Mine process stages')]")));    
        
        action.moveToElement(stageItem).click().perform(); 
        
        (new WebDriverWait(driver,
        		testSetting.wait_sec)).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(text(), 'Stage Mining Parameters')]")));    
    	    	
    	WebElement okButton = driver.findElement(By.xpath("//button[contains(text(), 'OK')]"));
    	action.moveToElement(okButton).perform();
	}

	public String call() throws Exception {

        try {
        	
        	long t1;        	
        	long remainingTime = testSetting.wait_sec;
        	long duration = 0;
        	
        	action.click().perform();  
        	
        	long stTime = t1 = System.currentTimeMillis();
           (new WebDriverWait(driver,
        		   remainingTime, TestConfig.pollingInMillis)).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(., 'Stage Mining Result')]")));       
           remainingTime -= (System.currentTimeMillis() - stTime) / 1000;
           
           duration += (System.currentTimeMillis() - t1);       	              
           TestLogger.logDuration(testSetting.logger, "Stage mining" , duration / 1000);
           
        }catch(TimeoutException ex)
		{
        	logUnsuccessfulOperation("Could not finish stage mining");
		}finally
		{
			closeBrowser(driver);
		}
		
		return null;
	}
	

}
