/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2013 - 2017 Queensland University of Technology.
 * Copyright (C) 2018, 2020 The University of Melbourne.
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

package org.apromore.annotation.model;

import java.math.BigDecimal;

/**
 * Annotation Data.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class AnnotationData {

    private BigDecimal oldX, oldY, newX, newY, oldH, oldW, newH, newW;

    public AnnotationData(
            BigDecimal oldX, BigDecimal oldY, BigDecimal newX, BigDecimal newY,
            BigDecimal oldH, BigDecimal oldW, BigDecimal newH, BigDecimal newW) {
        this.oldX = oldX;
        this.oldY = oldY;
        this.newX = newX;
        this.newY = newY;
        this.oldH = oldH;
        this.oldW = oldW;
        this.newH = newH;
        this.newW = newW;
    }

    public BigDecimal getOldX() {
        return oldX;
    }

    public BigDecimal getOldY() {
        return oldY;
    }

    public BigDecimal getNewX() {
        return newX;
    }

    public BigDecimal getNewY() {
        return newY;
    }

    public BigDecimal getOldH() {
        return oldH;
    }

    public BigDecimal getOldW() {
        return oldW;
    }

    public BigDecimal getNewH() {
        return newH;
    }

    public BigDecimal getNewW() {
        return newW;
    }
}
