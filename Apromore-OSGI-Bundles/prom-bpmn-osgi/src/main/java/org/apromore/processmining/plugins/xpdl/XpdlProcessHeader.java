package org.apromore.processmining.plugins.xpdl;

import java.util.Arrays;

import org.apromore.processmining.plugins.xpdl.text.XpdlDescription;
import org.apromore.processmining.plugins.xpdl.text.XpdlLimit;
import org.apromore.processmining.plugins.xpdl.text.XpdlPriority;
import org.apromore.processmining.plugins.xpdl.text.XpdlValidFrom;
import org.apromore.processmining.plugins.xpdl.text.XpdlValidTo;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="ProcessHeader"> <xsd:complexType> <xsd:sequence>
 *         <xsd:element ref="xpdl:Created" minOccurs="0"/> <xsd:element
 *         ref="xpdl:Description" minOccurs="0"/> <xsd:element
 *         ref="xpdl:Priority" minOccurs="0"/> <xsd:element ref="xpdl:Limit"
 *         minOccurs="0"/> <xsd:element ref="xpdl:ValidFrom" minOccurs="0"/>
 *         <xsd:element ref="xpdl:ValidTo" minOccurs="0"/> <xsd:element
 *         ref="xpdl:TimeEstimation" minOccurs="0"/> <xsd:any
 *         namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute
 *         name="DurationUnit"> <xsd:simpleType> <xsd:restriction
 *         base="xsd:NMTOKEN"> <xsd:enumeration value="Y"/> <xsd:enumeration
 *         value="M"/> <xsd:enumeration value="D"/> <xsd:enumeration value="h"/>
 *         <xsd:enumeration value="m"/> <xsd:enumeration value="s"/>
 *         </xsd:restriction> </xsd:simpleType> </xsd:attribute>
 *         <xsd:anyAttribute namespace="##other" processContents="lax"/>
 *         </xsd:complexType> </xsd:element>
 */
public class XpdlProcessHeader extends XpdlElement {

	/*
	 * Attributes
	 */
	private String durationUnit;

	/*
	 * Elements
	 */
	private XpdlCreated created;
	private XpdlDescription description;
	private XpdlPriority priority;
	private XpdlLimit limit;
	private XpdlValidFrom validFrom;
	private XpdlValidTo validTo;
	private XpdlTimeEstimation timeEstimation;

	public XpdlProcessHeader(String tag) {
		super(tag);

		durationUnit = null;

		created = null;
		description = null;
		priority = null;
		limit = null;
		validFrom = null;
		validTo = null;
		timeEstimation = null;
	}

	protected boolean importElements(XmlPullParser xpp, Xpdl xpdl) {
		if (super.importElements(xpp, xpdl)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("Created")) {
			created = new XpdlCreated("Created");
			created.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Description")) {
			description = new XpdlDescription("Description");
			description.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Priority")) {
			priority = new XpdlPriority("Priority");
			priority.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Limit")) {
			limit = new XpdlLimit("Limit");
			limit.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("ValidFrom")) {
			validFrom = new XpdlValidFrom("ValidFrom");
			validFrom.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("ValidTo")) {
			validTo = new XpdlValidTo("ValidTo");
			validTo.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("TimeEstimation")) {
			timeEstimation = new XpdlTimeEstimation("TimeEstimation");
			timeEstimation.importElement(xpp, xpdl);
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
		if (created != null) {
			s += created.exportElement();
		}
		if (description != null) {
			s += description.exportElement();
		}
		if (priority != null) {
			s += priority.exportElement();
		}
		if (limit != null) {
			s += limit.exportElement();
		}
		if (validFrom != null) {
			s += validFrom.exportElement();
		}
		if (validTo != null) {
			s += validTo.exportElement();
		}
		if (timeEstimation != null) {
			s += timeEstimation.exportElement();
		}
		return s;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "DurationUnit");
		if (value != null) {
			durationUnit = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (durationUnit != null) {
			s += exportAttribute("DurationUnit", durationUnit);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		checkRestriction(xpdl, "DurationUnit", durationUnit, Arrays.asList("Y", "M", "D", "h", "m", "s"), false);
	}

	public String getDurationUnit() {
		return durationUnit;
	}

	public void setDurationUnit(String durationUnit) {
		this.durationUnit = durationUnit;
	}

	public XpdlCreated getCreated() {
		return created;
	}

	public void setCreated(XpdlCreated created) {
		this.created = created;
	}

	public XpdlDescription getDescription() {
		return description;
	}

	public void setDescription(XpdlDescription description) {
		this.description = description;
	}

	public XpdlPriority getPriority() {
		return priority;
	}

	public void setPriority(XpdlPriority priority) {
		this.priority = priority;
	}

	public XpdlLimit getLimit() {
		return limit;
	}

	public void setLimit(XpdlLimit limit) {
		this.limit = limit;
	}

	public XpdlValidFrom getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(XpdlValidFrom validFrom) {
		this.validFrom = validFrom;
	}

	public XpdlValidTo getValidTo() {
		return validTo;
	}

	public void setValidTo(XpdlValidTo validTo) {
		this.validTo = validTo;
	}

	public XpdlTimeEstimation getTimeEstimation() {
		return timeEstimation;
	}

	public void setTimeEstimation(XpdlTimeEstimation timeEstimation) {
		this.timeEstimation = timeEstimation;
	}
}
