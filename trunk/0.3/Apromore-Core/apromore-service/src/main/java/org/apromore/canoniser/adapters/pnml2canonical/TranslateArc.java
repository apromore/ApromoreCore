package org.apromore.canoniser.adapters.pnml2canonical;

import java.math.BigInteger;

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

		if (data.getOutputnode().equals(
				((org.apromore.pnml.NodeType) arc.getTarget()).getId())) {
			if (data.get_andjoinmap().containsKey(
					String.valueOf(((org.apromore.pnml.NodeType) arc
							.getSource()).getId()))) {

				edge.setSourceId(BigInteger.valueOf(Long.valueOf(data
						.get_andjoinmap_value(String
								.valueOf(((org.apromore.pnml.NodeType) arc
										.getSource()).getId())))));
				edge.setTargetId(data.getOutputState());
			} else {
				edge.setSourceId(BigInteger.valueOf(Long.valueOf(data
						.get_id_map_value(String
								.valueOf(((org.apromore.pnml.NodeType) arc
										.getSource()).getId())))));

				edge.setTargetId(data.getOutputState());

			}
		} else if (data.getInputnode().equals(
				((org.apromore.pnml.NodeType) arc.getSource()).getId())) {

			if (data.get_andsplitmap().containsKey(
					String.valueOf(((org.apromore.pnml.NodeType) arc
							.getTarget()).getId()))) {

				edge.setSourceId(data.getInputEvent());
				edge.setTargetId(BigInteger.valueOf(Long.valueOf(data
						.get_andsplitmap_value(String
								.valueOf(((org.apromore.pnml.NodeType) arc
										.getTarget()).getId())))));
			} else {
				edge.setSourceId(data.getInputEvent());
				edge.setTargetId(BigInteger.valueOf(Long.valueOf(data
						.get_id_map_value(String
								.valueOf(((org.apromore.pnml.NodeType) arc
										.getTarget()).getId())))));

			}

		} else if (data.get_andjoinmap().containsKey(
				String.valueOf(((org.apromore.pnml.NodeType) arc.getSource())
						.getId()))) {
			edge.setSourceId(BigInteger.valueOf(Long.valueOf(data
					.get_andjoinmap_value(String
							.valueOf(((org.apromore.pnml.NodeType) arc
									.getSource()).getId())))));
			edge.setTargetId(BigInteger.valueOf(Long.valueOf(data
					.get_id_map_value(String
							.valueOf(((org.apromore.pnml.NodeType) arc
									.getTarget()).getId())))));

		} else if (data.get_andsplitmap().containsKey(
				String.valueOf(((org.apromore.pnml.NodeType) arc.getTarget())
						.getId()))) {

			edge.setSourceId(BigInteger.valueOf(Long.valueOf(data
					.get_id_map_value(String
							.valueOf(((org.apromore.pnml.NodeType) arc
									.getSource()).getId())))));
			edge.setTargetId(BigInteger.valueOf(Long.valueOf(data
					.get_andsplitmap_value(String
							.valueOf(((org.apromore.pnml.NodeType) arc
									.getTarget()).getId())))));
		} else if (data.get_andsplitjoinmap().containsKey(
				String.valueOf(((org.apromore.pnml.NodeType) arc.getSource())
						.getId()))) {

			edge.setSourceId(BigInteger.valueOf(Long.valueOf(data
					.get_andsplitjoinmap_value(String
							.valueOf(((org.apromore.pnml.NodeType) arc
									.getSource()).getId())))));
			edge.setTargetId(BigInteger.valueOf(Long.valueOf(data
					.get_id_map_value(String
							.valueOf(((org.apromore.pnml.NodeType) arc
									.getTarget()).getId())))));

		} else {

			edge.setSourceId(BigInteger.valueOf(Long.valueOf(data
					.get_id_map_value(String
							.valueOf(((org.apromore.pnml.NodeType) arc
									.getSource()).getId())))));

			edge.setTargetId(BigInteger.valueOf(Long.valueOf(data
					.get_id_map_value(String
							.valueOf(((org.apromore.pnml.NodeType) arc
									.getTarget()).getId())))));
		}
		edge.setId(BigInteger.valueOf(ids++));
		edge.setOriginalID(arc.getId());
		data.getNet().getEdge().add(edge);

	}

	public long getIds() {
		return ids;
	}
}