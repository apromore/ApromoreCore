package org.apromore.graph.canonical;

import java.util.List;
import java.util.Map;

/**
 * Interface class for {@link Resource}
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface IResource extends INonFlowNode {

    /**
     * @return the parent of this one.
     */
    IResource getParent();

    /**
     * Set the parent of this {@Resource}.
     * @param parent of this {@Resource}
     */
    void setParent(IResource parent);

    /**
     * Set if this {@link IResource} id.
     * @param id if configurable or not
     */
    void setId(String id);

    /**
     * Return this {@link IResource} Id.
     * @return the Id of the Resource
     */
    String getId();

    /**
     * set the Resource Type Id.
     * @param newResourceTypeId the new Id
     */
    void setResourceTypeId(String newResourceTypeId);

    /**
     * return the Resource Type Id.
     * @return the string id
     */
    String getResourceTypeId();

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
     * returns name of the Resource.
     * @return the name of the resource
     */
    @Override
    String getName();

    /**
     * sets the name of the Resource
     * @param newName the resources name
     */
    @Override
    void setName(String newName);

    /**
     * Set if this {@link IResource} is optional.
     * @param isOptional if optional or not
     */
    void setOptional(boolean isOptional);

    /**
     * Return this {@link IResource} optional.
     * @return if optional or not
     */
    boolean getOptional();

    /**
     * sets the Resource qualifier.
     * @param newQualifier the qualifier.
     */
    void setQualifier(String newQualifier);

    /**
     * Returns the Qualifier.
     * @return the qualifier
     */
    String getQualifier();

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
     * Get the list of Specialization Id's
     * @return the list of special id's
     */
    List<String> getSpecializationId();

    /**
     * Sets the specialization Id's
     * @param specializationId the list of new Specialisation id's
     */
    void setSpecializationId(List<String> specializationId);



    /**
     * Add an attribute to the {@link INode}.
     * @param name  the name of the attribute
     * @param value the simple value of the {@link IAttribute}
     * @param any the complex value of the {@link IAttribute}
     */
    void addAttribute(String name, String value, Object any);

    /**
     * Add an attribute to the {@link INode}.
     * @param name  the name of the attribute
     * @param value the simple value of the {@link IAttribute}
     */
    void addAttribute(String name, String value);

    /**
     * Set if this {@link INode} attributes.
     * @param attributes the map of attributes
     */
    void setAttributes(Map<String, IAttribute> attributes);

    /**
     * Return this {@link INode} attributes.
     * @return the attributes
     */
    Map<String, IAttribute> getAttributes();




    /**
     * Set if this {@link IResource} is type.
     * @param type the resource type
     */
    void setResourceType(ResourceTypeEnum type);

    /**
     * Return the {@link IResource} resource type.
     * @return the resource type
     */
    ResourceTypeEnum getResourceType();




    /**
     * Resource Type.
     */
    public enum ResourceTypeEnum {
        HUMAN, NONHUMAN
    }
}


