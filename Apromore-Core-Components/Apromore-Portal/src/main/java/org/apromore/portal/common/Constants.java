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

package org.apromore.portal.common;

public abstract class Constants {

    // max number of searches kept for users
    public static final int maxSearches = 10;

    public static final String EVENT_QUEUE_REFRESH_SCREEN = "UI_UPDATES";
    public static final String EVENT_QUEUE_SESSION_ATTRIBUTES = "SESSION_ATTRIBUTES";
    public static final String EVENT_MESSAGE_SAVE = "SaveEvent";
    public static final String EVENT_QUEUE_BPMN_EDITOR = "BPMN EDITOR";

    // colors and style used in the table view
    // #E5E5E5 light gray
    // #ACC6E4 blue

    public static final String TOOLBARBUTTON_STYLE = "font-size:12px";
    public static final String FONT_BOLD = "font-weight:bold";
    public static final String FOLDER = "background-color:#FFFFEE";
    public static final String UNSELECTED_VERSION = "background-color:#E5E5E5" + ";" + TOOLBARBUTTON_STYLE;
    public static final String SELECTED_PROCESS = "background-color:#ACC6E4" + ";" + TOOLBARBUTTON_STYLE;

    public static final String NO_ANNOTATIONS = "-- no annotations --";
    public static final String INITIAL_ANNOTATION = "Original";
    public static final String ANNOTATIONS = "Annotations";
    public static final String CANONICAL = "Canonical";

    public static final String RELEASE_NOTES = "https://apromore.org/platform/release-notes";
    public static final String MORE_INFO = "https://apromore.org/";
    public static final String FEATURES_INFO = "https://apromore.org/documentation/features";
    public static final String WEB_DAV = "http://apromore.qut.edu.au/filestore/dav";
    public static final String DEVELOPER_RESOURCES = "http://apromore-build.cis.unimelb.edu.au/";

    // public static final String FOLDER_ICON = "/img/icon/svg/folder_icons.svg";
    public static final String FOLDER_ICON = "/themes/ap/common/img/icons-2/folder-solid.svg";
    public static final String PROCESS_ICON = "/img/icon/svg/bpmn_model.svg";
    public static final String CLUSTER_ICON = "/img/icon/cluster-22x22.png";
    public static final String LOG_ICON = "/img/icon/svg/log_icon.svg";

    // Icons displayed in the "Ranking" column of the process summary list
    public static final String STAR_FULL_ICON = "/img/selectAll-12.png";
    public static final String STAR_BLK_ICON = "/img/unselectAll-12.png";
    public static final String STAR_MID_ICON = "/img/revertSelection-12.png";
    public static final String ANNOTATIONS_ONLY = "notationsOnly";

    // Icons displayed in the "Queryable?" column of the process summary list
    public static final String PQL_UNINDEXED_ICON = "/img/add.png";
    public static final String PQL_INDEXING_ICON = "/img/arrow_refresh.png";
    public static final String PQL_INDEXED_ICON = "/img/select.png";
    public static final String PQL_CANNOTINDEX_ICON = "/img/cross.png";
    public static final String PQL_ERROR_ICON = "/img/alert.png";

    public static final String INITIAL_VERSION = "1.0";
    public static final String dateFormat = "yyyy/MM/dd hh:mm a";

    public static final String ICOCLS_PREFIX = "ap-ico-";
    public static final String FOLDER_ICOCLS = ICOCLS_PREFIX + "folder";;
    public static final String PROCESS_ICOCLS = ICOCLS_PREFIX + "process";
    public static final String LOG_ICOCLS = ICOCLS_PREFIX + "log";

    public static final String STAR_FULL_CLS = "ap-star-full";
    public static final String STAR_HALF_CLS = "ap-star-half";
    public static final String STAR_NONE_CLS = "ap-star-none";

    public static final String MANAGER_SERVICE = "managerClient";
    public static final String EVENT_LOG_SERVICE = "eventLogService";
    public static final String SECURITY_SERVICE = "securityService";
    public static final String AUTH_SERVICE = "authorizationService";
    public static final String WORKSPACE_SERVICE = "workspaceService";

    public static final String USER_ADMIN_PLUGIN = "USER_ADMIN_PLUGIN";
    public static final String ACCESS_CONTROL_PLUGIN = "ACCESS_CONTROL_PLUGIN";

}
