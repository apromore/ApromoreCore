package org.apromore.portal.uitest;

import org.junit.*;
import static org.junit.Assert.*;
import org.openqa.selenium.*;

public class BPMNMinerUITest extends AbstractPortalUITest {

  final String BPMN_MINER_POPUP_XPATH = "/html/body/div[div/text()='BPMN Miner']";
  final String SPLIT_MINER_SETUP_POPUP_XPATH = "/html/body/div[div/text()='Split Miner Setup']";

  @Test
  public void discoverRepairExampleCancel() throws Exception {
    popup();
    driver.findElement(By.xpath(BPMN_MINER_POPUP_XPATH + "//button[text()=' Cancel']")).click();
    assertFalse(isElementPresent(By.xpath(BPMN_MINER_POPUP_XPATH)));
  }

  @Test
  public void discoverRepairExampleOKCancel() throws Exception {
    final String TEST_PROCESS_NAME = "Mined repairExample process 1";

    popup();
    WebElement modelName = driver.findElement(By.xpath(BPMN_MINER_POPUP_XPATH + "//tr[td/div/span/text()='Select Model Name']/td/div/input"));
    modelName.clear();
    modelName.sendKeys(TEST_PROCESS_NAME);
    driver.findElement(By.xpath(BPMN_MINER_POPUP_XPATH + "//button[text()=' OK']")).click();
    delay();
    assertFalse(isElementPresent(By.xpath(BPMN_MINER_POPUP_XPATH)));
    assertTrue(isElementPresent(By.xpath(SPLIT_MINER_SETUP_POPUP_XPATH)));
    driver.findElement(By.xpath(SPLIT_MINER_SETUP_POPUP_XPATH + "//button[text()=' Cancel']")).click();
    delay();
    assertFalse(isElementPresent(By.xpath(SPLIT_MINER_SETUP_POPUP_XPATH)));
  }

  @Test
  public void discoverRepairExampleOKOK() throws Exception {
    final String TEST_PROCESS_NAME = "Mined repairExample process 2";

    popup();
    WebElement modelName = driver.findElement(By.xpath(BPMN_MINER_POPUP_XPATH + "//tr[td/div/span/text()='Select Model Name']/td/div/input"));
    modelName.clear();
    modelName.sendKeys(TEST_PROCESS_NAME);
    driver.findElement(By.xpath(BPMN_MINER_POPUP_XPATH + "//button[text()=' OK']")).click();
    delay();
    assertFalse(isElementPresent(By.xpath(BPMN_MINER_POPUP_XPATH)));
    assertTrue(isElementPresent(By.xpath(SPLIT_MINER_SETUP_POPUP_XPATH)));
    driver.findElement(By.xpath(SPLIT_MINER_SETUP_POPUP_XPATH + "//button[text()=' OK']")).click();
    Thread.currentThread().sleep(5000);
    assertTrue(isProcessModel(TEST_PROCESS_NAME));
    try {
      assertFalse(isElementPresent(By.xpath(SPLIT_MINER_SETUP_POPUP_XPATH)));

    } finally {
      deleteProcessModel(TEST_PROCESS_NAME);
    }
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
    clickMenuItem("Discover Process Model");
    delay();
    assertTrue(isElementPresent(By.xpath(BPMN_MINER_POPUP_XPATH)));
  }
}
