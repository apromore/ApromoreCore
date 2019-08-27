package org.apromore.logman.classifier;

import org.apromore.logman.utils.LogUtils;
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
