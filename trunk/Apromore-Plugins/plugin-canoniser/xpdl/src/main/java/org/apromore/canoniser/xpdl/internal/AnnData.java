package org.apromore.canoniser.xpdl.internal;

/**
 * Used in the post processing of the annotation data of size and location.
 * eg. when transforming from EPC to BPMN the size of nodes and other elements change.
 */
public class AnnData {
    String elementID;
    double oldX, oldY, newX, newY, oldH, oldW, newH, newW;

    public AnnData(String id, double oldX, double oldY, double newX, double newY, double oldH, double oldW, double newH, double newW) {
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
}
