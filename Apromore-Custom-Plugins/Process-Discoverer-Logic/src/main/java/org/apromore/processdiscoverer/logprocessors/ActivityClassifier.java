package org.apromore.processdiscoverer.logprocessors;

import org.deckfour.xes.model.XEvent;

public class ActivityClassifier extends EventClassifier {
	public ActivityClassifier(String[] attributes) {
		super(attributes);
	}
	
	@Override
	public String getClassIdentity(XEvent event) {
		String eventClassifier = super.getClassIdentity(event);
		return LogUtils.getCollapsedEvent(eventClassifier);
	}
}
