package au.edu.qut.bpmn.structuring.core;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import au.edu.qut.bpmn.structuring.graph.Graph;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Adriano on 28/02/2016.
 */
public class StructuringCore {
//    private static final Logger LOGGER = LoggerFactory.getLogger(StructuringCore.class);

    public enum Policy {DEPTH, ASTAR, LIM_ASTAR, BREADTH, LIM_DEPTH}

    private Policy policy;
    private int maxDepth;
    private int maxSol;
    private int maxChildren;
    private int maxStates;
    private int maxMinutes;
    private boolean timeBounded;

    private Set<Graph> structuredGraphs;
    private Set<Structurer> structurers;
    private Set<Thread> structThreads;

    public StructuringCore(Policy policy, int maxDepth, int maxSol, int maxChildren, int maxStates, int maxMinutes, boolean timeBounded) {
        this.policy = policy;
        this.maxDepth = maxDepth;
        this.maxSol = maxSol;
        this.maxChildren = maxChildren;
        this.maxStates = maxStates;
        this.maxMinutes = maxMinutes;
        this.timeBounded = timeBounded;
    }

    public Set<Graph> structureAll(Set<Graph> graphs) {

        structurers = new HashSet<>();
        structThreads = new HashSet<>();

        for( Graph g : graphs )
            structurers.add(new Structurer(new StructuringState(g, 0), policy, maxDepth, maxSol, maxChildren, maxStates, maxMinutes, timeBounded));

        for( Structurer s : structurers )
            structThreads.add(new Thread(s));

        for( Thread t : structThreads ) t.start();

        try {
            for( Thread t : structThreads ) t.join();
        } catch(Exception e) {
            System.out.println("ERROR - something went wrong synchronizing the threads.");
        }

        structuredGraphs = new HashSet<>();
        for( Structurer s : structurers ) structuredGraphs.add(s.getSolution());
        return structuredGraphs;
    }

}
