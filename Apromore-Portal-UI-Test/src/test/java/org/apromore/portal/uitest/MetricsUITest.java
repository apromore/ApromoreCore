package org.apromore.portal.uitest;

import org.junit.*;
import static org.junit.Assert.*;
import org.openqa.selenium.*;

public class MetricsUITest extends AbstractPortalUITest {

  @Test
  public void measureRepairExample() throws Exception {
    final String TAB_NAME = "repairExample: Model Metrics";

    assertFalse(isTab(TAB_NAME));
    clickFolder("Home");
    delay();
    clickFolder("repair");
    delay();
    clickProcessModel("repairExample");
    delay();
    clickMenuBar("Analyze");
    delay();
    clickMenuItem("Measure");
    Thread.currentThread().sleep(2000);

    assertTrue(isTab(TAB_NAME));
    try {
      assertEquals("3.000", driver.findElement(By.xpath("//tr[td/div/text()='ACD']/td[3]/div")).getText());

    } finally {
      closeTab(TAB_NAME);
      delay();
      assertFalse(isTab(TAB_NAME));
    }
  }
}
