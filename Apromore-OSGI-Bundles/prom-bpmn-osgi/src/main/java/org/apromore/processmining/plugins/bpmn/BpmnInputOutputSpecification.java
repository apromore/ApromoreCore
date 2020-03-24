package org.apromore.processmining.plugins.bpmn;

import java.util.Collection;
import java.util.HashSet;

import org.xmlpull.v1.XmlPullParser;

public class BpmnInputOutputSpecification extends BpmnId {

	private Collection<BpmnId> dataInputs;
	private Collection<BpmnId> dataOutputs;
	
	public BpmnInputOutputSpecification(String tag) {
		super(tag);
		
		dataInputs = new HashSet<BpmnId>();
		dataOutputs = new HashSet<BpmnId>();
	}
	
	public Collection<BpmnId> getDataInputs() {
		
		return dataInputs;
	}
	
	public Collection<BpmnId> getDataOutputs() {
		
		return dataOutputs;
	}
	
	public void addDataInput(BpmnId dataInput) {
		
		dataInputs.add(dataInput);
	}
	
	public void addDataOutput(BpmnId dataOutput) {
		
		dataOutputs.add(dataOutput);
	}
	
	
	protected boolean importElements(XmlPullParser xpp, Bpmn bpmn) {
		if (super.importElements(xpp, bpmn)) {
			return true;
		}
		if (xpp.getName().equals("dataInput")) {
			BpmnId input = new BpmnId("dataInput");
			input.importElement(xpp, bpmn);
			dataInputs.add(input);
			return true;
		} else if (xpp.getName().equals("dataOutput")) {
			BpmnId output = new BpmnId("dataOutput");
			output.importElement(xpp, bpmn);
			dataInputs.add(output);
			return true;
		}
		/*
		 * Unknown tag.
		 */
		return false;
	}
	
	protected String exportElements() {
		/*
		 * Export node child elements.
		 */
		String s = super.exportElements();
		for (BpmnId input : dataInputs) {
			s += input.exportElement();
		}
		for (BpmnId output : dataOutputs) {
			s += output.exportElement();
		}
		return s;
	}
	
	protected void checkValidity(Bpmn bpmn) {
				// do not require id
	}
}
