package org.apromore.graph.canonical;

/**
 * Enumeration of the Type of Resource we could have.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public enum ResourceTypeEnum {

    HUMAN("human"), NONHUMAN("nonHuman");

    private final String value;

    ResourceTypeEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ResourceTypeEnum fromValue(String v) {
        for (ResourceTypeEnum c: ResourceTypeEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
