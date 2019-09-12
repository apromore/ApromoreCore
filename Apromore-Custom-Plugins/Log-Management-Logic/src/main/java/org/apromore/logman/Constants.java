package org.apromore.logman;

public class Constants {
    public static final String LIFECYCLE_START = "start";
    public static final String LIFECYCLE_COMPLETE = "complete";
    public static final String PLUS_COMPLETE_CODE = "+complete";
    public static final String PLUS_START_CODE = "+start";
    public static final String CONCEPT_NAME = "concept:name";
    public static final String TIMESTAMP_KEY = "time:timestamp";
    
	public final static String START_NAME = "|>"; //marker for the start event in a trace in simplifiedNameMap
    public final static String END_NAME = "[]"; //marker for the end event in a trace in simplifiedNameMap
    public final static int START_INT = 1; // marker for the start event in a trace in the simplified log (index-based)
    public final static int END_INT = 2; // marker for the end event in a trace in the simplified log
}
