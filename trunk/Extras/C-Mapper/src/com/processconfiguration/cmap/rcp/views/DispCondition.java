package com.processconfiguration.cmap.rcp.views;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import org.eclipse.ui.part.ViewPart;

import com.processconfiguration.cmap.rcp.Application;
import com.processconfiguration.cmap.rcp.cmap.CMap;
import com.processconfiguration.cmap.rcp.model.Connection;


public class DispCondition extends ViewPart {
	private String[] selectedFacts;
	private String selectedCondition;
	private String selectedPID;
	//private String[] symbolTexts = {"" + (char)0x00AC , "" + (char)0x02C4, "" + (char)0x02C5, "(", ")"};
	private String[] symbolTexts = {"-", ".", "+", "XOR","NOR",","," ", "=>","=","<=", "(", ")"};
	private String[] symbolinCondition = {"-", ".", "+", "XOR()","NOR()",","," ", "=>","=","<=", "(", ")"};
	private String[] symbolTextToolTip = {"NOT", "AND", "OR", "eXclusive OR", "NOR", "Comma", "Space", "=>", "Equal", "<=", "(", ")"};
	
	Text txtCondition = null;
	
	public DispCondition() {
		selectedFacts = Connection.SelectedFacts;
		selectedCondition = Connection.SelectedCondition;
		selectedPID = Connection.SelectedPID;
	}

	@Override
	public void createPartControl(final Composite parent) {
		if(selectedPID.isEmpty())
			return;

		int factCount = selectedFacts.length;
		int symCount = symbolTexts.length;
		
		//Warning we assume that the symbol count is always greater than the factcount
		int factCountDiff = symCount - factCount;
		
		//int allButtonCount = factCount + symCount;
		GridLayout gridLayout = new  GridLayout();
		//gridLayout.numColumns = allButtonCount;
		gridLayout.numColumns = symCount;
		parent.setLayout(gridLayout);
		
		
		for(int b=0; b<symCount; b++){
			Button sButton = new Button(parent, SWT.NONE);
			sButton.setText(symbolTexts[b]);
			sButton.setToolTipText(symbolTextToolTip[b]);
			
			GridData gridData = new GridData(50,25);
			sButton.setLayoutData(gridData);
			sButton.setLayoutData(gridData);
			
			final String displayText = symbolinCondition[b];
			
			sButton.addSelectionListener(new SelectionListener() {
				
				@Override
				public void widgetSelected(SelectionEvent e) {
					updateText(displayText);
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
										
				}
			});
		}
		
		
		for(int b=0; b<factCount; b++){
			Button fButton = new Button(parent, SWT.NONE);
			fButton.setText(selectedFacts[b]);
			
			GridData gridData = new GridData(50,25);
			fButton.setLayoutData(gridData);
			
			final String displayText = selectedFacts[b];
			
			fButton.addSelectionListener(new SelectionListener() {
				
				@Override
				public void widgetSelected(SelectionEvent e) {
					updateText(displayText);
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
										
				}
			});
		}
		
		GridData gridData = new GridData(500,25);
		gridData.horizontalSpan = factCountDiff;
		
		
		txtCondition = new Text(parent, SWT.NONE);
		txtCondition.setText(selectedCondition);
		
		gridData = new GridData(500,25);
		gridData.horizontalSpan = symCount;
		gridData.horizontalAlignment = SWT.CENTER;
		txtCondition.setLayoutData(gridData);
		
		Button okButton = new Button(parent, SWT.NONE);
		okButton.setText("Done");
		
		gridData = new GridData(80,25);
		gridData.horizontalSpan = symCount;
		gridData.horizontalAlignment = SWT.CENTER;
		okButton.setLayoutData(gridData);
		
		okButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(isConditionCorrect(txtCondition.getText())){
					Connection.SelectedCondition = "";
					Connection.SelectedPID = "";
					Connection.SelectedFacts = null;
					if(!selectedCondition.equals(txtCondition.getText())){
						CMap.addNewCondition(selectedPID, txtCondition.getText());
						Connection.CMapTreeSelectedPID = selectedPID;
						Connection.RefreshView(Application.Views_CMapView_ID);
					}
	
					Connection.HideView(Application.Views_DispConditionView_ID);
				}
				else {
					//Message: -- the condition is wrong
					MessageDialog.openWarning(parent.getShell(), "Warning", "Error in condition.");
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
								
			}
		});
	}

	private boolean isConditionCorrect(String text) {
		//-- Check the correctness of the entered condition
		String sCondition = txtCondition.getText();
		if(!Connection.isConditionValid(selectedFacts, sCondition)){
			return false;
		}
		
		if(!isSymbolsInConditionValid(sCondition)){
			return false;
		}
		
		return true;
	}
	
	private boolean isSymbolsInConditionValid(String sCondition) {
		//Code to check whether the symbols used in condition are right
		
		return true;
	}

	private void updateText(String newValue){
		int cPosition = txtCondition.getCaretPosition();
		if(cPosition>0){
			String oldText = txtCondition.getText();
			String newText = oldText.substring(0, cPosition) + newValue + oldText.substring(cPosition);
			txtCondition.setText(newText);
		}
		else {
			txtCondition.setText(txtCondition.getText() + newValue);
		}
	}
	
	@Override
	public void setFocus() {

	}

}
