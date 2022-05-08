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

package org.apromore.plugin.portal.about;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apromore.commons.config.ConfigBean;
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

@Component
public class AboutPlugin extends DefaultPortalPlugin {

    private static final Logger LOGGER = PortalLoggerFactory.getLogger(AboutPlugin.class);

    private String label = "About Apromore"; // default label

    private String commitId;
    private String buildDate;
    private String holder;
    private String detail;

    private ConfigBean config;

    public AboutPlugin(@Value("${git.commit.id.abbrev}") final String newCommitId,
            @Value("${git.commit.time}") final String newBuildDate,
            @Value("${site.aboutMeName}") final String newHolder, @Value("${newDetail:#{null}}") final String newDetail,
            ConfigBean configBean) {
		this.commitId = newCommitId;
		this.buildDate = newBuildDate;
		this.holder = newHolder;
		this.detail = newDetail;
		this.config = configBean;
    }

    public String getCommitId() {
	return this.commitId;
    }

    public String getBuildDate() {
	return this.buildDate;
    }

    // PortalPlugin overrides

    @Override
    public String getLabel(final Locale locale) {
	return Labels.getLabel("brand_about", label);
    }

    @Override
    public String getIconPath() {
	return "about-icon.svg";
    }

    @Override
    public void execute(final PortalContext portalContext) {
		LOGGER.info("Debug");

		try {

			Map args = new HashMap();
			args.put("community", config.isCommunity());
			args.put("edition", config.getVersionEdition());
			args.put("holder", this.holder);
			args.put("detail", this.detail);
			args.put("version", config.getVersionNumber() + " (commit "
					+ getCommitId() + " built on " + getBuildDate() + ")");
			final Window pluginWindow = (Window) Executions.getCurrent()
					.createComponentsDirectly(
							new InputStreamReader(
									getClass().getClassLoader().getResourceAsStream("about/zul/about.zul"), "UTF-8"),
							"zul", null, args);
			pluginWindow.setAttribute("version", "dummy");

			Button buttonOk = (Button) pluginWindow.getFellow("ok");
			buttonOk.addEventListener("onClick", new EventListener<Event>() {
			@Override
			public void onEvent(final Event event) throws Exception {
				pluginWindow.detach();
			}
			});
			pluginWindow.doModal();

		} catch (Exception e) {
			Messagebox.show(e.getMessage(), "Error", Messagebox.OK, Messagebox.ERROR);
			LOGGER.error("Unable to display About dialog", e);
		}
    }
}
