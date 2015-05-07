package org.apromore.manager.service;

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

import javax.xml.bind.JAXBElement;

import org.apromore.manager.ManagerPortalEndpoint;
import org.apromore.model.ObjectFactory;
import org.apromore.model.PluginInfo;
import org.apromore.model.PluginInfoResult;
import org.apromore.model.ReadPluginInfoInputMsgType;
import org.apromore.model.ReadPluginInfoOutputMsgType;
import org.apromore.plugin.DefaultPlugin;
import org.apromore.plugin.exception.PluginNotFoundException;

import org.junit.Assert;
import org.junit.Test;

public class ReadPluginInfoEndpointUnitTest extends AbstractEndpointUnitTest {

    @Test
    public void testReadPluginInfo() throws PluginNotFoundException {
        ReadPluginInfoInputMsgType msg = new ReadPluginInfoInputMsgType();
        msg.setPluginName("Test PLugin");
        msg.setPluginName("1.0");
        JAXBElement<ReadPluginInfoInputMsgType> request = new ObjectFactory().createReadPluginInfoRequest(msg);

        DefaultPlugin mockPlugin = createMock(DefaultPlugin.class);
        expect(mockPlugin.getName()).andReturn("Test Plugin");
        expect(mockPlugin.getVersion()).andReturn("1.0");
        expect(mockPlugin.getAuthor()).andReturn("Scott");
        expect(mockPlugin.getDescription()).andReturn("Beam me up");
        expect(mockPlugin.getType()).andReturn("Starship");
        expect(mockPlugin.getEMail()).andReturn("scott@enterprise.com");
        replay(mockPlugin);

        expect(pluginService.findByNameAndVersion(msg.getPluginName(), msg.getPluginVersion())).andReturn(mockPlugin);

        replay(pluginService);

        JAXBElement<ReadPluginInfoOutputMsgType> response = endpoint.readPluginInfo(request);
        PluginInfoResult infoResult = response.getValue().getPluginInfoResult();
        Assert.assertNotNull(infoResult);
        PluginInfo info = infoResult.getPluginInfo();
        Assert.assertNotNull(info);
        Assert.assertNull(infoResult.getMandatoryParameters());
        Assert.assertNull(infoResult.getOptionalParameters());
        Assert.assertEquals("Plugin name does not match", info.getName(), "Test Plugin");
        Assert.assertEquals("Plugin version does not match", info.getVersion(), "1.0");
        Assert.assertEquals("Plugin author does not match", info.getAuthor(), "Scott");
        Assert.assertEquals("Plugin descr does not match", info.getDescription(), "Beam me up");
        Assert.assertEquals("Plugin type does not match", info.getType(), "Starship");
        Assert.assertEquals("Plugin email does not match", info.getEmail(), "scott@enterprise.com");

        verify(pluginService);
        verify(mockPlugin);
    }

}
