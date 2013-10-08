package org.apromore.service.helper;

/**
 * List of methods that are used to help with getting names and details of annotations.
 *
 * @author Cameron James
 */
public class AnnotationHelper {

    private static final String ANNOTATION_START = "Annotations - ";


    /**
     * Private Constructor as this is a helper class.
     */
    private AnnotationHelper() {}


    /**
     * Takes an annotation name and extracts the real name, some annotations have "Annotation - " at the start
     * but most are stored in the db without this text.
     * @param originalName the name that is usually sent from the UI portal.
     * @return the name that is used for storage in the DB.
     */
    public static String getAnnotationName(String originalName) {
        String result;
        if (originalName.startsWith(ANNOTATION_START)) {
            result = originalName.replaceFirst(ANNOTATION_START, "");
        } else {
            result = originalName;
        }
        return result;
    }
}
