package org.apromore.manager.service;

import static org.powermock.api.easymock.PowerMock.createMock;

import org.apromore.manager.ManagerPortalEndpoint;
import org.apromore.service.*;
import org.apromore.service.helper.UIHelper;
import org.apromore.service.helper.UserInterfaceHelper;
import org.apromore.service.impl.*;
import org.junit.Before;

public abstract class AbstractEndpointUnitTest {

    protected ManagerPortalEndpoint endpoint;

    protected DeploymentService deploymentService;
    protected CanoniserService canoniserService;
    protected PluginService pluginService;
    protected FragmentService fragmentSrv;
    protected ProcessService procSrv;
    protected ClusterService clusterService;
    protected FormatService frmSrv;
    protected DomainService domSrv;
    protected UserService userSrv;
    protected SimilarityService simSrv;
    protected MergeService merSrv;
    protected SecurityService secSrv;
    protected WorkspaceService wrkSrv;
    protected UserInterfaceHelper uiHelper;
    protected PQLService pqlSrv;
    protected DatabaseService dbSrv;
    protected BPMNMinerService bpmnMinerSrv;
    protected ProDriftDetectionService proDriftSrv;

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
        pqlSrv = createMock(PQLServiceImpl.class);
        dbSrv = createMock(DatabaseServiceImpl.class);
        bpmnMinerSrv = createMock(BPMNMinerServiceImpl.class);
        proDriftSrv = createMock(ProDriftDetectionServiceImpl.class);

        endpoint = new ManagerPortalEndpoint(deploymentService, pluginService, fragmentSrv, canoniserService, procSrv,
                clusterService, frmSrv, domSrv, userSrv, simSrv, merSrv, secSrv, wrkSrv, uiHelper,
                pqlSrv, dbSrv, bpmnMinerSrv, proDriftSrv);
    }
}
