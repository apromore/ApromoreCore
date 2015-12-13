/*
 * Copyright © 2009-2015 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

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
