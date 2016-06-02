package au.edu.qut.structuring.core;

import au.edu.qut.structuring.graph.Graph;

import java.util.*;

/**
 * Created by Adriano on 29/02/2016.
 */

public class Structurer implements Runnable {

    private StructuringState iState;
    private HashSet<StructuringState> solutions;
    private HashSet<StructuringState> zombies;
    private StructuringCore.Policy policy;
    private int maxDepth;
    private int maxSol;
    private int maxStates;
    private int maxChildren;
    private boolean timeBounded;
    private long startingTime;
    private long timeBound;

    public Structurer(StructuringState iState, StructuringCore.Policy policy, int maxDepth, int maxSol, int maxChildren, int maxStates, int maxMinutes, boolean timeBounded) {
        System.out.println("DEBUG - new structurer created.");
        this.policy = policy;
        this.maxDepth = (maxDepth == 0 ? Integer.MAX_VALUE : maxDepth);
        this.maxSol = (maxSol == 0 ? Integer.MAX_VALUE : maxSol);
        this.maxChildren = (maxChildren == 0 ? Integer.MAX_VALUE : maxChildren);
        this.maxStates = maxStates;
        this.timeBounded = timeBounded;
        this.iState = iState;

        solutions = new HashSet<>();
        zombies = new HashSet<>();

        timeBound = 60000 * maxMinutes;

        if(timeBounded) System.out.println("DEBUG - [structurer: A*] time bound in ms: " + timeBound);

    }

    public Graph getSolution() {
        StructuringState best;

        if( solutions.isEmpty() ) {
            best = Collections.max(zombies);
            System.out.println("WARNING - no more move allowed. Maximum structuring reached with " + best.getGraph().getAlivePaths().size() + " paths.");
        } else best = Collections.min(solutions);

        System.out.println("RESULT * [structurer][" + solutions.size() + "][" + zombies.size() + "] - solution taken has cost: " + best.getCost());
        return best.getGraph();
    }

    public void run() {
        StructuringState state;
        StructuringState next;
        PriorityQueue<StructuringState> children;
        LinkedList<StructuringState> toVisit;
        PriorityQueue<StructuringState> toVisitSorted;
        SmartQueue toVisitQuick;
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
                    if( toVisitSorted.isEmpty() ) return;
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
                            toVisitSorted = null;
                            break;
                        }
                }

            case LIM_ASTAR:
                if( timeBounded ) {
                    System.out.println("DEBUG - [structurer: LIMITED A*] switched policy!");
                    startingTime = System.currentTimeMillis();
                }
                maxSol += solutions.size();
                toVisitQuick = new SmartQueue(maxStates);
                toVisitQuick.add(iState);
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
                            System.out.println("DEBUG - [structurer: LIMITED DEPTH] move had cost 0.");
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
