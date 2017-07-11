package org.apromore.service.bebop.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"verificationType",
		"definitionID",
		"status",
		"description",
		"guidelines"
})
@XmlRootElement(name = "UnderstandabilityResult")
public class GuidelinesFactory {

	@XmlElement(name = "VerificationType", required = true)
	private String verificationType;
	@XmlElement(name = "DefinitionID", required = true)
	private String definitionID;
	@XmlElement(name = "Status", required = true)
	private String status;
	@XmlElement(name = "Description", required = true)
	private String description;

	@XmlElementWrapper(name = "Guidelines", required = true)
	@XmlElement(name = "Guideline", required = true)
	private Collection<abstractGuideline> guidelines;

	@XmlTransient
	protected BlockingQueue<Runnable> threadPool;
	@XmlTransient
	private ExecutorService threadPoolExecutor;

	GuidelinesFactory(){

	}





	public Collection<abstractGuideline> getGuidelines(){
		return guidelines;
	}

	public String getVerificationType() {
		return verificationType;
	}

	public void setVerificationType(String verificationType) {
		this.verificationType = verificationType;
	}

	public String getDefinitionID() {
		return definitionID;
	}

	public void setDefinitionID(String DefinitionID) {
		this.definitionID = DefinitionID;
	}

	public String getStatus(){
		return status;
	}



	@Override
	public String toString() {
		String ret = "GuideLineFactory: \n\r";
		int index=0;
		for(abstractGuideline bp: guidelines){
			ret+=++index+") ";
			ret+=bp.toString();
			ret+="\n\r-------------------------------------\n\r";
		}
		return ret;
	}

	@SuppressWarnings("null")
	public ArrayList<String> getIDs() {
		ArrayList<String> ret = new ArrayList<String>();;//"GuideLineFactory: \n\r";
		int index=0;
		for(abstractGuideline bp: guidelines){
			if(bp.getID().isEmpty()) continue;

			else{
				ret.add(bp.getName());
				//The guideline that is not met
				ret.add(bp.getDescription());
				//Elements that do not meet the guideline
				ret.addAll(bp.getID());

			}
		}
		return ret;
	}

	public ArrayList<String> getElementsIDs() {
		ArrayList<String> ret = null;
		int index=0;
		for(abstractGuideline bp: guidelines){
			ret.add(bp.toString());
		}
		return ret;
	}

}
