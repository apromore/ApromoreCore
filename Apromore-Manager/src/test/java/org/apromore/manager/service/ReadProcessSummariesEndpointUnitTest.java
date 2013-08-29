package org.apromore.manager.service;

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import javax.xml.bind.JAXBElement;

import org.apromore.manager.ManagerPortalEndpoint;
import org.apromore.model.ObjectFactory;
import org.apromore.model.ProcessSummariesType;
import org.apromore.model.ReadProcessSummariesInputMsgType;
import org.apromore.model.ReadProcessSummariesOutputMsgType;
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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test the Manager Portal Endpoint WebService.
 */
public class ReadProcessSummariesEndpointUnitTest {

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
    public void testInvokeReadProcessSummaries() throws Exception {
        String searchExpression = "";

        ReadProcessSummariesInputMsgType msg = new ReadProcessSummariesInputMsgType();
        msg.setSearchExpression(searchExpression);
        JAXBElement<ReadProcessSummariesInputMsgType> request = new ObjectFactory().createReadProcessSummariesRequest(msg);

        ProcessSummariesType procSummary = new ProcessSummariesType();
        expect(procSrv.readProcessSummaries(searchExpression)).andReturn(procSummary);

        replayAll();

        JAXBElement<ReadProcessSummariesOutputMsgType> response = endpoint.readProcessSummaries(request);
        Assert.assertNotNull(response.getValue().getResult());
        Assert.assertNotNull(response.getValue().getProcessSummaries());
        Assert.assertEquals("Result Code Doesn't Match", response.getValue().getResult().getCode().intValue(), 0);
        Assert.assertEquals("UserNames should be empty", response.getValue().getProcessSummaries().getProcessSummary().size(), 0);

        verifyAll();
    }

}
