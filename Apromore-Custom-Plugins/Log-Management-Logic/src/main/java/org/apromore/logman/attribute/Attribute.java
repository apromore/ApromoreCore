package org.apromore.logman.attribute;

import org.deckfour.xes.model.XAttribute;

/**
 * Attribute represents an attribute in the whole log which is different
 * from XAttribute of OpenXES representing an attribute value.
 * So Attribute contains a range of all values of XAttribute of the same key,
 * level and type in the whole log.
 * 
 * @author Bruce Nguyen
 *
 */
public abstract class Attribute {
	private String key;
	private AttributeType type;
	private AttributeLevel level;
	
	public Attribute(String key, AttributeLevel level, AttributeType type) {
		this.key = key;
		this.type = type;
		this.level = level;
	}
	
	public String getKey() {
		if (level == AttributeLevel.LOG) {
			return "(log)" + key;
		}
		else if (level == AttributeLevel.TRACE) {
			return "(case)" + key;
		}
		else {
			return key;
		}
	}
	
	public AttributeType getType() {
		return this.type;
	}
	
	public AttributeLevel getLevel() {
		return this.level;
	}
	
	public abstract int registerXAttribute(XAttribute attr);
	
}
