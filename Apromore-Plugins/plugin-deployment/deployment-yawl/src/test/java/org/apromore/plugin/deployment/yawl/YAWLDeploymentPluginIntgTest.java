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
import org.apromore.plugin.deployment.exception.DeploymentException;
import org.apromore.plugin.property.PropertyType;
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
        for (PropertyType p: deploymentPlugin.getMandatoryProperties()) {
            if (p.getId().equals("yawlEngineUrl")) {
                p.setValue("http://localhost:8080/yawl/ia");
            } else if (p.getId().equals("yawlUsername")) {
                p.setValue("admin");
            } else if (p.getId().equals("yawlPassword")) {
                p.setValue("YAWL");
            }
        }
    }

    @Test
    public void testDeployProcessCanonicalProcessType() throws IOException, JAXBException, SAXException, DeploymentException {
        if (checkYAWLServerAvailable()) {
            BufferedInputStream cpfInputStream = new BufferedInputStream(new FileInputStream("src/test/resources/WPC1Sequence.yawl.cpf"));
            CanonicalProcessType cpf = CPFSchema.unmarshalCanonicalFormat(cpfInputStream, true).getValue();
            cpfInputStream.close();
            deploymentPlugin.deployProcess(cpf);
            assertEquals(1, deploymentPlugin.getPluginMessages().size());
            assertEquals("Process Simple Make Trip Process successfully deployed.", deploymentPlugin.getPluginMessages().get(0).getMessage());
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