package org.apromore.manager.service;

import org.apromore.TestData;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.exception.ImportException;
import org.apromore.manager.canoniser.ManagerCanoniserClient;
import org.apromore.model.EditSessionType;
import org.apromore.model.ImportProcessInputMsgType;
import org.apromore.model.ImportProcessOutputMsgType;
import org.apromore.model.ObjectFactory;
import org.apromore.model.PluginParameter;
import org.apromore.model.PluginParameters;
import org.apromore.model.ProcessSummaryType;
import org.apromore.plugin.message.PluginMessage;
import org.apromore.plugin.message.PluginMessageImpl;
import org.apromore.service.CanonicalConverter;
import org.apromore.service.CanoniserService;
import org.apromore.service.ClusterService;
import org.apromore.service.DeploymentService;
import org.apromore.service.DomainService;
import org.apromore.service.FormatService;
import org.apromore.service.FragmentService;
import org.apromore.service.MergeService;
import org.apromore.service.PluginService;
import org.apromore.service.ProcessService;
import org.apromore.service.SessionService;
import org.apromore.service.SimilarityService;
import org.apromore.service.UserService;
import org.apromore.service.impl.CanonicalConverterAdapter;
import org.apromore.service.impl.CanoniserServiceImpl;
import org.apromore.service.impl.ClusterServiceImpl;
import org.apromore.service.impl.DeploymentServiceImpl;
import org.apromore.service.impl.DomainServiceImpl;
import org.apromore.service.impl.FormatServiceImpl;
import org.apromore.service.impl.FragmentServiceImpl;
import org.apromore.service.impl.MergeServiceImpl;
import org.apromore.service.impl.PluginServiceImpl;
import org.apromore.service.impl.ProcessServiceImpl;
import org.apromore.service.impl.SessionServiceImpl;
import org.apromore.service.impl.SimilarityServiceImpl;
import org.apromore.service.impl.UserServiceImpl;
import org.apromore.service.model.CanonisedProcess;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import javax.activation.DataHandler;
import javax.xml.bind.JAXBElement;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

public class ImportProcessEndpointTest {

    private ManagerPortalEndpoint endpoint;

    private DeploymentService deploymentService;
    private CanoniserService canoniserService;
    private PluginService pluginService;
    private FragmentService fragmentSrv;
    private ProcessService procSrv;
    private ClusterService clusterService;
    private FormatService frmSrv;
    private DomainService domSrv;
    private UserService userSrv;
    private SimilarityService simSrv;
    private MergeService merSrv;
    private SessionService sesSrv;
    private CanonicalConverter convertor;
    private ManagerCanoniserClient caClient;

    @Before
    public void setUp() throws Exception {
        deploymentService = createMock(DeploymentServiceImpl.class);
        pluginService = createMock(PluginServiceImpl.class);
        fragmentSrv = createMock(FragmentServiceImpl.class);
        canoniserService = createMock(CanoniserServiceImpl.class);
        procSrv = createMock(ProcessServiceImpl.class);
        clusterService = createMock(ClusterServiceImpl.class);
        frmSrv = createMock(FormatServiceImpl.class);
        domSrv = createMock(DomainServiceImpl.class);
        userSrv = createMock(UserServiceImpl.class);
        simSrv = createMock(SimilarityServiceImpl.class);
        merSrv = createMock(MergeServiceImpl.class);
        sesSrv = createMock(SessionServiceImpl.class);
        convertor = createMock(CanonicalConverterAdapter.class);
        caClient = createMock(ManagerCanoniserClient.class);

        endpoint = new ManagerPortalEndpoint(deploymentService, pluginService, fragmentSrv, canoniserService, procSrv,
                clusterService, frmSrv, domSrv, userSrv, simSrv, merSrv, sesSrv, convertor, caClient);
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
        PluginParameters properties = new PluginParameters();
        PluginParameter property = new PluginParameter();
        property.setId("test");
        property.setClazz("java.lang.String");
        property.setName("test");
        property.setValue("");
        properties.getParameter().add(property);
        msg.setCanoniserParameters(properties);
        DataHandler nativeXml = new DataHandler(TestData.EPML, "text/xml");
        msg.setProcessDescription(nativeXml);
        JAXBElement<ImportProcessInputMsgType> request = new ObjectFactory().createImportProcessRequest(msg);

        CanonisedProcess cp = new CanonisedProcess();
        ArrayList<PluginMessage> pluginMsg = new ArrayList<PluginMessage>();
        pluginMsg.add(new PluginMessageImpl("test"));
        cp.setMessages(pluginMsg);
        expect(canoniserService.canonise(eq(edit.getNativeType()), anyObject(InputStream.class), anyObject(java.util.Set.class))).andReturn(cp);

        ProcessSummaryType procSummary = new ProcessSummaryType();
        expect(procSrv.importProcess(eq(edit.getUsername()), eq(edit.getUsername()), anyObject(String.class), eq(edit.getVersionName()),
                eq(edit.getNativeType()), eq(cp), anyObject(InputStream.class), eq(edit.getDomain()), anyObject(String.class),
                eq(edit.getCreationDate()), eq(edit.getLastUpdate()))).andReturn(procSummary);

        replayAll();

        JAXBElement<ImportProcessOutputMsgType> response = endpoint.importProcess(request);

        Assert.assertNotNull(response.getValue().getResult());
        Assert.assertNotNull(response.getValue().getImportProcessResult());
        Assert.assertEquals("Result Code Doesn't Match", response.getValue().getResult().getCode().intValue(), 0);

        Assert.assertNotNull(response.getValue().getImportProcessResult().getMessage());
        Assert.assertEquals(response.getValue().getImportProcessResult().getMessage().getMessage().get(0).getValue(), "test");
        Assert.assertNotNull(response.getValue().getImportProcessResult().getProcessSummary());

        verifyAll();
    }

}
