/**
 * Copyright (c) 2011-2012 Felix Mannhardt
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 * See: http://www.opensource.org/licenses/mit-license.php
 * 
 */
package de.hbrs.oryx.yawl.converter.handler.yawl;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Point;
import org.oryxeditor.server.diagram.StencilSetReference;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.yawlfoundation.yawl.elements.YSpecification;
import org.yawlfoundation.yawl.unmarshal.YMetaData;

import de.hbrs.oryx.yawl.YAWLUtils;
import de.hbrs.oryx.yawl.converter.context.YAWLConversionContext;

public class SpecificationHandler extends YAWLHandlerImpl {

	YSpecification specification;

	public SpecificationHandler(YAWLConversionContext context,
			YSpecification ySpec) {
		super(context);
		this.specification = ySpec;
	}

	public YSpecification getSpecification() {
		return specification;
	}

	@Override
	public void convert(String parentId) {
		BasicDiagram specDiagram = createEmptyDiagram("diagram-"
				+ getSpecification().getURI());

		specDiagram.setProperties(convertProperties());

		getContext().setSpecificationDiagram(specDiagram);
	}

	private HashMap<String, String> convertProperties() {
		HashMap<String, String> props = new HashMap<String, String>();

		final YSpecification spec = getSpecification();
		final YMetaData metaData = spec.getMetaData();
		
		// General Properties
		
		props.put("name", convertNullable(spec.getName()));
		props.put("yawlid", convertNullable(metaData.getUniqueID()));
		
		
		// Specification Related

		props.put("title", convertNullable(metaData.getTitle()));

		try {
			props.put("creators", convertListOfNames(metaData.getCreators()));
		} catch (JSONException e) {
			getContext().addConversionWarnings(
					"Could not convert YAWL specification 'creators'", e);
		}

		try {
			props.put("subject", convertListOfNames(metaData.getSubjects()));
		} catch (JSONException e) {
			getContext().addConversionWarnings(
					"Could not convert YAWL specification 'subject'", e);
		}

		props.put("description", convertNullable(metaData.getDescription()));

		try {
			props.put("contributor",
					convertListOfNames(metaData.getContributors()));
		} catch (JSONException e) {
			getContext().addConversionWarnings(
					"Could not convert YAWL specification 'subject'", e);
		}

		props.put("coverage", convertNullable(metaData.getCoverage()));

		props.put("validFrom",
				metaData.getValidFrom() != null ? new SimpleDateFormat(
						YAWLUtils.DATE_FORMAT).format(metaData.getValidFrom())
						: "");
		props.put("validUntil",
				metaData.getValidUntil() != null ? new SimpleDateFormat(
						YAWLUtils.DATE_FORMAT).format(metaData.getValidUntil())
						: "");
		
		props.put("created",
				metaData.getCreated() != null ? new SimpleDateFormat(
						YAWLUtils.DATE_FORMAT).format(metaData.getCreated())
						: "");		

		props.put("version", convertNullable(metaData.getVersion().toString()));
		
		props.put("status", convertNullable(metaData.getStatus()));
		
		props.put("persistent", new Boolean(metaData.isPersistent()).toString());
		
		props.put("uri", spec.getURI());

		props.put("datatypedefinitions", convertNullable(spec
				.getDataValidator().getSchema()));

		return props;
	}

	private String convertListOfNames(List<String> list) throws JSONException {
		JSONObject listOfNames = new JSONObject();
		JSONArray items = new JSONArray();
		for (String creator : list) {
			JSONObject obj = new JSONObject();
			obj.put("name", creator);
			items.put(obj);
		}
		listOfNames.put("items", items);
		return listOfNames.toString();
	}

	private BasicDiagram createEmptyDiagram(String id) {

		String stencilSetNs = "http://b3mn.org/stencilset/yawl2.2#";
		String url = getContext().getRootDir() + "stencilsets/yawl/yawl.json";
		StencilSetReference stencilSetRef = new StencilSetReference(
				stencilSetNs, url);

		BasicDiagram diagram = new BasicDiagram(id, "Diagram", stencilSetRef);
		// Set required properties to initial values
		// TODO: probably not used anymore in SIGNAVIO CORE COMPONENTS
		// diagram.setChildShapes(new ArrayList<Shape>());
		diagram.setBounds(new Bounds(new Point(0.0, 0.0), new Point(0.0, 0.0)));
		return diagram;
	}

}
