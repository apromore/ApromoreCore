package org.apromore.processdiscoverer.dfg.vis;

import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.jgraph.ProMJGraphVisualizer;
import org.processmining.models.jgraph.visualization.ProMJGraphPanel;
import org.processmining.models.jgraph.ProMGraphModel;
import org.processmining.models.jgraph.elements.ProMGraphPort;
import org.processmining.models.jgraph.elements.ProMGraphEdge;
import org.processmining.contexts.uitopia.UIContext;
import org.processmining.contexts.uitopia.UIPluginContext;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.swing.UIManager;
import org.jgraph.graph.AbstractCellView;
import org.jgraph.graph.DefaultGraphCell;
import org.processmining.models.graphbased.directed.DirectedGraphNode;
import org.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.processmining.models.graphbased.directed.bpmn.elements.SubProcess;
import org.processmining.models.graphbased.directed.bpmn.elements.Swimlane;
import org.processmining.models.jgraph.elements.ProMGraphCell;
import org.processmining.models.jgraph.views.JGraphPortView;

public class BPMNDiagramLayouter {
	
	public static Layout layout(BPMNDiagram diagram) throws Exception {
		UIContext context = new UIContext();
        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        UIPluginContext uiPluginContext = context.getMainPluginContext();
		ProMJGraphPanel graphPanel = ProMJGraphVisualizer.instance().visualizeGraph(uiPluginContext, diagram);
		ProMGraphModel graphModel = graphPanel.getGraph().getModel();

		Layout layout = new Layout();
		for (Object o : graphModel.getRoots()) {
			if (o instanceof ProMGraphCell) {
				ProMGraphCell graphCell = (ProMGraphCell) o;
				getCellLayout(graphCell, layout);
			}
			if (o instanceof ProMGraphPort) {
				ProMGraphPort graphPort = (ProMGraphPort) o;
				if(graphPort.getBoundingNode() != null) {
					getCellLayout(graphPort, layout);
				}
			}
			if (o instanceof ProMGraphEdge) {
				ProMGraphEdge graphEdge = (ProMGraphEdge) o;
				getEdgeLayout(graphEdge, layout);
			}
		}
		return layout;
	}
	
	private static void getCellLayout(DefaultGraphCell graphCell, Layout layout) {
		DirectedGraphNode graphNode = null;
		if(graphCell instanceof ProMGraphCell) {
			graphNode = ((ProMGraphCell)graphCell).getNode();
		} else if (graphCell instanceof ProMGraphPort) {
			graphNode = ((ProMGraphPort)graphCell).getBoundingNode();
		}
		// Create BPMNShape
		String bpmnElementId = graphNode.getId().toString().replace(' ', '_');
		boolean isExpanded = false;
		boolean isHorizontal = false;
		if(graphNode instanceof SubProcess) {
			SubProcess subProcess = (SubProcess)graphNode;
			if(!subProcess.isBCollapsed()) {
				isExpanded = true;
			}
		}
		if(graphNode instanceof Swimlane) {				
			isExpanded = true;
			isHorizontal = true;
		}
		AbstractCellView view = null;
		if(graphCell instanceof ProMGraphCell) {
			view = ((ProMGraphCell)graphCell).getView();
		} else if(graphCell instanceof ProMGraphPort) {
			view = ((ProMGraphPort)graphCell).getView();
		}
		Rectangle2D rectangle = view.getBounds();
		
		double x = rectangle.getX();
		double y = rectangle.getY();
		double width = rectangle.getWidth();
		double height = rectangle.getHeight();
		layout.add(new LayoutElement(bpmnElementId, x, y, width, height, isExpanded, isHorizontal));
		
		getChildLayout(graphCell, layout);
	}
	
	private static void getChildLayout(DefaultGraphCell graphCell, Layout layout){
		for (Object o : graphCell.getChildren()) {
			if (o instanceof ProMGraphCell) {
				ProMGraphCell childGraphCell = (ProMGraphCell) o;
				getCellLayout(childGraphCell, layout);
			}
			if (o instanceof ProMGraphPort) {
				ProMGraphPort childGraphPort = (ProMGraphPort) o;
				if(childGraphPort.getBoundingNode() != null) {
					getCellLayout(childGraphPort, layout);
				}
			}
			if (o instanceof ProMGraphEdge) {
				ProMGraphEdge childGraphEdge = (ProMGraphEdge) o;
				getEdgeLayout(childGraphEdge, layout);
			}
		}
	}
	
	private static void getEdgeLayout(ProMGraphEdge graphEdge, Layout layout) {
		@SuppressWarnings("rawtypes")
		BPMNEdge bpmnEdge = (BPMNEdge)graphEdge.getEdge();
		String bpmnElementId = bpmnEdge.getEdgeID().toString().replace(' ', '_');

		LayoutElement layoutEdge = new LayoutElement(bpmnElementId);
		for (Object point : graphEdge.getView().getPoints()) {
			Point2D point2D;
			if(point instanceof JGraphPortView) {
				JGraphPortView portView = (JGraphPortView) point;
				point2D = portView.getLocation();
			} else if(point instanceof Point2D) {
				point2D = (Point2D)point;
			} else {
				continue;
			}
			double x = point2D.getX();
			double y = point2D.getY();
			layoutEdge.addWayPoint(x, y);
		}
		layout.add(layoutEdge);
	}
}
