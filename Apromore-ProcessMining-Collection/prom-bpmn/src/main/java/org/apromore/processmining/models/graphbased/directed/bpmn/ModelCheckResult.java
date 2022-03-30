/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.processmining.models.graphbased.directed.bpmn;

import java.util.List;
import java.util.Map;
import lombok.NonNull;

public class ModelCheckResult {
    public static final int DISCONNECTED_ARC_CODE = 1;
    public static final int EMPTY_MODEL_CODE = 2;
    public static final int POOLS_NOT_SUPPORTED_CODE = 3;
    public static final int SELF_LOOP_CODE = 4;
    public static final int TASK_MULTIPLE_OUTGOING_ARCS_CODE = 5;
    public static final int TASK_MULTIPLE_INCOMING_ARCS_CODE = 6;
    public static final int TASK_MISSING_ARCS_CODE = 7;
    public static final int GATE_MISSING_ARCS_CODE = 8;
    public static final int START_MULTIPLE_OUTGOING_ARCS_CODE = 9;
    public static final int START_NO_OUTGOING_ARC_CODE = 10;
    public static final int START_INCOMING_ARCS_CODE = 11;
    public static final int END_MULTIPLE_INCOMING_ARCS_CODE = 12;
    public static final int END_NO_INCOMING_ARC_CODE = 13;
    public static final int END_OUTGOING_ARCS_CODE = 14;

    private static final String DISCONNECTED_ARC_MSG = "The model has disconnected arcs";
    private static final String EMPTY_MODEL_MSG = "The model is empty";
    private static final String POOLS_NOT_SUPPORTED_MSG = "There are pools in the model. Pools are not yet supported";
    private static final String SELF_LOOP_MSG_FORMAT = "The element %s has a self-loop";
    private static final String TASK_MULTIPLE_OUTGOING_ARCS_MSG_FORMAT = "The task %s has more than one outgoing arc";
    private static final String TASK_MULTIPLE_INCOMING_ARCS_MSG_FORMAT = "The task %s has more than one incoming arc";
    private static final String TASK_MISSING_ARCS_MSG_FORMAT = "The task %s has missing incoming or outgoing arcs";
    private static final String GATE_MISSING_ARCS_MSG_FORMAT = "The gateway %s has missing incoming or outgoing arcs";
    private static final String START_MULTIPLE_OUTGOING_ARCS_MSG = "The Start Event has more than one outgoing arc";
    private static final String START_NO_OUTGOING_ARC_MSG = "The Start Event has a missing outgoing arc";
    private static final String START_INCOMING_ARCS_MSG = "The Start Event has incoming arc(s)";
    private static final String END_MULTIPLE_INCOMING_ARCS_MSG = "The End Event has more than one incoming arc";
    private static final String END_NO_INCOMING_ARC_MSG = "The End Event has a missing incoming arc";
    private static final String END_OUTGOING_ARCS_MSG = "The End Event has outgoing arc(s)";

    private static final Map<Integer, String> ERROR_MAP = Map.ofEntries(
        Map.entry(DISCONNECTED_ARC_CODE, DISCONNECTED_ARC_MSG),
        Map.entry(EMPTY_MODEL_CODE, EMPTY_MODEL_MSG),
        Map.entry(POOLS_NOT_SUPPORTED_CODE, POOLS_NOT_SUPPORTED_MSG),
        Map.entry(SELF_LOOP_CODE, SELF_LOOP_MSG_FORMAT),
        Map.entry(TASK_MULTIPLE_OUTGOING_ARCS_CODE, TASK_MULTIPLE_OUTGOING_ARCS_MSG_FORMAT),
        Map.entry(TASK_MULTIPLE_INCOMING_ARCS_CODE, TASK_MULTIPLE_INCOMING_ARCS_MSG_FORMAT),
        Map.entry(TASK_MISSING_ARCS_CODE, TASK_MISSING_ARCS_MSG_FORMAT),
        Map.entry(GATE_MISSING_ARCS_CODE, GATE_MISSING_ARCS_MSG_FORMAT),
        Map.entry(START_MULTIPLE_OUTGOING_ARCS_CODE, START_MULTIPLE_OUTGOING_ARCS_MSG),
        Map.entry(START_NO_OUTGOING_ARC_CODE, START_NO_OUTGOING_ARC_MSG),
        Map.entry(START_INCOMING_ARCS_CODE, START_INCOMING_ARCS_MSG),
        Map.entry(END_MULTIPLE_INCOMING_ARCS_CODE, END_MULTIPLE_INCOMING_ARCS_MSG),
        Map.entry(END_NO_INCOMING_ARC_CODE, END_NO_INCOMING_ARC_MSG),
        Map.entry(END_OUTGOING_ARCS_CODE, END_OUTGOING_ARCS_MSG)
    );

    private Map<Integer, List<String>> errors;

    public ModelCheckResult(@NonNull Map<Integer, List<String>> errorMap) {
        this.errors = errorMap;
    }

    public boolean isValid() {
        return errors == null || errors.isEmpty();
    }

    public String getFormattedMessage() {
        StringBuilder errorMsgBuilder = new StringBuilder();
        for (Map.Entry<Integer, List<String>> entry : errors.entrySet()) {
            String msgFormat = ERROR_MAP.get(entry.getKey());
            for (String element : entry.getValue()) {
                errorMsgBuilder.append(String.format(msgFormat, element));
                errorMsgBuilder.append(System.getProperty("line.separator"));
            }
        }
        return errorMsgBuilder.toString();
    }

    public boolean contains(int errorCode) {
        return errors.containsKey(errorCode);
    }
}
