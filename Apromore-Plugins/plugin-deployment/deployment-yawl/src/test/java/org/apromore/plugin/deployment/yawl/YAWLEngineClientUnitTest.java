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

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.localserver.LocalTestServer;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;
import org.apromore.plugin.deployment.exception.DeploymentException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class YAWLEngineClientUnitTest {

    private LocalTestServer server;
    private YAWLEngineClient yawlEngineClient;

    @Before
    public void setUp() throws Exception {
        server = new LocalTestServer(null, null);
        server.start();
        yawlEngineClient = new YAWLEngineClient("http://localhost:"+server.getServiceAddress().getPort()+"/yawl/ia", "test", "test");
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
    }

    @Test
    public void testUploadYAWLSpecification() throws DeploymentException {
        server.register("/yawl/*", new HttpRequestHandler() {

            @Override
            public void handle(final HttpRequest request, final HttpResponse response, final HttpContext context) throws HttpException, IOException {
                String str = EntityUtils.toString( ((HttpEntityEnclosingRequest)request).getEntity());
                if (str.contains("action=upload")) {
                    if (str.contains("sessionHandle=test") && str.contains("specXML=test")) {
                        response.setEntity(new StringEntity("<response>test</response>", "UTF-8"));
                    } else {
                        response.setStatusCode(500);
                    }
                } else {
                    response.setStatusCode(500);
                }
            }

        });
        assertNotNull(yawlEngineClient.uploadYAWLSpecification("test", "test"));
    }

    @Test
    public void testConnectToYAWL() throws DeploymentException {
        server.register("/yawl/*", new HttpRequestHandler() {

            @Override
            public void handle(final HttpRequest request, final HttpResponse response, final HttpContext context) throws HttpException, IOException {
                String str = EntityUtils.toString( ((HttpEntityEnclosingRequest)request).getEntity());
                if (str.contains("action=connect")) {
                    if (str.contains("userID=test") && str.contains("password=qUqP5cyxm6YcTAhz05Hph5gvu9M%3D")) {
                        response.setEntity(new StringEntity("<response>test</response>", "UTF-8"));
                    } else {
                        response.setStatusCode(500);
                    }
                } else {
                    response.setStatusCode(500);
                }
            }

        });
        assertNotNull(yawlEngineClient.connectToYAWL());
    }

}
