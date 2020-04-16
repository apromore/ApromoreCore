/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
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

package org.apromore.canoniser.bpmn;

// Third party packages
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 * Test suite for {@link IdFactory}.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 * @since 0.4
 */
public class IdFactoryUnitTest {

    /**
     * Test {@link IdFactory#NCNAME} regular expression.
     *
     * @see <a href="http://www.w3.org/TR/xml-names11/#NT-NCName">Namespaces in XML 1.1</a>
     */ 
    @Test public final void testNCNAME() {

        // Valid NCNames
        assert IdFactory.NCNAME.matcher("_").matches();
        assert IdFactory.NCNAME.matcher("a").matches();
        assert IdFactory.NCNAME.matcher("aa").matches();
        assert IdFactory.NCNAME.matcher("a1").matches();
        assert IdFactory.NCNAME.matcher("a_").matches();
        assert IdFactory.NCNAME.matcher("a-").matches();
        assert IdFactory.NCNAME.matcher("a.b").matches();

        // Non-NCNames
        assert !IdFactory.NCNAME.matcher("").matches();
        assert !IdFactory.NCNAME.matcher(" ").matches();
        assert !IdFactory.NCNAME.matcher("1").matches();
        assert !IdFactory.NCNAME.matcher("-").matches();
        assert !IdFactory.NCNAME.matcher(".").matches();
        assert !IdFactory.NCNAME.matcher("a ").matches();
        assert !IdFactory.NCNAME.matcher("a/b").matches();
    }

    /**
     * Test {@link IdFactory#newId} method.
     */
    @Test
    public final void testNewId() {

        // Obtain test instance
        IdFactory idFactory = new IdFactory();

        // If we ask for an identifier that isn't already present, we should get it
        assertEquals("a", idFactory.newId("a"));
        assertEquals("b", idFactory.newId("b"));

        // If we ask for an identifier that is already present, we ought not to get it
        String aAgain = idFactory.newId("a");
        assertNotNull(aAgain);
        assertFalse("a".equals(aAgain));
        assertFalse("b".equals(aAgain));

        String bAgain = idFactory.newId("b");
        assertNotNull(bAgain);
        assertFalse("a".equals(bAgain));
        assertFalse("b".equals(bAgain));
        assertFalse(aAgain.equals(bAgain));
    }

    /**
     * Test {@link IdFactory#newId} method when <code>null</code> is passed to it.
     */
    @Test
    public final void testNewIdWithNullArgument() {

        // Obtain test instance
        IdFactory idFactory = new IdFactory();

        // Passing null still generates a unique identifier each time
        String nullId = idFactory.newId(null);
        assertNotNull(nullId);
        assertFalse(nullId.equals(idFactory.newId(null)));
    }
}
