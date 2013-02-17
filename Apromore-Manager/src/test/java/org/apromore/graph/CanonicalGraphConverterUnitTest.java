package org.apromore.graph;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Iterator;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apromore.cpf.CPFSchema;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.EventType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.ResourceTypeRefType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.TaskType;
import org.apromore.graph.canonical.CPFNode;
import org.apromore.graph.canonical.Canonical;
import org.apromore.graph.canonical.ICPFResource;
import org.apromore.graph.canonical.ICPFResourceReference;
import org.apromore.graph.canonical.IEdge;
import org.apromore.graph.canonical.converter.CanonicalToGraph;
import org.apromore.graph.canonical.converter.GraphToCanonical;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;
import org.xml.sax.SAXException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Test the Canonical to Graph converter and the Graph to Canonical Converter.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */

public class CanonicalGraphConverterUnitTest {

    private final static String CANONICAL_MODELS_DIR = "CPF_models/";

    private CanonicalToGraph c2g;
    private GraphToCanonical g2c;
    private Unmarshaller unmarshaller;


    @Before
    public void setup() throws JAXBException, SAXException {
        c2g = new CanonicalToGraph();
        g2c = new GraphToCanonical();

        JAXBContext jaxbContext = JAXBContext.newInstance(CanonicalProcessType.class);
        unmarshaller = jaxbContext.createUnmarshaller();
        unmarshaller.setSchema(CPFSchema.getCPFSchema());
    }


    @Test
    public void testSingleNetTwoNodesOneEdge() throws Exception {
        CanonicalProcessType cpf = newInstance(CANONICAL_MODELS_DIR + "test1.cpf");

        assertThat(cpf.getNet().size(), equalTo(1));
        assertThat(cpf.getNet().get(0).getNode().size(), equalTo(2));
        assertThat(cpf.getNet().get(0).getEdge().size(), equalTo(1));

        Canonical outputCanonical = c2g.convert(cpf);

        assertThat(outputCanonical.getNodes().size(), equalTo(2));
        assertThat(outputCanonical.getEdges().size(), equalTo(1));

        CanonicalProcessType outputCpf = g2c.convert(outputCanonical);

        assertThat(outputCpf.getNet().size(), equalTo(1));
        assertThat(outputCpf.getNet().get(0).getNode().size(), equalTo(2));
        assertThat(outputCpf.getNet().get(0).getEdge().size(), equalTo(1));
    }

    @Test
    public void testSingleNetWithResource() throws Exception {
        CanonicalProcessType cpf = newInstance(CANONICAL_MODELS_DIR + "test2.cpf");

        assertThat(cpf.getResourceType().size(), equalTo(1));
        assertThat(cpf.getNet().size(), equalTo(1));
        assertThat(cpf.getNet().get(0).getNode().size(), equalTo(3));
        assertThat(cpf.getNet().get(0).getEdge().size(), equalTo(2));

        Canonical outputCanonical = c2g.convert(cpf);

        assertThat(outputCanonical.getResources().size(), equalTo(1));
        assertThat(outputCanonical.getNodes().size(), equalTo(3));
        assertThat(outputCanonical.getEdges().size(), equalTo(2));

        CanonicalProcessType convertedCpf = g2c.convert(outputCanonical);

        assertThat(convertedCpf.getResourceType().size(), equalTo(1));
        assertThat(convertedCpf.getNet().size(), equalTo(1));
        assertThat(convertedCpf.getNet().get(0).getNode().size(), equalTo(3));
        assertThat(convertedCpf.getNet().get(0).getEdge().size(), equalTo(2));
    }

    @Test
    public void testProcessWithSubProcess() throws Exception {
        CanonicalProcessType cpf = newInstance(CANONICAL_MODELS_DIR + "test4.cpf");

        assertThat(cpf.getNet().size(), equalTo(2));
        assertThat(cpf.getNet().get(0).getNode().size(), equalTo(3));
        assertThat(cpf.getNet().get(0).getEdge().size(), equalTo(2));

        Canonical outputCanonical = c2g.convert(cpf);

        assertThat(outputCanonical.getNodes().size(), equalTo(6));
        assertThat(outputCanonical.getEdges().size(), equalTo(4));

        CanonicalProcessType convertedCpf = g2c.convert(outputCanonical);

        assertThat(convertedCpf.getNet().size(), equalTo(2));
        assertThat(convertedCpf.getNet().get(0).getNode().size(), equalTo(3));
        assertThat(convertedCpf.getNet().get(0).getEdge().size(), equalTo(2));
    }

    @Test
    public void testFullCanoniserTest() throws Exception {
        CanonicalProcessType cpf = newInstance(CANONICAL_MODELS_DIR + "test5.cpf");

        assertThat(cpf.getNet().size(), equalTo(9));
        assertThat(cpf.getNet().get(0).getNode().size(), equalTo(11));
        assertThat(cpf.getNet().get(0).getEdge().size(), equalTo(13));
        assertThat(cpf.getNet().get(0).getObject().size(), equalTo(7));
        assertThat(cpf.getResourceType().size(), equalTo(45));

        Canonical outputCanonical = c2g.convert(cpf);

        assertThat(outputCanonical.getNodes().size(), equalTo(137));
        assertThat(outputCanonical.getEdges().size(), equalTo(164));
        assertThat(outputCanonical.getObjects().size(), equalTo(51));
        assertThat(outputCanonical.getResources().size(), equalTo(45));

        CanonicalProcessType convertedCpf = g2c.convert(outputCanonical);

        assertThat(convertedCpf.getNet().size(), equalTo(9));
        assertThat(convertedCpf.getNet().get(0).getNode().size(), equalTo(11));
        assertThat(convertedCpf.getNet().get(0).getEdge().size(), equalTo(13));
        assertThat(convertedCpf.getNet().get(0).getObject().size(), equalTo(7));
        assertThat(convertedCpf.getResourceType().size(), equalTo(45));
    }


	@Ignore
    @Test
    public void testTwoNetsWithResourcesObjectTreeCorrectToCanonical() throws Exception {
        CanonicalProcessType cpf = newInstance(CANONICAL_MODELS_DIR + "test3.cpf");

        assertThat(cpf.getResourceType().size(), equalTo(2));
        assertThat(cpf.getNet().size(), equalTo(2));

        ResourceTypeType resource = cpf.getResourceType().get(0);
        assertThat(resource.getId(), equalTo("c7"));
        assertThat(resource.getName(), equalTo("P1"));
        resource = cpf.getResourceType().get(1);
        assertThat(resource.getId(), equalTo("c17"));
        assertThat(resource.getName(), equalTo("P2"));

        // ****** First Net  ******* //
        NetType net1 = cpf.getNet().get(0);
        assertThat(net1.getId(), equalTo("c6"));
        assertThat(net1.getNode().size(), equalTo(3));
        assertThat(net1.getEdge().size(), equalTo(2));

        EventType event = (EventType) net1.getNode().get(0);
        assertThat(event.getId(), equalTo("c1"));
        assertThat(event.getName(), equalTo("S1"));
        assertThat(event.getResourceTypeRef().size(), equalTo(1));
        ResourceTypeRefType resourceRef = event.getResourceTypeRef().get(0);
        assertThat(resourceRef.getId(), equalTo("c8"));
        assertThat(resourceRef.getResourceTypeId(), equalTo("c7"));

        TaskType task = (TaskType) net1.getNode().get(1);
        assertThat(task.getId(), equalTo("c2"));
        assertThat(task.getName(), equalTo("T1"));
        assertThat(task.getResourceTypeRef().size(), equalTo(1));
        resourceRef = task.getResourceTypeRef().get(0);
        assertThat(resourceRef.getId(), equalTo("c9"));
        assertThat(resourceRef.getResourceTypeId(), equalTo("c7"));

        event = (EventType) net1.getNode().get(2);
        assertThat(event.getId(), equalTo("c3"));
        assertThat(event.getName(), equalTo("E1"));
        assertThat(event.getResourceTypeRef().size(), equalTo(1));
        resourceRef = event.getResourceTypeRef().get(0);
        assertThat(resourceRef.getId(), equalTo("c10"));
        assertThat(resourceRef.getResourceTypeId(), equalTo("c7"));

        EdgeType edge = net1.getEdge().get(0);
        assertThat(edge.getId(), equalTo("c4"));
        assertThat(edge.getSourceId(), equalTo("c1"));
        assertThat(edge.getTargetId(), equalTo("c2"));
        edge = net1.getEdge().get(1);
        assertThat(edge.getId(), equalTo("c5"));
        assertThat(edge.getSourceId(), equalTo("c2"));
        assertThat(edge.getTargetId(), equalTo("c3"));

        // ****** Seconds Net  ******* //
        NetType net2 = cpf.getNet().get(1);
        assertThat(net2.getId(), equalTo("c16"));
        assertThat(net2.getNode().size(), equalTo(3));
        assertThat(net2.getEdge().size(), equalTo(2));

        event = (EventType) net2.getNode().get(0);
        assertThat(event.getId(), equalTo("c11"));
        assertThat(event.getName(), equalTo("S2"));
        assertThat(event.getResourceTypeRef().size(), equalTo(1));
        resourceRef = event.getResourceTypeRef().get(0);
        assertThat(resourceRef.getId(), equalTo("c18"));
        assertThat(resourceRef.getResourceTypeId(), equalTo("c17"));

        task = (TaskType) net2.getNode().get(1);
        assertThat(task.getId(), equalTo("c12"));
        assertThat(task.getName(), equalTo("T2"));
        assertThat(task.getResourceTypeRef().size(), equalTo(1));
        resourceRef = task.getResourceTypeRef().get(0);
        assertThat(resourceRef.getId(), equalTo("c19"));
        assertThat(resourceRef.getResourceTypeId(), equalTo("c17"));

        event = (EventType) net2.getNode().get(2);
        assertThat(event.getId(), equalTo("c13"));
        assertThat(event.getName(), equalTo("E2"));
        assertThat(event.getResourceTypeRef().size(), equalTo(1));
        resourceRef = event.getResourceTypeRef().get(0);
        assertThat(resourceRef.getId(), equalTo("c20"));
        assertThat(resourceRef.getResourceTypeId(), equalTo("c17"));

        edge = net2.getEdge().get(0);
        assertThat(edge.getId(), equalTo("c14"));
        assertThat(edge.getSourceId(), equalTo("c11"));
        assertThat(edge.getTargetId(), equalTo("c12"));
        edge = net2.getEdge().get(1);
        assertThat(edge.getId(), equalTo("c15"));
        assertThat(edge.getSourceId(), equalTo("c12"));
        assertThat(edge.getTargetId(), equalTo("c13"));


        // ****** Convert and Check the same structure  ******* //
        Canonical outputCanonical = c2g.convert(cpf);

        assertThat(outputCanonical.getResources().size(), equalTo(2));
        assertThat(outputCanonical.getNodes().size(), equalTo(6));
        assertThat(outputCanonical.getEdges().size(), equalTo(4));

        Iterator resItor = outputCanonical.getResources().iterator();
        ICPFResource cpfResource = (ICPFResource) resItor.next();
        assertThat(cpfResource.getId(), equalTo("c17"));
        assertThat(cpfResource.getName(), equalTo("P2"));
        cpfResource = (ICPFResource) resItor.next();
        assertThat(cpfResource.getId(), equalTo("c7"));
        assertThat(cpfResource.getName(), equalTo("P1"));

        CPFNode node1 = outputCanonical.getNode("c1");
        assertThat(node1.getId(), equalTo("c1"));
        assertThat(node1.getName(), equalTo("S1"));
        assertThat(node1.getResourceReferences().size(), equalTo(1));
        ICPFResourceReference cpfResourceRef = node1.getResourceReferences().iterator().next();
        assertThat(cpfResourceRef.getId(), equalTo("c8"));
        assertThat(cpfResourceRef.getResourceId(), equalTo("c7"));

        CPFNode node2 = outputCanonical.getNode("c2");
        assertThat(node2.getId(), equalTo("c2"));
        assertThat(node2.getName(), equalTo("T1"));
        assertThat(node2.getResourceReferences().size(), equalTo(1));
        cpfResourceRef = node2.getResourceReferences().iterator().next();
        assertThat(cpfResourceRef.getId(), equalTo("c9"));
        assertThat(cpfResourceRef.getResourceId(), equalTo("c7"));

        CPFNode node3 = outputCanonical.getNode("c3");
        assertThat(node3.getId(), equalTo("c3"));
        assertThat(node3.getName(), equalTo("E1"));
        assertThat(node3.getResourceReferences().size(), equalTo(1));
        cpfResourceRef = node3.getResourceReferences().iterator().next();
        assertThat(cpfResourceRef.getId(), equalTo("c10"));
        assertThat(cpfResourceRef.getResourceId(), equalTo("c7"));

        CPFNode node11 = outputCanonical.getNode("c11");
        assertThat(node11.getId(), equalTo("c11"));
        assertThat(node11.getName(), equalTo("S2"));
        assertThat(node11.getResourceReferences().size(), equalTo(1));
        cpfResourceRef = node11.getResourceReferences().iterator().next();
        assertThat(cpfResourceRef.getId(), equalTo("c18"));
        assertThat(cpfResourceRef.getResourceId(), equalTo("c17"));

        CPFNode node12 = outputCanonical.getNode("c12");
        assertThat(node12.getId(), equalTo("c12"));
        assertThat(node12.getName(), equalTo("T2"));
        assertThat(node12.getResourceReferences().size(), equalTo(1));
        cpfResourceRef = node12.getResourceReferences().iterator().next();
        assertThat(cpfResourceRef.getId(), equalTo("c19"));
        assertThat(cpfResourceRef.getResourceId(), equalTo("c17"));

        CPFNode node13 = outputCanonical.getNode("c13");
        assertThat(node13.getId(), equalTo("c13"));
        assertThat(node13.getName(), equalTo("E2"));
        assertThat(node13.getResourceReferences().size(), equalTo(1));
        cpfResourceRef = node13.getResourceReferences().iterator().next();
        assertThat(cpfResourceRef.getId(), equalTo("c20"));
        assertThat(cpfResourceRef.getResourceId(), equalTo("c17"));

        IEdge cpfEdge = outputCanonical.getEdge(node1, node2);
        assertThat(cpfEdge.getId(), equalTo("c4"));
        assertThat(cpfEdge.getSource().getId(), equalTo("c1"));
        assertThat(cpfEdge.getTarget().getId(), equalTo("c2"));

        cpfEdge = outputCanonical.getEdge(node2, node3);
        assertThat(cpfEdge.getId(), equalTo("c5"));
        assertThat(cpfEdge.getSource().getId(), equalTo("c2"));
        assertThat(cpfEdge.getTarget().getId(), equalTo("c3"));

        cpfEdge = outputCanonical.getEdge(node11, node12);
        assertThat(cpfEdge.getId(), equalTo("c14"));
        assertThat(cpfEdge.getSource().getId(), equalTo("c11"));
        assertThat(cpfEdge.getTarget().getId(), equalTo("c12"));

        cpfEdge = outputCanonical.getEdge(node12, node13);
        assertThat(cpfEdge.getId(), equalTo("c15"));
        assertThat(cpfEdge.getSource().getId(), equalTo("c12"));
        assertThat(cpfEdge.getTarget().getId(), equalTo("c13"));
    }

	@Ignore
    @Test
    public void testTwoNetsWithResourcesObjectTreeCorrectBackToGraph() throws Exception {
        CanonicalProcessType can = newInstance(CANONICAL_MODELS_DIR + "test3.cpf");
        Canonical outputCanonical = c2g.convert(can);

        CanonicalProcessType convertedCpf = g2c.convert(outputCanonical);

        assertThat(convertedCpf.getResourceType().size(), equalTo(2));
        assertThat(convertedCpf.getNet().size(), equalTo(2));

        ResourceTypeType resource = convertedCpf.getResourceType().get(0);
        assertThat(resource.getId(), equalTo("c17"));
        assertThat(resource.getName(), equalTo("P2"));
        resource = convertedCpf.getResourceType().get(1);
        assertThat(resource.getId(), equalTo("c7"));
        assertThat(resource.getName(), equalTo("P1"));

        // ****** First Net  ******* //
        NetType net1 = convertedCpf.getNet().get(0);
        assertThat(net1.getId(), equalTo("c6"));
        assertThat(net1.getNode().size(), equalTo(3));
        assertThat(net1.getEdge().size(), equalTo(2));

        EventType event = (EventType) net1.getNode().get(0);
        assertThat(event.getId(), equalTo("c1"));
        assertThat(event.getName(), equalTo("S1"));
        assertThat(event.getResourceTypeRef().size(), equalTo(1));
        ResourceTypeRefType resourceRef = event.getResourceTypeRef().get(0);
        assertThat(resourceRef.getId(), equalTo("c8"));
        assertThat(resourceRef.getResourceTypeId(), equalTo("c7"));

        event = (EventType) net1.getNode().get(1);
        assertThat(event.getId(), equalTo("c3"));
        assertThat(event.getName(), equalTo("E1"));
        assertThat(event.getResourceTypeRef().size(), equalTo(1));
        resourceRef = event.getResourceTypeRef().get(0);
        assertThat(resourceRef.getId(), equalTo("c10"));
        assertThat(resourceRef.getResourceTypeId(), equalTo("c7"));

        TaskType task = (TaskType) net1.getNode().get(2);
        assertThat(task.getId(), equalTo("c2"));
        assertThat(task.getName(), equalTo("T1"));
        assertThat(task.getResourceTypeRef().size(), equalTo(1));
        resourceRef = task.getResourceTypeRef().get(0);
        assertThat(resourceRef.getId(), equalTo("c9"));
        assertThat(resourceRef.getResourceTypeId(), equalTo("c7"));

        EdgeType edge = net1.getEdge().get(0);
        assertThat(edge.getId(), equalTo("c4"));
        assertThat(edge.getSourceId(), equalTo("c1"));
        assertThat(edge.getTargetId(), equalTo("c2"));
        edge = net1.getEdge().get(1);
        assertThat(edge.getId(), equalTo("c5"));
        assertThat(edge.getSourceId(), equalTo("c2"));
        assertThat(edge.getTargetId(), equalTo("c3"));

        // ****** Seconds Net  ******* //
        NetType net2 = convertedCpf.getNet().get(1);
        assertThat(net2.getId(), equalTo("c16"));
        assertThat(net2.getNode().size(), equalTo(3));
        assertThat(net2.getEdge().size(), equalTo(2));

        event = (EventType) net2.getNode().get(0);
        assertThat(event.getId(), equalTo("c13"));
        assertThat(event.getName(), equalTo("E2"));
        assertThat(event.getResourceTypeRef().size(), equalTo(1));
        resourceRef = event.getResourceTypeRef().get(0);
        assertThat(resourceRef.getId(), equalTo("c20"));
        assertThat(resourceRef.getResourceTypeId(), equalTo("c17"));

        task = (TaskType) net2.getNode().get(1);
        assertThat(task.getId(), equalTo("c12"));
        assertThat(task.getName(), equalTo("T2"));
        assertThat(task.getResourceTypeRef().size(), equalTo(1));
        resourceRef = task.getResourceTypeRef().get(0);
        assertThat(resourceRef.getId(), equalTo("c19"));
        assertThat(resourceRef.getResourceTypeId(), equalTo("c17"));

        event = (EventType) net2.getNode().get(2);
        assertThat(event.getId(), equalTo("c11"));
        assertThat(event.getName(), equalTo("S2"));
        assertThat(event.getResourceTypeRef().size(), equalTo(1));
        resourceRef = event.getResourceTypeRef().get(0);
        assertThat(resourceRef.getId(), equalTo("c18"));
        assertThat(resourceRef.getResourceTypeId(), equalTo("c17"));

        edge = net2.getEdge().get(0);
        assertThat(edge.getId(), equalTo("c15"));
        assertThat(edge.getSourceId(), equalTo("c12"));
        assertThat(edge.getTargetId(), equalTo("c13"));
        edge = net2.getEdge().get(1);
        assertThat(edge.getId(), equalTo("c14"));
        assertThat(edge.getSourceId(), equalTo("c11"));
        assertThat(edge.getTargetId(), equalTo("c12"));
    }


//    @Test
//    public void testComplexCPFToDotPNG() throws Exception {
//        CanonicalProcessType cpt = newInstance(CANONICAL_MODELS_DIR + "GP2.cpf");
//        Canonical graph = new CanonicalToGraph().convert(cpt);
//        IOUtils.toFile("GP2.dot", graph.toDOT());
//        IOUtils.invokeDOT("target/", "GP2.png", graph.toDOT());
//    }



    /* Loads the  */
    @SuppressWarnings("unchecked")
    private CanonicalProcessType newInstance(String fileName) throws JAXBException, SAXException, FileNotFoundException {
        InputStream stream = ClassLoader.getSystemResourceAsStream(fileName);
        return ((JAXBElement<CanonicalProcessType>) unmarshaller.unmarshal(stream)).getValue();
    }
}
