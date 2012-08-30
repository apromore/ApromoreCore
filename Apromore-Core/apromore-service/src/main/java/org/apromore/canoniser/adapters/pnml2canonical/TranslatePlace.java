package org.apromore.canoniser.adapters.pnml2canonical;

import org.apromore.cpf.EdgeType;
import org.apromore.cpf.EventType;
import org.apromore.cpf.StateType;
import org.apromore.pnml.PlaceType;

public class TranslatePlace {
    DataHandler data;
    long ids;

    public void setValues(DataHandler data, long ids) {
        this.data = data;
        this.ids = ids;
    }

    public void translateState(PlaceType place) {
        StateType node = new StateType();
        data.put_id_map(place.getId(), String.valueOf(ids));
        node.setId(String.valueOf(ids++));
        if (place.getName() != null) {
            node.setName(place.getName().getText());
        }
        node.setOriginalID(place.getId());
        data.getNet().getNode().add(node);

    }

    public void translateEvent(PlaceType place) {
        EventType node = new EventType();
        data.put_id_map(place.getId(), String.valueOf(ids));

        node.setId(String.valueOf(ids++));
        if (place.getName() != null) {
            node.setName(place.getName().getText());
        }
        node.setOriginalID(place.getId());
        data.getNet().getNode().add(node);
    }

    public void translateInput(PlaceType place) {

        EventType node = new EventType();
        StateType state = new StateType();
        data.setInputEdge(new EdgeType());

        data.put_id_map(place.getId(), String.valueOf(ids));
        node.setId(String.valueOf(ids++));
        node.setName(place.getName().getText());
        state.setId(String.valueOf(ids++));
        state.setName(place.getName().getText());
        data.setInputEvent(state.getId());
        node.setOriginalID(place.getId());
        data.getInputEdge().setId(String.valueOf(ids++));
        data.getInputEdge().setSourceId(node.getId());
        data.getInputEdge().setTargetId(state.getId());
        data.getNet().getNode().add(node);
        data.getNet().getNode().add(state);

    }

    public void translateOutput(PlaceType place) {
        EventType node = new EventType();
        StateType state = new StateType();
        data.setOutputEdge(new EdgeType());
        data.put_id_map(place.getId(), String.valueOf(ids));
        node.setId(String.valueOf(ids++));
        node.setName(place.getName().getText());
        state.setId(String.valueOf(ids++));
        state.setName(place.getName().getText());
        data.setOutputState(state.getId());
        data.getOutputEdge().setId(String.valueOf(ids++));
        data.getOutputEdge().setSourceId(state.getId());
        data.getOutputEdge().setTargetId(node.getId());
        node.setOriginalID(place.getId());
        data.getNet().getNode().add(state);
        data.getNet().getNode().add(node);
    }

    public long getIds() {
        return ids;
    }

}
