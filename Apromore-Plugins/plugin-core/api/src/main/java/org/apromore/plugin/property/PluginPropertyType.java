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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * PropertyType used by a Plugin defining its properties (e.g. parameters). It takes care of allowing values of specified Class only. Also two
 * Properties are considered 'equal' if their 'id' is the same.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 * @param <T>
 *            type of Property
 */
public class PluginPropertyType<T> implements PropertyType<T> {

    /**
     * ID of the Property
     */
    private final String id;

    /**
     * Short name of the Property
     */
    private final String name;

    /**
     * Type of the Property
     */
    private final Class<T> valueType;

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
    private T value;

    /**
     * Create a new Property with given attributes and specify a default value.
     *
     * @param id
     *            of the property
     * @param name
     *            of the property
     * @param valueType
     *            of the property
     * @param description
     *            of the property
     * @param isMandatory
     *            true if Plugin requires this property to work properly
     * @param defaultValue
     *            of type {@see #getValueType()}
     */
    @SuppressWarnings("unchecked")
    public PluginPropertyType(final String id, final String name, final String description, final Boolean isMandatory, final T defaultValue) {
        super();
        this.id = id;
        this.name = name;
        this.description = description;
        this.isMandatory = isMandatory;
        if (defaultValue != null) {
            this.valueType = (Class<T>) defaultValue.getClass();
            setValue(defaultValue);
        } else {
            throw new IllegalArgumentException("Tried to initalize Property " + id + " with NULL default value!");
        }
    }

    /**
     * Create a new Property with given attributes without specifying a default value. The value will be initialised with NULL.
     *
     * @param id
     *            of the property
     * @param name
     *            of the property
     * @param valueType
     *            of the property
     * @param description
     *            of the property
     * @param isMandatory
     *            true if Plugin requires this property to work properly
     */
    public PluginPropertyType(final String id, final String name, final Class<T> valueType, final String description, final Boolean isMandatory) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isMandatory = isMandatory;
        this.valueType = valueType;
    }

    /*
     * (non-Javadoc)
     *
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
        if (isMandatory == null) {
            return false;
        } else {
            return this.isMandatory;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.plugin.property.PropertyType#getValue()
     */
    @Override
    public final T getValue() {
        return value;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.plugin.property.PropertyType#setValue(java.lang.Object)
     */
    @Override
    public final void setValue(final T value) {
        this.value = value;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.plugin.property.PropertyType#getValueType()
     */
    @Override
    public final Class<T> getValueType() {
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
        return new HashCodeBuilder().append(getId()).toHashCode();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public final boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        PluginPropertyType<?> prop = (PluginPropertyType<?>) obj;
        // We just compare Name and Version, a Plugin is equals to another is Name and Version match
        return new EqualsBuilder().appendSuper(super.equals(obj)).append(getId(), prop.getId()).isEquals();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", getId()).append("name", getName()).append("class", getValueType().getSimpleName())
                .append("description", getDescription()).append("isMandatory", isMandatory()).toString();
    }

}
