/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2015 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.service.loganimation.replay;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XTraceImpl;

/*
* Similar to XTrace but with added dropFirst and equals method
*/
public class XTrace2 {
    private XTrace xTrace = null; //original trace
    
    public XTrace2(XTrace xtrace) {
        this.xTrace = xtrace;
    }
    
    //Return XEvent at ith position, starting from 0.
    public XEvent get(int i) {
        return xTrace.get(i);
    }
    
    public String getId() {
        //return xTrace.getAttributes().get("concept:name").toString();
        return LogUtility.getConceptName(xTrace).toString();
        
    }
    
    public String getFirstActName() {
        return xTrace.get(0).getAttributes().get("concept:name").toString();
    }
    
    public XTrace getTrace() {
        return xTrace;
    }
    
    public boolean isEmpty() {
        return xTrace.isEmpty();
    }
    
    public int size() {
        return xTrace.size();
    }

    //This method creates a new XTrace copied from previous one without the first event
    //Return a new StatefulTrace similar to the this trace but without the first event
    //The returned trace contains event references pointing to events in the original trace
    public XTrace2 dropFirst() {
        XTrace copyTrace = null;
        if (xTrace.size() > 0) {
            copyTrace =  (XTrace)xTrace.clone();
            //copyTrace =  new XTraceImpl(xTrace.getAttributes());
            copyTrace.clear();
            for (int i=1;i<xTrace.size();i++) {
                copyTrace.add(xTrace.get(i));
            }
        }
        return new XTrace2(copyTrace);
    }
    
    /*
    * Trace comparison is based on List interface
    * Two lists are defined to be equal if they contain the same elements in the same order
    * (Two elements e1 and e2 are equal if (e1==null ? e2==null : e1.equals(e2)).)     
    */
    public boolean equals(XTrace2 otherTrace) {
        return (this.xTrace.equals(otherTrace.getTrace()));
    }
    
    /*
    * Return a trace containing a number of leading events extracted
    * from this trace. Taking until the end of current trace if it is 
    * shorter than the input number of events
    */
    public XTrace2 getSubTrace(int numberOfEvents) {
        XTrace newTrace =  new XTraceImpl(xTrace.getAttributes());
        for (int i=0;i<numberOfEvents;i++) {
            if (i>=xTrace.size()) {
                break;
            }
            newTrace.add(xTrace.get(i));
        }
        return new XTrace2(newTrace);
    }
}
