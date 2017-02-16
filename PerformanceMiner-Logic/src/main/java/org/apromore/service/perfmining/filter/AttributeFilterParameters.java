package org.apromore.service.perfmining.filter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.framework.plugin.PluginContext;

public class AttributeFilterParameters {

	protected Map<String, Set<String>> filter;
	protected String name;

	public AttributeFilterParameters() {
		filter = new HashMap<String, Set<String>>();
		name = "";
	}

	public AttributeFilterParameters(PluginContext context, XLog log) {
		filter = new HashMap<String, Set<String>>();
		for (XTrace trace : log) {
			for (XEvent event : trace) {
				for (String key : event.getAttributes().keySet()) {
					if (!filter.containsKey(key)) {
						filter.put(key, new HashSet<String>());
					}
					filter.get(key).add(event.getAttributes().get(key).toString());
				}
			}
			//			context.getProgress().inc();
		}
		name = XConceptExtension.instance().extractName(log);
	}

	public void setFilter(Map<String, Set<String>> filter) {
		this.filter = filter;
	}

	public Map<String, Set<String>> getFilter() {
		return filter;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
