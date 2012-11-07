package org.apromore.graph.canonical;

/**
 * Canonical task interface.
 *
 * @author Cameron James
 */
public interface ITask extends IWork {

    /**
     * set the Sub net Id
     * @param newId the sub net id
     */
    void setSubNetId(String newId);

    /**
     * return the sub net Id
     * @return the sub net Id
     */
    String getSubNetId();

    /**
     * set if this task is external
     * @param isExternal either true or false
     */
    void setExternal(boolean isExternal);

    /**
     * returns if the task is external or not.
     * @return if external or not
     */
    boolean isExternal();

}