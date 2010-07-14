package org.apromore.portal.dialogController;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apromore.portal.common.Constants;
import org.apromore.portal.exception.ExceptionExport;
import org.apromore.portal.manager.RequestToManager;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
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
	private Row annotationsR;
	private Listbox annotationsLB;
	private Button okB;
	private Button cancelB;
	private Listbox nativeTypesLB;
	private int processId;
	private String versionName;
	private String originalType;
	private List<String> annotations;	// list of available annotations for this process version
	private HashMap<String, String> formats_ext; // <k, v> belongs to nativeTypes: the file extension k
	// is associated with the native type v (<xpdl,XPDL 1.2>)

	public ExportNativeController (MenuController menuC, int processId, 
			String processName, String originalType, String versionName, 
			List<String> annotations, HashMap<String, String> formats_ext)   {

		this.exportNativeW = (Window) Executions.createComponents("macros/exportnative.zul", null, null);
		this.processId = processId;
		this.versionName = versionName;
		this.originalType = originalType;
		this.formats_ext = formats_ext;
		this.annotations = annotations;
		String id = this.processId + " " + this.versionName;
		this.exportNativeW.setId(id);
		this.exportNativeW.setTitle("Export process model ");
		this.exportNatG = (Grid) this.exportNativeW.getFirstChild().getFirstChild();
		this.exportNatRs = (Rows) exportNatG.getFirstChild().getNextSibling();
		Row processNameR = (Row) exportNatRs.getFirstChild();
		Row versionNameR = (Row) processNameR.getNextSibling();
		Row nativeTypeR = (Row) versionNameR.getNextSibling();
		this.annotationsR = (Row) nativeTypeR.getNextSibling();
		Row buttonsR = (Row) this.annotationsR.getNextSibling();
		this.processNameL = (Label) processNameR.getFirstChild().getNextSibling();
		this.processNameL.setValue(processName);
		this.versionNameL = (Label) versionNameR.getFirstChild().getNextSibling();
		this.versionNameL.setValue(versionName);
		this.annotationsLB = (Listbox) this.annotationsR.getFirstChild().getNextSibling();
		this.nativeTypesLB = (Listbox) nativeTypeR.getFirstChild().getNextSibling();
		this.okB = (Button) buttonsR.getFirstChild().getFirstChild();
		this.cancelB = (Button) buttonsR.getFirstChild().getFirstChild().getNextSibling();

		Set<String> extensions = this.formats_ext.keySet();
		Iterator<String> it = extensions.iterator();
		while (it.hasNext()){
			Listitem cbi = new Listitem();
			this.nativeTypesLB.appendChild(cbi);
			cbi.setLabel(this.formats_ext.get(it.next()));
		}
		this.nativeTypesLB.setSelectedItem((Listitem) this.nativeTypesLB.getFirstChild());

		Listitem cb = new Listitem();
		cb.setLabel(Constants.NO_ANNOTATIONS);
		this.annotationsLB.appendChild(cb);
		for (int i=0; i<this.annotations.size(); i++){
			Listitem cbi = new Listitem();
			this.annotationsLB.appendChild(cbi);
			cbi.setLabel(this.annotations.get(i));
		}
		this.annotationsLB.setSelectedItem((Listitem) this.annotationsLB.getFirstChild());
		
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
	}


	protected void activateOkButton() {
		this.okB.setDisabled(false);
		//this.nativeTypesLB.removeChild(this.emptynative);
	}

	private void cancel() {
		this.exportNativeW.detach();
	}

	private void export() throws InterruptedException {
		try {
			if (this.nativeTypesLB.getSelectedItem().getLabel().compareTo("")==0) {
				Messagebox.show("Please choose a target native type", "Attention", Messagebox.OK,
						Messagebox.ERROR);
			} else {
				String nativeType = this.nativeTypesLB.getSelectedItem().getLabel();
				String ext = null ;
				Set<String> keys = this.formats_ext.keySet();
				Iterator<String> it = keys.iterator();
				while (it.hasNext()) {
					String k = it.next();
					if (this.formats_ext.get(k).compareTo(nativeType)==0) {
						ext = k;
						break;
					}
				}
				if (ext==null) {
					throw new ExceptionExport ("Native type " + nativeType
							+ " not supported. \n");
				}
				if (nativeType.compareTo(this.originalType)==0) {
					this.annotationsR.setVisible(true);
				}
				String processname = this.processNameL.getValue().replaceAll(" ", "_");
				String filename = processname + "." + ext;
				String annotation = this.annotationsLB.getSelectedItem().getLabel();
				Boolean withAnnotation = (this.annotationsLB.getSelectedItem().getLabel().compareTo(Constants.NO_ANNOTATIONS)!=0);
				RequestToManager request = new RequestToManager();
				InputStream native_is =
					request.ExportNative (this.processId, this.versionName, nativeType, annotation, withAnnotation);
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
