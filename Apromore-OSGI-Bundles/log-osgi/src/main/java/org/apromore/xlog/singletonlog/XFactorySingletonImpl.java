package org.apromore.xlog.singletonlog;

import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.id.XID;
import org.deckfour.xes.id.XIDFactory;
import org.deckfour.xes.model.*;
import org.deckfour.xes.model.impl.XAttributeContainerImpl;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.map.mutable.primitive.DoubleObjectHashMap;
import org.eclipse.collections.impl.map.mutable.primitive.LongObjectHashMap;

import java.net.URI;
import java.util.Date;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 4/08/2016.
 */
public class XFactorySingletonImpl implements XFactory {

    private XID fakeXID = XIDFactory.instance().createId();
    private UnifiedMap<String, String> map = new UnifiedMap<>();
    private LongObjectHashMap<Date> dateMap = new LongObjectHashMap<>();
    private DoubleObjectHashMap<Double> doubleMap = new DoubleObjectHashMap<>();
    private LongObjectHashMap<Long> longMap = new LongObjectHashMap<>();

//    private Table<String, String, XAttributeLiteral> attributeLiteralMap = HashBasedTable.create();
//    private Table<String, Double, XAttributeContinuous> attributeContinuousMap = HashBasedTable.create();
//    private Table<String, Long, XAttributeDiscrete> attributeDiscreteMap = HashBasedTable.create();

//    private UnifiedMap<String, UnifiedMap<String, XAttributeLiteral>> attributeLiteralMap = new UnifiedMap();
//    private UnifiedMap<String, DoubleObjectHashMap<XAttributeContinuous>> attributeContinuousMap = new UnifiedMap();
//    private UnifiedMap<String, LongObjectHashMap<XAttributeDiscrete>> attributeDiscreteMap = new UnifiedMap();
//
//    private UnifiedMap<String, XAttributeLiteral> mapLiteral;
//    private DoubleObjectHashMap<XAttributeContinuous> mapContinuous;
//    private LongObjectHashMap<XAttributeDiscrete> mapDiscrete;

    private boolean useLinkedList = false;

    public XFactorySingletonImpl() { }

    public XFactorySingletonImpl(boolean useLinkedList) {this.useLinkedList = useLinkedList;}

    public String getAuthor() {
        return "Raffaele Conforti";
    }

    public String getDescription() {
        return "Optimized factory meant for importing large CSV files, not indicated for log modification as it uses singletons.";
    }

    public String getName() {
        return "Standard / singleton";
    }

    public URI getUri() {
        return URI.create("http://www.xes-standard.org/");
    }

    public String getVendor() {
        return "xes-standard.org";
    }

    public XLog createLog() {
        if(useLinkedList) return new XLogLinkedListSingletonImpl(new XAttributeMapSingletonImpl());
        else return new XLogArrayListSingletonImpl(new XAttributeMapSingletonImpl());
    }

    public XLog createLog(XAttributeMap attributes) {
        if(useLinkedList) return new XLogLinkedListSingletonImpl(attributes);
        else return new XLogArrayListSingletonImpl(attributes);
    }

    public XTrace createTrace() {
        if(useLinkedList) return new XTraceLinkedListSingletonImpl(new XAttributeMapSingletonImpl());
        else return new XTraceArrayListSingletonImpl(new XAttributeMapSingletonImpl());
    }

    public XTrace createTrace(XAttributeMap attributes) {
        if(useLinkedList) return new XTraceLinkedListSingletonImpl(attributes);
        else return new XTraceArrayListSingletonImpl(attributes);
    }

    public XEvent createEvent() {
        return new XEventSingletonImpl(fakeXID);
    }

    public XEvent createEvent(XAttributeMap attributes) {
        return new XEventSingletonImpl(fakeXID, attributes);
    }

    public XEvent createEvent(XID id, XAttributeMap attributes) {
        return new XEventSingletonImpl(id, attributes);
    }

    public XAttributeMap createAttributeMap() {
        return new XAttributeMapSingletonImpl();
    }

    public XAttributeBoolean createAttributeBoolean(String key, boolean value, XExtension extension) {
        return new org.deckfour.xes.model.impl.XAttributeBooleanImpl(getKey(key), value, extension);
    }

    public XAttributeContinuous createAttributeContinuous(String key, double value, XExtension extension) {
        String keyMap = getKey(key);
        Double valueMap = doubleMap.get(value);
        if(valueMap == null) {
            valueMap = value;
            doubleMap.put(value, valueMap);
        }
        XAttributeContinuous attributeContinuous = getAttributeContinuousMap(keyMap, valueMap);
        if(attributeContinuous == null) {
            attributeContinuous = new XAttributeContinuousSingletonImpl(keyMap, valueMap, extension);
            putAttributeContinuousMap(keyMap, valueMap, attributeContinuous);
        }
        return attributeContinuous;
    }

    public XAttributeDiscrete createAttributeDiscrete(String key, long value, XExtension extension) {
        String keyMap = getKey(key);
        Long valueMap = longMap.get(value);
        if(valueMap == null) {
            valueMap = value;
            longMap.put(value, valueMap);
        }
        XAttributeDiscrete attributeDiscrete = getAttributeDiscreteMap(keyMap, valueMap);
        if(attributeDiscrete == null) {
            attributeDiscrete = new XAttributeDiscreteSingletonImpl(keyMap, valueMap, extension);
            putAttributeDiscreteMap(keyMap, valueMap, attributeDiscrete);
        }
        return attributeDiscrete;
    }

    public XAttributeLiteral createAttributeLiteral(String key, String value, XExtension extension) {
        String keyMap = getKey(key);
        String valueMap = getKey(value);
        XAttributeLiteral attributeLiteral = getAttributeLiteralMap(keyMap, valueMap);
        if(attributeLiteral == null) {
            attributeLiteral = new org.deckfour.xes.model.impl.XAttributeLiteralImpl(keyMap, valueMap, extension);
            putAttributeLiteralMap(keyMap, valueMap, attributeLiteral);
        }
        return attributeLiteral;
    }

    public XAttributeTimestamp createAttributeTimestamp(String key, Date value, XExtension extension) {
        Date valueMap = dateMap.get(value.getTime());
        if(valueMap == null) {
            valueMap = value;
            dateMap.put(value.getTime(), valueMap);
        }
        return new org.deckfour.xes.model.impl.XAttributeTimestampImpl(getKey(key), valueMap, extension);
    }

    public XAttributeTimestamp createAttributeTimestamp(String key, long millis, XExtension extension) {
        Date valueMap = dateMap.get(millis);
        if(valueMap == null) {
            valueMap = new Date(millis);
            dateMap.put(millis, valueMap);
        }
        return new org.deckfour.xes.model.impl.XAttributeTimestampImpl(getKey(key), valueMap, extension);
    }

    public XAttributeID createAttributeID(String key, XID value, XExtension extension) {
        return new org.deckfour.xes.model.impl.XAttributeIDImpl(getKey(key), value, extension);
    }

    public XAttributeList createAttributeList(String key, XExtension extension) {
        return new org.deckfour.xes.model.impl.XAttributeListImpl(getKey(key), extension);
    }

    public XAttributeContainer createAttributeContainer(String key, XExtension extension) {
        return new XAttributeContainerImpl(getKey(key), extension);
    }

    private String getKey(String key) {
        String keyMap = map.get(key);
        if(keyMap == null) {
            keyMap = key;
            map.put(key, keyMap);
        }
        return keyMap;
    }

    private XAttributeLiteral getAttributeLiteralMap(String key1, String key2) {
//        return attributeLiteralMap.get(key1, key2);
//        if((mapLiteral = attributeLiteralMap.get(key1)) != null) return mapLiteral.get(key2);
        return null;
    }

    private void putAttributeLiteralMap(String key1, String key2, XAttributeLiteral value) {
//        attributeLiteralMap.put(key1, key2, value);
//        if((mapLiteral = attributeLiteralMap.get(key1)) == null) {
//            mapLiteral = new UnifiedMap<>();
//            attributeLiteralMap.put(key1, mapLiteral);
//        }
//        mapLiteral.put(key2, value);
    }

    private XAttributeContinuous getAttributeContinuousMap(String key1, Double key2) {
//        return attributeContinuousMap.get(key1, key2);
//        if((mapContinuous = attributeContinuousMap.get(key1)) != null) return mapContinuous.get(key2);
        return null;
    }

    private void putAttributeContinuousMap(String key1, Double key2, XAttributeContinuous value) {
//        attributeContinuousMap.put(key1, key2, value);
//        if((mapContinuous = attributeContinuousMap.get(key1)) == null) {
//            mapContinuous = new DoubleObjectHashMap<>();
//            attributeContinuousMap.put(key1, mapContinuous);
//        }
//        mapContinuous.put(key2, value);
    }

    private XAttributeDiscrete getAttributeDiscreteMap(String key1, Long key2) {
//        return attributeDiscreteMap.get(key1, key2);
//        if((mapDiscrete = attributeDiscreteMap.get(key1)) != null) return mapDiscrete.get(key2);
        return null;
    }

    private void putAttributeDiscreteMap(String key1, Long key2, XAttributeDiscrete value) {
//        attributeDiscreteMap.put(key1, key2, value);
//        if((mapDiscrete = attributeDiscreteMap.get(key1)) == null) {
//            mapDiscrete = new LongObjectHashMap<>();
//            attributeDiscreteMap.put(key1, mapDiscrete);
//        }
//        mapDiscrete.put(key2, value);
    }

}
