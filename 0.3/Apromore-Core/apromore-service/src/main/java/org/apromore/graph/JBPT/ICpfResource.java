package org.apromore.graph.JBPT;

import org.jbpt.pm.IResource;

import java.util.List;
import java.util.Map;

/**
 * Interface class for {@link CpfResource}
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface ICpfResource extends IResource {

    /**
     * Set if this {@link ICpfResource} id.
     * @param id if configurable or not
     */
    void setId(String id);

    /**
     * Return this {@link ICpfResource} Id.
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
     * Set if this {@link ICpfResource} is optional.
     * @param config if optional or not
     */
    void setOptional(boolean isOptional);

    /**
     * Return this {@link ICpfResource} optional.
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
     * add an attribute to the {@link ICpfResource}.
     * @param name the name of the attribute
     * @param value the value of the attribute
     */
    void addAttribute(String name, String value);

    /**
     * Set if this {@link ICpfResource} attributes.
     * @param attributes the map of attributes
     */
    void setAttributes(Map<String, String> attributes);

    /**
     * Return this {@link ICpfResource} attributes.
     * @return the attributes
     */
    Map<String, String> getAttributes();


    /**
     * Set if this {@link ICpfResource} is type.
     * @param type the resource type
     */
    void setResourceType(ResourceType type);

    /**
     * Return the {@link ICpfResource} resource type.
     * @return the resource type
     */
    ResourceType getResourceType();



    /**
     * Resource Type.
     */
    public enum ResourceType {
        HUMAN, NONHUMAN
    }
}


