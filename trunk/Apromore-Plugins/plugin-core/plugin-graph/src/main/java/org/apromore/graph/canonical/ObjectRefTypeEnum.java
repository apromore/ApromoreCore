package org.apromore.graph.canonical;

/**
 * Enumeration of the Object Reference Type we could have.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public enum ObjectRefTypeEnum {

    INPUT("input"), OUTPUT("output");

    private final String value;

    ObjectRefTypeEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ObjectRefTypeEnum fromValue(String v) {
        for (ObjectRefTypeEnum c: ObjectRefTypeEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
