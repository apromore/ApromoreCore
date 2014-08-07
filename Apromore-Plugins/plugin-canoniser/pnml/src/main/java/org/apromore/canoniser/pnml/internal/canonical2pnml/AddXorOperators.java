/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of “Apromore”.
 *
 * “Apromore” is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * “Apromore” is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.canoniser.pnml.internal.canonical2pnml;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.pnml.ArcToolspecificType;
import org.apromore.pnml.ArcType;
import org.apromore.pnml.TransitionToolspecificType;
import org.apromore.pnml.TransitionType;

public class AddXorOperators {

    static final private Logger LOGGER = Logger.getLogger(AddXorOperators.class.getCanonicalName());

    DataHandler data;
    long ids;
    TransitionType trandouble;
    CanonicalProcessType cproc;
    List<ArcType> arcs;

    public void setValues(DataHandler data, long ids) {
        this.data = data;
        this.ids = ids;
    }

    public void add(CanonicalProcessType cproc) {
        this.cproc = cproc;
        for (NetType net : cproc.getNet()) {
            for (NodeType node : data.getxorconnectors()) {
                int splitcount = 0;
                int joincount = 0;
                if (data.get_dupjoinMap().containsKey(node.getId())) {
                    for (EdgeType edge : net.getEdge()) {
                        if (edge.getTargetId().equals(node.getId())) {
                            joincount++;
                        }
                    }
                    for (int i = 1; i < joincount; i++) {
                        TransitionType tran = new TransitionType();
                        TransitionType oldtran = data.get_dupjoinMap_value(node.getId());
                        //String id = node.getOriginalID().replace("op_1",
                        //        "op_" + String.valueOf(i + 1));
                        String id = node.getId() + "_join_source_" + String.valueOf(i);
                        tran.setId(id);
                        tran.setName(oldtran.getName());
                        tran.setGraphics(oldtran.getGraphics());
                        for (TransitionToolspecificType oldttt : oldtran
                                .getToolspecific()) {
                            tran.getToolspecific().add(oldttt);
                        }
                        data.getNet().getTransition().add(tran);
                        if (i == 1) {
                            arcs = new LinkedList<ArcType>();
                        }
                        ArcType newarc = null;
                        for (ArcType arc : data.getNet().getArc()) {
                            if (arc.getSource().equals(oldtran)) {
                                newarc = new ArcType();
                                newarc.setId(arc.getId() + "_" + i);
                                newarc.setSource(tran);
                                newarc.setTarget(arc.getTarget());
                                newarc.setInscription(arc.getInscription());
                                newarc.setGraphics(arc.getGraphics());
                                for (ArcToolspecificType oldatt : arc
                                        .getToolspecific()) {
                                    newarc.getToolspecific().add(oldatt);
                                }

                            } else if (arc.getTarget().equals(oldtran)) {
                                if (i == 1) {
                                    arcs.add(arc);
                                }
                            }
                        }
                        if (newarc != null) {
                            data.getNet().getArc().add(newarc);
                        }
                        Object[] changearcs = arcs.toArray();
                        ArcType update = (ArcType) changearcs[i];
                        update.setTarget(tran);

                    }
                } else if (data.get_dupsplitMap().containsKey(node.getId())) {
                    for (EdgeType edge : net.getEdge()) {
                        if (edge.getSourceId().equals(node.getId())) {
                            splitcount++;
                        }
                    }

                    for (int i = 1; i < splitcount; i++) {
                        TransitionType tran = new TransitionType();
                        TransitionType oldtran = data.get_dupsplitMap_value(node.getId());
                        //String id = node.getOriginalID().replace("op_1",
                        //        "op_" + String.valueOf(i + 1));
                        String id = node.getId() + "_split_target_" + String.valueOf(i);
                        tran.setId(id);
                        tran.setName(oldtran.getName());
                        tran.setGraphics(oldtran.getGraphics());
                        for (TransitionToolspecificType oldttt : oldtran
                                .getToolspecific()) {
                            tran.getToolspecific().add(oldttt);
                        }
                        data.getNet().getTransition().add(tran);
                        if (i == 1) {
                            arcs = new LinkedList<ArcType>();
                        }
                        ArcType newarc = null;
                        for (ArcType arc : data.getNet().getArc()) {
                            if (arc.getTarget().equals(oldtran)) {
                                newarc = new ArcType();
                                newarc.setId(arc.getId() + "_" + i);
                                newarc.setSource(arc.getSource());
                                newarc.setTarget(tran);
                                newarc.setInscription(arc.getInscription());
                                newarc.setGraphics(arc.getGraphics());
                                for (ArcToolspecificType oldatt : arc
                                        .getToolspecific()) {
                                    newarc.getToolspecific().add(oldatt);
                                }

                            } else if (arc.getSource().equals(oldtran)) {
                                if (i == 1) {
                                    arcs.add(arc);
                                }
                            }
                        }
                        if (newarc != null) {
                            data.getNet().getArc().add(newarc);
                        }
                        Object[] changearcs = arcs.toArray();
                        ArcType update = (ArcType) changearcs[i];
                        update.setSource(tran);

                    }
                }

            }
        }
    }

    public long getIds() {
        return ids;
    }

    public CanonicalProcessType getCanonicalProcess() {
        return cproc;
    }
}
