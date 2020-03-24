package org.apromore.processmining.plugins.xpdl;

import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="CostStructure"> <xsd:annotation>
 *         <xsd:documentation> Activities incur costs in a number of way, the
 *         use up resources which may be people, machines, services, computers,
 *         office space, etc. Activities also use up fixed costs which may be
 *         assigned on an activity by activity basis, thus allowing for the
 *         assignment of overhead. Fixed costs are assigned in bulk, that is to
 *         say there is one fixed cost per activity. However resource costs are
 *         assigned on a resource by resource basis, each one having a cost and
 *         an associated time unit. </xsd:documentation> </xsd:annotation>
 *         <xsd:complexType> <xsd:choice> <xsd:element ref="xpdl:ResourceCosts"
 *         minOccurs="0" maxOccurs="unbounded"/> <xsd:element name="FixedCost"
 *         type="xsd:integer"/> </xsd:choice> </xsd:complexType> </xsd:element>
 */
public class XpdlCostStructure extends XpdlElement {

	private class XpdlFixedCost extends XpdlElement {

		private String text;

		public XpdlFixedCost(String tag) {
			super(tag);

			text = "";
		}

		protected void importText(String text, Xpdl Xpdl) {
			this.text = (this.text + text).trim();
		}

		protected void checkValidity(Xpdl xpdl) {
			super.checkValidity(xpdl);
			checkInteger(xpdl, "Text", text, false);
		}
	}

	/*
	 * Elements
	 */
	private final List<XpdlResourceCosts> resourceCostsList;
	private XpdlFixedCost fixedCost;

	public XpdlCostStructure(String tag) {
		super(tag);

		resourceCostsList = new ArrayList<XpdlResourceCosts>();
		fixedCost = null;
	}

	protected boolean importElements(XmlPullParser xpp, Xpdl xpdl) {
		if (super.importElements(xpp, xpdl)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("ResourceCosts")) {
			XpdlResourceCosts resourceCosts = new XpdlResourceCosts("ResourceCosts");
			resourceCosts.importElement(xpp, xpdl);
			resourceCostsList.add(resourceCosts);
			return true;
		}
		if (xpp.getName().equals("FixedCost")) {
			fixedCost = new XpdlFixedCost("FixedCost");
			fixedCost.importElement(xpp, xpdl);
			return true;
		}
		/*
		 * Unknown tag
		 */
		return false;
	}

	protected String exportElements() {
		/*
		 * Export node child elements.
		 */
		String s = super.exportElements();
		for (XpdlResourceCosts resourceCosts : resourceCostsList) {
			s += resourceCosts.exportElement();
		}
		if (fixedCost != null) {
			s += fixedCost.exportElement();
		}
		return s;
	}

	public XpdlFixedCost getFixedCost() {
		return fixedCost;
	}

	public void setFixedCost(XpdlFixedCost fixedCost) {
		this.fixedCost = fixedCost;
	}

	public List<XpdlResourceCosts> getResourceCostsList() {
		return resourceCostsList;
	}
}
