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

package org.apromore.plugin.deployment.yawl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.localserver.LocalTestServer;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;
import org.apromore.cpf.CPFSchema;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.PluginRequestImpl;
import org.apromore.plugin.PluginResult;
import org.apromore.plugin.deployment.exception.DeploymentException;
import org.apromore.plugin.exception.PluginException;
import org.apromore.plugin.property.RequestParameterType;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Unit test using a LocalTestServer mocking a real YAWL Engine
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt</a>
 *
 */
public class YAWLDeploymentPluginUnitTest {

    private static final String TESTSESSIONHANDLE = "__testsessionhandle__";

    private YAWLDeploymentPlugin deploymentPlugin;
    private LocalTestServer server;

    @Before
    public void setUp() throws Exception {
        server = new LocalTestServer(null, null);
        server.start();
        deploymentPlugin = new YAWLDeploymentPlugin();
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
    }

    @Test
    public void testYAWLDeploymentPlugin() {
        assertNotNull(deploymentPlugin);
        assertEquals(4, deploymentPlugin.getAvailableParameters().size());
        assertEquals(3, deploymentPlugin.getMandatoryParameters().size());
    }

    @Test
    public void testDeployProcessCanonicalProcessType() throws IOException, JAXBException, SAXException, PluginException {
        server.register("/yawl/*", new HttpRequestHandler() {

            @Override
            public void handle(final HttpRequest request, final HttpResponse response, final HttpContext context) throws HttpException, IOException {
                String str = EntityUtils.toString( ((HttpEntityEnclosingRequest)request).getEntity());
                if (str.contains("action=connect")) {
                    if (str.contains("userID=admin") && str.contains("password=Se4tMaQCi9gr0Q2usp7P56Sk5vM%3D")) {
                        response.setEntity(new StringEntity("<response>"+TESTSESSIONHANDLE+"</response>", "UTF-8"));
                    } else {
                        response.setStatusCode(500);
                    }
                } else if (str.contains("action=upload")) {
                    if (str.contains("sessionHandle="+TESTSESSIONHANDLE) && str.contains("specXML=")) {
                        response.setEntity(new StringEntity("<response>test</response>", "UTF-8"));
                    } else {
                        response.setStatusCode(500);
                    }
                } else {
                    response.setStatusCode(500);
                }
            }

        });
        try (BufferedInputStream cpfInputStream = new BufferedInputStream(new FileInputStream("src/test/resources/SimpleMakeTripProcess.yawl.cpf"))) {
            CanonicalProcessType cpf = CPFSchema.unmarshalCanonicalFormat(cpfInputStream, true).getValue();
            PluginRequestImpl request = new PluginRequestImpl();
            request.addRequestProperty(new RequestParameterType<String>("yawlEngineUrl", "http://localhost:"+server.getServiceAddress().getPort()+"/yawl/ia"));
            request.addRequestProperty(new RequestParameterType<String>("yawlEngineUsername", "admin"));
            request.addRequestProperty(new RequestParameterType<String>("yawlEnginePassword", "YAWL"));
            request.addRequestProperty(new RequestParameterType<Boolean>("doAutoLaunch", true));
            PluginResult result = deploymentPlugin.deployProcess(cpf, request);
            assertEquals(1, result.getPluginMessage().size());
            assertEquals("YAWL Engine message: test", result.getPluginMessage().get(0).getMessage());
        }
    }

    @Test
    public void testDeployProcessCanonicalProcessTypeFailure() throws IOException, JAXBException, SAXException, PluginException {
        server.register("/yawl/*", new HttpRequestHandler() {

            @Override
            public void handle(final HttpRequest request, final HttpResponse response, final HttpContext context) throws HttpException, IOException {
                String str = EntityUtils.toString( ((HttpEntityEnclosingRequest)request).getEntity());
                if (str.contains("action=connect")) {
                    if (str.contains("userID=admin") && str.contains("password=Se4tMaQCi9gr0Q2usp7P56Sk5vM%3D")) {
                        response.setEntity(new StringEntity("<response>"+TESTSESSIONHANDLE+"</response>", "UTF-8"));
                    } else {
                        response.setStatusCode(500);
                    }
                } else if (str.contains("action=upload")) {
                    if (str.contains("sessionHandle="+TESTSESSIONHANDLE) && str.contains("specXML=")) {
                        response.setEntity(new StringEntity("<response><failure><reason><error><message>There is a specification with an identical id to [UID: WP1Sequence- Version: 0.1] already loaded into the engine.</message></error></reason></failure></response>", "UTF-8"));
                    } else {
                        response.setStatusCode(500);
                    }
                } else {
                    response.setStatusCode(500);
                }
            }

        });
        try (BufferedInputStream cpfInputStream = new BufferedInputStream(new FileInputStream("src/test/resources/SimpleMakeTripProcess.yawl.cpf"))) {
            CanonicalProcessType cpf = CPFSchema.unmarshalCanonicalFormat(cpfInputStream, true).getValue();
            PluginRequestImpl request = new PluginRequestImpl();
            request.addRequestProperty(new RequestParameterType<String>("yawlEngineUrl", "http://localhost:"+server.getServiceAddress().getPort()+"/yawl/ia"));
            request.addRequestProperty(new RequestParameterType<String>("yawlEngineUsername", "admin"));
            request.addRequestProperty(new RequestParameterType<String>("yawlEnginePassword", "YAWL"));
            request.addRequestProperty(new RequestParameterType<Boolean>("doAutoLaunch", false));
            PluginResult result = deploymentPlugin.deployProcess(cpf, request);
            assertEquals(2, result.getPluginMessage().size());
            assertEquals("Failure deploying process Simple Make Trip Process", result.getPluginMessage().get(0).getMessage());
            assertEquals("Error: There is a specification with an identical id to [UID: WP1Sequence- Version: 0.1] already loaded into the engine.", result.getPluginMessage().get(1).getMessage());
        }
    }

    @Test
    public void testGetNativeType() {
        assertEquals("YAWL 2.2", deploymentPlugin.getNativeType());
    }

    @Test
    public void testWrongParameters() {
        try {
            deploymentPlugin.deployProcess(new CanonicalProcessType(), new PluginRequestImpl());
            fail("Should have failed because of missing mandatory properties!");
        } catch (PluginException e) {
        }
        PluginRequestImpl request = new PluginRequestImpl();
        request.addRequestProperty(new RequestParameterType<String>("yawlEngineUrl", "localhost/yawl/ia"));
        request.addRequestProperty(new RequestParameterType<String>("yawlEngineUsername", "admin"));
        request.addRequestProperty(new RequestParameterType<String>("yawlEnginePassword", "YAWL"));
        try {
            deploymentPlugin.deployProcess(new CanonicalProcessType(), request);
            fail("Should have failed because of wrong URL!");
        } catch (PluginException e) {
        }
    }

    @Test
    public void testConnectionIssues() throws IOException, JAXBException, SAXException, PluginException {
        YAWLEngineClientFactory mockClientFactory = EasyMock.createMock(YAWLEngineClientFactory.class);
        YAWLEngineClient mockClient = EasyMock.createMock(YAWLEngineClient.class);

        EasyMock.expect(mockClientFactory.newInstance(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class), EasyMock.anyObject(String.class))).andReturn(mockClient);
        Node response = EasyMock.createMock(Node.class);
        EasyMock.expect(response.getNodeName()).andReturn("failure");
        EasyMock.expect(response.getTextContent()).andReturn("Could not connect");
        EasyMock.expect(mockClient.connectToYAWL()).andReturn(response);

        EasyMock.replay(mockClientFactory);
        EasyMock.replay(mockClient);
        EasyMock.replay(response);

        deploymentPlugin.setEngineClientFactory(mockClientFactory);

        try (BufferedInputStream cpfInputStream = new BufferedInputStream(new FileInputStream("src/test/resources/SimpleMakeTripProcess.yawl.cpf"))) {
            CanonicalProcessType cpf = CPFSchema.unmarshalCanonicalFormat(cpfInputStream, true).getValue();
            PluginRequestImpl request = new PluginRequestImpl();
            request.addRequestProperty(new RequestParameterType<String>("yawlEngineUrl", "http://localhost:"+server.getServiceAddress().getPort()+"/yawl/ia"));
            request.addRequestProperty(new RequestParameterType<String>("yawlEngineUsername", "admin"));
            request.addRequestProperty(new RequestParameterType<String>("yawlEnginePassword", "YAWL"));

            try {
                deploymentPlugin.deployProcess(cpf, request);
                fail("Should have thrown DeploymentException!");
            } catch (DeploymentException e) {
            }

        }

        EasyMock.verify(mockClientFactory);
        EasyMock.verify(mockClient);
        EasyMock.verify(response);
    }

}