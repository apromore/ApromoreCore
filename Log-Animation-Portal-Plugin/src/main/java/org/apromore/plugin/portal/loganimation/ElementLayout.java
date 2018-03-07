package org.apromore.plugin.portal.loganimation;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 3/11/17.
 */
public class ElementLayout {

    private String elementName;
    private String elementColor;

    private double width;
    private double height;
    private double x;
    private double y;

    public ElementLayout(String elementName, double width, double height, double x, double y, String elementColor) {
        this.elementName = elementName;
        this.elementColor = elementColor;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
    }

    public String getElementName() {
        return elementName;
    }

    public String getElementColor() {
        return elementColor;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

}
