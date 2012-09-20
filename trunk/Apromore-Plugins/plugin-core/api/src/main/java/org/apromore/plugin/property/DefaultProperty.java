package org.apromore.plugin.property;

public class DefaultProperty implements PropertyType {
	
	private String name;
	private String description;
	private Boolean isMandatory;
	private Object value;
	private Class<?> valueType;

	public DefaultProperty(String name, Class<?> valueType, String description, Boolean isMandatory, Object defaultValue) {
		super();
		this.name = name;
		this.valueType = valueType;
		this.description = description;
		this.isMandatory = isMandatory;
		this.value = defaultValue;
	}

	public DefaultProperty(String name, Class<?> valueType, String description, Boolean isMandatory) {
		this.name = name;
		this.description = description;
		this.isMandatory = isMandatory;
	}

	/* (non-Javadoc)
	 * @see org.apromore.plugin.property.PropertyType#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/* (non-Javadoc)
	 * @see org.apromore.plugin.property.PropertyType#getDescription()
	 */
	@Override
	public String getDescription() {
		return this.description;
	}

	/* (non-Javadoc)
	 * @see org.apromore.plugin.property.PropertyType#isMandatory()
	 */
	@Override
	public Boolean isMandatory() {
		return this.isMandatory;
	}

	/* (non-Javadoc)
	 * @see org.apromore.plugin.property.PropertyType#getValue()
	 */
	@Override
	public Object getValue() {
		return value;
	}

	/* (non-Javadoc)
	 * @see org.apromore.plugin.property.PropertyType#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Object value) {
		if (getValueType().isInstance(value)) {
			this.value = value;	
		} else {
			throw new IllegalArgumentException("");
		}
	}

	public Class<?> getValueType() {
		return valueType;
	}

	/* (non-Javadoc)
	 * @see org.apromore.plugin.property.PropertyType#hasValue()
	 */
	@Override
	public boolean hasValue() {
		return getValue() != null;
	}

}
