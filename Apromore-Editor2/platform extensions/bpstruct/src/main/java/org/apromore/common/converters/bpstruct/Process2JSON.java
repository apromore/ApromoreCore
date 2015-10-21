/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.common.converters.bpstruct;

import de.hpi.bpt.process.*;
import de.hpi.bpt.process.serialize.JSON2Process;
import de.hpi.bpt.process.serialize.SerializationException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Recreated the version in jBPT but added the entry for process at the top level.
 * BPStruct won't work without it.
 *
 * @author Cameron James
 */
public class Process2JSON {

    public static String convert(de.hpi.bpt.process.Process process) throws SerializationException {
        try {
            JSONObject json = new JSONObject();
            json.put("name", process.getName());

            JSONArray tasks = new JSONArray();
            for (Task task : process.getTasks()) {
                JSONObject jTask = new JSONObject();
                jTask.put("id", task.getId());
                jTask.put("label", task.getName());
                tasks.put(jTask);
            }
            json.put("tasks", tasks);

            JSONArray gateways = new JSONArray();
            for (Gateway gate : process.getGateways()) {
                JSONObject jGate = new JSONObject();
                jGate.put("id", gate.getId());
                jGate.put("type", determineGatewayType(gate.getGatewayType()));
                gateways.put(jGate);
            }
            json.put("gateways", gateways);

            JSONArray flows = new JSONArray();
            for (ControlFlow flow : process.getControlFlow()) {
                JSONObject jFlow = new JSONObject();
                jFlow.put("src", flow.getSource().getId());
                jFlow.put("tgt", flow.getTarget().getId());
                if (flow.getLabel() == null)
                    jFlow.put("label", JSONObject.NULL);
                else
                    jFlow.put("label", flow.getLabel());
                flows.put(jFlow);
            }
            json.put("flows", flows);

            JSONObject finaljson = new JSONObject();
            finaljson.put("process", json);

            return finaljson.toString();
        } catch (JSONException e) {
            throw new SerializationException(e.getMessage());
        }
    }

    private static String determineGatewayType(GatewayType type) throws SerializationException {
        if (type == GatewayType.XOR)
            return JSON2Process.XOR;
        if (type == GatewayType.AND)
            return JSON2Process.AND;
        if (type == GatewayType.OR)
            return JSON2Process.OR;
        throw new SerializationException("GatewayType is UNDEFINED.");
    }
}
