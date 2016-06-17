package au.edu.qut.processmining.log;

import au.edu.qut.processmining.log.graph.LogGraph;
import org.deckfour.xes.model.XLog;

/**
 * Created by Adriano on 14/06/2016.
 */
public class LogParser {

    public static LogGraph generateLogGraph(XLog log) {
        LogGraph graph = new LogGraph(log);
        return graph;
    }
}
