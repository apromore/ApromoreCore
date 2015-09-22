package org.apromore.service;

import org.apromore.dao.model.ProcessModelVersion;

/**
 * Created by corno on 5/07/2014.
 */
public interface Observable {
    void addObserver(Observer ob);

    void removeObserver(Observer ob);

    void notifyUpdate(ProcessModelVersion pmv);

    void notifyDelete(ProcessModelVersion pmv);
}
