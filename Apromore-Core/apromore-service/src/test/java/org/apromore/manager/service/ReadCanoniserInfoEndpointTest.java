package org.apromore.manager.service;

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBElement;

import org.apromore.canoniser.Canoniser;
import org.apromore.canoniser.DefaultAbstractCanoniser;
import org.apromore.model.ObjectFactory;
import org.apromore.model.PluginInfo;
import org.apromore.model.ReadCanoniserInfoInputMsgType;
import org.apromore.model.ReadCanoniserInfoOutputMsgType;
import org.apromore.plugin.exception.PluginNotFoundException;
import org.apromore.service.impl.CanoniserServiceImpl;
import org.junit.Assert;
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
public class ReadCanoniserInfoEndpointTest {

    private ManagerPortalEndpoint endpoint;

    private CanoniserServiceImpl canoniserService;

    @Before
    public void setUp() throws Exception {
        canoniserService = createMock(CanoniserServiceImpl.class);
        endpoint = new ManagerPortalEndpoint();
        endpoint.setCanoniserService(canoniserService);
    }

    @Test
    public void testReadCanoniserInfo() throws PluginNotFoundException {
        ReadCanoniserInfoInputMsgType msg = new ReadCanoniserInfoInputMsgType();
        msg.setNativeType("Test 9.2");
        JAXBElement<ReadCanoniserInfoInputMsgType> request = new ObjectFactory().createReadCanoniserInfoRequest(msg);

        DefaultAbstractCanoniser mockCanoniser = createMock(DefaultAbstractCanoniser.class);
        expect(mockCanoniser.getName()).andReturn("Test Plugin");
        expect(mockCanoniser.getVersion()).andReturn("1.0");
        expect(mockCanoniser.getAuthor()).andReturn("Scott");
        expect(mockCanoniser.getDescription()).andReturn("Beam me up");
        expect(mockCanoniser.getType()).andReturn("Starship");
        expect(mockCanoniser.getEMail()).andReturn("scott@mail.com");
        replay(mockCanoniser);

        Set<Canoniser> canoniserSet = new HashSet<Canoniser>();
        canoniserSet.add(mockCanoniser);

        expect(canoniserService.listByNativeType(msg.getNativeType())).andReturn(canoniserSet);
        replay(canoniserService);

        JAXBElement<ReadCanoniserInfoOutputMsgType> response = endpoint.readCanoniserInfo(request);
        verify(canoniserService);

        List<PluginInfo> infoResult = response.getValue().getPluginInfo();
        Assert.assertNotNull(infoResult);
        Assert.assertTrue(!infoResult.isEmpty());
        PluginInfo info = infoResult.iterator().next();
        Assert.assertNotNull(info);
        Assert.assertEquals("Plugin name does not match", info.getName(), "Test Plugin");
        Assert.assertEquals("Plugin version does not match", info.getVersion(), "1.0");
        Assert.assertEquals("Plugin author does not match", info.getAuthor(), "Scott");
        Assert.assertEquals("Plugin descr does not match", info.getDescription(), "Beam me up");
        Assert.assertEquals("Plugin type does not match", info.getType(), "Starship");
        Assert.assertEquals("Plugin type does not match", info.getEmail(), "scott@mail.com");

        verify(mockCanoniser);
    }

}
