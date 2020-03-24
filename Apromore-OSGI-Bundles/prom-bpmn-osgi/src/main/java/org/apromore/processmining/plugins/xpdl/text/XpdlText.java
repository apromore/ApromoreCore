package org.apromore.processmining.plugins.xpdl.text;

import org.apromore.processmining.plugins.xpdl.Xpdl;
import org.apromore.processmining.plugins.xpdl.XpdlElement;

public class XpdlText extends XpdlElement {

	protected String text;

	public XpdlText(String tag) {
		super(tag);

		text = "";
	}

	protected void importText(String text, Xpdl Xpdl) {
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
