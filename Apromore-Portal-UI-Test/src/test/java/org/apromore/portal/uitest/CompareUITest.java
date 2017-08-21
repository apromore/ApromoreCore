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

public class CompareUITest extends AbstractPortalUITest {

  @Test
  public void modelToModel() throws Exception {
    clickFolder("Home");
    delay();
    clickFolder("Compare");
    delay();
    clickFolderDisclosure("Compare");
    delay();
    clickFolder("Model to Model");
    delay();
    clickProcessModel("Model1");
    delay();
    shiftClickProcessModel("Model2");  // TODO: make this work
    delay();
    clickMenuBar("Analyze");
    delay();
    clickMenuItem("Compare");
    // This will fail because we can't actually select multiple models

/*
    driver.switchTo().frame(1);
    assertEquals("", driver.getTitle());
    driver.findElement(By.id("z_e")).click();

    Thread.currentThread().sleep(3000);
*/
  }
}
