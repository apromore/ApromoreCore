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
package org.apromore.annotation;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apromore.anf.AnnotationType;
import org.apromore.anf.AnnotationsType;
import org.apromore.anf.GraphicsType;
import org.apromore.anf.PositionType;
import org.apromore.anf.SizeType;
import org.apromore.annotation.model.AnnotationData;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.plugin.DefaultParameterAwarePlugin;

/**
 * Implements common functionality shared by all Annotation Post Processors and reads the supported native types from the Annotation 'plugin.config'
 * file. The key used is: 'annotation.processFormatProcessor'.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public abstract class DefaultAbstractAnnotationProcessor extends DefaultParameterAwarePlugin implements AnnotationProcessor {

    /*
     * (non-Javadoc)
     * @see org.apromore.annotation.Annotation#getProcessFormatProcessor()
     */
    @Override
    public String getProcessFormatProcessor() {
        return getConfigurationByName("annotation.processFormatProcessor");
    }


    /**
     * if the Annotation is null or empty we need to create an empty anf so we can process them.
     * @param cpf the canonical format.
     * @param anf the annotation format.
     */
    protected AnnotationsType createEmptyAnnotationFormat(CanonicalProcessType cpf, AnnotationsType anf) {
        if (anf == null) {
            anf = new AnnotationsType();
        }

        for (NetType net : cpf.getNet()) {
            for (NodeType node : net.getNode()) {
                anf.getAnnotation().add(createNodeAnnotation(node));
            }
            for (EdgeType edge : net.getEdge()) {
                anf.getAnnotation().add(createEdgeAnnotation(edge));
            }
        }

        return anf;
    }


    /* Find the Nodes that this node link. */
    protected Map<EdgeType, NodeType> findSplitNodeTargets(CanonicalProcessType cpf, String cpfId) {
        Map<EdgeType, NodeType> result = new HashMap<>();
        for (NetType net : cpf.getNet()) {
            for (EdgeType edge : net.getEdge()) {
                if (edge.getSourceId().equals(cpfId)) {
                    result.put(edge, findCPFNode(cpf, edge.getTargetId()));
                }
            }
        }
        return result;
    }

    /* Find an annotation that is a Graphics Annotation for this CPF Id. */
    protected GraphicsType findGraphicsType(AnnotationsType anf, String id) {
        GraphicsType graphicsType = null;
        for (AnnotationType annType : anf.getAnnotation()) {
            if (annType instanceof GraphicsType && annType.getCpfId().equals(id)) {
                graphicsType = (GraphicsType) annType;
                break;
            }
        }
        return graphicsType;
    }

    /* Find a node in the CPF using the cpfId */
    protected NodeType findCPFNode(CanonicalProcessType cpf, String cpfId) {
        NodeType result = null;
        for (NetType net : cpf.getNet()) {
            for (NodeType node : net.getNode()) {
                if (node.getId().equals(cpfId)) {
                    result = node;
                    break;
                }
            }
        }
        return result;
    }


    /* Create an anf graphic type for this node, Signavio doesn't display models without an annotation. */
    private AnnotationType createNodeAnnotation(NodeType node) {
        GraphicsType graphicsType = new GraphicsType();
        graphicsType.setId(UUID.randomUUID().toString());
        graphicsType.setCpfId(node.getId());

        SizeType size = new SizeType();
        size.setHeight(new BigDecimal(30.0));
        size.setWidth(new BigDecimal(30.0));
        graphicsType.setSize(size);

        PositionType position = new PositionType();
        position.setX(new BigDecimal(100.0));
        position.setY(new BigDecimal(100.0));
        graphicsType.getPosition().add(position);

        return graphicsType;
    }

    /* Create an anf graphic type for this edge, Signavio doesn't display models without an annotation. */
    private AnnotationType createEdgeAnnotation(EdgeType edge) {
        GraphicsType graphicsType = new GraphicsType();
        graphicsType.setId(UUID.randomUUID().toString());
        graphicsType.setCpfId(edge.getId());

        PositionType position = new PositionType();
        position.setX(new BigDecimal(100.0));
        position.setY(new BigDecimal(100.0));
        graphicsType.getPosition().add(position);

        position = new PositionType();
        position.setX(new BigDecimal(110.0));
        position.setY(new BigDecimal(110.0));
        graphicsType.getPosition().add(position);

        return graphicsType;
    }

    protected void changeShapeSize(GraphicsType annType, NodeType node, BigDecimal newHeight, BigDecimal newWidth,
                         Map<String, AnnotationData> annotations){
        if(annType.getSize()==null){
            SizeType sizeType=new SizeType();
            sizeType.setWidth(new BigDecimal(100));
            sizeType.setHeight(new BigDecimal(100));
            annType.setSize(sizeType);
        }
        if(annType.getPosition().isEmpty()){
            PositionType positionType=new PositionType();
            positionType.setX(new BigDecimal(0));
            positionType.setY(new BigDecimal(0));
            annType.getPosition().add(positionType);
        }
        //you should call this method every time you override that:    super.changeShapeSize(...)
    }

}
