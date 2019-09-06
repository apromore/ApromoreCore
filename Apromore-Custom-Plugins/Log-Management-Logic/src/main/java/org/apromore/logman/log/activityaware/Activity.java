package org.apromore.logman.log.activityaware;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XAttributable;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.impl.XAttributeContinuousImpl;
import org.deckfour.xes.model.impl.XAttributeMapImpl;
import org.deckfour.xes.model.impl.XAttributeTimestampImpl;
import org.deckfour.xes.util.XAttributeUtils;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.tuple.Tuples;

public class Activity implements Pair<XEvent, XEvent>, XAttributable {
    public static final String ACTIVITY_DURATION = "duration";
    public static final String ACTIVITY_START_TIME = "start_time";
    public static final String ACTIVITY_END_TIME = "end_time";
    
    private Pair<XEvent,XEvent> pair;
    private XAttributeMap attributes;
    
    public Activity(XEvent source, XEvent target) {
        this.pair = Tuples.pair(source, target);
        this.attributes = new XAttributeMapImpl();
        attributes.put(ACTIVITY_DURATION, new XAttributeContinuousImpl(ACTIVITY_DURATION, this.getDuration()));
        attributes.put(ACTIVITY_DURATION, new XAttributeTimestampImpl(ACTIVITY_START_TIME, this.getStartTime()));
        attributes.put(ACTIVITY_END_TIME, new XAttributeTimestampImpl(ACTIVITY_END_TIME, this.getEndTime()));
    }
    
    public long getDuration() {
        return (getEndTime().toInstant().getEpochSecond() - getStartTime().toInstant().getEpochSecond());
    }
    
    public Date getStartTime() {
        return XTimeExtension.instance().extractTimestamp(pair.getOne());
    }
    
    public Date getEndTime() {
        return XTimeExtension.instance().extractTimestamp(pair.getTwo());
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

    @Override
    public XAttributeMap getAttributes() {
        return this.attributes;
    }

    @Override
    public Set<XExtension> getExtensions() {
        return XAttributeUtils.extractExtensions(attributes);
    }

    @Override
    public void setAttributes(XAttributeMap arg0) {
        this.attributes = arg0;
    }

    @Override
    public boolean hasAttributes() {
        // TODO Auto-generated method stub
        return false;
    }
    
}
