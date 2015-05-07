package org.apromore.manager.service;

import static org.powermock.api.easymock.PowerMock.createMock;

import org.apromore.manager.ManagerPortalEndpoint;
import org.apromore.service.BPMNMinerService;
import org.apromore.service.CanoniserService;
import org.apromore.service.ClusterService;
import org.apromore.service.DatabaseService;
import org.apromore.service.DeploymentService;
import org.apromore.service.DomainService;
import org.apromore.service.FormatService;
import org.apromore.service.FragmentService;
import org.apromore.service.MergeService;
import org.apromore.service.PluginService;
import org.apromore.service.PQLService;
import org.apromore.service.ProcessService;
import org.apromore.service.SecurityService;
import org.apromore.service.SimilarityService;
import org.apromore.service.UserService;
import org.apromore.service.WorkspaceService;
import org.apromore.service.helper.UIHelper;
import org.apromore.service.helper.UserInterfaceHelper;
import org.apromore.service.impl.BPMNMinerServiceImpl;
import org.apromore.service.impl.CanoniserServiceImpl;
import org.apromore.service.impl.ClusterServiceImpl;
import org.apromore.service.impl.DatabaseServiceImpl;
import org.apromore.service.impl.DeploymentServiceImpl;
import org.apromore.service.impl.DomainServiceImpl;
import org.apromore.service.impl.FormatServiceImpl;
import org.apromore.service.impl.FragmentServiceImpl;
import org.apromore.service.impl.MergeServiceImpl;
import org.apromore.service.impl.PluginServiceImpl;
import org.apromore.service.impl.PQLServiceImpl;
import org.apromore.service.impl.ProcessServiceImpl;
import org.apromore.service.impl.SecurityServiceImpl;
import org.apromore.service.impl.SimilarityServiceImpl;
import org.apromore.service.impl.UserServiceImpl;
import org.apromore.service.impl.WorkspaceServiceImpl;
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

        endpoint = new ManagerPortalEndpoint(deploymentService, pluginService, fragmentSrv, canoniserService, procSrv,
                clusterService, frmSrv, domSrv, userSrv, simSrv, merSrv, secSrv, wrkSrv, uiHelper,
                pqlSrv, dbSrv, bpmnMinerSrv);
    }
}
