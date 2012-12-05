package org.apromore.manager.service;

import org.apromore.dao.model.User;
import org.apromore.manager.canoniser.ManagerCanoniserClient;
import org.apromore.mapper.UserMapper;
import org.apromore.model.ObjectFactory;
import org.apromore.model.UserType;
import org.apromore.model.WriteUserInputMsgType;
import org.apromore.model.WriteUserOutputMsgType;
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
import org.apromore.service.helper.UIHelper;
import org.apromore.service.helper.UserInterfaceHelper;
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
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.JAXBElement;

import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

/**
 * Test the Read User method on the Manager Portal Endpoint WebService.
 */
public class WriteUserEndpointTest {

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
    private UserInterfaceHelper uiHelper;
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
        uiHelper = createMock(UIHelper.class);
        convertor = createMock(CanonicalConverterAdapter.class);
        caClient = createMock(ManagerCanoniserClient.class);

        endpoint = new ManagerPortalEndpoint(deploymentService, pluginService, fragmentSrv, canoniserService, procSrv,
                clusterService, frmSrv, domSrv, userSrv, simSrv, merSrv, sesSrv, uiHelper, convertor, caClient);
    }



    @Test
    public void testInvokeWriteUser() throws Exception {
        mockStatic(UserMapper.class);

        UserType userType = createUserType();
        User user = UserMapper.convertFromUserType(userType);

        WriteUserInputMsgType msg = new WriteUserInputMsgType();
        msg.setUser(userType);
        JAXBElement<WriteUserInputMsgType> request = new ObjectFactory().createWriteUserRequest(msg);

        userSrv.writeUser(EasyMock.<User>anyObject());

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
        userType.setEmail("test@test.com");
        userType.setFirstname("test");
        userType.setLastname("tester");
        userType.setPasswd("test");
        return userType;
    }

}

