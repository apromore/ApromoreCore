package org.apromore.canoniser.adapters.canonical2pnml;

import org.apromore.anf.AnnotationType;
import org.apromore.anf.SimulationType;
import org.apromore.pnml.PlaceType;

public class TranslatePlaceToolspecific {
	DataHandler data;

	public void setValue(DataHandler data) {
		this.data = data;
	}

	public void translate(AnnotationType annotation, String cid) {
		if (annotation instanceof SimulationType) {
			SimulationType simu = (SimulationType) annotation;
			Object obj = data.get_pnmlRefMap_value(data.get_id_map_value(cid));
			if (obj instanceof PlaceType) {
				if (simu.getInitialMarking() != null) {

					((PlaceType) obj).setInitialMarking(new PlaceType.InitialMarking());
					((PlaceType) obj).getInitialMarking().setText(simu.getInitialMarking());
				}
			}

		}
	}

}
