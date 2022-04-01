/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.plugin.portal.processdiscoverer.vis;

import java.awt.Color;

/**
 * Blend two colors (start and end) with different level of strength between the two.
 * The blend ratio determines the color strength, 0 is start color and 1 is end color
 * For example, when start is light red and end is dark red, blend ratio close to 0
 * will produce ligher red and close to 1 will produce darker red. 
 */
public class LinearColorBlender {

    private final Color base_color_start;
    private final Color base_color_end;

    public LinearColorBlender(Color base_color_start, Color base_color_end) {
        this.base_color_start = base_color_start;
        this.base_color_end = base_color_end;
    }

    public Color blend(double blendRatio) {
        double inverseBlendRatio = 1 - blendRatio;
        float red =   (float) (base_color_end.getRed()   * blendRatio   +   base_color_start.getRed()   * inverseBlendRatio);
        float green = (float) (base_color_end.getGreen() * blendRatio   +   base_color_start.getGreen() * inverseBlendRatio);
        float blue =  (float) (base_color_end.getBlue()  * blendRatio   +   base_color_start.getBlue()  * inverseBlendRatio);
        return new Color (red/255, green/255, blue/255);
    }
}
