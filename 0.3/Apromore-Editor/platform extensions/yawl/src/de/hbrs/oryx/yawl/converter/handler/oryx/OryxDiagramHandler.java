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
package de.hbrs.oryx.yawl.converter.handler.oryx;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.basic.BasicShape;
import org.yawlfoundation.yawl.elements.YSpecVersion;
import org.yawlfoundation.yawl.elements.YSpecification;
import org.yawlfoundation.yawl.exceptions.YSyntaxException;
import org.yawlfoundation.yawl.unmarshal.YMetaData;

import de.hbrs.oryx.yawl.YAWLUtils;
import de.hbrs.oryx.yawl.converter.context.OryxConversionContext;
import de.hbrs.oryx.yawl.converter.exceptions.NoRootNetFoundException;
import de.hbrs.oryx.yawl.converter.handler.HandlerFactory;

/**
 * Converts the Diagram to a YAWL specification
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 *
 */
public class OryxDiagramHandler extends OryxHandlerImpl {

	private final BasicDiagram diagramShape;

	public OryxDiagramHandler(OryxConversionContext context,
			BasicDiagram diagramShape) {
		super(context);
		this.diagramShape = diagramShape;
	}

	/* (non-Javadoc)
	 * @see de.hbrs.oryx.yawl.converter.handler.oryx.OryxHandler#convert()
	 */
	@Override
	public void convert() {

		try {
			BasicShape rootNet = findRootNet();

			// First extract specification details
			YSpecification yawlSpec = convertSpecification();
			getContext().setSpecification(yawlSpec);

			// Then go on converting the root net
			HandlerFactory handlerFactory = getContext().getHandlerFactory();
			handlerFactory.createOryxConverter(rootNet).convert();

		} catch (NoRootNetFoundException e) {
			getContext().addConversionWarnings(e);
			// Abort conversion
			getContext().setConversionError(true);
		}

	}

	private BasicShape findRootNet() throws NoRootNetFoundException {
		for (BasicShape shape : diagramShape.getChildShapesReadOnly()) {
			if (isRootNet(shape)) {
				return shape;
			}
		}
		// No root net found
		throw new NoRootNetFoundException(
				"Could not find root net in this diagram."
						+ "Please select a diagram containing a root net.");
	}

	private boolean isRootNet(BasicShape shape) {
		if (shape.hasProperty("isrootnet")) {
			return new Boolean(shape.getProperty("isrootnet"));
		} else {
			return false;
		}
	}

	private YSpecification convertSpecification() {
		YSpecification spec = new YSpecification(
				diagramShape.getProperty("uri"));
		
		spec.setName(diagramShape.getProperty("name"));
		
		try {
			spec.setSchema(diagramShape.getProperty("datatypedefinitions"));
		} catch (YSyntaxException e) {
			getContext().addConversionWarnings("Invalid Data Definitions", e);
		}
		
		YMetaData metaData = new YMetaData();
		metaData.setTitle(diagramShape.getProperty("title"));
		metaData.setUniqueID(convertYawlId(diagramShape));
		metaData.setDescription(diagramShape.getProperty("description"));
		
	
		try {
			metaData.setVersion(new YSpecVersion(diagramShape
					.getProperty("version")));
		} catch (Exception e) {
			getContext().addConversionWarnings("Could not convert metadata 'version' of specification",
					e);
			metaData.setVersion(new YSpecVersion());
		}
		
		
		try {
			if (diagramShape.getProperty("validFrom") != null) {
				metaData.setValidFrom(new SimpleDateFormat(YAWLUtils.DATE_FORMAT).parse(
						diagramShape.getProperty("validFrom")));
			}
		} catch (ParseException e) {
			getContext().addConversionWarnings("Invalid Date-Format validFrom",
					e);
		}
		
		try {
			if (diagramShape.getProperty("validUntil") != null) {
				metaData.setValidUntil(new SimpleDateFormat(YAWLUtils.DATE_FORMAT).parse(
						diagramShape.getProperty("validUntil")));
			}
		} catch (ParseException e) {
			getContext()
					.addConversionWarnings("Invalid Date-Format validTo", e);
		}
		
		try {
			if (diagramShape.getProperty("created") != null) {
				metaData.setValidUntil(new SimpleDateFormat(YAWLUtils.DATE_FORMAT).parse(
						diagramShape.getProperty("created")));
			}
		} catch (ParseException e) {
			getContext()
					.addConversionWarnings("Invalid Date-Format validTo", e);
		}
		
		
		try {
			metaData.setCreators(convertListOfNames(diagramShape.getPropertyJsonObject("creators")));
		} catch (JSONException e) {
			getContext()
			.addConversionWarnings("Could not convert metadata 'creators' of specification", e);
		}
		
		try {
			metaData.setContributors(convertListOfNames(diagramShape.getPropertyJsonObject("contributor")));
		} catch (JSONException e) {
			getContext()
			.addConversionWarnings("Could not convert metadata 'contributor' of specification", e);
		}	
		
		try {
			metaData.setSubjects(convertListOfNames(diagramShape.getPropertyJsonObject("subject")));
		} catch (JSONException e) {
			getContext()
			.addConversionWarnings("Could not convert metadata 'subject' of specification", e);
		}	
		
		metaData.setCoverage(diagramShape.getProperty("coverage"));				
		metaData.setStatus(diagramShape.getProperty("status"));
		metaData.setPersistent(diagramShape.getPropertyBoolean("persistent"));
		
		spec.setMetaData(metaData);
		return spec;
	}

	private List<String> convertListOfNames(JSONObject prop) throws JSONException {
		List<String> listOfNames = new ArrayList<String>();
		JSONArray jsonArray = prop.getJSONArray("items");
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject obj = jsonArray.getJSONObject(i);
			String name = obj.getString("name");
			listOfNames.add(name);
		}
		return listOfNames;
	}

}
