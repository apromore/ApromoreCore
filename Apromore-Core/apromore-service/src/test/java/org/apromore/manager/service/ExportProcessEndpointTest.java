package org.apromore.manager.service;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import javax.activation.DataHandler;
import javax.xml.bind.JAXBElement;

import org.apromore.TestData;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.exception.ExportFormatException;
import org.apromore.exception.ImportException;
import org.apromore.model.ExportFormatInputMsgType;
import org.apromore.model.ExportFormatOutputMsgType;
import org.apromore.model.ExportFormatResultType;
import org.apromore.model.ObjectFactory;
import org.apromore.model.PluginMessages;
import org.apromore.model.PluginProperties;
import org.apromore.model.PluginProperty;
import org.apromore.plugin.message.PluginMessage;
import org.apromore.plugin.message.PluginMessageImpl;
import org.apromore.service.impl.ProcessServiceImpl;
import org.apromore.service.model.CanonisedProcess;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:META-INF/spring/applicationContext-jpa-TEST.xml",
        "classpath:META-INF/spring/applicationContext-services-TEST.xml" })
public class ExportProcessEndpointTest {

    private ManagerPortalEndpoint endpoint;

    private ProcessServiceImpl processService;

    @Before
    public void setUp() throws Exception {
        processService = createMock(ProcessServiceImpl.class);
        endpoint = new ManagerPortalEndpoint();
        endpoint.setProcSrv(processService);
    }

    @Test
    public void testImportProcess() throws ImportException, IOException, CanoniserException, ExportFormatException {

        ExportFormatInputMsgType msg = new ExportFormatInputMsgType();
        msg.setAnnotationName("");
        msg.setFormat("EPML 2.0");
        msg.setOwner("test");
        msg.setProcessId(123);
        msg.setProcessName("test");
        msg.setVersionName("1.0");
        msg.setWithAnnotations(true);

        PluginProperties properties = new PluginProperties();
        PluginProperty property = new PluginProperty();
        property.setId("test");
        property.setClazz("java.lang.String");
        property.setName("test");
        property.setValue("");
        property.setIsMandatory(false);
        property.setDescription("");
        properties.getProperty().add(property);
        msg.setCanoniserProperties(properties);

        JAXBElement<ExportFormatInputMsgType> request = new ObjectFactory().createExportFormatRequest(msg);

        CanonisedProcess cp = new CanonisedProcess();
        ArrayList<PluginMessage> pluginMsg = new ArrayList<PluginMessage>();
        pluginMsg.add(new PluginMessageImpl("test"));
        cp.setMessages(pluginMsg);

        ExportFormatResultType exportFormatResultType = new ExportFormatResultType();
        exportFormatResultType.setMessage(new PluginMessages());
        exportFormatResultType.setNative(new DataHandler(TestData.EPML, "text/xml"));
        expect(
                processService.exportProcess(eq(msg.getProcessName()), eq(msg.getProcessId()), eq(msg.getVersionName()), eq(msg.getFormat()),
                        eq(msg.getAnnotationName()), EasyMock.anyBoolean(), anyObject(Set.class))).andReturn(exportFormatResultType);
        replay(processService);

        JAXBElement<ExportFormatOutputMsgType> response = endpoint.exportFormat(request);

        Assert.assertNotNull(response.getValue().getResult());
        Assert.assertNotNull(response.getValue().getExportResult());
        Assert.assertEquals("Result Code Doesn't Match", response.getValue().getResult().getCode().intValue(), 0);

        verify(processService);
    }

}
