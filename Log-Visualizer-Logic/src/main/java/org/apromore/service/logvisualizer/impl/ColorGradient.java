package org.apromore.service.logvisualizer.impl;

import java.awt.*;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 6/10/17.
 */
public class ColorGradient {

    private final Color lower_color;
    private final Color upper_color;

    public static void main(String[] args) {
        ColorGradient frequency_gradient = new ColorGradient(new Color(100, 100, 100), new Color(41, 41, 41));
        ColorGradient duration_gradient = new ColorGradient(new Color(100, 100, 100), new Color(139, 0, 0));

        Color f = frequency_gradient.generateColor(0.5);
        Color d = duration_gradient.generateColor(0.5);
        System.out.println();
    }

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
