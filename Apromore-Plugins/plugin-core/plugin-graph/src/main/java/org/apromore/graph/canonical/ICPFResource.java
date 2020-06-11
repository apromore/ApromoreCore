/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * Copyright (C) 2013 Felix Mannhardt.
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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

package org.apromore.graph.canonical;

import java.util.List;
import java.util.Map;

/**
 * Interface class for {@link CPFResource}
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface ICPFResource extends INonFlowNode {

    /**
     * Return the OriginalId.
     * @return the original Id
     */
    String getOriginalId();

    /**
     * Set the Original Id.
     * @param newOriginalId the original id
     */
    void setOriginalId(String newOriginalId);

    /**
     * is this resource configurable.
     * @return true or false.
     */
    boolean isConfigurable();

    /**
     * Sets id this resource is configurable.
     * @param newConfigurable the new configurable value.
     */
    void setConfigurable(boolean newConfigurable);


    /**
     * sets the attributes.
     * @param properties the attributes
     */
    void setAttributes(Map<String, IAttribute> properties);

    /**
     * return the attributes.
     * @return the map of attributes
     */
    Map<String, IAttribute> getAttributes();

    /**
     * return a attribute.
     * @param name the name of the attribute
     * @return the value of the attribute we are searching for.
     */
    IAttribute getAttribute(String name);

    /**
     * Sets a attribute.
     * @param name  the name of the attribute
     * @param value the simple value text value of the attribute
     * @param any the complex XML value of the attribute
     */
    void setAttribute(String name, String value, java.lang.Object any);

    /**
     * Sets a attribute only the simple text based value.
     * @param name  the name of the attribute
     * @param value the simple value text value of the attribute
     */
    void setAttribute(String name, String value);



    /**
     * Adds a new Specialisation Id.
     * @param id the new Id.
     */
    void addSpecializationId(String id);

    /**
     * Get the list of Specialization Id's
     * @return the list of special id's
     */
    List<String> getSpecializationIds();

    /**
     * Sets the specialization Id's
     * @param specializationId the list of new Specialisation id's
     */
    void setSpecializationIds(List<String> specializationId);


    /**
     * Used to store the different Enum values.
     * @param newResourceType the type of enum we are dealing with.
     */
    void setResourceType(ResourceTypeEnum newResourceType);

    /**
     * Used to store the different Enum values.
     * @return returns the stored value for the resource type enum.
     */
    ResourceTypeEnum getResourceType();

    /**
     * Sets the Human Type.
     * @param newHumanType the human type.
     */
    void setHumanType(HumanTypeEnum newHumanType);

    /**
     * returns the human type.
     * @return the human type
     */
    HumanTypeEnum getHumanType();

    /**
     * Sets the v Human Type.
     * @param newNonHumanType the Non human type.
     */
    void setNonHumanType(NonHumanTypeEnum newNonHumanType);

    /**
     * returns the Non human type.
     * @return the Non human type
     */
    NonHumanTypeEnum getNonHumanType();


    /**
     * Can we merge this resource? needs to be of same type and have same name.
     * @param toMergeResource the resource to merge with.
     * @return true or false
     */
    boolean canMerge(ICPFResource toMergeResource);
}


