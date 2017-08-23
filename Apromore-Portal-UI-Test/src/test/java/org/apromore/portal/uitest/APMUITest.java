package org.apromore.portal.uitest;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import org.junit.*;
import static org.junit.Assert.*;
import org.openqa.selenium.*;

public class BEBoPUITest extends AbstractPortalUITest {

  final String SETUP_DIALOG_XPATH = "/html/body/div[div[text()='Select the sets to incorporate in the specification']]";
  final String SPECIFICATION_DIALOG_XPATH = "/html/body/div[div[text()='Specification']]";

  @Test
  public void cancel() throws Exception {
    popup("repairExample");
    driver.findElement(By.xpath(SETUP_DIALOG_XPATH + "//button[text()='Cancel']")).click();
    delay();
    assertFalse(isElementPresent(By.xpath(SETUP_DIALOG_XPATH)));
  }

  @Test
  public void accept() throws Exception {
    popup("repairExample");
    driver.findElement(By.xpath(SETUP_DIALOG_XPATH + "//button[text()='Accept']")).click();
    delay();
    assertFalse(isElementPresent(By.xpath(SETUP_DIALOG_XPATH)));
    assertTrue(isElementPresent(By.xpath(SPECIFICATION_DIALOG_XPATH)));
    assertEquals("EF({Inform User} & {t11})", driver.findElement(By.xpath(SPECIFICATION_DIALOG_XPATH + "//tr[td/div/span/text()='48']/td[2]/div/span")).getText());

    // Download the specification
    driver.findElement(By.xpath(SPECIFICATION_DIALOG_XPATH + "//button[text()='Download specification']")).click();
    delay();
    Robot robot = new Robot();
    robot.keyPress(KeyEvent.VK_ENTER);
    robot.keyRelease(KeyEvent.VK_ENTER);
    delay();
    // TODO: verify that verification.xml was downloaded

    // Close the specification dialog
    driver.findElement(By.xpath(SPECIFICATION_DIALOG_XPATH + "//button[text()='close']")).click();
    delay();
    assertFalse(isElementPresent(By.xpath(SPECIFICATION_DIALOG_XPATH)));
  }

  private void popup(String name) {
    clickFolder("Home");
    delay();
    clickFolder("repair");
    delay();

    clickProcessModel(name);
    delay();
    clickMenuBar("Discover");
    delay();
    clickMenuItem("Extract Variability Specification");
    delay();
    assertTrue(isElementPresent(By.xpath(SETUP_DIALOG_XPATH)));
  }
}
