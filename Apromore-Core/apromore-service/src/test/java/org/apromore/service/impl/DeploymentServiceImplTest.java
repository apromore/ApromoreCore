package org.apromore.service.impl;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apromore.anf.AnnotationsType;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.PluginRequest;
import org.apromore.plugin.deployment.DeploymentPlugin;
import org.apromore.plugin.deployment.provider.impl.OSGiDeploymentPluginProvider;
import org.apromore.plugin.exception.PluginException;
import org.apromore.plugin.exception.PluginNotFoundException;
import org.apromore.plugin.impl.PluginResultImpl;
import org.apromore.plugin.message.PluginMessage;
import org.apromore.plugin.property.PluginPropertyType;
import org.apromore.plugin.property.PropertyType;
import org.apromore.plugin.property.RequestPropertyType;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/applicationContext-jpa-TEST.xml",
        "classpath:META-INF/spring/applicationContext-services-TEST.xml"
})
public class DeploymentServiceImplTest {


    private DeploymentServiceImpl myService;
    private OSGiDeploymentPluginProvider provider;
    private Set<DeploymentPlugin> deploymentSet;
    private DeploymentPlugin mockDeploymentPlugin;

    @Before
    public void setup() {
        myService = new DeploymentServiceImpl();
        provider = new OSGiDeploymentPluginProvider();
        deploymentSet = new HashSet<DeploymentPlugin>();
        mockDeploymentPlugin = createMock(DeploymentPlugin.class);
        deploymentSet.add(mockDeploymentPlugin);
        provider.setDeploymentPluginSet(deploymentSet);
        myService.setDeploymentProvider(provider);
    }

    @Test
    public void testFindDeploymentPlugins() throws PluginNotFoundException {

        HashSet<PropertyType<?>> mandatoryProperties = new HashSet<PropertyType<?>>();
        PropertyType<String> prop = new PluginPropertyType<String>("test", "test", String.class, "test", true);
        mandatoryProperties.add(prop);
        expect(mockDeploymentPlugin.getNativeType()).andReturn("test");
        expect(mockDeploymentPlugin.getName()).andReturn("test");
        expect(mockDeploymentPlugin.getVersion()).andReturn("1.0");
        expect(mockDeploymentPlugin.getMandatoryProperties()).andReturn(mandatoryProperties);

        replayAll();

        Set<DeploymentPlugin> deploymentPlugin = myService.listDeploymentPlugin("test");
        assertNotNull(deploymentPlugin);
        assertTrue(!deploymentPlugin.isEmpty());
        assertEquals(mockDeploymentPlugin, deploymentPlugin.iterator().next());
        assertTrue(deploymentPlugin.iterator().next().getMandatoryProperties().contains(prop));

        verifyAll();
    }

    @Test
    public void testDeployProcess() throws PluginException {

        HashSet<RequestPropertyType<?>> mandatoryProperties = new HashSet<RequestPropertyType<?>>();
        RequestPropertyType<String> prop = new RequestPropertyType<String>("test", "test");
        mandatoryProperties.add(prop);

        CanonicalProcessType cpf = new CanonicalProcessType();
        AnnotationsType anf = new AnnotationsType();

        expect(mockDeploymentPlugin.getNativeType()).andReturn("test");
        expect(mockDeploymentPlugin.getName()).andReturn("test");
        expect(mockDeploymentPlugin.getVersion()).andReturn("1.0");
        PluginResultImpl result = new PluginResultImpl();
        result.addPluginMessage("test");
        expect(mockDeploymentPlugin.deployProcess(EasyMock.eq(cpf), EasyMock.eq(anf), EasyMock.anyObject(PluginRequest.class))).andReturn(result);

        replayAll();

        List<PluginMessage> messages = myService.deployProcess("test", cpf, anf, mandatoryProperties);
        assertTrue(messages.size() == 1);

        verifyAll();
    }

}
