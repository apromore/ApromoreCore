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

import org.apromore.model.ObjectFactory;
import org.apromore.model.ReadProcessSummariesInputMsgType;
import org.apromore.model.ReadProcessSummariesOutputMsgType;

import org.apromore.model.SummariesType;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.bind.JAXBElement;

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

/**
 * Test the Manager Portal Endpoint WebService.
 */
public class ReadProcessSummariesEndpointUnitTest extends AbstractEndpointUnitTest {

    @Test
    public void testInvokeReadProcessSummaries() throws Exception {
        String searchExpression = "";

        ReadProcessSummariesInputMsgType msg = new ReadProcessSummariesInputMsgType();
        msg.setFolderId(0);
        msg.setSearchExpression(searchExpression);
        JAXBElement<ReadProcessSummariesInputMsgType> request = new ObjectFactory().createReadProcessSummariesRequest(msg);

        SummariesType procSummary = new SummariesType();
        expect(procSrv.readProcessSummaries(0, searchExpression)).andReturn(procSummary);

        replayAll();

        JAXBElement<ReadProcessSummariesOutputMsgType> response = endpoint.readProcessSummaries(request);
        Assert.assertNotNull(response.getValue().getResult());
        Assert.assertNotNull(response.getValue().getProcessSummaries());
        Assert.assertEquals("Result Code Doesn't Match", response.getValue().getResult().getCode().intValue(), 0);
        Assert.assertEquals("UserNames should be empty", response.getValue().getProcessSummaries().getSummary().size(), 0);

        verifyAll();
    }

}
