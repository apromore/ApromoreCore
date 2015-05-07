package org.apromore.manager.service;

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;

import org.apromore.manager.ManagerPortalEndpoint;
import org.apromore.model.ObjectFactory;
import org.apromore.model.ReadDomainsInputMsgType;
import org.apromore.model.ReadDomainsOutputMsgType;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test the Read User method on the Manager Portal Endpoint WebService.
 */
public class ReadDomainsEndpointUnitTest extends AbstractEndpointUnitTest {

    @Test
    public void testInvokeReadDomains() throws Exception {
        ReadDomainsInputMsgType msg = new ReadDomainsInputMsgType();
        msg.setEmpty("");
        JAXBElement<ReadDomainsInputMsgType> request = new ObjectFactory().createReadDomainsRequest(msg);

        List<String> domains = new ArrayList<String>();
        expect(domSrv.findAllDomains()).andReturn(domains);

        replay(domSrv);

        JAXBElement<ReadDomainsOutputMsgType> response = endpoint.readDomains(request);
        Assert.assertNotNull(response.getValue().getResult());
        Assert.assertNotNull(response.getValue().getDomains());
        Assert.assertEquals("Result Code Doesn't Match", response.getValue().getResult().getCode().intValue(), 0);
        Assert.assertEquals("UserNames should be empty", response.getValue().getDomains().getDomain().size(), 0);

        verify(domSrv);
    }

}

