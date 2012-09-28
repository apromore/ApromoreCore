/**
 * Copyright 2012, Felix Mannhardt
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.apromore.canoniser.yawl.internal.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.CPFSchema;
import org.apromore.cpf.ExpressionType;
import org.apromore.cpf.InputExpressionType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.OutputExpressionType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Helper class to generate correct CPF XPath/XQuery expressions and convert them back to YAWL expresssion
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public final class ExpressionUtils {

    public static final String DEFAULT_TYPE_NAMESPACE = "http://www.w3.org/2001/XMLSchema";

    private ExpressionUtils() {
    }

    public static String buildObjectId(final String netId, final String varName) {
        return netId + "_" + varName;
    }

    public static String createExpressionReferencingNetObject(final String objectName, final NetType net) {
        return "/" + objectName;
    }

    public static String createQueryReferencingTaskVariables(final String xQuery) throws CanoniserException {
        return getTextFromXMLFragment(xQuery);
    }

    private static String getTextFromXMLFragment(final String xQuery) throws CanoniserException {
        try {
            final DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Element queryElement = docBuilder.parse(new ByteArrayInputStream(xQuery.getBytes())).getDocumentElement();
            return queryElement.getTextContent();
        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw new CanoniserException("Could not convert task parameter mapping query!", e);
        }
    }

    public static String createQueryReferencingNetObjects(final String xQuery) throws CanoniserException {
        return getTextFromXMLFragment(xQuery);
    }

    public static String determineResultType(final ExpressionType expr) {
        // TODO use xquery engine
        if (expr.getExpression().contains("text(")) {
            return "string";
        } else if (expr.getExpression().startsWith("(Boolean)")) {
            return "boolean";
        } else {
            return "any";
        }
    }

    public static String convertXQueryToYAWLNetQuery(final InputExpressionType expr) throws CanoniserException {
        String queryPart = getQueryPart(expr);
        String taskName = CPFSchema.getTaskObjectName(expr.getExpression());
        try {
            Element queryElement = createYAWLExpressionElement(queryPart, taskName);
            return elementToString(queryElement);
        } catch (ParserConfigurationException e) {
            throw new CanoniserException("Could not convert to YAWL net query!", e);
        }
    }

    public static String convertXQueryToYAWLTaskQuery(final OutputExpressionType expr) throws CanoniserException {
        String queryPart = getQueryPart(expr);
        String netName = CPFSchema.getNetObjectName(expr.getExpression());
        try {
            Element queryElement = createYAWLExpressionElement(queryPart, netName);
            return elementToString(queryElement);
        } catch (ParserConfigurationException e) {
            throw new CanoniserException("Could not convert to YAWL task query!", e);
        }
    }

    private static String getQueryPart(final ExpressionType expr) {
        String[] splittedQuery = expr.getExpression().split(" = ");
        StringBuilder queryBuilder = new StringBuilder(expr.getExpression().length());
        for (int i = 1; i < splittedQuery.length; i++) {
            queryBuilder.append(splittedQuery[i]);
        }
        return queryBuilder.toString();
    }

    private static Element createYAWLExpressionElement(final String queryPart, final String netName) throws ParserConfigurationException {
        DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document queryDoc = docBuilder.newDocument();
        Element queryElement = queryDoc.createElement(netName);
        queryElement.setTextContent(queryPart);
        return queryElement;
    }

    /**
     * Converts an Element into its XML String representation.
     *
     * @param element
     *            XML element
     * @return String representation of XML element
     */
    private static String elementToString(final Element element) {
        try {
            final DOMSource domSource = new DOMSource(element);
            final StringWriter writer = new StringWriter();
            final StreamResult result = new StreamResult(writer);
            final TransformerFactory tf = TransformerFactory.newInstance();
            final Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.transform(domSource, result);
            return writer.toString();
        } catch (final TransformerException e) {
            throw new IllegalArgumentException("Invalid Element!", e);
        }
    }

}
