package org.apromore.service.impl;

import org.apromore.graph.canonical.CPFNode;
import org.apromore.graph.canonical.Canonical;
import org.apromore.graph.canonical.CPFEdge;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.service.CanonicalConverter;
import org.apromore.service.CanoniserService;
import org.apromore.service.model.CanonisedProcess;
import org.apromore.service.model.FragmentNode;
import org.apromore.util.MutableTreeConstructor;
import org.jbpt.algo.tree.rpst.IRPSTNode;
import org.jbpt.algo.tree.rpst.RPST;
import org.jbpt.algo.tree.tctree.TCType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.HashSet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

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
@TestExecutionListeners(value = DependencyInjectionTestExecutionListener.class)
public class MutableTreeIntgTest {

    @Autowired
    private CanoniserService cSrv;
    @Autowired
    private CanonicalConverter converter;

    @Test
    @Rollback(true)
    public void CanonicalRPSTGraphTest() throws Exception {
        InputStream input = ClassLoader.getSystemResourceAsStream("models/Disconnected.xpdl");
        CanonisedProcess cp = cSrv.canonise("XPDL 2.2", input, new HashSet<RequestParameterType<?>>());
        Canonical g = converter.convert(cp.getCpt());

        RPST<CPFEdge, CPFNode> rpst = new RPST<>(g);
        for (IRPSTNode<CPFEdge, CPFNode> node : rpst.getRPSTNodes()) {
            if (rpst.isRoot(node)) {
                assertThat(node.getEntry(), nullValue());
                assertThat(node.getExit(), nullValue());
            } else {
                assertThat(node.getEntry(), notNullValue());
                assertThat(node.getExit(), notNullValue());
            }
        }

        assertThat(rpst.getRoot(), notNullValue());
        assertThat(rpst.getRPSTNodes(TCType.POLYGON).size(), equalTo(5));
        assertThat(rpst.getRPSTNodes(TCType.TRIVIAL).size(), equalTo(22));
        assertThat(rpst.getRPSTNodes(TCType.RIGID).size(), equalTo(0));
        assertThat(rpst.getRPSTNodes(TCType.BOND).size(), equalTo(3));
        assertThat(rpst.getRoot().getType(), equalTo(TCType.BOND));
        assertThat(rpst.getChildren(rpst.getRoot()).size(), equalTo(2));

        FragmentNode root = new MutableTreeConstructor().construct(rpst);
        assertThat(root.getNodes().size(), equalTo(22));
        assertThat(root.getEdges().size(), equalTo(22));
        assertThat(root.getChildren().size(), equalTo(2));
        assertThat(root.getEntry(), nullValue());
        assertThat(root.getExit(), nullValue());
    }

}
