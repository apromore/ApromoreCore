package org.apromore.processmining.models.graphbased.directed.bpmn.elements;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;

import org.apromore.processmining.models.graphbased.AttributeMap;
import org.apromore.processmining.models.graphbased.directed.AbstractDirectedGraph;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.shapes.Decorated;
import org.apromore.processmining.models.shapes.Diamond;

public class Gateway extends BPMNNode implements Decorated {

	private IGraphElementDecoration decorator = null;

	public enum GatewayType {
		DATABASED, EVENTBASED, INCLUSIVE, COMPLEX, PARALLEL
	}
	
	private boolean isMarkerVisible=true;

	private GatewayType gatewayType = GatewayType.DATABASED;
	private BPMNEdge<? extends BPMNNode, ? extends BPMNNode> defaultFlow = null;

	public BPMNEdge<? extends BPMNNode, ? extends BPMNNode> getDefaultFlow() {
		return defaultFlow;
	}

	public void setDefaultFlow(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> defaultFlow) {
		this.defaultFlow = defaultFlow;
	}

	public Gateway(AbstractDirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> bpmndiagram,
			String label, GatewayType gatewayType) {
		super(bpmndiagram);
		fillAttributes(label, gatewayType);
	}

	public Gateway(AbstractDirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> bpmndiagram,
			String label, GatewayType gatewayType, Swimlane parentSwimlane) {
		super(bpmndiagram, parentSwimlane);
		fillAttributes(label, gatewayType);
	}

	public Gateway(AbstractDirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> bpmndiagram,
			String label, GatewayType gatewayType, SubProcess parentSubProcess) {
		super(bpmndiagram, parentSubProcess);
		fillAttributes(label, gatewayType);
	}

	/**
	 * @param label
	 * @param gatewayType
	 */
	private void fillAttributes(String label, GatewayType gatewayType) {
		this.gatewayType = gatewayType;
		getAttributeMap().put(AttributeMap.LABEL, label);
		getAttributeMap().put(AttributeMap.SHOWLABEL, false);
		getAttributeMap().put(AttributeMap.SHAPE, new Diamond());
		getAttributeMap().put(AttributeMap.SQUAREBB, true);
		getAttributeMap().put(AttributeMap.RESIZABLE, false);
		getAttributeMap().put(AttributeMap.SIZE, new Dimension(25, 25));
	}

	public GatewayType getGatewayType() {
		return gatewayType;
	}

	public void setGatewayType(GatewayType gatewayType) {
		this.gatewayType = gatewayType;
	}

	public Swimlane getParentSwimlane() {
		if (getParent() != null) {
			if (getParent() instanceof Swimlane)
				return (Swimlane) getParent();
			else
				return null;
		}
		return null;
	}

	public SubProcess getParentSubProcess() {
		if (getParent() != null) {
			if (getParent() instanceof SubProcess)
				return (SubProcess) getParent();
			else
				return null;
		}
		return null;
	}

	public IGraphElementDecoration getDecorator() {
		return decorator;
	}

	public void setDecorator(IGraphElementDecoration decorator) {
		this.decorator = decorator;
	}

	public boolean isMarkerVisible() {
		return isMarkerVisible;
	}

	public void setMarkerVisible(boolean isMarkerVisible) {
		this.isMarkerVisible = isMarkerVisible;
	}

	public void decorate(Graphics2D g2d, double x, double y, double width, double height) {
		double scalefactor = 0;
		double scaleX=0;
		double scaleY=0;
		if (decorator == null) {
			scalefactor = width / 33;
			
		} else {
			scalefactor = width / 66;
			scaleX=28;
			scaleY=28;
			decorator.decorate(g2d, x, y, width, height);
		}

		GeneralPath gatewayDecorator = new GeneralPath();
		drawSigns(g2d, gatewayDecorator);

		AffineTransform at = new AffineTransform();
		at.scale(scalefactor, scalefactor);
		gatewayDecorator.transform(at);

		at = new AffineTransform();
		at.translate(x+scaleX, y+scaleY);
		gatewayDecorator.transform(at);

		if (gatewayType == GatewayType.DATABASED) {
			g2d.fill(gatewayDecorator);
		} else {
			g2d.draw(gatewayDecorator);
		}
	}

	private void drawSigns(Graphics2D g2d, GeneralPath gatewayDecorator) {
		if (gatewayType == GatewayType.DATABASED && isMarkerVisible) {
			gatewayDecorator.moveTo(8.75F, 7.55F);
			gatewayDecorator.lineTo(12.75F, 7.55F);
			gatewayDecorator.lineTo(23.15F, 24.45F);
			gatewayDecorator.lineTo(19.25F, 24.45F);
			gatewayDecorator.closePath();
			gatewayDecorator.moveTo(8.75F, 24.45F);
			gatewayDecorator.lineTo(19.25F, 7.55F);
			gatewayDecorator.lineTo(23.15F, 7.55F);
			gatewayDecorator.lineTo(12.75F, 24.45F);
			gatewayDecorator.closePath();
		} else if (gatewayType == GatewayType.EVENTBASED) {
			gatewayDecorator.append(new Ellipse2D.Double(7.5F, 7.5F, 17F, 17F), false);
			gatewayDecorator.append(new Ellipse2D.Double(5F, 5F, 22F, 22F), false);
			gatewayDecorator.moveTo(20.327514F, 21.344972F);
			gatewayDecorator.lineTo(11.259248F, 21.344216F);
			gatewayDecorator.lineTo(9.4577203F, 13.719549F);
			gatewayDecorator.lineTo(15.794545F, 9.389969F);
			gatewayDecorator.lineTo(22.130481F, 13.720774F);
			gatewayDecorator.closePath();
		} else if (gatewayType == GatewayType.INCLUSIVE && isMarkerVisible) {
			gatewayDecorator.append(new Ellipse2D.Double(7.5F, 7.5F, 17F, 17F), false);
			g2d.setStroke(new BasicStroke(2.5F));
		} else if (gatewayType == GatewayType.COMPLEX) {
			gatewayDecorator.moveTo(6.25F, 16F);
			gatewayDecorator.lineTo(25.75F, 16F);
			gatewayDecorator.moveTo(16F, 6.25F);
			gatewayDecorator.lineTo(16F, 25.75F);
			gatewayDecorator.moveTo(8.85F, 8.85F);
			gatewayDecorator.lineTo(23.15F, 23.15F);
			gatewayDecorator.moveTo(8.85F, 23.15F);
			gatewayDecorator.lineTo(23.15F, 8.85F);
			g2d.setStroke(new BasicStroke(2.5F));
		} else if (gatewayType == GatewayType.PARALLEL && isMarkerVisible) {
			gatewayDecorator.moveTo(6.75F, 16F);
			gatewayDecorator.lineTo(25.75F, 16F);
			gatewayDecorator.moveTo(16F, 6.75F);
			gatewayDecorator.lineTo(16F, 25.75F);
			g2d.setStroke(new BasicStroke(3));
		}
	}

}
