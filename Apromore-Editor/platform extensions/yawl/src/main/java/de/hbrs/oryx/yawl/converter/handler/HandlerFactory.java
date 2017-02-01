/*
 * Copyright © 2009-2017 The Apromore Initiative.
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
package de.hbrs.oryx.yawl.converter.handler;

import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.basic.BasicEdge;
import org.oryxeditor.server.diagram.basic.BasicShape;
import org.yawlfoundation.yawl.elements.YDecomposition;
import org.yawlfoundation.yawl.elements.YFlow;
import org.yawlfoundation.yawl.elements.YNetElement;
import org.yawlfoundation.yawl.elements.YSpecification;

import de.hbrs.oryx.yawl.converter.handler.oryx.OryxHandler;
import de.hbrs.oryx.yawl.converter.handler.yawl.YAWLHandler;

/**
 * Interface for the Factory that creates the appropriate YAWL handler for each type YAWL object and the appropriate Oryx handler for each shape.
 * 
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 * 
 */
public interface HandlerFactory {

    /**
     * Return a YAWLHandler for a whole YSpecification
     * 
     * @param ySpec
     * @return
     */
    YAWLHandler createYAWLConverter(YSpecification ySpec);

    /**
     * Returns a YAWLHandler for each type of YDecomposition, which may be YNet or YAWLServiceGatway
     * 
     * @param decomposition
     * @return an implementation of YAWLHandler
     */
    YAWLHandler createYAWLConverter(YDecomposition decomposition);

    /**
     * Returns a YAWLHandler for each type of YNetElement, which may be YAtomicTask, YCompositeTask, YInputCondition, YOutputCondition, YCondition
     * 
     * @param netElement
     * @return an implementation of YAWLHandler
     */
    YAWLHandler createYAWLConverter(YNetElement netElement);

    /**
     * Returns a YAWLHandler for each type of flow
     * 
     * @param yFlow
     * @return an implementation of YAWLHandler
     */
    YAWLHandler createYAWLConverter(YFlow yFlow);

    /**
     * Return a OryxHandler for a Diagram
     * 
     * @param diagramShape
     * @return an implementation of OryxHandler
     */
    OryxHandler createOryxConverter(BasicDiagram diagramShape);

    /**
     * Return a OryxHandler for a non-edge Shape
     * 
     * @param shape
     * @return an implementation of OryxHandler
     */
    OryxHandler createOryxConverter(BasicShape shape);

    /**
     * Return a OryxHandler for a edge Shape
     * 
     * @param flowShape
     *            the egde in Oryx
     * @param shape
     *            of the parent net is needed as edges sometimes are not part of a net
     * @return an implementation of OryxHandler
     */
    OryxHandler createOryxConverter(BasicEdge flowShape, BasicShape netShape);

}
