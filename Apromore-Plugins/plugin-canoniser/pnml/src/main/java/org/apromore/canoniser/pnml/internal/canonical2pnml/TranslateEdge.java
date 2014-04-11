package org.apromore.canoniser.pnml.internal.canonical2pnml;

import java.math.BigInteger;

import org.apromore.cpf.EdgeType;
import org.apromore.cpf.NodeType;
import org.apromore.pnml.ArcNameType;
import org.apromore.pnml.ArcType;
import org.apromore.pnml.PlaceType;
import org.apromore.pnml.TransitionType;

public class TranslateEdge {

    DataHandler data;
    private long ids;

    public void setValues(DataHandler data, long ids) {
        this.data = data;
        this.ids = ids;
    }

    public void translateArc(EdgeType edge) {
        ArcType arc = new ArcType();

        org.apromore.pnml.NodeType arcsource = new org.apromore.pnml.NodeType();
        org.apromore.pnml.NodeType arctarget = new org.apromore.pnml.NodeType();

        NodeType source = data.get_nodeRefMap_value(edge.getSourceId());
        NodeType target = data.get_nodeRefMap_value(edge.getTargetId());

        data.put_id_map(edge.getId(), String.valueOf(ids));
        arc.setId(String.valueOf(ids++));

        if (data.getStartNodeMap().containsKey(source)) {
            arcsource = data.getEndNodeMap().get(source);
        }

        if (data.getEndNodeMap().containsKey(target)) {
            arctarget = data.getStartNodeMap().get(target);
        }

        // Synthesize a place in the case of an edge between two transitions
        if (arcsource instanceof TransitionType && arctarget instanceof TransitionType) {

            // Create the synthetic place
            PlaceType place = new PlaceType();
            place.setId(String.valueOf(ids++));
            place.setGraphics(TranslateNode.newGraphicsNodeType(TranslateNode.dummyPosition(), TranslateNode.placeDefaultDimension()));
            data.getNet().getPlace().add(place);

            // Create outgoing arc from the synthetic place to the original arc's target
            ArcType arc2 = new ArcType();
            arc2.setId(String.valueOf(ids++));
            arc2.setSource(place);
            arc2.setTarget(arctarget);
            data.getNet().getArc().add(arc2);

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

    public long getIds() {
        return ids;
    }
}
