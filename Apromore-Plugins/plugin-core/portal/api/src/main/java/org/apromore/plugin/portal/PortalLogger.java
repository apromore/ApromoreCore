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
package org.apromore.plugin.portal;

import org.apromore.portal.model.UserType;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MDC;
import org.slf4j.ext.LoggerWrapper;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;

/**
 * Wrapper around a {@link Logger} which sets {@link MDC} attributes from the ZK {@link Session}
 * before every log message.
 */
class PortalLogger extends LoggerWrapper {

    PortalLogger(Logger wrappedLogger, String fqdn) {
        super(wrappedLogger, fqdn);
    }

    @Override public void debug(String msg)                              { debugMDC(); super.debug(msg); }
    @Override public void debug(String format, Object arg)               { debugMDC(); super.debug(format, arg); }
    @Override public void debug(String format, Object... args)           { debugMDC(); super.debug(format, args); }
    @Override public void debug(String format, Object arg1, Object arg2) { debugMDC(); super.debug(format, arg1, arg2); }
    @Override public void debug(String msg, Throwable t)                 { debugMDC(); super.debug(msg, t); }

    private void debugMDC() { if (isDebugEnabled()) { updateMDC(); } }

    @Override public void debug(Marker marker, String msg)                              { debugMDC(marker); super.debug(marker, msg); }
    @Override public void debug(Marker marker, String format, Object arg)               { debugMDC(marker); super.debug(marker, format, arg); }
    @Override public void debug(Marker marker, String format, Object... args)           { debugMDC(marker); super.debug(marker, format, args); }
    @Override public void debug(Marker marker, String format, Object arg1, Object arg2) { debugMDC(marker); super.debug(marker, format, arg1, arg2); }
    @Override public void debug(Marker marker, String msg, Throwable t)                 { debugMDC(marker); super.debug(marker, msg, t); }

    private void debugMDC(Marker marker) { if (isDebugEnabled(marker)) { updateMDC(); } }

    @Override public void error(String msg)                              { errorMDC(); super.error(msg); }
    @Override public void error(String format, Object arg)               { errorMDC(); super.error(format, arg); }
    @Override public void error(String format, Object... args)           { errorMDC(); super.error(format, args); }
    @Override public void error(String format, Object arg1, Object arg2) { errorMDC(); super.error(format, arg1, arg2); }
    @Override public void error(String msg, Throwable t)                 { errorMDC(); super.error(msg, t); }

    private void errorMDC() { if (isErrorEnabled()) { updateMDC(); } }

    @Override public void error(Marker marker, String msg)                              { errorMDC(marker); super.error(marker, msg); }
    @Override public void error(Marker marker, String format, Object arg)               { errorMDC(marker); super.error(marker, format, arg); }
    @Override public void error(Marker marker, String format, Object... args)           { errorMDC(marker); super.error(marker, format, args); }
    @Override public void error(Marker marker, String format, Object arg1, Object arg2) { errorMDC(marker); super.error(marker, format, arg1, arg2); }
    @Override public void error(Marker marker, String msg, Throwable t)                 { errorMDC(marker); super.error(marker, msg, t); }

    private void errorMDC(Marker marker) { if (isErrorEnabled(marker)) { updateMDC(); } }

    @Override public void info(String msg)                              { infoMDC(); super.info(msg); }
    @Override public void info(String format, Object arg)               { infoMDC(); super.info(format, arg); }
    @Override public void info(String format, Object... args)           { infoMDC(); super.info(format, args); }
    @Override public void info(String format, Object arg1, Object arg2) { infoMDC(); super.info(format, arg1, arg2); }
    @Override public void info(String msg, Throwable t)                 { infoMDC(); super.info(msg, t); }

    private void infoMDC() { if (isInfoEnabled()) { updateMDC(); } }

    @Override public void info(Marker marker, String msg)                              { infoMDC(marker); super.info(marker, msg); }
    @Override public void info(Marker marker, String format, Object arg)               { infoMDC(marker); super.info(marker, format, arg); }
    @Override public void info(Marker marker, String format, Object... args)           { infoMDC(marker); super.info(marker, format, args); }
    @Override public void info(Marker marker, String format, Object arg1, Object arg2) { infoMDC(marker); super.info(marker, format, arg1, arg2); }
    @Override public void info(Marker marker, String msg, Throwable t)                 { infoMDC(marker); super.info(marker, msg, t); }

    private void infoMDC(Marker marker) { if (isInfoEnabled(marker)) { updateMDC(); } }

    @Override public void trace(String msg)                              { traceMDC(); super.trace(msg); }
    @Override public void trace(String format, Object arg)               { traceMDC(); super.trace(format, arg); }
    @Override public void trace(String format, Object... args)           { traceMDC(); super.trace(format, args); }
    @Override public void trace(String format, Object arg1, Object arg2) { traceMDC(); super.trace(format, arg1, arg2); }
    @Override public void trace(String msg, Throwable t)                 { traceMDC(); super.trace(msg, t); }

    private void traceMDC() { if (isTraceEnabled()) { updateMDC(); } }

    @Override public void trace(Marker marker, String msg)                              { traceMDC(marker); super.trace(marker, msg); }
    @Override public void trace(Marker marker, String format, Object arg)               { traceMDC(marker); super.trace(marker, format, arg); }
    @Override public void trace(Marker marker, String format, Object... args)           { traceMDC(marker); super.trace(marker, format, args); }
    @Override public void trace(Marker marker, String format, Object arg1, Object arg2) { traceMDC(marker); super.trace(marker, format, arg1, arg2); }
    @Override public void trace(Marker marker, String msg, Throwable t)                 { traceMDC(marker); super.trace(marker, msg, t); }

    private void traceMDC(Marker marker) { if (isTraceEnabled(marker)) { updateMDC(); } }

    @Override public void warn(String msg)                              { warnMDC(); super.warn(msg); }
    @Override public void warn(String format, Object arg)               { warnMDC(); super.warn(format, arg); }
    @Override public void warn(String format, Object... args)           { warnMDC(); super.warn(format, args); }
    @Override public void warn(String format, Object arg1, Object arg2) { warnMDC(); super.warn(format, arg1, arg2); }
    @Override public void warn(String msg, Throwable t)                 { warnMDC(); super.warn(msg, t); }

    private void warnMDC() { if (isWarnEnabled()) { updateMDC(); } }

    @Override public void warn(Marker marker, String msg)                              { warnMDC(marker); super.warn(marker, msg); }
    @Override public void warn(Marker marker, String format, Object arg)               { warnMDC(marker); super.warn(marker, format, arg); }
    @Override public void warn(Marker marker, String format, Object... args)           { warnMDC(marker); super.warn(marker, format, args); }
    @Override public void warn(Marker marker, String format, Object arg1, Object arg2) { warnMDC(marker); super.warn(marker, format, arg1, arg2); }
    @Override public void warn(Marker marker, String msg, Throwable t)                 { warnMDC(marker); super.warn(marker, msg, t); }

    private void warnMDC(Marker marker) { if (isWarnEnabled(marker)) { updateMDC(); } }

    private void updateMDC() {
        Session session = Sessions.getCurrent();
        Object object = session == null ? null : session.getAttribute("USER");
        UserType user = object instanceof UserType ? (UserType) object : null;
        if (user == null) {
            MDC.remove(PortalLoggerFactory.MDC_APROMORE_USER_KEY);
        } else {
            MDC.put(PortalLoggerFactory.MDC_APROMORE_USER_KEY, user.getUsername());
        }
    }
}
