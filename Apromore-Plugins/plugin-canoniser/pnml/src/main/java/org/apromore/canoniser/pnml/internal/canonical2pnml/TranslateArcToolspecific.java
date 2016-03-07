/*
 * Copyright © 2009-2016 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.canoniser.pnml.internal.canonical2pnml;

import java.math.BigDecimal;

import org.apromore.anf.AnnotationType;
import org.apromore.anf.SimulationType;
import org.apromore.pnml.ArcType;
import org.apromore.pnml.PositionType;

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
