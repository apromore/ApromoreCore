package org.apromore.manager.service;

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;

import org.apromore.dao.model.User;
import org.apromore.manager.ManagerPortalEndpoint;
import org.apromore.model.ObjectFactory;
import org.apromore.model.ReadAllUsersInputMsgType;
import org.apromore.model.ReadAllUsersOutputMsgType;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test the Manager Portal Endpoint WebService.
 */
public class ReadAllUsersEndpointUnitTest extends AbstractEndpointUnitTest {

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
