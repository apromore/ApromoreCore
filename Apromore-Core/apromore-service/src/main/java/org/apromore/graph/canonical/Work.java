package org.apromore.graph.canonical;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementation of the Canonical Task Node.
 *
 * @author Cameron James
 */
public class Work extends Node implements IWork {

    private boolean teamWork;
    private Expression resourceDataExpr;
    private Expression resourceRuntimeExpr;
    private AllocationStrategyEnum allocation;

    private Set<IResource> resources = new HashSet<IResource>(0);
    private Set<IObject> objects = new HashSet<IObject>(0);
    private List<String> cancelNodes = new ArrayList<String>(0);
    private List<String> cancelEdges = new ArrayList<String>(0);
    private List<Expression> inputExpr = new ArrayList<Expression>(0);
    private List<Expression> outputExpr = new ArrayList<Expression>(0);


    /**
     * Empty constructor.
     */
    public Work() {
        super();
    }

    /**
     * Constructor with label of the place parameter.
     * @param label String to use as a label of this place.
     */
    public Work(String label) {
        super(label);
    }

    /**
     * Constructor with label and description of the place parameters.
     * @param label String to use as a label of this place.
     * @param desc  String to use as a description of this place.
     */
    public Work(String label, String desc) {
        super(label, desc);
    }


    @Override
    public Set<IResource> getResources() {
        return resources;
    }

    @Override
    public void setResources(final Set<IResource> resources) {
        this.resources = resources;
    }

    @Override
    public boolean isTeamWork() {
        return teamWork;
    }

    @Override
    public void setTeamWork(final boolean teamWork) {
        this.teamWork = teamWork;
    }

    @Override
    public List<Expression> getInputExpr() {
        return inputExpr;
    }

    @Override
    public void addInputExpr(final Expression newInputExpr) {
        this.inputExpr.add(newInputExpr);
    }

    @Override
    public List<Expression> getOutputExpr() {
        return this.outputExpr;
    }

    @Override
    public void addOutputExpr(final Expression newOutputExpr) {
        this.outputExpr.add(newOutputExpr);
    }

    @Override
    public void addCancelNode(final String cancelNode) {
        this.cancelNodes.add(cancelNode);
    }

    @Override
    public List<String> getCancelNodes() {
        return cancelNodes;
    }

    @Override
    public void addCancelEdge(final String cancelEdge) {
        this.cancelEdges.add(cancelEdge);
    }

    @Override
    public List<String> getCancelEdges() {
        return cancelEdges;
    }

    @Override
    public Expression getResourceDataExpr() {
        return resourceDataExpr;
    }

    @Override
    public void setResourceDataExpr(final Expression resourceDataExpr) {
        this.resourceDataExpr = resourceDataExpr;
    }

    @Override
    public Expression getResourceRuntimeExpr() {
        return resourceRuntimeExpr;
    }

    @Override
    public void setResourceRuntimeExpr(final Expression resourceRuntimeExpr) {
        this.resourceRuntimeExpr = resourceRuntimeExpr;
    }

    @Override
    public AllocationStrategyEnum getAllocation() {
        return allocation;
    }

    @Override
    public void setAllocation(final AllocationStrategyEnum allocation) {
        this.allocation = allocation;
    }


    /**
     * Add a given {@link org.apromore.graph.canonical.IResource} to this {@link INode}.
     * @param newResource to add to this {@link INode}
     */
    @Override
    public void addResource(IResource newResource) {
        resources.add(newResource);
    }

    /**
     * @return a {@link java.util.Collection} of all {@link org.apromore.graph.canonical.IObject}s of this {@link INode}.
     */
    @Override
    public Collection<IObject> getObjects() {
        return objects;
    }

    /**
     * Add a given {@link org.apromore.graph.canonical.IObject} to this {@link INode}.
     * @param object to add to this {@link INode}
     */
    @Override
    public void addObject(IObject object) {
        objects.add(object);
    }

    /**
     * @return a {@link java.util.Collection} of all {@link org.apromore.graph.canonical.IResource}s of this {@link INode}.
     */
    @Override
    public Collection<IResource> getResource() {
        return resources;
    }
}
