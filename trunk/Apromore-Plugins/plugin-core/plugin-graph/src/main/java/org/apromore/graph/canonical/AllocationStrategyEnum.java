package org.apromore.graph.canonical;

/**
 * Allocation Strategy Enumeration.
 */
public enum AllocationStrategyEnum {

    RANDOM("Random"),
    ROUND_ROBIN_BY_TIME("RoundRobinByTime"),
    ROUND_ROBIN_BY_FREQUENCY("RoundRobinByFrequency"),
    ROUND_ROBIN_BY_EXPERIENCE("RoundRobinByExperience"),
    SHORTEST_QUEUE("ShortestQueue"),
    OTHER("Other");

    private final String value;

    AllocationStrategyEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AllocationStrategyEnum fromValue(String v) {
        for (AllocationStrategyEnum c: AllocationStrategyEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}