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

import java.net.URI;
import org.slf4j.Logger;
import org.zkoss.zk.ui.Executions;

public class PortalUrlWrapper {
    private static final Logger LOGGER = PortalLoggerFactory.getLogger(PortalUrlWrapper.class);

    private PortalUrlWrapper() {

    }

    public static String getUrlWithReference(String url) {
        try {
            URI uri = new URI(url);
            if (uri.getQuery() == null) {
                url += "?REFER_ID=" + Executions.getCurrent().getDesktop().getId();
            } else {
                url += "&REFER_ID=" + Executions.getCurrent().getDesktop().getId();
            }
        } catch (Exception ex) {
            LOGGER.error("Invalid URL", ex);
        }
        return url;
    }
}