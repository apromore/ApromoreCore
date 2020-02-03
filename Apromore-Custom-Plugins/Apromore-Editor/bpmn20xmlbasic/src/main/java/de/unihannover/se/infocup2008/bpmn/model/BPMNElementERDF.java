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
package de.unihannover.se.infocup2008.bpmn.model;

import de.hpi.layouting.model.LayoutingDockers.Point;
import org.w3c.dom.Node;

/**
 * Implements the <code>BPMNElement</code> Interface.
 *
 * @author Team Royal Fawn
 */
public class BPMNElementERDF extends BPMNAbstractElement implements BPMNElement {
    private Node dockersNode = null;
    protected Node boundsNode = null;

    public void updateDataModel() {
        this.boundsNode.setNodeValue(geometry.getX() + "," + geometry.getY()
                + "," + geometry.getX2() + "," + geometry.getY2());
        StringBuilder dockerSB = new StringBuilder();
        for (Point p : dockers.getPoints()) {
            dockerSB.append(p.x);
            dockerSB.append(" ");
            dockerSB.append(p.y);
            dockerSB.append(" ");
        }
        dockerSB.append(" # ");
        dockersNode.setNodeValue(dockerSB.toString());
    }

    public Node getDockersNode() {
        return this.dockersNode;
    }

    public void setDockersNode(Node node) {
        this.dockersNode = node;
    }

    public Node getBoundsNode() {
        return boundsNode;
    }

    public void setBoundsNode(Node node) {
        this.boundsNode = node;
    }

}
