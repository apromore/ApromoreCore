package org.apromore.plugin;

import java.util.Set;

import org.apromore.plugin.property.PropertyType;

/**
 * A PropertyAwarePlugin defines a Set of properties that can or must be provided by the caller for proper operation of the Plugin.
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University oAS)
 * 
 */
public interface PropertyAwarePlugin extends Plugin {

	/**
	 * Returns the Set of all available properties for this Plugin.
	 * 
	 * @return Set of property names
	 */
	Set<PropertyType> getAvailableProperties();

	/**
	 * Returns the Set of all required properties for this Plugin.
	 * 
	 * @return Set of required property names
	 */
	Set<PropertyType> getMandatoryProperties();

}
