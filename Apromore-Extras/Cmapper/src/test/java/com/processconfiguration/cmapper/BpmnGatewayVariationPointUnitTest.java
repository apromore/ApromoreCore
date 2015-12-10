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

package com.processconfiguration.cmapper;

// Third party packages
import static org.junit.Assert.assertEquals;
import org.junit.Test;

// Local packages
import com.processconfiguration.cmap.TGatewayType;
import org.omg.spec.bpmn._20100524.model.ObjectFactory;
import org.omg.spec.bpmn._20100524.model.TDefinitions;
import org.omg.spec.bpmn._20100524.model.TExclusiveGateway;
import org.omg.spec.bpmn._20100524.model.TGatewayDirection;
import org.omg.spec.bpmn._20100524.model.TProcess;
import org.omg.spec.bpmn._20100524.model.TSequenceFlow;

/**
 * Test suite for {@link BpmnGatewayVariationPoint}.
 */
public class BpmnGatewayVariationPointUnitTest {

    /**
     * Test {@link BpmnGatewayVariationPoint#simplify}.
     */
    @Test
    public void testSimplify1() throws Exception {

        // Construct the test instance
        ObjectFactory factory = new ObjectFactory();
        TDefinitions definitions = factory.createTDefinitions();
        TProcess process = factory.createTProcess();
        definitions.getRootElement().add(factory.createProcess(process));

        TExclusiveGateway gateway = factory.createTExclusiveGateway();
        gateway.setId("test-id");
        gateway.setName("Test Name");
        gateway.setGatewayDirection(TGatewayDirection.DIVERGING);
        process.getFlowElement().add(factory.createExclusiveGateway(gateway));

        TSequenceFlow flow0 = factory.createTSequenceFlow();
        flow0.setName("flow0");
        flow0.setSourceRef(gateway);
        process.getFlowElement().add(factory.createSequenceFlow(flow0));

        TSequenceFlow flow1 = factory.createTSequenceFlow();
        flow1.setName("flow1");
        flow1.setSourceRef(gateway);
        process.getFlowElement().add(factory.createSequenceFlow(flow1));

        BpmnGatewayVariationPoint vp = new BpmnGatewayVariationPoint(gateway, definitions, TGatewayType.DATA_BASED_EXCLUSIVE);

        // Sanity check the structure of the constructed instance
        assertEquals(2, vp.getFlowCount());
        assertEquals(1, vp.getConfigurations().size());
        {
            VariationPoint.Configuration c0 = vp.getConfigurations().get(0);
            assertEquals("1", c0.getCondition());
            assertEquals("1", c0.getFlowCondition(0));
            assertEquals("1", c0.getFlowCondition(1));

            c0.setFlowCondition(0, "f1");
            c0.setFlowCondition(1, "f2");
        }
        
        // Simplify the flow constraints to boolean
        vp.simplify("1");

        // Check the structure of the simplified instance
        assertEquals(4, vp.getConfigurations().size());
        {
            VariationPoint.Configuration c0 = vp.getConfigurations().get(0);
            assertEquals("f1.f2", c0.getCondition());
            assertEquals("1", c0.getFlowCondition(0));
            assertEquals("1", c0.getFlowCondition(1));

            VariationPoint.Configuration c1 = vp.getConfigurations().get(1);
            assertEquals("f1.-f2", c1.getCondition());
            assertEquals("1", c1.getFlowCondition(0));
            assertEquals("0", c1.getFlowCondition(1));

            VariationPoint.Configuration c2 = vp.getConfigurations().get(2);
            assertEquals("-f1.f2", c2.getCondition());
            assertEquals("0", c2.getFlowCondition(0));
            assertEquals("1", c2.getFlowCondition(1));

            VariationPoint.Configuration c3 = vp.getConfigurations().get(3);
            assertEquals("-f1.-f2", c3.getCondition());
            assertEquals("0", c3.getFlowCondition(0));
            assertEquals("0", c3.getFlowCondition(1));
        }
    }
}
