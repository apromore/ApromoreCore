package com.processconfiguration.cmap.rcp.views;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.processconfiguration.cmap.rcp.Application;
import com.processconfiguration.cmap.rcp.Util;
import com.processconfiguration.cmap.rcp.cmap.CMap;
import com.processconfiguration.cmap.rcp.cmap.CMapCORItem;
import com.processconfiguration.cmap.rcp.cmap.CMapCXORItem;
import com.processconfiguration.cmap.rcp.cmap.CMapFunctionItem;
import com.processconfiguration.cmap.rcp.model.Connection;
import com.processconfiguration.cmap.rcp.model.Fact;


public class CMapView extends ViewPart {
	private Tree tree;
	
	public CMapView() {
		
	}

	@Override
	public void createPartControl(Composite parent) {
		prepareTree(parent);
		
		if(Connection.FileUpload){
			if(Application.cmapFileName != ""){
				if(Util.isCorrectCmapFile(Application.cmapFileName)){

					createCMapFromFile(parent);
					displayInTree(parent);
					Connection.HideView(Application.Views_DispConditionView_ID);
				}
				else
				{
					//Message -- Not a correct CMap file
					MessageDialog.openWarning(parent.getShell(), "Warning", "File is not a correct CMap file.");
				}
			}
			Connection.FileUpload = false;
		}
		else {
			displayInTree(parent);
		}
		
		createActions(parent);
	}

	@Override
	public void setFocus() {

	}
	
	public void prepareTree(final Composite parent){
		tree = new Tree(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
	    tree.setHeaderVisible(true);
	    tree.setLinesVisible(true);
	    
	    TreeColumn col = new TreeColumn(tree, SWT.LEFT);
	    col.setText("Variation Point");
	    col.setWidth(100);
	    
	    col = new TreeColumn(tree, SWT.LEFT);
	    col.setText("Variant");
	    col.setWidth(100);
	    
	    col = new TreeColumn(tree, SWT.LEFT);
	    col.setText("Domain Facts");
	    col.setWidth(200);
	    
	    col = new TreeColumn(tree, SWT.LEFT);
	    col.setText("Condition");
	    col.setWidth(350);
	    
	}
	
	public void displayInTree(final Composite parent){
		tree.removeAll();

	    ArrayList<CMapCORItem> CORItemList = CMap.CMapCORItemList;
	    ArrayList<CMapCXORItem> CXORItemList = CMap.CMapCXORItemList;
	    ArrayList<CMapFunctionItem> FunctionItemList = CMap.CMapFunctionItemList;
	  
	    displayORInTree(CORItemList, parent);
	    displayXORInTree(CXORItemList, parent);
	    displayFunctionInTree(FunctionItemList, parent);
	    
	    treeLastSelectedNodeExpand(parent);
 	}
	
	private void displayORInTree(ArrayList<CMapCORItem> CORItemList, Composite parent){
	    CMapCORItem cmapCorItem = null;
	    
		if(CORItemList!=null){
	    	for(int c=0; c<CORItemList.size(); c++){
	    		cmapCorItem = CORItemList.get(c);
				String corID = cmapCorItem.getCORid();
				String pID = cmapCorItem.getPId();
				  
				TreeItem corItem = new TreeItem(tree, SWT.NONE);
				
				//Display First Column --------------
				corItem.setText(0, "OR[" + corID + "]");
				
				//Display Second Column ---------------
				String processFact = "";
				String corType = cmapCorItem.getCORtype();
				processFact = pID + "-" + corType;
				if(corType.equals("seq")){
					processFact += " " + cmapCorItem.getCORgoto();
				}
				corItem.setText(1, processFact);

				//Display Third Column - FactList
				ArrayList<Fact> facts = cmapCorItem.getFactList();
				String factList = "";
				for(int f = 0; f < facts.size(); f++){
					Fact fact = facts.get(f);
					factList += fact.getFactID() + ",";
				}
				factList = factList.trim();
				if(factList.length()>1){
					factList = factList.substring(0, factList.length()-1);
				}
				corItem.setText(2, factList);
				
				//Display 4th Column - Condition
				String condition = cmapCorItem.getCORcondition();
				if(Connection.isConditionValid(facts, condition)){
					corItem.setText(3, condition);
				}
				else {
					//Display the text in RED
					Color color = parent.getDisplay().getSystemColor(SWT.COLOR_RED);
					corItem.setForeground(3, color);
					corItem.setText(3, condition);
				}
			  
				//Add necessary Data to the treeitem -----------
				corItem.setData("CMapType", "OR");
				corItem.setData("PID", pID);
				corItem.setData("CMapCondition", condition);
				corItem.setData("FactList", factList);
				corItem.setData(cmapCorItem);
				
				//Display the subtree item for each fact
				for(int f = 0; f < facts.size(); f++){
					Fact fact = facts.get(f);
					TreeItem subTreeItem = new TreeItem (corItem, SWT.NONE);
					
					subTreeItem.setText(2, fact.getFactID() + " - " + fact.getFactDescription());
					subTreeItem.setData("CMapType", "ORSub");
					subTreeItem.setData("PID", pID);
					subTreeItem.setData("CMapCondition", condition);
					subTreeItem.setData("FactList", factList);
					subTreeItem.setData(fact);
				}
	    	}
	    }
	}
	
	private void displayXORInTree(ArrayList<CMapCXORItem> CXORItemList, Composite parent){
		CMapCXORItem cmapCxorItem = null;
	    if(CXORItemList!=null){
	    	for(int c=0; c<CXORItemList.size(); c++){
	    		cmapCxorItem = CXORItemList.get(c);
				String cxorID = cmapCxorItem.getCXORid();
				String pID = cmapCxorItem.getPId();
				  
				TreeItem cxorItem = new TreeItem(tree, SWT.NONE);
				
				//Display First Column --------------
				cxorItem.setText(0, "XOR[" + cxorID + "]");
				
				//Display Second Column ---------------
				String processFact = "";
				String cxorType = cmapCxorItem.getCXORtype();
				processFact = pID + "-" + cxorType;
				if(cxorType.equals("seq")){
					processFact += " " + cmapCxorItem.getCXORgoto();
				}
				cxorItem.setText(1, processFact);

				//Display Third Column - FactList
				ArrayList<Fact> facts = cmapCxorItem.getFactList();
				String factList = "";
				for(int f = 0; f < facts.size(); f++){
					Fact fact = facts.get(f);
					factList += fact.getFactID() + ",";
				}
				factList = factList.trim();
				if(factList.length()>1){
					factList = factList.substring(0, factList.length()-1);
				}
				cxorItem.setText(2, factList);
				
				//Display 4th Column - Condition
				String condition = cmapCxorItem.getCXORcondition();
				if(Connection.isConditionValid(facts, condition)){
					cxorItem.setText(3, condition);
				}
				else {
					//Display the text in RED
					Color color = parent.getDisplay().getSystemColor(SWT.COLOR_RED);
					cxorItem.setForeground(3, color);
					cxorItem.setText(3, condition);
				}
			  
				//Add necessary Data to the treeitem -----------
				cxorItem.setData("CMapType", "XOR");
				cxorItem.setData("PID", pID);
				cxorItem.setData("CMapCondition", condition);
				cxorItem.setData("FactList", factList);
				cxorItem.setData(cmapCxorItem);
				
				//Display the subtree item for each fact
				for(int f = 0; f < facts.size(); f++){
					Fact fact = facts.get(f);
					TreeItem subTreeItem = new TreeItem (cxorItem, SWT.NONE);
					
					subTreeItem.setText(2, fact.getFactID() + " - " + fact.getFactDescription());
					subTreeItem.setData("CMapType", "XORSub");
					subTreeItem.setData("PID", pID);
					subTreeItem.setData("CMapCondition", condition);
					subTreeItem.setData("FactList", factList);
					subTreeItem.setData(fact);
				}
	    	}
	    }
	}
	
	private void displayFunctionInTree(ArrayList<CMapFunctionItem> FunctionItemList, Composite parent){
	    CMapFunctionItem cmapFunctionItem = null;
	    
		if(FunctionItemList!=null){
	    	for(int c=0; c<FunctionItemList.size(); c++){
	    		cmapFunctionItem = FunctionItemList.get(c);
				String functionID = cmapFunctionItem.getFunctionID();
				String pID = cmapFunctionItem.getPID();
				  
				TreeItem functionItem = new TreeItem(tree, SWT.NONE);
				
				//Display First Column --------------
				functionItem.setText(0, "Function[" + functionID + "]");
				
				//Display Second Column ---------------
				String processFact = "";
				String functionType = cmapFunctionItem.getFunctionType();
				processFact = pID + "-" + functionType;
				functionItem.setText(1, processFact);

				//Display Third Column - FactList
				ArrayList<Fact> facts = cmapFunctionItem.getFactList();
				String factList = "";
				for(int f = 0; f < facts.size(); f++){
					Fact fact = facts.get(f);
					factList += fact.getFactID() + ",";
				}
				factList = factList.trim();
				if(factList.length()>1){
					factList = factList.substring(0, factList.length()-1);
				}
				functionItem.setText(2, factList);
				
				//Display 4th Column - Condition
				String condition = cmapFunctionItem.getFunctionCondition();
				if(Connection.isConditionValid(facts, condition)){
					functionItem.setText(3, condition);
				}
				else {
					//Display the text in RED
					Color color = parent.getDisplay().getSystemColor(SWT.COLOR_RED);
					functionItem.setForeground(3, color);
					functionItem.setText(3, condition);
				}
			  
				//Add necessary Data to the treeitem -----------
				functionItem.setData("CMapType", "Function");
				functionItem.setData("PID", pID);
				functionItem.setData("CMapCondition", condition);
				functionItem.setData("FactList", factList);
				functionItem.setData(cmapFunctionItem);
				
				//Display the subtree item for each fact
				for(int f = 0; f < facts.size(); f++){
					Fact fact = facts.get(f);
					TreeItem subTreeItem = new TreeItem (functionItem, SWT.NONE);
					
					subTreeItem.setText(2, fact.getFactID() + " - " + fact.getFactDescription());
					subTreeItem.setData("CMapType", "FunctionSub");
					subTreeItem.setData("PID", pID);
					subTreeItem.setData("CMapCondition", condition);
					subTreeItem.setData("FactList", factList);
					subTreeItem.setData(fact);
				}
	    	}
	    }
	}
	
	private void treeLastSelectedNodeExpand(Composite parent) {
		if(!Connection.CMapTreeSelectedPID.equals("")){
			int totalTreeItem = tree.getItemCount();
			
			TreeItem treeItem;
			for(int i = 0; i<totalTreeItem; i++){
				treeItem = tree.getItem(i);
				//String CMapType = (String) qTreeItem.getData("CMapType");
				String CMapPID = (String) treeItem.getData("PID");
				
				if(CMapPID.equals(Connection.CMapTreeSelectedPID)){
					treeItem.setExpanded(true);
					//tree.showItem(qTreeItem);
					tree.showItem(treeItem.getItems()[treeItem.getItemCount()-1]);
					Connection.CMapTreeSelectedPID = "";
					break;
				}
	
			}
		}
		
	}

	private void createActions(final Composite parent) {
		Action actionOpen = new Action("Open...") {
			public void run(){
				openCMap(parent);
			}
		};
		
		Action actionClose = new Action("Close"){
			@Override
			public void run(){
				closeCMapFile();
			}
		};
		
		Action actionClear = new Action("Clear All"){
			@Override
			public void run(){
				clearCMapTree();
			}
		};
		
		Action actionExpandAll = new Action("Expand All"){
			@Override
			public void run(){
				expandTree();
			}
		};
		
		Action actionCollapseAll = new Action("Colapse All"){
			@Override
			public void run(){
				colapseTree();
			}
		};
		
		Action actionSave = new Action("Save") {
			public void run(){
				if(Application.cmapFileName==""){
					SaveCMap(parent, false);
				} else {
					SaveCMap(parent, true);
				}
			}
		};
		

		Action actionSaveAs = new Action("Save As") {
			public void run(){
				SaveCMap(parent, false);
			}
		};

		
		Action actionDelete = new Action("Delete"){
			public void run(){
				deleteFact(parent);
			}
		};
		
		Action actionUpdateCondition = new Action("Update condition"){
			public void run(){
				updateCondition(parent);
			}
		};

		IActionBars actionBars = getViewSite().getActionBars();
		IToolBarManager toolBar = actionBars.getToolBarManager();
	
		actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(), actionDelete);
		actionBars.updateActionBars();
		
		actionOpen.setToolTipText("Open C-Mapping File");
		actionOpen.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_ADD));
		toolBar.add(actionOpen);
		
		actionClose.setToolTipText("Close C-Mapping File");
		ImageDescriptor imgDesc = AbstractUIPlugin.imageDescriptorFromPlugin("com.processconfiguration.cmap.rcp", "/icons/Actions-remove-icon.png");
		actionClose.setImageDescriptor(imgDesc);
		toolBar.add(actionClose);

		actionClear.setToolTipText("Clear All");
		actionClear.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_CLEAR));
		toolBar.add(actionClear);
		
		imgDesc = AbstractUIPlugin.imageDescriptorFromPlugin("com.processconfiguration.cmap.rcp", "/icons/expandall.gif");
		actionExpandAll.setImageDescriptor(imgDesc);
		actionExpandAll.setToolTipText("Expand All");
		toolBar.add(actionExpandAll);
			
		actionCollapseAll.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_COLLAPSEALL));
		actionCollapseAll.setToolTipText("Collapse All");
		toolBar.add(actionCollapseAll);

		actionSave.setToolTipText("Save to current C-Mapping File");
		actionSave.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_SAVE_EDIT));
		toolBar.add(actionSave);

		actionSaveAs.setToolTipText("Save As...");
		actionSaveAs.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_SAVEAS_EDIT));
		toolBar.add(actionSaveAs);
		
		actionDelete.setToolTipText("Delete row");
		actionDelete.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_DELETE));
		toolBar.add(actionDelete);
		
		actionUpdateCondition.setToolTipText("Update seleted condition");
		actionUpdateCondition.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_NEW_WIZARD));
		toolBar.add(actionUpdateCondition);
	}
	
	private void closeCMapFile() {
		Application.cmapFileName = "";
		CMap.RefreshList();
		tree.removeAll();
		
	}
	
	private void clearCMapTree() {
		CMap.RefreshList();
		tree.removeAll();
	}
	
	private void expandTree(){
		int totalQuesItem = tree.getItemCount();
		
		TreeItem cTreeItem;
		for(int i = 0; i<totalQuesItem; i++){
			cTreeItem = tree.getItem(i);
			cTreeItem.setExpanded(true);
		}
	}
	
	private void colapseTree(){
		int totalQuesItem = tree.getItemCount();
		
		TreeItem cTreeItem;
		for(int i = 0; i<totalQuesItem; i++){
			cTreeItem = tree.getItem(i);
			cTreeItem.setExpanded(false);
		}
	}
	
	private void openCMap(Composite parent){
		FileDialog fd  = new FileDialog(parent.getShell(), SWT.OPEN);
		fd.setFilterExtensions(new String[]{"*.cmap"});
		
		String cmapFileName = fd.open();
		if(cmapFileName != null){
			if(Util.isCorrectCmapFile(cmapFileName)){
				Application.cmapFileName = cmapFileName;
				
				createCMapFromFile(parent);
				displayInTree(parent);
				Connection.HideView(Application.Views_DispConditionView_ID);
			}
		}
	}
	
	private void createCMapFromFile(Composite parent) {
		String cmapFileName = Application.cmapFileName;
		
		String QmlFile = getQMLFromCMapFile(cmapFileName);
		String EpmlFile = getEpmlFromCMapFile(cmapFileName);
		String QmlFilePath = "";
		String EpmlFilePath = "";
	
		File cmapFile = new File(cmapFileName);
		
		if(!QmlFile.isEmpty()){
			QmlFilePath = cmapFile.getParent() + Path.SEPARATOR + QmlFile;
			File file = new File(QmlFilePath);
			
	        if (!file.exists()) {
	        	FileDialog fd  = new FileDialog(parent.getShell(), SWT.OPEN);
	        	fd.setText("Please specify corresponding Qml file");
	        	fd.setFileName(QmlFile);
	    		fd.setFilterExtensions(new String[]{"*.qml"});
	    		fd.setFilterNames(new String[]{"QML File"});
	    		
	    		boolean done = false;
	
	    		while (!done) {
	    			QmlFilePath = fd.open();
	    			if (QmlFilePath == null) {
	    				done = true;
	    			} else {
	    					done = true;
	    		        }
	   			}
	    		
	    		file = new File(QmlFilePath);
	    		QmlFile = file.getName();
	    	}
	        
	        Application.qmlFilePath = QmlFilePath;
			Application.qmlFileName = QmlFile;
			Connection.RefreshView(Application.Views_QuestionView_ID);
		}
		else{
			Application.qmlFileName ="";
			Application.qmlFilePath = "";
		}

		System.out.println("QML File: " + Application.qmlFileName + ", Path: " + Application.qmlFilePath);
		
		
		if(!EpmlFile.isEmpty()){
			EpmlFilePath = cmapFile.getParent() + Path.SEPARATOR + EpmlFile;
			
			File file = new File(EpmlFilePath);
	        if (!file.exists()) {
	        	FileDialog fd  = new FileDialog(parent.getShell(), SWT.OPEN);
	        	fd.setText("Please specify corresponding Epml file");
	        	fd.setFileName(EpmlFile);
	    		fd.setFilterExtensions(new String[]{"*.epml"});
	    		fd.setFilterNames(new String[]{"EPML File"});
	    		
	    		boolean done = false;
	
	    		while (!done) {
	    			EpmlFilePath = fd.open();
	    			if (EpmlFile == null) {
	    				done = true;
	    			} else {
	    					done = true;
	    		        }
	   			}
	    		
	    		file = new File(EpmlFilePath);
	    		EpmlFile = file.getName();
	    	}
	
			Application.modelFileName = EpmlFile;
			Application.modelFilePath = EpmlFilePath;
			
			Connection.RefreshView(Application.Views_ModelView_ID);
		}
		else {
			Application.modelFileName = "";
			Application.modelFilePath = "";
		}
		
		System.out.println("EPML File: " + Application.modelFileName + ", Path: " + Application.modelFilePath);
		
		CMap.RefreshList();
		uploadORToCMap(cmapFileName);
		uploadXORToCMap(cmapFileName);
		uploadFunctionToCMap(cmapFileName);
	}

	private void uploadORToCMap(String cmapFileName) {
		String CORid = "";
		String CORType = "";
		String CORGoto = "";
		String CORCondition = "";

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
		
			Document doc = null;
			doc = builder.parse(cmapFileName);
				
			XPathFactory xFactory = XPathFactory.newInstance();
			XPath xpath = xFactory.newXPath();
		
			XPathExpression expr = null;
			expr = xpath.compile("//COR/value");
		
			Object result = expr.evaluate(doc, XPathConstants.NODESET);
			NodeList nodesValues = (NodeList) result;
			Element CORElement = null;
			Element valueElement = null;
			for(int c=0; c<nodesValues.getLength(); c++){
				valueElement = (Element)nodesValues.item(c);
				CORElement = (Element)valueElement.getParentNode();
				CORid = CORElement.getAttribute("id");
				
				CORType = valueElement.getAttribute("type");
				if(CORType.equals("seq")){
					CORGoto = valueElement.getAttribute("goto");
				}
				else {
					CORGoto = "";
				}
				
				CORCondition = valueElement.getAttribute("condition");
				
				CMapCORItem cmapCORItem = getCMapCORItem(CORid, CORType, CORGoto, CORCondition);
				CMap.CMapCORItemList.add(cmapCORItem);
			}
		
		} catch (ParserConfigurationException e) {

			e.printStackTrace();
		} catch (SAXException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			
			e.printStackTrace();
		}
		
	}
	
	private void uploadXORToCMap(String cmapFileName) {
		String CXORid = "";
		String CXORType = "";
		String CXORGoto = "";
		String CXORCondition = "";

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
		
			Document doc = null;
			doc = builder.parse(cmapFileName);
				
			XPathFactory xFactory = XPathFactory.newInstance();
			XPath xpath = xFactory.newXPath();
		
			XPathExpression expr = null;
			expr = xpath.compile("//CXOR/value");
		
			Object result = expr.evaluate(doc, XPathConstants.NODESET);
			NodeList nodesValues = (NodeList) result;
			Element CXORElement = null;
			Element valueElement = null;
			for(int x=0; x<nodesValues.getLength(); x++){
				valueElement = (Element)nodesValues.item(x);
				CXORElement = (Element)valueElement.getParentNode();
				CXORid = CXORElement.getAttribute("id");
				
				CXORType = valueElement.getAttribute("type");
				if(CXORType.equals("seq")){
					CXORGoto = valueElement.getAttribute("goto");
				}
				else {
					CXORGoto = "";
				}
				
				CXORCondition = valueElement.getAttribute("condition");
				
				CMapCXORItem cmapCXORItem = getCMapCXORItem(CXORid, CXORType, CXORGoto, CXORCondition);
				CMap.CMapCXORItemList.add(cmapCXORItem);
			}
		
		} catch (ParserConfigurationException e) {

			e.printStackTrace();
		} catch (SAXException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			
			e.printStackTrace();
		}
		
	}
	
	private void uploadFunctionToCMap(String cmapFileName) {
		String Funcid = "";
		String FuncType = "";
		String FuncCondition = "";
			
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
		
			Document doc = null;
			doc = builder.parse(cmapFileName);
				
			XPathFactory xFactory = XPathFactory.newInstance();
			XPath xpath = xFactory.newXPath();
		
			XPathExpression expr = null;
			expr = xpath.compile("//CFunction/value");
		
			Object result = expr.evaluate(doc, XPathConstants.NODESET);
			NodeList nodesValues = (NodeList) result;
			Element FuncElement = null;
			Element valueElement = null;
			for(int c=0; c<nodesValues.getLength(); c++){
				valueElement = (Element)nodesValues.item(c);
				FuncElement = (Element)valueElement.getParentNode();
				Funcid = FuncElement.getAttribute("id");
				
				FuncType = valueElement.getAttribute("type");
				FuncCondition = valueElement.getAttribute("condition");
				
				CMapFunctionItem cmapFunctionItem = getCMapFunctionItem(Funcid, FuncType, FuncCondition);
				CMap.CMapFunctionItemList.add(cmapFunctionItem);
			}
		
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		
	}
	
	private CMapCORItem getCMapCORItem(String cORid, String cORType, String cORGoto, String cORCondition) {
		CMapCORItem cmapCORItem = new CMapCORItem();
		
		ArrayList<Fact> factList = null;
		if(!cORCondition.equals("")){
			factList = getFactListFromCondition(cORCondition);
			cmapCORItem.setFactList(factList);
		}
		
		String pID = getORPID(cORid, cORType, cORGoto);
		
		cmapCORItem.setPId(pID);
		cmapCORItem.setCORtype(cORType);
		cmapCORItem.setCORid(cORid);
		
		if(!cORGoto.equals("")){
			cmapCORItem.setCORgoto(cORGoto);
		}
		
		cmapCORItem.setCORcondition(cORCondition);
		
		return cmapCORItem;
	}
	
	private CMapCXORItem getCMapCXORItem(String cXORid, String cXORType, String cXORGoto, String cXORCondition) {
		CMapCXORItem cmapCXORItem = new CMapCXORItem();
		
		ArrayList<Fact> factList = null;
		if(!cXORCondition.equals("")){
			factList = getFactListFromCondition(cXORCondition);
			cmapCXORItem.setFactList(factList);
		}
		
		String pID = getXORPID(cXORid, cXORType, cXORGoto);
		
		cmapCXORItem.setPId(pID);
		cmapCXORItem.setCXORtype(cXORType);
		cmapCXORItem.setCXORid(cXORid);
		
		if(!cXORGoto.equals("")){
			cmapCXORItem.setCXORgoto(cXORGoto);
		}
		
		cmapCXORItem.setCXORcondition(cXORCondition);
		
		return cmapCXORItem;
	}
	
	private CMapFunctionItem getCMapFunctionItem(String funcid, String funcType, String funcCondition) {
		CMapFunctionItem cmapFuncItem = new CMapFunctionItem();
		
		ArrayList<Fact> factList = null;
		if(!funcCondition.equals("")){
			factList = getFactListFromCondition(funcCondition);
			cmapFuncItem.setFactList(factList);
		}
		
		String pID = getFunctionPID(funcid, funcType);
		String funcName = Connection.getFunctionpName(funcid);
		
		cmapFuncItem.setFunctionName(funcName);
		cmapFuncItem.setPID(pID);
		cmapFuncItem.setFunctionType(funcType);
		cmapFuncItem.setFunctionID(funcid);

		cmapFuncItem.setFunctionCondition(funcCondition);
		
		return cmapFuncItem;
	}
	
	private String getORPID(String cORid, String cORType, String cORGoto) {
		String pid = Connection.getCORpID(cORid, cORType, cORGoto);
		
		return pid;
	}

	private String getXORPID(String cXORid, String cXORType, String cXORGoto) {
		String pid = Connection.getCXORpID(cXORid, cXORType, cXORGoto);
		
		return pid;
	}
	
	private String getFunctionPID(String funcid, String funcType) {
		String pid = Connection.getFunctionpID(funcid, funcType);
		
		return pid;
	}
	
	private ArrayList<Fact> getFactListFromCondition(String condition) {
		ArrayList<Fact> retValue = new ArrayList<Fact>();
		
		ArrayList<String> factIDList = Util.getUniqueSortedAscFactListFromCondition(condition);
		Fact fact = null;
		for(int f=0; f<factIDList.size();f++){
			fact = new Fact();
			fact.setFactID(factIDList.get(f));
			
			String fDesc = Connection.getFactDescription(factIDList.get(f));
			fact.setFactDescription(fDesc);
			retValue.add(fact);
		}
		
		return retValue;
	}

	private String getEpmlFromCMapFile(String cmapFileName) {
		// Extract EpmlFileName from the given cmapFileName
		String epmlFileName = "";
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
		
			Document doc = null;
			doc = builder.parse(cmapFileName);
				
			XPathFactory xFactory = XPathFactory.newInstance();
			XPath xpath = xFactory.newXPath();
		
			XPathExpression expr = null;
			expr = xpath.compile("//c-epc");
		
			Object result = expr.evaluate(doc, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;
			
			if(nodes.getLength()>0){
				Element cepcElement = (Element)nodes.item(0);
				epmlFileName = cepcElement.getAttribute("epml");
			}
			/* This was needed to make it to absolute path
			if(epmlFileName.indexOf(":")==-1){
				epmlFileName = getDirFromFile(cmapFileName) + "\\" + epmlFileName;
			}*/
		
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		
		return epmlFileName;
	}

	private String getQMLFromCMapFile(String cmapFileName) {
		// Extract QmlFileName from the given cmapFileName
		String qmlFileName = "";
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(false);
		
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
		
			Document doc = null;
			doc = builder.parse(cmapFileName);
				
			XPathFactory xFactory = XPathFactory.newInstance();
			XPath xpath = xFactory.newXPath();
		
			XPathExpression expr = null;
			expr = xpath.compile("/CMAP");
		
			
			Object result = expr.evaluate(doc, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;
			
			if(nodes.getLength()>0){
				Element cepcElement = (Element)nodes.item(0);
				
				qmlFileName = cepcElement.getAttribute("qml");
				
				/* It was needed to get the absolute file path
				if(qmlFileName.indexOf(":")==-1){
					qmlFileName = getDirFromFile(cmapFileName) + "\\" + qmlFileName;
				}*/
			}

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		
		return qmlFileName;
	}
	
	private void deleteFact(Composite parent){
		TreeItem[] selectedItemList = null;
		
		if(tree.getSelectionCount()>0){
			selectedItemList = tree.getSelection();
			String selectedCMapType = (String) selectedItemList[0].getData("CMapType");
			
			if(selectedCMapType.equals("ORSub") || selectedCMapType.equals("XORSub") || selectedCMapType.equals("FunctionSub")){
				Fact fact = (Fact) selectedItemList[0].getData();

				String selectedPID = (String)selectedItemList[0].getData("PID");
				String factID = fact.getFactID();
				
				CMap.deleteFact(selectedPID, factID);
				Connection.CMapTreeSelectedPID = selectedPID;
				displayInTree(parent);
			}
			
			if(selectedCMapType.equals("OR") || selectedCMapType.equals("XOR") || selectedCMapType.equals("Function")){
				String[] buttonText = {"Yes", "No"};
				
				MessageDialog msg = new MessageDialog(parent.getShell(),"Confirmation of Delete", null, "Are you sure want to delete the whole?", MessageDialog.QUESTION, buttonText, 1);
				if(msg.open()==MessageDialog.OK){
					String selectedPID = (String)selectedItemList[0].getData("PID");

					CMap.deleteProcess(selectedPID);
					Connection.CMapTreeSelectedPID = selectedPID;
					displayInTree(parent);
				}
			}
			
			if(selectedCMapType.equals("Function")){
				
			}
		}
		else {
			//message : select a node first
			MessageDialog.openWarning(parent.getShell(), "Warning", "Please select a correct node first.");
		}
		
	}
	
	private void updateCondition(Composite parent){
		TreeItem[] selectedItemList = null;
		
		if(tree.getSelectionCount()>0){
			selectedItemList = tree.getSelection();
			//String selectedCMapType = (String) selectedItemList[0].getData("CMapType");
			
			String existingCondition = (String)selectedItemList[0].getData("CMapCondition");
			Connection.SelectedCondition = existingCondition;
			
			String existingFactList = (String)selectedItemList[0].getData("FactList");
			String[] selectedFacts = existingFactList.split(",");
			Connection.SelectedFacts = selectedFacts;
				
			String selectedPID = (String)selectedItemList[0].getData("PID");
			Connection.SelectedPID = selectedPID;
			Connection.RefreshView(Application.Views_DispConditionView_ID);

			/*
			if(selectedCMapType.equals("OR") || selectedCMapType.equals("XOR") || selectedCMapType.equals("Function")){
				String existingCondition = selectedItemList[0].getText(3);
				String[] selectedFacts = selectedItemList[0].getText(2).split(",");
				Connection.SelectedCondition = existingCondition;
				Connection.SelectedFacts = selectedFacts;
				
				String selectedPID = (String)selectedItemList[0].getData("PID");
				Connection.SelectedPID = selectedPID;
				Connection.RefreshView(Application.Views_DispConditionView_ID);
			}
			*/
			
		}
		else {
			//message : select a node first
			MessageDialog.openWarning(parent.getShell(), "Warning", "Please select a correct node first.");
		}
	}

	private String OpenFile(Composite parent) {
		String fileName = null;

		FileDialog fd  = new FileDialog(parent.getShell(), SWT.SAVE);
		fd.setFilterExtensions(new String[]{"*.cmap"});
		fd.setFilterNames(new String[]{"CMap File"});
		
		boolean done = false;

		while (!done) {
			fileName = fd.open();
			if (fileName == null) {
				done = true;
			} else {
				File file = new File(fileName);

		        if (file.exists()) {
		        	MessageBox mb = new MessageBox(fd.getParent(), SWT.ICON_WARNING
		              | SWT.YES | SWT.NO);

		        	mb.setMessage(fileName + " already exists. Do you want to replace it?");

		        	done = mb.open() == SWT.YES;
		        } else {
		        	done = true;
		        }
			}
			
		}
		
		return fileName;
	}
	  
	private void SaveCMap(Composite parent, boolean overwrite) {
		
		String fileName = null;
		if(overwrite){
			fileName = Application.cmapFileName;
		}
		else {
			fileName = OpenFile(parent);
		
			if(fileName==null){
				return;
			}
			
			Application.cmapFileName = fileName;
		}
		
		/*
		String qmlFileName = getActualFileName(Application.qmlFileName, fileName);
		String epmlFileName = getActualFileName(Application.modelFileName, fileName);
		*/
		
		String qmlFileName = Application.qmlFileName;
		String epmlFileName = Application.modelFileName;
		
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
		try {
			docBuilder = factory.newDocumentBuilder();
		
			Document doc = docBuilder.newDocument();
			Element CMapElement = doc.createElement("tns:CMAP");
			CMapElement.setAttribute("xmlns:tns", "http://www.processconfiguration.com/CMAP");
			CMapElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
			CMapElement.setAttribute("xsi:schemaLocation", "http://www.processconfiguration.com/CMAP CMAP.xsd");
			CMapElement.setAttribute("qml", qmlFileName);
			
			Element cepcElement = doc.createElement("c-epc");
			cepcElement.setAttribute("epml", epmlFileName);
			
			
			ArrayList<String> uniqueOrVarPoint = new ArrayList<String>();
			ArrayList<String> uniqueXOrVarPoint = new ArrayList<String>();
			ArrayList<String> uniqueFVarPoint = new ArrayList<String>();
			
			
			ArrayList<String> OrVarPoint = new ArrayList<String>();
			ArrayList<String> XOrVarPoint = new ArrayList<String>();
			ArrayList<String> FVarPoint = new ArrayList<String>();
			
			for(int t=0; t<tree.getItemCount(); t++){
				TreeItem treeItem = tree.getItem(t);
				String curID = treeItem.getText(0);
				
				if(treeItem.getData("CMapType").equals("OR")){
					OrVarPoint.add(curID);
				}
				
				if(treeItem.getData("CMapType").equals("XOR")){
					XOrVarPoint.add(curID);
				}
				
				if(treeItem.getData("CMapType").equals("Function")){
					FVarPoint.add(curID);
				}
			}
			
			uniqueOrVarPoint = Util.getUniqueArraylist(OrVarPoint);
			uniqueXOrVarPoint = Util.getUniqueArraylist(XOrVarPoint);
			uniqueFVarPoint = Util.getUniqueArraylist(FVarPoint);
			
			for(int i =0; i<uniqueOrVarPoint.size();i++){
				System.out.println(uniqueOrVarPoint.get(i));
				
				String curVarPoint = uniqueOrVarPoint.get(i);
				Element corElement = doc.createElement("COR");
				boolean addedID = false;
				for(int t=0; t<tree.getItemCount(); t++){
					TreeItem treeItem = tree.getItem(t);
					if(treeItem.getData("CMapType").equals("OR")){
						CMapCORItem cmapCORItem = (CMapCORItem)tree.getItem(t).getData();
						String varPoint = tree.getItem(t).getText(0);
						if(varPoint.equals(curVarPoint)){
							if(!addedID){
								corElement.setAttribute("id", cmapCORItem.getCORid());
								addedID = true;
							}
							Element corSubItem = doc.createElement("value");
							corSubItem.setAttribute("condition", cmapCORItem.getCORcondition());
							
							if(cmapCORItem.getCORgoto()!=null){
								corSubItem.setAttribute("goto", cmapCORItem.getCORgoto());
							}
							corSubItem.setAttribute("type", cmapCORItem.getCORtype());
							
							corElement.appendChild(corSubItem);
						}
					}
				}
				
				cepcElement.appendChild(corElement);
			}

			for(int i =0; i<uniqueXOrVarPoint.size();i++){
				System.out.println(uniqueXOrVarPoint.get(i));
				
				String curVarPoint = uniqueXOrVarPoint.get(i);
				Element cxorElement = doc.createElement("CXOR");
				boolean addedID = false;
				for(int t=0; t<tree.getItemCount(); t++){
					TreeItem treeItem = tree.getItem(t);
					if(treeItem.getData("CMapType").equals("XOR")){
						CMapCXORItem cmapCXORItem = (CMapCXORItem)tree.getItem(t).getData();
						String varPoint = tree.getItem(t).getText(0);
						if(varPoint.equals(curVarPoint)){
							if(!addedID){
								cxorElement.setAttribute("id", cmapCXORItem.getCXORid());
								addedID = true;
							}
							Element cxorSubItem = doc.createElement("value");
							cxorSubItem.setAttribute("condition", cmapCXORItem.getCXORcondition());
							
							if(cmapCXORItem.getCXORgoto()!=null){
								cxorSubItem.setAttribute("goto", cmapCXORItem.getCXORgoto());
							}
							cxorSubItem.setAttribute("type", cmapCXORItem.getCXORtype());
							
							cxorElement.appendChild(cxorSubItem);
						}
					}
				}
				
				cepcElement.appendChild(cxorElement);
			}
			
			for(int i =0; i<uniqueFVarPoint.size();i++){
				System.out.println(uniqueFVarPoint.get(i));
				
				String curVarPoint = uniqueFVarPoint.get(i);
				Element funcElement = doc.createElement("CFunction");
				boolean addedID = false;
				for(int t=0; t<tree.getItemCount(); t++){
					TreeItem treeItem = tree.getItem(t);
					if(treeItem.getData("CMapType").equals("Function")){
						CMapFunctionItem cmapFunctionItem = (CMapFunctionItem)tree.getItem(t).getData();
						String varPoint = tree.getItem(t).getText(0);
						if(varPoint.equals(curVarPoint)){
							if(!addedID){
								funcElement.setAttribute("id", cmapFunctionItem.getFunctionID());
								addedID = true;
							}
							Element corSubItem = doc.createElement("value");
							corSubItem.setAttribute("condition", cmapFunctionItem.getFunctionCondition());
							
							corSubItem.setAttribute("type", cmapFunctionItem.getFunctionType());
							
							funcElement.appendChild(corSubItem);
						}
					}
				}
				
				cepcElement.appendChild(funcElement);
			}
			
			CMapElement.appendChild(cepcElement);
			
        	doc.appendChild(CMapElement);
        	
        	DOMSource domSource = new DOMSource(doc);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            StreamResult sr = new StreamResult(fileName);

            transformer.transform(domSource, sr);
        	
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}
	
	//To find out whether the EPML file is in the same directory or not
	private String getActualFileName(String modelFileName, String fileName) {
		String actualFileName = modelFileName;
	
		String dirCMapFile = fileName.substring(0, fileName.lastIndexOf("/"));
		//String onlyCMapFileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
		
		String dirModelFile = modelFileName.substring(0, modelFileName.lastIndexOf("/"));
		String onlyModelFileName = modelFileName.substring(modelFileName.lastIndexOf("/") + 1);
		
		if(dirCMapFile.equals(dirModelFile)){
			actualFileName = onlyModelFileName;
		}

		return actualFileName;
	}
}
