package com.processconfiguration.cmap.rcp.model;

public class CXOR {
	private String cxorID;
	private String pID;
	private String cxorType;
	private String cxorGoto;
	private String cxorGotoName;
	private String cxorSorT; //Source or Target ID
	
	public boolean Equals(CXOR cxor){
		if(cxor.getpID().equals(this.pID)){
			return true;
		}
		else {
			return false;
		}
	}
	
	public String getCxorID(){
		return cxorID;
	}
	
	public void setCxorID(String value){
		cxorID = value;
	}
	
	public String getCxorSorT(){
		return cxorSorT;
	}
	
	public void setCxorSorT(String value){
		cxorSorT = value;
	}
	
	public String getpID(){
		return pID;
	}
	
	public void setpID(String value){
		pID = value;
	}
	
	public String getCxorType(){
		return cxorType;
	}
	
	public void setCxorType(String value){
		cxorType = value;
	}
	
	public String getCxorGoto(){
		return cxorGoto;
	}
	
	public void setCxorGoto(String value){
		cxorGoto = value;
	}
	
	public String getCxorGotoName(){
		return cxorGotoName;
	}
	
	public void setCxorGotoName(String value){
		cxorGotoName = value;
	}
}
