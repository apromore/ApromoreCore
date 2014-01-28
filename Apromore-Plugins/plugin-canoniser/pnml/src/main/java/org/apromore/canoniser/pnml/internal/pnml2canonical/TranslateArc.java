package org.apromore.canoniser.pnml.internal.pnml2canonical;

import org.apromore.cpf.EdgeType;
import org.apromore.pnml.ArcType;

public class TranslateArc {

    DataHandler data;
    long ids;

    public void setValues(DataHandler data, long ids) {
        this.data = data;
        this.ids = ids;
    }

    public void translateEdge(ArcType arc) {
        EdgeType edge = new EdgeType();
        data.put_id_map(arc.getId(), String.valueOf(ids));
        
        String sourceId = ((org.apromore.pnml.NodeType) arc.getSource()).getId();
        String targetId = ((org.apromore.pnml.NodeType) arc.getTarget()).getId();

        if (data.getOutputnode().equals(targetId)) {
            edge.setSourceId(getEdgeRealId(sourceId));
            edge.setTargetId(getEdgeRealId(data.getOutputState()));
        } else if (data.getInputnode().equals(sourceId)) {
            edge.setSourceId(data.getInputEvent());
            edge.setTargetId(getEdgeRealId(targetId));
        } else if (data.get_andjoinmap().containsKey(sourceId)) {
            edge.setSourceId(getEdgeRealId(sourceId));
            edge.setTargetId(getEdgeRealId(targetId));
        } else if (data.get_andsplitmap().containsKey(targetId)) {
            edge.setSourceId(getEdgeRealId(sourceId));
            edge.setTargetId(getEdgeRealId(targetId));
        } else if (data.get_andsplitjoinmap().containsKey(sourceId)) {
            edge.setSourceId(getEdgeRealId(sourceId));
            edge.setTargetId(getEdgeRealId(targetId));
        } else {
            edge.setSourceId(getEdgeRealId(sourceId));
            edge.setTargetId(getEdgeRealId(targetId));
        }

        edge.setId(String.valueOf(ids++));
        edge.setOriginalID(arc.getId());

        data.getNet().getEdge().add(edge);
    }

    public long getIds() {
        return ids;
    }


    private String getEdgeRealId(String originalId) {
        return data.id_map.get(originalId);
    }
}