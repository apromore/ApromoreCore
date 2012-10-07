package org.apromore.canoniser.yawl.internal.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.StringReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFunction;
import javax.xml.xpath.XPathFunctionException;
import javax.xml.xpath.XPathFunctionResolver;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.CPFSchema;
import org.apromore.cpf.ExpressionType;
import org.apromore.cpf.InputExpressionType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.ObjectType;
import org.apromore.cpf.OutputExpressionType;
import org.apromore.cpf.SoftType;
import org.apromore.cpf.TaskType;
import org.junit.Test;
import org.springframework.util.xml.SimpleNamespaceContext;
import org.xml.sax.InputSource;
import org.yawlfoundation.yawlschema.ExternalTaskFactsType;
import org.yawlfoundation.yawlschema.NetFactsType;

public class ExpressionUtilsTest {

    @Test
    public void testCreateExpressionReferencingNetObject() throws CanoniserException, XPathExpressionException {
        try {
            ExpressionUtils.createExpressionReferencingNetObject("test", new NetType());
            fail();
        } catch (CanoniserException e) {
        }

        NetType net = new NetType();
        ObjectType obj = new ObjectType();
        obj.setName("test");
        net.getObject().add(obj);
        String expression = ExpressionUtils.createExpressionReferencingNetObject("test", net);
        assertEquals("cpf:getObjectValue('test')", expression);

        XPathFactory factory = XPathFactory.newInstance();
        factory.setXPathFunctionResolver(new XPathFunctionResolver() {

            private final QName name = new QName("http://www.apromore.org/cpf", "getObjectValue");

            @Override
            public XPathFunction resolveFunction(final QName functionName, final int arity) {
                if (name.equals(functionName)) {
                    return new XPathFunction() {

                        @Override
                        public Object evaluate(@SuppressWarnings("rawtypes") final List args) throws XPathFunctionException {
                            if (args.size() == 1 && args.get(0) instanceof String) {
                                String objectName = (String)args.get(0);
                                if (objectName.equals("test")) {
                                    return "testObjectValue";
                                }
                            }
                            throw new XPathFunctionException("Did not find Object invalid argument!");
                        }
                    };
                }
                return null;
            }
        });
        XPath newXPath = factory.newXPath();
        SimpleNamespaceContext namespaceContext = new SimpleNamespaceContext();
        namespaceContext.bindNamespaceUri("cpf", "http://www.apromore.org/cpf");
        newXPath.setNamespaceContext(namespaceContext);
        String result = newXPath.compile(expression).evaluate(new InputSource(new StringReader("<test></test>")));
        assertEquals("testObjectValue", result);
    }

    @Test
    public void testCreateQueryReferencingTaskVariables() throws CanoniserException {
        ExternalTaskFactsType task = new ExternalTaskFactsType();
        task.setId("Freight_in_Transit");
        TaskType cpfTask = new TaskType();
        cpfTask.setId("C-Freight_in_Transit");
        String rewrittenQuery1 = ExpressionUtils.createQueryReferencingTaskVariables("<test>{/Freight_in_Transit/AcceptanceCertificate/*}</test>",
                task);
        assertEquals("{cpf:getTaskObjectValue('AcceptanceCertificate')/*}", rewrittenQuery1);
        assertEquals("{/Ordering/PO_timedout/text()}",
                ExpressionUtils.createQueryReferencingTaskVariables("<test>{/Ordering/PO_timedout/text()}</test>", task));
        assertEquals("{cpf:getTaskObjectValue('PO_timedout')/text()}",
                ExpressionUtils.createQueryReferencingTaskVariables("<test>{/Freight_in_Transit/PO_timedout/text()}</test>", task));
        assertEquals("{current-date()}", ExpressionUtils.createQueryReferencingTaskVariables("<test>{current-date()}</test>", task));
        assertEquals("current-date()", ExpressionUtils.createQueryReferencingTaskVariables("current-date()", task));

    }

    @Test
    public void testCreateQueryReferencingNetObjects() throws CanoniserException {

        NetType cpfNet = new NetType();
        cpfNet.setId("testId");
        cpfNet.setOriginalID("Film_Production_Process");

        Set<String> objectList = new HashSet<String>();
        SoftType softType = new SoftType();
        softType.setName("cameraSheetNo");
        objectList.add(softType.getName());
        SoftType softType2 = new SoftType();
        softType2.setName("callSheetToday");
        objectList.add(softType2.getName());
        SoftType softType3 = new SoftType();
        softType3.setName("camRollsToday");
        objectList.add(softType3.getName());

        // Add duplicate
        objectList.add(softType.getName());

        String complexQuery = "<cameraInfo><sheetNumber>{/Film_Production_Process/cameraSheetNo/text()}</sheetNumber>"
                + "<camRoll/>"
                + "<studios_location>{for $x in /Film_Production_Process/callSheetToday/location/singleLocation     return concat(' ',$x/locationName,' @ ',$x/address,'.')}</studios_location>"
                + "</cameraInfo>";

        String simpleQuery = "<camRolls>{/Film_Production_Process/camRollsToday/text()}</camRolls>";

        String constantQuery = "true";

        String rewrittenComplex = ExpressionUtils.createQueryReferencingNetObjects(complexQuery, cpfNet, objectList);
        assertEquals(
                "<sheetNumber>{cpf:getObjectValue('cameraSheetNo')/text()}</sheetNumber><camRoll/><studios_location>{for $x in cpf:getObjectValue('callSheetToday')/location/singleLocation     return concat(' ',$x/locationName,' @ ',$x/address,'.')}</studios_location>",
                rewrittenComplex);
        String rewrittenSimple = ExpressionUtils.createQueryReferencingNetObjects(simpleQuery, cpfNet, objectList);
        assertEquals("{cpf:getObjectValue('camRollsToday')/text()}", rewrittenSimple);
        String rewrittenConstant = ExpressionUtils.createQueryReferencingNetObjects(constantQuery, cpfNet, objectList);
        assertEquals(constantQuery, rewrittenConstant);
    }

    @Test
    public void testDetermineResultType() {
        ExpressionType expr = new ExpressionType();
        expr.setLanguage(CPFSchema.EXPRESSION_LANGUAGE_XPATH);
        expr.setExpression("{cpf:getObjectValue('camRollsToday')/text()}");
        assertEquals("string", ExpressionUtils.determineResultType(expr));
        expr.setExpression("boolean({cpf:getObjectValue('camRollsToday')/text()})");
        assertEquals("boolean", ExpressionUtils.determineResultType(expr));
        expr.setExpression("cpf:getObjectValue('camRollsToday')");
        assertEquals("anyType", ExpressionUtils.determineResultType(expr));
    }

    @Test
    public void testConvertXQueryToYAWLNetQuery() throws CanoniserException {
        InputExpressionType expr = new InputExpressionType();
        expr.setLanguage(CPFSchema.EXPRESSION_LANGUAGE_XQUERY);
        expr.setExpression("test = {cpf:getObjectValue('camRollsToday')/text()}");
        NetFactsType net = new NetFactsType();
        net.setId("netId");
        String yawlXQuery = ExpressionUtils.convertXQueryToYAWLNetQuery(expr, net);
        assertEquals("<test>{/netId/camRollsToday/text()}</test>", yawlXQuery);
    }

    @Test
    public void testConvertXQueryToYAWLNetQueryShouldFail() {
        InputExpressionType expr = new InputExpressionType();
        expr.setLanguage(CPFSchema.EXPRESSION_LANGUAGE_MVEL);
        expr.setExpression("test = FOOBAR");
        NetFactsType net = new NetFactsType();
        net.setId("netId");
        try {
            ExpressionUtils.convertXQueryToYAWLNetQuery(expr, net);
            fail();
        } catch (CanoniserException e) {

        }
    }


    @Test
    public void testConvertXQueryToYAWLTaskQuery() throws CanoniserException {
        OutputExpressionType expr = new OutputExpressionType();
        expr.setLanguage(CPFSchema.EXPRESSION_LANGUAGE_XQUERY);
        expr.setExpression("test = {cpf:getTaskObjectValue('camRollsToday')/text()}");
        ExternalTaskFactsType task = new ExternalTaskFactsType();
        task.setId("taskId");
        String yawlXQuery = ExpressionUtils.convertXQueryToYAWLTaskQuery(expr, task);
        assertEquals("<test>{/taskId/camRollsToday/text()}</test>", yawlXQuery);
    }

    @Test
    public void testConvertXQueryToYAWLTaskQueryShouldFail() throws CanoniserException {
        OutputExpressionType expr = new OutputExpressionType();
        expr.setLanguage(CPFSchema.EXPRESSION_LANGUAGE_MVEL);
        expr.setExpression("test = FOOBAR");
        ExternalTaskFactsType task = new ExternalTaskFactsType();
        task.setId("taskId");
        try {
            ExpressionUtils.convertXQueryToYAWLTaskQuery(expr, task);
            fail();
        } catch (CanoniserException e) {
        }
    }

}
