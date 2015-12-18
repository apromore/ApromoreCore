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

package org.apromore.canoniser.yawl.yawl2cpf.patterns.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apromore.canoniser.yawl.utils.TestUtils;
import org.apromore.canoniser.yawl.yawl2cpf.patterns.BasePatternUnitTest;
import org.apromore.cpf.InputExpressionType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.ObjectRefType;
import org.apromore.cpf.OutputExpressionType;
import org.apromore.cpf.SoftType;
import org.apromore.cpf.TaskType;
import org.junit.Test;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class TaskDataUnitTest extends BasePatternUnitTest {

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.BaseYAWL2CPFUnitTest#getYAWLFile()
     */
    @Override
    protected File getYAWLFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "YAWL/Patterns/Data/WPD1TaskData.yawl");
    }

    @Test
    public void testDataTypeDefinitions() throws SAXException, IOException, ParserConfigurationException {
        String dataTypes = yawl2Canonical.getCpf().getDataTypes();
        assertNotNull(dataTypes);
        final DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Element element = docBuilder.parse(new ByteArrayInputStream(dataTypes.getBytes())).getDocumentElement();
        assertNotNull(element);
        assertEquals("xs:schema", element.getNodeName());

//        String yawlDataType = "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"><xs:complexType name=\"YDocumentType\"><xs:sequence><xs:element name=\"id\" type=\"xs:long\" minOccurs=\"0\" /><xs:element name=\"name\" type=\"xs:string\" /></xs:sequence></xs:complexType></xs:schema>";
//        // Otherwise return XML representation
//        Element yawlElement = docBuilder.parse(new ByteArrayInputStream(yawlDataType.getBytes())).getDocumentElement();
//        assertTrue(yawlElement.isEqualNode(element));
    }

    @Test
    public void testNetVariables() {
        final NetType rootNet = yawl2Canonical.getCpf().getNet().get(0);

        assertEquals(5, rootNet.getObject().size());

        final SoftType n1 = (SoftType) getObjectByName(rootNet, "n1");
        assertNotNull(n1);
        final SoftType n2 = (SoftType) getObjectByName(rootNet, "n2");
        assertNotNull(n2);
        final SoftType n3 = (SoftType) getObjectByName(rootNet, "n3");
        assertNotNull(n3);
        final SoftType n4 = (SoftType) getObjectByName(rootNet, "n4");
        assertNotNull(n4);
        final SoftType n5 = (SoftType) getObjectByName(rootNet, "n5");
        assertNotNull(n5);

        assertEquals("boolean", n1.getType());
        assertEquals("string", n2.getType());
        assertEquals("byte", n3.getType());
        assertEquals("string", n4.getType());
        assertEquals("YDocumentType", n5.getType());
    }

    @Test
    public void testTaskVariables() {
        final NetType rootNet = yawl2Canonical.getCpf().getNet().get(0);
        final TaskType taskA = (TaskType) getNodeByName(rootNet, "A");
        assertNotNull(taskA);

        InputExpressionType t1 = findExpression("t1", taskA.getInputExpr());
        assertNotNull(t1);
        assertEquals("t1 = {cpf:getObjectValue('n1')/text()}", t1.getExpression());
        InputExpressionType t2 = findExpression("t2", taskA.getInputExpr());
        assertNotNull(t2);
        assertEquals("t2 = {cpf:getObjectValue('n1')/text()}", t2.getExpression());
        InputExpressionType t3 = findExpression("t3", taskA.getInputExpr());
        assertNotNull(t3);
        assertEquals("t3 = {cpf:getObjectValue('n3')/text()}", t3.getExpression());

        InputExpressionType invalidExpr1 = findExpression("n3", taskA.getInputExpr());
        assertNull(invalidExpr1);

        OutputExpressionType n3 = findExpression("n3", taskA.getOutputExpr());
        assertNotNull(n3);
        assertEquals("n3 = {cpf:getTaskObjectValue('t3')/text()}", n3.getExpression());
        OutputExpressionType n2 = findExpression("n2", taskA.getOutputExpr());
        assertNotNull(n2);
        assertEquals("n2 = {cpf:getTaskObjectValue('t3')/text()}", n2.getExpression());
        OutputExpressionType n4 = findExpression("n4", taskA.getOutputExpr());
        assertNotNull(n4);
        assertEquals("n4 = {cpf:getTaskObjectValue('t4')/text()}", n4.getExpression());

        OutputExpressionType invalidExpr2 = findExpression("t1", taskA.getOutputExpr());
        assertNull(invalidExpr2);
    }

    @Test
    public void testTaskMappings() {
        final NetType rootNet = yawl2Canonical.getCpf().getNet().get(0);

        final TaskType taskA = (TaskType) getNodeByName(rootNet, "A");
        assertNotNull(taskA);
        final TaskType taskB = (TaskType) getNodeByName(rootNet, "B");
        assertNotNull(taskB);

        /********** TASK A *************/

        assertEquals(6, taskA.getObjectRef().size());

        // Test Input
        final List<ObjectRefType> inRefA1 = getObjectInputRef(taskA, getObjectByName(rootNet, "n1"));
        assertEquals(2, inRefA1.size());

        final List<ObjectRefType> inRefA2 = getObjectInputRef(taskA, getObjectByName(rootNet, "n2"));
        assertEquals(0, inRefA2.size());

        final List<ObjectRefType> inRefA3 = getObjectInputRef(taskA, getObjectByName(rootNet, "n3"));
        assertEquals(1, inRefA3.size());

        final List<ObjectRefType> inRefA4 = getObjectInputRef(taskA, getObjectByName(rootNet, "n4"));
        assertEquals(0, inRefA4.size());

        // Test Output
        final List<ObjectRefType> outRefA1 = getObjectOutputRef(taskA, getObjectByName(rootNet, "n1"));
        assertEquals(0, outRefA1.size());

        final List<ObjectRefType> outRefA2 = getObjectOutputRef(taskA, getObjectByName(rootNet, "n2"));
        assertEquals(1, outRefA2.size());

        final List<ObjectRefType> outRefA3 = getObjectOutputRef(taskA, getObjectByName(rootNet, "n3"));
        assertEquals(1, outRefA3.size());

        final List<ObjectRefType> outRefA4 = getObjectOutputRef(taskA, getObjectByName(rootNet, "n4"));
        assertEquals(1, outRefA4.size());

        /********** TASK B *************/

        assertEquals(4, taskB.getObjectRef().size());

        // Test Input
        final List<ObjectRefType> inRefB1 = getObjectInputRef(taskB, getObjectByName(rootNet, "n1"));
        assertEquals(1, inRefB1.size());

        final List<ObjectRefType> inRefB2 = getObjectInputRef(taskB, getObjectByName(rootNet, "n2"));
        assertEquals(1, inRefB2.size());

        final List<ObjectRefType> inRefB3 = getObjectInputRef(taskB, getObjectByName(rootNet, "n3"));
        assertEquals(0, inRefB3.size());

        final List<ObjectRefType> inRefB4 = getObjectInputRef(taskB, getObjectByName(rootNet, "n4"));
        assertEquals(0, inRefB4.size());

        // Test Output
        final List<ObjectRefType> outRefB1 = getObjectOutputRef(taskB, getObjectByName(rootNet, "n1"));
        assertEquals(0, outRefB1.size());

        final List<ObjectRefType> outRefB2 = getObjectOutputRef(taskB, getObjectByName(rootNet, "n2"));
        assertEquals(1, outRefB2.size());

        final List<ObjectRefType> outRefB3 = getObjectOutputRef(taskB, getObjectByName(rootNet, "n3"));
        assertEquals(1, outRefB3.size());

        final List<ObjectRefType> outRefB4 = getObjectOutputRef(taskB, getObjectByName(rootNet, "n4"));
        assertEquals(0, outRefB4.size());
    }
}
