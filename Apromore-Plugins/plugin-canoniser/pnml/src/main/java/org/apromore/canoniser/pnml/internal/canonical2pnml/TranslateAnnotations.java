/*
 * Copyright © 2009-2017 The Apromore Initiative.
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
