package org.apromore.portal.uitest;

import org.junit.*;
import static org.junit.Assert.*;
import org.openqa.selenium.*;

public class BEBoPUITest extends AbstractPortalUITest {

  @Test
  public void checkModelGuidelinesRepairExample() throws Exception {
    final String TAB_NAME = "Check model guidelines with BEBoP";
    final String RESULT_XPATH = "//div[@class='z-listbox' and div//th/div/text()='Understandability Guidelines Check Result']/div[3]/table/tbody";

    assertFalse(isTab(TAB_NAME));
    clickFolder("Home");
    delay();
    clickFolder("repair");
    delay();
    clickProcessModel("repairExample");
    delay();
    clickMenuBar("Analyze");
    delay();
    clickMenuItem("Check model guidelines with BEBoP");
    Thread.currentThread().sleep(3000);

    assertTrue(isTab(TAB_NAME));
    try {
      assertEquals(
        "  Converging gateways do not required to be labeled. When the convergence logic is not obvious, a text annotation should be associated to the gateway.",
        driver.findElement(By.xpath(RESULT_XPATH + "/tr[14]")).getText()
      );

    } finally {
      closeTab(TAB_NAME);
      delay();
      assertFalse(isTab(TAB_NAME));
    }
  }
}
