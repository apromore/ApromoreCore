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
