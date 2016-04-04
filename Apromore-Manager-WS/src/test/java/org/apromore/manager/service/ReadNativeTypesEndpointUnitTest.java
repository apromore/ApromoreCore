/*
 * Copyright © 2009-2016 The Apromore Initiative.
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
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;

import org.apromore.dao.model.NativeType;
import org.apromore.manager.ManagerPortalEndpoint;
import org.apromore.model.ObjectFactory;
import org.apromore.model.ReadNativeTypesInputMsgType;
import org.apromore.model.ReadNativeTypesOutputMsgType;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test the Manager Portal Endpoint WebService.
 */
public class ReadNativeTypesEndpointUnitTest extends AbstractEndpointUnitTest {

    @Test
    public void testInvokeReadNativeTypeSummaries() throws Exception {
        ReadNativeTypesInputMsgType msg = new ReadNativeTypesInputMsgType();
        msg.setEmpty("");
        JAXBElement<ReadNativeTypesInputMsgType> request = new ObjectFactory().createReadNativeTypesRequest(msg);

        List<NativeType> procSummary = new ArrayList<>();
        expect(frmSrv.findAllFormats()).andReturn(procSummary);

        replayAll();

        JAXBElement<ReadNativeTypesOutputMsgType> response = endpoint.readNativeTypes(request);
        Assert.assertNotNull(response.getValue().getResult());
        Assert.assertNotNull(response.getValue().getNativeTypes());
        Assert.assertEquals("Result Code Doesn't Match", response.getValue().getResult().getCode().intValue(), 0);
        Assert.assertEquals("nativeTypes should be empty", response.getValue().getNativeTypes().getNativeType().size(), 0);

        verifyAll();
    }

}
