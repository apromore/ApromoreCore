package org.apromore.canoniser.yawl.internal.impl.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.util.Collection;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.utils.ExtensionUtils;
import org.apromore.canoniser.yawl.utils.NoOpMessageManager;
import org.apromore.canoniser.yawl.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yawlfoundation.yawlschema.ExternalNetElementType;
import org.yawlfoundation.yawlschema.MetaDataType;
import org.yawlfoundation.yawlschema.NetFactsType;
import org.yawlfoundation.yawlschema.NetFactsType.ProcessControlElements;
import org.yawlfoundation.yawlschema.SpecificationSetFactsType;
import org.yawlfoundation.yawlschema.YAWLSpecificationFactsType;
import org.yawlfoundation.yawlschema.orgdata.OrgDataType;

public class YAWLConversionContextUnitTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(YAWLConversionContextUnitTest.class);

    private YAWLConversionContext context;
    private YAWLSpecificationFactsType specUnderTest;

    @Before
    public void setUp() throws Exception {
        final File file = new File(TestUtils.TEST_RESOURCES_DIRECTORY + "YAWL/Patterns/ControlFlow/WPC3Synchronization.yawl");
        final File orgDatafile = new File(TestUtils.TEST_RESOURCES_DIRECTORY + "YAWL/OrganisationalData/YAWLDefaultOrgData.ybkp");
        final SpecificationSetFactsType yawlXml = TestUtils.unmarshalYAWL(file);
        final OrgDataType orgDataXml = TestUtils.unmarshalYAWLOrgData(orgDatafile);
        specUnderTest = yawlXml.getSpecification().get(0);
        context = new YAWLConversionContext(specUnderTest, yawlXml.getLayout(), orgDataXml, new NoOpMessageManager());
    }

    @Test
    public void testAddAnnotations() throws CanoniserException {
        context.addToAnnotations(ExtensionUtils.marshalYAWLFragment("test", new MetaDataType(), MetaDataType.class));
    }

    @Test
    public void testGetLayoutForNet() throws CanoniserException {
        assertNotNull(context.getLayoutForNet("Net"));
    }

    @Test
    public void testGetLayoutLocale() {
        assertNotNull(context.getLayoutLocale());
    }

    @Test
    public void testGetNumberFormat() {
        assertNotNull(context.getNumberFormat());
    }

    @Test
    public void testGetSpecificationLayout() {
        assertNotNull(context.getSpecificationLayout());
    }

    @Test
    public void testGetLayoutVertexForElement() {
        assertNotNull(context.getLayoutVertexForElement("A"));
        assertNotNull(context.getLayoutVertexForElement("B"));
        assertNotNull(context.getLayoutVertexForElement("InputCondition"));
        assertNotNull(context.getLayoutVertexForElement("OutputCondition"));
    }

    @Test
    public void testGetLayoutLabelForElement() {
        assertNotNull(context.getLayoutLabelForElement("A"));
        assertNotNull(context.getLayoutLabelForElement("B"));
    }

    @Test
    public void testGetLayoutDecoratorForElement() {
        // Those elements have to SPLIT or JOIN decorator
        assertNull(context.getLayoutDecoratorForElement("B"));
        assertNull(context.getLayoutDecoratorForElement("C"));
        assertNull(context.getLayoutDecoratorForElement("D"));
        // Those elements should have a decorator
        assertNotNull(context.getLayoutDecoratorForElement("A"));
        assertNotNull(context.getLayoutDecoratorForElement("E"));
        // Just a single decorator
        assertEquals(1, context.getLayoutDecoratorForElement("A").size());
        assertEquals(1, context.getLayoutDecoratorForElement("E").size());
    }

    @Test
    public void testGetSuccessors() {
        final NetFactsType net = (NetFactsType) specUnderTest.getDecomposition().get(0);
        final ProcessControlElements pcElements = net.getProcessControlElements();
        LOGGER.debug("Successors Map:");
        checkSuccessors(pcElements.getInputCondition(), context.getPostSet(pcElements.getInputCondition()));
        for (final ExternalNetElementType element : pcElements.getTaskOrCondition()) {
            checkSuccessors(element, context.getPostSet(element));
        }
        checkSuccessors(pcElements.getOutputCondition(), context.getPostSet(pcElements.getOutputCondition()));
    }

    private void checkSuccessors(final ExternalNetElementType element, final Collection<ExternalNetElementType> successors) {
        printList(element, successors);

        if (element.getId().equals("InputCondition")) {
            assertEquals(1, successors.size());
        }

        if (element.getId().equals("A")) {
            assertEquals(3, successors.size());
        }

        if (element.getId().equals("B")) {
            assertEquals(1, successors.size());
            assertEquals("E", successors.iterator().next().getId());
        }

        if (element.getId().equals("C")) {
            assertEquals(1, successors.size());
            assertEquals("E", successors.iterator().next().getId());
        }

        if (element.getId().equals("D")) {
            assertEquals(1, successors.size());
            assertEquals("E", successors.iterator().next().getId());
        }

        if (element.getId().equals("E")) {
            assertEquals(1, successors.size());
            assertEquals("OutputCondition", successors.iterator().next().getId());
        }

        if (element.getId().equals("OutputCondition")) {
            assertEquals(0, successors.size());
        }

    }

    @Test
    public void testGetPredecessors() {
        final NetFactsType net = (NetFactsType) specUnderTest.getDecomposition().get(0);
        final ProcessControlElements pcElements = net.getProcessControlElements();
        LOGGER.debug("Predecessors Map:");
        checkPredecessors(pcElements.getInputCondition(), context.getPreSet(pcElements.getInputCondition()));
        for (final ExternalNetElementType element : pcElements.getTaskOrCondition()) {
            checkPredecessors(element, context.getPreSet(element));
        }
        checkPredecessors(pcElements.getOutputCondition(), context.getPreSet(pcElements.getOutputCondition()));
    }

    private void checkPredecessors(final ExternalNetElementType element, final Collection<ExternalNetElementType> predecessors) {
        printList(element, predecessors);

        if (element.getId().equals("InputCondition")) {
            assertEquals(0, predecessors.size());
        }

        if (element.getId().equals("A")) {
            assertEquals(1, predecessors.size());
            assertEquals("InputCondition", predecessors.iterator().next().getId());
        }

        if (element.getId().equals("B")) {
            assertEquals(1, predecessors.size());
            assertEquals("A", predecessors.iterator().next().getId());
        }

        if (element.getId().equals("C")) {
            assertEquals(1, predecessors.size());
            assertEquals("A", predecessors.iterator().next().getId());
        }

        if (element.getId().equals("D")) {
            assertEquals(1, predecessors.size());
            assertEquals("A", predecessors.iterator().next().getId());
        }

        if (element.getId().equals("E")) {
            assertEquals(3, predecessors.size());
        }

        if (element.getId().equals("OutputCondition")) {
            assertEquals(1, predecessors.size());
            assertEquals("E", predecessors.iterator().next().getId());
        }
    }

    private void printList(final ExternalNetElementType element, final Collection<ExternalNetElementType> list) {
        if (LOGGER.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder();
            sb.append(element.getId() + ": [");
            for (final ExternalNetElementType succElement : list) {
                sb.append(succElement.getId());
                sb.append(", ");
            }
            sb.append("]\n");
            LOGGER.debug(sb.toString());
        }
    }
}
