/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.canoniser.pnml.internal.pnml2canonical;

import java.util.List;

import org.apromore.anf.GraphicsType;
import org.apromore.anf.LineType;
import org.apromore.anf.PositionType;


public class TranslateEdgeAnnotations {
    DataHandler data;

    public void setValues(DataHandler data) {
        this.data = data;
    }

    public void addEdgeAnnotations(Object obj) {
        GraphicsType graphT = new GraphicsType();
        LineType line = new LineType();
        String cpfId = null;

        org.apromore.pnml.ArcType element = (org.apromore.pnml.ArcType) obj;

        cpfId = data.get_id_map_value(element.getId());

        if (element.getGraphics() != null) {

            if (element.getGraphics().getLine() != null) {
                line.setColor(element.getGraphics().getLine().getColor());
                line.setShape(element.getGraphics().getLine().getShape());
                line.setStyle(element.getGraphics().getLine().getStyle());
                line.setWidth(element.getGraphics().getLine().getWidth());
                graphT.setLine(line);
            }

            if (element.getGraphics().getPosition() != null) {
                List<org.apromore.pnml.PositionType> pos1 = element
                        .getGraphics().getPosition();
                for (Object obj1 : pos1) {
                    org.apromore.pnml.PositionType position = (org.apromore.pnml.PositionType) obj1;
                    PositionType pos = new PositionType();
                    if (position.getX() != null) {
                        pos.setX(position.getX());

                    }
                    if (position.getY() != null) {
                        pos.setY(position.getY());
                    }

                    graphT.getPosition().add(pos);
                }

            }

            graphT.setCpfId(cpfId);
            data.getAnnotations().getAnnotation().add(graphT);
        }
    }

}
