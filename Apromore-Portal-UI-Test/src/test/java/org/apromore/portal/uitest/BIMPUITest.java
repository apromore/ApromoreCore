package org.apromore.portal.uitest;

import org.junit.*;
import static org.junit.Assert.*;
import org.openqa.selenium.*;

public class BIMPUITest extends AbstractPortalUITest {

  final String SETUP_DIALOG_XPATH = "/html/body/div[div/text()='Input specification or XML file']";
  final String VERIFICATION_DIALOG_XPATH = "/html/body/div[div/text()='Verification']";

  @Test
  public void test() throws Exception {
    clickFolder("Home");
    delay();
    clickFolder("repair");
    delay();
    clickProcessModel("repairExample");
    delay();
    clickMenuBar("Analyze");
    delay();
    //String portalWindowHandle = driver.getWindowHandle();
    clickMenuItem("Simulate with BIMP");
    Thread.currentThread().sleep(3000);
    
    // Find the BIMP window handle
    String bimpWindowHandle = findNewWindowHandle();

    // Close the BIMP tab and switch back to the portal
    driver.switchTo().window(bimpWindowHandle);
    driver.close();
    driver.switchTo().window(portalWindowHandle);
  }
}

