package org.apromore.portal.common;

public final class Constants {
    // repository manager details
    public static final String PROPERTY_FILE = "apromore.properties";

    // max number of searches kept for users
    public static final Integer maxSearches = 10;

    // colors and style used in the table view
    // #E5E5E5 light gray
    // #ACC6E4 blue

    public static final String TOOLBARBUTTON_STYLE = "font-size:12px";
    public static final String FONT_BOLD = "font-weight:bold";
    public static final String FOLDER = "background-color:#FFFFEE";
    public static final String UNSELECTED_VERSION = "background-color:#E5E5E5" + ";" + TOOLBARBUTTON_STYLE;
    public static final String SELECTED_VERSION = "background-color:#ACC6E4" + ";" + TOOLBARBUTTON_STYLE;
    public static final String SELECTED_PROCESS = "background-color:#ACC6E4" + ";" + TOOLBARBUTTON_STYLE;
    public static final String UNSELECTED_EVEN = "background-color:#FFFFFF" + ";" + TOOLBARBUTTON_STYLE;
    public static final String UNSELECTED_ODD = "background-color:#F0FAFF" + ";" + TOOLBARBUTTON_STYLE;

    public static final String NO_ANNOTATIONS = "-- no annotations --";
    public static final String INITIAL_ANNOTATION = "Initial";
    public static final String ANNOTATIONS = "Annotations";
    public static final String CANONICAL = "Canonical";
    public static final String CANONICAL_EXT = "cpf";

    public static final String RELEASE_NOTES = "http://code.google.com/p/apromore/wiki/ReleaseNotes";
    public static final String MORE_INFO = "http://apromore.org/";

    public static final String MSG_WHEN_CLOSE = "You are about to leave Apromore. You might loose unsaved data.";

    public static final String STAR_FULL_ICON = "/img/selectAll-12.png";
    public static final String STAR_BLK_ICON = "/img/unselectAll-12.png";
    public static final String STAR_MID_ICON = "/img/revertSelection-12.png";
    public static final String SESSION_CODE = "sessionCode";
    public static final String ANNOTATIONS_ONLY = "notationsOnly";

    public static final String INITIAL_VERSION = "0.1";
    public static final String dateFormat = "yyyy/MM/dd hh:mm a";

}
