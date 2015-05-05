package org.apromore.service;

import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.dao.model.User;
import org.apromore.dao.model.NativeType;
import org.apromore.dao.model.Process;
import org.apromore.helper.Version;


import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

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
    public void notifyObserver(User user, NativeType nativeType,ProcessModelVersion pmv, boolean delete) {
        for(Observer ob: observers){
            ob.update(user,nativeType,pmv,delete);
        }
    }
}
