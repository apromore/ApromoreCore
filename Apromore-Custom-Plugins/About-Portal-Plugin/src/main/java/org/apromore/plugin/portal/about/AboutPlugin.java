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

package org.apromore.plugin.portal.about;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.portal.ConfigBean;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

public class AboutPlugin extends DefaultPortalPlugin {

    private static final Logger LOGGER = PortalLoggerFactory.getLogger(AboutPlugin.class);

    private String label = "About Apromore"; // default label
    private String groupLabel = "About";
    private BundleContext bundleContext;
    private String commitId;
    private String buildDate;
    private String holder;
    private String detail;

    public AboutPlugin(final BundleContext newBundleContext,
                       final String newCommitId,
                       final String newBuildDate,
                       final String newHolder,
                       final String newDetail) {
        this.bundleContext = newBundleContext;
        this.commitId = newCommitId;
        this.buildDate = newBuildDate;
        this.holder = newHolder;
        this.detail = newDetail;
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
    public String getGroupLabel(final Locale locale) {
        return groupLabel;
    }

    @Override
    public String getIconPath() {
        return "/about-icon.svg";
    }

    @Override
    public void execute(final PortalContext portalContext) {
        LOGGER.info("Debug");

        try {
            ConfigBean config = (ConfigBean) SpringUtil.getBean("portalConfig");
            Map args = new HashMap();
            args.put("community", config.isCommunity());
            args.put("edition", config.getVersionEdition());
            args.put("holder", this.holder);
            args.put("detail", this.detail);
            args.put("version", config.getMajorVersionNumber() +
                " (commit " +
                    getCommitId() + " built on " + getBuildDate() + " / core: " +
                    config.getMinorVersionNumber() + " built on " + config.getVersionBuildDate() +
                ")"
            );
            final Window pluginWindow = (Window) Executions.getCurrent().createComponentsDirectly(
                new InputStreamReader(
                    bundleContext.getBundle().getResource("/about/zul/about.zul").openStream(),
                    "UTF-8"
                ),
                "zul",
                null,
                args
            );
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
