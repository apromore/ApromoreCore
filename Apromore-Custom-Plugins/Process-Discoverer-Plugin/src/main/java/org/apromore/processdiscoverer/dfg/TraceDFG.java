package org.apromore.processdiscoverer.dfg;


import java.util.Date;

import org.apromore.processdiscoverer.AbstractionParams;
import org.apromore.processdiscoverer.dfg.collectors.ArcInfoCollector;
import org.apromore.processdiscoverer.dfg.collectors.NodeInfoCollector;
import org.apromore.processdiscoverer.dfg.vis.BPMNDiagramBuilder;
import org.apromore.processdiscoverer.logprocessors.EventClassifier;
import org.apromore.processdiscoverer.logprocessors.SimplifiedLog;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;

/**
 * 
 * @author Bruce Nguyen
 *
 */
public class TraceDFG {
	private ArcInfoCollector arcInfoCollector; 
	private NodeInfoCollector nodeInfoCollector;
	XTrace trace;
	LogDFG logDfg;
    
    public TraceDFG(XTrace trace, LogDFG logDfg) {
    	this.trace = trace;
    	this.logDfg = logDfg;
    	this.arcInfoCollector = new ArcInfoCollector(logDfg);
    	nodeInfoCollector = new NodeInfoCollector(logDfg, arcInfoCollector);
    }
    
    public XTrace getTrace() {
    	return this.trace;
    }
    
    public LogDFG getLogDFG() {
    	return this.logDfg;
    }
    
    public ArcInfoCollector getArcInfoCollector() {
    	return arcInfoCollector;
    }
    
    public NodeInfoCollector getNodeInfoCollector() {
    	return nodeInfoCollector;
    }
    
    private int getStart(XTrace trace, int pos, EventClassifier classifier) {
        XEvent event = trace.get(pos);
        XConceptExtension xce = XConceptExtension.instance();
        for(int i = pos - 1; i >= 0; i--) {
            XEvent event1 = trace.get(i);
            if(classifier.getClassIdentity(event1).toLowerCase().endsWith("start") && xce.extractName(event).equals(xce.extractName(event1))) {
                return i;
            }
        }
        return -1;
    }

    private int getPreviousComplete(XTrace trace, int pos, EventClassifier classifier) {
        for(int i = pos - 1; i >= 0; i--) {
            XEvent event1 = trace.get(i);
            if(classifier.getClassIdentity(event1).toLowerCase().endsWith("complete")) {
                return i;
            }
        }
        return -1;
    }
    
    // TODO: this abstraction stores all weights for nodes and arcs in the labels
    // The NodeInfoCollector and ArcInfoCollector is not used 
    // Node: each node is an activity in the event
    // Node weight: the duration of the start and complete events of the same activity
    // Arc weight: the duration from the previous complete event to an event of different activity
    public BPMNDiagram getDFG(AbstractionParams params) {
    	XConceptExtension xce = XConceptExtension.instance();
        XTimeExtension xte = XTimeExtension.instance();
        BPMNDiagramBuilder bpmnDiagramBuilder = new BPMNDiagramBuilder(arcInfoCollector);
        BPMNNode lastNode = bpmnDiagramBuilder.addNode(SimplifiedLog.START_NAME);
        for(int i = 0; i < trace.size(); i++) {
            XEvent event = trace.get(i);
            if(params.getClassifier().getClassIdentity(event).toLowerCase().endsWith("complete")) {
                String name = xce.extractName(event);
                int previous_start = getStart(trace, i, params.getClassifier());
                if(previous_start > -1) {
                    Date date1 = xte.extractTimestamp(trace.get(previous_start));
                    Date date2 = xte.extractTimestamp(event);
                    Long diff = date2.getTime() - date1.getTime();
                    name += "\\n\\n[" + diff.toString() + "]";
                }

                BPMNNode node = bpmnDiagramBuilder.addNode(name);
                String label = "";

                int previous_complete = getPreviousComplete(trace, i, params.getClassifier());
                if (previous_complete > -1) {
                    Date date1 = xte.extractTimestamp(trace.get(previous_complete));
                    Date date2 = xte.extractTimestamp(event);
                    Long diff = date2.getTime() - date1.getTime();
                    label = "[" + diff.toString() + "]";
                }
                bpmnDiagramBuilder.addFlow(lastNode, node, label);
                lastNode = node;
            }
        }
        BPMNNode node = bpmnDiagramBuilder.addNode(SimplifiedLog.END_NAME);
        bpmnDiagramBuilder.addFlow(lastNode, node, "");
        return bpmnDiagramBuilder.getBpmnDiagram();
    }
    
	public TraceAbstraction getTraceAbstraction(AbstractionParams params) {
		TraceAbstraction traceAbs = new TraceAbstraction(this, params);
		return traceAbs;
	}
}
