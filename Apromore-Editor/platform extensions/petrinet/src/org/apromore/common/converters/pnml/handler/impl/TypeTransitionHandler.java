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
import org.jbpt.petri.Transition;
import org.oryxeditor.server.diagram.basic.BasicNode;

import java.util.HashMap;
import java.util.Map;

public class TypeTransitionHandler extends NodeHandler {

    private final Transition transition;

    public TypeTransitionHandler(PNMLConversionContext context, Transition transition) {
        super(context);
        this.transition = transition;
    }

    @Override
    protected Map<String, String> convertProperties() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("title", transition.getName());
        hashMap.put("description", transition.getDescription());
        return hashMap;
    }

    @Override
    protected BasicNode createShape() {
        if (transition.getLabel() != null && !transition.getLabel().equals("")) {
            return new BasicNode(getShapeId(), "Transition");
        } else {
            return new BasicNode(getShapeId(), "VerticalEmptyTransition");
        }
    }

    /* (non-Javadoc)
      * @see org.apromore.common.converters.epml.impl.NodeConverter#getShapeId()
      */
    @Override
    protected String getShapeId() {
        return transition.getId();
    }
}
