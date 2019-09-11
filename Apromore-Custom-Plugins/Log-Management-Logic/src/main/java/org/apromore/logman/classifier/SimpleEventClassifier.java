package org.apromore.logman.classifier;

import org.deckfour.xes.classification.XEventAttributeClassifier;
import org.deckfour.xes.model.XEvent;

public class SimpleEventClassifier extends XEventAttributeClassifier {
	protected String attribute;
	protected XEventAttributeClassifier classifier;
	
	public SimpleEventClassifier(String attribute) {
		super("eventClassifier", attribute);
		this.attribute = attribute;
		classifier = new XEventAttributeClassifier(attribute);
	}
	
	@Override
	public String getClassIdentity(XEvent event) {
		return classifier.getClassIdentity(event);
	}
	
	public String getAttribute() {
		return attribute;
	}
	
	@Override
	public int hashCode() {
		return attribute.hashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof SimpleEventClassifier)) return false;
		SimpleEventClassifier otherClassifier = (SimpleEventClassifier) other;
		return this.getAttribute().equals(otherClassifier.getAttribute());
	}
}
