package org.apromore.common;

public class Constants {
	public static final String CONTEXT = "java:comp/env/jdbc/ApromoreDB";

	// repository manager details
	public static final String REPOSITORY_URI = "//localhost:8080/Apromore-repositoryManager/";
	public static final String PROTOCOLE = "http:";

	// resources exposed by repository manager
	public static final String READFORMATS = "ReadFormats";
	public static final String READUSER = "ReadUser";
	public static final String WRITEUSER = "WriteUser";
	public static final String READSUMMARIES = "ReadSummaries";
	public static final String READPROCESS = "ReadProcess";

	// JAXB contexts for messages
	public static final String JAXB_CONTEXT_FORMAT = "org.apromore.format";
	public static final String JAXB_CONTEXT_SUMMARIES = "org.apromore.processsummaries";
	public static final String JAXB_CONTEXT_READUSER = "org.apromore.readuser";
	public static final String JAXB_CONTEXT_PROCESS = "org.apromore.process";
	public static final String JAXB_CONTEXT_DOMAIN = "org.apromore.domain";
	public static final String JAXB_CONTEXT_WRITEUSER = "org.apromore.writeuser";
	public static final String JAXB_CONTEXT_MESSAGES = "org.apromore.messages";
	public static final String JAXB_CONTEXT_COMMON = "org.apromore.common";

    public final static String JAXB_CONTEXT_XPDL = "org.wfmc._2008.xpdl2";
	public static final String JAXB_CONTEXT_XPDL21 = "dataAccessServices.facades.xpdl21";
	public static final String JAXB_CONTEXT_ANF = "dataAccessServices.facades.anf01";
	public static final String JAXB_CONTEXT_CPF = "dataAccessServices.facades.cpf02";
	public static final String JAXB_CONTEXT_RLF = "dataAccessServices.facades.rlf01";
	public static final String INITIAL_ANNOTATION = "Initial";
	public static final String ANNOTATIONS = "Annotations";
	public static final String CANONICAL = "Canonical";

	// DA details
	public static final String DA_TOOLBOX_URI = "http://www.apromore.org/dao/service";
	public static final String DA_TOOLBOX_SERVICE = "DAToolboxService";

	// DA details
	public static final String DA_MANAGER_URI = "http://www.apromore.org/dao/service";
	public static final String DA_MANAGER_SERVICE = "DAManagerService";

	// Canoniser details
	public static final String CANONISER_MANAGER_URI = "http://www.apromore.org/canoniser/service" ;
	public static final String CANONISER_MANAGER_SERVICE = "CanoniserManagerService";

	// Toolbox details
	public static final String TOOLBOX_MANAGER_URI = "http://www.apromore.org/toolbox/service" ;
	public static final String TOOLBOX_MANAGER_SERVICE = "ToolboxManagerService";


	// JAXB
	public static final String JAXB_xpdl2Canonical = "org.apromore.messages";
	public static final String JAXB_RESULT = "org.apromore.common";
	public static final String JAXB_READUSER = "org.apromore.readuser";
	public static final String INITIAL_ANNOTATIONS = "Initial";

}
