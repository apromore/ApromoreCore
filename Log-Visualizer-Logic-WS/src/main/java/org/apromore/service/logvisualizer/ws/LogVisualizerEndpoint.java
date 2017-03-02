/*
 * Copyright © 2009-2017 The Apromore Initiative.
 *
 * This file is part of "Apromore".
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

package org.apromore.service.logvisualizer.ws;

import java.io.StringBufferInputStream;
import java.util.List;
import javax.xml.bind.JAXBElement;

import javax.inject.Inject;

import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import org.apromore.service.logvisualizer.LogVisualizerService;
import org.apromore.service.logvisualizer.ws.model.VisualizeLogInputMsgType;
import org.apromore.service.logvisualizer.ws.model.VisualizeLogOutputMsgType;
import org.apromore.service.logvisualizer.ws.model.ObjectFactory;

/**
 * Provide log visualization as a web service.
 */
@Endpoint
public class LogVisualizerEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogVisualizerEndpoint.class);

    private static final ObjectFactory WS_OBJECT_FACTORY = new ObjectFactory();

    private static final String NAMESPACE = "urn:qut-edu-au:schema:apromore:logvisualizer";

    private LogVisualizerService logVisualizerService;


    /**
     * Default Constructor for use with CGLib.
     */
    public LogVisualizerEndpoint() { }

    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     *
     * @param logVisualizerService  Raffaele's log visualizer servlce
     */
    @Inject
    public LogVisualizerEndpoint(final LogVisualizerService logVisualizerService) {

        this.logVisualizerService = logVisualizerService;
    }

    @PayloadRoot(localPart = "VisualizeLogRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<VisualizeLogOutputMsgType> visualizeLog(@RequestPayload final JAXBElement<VisualizeLogInputMsgType> req) {
        LOGGER.info("Executing operation visualizeLog");

        VisualizeLogInputMsgType input=req.getValue();
        VisualizeLogOutputMsgType res = new VisualizeLogOutputMsgType();

        try {
            XesXmlParser parser = new XesXmlParser();
            List<XLog> logs = parser.parse(new StringBufferInputStream(input.getLog()));
            if (logs.size() != 1) {
                throw new Exception("Parsed " + logs.size() + " logs rather than exactly 1");
            }

            String result = logVisualizerService.visualizeLog(logs.get(0), input.getActivities(), input.getArcs());
            res.setResult(result);

        } catch (Exception ex) {
            LOGGER.error("visualizeLog", ex);
            res.setResult("Unable to visualize log: " + ex.getMessage());
        }

        return WS_OBJECT_FACTORY.createVisualizeLogResponse(res);
    }
}
