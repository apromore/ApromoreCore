/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2011, 2012 , 2015 - 2017 Queensland University of Technology.
 * Copyright (C) 2012 Felix Mannhardt.
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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

package org.apromore.service.impl;

import org.apromore.anf.AnnotationsType;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.PluginRequest;
import org.apromore.plugin.PluginResultImpl;
import org.apromore.plugin.deployment.DeploymentPlugin;
import org.apromore.plugin.deployment.provider.DeploymentPluginProvider;
import org.apromore.plugin.exception.PluginException;
import org.apromore.plugin.exception.PluginNotFoundException;
import org.apromore.plugin.message.PluginMessage;
import org.apromore.plugin.property.ParameterType;
import org.apromore.plugin.property.PluginParameterType;
import org.apromore.plugin.property.RequestParameterType;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

//TODO fix with OSGi
@Ignore
public class DeploymentServiceImplUnitTest {

    private DeploymentServiceImpl myService;

    private DeploymentPluginProvider provider;
    private Set<DeploymentPlugin> deploymentSet;
    private DeploymentPlugin mockDeploymentPlugin;

    @Before
    public void setup() {
        mockDeploymentPlugin = createMock(DeploymentPlugin.class);

        deploymentSet = new HashSet<>();
        deploymentSet.add(mockDeploymentPlugin);

        myService = new DeploymentServiceImpl(provider);
    }

    @Test
    public void testFindDeploymentPlugins() throws PluginNotFoundException {
        HashSet<ParameterType<?>> mandatoryProperties = new HashSet<>();
        ParameterType<String> prop = new PluginParameterType<>("test", "test", String.class, "test", true);
        mandatoryProperties.add(prop);
        expect(mockDeploymentPlugin.getNativeType()).andReturn("test");
        expect(mockDeploymentPlugin.getName()).andReturn("test");
        expect(mockDeploymentPlugin.getVersion()).andReturn("1.0");
        expect(mockDeploymentPlugin.getMandatoryParameters()).andReturn(mandatoryProperties);

        replay(mockDeploymentPlugin);

        Set<DeploymentPlugin> deploymentPlugin = myService.listDeploymentPlugin("test");
        assertNotNull(deploymentPlugin);
        assertTrue(!deploymentPlugin.isEmpty());
        assertEquals(mockDeploymentPlugin, deploymentPlugin.iterator().next());
        assertTrue(deploymentPlugin.iterator().next().getMandatoryParameters().contains(prop));

        verify(mockDeploymentPlugin);
    }

    @Test
    public void testDeployProcess() throws PluginException {
        HashSet<RequestParameterType<?>> mandatoryProperties = new HashSet<>();
        RequestParameterType<String> prop = new RequestParameterType<>("test", "test");
        mandatoryProperties.add(prop);

        CanonicalProcessType cpf = new CanonicalProcessType();
        AnnotationsType anf = new AnnotationsType();

        expect(mockDeploymentPlugin.getNativeType()).andReturn("test");
        expect(mockDeploymentPlugin.getName()).andReturn("test");
        expect(mockDeploymentPlugin.getVersion()).andReturn("1.0");
        PluginResultImpl result = new PluginResultImpl();
        result.addPluginMessage("test");
        expect(mockDeploymentPlugin.deployProcess(EasyMock.eq(cpf), EasyMock.eq(anf), EasyMock.anyObject(PluginRequest.class))).andReturn(result);

        replay(mockDeploymentPlugin);

        List<PluginMessage> messages = myService.deployProcess("test", cpf, anf, mandatoryProperties);
        assertTrue(messages.size() == 1);

        verify(mockDeploymentPlugin);
    }

}
