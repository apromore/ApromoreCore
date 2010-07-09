package org.apromore.data_access.commons;


public final class ConstantDB {
	public static final String CONTEXT = "java:comp/env/jdbc/ApromoreDB";

	public static final String TABLE_VERSIONS = "process_versions";
	public static final String ATTR_PROCESSID = "processId";
	public static final String ATTR_VERSION_NAME = "version_name";
	public static final String ATTR_CREATION_DATE = "creation_date";
	public static final String ATTR_LAST_UPDATE = "last_update";
	public static final String ATTR_CANONICAL = "canonical";
	public static final String ATTR_RANKING = "ranking";
	public static final String ATTR_DOCUMENTATION = "documentation";

	public static final String TABLE_PROCESSES = "processes";
	public static final String ATTR_NAME = "name";
	public static final String ATTR_DOMAIN = "domain";
	public static final String ATTR_OWNER = "owner";
	public static final String ATTR_ORIGINAL_TYPE = "original_type";

	public static final String TABLE_NATIVES = "natives";
	public static final String ATTR_URI = "uri";
	public static final String ATTR_CONTENT = "content";
	public static final String ATTR_NAT_TYPE = "nat_type";
	public static final String ATTR_ANNOTATION = "annotation";

	public static final String TABLE_CANONICALS = "canonicals";
	public static final String TABLE_ANNOTATIONS = "annotations";
	
	public static final String TABLE_APFOFCPF = "apfOfCpf";
	public static final String ATTR_APF = "apf";
	public static final String ATTR_CPF = "cpf";
	
	public static final String TABLE_NATIVE_TYPES = "native_types";
	public static final String ATTR_EXTENSION = "extension";
	
	public static final String TABLE_SEARCH_HISTORIES = "search_histories";
	public static final String ATTR_SEARCH = "search";
	public static final String ATTR_NUM = "num" ;

	public static final String TABLE_USERS = "users";
	public static final String ATTR_LASTNAME = "lastname" ;
	public static final String ATTR_FIRSTNAME = "firstname" ;
	public static final String ATTR_EMAIL = "email" ;
	public static final String ATTR_USERNAME = "username" ;
	public static final String ATTR_PASSWD = "passwd" ;	

	public static final String TABLE_EDIT_SESSIONS = "edit_session_mappings";
	public static final String ATTR_CODE = "code";

	public static final String TABLE_DERIVED_VERSIONS = "derived_versions";
	public static final String ATTR_DERIVED_VERSION = "derived_version";
	public static final String ATTR_VERSION = "version";
	public static final String VIEW_PROCESS_RANKING = "process_ranking";

	public static final String VIEW_KEYWORDS = "keywords";
	public static final String ATTR_WORD = "word";
	
	public static final String VIEW_HEAD_VERSIONS = "head_versions";

	public static final String TABLE_TEMP_VERSIONS = "temp_versions";
	public static final String ATTR_RECORD_TIME = "recordTime";
	public static final String ATTR_NPF = "npf";

}


