package org.apromore.manager.service;

import org.apromore.dao.model.User;
import org.apromore.manager.canoniser.ManagerCanoniserClient;
import org.apromore.model.ObjectFactory;
import org.apromore.model.ReadAllUsersInputMsgType;
import org.apromore.model.ReadAllUsersOutputMsgType;
import org.apromore.service.UserService;
import org.apromore.service.impl.UserServiceImpl;
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
import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

/**
 * Test the Manager Portal Endpoint WebService.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/applicationContext-jpa-TEST.xml",
        "classpath:META-INF/spring/applicationContext-services-TEST.xml"
})
public class ReadAllUsersEndpointTest {

    private ManagerPortalEndpoint endpoint;
    private Document requestDocument;
    private Document responseDocument;
    private ManagerCanoniserClient caMock;


    private UserService userSrv;


    @Before
    public void setUp() throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        requestDocument = documentBuilder.newDocument();
        responseDocument = documentBuilder.newDocument();
        caMock = createMock(ManagerCanoniserClient.class);
        userSrv = createMock(UserServiceImpl.class);
        endpoint = new ManagerPortalEndpoint();
        endpoint.setCaClient(caMock);
        endpoint.setUserSrv(userSrv);
    }


    @Test
    public void testInvokeReadAllUsers() throws Exception {
        ReadAllUsersInputMsgType msg = new ReadAllUsersInputMsgType();
        msg.setEmpty("");
        JAXBElement<ReadAllUsersInputMsgType> request = new ObjectFactory().createReadAllUsersRequest(msg);

        List<User> users = new ArrayList<User>();
        expect(userSrv.findAllUsers()).andReturn(users);

        replay(userSrv);

        JAXBElement<ReadAllUsersOutputMsgType> response = endpoint.readAllUsers(request);
        Assert.assertNotNull(response.getValue().getResult());
        Assert.assertNotNull(response.getValue().getUsernames());
        Assert.assertEquals("Result Code Doesn't Match", response.getValue().getResult().getCode().intValue(), 0);
        Assert.assertEquals("UserNames should be empty", response.getValue().getUsernames().getUsername().size(), 0);

        verify(userSrv);
    }

//    @Test
//    public void testInvokeReadAllUsersThrowsException() throws Exception {
//        ReadAllUsersInputMsgType msg = new ReadAllUsersInputMsgType();
//        msg.setEmpty("");
//        JAXBElement<ReadAllUsersInputMsgType> request = new ObjectFactory().createReadAllUsersRequest(msg);
//
//        expect(userSrv.findAllUsers()).andThrow(new Exception());
//
//        replay(userSrv);
//
//        JAXBElement<ReadAllUsersOutputMsgType> response = endpoint.readAllUsers(request);
//        Assert.assertNotNull(response.getValue().getResult());
//        Assert.assertEquals("Result Code Doesn't Match", response.getValue().getResult().getCode().intValue(), -1);
//
//        verify(userSrv);
//    }

}
