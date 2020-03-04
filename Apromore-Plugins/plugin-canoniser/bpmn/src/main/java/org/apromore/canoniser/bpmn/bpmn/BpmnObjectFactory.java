/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2012, 2014 - 2017 Queensland University of Technology.
 * Copyright (C) 2018, 2020 The University of Melbourne.
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.canoniser.bpmn.bpmn;

// Java 2 Standard packages
import javax.xml.bind.annotation.XmlRegistry;

// Local classes
import org.omg.spec.bpmn._20100524.model.ObjectFactory;

/**
 * Element factory for a BPMN 2.0 object model with canonisation methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
@XmlRegistry
public class BpmnObjectFactory extends ObjectFactory {

    @Override
    public BpmnAssociation createTAssociation() {
        return new BpmnAssociation();
    }

    @Override
    public BpmnCallActivity createTCallActivity() {
        return new BpmnCallActivity();
    }

    @Override
    public BpmnDefinitions createTDefinitions() {
        return new BpmnDefinitions();
    }

    @Override
    public BpmnLane createTLane() {
        return new BpmnLane();
    }

    @Override
    public BpmnParticipant createTParticipant() {
        return new BpmnParticipant();
    }

    @Override
    public BpmnProcess createTProcess() {
        return new BpmnProcess();
    }

    @Override
    public BpmnSubProcess createTSubProcess() {
        return new BpmnSubProcess();
    }

    @Override
    public BpmnSequenceFlow createTSequenceFlow() {
        return new BpmnSequenceFlow();
    }

    @Override
    public BpmnTask createTTask() {
        return new BpmnTask();
    }
}
