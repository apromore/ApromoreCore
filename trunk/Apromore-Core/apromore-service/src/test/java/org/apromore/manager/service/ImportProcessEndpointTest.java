package org.apromore.manager.service;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.activation.DataHandler;
import javax.xml.bind.JAXBElement;

import org.apromore.TestData;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.exception.ImportException;
import org.apromore.model.EditSessionType;
import org.apromore.model.ImportProcessInputMsgType;
import org.apromore.model.ImportProcessOutputMsgType;
import org.apromore.model.ObjectFactory;
import org.apromore.model.PluginProperties;
import org.apromore.model.PluginProperty;
import org.apromore.model.ProcessSummaryType;
import org.apromore.plugin.message.PluginMessage;
import org.apromore.plugin.message.PluginMessageImpl;
import org.apromore.service.impl.CanoniserServiceImpl;
import org.apromore.service.impl.ProcessServiceImpl;
import org.apromore.service.model.CanonisedProcess;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:META-INF/spring/applicationContext-jpa-TEST.xml",
        "classpath:META-INF/spring/applicationContext-services-TEST.xml" })
public class ImportProcessEndpointTest {

    private ManagerPortalEndpoint endpoint;

    private ProcessServiceImpl processService;
    private CanoniserServiceImpl canoniserService;

    @Before
    public void setUp() throws Exception {
        processService = createMock(ProcessServiceImpl.class);
        canoniserService = createMock(CanoniserServiceImpl.class);
        endpoint = new ManagerPortalEndpoint();
        endpoint.setProcSrv(processService);
        endpoint.setCanoniserService(canoniserService);
    }

    @Test
    public void testImportProcess() throws ImportException, IOException, CanoniserException {
        ImportProcessInputMsgType msg = new ImportProcessInputMsgType();
        EditSessionType edit = new EditSessionType();
        edit.setAnnotation("test");
        edit.setCreationDate("");
        edit.setDomain("");
        edit.setLastUpdate("");
        edit.setNativeType("EPML 2.0");
        edit.setProcessId(12143);
        edit.setProcessName("test");
        edit.setUsername("test");
        edit.setVersionName("1.0");
        edit.setWithAnnotation(true);
        msg.setEditSession(edit);
        PluginProperties properties = new PluginProperties();
        PluginProperty property = new PluginProperty();
        property.setId("test");
        property.setClazz("java.lang.String");
        property.setName("test");
        property.setValue("");
        properties.getProperty().add(property);
        msg.setCanoniserProperties(properties);
        DataHandler nativeXml = new DataHandler(TestData.EPML, "text/xml");
        msg.setProcessDescription(nativeXml);
        JAXBElement<ImportProcessInputMsgType> request = new ObjectFactory().createImportProcessRequest(msg);

        CanonisedProcess cp = new CanonisedProcess();
        ArrayList<PluginMessage> pluginMsg = new ArrayList<PluginMessage>();
        pluginMsg.add(new PluginMessageImpl("test"));
        cp.setMessages(pluginMsg);
        expect(canoniserService.canonise(eq(edit.getNativeType()), anyObject(InputStream.class), anyObject(java.util.Set.class))).andReturn(cp);

        ProcessSummaryType procSummary = new ProcessSummaryType();
        expect(
                processService.importProcess(eq(edit.getUsername()), eq(edit.getUsername()), anyObject(String.class), eq(edit.getVersionName()),
                        eq(edit.getNativeType()), eq(cp), anyObject(InputStream.class), eq(edit.getDomain()), anyObject(String.class),
                        eq(edit.getCreationDate()), eq(edit.getLastUpdate()))).andReturn(procSummary);

        replay(canoniserService);
        replay(processService);

        JAXBElement<ImportProcessOutputMsgType> response = endpoint.importProcess(request);

        Assert.assertNotNull(response.getValue().getResult());
        Assert.assertNotNull(response.getValue().getImportProcessResult());
        Assert.assertEquals("Result Code Doesn't Match", response.getValue().getResult().getCode().intValue(), 0);

        Assert.assertNotNull(response.getValue().getImportProcessResult().getMessage());
        Assert.assertEquals(response.getValue().getImportProcessResult().getMessage().getMessage().get(0).getValue(), "test");
        Assert.assertNotNull(response.getValue().getImportProcessResult().getProcessSummary());

        verify(canoniserService);
        verify(processService);
    }

}
