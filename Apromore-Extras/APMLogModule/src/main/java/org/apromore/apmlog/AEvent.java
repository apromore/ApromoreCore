package org.apromore.apmlog;

import org.apromore.apmlog.util.Util;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.time.ZonedDateTime;

/**
 * @author Chii Chang (11/2019)
 */
public class AEvent {
    private String name = "";
    private long timestampMilli = 0;
    private String lifecycle = "";
    private String resource = "";
    private UnifiedMap<String, String> attributeMap;
    private UnifiedSet<String> attributeNameSet;
    private String timeZone = "";//2019-10-20

    public AEvent(String name, long timestampMilli, String lifecycle, String resource,
                  UnifiedMap<String, String> attributeMap,
                  UnifiedSet<String> attributeNameSet,
                  String timeZone) {
        this.name = name;
        this.timestampMilli = timestampMilli;
        this.lifecycle = lifecycle;
        this.resource = resource;
        this.attributeMap = attributeMap;
        this.attributeNameSet = attributeNameSet;
        this.timeZone = timeZone;
    }

    public AEvent(XEvent xEvent) {
        XAttributeMap xAttributeMap = xEvent.getAttributes();

        attributeMap = new UnifiedMap<>();
        attributeNameSet = new UnifiedSet<>();

        for(String key : xAttributeMap.keySet()) {
            if(key.toLowerCase().equals("concept:name")) this.name = xAttributeMap.get(key).toString();
            else if(key.toLowerCase().equals("lifecycle:transition")) this.lifecycle = xAttributeMap.get(key).toString().toLowerCase();
            else if(key.toLowerCase().equals("org:resource")) this.resource = xAttributeMap.get(key).toString();
            else if(!key.toLowerCase().equals("time:timestamp")){
                this.attributeMap.put(key, xAttributeMap.get(key).toString());
                this.attributeNameSet.put(key);
            }

        }
        if(xEvent.getAttributes().containsKey("time:timestamp")) {
            ZonedDateTime zdt = Util.zonedDateTimeOf(xEvent);//2019-10-20
            this.timestampMilli = Util.epochMilliOf(zdt);//2019-10-20
            this.timeZone = zdt.getZone().getId();//2019-10-20
        }
    }




    public void setName(String name) {
        this.name = name;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public void setLifecycle(String lifecycle) {
        this.lifecycle = lifecycle;
    }

    public String getName() {
        return name;
    }

    public String getResource() {
        return resource;
    }

    public String getLifecycle() {
        return lifecycle;
    }

    public long getTimestampMilli() {
        return timestampMilli;
    }

    public String getAttributeValue(String attributeKey) {
        return this.attributeMap.get(attributeKey);
    }

    public UnifiedMap<String, String> getAttributeMap() {
        return attributeMap;
    }

    public UnifiedSet<String> getAttributeNameSet() {
        return attributeNameSet;
    }

    public String getTimeZone() { //2019-10-20
        return timeZone;
    }

    public AEvent clone()  {
        String clnName = this.name;
        long clnTimestampMilli = this.timestampMilli;
        String clnLifecycle = this.lifecycle;
        String clnResource = this.resource;
        UnifiedMap<String, String> clnAttributeMap = new UnifiedMap<>(this.attributeMap);
        UnifiedSet<String> clnAttributeNameSet = new UnifiedSet<>(this.attributeNameSet);
        String clnTimeZone = this.timeZone;

        AEvent clnEvent = new AEvent(clnName, clnTimestampMilli, clnLifecycle, clnResource,
                clnAttributeMap, clnAttributeNameSet, clnTimeZone);
        return clnEvent;
    }
}
