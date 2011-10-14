package org.apromore.manager.service;

import org.apromore.manager.canoniser.ManagerCanoniserClient;
import org.apromore.manager.da.ManagerDataAccessClient;
import org.apromore.manager.toolbox.ManagerToolboxClient;
import org.apromore.model.ObjectFactory;
import org.apromore.model.ReadAllUsersInputMsgType;
import org.apromore.model.ReadAllUsersOutputMsgType;
import org.apromore.model.ReadUserInputMsgType;
import org.apromore.model.ReadUserOutputMsgType;
import org.apromore.model.UserType;
import org.apromore.model.UsernamesType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

/**
 * Test the Manager Portal Endpoint WebService.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:META-INF/spring/applicationContext-services-TEST.xml")
public class ReadAllUsersEndpointTest {

    private ManagerPortalEndpoint endpoint;
    private Document requestDocument;
    private Document responseDocument;
    private ManagerDataAccessClient daMock;
    private ManagerCanoniserClient caMock;
    private ManagerToolboxClient tbMock;

    @Before
    public void setUp() throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        requestDocument = documentBuilder.newDocument();
        responseDocument = documentBuilder.newDocument();
        daMock = createMock(ManagerDataAccessClient.class);
        caMock = createMock(ManagerCanoniserClient.class);
        tbMock = createMock(ManagerToolboxClient.class);
        endpoint = new ManagerPortalEndpoint();
        endpoint.setCaClient(caMock);
        endpoint.setTbClient(tbMock);
        endpoint.setDaClient(daMock);
    }


    @Test
    public void testInvokeReadAllUsers() throws Exception {
        ReadAllUsersInputMsgType msg = new ReadAllUsersInputMsgType();
        msg.setEmpty("");
        JAXBElement<ReadAllUsersInputMsgType> request = new ObjectFactory().createReadAllUsersRequest(msg);

        UsernamesType result = new UsernamesType();
        expect(daMock.ReadAllUsers()).andReturn(result);

        replay(daMock);

        JAXBElement<ReadAllUsersOutputMsgType> response = endpoint.readAllUsers(request);
        Assert.assertNotNull(response.getValue().getResult());
        Assert.assertNotNull(response.getValue().getUsernames());
        Assert.assertEquals("Result Code Doesn't Match", response.getValue().getResult().getCode().intValue(), 0);
        Assert.assertEquals("UserNames should be empty", response.getValue().getUsernames().getUsername().size(), 0);

        verify(daMock);
    }

}
