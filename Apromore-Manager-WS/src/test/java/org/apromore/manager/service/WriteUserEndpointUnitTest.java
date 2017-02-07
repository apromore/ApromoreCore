/*
 * Copyright © 2009-2017 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.manager.service;

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import javax.xml.bind.JAXBElement;

import org.apromore.dao.model.User;
import org.apromore.manager.ManagerPortalEndpoint;
import org.apromore.mapper.UserMapper;
import org.apromore.model.*;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test the Read User method on the Manager Portal Endpoint WebService.
 */
public class WriteUserEndpointUnitTest extends AbstractEndpointUnitTest {

    @Test
    public void testInvokeWriteUser() throws Exception {
        mockStatic(UserMapper.class);

        UserType userType = createUserType();
        User user = UserMapper.convertFromUserType(userType);

        WriteUserInputMsgType msg = new WriteUserInputMsgType();
        msg.setUser(userType);
        JAXBElement<WriteUserInputMsgType> request = new ObjectFactory().createWriteUserRequest(msg);

        expect(secSrv.createUser(EasyMock.<User>anyObject())).andReturn(user);

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
        userType.setFirstName("test");
        userType.setLastName("tester");

        MembershipType membership = new MembershipType();
        membership.setEmail("test@test.com");
        membership.setPassword("password");
        userType.setMembership(membership);

        return userType;
    }

}

