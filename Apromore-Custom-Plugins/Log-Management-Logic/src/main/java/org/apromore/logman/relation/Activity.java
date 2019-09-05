package org.apromore.logman.relation;

import java.util.Map;
import java.util.Map.Entry;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XEvent;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.tuple.Tuples;

public class Activity implements Pair<XEvent, XEvent> {
    private Pair<XEvent,XEvent> pair;
    
    public Activity(XEvent source, XEvent target) {
        this.pair = Tuples.pair(source, target);
    }
    
    public long getDuration() {
        return (XTimeExtension.instance().extractTimestamp(pair.getTwo()).toInstant().getEpochSecond() - 
            XTimeExtension.instance().extractTimestamp(pair.getOne()).toInstant().getEpochSecond());
    }

    @Override
    public int compareTo(Pair<XEvent, XEvent> o) {
        return pair.compareTo(o);
    }

    @Override
    public XEvent getOne() {
        return pair.getOne();
    }

    @Override
    public XEvent getTwo() {
        return pair.getTwo();
    }

    @Override
    public void put(Map<XEvent, XEvent> map) {
        pair.put(map);
    }

    @Override
    public Entry<XEvent, XEvent> toEntry() {
        return pair.toEntry();
    }

    @Override
    public Pair<XEvent, XEvent> swap() {
        //Cannot swap
        return null;
    }
    
    @Override
    public int hashCode() {
        // Use Cantor pairing function
        int k1 = this.getOne().hashCode();
        int k2 = this.getTwo().hashCode();
        return (k1+k2)*(k1+k2+1)/2 + k2;
    }
    
}
