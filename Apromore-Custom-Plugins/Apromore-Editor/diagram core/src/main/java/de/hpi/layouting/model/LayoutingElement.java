
package de.hpi.layouting.model;

/*-
 * #%L
 * Signavio Core Components
 * %%
 * Copyright (C) 2006 - 2020 Philipp Berger, Martin Czuchra, Gero Decker,
 * Ole Eckermann, Lutz Gericke,
 * Alexander Hold, Alexander Koglin, Oliver Kopp, Stefan Krumnow,
 * Matthias Kunze, Philipp Maschke, Falko Menge, Christoph Neijenhuis,
 * Hagen Overdick, Zhen Peng, Nicolas Peters, Kerstin Pfitzner, Daniel Polak,
 * Steffen Ryll, Kai Schlichting, Jan-Felix Schwarz, Daniel Taschik,
 * Willi Tscheschner, Björn Wagner, Sven Wagner-Boysen, Matthias Weidlich
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
 * 
 * 
 * 
 * Ext JS (http://extjs.com/) is used under the terms of the Open Source LGPL 3.0
 * license.
 * The license and the source files can be found in our SVN repository at:
 * http://oryx-editor.googlecode.com/.
 * #L%
 */

import java.util.List;

public interface LayoutingElement {

    public abstract String getId();

    public abstract void setId(String id);

    public abstract String getType();

    public abstract void setType(String type);

    public abstract List<LayoutingElement> getOutgoingLinks();

    public abstract void setOutgoingLinks(List<LayoutingElement> outgoingLinks);

    public abstract void addOutgoingLink(LayoutingElement element);

    public abstract void removeOutgoingLink(LayoutingElement element);

    public abstract List<LayoutingElement> getIncomingLinks();

    public abstract void setIncomingLinks(List<LayoutingElement> incomingLinks);

    public abstract void addIncomingLink(LayoutingElement element);

    public abstract void removeIncomingLink(LayoutingElement element);

    public abstract List<LayoutingElement> getFollowingElements();

    public abstract List<LayoutingElement> getPrecedingElements();

    public abstract String toString();

    public abstract LayoutingBounds getGeometry();

    public abstract void setGeometry(LayoutingBounds geometry);

    public abstract LayoutingDockers getDockers();

    public abstract void setDockers(LayoutingDockers dockers);

    /**
     * Indicates if the element has a parent other than the canvas
     *
     * @return <code>true</code> if the element has a parent other than the
     *         canvas e.g. a lane
     */
    public abstract boolean hasParent();

    public abstract void setParent(LayoutingElement element);

    public abstract LayoutingElement getParent();

    /**
     * @return true if Element joins more then one path
     */
    public abstract boolean isJoin();

    /**
     * @return true if Element has more then one following Element
     */
    public abstract boolean isSplit();

    /**
     * Searches <code>other</code> in forward direction
     *
     * @param other LayoutingElement to search for
     * @return number of elements between <code>other</code> and
     *         <code>this</code> or <code>Integer.MAX_VALUE</code> if the
     *         elements are not connected in forward direction
     */
    public abstract int forwardDistanceTo(LayoutingElement other);

    /**
     * Searches <code>other</code> in backward direction
     *
     * @param other LayoutingElement to search for
     * @return number of elements between <code>this</code> and
     *         <code>other</code> or <code>Integer.MAX_VALUE</code> if the
     *         elements are not connected in backward direction
     */
    public abstract int backwardDistanceTo(LayoutingElement other);

    /**
     * @return the closest split with the same parent before <code>this</code>.
     *         Is never <code>this</code> on cycle-free diagramms but maybe
     *         <code>null</code>
     */
    public abstract LayoutingElement prevSplit();
}
