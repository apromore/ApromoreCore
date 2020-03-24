package org.apromore.processmining.plugins.bpmn;

//import javax.swing.JComponent;
//
//import org.processmining.contexts.uitopia.annotations.Visualizer;
//import org.processmining.framework.plugin.PluginContext;
//import org.processmining.framework.plugin.annotations.Plugin;
//import org.processmining.framework.plugin.annotations.PluginLevel;
//import org.processmining.framework.plugin.annotations.PluginVariant;
//import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
//import org.processmining.models.jgraph.ProMJGraphVisualizer;
//
//@Plugin(name = "Visualize BPMN", level = PluginLevel.PeerReviewed, parameterLabels = { "BPMN Diagram" }, returnLabels = { "BPMN Diagram Visualization" }, returnTypes = { JComponent.class })
//@Visualizer
//public class BPMNVisualization {
//
//	@PluginVariant(requiredParameterLabels = { 0 })
//	public static JComponent visualize(PluginContext context, BPMNDiagram bpmndiagram) {
//		return ProMJGraphVisualizer.instance().visualizeGraph(context, bpmndiagram);
//	}
//
//}