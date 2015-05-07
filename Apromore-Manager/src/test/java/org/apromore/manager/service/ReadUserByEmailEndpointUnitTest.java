package org.apromore.manager.service;

import org.apromore.dao.model.User;
import org.apromore.manager.ManagerPortalEndpoint;
import org.apromore.model.ObjectFactory;
import org.apromore.model.ReadUserByEmailInputMsgType;
import org.apromore.model.ReadUserByEmailOutputMsgType;

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
public class ReadUserByEmailEndpointUnitTest extends AbstractEndpointUnitTest {

    @Test
    public void testInvokeReadUser() throws Exception {
        ReadUserByEmailInputMsgType msg = new ReadUserByEmailInputMsgType();
        msg.setEmail("someone");
        JAXBElement<ReadUserByEmailInputMsgType> request = new ObjectFactory().createReadUserByEmailRequest(msg);

        User user = new User();
        user.setLastActivityDate(new Date());
        expect(secSrv.getUserByEmail(msg.getEmail())).andReturn(user);

        replay(secSrv);

        JAXBElement<ReadUserByEmailOutputMsgType> response = endpoint.readUserByEmail(request);
        Assert.assertNotNull(response.getValue().getResult());
        Assert.assertNotNull(response.getValue().getUser());
        Assert.assertEquals("Result Code Doesn't Match", response.getValue().getResult().getCode().intValue(), 0);
        Assert.assertEquals("UserType shouldn't contain anything", response.getValue().getUser().getFirstName(), null);

        verify(secSrv);
    }

    @Test
    public void testInvokeReadUserNotFound() throws Exception {
        ReadUserByEmailInputMsgType msg = new ReadUserByEmailInputMsgType();
        msg.setEmail("someone");
        JAXBElement<ReadUserByEmailInputMsgType> request = new ObjectFactory().createReadUserByEmailRequest(msg);

        User user = null;
        expect(secSrv.getUserByEmail(msg.getEmail())).andReturn(user);

        replay(secSrv);

        JAXBElement<ReadUserByEmailOutputMsgType> response = endpoint.readUserByEmail(request);
        Assert.assertNotNull(response.getValue().getResult());
        Assert.assertEquals("Result Code Doesn't Match", response.getValue().getResult().getCode().intValue(), -1);

        verify(secSrv);
    }
}

