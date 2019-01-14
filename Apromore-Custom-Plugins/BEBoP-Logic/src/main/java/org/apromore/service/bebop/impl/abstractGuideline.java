package org.apromore.service.bebop.impl;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;

@XmlAccessorType(XmlAccessType.FIELD)

public  class abstractGuideline  {



	@XmlTransient
	protected boolean status;

	@XmlTransient
	protected String IDProcess;



	@XmlAttribute(name = "id", required = true)
	protected String id;
	@XmlAttribute(name = "Name", required = true)
	protected  String Name;

	@XmlElement(name = "Description", required = true)
	protected String Description;

	@XmlElement(name = "Suggestion", required = true)
	protected String Suggestion;
	@XmlElement(name = "ElementID", required = false)
	@XmlElementWrapper(name = "Elements",  nillable=false)
	protected Collection<ElementID> Elements = null;

	abstractGuideline(){

	}


	public boolean getStatus() {

		return status;
	}

	@SuppressWarnings("null")
	public ArrayList<String> getIDsNameAndType(){
		ArrayList<String> ret = new ArrayList<String>();
		if(!getStatus()){
			if(Elements!=null){
				for(ElementID elem: Elements)ret.add(elem.toStringIDName());
			}
		}
		return ret;
	}

	@SuppressWarnings("null")
	public ArrayList<String> getID(){
		ArrayList<String> ret = new ArrayList<String>();
		if(!getStatus()){
			if(Elements!=null){
				for(ElementID elem: Elements)ret.add(elem.toString());
			}
		}
		return ret;
	}


	public String getid() {
		return id;
	}



	public Collection<ElementID> getElements() {
		return Elements;
	}



	public void setElements(String element, String refprocessid, String name) {
		if(Elements==null){
			Elements = new ArrayList<ElementID>();
		}
		Elements.add(new ElementID(element, refprocessid, name));
	}


	public String getDescription() {

		return Description;

	}

	public String getName() {

		return Name;
	}


	public String getProcessID() {
		return IDProcess;
	}

	public String getSuggestion() {
		return Suggestion;
	}




	public String getState(){
		switch (Thread.currentThread().getState()) {
			case TERMINATED:
				return "OK";

			default:
				return "IN PROGRESS";
		}

	}

	public String getColor(){
		String color = "#47d147";// "green"; //"#FF0000";
		if(!(this.getSuggestion().equals("Well done!") | this.getSuggestion().equals("Ben Fatto!"))){
			color = " #cc2900";//"red";//"#00FF7F";
		}
		return color;
	}

}
