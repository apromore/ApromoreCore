package com.processconfiguration.cmap.rcp.model;

import java.util.ArrayList;

import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.processconfiguration.cmap.rcp.Application;
import com.processconfiguration.cmap.rcp.Util;


public class Connection {
	public static boolean FileUpload = false;

	public static ArrayList<SingleLink> LinkList = new ArrayList<SingleLink>();
	
	public static ArrayList<COR> CORList = new ArrayList<COR>();
	public static ArrayList<CXOR> CXORList = new ArrayList<CXOR>();
	public static ArrayList<Function> FunctionList = new ArrayList<Function>();
	public static ArrayList<Fact> FactList = new ArrayList<Fact>();
	
	public static String[] SelectedFacts;
	public static String SelectedCondition = "";
	public static String SelectedPID = "";
	
	public static String CMapTreeSelectedPID = "";
	
	public static void RefreshLinkList(){
		LinkList = new ArrayList<SingleLink>();
	}
	
	public static boolean isConditionValid(ArrayList<Fact> factList, String condition){
		ArrayList<String> factIDListinCondition = Util.getUniqueFactListFromCondition(condition);
		
		boolean isValid = true;
		for(int f = 0; f<factIDListinCondition.size(); f++){
			String curFactID = factIDListinCondition.get(f);
			boolean found = false;
			for(int i = 0; i<factList.size(); i++){
				Fact fact = factList.get(i);
				if(fact.getFactID().equals(curFactID)){
					found = true;
				}
			}
			if(!found){
				isValid = false;
				break;
			}
		}
		return isValid;
	}
	
	public static boolean isConditionValid(String[] factIDList, String condition){
		ArrayList<String> factIDListinCondition = Util.getUniqueFactListFromCondition(condition);
		
		boolean isValid = true;
		for(int f = 0; f<factIDListinCondition.size(); f++){
			String curFactID = factIDListinCondition.get(f);
			boolean found = false;
			for(int i = 0; i<factIDList.length; i++){
				
				if(factIDList[i].equals(curFactID)){
					found = true;
				}
			}
			if(!found){
				isValid = false;
				break;
			}
		}
		return isValid;
	}
	
	public static String getFactDescription(String factID){
		String retFactID = "";
		Fact fact = null;
		for(int f = 0; f<FactList.size(); f++){
			fact = FactList.get(f);
			if(fact.getFactID().equals(factID)){
				retFactID = fact.getFactDescription();
				break;
			}
		}
		
		return retFactID;
	}
	
	public static String getFunctionpName(String funcID){
		String retPID = "";
		
		Function func = null;
		for(int f=0; f<FunctionList.size(); f++){
			func = FunctionList.get(f);
			
			if(funcID.equals(func.getFunctionID())){
				retPID = func.getpID();
				break;
			}
		}
		
		return retPID;
	}
	
	public static String getFunctionpID(String funcID, String funcType){
		String retPID = "";
		
		Function func = null;
		for(int f=0; f<FunctionList.size(); f++){
			func = FunctionList.get(f);
			
			if(funcID.equals(func.getFunctionID()) && funcType.equals(func.getType())){
				retPID = func.getpID();
				break;
			}
		}
		
		return retPID;
	}
	
	public static String getCORpID(String cORid, String cORType, String cORGoto){
		String retPID = "";
		
		COR cor = null;
		for(int i = 0; i<CORList.size(); i++){
			cor = CORList.get(i);
			
			if(cORType.equals("seq") && cor.getCorType().equals("seq")){
				if(cor.getCorID().equals(cORid) && cor.getCorGoto().equals(cORGoto)){
					retPID = cor.getpID();
					break;
				}
			}
			else {
				if(cor.getCorID().equals(cORid) && cor.getCorType().equals(cORType)){
					retPID = cor.getpID();
					break;
				}
			}
		}
		
		return retPID;
	}
	
	public static String getCXORpID(String cXORid, String cXORType, String cXORGoto){
		String retPID = "";
		
		CXOR cxor = null;
		for(int i = 0; i<CXORList.size(); i++){
			cxor = CXORList.get(i);
			
			if(cXORType.equals("seq") && cxor.getCxorType().equals("seq")){
				if(cxor.getCxorID().equals(cXORid) && cxor.getCxorGoto().equals(cXORGoto)){
					retPID = cxor.getpID();
					break;
				}
			}
			else {
				if(cxor.getCxorID().equals(cXORid) && cxor.getCxorType().equals(cXORType)){
					retPID = cxor.getpID();
					break;
				}
			}
		}
		
		return retPID;
	}
	
	public static void RefreshCORandFunctionList(){
		CORList = new ArrayList<COR>();
		CXORList = new ArrayList<CXOR>();
		FunctionList = new ArrayList<Function>();
	}
	
	public static void RefreshFactList(){
		FactList = new ArrayList<Fact>();
	}
	
	public static ArrayList<SingleLink> getLinkList(){
		return LinkList;
	}
	
	public static void MakeLink(String QmlPart, String ModelPart){
		System.out.println(QmlPart + " AND " + ModelPart);
	}
	
	/*
	public static void MakeLink(Fact fact, COR cor){
		//SingleLink singleLink = new SingleLink(fact, cor);
		SingleLink singleLink = new SingleLink();
		singleLink.setCOR(cor);
		singleLink.setFact(fact);
		
		LinkList.add(singleLink);
	}*/
	
	
	//--- not included the CXOR
	public static void displayAllLinks(){
		showInCMapView();
		
		COR cor=null;
		
		Fact fact = null;
		SingleLink s = null;
		for(int i = 0; i<LinkList.size(); i++){
			s = LinkList.get(i);
			if(s.getType().equals("FO")){
				cor = s.getCOR();
				fact = s.getFact();
				
				String factOutput = "Fact " + fact.getFactID() + " - " + fact.getFactDescription();
				
				String corOutput = "COR [" + cor.getpID() + "] ";
				if(cor.getCorType()!="Seq"){
					corOutput += cor.getCorType();
				}
				else {
					corOutput += cor.getCorType() + " " + cor.getCorGoto() + ": " + cor.getCorGotoName();
				}
				System.out.println("Link " + i + ": " + factOutput + " to " + corOutput);
			}
		}
		
	}

	public static void RefreshView(String ViewID){
		IViewReference viewReferences[] = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getViewReferences();
		
		final IViewPart cmapView = getViewRef(viewReferences, ViewID);
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().hideView(cmapView);
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(ViewID);
			
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void HideView(String ViewID){
		IViewReference viewReferences[] = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getViewReferences();
		
		final IViewPart cmapView = getViewRef(viewReferences, ViewID);
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().hideView(cmapView);
	}
	
	private static void showInCMapView() {
		IViewReference viewReferences[] = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getViewReferences();
		
		//final IViewPart cmapView = getViewRef(viewReferences, "asadul.xmltree2.views.cmap");
		final IViewPart cmapView = getViewRef(viewReferences, Application.Views_CMapView_ID);
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().hideView(cmapView);
		try {
			//PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("asadul.xmltree2.views.cmap");
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(Application.Views_CMapView_ID);
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	private static IViewPart getViewRef(IViewReference[] viewRefs, String id){
		for(int i =0; i<viewRefs.length; i++){
			if(id.equals(viewRefs[i].getId())){
				return viewRefs[i].getView(false);
			}
		}
		
		return null;
	}
}
