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

package de.hpi.layouting.model;

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
