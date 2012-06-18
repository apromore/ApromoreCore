package org.apromore.manager.service;

import org.apromore.manager.canoniser.ManagerCanoniserClient;
import org.apromore.manager.da.ManagerDataAccessClient;
import org.apromore.model.ObjectFactory;
import org.apromore.model.ProcessSummariesType;
import org.apromore.model.ReadProcessSummariesInputMsgType;
import org.apromore.model.ReadProcessSummariesOutputMsgType;
import org.apromore.service.ProcessService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

/**
 * Test the Manager Portal Endpoint WebService.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/applicationContext-jpa-TEST.xml",
        "classpath:META-INF/spring/applicationContext-services-TEST.xml"
})
public class ReadProcessSummariesEndpointTest {

    private ManagerPortalEndpoint endpoint;
    private Document requestDocument;
    private Document responseDocument;
    private ManagerDataAccessClient daMock;
    private ManagerCanoniserClient caMock;

    private ProcessService processSrv;


    @Before
    public void setUp() throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        requestDocument = documentBuilder.newDocument();
        responseDocument = documentBuilder.newDocument();
        daMock = createMock(ManagerDataAccessClient.class);
        caMock = createMock(ManagerCanoniserClient.class);
        processSrv = createMock(ProcessService.class);
        endpoint = new ManagerPortalEndpoint();
        endpoint.setCaClient(caMock);
        endpoint.setDaClient(daMock);
        endpoint.setProcSrv(processSrv);
    }


    @Test
    public void testInvokeReadProcessSummaries() throws Exception {
        String searchExpression = "";

        ReadProcessSummariesInputMsgType msg = new ReadProcessSummariesInputMsgType();
        msg.setSearchExpression(searchExpression);
        JAXBElement<ReadProcessSummariesInputMsgType> request = new ObjectFactory().createReadProcessSummariesRequest(msg);

        ProcessSummariesType procSummary = new ProcessSummariesType();
        expect(processSrv.readProcessSummaries(searchExpression)).andReturn(procSummary);

        replay(processSrv);

        JAXBElement<ReadProcessSummariesOutputMsgType> response = endpoint.readProcessSummaries(request);
        Assert.assertNotNull(response.getValue().getResult());
        Assert.assertNotNull(response.getValue().getProcessSummaries());
        Assert.assertEquals("Result Code Doesn't Match", response.getValue().getResult().getCode().intValue(), 0);
        Assert.assertEquals("UserNames should be empty", response.getValue().getProcessSummaries().getProcessSummary().size(), 0);

        verify(processSrv);
    }

}
