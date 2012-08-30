package de.hpi.bpmn2xpdl;

import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Xmappr;

public class XPDLWorkflowProcessesTest extends TestCase {

	private String json = "{\"workflowprocessesunknowns\":\"rO0ABXNyACVkZS5ocGkuYnBtbjJ4cGRsLlhNTFVua25vd25zQ29ud" +
            "GFpbmVyAAAAAAAAAAECAAJMABF1bmtub3duQXR0cmlidXRlc3QAE0xqYXZhL3V0aWwvSGFzaE1hcDtMAA91bmtub3duRWxlbWVud" +
            "HN0ABVMamF2YS91dGlsL0FycmF5TGlzdDt4cHNyABFqYXZhLnV0aWwuSGFzaE1hcAUH2sHDFmDRAwACRgAKbG9hZEZhY3RvckkAC" +
            "XRocmVzaG9sZHhwP0AAAAAAAAx3CAAAABAAAAAAeHNyABNqYXZhLnV0aWwuQXJyYXlMaXN0eIHSHZnHYZ0DAAFJAARzaXpleHAAA" +
            "AABdwQAAAAKc3IAFW9yZy54bWFwcHIuRG9tRWxlbWVudImen1gfMVQJAgADTAAKYXR0cmlidXRlc3EAfgABTAAIZWxlbWVudHN0A" +
            "BBMamF2YS91dGlsL0xpc3Q7TAAEbmFtZXQAG0xqYXZheC94bWwvbmFtZXNwYWNlL1FOYW1lO3hwc3EAfgAEP0AAAAAAAAx3CAAAA" +
            "BAAAAAAeHNxAH4ABgAAAAB3BAAAAAp4c3IAGWphdmF4LnhtbC5uYW1lc3BhY2UuUU5hbWWBbagt/DvdbAIAA0wACWxvY2FsUGFyd" +
            "HQAEkxqYXZhL2xhbmcvU3RyaW5nO0wADG5hbWVzcGFjZVVSSXEAfgAPTAAGcHJlZml4cQB+AA94cHQADFVua25vd25DaGlsZHQAA" +
            "HEAfgASeA==\"}";
	private String xpdl = "<WorkflowProcesses><UnknownChild/></WorkflowProcesses>";
	
	public void testParse() throws JSONException {
		XPDLWorkflowProcesses processes = new XPDLWorkflowProcesses();
		processes.parse(new JSONObject(json));
	
		StringWriter writer = new StringWriter();
	
		Xmappr xmappr = new Xmappr(XPDLWorkflowProcesses.class);
		xmappr.setPrettyPrint(false);
		xmappr.toXML(processes, writer);
	
		assertEquals(xpdl, writer.toString());
	}

	public void testWrite() {		
		StringReader reader = new StringReader(xpdl);

		Xmappr xmappr = new Xmappr(XPDLWorkflowProcesses.class);
		XPDLWorkflowProcesses processes = (XPDLWorkflowProcesses) xmappr.fromXML(reader);

		JSONObject importObject = new JSONObject();
		processes.write(importObject);

		assertEquals(json, importObject.toString());
	}
}
