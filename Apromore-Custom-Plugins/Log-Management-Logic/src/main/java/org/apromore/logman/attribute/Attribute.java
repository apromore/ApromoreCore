package org.apromore.logman.attribute;

import org.deckfour.xes.model.XAttribute;

/**
 * Attribute represents an attribute in the whole log and stores all values found in the log
 * along with value type and attribute level (log, trace or event). 
 * It is different from XAttribute of OpenXES which represents an attribute value only.
 * 
 * The coordinate of an attribute is: key and level. For example, a log attribute
 * can have key="concept:name" and the level is LOG. a trace attribute can have
 * the key "concept:name" and the level is TRACE, and similr to event attributes.
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
