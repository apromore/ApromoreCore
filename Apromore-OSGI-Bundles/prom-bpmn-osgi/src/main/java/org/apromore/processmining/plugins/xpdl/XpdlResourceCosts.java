package org.apromore.processmining.plugins.xpdl;

import java.util.Arrays;

import org.apromore.processmining.plugins.xpdl.text.XpdlText;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="ResourceCosts"> <xsd:complexType> <xsd:sequence>
 *         <xsd:element name="ResourceCostName"> <xsd:simpleType>
 *         <xsd:restriction base="xsd:string"> <xsd:maxLength value="100"/>
 *         <xsd:minLength value="0"/> <xsd:whiteSpace value="preserve"/>
 *         </xsd:restriction> </xsd:simpleType> </xsd:element> <xsd:element
 *         name="ResourceCost"> <xsd:simpleType> <xsd:restriction
 *         base="xsd:decimal"> <xsd:fractionDigits value="2"/>
 *         </xsd:restriction> </xsd:simpleType> </xsd:element> <xsd:element
 *         name="CostUnitOfTime"> <xsd:simpleType> <xsd:restriction
 *         base="xsd:NMTOKEN"> <xsd:enumeration value="second"/>
 *         <xsd:enumeration value="minute"/> <xsd:enumeration value="hour"/>
 *         </xsd:restriction> </xsd:simpleType> </xsd:element> </xsd:sequence>
 *         </xsd:complexType> </xsd:element>
 */
public class XpdlResourceCosts extends XpdlElement {

	private class XpdlResourceCostName extends XpdlText {
		public XpdlResourceCostName(String tag) {
			super(tag);
		}
	}

	private class XpdlResourceCost extends XpdlText {
		public XpdlResourceCost(String tag) {
			super(tag);
		}
	}

	private class XpdlCostUnitOfTime extends XpdlText {
		public XpdlCostUnitOfTime(String tag) {
			super(tag);
		}

		protected void checkValidity(Xpdl xpdl) {
			super.checkValidity(xpdl);
			checkRestriction(xpdl, "Text", text, Arrays.asList("second", "minute", "hour"), true);
		}
	}

	/*
	 * Elements
	 */
	private XpdlResourceCostName resourceCostName;
	private XpdlResourceCost resourceCost;
	private XpdlCostUnitOfTime costUnitOfTime;

	public XpdlResourceCosts(String tag) {
		super(tag);

		resourceCostName = null;
		resourceCost = null;
		costUnitOfTime = null;
	}

	protected boolean importElements(XmlPullParser xpp, Xpdl xpdl) {
		if (super.importElements(xpp, xpdl)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("ResourceCostName")) {
			resourceCostName = new XpdlResourceCostName("ResourceCostName");
			resourceCostName.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("ResourceCost")) {
			resourceCost = new XpdlResourceCost("ResourceCost");
			resourceCost.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("CostUnitOfTime")) {
			costUnitOfTime = new XpdlCostUnitOfTime("CostUnitOfTime");
			costUnitOfTime.importElement(xpp, xpdl);
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
		if (resourceCostName != null) {
			s += resourceCostName.exportElement();
		}
		if (resourceCost != null) {
			s += resourceCost.exportElement();
		}
		if (costUnitOfTime != null) {
			s += costUnitOfTime.exportElement();
		}
		return s;
	}

	public XpdlResourceCostName getResourceCostName() {
		return resourceCostName;
	}

	public void setResourceCostName(XpdlResourceCostName resourceCostName) {
		this.resourceCostName = resourceCostName;
	}

	public XpdlResourceCost getResourceCost() {
		return resourceCost;
	}

	public void setResourceCost(XpdlResourceCost resourceCost) {
		this.resourceCost = resourceCost;
	}

	public XpdlCostUnitOfTime getCostUnitOfTime() {
		return costUnitOfTime;
	}

	public void setCostUnitOfTime(XpdlCostUnitOfTime costUnitOfTime) {
		this.costUnitOfTime = costUnitOfTime;
	}

}
