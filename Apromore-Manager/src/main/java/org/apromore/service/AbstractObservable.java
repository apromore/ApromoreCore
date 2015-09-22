package org.apromore.service;

import org.apromore.dao.model.ProcessModelVersion;

import java.util.HashSet;

/**
 * Created by corno on 7/07/2014.
 */
public abstract class AbstractObservable implements Observable {
    private HashSet<Observer> observers=new HashSet<>();

    @Override
    public void addObserver(Observer ob) {
        observers.add(ob);
    }

    @Override
    public void removeObserver(Observer ob) {
        observers.remove(ob);
    }

    @Override
    public void notifyUpdate(ProcessModelVersion pmv) {
        for(Observer ob: observers){
            ob.notifyUpdate(pmv);
        }
    }

    @Override
    public void notifyDelete(ProcessModelVersion pmv) {
        for(Observer ob: observers){
            ob.notifyDelete(pmv);
        }
    }
}
