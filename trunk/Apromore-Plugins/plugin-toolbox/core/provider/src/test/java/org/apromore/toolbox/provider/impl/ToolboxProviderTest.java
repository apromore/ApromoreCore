/**
 *  Copyright 2012, Felix Mannhardt
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
package org.apromore.toolbox.provider.impl;

import java.util.HashSet;
import java.util.Set;

import org.apromore.plugin.exception.PluginNotFoundException;
import org.apromore.toolbox.Toolbox;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static junit.framework.Assert.assertEquals;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the Canoniser Provider
 *
 * @author <a href="felix.mannhardt@smail.wir.h-brs.de"><a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a></a>
 */
public class ToolboxProviderTest {

    private ToolboxProviderImpl provider;

    private Toolbox c1;
    private Toolbox c2;
    private Toolbox c3;
    private Toolbox c4;

    @Before
    public void setUp() throws Exception {
        final ToolboxProviderImpl cp = new ToolboxProviderImpl();
        final Set<Toolbox> toolboxSet = new HashSet<Toolbox>();
        c1 = createMock(Toolbox.class);
        expect(c1.getName()).andReturn("Hungarian Similarity Search");
        expect(c1.getVersion()).andReturn("1.0.0");
        expect(c1.getToolName()).andReturn("Hungarian Search");
        replay(c1);
        toolboxSet.add(c1);
        c2 = createMock(Toolbox.class);
        toolboxSet.add(c2);
        c3 = createMock(Toolbox.class);
        toolboxSet.add(c3);
        cp.setToolboxSet(toolboxSet);
        c4 = createMock(Toolbox.class);
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
    public void testListByNativeType() throws PluginNotFoundException {
        assertNotNull(provider.findByToolName("Hungarian Search"));
    }

    @Test
    public void testListByNativeTypeAndName() throws PluginNotFoundException {
        assertNotNull(provider.findByToolNameAndName("Hungarian Search", "Hungarian Similarity Search"));
    }

    @Test
    public void testListByNativeTypeAndNameAndVersion() throws PluginNotFoundException {
        assertNotNull(provider.findByToolNameAndNameAndVersion("Hungarian Search", "Hungarian Similarity Search", "1.0.0"));
    }

    @Test
    public void testNotFound() throws PluginNotFoundException {
        exception.expect(PluginNotFoundException.class);
        provider.findByToolName("N/A");
    }

}
