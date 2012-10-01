/**
 *  Copyright 2012, Felix Mannhardt
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.apromore.plugin.property;

/**
 * Default implementation of the PropertyType interface. It takes care of allowing values of specified Class only. Also two Properties are considered
 * 'equal' if there 'name' is the same.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public class DefaultProperty implements PropertyType {

    /**
     * ID of the Property
     */
    private final String id;

    /**
     * Short name of the Property
     */
    private final String name;

    /**
     * Descriptive text of the Property that may be used on the UI.
     */
    private final String description;

    /**
     * If the property if needed to do the Plugins work.
     */
    private final Boolean isMandatory;

    /**
     * Holding the actual value of the Property
     */
    private Object value;

    /**
     * Type of the Property
     */
    private final Class<?> valueType;

    /**
     * Create a new Property with given attributes and specify a default value.
     *
     * @param id of the property
     * @param name of the property
     * @param valueType of the property
     * @param description of the property
     * @param isMandatory true if Plugin requires this property to work properly
     * @param defaultValue of type {@see #getValueType()}
     */
    public DefaultProperty(final String id, final String name, final Class<?> valueType, final String description, final Boolean isMandatory, final Object defaultValue) {
        super();
        this.id = id;
        this.name = name;
        this.valueType = valueType;
        this.description = description;
        this.isMandatory = isMandatory;
        setValue(defaultValue);
    }

    /**
     * Create a new Property with given attributes without specifying a default value. The value will be initialised with NULL.
     *
     * @param id of the property
     * @param name of the property
     * @param valueType of the property
     * @param description of the property
     * @param isMandatory true if Plugin requires this property to work properly
     */
    public DefaultProperty(final String id, final String name, final Class<?> valueType, final String description, final Boolean isMandatory) {
        this.id = id;
        this.name = name;
        this.valueType = valueType;
        this.description = description;
        this.isMandatory = isMandatory;
    }

    /* (non-Javadoc)
     * @see org.apromore.plugin.property.PropertyType#getId()
     */
    @Override
    public final String getId() {
        return this.id;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.plugin.property.PropertyType#getName()
     */
    @Override
    public final String getName() {
        return this.name;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.plugin.property.PropertyType#getDescription()
     */
    @Override
    public final String getDescription() {
        return this.description;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.plugin.property.PropertyType#isMandatory()
     */
    @Override
    public boolean isMandatory() {
        return this.isMandatory;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.plugin.property.PropertyType#getValue()
     */
    @Override
    public final Object getValue() {
        return value;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.plugin.property.PropertyType#setValue(java.lang.Object)
     */
    @Override
    public final void setValue(final Object value) {
        if (getValueType().isInstance(value)) {
            this.value = value;
        } else {
            throw new IllegalArgumentException("Wrong type " + value.getClass().getSimpleName() + " for property " + getName() + "Expected type is: "
                    + getValueType().getSimpleName());
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.plugin.property.PropertyType#getValueType()
     */
    @Override
    public final Class<?> getValueType() {
        return valueType;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.plugin.property.PropertyType#hasValue()
     */
    @Override
    public boolean hasValue() {
        return getValue() != null;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DefaultProperty other = (DefaultProperty) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("DefaultProperty [id=%s, name=%s, isMandatory=%s, value=%s, valueType=%s]",id , name, isMandatory, value, valueType);
    }

}
