package org.apromore.canoniser.yawl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.utils.TestUtils;
import org.apromore.cpf.CPFSchema;
import org.apromore.cpf.CanonicalProcessType;
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
    public void testCanonise() throws CanoniserException, FileNotFoundException {
        final YAWL22Canoniser c = new YAWL22Canoniser();
        final List<CanonicalProcessType> cList = new ArrayList<CanonicalProcessType>(0);
        final List<AnnotationsType> aList = new ArrayList<AnnotationsType>(0);
        final File file = new File(TestUtils.TEST_RESOURCES_DIRECTORY + "YAWL/Patterns/ControlFlow/WPC3Synchronization.yawl");
        c.canonise(new BufferedInputStream(new FileInputStream(file)), aList, cList);
        assertEquals(1, cList.size());
        assertEquals(1, aList.size());
    }

    @Test
    public void testDeCanonise() throws CanoniserException, FileNotFoundException, JAXBException, SAXException {
        final YAWL22Canoniser c = new YAWL22Canoniser();
        final File file = new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/WPC4ExclusiveChoice.yawl.cpf");
        final ByteArrayOutputStream nativeOutput = new ByteArrayOutputStream();
        c.deCanonise(CPFSchema.unmarshalCanonicalFormat(new BufferedInputStream(new FileInputStream(file)), true).getValue(), null, nativeOutput);
        assertNotNull(nativeOutput);
        final SpecificationSetFactsType yawlFormat = YAWLSchema.unmarshalYAWLFormat(new ByteArrayInputStream(nativeOutput.toByteArray()), true)
                .getValue();
        assertNotNull(yawlFormat);
    }

}
