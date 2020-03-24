package org.apromore.processmining.plugins.bpmn;

import java.util.Collection;
import java.util.Map;

import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.TextAnnotation;
import org.xmlpull.v1.XmlPullParser;

public class BpmnTextAnnotation extends BpmnId {

	private BpmnText text = new BpmnText("text");
	
	public BpmnTextAnnotation(String tag) {		
		super(tag);
	}
	
	protected boolean importElements(XmlPullParser xpp, Bpmn bpmn) {
		if (xpp.getName().equals("text")) {
			text.importElement(xpp, bpmn);
			return true;
		}
		return false;
	}
	
	protected String exportElements() {
		/*
		 * Export node child elements.
		 */
		String s ="";
		if (text != null) {
			s += text.exportElement();
		}
		return s;
	}
	
	public void unmarshall(BPMNDiagram diagram, Map<String, BPMNNode> id2node) {
        TextAnnotation textAnnotation = diagram.addTextAnnotation(text.getText());
        textAnnotation.getAttributeMap().put("Original id", id);
        id2node.put(id, textAnnotation);
	}

	public void unmarshall(BPMNDiagram diagram, Collection<String> elements, Map<String, BPMNNode> id2node) {
		if (elements.contains(id)) {
			TextAnnotation textAnnotation = diagram.addTextAnnotation(text.getText());
			textAnnotation.getAttributeMap().put("Original id", id);
			id2node.put(id, textAnnotation);
		}
	}
	
	public void marshall(TextAnnotation textAnnotation) {
		super.marshall(textAnnotation);
		text.setText(textAnnotation.getLabel());
	}
}
