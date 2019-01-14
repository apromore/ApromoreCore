package org.apromore.service.perfmining.filter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.UIPluginContext;

public class TraceAttributeFilterParameters extends AttributeFilterParameters {

	public TraceAttributeFilterParameters() {
		super();
	}

	public TraceAttributeFilterParameters(UIPluginContext context, XLog log) {
		super();
		filter = new HashMap<String, Set<String>>();
		for (XTrace trace : log) {
			for (String key : trace.getAttributes().keySet()) {
				if (!key.equals("concept:name")) {
					if (!filter.containsKey(key)) {
						filter.put(key, new HashSet<String>());
					}
					filter.get(key).add(trace.getAttributes().get(key).toString());
				}
			}
			context.getProgress().inc();
		}
		name = XConceptExtension.instance().extractName(log);
	}
}
