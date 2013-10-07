package com.processconfiguration.cmap.rcp.cmap;

import java.util.ArrayList;

import com.processconfiguration.cmap.rcp.model.COR;
import com.processconfiguration.cmap.rcp.model.CXOR;
import com.processconfiguration.cmap.rcp.model.Fact;
import com.processconfiguration.cmap.rcp.model.Function;
import com.processconfiguration.cmap.rcp.model.SingleLink;


public class CMap {
	public static ArrayList<CMapCORItem> CMapCORItemList = new ArrayList<CMapCORItem>();
	public static ArrayList<CMapCXORItem> CMapCXORItemList = new ArrayList<CMapCXORItem>();
	public static ArrayList<CMapFunctionItem> CMapFunctionItemList = new ArrayList<CMapFunctionItem>();
	
	public static void RefreshList(){
		CMapCORItemList = new ArrayList<CMapCORItem>();
		CMapCXORItemList = new ArrayList<CMapCXORItem>();
		CMapFunctionItemList = new ArrayList<CMapFunctionItem>();
	}
	
	public static void deleteProcess(String PID){
		deleteORItem(PID);
		deleteXORItem(PID);
		deleteFunctionItem(PID);
	}
	
	public static void deleteORItem(String PID){
		for(int c = 0; c<CMapCORItemList.size(); c++){
			if(CMapCORItemList.get(c).getPId().equals(PID)){
				
				CMapCORItemList.remove(c);
				
				break;
			}
		}
	}
	
	public static void deleteXORItem(String PID){
		for(int c = 0; c<CMapCXORItemList.size(); c++){
			if(CMapCXORItemList.get(c).getPId().equals(PID)){
				
				CMapCXORItemList.remove(c);
				
				break;
			}
		}
	}
	
	public static void deleteFunctionItem(String PID){
		for(int f = 0; f<CMapFunctionItemList.size(); f++){
			if(CMapFunctionItemList.get(f).getPID().equals(PID)){
				CMapFunctionItemList.remove(f);
				
				break;
			}
		}
	}
	
	public static void addNewLink(SingleLink newLink){
		if(newLink.getType().equals("FO")){
			addCORItemList(newLink);
		}
		
		if(newLink.getType().equals("FX")){
			addCXORItemList(newLink);
		}
		
		if(newLink.getType().equals("FF")){
			addFunctionItemList(newLink);
		}
	}
		
	private static void addFunctionItemList(SingleLink newLink) {
		// TODO Auto-generated method stub
		Function func = newLink.getFunction();
		Fact fact = newLink.getFact();
		
		boolean isExistingFunc = false;
		int existingFuncIndex  = 0;

		//if same COR found, then add only the fact in the list, else below
		for(int f = 0; f<CMapFunctionItemList.size(); f++){
			if(CMapFunctionItemList.get(f).getPID().equals(func.getpID())){
				isExistingFunc = true;
				existingFuncIndex = f;
				break;
			}
		}
		
		if(!isExistingFunc){
			CMapFunctionItem cmapFunctionItem = new CMapFunctionItem();
			cmapFunctionItem.addFact(fact);
			cmapFunctionItem.setFunctionID(func.getFunctionID());
			cmapFunctionItem.setPID(func.getpID());
			cmapFunctionItem.setFunctionType(func.getType());
		
			cmapFunctionItem.setFunctionCondition("");
			
			CMapFunctionItemList.add(cmapFunctionItem);
		}
		else {
			CMapFunctionItemList.get(existingFuncIndex).addFact(fact);
		}
	}

	private static void addCORItemList(SingleLink newLink) {
		// TODO Auto-generated method stub
		COR cor = newLink.getCOR();
		Fact fact = newLink.getFact();
		
		boolean isExistingCor = false;
		int existingCorIndex  = 0;

		//if same COR found, then add only the fact in the list, else below
		for(int c = 0; c<CMapCORItemList.size(); c++){
			if(CMapCORItemList.get(c).getPId().equals(cor.getpID())){
				isExistingCor = true;
				existingCorIndex = c;
				break;
			}
		}
		
		if(!isExistingCor){
			CMapCORItem cmapCorItem = new CMapCORItem();
			cmapCorItem.addFact(fact);
			cmapCorItem.setCORid(cor.getCorID());
			cmapCorItem.setPId(cor.getpID());
			cmapCorItem.setCORtype(cor.getCorType());
			cmapCorItem.setCORgoto(cor.getCorGoto());
			cmapCorItem.setCORcondition("");
			
			CMapCORItemList.add(cmapCorItem);
		}
		else {
			CMapCORItemList.get(existingCorIndex).addFact(fact);
		}
	}

	private static void addCXORItemList(SingleLink newLink) {
		// TODO Auto-generated method stub
		CXOR cxor = newLink.getCXOR();
		Fact fact = newLink.getFact();
		
		boolean isExistingCxor = false;
		int existingCxorIndex  = 0;

		//if same COR found, then add only the fact in the list, else below
		for(int c = 0; c<CMapCXORItemList.size(); c++){
			if(CMapCXORItemList.get(c).getPId().equals(cxor.getpID())){
				isExistingCxor = true;
				existingCxorIndex = c;
				break;
			}
		}
		
		if(!isExistingCxor){
			CMapCXORItem cmapCxorItem = new CMapCXORItem();
			cmapCxorItem.addFact(fact);
			cmapCxorItem.setCXORid(cxor.getCxorID());
			cmapCxorItem.setPId(cxor.getpID());
			cmapCxorItem.setCXORtype(cxor.getCxorType());
			cmapCxorItem.setCXORgoto(cxor.getCxorGoto());
			cmapCxorItem.setCXORcondition("");
			
			CMapCXORItemList.add(cmapCxorItem);
		}
		else {
			CMapCXORItemList.get(existingCxorIndex).addFact(fact);
		}
	}
	
	public static void deleteFact(String pID, String factID){
		for(int c = 0; c<CMapCORItemList.size(); c++){
			if(CMapCORItemList.get(c).getPId().equals(pID)){
				CMapCORItemList.get(c).deleteFact(factID);
				
				break;
			}
		}
		
		for(int c = 0; c<CMapCXORItemList.size(); c++){
			if(CMapCXORItemList.get(c).getPId().equals(pID)){
				CMapCXORItemList.get(c).deleteFact(factID);
				
				break;
			}
		}
		
		for(int f = 0; f<CMapFunctionItemList.size(); f++){
			if(CMapFunctionItemList.get(f).getPID().equals(pID)){
				CMapFunctionItemList.get(f).deleteFact(factID);
				
				break;
			}
		}
	}
	
	public static void addNewCondition(String pID, String Condition){
		for(int c = 0; c<CMapCORItemList.size(); c++){
			if(CMapCORItemList.get(c).getPId().equals(pID)){
				CMapCORItemList.get(c).setCORcondition(Condition);
				
				break;
			}
		}
		
		for(int c = 0; c<CMapCXORItemList.size(); c++){
			if(CMapCXORItemList.get(c).getPId().equals(pID)){
				CMapCXORItemList.get(c).setCXORcondition(Condition);
				
				break;
			}
		}
		
		for(int f = 0; f<CMapFunctionItemList.size(); f++){
			if(CMapFunctionItemList.get(f).getPID().equals(pID)){
				CMapFunctionItemList.get(f).setFunctionCondition(Condition);
				
				break;
			}
		}
	}
}
