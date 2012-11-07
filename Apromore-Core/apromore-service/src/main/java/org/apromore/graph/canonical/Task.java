package org.apromore.graph.canonical;

/**
 * Implementation of the Canonical Task Node.
 *
 * @author Cameron James
 */
public class Task extends Work implements ITask {

    private String subNetId;
    private boolean external = false;

    /**
     * Empty constructor.
     */
    public Task() {
        super();
    }

    /**
     * Constructor with label of the place parameter.
     * @param label String to use as a label of this place.
     */
    public Task(final String label) {
        super(label);
    }

    /**
     * Constructor with label and description of the place parameters.
     * @param label String to use as a label of this place.
     * @param desc  String to use as a description of this place.
     */
    public Task(final String label, final String desc) {
        super(label, desc);
    }


    /**
     * returns if the task is external or not.
     * @return if external or not
     */
    @Override
    public boolean isExternal() {
        return external;
    }

    /**
     * set the Sub net Id
     * @param newId the sub net id
     */
    @Override
    public void setSubNetId(final String newId) {
        subNetId = newId;
    }

    /**
     * return the sub net Id
     * @return the sub net Id
     */
    @Override
    public String getSubNetId() {
        return subNetId;
    }

    /**
     * set if this task is external
     * @param newExternal either true or false
     */
    @Override
    public void setExternal(final boolean newExternal) {
        external = newExternal;
    }

}
