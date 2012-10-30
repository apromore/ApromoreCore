package org.apromore.graph.canonical;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Canonical work interface.
 * 
 * @author Cameron James
 */
public interface IWork extends INode {

    Set<IResource> getResources();

    void setResources(Set<IResource> resources);

    boolean isTeamWork();

    void setTeamWork(boolean teamWork);

    List<Expression> getInputExpr();

    void addInputExpr(Expression inputExpr);

    List<Expression> getOutputExpr();

    void addOutputExpr(Expression outputExpr);

    void addCancelNode(final String cancelNode);

    List<String> getCancelNodes();

    void addCancelEdge(final String cancelEdge);

    List<String> getCancelEdges();

    Expression getResourceDataExpr();

    void setResourceDataExpr(Expression resourceDataExpr);

    Expression getResourceRuntimeExpr();

    void setResourceRuntimeExpr(Expression resourceRuntimeExpr);

    AllocationStrategyEnum getAllocation();

    void setAllocation(AllocationStrategyEnum allocation);

    /**
     * @return a {@link java.util.Collection} of all {@link IObject}s of this {@link INode}.
     */
    public Collection<IObject> getObjects();

    /**
     * Add a given {@link IObject} to this {@link INode}.
     * @param object to add to this {@link INode}
     */
    public void addObject(IObject object);

    /**
     * @return a {@link java.util.Collection} of all {@link IResource}s of this {@link INode}.
     */
    public Collection<IResource> getResource();

    /**
     * Add a given {@link IResource} to this {@link INode}.
     * @param newResource to add to this {@link INode}
     */
    public void addResource(IResource newResource);


    public enum AllocationStrategyEnum {
        RANDOM("Random"), ROUND_ROBIN_BY_TIME("RoundRobinByTime"), ROUND_ROBIN_BY_FREQUENCY("RoundRobinByFrequency"),
        ROUND_ROBIN_BY_EXPERIENCE("RoundRobinByExperience"), SHORTEST_QUEUE("ShortestQueue"), OTHER("Other");

        private final String value;

        AllocationStrategyEnum(String v) {
            value = v;
        }

        public String value() {
            return value;
        }

        public static AllocationStrategyEnum fromValue(String v) {
            for (AllocationStrategyEnum c: AllocationStrategyEnum.values()) {
                if (c.value.equals(v)) {
                    return c;
                }
            }
            throw new IllegalArgumentException(v);
        }
    }
}