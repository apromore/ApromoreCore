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

        if (data.get_tempmap().containsKey(source.getName())) {
            arcsource = (org.apromore.pnml.NodeType) data.get_tempmap_value(source.getName());
        }

        if (data.get_tempmap().containsKey(target.getName())) {
            arctarget = (org.apromore.pnml.NodeType) data.get_tempmap_value(target.getName());
        }

        arc.setSource(arcsource);
        arc.setTarget(arctarget);
        ArcNameType inscription = new ArcNameType();
        inscription.setText(1);
        arc.setInscription(inscription);
        data.getNet().getArc().add(arc);
        java.util.logging.Logger.getAnonymousLogger().info("Edge " + edge.getId() + " became Arc " + arc.getId());

        data.put_pnmlRefMap(arc.getId(), arc);
        data.put_originalid_map(BigInteger.valueOf(Long.valueOf(arc.getId())), edge.getOriginalID());

    }

    public long getIds() {
        return ids;
    }
}
