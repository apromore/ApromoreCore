package org.apromore.graph.canonical;

/**
 * Non-Human Resource Type Enumeration.
 */
public enum NonHumanTypeEnum {

    SOFTWARE_SYSTEM("SoftwareSystem"), EQUIPMENT("Equipment");

    private final String value;

    NonHumanTypeEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static NonHumanTypeEnum fromValue(String v) {
        for (NonHumanTypeEnum c: NonHumanTypeEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
