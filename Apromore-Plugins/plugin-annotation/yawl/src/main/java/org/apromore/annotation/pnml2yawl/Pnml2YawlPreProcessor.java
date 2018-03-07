/*
 * Copyright © 2009-2018 The Apromore Initiative.
 *
 * This file is part of "Apromore".
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

package org.apromore.annotation.pnml2yawl;

import org.apromore.anf.AnnotationsType;
import org.apromore.anf.GraphicsType;
import org.apromore.anf.PositionType;
import org.apromore.anf.SizeType;
import org.apromore.annotation.DefaultAbstractAnnotationProcessor;
import org.apromore.annotation.exception.AnnotationProcessorException;
import org.apromore.annotation.model.AnnotationData;
import org.apromore.annotation.result.AnnotationPluginResult;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.EventType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.RoutingType;
import org.apromore.cpf.SplitType;
import org.apromore.cpf.TaskType;
import org.apromore.plugin.PluginResult;
import org.apromore.plugin.message.PluginMessageImpl;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * PNML to YAWL Pre Processor.
 * Used to manipulate the PNML of the YAWL output when the input process langauge was EPML.
 * Used to change the size of the shapes as each language has different sizes elements.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Component("pnml2yawlPreAnnotationProcessor")
public class Pnml2YawlPreProcessor extends DefaultAbstractAnnotationProcessor {

    private static final BigDecimal divisor = new BigDecimal(2.0);
    private static final BigDecimal newEventHeight = new BigDecimal(32.0);
    private static final BigDecimal newEventWidth = new BigDecimal(32.0);
    private static final BigDecimal newTaskHeight = new BigDecimal(32.0);
    private static final BigDecimal newTaskWidth = new BigDecimal(32.0);

    @Override
    @SuppressWarnings("unchecked")
    public PluginResult processAnnotation(CanonicalProcessType canonisedFormat, AnnotationsType annotationFormat)
            throws AnnotationProcessorException {
        AnnotationPluginResult pluginResult = new AnnotationPluginResult();

        if (canonisedFormat == null) {
            pluginResult.getPluginMessage().add(new PluginMessageImpl("Canonised model passed into the Post Processor is Empty."));
        } else {
            try {
                Map<String, AnnotationData> annotations = new HashMap<>();
                if (annotationFormat == null || annotationFormat.getAnnotation() == null || annotationFormat.getAnnotation().isEmpty()) {
                    annotationFormat = createEmptyAnnotationFormat(canonisedFormat, annotationFormat);
                }

                manipulateShapes(canonisedFormat, annotationFormat, annotations);
                manipulateEdges(canonisedFormat, annotationFormat, annotations);

                pluginResult.setAnnotationsType(annotationFormat);
            } catch (Exception e) {
                throw new AnnotationProcessorException("Failed to execute the Post Processing.", e);
            }
        }

        return pluginResult;
    }



    /* loop through the list of nodes and process each one. */
    private void manipulateShapes(CanonicalProcessType cpf, AnnotationsType anf, Map<String, AnnotationData> annotations) {
        GraphicsType annotation;
        for (NetType net : cpf.getNet()) {
            for (NodeType node : net.getNode()) {
                annotation = findGraphicsType(anf, node.getId());
                if (annotation != null) {
                    if (node instanceof EventType) {
                        manipulateEvent(annotation, node, annotations);
                    } else if (node instanceof TaskType) {
                        manipulateTask(annotation, node, annotations);
                    }
                }
            }
        }
    }

    /* loop through the list of edges and process each one. */
    private void manipulateEdges(CanonicalProcessType cpf, AnnotationsType anf, Map<String, AnnotationData> annotations) {
        GraphicsType annotation;
        for (NetType net : cpf.getNet()) {
            for (EdgeType edge : net.getEdge()) {
                annotation = findGraphicsType(anf, edge.getId());
                if (annotation != null) {
                    manipulateEdge(annotation, edge, annotations);
                }
            }
        }
    }

    /* Changes the size of the Task Node. */
    private void manipulateTask(GraphicsType annType, NodeType node, Map<String, AnnotationData> annotations) {
        changeShapeSize(annType, node, newTaskHeight, newTaskWidth, annotations);
    }

    /* Changes the size of the Event Node. */
    private void manipulateEvent(GraphicsType annType, NodeType node, Map<String, AnnotationData> annotations) {
        changeShapeSize(annType, node, newEventHeight, newEventWidth, annotations);
    }


    protected void changeShapeSize(GraphicsType annType, NodeType node, BigDecimal newHeight, BigDecimal newWidth,
            Map<String, AnnotationData> annotations) {
        BigDecimal oldH = annType.getSize().getHeight();
        BigDecimal oldW = annType.getSize().getWidth();
        BigDecimal oldX = annType.getPosition().get(0).getX();
        BigDecimal oldY = annType.getPosition().get(0).getY();

        BigDecimal newX = oldW.subtract(newWidth).divide(divisor).add(oldX);
        BigDecimal newY = oldH.subtract(newHeight).divide(divisor).add(oldY);

        SizeType size = new SizeType();
        size.setHeight(newHeight);
        size.setWidth(newWidth);
        PositionType position = new PositionType();
        position.setX(newX);
        position.setY(newY);

        annType.setSize(size);
        annType.getPosition().remove(0);
        annType.getPosition().add(position);

        annotations.put(node.getId(), new AnnotationData(oldX, oldY, newX, newY, oldH, oldW, newHeight, newWidth));
    }


    /* Changes the position on the Edges From and Two coordinates. */
    private void manipulateEdge(GraphicsType graphicsType, EdgeType edge, Map<String, AnnotationData> annotations) {
        AnnotationData annData;

        if (annotations.containsKey(edge.getSourceId())) {
            annData = annotations.get(edge.getSourceId());
            changeLocation(graphicsType, annData);
        }
        if (annotations.containsKey(edge.getTargetId())) {
            annData = annotations.get(edge.getTargetId());
            changeLocation(graphicsType, annData);
        }
    }

    /* Changes the position of the edges X and Y. */
    private Map<PositionType, PositionType> changeLocation(GraphicsType graphicsType, AnnotationData annData) {
        int index = 0;
        PositionType newPos = new PositionType();
        Map<PositionType, PositionType> replace = new HashMap<>();

        for (PositionType pos : graphicsType.getPosition()) {
            if   ((pos.getX().compareTo(annData.getOldX()) >= 0) &&
                  (pos.getX().compareTo(annData.getOldX().add(annData.getOldW())) <= 0) &&
                  (pos.getY().compareTo(annData.getOldY()) >= 0) &&
                  (pos.getY().compareTo(annData.getOldY().add(annData.getOldH())) <= 0)) {
                newPos.setX(annData.getNewX().add(annData.getNewW().divide(divisor)));
                newPos.setY(annData.getNewY().add(annData.getNewH().divide(divisor)));
                graphicsType.getPosition().set(index, newPos);
            }
            index++;
        }

        return replace;
    }

}
