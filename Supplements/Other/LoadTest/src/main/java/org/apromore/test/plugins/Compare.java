package org.apromore.test.plugins;

import org.apromore.test.config.TestConfig;
import org.apromore.test.helper.TestLogger;
import org.apromore.test.helper.TestSetting;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Compare extends Plugin
{

	private String item1, item2;
	
	public Compare(TestSetting testSetting, String item1, String item2) throws Exception
	{
		super(testSetting);
		this.item1 = item1;
		this.item2 = item2;
		
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
		action.moveToElement(log).doubleClick().perform();  
        
        WebElement item1El = (new WebDriverWait(driver,
    			60)).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[contains(text(), '" + item1 + "')]"))); 
        
        WebElement item2El = (new WebDriverWait(driver,
    			60)).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[contains(text(), '" + item2 + "')]")));
        
      
        action.moveToElement(item1El).click().perform();  
        action.keyDown(Keys.CONTROL).perform();
        action.moveToElement(item2El).click().perform();  
        action.keyUp(Keys.CONTROL).perform();
        
        WebElement analyzeMenu = driver.findElement(By.xpath("//span[contains(text(), 'Analyze')]"));
        action.moveToElement(analyzeMenu).click().perform();  
        
        WebElement compareItem = (new WebDriverWait(driver,
    			60)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//span[./text()='Compare']"))).get(1);    
        
        action.moveToElement(compareItem).perform();
	}

	public String call() {

        try {
        	
        	long duration = 0;
        	
        	action.click().perform(); 
        	
       	
        	// wait for certain amount of time until the loading popup appears if it is going to of course
            WebElement loading = null;
            try {
         	   loading = (new WebDriverWait(driver,
 	        		   5)).until(ExpectedConditions.visibilityOfElementLocated(By.className("z-loading-indicator"))); 
 	        }catch(TimeoutException ex){}
            
            
            
            if(loading != null)
            {
         	   long t1 = System.currentTimeMillis();
                (new WebDriverWait(driver,
             		   testSetting.wait_sec, TestConfig.pollingInMillis)).until(ExpectedConditions.invisibilityOf(loading));        
        		
               duration += (System.currentTimeMillis() - t1);
            }
        	       	              
            TestLogger.logDuration(testSetting.logger, "Comparing " + item1 + " and " + item2 , duration / 1000);
           
        }catch(TimeoutException ex)
		{
        	logUnsuccessfulOperation("Could not finish comparing " + item1 + " and " + item2);
		}finally
		{
			closeBrowser(driver);
		}
		
		return null;
	}
		

}
