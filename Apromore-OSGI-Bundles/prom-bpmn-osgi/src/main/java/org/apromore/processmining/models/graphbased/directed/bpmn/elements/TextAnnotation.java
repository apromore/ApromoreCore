package org.apromore.processmining.models.graphbased.directed.bpmn.elements;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.apromore.processmining.models.graphbased.AttributeMap;
import org.apromore.processmining.models.graphbased.directed.AbstractDirectedGraph;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.shapes.Decorated;
import org.apromore.processmining.models.shapes.Rectangle;

public class TextAnnotation extends BPMNNode implements Decorated {
	
	private final static int stdWidth = 60;
	private final static int stdHeight = 40;
	private final static int stdLabelHight = 15;
	private final static int corner = 10;
	

	public TextAnnotation(AbstractDirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> bpmndiagram,
			String label) {
		super(bpmndiagram);
		fillAttributes(label);
	}
	
	public void decorate(Graphics2D g2d, double x, double y, double width, double height) {
		GeneralPath annotationDecorator = new GeneralPath();
		annotationDecorator.moveTo(0, 0);
		annotationDecorator.lineTo(stdWidth, 0);
		annotationDecorator.moveTo(0, 0);
		annotationDecorator.lineTo(0, stdHeight);
		annotationDecorator.lineTo(stdWidth, stdHeight);
		g2d.setColor(Color.black);
		g2d.draw(annotationDecorator);
		
		

		final int labelX = (int) Math.round(x) + 2;
		final int labelY = (int) Math.round(y);
		final int labelW = stdWidth - 2;
		final int labelH = stdHeight;

		JLabel label = new JLabel();
		label.setPreferredSize(new Dimension(labelW, labelH));
		label.setSize(new Dimension(labelW, labelH));
		label.setVerticalAlignment(SwingConstants.TOP);
		label.setText("<html>" + getLabel() + "</html>");

		label.setFont(new Font(label.getFont().getFamily(), label.getFont().getStyle(), 8));
		label.paint(g2d.create(labelX, labelY, labelW, labelH));
	}
	
	
	private void fillAttributes(String label) {
		getAttributeMap().put(AttributeMap.LABEL, label);
		getAttributeMap().put(AttributeMap.SHOWLABEL, false);
		getAttributeMap().put(AttributeMap.SHAPE, new Rectangle());
		getAttributeMap().put(AttributeMap.RESIZABLE, true);
		getAttributeMap().put(AttributeMap.SIZE, new Dimension(stdWidth, stdHeight));
		getAttributeMap().put(AttributeMap.BORDERWIDTH, 2);
		getAttributeMap().put(AttributeMap.STROKECOLOR, Color.white);
	}
}
