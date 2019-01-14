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


/**
 * This class represents a found backwards edge in the
 * <code>TopologicalSorter</code>
 *
 * @author Team Royal Fawn
 */
public class BackwardsEdge {

    private String source;
    private String target;
    private LayoutingElement edge = null;

    public BackwardsEdge(String source, String target) {
        this.source = source;
        this.target = target;
    }

    public LayoutingElement getEdge() {
        return edge;
    }

    public void setEdge(LayoutingElement edge) {
        this.edge = edge;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String toString() {
        return source + " -> " + target;
    }
}
