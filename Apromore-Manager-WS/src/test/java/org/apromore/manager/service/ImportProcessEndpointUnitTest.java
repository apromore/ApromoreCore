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

import java.io.IOException;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.exception.ImportException;
import org.apromore.manager.ManagerPortalEndpoint;
import org.junit.Test;

public class ImportProcessEndpointUnitTest extends AbstractEndpointUnitTest {

    @Test
    public void testImportProcess() throws ImportException, IOException, CanoniserException {
//        ImportProcessInputMsgType msg = new ImportProcessInputMsgType();
//        EditSessionType edit = new EditSessionType();
//        edit.setAnnotation("test");
//        edit.setCreationDate("");
//        edit.setDomain("");
//        edit.setLastUpdate("");
//        edit.setNativeType("EPML 2.0");
//        edit.setProcessId(12143);
//        edit.setProcessName("test");
//        edit.setUsername("test");
//        edit.setVersionName("1.0");
//        edit.setWithAnnotation(true);
//        msg.setEditSession(edit);
//        PluginParameters properties = new PluginParameters();
//        PluginParameter property = new PluginParameter();
//        property.setId("test");
//        property.setClazz("java.lang.String");
//        property.setName("test");
//        property.setValue("");
//        properties.getParameter().add(property);
//        msg.setCanoniserParameters(properties);
//        DataHandler nativeXml = new DataHandler(TestData.EPML, "text/xml");
//        msg.setProcessDescription(nativeXml);
//        JAXBElement<ImportProcessInputMsgType> request = new ObjectFactory().createImportProcessRequest(msg);
//
//        CanonisedProcess cp = new CanonisedProcess();
//        ArrayList<PluginMessage> pluginMsg = new ArrayList<PluginMessage>();
//        pluginMsg.add(new PluginMessageImpl("test"));
//        cp.setMessages(pluginMsg);
//        expect(canoniserService.canonise(eq(edit.getNativeType()), anyObject(InputStream.class), anyObject(java.util.Set.class))).andReturn(cp);
//
//        ProcessModelVersion procVersion = new ProcessModelVersion();
//        ProcessSummaryType procSummary = new ProcessSummaryType();
//        expect(procSrv.importProcess(eq(edit.getUsername()), eq(edit.getUsername()), anyObject(String.class), eq(edit.getVersionName()),
//                eq(edit.getNativeType()), eq(cp), anyObject(InputStream.class), eq(edit.getDomain()), anyObject(String.class),
//                eq(edit.getCreationDate()), eq(edit.getLastUpdate()))).andReturn(procVersion);
//        replayAll();
//
//        JAXBElement<ImportProcessOutputMsgType> response = endpoint.importProcess(request);
//
//        Assert.assertNotNull(response.getValue().getResult());
////        Assert.assertNotNull(response.getValue().getImportProcessResult());
////        Assert.assertEquals("Result Code Doesn't Match", response.getValue().getResult().getCode().intValue(), 0);
//
//        Assert.assertNotNull(response.getValue().getImportProcessResult().getMessage());
//        Assert.assertEquals(response.getValue().getImportProcessResult().getMessage().getMessage().get(0).getValue(), "test");
////        Assert.assertNotNull(response.getValue().getImportProcessResult().getProcessSummary());
//
//        verifyAll();
    }

}
