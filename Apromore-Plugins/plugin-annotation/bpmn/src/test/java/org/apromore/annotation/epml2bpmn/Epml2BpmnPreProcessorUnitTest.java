/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2013 - 2017 Queensland University of Technology.
 * Copyright (C) 2018, 2020 The University of Melbourne.
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

package org.apromore.annotation.epml2bpmn;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;

import org.apromore.anf.AnnotationsType;
import org.apromore.anf.GraphicsType;
import org.apromore.anf.PositionType;
import org.apromore.anf.SizeType;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.EventType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.TaskType;
import org.junit.Test;

/**
 * Test that the Epml2Bpmn Pre Processor does what it's suppose to.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class Epml2BpmnPreProcessorUnitTest {

    Epml2BpmnPreProcessor epml2BpmnPostProcessor;

    @Test
    public void testProcessAnnotation() throws Exception {
        epml2BpmnPostProcessor = new Epml2BpmnPreProcessor();

        CanonicalProcessType cpf = buildCPF();
        AnnotationsType anf = buildANF();

        epml2BpmnPostProcessor.processAnnotation(cpf, anf);

        assertThat(anf.getAnnotation().size(), equalTo(3));

        assertThat(((GraphicsType) anf.getAnnotation().get(0)).getPosition().size(),  equalTo(1));
        assertThat(((GraphicsType) anf.getAnnotation().get(0)).getPosition().get(0).getX().doubleValue(),  equalTo(85.5));
        assertThat(((GraphicsType) anf.getAnnotation().get(0)).getPosition().get(0).getY().doubleValue(),  equalTo(85.5));

        assertThat(((GraphicsType) anf.getAnnotation().get(1)).getPosition().size(),  equalTo(1));
        assertThat(((GraphicsType) anf.getAnnotation().get(1)).getPosition().get(0).getX().doubleValue(),  equalTo(150.5));
        assertThat(((GraphicsType) anf.getAnnotation().get(1)).getPosition().get(0).getY().doubleValue(),  equalTo(160.5));

        assertThat(((GraphicsType) anf.getAnnotation().get(2)).getPosition().size(),  equalTo(2));
        assertThat(((GraphicsType) anf.getAnnotation().get(2)).getPosition().get(0).getX().doubleValue(),  equalTo(100.5));
        assertThat(((GraphicsType) anf.getAnnotation().get(2)).getPosition().get(0).getY().doubleValue(),  equalTo(100.5));
        assertThat(((GraphicsType) anf.getAnnotation().get(2)).getPosition().get(1).getX().doubleValue(),  equalTo(200.5));
        assertThat(((GraphicsType) anf.getAnnotation().get(2)).getPosition().get(1).getY().doubleValue(),  equalTo(200.5));
    }


    private CanonicalProcessType buildCPF() {
        CanonicalProcessType cpf = new CanonicalProcessType();

        NetType net = new NetType();

        EventType event = new EventType();
        event.setId("1");
        event.setOriginalID("1");
        event.setName("event");

        TaskType task = new TaskType();
        task.setId("2");
        task.setOriginalID("2");
        task.setName("task");

        EdgeType edge = new EdgeType();
        edge.setId("3");
        edge.setOriginalID("3");
        edge.setSourceId("1");
        edge.setTargetId("2");

        net.getNode().add(event);
        net.getNode().add(task);
        net.getEdge().add(edge);
        cpf.getNet().add(net);

        return cpf;
    }


    private AnnotationsType buildANF() {
        AnnotationsType anf = new AnnotationsType();
        SizeType size = getSizeType();

        GraphicsType event = new GraphicsType();
        event.setId("4");
        event.setCpfId("1");
        event.setSize(size);
        event.getPosition().add(getPositionType(100, 100));

        GraphicsType task = new GraphicsType();
        task.setId("5");
        task.setCpfId("2");
        task.setSize(size);
        task.getPosition().add(getPositionType(200, 200));

        GraphicsType edge = new GraphicsType();
        edge.setId("6");
        edge.setCpfId("3");
        edge.setSize(size);
        edge.getPosition().add(getPositionType(100, 100));
        edge.getPosition().add(getPositionType(200, 200));

        anf.getAnnotation().add(event);
        anf.getAnnotation().add(task);
        anf.getAnnotation().add(edge);

        return anf;
    }


    private PositionType getPositionType(int x, int y) {
        PositionType position = new PositionType();
        position.setX(new BigDecimal(x));
        position.setY(new BigDecimal(y));
        return position;
    }

    private SizeType getSizeType() {
        SizeType size = new SizeType();
        size.setHeight(new BigDecimal(1));
        size.setWidth(new BigDecimal(1));
        return size;
    }

}
