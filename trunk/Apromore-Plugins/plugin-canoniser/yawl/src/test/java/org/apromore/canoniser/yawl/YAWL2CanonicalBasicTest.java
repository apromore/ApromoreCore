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
package org.apromore.canoniser.yawl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.YAWL2Canonical;
import org.apromore.canoniser.yawl.internal.impl.YAWL2CanonicalImpl;
import org.apromore.canoniser.yawl.utils.GraphvizVisualiser;
import org.apromore.canoniser.yawl.utils.TestUtils;
import org.apromore.cpf.CanonicalProcessType;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;
import org.yawlfoundation.yawlschema.DecompositionType;
import org.yawlfoundation.yawlschema.ExternalConditionFactsType;
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
public class YAWL2CanonicalBasicTest {

    private static File emptyNet;

    private YAWL2Canonical yawl2Canonical;

    @BeforeClass
    public static void setUpOnce() throws IOException {
        emptyNet = new File(TestUtils.TEST_RESOURCES_DIRECTORY + "YAWL/EmptyNet.yawl");
    }

    @Before
    public void setUp() throws Exception {
        yawl2Canonical = new YAWL2CanonicalImpl();
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

        TestUtils.printAnf(yawl2Canonical.getAnf(), new FileOutputStream(TestUtils.createTestOutputFile(this.getClass(), "EmptyNet.yawl.anf")));
        TestUtils.printCpf(yawl2Canonical.getCpf(), new FileOutputStream(TestUtils.createTestOutputFile(this.getClass(), "EmptyNet.yawl.cpf")));

        // Just build image if Graphviz exists
        if (new File(GraphvizVisualiser.DEFAULT_GRAPHVIZ_WINDOWS_PATH).exists()) {
            final GraphvizVisualiser v = new GraphvizVisualiser();
            v.createImageAsDOT(yawl2Canonical.getCpf().getNet().get(0),
                    new FileOutputStream(TestUtils.createTestOutputFile(this.getClass(), "emptyNetCpf.dot")));
            v.createImageAsPNG(yawl2Canonical.getCpf().getNet().get(0), TestUtils.createTestOutputFile(this.getClass(), "emptyNetCpf.png"));
        }
    }

    @Test
    public void testConvertToCanonical() throws JAXBException, FileNotFoundException, SAXException {
        final SpecificationSetFactsType yawlSpec = TestUtils.unmarshalYAWL(emptyNet);
        try {
            yawl2Canonical.convertToCanonical(yawlSpec);
        } catch (final CanoniserException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetAnf() throws JAXBException, CanoniserException, FileNotFoundException, SAXException {
        final SpecificationSetFactsType yawlSpec = TestUtils.unmarshalYAWL(emptyNet);
        yawl2Canonical.convertToCanonical(yawlSpec);
        assertNotNull("Annotation Format should not be NULL", yawl2Canonical.getAnf());
    }

    @Test
    public void testGetCpf() throws JAXBException, CanoniserException, FileNotFoundException, SAXException {
        final SpecificationSetFactsType yawlSpec = TestUtils.unmarshalYAWL(emptyNet);
        yawl2Canonical.convertToCanonical(yawlSpec);
        assertNotNull("Canonical Format should not be NULL", yawl2Canonical.getCpf());
    }

    @Test
    public void testConvertEmptyNet() throws CanoniserException, JAXBException, FileNotFoundException, SAXException {
        final SpecificationSetFactsType yawlSpec = TestUtils.unmarshalYAWL(emptyNet);
        yawl2Canonical.convertToCanonical(yawlSpec);

        // Basic check ANF
        final AnnotationsType anf = yawl2Canonical.getAnf();
        final YAWLSpecificationFactsType mainSpecification = yawlSpec.getSpecification().get(0);
        assertEquals("EmptyNet", anf.getName()); // Original Name is NULL, but we use the Uri instead
        assertEquals(mainSpecification.getUri(), anf.getUri());

        // Just InputCondition and OutputCondition and the RootNet, each 1 Graphic-Annotations
        assertEquals(3, anf.getAnnotation().size());

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
    public void testConvertMetadata() throws JAXBException, CanoniserException, FileNotFoundException, SAXException {
        final SpecificationSetFactsType yawlSpec = TestUtils.unmarshalYAWL(emptyNet);
        yawl2Canonical.convertToCanonical(yawlSpec);
        final CanonicalProcessType cpf = yawl2Canonical.getCpf();
        final YAWLSpecificationFactsType specFacts = yawlSpec.getSpecification().get(0);
        if (specFacts.getName() != null) {
            assertEquals(specFacts.getName(), cpf.getName());
        }
        assertEquals(specFacts.getUri(), cpf.getUri());
        assertEquals(specFacts.getMetaData().getVersion().toPlainString(), cpf.getVersion());
        // TODO check other metadata
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
