package org.apromore.service.impl;

import java.io.InputStream;
import java.util.HashSet;

import org.apromore.cpf.EdgeType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.graph.canonical.Canonical;
import org.apromore.graph.canonical.Edge;
import org.apromore.graph.canonical.Node;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.service.CanonicalConverter;
import org.apromore.service.CanoniserService;
import org.apromore.service.model.CanonisedProcess;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Unit test the UserService Implementation.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/applicationContext-jpa-TEST.xml",
        "classpath:META-INF/spring/applicationContext-services-TEST.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
public class CanonicalConvertorIntgTest {

    @Autowired
    private CanoniserService canoniserService;
    @Autowired
    private CanonicalConverter converter;

    @Test
    @Rollback(true)
    public void testConvertToGraph() throws Exception {
        System.out.println("Testing Canoniser has correct number of Nodes and Edges.");
        InputStream input = ClassLoader.getSystemResourceAsStream("models/Disconnected.xpdl");
        CanonisedProcess cp = canoniserService.canonise("XPDL 2.1", input, new HashSet<RequestParameterType<?>>());

        System.out.println("Nets: " + cp.getCpt().getNet().size());
        System.out.println("Net 1 Nodes: " + cp.getCpt().getNet().get(0).getNode().size());
        System.out.println("Net 2 Nodes: " + cp.getCpt().getNet().get(1).getNode().size());
        System.out.println("Net 3 Nodes: " + cp.getCpt().getNet().get(2).getNode().size());
        System.out.println();

        int nodeCount = 0;
        int edgeCount = 0;
        for (String id : cp.getCpt().getRootIds()) {
            System.out.println("ID: " + id);
        }

        for (NetType net : cp.getCpt().getNet()) {
            System.out.println("Net: " + net.getId());
            for (NodeType node : net.getNode()) {
                nodeCount++;
                System.out.println(nodeCount + ": " + node.getName() + " (" + node.getId() + ")");
            }
            for (EdgeType edge : net.getEdge()) {
                edgeCount++;
                System.out.println(edgeCount + ": " + edge.getSourceId() + " -> " + edge.getTargetId());
            }
            System.out.println();
        }

        assertThat(cp.getCpt().getNet().size(), equalTo(3));
        assertThat(cp.getCpt().getNet().get(0).getNode().size(), equalTo(0));
        assertThat(cp.getCpt().getNet().get(1).getNode().size(), equalTo(17));
        assertThat(cp.getCpt().getNet().get(2).getNode().size(), equalTo(5));

        Canonical g = converter.convert(cp.getCpt());
        for (Node node : g.getNodes()) {
            nodeCount++;
            System.out.println(node.getName() + " (" + node.getId() + ")");
        }
        System.out.println();
        for (Edge edge : g.getEdges()) {
            edgeCount++;
            System.out.println(edge.getId() + ": " + edge.getSource().getName() + " -> " + edge.getTarget().getName());
        }

    }

}
