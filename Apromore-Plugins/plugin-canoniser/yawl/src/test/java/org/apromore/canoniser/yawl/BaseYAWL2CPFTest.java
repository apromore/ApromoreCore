package org.apromore.canoniser.yawl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.YAWL2Canonical;
import org.apromore.canoniser.yawl.internal.impl.YAWL2CanonicalImpl;
import org.apromore.canoniser.yawl.utils.GraphvizVisualiser;
import org.apromore.canoniser.yawl.utils.TestUtils;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.HumanType;
import org.apromore.cpf.InputOutputType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ObjectRefType;
import org.apromore.cpf.ObjectType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.WorkType;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Base class for all Pattern based Unit tests
 *
 * @author <a href="felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public abstract class BaseYAWL2CPFTest {

    protected YAWL2Canonical yawl2Canonical;

    public BaseYAWL2CPFTest() {
        super();
    }

    protected abstract File getYAWLFile();

    protected File getYAWLOrgDataFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "YAWL/OrganisationalData/YAWLDefaultOrgData.ybkp");
    }

    @Before
    public void setUp() throws Exception {
        yawl2Canonical = new YAWL2CanonicalImpl();
        try {
            yawl2Canonical.convertToCanonical(TestUtils.unmarshalYAWL(getYAWLFile()), TestUtils.unmarshalYAWLOrgData(getYAWLOrgDataFile()));
        } catch (final CanoniserException e) {
            fail(e.getMessage());
        }
    }

    @Ignore
    @Test
    public void testSaveResult() throws JAXBException, IOException, SAXException {
        TestUtils.printAnf(yawl2Canonical.getAnf(),
                new FileOutputStream(TestUtils.createTestOutputFile(this.getClass(), getYAWLFile().getName() + ".anf")));
        TestUtils.printCpf(yawl2Canonical.getCpf(),
                new FileOutputStream(TestUtils.createTestOutputFile(this.getClass(), getYAWLFile().getName() + ".cpf")));

        final List<NetType> netList = yawl2Canonical.getCpf().getNet();
        for (final NetType net : netList) {
            createGraphImages(net);
        }
    }

    private void createGraphImages(final NetType net) throws FileNotFoundException, IOException {
        final GraphvizVisualiser v = new GraphvizVisualiser();
        v.createImageAsDOT(net,
                new FileOutputStream(TestUtils.createTestOutputFile(this.getClass(), getYAWLFile().getName() + "-" + net.getOriginalID() + ".dot")));
        try {
            v.createImageAsPNG(net, TestUtils.createTestOutputFile(this.getClass(), getYAWLFile().getName() + "-" + net.getOriginalID() + ".png"));
        } catch (final IOException e) {
            // Just build image if Graphviz exists
            System.out.println("WARN: " + e.getMessage());
        }
    }

    @Test
    public void testAllNodesReachable() {
        final NetType rootNet = yawl2Canonical.getCpf().getNet().get(0);
        final Set<String> sourceSet = new HashSet<String>();
        final Set<String> targetSet = new HashSet<String>();

        for (final NodeType node : rootNet.getNode()) {
            sourceSet.add(node.getId());
            targetSet.add(node.getId());
        }

        // Remove Node from Set if it is connected
        for (final EdgeType edge : rootNet.getEdge()) {
            assertNotNull(edge.getSourceId());
            sourceSet.remove(edge.getSourceId());
            assertNotNull(edge.getTargetId());
            targetSet.remove(edge.getTargetId());
        }

        assertTrue("Not all Nodes connected: " + printSet(sourceSet), sourceSet.size() == 1);
        assertTrue("Not all Nodes connected" + printSet(targetSet), targetSet.size() == 1);
    }

    @Test
    public void testBasicResourceType() {
        final Set<String> resourceId = new HashSet<String>();
        final Set<String> resourceName = new HashSet<String>();
        for (final ResourceTypeType resource : yawl2Canonical.getCpf().getResourceType()) {
            assertTrue("Duplicate ID in Resources " + resource.getId(), resourceId.add(resource.getId()));
            if (resource instanceof HumanType) {
                assertTrue("Duplicate Name in Resources " + resource.getName(), resourceName.add(resource.getName()));
            }
        }
    }

    private String printSet(final Set<String> sourceSet) {
        final StringBuilder sb = new StringBuilder();
        for (final String id : sourceSet) {
            sb.append(id + ", ");
        }
        return sb.toString();
    }

    protected NodeType getNodeByName(final NetType net, final String nodeName) {
        for (final NodeType node : net.getNode()) {
            if (node.getName() != null && node.getName().equals(nodeName)) {
                return node;
            }
        }
        return null;
    }

    protected ObjectType getObjectByName(final NetType net, final String objectName) {
        for (final ObjectType obj : net.getObject()) {
            if (obj.getName() != null && obj.getName().equals(objectName)) {
                return obj;
            }
        }
        return null;
    }

    protected List<ObjectRefType> getObjectOutputRef(final WorkType node, final ObjectType object) {
        return getObjectRef(node, object, InputOutputType.OUTPUT);
    }

    protected List<ObjectRefType> getObjectInputRef(final WorkType node, final ObjectType object) {
        return getObjectRef(node, object, InputOutputType.INPUT);
    }

    private List<ObjectRefType> getObjectRef(final WorkType node, final ObjectType object, final InputOutputType type) {
        final List<ObjectRefType> objectRefList = new ArrayList<ObjectRefType>();
        for (final ObjectRefType objRef : node.getObjectRef()) {
            if (objRef.getObjectId() != null && objRef.getObjectId().equals(object.getId())) {
                if (objRef.getType() == type) {
                    objectRefList.add(objRef);
                }
            }
        }
        return objectRefList;
    }

    protected NodeType getNodeByID(final NetType net, final String nodeId) {
        for (final NodeType node : net.getNode()) {
            if (node.getId() != null && node.getId().equals(nodeId)) {
                return node;
            }
        }
        return null;
    }

    protected int countOutgoingEdges(final NetType net, final String nodeId) {
        return getOutgoingEdges(net, nodeId).size();
    }

    protected int countIncomingEdges(final NetType net, final String nodeId) {
        return getIncomingEdges(net, nodeId).size();
    }

    protected List<EdgeType> getOutgoingEdges(final NetType net, final String id) {
        final List<EdgeType> edgeList = new ArrayList<EdgeType>();
        for (final EdgeType edge : net.getEdge()) {
            if (edge.getSourceId().equals(id)) {
                edgeList.add(edge);
            }
        }
        return edgeList;
    }

    protected List<EdgeType> getIncomingEdges(final NetType net, final String id) {
        final List<EdgeType> edgeList = new ArrayList<EdgeType>();
        for (final EdgeType edge : net.getEdge()) {
            if (edge.getTargetId().equals(id)) {
                edgeList.add(edge);
            }
        }
        return edgeList;
    }

    protected NodeType checkNode(final NetType net, final String id, final Class<?> classz, final int inEdges, final int outEdges) {
        final NodeType node = getNodeByName(net, id);
        return checkNode(net, node, classz, inEdges, outEdges);
    }

    protected NodeType checkNode(final NetType net, final NodeType node, final Class<?> classz, final int inEdges, final int outEdges) {
        assertTrue("Node " + node.getName() + " is not of class " + classz.getSimpleName(), classz.isInstance(node));
        assertEquals("Wrong count of outgoing edges at " + node.getName(), outEdges, countOutgoingEdges(net, node.getId()));
        assertEquals("Wrong count of incoming edges at " + node.getName(), inEdges, countIncomingEdges(net, node.getId()));
        return node;
    }

}