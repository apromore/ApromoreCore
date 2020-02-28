/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
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

package org.apromore.portal.portal;

import org.apromore.model.*;
import org.apromore.portal.dialogController.MainController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.inject.Inject;
import javax.xml.bind.JAXBElement;
import java.util.List;

/**
 * Created by corno on 9/07/2014.
 */
@Endpoint
public class PortalEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(PortalEndpoint.class.getName());

    private static final ObjectFactory WS_OBJECT_FACTORY = new ObjectFactory();

    private static final String NAMESPACE = "urn:qut-edu-au:schema:apromore:manager";

//    @Inject
    public PortalEndpoint() { }


    @PayloadRoot(namespace = NAMESPACE, localPart = "TabAPQLRequest")
    @ResponsePayload
    public JAXBElement<TabAPQLOutputMsgType> addNewTab(@RequestPayload final JAXBElement<TabAPQLInputMsgType> req) {
        LOGGER.debug("-------------------------ADDRESULTdebug: ");
        TabAPQLInputMsgType input = req.getValue();
        TabAPQLOutputMsgType res = new TabAPQLOutputMsgType();

        ResultType result = new ResultType();
        res.setResult(result);
        List<ResultPQL> results = input.getResults();
        List<Detail> details = input.getDetails();

        try {
            LOGGER.debug("-------------------------addNewTab prima: "+results+" "+input.getUserID()+" "+input.getQuery());
            MainController.getController().addResult(results, input.getUserID(), details, input.getQuery(),input.getNameQuery());

            result.setCode(0);
            result.setMessage("");

        } catch (Exception ex) {
            LOGGER.debug("Unable to add new PQL result tab", ex);
            result.setCode(-2);
            result.setMessage(ex.getMessage());
        }

        return WS_OBJECT_FACTORY.createTabAPQLResponse(res);
    }

}
