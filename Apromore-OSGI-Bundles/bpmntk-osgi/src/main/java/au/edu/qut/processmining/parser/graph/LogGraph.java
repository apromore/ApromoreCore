package au.edu.qut.processmining.parser.graph;

import org.deckfour.xes.model.XLog;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Adriano on 14/06/2016.
 */
public class LogGraph {
    private HashSet<LogEdge> edges;
    private HashSet<LogNode> nodes;
    private HashMap<String, LogEdge> outgoing;
    private HashMap<String, LogEdge> incoming;

    public LogGraph(XLog log) {
        generateGraph(log);
    }

    private void generateGraph(XLog log) {

    }
}
