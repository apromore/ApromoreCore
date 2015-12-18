/*
 * Copyright © 2009-2015 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package org.apromore.canoniser.yawl.internal.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.apromore.cpf.ObjectType;
import org.apromore.cpf.OutputExpressionType;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import org.yawlfoundation.yawlschema.ExternalTaskFactsType;
import org.yawlfoundation.yawlschema.NetFactsType;

/**
 * Helper class to generate correct CPF XPath/XQuery expressions and convert them back to YAWL expresssion
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public final class ExpressionUtils {

    private static final Pattern NCNAME_PATTERN = Pattern.compile("[\\p{Alpha}_][\\p{Alnum}-_\\x2E]*");
    
    private static final String XPATH_NET_VARIABLE_REGEX_FORMAT_STRING = "/%1$s/([a-zA-Z0-9_]+)(/[a-zA-Z0-9_]+)?(/text\\(\\))?";
    
    private static final String XPATH_TASK_VARIABLE_REGEX_FORMAT_STRING = "/%1$s/([a-zA-Z0-9_]+)";

    public static final String DEFAULT_TYPE_NAMESPACE = "http://www.w3.org/2001/XMLSchema";

    private ExpressionUtils() {
    }

    /**
     * Builds a unique Object ID using the Net ID as prefix
     *
     * @param netId
     * @param varName
     * @return netId_varName
     */
    public static String buildObjectId(final String netId, final String varName) {
        return netId + "_" + varName;
    }

    /**
     * Builds a XPath expression referencing an Object by its name
     *
     * @param objectName
     *            name of Object
     * @param net
     *            CPF Net
     * @return valid XPath
     * @throws CanoniserException
     *             in case the Object is not found
     */
    public static String createExpressionReferencingNetObject(final String objectName, final NetType net) throws CanoniserException {
        for (ObjectType obj : net.getObject()) {
            if (obj.getName().equals(objectName)) {
                return CPFSchema.createGetObjectValueExpr(obj.getName());
            }
        }
        throw new CanoniserException("Referenced Net Object not found " + objectName);
    }

    /**
     * Builds a XPath expression referencing a Task variable by its name.
     *
     * @param xQuery
     *            in YAWL form <startTag>{XQuery}</endTag>
     * @param yawlTask
     *            the YAWL Task the query is of
     * @return canonical XQuery
     * @throws CanoniserException
     *             in case the query can not be canonised
     */
    public static String createQueryReferencingTaskVariables(final String xQuery, final ExternalTaskFactsType yawlTask) throws CanoniserException {
        String originalQuery = convertEmbeddedXQueryToStandalone(xQuery);
        String yawlTaskIdForExpressions = yawlTask.getId().replaceFirst("_[0-9]+$", "");
        // TODO this should be done with a XQuery parser, so we really just convert the Task-ID in canonical form!
        Pattern taskIdPattern = Pattern.compile(String.format(XPATH_TASK_VARIABLE_REGEX_FORMAT_STRING, yawlTaskIdForExpressions));
        Matcher matcher = taskIdPattern.matcher(originalQuery);
        if (matcher.find()) {
            return matcher.replaceAll(CPFSchema.createGetTaskObjectValueExpr("$1"));
        } else {
            return originalQuery;
        }
    }

    /**
     * Builds a XPath expression referencing one or multiple Net Object by their name. The returned XQuery will reference Objects in this way:
     *
     * <pre>
     * // Net[@id='%netId%']/Object[name/text()='%objectName%']
     * </pre>
     *
     * @param xQuery
     *            in YAWL form <startTag>{XQuery}</endTag>
     * @param cpfNet
     *            converted CPF Net
     * @param objectNameList
     *            converted CPF Objects that this query refers to
     * @return canonical XQuery
     * @throws CanoniserException
     *             in case the query can not be canonised
     */
    public static String createQueryReferencingNetObjects(final String xQuery, final NetType cpfNet, final Set<String> objectNameList)
            throws CanoniserException {
        String originalQuery = convertEmbeddedXQueryToStandalone(xQuery);
        String yawlNetId = cpfNet.getOriginalID();
        String rewrittenQuery = originalQuery;

        // TODO this should be done with a XQuery parser, so we really just the XPath lookups to Net Objects to canonical form!
        for (String obj : objectNameList) {
            Pattern objectPattern = Pattern.compile("/" + yawlNetId + "/" + obj);
            Matcher objectMatcher = objectPattern.matcher(rewrittenQuery);
            if (objectMatcher.find()) {
                rewrittenQuery = objectMatcher.replaceAll(CPFSchema.createGetObjectValueExpr(obj));
            }
        }
        return rewrittenQuery;
    }

    /**
     * Tries to determine the result type of an Expression
     *
     * @param expression CPF expression in any language
     * @return anyType if not able to determine the type, otherwise the XSD type
     */
    public static String determineResultType(final ExpressionType expression) {
        if (expression.getLanguage().equals(CPFSchema.EXPRESSION_LANGUAGE_XPATH) || expression.getLanguage().equals(CPFSchema.EXPRESSION_LANGUAGE_XQUERY)) {
            // TODO determine result type based on a real XQuery/XPath evaluation against a document, but at the moment there is not representation of
            // the actual data in CPF
            if (expression.getExpression().endsWith("text()") || expression.getExpression().endsWith("text()}")) {
                return "string";
            } else if (expression.getExpression().startsWith("boolean(")) {
                return "boolean";
            } else if (expression.getExpression().startsWith("number(")) {
                return "double";
            } else {
                return "anyType";
            }
        } else {
            // Giving up for now
            return "anyType";
        }
    }

    /**
     * Takes a XQuery/XPath expression in Canonical Format and generates the corresponding YAWL expression.
     *
     * @param expression CPF expression in XQuery or XPath
     * @param net which is the source of the expression
     * @return YAWL XQuery
     * @throws CanoniserException
     */
    public static String convertXQueryToYAWLNetQuery(final InputExpressionType expression, final NetFactsType net) throws CanoniserException {
        if (!(expression.getLanguage().equals(CPFSchema.EXPRESSION_LANGUAGE_XPATH) || expression.getLanguage().equals(CPFSchema.EXPRESSION_LANGUAGE_XQUERY) )) {
           throw new CanoniserException("Unsupported Language for YAWL: "+expression.getLanguage());
        }
        String queryPart = getQueryPart(expression);
        // TODO this should be done with a XQuery parser, so we really just convert the user defined functions
        Pattern netQueryPattern = Pattern.compile("cpf:getObjectValue\\('(.+?)'\\)");
        Matcher netQueryMatcher = netQueryPattern.matcher(queryPart);
        String newQueryPart = netQueryMatcher.replaceAll("/"+net.getId()+"/$1");
        String taskName = CPFSchema.getTaskObjectName(expression.getExpression());
        try {
            Element queryElement = createYAWLExpressionElement(newQueryPart, taskName);
            return elementToString(queryElement);
        } catch (ParserConfigurationException e) {
            throw new CanoniserException("Could not convert to YAWL net query!", e);
        }
    }

    /**
     * Takes a XQuery/XPath expression in Canonical Format and generates the corresponding YAWL expression.
     *
     * @param expression CPF expression in XQuery or XPath
     * @param task which is the source for the expression
     * @return YAWL XQuery
     * @throws CanoniserException
     */
    public static String convertXQueryToYAWLTaskQuery(final OutputExpressionType expression, final ExternalTaskFactsType task) throws CanoniserException {
        if (!(expression.getLanguage().equals(CPFSchema.EXPRESSION_LANGUAGE_XPATH) || expression.getLanguage().equals(CPFSchema.EXPRESSION_LANGUAGE_XQUERY) )) {
            throw new CanoniserException("Unsupported Language for YAWL: "+expression.getLanguage());
         }
        String queryPart = getQueryPart(expression);
        // TODO this should be done with a XQuery parser, so we really just convert the user defined functions
        Pattern netQueryPattern = Pattern.compile("cpf:getTaskObjectValue\\('(.+?)'\\)");
        Matcher netQueryMatcher = netQueryPattern.matcher(queryPart);
        String newQueryPart = netQueryMatcher.replaceAll("/"+task.getId()+"/$1");
        String netObjectName = CPFSchema.getNetObjectName(expression.getExpression());
        try {
            Element queryElement = createYAWLExpressionElement(newQueryPart, netObjectName);
            return elementToString(queryElement);
        } catch (ParserConfigurationException e) {
            throw new CanoniserException("Could not convert to YAWL task query!", e);
        }
    }

    private static String getQueryPart(final ExpressionType expr) throws CanoniserException {
        String[] splittedQuery = expr.getExpression().split(" = ");
        StringBuilder queryBuilder = new StringBuilder(expr.getExpression().length());
        if (splittedQuery.length > 1) {
            for (int i = 1; i < splittedQuery.length; i++) {
                queryBuilder.append(splittedQuery[i]);
            }    
        } else {
            throw new CanoniserException("CPF query is missing a '='!");
        }
        return queryBuilder.toString();
    }
    
    private static Element createYAWLExpressionElement(final String queryPart, final String targetName) throws ParserConfigurationException, CanoniserException {
        DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        if (!NCNAME_PATTERN.matcher(targetName).matches()) {
            throw new CanoniserException("Could not created YAWL expression element for Object "+targetName+". Invalid name for use in YAWL XML!");
        } else {
            String query = String.format("<%1$s>%2$s</%1$s>", targetName, queryPart);
            try {
                return docBuilder.parse(new ByteArrayInputStream(query.getBytes("UTF-8"))).getDocumentElement();
            } catch (SAXException | IOException e) {
                throw new CanoniserException("Could not created YAWL expression element for Object "+targetName+". Invalid query for use in YAWL XML!", e);
            }   
        }
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

    private static String convertEmbeddedXQueryToStandalone(final String xQuery) throws CanoniserException {
        if (!xQuery.startsWith("<")) {
            return xQuery;
        }
        try {
            final DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Element queryElement = docBuilder.parse(new ByteArrayInputStream(xQuery.getBytes("UTF-8"))).getDocumentElement();
            if (queryElement.hasChildNodes()) {
                String outerTagName = queryElement.getNodeName();
                return xQuery.replaceFirst("<" + outerTagName + ">", "").replaceFirst("</" + outerTagName + ">", "");
            } else {
                return queryElement.getTextContent();
            }
        } catch (SAXException | IOException | ParserConfigurationException e) {
            return xQuery;
        }
    }

    public static String createYAWLInputExpression(final ObjectType netObject, final NetFactsType net) {
        return "/"+net.getId()+"/"+netObject.getName();
    }

    public static String createYAWLOutputExpression(final String taskObjectName, final ExternalTaskFactsType task) {
        return "/"+task.getId()+"/"+taskObjectName;
    }

    /**
     * @param xQuery
     * @param parentNet
     * @return a list of variable names that are used in the YAWL xQuery
     */
    public static Set<String> determinedUsedVariables(String xQuery, NetType parentNet) {
        // This will capture most of the variables that are used in a YAWL input mapping!
        Set<String> usedVariables = new HashSet<String>();
        final Pattern p = Pattern.compile(String.format(XPATH_NET_VARIABLE_REGEX_FORMAT_STRING, parentNet.getOriginalID()));
        final Matcher m = p.matcher(xQuery);
        while (m.find()) {
            final String varName = m.group(1);
            usedVariables.add(varName);
        }
        return usedVariables;
    }

}
