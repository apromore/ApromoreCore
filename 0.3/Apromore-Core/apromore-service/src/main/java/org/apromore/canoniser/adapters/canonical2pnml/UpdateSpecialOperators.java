package org.apromore.canoniser.adapters.canonical2pnml;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apromore.cpf.CanonicalProcessType;
import org.apromore.pnml.ArcType;
import org.apromore.pnml.PlaceType;
import org.apromore.pnml.TransitionType;

public class UpdateSpecialOperators {
	DataHandler data;
	long ids;
	TransitionType trandouble;
	CanonicalProcessType cproc;
	List<ArcType> arcs;
	Map<String, TransitionType> ups = new HashMap<String, TransitionType>();

	public void setValues(DataHandler data, long ids) {
		this.data = data;
		this.ids = ids;
	}

	public void add(CanonicalProcessType cproc) {
		this.cproc = cproc;
		TransitionType operator = null;
		String idcheck = null;
		for (TransitionType spec : data.getNet().getTransition()) {

			if (data.get_specialoperators().containsKey(
					"andjoinxorsplit-" + spec.getName().getText())) {
				if (spec.getId().contains("op_1")) {
					operator = spec;
					idcheck = spec.getId().replace("op_1", "op_");
				}
			} else if (data.get_specialoperators().containsKey(
					"xorjoinandsplit-" + spec.getName().getText())) {
				if (spec.getId().contains("op_1")) {
					operator = spec;
					idcheck = spec.getId().replace("op_1", "op_");
				}
			} else if (data.get_specialoperators().containsKey(
					"xorsplitjoin-" + spec.getName().getText())) {
				if (spec.getId().contains("op_1")) {
					operator = spec;
					idcheck = spec.getId().replace("op_1", "op_");
				}
			}
		}
		if (operator != null) {
			for (TransitionType up : data.getNet().getTransition()) {
				if (up.getId().contains(idcheck)) {
					up.setGraphics(operator.getGraphics());
					up.setName(operator.getName());
					ups.put(up.getId(), up);

				}
			}
			for (PlaceType cp : data.getNet().getPlace()) {
				if (cp.getId().contains(
						"CENTER_PLACE_" + operator.getName().getText())) {
					cp.setName(operator.getName());
					cp.setGraphics(operator.getGraphics());
				}
			}
			if (data.get_specialoperatorscount().containsKey(
					"splitcount-" + operator.getName().getText())
					&& !data.get_specialoperatorscount()
							.keySet()
							.contains(
									"joincount-" + operator.getName().getText())) {
				int splitcount = data
						.get_specialoperatorscount_value("splitcount-"
								+ operator.getName().getText());
				for (int i = 1; i <= splitcount; i++) {
					if (i == 1) {
						arcs = new LinkedList<ArcType>();
						for (ArcType at : data.getNet().getArc()) {
							if (at.getSource().equals(operator)) {
								arcs.add(at);
							}
						}
					}
					Object[] changearcs = arcs.toArray();
					ArcType update = (ArcType) changearcs[i];
					String idup = ((TransitionType) update.getSource()).getId()
							.replace("op_1", "op_" + String.valueOf(i + 1));
					TransitionType newsource = ups.get(idup);
					update.setSource(newsource);
				}
			}
			if (data.get_specialoperatorscount().keySet()
					.contains("joincount-" + operator.getName().getText())
					&& !data.get_specialoperatorscount().containsKey(
							"splitcount-" + operator.getName().getText())) {
				int joincount = data
						.get_specialoperatorscount_value("joincount-"
								+ operator.getName().getText());
				for (int i = 1; i <= joincount; i++) {
					if (i == 1) {
						arcs = new LinkedList<ArcType>();
						for (ArcType at : data.getNet().getArc()) {
							if (at.getTarget().equals(operator)) {
								arcs.add(at);

							}
						}
					}
					Object[] changearcs = arcs.toArray();
					ArcType update = (ArcType) changearcs[i];
					String idup = ((TransitionType) update.getTarget()).getId()
							.replace("op_1", "op_" + String.valueOf(i + 1));
					TransitionType newsource = ups.get(idup);
					update.setTarget(newsource);
				}
			}
			if (data.get_specialoperatorscount().keySet()
					.contains("joincount-" + operator.getName().getText())
					&& data.get_specialoperatorscount().containsKey(
							"splitcount-" + operator.getName().getText())) {
				int joincount = data
						.get_specialoperatorscount_value("joincount-"
								+ operator.getName().getText());
				int splitcount = data
						.get_specialoperatorscount_value("splitcount-"
								+ operator.getName().getText());
				for (int i = 1; i < joincount; i++) {
					if (i == 1) {
						arcs = new LinkedList<ArcType>();
						for (ArcType at : data.getNet().getArc()) {
							if (at.getTarget().equals(operator)) {
								arcs.add(at);

							}
						}
					}
					Object[] changearcs = arcs.toArray();
					ArcType update = (ArcType) changearcs[i];
					String idup = ((TransitionType) update.getTarget()).getId()
							.replace("op_1", "op_" + String.valueOf(i + 1));
					TransitionType newsource = ups.get(idup);
					update.setTarget(newsource);
				}
				for (int i = 1; i <= splitcount; i++) {
					if (i == 1) {
						arcs = new LinkedList<ArcType>();
						for (ArcType at : data.getNet().getArc()) {
							if (at.getSource().equals(operator)) {
								arcs.add(at);
							}
						}
					}
					Object[] changearcs = arcs.toArray();
					ArcType update = (ArcType) changearcs[i];
					String idup = ((TransitionType) update.getSource()).getId()
							.replace("op_1",
									"op_" + String.valueOf(i + joincount));
					TransitionType newsource = ups.get(idup);
					update.setSource(newsource);
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
