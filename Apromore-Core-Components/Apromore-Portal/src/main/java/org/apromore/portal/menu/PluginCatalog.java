/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2011 Marie Christine.
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
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

package org.apromore.portal.menu;

public abstract class PluginCatalog {

    private PluginCatalog () {}

    public static final String ITEM_SEPARATOR = "SEPARATOR";
    public static final String GROUP_FILE = "FILE";
    public static final String GROUP_DISCOVER = "DISCOVER";
    public static final String GROUP_ANALYZE = "ANALYZE";
    public static final String GROUP_REDESIGN = "REDESIGN";

    public static final String PLUGIN_USER_ADMIN = "org.apromore.plugin.portal.useradmin.UserAdminPlugin";
    public static final String PLUGIN_ACCESS_CONTROL = "org.apromore.plugin.portal.accesscontrol.AccessControlPlugin";
    public static final String PLUGIN_ETL = "org.apromore.etlplugin.portal.etlPortal.ETLPluginPortal";
    public static final String PLUGIN_JOB_SCHEDULER = "org.apromore.etlplugin.portal.etlPortal.JobSchedulerPortalImp";
    public static final String PLUGIN_CALENDAR = "org.apromore.plugin.portal.calendar.CalendarPlugin";
    public static final String PLUGIN_PUBLISH_MODEL = "org.apromore.plugin.portal.processpublisher.ProcessPublisherPlugin";

    public static final String PLUGIN_UPLOAD = "org.apromore.plugin.portal.file.UploadFilePlugin";
    public static final String PLUGIN_DOWNLOAD = "org.apromore.plugin.portal.file.DownloadSelectionPlugin";
    public static final String PLUGIN_CUT = "CUT";
    public static final String PLUGIN_COPY = "COPY";
    public static final String PLUGIN_PASTE = "PASTE";
    public static final String PLUGIN_SHARE = "SHARE";
    public static final String PLUGIN_RENAME_MENU = "RENAME";
    public static final String PLUGIN_DELETE_MENU = "DELETE";
    public static final String PLUGIN_CREATE_FOLDER = "org.apromore.plugin.portal.file.CreateFolderPlugin";
    public static final String PLUGIN_RENAME = "org.apromore.plugin.portal.file.EditSelectionMetadataPlugin";
    public static final String PLUGIN_DELETE = "org.apromore.plugin.portal.file.DeleteSelectionPlugin";

    public static final String PLUGIN_CREATE_MODEL = "org.apromore.plugin.portal.file.CreateProcessPlugin";
    public static final String PLUGIN_DISCOVER_MODEL = "org.apromore.plugin.portal.processdiscoverer.plugins.PDFrequencyPlugin";
    public static final String PLUGIN_EDIT_MODEL = "org.apromore.plugin.portal.file.EditSelectionPlugin";
    public static final String PLUGIN_FILTER_LOG = "org.apromore.plugin.portal.logfilteree.LogFilterEEPlugin";

    public static final String PLUGIN_ANIMATE_LOG = "org.apromore.plugin.portal.loganimation2.LogAnimationPlugin";
    public static final String PLUGIN_COMPARE_MODELS = "org.apromore.plugin.portal.bpmndiff.BPMNDiffPlugin";
    public static final String PLUGIN_CHECK_CONFORMANCE = "org.apromore.plugin.portal.conformancechecking.ConformanceCheckingPlugin";
    public static final String PLUGIN_SIMULATE_MODEL = "org.apromore.plugin.portal.bimp.BIMPPortalPlugin";
    public static final String PLUGIN_DASHBOARD = "dashboard.portal.DashboardPlugin";

    public static final String PLUGIN_MERGE_MODELS = "org.apromore.plugin.merge.portal.MergePlugin";
    public static final String PLUGIN_SEARCH_MODELS = "org.apromore.plugin.similaritysearch.portal.SimilaritySearchPlugin";

    public static final String PLUGIN_ABOUT_EE = "org.apromore.plugin.portal.aboutee.AboutEEPlugin";
    public static final String PLUGIN_ABOUT = "org.apromore.plugin.portal.about.AboutPlugin";
    public static final String PLUGIN_CHANGE_PASSWORD = "org.apromore.plugin.portal.account.ChangePasswordPlugin";
    public static final String PLUGIN_REPORT_ISSUE = "org.apromore.plugin.portal.account.ReportIssuePlugin";
    public static final String PLUGIN_SIGN_OUT = "org.apromore.plugin.portal.account.SignOutPlugin";
    public static final String PLUGIN_DISCOVER_MODEL_SUB_MENU = "DISCOVER_MODEL_SUB_MENU";
    public static final String PLUGIN_DASHBOARD_SUB_MENU = "DASHBOARD_SUB_MENU";
    public static final String PLUGIN_LOG_FILTER_SUB_MENU = "LOG_FILTER_SUB_MENU";
    public static final String PLUGIN_APPLY_CALENDAR_SUB_MENU = "APPLY_CALENDAR_SUB_MENU";
    public static final String PLUGIN_CREATE_NEW_CALENDAR = "CREATE_NEW_CALENDAR";
    public static final String PLUGIN_EXISTING_CALENDAR = "EXISTING_CALENDAR";
    public static final String PLUGIN_CREATE_NEW_DASHBOARD = "CREATE_NEW_DASHBOARD";
    public static final String PLUGIN_VIEW_EXISTING_DASHBOARD = "EXISTING_DASHBOARD";
    public static final String PLUGIN_VIEW_FULL_LOG_DISCOVER_MODEL = "VIEW_FULL_LOG_DISCOVER_MODEL";
    public static final String PLUGIN_CREATE_NEW_LOG_FILTER = "CREATE_NEW_LOG_FILTER";
    public static final String PLUGIN_VIEW_FILTER_LOG_DISCOVER_MODEL = "VIEW_FILTER_LOG_DISCOVER_MODEL";
    public static final String PLUGIN_VIEW_EXISTING_LOG_FILTER = "EXISTING_LOG_FILTER";
}
