package org.apromore.canoniser.pnml;

import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.pnml.internal.PNML2Canonical;
import org.apromore.canoniser.pnml.internal.pnml2canonical.NamespaceFilter;
import org.apromore.cpf.ANDJoinType;
import org.apromore.cpf.ANDSplitType;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.CpfObjectFactory;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ObjectFactory;
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
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.util.HashSet;
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

        CanonicalProcessType cpf = canonise("Join");

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

        CanonicalProcessType cpf = canonise("Split");

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
     * Test that PNML transitions with multiple incoming and outgoing arcs canonize with both
     * CPF AndJoin and AndSplit routing elements.
     */
    @Test
    public void testJoinSplit() throws Exception {

        CanonicalProcessType cpf = canonise("JoinSplit");

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
     * Test that PNML reset arcs are canonized into CPF cancellation sets.
     */
    @Test
    public void testReset1() throws Exception {

        CanonicalProcessType cpf = canonise("Reset1");

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

        CanonicalProcessType cpf = canonise("Reset2");

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

    private CanonicalProcessType canonise(final String fileName) throws Exception {

        // Parse <filename>.pnml into a test instance of PNML2Canonical
        JAXBElement<PnmlType> rootElement = (JAXBElement<PnmlType>) 
            JAXBContext.newInstance("org.apromore.pnml")
                       .createUnmarshaller()
                       .unmarshal(new FileInputStream("src/test/resources/PNML_testcases/" + fileName + ".pnml"));
        CanonicalProcessType cpf = new PNML2Canonical(rootElement.getValue()).getCPF();
        assert cpf != null;

        // Serialize <fileName>.cpf out from the test instance
        Marshaller m = JAXBContext.newInstance(/*"org.apromore.cpf"*/ CpfObjectFactory.class).createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        m.marshal(CpfObjectFactory.getInstance().createCanonicalProcess(cpf), new File("target/" + fileName + ".cpf"));

        return cpf;
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
                LOGGER.debug("Analysing " + filename);
                n++;
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

                    PNML2Canonical pn = new PNML2Canonical(pnml, filename_without_path);

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
                LOGGER.debug("Skipping " + filename);
            }
        }
        LOGGER.debug("Analysed " + n + " files.");
    }

}
