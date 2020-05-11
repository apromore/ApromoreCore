/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2011, 2012, 2015 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * Copyright (C) 2020, Apromore Pty Ltd.
 *
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

package org.apromore.portal.client;

import java.net.URI;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import static org.springframework.ws.soap.SoapVersion.SOAP_11;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import org.apromore.model.*;

/**
 * Created by corno on 9/07/2014.
 */
public class PortalServiceClient implements PortalService{
    private static final Logger LOGGER = LoggerFactory.getLogger(PortalServiceClient.class);
    private static final ObjectFactory WS_CLIENT_FACTORY = new ObjectFactory();

    private WebServiceTemplate webServiceTemplate;

    public PortalServiceClient(WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
    }

    public PortalServiceClient(URI portalEndpointURI) throws SOAPException {
        this.webServiceTemplate = createWebServiceTemplate(portalEndpointURI);
    }

    private static WebServiceTemplate createWebServiceTemplate(URI defaultURI) throws SOAPException {

        SaajSoapMessageFactory messageFactory = new SaajSoapMessageFactory(MessageFactory.newInstance());
        messageFactory.setSoapVersion(SOAP_11);

        HttpComponentsMessageSender httpSender = new HttpComponentsMessageSender();
        httpSender.setConnectionTimeout(1200000);
        httpSender.setReadTimeout(1200000);

        Jaxb2Marshaller serviceMarshaller = new Jaxb2Marshaller();
        serviceMarshaller.setContextPath("org.apromore.model");

        WebServiceTemplate webServiceTemplate = new WebServiceTemplate(messageFactory);
        webServiceTemplate.setMarshaller(serviceMarshaller);
        webServiceTemplate.setUnmarshaller(serviceMarshaller);
        webServiceTemplate.setMessageSender(httpSender);
        webServiceTemplate.setDefaultUri(defaultURI.toString());

        return webServiceTemplate;
    }

    @Override
    public void addNewTab(List<ResultPQL> result, String userID, List<Detail> details, String query, String nameQuery) {
//        LOGGER.trace("------------------PortalServiceClient: "+query);
//        LOGGER.info("------------------PortalServiceClient: "+query);

        TabAPQLInputMsgType msg= new TabAPQLInputMsgType();
        msg.getResults().addAll(result);
        msg.setUserID(userID);
        msg.getDetails().addAll(details);
        msg.setQuery(query);
        msg.setNameQuery(nameQuery);

        JAXBElement<TabAPQLInputMsgType> request = WS_CLIENT_FACTORY.createTabAPQLRequest(msg);
        JAXBElement<TabAPQLOutputMsgType> response = (JAXBElement<TabAPQLOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        if(response.getValue().getResult().getCode() == 0) {
            LOGGER.debug("------------------PortalServiceClientFine");
        }else {
            LOGGER.debug(response.getValue().getResult().getMessage());
        }
    }
}
