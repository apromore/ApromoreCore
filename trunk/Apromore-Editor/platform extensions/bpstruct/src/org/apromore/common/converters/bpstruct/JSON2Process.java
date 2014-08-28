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

import java.util.HashMap;
import java.util.Map;

import de.hpi.bpt.process.ControlFlow;
import de.hpi.bpt.process.Gateway;
import de.hpi.bpt.process.GatewayType;
import de.hpi.bpt.process.Node;
import de.hpi.bpt.process.Process;
import de.hpi.bpt.process.Task;
import de.hpi.bpt.process.serialize.SerializationException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSON2Process {

    public static String XOR = "XOR";
    public static String AND = "AND";
    public static String OR = "OR";

    public static Process convert(String json) throws SerializationException {
        try {
            JSONObject jsonObj = new JSONObject(json);
            return convert(jsonObj.getJSONObject("process"));
        } catch (JSONException e) {
            throw new SerializationException(e.getMessage());
        }
    }

    public static Process convert(JSONObject json) throws SerializationException {
        Process process;
        try {
            process = new Process(json.getString("name"));
            Map<String, Node> nodes = new HashMap<>();
            JSONArray tasks = json.getJSONArray("tasks");
            for (int i = 0; i < tasks.length(); i++) {
                Task task = new Task(tasks.getJSONObject(i).getString("label"));
                task.setId(tasks.getJSONObject(i).getString("id"));
                nodes.put(task.getId(), task);
            }
            JSONArray gateways = json.getJSONArray("gateways");
            for (int i = 0; i < gateways.length(); i++) {
                Gateway gate = new Gateway(determineGatewayType(gateways.getJSONObject(i)));
                gate.setId(gateways.getJSONObject(i).getString("id"));
                nodes.put(gate.getId(), gate);
            }
            process.addVertices(nodes.values());
            JSONArray flows = json.getJSONArray("flows");
            for (int i = 0; i < flows.length(); i++) {
                Node from, to;
                if (nodes.containsKey(flows.getJSONObject(i).getString("src"))) {
                    from = nodes.get(flows.getJSONObject(i).getString("src"));
                } else {
                    throw new SerializationException("Unknown node " + flows.getJSONObject(i).getString("src") + " was referenced by a flow as 'src'.");
                }
                if (nodes.containsKey(flows.getJSONObject(i).getString("tgt"))) {
                    to = nodes.get(flows.getJSONObject(i).getString("tgt"));
                } else {
                    throw new SerializationException("Unknown node " + flows.getJSONObject(i).getString("tgt") + " was referenced by a flow as 'tgt'.");
                }
                ControlFlow flow = process.addControlFlow(from, to);
                flow.setLabel(flows.getJSONObject(i).getString("label"));
            }
        } catch (JSONException e) {
            throw new SerializationException(e.getMessage());
        }
        return process;
    }

    private static GatewayType determineGatewayType(JSONObject obj) throws SerializationException {
        if (obj.has("type")) {
            String type = "";
            try {
                type = obj.getString("type");
            } catch (JSONException e) {
                throw new SerializationException(e.getMessage());
            }
            type = type.toUpperCase();
            if (type.equals(XOR))
                return GatewayType.XOR;
            if (type.equals(AND))
                return GatewayType.AND;
            if (type.equals(OR))
                return GatewayType.OR;
        }
        throw new SerializationException("Couldn't determine GatewayType.");
    }
}
