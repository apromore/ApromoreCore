package org.apromore.processmining.plugins.xpdl.datatypes;

import org.apromore.processmining.plugins.xpdl.Xpdl;
import org.apromore.processmining.plugins.xpdl.XpdlElement;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:group name="DataTypes"> <xsd:choice> <xsd:element
 *         ref="xpdl:BasicType"/> <xsd:element ref="xpdl:DeclaredType"/>
 *         <xsd:element ref="xpdl:SchemaType"/> <xsd:element
 *         ref="xpdl:ExternalReference"/> <xsd:element ref="xpdl:RecordType"/>
 *         <xsd:element ref="xpdl:UnionType"/> <xsd:element
 *         ref="xpdl:EnumerationType"/> <xsd:element ref="xpdl:ArrayType"/>
 *         <xsd:element ref="xpdl:ListType"/> </xsd:choice> </xsd:group>
 */
public class XpdlDataTypes extends XpdlElement {

	/*
	 * Elements
	 */
	private XpdlBasicType basicType;
	private XpdlDeclaredType declaredType;
	private XpdlSchemaType schemaType;
	private XpdlExternalReference externalReference;
	private XpdlRecordType recordType;
	private XpdlUnionType unionType;
	private XpdlEnumerationType enumerationType;
	private XpdlArrayType arrayType;
	private XpdlListType listType;

	public XpdlDataTypes(String tag) {
		super(tag);

		basicType = null;
		declaredType = null;
		schemaType = null;
		externalReference = null;
		recordType = null;
		unionType = null;
		enumerationType = null;
		arrayType = null;
		listType = null;
	}

	protected boolean importElements(XmlPullParser xpp, Xpdl xpdl) {
		if (super.importElements(xpp, xpdl)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("BasicType")) {
			basicType = new XpdlBasicType("BasicType");
			basicType.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("DeclaredType")) {
			declaredType = new XpdlDeclaredType("DeclaredType");
			declaredType.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("SchemaType")) {
			schemaType = new XpdlSchemaType("SchemaType");
			schemaType.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("ExternalReference")) {
			externalReference = new XpdlExternalReference("ExternalReference");
			externalReference.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("RecordType")) {
			recordType = new XpdlRecordType("RecordType");
			recordType.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("UnionType")) {
			unionType = new XpdlUnionType("UnionType");
			unionType.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("EnumerationType")) {
			enumerationType = new XpdlEnumerationType("EnumerationType");
			enumerationType.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("ArrayType")) {
			arrayType = new XpdlArrayType("ArrayType");
			arrayType.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("ListType")) {
			listType = new XpdlListType("ListType");
			listType.importElement(xpp, xpdl);
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
		if (basicType != null) {
			s += basicType.exportElement();
		}
		if (declaredType != null) {
			s += declaredType.exportElement();
		}
		if (schemaType != null) {
			s += schemaType.exportElement();
		}
		if (externalReference != null) {
			s += externalReference.exportElement();
		}
		if (recordType != null) {
			s += recordType.exportElement();
		}
		if (unionType != null) {
			s += unionType.exportElement();
		}
		if (enumerationType != null) {
			s += enumerationType.exportElement();
		}
		if (arrayType != null) {
			s += arrayType.exportElement();
		}
		if (listType != null) {
			s += listType.exportElement();
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		int nr = 0;
		if (basicType != null) {
			nr++;
		}
		if (declaredType != null) {
			nr++;
		}
		if (schemaType != null) {
			nr++;
		}
		if (externalReference != null) {
			nr++;
		}
		if (recordType != null) {
			nr++;
		}
		if (unionType != null) {
			nr++;
		}
		if (enumerationType != null) {
			nr++;
		}
		if (arrayType != null) {
			nr++;
		}
		if (listType != null) {
			nr++;
		}
		if (nr != 1) {
			xpdl.log(tag, lineNumber, "Expected a single basic type");
		}
	}

	public XpdlBasicType getBasicType() {
		return basicType;
	}

	public void setBasicType(XpdlBasicType basicType) {
		this.basicType = basicType;
	}

	public XpdlDeclaredType getDeclaredType() {
		return declaredType;
	}

	public void setDeclaredType(XpdlDeclaredType declaredType) {
		this.declaredType = declaredType;
	}

	public XpdlSchemaType getSchemaType() {
		return schemaType;
	}

	public void setSchemaType(XpdlSchemaType schemaType) {
		this.schemaType = schemaType;
	}

	public XpdlExternalReference getExternalReference() {
		return externalReference;
	}

	public void setExternalReference(XpdlExternalReference externalReference) {
		this.externalReference = externalReference;
	}

	public XpdlRecordType getRecordType() {
		return recordType;
	}

	public void setRecordType(XpdlRecordType recordType) {
		this.recordType = recordType;
	}

	public XpdlUnionType getUnionType() {
		return unionType;
	}

	public void setUnionType(XpdlUnionType unionType) {
		this.unionType = unionType;
	}

	public XpdlEnumerationType getEnumerationType() {
		return enumerationType;
	}

	public void setEnumerationType(XpdlEnumerationType enumerationType) {
		this.enumerationType = enumerationType;
	}

	public XpdlArrayType getArrayType() {
		return arrayType;
	}

	public void setArrayType(XpdlArrayType arrayType) {
		this.arrayType = arrayType;
	}

	public XpdlListType getListType() {
		return listType;
	}

	public void setListType(XpdlListType listType) {
		this.listType = listType;
	}
}
