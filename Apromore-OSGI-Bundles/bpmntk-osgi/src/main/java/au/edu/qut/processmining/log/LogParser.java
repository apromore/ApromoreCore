package au.edu.qut.processmining.log;

import au.edu.qut.processmining.log.graph.fuzzy.FuzzyNet;
import au.edu.qut.processmining.log.graph.heuristic.HeuristicNet;
import org.apache.commons.logging.Log;
import org.deckfour.xes.model.XLog;

/**
 * Created by Adriano on 14/06/2016.
 */
public class LogParser {

    private XLog log;

    public LogParser() { this.log = null; }
    public LogParser(XLog log) {
        this.log = log;
    }

    public void setLog(XLog log) { this.log = log; }
    public XLog getLog() { return log; }

    public FuzzyNet getFuzzyNet() {
        if( log == null ) return null;
        else return (new FuzzyNet(log));
    }

    public HeuristicNet getHeuristicNet() {
        if( log == null ) return null;
        else  return (new HeuristicNet(log));
    }

    public static FuzzyNet getFuzzyNet(XLog log) { return (new FuzzyNet(log)); }
    public static HeuristicNet getHeuristicNet(XLog log) { return (new HeuristicNet(log)); }
}
