package org.apromore.canoniser.adapters.canonical2pnml;

import java.util.LinkedList;
import java.util.List;

import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.pnml.ArcToolspecificType;
import org.apromore.pnml.ArcType;
import org.apromore.pnml.TransitionToolspecificType;
import org.apromore.pnml.TransitionType;

public class AddXorOperators {
	DataHandler data;
	long ids;
	TransitionType trandouble;
	CanonicalProcessType cproc;
	List<ArcType> arcs;

	public void setValues(DataHandler data, long ids) {
		this.data = data;
		this.ids = ids;
	}

	public void add(CanonicalProcessType cproc) {
		this.cproc = cproc;
		for (NetType net : cproc.getNet()) {
			for (NodeType node : data.getxorconnectors()) {
				NodeType test = node;
				int splitcount = 0;
				int joincount = 0;
				if (data.get_dupjoinMap().containsKey(node.getOriginalID())) {
					for (EdgeType edge : net.getEdge()) {
						if (edge instanceof EdgeType) {
							if (edge.getTargetId().equals(test.getId())
									&& edge.getOriginalID() != null) {
								joincount++;
							}
						}
					}
					for (int i = 1; i < joincount; i++) {
						TransitionType tran = new TransitionType();
						TransitionType oldtran = data.get_dupjoinMap_value(node
								.getOriginalID());
						String id = node.getOriginalID().replace("op_1",
								"op_" + String.valueOf(i + 1));
						tran.setId(id);
						tran.setName(oldtran.getName());
						tran.setGraphics(oldtran.getGraphics());
						for (TransitionToolspecificType oldttt : oldtran
								.getToolspecific()) {
							tran.getToolspecific().add(oldttt);
						}
						data.getNet().getTransition().add(tran);
						if (i == 1) {
							arcs = new LinkedList<ArcType>();
						}
						ArcType newarc = null;
						for (ArcType arc : data.getNet().getArc()) {
							if (arc.getSource().equals(oldtran)) {
								newarc = new ArcType();
								newarc.setId(arc.getId() + "_" + i);
								newarc.setSource(tran);
								newarc.setTarget(arc.getTarget());
								newarc.setInscription(arc.getInscription());
								newarc.setGraphics(arc.getGraphics());
								for (ArcToolspecificType oldatt : arc
										.getToolspecific()) {
									newarc.getToolspecific().add(oldatt);
								}

							} else if (arc.getTarget().equals(oldtran)) {
								if (i == 1) {
									arcs.add(arc);
								}
							}
						}
						if (newarc != null) {
							data.getNet().getArc().add(newarc);
						}
						Object[] changearcs = arcs.toArray();
						ArcType update = (ArcType) changearcs[i];
						update.setTarget(tran);

					}
				} else if (data.get_dupsplitMap().containsKey(
						node.getOriginalID())) {
					for (EdgeType edge : net.getEdge()) {
						if (edge instanceof EdgeType) {
							if (edge.getSourceId().equals(test.getId())
									&& edge.getOriginalID() != null) {
								splitcount++;

							}
						}
					}

					for (int i = 1; i < splitcount; i++) {
						TransitionType tran = new TransitionType();
						TransitionType oldtran = data
								.get_dupsplitMap_value(node.getOriginalID());
						String id = node.getOriginalID().replace("op_1",
								"op_" + String.valueOf(i + 1));
						tran.setId(id);
						tran.setName(oldtran.getName());
						tran.setGraphics(oldtran.getGraphics());
						for (TransitionToolspecificType oldttt : oldtran
								.getToolspecific()) {
							tran.getToolspecific().add(oldttt);
						}
						data.getNet().getTransition().add(tran);
						if (i == 1) {
							arcs = new LinkedList<ArcType>();
						}
						ArcType newarc = null;
						for (ArcType arc : data.getNet().getArc()) {
							if (arc.getTarget().equals(oldtran)) {
								newarc = new ArcType();
								newarc.setId(arc.getId() + "_" + i);
								newarc.setSource(arc.getSource());
								newarc.setTarget(tran);
								newarc.setInscription(arc.getInscription());
								newarc.setGraphics(arc.getGraphics());
								for (ArcToolspecificType oldatt : arc
										.getToolspecific()) {
									newarc.getToolspecific().add(oldatt);
								}

							} else if (arc.getSource().equals(oldtran)) {
								if (i == 1) {
									arcs.add(arc);
								}
							}
						}
						if (newarc != null) {
							data.getNet().getArc().add(newarc);
						}
						Object[] changearcs = arcs.toArray();
						ArcType update = (ArcType) changearcs[i];
						update.setSource(tran);

					}
				}

			}
		}
	}

	public long getIds() {
		return ids;
	}

	public CanonicalProcessType getCanonicalProcess() {
		return cproc;
	}
}
