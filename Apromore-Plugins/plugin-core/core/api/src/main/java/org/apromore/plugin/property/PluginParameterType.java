/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2013 Felix Mannhardt.
 * Copyright (C) 2014 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package org.apromore.plugin.property;

import java.util.Objects;

/**
 * ParameterType used by a Plugin defining its parameters. It takes care of allowing values of specified Class only. Also two parameters are
 * considered 'equal' if their 'id' is the same.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 * @param <T>
 *            type of ParameterType
 */
public class PluginParameterType<T> implements ParameterType<T> {

    /**
     * ID of the Property
     */
    private final String id;

    /**
     * Short name of the Property
     */
    private final String name;

    /**
     * Category of the Property
     */
    private final String category;

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
     * @param description
     *            of the property
     * @param isMandatory
     *            true if Plugin requires this property to work properly
     * @param defaultValue
     *            of type T
     */
    @SuppressWarnings("unchecked")
    public PluginParameterType(final String id, final String name, final String description, final Boolean isMandatory, final T defaultValue) {
        super();
        this.id = id;
        this.name = name;
        this.description = description;
        this.isMandatory = isMandatory;
        if (defaultValue != null) {
            this.valueType = (Class<T>) defaultValue.getClass();
            setValue(defaultValue);
        } else {
            throw new IllegalArgumentException("Tried to initalize parameter " + id + " with NULL default value!");
        }
        this.category = ParameterType.DEFAULT_CATEGORY;
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
    public PluginParameterType(final String id, final String name, final Class<T> valueType, final String description, final Boolean isMandatory) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isMandatory = isMandatory;
        this.valueType = valueType;
        this.category = ParameterType.DEFAULT_CATEGORY;
    }

    /**
     * Create a new Property with given attributes and specify a default value.
     *
     * @param id
     *            of the parameter
     * @param name
     *            of the parameter
     * @param description
     *            of the parameter
     * @param isMandatory
     *            true if Plugin requires this parameter to work properly
     * @param category of the parameter
     * @param defaultValue
     *            of type T
     */
    @SuppressWarnings("unchecked")
    public PluginParameterType(final String id, final String name, final String description, final Boolean isMandatory, final String category, final T defaultValue) {
        super();
        this.id = id;
        this.name = name;
        this.description = description;
        this.isMandatory = isMandatory;
        if (defaultValue != null) {
            this.valueType = (Class<T>) defaultValue.getClass();
            setValue(defaultValue);
        } else {
            throw new IllegalArgumentException("Tried to initalize parameter " + id + " with NULL default value!");
        }
        this.category = category;
    }

    /**
     * Create a new Property with given attributes without specifying a default value. The value will be initialised with NULL.
     *
     * @param id
     *            of the parameter
     * @param name
     *            of the parameter
     * @param valueType
     *            of the parameter
     * @param description
     *            of the parameter
     * @param category
     *            of the parameter
     * @param isMandatory
     *            true if Plugin requires this parameter to work properly
     */
    public PluginParameterType(final String id, final String name, final Class<T> valueType, final String description, final Boolean isMandatory,
            final String category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isMandatory = isMandatory;
        this.valueType = valueType;
        this.category = category;
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
     * @see org.apromore.plugin.property.PropertyType#getCategory()
     */
    @Override
    public String getCategory() {
        return this.category;
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
    	return Objects.hash(getId());        
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
        PluginParameterType<?> prop = (PluginParameterType<?>) obj;
        // We just compare Name and Version, a Plugin is equals to another is Name and Version match
        return Objects.equals(getId(), prop.getId());
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return new StringBuffer().append(getClass().getSimpleName()).append("id ="+getId()).append("name ="+getName()).append("class ="+getValueType().getSimpleName())
                .append("description ="+getDescription()).append("isMandatory ="+isMandatory()).toString();
    }

}
