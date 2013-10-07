package com.processconfiguration.cmap.rcp.views;

import java.io.File;
import java.io.IOException;

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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.ToolBar;
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
import com.processconfiguration.cmap.rcp.cmap.CMap;
import com.processconfiguration.cmap.rcp.model.COR;
import com.processconfiguration.cmap.rcp.model.CXOR;
import com.processconfiguration.cmap.rcp.model.Connection;
import com.processconfiguration.cmap.rcp.model.Fact;
import com.processconfiguration.cmap.rcp.model.Function;
import com.processconfiguration.cmap.rcp.model.SingleLink;


public class ModelViewPart extends ViewPart {
	Tree mTree = null;
	
	public ModelViewPart() {
		
	}

	@Override
	public void createPartControl(final Composite parent) {
		mTree = new Tree(parent, SWT.SINGLE);
		//mTree.setLinesVisible(true);
		
		try {
			if(Application.modelFilePath != ""){
				if(Util.isCorrectModelFile(Application.modelFilePath)){
					displayModelTree(parent, Application.modelFilePath);
				}
				else
				{
					//Message -- Not a correct EPML file
					MessageDialog.openWarning(parent.getShell(), "Warning", "File is not a correct EPML file.");
				}
			}
			
			createActions(parent.getShell());
			createDrapAndDrop(parent);
			
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

	private void createDrapAndDrop(Composite parent) {
		int operations = DND.DROP_MOVE | DND.DROP_COPY;
		
		DropTarget target = new DropTarget(mTree, operations);
		final TextTransfer textTransfer = TextTransfer.getInstance();
		final FileTransfer fileTransfer = FileTransfer.getInstance();
		
		Transfer[] dropTypes = new Transfer[]{textTransfer, fileTransfer};
		target.setTransfer(dropTypes);
		
		target.addDropListener(new DropTargetListener() {
			@Override
			public void dropAccept(DropTargetEvent event) {
				if(event.item.getData() == null){
					event.detail = DND.DROP_NONE;
				}
			}
			
			@Override
			public void drop(DropTargetEvent event) {
	            String dragText = (String) event.data;
	            String factID = dragText.substring(0, dragText.indexOf("-")-1);
				String factDesc = dragText.substring(dragText.indexOf("-")+1);
				Fact f = new Fact();
	            f.setFactID(factID.trim());
	    		f.setFactDescription(factDesc.trim());
				
	            TreeItem dropOnItem = (TreeItem)event.item;
	            String dropOnText = dropOnItem.getText();
	           
	            String nodeType = (String)dropOnItem.getData("NodeType");
	            if(nodeType.equals("or")){
		    		COR c = (COR)dropOnItem.getData();
		    		SingleLink singleLink = new SingleLink(f, c);
		    		
		    		Connection.LinkList.add(singleLink);
		    		CMap.addNewLink(singleLink);
		    		
		    		Connection.displayAllLinks();
	            }
	            
	            if(nodeType.equals("xor")){
		    		CXOR xor = (CXOR)dropOnItem.getData();
		    		SingleLink singleLink = new SingleLink(f, xor);
		    		
		    		Connection.LinkList.add(singleLink);
		    		CMap.addNewLink(singleLink);
		    		
		    		Connection.displayAllLinks();
	            }
	            
	            if(nodeType.equals("function")){
	            	Function func = (Function)dropOnItem.getData();
	            	SingleLink singleLink = new SingleLink(f, func);
	            	Connection.LinkList.add(singleLink);
	            	CMap.addNewLink(singleLink);
	            	Connection.displayAllLinks();
	            }
	            
	            /*
	            String fChar = dropOnText.substring(0, 1);
	            if(fChar.equals("p")){
		    		COR c = (COR)dropOnItem.getData();
		    		SingleLink singleLink = new SingleLink(f, c);
		    		
		    		Connection.LinkList.add(singleLink);
		    		CMap.addNewLink(singleLink);
		    		
		    		Connection.displayAllLinks();
	            }
	            
	            if(fChar.equals("F")){
	            	Function func = (Function)dropOnItem.getData();
	            	SingleLink singleLink = new SingleLink(f, func);
	            	Connection.LinkList.add(singleLink);
	            	CMap.addNewLink(singleLink);
	            	Connection.displayAllLinks();
	            }
	            */
			}
			
			@Override
			public void dragOver(DropTargetEvent event) {
				
			}
			
			@Override
			public void dragOperationChanged(DropTargetEvent event) {
				
			}
			
			@Override
			public void dragLeave(DropTargetEvent event) {
								
			}
			
			@Override
			public void dragEnter(DropTargetEvent event) {

			}
		});
	}

	private void expandTree(){
		int totalTreeItem = mTree.getItemCount();
		
		TreeItem treeItem;
		for(int i = 0; i<totalTreeItem; i++){
			treeItem = mTree.getItem(i);
			treeItem.setExpanded(true);
		}
	}
	
	private void colapseTree(){
		int totalTreeItem = mTree.getItemCount();
		
		TreeItem treeItem;
		for(int i = 0; i<totalTreeItem; i++){
			treeItem = mTree.getItem(i);
			treeItem.setExpanded(false);
		}
			
	}
	
	private void createActions(final Composite parent) {
		Action openAction = new Action("Open...") {
			@Override
			public void run(){
				FileDialog fd  = new FileDialog(parent.getShell(), SWT.OPEN);
				fd.setFilterExtensions(new String[]{"*.epml"});
				String mfileName = fd.open();
				
				if(mfileName != null){
					
					if(Util.isCorrectModelFile(mfileName)){
						File file = new File(mfileName);
						
						Application.modelFileName = file.getName();
						Application.modelFilePath = mfileName;
						System.out.println("Model file: " + Application.modelFileName + ", Path: " + mfileName);
						
						try {
							displayModelTree(parent, mfileName);
							
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
						//Message: -- Not a correct EPML file
						MessageDialog.openWarning(parent.getShell(), "Warning", "File is not a correct EPML file.");
					}
				}
			}
		};
		
		Action actionClose = new Action("Close"){
			@Override
			public void run(){
				closeModelFile();
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
		
		openAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_ADD));
		openAction.setToolTipText("Open Conf. Process Model File");
		toolBar.add(openAction);
		
		actionClose.setToolTipText("Close Conf. Process Model File");
		ImageDescriptor imgDesc = AbstractUIPlugin.imageDescriptorFromPlugin("com.processconfiguration.cmap.rcp", "/icons/Actions-remove-icon.png");
		actionClose.setImageDescriptor(imgDesc);
		toolBar.add(actionClose);
		
		//actionExpandAll.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_DEF_VIEW));
		imgDesc = AbstractUIPlugin.imageDescriptorFromPlugin("com.processconfiguration.cmap.rcp", "/icons/expandall.gif");
		actionExpandAll.setImageDescriptor(imgDesc);
		actionExpandAll.setToolTipText("Expand All");
		toolBar.add(actionExpandAll);
		
		
		actionCollapseAll.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_COLLAPSEALL));
		actionCollapseAll.setToolTipText("Collapse All");
		toolBar.add(actionCollapseAll);
	}

	private void closeModelFile() {
		Application.modelFileName = "";
		Application.modelFilePath ="";
		mTree.removeAll();
		
	}
	
	private void displayModelTree(Composite parent, String EpmlFile) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException{
		mTree.removeAll();

		Connection.RefreshCORandFunctionList();
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = null;
		doc = builder.parse(EpmlFile);

		DisplayOrNodes(doc);
		DisplayXOrNodes(doc);
		DisplayFuncNodes(doc);
	}
	
	private void DisplayFuncNodes(Document doc) throws XPathExpressionException {
		XPathFactory xFactory = XPathFactory.newInstance();
		XPath xpath = xFactory.newXPath();
		
		XPathExpression expr = null;
		expr = xpath.compile("//function");

		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		NodeList funcNodes = (NodeList) result;
		
		int fCount = 1;
		
		Function func = null;
		
		for(int i = 0; i<funcNodes.getLength(); i++){
			Element funcElement = (Element)funcNodes.item(i);

			NodeList nn = funcElement.getElementsByTagName("configurableFunction");
			
			if(nn.getLength()>0){
				String funcID = funcElement.getAttribute("id");
				TreeItem funcTreeItem = new TreeItem(mTree, 0);
				String funcName = funcElement.getElementsByTagName("name").item(0).getTextContent();
				
				funcTreeItem.setText("Function [" + funcID + "]: " + funcName);
				
				TreeItem fNodeItem = new TreeItem(funcTreeItem, 0);
				String fID = "F" + fCount++;
				func = new Function();
				func.setFunctionID(funcID);
				func.setpID(fID);
				func.setType("on");
				fNodeItem.setData(func);
				fNodeItem.setData("NodeType", "function");
				fNodeItem.setText(fID + " - on");
				Connection.FunctionList.add(func);
				
				fNodeItem = new TreeItem(funcTreeItem, 0);
				fID = "F" + fCount++;
				func = new Function();
				func.setFunctionID(funcID);
				func.setpID(fID);
				func.setType("off");
				fNodeItem.setData(func);
				fNodeItem.setData("NodeType", "function");
				fNodeItem.setText(fID + " - off");
				
				Connection.FunctionList.add(func);
			}
		}
	}

	private void DisplayOrNodes(Document doc) throws XPathExpressionException {
		XPathFactory xFactory = XPathFactory.newInstance();
		XPath xpath = xFactory.newXPath();
		
		XPathExpression expr = null;
		expr = xpath.compile("//or");

		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		NodeList orNodes = (NodeList) result;
		
		int pCount = 1;
		
		COR cor = null;
		
		for(int i = 0; i<orNodes.getLength(); i++){
			Element orElement = (Element)orNodes.item(i);

			if(orElement.getElementsByTagName("configurableConnector").getLength()>0){
				String orID = orElement.getAttribute("id");
				TreeItem orTreeItem = new TreeItem(mTree, 0);
				orTreeItem.setText("OR [" + orID + "]:");
				
				TreeItem pNodeItem = new TreeItem(orTreeItem, 0);
				String pID = "p" + pCount++;
				cor = new COR();
				cor.setpID(pID);
				cor.setCorID(orID);
				cor.setCorType("or");
				pNodeItem.setData(cor);
				pNodeItem.setData("NodeType", "or");
				pNodeItem.setData("orID", orID);
				pNodeItem.setText(pID + " - or");
				Connection.CORList.add(cor);
				
				pNodeItem = new TreeItem(orTreeItem, 0);
				pID = "p" + pCount++;
				cor = new COR();
				cor.setpID(pID);
				cor.setCorID(orID);
				cor.setCorType("and");
				pNodeItem.setData(cor);
				pNodeItem.setData("NodeType", "or");
				pNodeItem.setData("orID", orID);
				pNodeItem.setText(pID + " - and");
				Connection.CORList.add(cor);
				
				pNodeItem = new TreeItem(orTreeItem, 0);
				pID = "p" + pCount++;
				cor = new COR();
				cor.setpID(pID);
				cor.setCorID(orID);
				cor.setCorType("xor");
				pNodeItem.setData(cor);
				pNodeItem.setData("NodeType", "or");
				pNodeItem.setData("orID", orID);
				pNodeItem.setText(pID + " - xor");
				Connection.CORList.add(cor);
				
				expr = xpath.compile("//arc/flow[@source='" + orID + "']");
				result = expr.evaluate(doc, XPathConstants.NODESET);
				NodeList flowNodes = (NodeList) result;
				
				if(flowNodes.getLength()<2){
					expr = xpath.compile("//arc/flow[@target='" + orID + "']");
					result = expr.evaluate(doc, XPathConstants.NODESET);
					flowNodes = (NodeList) result;
					
					for (int f = 0; f<flowNodes.getLength(); f++){
						Element flowElement = (Element)flowNodes.item(f);
						String targetID = flowElement.getAttribute("source");
						
						Element arcElement = (Element)flowElement.getParentNode();
						String arcID = arcElement.getAttribute("id");
						
						expr = xpath.compile("//event[@id='" + targetID + "']/name");
						result = expr.evaluate(doc, XPathConstants.NODESET);
						NodeList eventNameNodes = (NodeList) result;
						
						expr = xpath.compile("//function[@id='" + targetID + "']/name");
						result = expr.evaluate(doc, XPathConstants.NODESET);
						NodeList functionNameNodes = (NodeList) result;

						if(eventNameNodes.getLength()>0){
							Element eventNameElement = (Element)eventNameNodes.item(0);
							String eventName = eventNameElement.getTextContent();
							pNodeItem = new TreeItem(orTreeItem, 0);
							pID = "p" + pCount++;
							cor = new COR();
							cor.setpID(pID);
							cor.setCorID(orID);
							cor.setCorType("seq");
							cor.setCorGoto(arcID);
							cor.setCorSorT(targetID);
							cor.setCorGotoName(eventName);
							pNodeItem.setData(cor);
							pNodeItem.setData("NodeType", "or");
							pNodeItem.setData("orID", orID);
							pNodeItem.setText(pID + " - seq[" + arcID + "] -> Event[" + targetID + "]: "+ eventName);
							Connection.CORList.add(cor);
						}
		
						if(functionNameNodes.getLength()>0){
							Element functionNameElement = (Element)functionNameNodes.item(0);
							String functionName = functionNameElement.getTextContent();
							pNodeItem = new TreeItem(orTreeItem, 0);
							pID = "p" + pCount++;
							cor = new COR();
							cor.setpID(pID);
							cor.setCorID(orID);
							cor.setCorType("seq");
							cor.setCorGoto(arcID);
							cor.setCorSorT(targetID);
							cor.setCorGotoName(functionName);
							pNodeItem.setData(cor);
							pNodeItem.setData("NodeType", "or");
							pNodeItem.setData("orID", orID);
							pNodeItem.setText(pID + " - seq[" + arcID + "] -> Function[" + targetID + "]: " + functionName);
							Connection.CORList.add(cor);
						}

					}
				}
				
				else {
				for (int f = 0; f<flowNodes.getLength(); f++){
					Element flowElement = (Element)flowNodes.item(f);
					String targetID = flowElement.getAttribute("target");
					
					Element arcElement = (Element)flowElement.getParentNode();
					String arcID = arcElement.getAttribute("id");
					
					expr = xpath.compile("//event[@id='" + targetID + "']/name");
					result = expr.evaluate(doc, XPathConstants.NODESET);
					NodeList eventNameNodes = (NodeList) result;
					//int totalEventNodes = eventNameNodes.getLength();
					
					expr = xpath.compile("//function[@id='" + targetID + "']/name");
					result = expr.evaluate(doc, XPathConstants.NODESET);
					NodeList functionNameNodes = (NodeList) result;
					//int totalFunctionNodes = functionNameNodes.getLength();
	
					if(eventNameNodes.getLength()>0){
						Element eventNameElement = (Element)eventNameNodes.item(0);
						String eventName = eventNameElement.getTextContent();
						pNodeItem = new TreeItem(orTreeItem, 0);
						pID = "p" + pCount++;
						cor = new COR();
						cor.setpID(pID);
						cor.setCorID(orID);
						cor.setCorType("seq");
						cor.setCorGoto(arcID);
						cor.setCorSorT(targetID);
						cor.setCorGotoName(eventName);
						pNodeItem.setData(cor);
						pNodeItem.setData("NodeType", "or");
						pNodeItem.setData("orID", orID);
						pNodeItem.setText(pID + " - seq[" + arcID + "] -> Event[" + targetID + "]: "+ eventName);
						Connection.CORList.add(cor);
					}
	
					if(functionNameNodes.getLength()>0){
						Element functionNameElement = (Element)functionNameNodes.item(0);
						String functionName = functionNameElement.getTextContent();
						pNodeItem = new TreeItem(orTreeItem, 0);
						pID = "p" + pCount++;
						cor = new COR();
						cor.setpID(pID);
						cor.setCorID(orID);
						cor.setCorType("seq");
						cor.setCorGoto(arcID);
						cor.setCorSorT(targetID);
						cor.setCorGotoName(functionName);
						pNodeItem.setData(cor);
						pNodeItem.setData("NodeType", "or");
						pNodeItem.setData("orID", orID);
						pNodeItem.setText(pID + " - seq[" + arcID + "] -> Function[" + targetID + "]: " + functionName);
						Connection.CORList.add(cor);
					}
				}
				}
				
				//Un-comment following code to show the tree expanded at the time of open
				//orTreeItem.setExpanded(true);
			}
		}
	}

	private void DisplayXOrNodes(Document doc) throws XPathExpressionException {
		XPathFactory xFactory = XPathFactory.newInstance();
		XPath xpath = xFactory.newXPath();
		
		XPathExpression expr = null;
		expr = xpath.compile("//xor");

		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		NodeList xorNodes = (NodeList) result;
		
		int pCount = 1;
		
		CXOR cxor = null;
		
		for(int i = 0; i<xorNodes.getLength(); i++){
			Element xorElement = (Element)xorNodes.item(i);

			if(xorElement.getElementsByTagName("configurableConnector").getLength()>0){
				String xorID = xorElement.getAttribute("id");
				TreeItem xorTreeItem = new TreeItem(mTree, 0);
				xorTreeItem.setText("XOR [" + xorID + "]:");
				
				TreeItem pNodeItem = new TreeItem(xorTreeItem, 0);
				String pID = "p" + pCount++;
				cxor = new CXOR();
				cxor.setpID(pID);
				cxor.setCxorID(xorID);
				cxor.setCxorType("xor");
				pNodeItem.setData(cxor);
				pNodeItem.setData("NodeType", "xor");
				pNodeItem.setData("xorID", xorID);
				pNodeItem.setText(pID + " - xor");
				Connection.CXORList.add(cxor);
		
				expr = xpath.compile("//arc/flow[@source='" + xorID + "']");
				result = expr.evaluate(doc, XPathConstants.NODESET);
				NodeList flowNodes = (NodeList) result;
				
				if(flowNodes.getLength()<2){
					expr = xpath.compile("//arc/flow[@target='" + xorID + "']");
					result = expr.evaluate(doc, XPathConstants.NODESET);
					flowNodes = (NodeList) result;
					
					for (int f = 0; f<flowNodes.getLength(); f++){
						Element flowElement = (Element)flowNodes.item(f);
						String targetID = flowElement.getAttribute("source");
						
						Element arcElement = (Element)flowElement.getParentNode();
						String arcID = arcElement.getAttribute("id");
						
						expr = xpath.compile("//event[@id='" + targetID + "']/name");
						result = expr.evaluate(doc, XPathConstants.NODESET);
						NodeList eventNameNodes = (NodeList) result;
						
						expr = xpath.compile("//function[@id='" + targetID + "']/name");
						result = expr.evaluate(doc, XPathConstants.NODESET);
						NodeList functionNameNodes = (NodeList) result;

						if(eventNameNodes.getLength()>0){
							Element eventNameElement = (Element)eventNameNodes.item(0);
							String eventName = eventNameElement.getTextContent();
							pNodeItem = new TreeItem(xorTreeItem, 0);
							pID = "p" + pCount++;
							cxor = new CXOR();
							cxor.setpID(pID);
							cxor.setCxorID(xorID);
							cxor.setCxorType("seq");
							cxor.setCxorGoto(arcID);
							cxor.setCxorSorT(targetID);
							cxor.setCxorGotoName(eventName);
							pNodeItem.setData(cxor);
							pNodeItem.setData("NodeType", "xor");
							pNodeItem.setData("xorID", xorID);
							pNodeItem.setText(pID + " - seq[" + arcID + "] -> Event[" + targetID + "]: "+ eventName);
							Connection.CXORList.add(cxor);
						}
		
						if(functionNameNodes.getLength()>0){
							Element functionNameElement = (Element)functionNameNodes.item(0);
							String functionName = functionNameElement.getTextContent();
							pNodeItem = new TreeItem(xorTreeItem, 0);
							pID = "p" + pCount++;
							cxor = new CXOR();
							cxor.setpID(pID);
							cxor.setCxorID(xorID);
							cxor.setCxorType("seq");
							cxor.setCxorGoto(arcID);
							cxor.setCxorSorT(targetID);
							cxor.setCxorGotoName(functionName);
							pNodeItem.setData(cxor);
							pNodeItem.setData("NodeType", "xor");
							pNodeItem.setData("xorID", xorID);
							pNodeItem.setText(pID + " - seq[" + arcID + "] -> Function[" + targetID + "]: " + functionName);
							Connection.CXORList.add(cxor);
						}

					}
				}
				
				else {
				for (int f = 0; f<flowNodes.getLength(); f++){
					Element flowElement = (Element)flowNodes.item(f);
					String targetID = flowElement.getAttribute("target");
					
					Element arcElement = (Element)flowElement.getParentNode();
					String arcID = arcElement.getAttribute("id");
					
					expr = xpath.compile("//event[@id='" + targetID + "']/name");
					result = expr.evaluate(doc, XPathConstants.NODESET);
					NodeList eventNameNodes = (NodeList) result;
					//int totalEventNodes = eventNameNodes.getLength();
					
					expr = xpath.compile("//function[@id='" + targetID + "']/name");
					result = expr.evaluate(doc, XPathConstants.NODESET);
					NodeList functionNameNodes = (NodeList) result;
					//int totalFunctionNodes = functionNameNodes.getLength();
	
					if(eventNameNodes.getLength()>0){
						Element eventNameElement = (Element)eventNameNodes.item(0);
						String eventName = eventNameElement.getTextContent();
						pNodeItem = new TreeItem(xorTreeItem, 0);
						pID = "p" + pCount++;
						cxor = new CXOR();
						cxor.setpID(pID);
						cxor.setCxorID(xorID);
						cxor.setCxorType("seq");
						cxor.setCxorGoto(arcID);
						cxor.setCxorSorT(targetID);
						cxor.setCxorGotoName(eventName);
						pNodeItem.setData(cxor);
						pNodeItem.setData("NodeType", "xor");
						pNodeItem.setData("orID", xorID);
						pNodeItem.setText(pID + " - seq[" + arcID + "] -> Event[" + targetID + "]: "+ eventName);
						Connection.CXORList.add(cxor);
					}
	
					if(functionNameNodes.getLength()>0){
						Element functionNameElement = (Element)functionNameNodes.item(0);
						String functionName = functionNameElement.getTextContent();
						pNodeItem = new TreeItem(xorTreeItem, 0);
						pID = "p" + pCount++;
						cxor = new CXOR();
						cxor.setpID(pID);
						cxor.setCxorID(xorID);
						cxor.setCxorType("seq");
						cxor.setCxorGoto(arcID);
						cxor.setCxorSorT(targetID);
						cxor.setCxorGotoName(functionName);
						pNodeItem.setData(cxor);
						pNodeItem.setData("NodeType", "xor");
						pNodeItem.setData("xorID", xorID);
						pNodeItem.setText(pID + " - seq[" + arcID + "] -> Function[" + targetID + "]: " + functionName);
						Connection.CXORList.add(cxor);
					}
				}
				}
				
				//Un-comment following code to show the tree expanded at the time of open
				//orTreeItem.setExpanded(true);
			}
		}
	}
	@Override
	public void setFocus() {
		
	}

}
