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

package org.apromore.service.pql.ws;

import java.util.List;
import javax.xml.bind.JAXBElement;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import org.apromore.service.pql.DatabaseService;
import org.apromore.service.pql.PQLService;
import org.apromore.service.pql.ws.model.DBInputMsgType;
import org.apromore.service.pql.ws.model.DBOutputMsgType;
import org.apromore.service.pql.ws.model.Detail;
import org.apromore.service.pql.ws.model.DetailInputMsgType;
import org.apromore.service.pql.ws.model.DetailOutputMsgType;
import org.apromore.service.pql.ws.model.ObjectFactory;
import org.apromore.service.pql.ws.model.ResultType;
import org.apromore.service.pql.ws.model.RunAPQLInputMsgType;
import org.apromore.service.pql.ws.model.RunAPQLOutputMsgType;


/**
 * Provide PQL querying as a web service.
 */
@Endpoint
public class PQLEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(PQLEndpoint.class.getName());

    private static final ObjectFactory WS_OBJECT_FACTORY = new ObjectFactory();

    private static final String NAMESPACE = "urn:qut-edu-au:schema:apromore:pql";

    private PQLService pqlService;
    private DatabaseService dbService;


    /**
     * Default Constructor for use with CGLib.
     */
    public PQLEndpoint() { }

    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     * @param pqlService  Artem's PQL process query language servlce
     * @param dbService  Luigi's flagrant direct-access-to-the-underlying-database service
     */
    @Inject
    public PQLEndpoint(final PQLService pqlService,  final DatabaseService dbService) {

        this.pqlService = pqlService;
        this.dbService = dbService;
    }

    @PayloadRoot(localPart = "RunAPQLRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<RunAPQLOutputMsgType> runAPQLExpression(@RequestPayload final JAXBElement<RunAPQLInputMsgType> req) {
        LOGGER.info("Executing operation runAPQLExpression");

        RunAPQLInputMsgType input=req.getValue();
        RunAPQLOutputMsgType res = new RunAPQLOutputMsgType();

        ResultType resultType = new ResultType();

        try {
//            pqlService.indexAllModels();
            LOGGER.error("PRIMA RUNAPQL: "+input.getAPQLExpression()+" "+input.getIds()+" "+input.getUserID());
//            pqlService.indexAllModels();
            List<String> results=pqlService.runAPQLQuery(input.getAPQLExpression(), input.getIds(), input.getUserID());
//            List<Detail> details=pqlService.getDetails();

            if(!results.isEmpty() && !results.get(0).matches("([0-9]+[/][a-zA-Z0-9]+[;]?)+[/]([0-9]+([.][0-9]+){1,2})")) {
                LOGGER.error("PQL results contains errors " + results);
                resultType.setMessage("ERRORS");
                resultType.setCode(0);

            }else {
                LOGGER.error("PQL Results: " + results);
                resultType.setMessage("RESULTS");
                resultType.setCode(1);
            }
            res.getProcessResult().addAll(results);
            res.setResult(resultType);
        } catch (Exception ex) {
            LOGGER.error("runAPQLExpression", ex);
            resultType.setCode(-1);
            resultType.setMessage(ex.getMessage()+" runAPQLExpression");
        }

        return WS_OBJECT_FACTORY.createRunAPQLResponse(res);
    }

    @PayloadRoot(localPart = "DBRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<DBOutputMsgType> getProcessesLabels(@RequestPayload final JAXBElement<DBInputMsgType> req) {
        LOGGER.trace("Executing operation getProcessesLabels");

        DBInputMsgType input=req.getValue();
        DBOutputMsgType res = new DBOutputMsgType();

        try {
            List<String> labels=dbService.getLabels(input.getTable(),input.getColumnName());
            res.getLabels().clear();
            res.getLabels().addAll(labels);
        } catch (Exception ex) {
            LOGGER.error("getProcessesLabels", ex);
        }

        return WS_OBJECT_FACTORY.createDBResponse(res);
    }

    @PayloadRoot(localPart = "DetailRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<DetailOutputMsgType> getDetails(@RequestPayload final JAXBElement<DetailInputMsgType> req) {
        LOGGER.trace("Executing operation getDetails");

        DetailInputMsgType input=req.getValue();
        DetailOutputMsgType res = new DetailOutputMsgType();

        ResultType resultType = new ResultType();

        try {
            //List<Detail> details=pqlService.getDetails();
            //res.getDetail().addAll(details);

            // Because org.apromore.model.Detail and org.apromore.service.pql.model.ws.Detail both exists, we have to do this conversion
            // TODO: Remove PQL classes from org.apromore.model so that there's only one Detail class
            for (org.apromore.model.Detail detail: pqlService.getDetails()) {
                Detail d = WS_OBJECT_FACTORY.createDetail();
                d.getDetail().addAll(detail.getDetail());
                d.setLabelOne(detail.getLabelOne());
                d.setSimilarityLabelOne(detail.getSimilarityLabelOne());
                res.getDetail().add(d);
            }

        } catch (Exception ex) {
            LOGGER.error("Failed to getDetails", ex);
            resultType.setCode(-1);
            resultType.setMessage(ex.getMessage() + " getDetails");
        }

        return WS_OBJECT_FACTORY.createDetailResponse(res);
    }
}
