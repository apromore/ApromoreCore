/*
 * Copyright © 2019 The University of Melbourne.
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

package org.apromore.plugin.portal.processdiscoverer.util;

import java.awt.*;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 05/08/2018.
 */
public class ColorGradient {

    private final Color lower_color;
    private final Color upper_color;

    public ColorGradient(Color lower_color, Color upper_color) {
        this.lower_color = lower_color;
        this.upper_color = upper_color;
    }

    public Color generateColor(double blending) {
        double inverse_blending = 1 - blending;

        float red =   (float) (upper_color.getRed()   * blending   +   lower_color.getRed()   * inverse_blending);
        float green = (float) (upper_color.getGreen() * blending   +   lower_color.getGreen() * inverse_blending);
        float blue =  (float) (upper_color.getBlue()  * blending   +   lower_color.getBlue()  * inverse_blending);

        return new Color (red / 255, green / 255, blue / 255);
    }
}
