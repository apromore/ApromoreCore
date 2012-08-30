package de.hpi.bpmn2xpdl;

import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Xmappr;

public class XPDLAssociationsTest extends TestCase {
	private String json = "{\"associationsunknowns\":\"rO0ABXNyACVkZS5ocGkuYnBtbjJ4cGRsLlhNTFVua25vd25zQ29ud" +
            "GFpbmVyAAAAAAAAAAECAAJMABF1bmtub3duQXR0cmlidXRlc3QAE0xqYXZhL3V0aWwvSGFzaE1hcDtMAA91bmtub3duRWxl" +
            "bWVudHN0ABVMamF2YS91dGlsL0FycmF5TGlzdDt4cHNyABFqYXZhLnV0aWwuSGFzaE1hcAUH2sHDFmDRAwACRgAKbG9hZEZ" +
            "hY3RvckkACXRocmVzaG9sZHhwP0AAAAAAAAx3CAAAABAAAAAAeHNyABNqYXZhLnV0aWwuQXJyYXlMaXN0eIHSHZnHYZ0DAA" +
            "FJAARzaXpleHAAAAABdwQAAAAKc3IAFW9yZy54bWFwcHIuRG9tRWxlbWVudImen1gfMVQJAgADTAAKYXR0cmlidXRlc3EAf" +
            "gABTAAIZWxlbWVudHN0ABBMamF2YS91dGlsL0xpc3Q7TAAEbmFtZXQAG0xqYXZheC94bWwvbmFtZXNwYWNlL1FOYW1lO3hw" +
            "c3EAfgAEP0AAAAAAAAx3CAAAABAAAAAAeHNxAH4ABgAAAAB3BAAAAAp4c3IAGWphdmF4LnhtbC5uYW1lc3BhY2UuUU5hbWW" +
            "Bbagt/DvdbAIAA0wACWxvY2FsUGFydHQAEkxqYXZhL2xhbmcvU3RyaW5nO0wADG5hbWVzcGFjZVVSSXEAfgAPTAAGcHJlZm" +
            "l4cQB+AA94cHQADFVua25vd25DaGlsZHQAAHEAfgASeA==\"}";
	private String xpdl = "<Associations><UnknownChild/></Associations>";

	public void testParse() throws JSONException {
		XPDLAssociations associations = new XPDLAssociations();
		associations.parse(new JSONObject(json));

		StringWriter writer = new StringWriter();

		Xmappr xmappr = new Xmappr(XPDLAssociations.class);
		xmappr.setPrettyPrint(false);
		xmappr.toXML(associations, writer);

		assertEquals(xpdl, writer.toString());
	}

	public void testWrite() {		
		StringReader reader = new StringReader(xpdl);

		Xmappr xmappr = new Xmappr(XPDLAssociations.class);
		XPDLAssociations associations = (XPDLAssociations) xmappr.fromXML(reader);

		JSONObject importObject = new JSONObject();
		associations.write(importObject);

		assertEquals(json, importObject.toString());
	}
}
