package org.apromore.apmlog.util;

public class AttributeKeyTranslator {

    public static String translate(String xesAttributeKey) {
        switch (xesAttributeKey) {
            case "concept:name": return "Activity";
            case "org:resource": return "Resource";
            case "org:group": return "Group";
            case "org:role": return "Role";
            case "lifecycle:transition": return "Status";
            default: return xesAttributeKey;
        }
    }
}
