/*
 * Copyright © 2009-2018 The Apromore Initiative.
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

package com.apql.Apql;

import org.apromore.helper.PluginHelper;
import org.apromore.manager.client.helper.DeleteProcessVersionHelper;
import org.apromore.manager.client.helper.MergeProcessesHelper;
import org.apromore.manager.client.helper.SearchForSimilarProcessesHelper;
import org.apromore.manager.client.util.StreamUtil;
import org.apromore.model.Detail;
import org.apromore.model.SummariesType;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.service.pql.DatabaseService;
import org.apromore.service.pql.ExternalId;
import org.apromore.service.pql.PQLService;

import org.apromore.service.pql.ws.model.*;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.out.XesXmlSerializer;
import org.pql.index.IndexStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import static org.springframework.ws.soap.SoapVersion.SOAP_11;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.bind.JAXBElement;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * SOAP proxy to the Process Query Language (PQL) service.
 */
public class PQLServiceClient implements DatabaseService, PQLService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PQLServiceClient.class);
    private static final ObjectFactory WS_CLIENT_FACTORY = new ObjectFactory();

    private WebServiceTemplate webServiceTemplate;

    /**
     * Default Constructor.
     *
     * @param webServiceTemplate the webservice template
     */
    public PQLServiceClient(WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
    }

    /**
     * Constructor for use outside the Spring IoC container.
     *
     * @param siteHost
     * @param sitePort
     * @param siteManager
     */
    public PQLServiceClient(String siteHost, int sitePort, String siteManager) throws SOAPException, URISyntaxException {
        URI uri = new URI("http", null, siteHost, sitePort, siteManager + "/services", null, null);
        System.out.println("Creating PQL service client at composed URI " + uri);
        this.webServiceTemplate = createWebServiceTemplate(new URI("http", null, siteHost, sitePort, siteManager + "/services", null, null));
    }

    public PQLServiceClient(URI endpointURI) throws SOAPException {
        System.out.println("Creating PQL service client at URI " + endpointURI);
        this.webServiceTemplate = createWebServiceTemplate(endpointURI);
    }


    /**
     * @param managerEndpointURI the externally reachable URL of the manager endpoint, e.g. "http://localhost:9000/manager/services"
     */
    private static WebServiceTemplate createWebServiceTemplate(URI managerEndpointURI) throws SOAPException {

        SaajSoapMessageFactory messageFactory = new SaajSoapMessageFactory(MessageFactory.newInstance());
        messageFactory.setSoapVersion(SOAP_11);

        HttpComponentsMessageSender httpSender = new HttpComponentsMessageSender();
        httpSender.setConnectionTimeout(1200000);
        httpSender.setReadTimeout(1200000);

        Jaxb2Marshaller serviceMarshaller = new Jaxb2Marshaller();
        serviceMarshaller.setContextPath("org.apromore.service.pql.ws.model");

        WebServiceTemplate webServiceTemplate = new WebServiceTemplate(messageFactory);
        webServiceTemplate.setMarshaller(serviceMarshaller);
        webServiceTemplate.setUnmarshaller(serviceMarshaller);
        webServiceTemplate.setMessageSender(httpSender);
        webServiceTemplate.setDefaultUri(managerEndpointURI.toString());

        return webServiceTemplate;
    }


    //
    // Method implementing DatabaseService
    //

    @Override
    public List<String> getLabels(String table, String columnName) {
        DBInputMsgType msg = new DBInputMsgType();
        msg.setColumnName(columnName);
        msg.setTable(table);
        JAXBElement<DBInputMsgType> request = WS_CLIENT_FACTORY.createDBRequest(msg);
        JAXBElement<DBOutputMsgType> response = (JAXBElement<DBOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        return response.getValue().getLabels();
    }


    //
    // Methods implementing PQLService
    //

    @Override
    public List<Detail> getDetails() /*throws Exception*/ {
        DetailInputMsgType detail = new DetailInputMsgType();
        JAXBElement<DetailInputMsgType> request = WS_CLIENT_FACTORY.createDetailRequest(detail);
        JAXBElement<DetailOutputMsgType> response = (JAXBElement<DetailOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        //return response.getValue().getDetail();

        // Map results from org.apromore.manager.Detail to org.apromore.service.pql.ws.model.Detail like a savage, since lambdas are only in Java 8.
        List<Detail> results = new ArrayList<>();
        for (org.apromore.service.pql.ws.model.Detail d: response.getValue().getDetail()) {
            Detail result = new Detail();
            result.getDetail().addAll(d.getDetail());
            result.setLabelOne(d.getLabelOne());
            result.setSimilarityLabelOne(d.getSimilarityLabelOne());
            results.add(result);
        }
        return results;
    }

    public SummariesType query(String pql) throws PQLService.QueryException {
        throw new RuntimeException("Not implemented");
    }

    public IndexStatus getIndexStatus(ExternalId id) throws SQLException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * @see PQLService#runAPQLQuery(String, List, String)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<String> runAPQLQuery(final String searchExpression, final List<String> ids, final String userID) /*throws Exception*/ {
        LOGGER.debug("@@@@@@@@@@@@@@@@@@@Preparing RunAPQLRequest.....");

        RunAPQLInputMsgType msg = new RunAPQLInputMsgType();
        msg.setAPQLExpression(searchExpression);
        msg.setUserID(userID);
        msg.getIds().clear();
        msg.getIds().addAll(ids);

        JAXBElement<RunAPQLInputMsgType> request = WS_CLIENT_FACTORY.createRunAPQLRequest(msg);
        LOGGER.debug("@@@@@@@@@@@@@@@@@@@ After Request: ");
        JAXBElement<RunAPQLOutputMsgType> response = (JAXBElement<RunAPQLOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        LOGGER.debug("@@@@@@@@@@@@@@@@@@@ ManagerServiceClient Response List: "+response.getValue().getProcessResult());
        RunAPQLOutputMsgType resp = response.getValue();

        if (resp.getResult().getCode().equals(-1)) {
            //throw new Exception(resp.getResult().getMessage());
            throw new RuntimeException(resp.getResult().getMessage());
        } else {
            LOGGER.debug("@@@@@@@@@@@@@@@@@@@ ManagerServiceClient Response Error: "+response.getValue().getResult().getCode()+" "+response.getValue().getResult().getMessage());
//            return response.getValue().getProcessSummaries();
            return response.getValue().getProcessResult();
        }
    }
}