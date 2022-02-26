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

package org.apromore.plugin.portal;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;

public final class PortalContextHolder {
	private static final Logger LOGGER = PortalLoggerFactory.getLogger(PortalContextHolder.class);

	public static PortalContext getActivePortalContext() {
		PortalContext portalContext=null;
		try {
			String referId=getReferId();
			Session current = Sessions.getCurrent();
			if (current!=null && referId!=null && current.getAttribute("portalContext_"+referId) != null) {
				portalContext=(PortalContext) current.getAttribute("portalContext_"+referId);
			}
		} catch (Exception ex) {
			LOGGER.error("Error in retrieving PortalContext", ex);
		}
		return portalContext;
	}
	/*
	 *	Using portal's reference id from the desktop scope, we will us it to retrieve the portalContext object from Session
	 */
	private static String getReferId() {
		Desktop desktop = Executions.getCurrent().getDesktop();
		String portalRef = null;
		if (desktop.getAttribute("PORTAL_REF_ID") != null) {
			portalRef = (String) desktop.getAttribute("PORTAL_REF_ID");
		}
		return portalRef;
	}

	public static void addNewPortalContextReference(String key, String referenceId) {
		try {
			Session current = Sessions.getCurrent();
			Map map = null;
			if (current.getAttribute("REF_PARENT") != null) {
				map = (Map<String, String>) current.getAttribute("REF_PARENT");
			} else {
				map = new HashMap<>();
			}
			map.put(key, referenceId);
			current.setAttribute("REF_PARENT", map);
		} catch (Exception ex) {
			LOGGER.error("Error in creating portalContext reference", ex);
		}
	}

	public static void removePortalContextReference(Desktop desktop) {
		try {
			Session session = Executions.getCurrent().getSession();
			if (session.getAttribute("portalContext_" + desktop.getId()) != null) {
				session.removeAttribute("portalContext_" + desktop.getId());
				LOGGER.info("Successfully removed from session with ID:{}",desktop.getId());
			}
		} catch (Exception ex) {
			LOGGER.error("Error in removing portalContext", ex);
		}
	}
}