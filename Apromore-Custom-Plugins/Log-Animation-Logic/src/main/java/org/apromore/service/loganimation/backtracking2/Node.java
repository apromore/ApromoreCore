/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2015 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.service.loganimation.backtracking2;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;

/*
* Represent a node in the state search tree
* Root node has null parent
*/
public class Node {
    private Node parent;
    private State state;
    private double cost = Integer.MIN_VALUE;
    private int depth = Integer.MIN_VALUE;
    private int benefit = Integer.MIN_VALUE;
    private int matchCount = Integer.MIN_VALUE;
    private int activitySkipCount = Integer.MIN_VALUE;
    private boolean isComplete = false;
    
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
    
    public int getConsecutiveUnmatch() {
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
    
    /**
     * Set if this node is a complete node (end of trace finished within pruning conditions)
     * @param isComplete 
     */
    public void setComplete(boolean isComplete) {
        this.isComplete = isComplete;
    }
    
    public boolean isComplete() {
        return this.isComplete;
    }
    
    public Node getParent() {
        return this.parent;
    }    

    public SortedSet<Node> getChildNodes() {
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
                                            else if (n1.getCost() == n2.getCost()) {
                                                if (n1.getState().getMarkings().size() < n2.getState().getMarkings().size()) {
                                                    return -1;
                                                }
                                                else if (n1.getState().getMarkings().size() == n2.getState().getMarkings().size()) {
                                                    if (n1.getDepth() < n2.getDepth()) {
                                                        return -1;
                                                    }
                                                }
                                            }
                                        }
                                        return +1;
                                    }
                                }); 
        for (State nextState : state.nextStates(this)) {
            sortedChilds.add(new Node(this, nextState));
        }
        return sortedChilds;
    }    
    
    public Set<Node> getChildNodesForShortestPathFinding() {
        SortedSet<Node> sortedChilds = new TreeSet<>(
                                new Comparator<Node>() {
                                    @Override
                                    public int compare(Node n1, Node n2) {
                                        if (n1.getState().getMarkings().size() < n2.getState().getMarkings().size()) {
                                            return -1;
                                        }
                                        return +1;
                                    }
                                });         
        for (State nextState : state.nextStatesForShortestPathExploration(this)) {
            sortedChilds.add(new Node(this, nextState));
        }
        return sortedChilds;
    } 
    
    @Override
    public String toString() {
        return state.getName();
    }
    
    /*
     * Print the whole path from root node to this node.
     */
    public String getPathString() {
        String printString = "";
        Stack<Node> stack = new Stack<>();
        Node element = this;
        while (element != null) {
            stack.push(element);
            element = element.getParent();
        }
        
        Node sNode=null;
        while (!stack.empty()) {
            sNode = (Node)stack.pop();
            printString += sNode.toString() + " > ";
        }
        
        return printString;
    }
    
    /*
     * Compare to see if this node is better than the input 
     */
    public boolean isBetter(Node node) {
        if (this.getMatchCount() > node.getMatchCount()) {
            return true;
        }
        else if (this.getMatchCount() == node.getMatchCount()) {
            if (this.getCost() < node.getCost()) {
                return true;
            }
            else if (this.getCost() == node.getCost()) {
                if (this.getDepth() < node.getDepth()) {
                    return true;
                }
            }
        }        
        return false;
    }
    
    public boolean isBetterOrEqual(Node node) {
        if (this.getMatchCount() > node.getMatchCount()) {
            return true;
        }
        else if (this.getMatchCount() == node.getMatchCount()) {
            if (this.getCost() < node.getCost()) {
                return true;
            }
            else if (this.getCost() == node.getCost()) {
                if (this.getDepth() <= node.getDepth()) {
                    return true;
                }
            }
        }        
        return false;
    }    
    
    /**
     * Check if this node contains less number of Activity Skip node from root
     * than the input node
     * @param node
     * @return
     */
    public boolean isShorterOrEqual(Node node) {      
        return (this.getActivitySkipCount() <= node.getActivitySkipCount());
    }    
    
    @Override
    public boolean equals(Object node) {
        if (node == null) {
            return false;
        }
        if (node == this) {
            return true;
        }
        if (node.getClass() != this.getClass()) {
            return false;
        }        
        return state.equals(((Node)node).getState());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + this.state.hashCode();
        return hash;
    }
    
    public void clear() {
        parent = null;
        state.clear();
        state = null;
    }
    
}
