/*
 * Copyright © 2019 The University of Melbourne.
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

package org.apromore.plugin.processdiscoverer.impl.reachability;

import org.apromore.plugin.processdiscoverer.impl.Arc;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
import org.eclipse.collections.impl.set.mutable.primitive.IntHashSet;

import java.util.Set;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 05/08/2018.
 */
public class ReachabilityChecker {

	//Check reachability on a directed graph from the source (node=1) to a node by breadth-first search
    public boolean reachable(int node, Set<Arc> retained_arcs) {
        if(node == 1) return true;

        IntHashSet visited = new IntHashSet();
        IntArrayList reached = new IntArrayList(); // FIFO queue
        reached.add(1);

        while (reached.size() > 0) {
            int current = reached.removeAtIndex(0);
            for (Arc arc : retained_arcs) {
                if(arc.getSource() == current) {
                    if(arc.getTarget() == node) {
                        return true;
                    }else if(!visited.contains(arc.getTarget())) {
                        visited.add(arc.getTarget());
                        reached.add(arc.getTarget());
                    }
                }
            }
        }

        return false;
    }

    // Check reachability on a directed graph from a node to the sink (node=2) by breadth-first search
    public boolean reaching(int node, Set<Arc> retained_arcs) {
        if(node == 2) return true;

        IntHashSet visited = new IntHashSet();
        IntArrayList reached = new IntArrayList();
        reached.add(2);

        while (reached.size() > 0) {
            int current = reached.removeAtIndex(0);
            for (Arc arc : retained_arcs) {
                if(arc.getTarget() == current) {
                    if(arc.getSource() == node) {
                        return true;
                    }else if(!visited.contains(arc.getSource())) {
                        visited.add(arc.getSource());
                        reached.add(arc.getSource());
                    }
                }
            }
        }

        return false;
    }

}
