package org.apromore.common;

public final class ConstantDB {

	public static final String TABLE_CANONICALS = "canonical";
	public static final String TABLE_ANNOTATIONS = "annotation";

	public static final String ATTR_NATIVE = "native";
	public static final String ATTR_PROCESSID = "processId";
	public static final String ATTR_VERSION_NAME = "version_name";
	public static final String ATTR_CREATION_DATE = "creation_date";
	public static final String ATTR_LAST_UPDATE = "last_update";
	public static final String ATTR_CANONICAL = "canonical";
	public static final String ATTR_RANKING = "ranking";
	public static final String ATTR_DOCUMENTATION = "documentation";

	public static final String TABLE_PROCESSES = "process";
	public static final String ATTR_NAME = "name";
	public static final String ATTR_DOMAIN = "domain";
	public static final String ATTR_OWNER = "owner";
	public static final String ATTR_ORIGINAL_TYPE = "original_type";

	public static final String TABLE_NATIVES = "native";
	public static final String ATTR_URI = "uri";
	public static final String ATTR_CONTENT = "content";
	public static final String ATTR_NAT_TYPE = "nat_type";
	public static final String ATTR_ANNOTATION = "annotation";

	public static final String ATTR_USERNAME = "username" ;

	public static final String TABLE_EDIT_SESSIONS = "edit_session_mapping";
	public static final String ATTR_CODE = "code";

	public static final String TABLE_DERIVED_VERSIONS = "derived_version";
	public static final String VIEW_PROCESS_RANKING = "process_ranking";

	public static final String TABLE_TEMP_VERSIONS = "temp_version";
	public static final String ATTR_RECORD_TIME = "recordTime";
	public static final String ATTR_NPF = "npf";
	public static final String ATTR_CPF = "cpf";
	public static final String ATTR_APF = "apf";
	public static final String ATTR_PRE_VERSION_NAME = "pre_version";
	public static final String ATTR_NEW_VERSION_NAME = "new_version";

	public static final String TABLE_MERGED_VERSIONS = " merged_version";
	public static final String ATTR_URI_MERGED = "uri_merged";
	public static final String ATTR_URI_SOURCE = "uri_source";
	

	public static final int ERROR_UNIQUE = 1062;
}


