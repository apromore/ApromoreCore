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
package de.hpi.layouting.topologicalsort;

import de.hpi.layouting.model.LayoutingElement;

import java.util.SortedSet;
import java.util.TreeSet;


/**
 * A simplified view on a <tt>LayoutingElement</tt>, suitable for topological sorting.
 *
 * @author Team Royal Fawn
 */
public class SortableLayoutingElement {

    private LayoutingElement element;
    private SortedSet<String> outgoingLinks;
    private SortedSet<String> incomingLinks;
    private boolean join;
    private int oldInCount;

    public SortableLayoutingElement(LayoutingElement element) {
        this.element = element;
        this.incomingLinks = new TreeSet<String>();
        this.outgoingLinks = new TreeSet<String>();
        for (LayoutingElement pre : element.getPrecedingElements()) {
            // String type = pre.getType();
            // if(!BPMNType.isASwimlane(type)){
            incomingLinks.add(pre.getId());
            // }
        }
        for (LayoutingElement post : element.getFollowingElements()) {
            outgoingLinks.add(post.getId());
        }
        this.join = element.isJoin();
        this.oldInCount = incomingLinks.size();
    }

    public LayoutingElement getLayoutingElement() {
        return this.element;
    }

    public String getId() {
        return this.element.getId();
    }

    public String getType() {
        return this.element.getType();
    }

    public boolean isFree() {
        return this.incomingLinks.isEmpty();
    }

    public boolean isJoin() {
        return join;
    }

    public int getOldInCount() {
        return oldInCount;
    }

    public SortedSet<String> getOutgoingLinks() {
        return this.outgoingLinks;
    }

    public SortedSet<String> getIncomingLinks() {
        return this.incomingLinks;
    }

    public void removeIncomingLinkFrom(String id) {
        incomingLinks.remove(id);
    }

    public void reverseIncomingLinkFrom(String id) {
        removeIncomingLinkFrom(id);
        outgoingLinks.add(id);
    }

    public void reverseOutgoingLinkTo(String id) {
        outgoingLinks.remove(id);
        incomingLinks.add(id);
    }
}
