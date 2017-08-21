SETUP

To work in Firefox, the geckodriver executable must be on your path.
See https://github.com/mozilla/geckodriver/releases/latest to download.

To work in Safari:
- In Preferences/Advanced select "Show Develop menu in menu bar"
- In the Develop menu select "Allow remote automation"

Apromore must be running at localhost:9000.
The compare test requires specific test data to be present.  This can be installed using:

  ant install-comparison-data


EXECUTION

Execute the UI test suite with the following command:

  mvn test -Dwebdriver=org.openqa.selenium.firefox.FirefoxDriver


Execute a single UI test:

  mvn test -Dwebdriver=org.openqa.selenium.firefox.FirefoxDriver -Dtest=FileUITest#createModelCancel



