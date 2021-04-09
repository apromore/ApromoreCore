/*-
 * #%L
 * This file is part of "Apromore Core".
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
package org.apromore.portal.security.helper;

import org.apromore.portal.common.PortalSession;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueue;

public class PortalSessionQePair {

    private PortalSession portalSession;
    private EventQueue<Event> qe;

    public PortalSessionQePair(final PortalSession portalSession, final EventQueue<Event> qe) {
        this.portalSession = portalSession;
        this.qe = qe;
    }

    public PortalSession getPortalSession() {
        return portalSession;
    }

    public void setPortalSession(PortalSession portalSession) {
        this.portalSession = portalSession;
    }

    public EventQueue<Event> getQe() {
        return qe;
    }

    public void setQe(EventQueue<Event> qe) {
        this.qe = qe;
    }
}
