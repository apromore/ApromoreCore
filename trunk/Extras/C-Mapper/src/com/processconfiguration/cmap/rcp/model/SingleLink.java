package com.processconfiguration.cmap.rcp.model;

public class SingleLink {
	private Fact fact;
	private COR cor;
	private CXOR cxor;
	private Function func;
	private String type;
	
	public String getType(){
		return type;
	}
	
	public void setType(String value){
		type = value;
	}
	
	public SingleLink(){
		fact = new Fact();
		cor = new COR();
		cxor = new CXOR();
		func = new Function();
	}
	
	public SingleLink(Fact f, COR or){
		fact = f;
		cor = or;
		type = "FO";
	}
	
	public SingleLink(Fact f, CXOR xor){
		fact = f;
		cxor = xor;
		type = "FX";
	}
	
	public SingleLink(Fact f, Function func){
		fact = f;
		this.func = func;
		type = "FF";
	}
	
	public void setFunction(Function value){
		func = value;
	}
	
	public Function getFunction(){
		return func;
	}
	
	public void setFact(Fact value){
		fact = value;
	}
	
	public Fact getFact(){
		return fact;
	}
	
	public void setCOR(COR value){
		cor = value;
	}
	
	public COR getCOR(){
		return cor;
	}
	
	public void setCXOR(CXOR value){
		cxor = value;
	}
	
	public CXOR getCXOR(){
		return cxor;
	}
}
