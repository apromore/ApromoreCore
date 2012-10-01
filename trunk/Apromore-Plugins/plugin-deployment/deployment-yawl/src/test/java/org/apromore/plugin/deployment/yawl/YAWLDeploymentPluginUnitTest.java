package org.apromore.plugin.deployment.yawl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
import org.apromore.plugin.deployment.exception.DeploymentException;
import org.apromore.plugin.property.PropertyType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
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
        for (PropertyType p: deploymentPlugin.getMandatoryProperties()) {
            if (p.getId().equals("yawlEngineUrl")) {
                p.setValue("http://localhost:"+server.getServiceAddress().getPort()+"/yawl/ia");
            } else if (p.getId().equals("yawlUsername")) {
                p.setValue("admin");
            } else if (p.getId().equals("yawlPassword")) {
                p.setValue("YAWL");
            }
        }
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
    }

    @Test
    public void testYAWLDeploymentPlugin() {
        assertNotNull(deploymentPlugin);
        assertEquals(4, deploymentPlugin.getAvailableProperties().size());
        assertEquals(3, deploymentPlugin.getMandatoryProperties().size());
    }

    @Test
    public void testDeployProcessCanonicalProcessType() throws IOException, JAXBException, SAXException, DeploymentException {
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
        BufferedInputStream cpfInputStream = new BufferedInputStream(new FileInputStream("src/test/resources/SimpleMakeTripProcess.yawl.cpf"));
        CanonicalProcessType cpf = CPFSchema.unmarshalCanonicalFormat(cpfInputStream, true).getValue();
        cpfInputStream.close();
        deploymentPlugin.deployProcess(cpf);
        assertEquals(1, deploymentPlugin.getPluginMessages().size());
        assertEquals("YAWL Engine message: test", deploymentPlugin.getPluginMessages().get(0).getMessage());
    }


    @Test
    public void testGetNativeType() {
        assertEquals("YAWL 2.2", deploymentPlugin.getNativeType());
    }

}