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

package org.apromore.canoniser.pnml.internal.pnml2canonical;

import java.util.List;

import org.apromore.anf.CpfTypeEnum;
import org.apromore.anf.SimulationType;
import org.apromore.pnml.ArcToolspecificType;

public abstract class TranslateArcToolspecific {

    static public void translate(Object obj, DataHandler data) {
        org.apromore.pnml.ArcType element = (org.apromore.pnml.ArcType) obj;
        List<ArcToolspecificType> pnmlArcToolSpecific = element.getToolspecific();

        SimulationType probabillity = new SimulationType();

        String cpfId = data.get_id_map_value(element.getId());
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

}
