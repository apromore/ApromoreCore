package com.processconfiguration.cmap.rcp.views;


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.processconfiguration.cmap.rcp.Application;
import com.processconfiguration.cmap.rcp.Util;
import com.processconfiguration.cmap.rcp.model.Connection;
import com.processconfiguration.cmap.rcp.model.Fact;


public class QmlViewPart extends ViewPart {
	Tree qTree = null;
	
	public QmlViewPart() {
		
	}

	@Override
	public void createPartControl(final Composite parent) {
		qTree = new Tree(parent, SWT.SINGLE);
		//qTree.setLinesVisible(true);
		
		try {
			if(Application.qmlFilePath != ""){
				
				if(Util.isCorrectQmlFile(Application.qmlFilePath)){
					displayTree(parent, Application.qmlFilePath);
				}
				else {
					//Message: -- QML file not in correct format
				}
			}
			
			createActions(parent);
			createDragAndDrop(parent);
			
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	private void createDragAndDrop(Composite parent) {
		int operations = DND.DROP_MOVE | DND.DROP_COPY;
		DragSource source = new DragSource(qTree, operations);
		
		Transfer[] types = new Transfer[] {TextTransfer.getInstance()};
		source.setTransfer(types);
		
		source.addDragListener(new DragSourceListener() {
			
			@Override
			public void dragStart(DragSourceEvent event) {
				TreeItem[] selection = qTree.getSelection();
				
				System.out.println("Start Dragging" + selection[0]);
				
				if(selection.length == 0)
					event.doit = false;
				else {
					TreeItem selectedItem = selection[0];
					if(selectedItem.getItemCount() >0){
						event.doit = false;
					}
				}
			}
			
			@Override
			public void dragSetData(DragSourceEvent event) {
				TreeItem[] selection = qTree.getSelection();
				String TreeItemText = selection[0].getText();
				event.data = TreeItemText;
			}
			
			@Override
			public void dragFinished(DragSourceEvent event) {
				
			}
		});
	}

	private void createActions(final Composite parent) {
		Action action = new Action("Open...") {
			@Override
			public void run(){
				FileDialog fd  = new FileDialog(parent.getShell(), SWT.OPEN);
				fd.setFilterExtensions(new String[]{"*.qml"});
				String qfileName = fd.open();
				
				if(qfileName != null){
					if(Util.isCorrectQmlFile(qfileName)){
						File file = new File(qfileName);
						
						Application.qmlFileName = file.getName();
						Application.qmlFilePath = qfileName;
						System.out.println("Question Model File: " + Application.qmlFileName + ", Path: " + qfileName);
						
						try {
							displayTree(parent, qfileName);
							
						} catch (XPathExpressionException e) {
							e.printStackTrace();
						} catch (SAXException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} catch (ParserConfigurationException e) {
							e.printStackTrace();
						}
					}
					else {
						
						//--Message: The Qml file is not in correct format
					}
				}
			}

			
		};
		
		Action actionClose = new Action("Close"){
			@Override
			public void run(){
				closeQmlFile();
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
		IActionBars actionBars = getViewSite().getActionBars();
		IToolBarManager toolBar = actionBars.getToolBarManager();
		
		
		action.setToolTipText("Open Questionnaire Model File");
		action.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_ADD));
		toolBar.add(action);
		
		actionClose.setToolTipText("Close Questionnaire Model File");
		ImageDescriptor imgDesc = AbstractUIPlugin.imageDescriptorFromPlugin("com.processconfiguration.cmap.rcp", "/icons/Actions-remove-icon.png");
		actionClose.setImageDescriptor(imgDesc);
		toolBar.add(actionClose);
		
		actionExpandAll.setToolTipText("Expand All");
		imgDesc = AbstractUIPlugin.imageDescriptorFromPlugin("com.processconfiguration.cmap.rcp", "/icons/expandall.gif");
		actionExpandAll.setImageDescriptor(imgDesc);
		toolBar.add(actionExpandAll);
		
		actionCollapseAll.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_COLLAPSEALL));
		actionCollapseAll.setToolTipText("Collapse All");
		toolBar.add(actionCollapseAll);
	}

	private void closeQmlFile() {
		Application.qmlFileName = "";
		Application.qmlFilePath ="";
		qTree.removeAll();
		
	}
	
	@Override
	public void setFocus() {


	}

	private void expandTree(){
		int totalQuesItem = qTree.getItemCount();
		
		TreeItem qTreeItem;
		for(int i = 0; i<totalQuesItem; i++){
			qTreeItem = qTree.getItem(i);
			qTreeItem.setExpanded(true);
		}
			
	}
	
	private void colapseTree(){
		int totalQuesItem = qTree.getItemCount();
		
		TreeItem qTreeItem;
		for(int i = 0; i<totalQuesItem; i++){
			qTreeItem = qTree.getItem(i);
			qTreeItem.setExpanded(false);
		}
			
	}
	
	private void displayTree(Composite parent, String QmlFileName) 
				throws SAXException, IOException, XPathExpressionException, ParserConfigurationException{
		qTree.removeAll();
		Connection.RefreshFactList();
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = null;
		doc = builder.parse(QmlFileName);
				
		XPathFactory xFactory = XPathFactory.newInstance();
		XPath xpath = xFactory.newXPath();
		
		XPathExpression expr = null;
		expr = xpath.compile("//Question");
		
		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		NodeList qNodes = (NodeList) result;
		for(int i = 0; i<qNodes.getLength(); i++){
			Element qElement = (Element)qNodes.item(i);
			String qID = qElement.getAttribute("id");
			
			Element qDescElement = (Element)qElement.getElementsByTagName("description").item(0);
			String qDescription = qDescElement.getTextContent();
			
			TreeItem qTreeItem = new TreeItem(qTree, 0);
			qTreeItem.setText(qID + " - " + qDescription);
			
			String qMapQF = qElement.getAttribute("mapQF"); 
	
			String[] factIDs = qMapQF.split(" ");
			
			Fact fact = null;
			for(int f = 0; f<factIDs.length; f++){
				String factID = factIDs[f].substring(1); //to Remove the '#' prefix
				expr = xpath.compile("//Fact[@id = '" + factID + "']");
				result = expr.evaluate(doc, XPathConstants.NODESET);
				NodeList factList = (NodeList)result;
				Element factElement = (Element)factList.item(0);
				
				Element fDescElement = (Element)factElement.getElementsByTagName("description").item(0);
				String fDescription = fDescElement.getTextContent();
				
				TreeItem fTreeItem = new TreeItem(qTreeItem, 0);
				fTreeItem.setText(factID + " - " + fDescription);
				
				fact = new Fact();
				fact.setFactID(factID);
				fact.setFactDescription(fDescription);
				Connection.FactList.add(fact);
			}
			
			//--Uncomment following code to make the tree expanded when first open
			//qTreeItem.setExpanded(true);
		}
		
	}
}
