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
package org.apromore.common.converters.pnml.handler.impl;

import org.apromore.common.converters.pnml.context.PNMLConversionContext;
import org.apromore.pnml.ArcType;
import org.apromore.pnml.GraphicsArcType;
import org.apromore.pnml.NodeType;
import org.apromore.pnml.PositionType;
import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Point;
import org.oryxeditor.server.diagram.basic.BasicEdge;
import org.oryxeditor.server.diagram.basic.BasicShape;
import sun.net.www.content.audio.basic;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TypeArcHandler extends PNMLHandlerImpl {

    private final ArcType arc;

    public TypeArcHandler(PNMLConversionContext context, ArcType arc) {
        super(context);
        this.arc = arc;
    }

    @Override
    public BasicEdge convert() {
        return createControlFlowShape(arc);
    }


    private BasicEdge createControlFlowShape(ArcType typeFlow) {
        BasicEdge basicEdge = new BasicEdge(arc.getId(), "Arc");
        connectEdge(basicEdge, (NodeType) typeFlow.getSource(), (NodeType) typeFlow.getTarget());
        return basicEdge;
    }

    private void connectEdge(BasicEdge basicEdge, NodeType source, NodeType target) {
        BasicShape incomingShape = getContext().getShape(source.getId());
        basicEdge.connectToASource(incomingShape);
        basicEdge.addDocker(deriveDockerFromShape(incomingShape));

        BasicShape outgoingShape = getContext().getShape(target.getId());
        basicEdge.connectToATarget(outgoingShape);
        basicEdge.addDocker(deriveDockerFromShape(outgoingShape));
    }

    private Point deriveDockerFromShape(BasicShape shape) {
        Bounds bounds = shape.getBounds();
        return bounds.getMiddle();
    }

}
