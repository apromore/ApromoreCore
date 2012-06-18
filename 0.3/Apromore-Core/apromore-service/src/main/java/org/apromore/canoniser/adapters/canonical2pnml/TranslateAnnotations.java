package org.apromore.canoniser.adapters.canonical2pnml;

import org.apromore.anf.AnnotationType;
import org.apromore.anf.AnnotationsType;
import org.apromore.anf.GraphicsType;
import org.apromore.anf.SimulationType;
import org.apromore.pnml.ArcType;
import org.apromore.pnml.PlaceType;
import org.apromore.pnml.TransitionType;

public class TranslateAnnotations {
	DataHandler data;

	public void setValue(DataHandler data) {
		this.data = data;
	}

	public void mapNodeAnnotations(AnnotationsType annotations) {
		for (AnnotationType annotation : annotations.getAnnotation()) {
			if (data.get_nodeRefMap().containsKey(annotation.getCpfId())
					|| data.get_edgeRefMap().containsKey(annotation.getCpfId())) {
                String cid = annotation.getCpfId();

				if (annotation instanceof GraphicsType) {
					TranslateNodeAnnotations tna = new TranslateNodeAnnotations();
					tna.setValue(data);
					tna.translate(annotation, cid);

				}
				if (annotation instanceof SimulationType) {
					Object obj = data.get_pnmlRefMap_value(data.get_id_map_value(cid));
					if (obj instanceof TransitionType) {
						TranslateTransitionToolspecific ttt = new TranslateTransitionToolspecific();
						ttt.setValue(data);
						ttt.translate(annotation, cid);
					} else if (obj instanceof PlaceType) {
						TranslatePlaceToolspecific tpt = new TranslatePlaceToolspecific();
						tpt.setValue(data);
						tpt.translate(annotation, cid);
					} else if (obj instanceof ArcType) {
						TranslateArcToolspecific tat = new TranslateArcToolspecific();
						tat.setValue(data);
						tat.translate(annotation, cid);
					}
				}

			}

		}
	}

}
