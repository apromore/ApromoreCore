package org.apromore.processdiscoverer.qualitymeasures;

import java.util.Map;

import org.apromore.processdiscoverer.dfg.vis.BPMNDiagramBuilder;
import org.apromore.processdiscoverer.logprocessors.ActivityClassifier;
import org.apromore.processdiscoverer.logprocessors.EventClassifier;
import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.connections.petrinets.behavioral.FinalMarkingConnection;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.astar.petrinet.PetrinetReplayerWithILP;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.algorithms.costbasedcomplete.CostBasedCompleteParam;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.replayer.replayresult.SyncReplayResult;

import com.raffaeleconforti.context.FakePluginContext;
import com.raffaeleconforti.conversion.bpmn.BPMNToPetriNetConverter;

import nl.tue.astar.AStarException;

public class AlignmentBasedFitness {
    public static double measureFitness(BPMNDiagram diagram, XLog log, EventClassifier eventClassifier) {
        for (BPMNNode node : diagram.getNodes()) {
            if(node instanceof org.processmining.models.graphbased.directed.bpmn.elements.Event) {
                org.processmining.models.graphbased.directed.bpmn.elements.Event event1 = (org.processmining.models.graphbased.directed.bpmn.elements.Event) node;
                if (event1.getEventType() == org.processmining.models.graphbased.directed.bpmn.elements.Event.EventType.START) {
                    event1.getAttributeMap().put("ProM_Vis_attr_label", "START");
                } else if (event1.getEventType() == org.processmining.models.graphbased.directed.bpmn.elements.Event.EventType.END) {
                    event1.getAttributeMap().put("ProM_Vis_attr_label", "END");
                } else {
                    event1.getAttributeMap().put("ProM_Vis_attr_label", "");
                }
            }
        }

        Object[] petrinet = BPMNToPetriNetConverter.convert(diagram);

        PNRepResult result = computeAlignment(
                new FakePluginContext(),
                eventClassifier,
                (Petrinet) petrinet[0],
                (Marking) petrinet[1],
                (Marking) petrinet[2],
                log);
        return getAlignmentValue(result);
    }
    
    private static PNRepResult computeAlignment(PluginContext pluginContext, XEventClassifier xEventClassifier, Petrinet petrinet, Marking initialMarking, Marking finalMarking, XLog log) {
        pluginContext.addConnection(new InitialMarkingConnection(petrinet, initialMarking));
        pluginContext.addConnection(new FinalMarkingConnection(petrinet, finalMarking));

        PetrinetReplayerWithILP replayer = new PetrinetReplayerWithILP();

        XEventClass dummyEvClass = new XEventClass("DUMMY",99999);

        Map<Transition, Integer> transitions2costs = constructTTCMap(petrinet);
        Map<XEventClass, Integer> events2costs = constructETCMap(petrinet, xEventClassifier, log, dummyEvClass);

        CostBasedCompleteParam parameters = constructParameters(transitions2costs, events2costs, petrinet, initialMarking, finalMarking);
        TransEvClassMapping mapping = constructMapping(petrinet, xEventClassifier, log, dummyEvClass);

        try {
            return replayer.replayLog(pluginContext, petrinet, log, mapping, parameters);
        } catch (AStarException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        return null;
    }


    private static Map<Transition, Integer> constructTTCMap(Petrinet petrinet) {
        Map<Transition, Integer> transitions2costs = new UnifiedMap<>();

        for(Transition t : petrinet.getTransitions()) {
            if(t.isInvisible()) {
                transitions2costs.put(t, 0);
            }else {
                transitions2costs.put(t, 1);
            }
        }
        return transitions2costs;
    }

    private static Map<XEventClass, Integer> constructETCMap(Petrinet petrinet, XEventClassifier xEventClassifier, XLog log, XEventClass dummyEvClass) {
        Map<XEventClass,Integer> costMOT = new UnifiedMap<>();
        XLogInfo summary = XLogInfoFactory.createLogInfo(log, xEventClassifier);

        for (XEventClass evClass : summary.getEventClasses().getClasses()) {
            int value = 1;
            for(Transition t : petrinet.getTransitions()) {
                if(t.getLabel().equals(evClass.getId())) {
                    value = 1;
                    break;
                }
            }
            costMOT.put(evClass, value);
        }

        costMOT.put(dummyEvClass, 1);

        return costMOT;
    }

    private static CostBasedCompleteParam constructParameters(Map<Transition, Integer> transitions2costs, Map<XEventClass, Integer> events2costs, Petrinet petrinet, Marking initialMarking, Marking finalMarking) {
        CostBasedCompleteParam parameters = new CostBasedCompleteParam(events2costs, transitions2costs);

        parameters.setInitialMarking(initialMarking);
        parameters.setFinalMarkings(finalMarking);
        parameters.setGUIMode(false);
        parameters.setCreateConn(false);
        ((CostBasedCompleteParam) parameters).setMaxNumOfStates(Integer.MAX_VALUE);

        return  parameters;
    }

    private static TransEvClassMapping constructMapping(Petrinet net, XEventClassifier xEventClassifier, XLog log, XEventClass dummyEvClass) {
        TransEvClassMapping mapping = new TransEvClassMapping(xEventClassifier, dummyEvClass);

        XLogInfo summary = XLogInfoFactory.createLogInfo(log, xEventClassifier);

        for (Transition t : net.getTransitions()) {
            boolean mapped = false;

            for (XEventClass evClass : summary.getEventClasses().getClasses()) {
                String id = evClass.getId();

                if (t.getLabel().equals(id)) {
                    mapping.put(t, evClass);
                    mapped = true;
                    break;
                }
            }

            if (!mapped) {
                mapping.put(t, dummyEvClass);
            }

        }

        return mapping;
    }

    private static double getAlignmentValue(PNRepResult pnRepResult) {
        int unreliable = 0;
        if(pnRepResult == null) {
//            System.out.println("UNRELIABLE");
            return Double.NaN;
        }
        for(SyncReplayResult srp : pnRepResult) {
            if(!srp.isReliable()) {
                unreliable += srp.getTraceIndex().size();
            }
        }
        if(unreliable > pnRepResult.size() / 2) {
//            System.out.println("UNRELIABLE");
            return Double.NaN;
        }else {
            return (Double) pnRepResult.getInfo().get(PNRepResult.TRACEFITNESS);
        }
    }
}
