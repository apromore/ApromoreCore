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

package org.apromore.canoniser.bpmn;

// Java 2 Standard packages

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.bpmn.bpmn.BpmnDefinitions;
import org.apromore.canoniser.bpmn.cpf.CpfCanonicalProcessType;
import org.apromore.canoniser.bpmn.cpf.CpfEventTypeImpl;
import org.apromore.canoniser.bpmn.cpf.CpfTaskType;
import org.apromore.cpf.CPFSchema;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.plugin.PluginRequest;
import org.apromore.plugin.PluginResult;
import org.junit.Ignore;
import org.junit.Test;

// Third party packages
// Local packages

/**
 * Test suite for {@link BPMN20Canoniser}.
 * <p/>
 * A number of these tests are from <cite>Canonization Service for AProMoRe</cite>.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 * @see <a href="http://apromore.org/wp-content/uploads/2010/12/AProMoReCanonization_v1.0.pdf">Canonization
 *      Service for AProMoRe</a>, page 24-25
 * @since 0.4
 */
public class BPMN20CanoniserUnitTest implements TestConstants {

    // Tests

    /**
     * Test {@link BPMN20Canoniser#canonise(InputStream, List<AnnotationsType>, List<CanonicalProcessType>, PluginRequest)}.
     */
    @Test
    public final void testCanonise() throws Exception {

        // Construct test instance
        BPMN20Canoniser canoniser = new BPMN20Canoniser();
        PluginRequest request = null;
        InputStream bpmnInput = new FileInputStream(new File(BPMN_MODELS_DIR, "Case 1.bpmn"));
        List<AnnotationsType> anfs = new ArrayList<>();
        List<CanonicalProcessType> cpfs = new ArrayList<>();
        canoniser.canonise(bpmnInput, anfs, cpfs, request);

        // Inspect the result
        assertEquals(1, anfs.size());
        assertEquals(1, cpfs.size());
        CanonicalProcessType cpf = cpfs.get(0);

        // Expect 3 nodes
        NetType net = cpf.getNet().get(0);
        assertEquals(3, net.getNode().size());

        // Start event "E1"
        NodeType e1 = net.getNode().get(0);
        assertEquals("E1", e1.getName());
        assertEquals(CpfEventTypeImpl.class, e1.getClass());

        // Task "A"
        NodeType a = net.getNode().get(1);
        assertEquals("A", a.getName());
        assertEquals(CpfTaskType.class, a.getClass());

        // End event "E2"
        NodeType e2 = net.getNode().get(2);
        assertEquals("E2", e2.getName());
        assertEquals(CpfEventTypeImpl.class, e2.getClass());

        // Expect 2 edges
        assertEquals(2, net.getEdge().size());

        // Sequence flow from E1 to A
        EdgeType e1_a = net.getEdge().get(0);
        assertNull(e1_a.getConditionExpr());
        assertEquals(e1.getId(), e1_a.getSourceId());
        assertEquals(a.getId(), e1_a.getTargetId());

        // Sequence flow from A to E2
        EdgeType a_e2 = net.getEdge().get(1);
        assertNull(a_e2.getConditionExpr());
        assertEquals(a.getId(), a_e2.getSourceId());
        assertEquals(e2.getId(), a_e2.getTargetId());
    }

    /**
     * Test {@link BPMN20Canoniser#createInitialNativeFormat}.
     */
    @Test
    public final void testCreateInitialNativeFormat() throws Exception {

        // Construct test instance
        ByteArrayOutputStream initialBPMN = new ByteArrayOutputStream();
        BPMN20Canoniser canoniser = new BPMN20Canoniser();
        Date now = new Date();
        PluginRequest request = null;
        canoniser.createInitialNativeFormat(initialBPMN,
                "Test",                         // process name
                "0.0",                          // process version
                getClass().getCanonicalName(),  // process author
                now,                            // creation timestamp
                request);
        initialBPMN.close();

        // Serialize out the empty BPMN model for offline inspection
        OutputStream out = new FileOutputStream(new File(OUTPUT_DIR, "initial.bpmn"));
        out.write(initialBPMN.toByteArray());
        out.close();

        // Validate the empty BPMN model
        BpmnDefinitions.newInstance(new ByteArrayInputStream(initialBPMN.toByteArray()), true);
    }

    /**
     * Test {@link BPMN20Canoniser#deCanonise}.
     * <p/>
     * The most important thing this test does is pass a {@link CanonicalProcessType} rather than a {@link CpfCanonicalProcessType},
     * so that the remarshalling of the CPF classes from {@link org.apromore.cpf} into instrumented classes from
     * {@link org.apromore.canoniser.bpmn.cpf} is exercised.
     */
    @Test
    public final void testDeCanonise() throws Exception {

        CanonicalProcessType cpf = ((JAXBElement<CanonicalProcessType>) JAXBContext.newInstance(CPFSchema.CPF_CONTEXT)
                .createUnmarshaller()
                .unmarshal(new File(CANONICAL_MODELS_DIR, "Basic.cpf"))).getValue();
        AnnotationsType anf = null;
        ByteArrayOutputStream bpmnOutput = new ByteArrayOutputStream();
        PluginRequest request = null;
        BPMN20Canoniser canoniser = new BPMN20Canoniser();
        PluginResult result = canoniser.deCanonise(cpf, anf, bpmnOutput, request);

        // Validate plugin result
        assertNotNull(result.getPluginMessage());
        assertEquals(0, result.getPluginMessage().size());
        //assertEquals("BPMN 2.0 de-canonised OK", result.getPluginMessage().get(0).getMessage());

        // Write BPMN output out for inspection
        new FileOutputStream(new File(OUTPUT_DIR, "Basic.bpmn")).write(bpmnOutput.toByteArray());

        // Validate BPMN output
        BpmnDefinitions.newInstance(new ByteArrayInputStream(bpmnOutput.toByteArray()), true);
    }
}
