/*
 * Copyright © 2009-2018 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.canoniser.pnml.internal.canonical2pnml;

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
