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

package org.apromore.plugin.deployment.yawl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.apache.http.HttpVersion;
import org.apache.http.client.fluent.Request;
import org.apromore.cpf.CPFSchema;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.PluginRequestImpl;
import org.apromore.plugin.PluginResult;
import org.apromore.plugin.deployment.exception.DeploymentException;
import org.apromore.plugin.exception.PluginException;
import org.apromore.plugin.exception.PluginPropertyNotFoundException;
import org.apromore.plugin.property.RequestParameterType;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Integration test using a real YAWL Engine if available. It will be searched for on localhost:8080!
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt</a>
 *
 */
public class YAWLDeploymentPluginIntgTest {

    private YAWLDeploymentPlugin deploymentPlugin;

    @Before
    public void setUp() throws Exception {
        deploymentPlugin = new YAWLDeploymentPlugin();
    }

    @Test
    public void testDeploySequence() throws IOException, JAXBException, SAXException, PluginException {
        if (checkYAWLServerAvailable()) {
            try (BufferedInputStream cpfInputStream = new BufferedInputStream(new FileInputStream("src/test/resources/WPC1Sequence.yawl.cpf"))) {
                PluginResult result = doDeployProcess(cpfInputStream);
                checkResult(result, "WP1Sequence", "WP1Sequence", "0.1");
            }
        }
    }


    @Test
    public void testDeploySimpleMakeTrip() throws IOException, JAXBException, SAXException, PluginException {
        if (checkYAWLServerAvailable()) {
            try (BufferedInputStream cpfInputStream = new BufferedInputStream(new FileInputStream("src/test/resources/SimpleMakeTripProcess.yawl.cpf"))) {
                PluginResult result = doDeployProcess(cpfInputStream);
                checkResult(result, "Simple Make Trip Process", "SimpleMakeTripProcess.ywl", "1.3");
            }
        }
    }

    @Test
    public void testDeployFilmProduction() throws IOException, JAXBException, SAXException, PluginException {
        if (checkYAWLServerAvailable()) {
            try (BufferedInputStream cpfInputStream = new BufferedInputStream(new FileInputStream("src/test/resources/filmproduction.yawl.cpf"))) {
                PluginResult result = doDeployProcess(cpfInputStream);
                checkResult(result, "", "", "1.0");
            }
        }
    }

    @Test
    public void testDeployCreditRatingProcess() throws IOException, JAXBException, SAXException, PluginException {
        if (checkYAWLServerAvailable()) {
            try (BufferedInputStream cpfInputStream = new BufferedInputStream(new FileInputStream("src/test/resources/CreditRatingProcess.yawl.cpf"))) {
                PluginResult result = doDeployProcess(cpfInputStream);
                checkResult(result, "Credit Rating Process", "CreditRatingProcess.ywl", "1.0");
            }
        }
    }

    @Test
    public void testDeployCreditApplicationProcess() throws IOException, JAXBException, SAXException, PluginException {
        if (checkYAWLServerAvailable()) {
            try (BufferedInputStream cpfInputStream = new BufferedInputStream(new FileInputStream("src/test/resources/CreditApplicationProcess.yawl.cpf"))) {
                PluginResult result = doDeployProcess(cpfInputStream);
                checkResult(result, "Credit application process", "CreditAppProcess2.0", "0.1");
            }
        }
    }

    private void checkResult(final PluginResult result, final String processName, final String processId, final String processVersion) {
        assertTrue(result.getPluginMessage().size() >= 1);
        if (result.getPluginMessage().size() == 2) {
            assertEquals("Failure deploying process "+processName, result.getPluginMessage().get(0).getMessage());
            assertEquals("Error: There is a specification with an identical id to [UID: "+processId+"- Version: "+processVersion+"] already loaded into the engine.", result.getPluginMessage().get(1).getMessage());
            fail("Please unload specifiction '"+processName+"' before integration testing!");
        } else {
            if (!result.getPluginMessage().get(0).getMessage().equals("Process "+processName+" successfully deployed.")) {
                assertEquals("Error: There is a specification with an identical id to [UID: "+processName+"- Version: "+processVersion+"] already loaded into the engine.", result.getPluginMessage().get(0).getMessage());
                fail("Please unload specifiction '"+processName+"' before integration testing!");
            }
        }
    }

    private PluginResult doDeployProcess(final BufferedInputStream cpfInputStream) throws JAXBException, SAXException, DeploymentException,
            PluginPropertyNotFoundException {
        CanonicalProcessType cpf = CPFSchema.unmarshalCanonicalFormat(cpfInputStream, true).getValue();
        PluginRequestImpl request = new PluginRequestImpl();
        request.addRequestProperty(new RequestParameterType<String>("yawlEngineUrl", "http://localhost:8080/yawl/ia"));
        request.addRequestProperty(new RequestParameterType<String>("yawlEngineUsername", "admin"));
        request.addRequestProperty(new RequestParameterType<String>("yawlEnginePassword", "YAWL"));
        PluginResult result = deploymentPlugin.deployProcess(cpf, request);
        return result;
    }

    private boolean checkYAWLServerAvailable() {
        try {
            return Request.Get("http://localhost:8080/yawl/ia").useExpectContinue()
                    .addHeader("Accept-Charset", "UTF-8")
                    .version(HttpVersion.HTTP_1_1)
                    .execute().returnContent().asString().startsWith("<response><failure><reason>");
        } catch (IOException e) {
            return false;
        }
    }


}