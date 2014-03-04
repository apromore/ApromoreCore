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

public class TranslatePetriNet {

    final static Logger LOGGER = Logger.getLogger(TranslatePetriNet.class.getCanonicalName());

    TranslateOperations            to  = new TranslateOperations();
    TranslateArc                   ta  = new TranslateArc();
    TranslatePlace                 tts = new TranslatePlace();
    TranslateTransition            tt  = new TranslateTransition();
    TranslateTransitionToolspecifc ttt = new TranslateTransitionToolspecifc();
    TranslateArcToolspecific       tat = new TranslateArcToolspecific();
    TranslateNodeAnnotations       tna = new TranslateNodeAnnotations();
    TranslateEdgeAnnotations       tea = new TranslateEdgeAnnotations();

    private DataHandler data;
    private long        ids;

    public void setValues(final DataHandler data, final long ids) {
        this.data = data;
        this.ids = ids;
    }

    public void translatePetriNet(final NetType pnet) throws CanoniserException {

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

        RemoveDuplicateListItems removeDuplicateListItems = new RemoveDuplicateListItems();
        removeDuplicateListItems.transform(data.gettargetvalues());
        removeDuplicateListItems.transform(data.getsourcevalues());

        // Process Place
        for (PlaceType place : pnet.getPlace()) {
            if (data.gettargetvalues().contains(place.getId()) || data.getsourcevalues().contains(place.getId())) {
                // place has arcs
                if (!data.getinput().contains(place.getId()) && data.getoutput().contains(place.getId())) {
                    // start of process (only outgoing arcs)
                    data.setOutputnode(String.valueOf(place.getId()));
                    tts.setValues(data, ids);
                    tts.translateOutput(place);
                    addNodeAnnotations(place);

                } else if (data.getinput().contains(place.getId()) && !data.getoutput().contains(place.getId())) {
                    // end of process (only incoming arcs)
                    data.setInputnode(String.valueOf(place.getId()));
                    tts.setValues(data, ids);
                    tts.translateInput(place);
                    addNodeAnnotations(place);

                } else {
                    // internal to process (both incoming and outgoing arcs)
                    tts.setValues(data, ids);
                    tts.translateState(place);
                    addNodeAnnotations(place);
                }

            } else {
                // isolated place (no incoming or outgoing arcs)
                tts.setValues(data, ids);
                tts.translateEvent(place);
                addNodeAnnotations(place);
            }

            ids = tts.getIds();
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

                            to.setValues(data, ids);
                            to.translateOperation(null, transition);
                            addNodeAnnotations(transition);
                            ids = to.getIds();

                        } else {
                            if (obj2.getOperator() != null) {
                                to.setValues(data, ids);
                                to.translateOperation((TransitionToolspecificType) obj2, transition);
                                ids = to.getIds();
                                addNodeAnnotations(transition);

                            } else {
                                tt.setValues(data, ids);
                                tt.translateTask(transition);
                                addNodeAnnotations(transition);
                                ids = tt.getIds();
                            }
                        }
                    }

                } else {
                    to.setValues(data, ids);
                    to.translateOperation(null, transition);
                    addNodeAnnotations(transition);
                    ids = to.getIds();
                }
            } else {
                tt.setValues(data, ids);
                tt.translateTask(transition);
                addNodeAnnotations(transition);
                ids = tt.getIds();
            }

            ttt.setValues(data, ids);
            ttt.translate(transition);
            ids = ttt.getIds();
        }

        // Process Arc, ArcToolspecific
        for (ArcType arc : pnet.getArc()) {
            ta.setValues(data, ids);
            ta.translateEdge(arc);
            addEdgeAnnotations(arc);
            ids = ta.getIds();

            tat.setValues(data, ids);
            tat.translate(arc);
            ids = tat.getIds();
        }

        // Populate HumanResources
        for (NetToolspecificType toolspecific : pnet.getToolspecific()) {
            TranslateHumanResources thr = new TranslateHumanResources();
            thr.setValues(data, ids);
            thr.translate(pnet);
            ids = thr.getIds();
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
                    cancel(cancellingNode, findCpfNodeById(cancellationRef.getRefId()), 0, emptySet, emptySet);
                }
            }
        }
    }

    // TODO: replace this with a hashcode lookup to improve on the current linear complexity
    private org.apromore.cpf.NodeType findCpfNodeById(final String cpfId) {
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
    private void cancel(final WorkType                  cancellingNode,
                        final org.apromore.cpf.NodeType node,
                        int                             distance,
                        final Set<CancellationRefType>  cancelledNodes,
                        final Set<CancellationRefType>  cancelledEdges) {

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

                    org.apromore.cpf.NodeType newNode = findCpfNodeById(edge.getTargetId());

                    Set<CancellationRefType> newCancelledNodes = new HashSet(cancelledNodes);
                    CancellationRefType nodeCancellationRef = CpfObjectFactory.getInstance().createCancellationRefType();
                    nodeCancellationRef.setRefId(newNode.getId());
                    newCancelledNodes.add(nodeCancellationRef);

                    if (newNode instanceof WorkType) { distance++; };
                    cancel(cancellingNode, newNode, distance, newCancelledNodes, newCancelledEdges);
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

    private void addNodeAnnotations(Object obj) {
        tna.setValues(data);
        tna.addNodeAnnotations(obj);
    }

    private void addEdgeAnnotations(Object obj) {
        tea.setValues(data);
        tea.addEdgeAnnotations(obj);
    }

    public long getIds() {
        return ids;
    }

}
