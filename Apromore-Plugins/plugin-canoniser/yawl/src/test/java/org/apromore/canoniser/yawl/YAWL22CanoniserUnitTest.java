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

package org.apromore.canoniser.yawl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.utils.TestUtils;
import org.apromore.cpf.CPFSchema;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.PluginRequestImpl;
import org.apromore.plugin.PluginResult;
import org.apromore.plugin.property.RequestParameterType;
import org.junit.Test;
import org.xml.sax.SAXException;
import org.yawlfoundation.yawlschema.SpecificationSetFactsType;
import org.yawlfoundation.yawlschema.YAWLSchema;

public class YAWL22CanoniserUnitTest {

    @Test
    public void testYAWL22Canoniser() {
        assertNotNull(new YAWL22Canoniser());
    }

    @Test
    public void testCanoniseWithOrgData() throws CanoniserException, IOException {
        final YAWL22Canoniser c = new YAWL22Canoniser();
        final List<CanonicalProcessType> cList = new ArrayList<CanonicalProcessType>(0);
        final List<AnnotationsType> aList = new ArrayList<AnnotationsType>(0);
        final File file = new File(TestUtils.TEST_RESOURCES_DIRECTORY + "YAWL/Patterns/ControlFlow/WPC3Synchronization.yawl");
        final File fileOrg = new File(TestUtils.TEST_RESOURCES_DIRECTORY + "YAWL/OrganisationalData/YAWLDefaultOrgData.ybkp");
        BufferedInputStream nativeInput = new BufferedInputStream(new FileInputStream(file));
        PluginRequestImpl request = new PluginRequestImpl();
        request.addRequestProperty(new RequestParameterType<InputStream>("readOrgData", new FileInputStream(fileOrg)));
        PluginResult result = c.canonise(nativeInput, aList, cList, request);
        nativeInput.close();
        assertEquals(1, cList.size());
        assertEquals(1, aList.size());
        assertNotNull(result.getPluginMessage());
    }

    @Test
    public void testCanoniseWithoutOrgData() throws CanoniserException, IOException {
        final YAWL22Canoniser c = new YAWL22Canoniser();
        final List<CanonicalProcessType> cList = new ArrayList<CanonicalProcessType>(0);
        final List<AnnotationsType> aList = new ArrayList<AnnotationsType>(0);
        final File file = new File(TestUtils.TEST_RESOURCES_DIRECTORY + "YAWL/Patterns/ControlFlow/WPC3Synchronization.yawl");
        BufferedInputStream nativeInput = new BufferedInputStream(new FileInputStream(file));
        PluginRequestImpl request = new PluginRequestImpl();
        PluginResult result = c.canonise(nativeInput, aList, cList, request);
        nativeInput.close();
        assertEquals(1, cList.size());
        assertEquals(1, aList.size());
        assertNotNull(result.getPluginMessage());
    }

    @Test
    public void testDeCanoniseWithOrgData() throws CanoniserException, JAXBException, SAXException, IOException {
        final YAWL22Canoniser c = new YAWL22Canoniser();
        final File file = new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/WPC4ExclusiveChoice.yawl.cpf");
        final ByteArrayOutputStream nativeOutput = new ByteArrayOutputStream();
        final File fileOrg = new File(TestUtils.TEST_RESOURCES_DIRECTORY + "YAWL/OrganisationalData/YAWLDefaultOrgData.ybkp");
        PluginRequestImpl request = new PluginRequestImpl();
        request.addRequestProperty(new RequestParameterType<InputStream>("readOrgData", new FileInputStream(fileOrg)));
        PluginResult result = c.deCanonise(CPFSchema.unmarshalCanonicalFormat(new BufferedInputStream(new FileInputStream(file)), true).getValue(), null, nativeOutput, request);
        assertNotNull(nativeOutput);
        final SpecificationSetFactsType yawlFormat = YAWLSchema.unmarshalYAWLFormat(new ByteArrayInputStream(nativeOutput.toByteArray()), true)
                .getValue();
        assertNotNull(yawlFormat);
        assertNotNull(result.getPluginMessage());
        nativeOutput.close();
    }

}
