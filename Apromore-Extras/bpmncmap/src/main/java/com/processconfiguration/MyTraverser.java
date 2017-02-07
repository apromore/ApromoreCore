/*
 * Copyright © 2009-2017 The Apromore Initiative.
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

package com.processconfiguration;

import java.util.HashSet;
import java.util.Set;

import com.processconfiguration.Configurable;
import org.omg.spec.bpmn._20100524.model.DepthFirstTraverserImpl;
import org.omg.spec.bpmn._20100524.model.TDataOutputAssociation;
import org.omg.spec.bpmn._20100524.model.TMessageFlow;
import org.omg.spec.bpmn._20100524.model.Visitor;

/**
 * Workaround for looping traversal of the auto-generated {@link DepthFirstTraverserImpl}.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class MyTraverser extends DepthFirstTraverserImpl {

    /** The set of previously traversed BPMN data output associations. */
    Set<TDataOutputAssociation> traversedSet = new HashSet<>();

    @Override public void traverse(TDataOutputAssociation aBean, Visitor aVisitor) {
        if (traversedSet.contains(aBean)) { return; }  // break the loop between TDataOutputAssociation@sourceRef and TTask@dataOutputAssociation

        traversedSet.add(aBean);
        super.traverse(aBean, aVisitor);
    }

    @Override public void traverse(Configurable.Configuration aBean, Visitor aVisitor) {
        return;  // avoid traversing the references within configuration elements
    }
}





