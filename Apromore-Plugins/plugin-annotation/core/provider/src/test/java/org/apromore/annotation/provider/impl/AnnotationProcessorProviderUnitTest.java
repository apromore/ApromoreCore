/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2013 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package org.apromore.annotation.provider.impl;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.util.HashSet;
import java.util.Set;

import org.apromore.annotation.AnnotationProcessor;
import org.apromore.plugin.exception.PluginNotFoundException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Tests for the annotation post processor Provider
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class AnnotationProcessorProviderUnitTest {

    private AnnotationProcessorProviderImpl provider;

    private AnnotationProcessor c1;
    private AnnotationProcessor c2;
    private AnnotationProcessor c3;
    private AnnotationProcessor c4;

    @Before
    public void setUp() throws Exception {
        final AnnotationProcessorProviderImpl cp = new AnnotationProcessorProviderImpl();
        final Set<AnnotationProcessor> annotationSet = new HashSet<>();
        c1 = createMock(AnnotationProcessor.class);
        expect(c1.getName()).andReturn("XPDL 2.0 - BPMN 2.0 - Annotation Post Processor");
        expect(c1.getVersion()).andReturn("1.0.0");
        expect(c1.getProcessFormatProcessor()).andReturn("XPDL 2.0 BPMN 2.0");
        replay(c1);
        annotationSet.add(c1);
        c2 = createMock(AnnotationProcessor.class);
        annotationSet.add(c2);
        c3 = createMock(AnnotationProcessor.class);
        annotationSet.add(c3);
        cp.setAnnotationProcessorSet(annotationSet);
        c4 = createMock(AnnotationProcessor.class);
        this.provider = cp;
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testListAll() {
        assertNotNull(provider.listAll());
        assertEquals(provider.listAll().size(), 3);
        assertTrue(provider.listAll().contains(c1));
        assertFalse(provider.listAll().contains(c4));
        provider.listAll().add(c4);
    }

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testListBySourceAndTargetProcessType() throws PluginNotFoundException {
        assertNotNull(provider.listBySourceAndTargetProcessType("XPDL 2.0 BPMN 2.0"));
    }

    @Test
    public void testListBySourceAndTargetProcessTypeAndName() throws PluginNotFoundException {
        assertNotNull(provider.listBySourceAndTargetProcessTypeAndName("XPDL 2.0 BPMN 2.0", "XPDL 2.0 - BPMN 2.0 - Annotation Post Processor"));
    }

    @Test
    public void testFindBySourceAndTargetProcessTypeAndNameAndVersion() throws PluginNotFoundException {
        assertNotNull(provider.findBySourceAndTargetProcessTypeAndNameAndVersion("XPDL 2.0 BPMN 2.0", "XPDL 2.0 - BPMN 2.0 - Annotation Post Processor", "1.0.0"));
    }

    @Test
    public void testNotFound() throws PluginNotFoundException {
        exception.expect(PluginNotFoundException.class);
        provider.listBySourceAndTargetProcessType("N/A N/A");
    }

}
