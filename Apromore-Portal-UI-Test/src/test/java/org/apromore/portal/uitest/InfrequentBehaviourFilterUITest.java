package org.apromore.portal.uitest;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import org.junit.*;
import static org.junit.Assert.*;
import org.openqa.selenium.*;

public class InfrequentBehaviourFilterUITest extends AbstractPortalUITest {

  final String ACTIVITY_FILTER_DIALOG_XPATH = "/html/body/div[div[text()='Activity Filter']]";
  final String TEST_LOG_NAME = "repairExample_complete_lifecycle_only";

  @Test
  public void filterActivityCancel() throws Exception {
    popup("Filter Out Infrequent Activities", TEST_LOG_NAME);
    assertTrue(isElementPresent(By.xpath(ACTIVITY_FILTER_DIALOG_XPATH)));
    driver.findElement(By.xpath(ACTIVITY_FILTER_DIALOG_XPATH + "//button[text()=' Cancel']")).click();
    delay();
    assertFalse(isElementPresent(By.xpath(ACTIVITY_FILTER_DIALOG_XPATH)));
  }

  @Test
  public void filterActivityOK() throws Exception {
    final String TEST_FILTERED_LOG_NAME = TEST_LOG_NAME + "_activity_filtered";

    popup("Filter Out Infrequent Activities", TEST_LOG_NAME);
    assertTrue(isElementPresent(By.xpath(ACTIVITY_FILTER_DIALOG_XPATH)));
    driver.findElement(By.xpath(ACTIVITY_FILTER_DIALOG_XPATH + "//td/div[contains(text(),'complete')]/span")).click();
    driver.findElement(By.xpath(ACTIVITY_FILTER_DIALOG_XPATH + "//button[text()=' OK']")).click();
    Thread.currentThread().sleep(1000);
    assertTrue(isProcessModel(TEST_FILTERED_LOG_NAME));
    try {
      assertFalse(isElementPresent(By.xpath(ACTIVITY_FILTER_DIALOG_XPATH)));

    } finally {
      deleteProcessModel(TEST_FILTERED_LOG_NAME);
    }
  }

  @Test
  public void filterBehaviour() throws Exception {
    final String TEST_FILTERED_LOG_NAME = TEST_LOG_NAME + "_behavioural_filtered";

    assertFalse(isProcessModel(TEST_FILTERED_LOG_NAME));
    popup("Filter Out Infrequent Behavior", TEST_LOG_NAME);
    Thread.currentThread().sleep(1000);
    assertTrue(isProcessModel(TEST_FILTERED_LOG_NAME));
    deleteProcessModel(TEST_FILTERED_LOG_NAME);
  }

  private void popup(String menuItemName, String logName) {
    clickFolder("Home");
    delay();
    clickFolder("repair");
    delay();

    clickProcessModel(logName);
    delay();
    clickMenuBar("Discover");
    delay();
    clickMenuItem(menuItemName);
    delay();
  }
}

