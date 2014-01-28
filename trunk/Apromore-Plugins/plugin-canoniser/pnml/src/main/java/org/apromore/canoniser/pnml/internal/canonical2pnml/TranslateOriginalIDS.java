package org.apromore.canoniser.pnml.internal.canonical2pnml;

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
                originalid = data.get_originalid_map_value(BigInteger.valueOf(Long.valueOf(place.getId())));
                if (originalid != null) {
                    place.setId(originalid);
                }
            }
        }
        for (TransitionType tran : data.getNet().getTransition()) {
            if (!tran.getId().contains("op")) {
                originalid = data.get_originalid_map_value(BigInteger.valueOf(Long.valueOf(tran.getId())));
                if (originalid != null) {
                    tran.setId(originalid);
                }
            }
        }
        for (ArcType arc : data.getNet().getArc()) {
            if (!arc.getId().contains("a")) {
                originalid = data.get_originalid_map_value(BigInteger.valueOf(Long.valueOf(arc.getId())));
                if (originalid != null) {
                    arc.setId(originalid);
                }
            }
        }
    }
}
