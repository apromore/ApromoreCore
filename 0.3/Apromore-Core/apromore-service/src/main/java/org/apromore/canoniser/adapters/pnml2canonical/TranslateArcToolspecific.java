package org.apromore.canoniser.adapters.pnml2canonical;

import org.apromore.anf.CpfTypeEnum;
import org.apromore.anf.SimulationType;
import org.apromore.pnml.ArcToolspecificType;

import java.util.List;

public class TranslateArcToolspecific {
	DataHandler data;
	long ids;
	String cpfId = null;

	public void setValues(DataHandler data, long ids) {
		this.data = data;
		this.ids = ids;
	}


	public void translate(Object obj) {
		org.apromore.pnml.ArcType element = (org.apromore.pnml.ArcType) obj;
		List<ArcToolspecificType> pnmlArcToolSpecific = element.getToolspecific();

		SimulationType probabillity = new SimulationType();

		cpfId = data.get_id_map_value(element.getId());
		if (element.getToolspecific() != null) {
			for (Object obj1 : pnmlArcToolSpecific) {
				if (obj1 instanceof ArcToolspecificType) {
					if (((ArcToolspecificType) obj1).getProbability() != null) {
						probabillity.setCpfId(cpfId);
						probabillity.setCpfType(CpfTypeEnum.fromValue("EdgeType"));
						probabillity.setProbability(((ArcToolspecificType) obj1).getProbability());
						data.getAnnotations().getAnnotation().add(probabillity);
					}

				}
			}

		}
	}

	public long getIds() {
		return ids;
	}
}
