package org.apromore.service.bebop.impl;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

public class ElementID {

	@XmlAttribute(name = "refProcessID")
	private String refprocessid;

	@XmlAttribute(name = "refName")
	private String name;

	@XmlValue
	private String value;

	ElementID(){

	}


	public ElementID( String value, String refprocessid, String name) {

		this.name= name;
		this.value = value;
		this.refprocessid  = refprocessid;
	}


	public String getRefprocessid() {
		return refprocessid;
	}


	public String getValue() {
		return value;
	}

	public String getName(){
		return name;
	}


	@Override
	public String toString() {
		return  value;
	}

	public String toStringIDName() {
		return  name+" "+value;
	}


}
