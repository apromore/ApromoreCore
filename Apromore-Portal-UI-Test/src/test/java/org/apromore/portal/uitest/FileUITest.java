package org.apromore.portal.uitest;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import org.junit.*;
import static org.junit.Assert.*;
import org.openqa.selenium.*;

public class FileUITest extends AbstractPortalUITest {

  final String CREATE_NEW_PROCESS_DIALOG_XPATH = "/html/body/div[div[text()='Create new process ']]";
  final String IMPORT_DIALOG_XPATH             = "/html/body/div[div[text()='Import process models or logs']]";
  final String EXPORT_DIALOG_XPATH             = "/html/body/div[div[text()='Export process model']]";
  final String EDIT_MODEL_DIALOG_XPATH         = "/html/body/div[div[starts-with(text(),'Edit process ')]]";
  final String EDIT_METADATA_DIALOG_XPATH      = "/html/body/div[div[text()='Edit process model metadata']]";

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

    // Test the "OK" button
    driver.findElement(By.xpath(CREATE_NEW_PROCESS_DIALOG_XPATH + "//button[text()=' OK']")).click();
    delay();
    String editorWindowHandle = findNewWindowHandle();

    // Teardown
    driver.switchTo().window(editorWindowHandle);
    driver.close();
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

      // Tesr the "OK" button
      driver.findElement(By.xpath(EXPORT_DIALOG_XPATH + "//button[text()=' OK']")).click();
      delay();
      // Should frob the "Export format" drop-down

      // TODO: accept the browser's download popup
      Robot robot = new Robot();
      robot.setAutoDelay(500);
      robot.keyPress(KeyEvent.VK_ENTER);
      robot.keyRelease(KeyEvent.VK_ENTER);
      delay();
      // Should have downloaded a CPF file - works on Safari, but not on Firefox

    } finally {
      deleteProcessModel(TEST_PROCESS_NAME);
    }
  }

  // Edit Model

  @Test
  public void editModelCancel() throws Exception {

    final String TEST_PROCESS_NAME = "Test process name 4";
    createProcessModel(TEST_PROCESS_NAME);
    try {
      // Setup
      clickProcessModel(TEST_PROCESS_NAME);
      clickMenuBar("File");
      clickMenuItem("Edit Model");

      // Test the "Cancel" button
      driver.findElement(By.xpath(EDIT_MODEL_DIALOG_XPATH + "//button[text()=' Cancel']")).click();
      delay();
      assertFalse(isElementPresent(By.xpath(EDIT_MODEL_DIALOG_XPATH)));  // Make sure the ZK dialog box was dismissed
      
    } finally {
      deleteProcessModel(TEST_PROCESS_NAME);
    }
  }

  @Test
  public void editModelOK() throws Exception {

    final String TEST_PROCESS_NAME = "Test process name 5";
    createProcessModel(TEST_PROCESS_NAME);
    try {
      // Setup
      clickProcessModel(TEST_PROCESS_NAME);
      clickMenuBar("File");
      clickMenuItem("Edit Model");

      // Test the "OK" button
      driver.findElement(By.xpath(EDIT_MODEL_DIALOG_XPATH + "//button[text()=' OK']")).click();
      delay();
      assertFalse(isElementPresent(By.xpath(EDIT_MODEL_DIALOG_XPATH)));  // Make sure the ZK dialog box was dismissed
      String editorWindowHandle = findNewWindowHandle();  // Make sure the editor window was opened

      driver.switchTo().window(editorWindowHandle);
      driver.close();
      driver.switchTo().window(portalWindowHandle);

    } finally {
      deleteProcessModel(TEST_PROCESS_NAME);
    }
  }

  // Edit Metadata

  @Test
  public void editMetadataCancel() throws Exception {

    final String TEST_PROCESS_NAME = "Test process name 6";
    createProcessModel(TEST_PROCESS_NAME);
    try {
      // Setup
      clickProcessModel(TEST_PROCESS_NAME);
      clickMenuBar("File");
      clickMenuItem("Edit Metadata");
      delay();
      assertTrue(isElementPresent(By.xpath(EDIT_METADATA_DIALOG_XPATH)));  // Make sure the ZK dialog box has appeared

      // Test the "Cancel" button
      driver.findElement(By.xpath(EDIT_METADATA_DIALOG_XPATH + "//button[text()=' Cancel']")).click();
      delay();
      assertFalse(isElementPresent(By.xpath(EDIT_METADATA_DIALOG_XPATH)));  // Make sure the ZK dialog box was dismissed
      
    } finally {
      clickFolder("Home");
      deleteProcessModel(TEST_PROCESS_NAME);
    }
  }

  @Test
  public void editMetadataReset() throws Exception {

    final String TEST_PROCESS_NAME = "Test process name 7";
    final String MODIFIED_TEST_PROCESS_NAME = "Modified test process name 7";
    createProcessModel(TEST_PROCESS_NAME);
    try {
      // Setup
      clickProcessModel(TEST_PROCESS_NAME);
      clickMenuBar("File");
      clickMenuItem("Edit Metadata");
      delay();
      assertTrue(isElementPresent(By.xpath(EDIT_METADATA_DIALOG_XPATH)));  // Make sure the ZK dialog box has appeared

      WebElement processName = driver.findElement(By.xpath(EDIT_METADATA_DIALOG_XPATH + "//tr[td/div/span[text()='Process name*']]//input"));
      processName.clear();
      processName.sendKeys(MODIFIED_TEST_PROCESS_NAME);
      assertEquals(MODIFIED_TEST_PROCESS_NAME, processName.getAttribute("value"));

      // Test the "Reset" button
      driver.findElement(By.xpath(EDIT_METADATA_DIALOG_XPATH + "//button[text()=' Reset']")).click();
      assertEquals(TEST_PROCESS_NAME, processName.getAttribute("value"));

    } finally {
      driver.findElement(By.xpath(EDIT_METADATA_DIALOG_XPATH + "//button[text()=' Cancel']")).click();
      clickFolder("Home");
      deleteProcessModel(TEST_PROCESS_NAME);
    }
  }

  @Test
  public void editMetadataOK() throws Exception {

    final String TEST_PROCESS_NAME = "Test process name 8";
    final String MODIFIED_TEST_PROCESS_NAME = "Modified test process name 8";
    createProcessModel(TEST_PROCESS_NAME);
    try {
      // Setup
      clickProcessModel(TEST_PROCESS_NAME);
      clickMenuBar("File");
      clickMenuItem("Edit Metadata");
      delay();
      assertTrue(isElementPresent(By.xpath(EDIT_METADATA_DIALOG_XPATH)));  // Make sure the ZK dialog box has appeared

      WebElement processName = driver.findElement(By.xpath(EDIT_METADATA_DIALOG_XPATH + "//tr[td/div/span[text()='Process name*']]//input"));
      processName.clear();
      processName.sendKeys(MODIFIED_TEST_PROCESS_NAME);
      assertEquals(MODIFIED_TEST_PROCESS_NAME, processName.getAttribute("value"));

      // Test the "OK" button
      driver.findElement(By.xpath(EDIT_METADATA_DIALOG_XPATH + "//button[text()=' OK']")).click();
      delay();
      assertFalse(isProcessModel(TEST_PROCESS_NAME));
      assertTrue(isProcessModel(MODIFIED_TEST_PROCESS_NAME));

    } finally {
      deleteProcessModel(MODIFIED_TEST_PROCESS_NAME);
    }
  }

  // Delete

  @Test
  public void delete() throws Exception {

    final String TEST_PROCESS_NAME = "Test process name 9";

    // Setup
    createProcessModel(TEST_PROCESS_NAME);
    clickProcessModel(TEST_PROCESS_NAME);
    clickMenuBar("File");

    // Test the "Delete" function
    clickMenuItem("Delete");
    delay();
    assertFalse(isProcessModel(TEST_PROCESS_NAME));
  }
}
