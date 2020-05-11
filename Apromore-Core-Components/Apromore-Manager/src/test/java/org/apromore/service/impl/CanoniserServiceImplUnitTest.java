/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2011, 2012 , 2015 - 2017 Queensland University of Technology.
 * Copyright (C) 2012 Felix Mannhardt.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * Copyright (C) 2020, Apromore Pty Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.service.impl;

import org.apromore.canoniser.Canoniser;
import org.apromore.canoniser.provider.CanoniserProvider;
import org.apromore.common.ConfigBean;
import org.apromore.plugin.exception.PluginNotFoundException;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

/**
 * Unit test the UserService Implementation.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class CanoniserServiceImplUnitTest {

    private CanoniserServiceImpl myService;
    private CanoniserProvider mockProvider;
    private Canoniser mockCanoniser;

    @Before
    public final void setUp() throws Exception {
        mockProvider = createMock(CanoniserProvider.class);
        mockCanoniser = createMock(Canoniser.class);
        myService = new CanoniserServiceImpl(mockProvider, new ConfigBean());
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
        HashSet<Canoniser> canoniserSet = new HashSet<>();
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


//    @Test
//    @SuppressWarnings("unchecked")
//    public void deserialize() throws Exception {
//        JAXBContext jc = JAXBContext.newInstance(Constants.CPF_CONTEXT);
//        Unmarshaller u = jc.createUnmarshaller();
//        InputStream data = new ByteArrayInputStream(TestData.CPF2.getBytes());
//        JAXBElement<CanonicalProcessType> rootElement = (JAXBElement<CanonicalProcessType>) u.unmarshal(data);
//        CanonicalProcessType canType = rootElement.getValue();
//
//        Canonical graph = myService.deserializeCPF(canType);
//        assertThat(graph, notNullValue());
//
//        DirectedGraphAlgorithms<ControlFlow<FlowNode>, FlowNode> dga = new DirectedGraphAlgorithms<ControlFlow<FlowNode>, FlowNode>();
//        assertThat(dga.isCyclic(graph), is(false));
//
//        RPST<Edge, Node> rpst = new RPST<Edge, Node>(graph);
//        assertThat(rpst, notNullValue());
//    }
//
//    @Test
//    @SuppressWarnings("unchecked")
//    public void deserialize2() throws Exception {
//        JAXBContext jc = JAXBContext.newInstance(Constants.CPF_CONTEXT);
//        Unmarshaller u = jc.createUnmarshaller();
//        InputStream data = new ByteArrayInputStream(TestData.CPF2.getBytes());
//        JAXBElement<CanonicalProcessType> rootElement = (JAXBElement<CanonicalProcessType>) u.unmarshal(data);
//        CanonicalProcessType canType = rootElement.getValue();
//
//        Canonical graph = myService.deserializeCPF(canType);
//        assertThat(graph, notNullValue());
//
//        CanonicalProcessType canTyp2 = myService.serializeCPF(graph);
//        assertThat(canTyp2, notNullValue());
//    }

}
