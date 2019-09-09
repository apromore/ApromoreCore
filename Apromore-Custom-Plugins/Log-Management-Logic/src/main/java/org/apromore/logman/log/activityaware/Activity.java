package org.apromore.logman.log.activityaware;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.impl.XAttributeContinuousImpl;
import org.deckfour.xes.model.impl.XAttributeTimestampImpl;
import org.deckfour.xes.model.impl.XEventImpl;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.tuple.Tuples;

public class Activity extends XEventImpl implements Map.Entry<XEvent,XEvent> {// Pair<XEvent, XEvent> {
    public static final String ACTIVITY_DURATION = "activity:duration";
    public static final String ACTIVITY_START_TIME = "activity:start_time";
    public static final String ACTIVITY_END_TIME = "activity:end_time";
    
    //private Pair<XEvent,XEvent> pair;
    XEvent start, complete;
    private boolean useComplete;
    
    public Activity(XEvent event) {
    	this(event,event,true);
    }
    
    public Activity(XEvent start, XEvent complete) {
    	this(start,complete,true);
    }
    
    public Activity(XEvent start, XEvent complete, boolean useComplete) {
    	super();
    	this.setAttributes(useComplete ? complete.getAttributes() : start.getAttributes());
        //this.pair = Tuples.pair(start, complete);
    	this.start = start;
    	this.complete = complete;
        this.useComplete = useComplete;
        this.getAttributes().put(ACTIVITY_DURATION, new XAttributeContinuousImpl(ACTIVITY_DURATION, this.getDuration()));
        this.getAttributes().put(ACTIVITY_DURATION, new XAttributeTimestampImpl(ACTIVITY_START_TIME, this.getStartTime()));
        this.getAttributes().put(ACTIVITY_END_TIME, new XAttributeTimestampImpl(ACTIVITY_END_TIME, this.getEndTime()));
    }
    
    public long getDuration() {
        return (getEndTime().toInstant().getEpochSecond() - getStartTime().toInstant().getEpochSecond());
    }
    
    public Date getStartTime() {
        return XTimeExtension.instance().extractTimestamp(start);
    }
    
    public Date getEndTime() {
        return XTimeExtension.instance().extractTimestamp(complete);
    }
    
    public boolean isUseComplete() {
    	return this.useComplete;
    }

	@Override
	public XEvent getKey() {
		return start;
	}
	
	public void setKey(XEvent newKey) {
		this.start = newKey;
	}

	@Override
	public XEvent getValue() {
		return complete;
	}

	@Override
	public XEvent setValue(XEvent value) {
		XEvent oldEvent = this.complete;
		this.complete = value;
		return oldEvent;
	}

//    @Override
//    public int compareTo(Pair<XEvent, XEvent> o) {
//        return pair.compareTo(o);
//    }
//
//    @Override
//    public XEvent getOne() {
//        return pair.getOne();
//    }
//
//    @Override
//    public XEvent getTwo() {
//        return pair.getTwo();
//    }
//
//    @Override
//    public void put(Map<XEvent, XEvent> map) {
//        pair.put(map);
//    }
//
//    @Override
//    public Entry<XEvent, XEvent> toEntry() {
//        return pair.toEntry();
//    }
//
//    @Override
//    public Pair<XEvent, XEvent> swap() {
//        //Cannot swap
//        return null;
//    }
    
}
