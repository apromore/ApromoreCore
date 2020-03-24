package org.apromore.processmining.plugins.bpmn;


public class BpmnText extends BpmnElement {

	protected String text;

	public BpmnText(String tag) {
		super(tag);

		text = "";
	}

	protected void importText(String text, Bpmn bpmn) {
		this.text = (this.text + text).trim();
	}

	protected String exportElements() {
		return text;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
