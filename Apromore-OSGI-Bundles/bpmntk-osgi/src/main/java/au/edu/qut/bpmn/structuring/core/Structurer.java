/*
 * Copyright © 2009-2018 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package au.edu.qut.bpmn.structuring.core;

import au.edu.qut.bpmn.structuring.graph.Graph;

import java.util.*;

/**
 * Created by Adriano on 29/02/2016.
 */

public class Structurer {

    /* */
    private StructuringCore.Policy policy;
    private int maxDepth;
    private int maxSol;
    private int maxStates;
    private int maxChildren;
    private boolean timeBounded;
    private long startingTime;
    private long timeBound;

    private boolean stop;

    private StructuringState iState;
    private HashSet<StructuringState> solutions;
    private HashSet<StructuringState> zombies;

    public Structurer(StructuringCore.Policy policy, int maxDepth, int maxSol, int maxChildren, int maxStates, int maxMinutes, boolean timeBounded) {
        System.out.println("DEBUG - new structurer created.");
        this.policy = policy;
        this.maxDepth = (maxDepth == 0 ? Integer.MAX_VALUE : maxDepth);
        this.maxSol = (maxSol == 0 ? Integer.MAX_VALUE : maxSol);
        this.maxChildren = (maxChildren == 0 ? Integer.MAX_VALUE : maxChildren);
        this.maxStates = maxStates;
        this.timeBounded = timeBounded;

        timeBound = 60000 * maxMinutes;
        stop = false;
    }

    public Graph getStructuredRigid(StructuringState iState) {
        StructuringState best;

        this.iState = iState;
        solutions = new HashSet<>();
        zombies = new HashSet<>();

        this.structure();

        if( solutions.isEmpty() ) {
            if( zombies.isEmpty() ) {
                System.out.println("WARNING - no solutions found, returning the initial rigid.");
                return iState.getGraph();
            }
            best = Collections.max(zombies);
            System.out.println("WARNING - no more move allowed. Maximum structuring reached with " + best.getGraph().getAlivePaths().size() + " paths.");
        } else best = Collections.min(solutions);

        System.out.println("RESULT * [structurer][" + solutions.size() + "][" + zombies.size() + "] - solution taken has cost: " + best.getCost());
        return best.getGraph();
    }

    private void structure() {
        StructuringState state;
        StructuringState next;
        PriorityQueue<StructuringState> children;
        LinkedList<StructuringState> toVisit;
        PriorityQueue<StructuringState> toVisitSorted = null;
        SmartQueue toVisitQuick = null;
        ArrayList<StructuringState> tmpChildren;

        System.out.println("DEBUG - new structurer running.");

        int c = 0;
        int depth = 0;

        switch( policy ) {
            case DEPTH:
                tmpChildren = new ArrayList<>();
                toVisit = new LinkedList<>();
                toVisit.addFirst(iState);
                while( (toVisit.size() != 0) ) {
                    state = toVisit.removeFirst();
                    children = state.generateChildren();
                    //System.out.println("DEBUG - [structurer: DEPTH] generated.");
                    while( (next = children.poll()) != null ) {
                        if( next.isSolved() ) {
                            solutions.add(next);
                            return;
                        }
                        if( next.isDead() ) {
                            zombies.add(next);
                        } else {
                            tmpChildren.add(0, next);
                        }
                    }
                    //System.out.println("DEBUG - [structurer: DEPTH] parsed.");
                    while( !tmpChildren.isEmpty() ) toVisit.addFirst(tmpChildren.remove(0));
                    //System.out.println("DEBUG - [structurer: DEPTH] state(" + toVisit.size() + ")");
                }

            case BREADTH:
                toVisit = new LinkedList<>();
                toVisit.addFirst(iState);
                toVisit.addLast(new StructuringState());
                while( !toVisit.isEmpty() && (depth <= maxDepth) && (solutions.size() <= maxSol) ) {
                    state = toVisit.removeFirst();
                    if( state.isFake() ) {
                        //this is to keep track of the depth in the breadth-first algorithm
                        depth++;
                        toVisit.addLast(new StructuringState());
                        continue;
                    }
                    children = state.generateChildren();
                    while( (next = children.poll()) != null ) {
                        if( next.isSolved() )  solutions.add(next);
                        else if( next.isDead() ) zombies.add(next);
                        else toVisit.addLast(next);
                    }
                    //System.out.println("DEBUG - [structurer: BREADTH] state(" + depth + ")(" + solutions.size() + ")(" + zombies.size() + ")");
                }
                return;

            case ASTAR:
                startingTime = System.currentTimeMillis();
                toVisitSorted = new PriorityQueue<>();
                toVisitSorted.add(iState);
                while( true ) {
                    if( toVisitSorted.isEmpty() ) {
//                        if( solutions.isEmpty() && !stop) {
//                            for(StructuringState ss : zombies) ss.getGraph().enablePullUp();
//                            timeBound = timeBound*2;
//                            stop = true;
//                        } else return;
                        return;
                    }
                    state = toVisitSorted.poll();
                    if( state.isSolved() ) {
                        solutions.add(state);
                        return;
                    }
                    if( state.isDead() ) {
                        zombies.add(state);
                        continue;
                    }
                    children = state.generateChildren();
                    while( (next = children.poll()) != null ) {
                        toVisitSorted.add(next);
                        if( next.isSolved() ) solutions.add(next);
                        if( next.isDead() ) zombies.add(next);
                    }
                    //System.out.println("DEBUG - [structurer: A*] state(" + toVisitSorted.size() + ")");
                    if( timeBounded )
                        if( (System.currentTimeMillis() - startingTime) > timeBound ) {
                            System.out.println("DEBUG - [structurer: A*] found " + solutions.size() + " solutions. Switching policy to LIMITED A*");
                            break;
                        }
                }

            case LIM_ASTAR:
                if( toVisitSorted == null  || toVisitSorted.isEmpty() ) toVisitSorted.add(iState);
                if( timeBounded ) {
                    System.out.println("DEBUG - [structurer: LIMITED A*] switched policy!");
                    startingTime = System.currentTimeMillis();
                }
                maxSol += solutions.size();
                toVisitQuick = new SmartQueue(maxStates);
                for( StructuringState ss : toVisitSorted ) toVisitQuick.add(ss);
                while( true ) {
                    if( toVisitQuick.isEmpty() || (solutions.size() > maxSol) ) return;
                    c = 0;
                    state = toVisitQuick.poll();
                    if( state.isSolved() ) {
                        solutions.add(state);
                        return;
                    }
                    if( state.isDead() ) {
                        zombies.add(state);
                        continue;
                    }
                    children = state.generateChildren();
                    while( ((next = children.poll()) != null) && (c++ <= maxChildren) ) {
                        toVisitQuick.add(next);
                        if( next.isSolved() ) solutions.add(next);
                    }
                    //System.out.println("DEBUG - [structurer: LIMITED A*] state(" + toVisitQuick.size() + ")(" + toVisitQuick.minCost() + ")" + "(" +  toVisitQuick.maxCost() + ")" + "(" + solutions.size() + ")");
                    if( (System.currentTimeMillis() - startingTime) > timeBound ) {
                        System.out.println("DEBUG - [structurer: LIMITED A*] found " + solutions.size() + " solutions. Switching policy to LIMITED DEPTH");
                        break;
                    }
                }

            case LIM_DEPTH:
                if( timeBounded ) {
                    System.out.println("DEBUG - [structurer: LIMITED DEPTH] switched policy!");
                    startingTime = System.currentTimeMillis();
                    if( toVisitQuick != null && !toVisitQuick.isEmpty() ) iState =  toVisitQuick.poll();
                }
                tmpChildren = new ArrayList<>();
                toVisit = new LinkedList<>();
                toVisit.addFirst(iState);
                while( (toVisit.size() != 0) ) {
                    c = 0;
                    state = toVisit.removeFirst();
                    children = state.generateChildren();
                    //System.out.println("DEBUG - [structurer: DEPTH] generated.");
                    while( ((next = children.poll()) != null) && (c <= maxChildren)  ) {
                        if( next.isSolved() ) {
                            solutions.add(next);
                            return;
                        }
                        if( next.isDead() ) {
                            zombies.add(next);
                        } else {
                            c++;
                            tmpChildren.add(0, next);
                        }
                    }
                    //System.out.println("DEBUG - [structurer: DEPTH] parsed.");
                    while( !tmpChildren.isEmpty() ) {
                        toVisit.addFirst(tmpChildren.get(0));
                        if(tmpChildren.remove(0).getCost() == 0) {
                            tmpChildren = new ArrayList<>();
//                            System.out.println("DEBUG - [structurer: LIMITED DEPTH] move had cost 0.");
                            break;
                        }
                    }
                    //System.out.println("DEBUG - [structurer: DEPTH] state(" + toVisit.size() + ")");

                    if( (System.currentTimeMillis() - startingTime) > timeBound ) {
                        System.out.println("DEBUG - [structurer: LIMITED DEPTH] found no solutions. Returning best state reached.");
                        solutions.add(toVisit.removeFirst());
                        return;
                    }
                }

            default:
                System.out.println("ERROR - wrong policy.");
                return;
        }
    }


    private class SmartQueue {
        HashMap<Integer, PriorityQueue<StructuringState>> states;
        private int maxStates;
        private int size;

        SmartQueue(int maxStates){
            states = new HashMap<>();
            this.maxStates = maxStates;
        }

        void add(StructuringState s) {
            int cost = s.getCost();

            if( !states.containsKey(cost) ) {
                states.put(cost, new PriorityQueue<StructuringState>());
                states.get(cost).add(s);
                size++;
                return;
            }

            if( states.get(cost).size() > maxStates ) return;
            else states.get(cost).add(s);
            size++;
        }

        StructuringState poll() {
            int minCost = Collections.min(states.keySet());
            StructuringState best = states.get(minCost).poll();

            if( states.get(minCost).isEmpty() ) states.remove(minCost);

            size--;
            return best;
        }

        boolean isEmpty() { return states.isEmpty(); }
        int size() { return size; }
        int minCost() { return Collections.min(states.keySet()); }
        int maxCost() { return Collections.max(states.keySet()); }

    }

}
