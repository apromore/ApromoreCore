package org.apromore.canoniser.yawl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.YAWL2Canonical;
import org.apromore.canoniser.yawl.internal.impl.YAWL2CanonicalImpl;
import org.apromore.canoniser.yawl.utils.GraphvizVisualiser;
import org.apromore.canoniser.yawl.utils.NoOpMessageManager;
import org.apromore.canoniser.yawl.utils.NullOutputStream;
import org.apromore.canoniser.yawl.utils.TestUtils;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.HumanType;
import org.apromore.cpf.InputOutputType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ObjectRefType;
import org.apromore.cpf.ObjectType;
import org.apromore.cpf.ResourceTypeRefType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.TypeAttribute;
import org.apromore.cpf.WorkType;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.yawlfoundation.yawlschema.orgdata.OrgDataType;

/**
 * Base class for all Pattern based Unit tests
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public abstract class BaseYAWL2CPFUnitTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseYAWL2CPFUnitTest.class);

    protected YAWL2Canonical yawl2Canonical;

    public BaseYAWL2CPFUnitTest() {
        super();
    }

    protected abstract File getYAWLFile();

    protected File getYAWLOrgDataFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "YAWL/OrganisationalData/YAWLDefaultOrgData.ybkp");
    }

    @Before
    public void setUp() throws Exception {
        yawl2Canonical = new YAWL2CanonicalImpl(new NoOpMessageManager());
        try {
            if (getYAWLOrgDataFile() != null) {
                OrgDataType orgData = TestUtils.unmarshalYAWLOrgData(getYAWLOrgDataFile());
                yawl2Canonical.convertToCanonical(TestUtils.unmarshalYAWL(getYAWLFile()), orgData);
            } else {
                yawl2Canonical.convertToCanonical(TestUtils.unmarshalYAWL(getYAWLFile()));
            }
        } catch (final CanoniserException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testSaveResult() throws JAXBException, IOException, SAXException {
        OutputStream anfStream = null;
        OutputStream cpfStream = null;
        if (LOGGER.isDebugEnabled()) {
            anfStream = new FileOutputStream(TestUtils.createTestOutputFile(this.getClass(), getYAWLFile().getName() + ".anf"));
            cpfStream = new FileOutputStream(TestUtils.createTestOutputFile(this.getClass(), getYAWLFile().getName() + ".cpf"));
        } else {
            anfStream = new NullOutputStream();
            cpfStream = new NullOutputStream();
        }
        TestUtils.printAnf(yawl2Canonical.getAnf(), anfStream);
        TestUtils.printCpf(yawl2Canonical.getCpf(), cpfStream);
        if (LOGGER.isDebugEnabled()) {
            final List<NetType> netList = yawl2Canonical.getCpf().getNet();
            for (final NetType net : netList) {
                createGraphImages(net);
            }
        }
    }

    private void createGraphImages(final NetType net) throws FileNotFoundException, IOException {
        final GraphvizVisualiser v = new GraphvizVisualiser();
        v.createImageAsDOT(net, false,
                new FileOutputStream(TestUtils.createTestOutputFile(this.getClass(), getYAWLFile().getName() + "-" + net.getOriginalID() + ".dot")));
        try {
            v.createImageAsPNG(net, true, TestUtils.createTestOutputFile(this.getClass(), getYAWLFile().getName() + "-" + net.getOriginalID() + ".png"));
        } catch (final IOException e) {
            // Just build image if Graphviz exists
            LOGGER.debug("WARN: " + e.getMessage());
        }
    }

    @Test
    public void testBasics() {
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
            if (nodeName.equals(node.getName())) {
                return node;
            }
        }
        return null;
    }

    protected NodeType getNodeById(final NetType net, final String nodeId) {
        for (final NodeType node : net.getNode()) {
            if (nodeId.equals(node.getId())) {
                return node;
            }
        }
        return null;
    }

    protected ObjectType getObjectByName(final NetType net, final String objectName) {
        for (final ObjectType obj : net.getObject()) {
            if (objectName.equals(obj.getName())) {
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
            if (object.getId().equals(objRef.getObjectId())) {
                if (objRef.getType() == type) {
                    objectRefList.add(objRef);
                }
            }
        }
        return objectRefList;
    }

    protected NodeType getNodeByID(final NetType net, final String nodeId) {
        for (final NodeType node : net.getNode()) {
            if (nodeId.equals(node.getId())) {
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
            if (id.equals(edge.getSourceId())) {
                edgeList.add(edge);
            }
        }
        return edgeList;
    }

    protected List<EdgeType> getIncomingEdges(final NetType net, final String id) {
        final List<EdgeType> edgeList = new ArrayList<EdgeType>();
        for (final EdgeType edge : net.getEdge()) {
            if (id.equals(edge.getTargetId())) {
                edgeList.add(edge);
            }
        }
        return edgeList;
    }

    protected <T> T checkNode(final NetType net, final String name, final Class<? extends NodeType> classz, final int inEdges, final int outEdges) {
        final NodeType node = getNodeByName(net, name);
        assertNotNull("Could not find Node with name: " + name, node);
        return checkNode(net, node, classz, inEdges, outEdges);
    }

    protected <T> T checkNodeById(final NetType net, final String id, final Class<? extends NodeType> classz, final int inEdges, final int outEdges) {
        final NodeType node = getNodeById(net, id);
        assertNotNull("Could not find Node with ID: " + id, node);
        return checkNode(net, node, classz, inEdges, outEdges);
    }

    @SuppressWarnings("unchecked")
    protected <T> T checkNode(final NetType net, final NodeType node, final Class<? extends NodeType> classz, final int inEdges, final int outEdges) {
        assertNotNull("Node is NULL", node);
        final String nullSafeName = node.getName() != null ? node.getName() : "null";
        assertTrue("Node " + nullSafeName + " is not of class " + classz.getSimpleName()+ ", but "+ node.getClass().getSimpleName(), classz.isInstance(node));
        assertEquals("Wrong count of outgoing edges at " + nullSafeName, outEdges, countOutgoingEdges(net, node.getId()));
        assertEquals("Wrong count of incoming edges at " + nullSafeName, inEdges, countIncomingEdges(net, node.getId()));
        return (T) node;
    }

    protected TypeAttribute findExtensionByName(final NodeType node, final String name) {
        for (final TypeAttribute attr : node.getAttribute()) {
            if (name.equals(attr.getName())) {
                return attr;
            }
        }
        return null;
    }

    protected NodeType getFirstPredecessor(final NetType rootNet, final NodeType a) {
        return getNodeById(rootNet, getIncomingEdges(rootNet, a.getId()).get(0).getSourceId());
    }

    protected NodeType getFirstSuccessor(final NetType rootNet, final NodeType nodeA) {
        return getNodeByID(rootNet, getOutgoingEdges(rootNet, nodeA.getId()).get(0).getTargetId());
    }

    protected ResourceTypeType getResourceById(final CanonicalProcessType process, final String resourceId) {
        // Check Resource correct
        for (final ResourceTypeType resource : process.getResourceType()) {
            if (resource.getId().equals(resourceId)) {
                return resource;
            }
        }
        return null;
    }

    protected ResourceTypeType getResourceByName(final CanonicalProcessType process, final String resourceName) {
        for (final ResourceTypeType resource : process.getResourceType()) {
            if (resource.getName().equals(resourceName)) {
                return resource;
            }
        }
        return null;
    }

    protected ResourceTypeType hasResourceType(final WorkType node, final CanonicalProcessType cpf, final String resourceName) {
        for (ResourceTypeRefType ref : node.getResourceTypeRef()) {
            ResourceTypeType r = getResourceById(cpf, ref.getResourceTypeId());
            if (r != null && r.getName().equals(resourceName)) {
                return r;
            }
        }
        fail("Node " + node.getName() + " is missing Resource " + resourceName);
        return null;
    }

    protected ResourceTypeType hasResourceType(final WorkType node, final CanonicalProcessType cpf, final String resourceName, final String qualifier) {
        for (ResourceTypeRefType ref : node.getResourceTypeRef()) {
            ResourceTypeType r = getResourceById(cpf, ref.getResourceTypeId());
            if (r != null && r.getName().equals(resourceName) && qualifier.equals(ref.getQualifier())) {
                return r;
            }
        }
        fail("Node " + node.getName() + " is missing Resource " + resourceName);
        return null;
    }

    protected TypeAttribute hasAttribute(final ResourceTypeType roleX, final String name, final String value) {
        for (TypeAttribute attr : roleX.getAttribute()) {
            if (name.equals(attr.getName()) && value.equals(attr.getValue())) {
                return attr;
            }
        }
        fail("Resource " + roleX.getName() + " is missing Attribute " + name + " with Value " + value);
        return null;
    }


    protected void checkOnlyOneDefaultEdge(final List<EdgeType> edges) {
       int defaultEdgeCounter = 0;
       for (EdgeType e: edges) {
           if (e.getConditionExpr() == null) {
               defaultEdgeCounter ++;
           }
       }
       assertTrue(defaultEdgeCounter == 1);
    }
}