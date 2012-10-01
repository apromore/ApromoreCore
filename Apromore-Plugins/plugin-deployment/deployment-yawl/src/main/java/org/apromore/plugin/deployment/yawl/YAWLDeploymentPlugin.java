/**
 * Copyright 2012, Felix Mannhardt
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
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
import org.apromore.plugin.deployment.exception.DeploymentException;
import org.apromore.plugin.deployment.impl.DefaultDeploymentPlugin;
import org.apromore.plugin.property.BooleanProperty;
import org.apromore.plugin.property.StringProperty;
import org.w3c.dom.Node;

/**
 *
 * Implementation of your new Plugin API. This class extends the default implementation, that is often provided by APIs.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt</a>
 *
 */
public class YAWLDeploymentPlugin extends DefaultDeploymentPlugin {

    private static final String DEFAULT_YAWL_ENGINE_URL = "http://localhost:8080/yawl/ia";
    private static final String ENGINE_PASSWORD = "YAWL";
    private static final String ENGINE_USERNAME = "admin";

    private static final String YAWL_ENGINE_PASSWORD_PROPERTY_NAME = "YAWL Engine Password";
    private static final String YAWL_ENGINE_USERNAME_PROPERTY_NAME = "YAWL Engine Username";
    private static final String YAWL_ENGINE_URL_PROPERTY_NAME = "YAWL Engine URL";
    private static final String AUTOLAUNCH_PROPERTY_NAME = "Launch case automatically?";

    private final StringProperty yawlEngineUrl;
    private final StringProperty yawlEngineUsername;
    private final StringProperty yawlEnginePassword;
    private final BooleanProperty autoLaunch;

    public YAWLDeploymentPlugin() {
        super();
        yawlEngineUrl = new StringProperty("yawlEngineUrl", YAWL_ENGINE_URL_PROPERTY_NAME, "", true, DEFAULT_YAWL_ENGINE_URL);
        addProperty(yawlEngineUrl);
        yawlEngineUsername = new StringProperty("yawlEngineUsername", YAWL_ENGINE_USERNAME_PROPERTY_NAME, "", true, ENGINE_USERNAME);
        addProperty(yawlEngineUsername);
        yawlEnginePassword = new StringProperty("yawlEnginePassword", YAWL_ENGINE_PASSWORD_PROPERTY_NAME, "", true, ENGINE_PASSWORD);
        addProperty(yawlEnginePassword);
        autoLaunch = new BooleanProperty("autoLaunch", AUTOLAUNCH_PROPERTY_NAME, "", false, false);
        addProperty(autoLaunch);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.plugin.deployment.DeploymentPlugin#deployProcess(org.apromore.cpf.CanonicalProcessType)
     */
    @Override
    public void deployProcess(final CanonicalProcessType canonicalProcess) throws DeploymentException {
        deployProcess(canonicalProcess, null);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.plugin.deployment.DeploymentPlugin#deployProcess(org.apromore.cpf.CanonicalProcessType, org.apromore.anf.AnnotationsType)
     */
    @Override
    public void deployProcess(final CanonicalProcessType canonicalProcess, final AnnotationsType annotation) throws DeploymentException {
        checkProperties();

        YAWLEngineClient yawlEngineClient = new YAWLEngineClient(yawlEngineUrl.getValueAsString(), yawlEngineUsername.getValueAsString(),
                yawlEnginePassword.getValueAsString());

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
                        for (int i = 0; i < uploadResponse.getChildNodes().getLength(); i++) {
                            convertToPluginMessage(uploadResponse.getChildNodes().item(i));
                        }
                    } else {
                        if (uploadResponse.getTextContent().isEmpty()) {
                            addPluginMessage("Process {0} successfully deployed.", canonicalProcess.getName(), uploadResponse.getTextContent());
                        } else {
                            addPluginMessage("YAWL Engine message: {0}", uploadResponse.getTextContent());
                        }
                    }
                } else {
                    throw new DeploymentException("Could not connect to YAWL engine. Invalid Session Handle returned!");
                }
            } catch (CanoniserException e) {
                throw new DeploymentException("Could not deCanonise to YAWL", e);
            }
        }

    }

    private void convertToPluginMessage(final Node errorMessage) {
        if (errorMessage.getChildNodes().getLength() > 1) {
            addPluginMessage("Error in Node {0}: {1}", errorMessage.getFirstChild().getTextContent(), errorMessage.getLastChild().getTextContent());
        } else {
            addPluginMessage("Error: {0}", errorMessage.getFirstChild().getTextContent());
        }
    }

    private String deCanoniseYAWL(final CanonicalProcessType canonicalProcess, final AnnotationsType annotation) throws CanoniserException,
            DeploymentException {
        YAWL22Canoniser yawlCanoniser = new YAWL22Canoniser();
        ByteArrayOutputStream yawlSpecOS = new ByteArrayOutputStream();
        yawlCanoniser.deCanonise(canonicalProcess, annotation, yawlSpecOS);
        try {
            return new String(yawlSpecOS.toByteArray(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new DeploymentException("Could not deCanonise to YAWL", e);
        }
    }

    /**
     * Checks if the supplied properties are valid
     *
     * @return
     * @throws DeploymentException
     */
    private boolean checkProperties() throws DeploymentException {
        if (!yawlEngineUrl.hasValue()) {
            throw new DeploymentException("Invalid parameter " + yawlEngineUrl.getName());
        }
        try {
            new URL(yawlEngineUrl.getValueAsString());
        } catch (MalformedURLException e) {
            throw new DeploymentException("Invalid URL: ", e);
        }
        return true;
    }

}