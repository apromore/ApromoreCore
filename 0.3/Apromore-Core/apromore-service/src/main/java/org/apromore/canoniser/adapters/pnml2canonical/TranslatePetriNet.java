package org.apromore.canoniser.adapters.pnml2canonical;

import org.apromore.exception.CanoniserException;
import org.apromore.pnml.ArcType;
import org.apromore.pnml.PlaceType;
import org.apromore.pnml.ToolspecificType;
import org.apromore.pnml.TransitionToolspecificType;
import org.apromore.pnml.TransitionType;

public class TranslatePetriNet {
	TranslateOperations to = new TranslateOperations();
	TranslateArc ta = new TranslateArc();
	TranslatePlace tts = new TranslatePlace();
	TranslateTransition tt = new TranslateTransition();
	TranslateTransitionToolspecifc ttt = new TranslateTransitionToolspecifc();
	TranslateArcToolspecific tat = new TranslateArcToolspecific();
	TranslateNodeAnnotations tna = new TranslateNodeAnnotations();
	TranslateEdgeAnnotations tea = new TranslateEdgeAnnotations();
	DataHandler data;
	long ids;

	public void setValues(DataHandler data, long ids) {
		this.data = data;
		this.ids = ids;
	}

	public void translatePetriNet(org.apromore.pnml.NetType pnet) throws CanoniserException {
		for (Object obj : pnet.getArc()) {
			if (obj instanceof ArcType) {
				data.addtargetvalues(String
						.valueOf(((org.apromore.pnml.NodeType) ((ArcType) obj)
								.getSource()).getId()));
				data.addsourcevalues(String
						.valueOf(((org.apromore.pnml.NodeType) ((ArcType) obj)
								.getTarget()).getId()));
				data.addinput(String
						.valueOf(((org.apromore.pnml.NodeType) ((ArcType) obj)
								.getSource()).getId()));
				data.addoutput(String
						.valueOf(((org.apromore.pnml.NodeType) ((ArcType) obj)
								.getTarget()).getId()));

			}
		}
		RemoveDuplicateListItems removeDuplicateListItems = new RemoveDuplicateListItems();
		removeDuplicateListItems.transform(data.gettargetvalues());
		removeDuplicateListItems.transform(data.getsourcevalues());

		for (Object obj : pnet.getPlace()) {
			if (obj instanceof PlaceType) {
				if (data.gettargetvalues().contains(((PlaceType) obj).getId())
						|| data.getsourcevalues().contains(
								((PlaceType) obj).getId())) {
					if (!data.getinput().contains(((PlaceType) obj).getId())
							&& data.getoutput().contains(
									((PlaceType) obj).getId())) {

						data.setOutputnode(String
								.valueOf(((org.apromore.pnml.NodeType) obj)
										.getId()));
						tts.setValues(data, ids);
						tts.translateOutput((PlaceType) obj);
						addNodeAnnotations(obj);
					} else if (data.getinput().contains(
							((PlaceType) obj).getId())
							&& !data.getoutput().contains(
									((PlaceType) obj).getId())) {

						data.setInputnode(String
								.valueOf(((org.apromore.pnml.NodeType) obj)
										.getId()));
						tts.setValues(data, ids);
						tts.translateInput((PlaceType) obj);
						addNodeAnnotations(obj);
					} else {
						tts.setValues(data, ids);
						tts.translateState((PlaceType) obj);

						addNodeAnnotations(obj);
					}
				} else {
					tts.setValues(data, ids);
					tts.translateEvent((PlaceType) obj);
					addNodeAnnotations(obj);
				}

				ids = tts.getIds();
			}

		}
		for (Object obj : pnet.getTransition()) {
			if (obj instanceof TransitionType) {

				if (((TransitionType) obj).getToolspecific().size() >= 1
						|| (data.gettargetvalues().contains(
								((TransitionType) obj).getId()) || data
								.getsourcevalues().contains(
										((TransitionType) obj).getId()))) {

					if (((TransitionType) obj).getToolspecific().size() >= 1) {
						for (Object obj2 : ((TransitionType) obj)
								.getToolspecific()) {

							if ((data.gettargetvalues().contains(
									((TransitionType) obj).getId()) || data
									.getsourcevalues().contains(
											((TransitionType) obj).getId()))
									&& ((TransitionToolspecificType) obj2)
											.getOperator() == null) {

								to.setValues(data, ids);
								to.translateOperation(null,
										(TransitionType) obj);
								addNodeAnnotations(obj);
								ids = to.getIds();
							} else {

								if (((TransitionToolspecificType) obj2)
										.getOperator() != null) {

									to.setValues(data, ids);
									to.translateOperation(
											(TransitionToolspecificType) obj2,
											(TransitionType) obj);
									ids = to.getIds();
									addNodeAnnotations(obj);
								} else {
									tt.setValues(data, ids);
									tt.translateTask((TransitionType) obj);
									addNodeAnnotations(obj);
									ids = tt.getIds();
								}
							}
						}
					} else {
						to.setValues(data, ids);
						to.translateOperation(null, (TransitionType) obj);
						addNodeAnnotations(obj);
						ids = to.getIds();

					}

				} else {
					tt.setValues(data, ids);
					tt.translateTask((TransitionType) obj);
					addNodeAnnotations(obj);
					ids = tt.getIds();
				}
			}

			ttt.setValues(data, ids);
			ttt.translate(obj);
			ids = ttt.getIds();

		}

		for (Object obj : pnet.getArc()) {
			if (obj instanceof ArcType) {
				ta.setValues(data, ids);
				ta.translateEdge((ArcType) obj);
				addEdgeAnnotations(obj);
				ids = ta.getIds();

			}
			tat.setValues(data, ids);
			tat.translate(obj);
			ids = tat.getIds();
		}
		for (Object obj : pnet.getToolspecific()) {
			if (obj instanceof ToolspecificType) {
				TranslateHumanResources thr = new TranslateHumanResources();
				thr.setValues(data, ids);
				thr.translate(pnet);
				ids = thr.getIds();
			}
		}

		if (!data.getOutputnode().equals("end")) {
			data.getNet().getEdge().add(data.getOutputEdge());
			data.setOutputnode("end");
		}
		if (!data.getInputnode().equals("start")) {
			data.getNet().getEdge().add(data.getInputEdge());
			data.setInputnode("start");
		}

	}

	private void addNodeAnnotations(Object obj) {
		tna.setValues(data);
		tna.addNodeAnnotations(obj);
	}

	private void addEdgeAnnotations(Object obj) {
		tea.setValues(data);
		tea.addEdgeAnnotations(obj);
	}

	public long getIds() {
		return ids;
	}

}
