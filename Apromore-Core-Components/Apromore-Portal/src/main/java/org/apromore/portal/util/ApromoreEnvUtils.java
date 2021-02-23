package org.apromore.portal.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public final class ApromoreEnvUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApromoreEnvUtils.class);

    public static String getEnvPropValue(final String envPropKey, final String errMsgIfNotFound) {
        if (envPropKey == null) {
            reportEnvKeyErrorState(errMsgIfNotFound);
        }
        String envPropValue = System.getenv(envPropKey);

        if (! StringUtils.hasText(envPropValue)) {
            reportEnvKeyErrorState(errMsgIfNotFound);
        }

        return envPropValue;
    }

    private static String reportEnvKeyErrorState(final String errMsgIfNotFound) {
        LOGGER.error("\n\n{}\n", errMsgIfNotFound);
        throw new IllegalStateException(errMsgIfNotFound);
    }

}
