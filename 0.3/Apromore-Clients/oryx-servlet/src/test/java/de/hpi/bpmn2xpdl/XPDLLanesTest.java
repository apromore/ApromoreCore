package de.hpi.bpmn2xpdl;

import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Xmappr;

public class XPDLLanesTest extends TestCase {
	private String json = "{\"lanesunknowns\":\"rO0ABXNyACVkZS5ocGkuYnBtbjJ4cGRsLlhNTFVua25vd25zQ29udGFpbmVyAAAAA" +
            "AAAAAECAAJMABF1bmtub3duQXR0cmlidXRlc3QAE0xqYXZhL3V0aWwvSGFzaE1hcDtMAA91bmtub3duRWxlbWVudHN0ABVMamF2Y" +
            "S91dGlsL0FycmF5TGlzdDt4cHNyABFqYXZhLnV0aWwuSGFzaE1hcAUH2sHDFmDRAwACRgAKbG9hZEZhY3RvckkACXRocmVzaG9sZ" +
            "HhwP0AAAAAAAAx3CAAAABAAAAAAeHNyABNqYXZhLnV0aWwuQXJyYXlMaXN0eIHSHZnHYZ0DAAFJAARzaXpleHAAAAABdwQAAAAKc" +
            "3IAFW9yZy54bWFwcHIuRG9tRWxlbWVudImen1gfMVQJAgADTAAKYXR0cmlidXRlc3EAfgABTAAIZWxlbWVudHN0ABBMamF2YS91d" +
            "GlsL0xpc3Q7TAAEbmFtZXQAG0xqYXZheC94bWwvbmFtZXNwYWNlL1FOYW1lO3hwc3EAfgAEP0AAAAAAAAx3CAAAABAAAAAAeHNxA" +
            "H4ABgAAAAB3BAAAAAp4c3IAGWphdmF4LnhtbC5uYW1lc3BhY2UuUU5hbWWBbagt/DvdbAIAA0wACWxvY2FsUGFydHQAEkxqYXZhL" +
            "2xhbmcvU3RyaW5nO0wADG5hbWVzcGFjZVVSSXEAfgAPTAAGcHJlZml4cQB+AA94cHQADFVua25vd25DaGlsZHQAAHEAfgASeA==\"}";
	private String xpdl = "<Lanes><UnknownChild/></Lanes>";

	public void testParse() throws JSONException {
		XPDLLanes lanes = new XPDLLanes();
		lanes.parse(new JSONObject(json));

		StringWriter writer = new StringWriter();

		Xmappr xmappr = new Xmappr(XPDLLanes.class);
		xmappr.setPrettyPrint(false);
		xmappr.toXML(lanes, writer);

		assertEquals(xpdl, writer.toString());
	}

	public void testWrite() {		
		StringReader reader = new StringReader(xpdl);

		Xmappr xmappr = new Xmappr(XPDLLanes.class);
		XPDLLanes lanes = (XPDLLanes) xmappr.fromXML(reader);

		JSONObject importObject = new JSONObject();
		lanes.write(importObject);

		assertEquals(json, importObject.toString());
	}
}
