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
package org.apromore.common.converters.pnml.handler;

import org.apromore.common.converters.pnml.context.PNMLConversionContext;
import org.apromore.common.converters.pnml.handler.impl.TypeFlowHandler;
import org.apromore.common.converters.pnml.handler.impl.TypePlaceHandler;
import org.apromore.common.converters.pnml.handler.impl.TypeTransitionHandler;
import org.jbpt.petri.Flow;
import org.jbpt.petri.Marking;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;

public class PNMLHandlerFactory {

    private final PNMLConversionContext context;

    public PNMLHandlerFactory(final PNMLConversionContext context) {
        this.context = context;
    }

    public PNMLHandler createNodeConverter(final Object obj, final Marking marking) {
        if (obj instanceof Transition) {
            return new TypeTransitionHandler(context, (Transition) obj);
        } else if (obj instanceof Place) {
            return new TypePlaceHandler(context, (Place) obj, marking);
        }
        return null;
    }

    public PNMLHandler createEdgeConverter(final Object obj) {
        if (obj instanceof Flow) {
            return new TypeFlowHandler(context, (Flow) obj);
        }
        return null;
    }

}
