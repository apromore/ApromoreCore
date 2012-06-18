package org.apromore.canoniser.adapters.pnml2canonical;

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

        if (data.getOutputnode().equals(((org.apromore.pnml.NodeType) arc.getTarget()).getId())) {
            if (data.get_andjoinmap().containsKey(((org.apromore.pnml.NodeType) arc.getSource()).getId())) {
                edge.setSourceId(((org.apromore.pnml.NodeType) arc.getSource()).getId());
                edge.setTargetId(data.getOutputState());
            } else {
                edge.setSourceId((((org.apromore.pnml.NodeType) arc.getSource()).getId()));
                edge.setTargetId(data.getOutputState());
            }
        } else if (data.getInputnode().equals(((org.apromore.pnml.NodeType) arc.getSource()).getId())) {
            if (data.get_andsplitmap().containsKey(((org.apromore.pnml.NodeType) arc.getTarget()).getId())) {
                edge.setSourceId(data.getInputEvent());
                edge.setTargetId((((org.apromore.pnml.NodeType) arc.getTarget()).getId()));
            } else {
                edge.setSourceId(data.getInputEvent());
                edge.setTargetId((((org.apromore.pnml.NodeType) arc.getTarget()).getId()));
            }
        } else if (data.get_andjoinmap().containsKey(((org.apromore.pnml.NodeType) arc.getSource()).getId())) {
            edge.setSourceId(((org.apromore.pnml.NodeType) arc.getSource()).getId());
            edge.setTargetId(((org.apromore.pnml.NodeType) arc.getTarget()).getId());
        } else if (data.get_andsplitmap().containsKey(((org.apromore.pnml.NodeType) arc.getTarget()).getId())) {
            edge.setSourceId(((org.apromore.pnml.NodeType) arc.getSource()).getId());
            edge.setTargetId(((org.apromore.pnml.NodeType) arc.getTarget()).getId());
        } else if (data.get_andsplitjoinmap().containsKey(((org.apromore.pnml.NodeType) arc.getSource()).getId())) {
            edge.setSourceId(((org.apromore.pnml.NodeType) arc.getSource()).getId());
            edge.setTargetId(((org.apromore.pnml.NodeType) arc.getTarget()).getId());
        } else {
            edge.setSourceId(((org.apromore.pnml.NodeType) arc .getSource()).getId());
            edge.setTargetId(((org.apromore.pnml.NodeType) arc.getTarget()).getId());
        }
        edge.setId(String.valueOf(ids++));
        edge.setOriginalID(arc.getId());
        data.getNet().getEdge().add(edge);

    }

    public long getIds() {
        return ids;
    }
}