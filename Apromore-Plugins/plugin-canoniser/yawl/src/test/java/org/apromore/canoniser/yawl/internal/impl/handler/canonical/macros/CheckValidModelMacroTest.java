package org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.xml.bind.JAXBException;

import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.impl.MessageManagerImpl;
import org.apromore.canoniser.yawl.internal.impl.context.CanonicalConversionContext;
import org.apromore.canoniser.yawl.utils.TestUtils;
import org.apromore.cpf.CPFSchema;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.impl.DefaultMessageAwarePlugin;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

public class CheckValidModelMacroTest {

    private DefaultMessageAwarePlugin pluginMock;
    private MessageManagerImpl messageMock;

    @Before
    public void setUp() {
        pluginMock = new DefaultMessageAwarePlugin() {
        };
        messageMock = new MessageManagerImpl(pluginMock);
    }

    @Test
    public void testValidNet() throws FileNotFoundException, JAXBException, SAXException, CanoniserException {
        final File file = new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/orderfulfillment.yawl.cpf");
        final CanonicalProcessType cpf = CPFSchema.unmarshalCanonicalFormat(new BufferedInputStream(new FileInputStream(file)), true).getValue();
        final CanonicalConversionContext context = new CanonicalConversionContext(cpf, new AnnotationsType(), messageMock);

        final CheckValidModelMacro m = new CheckValidModelMacro(context);
        assertFalse(m.rewrite(cpf));
        assertTrue(pluginMock.getPluginMessages().isEmpty());
    }

    @Test
    public void testNetWithoutSourceAndSink() throws FileNotFoundException, JAXBException, SAXException, CanoniserException {
        final File invalidFile = new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/External/PNML/12_VendingMachine.cpf");
        final CanonicalProcessType invalidCPF = CPFSchema.unmarshalCanonicalFormat(new BufferedInputStream(new FileInputStream(invalidFile)), false)
                .getValue();
        final CanonicalConversionContext invalidContext = new CanonicalConversionContext(invalidCPF, new AnnotationsType(), messageMock);

        final CheckValidModelMacro m = new CheckValidModelMacro(invalidContext);
        try {
            m.rewrite(invalidCPF);
            fail();
        } catch (CanoniserException e) {
        }
    }

    @Test
    public void testNetWithDisconnectedNodes() throws FileNotFoundException, JAXBException, SAXException, CanoniserException {
        final File invalidFile = new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/EmptyNet.yawl.cpf");
        final CanonicalProcessType warningCPF = CPFSchema.unmarshalCanonicalFormat(new BufferedInputStream(new FileInputStream(invalidFile)), false)
                .getValue();

        final CanonicalConversionContext invalidContext = new CanonicalConversionContext(warningCPF, new AnnotationsType(), messageMock);
        final CheckValidModelMacro m = new CheckValidModelMacro(invalidContext);
        assertFalse(m.rewrite(warningCPF));
        assertFalse(pluginMock.getPluginMessages().isEmpty());
        assertNotNull(pluginMock.getPluginMessages().iterator().next().getMessage());
    }

}
