package com.processconfiguration.cmap.rcp.cmap;

import java.util.ArrayList;

import com.processconfiguration.cmap.rcp.model.Fact;


public class CMapCXORItem {
	private String cXORid;
	private String pid;
	private String cXORtype;
	private String cXORgoto;
	private String cXORcondition;
	private ArrayList<Fact> factList;
	private String cXORSorT;
	
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
	
	public String getCxorSorT(){
		return cXORSorT;
	}
	
	public void setCxorSorT(String value){
		cXORSorT = value;
	}
	
	public CMapCXORItem(){
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
	
	public void setCXORid(String value){
		cXORid = value;
	}
	
	public String getCXORid(){
		return cXORid;
	}
	
	public void setPId(String value){
		pid = value;
	}
	
	public String getPId(){
		return pid;
	}
	
	public void setCXORtype(String value){
		cXORtype = value;
	}
	
	public String getCXORtype(){
		return cXORtype;
	}
	
	public void setCXORgoto(String value){
		cXORgoto = value;
	}
	
	public String getCXORgoto(){
		return cXORgoto;
	}
	
	public void setCXORcondition(String value){
		cXORcondition = value;
	}
	
	public String getCXORcondition(){
		return cXORcondition;
	}
	
}
