package de.hpi.bpmn2xpdl;

import java.io.StringReader;
import java.io.StringWriter;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Xmappr;

import junit.framework.TestCase;

public class XPDLArtifactsTest extends TestCase {
	private String json = "{\"artifactsunknowns\":\"rO0ABXNyACVkZS5ocGkuYnBtbjJ4cGRsLlhNTFVua25vd25zQ29udGFpbm" +
            "VyAAAAAAAAAAECAAJMABF1bmtub3duQXR0cmlidXRlc3QAE0xqYXZhL3V0aWwvSGFzaE1hcDtMAA91bmtub3duRWxlbWVudHN" +
            "0ABVMamF2YS91dGlsL0FycmF5TGlzdDt4cHNyABFqYXZhLnV0aWwuSGFzaE1hcAUH2sHDFmDRAwACRgAKbG9hZEZhY3RvckkA" +
            "CXRocmVzaG9sZHhwP0AAAAAAAAx3CAAAABAAAAAAeHNyABNqYXZhLnV0aWwuQXJyYXlMaXN0eIHSHZnHYZ0DAAFJAARzaXple" +
            "HAAAAABdwQAAAAKc3IAFW9yZy54bWFwcHIuRG9tRWxlbWVudImen1gfMVQJAgADTAAKYXR0cmlidXRlc3EAfgABTAAIZWxlbW" +
            "VudHN0ABBMamF2YS91dGlsL0xpc3Q7TAAEbmFtZXQAG0xqYXZheC94bWwvbmFtZXNwYWNlL1FOYW1lO3hwc3EAfgAEP0AAAAA" +
            "AAAx3CAAAABAAAAAAeHNxAH4ABgAAAAB3BAAAAAp4c3IAGWphdmF4LnhtbC5uYW1lc3BhY2UuUU5hbWWBbagt/DvdbAIAA0wA" +
            "CWxvY2FsUGFydHQAEkxqYXZhL2xhbmcvU3RyaW5nO0wADG5hbWVzcGFjZVVSSXEAfgAPTAAGcHJlZml4cQB+AA94cHQADFVua" +
            "25vd25DaGlsZHQAAHEAfgASeA==\"}";
	private String xpdl = "<Artifacts><UnknownChild/></Artifacts>";

	public void testParse() throws JSONException {
		XPDLArtifacts artifacts = new XPDLArtifacts();
		artifacts.parse(new JSONObject(json));

		StringWriter writer = new StringWriter();

		Xmappr xmappr = new Xmappr(XPDLArtifacts.class);
		xmappr.setPrettyPrint(false);
		xmappr.toXML(artifacts, writer);

		assertEquals(xpdl, writer.toString());
	}

	public void testWrite() {		
		StringReader reader = new StringReader(xpdl);

		Xmappr xmappr = new Xmappr(XPDLArtifacts.class);
		XPDLArtifacts artifacts = (XPDLArtifacts) xmappr.fromXML(reader);

		JSONObject importObject = new JSONObject();
		artifacts.write(importObject);

		assertEquals(json, importObject.toString());
	}
}
