package org.apromore.manager.service;

import java.util.ArrayList;
import java.util.HashSet;
import javax.xml.bind.JAXBElement;

import org.apromore.anf.AnnotationsType;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.exception.LockFailedException;
import org.apromore.exception.SerializationException;
import org.apromore.graph.canonical.Canonical;
import org.apromore.manager.ManagerPortalEndpoint;
import org.apromore.model.DeployProcessInputMsgType;
import org.apromore.model.DeployProcessOutputMsgType;
import org.apromore.model.ObjectFactory;
import org.apromore.model.PluginParameters;
import org.apromore.plugin.deployment.exception.DeploymentException;
import org.apromore.plugin.message.PluginMessage;
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
import org.apromore.service.SessionService;
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
import org.apromore.service.impl.SessionServiceImpl;
import org.apromore.service.impl.SimilarityServiceImpl;
import org.apromore.service.impl.UserServiceImpl;
import org.apromore.service.impl.WorkspaceServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isNull;
import static org.junit.Assert.assertNotNull;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

public class DeployProcessEndpointTest {

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
        sesSrv = createMock(SessionServiceImpl.class);
        secSrv = createMock(SecurityServiceImpl.class);
        wrkSrv = createMock(WorkspaceServiceImpl.class);
        uiHelper = createMock(UIHelper.class);

        endpoint = new ManagerPortalEndpoint(deploymentService, pluginService, fragmentSrv, canoniserService, procSrv,
                clusterService, frmSrv, domSrv, userSrv, simSrv, merSrv, sesSrv, secSrv, wrkSrv, uiHelper);
    }

    @Test
    public void testDeployProces() throws DeploymentException, SerializationException, LockFailedException {
        DeployProcessInputMsgType msg = new DeployProcessInputMsgType();
        JAXBElement<DeployProcessInputMsgType> request = new ObjectFactory().createDeployProcessRequest(msg );

        String branchName = "test";
        request.getValue().setBranchName(branchName);
        String processName = "test";
        request.getValue().setProcessName(processName);
        Double versionName = 1.0d;
        request.getValue().setVersionName(versionName);
        String nativeType = "Test 2.1";
        request.getValue().setNativeType(nativeType);
        PluginParameters pluginProperties = new PluginParameters();
        request.getValue().setDeploymentParameters(pluginProperties);

        Canonical cpf = new Canonical();
        expect(procSrv.getCurrentProcessModel(processName, branchName, false)).andReturn(new CanonicalProcessType());
        //expect(convertor.convert(cpf)).andReturn(new CanonicalProcessType());
        expect(deploymentService.deployProcess(eq(nativeType), anyObject(CanonicalProcessType.class), isNull(AnnotationsType.class), anyObject(HashSet.class))).andReturn(new ArrayList<PluginMessage>());

        replayAll();

        JAXBElement<DeployProcessOutputMsgType> result = endpoint.deployProcess(request);

        Assert.assertNotNull(result.getValue().getResult());
        Assert.assertEquals("Result Code Doesn't Match", result.getValue().getResult().getCode().intValue(), 0);

        assertNotNull(result.getValue().getMessage());

        verifyAll();
    }

}
