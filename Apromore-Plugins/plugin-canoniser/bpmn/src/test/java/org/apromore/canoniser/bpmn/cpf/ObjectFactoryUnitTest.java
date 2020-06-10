/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.xml.sax.SAXException;

// Third party packages
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

// Local packages
import org.apromore.canoniser.bpmn.TestConstants;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.CPFSchema;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.EventType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.XORJoinType;
import org.apromore.cpf.XORSplitType;

/**
 * Test suite for {@link ObjectFactory}.
 *
 * A number of these tests are from <cite>Canonization Service for AProMoRe</cite>.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 * @since 0.3
 * @see <a href="http://apromore.org/wp-content/uploads/2010/12/AProMoReCanonization_v1.0.pdf">Canonization
 *     Service for AProMoRe</a>, page 24-25
 */
public class ObjectFactoryUnitTest implements TestConstants {

    /**
     * Test CPF convenience methods.
     */
    @Test
    public void testParseCpf() throws Exception {

        CpfCanonicalProcessType cpf = CpfCanonicalProcessType.newInstance(new FileInputStream(new File(CANONICAL_MODELS_DIR, "Basic.cpf")), true);

        NetType net = cpf.getNet().get(0);

        assertEquals(3, net.getNode().size());
        CpfEventType e1 = (CpfEventType) net.getNode().get(0);
        CpfTaskType  e2 =  (CpfTaskType) net.getNode().get(1);
        CpfEventType e3 = (CpfEventType) net.getNode().get(2);

        assertEquals(2, net.getEdge().size());
        EdgeType e4 = net.getEdge().get(0);
        EdgeType e5 = net.getEdge().get(1);

        assertEquals(0, e1.getIncomingEdges().size());
        assertEquals(1, e1.getOutgoingEdges().size());

        assertEquals(1, e2.getIncomingEdges().size());
        assertEquals(1, e2.getOutgoingEdges().size());

        assertEquals(1, e3.getIncomingEdges().size());
        assertEquals(0, e3.getOutgoingEdges().size());
    }
}
