package org.apromore.graph.canonical;

/**
 * Enumeration of the Type opf Node we could have.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public enum ObjectTypeEnum {

    HARD("hard"), SOFT("soft");

    private final String value;

    ObjectTypeEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ObjectTypeEnum fromValue(String v) {
        for (ObjectTypeEnum c: ObjectTypeEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
