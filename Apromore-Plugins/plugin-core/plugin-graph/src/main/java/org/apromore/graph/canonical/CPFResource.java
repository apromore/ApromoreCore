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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for CPF Resources.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class CPFResource extends CPFNonFlowNode implements ICPFResource, Cloneable {

    private String originalId;
    private boolean configurable;

    private ResourceTypeEnum resourceType;
    private HumanTypeEnum humanType;
    private NonHumanTypeEnum nonHumanType;

    private List<String> specializationId = new ArrayList<String>(0);
    private Map<String, IAttribute> attributes = new HashMap<String, IAttribute>(0);


    /**
     * Create a new instance of this class where parent and label are set to <code>null</code>.
     */
    public CPFResource() {
        super();
    }



    @Override
    public String getOriginalId() {
        return originalId;
    }

    @Override
    public void setOriginalId(String newOriginalId) {
        originalId = newOriginalId;
    }


    @Override
    public boolean isConfigurable() {
        return configurable;
    }

    @Override
    public void setConfigurable(boolean newConfigurable) {
        configurable = newConfigurable;
    }



    @Override
    public void addSpecializationId(String id) {
        this.specializationId.add(id);
    }

    @Override
    public List<String> getSpecializationIds() {
        return specializationId;
    }

    @Override
    public void setSpecializationIds(List<String> specializationId) {
        this.specializationId = specializationId;
    }



    @Override
    public void setResourceType(ResourceTypeEnum newResourceType) {
        resourceType = newResourceType;
    }

    @Override
    public ResourceTypeEnum getResourceType() {
        return resourceType;
    }

    @Override
    public void setHumanType(HumanTypeEnum newHumanType) {
        humanType = newHumanType;
    }

    @Override
    public HumanTypeEnum getHumanType() {
        return humanType;
    }

    @Override
    public void setNonHumanType(NonHumanTypeEnum newNonHumanType) {
        nonHumanType = newNonHumanType;
    }

    @Override
    public NonHumanTypeEnum getNonHumanType() {
        return nonHumanType;
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
    public CPFResource clone() {
        CPFResource clone;
        clone = (CPFResource) super.clone();
        if (this.getId() != null) {
            clone.setId(this.getId());
        }
        if (this.originalId != null) {
            clone.setOriginalId(this.originalId);
        }
        clone.setName(this.getName());
        clone.setConfigurable(this.configurable);
        clone.setResourceType(this.resourceType);
        clone.setHumanType(this.humanType);
        clone.setNonHumanType(this.nonHumanType);
        clone.setSpecializationIds(this.specializationId);

        clone.setAttributes(this.attributes);

        return clone;
    }

    @Override
    public boolean canMerge(final ICPFResource toMergeResource) {
        return (toMergeResource.getName() != null && getName().equals(toMergeResource.getName()) &&
                toMergeResource.getResourceType() != null && getResourceType().equals(toMergeResource.getResourceType()));
    }
}
