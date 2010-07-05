package org.apromore.portal.dialogController;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;

import org.apromore.portal.exception.ExceptionImport;
import org.apromore.portal.exception.ExceptionWriteEditSession;
import org.apromore.portal.manager.RequestToManager;
import org.apromore.portal.model_manager.EditSessionType;
import org.apromore.portal.model_manager.ProcessSummaryType;
import org.apromore.portal.model_manager.VersionSummaryType;
import org.wfmc._2008.xpdl2.PackageHeader;
import org.wfmc._2008.xpdl2.PackageType;
import org.wfmc._2008.xpdl2.RedefinableHeader;
import org.wfmc._2008.xpdl2.Version;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import de.epml.TypeEPML;

public class CreateProcessController {

	private Window createProcessW;

	private MainController mainC ;
	private Button okB;
	private Button cancelB;
	private Button resetB;
	private Radio r0;
	private Radio r1;
	private Radio r2;
	private Radio r3;
	private Radio r4;
	private Radio r5;
	private Textbox processNameT;
	private Textbox versionNameT;
	private Radiogroup rankingRG;
	private Row domainR;
	private Row ownerR;
	private Listbox nativeTypesLB;
	private HashMap<String, String> formats_ext; // <k, v> belongs to nativeTypes: the file extension k
	// is associated with the native type v (<xpdl,XPDL 1.2>)

	private SelectDynamicListController ownerCB;
	private SelectDynamicListController domainCB;

	public CreateProcessController(MainController mainC, HashMap<String, String> formats_ext) 
	throws SuspendNotAllowedException, InterruptedException {
		this.mainC = mainC;
		this.formats_ext = formats_ext;

		Window win = (Window) Executions.createComponents("macros/createprocess.zul", null, null);

		this.createProcessW = (Window) win.getFellow("createprocessW");
		this.okB = (Button) win.getFellow("createprocessOkB");
		this.cancelB = (Button) win.getFellow("createprocessCancelB");
		this.resetB = (Button) win.getFellow("createprocessResetB");
		this.processNameT = (Textbox) win.getFellow("processname");
		this.versionNameT = (Textbox) win.getFellow("versionname");
		this.rankingRG = (Radiogroup) win.getFellow("ranking");
		this.r0 = (Radio) this.rankingRG.getFellow("r0");
		this.r1 = (Radio) this.rankingRG.getFellow("r1");
		this.r2 = (Radio) this.rankingRG.getFellow("r2");
		this.r3 = (Radio) this.rankingRG.getFellow("r3");
		this.r4 = (Radio) this.rankingRG.getFellow("r4");
		this.r5 = (Radio) this.rankingRG.getFellow("r5");
		this.domainR = (Row) win.getFellow("domain");
		this.domainCB = new SelectDynamicListController(this.mainC.getDomains());
		this.domainCB.setReference(this.mainC.getDomains());
		this.domainCB.setId(this.domainR.getId() + "domain");
		this.domainCB.setAutodrop(true);
		this.domainCB.setWidth("85%");
		this.domainCB.setHeight("100%");
		this.domainCB.setAttribute("hflex", "1");
		this.domainR.appendChild(domainCB);
		this.ownerR = (Row) win.getFellow("owner");
		this.ownerCB = new SelectDynamicListController(this.mainC.getUsers());
		this.ownerCB.setReference(this.mainC.getUsers());
		this.ownerCB.setId(this.ownerR.getId() + "owner");
		this.ownerCB.setAutodrop(true);
		this.ownerCB.setWidth("85%");
		this.ownerCB.setHeight("100%");
		this.ownerCB.setAttribute("hflex", "1");
		this.ownerR.appendChild(ownerCB);
		this.nativeTypesLB = (Listbox) win.getFellow("nativetypes");

		Set<String> extensions = this.formats_ext.keySet();
		Iterator<String> it = extensions.iterator();
		while (it.hasNext()){
			Listitem cbi = new Listitem();
			this.nativeTypesLB.appendChild(cbi);
			cbi.setLabel(this.formats_ext.get(it.next()));
		}

		// manage grants... TODO
		this.ownerCB.setValue(this.mainC.getCurrentUser().getUsername());
		this.ownerCB.setDisabled(true);
		
		reset();

		this.okB.addEventListener("onClick",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				createProcess();
			}
		});

		this.createProcessW.addEventListener("onOK",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				createProcess();
			}
		});

		this.cancelB.addEventListener("onClick",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				cancel();
			}
		});	
		this.resetB.addEventListener("onClick",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				reset();
			}
		});	
		win.doModal();
	}

	protected void createProcess() throws Exception  {
		RequestToManager request = new RequestToManager();
		try {
			if (this.processNameT.getValue().compareTo("")==0
					|| this.versionNameT.getValue().compareTo("")==0) {
				throw new ExceptionImport("Please enter a value for each field.");
			} else {
				String domain = this.domainCB.getValue();
				// create an empty model corresponding to the native type
				InputStream nativeProcess = null;
				if (((String) this.nativeTypesLB.getSelectedItem().getValue()).compareTo("XPDL 2.1")==0) {
					PackageType pkg = new PackageType();
					pkg.setName(this.processNameT.getValue());
					PackageHeader hder = new PackageHeader();
					pkg.setPackageHeader(hder);
					RedefinableHeader rhder = new RedefinableHeader();
					pkg.setRedefinableHeader(rhder);
					Version version = new Version();
					rhder.setVersion(version);
					version.setValue(this.versionNameT.getValue());
					JAXBContext jc = JAXBContext.newInstance("org.wfmc._2008.xpdl2");
	                Marshaller m = jc.createMarshaller();
	                m.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
	                JAXBElement<PackageType> rootxpdl = new org.wfmc._2008.xpdl2.ObjectFactory().createPackage(pkg);
	                ByteArrayOutputStream xpdl_xml = new ByteArrayOutputStream();
	                m.marshal(rootxpdl, xpdl_xml);
	                nativeProcess = new ByteArrayInputStream(xpdl_xml.toByteArray());

				} else if (((String) this.nativeTypesLB.getSelectedItem().getValue()).compareTo("EPML 2.0")==0) {
					TypeEPML epml = new TypeEPML();
					JAXBContext jc = JAXBContext.newInstance("de.epml");
	                Marshaller m = jc.createMarshaller();
	                m.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
	                JAXBElement<TypeEPML> rootepml = new de.epml.ObjectFactory().createEpml(epml);
	                ByteArrayOutputStream epml_xml = new ByteArrayOutputStream();
	                m.marshal(rootepml, epml_xml);
	                nativeProcess = new ByteArrayInputStream(epml_xml.toByteArray());
				}
				ProcessSummaryType process = 
					request.importProcess(this.mainC.getCurrentUser().getUsername(), 
							(String) this.nativeTypesLB.getSelectedItem().getValue(), (String)  this.processNameT.getValue(), 
							(String)  this.versionNameT.getValue(), nativeProcess, (String) this.domainCB.getSelectedItem().getValue(),
							null, null, null);

				this.mainC.displayNewProcess(process);
				/* keep list of domains update */
				this.domainCB.addItem(domain);

				/* call editor */
				editProcess(process);
			}
		} catch (WrongValueException e) {
			e.printStackTrace();
			Messagebox.show("Creation failed (" + e.getMessage() + ")", "Attention", Messagebox.OK,
					Messagebox.ERROR);
		} catch (ExceptionImport e) {
			e.printStackTrace();
			Messagebox.show("Creation failed (" + e.getMessage() + ")", "Attention", Messagebox.OK,
					Messagebox.ERROR);
		} catch (IOException e) {
			e.printStackTrace();
			Messagebox.show("Creation failed (" + e.getMessage() + ")", "Attention", Messagebox.OK,
					Messagebox.ERROR);
		} finally {
			closePopup();
		}
		closePopup();
	}

	protected void editProcess(ProcessSummaryType process) throws Exception {

		String instruction="", url=this.mainC.getHost();
		int offsetH = 100, offsetV=200;
		int editSessionCode ;

		Listitem cbi = this.nativeTypesLB.getSelectedItem();
		EditSessionType editSession = new EditSessionType();
		editSession.setDomain(process.getDomain());
		editSession.setNativeType(cbi.getLabel());
		editSession.setProcessId(process.getId());
		editSession.setProcessName(process.getName());
		editSession.setUsername(this.mainC.getCurrentUser().getUsername());
		VersionSummaryType version = process.getVersionSummaries().get(0); // only one version...

		try {
			RequestToManager request = new  RequestToManager();
			editSessionCode = request.WriteEditSession(editSession);
			if (cbi.getLabel().compareTo("XPDL 2.1")==0) {
				url += this.mainC.getOryxEndPoint_xpdl()+"sessionCode=";
			} else if (cbi.getLabel().compareTo("EPML 2.0")==0) {
				url += this.mainC.getOryxEndPoint_epml()+"sessionCode=";
			} else {
				throw new ExceptionWriteEditSession("Native format not supported.");
			}
			url += editSessionCode;
			instruction += "window.open('" + url + "','','top=" + offsetH + ",left=" + offsetV 
			+ ",height=600,width=800,scrollbars=1,resizable=1'); ";
			Clients.evalJavaScript(instruction);	
			cancel();
		} catch (ExceptionWriteEditSession e) {
			Messagebox.show("Cannot edit " + process.getName() + " (" 
					+e.getMessage()+")", "Attention", Messagebox.OK,
					Messagebox.ERROR);
		}
	}
	protected void cancel() throws Exception {
		closePopup();
	}	

	private void closePopup() {
		this.createProcessW.detach();
	}

	protected void reset() {
		String empty = "";
		this.processNameT.setValue(empty);
		this.versionNameT.setValue(empty);
		this.domainCB.setValue(empty);
		r0.setChecked(true);
		r1.setChecked(false);
		r2.setChecked(false);
		r3.setChecked(false);
		r4.setChecked(false);
		r5.setChecked(false);
	}	
}
