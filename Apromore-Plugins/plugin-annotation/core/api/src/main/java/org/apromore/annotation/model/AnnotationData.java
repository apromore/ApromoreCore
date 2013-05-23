package org.apromore.annotation.model;

import java.math.BigDecimal;

/**
 * Annotation Data.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class AnnotationData {

    private String elementID;
    private BigDecimal oldX, oldY, newX, newY, oldH, oldW, newH, newW;

    public AnnotationData(String id,
            BigDecimal oldX, BigDecimal oldY, BigDecimal newX, BigDecimal newY,
            BigDecimal oldH, BigDecimal oldW, BigDecimal newH, BigDecimal newW) {
        this.elementID = id;
        this.oldX = oldX;
        this.oldY = oldY;
        this.newX = newX;
        this.newY = newY;
        this.oldH = oldH;
        this.oldW = oldW;
        this.newH = newH;
        this.newW = newW;
    }

    public String getElementID() {
        return elementID;
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
