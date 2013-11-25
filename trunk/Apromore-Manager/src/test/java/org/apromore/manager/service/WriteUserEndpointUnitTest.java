package org.apromore.manager.service;

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import javax.xml.bind.JAXBElement;

import org.apromore.dao.model.User;
import org.apromore.manager.ManagerPortalEndpoint;
import org.apromore.mapper.UserMapper;
import org.apromore.model.*;
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
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test the Read User method on the Manager Portal Endpoint WebService.
 */
public class WriteUserEndpointUnitTest {

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
    public void testInvokeWriteUser() throws Exception {
        mockStatic(UserMapper.class);

        UserType userType = createUserType();
        User user = UserMapper.convertFromUserType(userType);

        WriteUserInputMsgType msg = new WriteUserInputMsgType();
        msg.setUser(userType);
        JAXBElement<WriteUserInputMsgType> request = new ObjectFactory().createWriteUserRequest(msg);

        expect(secSrv.createUser(EasyMock.<User>anyObject())).andReturn(user);

        replayAll();

        JAXBElement<WriteUserOutputMsgType> response = endpoint.writeUser(request);
        Assert.assertNotNull(response.getValue().getResult());
        Assert.assertEquals("Result Code Doesn't Match", response.getValue().getResult().getCode().intValue(), 0);

        verifyAll();
    }

//    @Test
//    public void testInvokeWriteUserThrowsException() throws Exception {
//        mockStatic(UserMapper.class);
//
//        UserType userType = createUserType();
//
//        WriteUserInputMsgType msg = new WriteUserInputMsgType();
//        msg.setUser(userType);
//        JAXBElement<WriteUserInputMsgType> request = new ObjectFactory().createWriteUserRequest(msg);
//
//        Exception e = new Exception("WriteUser threw exception");
//        userSrv.writeUser(EasyMock.<User>anyObject());
//        expectLastCall().andThrow(e);
//
//        replayAll();
//
//        JAXBElement<WriteUserOutputMsgType> response = endpoint.writeUser(request);
//        Assert.assertNotNull(response.getValue().getResult());
//        Assert.assertEquals("Result Code Doesn't Match", response.getValue().getResult().getCode().intValue(), -1);
//        Assert.assertEquals("Result Message Doesn't Match", response.getValue().getResult().getMessage(), "WriteUser threw exception");
//
//        verifyAll();
//    }

    private UserType createUserType() {
        UserType userType = new UserType();
        userType.setUsername("test");
        userType.setFirstName("test");
        userType.setLastName("tester");

        MembershipType membership = new MembershipType();
        membership.setEmail("test@test.com");
        membership.setPassword("password");
        userType.setMembership(membership);

        return userType;
    }

}

