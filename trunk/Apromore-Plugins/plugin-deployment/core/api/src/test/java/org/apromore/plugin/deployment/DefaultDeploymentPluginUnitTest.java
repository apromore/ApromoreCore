package org.apromore.plugin.deployment;

import static org.junit.Assert.assertNull;

import org.apromore.anf.AnnotationsType;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.PluginRequest;
import org.apromore.plugin.PluginResult;
import org.apromore.plugin.deployment.exception.DeploymentException;
import org.apromore.plugin.exception.PluginPropertyNotFoundException;
import org.junit.Test;

public class DefaultDeploymentPluginUnitTest {

    @Test
    public void testGetNativeType() {
        assertNull(new DefaultDeploymentPlugin() {

            @Override
            public PluginResult deployProcess(final CanonicalProcessType canonicalProcess, final AnnotationsType annotation, final PluginRequest request)
                    throws DeploymentException, PluginPropertyNotFoundException {
                return null;
            }

            @Override
            public PluginResult deployProcess(final CanonicalProcessType canonicalProcess, final PluginRequest request) throws DeploymentException,
                    PluginPropertyNotFoundException {
                return null;
            }
        }.getNativeType());
    }

}
