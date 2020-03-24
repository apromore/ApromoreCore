package org.apromore.processmining.plugins.xpdl;

import org.apromore.processmining.plugins.xpdl.collections.XpdlVendorExtensions;
import org.apromore.processmining.plugins.xpdl.graphics.XpdlLayoutInfo;
import org.apromore.processmining.plugins.xpdl.text.XpdlDescription;
import org.apromore.processmining.plugins.xpdl.text.XpdlDocumentation;
import org.apromore.processmining.plugins.xpdl.text.XpdlModificationDate;
import org.apromore.processmining.plugins.xpdl.text.XpdlPriorityUnit;
import org.apromore.processmining.plugins.xpdl.text.XpdlVendor;
import org.apromore.processmining.plugins.xpdl.text.XpdlXpdlVersion;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="PackageHeader"> <xsd:complexType> <xsd:sequence>
 *         <xsd:element ref="xpdl:XPDLVersion"/> <xsd:element
 *         ref="xpdl:Vendor"/> <xsd:element ref="xpdl:Created"/> <xsd:element
 *         ref="xpdl:ModificationDate" minOccurs="0"/> <xsd:element
 *         ref="xpdl:Description" minOccurs="0"/> <xsd:element
 *         ref="xpdl:Documentation" minOccurs="0"/> <xsd:element
 *         ref="xpdl:PriorityUnit" minOccurs="0"/> <xsd:element
 *         ref="xpdl:CostUnit" minOccurs="0"/> <xsd:element
 *         ref="xpdl:VendorExtensions" minOccurs="0"/> <xsd:element
 *         ref="xpdl:LayoutInfo" minOccurs="0"/> <xsd:any namespace="##other"
 *         processContents="lax" minOccurs="0" maxOccurs="unbounded"/>
 *         </xsd:sequence> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:complexType> </xsd:element>
 */
public class XpdlPackageHeader extends XpdlElement {

	/*
	 * Elements
	 */
	private XpdlXpdlVersion version;
	private XpdlVendor vendor;
	private XpdlCreated created;
	private XpdlModificationDate modificationDate;
	private XpdlDescription description;
	private XpdlDocumentation documentation;
	private XpdlPriorityUnit priorityUnit;
	private XpdlCostUnit costUnit;
	private XpdlVendorExtensions vendorExtensions;
	private XpdlLayoutInfo layoutInfo;

	public XpdlPackageHeader(String tag) {
		super(tag);

		version = null;
		vendor = null;
		created = null;
		modificationDate = null;
		description = null;
		documentation = null;
		priorityUnit = null;
		costUnit = null;
		vendorExtensions = null;
		layoutInfo = null;
	}

	protected boolean importElements(XmlPullParser xpp, Xpdl xpdl) {
		if (super.importElements(xpp, xpdl)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("XPDLVersion")) {
			version = new XpdlXpdlVersion("XPDLVersion");
			version.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Vendor")) {
			vendor = new XpdlVendor("Vendor");
			vendor.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Created")) {
			created = new XpdlCreated("Created");
			created.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("ModificationDate")) {
			modificationDate = new XpdlModificationDate("ModificationDate");
			modificationDate.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Description")) {
			description = new XpdlDescription("Description");
			description.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Documentation")) {
			documentation = new XpdlDocumentation("Documentation");
			documentation.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("PriorityUnit")) {
			priorityUnit = new XpdlPriorityUnit("PriorityUnit");
			priorityUnit.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("CostUnit")) {
			costUnit = new XpdlCostUnit("CostUnit");
			costUnit.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("VendorExtensions")) {
			vendorExtensions = new XpdlVendorExtensions("VendorExtensions");
			vendorExtensions.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("LayoutInfo")) {
			layoutInfo = new XpdlLayoutInfo("LayoutInfo");
			layoutInfo.importElement(xpp, xpdl);
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
		if (version != null) {
			s += version.exportElement();
		}
		if (vendor != null) {
			s += vendor.exportElement();
		}
		if (created != null) {
			s += created.exportElement();
		}
		if (modificationDate != null) {
			s += modificationDate.exportElement();
		}
		if (description != null) {
			s += description.exportElement();
		}
		if (documentation != null) {
			s += documentation.exportElement();
		}
		if (priorityUnit != null) {
			s += priorityUnit.exportElement();
		}
		if (costUnit != null) {
			s += costUnit.exportElement();
		}
		if (vendorExtensions != null) {
			s += vendorExtensions.exportElement();
		}
		if (layoutInfo != null) {
			s += layoutInfo.exportElement();
		}
		return s;
	}

	public XpdlXpdlVersion getVersion() {
		return version;
	}

	public void setVersion(XpdlXpdlVersion version) {
		this.version = version;
	}

	public XpdlVendor getVendor() {
		return vendor;
	}

	public void setVendor(XpdlVendor vendor) {
		this.vendor = vendor;
	}

	public XpdlCreated getCreated() {
		return created;
	}

	public void setCreated(XpdlCreated created) {
		this.created = created;
	}

	public XpdlModificationDate getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(XpdlModificationDate modificationDate) {
		this.modificationDate = modificationDate;
	}

	public XpdlDescription getDescription() {
		return description;
	}

	public void setDescription(XpdlDescription description) {
		this.description = description;
	}

	public XpdlDocumentation getDocumentation() {
		return documentation;
	}

	public void setDocumentation(XpdlDocumentation documentation) {
		this.documentation = documentation;
	}

	public XpdlPriorityUnit getPriorityUnit() {
		return priorityUnit;
	}

	public void setPriorityUnit(XpdlPriorityUnit priorityUnit) {
		this.priorityUnit = priorityUnit;
	}

	public XpdlCostUnit getCostUnit() {
		return costUnit;
	}

	public void setCostUnit(XpdlCostUnit costUnit) {
		this.costUnit = costUnit;
	}

	public XpdlVendorExtensions getVendorExtensions() {
		return vendorExtensions;
	}

	public void setVendorExtensions(XpdlVendorExtensions vendorExtensions) {
		this.vendorExtensions = vendorExtensions;
	}

	public XpdlLayoutInfo getLayoutInfo() {
		return layoutInfo;
	}

	public void setLayoutInfo(XpdlLayoutInfo layoutInfo) {
		this.layoutInfo = layoutInfo;
	}
}
