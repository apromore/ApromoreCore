/**
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
package org.apromore.plugin.portal.processpublisher;

import org.apromore.commons.config.ConfigBean;
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zkoss.util.resource.Labels;

import java.util.Locale;

/**
 * The process publisher plugin is responsible for creating and revoking links to view models in view-only mode.
 *
 * @author Jane Hoh.
 */
@Component
public class ProcessPublisherPlugin extends DefaultPortalPlugin {

    @Autowired
    ConfigBean config;

    @Override
    public String getLabel(final Locale locale) {
        return Labels.getLabel("plugin_process_publish_text","Publish model");
    }

    @Override
    public Availability getAvailability() {
        return config.isEnableModelPublish() ? Availability.AVAILABLE : Availability.UNAVAILABLE;
    }
}
