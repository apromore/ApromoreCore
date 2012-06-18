package org.apromore.canoniser.adapters.pnml2canonical;

import org.apromore.cpf.ResourceTypeRefType;
import org.apromore.cpf.TaskType;
import org.apromore.pnml.TransitionToolspecificType;
import org.apromore.pnml.TransitionType;

import java.util.List;

public class TranslateTransition {
	DataHandler data;
	long ids;

	public void setValues(DataHandler data, long ids) {
		this.data = data;
		this.ids = ids;
	}

	public void translateTask(TransitionType tran) {
		data.put_id_map(tran.getId(), String.valueOf(ids));
		TranslateTrigger tt = new TranslateTrigger();
		tt.setValues(data, ids);
		boolean translated = tt.translateTrigger(tran);
		ids = tt.getIds();
		if (translated == false) {
			TaskType task = new TaskType();

			data.put_objectmap(String.valueOf(ids), task);
			task.setId(String.valueOf(ids++));
			if (tran.getName() != null) {
				task.setName(tran.getName().getText());
			}
			task.setOriginalID(tran.getId());
			if (tran.getToolspecific() != null) {
				List<TransitionToolspecificType> pnmlTransitionToolspecific = tran
						.getToolspecific();
				for (Object obj : pnmlTransitionToolspecific) {
					if (obj instanceof TransitionToolspecificType) {

						TransitionToolspecificType transitionToolspecific = (TransitionToolspecificType) obj;
						if (transitionToolspecific.getTrigger() != null
								&& transitionToolspecific.getTrigger()
										.getType() == 200) {
							if (transitionToolspecific.getTransitionResource() == null) {

								task.getResourceTypeRef().add(
										new ResourceTypeRefType());

							}
						}
						if (transitionToolspecific.isSubprocess() != null) {
							task.setSubnetId(task.getId());
						}
					}
				}
			}
			data.getNet().getNode().add(task);

		}
	}

	public long getIds() {
		return ids;
	}

}
