package org.apromore.service.impl;

import static org.easymock.EasyMock.expect;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import org.apromore.TestData;
import org.apromore.canoniser.Canoniser;
import org.apromore.canoniser.provider.CanoniserProvider;
import org.apromore.canoniser.provider.impl.CanoniserProviderImpl;
import org.apromore.canoniser.provider.impl.OSGiCanoniserProvider;
import org.apromore.common.Constants;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.graph.JBPT.CPF;
import org.apromore.plugin.exception.PluginNotFoundException;
import org.apromore.plugin.property.PluginParameterType;
import org.apromore.plugin.property.ParameterType;
import org.jbpt.graph.algo.DirectedGraphAlgorithms;
import org.jbpt.graph.algo.rpst.RPST;
import org.jbpt.pm.ControlFlow;
import org.jbpt.pm.FlowNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Unit test the UserService Implementation.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/applicationContext-jpa-TEST.xml",
        "classpath:META-INF/spring/applicationContext-services-TEST.xml"
})
public class CanoniserServiceImplUnitTest {

    private CanoniserServiceImpl myService;
    private CanoniserProvider mockProvider;
    private Canoniser mockCanoniser;

    @Before
    public final void setUp() throws Exception {
        myService = new CanoniserServiceImpl();
        mockProvider = createMock(CanoniserProvider.class);
        mockCanoniser = createMock(Canoniser.class);
        myService.setCanoniserProvider(mockProvider);
    }
    
    @Test
    public void testFindByNativeType() throws PluginNotFoundException {
        expect(mockProvider.findByNativeType("test")).andReturn(mockCanoniser).anyTimes();
        expect(mockProvider.findByNativeType("invalid")).andThrow(new PluginNotFoundException());

        replayAll();
        
        Canoniser c = myService.findByNativeType("test");
        assertNotNull(c);
        assertEquals(mockCanoniser, c);

        try {
            myService.findByNativeType("invalid");
            fail();
        } catch (PluginNotFoundException e) {
        }
        
        verifyAll();
    }
    
    @Test
    public void testListByNativeType() throws PluginNotFoundException {
        HashSet<Canoniser> canoniserSet = new HashSet<Canoniser>();
        canoniserSet.add(mockCanoniser);

        expect(mockProvider.listByNativeType("test")).andReturn(canoniserSet).anyTimes();
        expect(mockProvider.listByNativeType("invalid")).andReturn(new HashSet<Canoniser>());

        replayAll();
        
        Set<Canoniser> c = myService.listByNativeType("test");
        assertNotNull(c);
        assertEquals(1, c.size());

        Set<Canoniser> c2 = myService.listByNativeType("invalid");
        assertNotNull(c2);
        assertTrue(c2.isEmpty());
        
        verifyAll();
    }    


    @Test
    @SuppressWarnings("unchecked")
    public void deserialize() throws Exception {
        JAXBContext jc = JAXBContext.newInstance(Constants.CPF_CONTEXT);
        Unmarshaller u = jc.createUnmarshaller();
        InputStream data = new ByteArrayInputStream(TestData.CPF2.getBytes());
        JAXBElement<CanonicalProcessType> rootElement = (JAXBElement<CanonicalProcessType>) u.unmarshal(data);
        CanonicalProcessType canType = rootElement.getValue();

        CPF graph = myService.deserializeCPF(canType);
        assertThat(graph, notNullValue());

        DirectedGraphAlgorithms<ControlFlow<FlowNode>, FlowNode> dga = new DirectedGraphAlgorithms<ControlFlow<FlowNode>, FlowNode>();
        assertThat(dga.isCyclic(graph), is(false));

        RPST<ControlFlow<FlowNode>, FlowNode> rpst = new RPST<ControlFlow<FlowNode>, FlowNode>(graph);
        assertThat(rpst, notNullValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void deserialize2() throws Exception {
        JAXBContext jc = JAXBContext.newInstance(Constants.CPF_CONTEXT);
        Unmarshaller u = jc.createUnmarshaller();
        InputStream data = new ByteArrayInputStream(TestData.CPF2.getBytes());
        JAXBElement<CanonicalProcessType> rootElement = (JAXBElement<CanonicalProcessType>) u.unmarshal(data);
        CanonicalProcessType canType = rootElement.getValue();

        CPF graph = myService.deserializeCPF(canType);
        assertThat(graph, notNullValue());

        CanonicalProcessType canTyp2 = myService.serializeCPF(graph);
        assertThat(canTyp2, notNullValue());
    }



}
