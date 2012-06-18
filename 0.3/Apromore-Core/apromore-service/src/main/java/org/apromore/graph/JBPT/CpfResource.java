package org.apromore.graph.JBPT;

import org.jbpt.pm.IResource;
import org.jbpt.pm.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for CPF Resources.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class CpfResource extends Resource implements ICpfResource {

    private String id;
    private String originalId;
    private String Name;
    private String qualifier;
    private String resourceTypeId;
    private boolean optional;
    private boolean configurable;

    private ResourceType type;
    private List<String> specializationId = new ArrayList<String>(0);
    private Map<String, String> attributes = new HashMap<String, String>(0);


    /**
     * @return a new instance of this class where parent and label are set to <code>null</code>.
     */
    public CpfResource() { }

    /**
     * @param parent of this {@link Resource}
     * @return a new instance of this class where parent is set to the given one and label is set to <code>null</code>.
     */
    public CpfResource(IResource parent) {
        super(parent);
    }

    /**
     * @param parent of this {@link Resource}
     * @param label of this {@link Resource}
     * @return a new instance of this class where parent and label are set to the given values.
     */
    public CpfResource(IResource parent, String label) {
        super(parent, label);
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
    public ResourceType getResourceType() {
        return type;
    }

    @Override
    public void setResourceType(final ResourceType newType) {
        type = newType;
    }

    @Override
    public Map<String, String> getAttributes() {
        return attributes;
    }

    @Override
    public void setAttributes(final Map<String, String> newAttributes) {
        attributes = newAttributes;
    }

    @Override
    public void addAttribute(final String name, final String value) {
        attributes.put(name, value);
    }


}
