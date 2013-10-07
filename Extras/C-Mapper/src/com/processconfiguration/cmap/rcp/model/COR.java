package com.processconfiguration.cmap.rcp.model;

public class COR {
	private String corID;
	private String pID;
	private String corType;
	private String corGoto;
	private String corGotoName;
	private String corSorT; //Source or Target ID
	
	public boolean Equals(COR cor){
		if(cor.getpID().equals(this.pID)){
			return true;
		}
		else {
			return false;
		}
			
	}
	
	public String getCorID(){
		return corID;
	}
	
	public void setCorID(String value){
		corID = value;
	}
	
	public String getCorSorT(){
		return corSorT;
	}
	
	public void setCorSorT(String value){
		corSorT = value;
	}
	
	public String getpID(){
		return pID;
	}
	
	public void setpID(String value){
		pID = value;
	}
	
	public String getCorType(){
		return corType;
	}
	
	public void setCorType(String value){
		corType = value;
	}
	
	public String getCorGoto(){
		return corGoto;
	}
	
	public void setCorGoto(String value){
		corGoto = value;
	}
	
	public String getCorGotoName(){
		return corGotoName;
	}
	
	public void setCorGotoName(String value){
		corGotoName = value;
	}
}
