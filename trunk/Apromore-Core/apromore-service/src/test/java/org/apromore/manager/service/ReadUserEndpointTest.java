package org.apromore.manager.service;

import org.apromore.dao.model.User;
import org.apromore.exception.UserNotFoundException;
import org.apromore.manager.canoniser.ManagerCanoniserClient;
import org.apromore.model.ObjectFactory;
import org.apromore.model.ReadUserInputMsgType;
import org.apromore.model.ReadUserOutputMsgType;
import org.apromore.service.UserService;
import org.apromore.service.impl.UserServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.xml.bind.JAXBElement;

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

/**
 * Test the Read User method on the Manager Portal Endpoint WebService.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/applicationContext-jpa-TEST.xml",
        "classpath:META-INF/spring/applicationContext-services-TEST.xml"
})
public class ReadUserEndpointTest {

    private ManagerPortalEndpoint endpoint;
    private ManagerCanoniserClient caMock;

    private UserService userSrv;


    @Before
    public void setUp() throws Exception {
        caMock = createMock(ManagerCanoniserClient.class);
        userSrv = createMock(UserServiceImpl.class);
        endpoint = new ManagerPortalEndpoint();
        endpoint.setCaClient(caMock);
        endpoint.setUserSrv(userSrv);
    }


    @Test
    public void testInvokeReadUser() throws Exception {
        ReadUserInputMsgType msg = new ReadUserInputMsgType();
        msg.setUsername("someone");
        JAXBElement<ReadUserInputMsgType> request = new ObjectFactory().createReadUserRequest(msg);

        User user = new User();
        expect(userSrv.findUserByLogin(msg.getUsername())).andReturn(user);

        replay(userSrv);

        JAXBElement<ReadUserOutputMsgType> response = endpoint.readUser(request);
        Assert.assertNotNull(response.getValue().getResult());
        Assert.assertNotNull(response.getValue().getUser());
        Assert.assertEquals("Result Code Doesn't Match", response.getValue().getResult().getCode().intValue(), 0);
        Assert.assertEquals("UserType shouldn't contain anything", response.getValue().getUser().getFirstname(), null);

        verify(userSrv);
    }

    @Test
    public void testInvokeReadUserThrowsException() throws Exception {
        ReadUserInputMsgType msg = new ReadUserInputMsgType();
        msg.setUsername("someone");
        JAXBElement<ReadUserInputMsgType> request = new ObjectFactory().createReadUserRequest(msg);

        expect(userSrv.findUserByLogin(msg.getUsername())).andThrow(new UserNotFoundException());

        replay(userSrv);

        JAXBElement<ReadUserOutputMsgType> response = endpoint.readUser(request);
        Assert.assertNotNull(response.getValue().getResult());
        Assert.assertEquals("Result Code Doesn't Match", response.getValue().getResult().getCode().intValue(), -1);

        verify(userSrv);
    }
}

