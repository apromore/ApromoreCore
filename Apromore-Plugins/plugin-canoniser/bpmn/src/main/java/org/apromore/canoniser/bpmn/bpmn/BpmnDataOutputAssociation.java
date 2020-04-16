/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012, 2014 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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

package org.apromore.canoniser.bpmn.bpmn;

// Java 2 Standard packages
import java.util.List;
import java.util.logging.Logger;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

// Local packages
import org.apromore.canoniser.bpmn.Initialization;
import org.apromore.canoniser.bpmn.cpf.CpfObjectRefType;
import org.apromore.canoniser.exception.CanoniserException;
import static org.apromore.cpf.InputOutputType.OUTPUT;
import org.apromore.cpf.TypeAttribute;
import org.omg.spec.bpmn._20100524.model.TActivity;
import org.omg.spec.bpmn._20100524.model.TDataOutput;
import org.omg.spec.bpmn._20100524.model.TDataOutputAssociation;
import org.omg.spec.bpmn._20100524.model.TInputOutputSpecification;
import org.omg.spec.bpmn._20100524.model.TInputSet;
import org.omg.spec.bpmn._20100524.model.TOutputSet;

/**
 * BPMN Data Output Association with canonisation methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class BpmnDataOutputAssociation extends TDataOutputAssociation {

    private static final Logger LOGGER = Logger.getLogger(BpmnDataOutputAssociation.class.getCanonicalName());

    /** No-arg constructor. */
    public BpmnDataOutputAssociation() { }

    /**
     * Construct a BPMN Data Output Association corresponding to a CPF ObjectRef.
     *
     * @param objectRef  a CPF Object Reference of type {@link #OUTPUT}
     * @param parent  the BPMN activity containing this instance
     * @param initializer  BPMN document construction state
     * @throws CanoniserException  if the association can't be constructed
     */
    public BpmnDataOutputAssociation(final CpfObjectRefType objectRef,
                                     final TActivity        parent,
                                     final Initializer      initializer) throws CanoniserException {
        assert OUTPUT.equals(objectRef.getType()) : objectRef.getId() + " is not typed as an output";
        initializer.populateBaseElement(this, objectRef);

        initializer.defer(new Initialization() {
            public void initialize() {
                //assert initializer.findElement(objectRef.getObjectId()) != null;
                setTargetRef(initializer.findElement(objectRef.getObjectId()));

                boolean sourceRefSet = false;
                if (objectRef.getAttribute() != null) {
                    for (TypeAttribute attribute: objectRef.getAttribute()) {
                        switch (attribute.getName()) {
                        case "bpmn:dataOutputAssociation.sourceRef":
                            String id = attribute.getValue();
                            if (parent.getIoSpecification() != null && parent.getIoSpecification().getDataOutput() != null) {
                                for (TDataOutput dataOutput: parent.getIoSpecification().getDataOutput()) {
                                    if (id.equals(dataOutput.getId())) {
                                        // There's a bug in JAXB that makes it impossible to directly add elements to collections of IDREFs, like sourceRef
                                        // As a workaround, I put the id of the sourceRef into an attribute and fix it later using XSLT
                                        getOtherAttributes().put(new QName("workaround"), dataOutput.getId());

                                        /*
                                        // This is what the code would look like if the workaround wasn't necessary:
                                        BpmnObjectFactory factory = new BpmnObjectFactory();
                                        getSourceRef().add((JAXBElement) factory.createDataOutput(dataOutput));
                                        */
                                        sourceRefSet = true;
                                        break;
                                    }
                                }
                            }
                            break;
                        }
                    }
                }

                // Synthesize a dummy ioSpecification/dataOutput if the CPF didn't have a bpmn:dataOutputAssociation.sourceRef attribute
                if (!sourceRefSet) {
                    BpmnObjectFactory factory = initializer.getFactory();

                    TInputOutputSpecification ioSpec = parent.getIoSpecification();
                    if (ioSpec == null) {
                        ioSpec = factory.createTInputOutputSpecification();
                        parent.setIoSpecification(ioSpec);
                    }
                    assert ioSpec != null;

                    TDataOutput dataOutput = factory.createTDataOutput();
                    dataOutput.setId(initializer.newId(parent.getId() + "_dataOutput"));
                    dataOutput.setName("Apromore generated");
                    ioSpec.getDataOutput().add(dataOutput);

                    List<TInputSet> inputSets = ioSpec.getInputSet();
                    assert inputSets != null;
                    if (inputSets.isEmpty())  {
                        TInputSet inputSet = factory.createTInputSet();
                        inputSet.setId(initializer.newId(parent.getId() + "_inputSet"));
                        inputSet.setName("Apromore generated");
                        inputSets.add(inputSet);
                    }

                    List<TOutputSet> outputSets = ioSpec.getOutputSet();
                    assert outputSets != null;
                    if (outputSets.isEmpty()) {
                        TOutputSet outputSet = factory.createTOutputSet();
                        outputSet.setId(initializer.newId(parent.getId() + "_outputSet"));
                        outputSet.setName("Apromore generated");
                        outputSet.getDataOutputRefs().add((JAXBElement) factory.createDataOutput(dataOutput));  // TODO: figure out why this doesn't work
                        outputSets.add(outputSet);
                    }

                    // There's a bug in JAXB that makes it impossible to directly add elements to collections of IDREFs, like sourceRef
                    // As a workaround, I put the id of the sourceRef into an attribute and fix it later using XSLT
                    getOtherAttributes().put(new QName("workaround"), dataOutput.getId());

                    LOGGER.info("Synthesized bpmn:dataOutput " + dataOutput.getId() + " for cpf:objectRef " + objectRef.getId());
                }
            }
        });
    }
}
