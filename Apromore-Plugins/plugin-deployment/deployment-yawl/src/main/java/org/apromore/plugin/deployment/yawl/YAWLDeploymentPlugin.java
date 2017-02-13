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
package org.apromore.plugin.deployment.yawl;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.YAWL22Canoniser;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.PluginRequest;
import org.apromore.plugin.PluginResult;
import org.apromore.plugin.PluginResultImpl;
import org.apromore.plugin.deployment.DefaultDeploymentPlugin;
import org.apromore.plugin.deployment.exception.DeploymentException;
import org.apromore.plugin.exception.PluginPropertyNotFoundException;
import org.apromore.plugin.property.ParameterType;
import org.apromore.plugin.property.PluginParameterType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;

/**
 *
 * Implementation of your new Plugin API. This class extends the default implementation, that is often provided by APIs.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt</a>
 *
 */
@Component("yawlDeploymentPlugin")
public class YAWLDeploymentPlugin extends DefaultDeploymentPlugin {

    private static final Logger LOGGER = LoggerFactory.getLogger(YAWLDeploymentPlugin.class);

    private static final String DEFAULT_YAWL_ENGINE_URL = "http://localhost:8080/yawl/ia";
    private static final String ENGINE_PASSWORD = "YAWL";
    private static final String ENGINE_USERNAME = "admin";

    private static final String YAWL_ENGINE_PASSWORD_PROPERTY_NAME = "YAWL Engine Password";
    private static final String YAWL_ENGINE_USERNAME_PROPERTY_NAME = "YAWL Engine Username";
    private static final String YAWL_ENGINE_URL_PROPERTY_NAME = "YAWL Engine URL";
    private static final String AUTOLAUNCH_PROPERTY_NAME = "Launch case automatically? (Not yet available)";

    private final PluginParameterType<String> yawlEngineUrl;
    private final PluginParameterType<String> yawlEngineUsername;
    private final PluginParameterType<String> yawlEnginePassword;
    private final PluginParameterType<Boolean> autoLaunch;

    private YAWLEngineClientFactory engineClientFactory;

    public YAWLDeploymentPlugin() {
        super();
        yawlEngineUrl = new PluginParameterType<String>("yawlEngineUrl", YAWL_ENGINE_URL_PROPERTY_NAME, "", true, DEFAULT_YAWL_ENGINE_URL);
        registerParameter(yawlEngineUrl);
        yawlEngineUsername = new PluginParameterType<String>("yawlEngineUsername", YAWL_ENGINE_USERNAME_PROPERTY_NAME, "", true, ENGINE_USERNAME);
        registerParameter(yawlEngineUsername);
        yawlEnginePassword = new PluginParameterType<String>("yawlEnginePassword", YAWL_ENGINE_PASSWORD_PROPERTY_NAME, "", true, ENGINE_PASSWORD);
        registerParameter(yawlEnginePassword);
        autoLaunch = new PluginParameterType<Boolean>("autoLaunch", AUTOLAUNCH_PROPERTY_NAME, "", false, false);
        registerParameter(autoLaunch);
        engineClientFactory = new YAWLEngineClientFactory();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.plugin.deployment.DeploymentPlugin#deployProcess(org.apromore.cpf.CanonicalProcessType)
     */
    @Override
    public PluginResult deployProcess(final CanonicalProcessType canonicalProcess, final PluginRequest request) throws DeploymentException, PluginPropertyNotFoundException {
        return deployProcess(canonicalProcess, null, request);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.plugin.deployment.DeploymentPlugin#deployProcess(org.apromore.cpf.CanonicalProcessType, org.apromore.anf.AnnotationsType)
     */
    @Override
    public PluginResult deployProcess(final CanonicalProcessType canonicalProcess, final AnnotationsType annotation, final PluginRequest request)
            throws DeploymentException, PluginPropertyNotFoundException {
        ParameterType<String> userEngineUrl = request.getRequestParameter(yawlEngineUrl);
        ParameterType<String> userUsername = request.getRequestParameter(yawlEngineUsername);
        ParameterType<String> userPassword = request.getRequestParameter(yawlEnginePassword);
        ParameterType<Boolean> doAutoLaunch = request.getRequestParameter(autoLaunch);

        checkProperties(userEngineUrl);

        PluginResultImpl pluginResult = newPluginResult();

        YAWLEngineClient yawlEngineClient = engineClientFactory.newInstance(userEngineUrl.getValue(), userUsername.getValue(),
                userPassword.getValue());

        Node connectResponse = yawlEngineClient.connectToYAWL();
        if (connectResponse.getNodeName().equals("failure")) {
            throw new DeploymentException("Could not connect to YAWL engine. Reason: " + connectResponse.getTextContent());
        } else {
            try {
                String sessionHandle = connectResponse.getTextContent();
                if (sessionHandle != null) {
                    String yawlSpec = deCanoniseYAWL(canonicalProcess, annotation);
                    Node uploadResponse = yawlEngineClient.uploadYAWLSpecification(yawlSpec, sessionHandle);
                    if (uploadResponse.getNodeName().equals("failure")) {
                        pluginResult.addPluginMessage("Failure deploying process {0}", canonicalProcess.getName());
                        if (uploadResponse.getChildNodes().getLength() > 0) {
                            Node reasonNode = uploadResponse.getChildNodes().item(0);
                            for (int i = 0; i < reasonNode.getChildNodes().getLength(); i++) {
                                convertToPluginMessage(pluginResult, reasonNode.getChildNodes().item(i));
                            }
                        }
                    } else {
                        if (uploadResponse.getTextContent().isEmpty()) {
                            pluginResult.addPluginMessage("Process {0} successfully deployed.", canonicalProcess.getName(),
                                    uploadResponse.getTextContent());
                            if (doAutoLaunch.getValue()) {
                                doAutoLaunch();
                            }
                        } else {
                            pluginResult.addPluginMessage("YAWL Engine message: {0}", uploadResponse.getTextContent());
                        }
                    }
                } else {
                    throw new DeploymentException("Could not connect to YAWL engine. Invalid Session Handle returned!");
                }
            } catch (CanoniserException e) {
                throw new DeploymentException("Could not deCanonise to YAWL", e);
            }
        }

        return pluginResult;
    }

    private void doAutoLaunch() {
        // TODO Would do auto-launch
        LOGGER.warn("Auto launch not yet supported");
    }

    private void convertToPluginMessage(final PluginResultImpl pluginResult, final Node errorMessage) {
        if (errorMessage.getChildNodes().getLength() > 1) {
            pluginResult.addPluginMessage("{0}: {1}", errorMessage.getFirstChild().getTextContent(), errorMessage.getLastChild()
                    .getTextContent());
        } else {
            pluginResult.addPluginMessage("Error: {0}", errorMessage.getFirstChild().getTextContent());
        }
    }

    private String deCanoniseYAWL(final CanonicalProcessType canonicalProcess, final AnnotationsType annotation) throws CanoniserException,
            DeploymentException {
        // TODO replace with OSGi dependency injection of CanoniserProvider
        YAWL22Canoniser yawlCanoniser = new YAWL22Canoniser();
        ByteArrayOutputStream yawlSpecOS = new ByteArrayOutputStream();
        // TODO specify YAWL canoniser properties
        yawlCanoniser.deCanonise(canonicalProcess, annotation, yawlSpecOS, newPluginRequest());
        try {
            return new String(yawlSpecOS.toByteArray(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new DeploymentException("Could not deCanonise to YAWL", e);
        }
    }

    /**
     * Checks if the supplied properties are valid
     *
     * @param userEngineUrl
     *
     * @return
     * @throws DeploymentException
     */
    private boolean checkProperties(final ParameterType<String> userEngineUrl) throws DeploymentException {
        if (!userEngineUrl.hasValue()) {
            throw new DeploymentException("Invalid parameter " + yawlEngineUrl.getName());
        }
        try {
            new URL(userEngineUrl.getValue());
        } catch (MalformedURLException e) {
            throw new DeploymentException("Invalid URL: ", e);
        }
        return true;
    }

    /**
     * Mainly for JUnit testing
     *
     * @param factory
     *            different factory to use
     */
    public void setEngineClientFactory(final YAWLEngineClientFactory factory) {
        this.engineClientFactory = factory;
    }

}