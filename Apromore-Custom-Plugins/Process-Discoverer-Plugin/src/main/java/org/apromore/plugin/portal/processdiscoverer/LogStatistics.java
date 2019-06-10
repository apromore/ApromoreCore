package org.apromore.plugin.portal.processdiscoverer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.raffaeleconforti.foreignkeydiscovery.Pair;

public class LogStatistics {
	private XLog log;
	private long logMinTimestamp; 
	private long logMaxTimestamp;
	private String classifyingAttribute;
	private Map<String, Map<String, Integer>> attributeStats = new HashMap<>();
	private Map<String, Map<String, Integer>> relationStats = new HashMap<>();
	
	// list of trace variants, 1st: index of the first trace in the variant, 2nd: number of traces in the variant 
	private List<List<Integer>> variants = new ArrayList<>();
	
	private List<List<String>> cases = new ArrayList<>(); 
	
	public LogStatistics(XLog log, String classifyingAttribute) {
		this.log = log;
		this.classifyingAttribute = classifyingAttribute;
		this.createAttributeStatistics();
		this.createCaseStatistics(classifyingAttribute);
	}
	
	public void setClassifyingAttribute(String classifyingAttribute) {
		this.classifyingAttribute = classifyingAttribute;
		this.createCaseStatistics(classifyingAttribute);
	}
	
	public long getLogMaxTimestamp() {
		return this.logMaxTimestamp;
	}
	
	public long getLogMinTimestamp() {
		return this.logMinTimestamp;
	}
	
	public String getClassifyingAttribute() {
		return this.classifyingAttribute;
	}
	
	public Integer getVariantLength(int variantIndex) {
		if (variantIndex < 0 || variantIndex >= variants.size()) {
			return null;
		}
		else {
			return this.log.get(variants.get(variantIndex).get(0)).size();
		}
	}
	
	public List<List<Integer>> getVariants() {
		return Collections.unmodifiableList(variants);
	}
	
	public List<List<String>> getCases() {
		return Collections.unmodifiableList(cases);
	}
	
    /**
     * Collect frequency statistics for all event attributes except the timestamp
     * attributeStats is updated
     * Key: attribute key
     * Value: map (key: attribute value, value: frequency count of the value)
     * @param log
     */
    private void createAttributeStatistics() {
    	attributeStats.clear();
    	variants.clear();
    	cases.clear();
    	
        Multimap<String, String> tmp_options = HashMultimap.create(); //map from attribute key to attribute values
        //key: type of attribute (see LogFilterTypeSelector), value: map (key: attribute value, value: frequency count)
        Map<String, Map<String, Integer>> tmp_attributeStats = new HashMap<>();

        for (XTrace trace : log) {
            for (XEvent event : trace) {
                for (XAttribute attribute : event.getAttributes().values()) {
                    String key = attribute.getKey();
                    if (!(key.equals("lifecycle:model") || key.equals("time:timestamp"))) {
                        tmp_options.put(key, attribute.toString());
                        if(tmp_attributeStats.get(key) == null) tmp_attributeStats.put(key, new HashMap<>());

                        Integer i = tmp_attributeStats.get(key).get(attribute.toString());
                        if (i == null) tmp_attributeStats.get(key).put(attribute.toString(), 1);
                        else tmp_attributeStats.get(key).put(attribute.toString(), i + 1);
                    }
                    if (key.equals("time:timestamp")) {
                    	logMinTimestamp = Math.min(logMinTimestamp, ((XAttributeTimestamp) attribute).getValueMillis());
                        logMaxTimestamp = Math.max(logMaxTimestamp, ((XAttributeTimestamp) attribute).getValueMillis());
                    }
                }
            }
        }

        attributeStats.putAll(tmp_attributeStats);
        attributeStats.put("time:timestamp", new HashMap<>());
        attributeStats.put("time:duration", new HashMap<>());
    }
    
    private void createCaseStatistics(String classifyingAttribute) {
    	relationStats.clear();
    	
    	List<String> tmp_variants = new ArrayList<>();
    	
        Multimap<String, String> tmp_options = HashMultimap.create(); //map from attribute key to attribute values
        //key: type of attribute (see LogFilterTypeSelector), value: map (key: attribute value, value: frequency count)
        Map<String, Map<String, Integer>> tmp_attributeStats = new HashMap<>();

        for (XTrace trace : log) {
        	StringBuilder traceBuilder = new StringBuilder();
        	
            for (int i = -1; i < trace.size(); i++) {
            	//--------------------------------------------------
            	// Create statistics of variants
            	//--------------------------------------------------
            	if (i >= 0) {
            		XEvent event = trace.get(i);
                         String eventLabel = event.getAttributes().get(classifyingAttribute).toString();
                         traceBuilder.append(eventLabel + ",");
            	}
            	
            	//--------------------------------------------------
            	// Create statistics of relations 
            	//--------------------------------------------------
                String event1;
                if (i == -1) event1 = "|>";
                else event1 = trace.get(i).getAttributes().get(classifyingAttribute).toString();

                for (int j = i + 1; j < trace.size() + 1; j++) {
                    String event2;
                    if (j == trace.size()) event2 = "[]";
                    else {
                        XAttribute attribute = trace.get(j).getAttributes().get(classifyingAttribute);
                        if (attribute != null) event2 = attribute.toString();
                        else event2 = "";
                    }

                    if(j == i + 1) {
                        String df = (event1 + " => " + event2);
                        tmp_options.put("direct:follow", df);
                        if (tmp_attributeStats.get("direct:follow") == null)
                            tmp_attributeStats.put("direct:follow", new HashMap<>());
                        Integer k = tmp_attributeStats.get("direct:follow").get(df);
                        if (k == null) tmp_attributeStats.get("direct:follow").put(df, 1);
                        else tmp_attributeStats.get("direct:follow").put(df, k + 1);
                    }
                    if(i != -1 && j != trace.size()) {
                        String ef = (event1 + " => " + event2);
                        tmp_options.put("eventually:follow", ef);
                        if (tmp_attributeStats.get("eventually:follow") == null)
                            tmp_attributeStats.put("eventually:follow", new HashMap<>());
                        Integer k = tmp_attributeStats.get("eventually:follow").get(ef);
                        if (k == null) tmp_attributeStats.get("eventually:follow").put(ef, 1);
                        else tmp_attributeStats.get("eventually:follow").put(ef, k + 1);
                    }
                }
            }
            
            String s = traceBuilder.toString();
            int variantIndex = tmp_variants.indexOf(s);
            if(variantIndex >=0) {
            	List<Integer> variant = variants.get(variantIndex);
            	variant.set(1, variant.get(1) + 1);
            }
            else {
            	List<Integer> variant = new ArrayList<>(Arrays.asList(log.indexOf(trace), 1));
                variants.add(variant);
                tmp_variants.add(s);
                variantIndex = variant.size();
            }
            
            List<String> one_case = new ArrayList<>(Arrays.asList(XConceptExtension.instance().extractName(trace), 
            												trace.size()+"", variantIndex + ""));
            cases.add(one_case);
        }

        relationStats.putAll(tmp_attributeStats);
    }
}
