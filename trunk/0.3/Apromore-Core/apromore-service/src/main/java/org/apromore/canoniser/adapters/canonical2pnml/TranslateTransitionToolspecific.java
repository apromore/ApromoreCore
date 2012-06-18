package org.apromore.canoniser.adapters.canonical2pnml;

import java.math.BigInteger;

import org.apromore.anf.AnnotationType;
import org.apromore.anf.SimulationType;
import org.apromore.pnml.TransitionToolspecificType;
import org.apromore.pnml.TransitionType;

public class TranslateTransitionToolspecific {
	DataHandler data;

	public void setValue(DataHandler data) {
		this.data = data;
	}

	public void translate(AnnotationType annotation, String cid) {
		Object obj = data.get_pnmlRefMap_value(data.get_id_map_value(cid));
		if (obj instanceof TransitionType) {
			SimulationType simu = (SimulationType) annotation;
			TransitionToolspecificType ttt = new TransitionToolspecificType();
			if (simu.getTime() != null) {
				ttt.setTime(Integer.valueOf(String.valueOf(simu.getTime())));
			}
			if (simu.getTimeUnit() != null) {
				ttt.setTimeUnit(Integer.valueOf(String.valueOf(simu
						.getTimeUnit())));
			}

			ttt.setOrientation(1);
			if (((TransitionType) obj).getToolspecific().size() > 0) {
				TransitionToolspecificType alreadythere = ((TransitionType) obj)
						.getToolspecific().get(0);
				alreadythere.setTime(ttt.getTime());
				alreadythere.setTimeUnit(ttt.getTimeUnit());
				if (alreadythere.getOrientation() == null) {
					alreadythere.setOrientation(1);
				}

			} else {
				ttt.setTool("WoPeD");
				ttt.setVersion("1.0");
				((TransitionType) obj).getToolspecific().add(ttt);
			}
		}

	}

}
