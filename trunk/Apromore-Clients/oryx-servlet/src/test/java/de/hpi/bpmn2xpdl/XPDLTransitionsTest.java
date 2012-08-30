package de.hpi.bpmn2xpdl;

import java.io.StringReader;
import java.io.StringWriter;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Xmappr;

import junit.framework.TestCase;

public class XPDLTransitionsTest extends TestCase {

	private String json = "{\"transitionsunknowns\":\"rO0ABXNyACVkZS5ocGkuYnBtbjJ4cGRsLlhNTFVua25vd25zQ29ud" +
            "GFpbmVyAAAAAAAAAAECAAJMABF1bmtub3duQXR0cmlidXRlc3QAE0xqYXZhL3V0aWwvSGFzaE1hcDtMAA91bmtub3duRWx" +
            "lbWVudHN0ABVMamF2YS91dGlsL0FycmF5TGlzdDt4cHNyABFqYXZhLnV0aWwuSGFzaE1hcAUH2sHDFmDRAwACRgAKbG9hZ" +
            "EZhY3RvckkACXRocmVzaG9sZHhwP0AAAAAAAAx3CAAAABAAAAAAeHNyABNqYXZhLnV0aWwuQXJyYXlMaXN0eIHSHZnHYZ0" +
            "DAAFJAARzaXpleHAAAAABdwQAAAAKc3IAFW9yZy54bWFwcHIuRG9tRWxlbWVudImen1gfMVQJAgADTAAKYXR0cmlidXRlc" +
            "3EAfgABTAAIZWxlbWVudHN0ABBMamF2YS91dGlsL0xpc3Q7TAAEbmFtZXQAG0xqYXZheC94bWwvbmFtZXNwYWNlL1FOYW1" +
            "lO3hwc3EAfgAEP0AAAAAAAAx3CAAAABAAAAAAeHNxAH4ABgAAAAB3BAAAAAp4c3IAGWphdmF4LnhtbC5uYW1lc3BhY2UuU" +
            "U5hbWWBbagt/DvdbAIAA0wACWxvY2FsUGFydHQAEkxqYXZhL2xhbmcvU3RyaW5nO0wADG5hbWVzcGFjZVVSSXEAfgAPTAA" +
            "GcHJlZml4cQB+AA94cHQADFVua25vd25DaGlsZHQAAHEAfgASeA==\"}";
	private String xpdl = "<Transitions><UnknownChild/></Transitions>";

	public void testParse() throws JSONException {
		XPDLTransitions transitions = new XPDLTransitions();
		transitions.parse(new JSONObject(json));

		StringWriter writer = new StringWriter();

		Xmappr xmappr = new Xmappr(XPDLTransitions.class);
		xmappr.setPrettyPrint(false);
		xmappr.toXML(transitions, writer);

		assertEquals(xpdl, writer.toString());
	}

	public void testWrite() {		
		StringReader reader = new StringReader(xpdl);

		Xmappr xmappr = new Xmappr(XPDLTransitions.class);
		XPDLTransitions transitions = (XPDLTransitions) xmappr.fromXML(reader);

		JSONObject importObject = new JSONObject();
		transitions.write(importObject);

		assertEquals(json, importObject.toString());
	}
}
