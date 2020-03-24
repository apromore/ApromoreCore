package org.apromore.processmining.models.graphbased.directed.bpmn.elements;

import java.awt.Graphics2D;

import org.apromore.processmining.models.graphbased.AttributeMap;
import org.apromore.processmining.models.graphbased.AttributeMap.ArrowType;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.shapes.Decorated;
import org.apromore.processmining.plugins.bpmn.BpmnAssociation.AssociationDirection;

public class Association extends BPMNEdge<BPMNNode, BPMNNode> implements Decorated {
	
	private IGraphElementDecoration decorator = null;

	private AssociationDirection direction;
	
	public Association(BPMNNode source, BPMNNode target, AssociationDirection direction) {
		super(source, target);
		this.direction = direction;
		fillAttributes();
	}

	private void fillAttributes() {
		if (direction != null) {
			switch(direction) {
				case BOTH: 
					getAttributeMap().put(AttributeMap.EDGESTART, ArrowType.ARROWTYPE_SIMPLE);
					//$FALL-THROUGH$
				case ONE:
					getAttributeMap().put(AttributeMap.EDGESTART, ArrowType.ARROWTYPE_SIMPLE);
					//$FALL-THROUGH$
				default :
					break;
			}
		}
		getAttributeMap().put(AttributeMap.EDGESTARTFILLED, false);
		getAttributeMap().put(AttributeMap.EDGEENDFILLED, false);	
		getAttributeMap().put(AttributeMap.DASHPATTERN, new float[] { (float)2.0, (float)2.0 });
	}

	public IGraphElementDecoration getDecorator() {
		return decorator;
	}

	public void setDecorator(IGraphElementDecoration decorator) {
		this.decorator = decorator;
	}

	public void decorate(Graphics2D g2d, double x, double y, double width, double height) {
		if (decorator != null) {
			decorator.decorate(g2d, x, y, width, height);
		}
	}
	
	public AssociationDirection getDirection() {
		return direction;
	}
	
	public void setDirection(AssociationDirection direction) {
		this.direction = direction;
	}
}
