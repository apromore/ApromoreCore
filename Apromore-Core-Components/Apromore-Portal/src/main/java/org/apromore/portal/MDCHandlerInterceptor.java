/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
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
package org.apromore.portal;

import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apromore.commons.logging.MDCKey;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Tag HTTP requests with a UUID via the MDC.
 */
@Slf4j
public class MDCHandlerInterceptor implements HandlerInterceptor {

    // Spring HandlerInterceptor

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        MDC.put(MDCKey.REQUEST, UUID.randomUUID().toString());
        log.debug("Spring HTTP request start: {}", MDC.get(MDCKey.REQUEST));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        log.debug("Spring HTTP request end: {}", MDC.get(MDCKey.REQUEST));
        MDC.remove(MDCKey.REQUEST);
    }
}
