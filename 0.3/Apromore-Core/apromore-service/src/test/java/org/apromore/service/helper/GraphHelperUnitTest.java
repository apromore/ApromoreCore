package org.apromore.service.helper;

import org.apromore.TestData;
import org.apromore.common.Constants;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ObjectType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.TypeAttribute;
import org.apromore.graph.JBPT.CPF;
import org.apromore.graph.JBPT.CpfTask;
import org.jbpt.pm.ControlFlow;
import org.jbpt.pm.FlowNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Unit Test for the GraphToCPFHelper.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/applicationContext-jpa-TEST.xml",
        "classpath:META-INF/spring/applicationContext-services-TEST.xml"
})
public class GraphHelperUnitTest {

    @Test
    public void testCreatingAGraphFromCPT() {
        CanonicalProcessType cpt = new CanonicalProcessType();
        cpt.getAttribute().add(createAttribute("name", "value"));
        cpt.getObject().add(createObject("123", "name"));
        cpt.getNet().add(createNet("321"));

        CPF graph = CPFtoGraphHelper.createGraph(cpt);

        assertThat(graph.getEdges().size(), equalTo(1));
        assertThat(graph.getFlowNodes().size(), equalTo(2));
        assertThat(graph.getProperties().size(), equalTo(1));
        assertThat(((CpfTask) graph.getFlowNodes().iterator().next()).getAttribute("testNodeB"), equalTo("NodeValuetestNodeB"));
        for (ControlFlow<FlowNode> fn : graph.getEdges()) {
            assertThat(fn.getSource().getName(), equalTo("testNodeA"));
            assertThat(fn.getTarget().getName(), equalTo("testNodeB"));
        }
    }


    @Test
    @SuppressWarnings("unchecked")
    public void CreateGraphFromRealCPF() throws Exception {
        JAXBContext jc = JAXBContext.newInstance(Constants.CPF_CONTEXT);
        Unmarshaller u = jc.createUnmarshaller();
        InputStream data = new ByteArrayInputStream(TestData.CPF2.getBytes());
        JAXBElement<CanonicalProcessType> rootElement = (JAXBElement<CanonicalProcessType>) u.unmarshal(data);
        CanonicalProcessType canType = rootElement.getValue();

        CPF graph = CPFtoGraphHelper.createGraph(canType);

        assertThat(graph, notNullValue());

        //TODO: ADD some more asserts
    }



    @Test
    @SuppressWarnings("unchecked")
    public void ToAndFromGraphUsingCPF() throws Exception {
        JAXBContext jc = JAXBContext.newInstance(Constants.CPF_CONTEXT);
        Unmarshaller u = jc.createUnmarshaller();
        InputStream data = new ByteArrayInputStream(TestData.CPF2.getBytes());
        JAXBElement<CanonicalProcessType> rootElement = (JAXBElement<CanonicalProcessType>) u.unmarshal(data);
        CanonicalProcessType canType = rootElement.getValue();

        CPF graph = CPFtoGraphHelper.createGraph(canType);
        assertThat(graph, notNullValue());

        CanonicalProcessType returnType = GraphToCPFHelper.createCanonicalProcess(graph);
        assertThat(returnType, notNullValue());

        assertThat(returnType.getAttribute().size(), equalTo(canType.getAttribute().size()));
        assertThat(returnType.getObject().size(), equalTo(canType.getObject().size()));
        assertThat(returnType.getResourceType().size(), equalTo(canType.getResourceType().size()));
        assertThat(returnType.getNet().size(), equalTo(canType.getNet().size()));

        // Now Find the Node Named 'F1' from each and compare the Resources List equals.
        TaskType node1 = null;
        TaskType node2 = null;
        for (NodeType node : returnType.getNet().get(0).getNode()) {
            if (node.getName().equals("F1")) {
                node1 = (TaskType) node;
            }
        }
        for (NodeType node : canType.getNet().get(0).getNode()) {
            if (node.getName().equals("F1")) {
                node2 = (TaskType) node;
            }
        }
        assertThat(node1.getResourceTypeRef().size(), equalTo(node2.getResourceTypeRef().size()));
    }






    private NetType createNet(String id) {
        NetType net = new NetType();
        net.setId(id);
        net.getNode().add(createNode("2", "testNodeA"));
        net.getNode().add(createNode("3", "testNodeB"));
        net.getEdge().add(createEdge("4", "2", "3"));
        return net;
    }


    private TypeAttribute createAttribute(String name, String value) {
        TypeAttribute ta = new TypeAttribute();
        ta.setTypeRef(name);
        ta.setValue(value);
        return ta;
    }

    private ObjectType createObject(String id, String name) {
        ObjectType obj = new ObjectType();
        obj.setId(id);
        obj.setName(name);
        obj.setConfigurable(Boolean.TRUE);
        return obj;
    }

    private TaskType createNode(String id, String name) {
        TaskType node = new TaskType();
        node.setId(id);
        node.setName(name);
        node.setConfigurable(Boolean.FALSE);
        node.getAttribute().add(createAttribute(name, "NodeValue" + name));
        return node;
    }

    private EdgeType createEdge(String id, String source, String target) {
        EdgeType edge = new EdgeType();
        edge.setId(id);
        edge.setCondition("");
        edge.setDefault(Boolean.FALSE);
        edge.setSourceId(source);
        edge.setTargetId(target);
        return edge;
    }

}
