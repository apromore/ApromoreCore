package org.apromore.data_access.commons;

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

		public static final String JAXB_CONTEXT_XPDL21 = "dataAccessServices.facades.xpdl21";
		public static final String JAXB_CONTEXT_ANF = "dataAccessServices.facades.anf01";
		public static final String JAXB_CONTEXT_CPF = "dataAccessServices.facades.cpf02";
		public static final String JAXB_CONTEXT_RLF = "dataAccessServices.facades.rlf01";

	}