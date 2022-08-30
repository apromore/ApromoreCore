/*-
 * #%L
 * This file is part of "Apromore Enterprise Edition".
 * %%
 * Copyright (C) 2019 - 2022 Apromore Pty Ltd. All Rights Reserved.
 * %%
 * NOTICE:  All information contained herein is, and remains the
 * property of Apromore Pty Ltd and its suppliers, if any.
 * The intellectual and technical concepts contained herein are
 * proprietary to Apromore Pty Ltd and its suppliers and may
 * be covered by U.S. and Foreign Patents, patents in process,
 * and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this
 * material is strictly forbidden unless prior written permission
 * is obtained from Apromore Pty Ltd.
 * #L%
 */

package org.apromore.commons.logging;

import lombok.experimental.UtilityClass;

/**
 * Constants used as keys within the {@link org.slf4j.MDC}.
 */
@UtilityClass
public class MDCKey {

    /** UUID-valued HTTP request identifier. */
    public static final String REQUEST = "apromore.request";

    /** User name for authenticated HTTP requests. */
    public static final String USER = "apromore.user";
}
