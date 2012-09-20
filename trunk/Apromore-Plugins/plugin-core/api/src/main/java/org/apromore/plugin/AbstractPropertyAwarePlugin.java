package org.apromore.plugin;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apromore.plugin.property.PropertyType;


/**
 * Abstract default implementation of handling the properties.
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University oAS)
 *
 */
public abstract class AbstractPropertyAwarePlugin implements PropertyAwarePlugin {
	
	private Set<PropertyType> availableProperties;

	public AbstractPropertyAwarePlugin() {
		super();
		availableProperties = new HashSet<PropertyType>(); 
	}

	/* (non-Javadoc)
	 * @see org.apromore.plugin.PropertyAwarePlugin#getMandatoryProperties()
	 */
	@Override
	public Set<PropertyType> getMandatoryProperties() {
		Set<PropertyType> mandatoryProperties = new HashSet<PropertyType>();
		for (PropertyType property: getAvailableProperties()) {
			if (property.isMandatory()) {
				mandatoryProperties.add(property);	
			}			
		}
		return Collections.unmodifiableSet(mandatoryProperties);
	}
	
	/* (non-Javadoc)
	 * @see org.apromore.plugin.PropertyAwarePlugin#getAvailableProperties()
	 */
	@Override
	public Set<PropertyType> getAvailableProperties() {
		return Collections.unmodifiableSet(availableProperties);
	}

	/**
	 * Add a property to our list of available properties
	 * 
	 * @param property
	 */
	protected void addProperty(PropertyType property) {
		this.availableProperties.add(property);
	}


}
