/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2017 Queensland University of Technology.
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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

package org.apromore.plugin.portal.loganimation;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 3/11/17.
 */
public class ElementLayout {

    private String elementName;
    private String elementColor;
    private String elementId;

    private double width;
    private double height;
    private double x;
    private double y;

    public ElementLayout(String elementName, String elementId, double width, double height, double x, double y, String elementColor) {
        this.elementName = elementName;
        this.elementId = elementId;
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
