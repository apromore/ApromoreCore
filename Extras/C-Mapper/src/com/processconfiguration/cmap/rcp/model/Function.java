package com.processconfiguration.cmap.rcp.model;

public class Function {
	private String functionID;
	private String functionName;
	private String type;
	private String pID;
	
	public String getpID(){
		return pID;
	}
	
	public void setpID(String value){
		pID = value;
	}
	
	public void setFunctionID(String value){
		functionID = value;
	}
	
	public String getFunctionID(){
		return functionID;
	}
	
	public void setFunctionName(String value){
		functionName = value;
	}
	
	public String getFunctionName(){
		return functionName;
	}
	
	public void setType(String value){
		type = value;
	}
	
	public String getType(){
		return type;
	}
}
