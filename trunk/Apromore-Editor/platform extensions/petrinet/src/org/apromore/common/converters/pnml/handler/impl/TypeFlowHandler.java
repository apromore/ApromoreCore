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
import org.jbpt.petri.Flow;
import org.oryxeditor.server.diagram.basic.BasicEdge;
import org.oryxeditor.server.diagram.basic.BasicShape;

import java.util.ArrayList;
import java.util.List;

public class TypeFlowHandler extends PNMLHandlerImpl {

    private final Flow arc;

    public TypeFlowHandler(PNMLConversionContext context, Flow arc) {
        super(context);
        this.arc = arc;
    }

    @Override
    public BasicEdge convert() {
        return createControlFlowShape(arc);
    }


    private BasicEdge createControlFlowShape(Flow typeFlow) {
        BasicEdge basicEdge = new BasicEdge(arc.getId(), "Arc");
        connectEdge(basicEdge, typeFlow.getSource().getId(), typeFlow.getTarget().getId());
        return basicEdge;
    }

    private void connectEdge(BasicEdge basicEdge, String source, String target) {
        List<BasicShape> outgoings = new ArrayList<>();
        BasicShape outgoingShape = getContext().getShape(target);
        if (outgoingShape != null) {
            outgoings.add(outgoingShape);
        }
        basicEdge.setOutgoingsAndUpdateTheirIncomings(outgoings);

        List<BasicShape> incomings = new ArrayList<>();
        BasicShape incomingShape = getContext().getShape(source);
        if (incomingShape != null) {
            incomings.add(incomingShape);
        }
        basicEdge.setIncomingsAndUpdateTheirOutgoings(incomings);
    }

}
