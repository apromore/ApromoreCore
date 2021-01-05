/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2013, 2015 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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

package org.apromore.portal.util;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

/**
 * Tests the Random Password Generator.
 */
public class RandomPasswordGeneratorTest {

    @Test(expected = IllegalArgumentException.class)
    public void testMinGreaterThanMax() throws Exception {
        int min = 3;
        int max = 2;
        RandomPasswordGenerator.generatePassword(min, max, 0, 0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMaxLessThanTotal() throws Exception {
        int max = 2;
        int caps = 2;
        int digits = 2;
        int special = 2;
        RandomPasswordGenerator.generatePassword(0, max, caps, digits, special);
    }

    @Test
    public void testPasswordBetweenMinAndMax() {
        int min = 3;
        int max = 8;
        char[] testPassword = RandomPasswordGenerator.generatePassword(min, max, 0, 0, 0);
        String strTestPassword = new String(testPassword);
        assertThat(strTestPassword.length(), lessThanOrEqualTo(max));
        assertThat(strTestPassword.length(), greaterThanOrEqualTo(min));

        min = 2;
        max = 2;
        testPassword = RandomPasswordGenerator.generatePassword(min, max, 0, 0, 0);
        strTestPassword = new String(testPassword);
        assertThat(strTestPassword.length(), lessThanOrEqualTo(max));
        assertThat(strTestPassword.length(), greaterThanOrEqualTo(min));
    }

}
