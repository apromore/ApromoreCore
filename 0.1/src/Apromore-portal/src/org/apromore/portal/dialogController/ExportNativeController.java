package org.apromore.portal.dialogController;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apromore.portal.exception.ExceptionExport;
import org.apromore.portal.manager.RequestToManager;
import org.apromore.portal.model_manager.FormatsType;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Window;
import org.zkoss.zul.api.Row;


public class ExportNativeController extends Window {

	private Window exportNativeW;
	private Grid exportNatG;
	private Rows exportNatRs;
	private Label processNameL;
	private Label versionNameL;
	private Button okB;
	private Button cancelB;
	private Listbox nativeTypesLB;
	private int processId;
	private String versionName;
	private HashMap<String, String> formats_ext;
	
	public ExportNativeController (MenuController menuC, int processId, 
				String processName, String versionName, HashMap<String, String> formats_ext)   {

		this.exportNativeW = (Window) Executions.createComponents("macros/exportnative.zul", null, null);
		this.processId = processId;
		this.versionName = versionName;
		this.formats_ext = formats_ext;
		
		String id = this.processId + " " + this.versionName;
		this.exportNativeW.setId(id);
		this.exportNativeW.setTitle("Export Native (" + processName + ", " + versionName +")");
		this.exportNatG = (Grid) this.exportNativeW.getFirstChild().getFirstChild();
		this.exportNatRs = (Rows) exportNatG.getFirstChild().getNextSibling();
		Row processNameR = (Row) exportNatRs.getFirstChild();
		Row versionNameR = (Row) processNameR.getNextSibling();
		Row nativeTypeR = (Row) versionNameR.getNextSibling();
		Row buttonsR = (Row) nativeTypeR.getNextSibling();

		this.processNameL = (Label) processNameR.getFirstChild().getNextSibling();
		this.processNameL.setValue(processName + " (" + processId + ")");
		this.versionNameL = (Label) versionNameR.getFirstChild().getNextSibling();
		this.versionNameL.setValue(versionName);
		this.nativeTypesLB = (Listbox) nativeTypeR.getFirstChild().getNextSibling();
		this.okB = (Button) buttonsR.getFirstChild().getFirstChild();
		this.cancelB = (Button) buttonsR.getFirstChild().getFirstChild().getNextSibling();
		
		Set<String> extensions = formats_ext.keySet();
		Iterator<String> it = extensions.iterator();
		while (it.hasNext()){
			Listitem cbi = new Listitem();
			this.nativeTypesLB.appendChild(cbi);
			cbi.setLabel(it.next());
		}

		this.nativeTypesLB.addEventListener("onSelect",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				activateOkButton();
			}
		});
		
		this.okB.addEventListener("onClick",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				export();
			}
		});
		this.exportNativeW.addEventListener("onOK",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				export();
			}
		});
		this.cancelB.addEventListener("onClick",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				cancel();
			}
		});	
		
		//this.exportNativeW.doOverlapped();
	}
	
	protected void activateOkButton() {
		this.okB.setDisabled(false);
		//this.nativeTypesLB.removeChild(this.emptynative);
	}
	
	private void cancel() {
		this.exportNativeW.detach();
		//inform the menuC
		
	}
	
	private void export () throws InterruptedException {

		try {
			if (this.nativeTypesLB.getSelectedItem().getLabel().compareTo("")==0) {
				Messagebox.show("Please choose a target native type", "Attention", Messagebox.OK,
						Messagebox.ERROR);
			} else {
				String nativeType = this.nativeTypesLB.getSelectedItem().getLabel();
				String ext = this.formats_ext.get(nativeType);
				if (ext==null) {
					throw new ExceptionExport ("ExportNativeController: Native type " + nativeType
							+ " not supported. \n");
				}
				String filename = URLEncoder.encode(this.processNameL.getValue()+ "." 
						+ this.versionName + "." + ext,"UTF-8") ;
				RequestToManager request = new RequestToManager();
				InputStream native_is =
					request.ExportNative (this.processId, this.versionName, 
					this.nativeTypesLB.getSelectedItem().getLabel());
				Filedownload.save(native_is, "text.xml", filename);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			Messagebox.show("Export failed (" + e.getMessage() + ")", "Attention", Messagebox.OK,
					Messagebox.ERROR);
		} catch (ExceptionExport e) {
			e.printStackTrace();
			Messagebox.show("Export failed (" + e.getMessage() + ")", "Attention", Messagebox.OK,
					Messagebox.ERROR);
		} catch (IOException e) {
			e.printStackTrace();
			Messagebox.show("Export failed (" + e.getMessage() + ")", "Attention", Messagebox.OK,
					Messagebox.ERROR);
		} finally {
			cancel();
		}
	}
}
