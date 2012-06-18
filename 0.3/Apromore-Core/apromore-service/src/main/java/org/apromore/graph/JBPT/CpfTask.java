package org.apromore.graph.JBPT;

/**
 * CPF CpfTask/function implementation.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class CpfTask extends CpfWork implements ICpfTask {

    private int subNetId;
    private boolean external = false;


    public CpfTask() {
		super();
	}

	public CpfTask(String name) {
		super(name);
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
    public void setSubNetId(int newId) {
        subNetId = newId;
    }

    /**
     * return the sub net Id
     * @return the sub net Id
     */
    @Override
    public int getSubNetId() {
        return subNetId;
    }

    /**
     * set if this task is external
     * @param newExternal either true or false
     */
    @Override
    public void setExternal(boolean newExternal) {
        external = newExternal;
    }
}
