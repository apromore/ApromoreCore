package org.apromore.canoniser.adapters;

/**
 * Created by IntelliJ IDEA.
 * User: lappie
 * Date: 4/08/11
 * Time: 3:18 PM
 * To change this template use File | Settings | File Templates.
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
