package org.apromore.processmining.models.graphbased.directed.bpmn.elements;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;

import javax.swing.JLabel;

import org.apromore.processmining.models.graphbased.AttributeMap;
import org.apromore.processmining.models.graphbased.directed.AbstractDirectedGraph;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.shapes.Decorated;
import org.apromore.processmining.models.shapes.Rectangle;

public class DataObject extends BPMNNode implements Decorated {

	private final static int stdWidth = 40;
	private final static int stdHeight = 60;
	private final static int stdLabelHight = 15;
	private final static int corner = 10;
	
	public DataObject(AbstractDirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> bpmndiagram,
			String label) {
		super(bpmndiagram);
		fillAttributes(label);
	}
	
	public void decorate(Graphics2D g2d, double x, double y, double width, double height) {

		GeneralPath dataDecorator = new GeneralPath();
		dataDecorator.moveTo(0, 0);
		dataDecorator.lineTo(0, stdHeight - stdLabelHight);
		dataDecorator.lineTo(stdWidth, stdHeight - stdLabelHight);
		dataDecorator.lineTo(stdWidth, corner);
		dataDecorator.lineTo(stdWidth - corner, 0);
		dataDecorator.lineTo(0, 0);
		dataDecorator.moveTo(stdWidth - corner, 0);
		dataDecorator.lineTo(stdWidth - corner , corner);
		dataDecorator.lineTo(stdWidth, corner);

		g2d.setStroke(new BasicStroke(1.0f));
		g2d.draw(dataDecorator);

		final int labelX = (int) Math.round(x);
		final int labelY = (int) Math.round(y + stdHeight - stdLabelHight);
		final int labelW = stdWidth;
		final int labelH = stdLabelHight;

		JLabel label = new JLabel(getLabel());
		label.setPreferredSize(new Dimension(labelW, labelH));
		label.setSize(new Dimension(labelW, labelH));

		label.setFont(new Font(label.getFont().getFamily(), label.getFont().getStyle(), 8));
		label.paint(g2d.create(labelX, labelY, labelW, labelH));
	}
	
	private void fillAttributes(String label) {
		getAttributeMap().put(AttributeMap.LABEL, label);
		getAttributeMap().put(AttributeMap.SHOWLABEL, false);
		getAttributeMap().put(AttributeMap.SHAPE, new Rectangle());
		getAttributeMap().put(AttributeMap.RESIZABLE, false);
		getAttributeMap().put(AttributeMap.SIZE, new Dimension(stdWidth, stdHeight));
		getAttributeMap().put(AttributeMap.BORDERWIDTH, 0);
		getAttributeMap().put(AttributeMap.STROKECOLOR, Color.white);
	}
}

