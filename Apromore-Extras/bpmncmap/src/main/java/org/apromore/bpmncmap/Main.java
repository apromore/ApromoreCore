/*
 * Copyright © 2009-2016 The Apromore Initiative.
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

package org.apromore.bpmncmap;

// Java 2 Standard Edition classes
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringBufferInputStream;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

// Local classes
import com.processconfiguration.cmap.CBpmnType;
import com.processconfiguration.cmap.CMAP;
import com.processconfiguration.dcl.DCL;
import com.processconfiguration.dcl.FactType;
import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDPairing;
import org.apromore.bpmncmap.parser.ParseException;
import org.apromore.bpmncmap.parser.Parser;
import org.apromore.canoniser.bpmn.bpmn.BpmnDefinitions;
import org.omg.spec.bpmn._20100524.model.TBaseElement;
import org.omg.spec.bpmn._20100524.model.TFlowElement;
import org.omg.spec.bpmn._20100524.model.TProcess;
import org.omg.spec.bpmn._20100524.model.TRootElement;

/**
 * Command line configurator.
 */
public class Main {

    /**
     * Command line configurator.
     *
     * @param arg  command line arguments
     */
    public static void main(String[] arg) throws IOException, JAXBException {

	// Check that the command line arguments are in order
	if (arg.length != 2) {
		System.err.println("Usage: bpmncmap file2.cmap file3.dcl < input.bpmn > output.bpmn");
        	System.exit(-1);  // indicate termination due to failure
	}

	// Parse the Configurable BPMN process model
	BpmnDefinitions bpmn = BpmnDefinitions.newInstance(System.in, true);

        // Parse the configuration mapping
        CMAP cmap = (CMAP) JAXBContext.newInstance("com.processconfiguration.cmap").createUnmarshaller().unmarshal(new File(arg[0]));
        //JAXBContext.newInstance("com.processconfiguration.cmap").createMarshaller().marshal(cmap, System.out);

        // Parse the configuration
        DCL dcl = (DCL) JAXBContext.newInstance("com.processconfiguration.dcl").createUnmarshaller().unmarshal(new File(arg[1]));

        // Mutate the BPMN process model so that it is individualized
	configure(bpmn, cmap, dcl);

        // Serialize the individualized BPMN
        bpmn.marshal(System.out, true);

        System.exit(0);  // indicate successful termination
    }

    public static void configure(final BpmnDefinitions bpmn, CMAP cmap, DCL dcl) {

        final Parser parser = new Parser(new StringBufferInputStream("1"));
        parser.init();
        final BDD g = parser.getFactory().one();

	// Iterate through the facts in the configuration (DCL)
        dcl.accept(new com.processconfiguration.dcl.TraversingVisitor(new com.processconfiguration.dcl.DepthFirstTraverserImpl(),
                                                                      new com.processconfiguration.dcl.BaseVisitor() {
            @Override
            public void visit(FactType fact) {
                //System.err.println("Fact " + fact.getId() + " is " + fact.isValue());
                BDD gTerm = parser.findVariable(fact.getId());
                if (!fact.isValue()) {
                  gTerm = gTerm.not();
                }
                g.andWith(gTerm);
            }
	}));

	// Map the configurable BPMN elements
        final Map<String, TFlowElement> bpmnIdMap = new HashMap<>();
        for (JAXBElement<? extends TRootElement> jaxbElement1: bpmn.getRootElement()) {
            TRootElement root = jaxbElement1.getValue();
            if (root instanceof TProcess) {
                for (JAXBElement<? extends TFlowElement> jaxbElement2: ((TProcess) root).getFlowElement()) {
                   TFlowElement flow = jaxbElement2.getValue();
                   bpmnIdMap.put(flow.getId(), flow);
                }
            }
        }

        // Iterate through the configuration mappings (CMAP)
        cmap.accept(new com.processconfiguration.cmap.TraversingVisitor(new com.processconfiguration.cmap.DepthFirstTraverserImpl(),
                                                                        new com.processconfiguration.cmap.BaseVisitor() {
            @Override
            public void visit(CBpmnType.Configurable configurable) {
                //System.err.println("Configure BPMN element " + configurable.getBpmnid());
                int validConfigurationCount = 0;
                for (CBpmnType.Configurable.Configuration configuration: configurable.getConfiguration()) {
                    //System.err.println("  Condition " + configuration.getCondition());

                    BDD bdd = null;
                    BDD bdd2 = null;
                    try {
                        parser.ReInit(new StringBufferInputStream(configuration.getCondition()));
                        bdd = parser.AdditiveExpression();
                        //System.err.println("  BDD " + bdd);
                        //System.err.println("  G " + g);
                        bdd2 = bdd.restrict(g);
                        //System.err.println("  BDD[G] " + bdd2);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (bdd2.isOne()) {
                         validConfigurationCount++;

                         // We ought not to ever get more than one valid configuration
                         if (validConfigurationCount > 1) {
                             System.err.println(configurable.getBpmnid() + " satisfies an additional constraint: " + configuration.getCondition());
                             continue;
                         } 

                         // Apply the valid configuration to the BPMN
                         TBaseElement base = bpmnIdMap.get(configurable.getBpmnid());
                         if (base == null) {
                             System.err.println(configurable.getBpmnid() + " not found in BPMN model");
                         }
                         for (Object object: base.getExtensionElements().getAny()) {
                             //System.err.println("Checking " + object);
                             if (object instanceof com.processconfiguration.Configurable) {
                                 //System.err.println("Accepting " + object);
                                 ((com.processconfiguration.Configurable) object).setConfiguration(newConfiguration(configuration, bpmnIdMap));
                                 //System.err.println("Accepted " + object);
                             }
                         }
                    }
                }
                if (validConfigurationCount == 0) {
                    System.err.println(configurable.getBpmnid() + " didn't satisfy the condition for any configuration");
                }
            }
        }));
    }

    /**
     * Transfer a configuration from the CMAP schema to the C-BPMN schema.
     *
     * @param source  a configuration from the source CMAP file
     * @return an equivalent configuration, suitable for the targeted configured BPMN file
     */
    static com.processconfiguration.Configurable.Configuration newConfiguration(final CBpmnType.Configurable.Configuration source,
                                                                                final  Map<String, TFlowElement> bpmnIdMap ) {
        com.processconfiguration.Configurable.Configuration target = new com.processconfiguration.Configurable.Configuration();

        for (String nmtoken: source.getSourceRefs()) {
            System.err.println("Source " + nmtoken);
            target.getSourceRefs().add(bpmnIdMap.get(nmtoken));
        }

        for (String nmtoken: source.getTargetRefs()) {
            System.err.println("Target " + nmtoken);
            target.getTargetRefs().add(bpmnIdMap.get(nmtoken));
        }

        if (source.getType() != null) {
            target.setType(com.processconfiguration.TGatewayType.fromValue(source.getType().value()));
        } else {
            target.setType(com.processconfiguration.TGatewayType.EVENT_BASED_EXCLUSIVE);
        }

        return target;
    }
}
