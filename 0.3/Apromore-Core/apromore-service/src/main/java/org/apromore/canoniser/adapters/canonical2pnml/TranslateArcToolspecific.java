package org.apromore.canoniser.adapters.canonical2pnml;

import org.apromore.anf.AnnotationType;
import org.apromore.anf.SimulationType;
import org.apromore.pnml.ArcType;
import org.apromore.pnml.PositionType;

import java.math.BigDecimal;

//import org.apromore.anf.AdditionalEdgeInformationType;

public class TranslateArcToolspecific {
	DataHandler data;

	public void setValue(DataHandler data) {
		this.data = data;
	}

	public void translate(AnnotationType annotation, String cid) {
		if (annotation instanceof SimulationType) {
			SimulationType simu = (SimulationType) annotation;
			org.apromore.pnml.ArcToolspecificType pnmlArcToolspecific = new org.apromore.pnml.ArcToolspecificType();
			pnmlArcToolspecific.setTool("WoPeD");
			pnmlArcToolspecific.setVersion("1.0");
			if (simu.getProbability() != null) {
				double prob = simu.getProbability();
				pnmlArcToolspecific.setProbability(prob);
				if (prob == 1.0 || prob == 0.0) {
					pnmlArcToolspecific.setDisplayProbabilityOn(false);
				} else {
					pnmlArcToolspecific.setDisplayProbabilityOn(true);
				}
			}
			PositionType pt = new PositionType();
			pt.setX(BigDecimal.valueOf(Double.valueOf(500.0)));
			pt.setY(BigDecimal.valueOf(Double.valueOf(0.0)));
			pnmlArcToolspecific.setDisplayProbabilityPosition(pt);

			Object obj = data.get_pnmlRefMap_value(data.get_id_map_value(cid));

			if (obj instanceof ArcType) {

				((ArcType) obj).getToolspecific().add(pnmlArcToolspecific);

			}
		}

	}

}
