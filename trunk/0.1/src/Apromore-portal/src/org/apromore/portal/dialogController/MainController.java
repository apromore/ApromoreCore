package org.apromore.portal.dialogController;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Properties;

import org.apromore.portal.common.Constants;
import org.apromore.portal.manager.RequestToManager;
import org.apromore.portal.model_manager.ProcessSummariesType;
import org.apromore.portal.model_manager.UserType;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;



/*
 * Controller for the window index.zul
 * 
 */
public class MainController extends Window {

	private Window mainW;
	private HeaderController header;
	private MenuController menu;
	private ProcessTableController processtable;
	private NavigationController navigation;
	private SimpleSearchController simplesearch;
	private RefinedSearchController refinedsearch;
	private UserType currentUser ;				// the connected user, if any
	private ShortMessageController shortmessageC;
	private Window shortmessageW;
	private String OryxEndPoint_xpdl;
	private String OryxEndPoint_epml;
	/**
	 * onCreate is executed after the main window has been created
	 * it is responsible for instantiating all necessary controllers
	 * (one for each window defined in the interface)
	 * 
	 * see description in index.zul
	 * @throws InterruptedException 
	 */
	public void onCreate() throws InterruptedException {		
		try {
			/**
			 * to get data
			 */
			this.mainW = (Window) this.getFellow("mainW");
			this.shortmessageW = (Window) this.getFellow("shortmessagescomp").getFellow("shortmessage");
			this.shortmessageC = new ShortMessageController(shortmessageW);
			this.processtable = new ProcessTableController(this);
			this.header = new HeaderController (this);
			this.navigation = new NavigationController (this);
			this.simplesearch = new SimpleSearchController(this);
			this.menu = new MenuController(this);
            
			// read Oryx access point in properties
			InputStream inputStream = getClass().getClassLoader().getResourceAsStream(Constants.PROPERTY_FILE);;  
			Properties properties = new Properties();  
			properties.load(inputStream);  
			
			this.OryxEndPoint_xpdl = properties.getProperty("OryxEndPoint_xpdl");  
			this.OryxEndPoint_epml = properties.getProperty("OryxEndPoint_epml");  
			
			// Listens to event onSize on the main window. Readjusts accordingly the table view window size
			this.mainW.addEventListener("onSize", new EventListener() {
				public void onEvent(Event event) throws Exception {
					watchesSize();
				}
			});
			
		} catch (Exception e) {
			Messagebox.show("Repository not available ("+e.getMessage()+")", "Attention", Messagebox.OK,
					Messagebox.ERROR);
		}
	}

	protected void watchesSize() {

		System.out.println("Current size is: " + this.mainW.getAttribute("size"));
		
	}

	public void displayProcessSummaries(ProcessSummariesType processSummaries) throws Exception {
		this.processtable.emptyProcessSummaries();
		this.processtable.displayProcessSummaries(processSummaries);
	}

	public void updateActions (){

		Boolean connected = this.currentUser != null ;

		this.simplesearch.getPrevioussearchesCB().setVisible(connected);
		this.simplesearch.getSimplesearchesBu().setVisible(connected);
		// disable/enable menu items in menu bar
		Iterator<Component> itC = this.menu.getMenuB().getFellows().iterator();
		while (itC.hasNext()) {
			Component C = itC.next();
			if (C.getClass().getName().compareTo("org.zkoss.zul.Menuitem")==0) {
				((Menuitem) C).setDisabled(!connected);
			}
		}
	}

	public void refreshProcessSummaries () throws Exception  {
		RequestToManager request = new RequestToManager();
		ProcessSummariesType processSummaries = request.ReadProcessSummariesType("");
		this.displayMessage(processSummaries.getProcessSummary().size() + " processes.");
		this.displayProcessSummaries(processSummaries);
	}
	/**
	 * reset displayed informations:
	 * - short message
	 * - process summaries
	 * - simple search
	 * @throws Exception 
	 */
	public void resetUserInformation() throws Exception {
		eraseMessage();
		this.currentUser = null;
		this.simplesearch.clearSearches();
		
		this.refreshProcessSummaries();
	}
	public void displayMessage (String mes) {
		this.shortmessageC.displayMessage(mes);
	}

	public void eraseMessage () {
		this.shortmessageC.eraseMessage();
	}
	

	public HeaderController getHeader() {
		return header;
	}

	public void setHeader(HeaderController header) {
		this.header = header;
	}

	public MenuController getMenu() {
		return menu;
	}

	public void setMenu(MenuController menu) {
		this.menu = menu;
	}

	public ProcessTableController getProcesstable() {
		return processtable;
	}

	public void setProcesstable(ProcessTableController processtable) {
		this.processtable = processtable;
	}


	public NavigationController getNavigation() {
		return navigation;
	}

	public void setNavigation(NavigationController navigation) {
		this.navigation = navigation;
	}

	public SimpleSearchController getSimplesearch() {
		return simplesearch;
	}

	public void setSimplesearch(SimpleSearchController simplesearch) {
		this.simplesearch = simplesearch;
	}

	public RefinedSearchController getRefinedsearch() {
		return refinedsearch;
	}

	public void setRefinedsearch(RefinedSearchController refinedsearch) {
		this.refinedsearch = refinedsearch;
	}

	public UserType getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(UserType currentUser) {
		this.currentUser = currentUser;
	}

	public ShortMessageController getShortmessageC() {
		return shortmessageC;
	}

	public void setShortmessageC(ShortMessageController shortmessageC) {
		this.shortmessageC = shortmessageC;
	}

	public Window getShortmessageW() {
		return shortmessageW;
	}

	public void setShortmessageW(Window shortmessageW) {
		this.shortmessageW = shortmessageW;
	}

	public String getOryxEndPoint_xpdl() {
		return OryxEndPoint_xpdl;
	}

	public String getOryxEndPoint_epml() {
		return OryxEndPoint_epml;
	}
	
}
