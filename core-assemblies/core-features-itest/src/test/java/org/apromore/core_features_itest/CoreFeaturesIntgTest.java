package org.apromore.core_features_itest;

import org.apache.karaf.itests.KarafTestSupport;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;

/**
 * Integration test suite for the core-features repository.
 *
 * This starts from a stock Karaf distribution and confirms that Apromore Core can be installed
 * via the command shell.
 */
@RunWith(PaxExam.class)
public class CoreFeaturesIntgTest extends KarafTestSupport {

    /**
     * Installs core-features repository and server configuration files.
     */
    @Before
    public void setup() throws Exception {
        String source = executeCommand("shell:source ../../test-classes/setup.karaf");
        Assert.assertEquals(
            "Adding feature url mvn:org.apromore/core-features/7.20-SNAPSHOT/xml\n" +
            "Creating configuration file ehcache.xml\n" +
            "Creating configuration file git.cfg\n" +
            "Creating configuration file git.core.cfg\n" +
            "Creating configuration file portalContext-security.xml\n" +
            "Creating configuration file site.cfg\n", source);
    }

    /**
     * Test that the apromore-core feature starts successfully.
     *
     * To pass, the apromore-core feature must deploy successfully (implying that all its dependencies
     * also succeeded) and all web applications must also deploy successfully.
     */
    @Test
    public void apromoreCore() throws Exception {

        installAndAssertFeature("apromore-core");
        assertDeployedWebApplicationCount(2);
    }

    /**
     * Test that the apromore-manager feature starts successfully.
     *
     * Since only the business logic starts, there should be no web applications.
     */
    @Test
    public void apromoreManager() throws Exception {

        installAndAssertFeature("apromore-manager");
        assertDeployedWebApplicationCount(0);
    }

    /**
     * Test that the apromore-portal feature starts successfully.
     *
     * Since only the ZK presentation layer starts and not the REST endpoint, there should only be
     * one web application deployed.
     */
    @Ignore("Disabled until integration testing switches from Maven's surefire plugin to the failsafe plugin")
    @Test
    public void apromorePortal() throws Exception {

        installAndAssertFeature("zk-frameworkaround");
        installAndAssertFeature("zk-framework");
        installAndAssertFeature("apromore-portal");
        assertDeployedWebApplicationCount(1);
    }

    @Test
    public void virgoCompatibility() throws Exception {
        installAndAssertFeature("virgo-compatibility");
    }

    @Test
    public void zkFramework() throws Exception {
        installAndAssertFeature("zk-frameworkaround");
        installAndAssertFeature("zk-framework");
    }


    // Internal methods

    /**
     * @param expectedCount  the expected number of web applications
     * @throws AssertionError if there aren't <var>count</var> deployed web applications or they don't all deploy
     * @throws Exception if more than 30 seconds expire before the web applications finish deploying
     */
    private void assertDeployedWebApplicationCount(int expectedCount) throws Exception {

        // Wait for the web applications to deploy
        String web = awaitWebServicesDeployment(30000);

        // Check that the expected number of web applications are present
        int actualCount = web.isEmpty() ? 0 : web.split("\n").length;
        Assert.assertEquals("Unexpected number of web applications", expectedCount, actualCount);

        // Check that all the web applications successfully deployed
        if (actualCount > 0) {
            for (String line: web.split("\n")) {
                assertContains("Deployed", line);
            }
        }
    }

    /**
     * Wait until all web applications are deployed.
     *
     * This is achieved by polling the <code>web:list</code> command until none of the WAR bundles report
     * their web-status as "Deploying".
     *
     * @param timeout  milliseconds
     * @return the text of the <code>web:list --no-format</code> command which indicated that all web applications
     *     had finished (or failed) deploying
     * @throws Exception if the web services are still "Deploying" after <var>timeout</var>
     */
    private String awaitWebServicesDeployment(long timeout) throws Exception {

        long pollingPeriod = 1000;  // poll once per second
        String web = "(Never polled)";

        for (long ms = 0; ms <= timeout; ms += pollingPeriod) {
            web = executeCommand("web:list --no-format");
            if (web.contains("Deploying")) {
                Thread.sleep(pollingPeriod);
            }
            else {
                return web;
            }
        }

        throw new Exception("Web application timed out after " + timeout + "ms:\n" + web);
    }
}
