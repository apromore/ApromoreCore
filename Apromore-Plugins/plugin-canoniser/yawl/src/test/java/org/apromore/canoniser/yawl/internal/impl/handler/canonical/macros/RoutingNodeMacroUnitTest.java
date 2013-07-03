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
