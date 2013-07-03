package org.apromore.plugin.deployment.yawl;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class YAWLEngineClientFactoryUnitTest {

    @Test
    public void testNewInstance() {
        assertNotNull(new YAWLEngineClientFactory().newInstance("test", "test", "test"));
    }

}
