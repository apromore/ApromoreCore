package org.apromore.service.impl;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apromore.plugin.PluginRequest;
import org.apromore.plugin.deployment.DeploymentPlugin;
import org.apromore.plugin.deployment.provider.impl.OSGiDeploymentPluginProvider;
import org.apromore.plugin.exception.PluginException;
import org.apromore.plugin.exception.PluginNotFoundException;
import org.apromore.plugin.impl.DefaultPluginResult;
import org.apromore.plugin.message.PluginMessage;
import org.apromore.plugin.property.PluginPropertyType;
import org.apromore.plugin.property.PropertyType;
import org.apromore.plugin.property.RequestPropertyType;
import org.apromore.service.model.CanonisedProcess;
import org.easymock.EasyMock;
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


    @Test
    public void testFindDeploymentPlugins() throws PluginNotFoundException {
        DeploymentServiceImpl myService = new DeploymentServiceImpl();
        
        OSGiDeploymentPluginProvider provider = new OSGiDeploymentPluginProvider();
        Set<DeploymentPlugin> deploymentSet = new HashSet<DeploymentPlugin>();        
        DeploymentPlugin mockDeploymentPlugin = createMock(DeploymentPlugin.class);
        deploymentSet.add(mockDeploymentPlugin);
        
        provider.setDeploymentPluginSet(deploymentSet);
        myService.setDeploymentProvider(provider);
        
        HashSet<PropertyType<?>> mandatoryProperties = new HashSet<PropertyType<?>>();
        PropertyType<String> prop = new PluginPropertyType<String>("test", "test", String.class, "test", true);
        mandatoryProperties.add(prop);
        expect(mockDeploymentPlugin.getNativeType()).andReturn("test");
        expect(mockDeploymentPlugin.getName()).andReturn("test");
        expect(mockDeploymentPlugin.getMandatoryProperties()).andReturn(mandatoryProperties);

        replayAll();

        DeploymentPlugin deploymentPlugin = myService.findDeploymentPlugins("test");
        assertEquals(mockDeploymentPlugin, deploymentPlugin);
        assertTrue(deploymentPlugin.getMandatoryProperties().contains(prop));

        verifyAll();
    }

    @Test
    public void testDeployProcess() throws PluginException {
        DeploymentServiceImpl myService = new DeploymentServiceImpl();
        
        HashSet<RequestPropertyType<?>> mandatoryProperties = new HashSet<RequestPropertyType<?>>();
        RequestPropertyType<String> prop = new RequestPropertyType<String>("test", "test");
        mandatoryProperties.add(prop);
        
        OSGiDeploymentPluginProvider provider = new OSGiDeploymentPluginProvider();
        Set<DeploymentPlugin> deploymentSet = new HashSet<DeploymentPlugin>();        
        DeploymentPlugin mockDeploymentPlugin = createMock(DeploymentPlugin.class);
        deploymentSet.add(mockDeploymentPlugin);
        provider.setDeploymentPluginSet(deploymentSet);
        myService.setDeploymentProvider(provider);
        
        CanonisedProcess canonisedProcess = new CanonisedProcess();
        
        expect(mockDeploymentPlugin.getNativeType()).andReturn("test");
        expect(mockDeploymentPlugin.getName()).andReturn("test");
        DefaultPluginResult result = new DefaultPluginResult();
        result.addPluginMessage("test");
        expect(mockDeploymentPlugin.deployProcess(EasyMock.eq(canonisedProcess.getCpt()), EasyMock.anyObject(PluginRequest.class) )).andReturn(result);
        
        replayAll();
        
        List<PluginMessage> messages = myService.deployProcess("test", canonisedProcess, mandatoryProperties);
        assertTrue(messages.size() == 1);
        
        verifyAll();
    }

}
