package org.apromore.portal.uitest;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.openqa.selenium.*;

public class FileUITest extends AbstractPortalUITest {

  final String CREATE_NEW_PROCESS_DIALOG_XPATH = "//div[div[text()='Create new process ']]";
  final String IMPORT_DIALOG_XPATH             = "/html/body/div[div[text()='Import process models or logs']]";
  final String EXPORT_DIALOG_XPATH             = "/html/body/div[div[text()='Export process model']]";

  // Create Model

  @Test
  public void createModelCancel() throws Exception {

    // Setup
    clickMenuBar("File");
    clickMenuItem("Create Model");
    assertTrue(isElementPresent(By.xpath(CREATE_NEW_PROCESS_DIALOG_XPATH)));  // Make sure the ZK dialog box is visible

    // Test the "Cancel" button
    driver.findElement(By.xpath(CREATE_NEW_PROCESS_DIALOG_XPATH + "//button[text()=' Cancel']")).click();
    delay();
    assertFalse(isElementPresent(By.xpath(CREATE_NEW_PROCESS_DIALOG_XPATH)));  // Make sure the ZK dialog box was dismissed
  }

  @Ignore("Reset button not yet implemented")
  @Test
  public void createModelReset() throws Exception {

    // Setup
    clickMenuBar("File");
    clickMenuItem("Create Model");
    assertTrue(isElementPresent(By.xpath(CREATE_NEW_PROCESS_DIALOG_XPATH)));  // Make sure the ZK dialog box is visible

    WebElement processName = driver.findElement(By.xpath(CREATE_NEW_PROCESS_DIALOG_XPATH + "//tr[td/div/span[text()='Process name*']]//input"));
    processName.sendKeys("Test process name");
    delay();
    assertEquals("Test process name", processName.getAttribute("value"));

    // Test the "Reset" button
    driver.findElement(By.xpath(CREATE_NEW_PROCESS_DIALOG_XPATH + "//button[text()=' Reset']")).click();
    delay();
    assertEquals("", processName.getAttribute("value"));
  }

  @Test
  public void createModelOK() throws Exception {

    final String TEST_PROCESS_NAME = "Test process name";

    // Setup
    if (isElementPresent(By.xpath(MAIN_PANEL_XPATH + "//span[text()='" + TEST_PROCESS_NAME + "']"))) {
      deleteProcessModel(TEST_PROCESS_NAME);
    }

    clickMenuBar("File");
    clickMenuItem("Create Model");
    assertTrue(isElementPresent(By.xpath(CREATE_NEW_PROCESS_DIALOG_XPATH)));  // Make sure the ZK dialog box is visible

    WebElement processName = driver.findElement(By.xpath(CREATE_NEW_PROCESS_DIALOG_XPATH + "//tr[td/div/span[text()='Process name*']]//input"));
    processName.sendKeys(TEST_PROCESS_NAME);
    delay();
    assertEquals(TEST_PROCESS_NAME, processName.getAttribute("value"));
    String portalWindowHandle = driver.getWindowHandle();

    // Test the "OK" button
    driver.findElement(By.xpath(CREATE_NEW_PROCESS_DIALOG_XPATH + "//button[text()=' OK']")).click();
    delay();

    // Teardown
    driver.switchTo().window(portalWindowHandle);
    deleteProcessModel(TEST_PROCESS_NAME);
  }

  // Import

  @Test
  public void importCancel() throws Exception {

    // Setup
    clickMenuBar("File");
    clickMenuItem("Import");
    assertTrue(isElementPresent(By.xpath(IMPORT_DIALOG_XPATH)));  // Make sure the ZK dialog box is visible

    // Test the "Cancel" button
    driver.findElement(By.xpath(IMPORT_DIALOG_XPATH + "//button[text()=' Cancel']")).click();
    delay();
    assertFalse(isElementPresent(By.xpath(IMPORT_DIALOG_XPATH)));  // Make sure the ZK dialog box was dismissed
  }

  @Ignore("Unable to find test file")
  @Test
  public void importOK() throws Exception {

    // Setup
    clickMenuBar("File");
    clickMenuItem("Import");
    assertTrue(isElementPresent(By.xpath(IMPORT_DIALOG_XPATH)));  // Make sure the ZK dialog box is visible

    driver.findElement(By.xpath(IMPORT_DIALOG_XPATH + "//button[text()='Browse']")).click();
    delay();

    File file = new File(getClass().getClassLoader().getResource("Basic.bpmn").toURI());
    assertTrue(file.exists());
    WebElement fileInput = driver.findElement(By.xpath(IMPORT_DIALOG_XPATH + "//input[@name='file']"));
    fileInput.sendKeys(file.getAbsolutePath());  // TODO: Why does this report file not found? 

    Robot robot = new Robot();
    robot.keyPress(KeyEvent.VK_ESCAPE);
    robot.keyRelease(KeyEvent.VK_ESCAPE);
    delay();

    // Test the "OK" button
    driver.findElement(By.xpath(IMPORT_DIALOG_XPATH + "//button[text()=' OK']")).click();
    Thread.currentThread().sleep(5000);
  }

  // Export

  @Test
  public void exportCancel() throws Exception {

    final String TEST_PROCESS_NAME = "Test process name 2";

    createProcessModel(TEST_PROCESS_NAME);
    try {
      // Setup
      clickProcessModel(TEST_PROCESS_NAME);
      clickMenuBar("File");
      clickMenuItem("Export");
      delay();

      // Test the "Cancel" button
      driver.findElement(By.xpath(EXPORT_DIALOG_XPATH + "//button[text()=' Cancel']")).click();
      delay();
      assertFalse(isElementPresent(By.xpath(EXPORT_DIALOG_XPATH)));  // Make sure the ZK dialog box was dismissed

    } finally {
      deleteProcessModel(TEST_PROCESS_NAME);
    }
  }

  @Test
  public void exportOK() throws Exception {

    final String TEST_PROCESS_NAME = "Test process name 3";

    createProcessModel(TEST_PROCESS_NAME);
    try {
      // Setup
      clickProcessModel(TEST_PROCESS_NAME);
      clickMenuBar("File");
      clickMenuItem("Export");
      delay();

      // Tesk the "OK" button
      driver.findElement(By.xpath(EXPORT_DIALOG_XPATH + "//button[text()=' OK']")).click();
      delay();
      // Should frob the "Export format" drop-down

      // TODO: accept the browser's download popup
      Robot robot = new Robot();
      robot.setAutoDelay(500);
      robot.keyPress(KeyEvent.VK_ENTER);
      robot.keyRelease(KeyEvent.VK_ENTER);
      delay();
      // Should have downloaded a CPF file

    } finally {
      deleteProcessModel(TEST_PROCESS_NAME);
    }
  }

  // Edit Model

  @Test
  public void editModelCancel() throws Exception {
    // Setup
    clickMenuBar("File");
    clickMenuItem("Edit Model");
  }

  @Test
  public void editModelOK() throws Exception {
    // Setup
    clickMenuBar("File");
    clickMenuItem("Edit Model");
  }

  // Edit Metadata

  @Test
  public void editMetadataCancel() throws Exception {
    // Setup
    clickMenuBar("File");
    clickMenuItem("Edit Metadata");
  }

  public void editMetadataReset() throws Exception {
    // Setup
    clickMenuBar("File");
    clickMenuItem("Edit Metadata");
  }

  public void editMetadataOK() throws Exception {
    // Setup
    clickMenuBar("File");
    clickMenuItem("Edit Metadata");
  }

  // Delete

  public void delete() throws Exception {
    // Setup
    clickMenuBar("File");
    clickMenuItem("Delete");
  }
}

