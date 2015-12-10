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

package org.apromore.canoniser.yawl.internal.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.TaskType;
import org.junit.Test;
import org.w3c.dom.Element;
import org.yawlfoundation.yawlschema.ConfigurationType;
import org.yawlfoundation.yawlschema.WebServiceGatewayFactsType;
import org.yawlfoundation.yawlschema.WebServiceGatewayFactsType.YawlService;

public class ExtensionUtilsUnitTest {

    @Test
    public void testIsValidFragment() throws CanoniserException {
        final Element fragment = ExtensionUtils.marshalYAWLFragment("yawlService", new WebServiceGatewayFactsType.YawlService(),
                WebServiceGatewayFactsType.YawlService.class);
        assertTrue(ExtensionUtils.isValidFragment(fragment, "http://www.yawlfoundation.org/yawlschema", "yawlService"));
        assertFalse(ExtensionUtils.isValidFragment(fragment, "", "yawlService"));
    }

    @Test
    public void testUnmarshalYAWLFragment() throws CanoniserException {
        final WebServiceGatewayFactsType.YawlService serviceOld = new WebServiceGatewayFactsType.YawlService();
        serviceOld.setDocumentation("test1");
        serviceOld.setId("test2");
        serviceOld.setOperationName("test3");
        serviceOld.setWsdlLocation("test4");
        final Element fragment = ExtensionUtils.marshalYAWLFragment("yawlService", serviceOld, WebServiceGatewayFactsType.YawlService.class);
        final YawlService serviceNew = ExtensionUtils.unmarshalYAWLFragment(fragment, WebServiceGatewayFactsType.YawlService.class);
        assertNotNull(serviceNew);
        assertNotSame(serviceOld, serviceNew);
        assertEquals("test1", serviceNew.getDocumentation());
        assertEquals("test2", serviceNew.getId());
        assertEquals("test3", serviceNew.getOperationName());
        assertEquals("test4", serviceNew.getWsdlLocation());
    }

    @Test
    public void testMarshalYAWLFragment() throws CanoniserException {
        final Element fragment = ExtensionUtils.marshalYAWLFragment("yawlService", new WebServiceGatewayFactsType.YawlService(),
                WebServiceGatewayFactsType.YawlService.class);
        assertNotNull(fragment);
        final Element fragment2 = ExtensionUtils.marshalYAWLFragment("configuration", new ConfigurationType(), ConfigurationType.class);
        assertNotNull(fragment2);
    }

    @Test
    public void testAddToExtensionsElementNodeType() throws CanoniserException {
        final Element fragment = ExtensionUtils.marshalYAWLFragment("yawlService", new WebServiceGatewayFactsType.YawlService(),
                WebServiceGatewayFactsType.YawlService.class);
        final NodeType node = new TaskType();
        ExtensionUtils.addToExtensions(fragment, node);
        assertEquals(1, node.getAttribute().size());
        assertEquals(fragment, node.getAttribute().get(0).getAny());
        assertEquals("http://www.yawlfoundation.org/yawlschema/yawlService", node.getAttribute().get(0).getName());
        assertNull(node.getAttribute().get(0).getValue());
    }

    @Test
    public void testAddToExtensionsElementCanonicalProcessType() throws CanoniserException {
        final Element fragment = ExtensionUtils.marshalYAWLFragment("yawlService", new WebServiceGatewayFactsType.YawlService(),
                WebServiceGatewayFactsType.YawlService.class);
        final CanonicalProcessType cpf = new CanonicalProcessType();
        ExtensionUtils.addToExtensions(fragment, cpf);
        assertEquals(1, cpf.getAttribute().size());
        assertEquals(fragment, cpf.getAttribute().get(0).getAny());
        assertEquals("http://www.yawlfoundation.org/yawlschema/yawlService", cpf.getAttribute().get(0).getName());
        assertNull(cpf.getAttribute().get(0).getValue());
    }

    @Test
    public void testAddToExtensionsElementNetType() throws CanoniserException {
        final Element fragment = ExtensionUtils.marshalYAWLFragment("yawlService", new WebServiceGatewayFactsType.YawlService(),
                WebServiceGatewayFactsType.YawlService.class);
        final NetType net = new NetType();
        ExtensionUtils.addToExtensions(fragment, net);
        assertEquals(1, net.getAttribute().size());
        assertEquals(fragment, net.getAttribute().get(0).getAny());
        assertEquals("http://www.yawlfoundation.org/yawlschema/yawlService", net.getAttribute().get(0).getName());
        assertNull(net.getAttribute().get(0).getValue());
    }

    @Test
    public void testAddToExtensionsElementResourceTypeType() throws CanoniserException {
        final Element fragment = ExtensionUtils.marshalYAWLFragment("yawlService", new WebServiceGatewayFactsType.YawlService(),
                WebServiceGatewayFactsType.YawlService.class);
        final ResourceTypeType resource = new ResourceTypeType();
        ExtensionUtils.addToExtensions(fragment, resource);
        assertEquals(1, resource.getAttribute().size());
        assertEquals(fragment, resource.getAttribute().get(0).getAny());
        assertEquals("http://www.yawlfoundation.org/yawlschema/yawlService", resource.getAttribute().get(0).getName());
    }

}
