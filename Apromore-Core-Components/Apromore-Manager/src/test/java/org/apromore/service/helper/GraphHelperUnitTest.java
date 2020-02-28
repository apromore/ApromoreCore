/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.service.helper;

import org.apromore.TestData;
import org.apromore.common.Constants;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.ConditionExpressionType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ObjectType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.TypeAttribute;
import org.apromore.graph.canonical.CPFEdge;
import org.apromore.graph.canonical.Canonical;
import org.apromore.service.CanonicalConverter;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

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
@Ignore
public class GraphHelperUnitTest {

    @Inject
    private CanonicalConverter convertor;

    @Test
    public void createAGraphFromCPT() {
        CanonicalProcessType cpt = new CanonicalProcessType();
        cpt.getAttribute().add(createAttribute("name", "value"));
        cpt.getNet().add(createNet("321"));

        Canonical graph = convertor.convert(cpt);

        assertThat(graph.getEdges().size(), equalTo(1));
        assertThat(graph.getNodes().size(), equalTo(2));
        assertThat(graph.getProperties().size(), equalTo(1));
        assertThat((graph.getNodes().iterator().next()).getAttribute("testNodeB").getValue(), equalTo("NodeValuetestNodeB"));
        for (CPFEdge fn : graph.getEdges()) {
            assertThat(fn.getSource().getName(), equalTo("testNodeA"));
            assertThat(fn.getTarget().getName(), equalTo("testNodeB"));
        }
    }


    @Test
    @SuppressWarnings("unchecked")
    public void createGraphFromRealCPF() throws Exception {
        JAXBContext jc = JAXBContext.newInstance(Constants.CPF_CONTEXT);
        Unmarshaller u = jc.createUnmarshaller();
        InputStream data = new ByteArrayInputStream(TestData.CPF2.getBytes());
        JAXBElement<CanonicalProcessType> rootElement = (JAXBElement<CanonicalProcessType>) u.unmarshal(data);
        CanonicalProcessType canType = rootElement.getValue();

        Canonical graph = convertor.convert(canType);

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

        Canonical graph = convertor.convert(canType);
        assertThat(graph, notNullValue());

        CanonicalProcessType returnType = convertor.convert(graph);
        assertThat(returnType, notNullValue());

        assertThat(returnType.getAttribute().size(), equalTo(canType.getAttribute().size()));
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






    private NetType createNet(final String id) {
        NetType net = new NetType();
        net.setId(id);
        net.getNode().add(createNode("2", "testNodeA"));
        net.getNode().add(createNode("3", "testNodeB"));
        net.getEdge().add(createEdge("4", "2", "3"));
        return net;
    }


    private TypeAttribute createAttribute(final String name, final String value) {
        TypeAttribute ta = new TypeAttribute();
        ta.setName(name);
        ta.setValue(value);
        //TODO test setAny
        //ta.setAny();
        return ta;
    }

    private ObjectType createObject(final String id, final String name) {
        ObjectType obj = new ObjectType();
        obj.setId(id);
        obj.setName(name);
        obj.setConfigurable(Boolean.TRUE);
        return obj;
    }

    private TaskType createNode(final String id, final String name) {
        TaskType node = new TaskType();
        node.setId(id);
        node.setName(name);
        node.setConfigurable(Boolean.FALSE);
        node.getAttribute().add(createAttribute(name, "NodeValue" + name));
        return node;
    }

    private EdgeType createEdge(final String id, final String source, final String target) {
        EdgeType edge = new EdgeType();
        edge.setId(id);
        ConditionExpressionType expr = new ConditionExpressionType();
        expr.setExpression("");
        edge.setConditionExpr(expr);
        edge.setDefault(Boolean.FALSE);
        edge.setSourceId(source);
        edge.setTargetId(target);
        return edge;
    }

}
