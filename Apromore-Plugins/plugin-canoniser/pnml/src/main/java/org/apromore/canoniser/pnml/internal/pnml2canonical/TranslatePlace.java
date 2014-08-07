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

package org.apromore.canoniser.pnml.internal.pnml2canonical;

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
