package org.apromore.plugin.property;

/**
 * Property of a Plugin 
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University oAS)
 *
 */
public interface PropertyType {
	
	String getName();
	Class<?> getValueType();
	Boolean isMandatory();
	
	String getDescription();
	
	Object getValue();
	void setValue(Object value);
	boolean hasValue();
}
