package org.apromore.service.impl;

import java.io.InputStream;
import java.util.HashSet;

import org.apromore.cpf.CanonicalProcessType;
import org.apromore.graph.canonical.Canonical;
import org.apromore.graph.canonical.Edge;
import org.apromore.graph.canonical.Node;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.service.CanonicalConverter;
import org.apromore.service.CanoniserService;
import org.apromore.service.model.CanonisedProcess;
import org.jbpt.algo.tree.rpst.RPST;
import org.jbpt.algo.tree.tctree.TCType;
import org.jbpt.utils.IOUtils;
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
import static org.hamcrest.Matchers.notNullValue;

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
    public void testConvertXPDLToGraph() throws Exception {
        InputStream input = ClassLoader.getSystemResourceAsStream("models/Disconnected.xpdl");
        CanonisedProcess cp = canoniserService.canonise("XPDL 2.1", input, new HashSet<RequestParameterType<?>>());

        assertThat(cp.getCpt().getNet().size(), equalTo(3));
        assertThat(cp.getCpt().getNet().get(0).getNode().size(), equalTo(0));
        assertThat(cp.getCpt().getNet().get(1).getNode().size(), equalTo(17));
        assertThat(cp.getCpt().getNet().get(2).getNode().size(), equalTo(5));
    }

    @Test
    @Rollback(true)
    public void testConvertYAWLToGraph() throws Exception {
        InputStream input = ClassLoader.getSystemResourceAsStream("models/filmproduction.yawl");
        CanonisedProcess cp = canoniserService.canonise("YAWL 2.2", input, new HashSet<RequestParameterType<?>>());
        Canonical g = converter.convert(cp.getCpt());

        RPST<Edge, Node> rpst = new RPST<Edge, Node>(g);
        IOUtils.toFile("canonised.dot", rpst.toDOT());
        assertThat(rpst.getRoot(), notNullValue());
        assertThat(rpst.getRPSTNodes(TCType.POLYGON).size(), equalTo(24));
        assertThat(rpst.getRPSTNodes(TCType.TRIVIAL).size(), equalTo(59));
        assertThat(rpst.getRPSTNodes(TCType.RIGID).size(), equalTo(2));
        assertThat(rpst.getRPSTNodes(TCType.BOND).size(), equalTo(7));
        assertThat(rpst.getRoot().getType(), equalTo(TCType.POLYGON));
        assertThat(rpst.getChildren(rpst.getRoot()).size(), equalTo(7));

        assertThat(cp.getCpt().getNet().size(), equalTo(1));
        assertThat(cp.getCpt().getNet().get(0).getNode().size(), equalTo(46));
        assertThat(cp.getCpt().getNet().get(0).getEdge().size(), equalTo(59));
        assertThat(cp.getCpt().getNet().get(0).getObject().size(), equalTo(43));

        CanonicalProcessType cpt = converter.convert(g);

        Canonical g1 = converter.convert(cp.getCpt());
        RPST<Edge, Node> rpst1 = new RPST<Edge, Node>(g1);
        IOUtils.toFile("decanonised.dot", rpst1.toDOT());

        canoniserService.deCanonise(1, "1.0", "YAWL 2.2", cpt, null, new HashSet<RequestParameterType<?>>());

        assertThat(cpt.getNet().size(), equalTo(1));
        assertThat(cpt.getNet().get(0).getNode().size(), equalTo(46));
        assertThat(cpt.getNet().get(0).getEdge().size(), equalTo(59));
        assertThat(cpt.getNet().get(0).getObject().size(), equalTo(43));
    }

}
