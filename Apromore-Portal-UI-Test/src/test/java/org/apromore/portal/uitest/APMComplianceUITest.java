package org.apromore.portal.uitest;

import org.junit.*;
import static org.junit.Assert.*;
import org.openqa.selenium.*;

public class APMComplianceUITest extends AbstractPortalUITest {

  final String SETUP_DIALOG_XPATH = "/html/body/div[div/text()='Input specification or XML file']";
  final String VERIFICATION_DIALOG_XPATH = "/html/body/div[div/text()='Verification']";

  @Test
  public void cancel() throws Exception {
    popup();
    driver.findElement(By.xpath(SETUP_DIALOG_XPATH + "//button[text()=' Cancel']")).click();
    delay();
    assertFalse(isElementPresent(By.xpath(SETUP_DIALOG_XPATH)));
  }

  @Test
  public void ok() throws Exception {
    final String TEST_PROCESS_NAME = "Test structured process";

    popup();
    WebElement directInput = driver.findElement(By.xpath(SETUP_DIALOG_XPATH + "//textarea"));
    directInput.clear();
    directInput.sendKeys("Foo");
    driver.findElement(By.xpath(SETUP_DIALOG_XPATH + "//button[text()=' OK']")).click();
    Thread.currentThread().sleep(1000);
    assertTrue(isElementPresent(By.xpath(VERIFICATION_DIALOG_XPATH)));
    driver.findElement(By.xpath(VERIFICATION_DIALOG_XPATH + "//button[text()='close']")).click();
    delay();
    assertFalse(isElementPresent(By.xpath(VERIFICATION_DIALOG_XPATH)));
  }

  private void popup() {
    clickFolder("Home");
    delay();
    clickFolder("repair");
    delay();

    clickProcessModel("repairExample");
    delay();
    clickMenuBar("Analyze");
    delay();
    clickMenuItem("Verify Compliance");
    delay();
    assertTrue(isElementPresent(By.xpath(SETUP_DIALOG_XPATH)));
  }
}

