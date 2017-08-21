package org.apromore.portal.uitest;

import java.time.Duration;
import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

public abstract class AbstractPortalUITest {
  protected WebDriver  driver = null;
  private String       baseUrl = "http://localhost:9000/";
  private boolean      acceptNextAlert = true;
  private StringBuffer verificationErrors = new StringBuffer();

  final static String LEFT_SIDEBAR_XPATH = "//div[@class='z-west']";
  final static String MAIN_PANEL_XPATH   = "//div[@class='z-center']";
  final static String MENU_BAR_XPATH     = "//div[@class='z-menubar z-menubar-horizontal']";
  final static String MENU_POPUP_XPATH   = "//div[@class='z-menupopup z-menupopup-shadow z-menupopup-open']";

  //
  // Test life cycle
  //

  @Before
  public void setUp() throws Exception {
    String driverClassName = System.getProperty("webdriver");
    if (driverClassName == null) {
      throw new Exception("No WebDriver class specified, e.g. -Dwebdriver=org.openqa.selenium.firefox.FirefoxDriver");
    }
    driver = (WebDriver) getClass().forName(driverClassName).newInstance();
    driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
    login();
  }

  @After
  public void tearDown() throws Exception {
    logout();
    driver.quit();
    String verificationErrorString = verificationErrors.toString();
    if (!"".equals(verificationErrorString)) {
      fail(verificationErrorString);
    }
  }

  //
  // UI operations
  //

  protected boolean isProcessModel(String name) {
    return isElementPresent(By.xpath(MAIN_PANEL_XPATH + "//span[text()='" + name + "']"));
  }

  protected void clickProcessModel(String name) {
    assertTrue("Process named \"" + name + "\" does not exist.", isProcessModel(name));
    driver.findElement(By.xpath(MAIN_PANEL_XPATH + "//span[text()='" + name + "']")).click();
  }

  protected void shiftClickProcessModel(String name) {
    (new Actions(driver)).keyDown(Keys.SHIFT).click(driver.findElement(By.xpath(MAIN_PANEL_XPATH + "//span[text()='" + name + "']"))).keyUp(Keys.SHIFT).perform();
  }

  protected void clickFolder(String name) {
    driver.findElement(By.xpath(LEFT_SIDEBAR_XPATH + "//span[text()='" + name + "']")).click();
  }

  protected void clickFolderDisclosure(String name) {
    driver.findElement(By.xpath(LEFT_SIDEBAR_XPATH + "//div[div/div/span/text()='" + name + "']//i")).click();
  }

  protected void createProcessModel(String name) {
    final String CREATE_NEW_PROCESS_DIALOG_XPATH = "//div[div[text()='Create new process ']]";

    assertEquals(baseUrl + "index.zul", driver.getCurrentUrl());
    assertFalse(
      "Process named \"" + name + "\" already present.",
      isElementPresent(By.xpath(MAIN_PANEL_XPATH + "//span[text()='" + name + "']"))
    );
    driver.findElement(By.xpath(MAIN_PANEL_XPATH + "//button[@title='Add Process']")).click();
    delay();
    assertTrue(isElementPresent(By.xpath(CREATE_NEW_PROCESS_DIALOG_XPATH)));  // Make sure the ZK dialog box is visible

    WebElement processName = driver.findElement(By.xpath(CREATE_NEW_PROCESS_DIALOG_XPATH + "//tr[td/div/span[text()='Process name*']]//input"));
    processName.sendKeys(name);
    assertEquals(name, processName.getAttribute("value"));
    String portalWindowHandle = driver.getWindowHandle();

    driver.findElement(By.xpath(CREATE_NEW_PROCESS_DIALOG_XPATH + "//button[text()=' OK']")).click();
    delay();
    driver.switchTo().window(portalWindowHandle);
    assertTrue(
      "Process named \"" + name + "\" could not be created.",
      isElementPresent(By.xpath(MAIN_PANEL_XPATH + "//span[text()='" + name + "']"))
    );
  }

  protected static void delay() {
    try {
      Thread.currentThread().sleep(500);
    } catch (InterruptedException e) {
      // That's okay since some kind of delay happened anyway
    }
  }

  protected void deleteProcessModel(String name) {
    assertEquals(baseUrl + "index.zul", driver.getCurrentUrl());
    clickFolder("Home");  // This removes any pre-existing selection
    clickProcessModel(name);  // Select the target process model
    driver.findElement(By.xpath(MAIN_PANEL_XPATH + "//button[@title='Delete']")).click();
    delay();
    driver.findElement(By.xpath("//div[div[text()='Alert']]//button[text()='Yes']")).click();
    delay();
    assertFalse(
      "Process named \"" + name + "\" couldn't be removed.",
      isElementPresent(By.xpath(MAIN_PANEL_XPATH + "//span[text()='" + name + "']"))
    );
  }

  /**
   * Go to the login page and log in as the admin account.
   *
   * If successful, the browser URL will be <code>/</code>.
   */
  private void login() throws InterruptedException {
    driver.get(baseUrl + "login.zul");
    driver.findElement(By.name("j_username")).clear();
    driver.findElement(By.name("j_username")).sendKeys("admin");
    driver.findElement(By.name("j_password")).clear();
    driver.findElement(By.name("j_password")).sendKeys("password");
    driver.findElement(By.xpath("//button[@type='submit']")).click();
    delay();
    assertEquals(baseUrl + "index.zul", driver.getCurrentUrl());
  }

  /**
   * Go to the main portal page and log out.
   */
  private void logout() throws InterruptedException {
    driver.get(baseUrl + "/");
    driver.findElement(By.xpath("//a/span[text()='Sign out']")).click();
    // ZK pseudo-alert is visible: "Are you sure you want to logout?"
    driver.findElement(By.xpath("//button[text()='Yes']")).click();
    delay();
    assertEquals(baseUrl + "login.zul", driver.getCurrentUrl());
  }

  protected void clickMenuBar(String menu) {
    driver.findElement(By.xpath(MENU_BAR_XPATH + "//span[text()='" + menu + "']")).click();
  }

  protected void clickMenuItem(String item) {
    assertTrue(isElementPresent(By.xpath(MENU_POPUP_XPATH)));
    driver.findElement(By.xpath(MENU_POPUP_XPATH + "//span[text()='" + item + "']")).click();
  }

  //
  // Utilities generated by Selenium IDE exporter
  //

  public boolean isElementPresent(By by) {
    try {
      driver.findElement(by);
      return true;
    } catch (NoSuchElementException e) {
      return false;
    }
  }

  public boolean isAlertPresent() {
    try {
      driver.switchTo().alert();
      return true;
    } catch (NoAlertPresentException e) {
      return false;
    }
  }

  public String closeAlertAndGetItsText() {
    try {
      Alert alert = driver.switchTo().alert();
      String alertText = alert.getText();
      if (acceptNextAlert) {
        alert.accept();
      } else {
        alert.dismiss();
      }
      return alertText;
    } finally {
      acceptNextAlert = true;
    }
  }
}
