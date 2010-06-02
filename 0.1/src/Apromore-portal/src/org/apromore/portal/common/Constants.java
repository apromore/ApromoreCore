package org.apromore.portal.common;

public final class Constants {
	// repository manager details
	public static final String PROPERTY_FILE = "apromore.properties";
	// resources exposed by repository manager
	public static final String READFORMATS = "ReadFormats";
	public static final String READUSER = "ReadUser";
	public static final String WRITEUSER = "WriteUser";
	public static final String READSUMMARIES = "ReadSummaries";
	public static final String READPROCESS = "ReadProcess";
	public static final String READDOMAINS = "ReadDomains";
	public static final String IMPORT = "Import";
	
	public static final String JAXBCONTEXT = "org.apromore.";
	
	
	// max number of searches kept for each user
	public static final Integer maxSearches = 10;
	
	
	public static final String JAXBCONTEXT_DOMAIN = "org.apromore.domain";
	public static final String JAXBCONTEXT_FORMAT = "org.apromore.format";
	public static final String JAXBCONTEXT_PROCESS = "org.apromore.process";
	public static final String JAXBCONTEXT_PROCESSSUMMARIES = "org.apromore.processsummaries";
	public static final String JAXBCONTEXT_READUSER = "org.apromore.readuser";
	public static final String JAXBCONTEXT_WRITEUSER = "org.apromore.writeuser";
	public static final String JAXBCONTEXT_XPDL = "org.wfmc._2008.xpdl2";
	public static final String JAXB_RESULT = "org.apromore.common";
	
	// colors and style used in the table view
	// #E5E5E5 light gray
	// #598DCA blue

	public static final String TOOLBARBUTTON_STYLE = "font-size:12px";
	public static final String UNSELECTED_VERSION = "background-color:#E5E5E5" + ";" + TOOLBARBUTTON_STYLE;
	public static final String SELECTED_VERSION = "background-color:#598DCA" + ";" + TOOLBARBUTTON_STYLE;
	public static final String SELECTED_PROCESS = "background-color:#598DCA" + ";" + TOOLBARBUTTON_STYLE ;
	public static final String UNSELECTED_EVEN = "background-color:#FFFFFF" + ";" + TOOLBARBUTTON_STYLE;
	public static final String UNSELECTED_ODD = "background-color:#F0FAFF" + ";" + TOOLBARBUTTON_STYLE;
}
