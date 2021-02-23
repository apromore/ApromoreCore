package org.apromore.portal.util;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.rules.ExpectedException;

public class ApromoreEnvUtilsTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Rule
    public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

    @Test
    public void getEnvPropValue_envPropKeyFound_returnsEnvKeyPropValue() {
        environmentVariables.set("EXISTING_KEY_VALUE", "someKeyValue");

        final String envPropValue =
                ApromoreEnvUtils.getEnvPropValue("EXISTING_KEY_VALUE", "EXISTING_KEY_VALUE undefined");

        Assert.assertNotNull(envPropValue);
        Assert.assertEquals("someKeyValue", envPropValue);
    }

    @Test
    public void getEnvPropValue_envPropKeyNullNotFound_throwsIllegalStateException() {
        exceptionRule.expect(IllegalStateException.class);
        exceptionRule.expectMessage("Key could not be looked up from environment");

        ApromoreEnvUtils.getEnvPropValue(null,
                "Key could not be looked up from environment");
    }

    @Test
    public void getEnvPropValue_envPropKeyNonNullNotFound_throwsIllegalStateException() {
        exceptionRule.expect(IllegalStateException.class);
        exceptionRule.expectMessage("That key SOME_NON_EXISTING_ENV_KEY could not be found");

        ApromoreEnvUtils.getEnvPropValue("SOME_NON_EXISTING_ENV_KEY",
                "That key SOME_NON_EXISTING_ENV_KEY could not be found");
    }
}
