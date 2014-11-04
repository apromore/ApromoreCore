package org.processmining.plugins.signaturediscovery.encoding;

import de.hpi.bpmn2_0.replay.LogUtility;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

/**
 * 
 * @author R. P. Jagadeesh Chandra Bose (JC)
 * @email  j.c.b.rantham.prabhakara@tue.nl
 * @date   02 July 2009
 * @version 1.0 
 * Bruce (4.11.2014): t
 *      - Modified for trace clustering purpose
 *      - Note: trace attribute "concept:name" is used as Id as commonly seen in event log
 *      - Only use activity name, not yet use lifecycle:transition (start, complete...)
 */
public class EncodeTraces {
    //Mapping from traceId to its char string representation
    //Note: traceId here is retrieved from concept:name attribute of trace, must be unique
    private Map<String,String> traceCharMap = new HashMap<>(); 
    private static EncodeTraces singleton=null; 
    
    protected EncodeTraces() {
        //do nothing (this is for Singleton)
    }
    
    public static EncodeTraces getEncodeTraces() {
        if (singleton == null) {
            singleton = new EncodeTraces();
        }
        return singleton;
    }

    public void read(XLog log) throws EncodingNotFoundException{
       /* --------------------------------------------------------
        * activitySet accumulates the set of distinct activity names
        --------------------------------------------------------- */
        Set<String> activitySet = new HashSet<String>();
        XAttributeMap attributeMap;
        for (XTrace trace : log) {
            for (XEvent event : trace) {
                attributeMap = event.getAttributes();
                activitySet.add(attributeMap.get("concept:name").toString()); // + "-" + attributeMap.get("lifecycle:transition").toString());
            }
        }

        try {
            /* --------------------------------------------------------
             * Build a mapping from activity name to encoded characters
             --------------------------------------------------------- */                  
            EncodeActivitySet encodeActivitySet = new EncodeActivitySet(activitySet);
            Map<String, String> activityCharMap = encodeActivitySet.getActivityCharMap();

            /* --------------------------------------------------------
             * Build a mapping from every traceId to its encoded character stream
             --------------------------------------------------------- */                
            StringBuilder charStreamBuilder = new StringBuilder();
            StringBuilder activityBuilder = new StringBuilder();
            for(XTrace trace : log){
                charStreamBuilder.setLength(0);
                for(XEvent event : trace) {
                    attributeMap = event.getAttributes();
                    activityBuilder.setLength(0);
                    activityBuilder.append(attributeMap.get("concept:name").toString()); //.append("-").append(attributeMap.get("lifecycle:transition").toString());
                    if(activityCharMap.containsKey(activityBuilder.toString())){
                        charStreamBuilder.append(activityCharMap.get(activityBuilder.toString()));
                    }else{
                        traceCharMap.clear();
                        throw new EncodingNotFoundException(activityBuilder.toString());
                    }
                }
                traceCharMap.put(LogUtility.getConceptName(trace), charStreamBuilder.toString());
            }
        } catch (ActivityOverFlowException e) {
            traceCharMap.clear();
            e.printStackTrace();
        }
    }
    
    public void read(Collection<XLog> logs) throws EncodingNotFoundException{
        for (XLog log : logs) {
            read(log);
        }
    } 
    
    public void reset() {
        traceCharMap.clear();
    }

    public String getCharStream(String traceId) {
        return traceCharMap.get(traceId);
    }
}
