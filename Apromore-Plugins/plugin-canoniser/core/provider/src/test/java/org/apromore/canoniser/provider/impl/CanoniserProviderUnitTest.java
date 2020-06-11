/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 Felix Mannhardt.
 * Copyright (C) 2013 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
 * %%
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
package org.apromore.canoniser.provider.impl;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.apromore.canoniser.Canoniser;
import org.apromore.plugin.exception.PluginNotFoundException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Tests for the Canoniser Provider
 *
 * @author <a href="felix.mannhardt@smail.wir.h-brs.de"><a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a></a>
 *
 */
public class CanoniserProviderUnitTest {

	private CanoniserProviderImpl provider;

	private Canoniser c1;
	private Canoniser c2;
	private Canoniser c3;
	private Canoniser c4;

	@Before
	public void setUp() throws Exception {
		final CanoniserProviderImpl cp = new CanoniserProviderImpl();
		final Set<Canoniser> canoniserSet = new HashSet<Canoniser>();
		c1 = createMock(Canoniser.class);
		expect(c1.getName()).andReturn("XPDL 2.0 - Canoniser XY");
		expect(c1.getVersion()).andReturn("1.0.0");
		expect(c1.getNativeType()).andReturn("XPDL 2.0");
		replay(c1);
		canoniserSet.add(c1);
		c2 = createMock(Canoniser.class);
		canoniserSet.add(c2);
		c3 = createMock(Canoniser.class);
		canoniserSet.add(c3);
		cp.setCanoniserSet(canoniserSet);
		c4 = createMock(Canoniser.class);
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
		assertNotNull(provider.findByNativeType("XPDL 2.0"));
	}

	@Test
	public void testListByNativeTypeAndName() throws PluginNotFoundException {
		assertNotNull(provider.findByNativeTypeAndName("XPDL 2.0", "XPDL 2.0 - Canoniser XY"));
	}

	@Test
	public void testListByNativeTypeAndNameAndVersion() throws PluginNotFoundException {
		assertNotNull(provider.findByNativeTypeAndNameAndVersion("XPDL 2.0", "XPDL 2.0 - Canoniser XY", "1.0.0"));
	}

	@Test
	public void testNotFound() throws PluginNotFoundException {
		exception.expect(PluginNotFoundException.class);
		provider.findByNativeType("N/A");
	}

}
