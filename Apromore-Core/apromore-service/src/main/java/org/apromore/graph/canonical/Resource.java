package org.apromore.graph.canonical;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for CPF Resources.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class Resource extends NonFlowNode implements IResource {

    protected String id;
    protected String originalId;
    protected String Name;
    protected String qualifier;
    protected String resourceTypeId;
    protected boolean optional;
    protected boolean configurable;

    protected IResource parent = null;
    protected ResourceTypeEnum type;
    protected List<String> specializationId = new ArrayList<String>(0);

    /**
     * Create a new instance of this class where parent and label are set to <code>null</code>.
     */
    public Resource() {
        super();
    }

    /**
     * Create a new instance of this class where parent is set to the given one and label is set to <code>null</code>.
     * @param parent of this {@link Resource}
     */
    public Resource(IResource parent) {
        this.parent = parent;
    }


    @Override
    public IResource getParent() {
        return this.parent;
    }

    @Override
    public void setParent(IResource parent) {
        this.parent = parent;
    }


    @Override
    public void setId(String newId) {
        id = newId;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setResourceTypeId(String newResourceTypeId) {
        resourceTypeId = newResourceTypeId;
    }

    @Override
    public String getResourceTypeId() {
        return resourceTypeId;
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
    public String getName() {
        return Name;
    }

    @Override
    public void setName(String newName) {
        Name = newName;
    }

    @Override
    public String getQualifier() {
        return qualifier;
    }

    @Override
    public void setQualifier(String newQualifier) {
        qualifier = newQualifier;
    }

    @Override
    public boolean getOptional() {
        return optional;
    }

    @Override
    public void setOptional(final boolean isOptional) {
        optional = isOptional;
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
    public List<String> getSpecializationId() {
        return specializationId;
    }

    @Override
    public void setSpecializationId(List<String> specializationId) {
        this.specializationId = specializationId;
    }


    @Override
    public ResourceTypeEnum getResourceType() {
        return type;
    }

    @Override
    public void setResourceType(final ResourceTypeEnum newType) {
        type = newType;
    }

}
