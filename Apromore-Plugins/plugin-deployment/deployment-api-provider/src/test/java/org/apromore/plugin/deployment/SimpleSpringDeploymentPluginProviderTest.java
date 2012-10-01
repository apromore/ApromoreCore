package org.apromore.plugin.deployment;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.apromore.plugin.deployment.provider.DeploymentPluginProvider;
import org.apromore.plugin.exception.PluginNotFoundException;
import org.junit.Before;
import org.junit.Test;

public class SimpleSpringDeploymentPluginProviderTest {

    private DeploymentPluginProvider provider;

    @Before
    public void setUp() {
        this.provider = new SimpleSpringDeploymentPluginProvider();
    }

    @Test
    public void testListAll() {
        assertNotNull(provider.listAll());
        assertFalse(provider.listAll().isEmpty());
    }

    @Test
    public void testFindByName() throws PluginNotFoundException {
        assertNotNull(provider.findByName("test"));
        try {
            provider.findByName("invalid");
            fail();
          } catch (PluginNotFoundException e) {
          }
    }

    @Test
    public void testFindByNativeType() throws PluginNotFoundException {
        assertNotNull(provider.findByNativeType("YAWL 2.2"));
        try {
          provider.findByNativeType("invalid");
          fail();
        } catch (PluginNotFoundException e) {
        }
    }
}
