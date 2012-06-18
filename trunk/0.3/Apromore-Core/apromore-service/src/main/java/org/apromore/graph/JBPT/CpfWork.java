package org.apromore.graph.JBPT;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * CPF Work implementation.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class CpfWork extends CpfNode implements ICpfWork {

    private Set<ICpfResource> resources = new HashSet<ICpfResource>(0);
    private Set<ICpfObject> objects = new HashSet<ICpfObject>(0);


    public CpfWork() {
        super();
    }

    public CpfWork(String name) {
        super(name);
    }


    /**
     * Add a given {@link ICpfResource} to this {@link IFlowNode}.
     * @param newResource to add to this {@link IFlowNode}
     */
    @Override
    public void addResource(ICpfResource newResource) {
        resources.add(newResource);
    }

    /**
     * @return a {@link java.util.Collection} of all {@link ICpfObject}s of this {@link IFlowNode}.
     */
    @Override
    public Collection<ICpfObject> getObjects() {
        return objects;
    }

    /**
     * Add a given {@link ICpfObject} to this {@link IFlowNode}.
     * @param object to add to this {@link IFlowNode}
     */
    @Override
    public void addObject(ICpfObject object) {
        objects.add(object);
    }

    /**
     * @return a {@link java.util.Collection} of all {@link ICpfResource}s of this {@link IFlowNode}.
     */
    @Override
    public Collection<ICpfResource> getResource() {
        return resources;
    }
}
