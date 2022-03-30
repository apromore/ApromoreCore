/*-
 * #%L
 * This file is part of "Apromore Enterprise Edition".
 * %%
 * Copyright (C) 2019 - 2022 Apromore Pty Ltd. All Rights Reserved.
 * %%
 * NOTICE:  All information contained herein is, and remains the
 * property of Apromore Pty Ltd and its suppliers, if any.
 * The intellectual and technical concepts contained herein are
 * proprietary to Apromore Pty Ltd and its suppliers and may
 * be covered by U.S. and Foreign Patents, patents in process,
 * and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this
 * material is strictly forbidden unless prior written permission
 * is obtained from Apromore Pty Ltd.
 * #L%
 */
package org.apromore.processmining.models.graphbased.directed.bpmn;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <b>LocalIDGenerator</b> is used to generate sequential IDs. These IDs are only unique within one instance of
 * LocalIDGenerator, as such they are only used as local IDs, e.g. IDs for elements within one BPMN Diagram, but they are
 * not unique across two BPMN diagrams.
 *
 * @author Bruce Nguyen
 */
public class LocalIDGenerator {
    public static final String ID_PREFIX_NODE = "node";
    public static final String ID_PREFIX_EDGE = "edge";

    private final Map<String, Integer> idPrefixCounters = new HashMap<>() {{
        put(ID_PREFIX_NODE, 0);
        put(ID_PREFIX_EDGE, 0);
    }};

    public String nextId(String elementType) {
        if (!idPrefixCounters.containsKey(elementType)) throw new IllegalArgumentException("Unsupported element type in generating ID: "
                + elementType);
        int elementTypeCounter = idPrefixCounters.get(elementType);
        idPrefixCounters.put(elementType, elementTypeCounter + 1);
        return elementType + elementTypeCounter;
    }

    public boolean isValidId(String id) {
        for (String prefix : idPrefixCounters.keySet()) {
            if (id.startsWith(prefix)) {
                String suffix = id.substring(prefix.length());
                return Pattern.matches("[0-9]+", suffix);
            }
        }
        return false;
    }
}
