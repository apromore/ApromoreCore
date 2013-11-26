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
