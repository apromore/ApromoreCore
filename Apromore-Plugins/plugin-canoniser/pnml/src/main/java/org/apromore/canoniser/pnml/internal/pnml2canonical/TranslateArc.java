package org.apromore.canoniser.pnml.internal.pnml2canonical;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.apromore.cpf.ANDJoinType;
import org.apromore.cpf.CancellationRefType;
import org.apromore.cpf.CpfObjectFactory;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.WorkType;
import org.apromore.pnml.ArcType;
import org.apromore.pnml.ArcTypeType;
import org.apromore.pnml.NodeType;

public class TranslateArc {

    static private final Logger LOGGER = Logger.getLogger(TranslateArc.class.getCanonicalName());

    DataHandler data;
    long ids;

    public void setValues(DataHandler data, long ids) {
        this.data = data;
        this.ids = ids;
    }

    public void translateEdge(ArcType arc) {

        data.put_id_map(arc.getId(), String.valueOf(ids));

        String sourceId = ((NodeType) arc.getSource()).getId();

        // Convert sourceId from PNML to CPF
        if (data.getInputnode().equals(sourceId)) {
            sourceId = data.getInputEvent();
        } else if (data.get_andjoinmap().containsKey(sourceId)) {
            // the AND-join was inserted, so this outgoing edge ought to source from the transition/task
            sourceId = data.get_andjoinmap().get(sourceId);
        } else if (data.get_andsplitjoinmap().containsKey(sourceId)) {
            // the AND-join was inserted as was an AND-split, so this edge ought to source from the split
            sourceId = data.get_andsplitjoinmap().get(sourceId);
        } else {
            sourceId = getEdgeRealId(sourceId);
        }

        String targetId = ((NodeType) arc.getTarget()).getId();

        // Convert targetId from PNML to CPF
        if (data.getOutputnode().equals(targetId)) {
            targetId = getEdgeRealId(data.getOutputState());
        } else if (data.get_andsplitmap().containsKey(targetId)) {
            // the AND-split was inserted, so this incoming edge ought to target to the transition/task
            targetId = data.get_andsplitmap().get(targetId);
        } else {
            // even if an AND-join was inserted, its id would be the same as the original transition/task's
            targetId = getEdgeRealId(targetId);
        }

        ArcTypeType type = arc.getType();
        if (type != null && "reset".equals(type.getText())) {
            // This PNML arc corresponds to a CPF cancellation set element

            org.apromore.cpf.NodeType cancellingNode = findCpfNodeById(targetId);
            org.apromore.cpf.NodeType cancelledNode  = findCpfNodeById(sourceId);
            
            while (cancellingNode instanceof ANDJoinType) {
                // The resetted PNML transition had multiple incoming arcs, so this routing element was inserted
                // consequently we need to traverse the forward edge to find the actual cancelling node.
                Set<EdgeType> outgoingEdges = findCpfNodeOutgoingEdges(cancellingNode.getId());
                LOGGER.info("Outgoing edge count: " + outgoingEdges.size());
                for (EdgeType outgoing: outgoingEdges) {
                    LOGGER.info("Outgoing edge " + outgoing.getId());
                }
                for (EdgeType outgoing: outgoingEdges) {
                    LOGGER.info("Outgoing edge from " + cancellingNode.getName() + " to ");
                    cancellingNode = findCpfNodeById(outgoing.getTargetId());
                    LOGGER.info("...to " + cancellingNode.getName());
                }
            }

            if (!(cancellingNode instanceof WorkType)) {
                LOGGER.warning("Reset arc cannot be represented as cancellation set of " + cancellingNode.getName() + " class " + cancellingNode.getClass());
            }
            // Found the cancelledNode and cancellingNode, if they exist

            if (cancelledNode != null && cancellingNode != null) {
                CancellationRefType cancellationRef = CpfObjectFactory.getInstance().createCancellationRefType();
                cancellationRef.setRefId(cancelledNode.getId());
                ((WorkType) cancellingNode).getCancelNodeId().add(cancellationRef);
            }

        } else {
            // This PNML arc corresponds to a CPF edge
            EdgeType edge = new EdgeType();

            edge.setId(String.valueOf(ids++));
            edge.setOriginalID(arc.getId());
            edge.setSourceId(sourceId);
            edge.setTargetId(targetId);

            data.getNet().getEdge().add(edge);
        }
    }

    // TODO: keep track of this on the DataHandler rather than doing a linear search
    private Set<EdgeType> findCpfNodeOutgoingEdges(final String sourceId) {
        Set<EdgeType> outgoingEdgeSet = new HashSet();
        for (EdgeType edge: data.getNet().getEdge()) {
            if (edge.getSourceId().equals(sourceId)) {
                outgoingEdgeSet.add(edge);
            }
        }
        return outgoingEdgeSet;
    }

    // TODO: keep track of this on the DataHandler rather than doing a linear search
    private org.apromore.cpf.NodeType findCpfNodeById(final String cpfId) {
        for (org.apromore.cpf.NodeType node: data.getNet().getNode()) {
            if (node.getId().equals(cpfId)) {
                return node;
            }
        }
        return null;
    }

    public long getIds() {
        return ids;
    }


    private String getEdgeRealId(String originalId) {
        return data.id_map.get(originalId);
    }
}
