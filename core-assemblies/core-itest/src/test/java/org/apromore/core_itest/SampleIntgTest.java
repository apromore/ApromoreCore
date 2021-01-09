package org.apromore.core_itest;

import org.apache.karaf.features.Feature;
import org.apache.karaf.features.FeaturesService;
import org.apache.karaf.itests.KarafTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

/**
 * Deploy Apromore to a stock Karaf server.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class SampleIntgTest extends KarafTestSupport {

    @Test
    public void listBundleCommand() throws Exception {

        // execute the script to start Apromore
        String source = executeCommand("shell:source ../../test-classes/sample.karaf");
        System.out.print(source);

        installAndAssertFeature("apromore-core");
        installAndAssertFeature("webconsole");
        String features = executeCommand("feature:list -i");
        System.out.print(features);
        assertContains("apromore-rest-endpoint", features);

        // wait for the web applications to finish deploying
        String web;
        do {
            Thread.sleep(2000);
            web = executeCommand("web:list");
            System.out.print(web);
        } while (web.contains("Deploying"));

/*
        assertServiceAvailable(FeaturesService.class);
        FeaturesService featuresService = getOsgiService(FeaturesService.class);
        Feature scr = featuresService.getFeature("scr");
        Assert.assertEquals("scr", scr.getName());
*/
        
    }
}
