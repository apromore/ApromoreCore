package org.apromore.processmining.models.graphbased.directed.bpmn.elements;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.apromore.processmining.models.graphbased.AttributeMap;
import org.apromore.processmining.models.graphbased.directed.AbstractDirectedGraph;
import org.apromore.processmining.models.graphbased.directed.BoundaryDirectedGraphNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.shapes.Decorated;
import org.apromore.processmining.models.shapes.Ellipse;

public class Event extends BPMNNode implements Decorated, BoundaryDirectedGraphNode {
	
	private IGraphElementDecoration decorator = null;
	
	private boolean isInterrupting = true;
	
	private String parallelMultiple;
	
	public enum EventTrigger {
		NONE, MESSAGE, TIMER, ERROR, CANCEL, COMPENSATION, CONDITIONAL, LINK, SIGNAL, TERMINATE, MULTIPLE;
	}

	public enum EventType {
		START, INTERMEDIATE, END;
	}

	public enum EventUse {
		THROW, CATCH;
	}

	private EventType eventType = EventType.START;
	private EventTrigger eventTrigger = EventTrigger.NONE;
	private EventUse eventUse = EventUse.CATCH;
	private Activity exceptionFor;

	public Event(AbstractDirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> bpmndiagram,
			String label, EventType eventType, EventTrigger eventTrigger, EventUse eventUse, Activity exceptionFor) {
		super(bpmndiagram);
		fillAttributes(label, eventType, eventTrigger, eventUse, true, exceptionFor);
	}

	public Event(AbstractDirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> bpmndiagram,
			String label, EventType eventType, EventTrigger eventTrigger, EventUse eventUse,
			SubProcess parentSubProcess, Activity exceptionFor) {
		super(bpmndiagram, parentSubProcess);
		fillAttributes(label, eventType, eventTrigger, eventUse, true, exceptionFor);
	}

	public Event(AbstractDirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> bpmndiagram,
			String label, EventType eventType, EventTrigger eventTrigger, EventUse eventUse, Swimlane parentSwimlane,
			Activity exceptionFor) {
		super(bpmndiagram, parentSwimlane);
		fillAttributes(label, eventType, eventTrigger, eventUse, true, exceptionFor);
	}
	
	public Event(AbstractDirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> bpmndiagram,
			String label, EventType eventType, EventTrigger eventTrigger, EventUse eventUse, 
			boolean isInterrupting, Activity exceptionFor) {
		super(bpmndiagram);
		fillAttributes(label, eventType, eventTrigger, eventUse, isInterrupting, exceptionFor);
	}

	public Event(AbstractDirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> bpmndiagram,
			String label, EventType eventType, EventTrigger eventTrigger, EventUse eventUse,
			SubProcess parentSubProcess, boolean isInterrupting, Activity exceptionFor) {
		super(bpmndiagram, parentSubProcess);
		fillAttributes(label, eventType, eventTrigger, eventUse, isInterrupting, exceptionFor);
	}

	public Event(AbstractDirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> bpmndiagram,
			String label, EventType eventType, EventTrigger eventTrigger, EventUse eventUse, Swimlane parentSwimlane,
			boolean isInterrupting, Activity exceptionFor) {
		super(bpmndiagram, parentSwimlane);
		fillAttributes(label, eventType, eventTrigger, eventUse, isInterrupting, exceptionFor);
	}
	
	/**
	 * @param label
	 * @param eventType
	 * @param eventTrigger
	 * @param eventUse
	 * @param isInterrupting
	 * @param exceptionFor
	 */
	private void fillAttributes(String label, EventType eventType, EventTrigger eventTrigger, EventUse eventUse,
			boolean isInterrupting, Activity exceptionFor) {
		this.eventType = eventType;
		this.eventTrigger = eventTrigger;
		this.eventUse = eventUse;
		this.isInterrupting = isInterrupting;
		this.exceptionFor = exceptionFor;
		if (eventType == EventType.END) {
			getAttributeMap().put(AttributeMap.BORDERWIDTH, 3);
		}
		if(!isInterrupting) {
			getAttributeMap().put(AttributeMap.DASHPATTERN, new float[] { (float)3.0, (float)3.0 });
		}
		getAttributeMap().put(AttributeMap.LABEL, label);
		getAttributeMap().put(AttributeMap.SHOWLABEL, false);
		getAttributeMap().put(AttributeMap.SHAPE, new Ellipse());
		getAttributeMap().put(AttributeMap.SQUAREBB, true);
		getAttributeMap().put(AttributeMap.RESIZABLE, false);
		if(exceptionFor==null) {
			getAttributeMap().put(AttributeMap.SIZE, new Dimension(25, 25));
			getAttributeMap().put(AttributeMap.FILLCOLOR, Color.WHITE);
		} else {
			exceptionFor.incNumOfBoundaryEvents();
			getAttributeMap().put(AttributeMap.SIZE, new Dimension(25, 25));
			getAttributeMap().put(AttributeMap.PORTOFFSET, 
					new Point2D.Double(1000 - exceptionFor.getNumOfBoundaryEvents()*80, 1000));
			getAttributeMap().put(AttributeMap.FILLCOLOR, Color.WHITE);
		}
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

	public void decorate(Graphics2D g2d, double x, double y, double width, double height) {
		double scalefactor = width / 33;
		GeneralPath eventDecorator = new GeneralPath();

		if (eventType == EventType.INTERMEDIATE) {
			g2d.draw(new Ellipse2D.Double(x + 3, y + 3, width - 7, height - 7));
		}

		if (eventTrigger == EventTrigger.MESSAGE) {
			if (eventUse == EventUse.CATCH) {
				eventDecorator.moveTo(8, 11);
				eventDecorator.lineTo(8, 21);
				eventDecorator.lineTo(24, 21);
				eventDecorator.lineTo(24, 11);
				eventDecorator.closePath();
				eventDecorator.moveTo(8, 11);
				eventDecorator.lineTo(16, 17);
				eventDecorator.lineTo(24, 11);
				eventDecorator.closePath();
			} else if (eventUse == EventUse.THROW) {
				eventDecorator.moveTo(8, 12);
				eventDecorator.lineTo(8, 21);
				eventDecorator.lineTo(24, 21);
				eventDecorator.lineTo(24, 12);
				eventDecorator.lineTo(16, 18);
				eventDecorator.closePath();

				eventDecorator.moveTo(8, 11);
				eventDecorator.lineTo(16, 17);
				eventDecorator.lineTo(24, 11);
				eventDecorator.closePath();
			}
		} else if (eventTrigger == EventTrigger.TIMER) {
			eventDecorator.moveTo(16, 6);
			eventDecorator.lineTo(16, 9);
			eventDecorator.moveTo(21, 7);
			eventDecorator.lineTo(19.5F, 10F);
			eventDecorator.moveTo(25, 11);
			eventDecorator.lineTo(22F, 12.5F);
			eventDecorator.moveTo(26, 16);
			eventDecorator.lineTo(23F, 16F);
			eventDecorator.moveTo(25, 21);
			eventDecorator.lineTo(22F, 19.5F);
			eventDecorator.moveTo(21, 25);
			eventDecorator.lineTo(19.5F, 22F);
			eventDecorator.moveTo(16, 26);
			eventDecorator.lineTo(16F, 23F);
			eventDecorator.moveTo(11, 25);
			eventDecorator.lineTo(12.5F, 22F);
			eventDecorator.moveTo(7, 21);
			eventDecorator.lineTo(10F, 19.5F);
			eventDecorator.moveTo(6, 16);
			eventDecorator.lineTo(9F, 16F);
			eventDecorator.moveTo(7, 11);
			eventDecorator.lineTo(10F, 12.5F);
			eventDecorator.moveTo(11, 7);
			eventDecorator.lineTo(12.5F, 10F);
			eventDecorator.moveTo(18, 9);
			eventDecorator.lineTo(16F, 16F);
			eventDecorator.lineTo(20, 16);
		} else if (eventTrigger == EventTrigger.ERROR) {
			eventDecorator.moveTo(22.820839F, 11.171502F);
			eventDecorator.lineTo(19.36734F, 24.58992F);
			eventDecorator.lineTo(13.54138F, 14.281819F);
			eventDecorator.lineTo(9.3386512F, 20.071607F);
			eventDecorator.lineTo(13.048949F, 6.8323057F);
			eventDecorator.lineTo(18.996148F, 16.132659F);
			eventDecorator.closePath();
		} else if (eventTrigger == EventTrigger.CANCEL) {
			eventDecorator.moveTo(7.2839105F, 10.27369F);
			eventDecorator.lineTo(10.151395F, 7.4062062F);
			eventDecorator.lineTo(15.886362F, 13.141174F);
			eventDecorator.lineTo(21.621331F, 7.4062056F);
			eventDecorator.lineTo(24.488814F, 10.273689F);
			eventDecorator.lineTo(18.753846F, 16.008657F);
			eventDecorator.lineTo(24.488815F, 21.743626F);
			eventDecorator.lineTo(21.621331F, 24.611111F);
			eventDecorator.lineTo(15.886362F, 18.876142F);
			eventDecorator.lineTo(10.151394F, 24.611109F);
			eventDecorator.lineTo(7.283911F, 21.743625F);
			eventDecorator.lineTo(13.018878F, 16.008658F);
			eventDecorator.closePath();
		} else if (eventTrigger == EventTrigger.COMPENSATION) {
			eventDecorator.moveTo(15, 9);
			eventDecorator.lineTo(15, 23);
			eventDecorator.lineTo(8, 16);
			eventDecorator.closePath();
			eventDecorator.moveTo(22, 9);
			eventDecorator.lineTo(22, 23);
			eventDecorator.lineTo(15, 16);
			eventDecorator.closePath();
		} else if (eventTrigger == EventTrigger.CONDITIONAL) {
			eventDecorator.append(new Rectangle2D.Double(8, 8, 16, 16), false);
			eventDecorator.moveTo(10, 10);
			eventDecorator.lineTo(22, 10);
			eventDecorator.moveTo(10, 14);
			eventDecorator.lineTo(22, 14);
			eventDecorator.moveTo(10, 18);
			eventDecorator.lineTo(22, 18);
			eventDecorator.moveTo(10, 22);
			eventDecorator.lineTo(22, 22);
		} else if (eventTrigger == EventTrigger.LINK) {
			eventDecorator.moveTo(9, 13);
			eventDecorator.lineTo(19, 13);
			eventDecorator.lineTo(19, 10);
			eventDecorator.lineTo(25, 16);
			eventDecorator.lineTo(19, 22);
			eventDecorator.lineTo(19, 19);
			eventDecorator.lineTo(9, 19);
			eventDecorator.closePath();
		} else if (eventTrigger == EventTrigger.SIGNAL) {
			eventDecorator.moveTo(8.7124971F, 21.247342F);
			eventDecorator.lineTo(23.333334F, 21.247342F);
			eventDecorator.lineTo(16.022915F, 8.5759512F);
			eventDecorator.closePath();
		} else if (eventTrigger == EventTrigger.TERMINATE) {
			eventDecorator.append(new Ellipse2D.Double(7, 7, 18, 18), false);
		} else if (eventTrigger == EventTrigger.MULTIPLE) {
			eventDecorator.moveTo(20.834856F, 22.874369F);
			eventDecorator.lineTo(10.762008F, 22.873529F);
			eventDecorator.lineTo(7.650126F, 13.293421F);
			eventDecorator.lineTo(15.799725F, 7.3734296F);
			eventDecorator.lineTo(23.948336F, 13.294781F);
			eventDecorator.closePath();
		}

		AffineTransform at = new AffineTransform();
		at.scale(scalefactor, scalefactor);
		eventDecorator.transform(at);

		at = new AffineTransform();
		at.translate(x, y);
		eventDecorator.transform(at);
			
		g2d.setStroke(new BasicStroke(1));
		if (eventUse == EventUse.CATCH) {
			g2d.draw(eventDecorator);
		} else if (eventUse == EventUse.THROW) {
			g2d.fill(eventDecorator);
		}
		//eventDecorator.closePath();
		if (decorator!=null) {
			decorator.decorate(g2d, x, y, width, height);
		}
	}

	public EventType getEventType() {
		return eventType;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	public EventTrigger getEventTrigger() {
		return eventTrigger;
	}

	public void setEventTrigger(EventTrigger eventTrigger) {
		this.eventTrigger = eventTrigger;
	}

	public EventUse getEventUse() {
		return eventUse;
	}

	public void setEventUse(EventUse eventUse) {
		this.eventUse = eventUse;
	}

	public void setExceptionFor(Activity exceptionFor) {
		this.exceptionFor = exceptionFor;
	}

	public Activity getBoundingNode() {
		return exceptionFor;
	}

	public IGraphElementDecoration getDecorator() {
		return decorator;
	}

	public void setDecorator(IGraphElementDecoration decorator) {
		this.decorator = decorator;
	}
	
	@Deprecated
	public String isInterrupting() {
		return new Boolean(isInterrupting).toString();
	}
	
	@Deprecated
	public void setInterrupting(String isInterrupting) {
		this.isInterrupting = isInterrupting.equals("false")? false : true;
	}
	
	public String getParallelMultiple() {
		return parallelMultiple;
	}
	
	public void setParallelMultiple(String parallelMultiple) {
		this.parallelMultiple = parallelMultiple;
	}
}
