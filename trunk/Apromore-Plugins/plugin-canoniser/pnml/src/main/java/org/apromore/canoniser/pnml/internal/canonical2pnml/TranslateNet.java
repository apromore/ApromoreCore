package org.apromore.canoniser.pnml.internal.canonical2pnml;

import java.util.logging.Logger;

import org.apromore.anf.AnnotationsType;
import org.apromore.cpf.ANDJoinType;
import org.apromore.cpf.ANDSplitType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.EventType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ORJoinType;
import org.apromore.cpf.ORSplitType;
import org.apromore.cpf.RoutingType;
import org.apromore.cpf.StateType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.WorkType;
import org.apromore.cpf.XORJoinType;
import org.apromore.cpf.XORSplitType;

public class TranslateNet {

    static final private Logger LOGGER = Logger.getLogger(TranslateNet.class.getCanonicalName());

    DataHandler data;
    long ids;
    TranslateNode tn = new TranslateNode();
    TranslateEdge te = new TranslateEdge();
    TranslateOperators to = new TranslateOperators();
    TranslateToolspecifc tt = new TranslateToolspecifc();
    TranslateOriginalIDS moids = new TranslateOriginalIDS();
    AnnotationsType annotations;

    public void setValues(DataHandler data, long ids, AnnotationsType annotations) {
        this.data = data;
        this.ids = ids;
        this.annotations = annotations;
    }

    public void translateNet(NetType net) {
        for (NodeType node : net.getNode()) {
            if (node instanceof WorkType) {
                if (node instanceof TaskType || node instanceof EventType)  {
                    tn.setValues(data, ids);
                    if (node instanceof TaskType) {
                        tn.translateTask((TaskType) node);
                    } else {
                        tn.translateEvent(node);
                    }
                    ids = tn.getIds();
                }

            } else if (node instanceof RoutingType) {
                if (node instanceof StateType) {
                    tn.setValues(data, ids);
                    tn.translateState(node);
                    ids = tn.getIds();

                } else if (node instanceof ANDJoinType || node instanceof ANDSplitType || node instanceof XORJoinType
                        || node instanceof XORSplitType) {
                    to.setValues(data, ids);
                    to.translate(node);
                    ids = to.getIds();

                } else if (node instanceof ORJoinType || node instanceof ORSplitType) {
                    LOGGER.warning("Node " + node.getId() + " was inclusive OR; treated as XOR instead");
                    to.setValues(data, ids);
                    to.translate(node);
                    ids = to.getIds();
                    
                } else {
                    throw new RuntimeException("Unsupported routing type for node " + node.getId() + ": " + node.getClass());
                }
            }

            data.put_nodeRefMap(node.getId(), node);
        }

        LOGGER.info("Processing edges: " + net.getEdge());
        for (EdgeType edge : net.getEdge()) {
            assert edge != null;
            LOGGER.info("Processing edge " + edge.getId());

            te.setValues(data, ids);
            te.translateArc(edge);
            ids = te.getIds();

            data.put_edgeRefMap(edge.getId(), edge);
        }

        tt.setValues(data, ids);
        tt.translate(annotations);
        ids = tt.getIds();
        moids.setValues(data, ids);
        moids.mapIDS();
    }

    public long getIds() {
        return ids;
    }
}
