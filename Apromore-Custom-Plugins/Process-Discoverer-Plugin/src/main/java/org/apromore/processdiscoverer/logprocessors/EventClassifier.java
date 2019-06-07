package org.apromore.processdiscoverer.logprocessors;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.deckfour.xes.classification.XEventAttributeClassifier;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventLifeTransClassifier;
import org.deckfour.xes.model.XEvent;

public class EventClassifier {
	private String[] attributes;
	private XEventAttributeClassifier[] classifiers;
	
	public EventClassifier(String... attributes) {
		this.attributes = attributes;
		
		classifiers = new XEventAttributeClassifier[attributes.length];
		for (int i=0;i<attributes.length;i++) {
			classifiers[i] = new XEventAttributeClassifier(attributes[i], attributes[i]); 
		}
	}
	
	public String getClassIdentity(XEvent event) {
		StringBuilder identity = new StringBuilder();
		for (int i=0;i<classifiers.length;i++) {
			String identity1 = classifiers[i].getClassIdentity(event);
			if (identity1 != null) identity.append(identity1);
			if (i < classifiers.length-1) identity.append("+");
		}
		XEventClassifier lifecycleClassifier = new XEventLifeTransClassifier();
		String lifecycle = lifecycleClassifier.getClassIdentity(event);
		if (lifecycle != null) identity.append("+" + lifecycle); 
		
		return identity.toString();
	}
	
	public List<String> getAttributes() {
		return Arrays.asList(attributes);
	}
	
	@Override
	public int hashCode() {
		String[] copy = Arrays.copyOf(attributes, attributes.length);
		Arrays.sort(copy);
		return Arrays.hashCode(copy);
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof EventClassifier)) return false;
		EventClassifier otherClassifier = (EventClassifier) other;
		Set<String> attributes1 = new HashSet<>(this.getAttributes());
		Set<String> attributes2 = new HashSet<>(otherClassifier.getAttributes());
		return attributes1.equals(attributes2);
	}
}
