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
package org.apromore.plugin.deployment.yawl;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpVersion;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apromore.plugin.deployment.exception.DeploymentException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * Simple YAWL Engine Client
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt</a>
 *
 */
public class YAWLEngineClient {

    private static final String ENCODING = "UTF-8";

    private final String engineUrl;
    private final String username;
    private final String password;

    /**
     * Stateful YAWL Engine Client
     *
     * @param engineUrl
     *            full URL to YAWL Engine
     * @param username
     *            of YAWL user
     * @param password
     *            of YAWL user
     */
    public YAWLEngineClient(final String engineUrl, final String username, final String password) {
        super();
        this.engineUrl = engineUrl;
        this.username = username;
        this.password = password;
    }

    /**
     * Upload YAWL specification
     *
     * @param yawlSpec
     *            XML of YAWL
     * @param sessionHandle
     *            retrieved by {@link #connectToYAWL()}
     * @return Message of YAWL Engine
     * @throws DeploymentException
     *             if YAWL engine not available
     */
    public Node uploadYAWLSpecification(final String yawlSpec, final String sessionHandle) throws DeploymentException {
        try {
            String uploadRequest = prepareUploadRequestBody(yawlSpec, sessionHandle);
            String response = sendRequestToYAWL(uploadRequest);
            if (response != null) {
                return getYAWLMessage(response);
            } else {
                throw new DeploymentException("Could not connect to YAWL engine. No Response!");
            }
        } catch (UnsupportedEncodingException e) {
            throw new DeploymentException("Could not prepare upload request", e);
        }
    }

    /**
     * Connect to YAWL Engine an return a Session handle
     *
     * @return Node containing Session handle
     * @throws DeploymentException
     *             if YAWL engine not available
     */
    public Node connectToYAWL() throws DeploymentException {
        try {
            String connectRequest = prepareConnectRequestBody(username, password);
            String response = sendRequestToYAWL(connectRequest);
            if (response != null) {
                return getYAWLMessage(response);
            } else {
                throw new DeploymentException("Could not connect to YAWL engine. No Response!");
            }
        } catch (UnsupportedEncodingException e) {
            throw new DeploymentException("Could not prepare connect request", e);
        }
    }

    private Node getYAWLMessage(final String response) throws DeploymentException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Element message = db.parse(new InputSource(new StringReader(response))).getDocumentElement();
            if (message.getNodeName().equals("response")) {
                return message.getFirstChild();
            } else {
                throw new DeploymentException("Could not talk to YAWL engine. Invalid respone: " + response);
            }
        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw new DeploymentException("Could not talk to YAWL engine.", e);
        }

    }

    private String sendRequestToYAWL(final String request) throws DeploymentException {
        try {
            return Request.Post(engineUrl).useExpectContinue().addHeader("Accept-Charset", ENCODING).version(HttpVersion.HTTP_1_1)
                    .bodyString(request, ContentType.APPLICATION_FORM_URLENCODED).execute().returnContent().asString();
        } catch (IOException e) {
            throw new DeploymentException("Request to YAWL engine failed.", e);
        }
    }

    private String prepareUploadRequestBody(final String yawlSpec, final String sessionHandle) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        sb.append("action=upload&");
        sb.append(String.format("sessionHandle=%s&", URLEncoder.encode(sessionHandle, ENCODING)));
        sb.append(String.format("specXML=%s", URLEncoder.encode(yawlSpec, ENCODING)));
        return sb.toString();
    }

    private String prepareConnectRequestBody(final String yawlUsername, final String yawlPassword) throws UnsupportedEncodingException,
            DeploymentException {
        StringBuilder sb = new StringBuilder();
        sb.append("action=connect&");
        sb.append(String.format("userID=%s&", URLEncoder.encode(yawlUsername, ENCODING)));
        sb.append(String.format("password=%s", URLEncoder.encode(getEncryptedYAWLPassword(yawlPassword), ENCODING)));
        return sb.toString();
    }

    private String getEncryptedYAWLPassword(final String yawlPassword) throws DeploymentException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA");
            byte[] cryptedPW = digest.digest(yawlPassword.getBytes(ENCODING));
            return new Base64(-1).encodeAsString(cryptedPW);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new DeploymentException("Could not encrypt YAWL password.", e);
        }
    }

}
