package org.apromore.plugin.deployment.yawl;

import static org.junit.Assert.assertEquals;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.apache.http.HttpVersion;
import org.apache.http.client.fluent.Request;
import org.apromore.cpf.CPFSchema;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.PluginResult;
import org.apromore.plugin.exception.PluginException;
import org.apromore.plugin.impl.DefaultPluginRequest;
import org.apromore.plugin.property.RequestPropertyType;
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
    public void testDeployProcessCanonicalProcessType() throws IOException, JAXBException, SAXException, PluginException {
        if (checkYAWLServerAvailable()) {
            BufferedInputStream cpfInputStream = new BufferedInputStream(new FileInputStream("src/test/resources/WPC1Sequence.yawl.cpf"));
            CanonicalProcessType cpf = CPFSchema.unmarshalCanonicalFormat(cpfInputStream, true).getValue();
            cpfInputStream.close();
            DefaultPluginRequest request = new DefaultPluginRequest();
            request.addRequestProperty(new RequestPropertyType<String>("yawlEngineUrl", "http://localhost:8080/yawl/ia"));
            request.addRequestProperty(new RequestPropertyType<String>("yawlUsername", "admin"));
            request.addRequestProperty(new RequestPropertyType<String>("yawlPassword", "YAWL"));
            PluginResult result = deploymentPlugin.deployProcess(cpf, request);
            assertEquals(1, result.getPluginMessage().size());
            assertEquals("Process Simple Make Trip Process successfully deployed.", result.getPluginMessage().get(0).getMessage());
        }
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