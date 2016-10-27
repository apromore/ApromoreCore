package au.edu.qut.processmining.miners.heuristic;

import au.edu.qut.processmining.log.LogParser;
import au.edu.qut.processmining.log.SimpleLog;
import au.edu.qut.processmining.miners.heuristic.net.HeuristicNet;
import org.deckfour.xes.model.XLog;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;

/**
 * Created by Adriano on 24/10/2016.
 */
public class HeuristicMinerPlus {

    private SimpleLog log;
    private HeuristicNet heuristicNet;

    public HeuristicMinerPlus(XLog log) {
        this.log = LogParser.getSimpleLog(log);
        System.out.println("HM+ - log parsed successfully");
    }

    public HeuristicNet mineHeuristicNet(double dependencyThreshold, double positiveObservations, double relative2bestThreshold) {
        System.out.println("HM+ - starting: ");
        System.out.println("HM+ - [Setting] dependency threshold: " + dependencyThreshold);
        System.out.println("HM+ - [Setting] positive observations: " + positiveObservations);
        System.out.println("HM+ - [Setting] relative to best threshold: " + relative2bestThreshold);

        heuristicNet = new HeuristicNet(log, dependencyThreshold, positiveObservations, relative2bestThreshold);
        heuristicNet.generateHeuristicNet();
        return heuristicNet;
    }


    public BPMNDiagram getBPMNDiagram() {
        return heuristicNet.getHeuristicNet();
    }

}
