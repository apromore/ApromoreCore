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

package org.apromore.service.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import javax.inject.Inject;
import java.io.InputStream;
import java.util.HashSet;

import org.apromore.graph.canonical.CPFEdge;
import org.apromore.graph.canonical.CPFNode;
import org.apromore.graph.canonical.Canonical;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.service.CanonicalConverter;
import org.apromore.service.CanoniserService;
import org.apromore.service.model.CanonisedProcess;
import org.jbpt.algo.tree.rpst.RPST;
import org.jbpt.algo.tree.tctree.TCType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

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
public class CanonicalConvertorIntgTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CanonicalConvertorIntgTest.class);

    @Inject
    private CanoniserService canoniserService;
    @Inject
    private CanonicalConverter converter;



    @Test
    @Rollback(true)
    public void testConvertXPDLToGraph() throws Exception {
        InputStream input = ClassLoader.getSystemResourceAsStream("models/Disconnected.xpdl");
        CanonisedProcess cp = canoniserService.canonise("XPDL 2.2", input, new HashSet<RequestParameterType<?>>());

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

        RPST<CPFEdge, CPFNode> rpst = new RPST<CPFEdge, CPFNode>(g);
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
    }

}
