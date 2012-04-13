package org.apromore.canoniser.adapters.canonical2pnml;

import org.apromore.anf.AnnotationsType;
import org.apromore.cpf.ANDJoinType;
import org.apromore.cpf.ANDSplitType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.EventType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.RoutingType;
import org.apromore.cpf.StateType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.WorkType;
import org.apromore.cpf.XORJoinType;
import org.apromore.cpf.XORSplitType;

public class TranslateNet {
	DataHandler data;
	long ids;
	TranslateAnnotations ta = new TranslateAnnotations();
	TranslateNode tn = new TranslateNode();
	TranslateEdge te = new TranslateEdge();
	TranslateOperators to = new TranslateOperators();
	TranslateToolspecifc tt = new TranslateToolspecifc();
	TranslateOriginalIDS moids = new TranslateOriginalIDS();
	AnnotationsType annotations;

	public void setValues(DataHandler data, long ids,
			AnnotationsType annotations) {
		this.data = data;
		this.ids = ids;
		this.annotations = annotations;
	}

	public void translateNet(NetType net) {

		for (NodeType node : net.getNode()) {
			if (node instanceof WorkType) {
				if (node instanceof TaskType || node instanceof EventType)

				{

					tn.setValues(data, ids);
					if (node instanceof TaskType) {

						tn.translateTask((TaskType) node);

					} else if (node instanceof EventType) {

						tn.translateEvent(node);
					}

					ids = tn.getIds();
				}
			} else if (node instanceof RoutingType) {
				if (node instanceof StateType) {
					tn.setValues(data, ids);
					tn.translateState(node);

					ids = tn.getIds();
				} else if (node instanceof ANDJoinType
						|| node instanceof ANDSplitType
						|| node instanceof XORJoinType
						|| node instanceof XORSplitType) {

					to.setValues(data, ids);
					to.translate(node);
					ids = to.getIds();
				}
			}
			data.put_nodeRefMap(node.getId(), node);

		}

		for (EdgeType edge : net.getEdge()) {
			if (edge instanceof EdgeType) {

				te.setValues(data, ids);
				te.translateArc(edge);

				ids = te.getIds();
			}
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
