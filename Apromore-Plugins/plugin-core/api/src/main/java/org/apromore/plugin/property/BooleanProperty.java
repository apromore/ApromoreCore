package org.apromore.plugin.property;

public class BooleanProperty extends DefaultProperty {

	public BooleanProperty(String name, String description, Boolean isMandatory, Object defaultValue) {
		super(name, Boolean.class, description, isMandatory, defaultValue);
	}

	public BooleanProperty(String name, String description, Boolean isMandatory) {
		super(name, Boolean.class, description, isMandatory);
	}

	public Boolean getValueAsBoolean() {		
		return (Boolean) getValue();
	}

	
}
