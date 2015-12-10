/*
 * Copyright © 2009-2015 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.canoniser.pnml.internal.canonical2pnml;

import java.math.BigInteger;
import java.util.logging.Logger;

import org.apromore.cpf.EdgeType;
import org.apromore.cpf.NodeType;
import org.apromore.pnml.ArcNameType;
import org.apromore.pnml.ArcType;
import org.apromore.pnml.PlaceType;
import org.apromore.pnml.TransitionType;

public class TranslateEdge {

    static private Logger LOGGER = Logger.getLogger(TranslateEdge.class.getCanonicalName());

    DataHandler data;
    private long ids;

    public void setValues(DataHandler data, long ids) {
        this.data = data;
        this.ids = ids;
    }

    public void translateArc(EdgeType edge) {
        //LOGGER.info("Translating edge " + edge.getId());
        org.apromore.pnml.NodeType arcsource = new org.apromore.pnml.NodeType();
        org.apromore.pnml.NodeType arctarget = new org.apromore.pnml.NodeType();

        NodeType source = data.get_nodeRefMap_value(edge.getSourceId());
        NodeType target = data.get_nodeRefMap_value(edge.getTargetId());

        data.put_id_map(edge.getId(), String.valueOf(ids));

        if (data.getStartNodeMap().containsKey(source)) {
            arcsource = data.getEndNodeMap().get(source);
        }

        if (data.getEndNodeMap().containsKey(target)) {
            arctarget = data.getStartNodeMap().get(target);
        }

        if (data.isCpfEdgePnmlPlace()) {
            // Create a PNML Place corresponding to the CPF Edge
            PlaceType place = new PlaceType();
            place.setId(String.valueOf(ids++));
            place.setGraphics(TranslateNode.newGraphicsNodeType(TranslateNode.dummyPosition(), TranslateNode.placeDefaultDimension()));
            data.getNet().getPlace().add(place);
            //data.put_pnmlRefMap(place.getId(), place);  TranslateNodeAnnotation can't cope with ANF Edges generating PNML Place Graphics

            // Create incoming arc
            ArcType incomingArc = new ArcType();
            incomingArc.setId(String.valueOf(ids++));
            incomingArc.setTarget(place);
            data.getNet().getArc().add(incomingArc);
            data.put_pnmlRefMap(incomingArc.getId(), incomingArc);

            if (!(arcsource instanceof PlaceType)) {
                incomingArc.setSource(arcsource);
            }
            else {  // insert a silent transition before the place
                TransitionType transition = new TransitionType();
                transition.setId(String.valueOf(ids++));
                transition.setGraphics(TranslateNode.newGraphicsNodeType(TranslateNode.dummyPosition(), TranslateNode.blindTransitionDefaultDimension()));
                data.getNet().getTransition().add(transition);
                data.put_pnmlRefMap(transition.getId(), transition);

                incomingArc.setSource(transition);

                ArcType arc = new ArcType();
                arc.setId(String.valueOf(ids++));
                arc.setSource(arcsource);
                arc.setTarget(transition);
                data.getNet().getArc().add(arc);
                data.put_pnmlRefMap(arc.getId(), arc);
            }

            // Create outgoing arc
            ArcType outgoingArc = new ArcType();
            outgoingArc.setId(String.valueOf(ids++));
            outgoingArc.setSource(place);
            data.getNet().getArc().add(outgoingArc);
            data.put_pnmlRefMap(outgoingArc.getId(), outgoingArc);

            if (!(arctarget instanceof PlaceType)) {
                outgoingArc.setTarget(arctarget);
            }
            else {  // insert a silent transition after the place
                TransitionType transition = new TransitionType();
                transition.setId(String.valueOf(ids++));
                transition.setGraphics(TranslateNode.newGraphicsNodeType(TranslateNode.dummyPosition(), TranslateNode.blindTransitionDefaultDimension()));
                data.getNet().getTransition().add(transition);
                data.put_pnmlRefMap(transition.getId(), transition);

                outgoingArc.setTarget(transition);

                ArcType arc = new ArcType();
                arc.setId(String.valueOf(ids++));
                arc.setSource(transition);
                arc.setTarget(arctarget);
                data.getNet().getArc().add(arc);
                data.put_pnmlRefMap(arc.getId(), arc);
            }

            data.put_originalid_map(BigInteger.valueOf(Long.valueOf(place.getId())), edge.getOriginalID());
        }
        else {  // !data.isCpfEdgePnmlPlace()
            ArcType arc = new ArcType();
            arc.setId(String.valueOf(ids++));

            // Synthesize a place in the case of an edge between two transitions
            if (arcsource instanceof TransitionType && arctarget instanceof TransitionType) {
                // Create the synthetic place
                PlaceType place = new PlaceType();
                place.setId(String.valueOf(ids++));
                place.setGraphics(TranslateNode.newGraphicsNodeType(TranslateNode.dummyPosition(), TranslateNode.placeDefaultDimension()));
                data.getNet().getPlace().add(place);
                data.getSynthesizedPlaces().add(place);
                data.put_pnmlRefMap(place.getId(), place);

                // Create outgoing arc from the synthetic place to the original arc's target
                ArcType arc2 = new ArcType();
                arc2.setId(String.valueOf(ids++));
                arc2.setSource(place);
                arc2.setTarget(arctarget);
                data.getNet().getArc().add(arc2);
                data.put_pnmlRefMap(arc2.getId(), arc2);

                // Our original arc now targets the synthesized place
                arctarget = place;
            }

            arc.setSource(arcsource);
            arc.setTarget(arctarget);
            ArcNameType inscription = new ArcNameType();
            inscription.setText(1);
            arc.setInscription(inscription);
            data.getNet().getArc().add(arc);
            data.put_pnmlRefMap(arc.getId(), arc);

            data.put_originalid_map(BigInteger.valueOf(Long.valueOf(arc.getId())), edge.getOriginalID());
        }
        //LOGGER.info("Translated edge " + edge.getId() + " as arc " + arc.getId());
    }

    public long getIds() {
        return ids;
    }
}
