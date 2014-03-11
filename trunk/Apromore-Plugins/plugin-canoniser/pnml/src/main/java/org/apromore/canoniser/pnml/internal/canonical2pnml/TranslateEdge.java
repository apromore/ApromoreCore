package org.apromore.canoniser.pnml.internal.canonical2pnml;

import java.math.BigInteger;

import org.apromore.cpf.EdgeType;
import org.apromore.cpf.NodeType;
import org.apromore.pnml.ArcNameType;
import org.apromore.pnml.ArcType;

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
