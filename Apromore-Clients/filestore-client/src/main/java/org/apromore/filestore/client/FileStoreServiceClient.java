/*
 * Copyright © 2009-2015 The Apromore Initiative.
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

package org.apromore.filestore.client;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.bind.JAXBElement;

import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import static org.springframework.ws.soap.SoapVersion.SOAP_11;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import org.apromore.model.*;

/**
 * Implementation of the Apromore File Store Client.
 *
 * @author Cameron James
 */
public class FileStoreServiceClient implements FileStoreService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileStoreServiceClient.class);
    private static final ObjectFactory WS_CLIENT_FACTORY = new ObjectFactory();

    private static final String USERNAME = "admin";
    private static final String PASSWORD = "password";

    private URI                baseURI;
    private WebServiceTemplate webServiceTemplate;

    /**
     * Default Constructor.
     *
     * @param webServiceTemplate the webservice template
     */
    public FileStoreServiceClient(WebServiceTemplate webServiceTemplate) {
        try {
            this.baseURI = new URI(webServiceTemplate.getDefaultUri());
        } catch (URISyntaxException e) {
            throw new Error("Bad hardcoded URI", e);
        }

        this.webServiceTemplate = webServiceTemplate;
    }

    /**
     * @param filestoreURLString  the externally reachable URL of the WebDAV filestore, e.g. "http://localhost:9000/filestore"
     */
    public FileStoreServiceClient(URI filestoreURLString) throws SOAPException {

        WebServiceTemplate webServiceTemplate = createWebServiceTemplate(filestoreURLString);
        
        try {
            this.baseURI = new URI(webServiceTemplate.getDefaultUri());
        } catch (URISyntaxException e) {
            throw new Error("Bad hardcoded URI", e);
        }

        this.webServiceTemplate = webServiceTemplate;
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

    /**
     * @see FileStoreService#list(String)
     * {@inheritDoc}
     */
    @Override
    public List<DavResource> list(String folderLocation) throws Exception {
        Sardine sardine = SardineFactory.begin(USERNAME, PASSWORD);
        return sardine.list(rewrite(folderLocation));
    }

    /**
     * @see FileStoreService#exists(String)
     * {@inheritDoc}
     */
    @Override
    public boolean exists(String url) throws Exception {
        Sardine sardine = SardineFactory.begin(USERNAME, PASSWORD);
        return sardine.exists(rewrite(url));
    }

    /**
     * @see FileStoreService#getFile(String)
     * {@inheritDoc}
     */
    @Override
    public InputStream getFile(String url) throws Exception {
        Sardine sardine = SardineFactory.begin(USERNAME, PASSWORD);
        LOGGER.info("Getting DAV file from url=" + url + " rewritten as " + rewrite(url));
        return sardine.get(rewrite(url));
    }

    /**
     * @see FileStoreService#put(String, byte[])
     * {@inheritDoc}
     */
    @Override
    public void put(String url, byte[] data) throws Exception {
        SardineFactory.begin(USERNAME, PASSWORD).put(rewrite(url), data);
    }

    /**
     * @see FileStoreService#put(String, byte[], String)
     * {@inheritDoc}
     */
    @Override
    public void put(String url, byte[] data, String contentType) throws Exception {
        SardineFactory.begin(USERNAME, PASSWORD).put(rewrite(url), data, contentType);
    }

    /**
     * @see FileStoreService#createFolder(String)
     * {@inheritDoc}
     */
    @Override
    public void createFolder(String url) throws Exception {
        SardineFactory.begin(USERNAME, PASSWORD).createDirectory(rewrite(url));
    }

    /**
     * @see FileStoreService#delete(String)
     * {@inheritDoc}
     */
    @Override
    public void delete(String url) throws Exception {
        SardineFactory.begin(USERNAME, PASSWORD).delete(rewrite(url));
    }

    /**
     * @see org.apromore.filestore.client.FileStoreService#readUserByEmail(String)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public UserType readUserByEmail(String email) throws Exception {
        LOGGER.debug("Preparing ResetUserRequest.....");

        ReadUserByEmailInputMsgType msg = new ReadUserByEmailInputMsgType();
        msg.setEmail(email);

        JAXBElement<ReadUserByEmailInputMsgType> request = WS_CLIENT_FACTORY.createReadUserByEmailRequest(msg);

        JAXBElement<ReadUserByEmailOutputMsgType> response = (JAXBElement<ReadUserByEmailOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        if (response.getValue().getResult().getCode() == -1) {
            throw new Exception(response.getValue().getResult().getMessage());
        } else {
            return response.getValue().getUser();
        }
    }

    /**
     * @see org.apromore.filestore.client.FileStoreService#resetUserPassword(String, String)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean resetUserPassword(String username, String password) {
        LOGGER.debug("Preparing Reset the Users Password.....");

        ResetUserPasswordInputMsgType msg = new ResetUserPasswordInputMsgType();
        msg.setUsername(username);
        msg.setPassword(password);

        JAXBElement<ResetUserPasswordInputMsgType> request = WS_CLIENT_FACTORY.createResetUserPasswordRequest(msg);

        JAXBElement<ResetUserPasswordOutputMsgType> response = (JAXBElement<ResetUserPasswordOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        return response.getValue().isSuccess();
    }

    /**
     * @see org.apromore.filestore.client.FileStoreService#writeUser(org.apromore.model.UserType)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public UserType writeUser(UserType user) throws Exception {
        LOGGER.debug("Preparing WriteUserRequest.....");

        WriteUserInputMsgType msg = new WriteUserInputMsgType();
        msg.setUser(user);

        JAXBElement<WriteUserInputMsgType> request = WS_CLIENT_FACTORY.createWriteUserRequest(msg);

        JAXBElement<WriteUserOutputMsgType> response = (JAXBElement<WriteUserOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        if (response.getValue().getResult().getCode() == -1) {
            throw new Exception(response.getValue().getResult().getMessage());
        }

        return response.getValue().getUser();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getBaseURI() {
        return baseURI;
    }

    /**
     * @param urlString  client-supplied URL-formatted WebDAV address
     * @return the <var>UrlString</var> re-written to include the user information that Sardine demands
     */
    private String rewrite(String urlString) throws URISyntaxException {
        return urlString;
        /*
        URI uri = new URI(urlString);
        return new URI(uri.getScheme(),
                       USERNAME + ":"  + PASSWORD,
                       uri.getHost(),
                       uri.getPort(),
                       uri.getPath(),
                       uri.getQuery(),
                       uri.getFragment()).toString();
        */
    }
}
