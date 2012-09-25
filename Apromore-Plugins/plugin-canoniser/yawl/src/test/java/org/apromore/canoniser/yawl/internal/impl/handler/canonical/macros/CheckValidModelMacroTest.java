package org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros;

import static org.junit.Assert.assertFalse;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.xml.bind.JAXBException;

import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.impl.context.CanonicalConversionContext;
import org.apromore.canoniser.yawl.utils.TestUtils;
import org.apromore.cpf.CPFSchema;
import org.apromore.cpf.CanonicalProcessType;
import org.junit.Test;
import org.xml.sax.SAXException;

public class CheckValidModelMacroTest {

    @Test
    public void testRewrite() throws FileNotFoundException, JAXBException, SAXException, CanoniserException {
        final File file = new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/orderfulfillment.yawl.cpf");
        final CanonicalProcessType cpf = CPFSchema.unmarshalCanonicalFormat(new BufferedInputStream(new FileInputStream(file)), true).getValue();
        final CanonicalConversionContext context = new CanonicalConversionContext(cpf, new AnnotationsType());

        final CheckValidModelMacro m = new CheckValidModelMacro(context);
        assertFalse(m.rewrite(cpf));

        final File invalidFile = new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/External/PNML/12_VendingMachine.cpf");
        final CanonicalProcessType invalidCPF = CPFSchema.unmarshalCanonicalFormat(new BufferedInputStream(new FileInputStream(invalidFile)), false)
                .getValue();
        final CanonicalConversionContext invalidContext = new CanonicalConversionContext(invalidCPF, new AnnotationsType());

//        final CheckValidModelMacro m2 = new CheckValidModelMacro(invalidContext);
//        try {
//            m2.rewrite(invalidCPF);
//            fail("Invalid model not detected!");
//        } catch (final CanoniserException e) {
//        }
    }

}
