/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.graph.canonical;

import java.util.Map;

/**
 * Interface class for {@link Object}
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface ICPFObject extends org.jbpt.hypergraph.abs.IGObject {


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
     * Return the NetId.
     * @return the net Id
     */
    String getNetId();

    /**
     * Set the Net Id.
     * @param newNetId the net id
     */
    void setNetId(String newNetId);

    /**
     * Returns the type of the soft type.
     * @return the soft type
     */
    String getSoftType();

    /**
     * Sets the soft type of this object.
     * @param newSoftType the new soft type.
     */
    void setSoftType(String newSoftType);

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
     * Set if this {@link ICPFObject} is type.
     * @param type the object type
     */
    void setObjectType(ObjectTypeEnum type);

    /**
     * Return the {@link ICPFObject} object type.
     * @return the object type
     */
    ObjectTypeEnum getObjectType();


    /**
     * Can we merge this object? needs to be of same type and have same name.
     * @param toMergeObject the object to merge with.
     * @return true or false
     */
    boolean canMerge(ICPFObject toMergeObject);

}


