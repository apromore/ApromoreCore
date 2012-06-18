package org.apromore.canoniser.adapters.canonical2pnml;

import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.EventType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.StateType;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RemoveState {
	DataHandler data;
	CanonicalProcessType cproc = new CanonicalProcessType();
	RemoveDuplicateListItems tl = new RemoveDuplicateListItems();

	public void setValue(DataHandler data, CanonicalProcessType cproc) {
		this.data = data;
		this.cproc = cproc;
	}

	public void remove() {

		Map<String, NodeType> nodemap = new HashMap<String, NodeType>();
		Map<String, EdgeType> joinmap = new HashMap<String, EdgeType>();
		Map<String, EdgeType> splitmap = new HashMap<String, EdgeType>();
		List<NodeType> removenodes = new LinkedList<NodeType>();
		List<EdgeType> removeedges = new LinkedList<EdgeType>();

		for (NetType net : cproc.getNet()) {
			for (EdgeType edge : net.getEdge()) {
				if (edge instanceof EdgeType) {
					joinmap.put(edge.getSourceId(), edge);
					splitmap.put(edge.getTargetId(), edge);
					if (edge.getOriginalID() == null) {
						removeedges.add(edge);
					}
				}

			}

			for (NodeType node : net.getNode()) {
				if (node instanceof StateType) {
					if (node.getOriginalID() == null) {
						nodemap.put(node.getName(), node);
					}
				}
			}
			for (NodeType node : net.getNode()) {
				if (node instanceof EventType) {

					if (nodemap.containsKey(node.getName())) {

						NodeType remove = nodemap.get(node.getName());
						if (joinmap.containsKey(remove.getId())) {

							for (EdgeType edge : net.getEdge()) {
								if (edge instanceof EdgeType) {
									if ((String.valueOf(edge.getSourceId()))
											.equals(String.valueOf(remove
													.getId()))) {
										edge.setSourceId(node.getId());
									}
								}
							}
							removenodes.add(remove);

						} else if (splitmap.containsKey(remove.getId())) {

							for (EdgeType edge : net.getEdge()) {
								if (edge instanceof EdgeType) {
									if ((String.valueOf(edge.getTargetId()))
											.equals(String.valueOf(remove
													.getId()))) {
										edge.setTargetId(node.getId());
									}
								}
							}
							removenodes.add(remove);
						}
					}
				}
			}
		}

		for (NetType net : cproc.getNet()) {
			if (removenodes.size() > 0) {
				for (Object obj : removenodes) {
					net.getNode().remove(obj);
				}
			}
			if (removeedges.size() > 0) {
				for (Object obj : removeedges) {
					net.getEdge().remove(obj);
				}
			}
		}
	}

	public CanonicalProcessType getCanonicalProcess() {
		return cproc;
	}
}
