/*
 * Copyright © 2009-2016 The Apromore Initiative.
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

package org.apromore.canoniser.pnml;

import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.pnml.internal.PNML2Canonical;
import org.apromore.canoniser.pnml.internal.pnml2canonical.NamespaceFilter;
import org.apromore.cpf.ANDJoinType;
import org.apromore.cpf.ANDSplitType;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.CpfObjectFactory;
import org.apromore.cpf.CPFSchema;
import org.apromore.cpf.CPFValidator;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.EventType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ObjectFactory;
import org.apromore.cpf.StateType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.WorkType;
import org.apromore.pnml.PnmlType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import static java.util.Collections.emptyList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.sax.SAXSource;

import static org.junit.Assert.assertTrue;

public class PNML2CanonicalUnitTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(PNML2CanonicalUnitTest.class.getName());

    /**
     * Test that PNML transitions with multiple incoming arcs canonize with a CPF AndJoin routing element.
     */
    @Test
    public void testJoin() throws Exception {

        CanonicalProcessType cpf = canonise("Join", false, false);
        CanonicalProcessType expected = unmarshal("Join");

        assertCPFMatch(expected, cpf);

        // Expect a task with 1 incoming and 1 outgoing edge
        NodeType t1 = findNodeByNameAndClass(cpf, "t1", TaskType.class);
        assertNotNull(t1);
        assertEquals(1, findEdgesByNode(cpf, t1.getId(), true, false).size());  // incoming edges
        assertEquals(1, findEdgesByNode(cpf, t1.getId(), false, true).size());  // outgoing edges

        // Expect a join with 2 incoming and 1 outgoing edges
        NodeType t1join = findNodeByNameAndClass(cpf, "t1", ANDJoinType.class);
        assertNotNull(t1join);
        assertEquals(2, findEdgesByNode(cpf, t1join.getId(), true, false).size());  // incoming edges
        assertEquals(1, findEdgesByNode(cpf, t1join.getId(), false, true).size());  // outgoing edges
    }

    /**
     * Test that PNML transitions with multiple outgoing arcs canonize with a CPF AndSplit routing element.
     */
    @Test
    public void testSplit() throws Exception {

        CanonicalProcessType cpf = canonise("Split", false, false);
        CanonicalProcessType expected = unmarshal("Split");

        assertCPFMatch(expected, cpf);

        // Expect a task with 1 incoming and 1 outgoing edge
        NodeType t1 = findNodeByNameAndClass(cpf, "t1", TaskType.class);
        assertNotNull(t1);
        assertEquals(1, findEdgesByNode(cpf, t1.getId(), true, false).size());  // incoming edges
        assertEquals(1, findEdgesByNode(cpf, t1.getId(), false, true).size());  // outgoing edges

        // Expect a split with 1 incoming and 2 outgoing edges
        NodeType t1split = findNodeByNameAndClass(cpf, "t1", ANDSplitType.class);
        assertNotNull(t1split);
        assertEquals(1, findEdgesByNode(cpf, t1split.getId(), true, false).size());  // incoming edges
        assertEquals(2, findEdgesByNode(cpf, t1split.getId(), false, true).size());  // outgoing edges
    }

    /**
     * Test that PNML places with multiple outgoing arc canonize with a CPF State routing element.
     *
     * (Perhaps surprisingly, not an XorSplit.)
     */
    @Test
    public void testSplit2() throws Exception {

        CanonicalProcessType cpf = canonise("Split2", false, false);
        CanonicalProcessType expected = unmarshal("Split2");

        assertCPFMatch(expected, cpf);

        NodeType a  = findNodeByNameAndClass(cpf, "A",  EventType.class);
        NodeType aS = findNodeByNameAndClass(cpf, "A",  StateType.class);
        NodeType b1 = findNodeByNameAndClass(cpf, "B1", EventType.class);
        NodeType b2 = findNodeByNameAndClass(cpf, "B2", EventType.class);
        NodeType t1 = findNodeByNameAndClass(cpf, "t1", TaskType.class);
        NodeType t2 = findNodeByNameAndClass(cpf, "t2", TaskType.class);

        assertNotNull(a);
        assertNotNull(aS);
        assertNotNull(b1);
        assertNotNull(b2);
        assertNotNull(t1);
        assertNotNull(t2);

        assertNotNull(findEdge(cpf, a, aS));
        assertNotNull(findEdge(cpf, aS, t1));
        assertNotNull(findEdge(cpf, aS, t2));
        assertNotNull(findEdge(cpf, t1, b1));
        assertNotNull(findEdge(cpf, t2, b2));
    }

    /**
     * Test that PNML transitions with multiple incoming and outgoing arcs canonize with both
     * CPF AndJoin and AndSplit routing elements.
     */
    @Test
    public void testJoinSplit() throws Exception {

        CanonicalProcessType cpf = canonise("JoinSplit", false, false);
        CanonicalProcessType expected = unmarshal("JoinSplit");

        assertCPFMatch(expected, cpf);

        // Expect a task with 1 incoming and 1 outgoing edge
        NodeType t1 = findNodeByNameAndClass(cpf, "t1", TaskType.class);
        assertNotNull(t1);
        assertEquals(1, findEdgesByNode(cpf, t1.getId(), true, false).size());  // incoming edges
        assertEquals(1, findEdgesByNode(cpf, t1.getId(), false, true).size());  // outgoing edges

        // Expect a join with 2 incoming and 1 outgoing edges
        NodeType t1join = findNodeByNameAndClass(cpf, "t1", ANDJoinType.class);
        assertNotNull(t1join);
        assertEquals(2, findEdgesByNode(cpf, t1join.getId(), true, false).size());  // incoming edges
        assertEquals(1, findEdgesByNode(cpf, t1join.getId(), false, true).size());  // outgoing edges

        // Expect a split with 1 incoming and 2 outgoing edges
        NodeType t1split = findNodeByNameAndClass(cpf, "t1", ANDSplitType.class);
        assertNotNull(t1split);
        assertEquals(1, findEdgesByNode(cpf, t1split.getId(), true, false).size());  // incoming edges
        assertEquals(2, findEdgesByNode(cpf, t1split.getId(), false, true).size());  // outgoing edges
    }

    /**
     * Test that a simple PNML branch is canonized into a CPF split followed by a join.
     */
    @Test
    public void testSplitJoin() throws Exception {

        CanonicalProcessType actual   = canonise("SplitJoin", false, false);
        CanonicalProcessType expected = unmarshal("SplitJoin");

        assertCPFMatch(expected, actual);
    }

    /**
     * Cursory check for significant differences between two CPF models.
     *
     * This is not a terribly rigorous comparison.  Only nodes with names are compared.  Membership to different nets are ignored.
     *
     * @param expected
     * @param actual
     * @throws AssertionError if any difference is detected between the <var>expected</var> and <var>actual</var> process models
     */
    private void assertCPFMatch(CanonicalProcessType expected, CanonicalProcessType actual) {
        assertCPFContains(expected, actual);
        assertCPFContains(actual, expected);
    }

    /**
     * @param lhs  container
     * @param rhs  contained
     * @throws AssertionError if the <var>lhs</var> has a named node or edge that isn't in the <var>rhs</var>
     */
    private void assertCPFContains(CanonicalProcessType lhs, CanonicalProcessType rhs) {

        // Populate a map between nodes of the LHS and RHS process models
        Map<String, NodeType> map = new HashMap<>();
        String mapString = "";
        for (NetType rhsNet: rhs.getNet()) {
            for (NodeType rhsNode: rhsNet.getNode()) {
                for (NetType lhsNet: lhs.getNet()) {
                    for (NodeType lhsNode: lhsNet.getNode()) {
                        if (lhsNode.getName() != null && lhsNode.getName().equals(rhsNode.getName()) && lhsNode.getClass().equals(rhsNode.getClass())) {
                            map.put(lhsNode.getId(), rhsNode);
                            mapString += ("\n" + lhsNode.getId() + " -> " + rhsNode.getId() + "\t" + lhsNode.getName());
                        }
                    }
                }
            }
        }

        for (NetType net: lhs.getNet()) {
            for (NodeType node: net.getNode()) {
                if (node.getName() != null) {
                    NodeType node2 = findNodeByNameAndClass(rhs, node.getName(), node.getClass());
                    assertNotNull("Unable to find node " + node.getId() + " (" + node.getName() + ") class: " + node.getClass(), node2);
                    map.put(node.getName(), node2);
                }
            }

            for (EdgeType edge: net.getEdge()) {
                NodeType source = map.get(edge.getSourceId());
                NodeType target = map.get(edge.getTargetId());
                assertNotNull(source);
                assertNotNull(target);
                assertNotNull("Edge " + edge.getId() + " from " + edge.getSourceId() + " = " + source.getId() + " (" + source.getName() + ") to " + edge.getTargetId() + " = " + target.getId() + " (" + target.getName() + ") missing: " + mapString, findEdge(rhs, source, target));
            }
        }
    }

    /**
     * Test that PNML reset arcs are canonized into CPF cancellation sets.
     */
    @Test
    public void testReset1() throws Exception {

        CanonicalProcessType cpf = canonise("Reset1", false, false);

        // Inspect the test result
        WorkType cancellingNode = null;
        for (NetType net: cpf.getNet()) {
            for (NodeType node: net.getNode()) {
                if (node instanceof WorkType && "t2".equals(node.getName())) {
                    cancellingNode = (WorkType) node;
                }
            }
        }
        assertNotNull(cancellingNode);
        assertEquals(3, cancellingNode.getCancelNodeId().size());
        assertEquals(2, cancellingNode.getCancelEdgeId().size());
    }

    /**
     * Test that PNML reset arcs are canonized into CPF cancellation sets.
     *
     * The added complication compared to {@link testReset1} is additional routing nodes around task <code>t2</code>.
     */
    @Test
    public void testReset2() throws Exception {

        CanonicalProcessType cpf = canonise("Reset2", false, false);

        // Inspect the test result
        WorkType cancellingNode = null;
        for (NetType net: cpf.getNet()) {
            for (NodeType node: net.getNode()) {
                if (node instanceof WorkType && "t2".equals(node.getName())) {
                    cancellingNode = (WorkType) node;
                }
            }
        }
        assertNotNull(cancellingNode);
        assertEquals(3, cancellingNode.getCancelNodeId().size());
        assertEquals(2, cancellingNode.getCancelEdgeId().size());
    }

    // Internal methods

    private CanonicalProcessType canonise(final String  fileName,
                                          final boolean isCpfTaskPnmlTransition,
                                          final boolean isCpfEdgePnmlPlace) throws Exception {

        // Parse <filename>.pnml into a test instance of PNML2Canonical
        JAXBElement<PnmlType> rootElement = (JAXBElement<PnmlType>) 
            JAXBContext.newInstance("org.apromore.pnml")
                       .createUnmarshaller()
                       .unmarshal(new FileInputStream("src/test/resources/PNML_testcases/" + fileName + ".pnml"));
        CanonicalProcessType cpf = new PNML2Canonical(rootElement.getValue(), null, isCpfTaskPnmlTransition, isCpfEdgePnmlPlace).getCPF();
        assert cpf != null;

        // Serialize <fileName>.cpf out from the test instance
        Marshaller m = JAXBContext.newInstance(CpfObjectFactory.class).createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        m.marshal(CpfObjectFactory.getInstance().createCanonicalProcess(cpf), new File("target/" + fileName + ".cpf"));

        assertEquals(emptyList(), (new CPFValidator(cpf)).validate());

        return cpf;
    }

    private CanonicalProcessType unmarshal(final String fileName) throws FileNotFoundException, JAXBException, SAXException {
        return CPFSchema.unmarshalCanonicalFormat(new FileInputStream("src/test/resources/PNML_testcases/" + fileName + ".cpf"), false).getValue();
    }

    private NodeType findNodeByNameAndClass(final CanonicalProcessType cpf, final String name, final Class klass) {
        for (NetType net: cpf.getNet()) {
            for (NodeType node: net.getNode()) {
                if (klass.isInstance(node) && (name == null ? node.getName() == null : node.getName().equals(name))) {
                    return node;
                }
            }
        }
        return null;
    }

    /**
     * Find the edge between two nodes.
     *
     * @param source
     * @param target
     * @return the unique edge between <var>source</var> and <var>target</var>, or <code>null</code> if there is no such edge
     * @throws AssertionError if there are multiple edges between <var>source</var> and <var>target</var>
     */
    private EdgeType findEdge(final CanonicalProcessType cpf, final NodeType source, final NodeType target) {
        EdgeType result = null;
        for (NetType net: cpf.getNet()) {
            for (EdgeType edge: net.getEdge()) {
                if (edge.getSourceId().equals(source.getId()) && edge.getTargetId().equals(target.getId())) {
                    if (result == null) {
                        result = edge;
                    } else {
                        throw new AssertionError("Multiple edges from \"" + source.getId() + "\" to \"" + target.getId() + "\"");
                    }
                }
            }
        }

        return result;
    }

    private EdgeType findEdge(final CanonicalProcessType cpf, final String sourceId, final String targetId) {
        EdgeType result = null;
        for (NetType net: cpf.getNet()) {
            for (EdgeType edge: net.getEdge()) {
                if (edge.getSourceId().equals(sourceId) && edge.getTargetId().equals(targetId)) {
                    if (result == null) {
                        result = edge;
                    } else {
                        throw new AssertionError("Multiple edges from \"" + sourceId + "\" to \"" + targetId + "\"");
                    }
                }
            }
        }

        return result;
    }

    private Set<EdgeType> findEdgesByNode(final CanonicalProcessType cpf, final String cpfId, final boolean incoming, final boolean outgoing) {
        Set<EdgeType> set = new HashSet<>();
        for (NetType net: cpf.getNet()) {
            for (EdgeType edge: net.getEdge()) {
                if ((incoming && edge.getTargetId().equals(cpfId)) ||
                    (outgoing && edge.getSourceId().equals(cpfId))) {
                    set.add(edge);
                }
            }
        }
        return set;
    }


    // Older tests -- these have been salvaged to run, but don't actually test anything other than parsing

    @Test
    public void testWoped() {
        File foldersave = new File("target");
        assert foldersave.isDirectory();
        File folder = new File("src/test/resources/PNML_testcases/woped_cases_expected_pnml");
        assert folder.isDirectory();
        FileFilter fileFilter = new FileFilter() {
            public boolean accept(File file) {
                return file.isFile();
            }
        };

        int n = 0;
        File[] folderContent = folder.listFiles(fileFilter);
        for (int i = 0; i < folderContent.length; i++) {
            File file = folderContent[i];
            String filename = file.getName();
            StringTokenizer tokenizer = new StringTokenizer(filename, ".");
            String filename_without_path = tokenizer.nextToken();
            String extension = filename.split("\\.")[filename.split("\\.").length - 1];

            if (extension.compareTo("pnml") == 0) {
                //LOGGER.debug("Analysing " + filename);
                n++;
                
                // @ignore the following failing tests
                if (java.util.Arrays.asList("03_Example.pnml",
                                            "04_Example-Workflow.pnml",
                                                // XOR-split (t0_op_1, t0_op_2) and -join transitions (t6_op_1, t6_op_2) are duplicated
                                            "06_LoanApplication.pnml",
                                            "07_LoanApplicationResources.pnml",
                                                // MessageType event (id 37) created for node named "wait for reply"
                                            "15_XorSplitJoin.pnml"
                                                // Multiple transitions named "t1" confuse the test matcher
                ).contains(filename)) {
                    LOGGER.info("Bypassing " + filename);
                    continue;
                }

                try {
                    JAXBContext jc = JAXBContext.newInstance("org.apromore.pnml");
                    Unmarshaller u = jc.createUnmarshaller();
                    XMLReader reader = XMLReaderFactory.createXMLReader();

                    // Create the filter (to add namespace) and set the xmlReader as its parent.
                    NamespaceFilter inFilter = new NamespaceFilter("pnml.apromore.org", true);
                    inFilter.setParent(reader);

                    // Prepare the input, in this case a java.io.File (output)
                    InputSource is = new InputSource(new FileInputStream(file));

                    // Create a SAXSource specifying the filter
                    SAXSource source = new SAXSource(inFilter, is);
                    JAXBElement<PnmlType> rootElement = (JAXBElement<PnmlType>) u.unmarshal(source);
                    PnmlType pnml = rootElement.getValue();

                    PNML2Canonical pn = new PNML2Canonical(pnml, filename_without_path, false, false);

                    // Compare to the expected value
                    CanonicalProcessType actual = pn.getCPF();
                    assertNotNull(actual);
                    CanonicalProcessType expected = unmarshal("woped_cases_expected_cpf/" + filename_without_path);
                    assertNotNull(expected);
                    assertCPFMatch(expected, actual);

                    // Dump the canonized CPF and ANF

                    jc = JAXBContext.newInstance(CpfObjectFactory.class);
                    Marshaller m = jc.createMarshaller();
                    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                    JAXBElement<CanonicalProcessType> cprocRootElem = CpfObjectFactory.getInstance().createCanonicalProcess(pn.getCPF());
                    m.marshal(cprocRootElem, new File(foldersave, filename_without_path + ".cpf"));

                    jc = JAXBContext.newInstance("org.apromore.anf");
                    m = jc.createMarshaller();
                    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                    JAXBElement<AnnotationsType> annsRootElem = new org.apromore.anf.ObjectFactory().createAnnotations(pn.getANF());
                    m.marshal(annsRootElem, new File(foldersave, filename_without_path + ".anf"));

                } catch (JAXBException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                //LOGGER.debug("Skipping " + filename);
            }
        }
        LOGGER.debug("Analysed " + n + " files.");
    }

}
