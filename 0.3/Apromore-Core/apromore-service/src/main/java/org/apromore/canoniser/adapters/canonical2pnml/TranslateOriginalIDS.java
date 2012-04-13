package org.apromore.canoniser.adapters.canonical2pnml;

import java.math.BigInteger;

import org.apromore.pnml.ArcType;
import org.apromore.pnml.PlaceType;
import org.apromore.pnml.TransitionType;

public class TranslateOriginalIDS {
	DataHandler data;
	long ids;
	String originalid;

	public void setValues(DataHandler data, long ids) {
		this.data = data;
		this.ids = ids;
	}

	public void mapIDS() {
		for (PlaceType place : data.getNet().getPlace()) {
			if (!place.getId().contains("CENTER_PLACE_")) {
				originalid = data.get_originalid_map_value(BigInteger
						.valueOf(Long.valueOf(place.getId())));
				place.setId(originalid);
			}
		}
		for (TransitionType tran : data.getNet().getTransition()) {
			if (!tran.getId().contains("op")) {
				originalid = data.get_originalid_map_value(BigInteger
						.valueOf(Long.valueOf(tran.getId())));
				tran.setId(originalid);
			}
		}
		for (ArcType arc : data.getNet().getArc()) {
			if (!arc.getId().contains("a")) {
				originalid = data.get_originalid_map_value(BigInteger
						.valueOf(Long.valueOf(arc.getId())));
				arc.setId(originalid);
			}
		}
	}
}
