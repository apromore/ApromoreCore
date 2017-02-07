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

package org.apromore.canoniser.pnml.internal.pnml2canonical;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.CancellationRefType;
import org.apromore.cpf.CpfObjectFactory;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.WorkType;
import org.apromore.pnml.ArcType;
import org.apromore.pnml.NetToolspecificType;
import org.apromore.pnml.NetType;
import org.apromore.pnml.NodeType;
import org.apromore.pnml.PlaceType;
import org.apromore.pnml.TransitionToolspecificType;
import org.apromore.pnml.TransitionType;

public abstract class TranslatePetriNet {

    final static Logger LOGGER = Logger.getLogger(TranslatePetriNet.class.getCanonicalName());

    static public void translatePetriNet(final NetType pnet, final DataHandler data) throws CanoniserException {

        // Populate "data" based on PNML arc sources and targets
        for (ArcType arc : pnet.getArc()) {
            if (arc.getType() == null || !"reset".equals(arc.getType().getText())) {
                String source = String.valueOf(((NodeType) arc.getSource()).getId());
                data.addtargetvalues(source);
                data.addinput(source);

                String target = String.valueOf(((NodeType) arc.getTarget()).getId());
                data.addsourcevalues(target);
                data.addoutput(target);
            }
        }

        RemoveDuplicateListItems.transform(data.gettargetvalues());
        RemoveDuplicateListItems.transform(data.getsourcevalues());

        // Process Place
        for (PlaceType place : pnet.getPlace()) {
            boolean hasMultipleIncomingArcs = data.getsourcevalues().contains(place.getId());
            boolean hasMultipleOutgoingArcs = data.gettargetvalues().contains(place.getId());

            if (hasMultipleIncomingArcs || hasMultipleOutgoingArcs) {  // place has arcs
                boolean hasOutgoingArc = data.getinput().contains(place.getId());
                boolean hasIncomingArc = data.getoutput().contains(place.getId());

                if (!hasOutgoingArc && hasIncomingArc) {  // start of process (only outgoing arcs)
                    data.setOutputnode(String.valueOf(place.getId()));
                    TranslatePlace.translateOutput(place, data);

                } else if (hasOutgoingArc && !hasIncomingArc) {  // end of process (only incoming arcs)
                    data.setInputnode(String.valueOf(place.getId()));
                    TranslatePlace.translateInput(place, data);

                } else {  // internal to process (both incoming and outgoing arcs)
                    TranslatePlace.translateState(place, data);
                }

            } else {  // isolated place (no incoming or outgoing arcs)
                TranslatePlace.translateEvent(place, data);
            }

            addNodeAnnotations(place, data);
        }

        // Process Operation, Transition, TransitionToolspecifc
        for (TransitionType transition : pnet.getTransition()) {
            if (transition.getToolspecific().size() >= 1 ||
                (data.gettargetvalues().contains(transition.getId()) ||
                 data.getsourcevalues().contains(transition.getId()))) {

                if (transition.getToolspecific().size() >= 1) {
                    for (TransitionToolspecificType obj2 : transition.getToolspecific()) {
                        if ((data.gettargetvalues().contains(transition.getId()) ||
                             data.getsourcevalues().contains(transition.getId())) &&
                            obj2.getOperator() == null) {

                            TranslateOperations.translateOperation(null, transition, data);
                            addNodeAnnotations(transition, data);

                        } else {
                            if (obj2.getOperator() != null) {
                                TranslateOperations.translateOperation((TransitionToolspecificType) obj2, transition, data);
                                addNodeAnnotations(transition, data);

                            } else {
                                TranslateTransition.translateTask(transition, data);
                                addNodeAnnotations(transition, data);
                            }
                        }
                    }

                } else {
                    TranslateOperations.translateOperation(null, transition, data);
                    addNodeAnnotations(transition, data);
                }
            } else {
                TranslateTransition.translateTask(transition, data);
                addNodeAnnotations(transition, data);
            }

            TranslateTransitionToolspecific.translate(transition, data);
        }

        // Process Arc, ArcToolspecific
        for (ArcType arc : pnet.getArc()) {
            TranslateArc.translateEdge(arc, data);
            addEdgeAnnotations(arc, data);

            TranslateArcToolspecific.translate(arc, data);
        }

        // Populate HumanResources
        for (NetToolspecificType toolspecific : pnet.getToolspecific()) {
            TranslateHumanResources.translate(pnet, data);
        }

        if (!data.getOutputnode().equals("end")) {
            data.getNet().getEdge().add(data.getOutputEdge());
            data.setOutputnode("end");
        }
        if (!data.getInputnode().equals("start")) {
            data.getNet().getEdge().add(data.getInputEdge());
            data.setInputnode("start");
        }

        // Cancel CPF elements corresponding to the arcs and transitions between resetted places
        for (org.apromore.cpf.NodeType node: data.getNet().getNode()) {
            if (node instanceof WorkType) {
                WorkType cancellingNode = (WorkType) node;
                for (CancellationRefType cancellationRef: new ArrayList<CancellationRefType>(cancellingNode.getCancelNodeId())) {
                    Set<CancellationRefType> emptySet = Collections.emptySet();
                    cancel(cancellingNode, findCpfNodeById(cancellationRef.getRefId(), data), 0, emptySet, emptySet, data);
                }
            }
        }
    }

    /**
     * Dump the CPF net component of <var>data</var> in XML format to {@link System#err}.
     *
     * This is intended for debugging.
     *
     * @param data
     */
    private static void dump(DataHandler data) {
        try {
            org.apromore.cpf.CanonicalProcessType cpf = new org.apromore.cpf.CanonicalProcessType();
            cpf.getNet().add(data.getNet());
            org.apromore.cpf.CPFSchema.marshalCanonicalFormat(System.err, cpf, false);
        } catch (javax.xml.bind.JAXBException | org.xml.sax.SAXException e) {
            e.printStackTrace();
        }
    }

    // TODO: replace this with a hashcode lookup to improve on the current linear complexity
    private static org.apromore.cpf.NodeType findCpfNodeById(final String cpfId, DataHandler data) {
        for (org.apromore.cpf.NodeType node: data.getNet().getNode()) {
            if (cpfId.equals(node.getId())) {
                return node;
            }
        }
        return null;
    }

    /**
     * Add a <var>node</var> to the cancellation set of another <var>cancellingNode</var>, and
     * recursively add neighboring edges and nodes between any adjacent cancelled node.
     *
     * @param cancellingNode
     * @param node  the cancelled node
     * @param distance
     * @param cancelledNodes
     * @param cancelledEdges
     */
    private static void cancel(final WorkType                  cancellingNode,
                        final org.apromore.cpf.NodeType node,
                        int                             distance,
                        final Set<CancellationRefType>  cancelledNodes,
                        final Set<CancellationRefType>  cancelledEdges,
                        final DataHandler               data) {

        switch (distance) {
        case 0:
        case 1:
            for (EdgeType edge: data.getNet().getEdge()) {
                if (node.getId().equals(edge.getSourceId())) {
                    Set<CancellationRefType> newCancelledEdges = new HashSet(cancelledEdges);
                    CancellationRefType edgeCancellationRef = CpfObjectFactory.getInstance().createCancellationRefType();
                    edgeCancellationRef.setRefId(edge.getId());
                    if (!newCancelledEdges.add(edgeCancellationRef)) {
                        LOGGER.warning("Detected cycle in graph traversal, aborting");
                        break;
                    }

                    org.apromore.cpf.NodeType newNode = findCpfNodeById(edge.getTargetId(), data);

                    Set<CancellationRefType> newCancelledNodes = new HashSet(cancelledNodes);
                    CancellationRefType nodeCancellationRef = CpfObjectFactory.getInstance().createCancellationRefType();
                    nodeCancellationRef.setRefId(newNode.getId());
                    newCancelledNodes.add(nodeCancellationRef);

                    if (newNode instanceof WorkType) { distance++; };
                    cancel(cancellingNode, newNode, distance, newCancelledNodes, newCancelledEdges, data);
                }
            }
            break;

        case 2:
            for (CancellationRefType cancellationRef: cancellingNode.getCancelNodeId()) {
                if (cancellationRef.getRefId().equals(node.getId())) {
                    cancellingNode.getCancelNodeId().removeAll(cancelledNodes);
                    cancellingNode.getCancelNodeId().addAll(cancelledNodes);
                    cancellingNode.getCancelEdgeId().removeAll(cancelledEdges);
                    cancellingNode.getCancelEdgeId().addAll(cancelledEdges);
                    break;
                }
            }
            break;
            
        default:
            throw new RuntimeException("Somehow exceeded search radius, distance=" + distance);
        }
    }

    static private void addNodeAnnotations(Object obj, DataHandler data) {
        TranslateNodeAnnotations.addNodeAnnotations(obj, data);
    }

    static private void addEdgeAnnotations(Object obj, DataHandler data) {
        TranslateEdgeAnnotations.addEdgeAnnotations(obj, data);
    }

}
