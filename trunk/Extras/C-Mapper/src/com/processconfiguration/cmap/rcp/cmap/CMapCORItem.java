package com.processconfiguration.cmap.rcp.cmap;

import java.util.ArrayList;

import com.processconfiguration.cmap.rcp.model.Fact;


public class CMapCORItem {
	private String cORid;
	private String pid;
	private String cORtype;
	private String cORgoto;
	private String cORcondition;
	private ArrayList<Fact> factList;
	private String cORSorT;
	
	public void deleteFact(String factID){
		Fact fact = null;
		for(int f=0; f<factList.size(); f++){
			fact = factList.get(f);
			if(fact.getFactID().equals(factID)){
				factList.remove(f);
				break;
			}
		}
	}
	
	public String getCorSorT(){
		return cORSorT;
	}
	
	public void setCorSorT(String value){
		cORSorT = value;
	}
	
	public CMapCORItem(){
		factList = new ArrayList<Fact>();
	}
	
	public void addFact(Fact fact){
		factList.add(fact);
	}
	
	public ArrayList<Fact> getFactList(){
		return factList;
	}
	
	public void setFactList(ArrayList<Fact> value){
		factList = value;
	}
	
	public void setCORid(String value){
		cORid = value;
	}
	
	public String getCORid(){
		return cORid;
	}
	
	public void setPId(String value){
		pid = value;
	}
	
	public String getPId(){
		return pid;
	}
	
	public void setCORtype(String value){
		cORtype = value;
	}
	
	public String getCORtype(){
		return cORtype;
	}
	
	public void setCORgoto(String value){
		cORgoto = value;
	}
	
	public String getCORgoto(){
		return cORgoto;
	}
	
	public void setCORcondition(String value){
		cORcondition = value;
	}
	
	public String getCORcondition(){
		return cORcondition;
	}
	
}
