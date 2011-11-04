package org.apromore.manager.service;

import org.apromore.dao.model.User;
import org.apromore.mapper.UserMapper;
import org.apromore.model.ObjectFactory;
import org.apromore.model.UserType;
import org.apromore.model.WriteUserInputMsgType;
import org.apromore.model.WriteUserOutputMsgType;
import org.apromore.service.UserService;
import org.apromore.service.impl.UserServiceImpl;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

/**
 * Test the Read User method on the Manager Portal Endpoint WebService.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/applicationContext-jpa-TEST.xml",
        "classpath:META-INF/spring/applicationContext-services-TEST.xml"
})
@PrepareForTest({ UserService.class })
public class WriteUserEndpointTest {

    private ManagerPortalEndpoint endpoint;
    private Document requestDocument;
    private Document responseDocument;

    private UserService userSrv;


    @Before
    public void setUp() throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        requestDocument = documentBuilder.newDocument();
        responseDocument = documentBuilder.newDocument();
        userSrv = createMock(UserServiceImpl.class);
        endpoint = new ManagerPortalEndpoint();
        endpoint.setUserSrv(userSrv);
    }


    @Test
    public void testInvokeWriteUser() throws Exception {
        mockStatic(UserMapper.class);

        UserType userType = createUserType();
        User user = UserMapper.convertFromUserType(userType);

        WriteUserInputMsgType msg = new WriteUserInputMsgType();
        msg.setUser(userType);
        JAXBElement<WriteUserInputMsgType> request = new ObjectFactory().createWriteUserRequest(msg);

        userSrv.writeUser(EasyMock.<User>anyObject());

        replayAll();

        JAXBElement<WriteUserOutputMsgType> response = endpoint.writeUser(request);
        Assert.assertNotNull(response.getValue().getResult());
        Assert.assertEquals("Result Code Doesn't Match", response.getValue().getResult().getCode().intValue(), 0);

        verifyAll();
    }

//    @Test
//    public void testInvokeWriteUserThrowsException() throws Exception {
//        mockStatic(UserMapper.class);
//
//        UserType userType = createUserType();
//
//        WriteUserInputMsgType msg = new WriteUserInputMsgType();
//        msg.setUser(userType);
//        JAXBElement<WriteUserInputMsgType> request = new ObjectFactory().createWriteUserRequest(msg);
//
//        Exception e = new Exception("WriteUser threw exception");
//        userSrv.writeUser(EasyMock.<User>anyObject());
//        expectLastCall().andThrow(e);
//
//        replayAll();
//
//        JAXBElement<WriteUserOutputMsgType> response = endpoint.writeUser(request);
//        Assert.assertNotNull(response.getValue().getResult());
//        Assert.assertEquals("Result Code Doesn't Match", response.getValue().getResult().getCode().intValue(), -1);
//        Assert.assertEquals("Result Message Doesn't Match", response.getValue().getResult().getMessage(), "WriteUser threw exception");
//
//        verifyAll();
//    }

    private UserType createUserType() {
        UserType userType = new UserType();
        userType.setUsername("test");
        userType.setEmail("test@test.com");
        userType.setFirstname("test");
        userType.setLastname("tester");
        userType.setPasswd("test");
        return userType;
    }

}

