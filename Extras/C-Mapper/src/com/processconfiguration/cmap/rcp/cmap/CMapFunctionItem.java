package com.processconfiguration.cmap.rcp.cmap;

import java.util.ArrayList;

import com.processconfiguration.cmap.rcp.model.Fact;


public class CMapFunctionItem {
	private String funcID;
	private String funcName;
	
	private String pid;
	private String funcType;
	
	private String funcCondition;
	private ArrayList<Fact> factList;
	
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
	
	public void setFactList(ArrayList<Fact> value){
		factList = value;
	}
	
	public CMapFunctionItem(){
		factList = new ArrayList<Fact>();
	}
	
	public void addFact(Fact fact){
		factList.add(fact);
	}
	
	public ArrayList<Fact> getFactList(){
		return factList;
	}
	public void setFunctionType(String value){
		funcType = value;
	}
	
	public String getFunctionType(){
		return funcType;
	}
	
	public void setFunctionCondition(String value){
		funcCondition = value;
	}
	
	public String getFunctionCondition(){
		return funcCondition;
	}
	
	public void setPID(String value){
		pid = value;
	}
	
	public String getPID(){
		return pid;
	}
	
	public void setFunctionID(String value){
		funcID = value;
	}
	
	public String getFunctionID(){
		return funcID;
	}
	
	public void setFunctionName(String value){
		funcName = value;
	}
	
	public String getFunctionName(){
		return funcName;
	}
}

