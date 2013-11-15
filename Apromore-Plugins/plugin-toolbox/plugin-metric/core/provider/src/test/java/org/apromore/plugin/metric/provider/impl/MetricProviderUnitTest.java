/**
 *  Copyright 2013
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.apromore.plugin.metric.provider.impl;

import org.apromore.plugin.exception.PluginNotFoundException;
import org.apromore.plugin.metric.MetricPlugin;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.*;
import static org.easymock.EasyMock.*;

/**
 * Tests for the metric plugin Provider
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class MetricProviderUnitTest {

    private MetricPluginProviderImpl provider;

    private MetricPlugin c1;
    private MetricPlugin c2;
    private MetricPlugin c3;
    private MetricPlugin c4;

    @Before
    public void setUp() throws Exception {
        final MetricPluginProviderImpl cp = new MetricPluginProviderImpl();
        final Set<MetricPlugin> metricSet = new HashSet<>();
        c1 = createMock(MetricPlugin.class);
        expect(c1.getName()).andReturn("Size");
        expect(c1.getVersion()).andReturn("1.0.0");
        replay(c1);
        metricSet.add(c1);
        c2 = createMock(MetricPlugin.class);
        metricSet.add(c2);
        c3 = createMock(MetricPlugin.class);
        metricSet.add(c3);
        cp.setMetricPluginSet(metricSet);
        c4 = createMock(MetricPlugin.class);
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
    public void testFindByName() throws PluginNotFoundException {
        assertNotNull(provider.findByName("Size"));
    }

    @Test
    public void testFindByNameAndVersion() throws PluginNotFoundException {
        assertNotNull(provider.findByNameAndVersion("Size", "1.0.0"));
    }

    @Test
    public void testNotFound() throws PluginNotFoundException {
        exception.expect(PluginNotFoundException.class);
        provider.findByName("N/A");
    }

}
