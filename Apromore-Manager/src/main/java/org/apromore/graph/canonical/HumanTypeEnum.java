package org.apromore.graph.canonical;

/**
 * Human Resource Type Enumeration.
 */
public enum HumanTypeEnum {

    ORGANISATION("Organisation"), DEPARTMENT("Department"), UNIT("Unit"),
    TEAM("Team"), GROUP("Group"), ROLE("Role"), PARTICIPANT("Participant");

    private final String value;

    HumanTypeEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static HumanTypeEnum fromValue(String v) {
        for (HumanTypeEnum c: HumanTypeEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
