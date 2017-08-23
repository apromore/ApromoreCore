package org.apromore.portal.uitest;

import java.time.Duration;
import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

public class IBPStructUITest extends AbstractPortalUITest {

  final String IBPSTRUCT_SETUP_DIALOG_XPATH = "/html/body/div[div[text()='iBPStruct setup']]";

  @Test
  public void cancel() throws Exception {
    final String TEST_PROCESS_NAME = "Test structured process";

    popup("repairExample");
    driver.findElement(By.xpath(IBPSTRUCT_SETUP_DIALOG_XPATH + "//button[text()=' Cancel']")).click();
    delay();
    assertFalse(isElementPresent(By.xpath(IBPSTRUCT_SETUP_DIALOG_XPATH)));
  }

  @Test
  public void structure() throws Exception {
    final String TEST_PROCESS_NAME = "Test structured process";

    popup("repairExample");
    assertFalse(isProcessModel(TEST_PROCESS_NAME));
    WebElement structuredProcessName = driver.findElement(By.name("Structured Process Name"));
    //assertEquals("structured_repairExample", structuredProcessName.getText());
    structuredProcessName.clear();
    structuredProcessName.sendKeys(TEST_PROCESS_NAME);
    driver.findElement(By.xpath(IBPSTRUCT_SETUP_DIALOG_XPATH + "//button[text()=' Structure']")).click();
    Thread.currentThread().sleep(2000);
    assertTrue(isProcessModel(TEST_PROCESS_NAME));

    deleteProcessModel(TEST_PROCESS_NAME);
  }

  private void popup(String name) {
    clickFolder("Home");
    delay();
    clickFolder("repair");
    delay();

    clickProcessModel("repairExample");
    delay();
    clickMenuBar("Discover");
    delay();
    clickMenuItem("Structure Process Model");
    delay();
    assertTrue(isElementPresent(By.xpath(IBPSTRUCT_SETUP_DIALOG_XPATH)));
  }
}

