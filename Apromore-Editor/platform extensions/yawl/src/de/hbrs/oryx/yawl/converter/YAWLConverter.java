/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package de.hbrs.oryx.yawl.converter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jdom2.JDOMException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.basic.BasicDiagramBuilder;
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
import de.hbrs.oryx.yawl.util.YAWLUtils;

/**
 * Validates and converts a YAWL specification to the JSON representation of an Oryx diagram and vice-versa.
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

        public OryxResult(final YAWLConversionContext context) {
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
            BasicDiagram rootNet = yawlContext.getRootNet();
            mergeProperties(specDiagram, rootNet);
            return rootNet;
        }

        /**
         * Merges the specification properties to the Net as there is only one Net per Diagram
         * 
         * @param specDiagram
         * @param rootNet
         */
        private void mergeProperties(final BasicDiagram specDiagram, final BasicDiagram rootNet) {
            Map<String, String> netProperties = rootNet.getPropertiesReadOnly();
            Map<String, String> specProperties = specDiagram.getPropertiesReadOnly();
            HashMap<String, String> mergedProperties = new HashMap<String, String>();
            mergedProperties.putAll(netProperties);
            mergedProperties.putAll(specProperties);
            rootNet.setProperties(mergedProperties);
        }

        /**
         * Get all diagrams of the converted YAWL specification
         * 
         * @return all converted Diagrams <ID, Diagram>
         */
        public Set<Entry<String, BasicDiagram>> getDiagrams() {
            Map<String, BasicDiagram> diagramMap = new HashMap<String, BasicDiagram>();
            for (Entry<String, BasicDiagram> netEntry : yawlContext.getNetSet()) {
                mergeProperties(yawlContext.getSpecificationDiagram(), netEntry.getValue());
                diagramMap.put(netEntry.getKey(), netEntry.getValue());
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

        public YAWLResult(final OryxConversionContext context) {
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
                String yawlSpec = YMarshal.marshal(oryxContext.getSpecification());
                int closingTag = yawlSpec.lastIndexOf("</");
                return yawlSpec.substring(0, closingTag) + oryxContext.getLayout().toXML() + yawlSpec.substring(closingTag);
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
    private final YAWLConversionContext yawlContext;

    /**
     * Conversion context valid for one conversion from Oryx to YAWL
     */
    private final OryxConversionContext oryxContext;

    /**
     * Create an converter instance that may be used to convert one YAWL specification.
     * 
     * @param rootDir
     *            of the editor appplication used to retrieve Stencil Set
     */
    public YAWLConverter() {
        this.yawlContext = new YAWLConversionContext();
        this.oryxContext = new OryxConversionContext();
        // Handler Factory for both conversion directions
        this.converterFactory = new HandlerFactoryImpl(yawlContext, oryxContext);
    }

    /**
     * 
     * @param converterFactory
     */
    public void setConverterFactory(final HandlerFactoryImpl converterFactory) {
        this.converterFactory = converterFactory;
    }

    /**
     * Converts the given YAWL specification to one or more Oryx diagrams. The diagrams can be retrieved with the @link getDiagrams method of the
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
    public OryxResult convertYAWLToOryx(final String specification) throws YSyntaxException, JDOMException, IOException {

        // First convert the layout element of the YAWL specification
        YAWLLayoutConverter yawlLayoutConverter = new YAWLLayoutConverter(specification, yawlContext);
        yawlLayoutConverter.convertLayout();
        // Now load alle YAWL nets, using the layout information loaded before.
        loadSpecifications(specification);

        return new OryxResult(yawlContext);
    }

    /**
     * Let the YAWL Unmarshaller do it's work and load and convert all decompositions.
     * 
     * @param specification
     * @throws YSyntaxException
     *             in case of an invalid specification
     */
    private void loadSpecifications(final String specification) throws YSyntaxException {

        // Do not validate YAWL specification as otherwise some YAWL
        // specification saved by the orgiginal YAWL editor may not load
        List<YSpecification> specificationList = YMarshal.unmarshalSpecifications(specification, false);

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
    public YAWLResult convertOryxToYAWL(final String jsonData) {

        try {
            JSONObject jsonObject = new JSONObject(jsonData);

            // Get a list of all Diagrams representing Sub Nets and store them
            // for later use
            JSONArray subDiagramList = jsonObject.getJSONArray("subDiagrams");
            for (int i = 0; i < subDiagramList.length(); i++) {
                JSONObject subnetElement = (JSONObject) subDiagramList.get(i);
                BasicDiagram subnetDiagram = BasicDiagramBuilder.parseJson(subnetElement.getJSONObject("diagram"));
                oryxContext.addSubnetDiagram(subnetElement.getString("id"), subnetDiagram);
            }

            // Get the Diagram of the Root Net
            JSONObject rootDiagram = jsonObject.getJSONObject("rootDiagram");
            BasicDiagram yawlDiagram = BasicDiagramBuilder.parseJson(rootDiagram);
            oryxContext.setRootNetID(YAWLUtils.convertYawlId(yawlDiagram));

            // Starting converting the Root Net
            OryxHandler converter = converterFactory.createOryxConverter(yawlDiagram);
            converter.convert();

            return new YAWLResult(oryxContext);

        } catch (JSONException e) {
            oryxContext.addConversionWarnings("Could not parse Oryx diagram JSON", e);
            oryxContext.setConversionError(true);
            return new YAWLResult(oryxContext);
        }

    }

}
