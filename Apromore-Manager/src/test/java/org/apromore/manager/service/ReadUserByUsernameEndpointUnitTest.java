package org.apromore.manager.service;

import org.apromore.dao.model.User;
import org.apromore.manager.ManagerPortalEndpoint;
import org.apromore.model.ObjectFactory;
import org.apromore.model.ReadUserByUsernameInputMsgType;
import org.apromore.model.ReadUserByUsernameOutputMsgType;

import org.junit.Assert;
import org.junit.Test;

import javax.xml.bind.JAXBElement;
import java.util.Date;

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

/**
 * Test the Read User method on the Manager Portal Endpoint WebService.
 */
public class ReadUserByUsernameEndpointUnitTest extends AbstractEndpointUnitTest {

    @Test
    public void testInvokeReadUser() throws Exception {
        ReadUserByUsernameInputMsgType msg = new ReadUserByUsernameInputMsgType();
        msg.setUsername("someone");
        JAXBElement<ReadUserByUsernameInputMsgType> request = new ObjectFactory().createReadUserByUsernameRequest(msg);

        User user = new User();
        user.setLastActivityDate(new Date());
        expect(secSrv.getUserByName(msg.getUsername())).andReturn(user);

        replay(secSrv);

        JAXBElement<ReadUserByUsernameOutputMsgType> response = endpoint.readUserByUsername(request);
        Assert.assertNotNull(response.getValue().getResult());
        Assert.assertNotNull(response.getValue().getUser());
        Assert.assertEquals("Result Code Doesn't Match", response.getValue().getResult().getCode().intValue(), 0);
        Assert.assertEquals("UserType shouldn't contain anything", response.getValue().getUser().getFirstName(), null);

        verify(secSrv);
    }

    @Test
    public void testInvokeReadUserNotFound() throws Exception {
        ReadUserByUsernameInputMsgType msg = new ReadUserByUsernameInputMsgType();
        msg.setUsername("someone");
        JAXBElement<ReadUserByUsernameInputMsgType> request = new ObjectFactory().createReadUserByUsernameRequest(msg);

        User user = null;
        expect(secSrv.getUserByName(msg.getUsername())).andReturn(user);

        replay(secSrv);

        JAXBElement<ReadUserByUsernameOutputMsgType> response = endpoint.readUserByUsername(request);
        Assert.assertNotNull(response.getValue().getResult());
        Assert.assertEquals("Result Code Doesn't Match", response.getValue().getResult().getCode().intValue(), -1);

        verify(secSrv);
    }
}

