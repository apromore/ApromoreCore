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

package org.apromore.service.ibpstruct.impl;

import org.processmining.contexts.uitopia.UIContext;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.plugins.bpmn.BpmnDefinitions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.apromore.service.ibpstruct.IBPStructService;

import au.edu.qut.structuring.StructuringService;

/**
 * Created by Adriano Augusto on 18/04/2016.
 */
@Service
public class IBPStructServiceImpl implements IBPStructService {
    private static final Logger LOGGER = LoggerFactory.getLogger(IBPStructServiceImpl.class);

    @Override
    public BPMNDiagram structureProcess( BPMNDiagram model,
                                         String  policy,
                                         int     maxDepth,
                                         int     maxSolutions,
                                         int     maxChildren,
                                         int     maxStates,
                                         int     maxMinutes,
                                         boolean timeBounded,
                                         boolean keepBisimulation,
                                         boolean forceStructuring )
    {
        StructuringService ss = new StructuringService();
        return ss.structureDiagram( model, policy, maxDepth, maxSolutions, maxChildren,
                                    maxStates, maxMinutes, timeBounded, keepBisimulation, forceStructuring);
    }

//    @Override
//    public String structureProcess( BPMNDiagram model,
//                                         String  policy,
//                                         int     maxDepth,
//                                         int     maxSolutions,
//                                         int     maxChildren,
//                                         int     maxStates,
//                                         int     maxMinutes,
//                                         boolean timeBounded,
//                                         boolean keepBisimulation,
//                                         boolean forceStructuring )
//    {
//        StructuringService ss = new StructuringService();
//
//        BPMNDiagram structuredDiagram = ss.structureDiagram( model, policy, maxDepth, maxSolutions, maxChildren, maxStates, maxMinutes, timeBounded, keepBisimulation, forceStructuring);
//        String structuredModel;
//
//        UIContext context = new UIContext();
//        UIPluginContext uiPluginContext = context.getMainPluginContext();
//        BpmnDefinitions.BpmnDefinitionsBuilder definitionsBuilder = new BpmnDefinitions.BpmnDefinitionsBuilder(uiPluginContext, structuredDiagram);
//        BpmnDefinitions definitions = new BpmnDefinitions("definitions", definitionsBuilder);
//
//        StringBuilder sb = new StringBuilder();
//        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
//                "<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\"\n " +
//                "xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\"\n " +
//                "xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\"\n " +
//                "xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\"\n " +
//                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n " +
//                "targetNamespace=\"http://www.omg.org/bpmn20\"\n " +
//                "xsi:schemaLocation=\"http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd\">");
//        sb.append(definitions.exportElements());
//        sb.append("</definitions>");
//
//        structuredModel = sb.toString();
//
//        structuredModel = structuredModel.replaceAll("\n", "&#10;");
//        structuredModel = structuredModel.replaceAll(">&#10;", ">\n");
//        structuredModel = structuredModel.replaceAll("\"&#10;", "\"\n");
//
//        return structuredModel;
//    }
}
