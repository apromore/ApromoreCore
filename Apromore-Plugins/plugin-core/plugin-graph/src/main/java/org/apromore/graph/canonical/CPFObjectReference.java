/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * Copyright (C) 2013 Felix Mannhardt.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * Copyright (C) 2020, Apromore Pty Ltd.
 *
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

import java.util.HashMap;
import java.util.Map;

/**
 * Class for CPF Object.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class CPFObjectReference extends CPFNonFlowNode implements ICPFObjectReference, Cloneable {

    private String objectId;
    private boolean optional = false;
    private boolean consumed = false;

    private ObjectRefTypeEnum objectRefType;

    private Map<String, IAttribute> attributes = new HashMap<String, IAttribute>(0);


    /**
     * @return a new instance of this class where parent and label are set to <code>null</code>.
     */
    public CPFObjectReference() { }



    @Override
    public String getObjectId() {
        return objectId;
    }

    @Override
    public void setObjectId(String newObjectId) {
        objectId = newObjectId;
    }

    @Override
    public boolean isOptional() {
        return optional;
    }

    @Override
    public void setOptional(boolean newOptional) {
        optional = newOptional;
    }

    @Override
    public boolean isConsumed() {
        return consumed;
    }

    @Override
    public void setConsumed(boolean newConsumed) {
        consumed = newConsumed;
    }


    @Override
    public ObjectRefTypeEnum getObjectRefType() {
        return objectRefType;
    }

    @Override
    public void setObjectRefType(final ObjectRefTypeEnum newType) {
        objectRefType = newType;
    }


    @Override
    public void setAttributes(final Map<String, IAttribute> properties) {
        this.attributes = properties;
    }

    @Override
    public Map<String, IAttribute> getAttributes() {
        return attributes;
    }

    @Override
    public IAttribute getAttribute(final String name) {
        return attributes.get(name);
    }

    @Override
    public void setAttribute(final String name, final String value, final java.lang.Object any) {
        attributes.put(name, new CPFAttribute(value, any));
    }

    @Override
    public void setAttribute(final String name, final String value) {
        setAttribute(name, value, null);
    }


    @Override
    public CPFObjectReference clone() {
        CPFObjectReference clone;
        clone = (CPFObjectReference) super.clone();
        if (this.getId() != null) {
            clone.setId(this.getId());
        }
        if (this.objectId != null) {
            clone.setObjectId(this.objectId);
        }
        clone.setName(this.getName());
        clone.setConsumed(this.consumed);
        clone.setOptional(this.optional);

        clone.setObjectRefType(this.objectRefType);
        clone.setAttributes(this.attributes);

        return clone;
    }

}
