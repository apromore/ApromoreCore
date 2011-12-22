package de.hpi.bpmn2xpdl;

import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Xmappr;

public class XPDLCategoriesTest extends TestCase {
	
	private String json = "{\"categoriesunknowns\":\"rO0ABXNyACVkZS5ocGkuYnBtbjJ4cGRsLlhNTFVua25vd25zQ29udGFp" +
            "bmVyAAAAAAAAAAECAAJMABF1bmtub3duQXR0cmlidXRlc3QAE0xqYXZhL3V0aWwvSGFzaE1hcDtMAA91bmtub3duRWxlbWVu" +
            "dHN0ABVMamF2YS91dGlsL0FycmF5TGlzdDt4cHNyABFqYXZhLnV0aWwuSGFzaE1hcAUH2sHDFmDRAwACRgAKbG9hZEZhY3Rv" +
            "ckkACXRocmVzaG9sZHhwP0AAAAAAAAx3CAAAABAAAAAAeHNyABNqYXZhLnV0aWwuQXJyYXlMaXN0eIHSHZnHYZ0DAAFJAARz" +
            "aXpleHAAAAABdwQAAAAKc3IAFW9yZy54bWFwcHIuRG9tRWxlbWVudImen1gfMVQJAgADTAAKYXR0cmlidXRlc3EAfgABTAAI" +
            "ZWxlbWVudHN0ABBMamF2YS91dGlsL0xpc3Q7TAAEbmFtZXQAG0xqYXZheC94bWwvbmFtZXNwYWNlL1FOYW1lO3hwc3EAfgAE" +
            "P0AAAAAAAAx3CAAAABAAAAAAeHNxAH4ABgAAAAB3BAAAAAp4c3IAGWphdmF4LnhtbC5uYW1lc3BhY2UuUU5hbWWBbagt/Dvd" +
            "bAIAA0wACWxvY2FsUGFydHQAEkxqYXZhL2xhbmcvU3RyaW5nO0wADG5hbWVzcGFjZVVSSXEAfgAPTAAGcHJlZml4cQB+AA94" +
            "cHQADFVua25vd25DaGlsZHQAAHEAfgASeA==\"}";
	private String xpdl = "<Categories><UnknownChild/></Categories>";

	public void testParse() throws JSONException {
		XPDLCategories categories = new XPDLCategories();
		categories.parse(new JSONObject(json));

		StringWriter writer = new StringWriter();

		Xmappr xmappr = new Xmappr(XPDLCategories.class);
		xmappr.setPrettyPrint(false);
		xmappr.toXML(categories, writer);

		assertEquals(xpdl, writer.toString());
	}

	public void testWrite() {		
		StringReader reader = new StringReader(xpdl);

		Xmappr xmappr = new Xmappr(XPDLCategories.class);
		XPDLCategories categories = (XPDLCategories) xmappr.fromXML(reader);

		JSONObject importObject = new JSONObject();
		categories.write(importObject);

		assertEquals(json, importObject.toString());
	}
}
