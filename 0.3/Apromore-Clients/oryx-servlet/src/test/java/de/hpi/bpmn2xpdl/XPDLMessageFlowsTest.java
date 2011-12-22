package de.hpi.bpmn2xpdl;

import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Xmappr;

public class XPDLMessageFlowsTest extends TestCase {
	private String json = "{\"messageflowsunknowns\":\"rO0ABXNyACVkZS5ocGkuYnBtbjJ4cGRsLlhNTFVua25vd25zQ29udGFp" +
            "bmVyAAAAAAAAAAECAAJMABF1bmtub3duQXR0cmlidXRlc3QAE0xqYXZhL3V0aWwvSGFzaE1hcDtMAA91bmtub3duRWxlbWVudH" +
            "N0ABVMamF2YS91dGlsL0FycmF5TGlzdDt4cHNyABFqYXZhLnV0aWwuSGFzaE1hcAUH2sHDFmDRAwACRgAKbG9hZEZhY3RvckkA" +
            "CXRocmVzaG9sZHhwP0AAAAAAAAx3CAAAABAAAAAAeHNyABNqYXZhLnV0aWwuQXJyYXlMaXN0eIHSHZnHYZ0DAAFJAARzaXpleH" +
            "AAAAABdwQAAAAKc3IAFW9yZy54bWFwcHIuRG9tRWxlbWVudImen1gfMVQJAgADTAAKYXR0cmlidXRlc3EAfgABTAAIZWxlbWVu" +
            "dHN0ABBMamF2YS91dGlsL0xpc3Q7TAAEbmFtZXQAG0xqYXZheC94bWwvbmFtZXNwYWNlL1FOYW1lO3hwc3EAfgAEP0AAAAAAAA" +
            "x3CAAAABAAAAAAeHNxAH4ABgAAAAB3BAAAAAp4c3IAGWphdmF4LnhtbC5uYW1lc3BhY2UuUU5hbWWBbagt/DvdbAIAA0wACWxv" +
            "Y2FsUGFydHQAEkxqYXZhL2xhbmcvU3RyaW5nO0wADG5hbWVzcGFjZVVSSXEAfgAPTAAGcHJlZml4cQB+AA94cHQADFVua25vd2" +
            "5DaGlsZHQAAHEAfgASeA==\"}";
	private String xpdl = "<MessageFlows><UnknownChild/></MessageFlows>";

	public void testParse() throws JSONException {
		XPDLMessageFlows messageFlows = new XPDLMessageFlows();
		messageFlows.parse(new JSONObject(json));

		StringWriter writer = new StringWriter();

		Xmappr xmappr = new Xmappr(XPDLMessageFlows.class);
		xmappr.setPrettyPrint(false);
		xmappr.toXML(messageFlows, writer);

		assertEquals(xpdl, writer.toString());
	}

	public void testWrite() {		
		StringReader reader = new StringReader(xpdl);

		Xmappr xmappr = new Xmappr(XPDLMessageFlows.class);
		XPDLMessageFlows messageFlows = (XPDLMessageFlows) xmappr.fromXML(reader);

		JSONObject importObject = new JSONObject();
		messageFlows.write(importObject);

		assertEquals(json, importObject.toString());
	}
}
