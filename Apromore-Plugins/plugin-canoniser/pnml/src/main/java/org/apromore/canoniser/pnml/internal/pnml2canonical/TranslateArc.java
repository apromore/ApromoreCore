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
        String targetId = ((NodeType) arc.getTarget()).getId();

        if (data.getOutputnode().equals(targetId)) {
            sourceId = getEdgeRealId(sourceId);
            targetId = getEdgeRealId(data.getOutputState());
        } else if (data.getInputnode().equals(sourceId)) {
            sourceId = data.getInputEvent();
            targetId = getEdgeRealId(targetId);
        } else if (data.get_andjoinmap().containsKey(sourceId)) {
            sourceId = getEdgeRealId(sourceId);
            targetId = getEdgeRealId(targetId);
        } else if (data.get_andsplitmap().containsKey(targetId)) {
            sourceId = getEdgeRealId(sourceId);
            targetId = getEdgeRealId(targetId);
        } else if (data.get_andsplitjoinmap().containsKey(sourceId)) {
            sourceId = getEdgeRealId(sourceId);
            targetId = getEdgeRealId(targetId);
        } else {
            sourceId = getEdgeRealId(sourceId);
            targetId = getEdgeRealId(targetId);
        }

        ArcTypeType type = arc.getType();
        if (type != null && "reset".equals(type.getText())) {
            // This PNML arc corresponds to a CPF cancellation set element

            WorkType cancellingNode = null;
            org.apromore.cpf.NodeType cancelledNode = null;

            for (org.apromore.cpf.NodeType node: data.getNet().getNode()) {
                // Look for the cancelledNode
                if (node.getId().equals(sourceId)) {
                    cancelledNode = node;
                }

                // Look for the cancellingNode
                if (node.getId().equals(targetId)) {
                    org.apromore.cpf.NodeType target = node;
                    while (target instanceof ANDJoinType) {
                        // The resetted PNML transition had multiple incoming arcs, so this routing element was inserted
                        // consequently we need to traverse the forward edge to find the actual cancelling node.
                        for (EdgeType outgoing: findCpfNodeOutgoingEdges(target.getId())) {
                            target = findCpfNodeById(outgoing.getTargetId());
                        }
                    }

                    if (target instanceof WorkType) {
                        cancellingNode = (WorkType) target;
                    } else {
                        LOGGER.warning("Reset arc cannot be represented as cancellation set of " + target.getName() + " class " + target.getClass());
                    }
                }
            }
            // Found the cancelledNode and cancellingNode, if they exist

            if (cancelledNode != null && cancellingNode != null) {
                CancellationRefType cancellationRef = CpfObjectFactory.getInstance().createCancellationRefType();
                cancellationRef.setRefId(cancelledNode.getId());
                cancellingNode.getCancelNodeId().add(cancellationRef);
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
