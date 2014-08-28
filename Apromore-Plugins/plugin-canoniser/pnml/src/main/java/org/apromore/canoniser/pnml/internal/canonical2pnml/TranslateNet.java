/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.canoniser.pnml.internal.canonical2pnml;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.apromore.anf.AnnotationsType;
import org.apromore.cpf.ANDJoinType;
import org.apromore.cpf.ANDSplitType;
import org.apromore.cpf.CancellationRefType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.EventType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ORJoinType;
import org.apromore.cpf.ORSplitType;
import org.apromore.cpf.RoutingType;
import org.apromore.cpf.StateType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.WorkType;
import org.apromore.cpf.XORJoinType;
import org.apromore.cpf.XORSplitType;
import org.apromore.pnml.ArcType;
import org.apromore.pnml.ArcTypeType;
import org.apromore.pnml.PlaceType;
import org.apromore.pnml.TransitionType;

public class TranslateNet {

    static final private Logger LOGGER = Logger.getLogger(TranslateNet.class.getCanonicalName());

    DataHandler data;
    long ids;
    TranslateNode tn = new TranslateNode();
    TranslateEdge te = new TranslateEdge();
    TranslateOperators to = new TranslateOperators();
    TranslateToolspecifc tt = new TranslateToolspecifc();
    TranslateOriginalIDS moids = new TranslateOriginalIDS();
    AnnotationsType annotations;

    public void setValues(DataHandler data, long ids, AnnotationsType annotations) {
        this.data = data;
        this.ids = ids;
        this.annotations = annotations;
    }

    public void translateNet(NetType net) {
        for (NodeType node : net.getNode()) {
            if (node instanceof WorkType) {
                if (node instanceof TaskType || node instanceof EventType)  {
                    tn.setValues(data, ids);
                    if (node instanceof TaskType) {
                        tn.translateTask((TaskType) node);
                    } else {
                        tn.translateEvent(node);
                    }
                    ids = tn.getIds();
                }

            } else if (node instanceof RoutingType) {
                if (node instanceof StateType) {
                    tn.setValues(data, ids);
                    tn.translateState(node);
                    ids = tn.getIds();

                } else if (node instanceof ANDJoinType || node instanceof ANDSplitType || node instanceof XORJoinType
                        || node instanceof XORSplitType) {
                    to.setValues(data, ids);
                    to.translate(node);
                    ids = to.getIds();

                } else if (node instanceof ORJoinType || node instanceof ORSplitType) {
                    LOGGER.warning("Node " + node.getId() + " was inclusive OR; treated as XOR instead");
                    to.setValues(data, ids);
                    to.translate(node);
                    ids = to.getIds();
                    
                } else {
                    throw new RuntimeException("Unsupported routing type for node " + node.getId() + ": " + node.getClass());
                }
            }

            data.put_nodeRefMap(node.getId(), node);
        }

        for (EdgeType edge : net.getEdge()) {
            assert edge != null;

            te.setValues(data, ids);
            te.translateArc(edge);
            ids = te.getIds();

            data.put_edgeRefMap(edge.getId(), edge);
        }

        tt.setValues(data, ids);
        tt.translate(annotations);
        ids = tt.getIds();
        moids.setValues(data, ids);
        moids.mapIDS();

        // Second pass through the nodes, to translate CPF cancellation sets into PNML reset arcs
        for (NodeType node : net.getNode()) {
            if (node instanceof WorkType) {
                WorkType work = (WorkType) node;
                for (CancellationRefType cancellationRef: work.getCancelNodeId()) {
                    //LOGGER.info("Work node " + work.getId() + " cancels node " + cancellationRef.getRefId());
                    NodeType cpfNode = data.nodeRefMap.get(cancellationRef.getRefId());
                    assert cpfNode != null: "Unable to find CPF node for id " + cancellationRef.getRefId();
                    org.apromore.pnml.NodeType cancelled = data.getEndNodeMap().get(cpfNode);
                    if (cancelled != null) {
                        //String cancellingId = data.get_id_map_value(work.getId());
                        //LOGGER.info("Transition " + cancellingId + " cancelled node " + cancelled.getId());
                        assert data.getEndNodeMap().containsKey(work):
                            "CPF Work node " + work.getId() + " doesn't have an ending PNML element";
                        assert data.getEndNodeMap().get(work) instanceof TransitionType:
                            "CPF Work node " + work.getId() + " ended by non-transition PNML element " + data.getEndNodeMap().get(work).getId();
                        TransitionType cancelling = (TransitionType) data.getEndNodeMap().get(work);

                        // Put a reset arc between the cancelling node and every place from the cancelled node to the preceding fork
                        //LOGGER.info("Processing places reset by transition " + cancelling.getId());
                        Set<org.apromore.pnml.NodeType> visitedNodes = new HashSet<>();
                        org.apromore.pnml.NodeType currentNode = cancelled;
                        label1: while (true) {
                            visitedNodes.add(currentNode);
                            if (currentNode instanceof PlaceType) {
                                // Add a reset arc from the cancelling node to the currentNode
                                //LOGGER.info("  Adding reset arc from " + cancelling.getId() + " to " + currentNode.getId());
                                ArcType resetArc = new ArcType();
                                resetArc.setId(String.valueOf(ids++));
                                resetArc.setSource(currentNode);
                                resetArc.setTarget(cancelling);

                                ArcTypeType resetArcType = new ArcTypeType();
                                resetArcType.setText("reset");
                                resetArc.setType(resetArcType);

                                data.getNet().getArc().add(resetArc);
                            }

                            // Look for a unique incoming arc
                            ArcType incomingArc = null;
                            for (ArcType arc: data.getNet().getArc()) {
                                if (arc.getType() != null) {  // only interested in regular arcs, not reset/inhibit/etc
                                    continue;
                                }
                                if (currentNode.equals(arc.getTarget())) {
                                    if (incomingArc == null) {
                                        incomingArc = arc;
                                    }
                                    else {
                                        // More than one incoming arc -- we've arrived at a join.  Done!
                                        //LOGGER.info("  Breaking because " + currentNode.getId() + " is a join");
                                        break label1;
                                    }
                                }
                            }
                            // Assert: incomingArc is the unique incoming arc

                            // Traverse back along the branch
                            //LOGGER.info("  Traversing back along arc " + incomingArc.getId() + " from " + currentNode.getId() + " to " + ((org.apromore.pnml.NodeType) incomingArc.getSource()).getId());
                            currentNode = (org.apromore.pnml.NodeType) incomingArc.getSource();
                            if (visitedNodes.contains(currentNode)) {
                                //LOGGER.info("  Breaking because looped back to " + currentNode.getId());
                                break label1;
                            }

                            // Check for any other outgoing arcs from currentNode
                            ArcType outgoingArc = null;
                            for (ArcType arc: data.getNet().getArc()) {
                                if (arc.getType() != null) {  // only interested in regular arcs, not reset/inhibit/etc
                                    continue;
                                }
                                if (currentNode.equals(arc.getSource())) {
                                    
                                    if (outgoingArc == null) {
                                        outgoingArc = arc;
                                    }
                                    else {
                                        // More than one outgoing arc -- we've arrived at a split.  Done!
                                        //LOGGER.info("  Breaking because " + currentNode.getId() + " is a split");
                                        break label1;
                                    }
                                }
                            }
                            // Assert: outgoingArc is the unique outgoing arc
                            assert outgoingArc.equals(incomingArc);
                        }
                        //LOGGER.info("Processed places reset by transition " + cancelling.getId());
                    }
                }
                for (CancellationRefType cancellationRef: work.getCancelEdgeId()) {
                    // not yet implemented
                }
            }
        }
    }

    public long getIds() {
        return ids;
    }
}
