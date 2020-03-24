package org.apromore.processmining.plugins.xpdl;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apromore.processmining.plugins.xpdl.text.XpdlText;
import org.xmlpull.v1.XmlPullParser;

/**
 * Basic XPDL element. All XPDL objects extend this class (either directly or
 * indirectly).
 * 
 * @author hverbeek
 */
public class XpdlElement {

	public XpdlText XpdlText;
	/**
	 * The PNML tag for this element.
	 */
	public String tag;

	public int lineNumber;

	/**
	 * Creates a fresh PNML element.
	 * 
	 * @param tag
	 */
	public XpdlElement(String tag) {
		this.tag = tag;
	}

	/**
	 * Imports the given element.
	 * 
	 * @param xpp
	 * @param pnml
	 */
	public void importElement(XmlPullParser xpp, Xpdl xpdl) {
		lineNumber = xpp.getLineNumber();
		/*
		 * Import all attributes of this element.
		 */
		importAttributes(xpp, xpdl);
		/*
		 * Create afresh stack to keep track of start tags to match.
		 */
		ArrayList<String> stack = new ArrayList<String>();
		/*
		 * Add the current tag to this stack, as we still have to find the
		 * matching end tag.
		 */
		stack.add(tag);
		/*
		 * As long as the stack is not empty, we're still working on this
		 * object.
		 */
		while (!stack.isEmpty()) {
			/*
			 * Get next event.
			 */
			try {
				int eventType = xpp.next();
				if (eventType == XmlPullParser.END_DOCUMENT) {
					/*
					 * End of document. Should not happen.
					 */
					xpdl.log(tag, xpp.getLineNumber(), "Found end of document");
					//System.err.println("Line " + xpp.getLineNumber() + ": Malformed PNML document: No </"+ tag + "> found.");
					//throw new Exception("Malformed PNML document: No </"+ tag + "> found.");
					return;
				} else if (eventType == XmlPullParser.START_TAG) {
					//pnml.logInfo("Tag " + tag, XLifecycleExtension.StandardModel.START, "Line " + xpp.getLineNumber());
					/*
					 * Start tag. Push it on the stack.
					 */
					stack.add(xpp.getName());
					/*
					 * If this tag is the second on the stack, then it is a
					 * direct child.
					 */
					if (stack.size() == 2) {
						/*
						 * For a direct child, check whether the tag is known.
						 * If so, take proper action. Note that this needs not
						 * to be done for other offspring.
						 */
						if (importElements(xpp, xpdl)) {
							/*
							 * Known start tag. The end tag has been matched and
							 * can be popped from the stack.
							 */
							stack.remove(stack.size() - 1);
						}
					}
				} else if ((eventType == XmlPullParser.END_TAG)) {
					//pnml.logInfo("Tag " + tag, XLifecycleExtension.StandardModel.COMPLETE, "Line " + xpp.getLineNumber());
					/*
					 * End tag. Should be identical to top of the stack.
					 */
					if (xpp.getName().equals(stack.get(stack.size() - 1))) {
						/*
						 * Yes it is. Pop the stack.
						 */
						stack.remove(stack.size() - 1);
					} else {
						/*
						 * No it is not. XML violation.
						 */
						xpdl.log(tag, xpp.getLineNumber(),
								"Found " + xpp.getName() + ", expected " + stack.get(stack.size() - 1));
						return;
					}
				} else if (eventType == XmlPullParser.TEXT) {
					/*
					 * Plain text. Import it.
					 */
					//pnml.logInfo("Text", XLifecycleExtension.StandardModel.UNKNOWN, "Line " + xpp.getLineNumber(), xpp.getText());
					importText(xpp.getText(), xpdl);
				}
			} catch (Exception ex) {
				xpdl.log(tag, xpp.getLineNumber(), ex.getMessage());
				return;
			}
		}
		/*
		 * The element has been imported. Now is a good time to check its
		 * validity.
		 */
		checkValidity(xpdl);
	}

	/**
	 * Exports the element.
	 * 
	 * @return
	 */
	public String exportElement() {
		/*
		 * Export all attributes of this element.
		 */
		String s = "<" + tag;
		s += exportAttributes();
		/*
		 * Export all child elements.
		 */
		String t = exportElements();
		if (getXpdlText() == null) {
			if (t.equals("")) {
				/*
				 * No child elements, use combined start-end tag.
				 */
				s += "/>\n";
			} else {
				/*
				 * Child elements, use separated start and end tags.
				 */
				s += ">\n\t" + t + "\n</" + tag + ">\n";
			}
		} else {
			if (t.equals("")) {
				/*
				 * No child elements, use combined start-end tag.
				 */
				s += ">" + getXpdlText().getText() + "</" + tag + ">\n";
			} else {
				/*
				 * Child elements, use separated start and end tags.
				 */
				s += ">\n\t" + getXpdlText().getText() + t + "\n</" + tag + ">\n";
			}
		}

		return s;
	}

	/**
	 * Imports all standard attributes: None. If some subclass has attributes,
	 * this method needs to be overruled by it.
	 * 
	 * @param xpp
	 * @param xpdl
	 */
	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
	}

	/**
	 * Exports all standard attributes: None. If some subclass has attributes,
	 * this method needs to be overruled by it.
	 * 
	 * @return
	 */
	protected String exportAttributes() {
		return "";
	}

	/**
	 * Imports all standard child elements: None. If some subclass has child
	 * elements, this method needs to be overruled by it.
	 * 
	 * @param xpp
	 * @param xpdl
	 * @return
	 */
	protected boolean importElements(XmlPullParser xpp, Xpdl xpdl) {
		return false;
	}

	/**
	 * Exports all standard elements: None. If some subclass has child elements,
	 * this method needs to be overruled by it.
	 * 
	 * @return
	 */
	protected String exportElements() {
		return "";
	}

	/**
	 * Imports standard text: No action. If some subclass needs to import text,
	 * this method needs to be overruled by it.
	 * 
	 * @param text
	 * @param xpdl
	 */
	protected void importText(String text, Xpdl Xpdl) {
	}

	/**
	 * Default way to export some attribute.
	 * 
	 * @param tag
	 *            The attribute tag.
	 * @param value
	 *            The attribute value.
	 * @return
	 */
	protected String exportAttribute(String tag, String value) {
		return " " + tag + "=\"" + value + "\"";
	}

	/**
	 * Default check for validity: No action. If some subclass needs to check
	 * validity, this method needs to be overruled by it.
	 * 
	 * @param xpdl
	 */
	protected void checkValidity(Xpdl xpdl) {
	}

	protected void checkRestriction(Xpdl xpdl, String label, String value, List<String> restriction, boolean isRequired) {
		if (isRequired) {
			checkRequired(xpdl, label, value);
		}
		if ((value != null) && !restriction.contains(value)) {
			xpdl.log(tag, lineNumber, "Expected a value from " + restriction.toString() + " for " + label);
		}
	}

	protected void checkBoolean(Xpdl xpdl, String label, String value, boolean isRequired) {
		String lowerCaseValue = (value == null) ? null : value.toLowerCase();
		checkRestriction(xpdl, label, lowerCaseValue, Arrays.asList("true", "false", "1", "0"), isRequired);
	}

	protected void checkRequired(Xpdl xpdl, String label, String value) {
		if (value == null) {
			xpdl.log(tag, lineNumber, "Expected " + label);
		}
	}

	protected void checkDouble(Xpdl xpdl, String label, String value, boolean isRequired) {
		if (isRequired) {
			checkRequired(xpdl, label, value);
		}
		if (value != null) {
			try {
				Double.valueOf(value);
			} catch (Exception ex) {
				xpdl.log(tag, lineNumber, ex.getMessage());
			}
		}
	}

	protected void checkInteger(Xpdl xpdl, String label, String value, boolean isRequired) {
		if (isRequired) {
			checkRequired(xpdl, label, value);
		}
		if (value != null) {
			try {
				Integer.valueOf(value);
			} catch (Exception ex) {
				xpdl.log(tag, lineNumber, ex.getMessage());
			}
		}
	}

	protected void checkURI(Xpdl xpdl, String label, String value, boolean isRequired) {
		if (isRequired) {
			checkRequired(xpdl, label, value);
		}
		if (value != null) {
			try {
				new URI(value);
			} catch (Exception ex) {
				xpdl.log(tag, lineNumber, ex.getMessage());
			}
		}
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public XpdlText getXpdlText() {
		return XpdlText;
	}

	public void setXpdlText(XpdlText xpdlText) {
		XpdlText = xpdlText;
	}

}
