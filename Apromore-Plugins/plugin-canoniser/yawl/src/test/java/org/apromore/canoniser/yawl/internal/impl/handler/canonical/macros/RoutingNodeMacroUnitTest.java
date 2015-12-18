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

package org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.impl.context.CanonicalConversionContext;
import org.apromore.canoniser.yawl.internal.utils.ConversionUtils;
import org.apromore.canoniser.yawl.utils.NoOpMessageManager;
import org.apromore.canoniser.yawl.utils.TestUtils;
import org.apromore.cpf.CPFSchema;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.JoinType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.SplitType;
import org.junit.Test;
import org.xml.sax.SAXException;
import org.yawlfoundation.yawlschema.ControlTypeCodeType;

public class RoutingNodeMacroUnitTest {

    @Test
    public void testRewrite() throws FileNotFoundException, JAXBException, SAXException, CanoniserException {
        final File file = new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/orderfulfillment.yawl.cpf");
        final CanonicalProcessType cpf = CPFSchema.unmarshalCanonicalFormat(new BufferedInputStream(new FileInputStream(file)), true).getValue();
        final CanonicalConversionContext context = new CanonicalConversionContext(cpf, new AnnotationsType(), new NoOpMessageManager());

        final RoutingNodeMacro macro = new RoutingNodeMacro(context);

        final Set<String> splitIdList = new HashSet<String>();
        final Set<String> joinIdList = new HashSet<String>();

        for (final NetType net : cpf.getNet()) {
            for (final NodeType node : net.getNode()) {
                if (node instanceof SplitType) {
                    splitIdList.add(node.getId());
                }
                if (node instanceof JoinType) {
                    joinIdList.add(node.getId());
                }
            }
        }

        macro.rewrite(cpf);

        // Some manual checks
        assertNotNull(context.getNodeById("C-Issue-Trackpoint-Notice-813"));
        assertEquals(ControlTypeCodeType.XOR, context.getControlFlowContext().getElementInfo("C-Issue-Trackpoint-Notice-813").getJoinType().getCode());

        // No Split or Joins should be present anymore
        for (final NetType net : cpf.getNet()) {
            for (final NodeType node : net.getNode()) {
                assertFalse(node instanceof SplitType);
                assertFalse(node instanceof JoinType);
            }
        }

        // Check if all Edges are removed correctly
        for (final NetType net : cpf.getNet()) {
            for (final EdgeType edge : net.getEdge()) {
                assertFalse("Edge from Join not correctly removed " + ConversionUtils.toString(edge), joinIdList.contains(edge.getSourceId()));
                assertFalse("Edge from Split not correctly removed " + ConversionUtils.toString(edge), splitIdList.contains(edge.getSourceId()));
                assertFalse("Edge to Join not correctly removed " + ConversionUtils.toString(edge), joinIdList.contains(edge.getTargetId()));
                assertFalse("Edge to Split not correctly removed " + ConversionUtils.toString(edge), splitIdList.contains(edge.getTargetId()));
            }
        }

        final CheckValidModelMacro validMacro = new CheckValidModelMacro(context);
        assertFalse("Process invalid after rewriting Nodes", validMacro.rewrite(cpf));

        final MESEToSESEMacro meseMacro = new MESEToSESEMacro(context);
        assertFalse("Process has multiple source nodes after rewriting Nodes", meseMacro.rewrite(cpf));

        final SEMEToSESEMacro semeMacro = new SEMEToSESEMacro(context);
        assertFalse("Process has multiple sink nodes after rewriting Nodes", semeMacro.rewrite(cpf));
    }

}
