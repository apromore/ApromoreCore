package org.apromore.canoniser.yawl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.utils.TestUtils;
import org.apromore.cpf.CPFSchema;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.property.PropertyType;
import org.junit.Test;
import org.xml.sax.SAXException;
import org.yawlfoundation.yawlschema.SpecificationSetFactsType;
import org.yawlfoundation.yawlschema.YAWLSchema;

public class YAWL22CanoniserTest {

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
        for (PropertyType prop: c.getAvailableProperties()) {
            if (prop.getValueType().equals(InputStream.class)) {
                prop.setValue(new FileInputStream(fileOrg));
            }
        }
        BufferedInputStream nativeInput = new BufferedInputStream(new FileInputStream(file));
        c.canonise(nativeInput, aList, cList);
        nativeInput.close();
        assertEquals(1, cList.size());
        assertEquals(1, aList.size());
        assertNotNull(c.getPluginMessages());
    }

    @Test
    public void testCanoniseWithoutOrgData() throws CanoniserException, IOException {
        final YAWL22Canoniser c = new YAWL22Canoniser();
        final List<CanonicalProcessType> cList = new ArrayList<CanonicalProcessType>(0);
        final List<AnnotationsType> aList = new ArrayList<AnnotationsType>(0);
        final File file = new File(TestUtils.TEST_RESOURCES_DIRECTORY + "YAWL/Patterns/ControlFlow/WPC3Synchronization.yawl");
        BufferedInputStream nativeInput = new BufferedInputStream(new FileInputStream(file));
        c.canonise(nativeInput, aList, cList);
        nativeInput.close();
        assertEquals(1, cList.size());
        assertEquals(1, aList.size());
        assertNotNull(c.getPluginMessages());
    }

    @Test
    public void testDeCanoniseWithOrgData() throws CanoniserException, JAXBException, SAXException, IOException {
        final YAWL22Canoniser c = new YAWL22Canoniser();
        final File file = new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/WPC4ExclusiveChoice.yawl.cpf");
        final ByteArrayOutputStream nativeOutput = new ByteArrayOutputStream();
        final File fileOrg = new File(TestUtils.TEST_RESOURCES_DIRECTORY + "YAWL/OrganisationalData/YAWLDefaultOrgData.ybkp");
        ByteArrayOutputStream orgData = new ByteArrayOutputStream();
        for (PropertyType prop: c.getAvailableProperties()) {
            if (prop.getValueType().equals(OutputStream.class)) {
                prop.setValue(orgData);
            }
            if (prop.getValueType().equals(InputStream.class)) {
                prop.setValue(new FileInputStream(fileOrg));
            }
        }
        c.deCanonise(CPFSchema.unmarshalCanonicalFormat(new BufferedInputStream(new FileInputStream(file)), true).getValue(), null, nativeOutput);
        assertNotNull(nativeOutput);
        final SpecificationSetFactsType yawlFormat = YAWLSchema.unmarshalYAWLFormat(new ByteArrayInputStream(nativeOutput.toByteArray()), true)
                .getValue();
        assertNotNull(yawlFormat);
        assertNotNull(c.getPluginMessages());
        assertNotNull(orgData);
        assertTrue(orgData.size() > 0);
        orgData.close();
        nativeOutput.close();
    }

}
