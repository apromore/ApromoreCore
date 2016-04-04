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

import org.apromore.anf.AnnotationsType;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.exception.LockFailedException;
import org.apromore.exception.SerializationException;
import org.apromore.graph.canonical.Canonical;
import org.apromore.manager.ManagerPortalEndpoint;
import org.apromore.model.DeployProcessInputMsgType;
import org.apromore.model.DeployProcessOutputMsgType;
import org.apromore.model.ObjectFactory;
import org.apromore.model.PluginParameters;
import org.apromore.plugin.deployment.exception.DeploymentException;
import org.apromore.plugin.message.PluginMessage;

import org.junit.Assert;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.HashSet;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isNull;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

public class DeployProcessEndpointUnitTest extends AbstractEndpointUnitTest {

    @Test
    public void testDeployProces() throws DeploymentException, SerializationException, LockFailedException {
        DeployProcessInputMsgType msg = new DeployProcessInputMsgType();
        JAXBElement<DeployProcessInputMsgType> request = new ObjectFactory().createDeployProcessRequest(msg );

        String branchName = "test";
        request.getValue().setBranchName(branchName);
        String processName = "test";
        request.getValue().setProcessName(processName);
        String versionName = "1.0";
        request.getValue().setVersionName(versionName);
        String nativeType = "Test 2.1";
        request.getValue().setNativeType(nativeType);
        PluginParameters pluginProperties = new PluginParameters();
        request.getValue().setDeploymentParameters(pluginProperties);

        expect(procSrv.getCurrentProcessModel(processName, branchName, false)).andReturn(new CanonicalProcessType());
        expect(deploymentService.deployProcess(eq(nativeType), anyObject(CanonicalProcessType.class), isNull(AnnotationsType.class),
                anyObject(HashSet.class))).andReturn(new ArrayList<PluginMessage>());

        replayAll();

        JAXBElement<DeployProcessOutputMsgType> result = endpoint.deployProcess(request);

        Assert.assertNotNull(result.getValue().getResult());
        Assert.assertEquals("Result Code Doesn't Match", result.getValue().getResult().getCode().intValue(), 0);

        assertNotNull(result.getValue().getMessage());

        verifyAll();
    }
}
