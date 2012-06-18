package org.apromore.graph.JBPT;

/**
 * CPF Task/function interface
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface ICpfTask extends ICpfWork {

    /**
     * set the Sub net Id
     * @param newId the sub net id
     */
    void setSubNetId(int newId);

    /**
     *  return the sub net Id
     * @return the sub net Id
     */
    int getSubNetId();

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
