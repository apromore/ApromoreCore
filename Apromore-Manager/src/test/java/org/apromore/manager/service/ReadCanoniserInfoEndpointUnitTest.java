/*
 * Copyright © 2009-2015 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.manager.service;

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

import javax.xml.bind.JAXBElement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apromore.canoniser.Canoniser;
import org.apromore.canoniser.DefaultAbstractCanoniser;
import org.apromore.manager.ManagerPortalEndpoint;
import org.apromore.model.ObjectFactory;
import org.apromore.model.PluginInfo;
import org.apromore.model.ReadCanoniserInfoInputMsgType;
import org.apromore.model.ReadCanoniserInfoOutputMsgType;
import org.apromore.plugin.exception.PluginNotFoundException;

import org.junit.Assert;
import org.junit.Test;

public class ReadCanoniserInfoEndpointUnitTest extends AbstractEndpointUnitTest {

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
