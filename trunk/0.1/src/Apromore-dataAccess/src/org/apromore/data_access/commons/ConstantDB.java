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

	public static final String TABLE_PROCESSES = "processes";
	public static final String ATTR_NAME = "name";
	public static final String ATTR_DOMAIN = "domain";
	public static final String ATTR_OWNER = "owner";
	public static final String ATTR_ORIGINAL_TYPE = "original_type";

	public static final String TABLE_NATIVES = "natives";
	public static final String ATTR_URI = "uri";
	public static final String ATTR_CONTENT = "content";
	public static final String ATTR_NAT_TYPE = "nat_type";
	public static final String ATTR_RELATION = "relation";

	public static final String TABLE_CANONICALS = "canonicals";

	public static final String TABLE_RELATIONS = "relations";
	public static final String TABLE_ANNOTATIONS = "annotations";
	
	public static final String TABLE_NATIVE_TYPES = "native_types";

	public static final String TABLE_SEARCH_HISTORIES = "search_histories";
	public static final String ATTR_SEARCH = "search";
	public static final String ATTR_NUM = "num" ;

	public static final String TABLE_USERS = "users";
	public static final String ATTR_USERID = "userId" ;
	public static final String ATTR_LASTNAME = "lastname" ;
	public static final String ATTR_FIRSTNAME = "firstname" ;
	public static final String ATTR_EMAIL = "email" ;
	public static final String ATTR_USERNAME = "username" ;
	public static final String ATTR_PASSWD = "passwd" ;	

	public static final String VIEW_PROCESS_RANKING = "process_ranking";

	public static final String VIEW_KEYWORDS = "keywords";
	public static final String ATTR_WORD = "word";

}


