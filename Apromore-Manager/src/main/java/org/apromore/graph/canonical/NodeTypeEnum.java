package org.apromore.graph.canonical;

/**
 * Enumeration of the Type opf Node we could have.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public enum NodeTypeEnum {

    WORK("Work"), EVENT("Event"), TASK("Task"), MESSAGE("Message"), TIMER("Timer"),
    ROUTING("Routing"), JOIN("Join"), SPLIT("Split"), STATE("State"),
    ANDSPLIT("AndSplit"), ORSPLIT("OrSplit"), XORSPLIT("XOrSplit"),
    ANDJOIN("AndJoin"), ORJOIN("OrJoin"), XORJOIN("XOrJoin");

    private final String value;

    NodeTypeEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static NodeTypeEnum fromValue(String v) {
        for (NodeTypeEnum c: NodeTypeEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
