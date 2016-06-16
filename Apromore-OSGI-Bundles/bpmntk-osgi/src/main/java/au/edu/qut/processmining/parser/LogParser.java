package au.edu.qut.processmining.parser;

import au.edu.qut.processmining.parser.graph.LogGraph;
import org.deckfour.xes.model.XLog;

/**
 * Created by Adriano on 14/06/2016.
 */
public class LogParser {

    public LogGraph generateLogGraph(XLog log) {
        LogGraph graph = new LogGraph(log);

        return graph;
    }
}
