
package de.unihannover.se.infocup2008.bpmn.model;

/*-
 * #%L
 * Signavio Core Components
 * %%
 * Copyright (C) 2006 - 2020 The University of Melbourne.
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 * #L%
 */

import de.hpi.layouting.model.LayoutingAbstractDiagram;
import de.hpi.layouting.model.LayoutingElement;

import java.util.LinkedList;
import java.util.List;

public abstract class BPMNAbstractDiagram extends LayoutingAbstractDiagram<LayoutingElement> implements BPMNDiagram {

    /* (non-Javadoc)
      * @see de.unihannover.se.infocup2008.bpmn.model.BPMNDiagram#getStartEvents()
      */
    public List<LayoutingElement> getStartEvents() {
        List<LayoutingElement> resultList = new LinkedList<LayoutingElement>();

        for (LayoutingElement element : getElements().values()) {
            if (BPMNType.isAStartEvent(element.getType())) {
                resultList.add((LayoutingElement) element);
            }
        }

        return resultList;
    }

    /* (non-Javadoc)
      * @see de.unihannover.se.infocup2008.bpmn.model.BPMNDiagram#getConnectingElements()
      */
    public List<LayoutingElement> getConnectingElements() {
        List<LayoutingElement> resultList = new LinkedList<LayoutingElement>();

        for (LayoutingElement element : getElements().values()) {
            if (BPMNType.isAConnectingElement(element.getType())) {
                resultList.add((LayoutingElement) element);
            }
        }

        return resultList;
    }

    /* (non-Javadoc)
      * @see de.unihannover.se.infocup2008.bpmn.model.BPMNDiagram#getGateways()
      */
    public List<LayoutingElement> getGateways() {
        List<LayoutingElement> resultList = new LinkedList<LayoutingElement>();

        for (LayoutingElement element : getElements().values()) {
            if (BPMNType.isAGateWay(element.getType())) {
                resultList.add((LayoutingElement) element);
            }
        }

        return resultList;
    }

}
