package org.apromore.manager.service;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import javax.activation.DataHandler;
import javax.xml.bind.JAXBElement;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import org.apromore.TestData;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.exception.ExportFormatException;
import org.apromore.exception.ImportException;
import org.apromore.helper.Version;
import org.apromore.manager.ManagerPortalEndpoint;
import org.apromore.model.ExportFormatInputMsgType;
import org.apromore.model.ExportFormatOutputMsgType;
import org.apromore.model.ExportFormatResultType;
import org.apromore.model.ObjectFactory;
import org.apromore.model.PluginMessages;
import org.apromore.model.PluginParameter;
import org.apromore.model.PluginParameters;
import org.apromore.plugin.message.PluginMessage;
import org.apromore.plugin.message.PluginMessageImpl;
import org.apromore.service.CanoniserService;
import org.apromore.service.ClusterService;
import org.apromore.service.DeploymentService;
import org.apromore.service.DomainService;
import org.apromore.service.FormatService;
import org.apromore.service.FragmentService;
import org.apromore.service.MergeService;
import org.apromore.service.PluginService;
import org.apromore.service.ProcessService;
import org.apromore.service.SecurityService;
import org.apromore.service.SimilarityService;
import org.apromore.service.UserService;
import org.apromore.service.WorkspaceService;
import org.apromore.service.helper.UIHelper;
import org.apromore.service.helper.UserInterfaceHelper;
import org.apromore.service.impl.CanoniserServiceImpl;
import org.apromore.service.impl.ClusterServiceImpl;
import org.apromore.service.impl.DeploymentServiceImpl;
import org.apromore.service.impl.DomainServiceImpl;
import org.apromore.service.impl.FormatServiceImpl;
import org.apromore.service.impl.FragmentServiceImpl;
import org.apromore.service.impl.MergeServiceImpl;
import org.apromore.service.impl.PluginServiceImpl;
import org.apromore.service.impl.ProcessServiceImpl;
import org.apromore.service.impl.SecurityServiceImpl;
import org.apromore.service.impl.SimilarityServiceImpl;
import org.apromore.service.impl.UserServiceImpl;
import org.apromore.service.impl.WorkspaceServiceImpl;
import org.apromore.service.model.CanonisedProcess;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ExportProcessEndpointUnitTest {

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
    private SecurityService secSrv;
    private WorkspaceService wrkSrv;
    private UserInterfaceHelper uiHelper;

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
        secSrv = createMock(SecurityServiceImpl.class);
        wrkSrv = createMock(WorkspaceServiceImpl.class);
        uiHelper = createMock(UIHelper.class);

        endpoint = new ManagerPortalEndpoint(deploymentService, pluginService, fragmentSrv, canoniserService, procSrv,
                clusterService, frmSrv, domSrv, userSrv, simSrv, merSrv, secSrv, wrkSrv, uiHelper);
    }



    @Test
    public void testImportProcess() throws ImportException, IOException, CanoniserException, ExportFormatException {

        ExportFormatInputMsgType msg = new ExportFormatInputMsgType();
        msg.setAnnotationName("");
        msg.setFormat("EPML 2.0");
        msg.setOwner("test");
        msg.setProcessId(123);
        msg.setProcessName("test");
        msg.setBranchName("1.0");
        msg.setVersionNumber("1.0");
        msg.setWithAnnotations(true);

        PluginParameters properties = new PluginParameters();
        PluginParameter property = new PluginParameter();
        property.setId("test");
        property.setClazz("java.lang.String");
        property.setName("test");
        property.setValue("");
        property.setIsMandatory(false);
        property.setDescription("");
        properties.getParameter().add(property);
        msg.setCanoniserParameters(properties);

        JAXBElement<ExportFormatInputMsgType> request = new ObjectFactory().createExportFormatRequest(msg);

        CanonisedProcess cp = new CanonisedProcess();
        ArrayList<PluginMessage> pluginMsg = new ArrayList<>();
        pluginMsg.add(new PluginMessageImpl("test"));
        cp.setMessages(pluginMsg);

        Version version = new Version(msg.getVersionNumber());
        ExportFormatResultType exportFormatResultType = new ExportFormatResultType();
        exportFormatResultType.setMessage(new PluginMessages());
        exportFormatResultType.setNative(new DataHandler(TestData.EPML, "text/xml"));
        expect(procSrv.exportProcess(eq(msg.getProcessName()), eq(msg.getProcessId()), eq(msg.getBranchName()), eq(version),
                eq(msg.getFormat()), eq(msg.getAnnotationName()), EasyMock.anyBoolean(), anyObject(Set.class))).andReturn(exportFormatResultType);
        replayAll();

        JAXBElement<ExportFormatOutputMsgType> response = endpoint.exportFormat(request);

        Assert.assertNotNull(response.getValue().getResult());
        Assert.assertNotNull(response.getValue().getExportResult());
        Assert.assertEquals("Result Code Doesn't Match", response.getValue().getResult().getCode().intValue(), 0);

        verifyAll();
    }

}
