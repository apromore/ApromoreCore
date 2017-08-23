package org.apromore.portal.uitest;

import org.junit.*;
import static org.junit.Assert.*;
import org.openqa.selenium.*;

public class LogVisualizerUITest extends AbstractPortalUITest {

  @Test
  public void visualizeRepairExample() throws Exception {
    clickFolder("Home");
    delay();
    clickFolder("repair");
    delay();
    clickProcessModel("repairExample_complete_lifecycle_only");
    delay();
    clickMenuBar("Discover");
    delay();
    clickMenuItem("Visualize Log");
    Thread.currentThread().sleep(2000);

    //driver.switchTo().activeElement();
    WebElement activities = driver.findElement(By.xpath("//tr[td/div/span/text()='Activities']/td/div/input"));
    activities.clear();
    activities.sendKeys("60");
    WebElement arcs = driver.findElement(By.xpath("//tr[td/div/span/text()='Arcs']/td/div/input"));
    arcs.clear();
    arcs.sendKeys("60");
    activities.click();  // Weirdly, this is required to get the arcs to update
    delay();

    driver.findElement(By.xpath("//div[@title='Close']")).click();
    delay();
  }
}
