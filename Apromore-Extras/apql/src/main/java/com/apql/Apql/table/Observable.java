package com.apql.Apql.table;

import java.awt.*;

/**
 * Created by corno on 10/08/2014.
 */
public interface Observable {
    public void notifyObservers(Component parent, int... widthColumn);
}
