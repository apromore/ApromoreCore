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

