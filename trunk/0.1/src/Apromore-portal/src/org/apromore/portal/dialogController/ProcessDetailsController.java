package org.apromore.portal.dialogController;


import java.text.ParseException;
import java.util.Date;
import java.util.Vector;

import javax.xml.bind.JAXBException;

import org.apromore.portal.exception.ExceptionDao;
import org.apromore.portal.manager.RequestToManager;
import org.apromore.portal.model_manager.FormatsType;
import org.apromore.portal.model_manager.ProcessSummaryType;
import org.apromore.portal.model_manager.VersionSummaryType;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;


public class ProcessDetailsController extends Window {


	private MainController mainC;					// the main controller
	private ProcessTableController processTableC;	// the controller associated to process summaries
	private Window processDetailsW;					// the view associated to ProcessDetailsController
	// for displaying selected process versions
	private Textbox nameT;							// process name
	private Textbox originalLanguageT;				// process original native language
	private Textbox domainT;						// process domain
	private Textbox versionT;						// version name
	private Textbox rankingT;						// process ranking
	private Datebox creationDateD; 					// version creation date
	private Datebox lastUpdateD;					// version last update
	private Listbox exportL;						// choice of export formats
	private Window graphicW;					// the window where to display process graphical view
	private Menuitem asPopupM;						// button to make previous window as a popup
	private Menuitem backM;							// button to embed the previous windows when it is a popup
	private Image processImage; 					// the actual image to display in the window
	private Label pushToSign;						// the message displayed when no user connected

	private class HistoryElement {
		Object value;
		Integer nb;
		public Object getValue() {
			return value;
		}
		public void setValue(Object value) {
			this.value = value;
		}
		public Integer getNb() {
			return nb;
		}
		public void setNb(Integer nb) {
			this.nb = nb;
		}
	}
	private Vector<HistoryElement> processNameH;
	private Vector<HistoryElement> originalLanguageH;
	private Vector<HistoryElement> domainH;
	private Vector<HistoryElement> versionNameH;
	private Vector<HistoryElement> creationDateH;
	private Vector<HistoryElement> lastUpdateH;
	private Vector<HistoryElement> rankingH;

	public ProcessDetailsController(MainController mainController,
			ProcessTableController processtable) throws JAXBException, ExceptionDao {
		this.mainC = mainController;
		this.processTableC = processtable;
		this.processDetailsW = (Window) mainC.getFellow("processdetailscomp").getFellow("processdetails");

		/**
		 * get the components
		 */
		this.nameT = (Textbox) processDetailsW.getFellow("nameT");
		this.originalLanguageT = (Textbox) processDetailsW.getFellow("originalLanguageT");
		this.domainT = (Textbox) processDetailsW.getFellow("domainT");
		this.versionT = (Textbox) processDetailsW.getFellow("versionT");
		this.rankingT = (Textbox) processDetailsW.getFellow("rankingT");
		this.creationDateD = (Datebox) processDetailsW.getFellow("creationDateD");
		this.lastUpdateD = (Datebox) processDetailsW.getFellow("lastUpdateD");
		this.exportL = (Listbox) processDetailsW.getFellow("exportL");

		this.graphicW = (Window) processDetailsW.getFellow("graphicW");
		//this.asPopupM = (Menuitem) graphicW.getFellow("asPopupM");
		//this.backM = (Menuitem) graphicW.getFellow("backM");
		this.processImage = (Image) graphicW.getFellow("processImage");
		this.pushToSign = (Label) graphicW.getFellow("pushToSign");

		/** 
		 * initialise history of grid attributes values
		 */
		processNameH = new Vector<HistoryElement>();
		originalLanguageH = new Vector<HistoryElement>();
		domainH = new Vector<HistoryElement>();
		versionNameH = new Vector<HistoryElement>();
		creationDateH = new Vector<HistoryElement>();
		lastUpdateH = new Vector<HistoryElement>();
		rankingH = new Vector<HistoryElement>();

		/**
		 * get list of formats to built export option list
		 */
		RequestToManager request = new RequestToManager();
		FormatsType formats = request.ReadFormats();
		for (int i=0;i<formats.getFormat().size();i++) {
			Listitem format = new Listitem();
			format.setLabel(formats.getFormat().get(i).getFormat());
			this.exportL.appendChild(format);
		}

//		asPopupM.addEventListener("onClick",
//				new EventListener() {
//			public void onEvent(Event event) throws Exception {
//				graphicWAsPopup();
//			}
//		});	
//		backM.addEventListener("onClick",
//				new EventListener() {
//			public void onEvent(Event event) throws Exception {
//				graphicWBack();
//			}
//		});	
	}

//	protected void graphicWBack() {
//		graphicW.doEmbedded();
//		backM.setVisible(false);
//		asPopupM.setVisible(true);
//	}
//
//	protected void graphicWAsPopup() {
//		asPopupM.setVisible(false);
//		backM.setVisible(true);
//		graphicW.doPopup();
//	}

	/**
	 * Add to the data displayed in the window processDetailsW those related to version
	 * associated with process
	 * @param process
	 * @param version TODO
	 * @throws JAXBException 
	 * @throws ExceptionDao 
	 * @throws NumberFormatException 
	 * @throws ParseException 
	 */
	
	public void displayProcessVersionDetails(ProcessSummaryType process, VersionSummaryType version, Boolean displayed) 
	throws NumberFormatException, ExceptionDao, JAXBException, ParseException {

		String processName = process.getName();
		String language = process.getOriginalNativeType();
		String domain = process.getDomain();
		String ranking = version.getRanking().toString();
		String creationDate = version.getCreationDate();
		String lastUpdate = version.getLastUpdate();
		String versionName = version.getName();
		/*
		 * for each grid attribute is associated an history whose elements are of the
		 * form of <v,n>: v is a value for n selected process versions
		 * n > 0
		 * for each history, if singleton {<v,n>} then display v in the corresponding
		 * grid attribute otherwise don't display anything
		 */
		if (displayed) {
			insertInHistory (processName, this.processNameH);
			insertInHistory (language, this.originalLanguageH);
			insertInHistory (domain, this.domainH);
			insertInHistory (ranking, this.rankingH);
			insertInHistory (creationDate, this.creationDateH);
			insertInHistory (lastUpdate, this.lastUpdateH);
			insertInHistory (versionName, this.versionNameH);
		} else {
			dropFromHistory (processName, this.processNameH);
			dropFromHistory (language, this.originalLanguageH);
			dropFromHistory (domain, this.domainH);
			dropFromHistory (ranking, this.rankingH);
			dropFromHistory (creationDate, this.creationDateH);
			dropFromHistory (lastUpdate, this.lastUpdateH);
			dropFromHistory (versionName, this.versionNameH);
		}
		
		if (this.processNameH.size() == 1) {
			this.nameT.setValue((String) this.processNameH.get(0).getValue());
		} else {
			this.nameT.setValue("");
		}

		if (this.originalLanguageH.size() == 1) {
			this.originalLanguageT.setValue((String) this.originalLanguageH.get(0).getValue());
		} else {
			this.originalLanguageT.setValue("");
		}
		
		if (domainH.size() == 1) {
			this.domainT.setValue((String) this.domainH.get(0).getValue());
		} else {
			this.domainT.setValue("");
		}
		if (rankingH.size() == 1) {
			this.rankingT.setValue((String) this.rankingH.get(0).getValue());
		} else {
			this.rankingT.setValue("");
		}
		if (this.lastUpdateH.size() == 1) {
			this.lastUpdateD.setValue((Date) this.lastUpdateH.get(0).getValue());
		} else {
			this.lastUpdateD.setValue(null);
		}
		if (this.creationDateH.size() == 1) {
//			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			Date date = format.parse(this.creationDateH.get(0).getValue());
//			this.creationDateD.setValue(date);
			this.creationDateD.setValue((Date) this.creationDateH.get(0).getValue());
		} else {
			this.creationDateD.setValue(null);
		}
		if (this.versionNameH.size() == 1) {
			this.versionT.setValue((String) this.versionNameH.get(0).getValue());
		} else {
			this.versionT.setValue("");
		}
	}

	/** 
	 * insert value into history. history is kept sorted according to v.
	 * for each <v,n> in history n > 0
	 * @param value
	 * @param history
	 */
	private void insertInHistory(Object value, Vector<HistoryElement> history) {
		
		int i = 0;
		while (i<history.size() && !value.equals(history.get(i).getValue())) {
			i++;
		}
		if (i==history.size()){
			// value not found in history and greater than all history values
			HistoryElement el = new HistoryElement();
			el.setValue(value);
			el.setNb(1);
			history.add(el);
		} else {
			if (value.equals(history.get(i).getValue())) {
				// value found, +1 on number
				history.get(i).setNb(1+history.get(i).getNb());
			} else {
				// insert element at i
				HistoryElement el = new HistoryElement();
				el.setValue(value);
				el.setNb(1);
				history.add(i, el);
			}
		}
	}
	
	private void dropFromHistory (Object value, Vector<HistoryElement> history) {
		int i = 0;
		while (i<history.size() && !value.equals(history.get(i).getValue())) {
			i++;
		}
		if (i!=history.size()){
			// value found in history
			if (history.get(i).getNb() > 1){
				history.get(i).setNb(history.get(i).getNb()-1);
			} else {
				history.remove(i);
			}
		} 
	}

	public ProcessTableController getProcessTableC() {
		return processTableC;
	}

	public void setProcessTableC(ProcessTableController processTableC) {
		this.processTableC = processTableC;
	}

	public Window getProcessDetailsW() {
		return processDetailsW;
	}

	public void setProcessDetailsW(Window processDetailsW) {
		this.processDetailsW = processDetailsW;
	}

	public Textbox getNameT() {
		return nameT;
	}

	public void setNameT(Textbox nameT) {
		this.nameT = nameT;
	}

	public Textbox getOriginalLanguageT() {
		return originalLanguageT;
	}

	public void setOriginalLanguageT(Textbox originalLanguageT) {
		this.originalLanguageT = originalLanguageT;
	}

	public Textbox getDomainT() {
		return domainT;
	}

	public void setDomainT(Textbox domainT) {
		this.domainT = domainT;
	}

	public Textbox getRankingT() {
		return rankingT;
	}

	public void setRankingT(Textbox rankingT) {
		this.rankingT = rankingT;
	}

	public Datebox getCreationDateD() {
		return creationDateD;
	}

	public void setCreationDateD(Datebox creationDateD) {
		this.creationDateD = creationDateD;
	}

	public Datebox getLastUpdateD() {
		return lastUpdateD;
	}

	public void setLastUpdateD(Datebox lastUpdateD) {
		this.lastUpdateD = lastUpdateD;
	}

	public Listbox getExportL() {
		return exportL;
	}

	public void setExportL(Listbox exportL) {
		this.exportL = exportL;
	}

	public Window getGraphicW() {
		return graphicW;
	}

	public void setGraphicW(Window graphicW) {
		this.graphicW = graphicW;
	}

	public Image getProcessImage() {
		return processImage;
	}

	public void setProcessImage(Image processImage) {
		this.processImage = processImage;
	}

	public Label getPushToSign() {
		return pushToSign;
	}

	public void setPushToSign(Label pushToSign) {
		this.pushToSign = pushToSign;
	}

	public Vector<HistoryElement> getProcessNameH() {
		return processNameH;
	}

	public void setProcessNameH(Vector<HistoryElement> processNameH) {
		this.processNameH = processNameH;
	}

	public Vector<HistoryElement> getOriginalLanguageH() {
		return originalLanguageH;
	}

	public void setOriginalLanguageH(Vector<HistoryElement> originalLanguageH) {
		this.originalLanguageH = originalLanguageH;
	}

	public Vector<HistoryElement> getDomainH() {
		return domainH;
	}

	public void setDomainH(Vector<HistoryElement> domainH) {
		this.domainH = domainH;
	}

	public Vector<HistoryElement> getCreationDateH() {
		return creationDateH;
	}

	public void setCreationDateH(Vector<HistoryElement> creationDateH) {
		this.creationDateH = creationDateH;
	}

	public Vector<HistoryElement> getLastUpdateH() {
		return lastUpdateH;
	}

	public void setLastUpdateH(Vector<HistoryElement> lastUpdateH) {
		this.lastUpdateH = lastUpdateH;
	}

	public Vector<HistoryElement> getRankingH() {
		return rankingH;
	}

	public void setRankingH(Vector<HistoryElement> rankingH) {
		this.rankingH = rankingH;
	}

	public Textbox getVersionT() {
		return versionT;
	}

	public void setVersionT(Textbox versionT) {
		this.versionT = versionT;
	}

}
