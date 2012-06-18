package org.apromore.canoniser.adapters.pnml2canonical;

import org.apromore.cpf.EdgeType;
import org.apromore.cpf.MessageType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.TimerType;
import org.apromore.pnml.TransitionToolspecificType;
import org.apromore.pnml.TransitionType;

import java.util.List;

public class TranslateTrigger {

	boolean translated = false;
	DataHandler data;
	long ids;

	public void setValues(DataHandler data, long ids) {
		this.data = data;
		this.ids = ids;
	}

	public boolean translateTrigger(TransitionType tran) {
		if (tran.getToolspecific() != null) {
			List<TransitionToolspecificType> pnmlTransitionToolspecific = tran
					.getToolspecific();
			for (Object obj : pnmlTransitionToolspecific) {
				if (obj instanceof TransitionToolspecificType) {

					TransitionToolspecificType transitionToolspecific = (TransitionToolspecificType) obj;

					if (transitionToolspecific.getTrigger() != null) {
						if (transitionToolspecific.getTrigger().getType() == 201) {
							MessageType msg = new MessageType();

							data.put_objectmap(String.valueOf(ids), msg);
							msg.setId(String.valueOf(ids++));
							if (tran.getName() != null) {
								msg.setName(tran.getName().getText());
							}
							TaskType msgtask = new TaskType();
							msgtask.setId(String.valueOf(ids++));
							if (tran.getName() != null) {
								msgtask.setName(tran.getName().getText());
							}
							msgtask.setOriginalID(tran.getId());
							EdgeType msgedge = new EdgeType();
							msgedge.setId(String.valueOf(ids++));
							msgedge.setSourceId(msg.getId());
							msgedge.setTargetId(msgtask.getId());

							data.getNet().getNode().add(msg);
							data.getNet().getEdge().add(msgedge);
							data.getNet().getNode().add(msgtask);
							data.put_andjoinmap(String.valueOf(tran.getId()),
									String.valueOf(msgtask.getId()));
							translated = true;
							return translated;
						} else if (transitionToolspecific.getTrigger()
								.getType() == 202) {
							TimerType time = new TimerType();

							data.put_objectmap(String.valueOf(ids), time);
							time.setId(String.valueOf(ids++));
							if (tran.getName() != null) {
								time.setName(tran.getName().getText());
							}
							TaskType timetask = new TaskType();
							timetask.setId(String.valueOf(ids++));
							if (tran.getName() != null) {
								timetask.setName(tran.getName().getText());
							}
							timetask.setOriginalID(tran.getId());
							EdgeType timeedge = new EdgeType();
							timeedge.setId(String.valueOf(ids++));
							timeedge.setSourceId(time.getId());
							timeedge.setTargetId(timetask.getId());

							data.getNet().getNode().add(time);
							data.getNet().getEdge().add(timeedge);
							data.getNet().getNode().add(timetask);
							data.put_andjoinmap(String.valueOf(tran.getId()),
									String.valueOf(timetask.getId()));
							translated = true;
							return translated;
						} else {
							translated = false;
							return translated;
						}

					}
				}
			}
		}
		translated = false;
		return translated;
	}

	public String translateOperationTrigger(TransitionType tran) {
		if (tran.getToolspecific() != null) {
			List<TransitionToolspecificType> pnmlTransitionToolspecific = tran
					.getToolspecific();
			for (Object obj : pnmlTransitionToolspecific) {
				if (obj instanceof TransitionToolspecificType) {

					TransitionToolspecificType transitionToolspecific = (TransitionToolspecificType) obj;

					if (transitionToolspecific.getTrigger() != null) {
						if (transitionToolspecific.getTrigger().getType() == 201) {

							return "message";
						} else if (transitionToolspecific.getTrigger()
								.getType() == 202) {
							return "time";
						} else if (transitionToolspecific.getTrigger()
								.getType() == 200) {
							return "res";
						} else {
							return "none";
						}

					}
				}
			}
		}
		return "none";
	}

	public long getIds() {
		return ids;
	}
}
