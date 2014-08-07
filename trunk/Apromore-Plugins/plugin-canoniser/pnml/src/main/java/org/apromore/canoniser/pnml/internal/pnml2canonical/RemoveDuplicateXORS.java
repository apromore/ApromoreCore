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

import java.util.LinkedList;
import java.util.List;

import org.apromore.pnml.ArcType;
import org.apromore.pnml.NodeType;
import org.apromore.pnml.PlaceType;
import org.apromore.pnml.PnmlType;
import org.apromore.pnml.TransitionType;

public class RemoveDuplicateXORS {
    RemoveDuplicateListItems removeDuplicateListItems = new RemoveDuplicateListItems();
    PnmlType pnml;
    List<NodeType> nodes = new LinkedList<NodeType>();
    List<NodeType> places = new LinkedList<NodeType>();
    List<String> arcs = new LinkedList<String>();
    List<ArcType> delarcs = new LinkedList<ArcType>();
    DataHandler data;

    public PnmlType remove(PnmlType pnml, DataHandler data) {
        this.pnml = pnml;
        this.data = data;
        ArcType arc = new ArcType();
        if (pnml.getNet() != null && pnml.getNet().size() > 0) {
            for (int i = 0; i < pnml.getNet().size(); i++) {

                for (org.apromore.pnml.NetType pnet : pnml.getNet()) {
                    for (Object obj : pnet.getTransition()) {
                        if (obj instanceof TransitionType) {
                            for (int i1 = 2; i1 < 25; i1++) {
                                if (((TransitionType) obj).getId().contains(
                                        "op_" + i1)) {

                                    nodes.add(((NodeType) obj));
                                    if (i1 > 2) {

                                        String name = ((NodeType) obj).getId();
                                        String rename = name.replace(
                                                "op_" + i1, "op_1");

                                        if (data.get_xorcounter().containsKey(
                                                rename)) {
                                            data.get_xorcounter()
                                                    .remove(rename);
                                            data.put_xorcounter(rename,
                                                    String.valueOf(i1));
                                        } else {
                                            data.put_xorcounter(rename,
                                                    String.valueOf(i1));
                                        }
                                    }
                                }
                            }
                        }
                    }
                    for (Object obj : pnet.getPlace()) {
                        if (obj instanceof PlaceType) {
                            if (((PlaceType) obj).getId().contains(
                                    "CENTER_PLACE_t")) {

                                data.addCenter((PlaceType) obj);
                                places.add(((NodeType) obj));
                            }
                        }
                    }
                    for (Object obj : pnet.getArc()) {
                        if (obj instanceof ArcType) {
                            arcs.add(((ArcType) obj).getId());
                        }
                    }
                    removeDuplicateListItems.transform(arcs);

                    for (Object obj : pnet.getArc()) {
                        if (obj instanceof ArcType) {

                            if (arcs.contains(((ArcType) obj).getId())
                                    && !(((NodeType) ((ArcType) obj)
                                    .getSource()).getId()
                                    .contains("op_1"))
                                    && !(((NodeType) ((ArcType) obj)
                                    .getTarget()).getId()
                                    .contains("op_1"))) {
                                arc = ((ArcType) obj);
                                arcs.remove(((ArcType) obj).getId());
                                delarcs.add(arc);

                            } else if (String.valueOf(
                                    ((NodeType) ((ArcType) obj).getSource())
                                            .getId())
                                    .contains("CENTER_PLACE_t")
                                    || String.valueOf(
                                    ((NodeType) ((ArcType) obj)
                                            .getTarget()).getId())
                                    .contains("CENTER_PLACE_t")) {
                                ArcType oldarc = new ArcType();
                                oldarc = ((ArcType) obj);
                                NodeType oldtarget = new NodeType();
                                oldtarget.setId((((NodeType) ((ArcType) obj)
                                        .getTarget()).getId()));
                                NodeType oldsource = new NodeType();
                                oldsource.setId((((NodeType) ((ArcType) obj)
                                        .getSource()).getId()));
                                oldarc.setSource(oldsource);
                                oldarc.setTarget(oldtarget);
                                data.addCenterArc(oldarc);
                                arc = ((ArcType) obj);
                                arcs.remove(((ArcType) obj).getId());
                                delarcs.add(arc);
                            } else if ((((NodeType) ((ArcType) obj).getSource())
                                    .getId().contains("op_3"))) {
                                ArcType oldarc = new ArcType();
                                NodeType oldsource = new NodeType();
                                oldsource.setId((((NodeType) ((ArcType) obj)
                                        .getSource()).getId()));
                                oldarc.setId(((ArcType) obj).getId());
                                oldarc.setSource(oldsource);
                                oldarc.setTarget(((ArcType) obj).getTarget());
                                data.addupdatedarc(oldarc);
                                data.addNoderef(oldsource);
                            }

                        }
                    }

                    for (int i2 = 0; i2 < nodes.size(); i2++) {
                        pnet.getTransition().remove(nodes.get(i2));

                    }
                    for (int i1 = 0; i1 < delarcs.size(); i1++) {
                        pnet.getArc().remove(delarcs.get(i1));

                    }

                    for (int i3 = 0; i3 < places.size(); i3++) {
                        pnet.getPlace().remove(places.get(i3));
                    }

                    for (Object obj : pnet.getArc()) {
                        if (obj instanceof ArcType) {
                            for (int i4 = 2; i4 < 25; i4++) {
                                if (String
                                        .valueOf(
                                                ((NodeType) ((ArcType) obj)
                                                        .getSource()).getId())
                                        .contains("op_" + i4)) {

                                    ArcType oldarc = new ArcType();
                                    NodeType oldsource = new NodeType();
                                    oldsource
                                            .setId((((NodeType) ((ArcType) obj)
                                                    .getSource()).getId()));
                                    oldarc.setId(((ArcType) obj).getId());
                                    oldarc.setSource(oldsource);
                                    oldarc.setTarget(((ArcType) obj)
                                            .getTarget());
                                    data.addupdatedarc(oldarc);
                                    data.addNoderef(oldsource);

                                    String rename = null;
                                    if (String.valueOf(
                                            ((NodeType) ((ArcType) obj)
                                                    .getSource()).getId())
                                            .contains("op_" + i4)) {

                                        rename = String.valueOf(
                                                ((NodeType) ((ArcType) obj)
                                                        .getSource()).getId())
                                                .replace("op_" + i4, "op_1");
                                    }
                                    NodeType source = ((NodeType) ((ArcType) obj)
                                            .getSource());
                                    source.setId(rename);
                                    ((ArcType) obj).setSource(source);

                                } else if (String
                                        .valueOf(
                                                ((NodeType) ((ArcType) obj)
                                                        .getTarget()).getId())
                                        .contains("op_" + i4)) {

                                    ArcType oldarc = new ArcType();
                                    NodeType oldtarget = new NodeType();
                                    oldtarget
                                            .setId((((NodeType) ((ArcType) obj)
                                                    .getTarget()).getId()));
                                    oldarc.setId(((ArcType) obj).getId());
                                    oldarc.setSource(((ArcType) obj)
                                            .getSource());
                                    oldarc.setTarget(oldtarget);
                                    data.addupdatedarc(oldarc);
                                    data.addNoderef(oldtarget);

                                    String rename = null;
                                    if (String.valueOf(
                                            ((NodeType) ((ArcType) obj)
                                                    .getTarget()).getId())
                                            .contains("op_" + i4)) {

                                        rename = String.valueOf(
                                                ((NodeType) ((ArcType) obj)
                                                        .getTarget()).getId())
                                                .replace("op_" + i4, "op_1");
                                    }

                                    NodeType target = ((NodeType) ((ArcType) obj)
                                            .getTarget());
                                    target.setId(rename);
                                    ((ArcType) obj).setTarget(target);
                                } else if (String
                                        .valueOf(
                                                ((NodeType) ((ArcType) obj)
                                                        .getSource()).getId())
                                        .contains("op_" + i4)
                                        && String.valueOf(
                                        ((NodeType) ((ArcType) obj)
                                                .getTarget()).getId())
                                        .contains("op_" + i4)) {
                                    ArcType oldarc = new ArcType();
                                    oldarc.setId(((ArcType) obj).getId());
                                    NodeType oldtarget = new NodeType();
                                    oldtarget
                                            .setId((((NodeType) ((ArcType) obj)
                                                    .getTarget()).getId()));
                                    NodeType oldsource = new NodeType();
                                    oldsource
                                            .setId((((NodeType) ((ArcType) obj)
                                                    .getSource()).getId()));
                                    oldarc.setSource(oldsource);
                                    oldarc.setTarget(oldtarget);
                                    data.addupdatedarc(oldarc);
                                    data.addNoderef(oldsource);
                                    data.addNoderef(oldtarget);

                                    String rename = null;

                                    if (String.valueOf(
                                            ((NodeType) ((ArcType) obj)
                                                    .getTarget()).getId())
                                            .contains("op_" + i4)) {

                                        rename = String.valueOf(
                                                ((NodeType) ((ArcType) obj)
                                                        .getTarget()).getId())
                                                .replace("op_" + i4, "op_1");
                                    }
                                    String renamesource = null;
                                    if (String.valueOf(
                                            ((NodeType) ((ArcType) obj)
                                                    .getSource()).getId())
                                            .contains("op_" + i4)) {
                                        renamesource = String.valueOf(
                                                ((NodeType) ((ArcType) obj)
                                                        .getSource()).getId())
                                                .replace("op_" + i4, "op_1");
                                    }
                                    NodeType source = ((NodeType) ((ArcType) obj)
                                            .getSource());
                                    source.setId(renamesource);
                                    ((ArcType) obj).setSource(source);
                                    NodeType target = ((NodeType) ((ArcType) obj)
                                            .getTarget());
                                    target.setId(rename);
                                    ((ArcType) obj).setTarget(target);

                                }
                            }
                        }
                    }

                }

            }

        }

        return pnml;
    }

}
