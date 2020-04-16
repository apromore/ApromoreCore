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
 * Enumeration of the Type opf Node we could have.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public enum NodeTypeEnum {

    WORK("Work"), EVENT("Event"), TASK("Task"), MESSAGE("Message"), TIMER("Timer"),
    ROUTING("Routing"), JOIN("Join"), SPLIT("Split"), STATE("State"),
    ANDSPLIT("AndSplit"), ORSPLIT("OrSplit"), XORSPLIT("XOrSplit"),
    ANDJOIN("AndJoin"), ORJOIN("OrJoin"), XORJOIN("XOrJoin"),
    POCKET("Pocket");

    private final String value;

    NodeTypeEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static NodeTypeEnum fromValue(String v) {
        for (NodeTypeEnum c: NodeTypeEnum.values()) {
            if (c.value().equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

    public static NodeTypeEnum fromName(String v) {
        for (NodeTypeEnum c: NodeTypeEnum.values()) {
            if (c.name().equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
