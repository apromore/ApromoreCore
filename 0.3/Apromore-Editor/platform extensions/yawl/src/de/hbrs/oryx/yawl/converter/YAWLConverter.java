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
package de.hbrs.oryx.yawl.converter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jdom.JDOMException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Point;
import org.oryxeditor.server.diagram.StencilSetReference;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.basic.BasicDiagramBuilder;
import org.oryxeditor.server.diagram.basic.BasicShape;
import org.yawlfoundation.yawl.elements.YDecomposition;
import org.yawlfoundation.yawl.elements.YSpecification;
import org.yawlfoundation.yawl.exceptions.YSyntaxException;
import org.yawlfoundation.yawl.unmarshal.YMarshal;

import de.hbrs.oryx.yawl.converter.context.OryxConversionContext;
import de.hbrs.oryx.yawl.converter.context.YAWLConversionContext;
import de.hbrs.oryx.yawl.converter.exceptions.ConversionException;
import de.hbrs.oryx.yawl.converter.handler.HandlerFactoryImpl;
import de.hbrs.oryx.yawl.converter.handler.oryx.OryxHandler;
import de.hbrs.oryx.yawl.converter.handler.yawl.YAWLHandler;
import de.hbrs.oryx.yawl.converter.layout.YAWLLayoutConverter;

/**
 * Validates and converts a YAWL specification to the JSON representation of an
 * Oryx diagram and vice-versa.
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 * 
 */
public class YAWLConverter {

	/**
	 * Result of the conversion YAWL -> Oryx
	 */
	public class OryxResult {

		private final YAWLConversionContext yawlContext;

		public OryxResult(YAWLConversionContext context) {
			yawlContext = context;
		}

		/**
		 * Name of the Root-Net
		 * 
		 * @return the YAWL name of the Root-Net
		 */
		public String getRootNetId() {
			return yawlContext.getRootNetId();
		}

		/**
		 * Get the Diagram of the YAWL root-net. Always returns a valid Diagram.
		 * 
		 * @return the diagram of the YAWL root-net
		 */
		public BasicDiagram getRootDiagram() {
			BasicDiagram specDiagram = yawlContext.getSpecificationDiagram();
			BasicShape rootNet = yawlContext.getRootNet();
			specDiagram.setBounds(rootNet.getBounds());
			specDiagram.addChildShape(rootNet);
			return specDiagram;
		}

		private BasicDiagram createDiagram(BasicShape net) {
			// Create Oryx diagram for a net
			BasicDiagram netDiagram;
			if (yawlContext.getRootNetId().equals(net.getResourceId())) {
				// Attach specification only to rootNet
				netDiagram = yawlContext.getSpecificationDiagram();
			} else {
				// Create a new diagram for each subNet
				netDiagram = createEmptyDiagram("diagram-"
						+ net.getResourceId());
				netDiagram.setProperties(yawlContext.getSpecificationDiagram()
						.getPropertiesReadOnly());
			}
			netDiagram.setBounds(net.getBounds());
			netDiagram.addChildShape(net);
			return netDiagram;
		}

		private BasicDiagram createEmptyDiagram(String id) {
			String stencilSetNs = "http://b3mn.org/stencilset/yawl2.2#";
			String url = yawlContext.getRootDir()
					+ "stencilsets/yawl/yawl.json";
			StencilSetReference stencilSet = new StencilSetReference(stencilSetNs, url);

			BasicDiagram diagram = new BasicDiagram(id, "Diagram", stencilSet);
			// Set required properties to initial values
			diagram.setBounds(new Bounds(new Point(0.0, 0.0), new Point(0.0,
					0.0)));
			return diagram;
		}

		/**
		 * Get all diagrams of the converted YAWL specification
		 * 
		 * @return all converted Diagrams <ID, Diagram>
		 */
		public Set<Entry<String, BasicDiagram>> getDiagrams() {
			Map<String, BasicDiagram> diagramMap = new HashMap<String, BasicDiagram>();
			for (Entry<String, BasicShape> netEntry : yawlContext.getNetSet()) {
				diagramMap.put(netEntry.getKey(),
						createDiagram(netEntry.getValue()));
			}
			return diagramMap.entrySet();
		}
		
		/**
		 * @return true if there are warnings during conversion
		 */
		public boolean hasWarnings() {
			return yawlContext.getConversionWarnings().size() > 0;
		}

		/**
		 * @return true if conversion failed
		 */
		public boolean hasFailed() {
			return yawlContext.getConversionError();
		}

		/**
		 * @return all warnings and errors during conversion
		 */
		public List<ConversionException> getWarnings() {
			return yawlContext.getConversionWarnings();
		}		

	}

	/**
	 * Result of the conversion Oryx -> YAWL
	 */
	public class YAWLResult {

		private final OryxConversionContext oryxContext;

		public YAWLResult(OryxConversionContext context) {
			oryxContext = context;
		}

		/**
		 * @return true if there are warnings during conversion
		 */
		public boolean hasWarnings() {
			return oryxContext.getConversionWarnings().size() > 0;
		}

		/**
		 * @return true if conversion failed
		 */
		public boolean hasFailed() {
			return oryxContext.getConversionError();
		}

		/**
		 * @return all warnings and errors during conversion
		 */
		public List<ConversionException> getWarnings() {
			return oryxContext.getConversionWarnings();
		}

		/**
		 * Marshals the YAWL Specification to XML if conversion is successful.
		 * 
		 * @return YAWL XML or empty string
		 */
		public String getYAWLAsXML() {
			if (oryxContext.getConversionError()) {
				return "";
			} else {
				return YMarshal.marshal(oryxContext.getSpecification());
			}
		}

		/**
		 * @return proposed YAWL filename (URI)
		 */
		public String getFilename() {
			if (oryxContext.getConversionError()) {
				return "ERROR";
			} else {
				return oryxContext.getSpecification().getURI();
			}
		}

	}

	/**
	 * Factory to create YAWL and Oryx handlers
	 */
	private HandlerFactoryImpl converterFactory;

	/**
	 * Conversion context valid for one conversion from YAWL to Oryx
	 */
	private YAWLConversionContext yawlContext;

	/**
	 * Conversion context valid for one conversion from Oryx to YAWL
	 */
	private OryxConversionContext oryxContext;

	/**
	 * Create an converter instance that may be used to convert one YAWL
	 * specification.
	 * 
	 * @param rootDir
	 *            of the editor appplication used to retrieve Stencil Set
	 */
	public YAWLConverter(String rootDir, String oryxBackendUrl) {
		this.yawlContext = new YAWLConversionContext(rootDir);
		this.oryxContext = new OryxConversionContext(oryxBackendUrl);
		// Handler Factory for both conversion directions
		this.converterFactory = new HandlerFactoryImpl(yawlContext, oryxContext);
	}

	/**
	 * 
	 * @param converterFactory
	 */
	public void setConverterFactory(HandlerFactoryImpl converterFactory) {
		this.converterFactory = converterFactory;
	}

	/**
	 * Converts the given YAWL specification to one or more Oryx diagrams. The
	 * diagrams can be retrieved with the @link getDiagrams method of the
	 * OryxResult object.
	 * 
	 * @param specification
	 *            of the YAWL file
	 * @throws YSyntaxException
	 *             in case of invalid YAWL syntax
	 * @throws JDOMException
	 *             in case of an error converting the layout
	 * @throws IOException
	 *             in case of an error converting the layout
	 */
	public OryxResult convertYAWLToOryx(String specification)
			throws YSyntaxException, JDOMException, IOException {

		// First convert the layout element of the YAWL specification
		YAWLLayoutConverter yawlLayoutConverter = new YAWLLayoutConverter(
				specification, yawlContext);
		yawlLayoutConverter.convertLayout();
		// Now load alle YAWL nets, using the layout information loaded before.
		loadSpecifications(specification);

		return new OryxResult(yawlContext);
	}

	/**
	 * Let the YAWL Unmarshaller do it's work and load and convert all
	 * decompositions.
	 * 
	 * @param specification
	 * @throws YSyntaxException
	 *             in case of an invalid specification
	 */
	private void loadSpecifications(String specification)
			throws YSyntaxException {

		// Do not validate YAWL specification as otherwise some YAWL
		// specification saved by the orgiginal YAWL editor may not load
		List<YSpecification> specificationList = YMarshal
				.unmarshalSpecifications(specification, false);

		for (YSpecification ySpec : specificationList) {

			YAWLHandler converter = converterFactory.createYAWLConverter(ySpec);
			converter.convert("");

			for (YDecomposition yDecomp : ySpec.getDecompositions()) {

				converter = converterFactory.createYAWLConverter(yDecomp);
				if (converter != null) {
					// Using the Specification ID as Parent
					converter.convert(ySpec.getID());
				}
			}

			// Currently only converting the first specification
			break;
		}
	}

	/**
	 * Converts the Oryx diagram specified by jsonData to a YAWL specification.
	 * 
	 * @param jsonData
	 *            of an YAWL diagram from Oryx
	 * @return YAWLResult for retrieving the specification
	 */
	public YAWLResult convertOryxToYAWL(String jsonData) {

		try {
			JSONObject jsonObject = new JSONObject(jsonData);

			// Get a list of all Diagrams representing Sub Nets and store them
			// for later use
			JSONArray subDiagramList = jsonObject.getJSONArray("subDiagrams");
			for (int i = 0; i < subDiagramList.length(); i++) {
				JSONObject subnetElement = (JSONObject) subDiagramList.get(i);
				BasicDiagram subnetDiagram = BasicDiagramBuilder
						.parseJson(subnetElement.getJSONObject("diagram"));
				oryxContext.addSubnetDiagram(subnetElement.getString("id"),
						subnetDiagram);
			}

			// Get the Diagram of the Root Net
			JSONObject rootDiagram = jsonObject.getJSONObject("rootDiagram");
			BasicDiagram yawlDiagram = BasicDiagramBuilder
					.parseJson(rootDiagram);
			                        
			// Starting converting the Root Net			
			OryxHandler converter = converterFactory
					.createOryxConverter(yawlDiagram);
			converter.convert();
			return new YAWLResult(oryxContext);

		} catch (JSONException e) {
			oryxContext.addConversionWarnings(
					"Could not parse Oryx diagram JSON", e);
			oryxContext.setConversionError(true);
			return new YAWLResult(oryxContext);
		}

	}

}
