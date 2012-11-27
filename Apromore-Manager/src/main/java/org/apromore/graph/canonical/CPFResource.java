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
    }}
