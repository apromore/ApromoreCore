/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * Copyright (C) 2013 Felix Mannhardt.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

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