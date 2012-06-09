/**
 * Copyright (c) 2011-2012 Felix Mannhardt
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 * See: http://www.opensource.org/licenses/mit-license.php
 * 
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
 * Interface for the Factory that creates the appropriate YAWL handler for each
 * type YAWL object and the appropriate Oryx handler for each shape.
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
	 * Returns a YAWLHandler for each type of YDecomposition, which may be YNet
	 * or YAWLServiceGatway
	 * 
	 * @param decomposition
	 * @return an implementation of YAWLHandler
	 */
	YAWLHandler createYAWLConverter(YDecomposition decomposition);

	/**
	 * Returns a YAWLHandler for each type of YNetElement, which may be
	 * YAtomicTask, YCompositeTask, YInputCondition, YOutputCondition,
	 * YCondition
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
	 *            of the parent net is needed as edges sometimes are not part of
	 *            a net
	 * @return an implementation of OryxHandler
	 */
	OryxHandler createOryxConverter(BasicEdge flowShape, BasicShape netShape);

}
