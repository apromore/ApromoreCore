package org.apromore.graph.JBPT;

import java.util.Collection;

/**
 * CPF Work interface
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface ICpfWork extends ICpfNode {

    /**
     * @return a {@link java.util.Collection} of all {@link org.apromore.graph.JBPT.ICpfObject}s of this {@link IFlowNode}.
     */
    public Collection<ICpfObject> getObjects();

    /**
     * Add a given {@link org.apromore.graph.JBPT.ICpfObject} to this {@link IFlowNode}.
     * @param object to add to this {@link IFlowNode}
     */
    public void addObject(ICpfObject object);

    /**
     * @return a {@link java.util.Collection} of all {@link org.apromore.graph.JBPT.ICpfResource}s of this {@link IFlowNode}.
     */
    public Collection<ICpfResource> getResource();

    /**
     * Add a given {@link org.apromore.graph.JBPT.ICpfResource} to this {@link IFlowNode}.
     * @param newResource to add to this {@link IFlowNode}
     */
    public void addResource(ICpfResource newResource);

}
