package de.hpi.bpmn2_0.backtracking2;

import de.hpi.bpmn2_0.model.FlowNode;
import de.hpi.bpmn2_0.model.connector.SequenceFlow;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.SortedSet;
import java.util.TreeSet;
import org.deckfour.xes.model.XTrace;
import org.joda.time.DateTime;

/*
* Represent a node in the state search tree
* Root node has null parent
*/
public class Node {
    private Node parent;
    private SortedSet<Node> children = null;
    private State state;
    private double cost = Integer.MIN_VALUE;
    private int depth = Integer.MIN_VALUE;
    private int benefit = Integer.MIN_VALUE;
    private int matchCount = Integer.MIN_VALUE;
    private int activitySkipCount = Integer.MIN_VALUE;
    
    public Node(Node parent, State state) {
        this.parent = parent;
        this.state = state;
    }
    
    public State getState() {
        return state;
    }
    
    //Depth of this node in the state search tree
    public int getDepth() {
        if (depth == Integer.MIN_VALUE) {
            if (this.parent != null) {
                depth = parent.getDepth() + state.getDepth();
            }
            else {
                depth = state.getDepth();
            }
        }
        return depth;
    }
    
    public int getMatchCount() {
        if (matchCount == Integer.MIN_VALUE) {
            if (state.isMatch()) {
                if (this.parent != null) {
                    matchCount = parent.getMatchCount() + 1;
                } else {
                    matchCount = 1;
                }
            }
            else {
                if (this.parent != null) {
                    matchCount = parent.getMatchCount();
                } else {
                    matchCount = 0;
                }
            }
        }
        
        return matchCount;
    }
    
    //Cost represent the expense required to reach this node
    public double getCost() {
        if (cost == Integer.MIN_VALUE) {
            if (this.parent != null) {
                cost = state.getCost() + parent.getCost();
            }
            else {
                cost = state.getCost();
            }
        }
        return cost;
    }
    
    public int getDiffSeries() {
        int count = 0;
        if (this.getState().getElementStatus() == StateElementStatus.EVENT_SKIPPED || 
            this.getState().getElementStatus() == StateElementStatus.ACTIVITY_SKIPPED) {
            count++;
            Node node = this.parent;
            while (node != null) {
                if (node.getState().getElementStatus() == StateElementStatus.EVENT_SKIPPED ||
                    node.getState().getElementStatus() == StateElementStatus.ACTIVITY_SKIPPED) {
                    count++;
                    node = node.getParent();
                } else if (node.getState().getElementStatus() == StateElementStatus.ACTIVITY_MATCHED) {
                    break;
                } else {
                    node = node.getParent();
                }
            }
        }
        return count;
    }
    
    public int getActivitySkipCount() {
        if (activitySkipCount == Integer.MIN_VALUE) {
            if (this.parent != null) {
                if (this.getState().isActivitySkip()) {
                    activitySkipCount = 1 + parent.getActivitySkipCount();
                } else {
                    activitySkipCount = parent.getActivitySkipCount();
                }
            }
            else {
                if (this.getState().isActivitySkip()) {
                    activitySkipCount = 1;
                } else {
                    activitySkipCount = 0;
                }
            }
        }
        return activitySkipCount;
    }
    
    //Check if this node reaches an end state while searching
    public boolean isEndState() {
        return state.isEndState();
    }
    
    //Benefit of this state, can be different from the cost perspective
    public int getBenefit() {
        if (benefit == Integer.MIN_VALUE) {
            if (this.parent != null) {
                benefit = state.getBenefit() + parent.getBenefit();
            }
            else {
                benefit = state.getBenefit();
            }
            
        }
        return benefit;
    }
    
    public double getValue() {
        return 1.0*(this.getBenefit() - this.getCost());
    }
    
    public Node getParent() {
        return this.parent;
    }    

    public SortedSet<Node> getChildren() {
        //Use comparator to priroritize the list of nodes
        //Nodes with higher match count, lower cost and  lower depth 
        //will be in first order and selected first from the set.
        SortedSet<Node> sortedChilds = new TreeSet<>(
                                new Comparator<Node>() {
                                    @Override
                                    public int compare(Node n1, Node n2) {
                                        if (n1.getMatchCount() > n2.getMatchCount()) {
                                            return -1;
                                        }
                                        else if (n1.getMatchCount() == n2.getMatchCount()) {
                                            if (n1.getCost() < n2.getCost()) {
                                                return -1;
                                            }
                                        }
                                        else if (n1.getCost() == n2.getCost()) {
                                            if (n1.getDepth() < n2.getDepth()) {
                                                return -1;
                                            }
                                        }
                                        return +1;
                                    }
                                }); 
        if (children == null) {
            for (State nextState : state.nextStates()) {
                sortedChilds.add(new Node(this, nextState));
            }
            children = sortedChilds;
        }
        return children;
    }    
    
    @Override
    public String toString() {
        return state.getName();
    }
    
    
    
}