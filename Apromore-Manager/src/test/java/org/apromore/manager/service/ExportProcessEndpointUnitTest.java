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

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import javax.activation.DataHandler;
import javax.xml.bind.JAXBElement;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import org.apromore.TestData;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.exception.ExportFormatException;
import org.apromore.exception.ImportException;
import org.apromore.helper.Version;
import org.apromore.manager.ManagerPortalEndpoint;
import org.apromore.model.ExportFormatInputMsgType;
import org.apromore.model.ExportFormatOutputMsgType;
import org.apromore.model.ExportFormatResultType;
import org.apromore.model.ObjectFactory;
import org.apromore.model.PluginMessages;
import org.apromore.model.PluginParameter;
import org.apromore.model.PluginParameters;
import org.apromore.plugin.message.PluginMessage;
import org.apromore.plugin.message.PluginMessageImpl;
import org.apromore.service.model.CanonisedProcess;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

public class ExportProcessEndpointUnitTest extends AbstractEndpointUnitTest {

    @Test
    public void testImportProcess() throws ImportException, IOException, CanoniserException, ExportFormatException {

        ExportFormatInputMsgType msg = new ExportFormatInputMsgType();
        msg.setAnnotationName("");
        msg.setFormat("EPML 2.0");
        msg.setOwner("test");
        msg.setProcessId(123);
        msg.setProcessName("test");
        msg.setBranchName("1.0");
        msg.setVersionNumber("1.0");
        msg.setWithAnnotations(true);

        PluginParameters properties = new PluginParameters();
        PluginParameter property = new PluginParameter();
        property.setId("test");
        property.setClazz("java.lang.String");
        property.setName("test");
        property.setValue("");
        property.setIsMandatory(false);
        property.setDescription("");
        properties.getParameter().add(property);
        msg.setCanoniserParameters(properties);

        JAXBElement<ExportFormatInputMsgType> request = new ObjectFactory().createExportFormatRequest(msg);

        CanonisedProcess cp = new CanonisedProcess();
        ArrayList<PluginMessage> pluginMsg = new ArrayList<>();
        pluginMsg.add(new PluginMessageImpl("test"));
        cp.setMessages(pluginMsg);

        Version version = new Version(msg.getVersionNumber());
        ExportFormatResultType exportFormatResultType = new ExportFormatResultType();
        exportFormatResultType.setMessage(new PluginMessages());
        exportFormatResultType.setNative(new DataHandler(TestData.EPML, "text/xml"));
        expect(procSrv.exportProcess(eq(msg.getProcessName()), eq(msg.getProcessId()), eq(msg.getBranchName()), eq(version),
                eq(msg.getFormat()), eq(msg.getAnnotationName()), EasyMock.anyBoolean(), anyObject(Set.class))).andReturn(exportFormatResultType);
        replayAll();

        JAXBElement<ExportFormatOutputMsgType> response = endpoint.exportFormat(request);

        Assert.assertNotNull(response.getValue().getResult());
        Assert.assertNotNull(response.getValue().getExportResult());
        Assert.assertEquals("Result Code Doesn't Match", response.getValue().getResult().getCode().intValue(), 0);

        verifyAll();
    }

}
