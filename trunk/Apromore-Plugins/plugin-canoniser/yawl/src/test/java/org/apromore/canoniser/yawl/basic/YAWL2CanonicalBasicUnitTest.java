/**
 * Copyright 2012, Felix Mannhardt
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.apromore.canoniser.yawl.basic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;

import javax.xml.bind.JAXBException;

import org.apromore.anf.AnnotationType;
import org.apromore.anf.AnnotationsType;
import org.apromore.anf.GraphicsType;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.YAWL2Canonical;
import org.apromore.canoniser.yawl.internal.impl.YAWL2CanonicalImpl;
import org.apromore.canoniser.yawl.internal.utils.ExtensionUtils;
import org.apromore.canoniser.yawl.utils.GraphvizVisualiser;
import org.apromore.canoniser.yawl.utils.NoOpMessageManager;
import org.apromore.canoniser.yawl.utils.NullOutputStream;
import org.apromore.canoniser.yawl.utils.TestUtils;
import org.apromore.cpf.CanonicalProcessType;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.yawlfoundation.yawlschema.DecompositionType;
import org.yawlfoundation.yawlschema.ExternalConditionFactsType;
import org.yawlfoundation.yawlschema.LayoutLocaleType;
import org.yawlfoundation.yawlschema.MetaDataType;
import org.yawlfoundation.yawlschema.NetFactsType;
import org.yawlfoundation.yawlschema.NetFactsType.ProcessControlElements;
import org.yawlfoundation.yawlschema.OutputConditionFactsType;
import org.yawlfoundation.yawlschema.SpecificationSetFactsType;
import org.yawlfoundation.yawlschema.YAWLSpecificationFactsType;

/**
 * Unit Tests trying the conversion from YAWL to canonical format.
 *
 * @author Felix Mannhardt
 *
 */
public class YAWL2CanonicalBasicUnitTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(YAWL2CanonicalBasicUnitTest.class);

    private static File emptyNet;

    private YAWL2Canonical yawl2Canonical;

    @BeforeClass
    public static void setUpOnce() throws IOException {
        emptyNet = new File(TestUtils.TEST_RESOURCES_DIRECTORY + "YAWL/EmptyNet.yawl");
    }

    @Before
    public void setUp() throws Exception {
        yawl2Canonical = new YAWL2CanonicalImpl(new NoOpMessageManager());
    }

    /**
     * Show the result of a canonisation, not really a test!
     *
     * @throws IOException
     * @throws SAXException
     */
    @Test
    public void testSaveResult() throws JAXBException, IOException, SAXException {
        final SpecificationSetFactsType yawlSpec = TestUtils.unmarshalYAWL(emptyNet);
        try {
            yawl2Canonical.convertToCanonical(yawlSpec);
        } catch (final CanoniserException e) {
            fail(e.getMessage());
        }

        OutputStream anfStream = null;
        OutputStream cpfStream = null;
        if (LOGGER.isDebugEnabled()) {
            anfStream = new FileOutputStream(TestUtils.createTestOutputFile(this.getClass(), "EmptyNet.yawl.anf"));
            cpfStream = new FileOutputStream(TestUtils.createTestOutputFile(this.getClass(), "EmptyNet.yawl.cpf"));
        } else {
            anfStream = new NullOutputStream();
            cpfStream = new NullOutputStream();
        }
        TestUtils.printAnf(yawl2Canonical.getAnf(), anfStream);
        TestUtils.printCpf(yawl2Canonical.getCpf(), cpfStream);

        if (LOGGER.isDebugEnabled()) {
            // Just build image if Graphviz exists
            if (new File(GraphvizVisualiser.DEFAULT_GRAPHVIZ_WINDOWS_PATH).exists()) {
                final GraphvizVisualiser v = new GraphvizVisualiser();
                v.createImageAsDOT(yawl2Canonical.getCpf().getNet().get(0), false,
                        new FileOutputStream(TestUtils.createTestOutputFile(this.getClass(), "emptyNetCpf.dot")));
                v.createImageAsPNG(yawl2Canonical.getCpf().getNet().get(0), true, TestUtils.createTestOutputFile(this.getClass(), "emptyNetCpf.png"));
            }
        }
    }

    @Test
    public void testConvertToCanonical() throws JAXBException, SAXException, IOException {
        final SpecificationSetFactsType yawlSpec = TestUtils.unmarshalYAWL(emptyNet);
        try {
            yawl2Canonical.convertToCanonical(yawlSpec);
        } catch (final CanoniserException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetAnf() throws JAXBException, CanoniserException, SAXException, IOException {
        final SpecificationSetFactsType yawlSpec = TestUtils.unmarshalYAWL(emptyNet);
        yawl2Canonical.convertToCanonical(yawlSpec);
        assertNotNull("Annotation Format should not be NULL", yawl2Canonical.getAnf());
    }

    @Test
    public void testGetCpf() throws JAXBException, CanoniserException, SAXException, IOException {
        final SpecificationSetFactsType yawlSpec = TestUtils.unmarshalYAWL(emptyNet);
        yawl2Canonical.convertToCanonical(yawlSpec);
        assertNotNull("Canonical Format should not be NULL", yawl2Canonical.getCpf());
    }

    @Test
    public void testConvertEmptyNet() throws CanoniserException, JAXBException, SAXException, IOException {
        final SpecificationSetFactsType yawlSpec = TestUtils.unmarshalYAWL(emptyNet);
        yawl2Canonical.convertToCanonical(yawlSpec);

        final YAWLSpecificationFactsType mainSpecification = yawlSpec.getSpecification().get(0);

        // Basic check CPF
        final CanonicalProcessType cpf = yawl2Canonical.getCpf();

        if (mainSpecification.getName() != null) {
            assertEquals(mainSpecification.getName(), cpf.getName());
        }
        assertEquals(countSubnets(mainSpecification), cpf.getNet().size());

        final NetFactsType rootNet = (NetFactsType) mainSpecification.getDecomposition().get(0);
        final ProcessControlElements processControlElements = rootNet.getProcessControlElements();

        assertEquals(0, processControlElements.getTaskOrCondition().size());

        final ExternalConditionFactsType inputCondition = processControlElements.getInputCondition();
        assertNotNull(inputCondition);

        final OutputConditionFactsType outputCondition = processControlElements.getOutputCondition();
        assertNotNull(outputCondition);
    }

    @Test
    public void testMetaData() throws CanoniserException, JAXBException, SAXException, IOException {
        final SpecificationSetFactsType yawlSpec = TestUtils.unmarshalYAWL(emptyNet);

        yawl2Canonical.convertToCanonical(yawlSpec);

        final CanonicalProcessType cpf = yawl2Canonical.getCpf();
        final AnnotationsType anf = yawl2Canonical.getAnf();
        final YAWLSpecificationFactsType mainSpecification = yawlSpec.getSpecification().get(0);

        assertEquals("EmptyNet Workfow", anf.getName()); // Original Name is NULL, but we use the Uri instead
        assertEquals(mainSpecification.getUri(), anf.getUri());

        if (mainSpecification.getName() != null) {
            assertEquals(mainSpecification.getName(), cpf.getName());
        }

        assertEquals(mainSpecification.getUri(), mainSpecification.getUri());
        assertEquals(mainSpecification.getMetaData().getVersion().toPlainString(), cpf.getVersion());

        // Just InputCondition, OutputCondition, the RootNet and the Specification, each 1 Graphic-Annotations (+ Metadata Extension)
        assertEquals(4, anf.getAnnotation().size());

        AnnotationType netAnnotation = null;
        AnnotationType inputAnnotation = null;
        AnnotationType outputAnnotation = null;
        AnnotationType specAnnotation = null;

        for (final AnnotationType ann : anf.getAnnotation()) {
            if ("N-EmptyNet".equals(ann.getCpfId())) {
                netAnnotation = ann;
            } else if ("C-InputCondition".equals(ann.getCpfId())) {
                inputAnnotation = ann;
            } else if ("C-OutputCondition".equals(ann.getCpfId())) {
                outputAnnotation = ann;
            } else {
                specAnnotation = ann;
            }
        }
        assertNotNull(netAnnotation);
        assertTrue(netAnnotation instanceof GraphicsType);
        assertNotNull(inputAnnotation);
        assertTrue(inputAnnotation instanceof GraphicsType);
        assertNotNull(outputAnnotation);
        assertTrue(outputAnnotation instanceof GraphicsType);
        assertNotNull(specAnnotation);
        assertEquals(2, specAnnotation.getAny().size());
        LayoutLocaleType locale = ExtensionUtils.getFromAnnotationsExtension(specAnnotation, ExtensionUtils.LOCALE, LayoutLocaleType.class, null);
        assertNotNull(locale);
        assertEquals("de", locale.getLanguage());
        assertEquals("DE", locale.getCountry());

        MetaDataType metaData = ExtensionUtils.getFromAnnotationsExtension(specAnnotation, ExtensionUtils.METADATA, MetaDataType.class, null);
        assertNotNull("Missing Metadata", metaData);
        assertEquals(new BigDecimal("0.1"), metaData.getVersion());
    }

    private int countSubnets(final YAWLSpecificationFactsType mainSpecification) {
        int subnetCount = 0;
        for (final DecompositionType d : mainSpecification.getDecomposition()) {
            if (d instanceof NetFactsType) {
                subnetCount++;
            }
        }
        return subnetCount;
    }

}
