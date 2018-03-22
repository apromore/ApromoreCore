package org.apromore.portal.uitest;

import org.junit.*;
import static org.junit.Assert.*;
import org.openqa.selenium.*;

public class StageMiningUITest extends AbstractPortalUITest {

  final String STAGE_MINING_PARAMETERS_POPUP_XPATH = "/html/body/div[div/text()='Stage Mining Parameters']";

  @Test
  public void cancel() throws Exception {
    popup();
    driver.findElement(By.xpath(STAGE_MINING_PARAMETERS_POPUP_XPATH + "//button[text()=' Cancel']")).click();
    delay();
    assertFalse(isElementPresent(By.xpath(STAGE_MINING_PARAMETERS_POPUP_XPATH)));
  }

  @Test
  public void ok() throws Exception {
    popup();
    WebElement minimumStageSize = driver.findElement(By.xpath(STAGE_MINING_PARAMETERS_POPUP_XPATH + "//tr[td/div/span/text()='Minimum stage size']/td/div/input"));
    minimumStageSize.clear();
    minimumStageSize.sendKeys("3");
    driver.findElement(By.xpath(STAGE_MINING_PARAMETERS_POPUP_XPATH + "//button[text()=' OK']")).click();
    delay();

    driver.findElement(By.xpath("//div[@title='Close']")).click();
    delay();

    driver.findElement(By.xpath(STAGE_MINING_PARAMETERS_POPUP_XPATH + "//button[text()=' Cancel']")).click();
    delay();
    assertFalse(isElementPresent(By.xpath(STAGE_MINING_PARAMETERS_POPUP_XPATH)));
  }

  private void popup() {
    clickFolder("Home");
    delay();
    clickFolder("repair");
    delay();
    clickProcessModel("repairExample_complete_lifecycle_only");
    delay();
    clickMenuBar("Discover");
    delay();
    clickMenuItem("Mine Process Stages");
    delay();
    assertTrue(isElementPresent(By.xpath(STAGE_MINING_PARAMETERS_POPUP_XPATH)));
  }
}
