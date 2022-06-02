/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
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

package org.apromore.commons.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties
public class ConfigBean {

    private String numOfEvent;
    private String numOfTrace;
    // Fallback Storage path

    private static final long serialVersionUID = 117L;
    private static final String COMMUNITY_TAG = "community";

    private boolean sanitizationEnabled = false;
    private Site site = new Site();
    private Keycloak keycloak = new Keycloak();
    private Logs logs = new Logs();
    private Storage storage = new Storage();
    private Cache cache = new Cache();

    private Integer maxEventCount;

    private String volumeExportDir;
    private String volumeFileDir;
    private boolean templateEnabled;
    private boolean newdldash;

    @Data
    public class Logs {
	private String dir = "../Event-Logs-Repository";

    }

    @Data
    public class Cache {
	private String numOfEvent;
	private String numOfTrace;

    }

    @Data
    public class Storage {
	private String path = "FILE::../Event-Logs-Repository";

    }

    @Data
    public class Site {
	private String editor;
	private String logvisualizer;
	private String portal;
	private String contactEmail;
	private String aboutMeName;
    }

    // Switches to enable features
    private boolean enablePublish;
    private boolean enableTC;
    private boolean enablePP;
    private boolean enableUserReg;
    private boolean enableFullUserReg;
    private boolean enableSubscription;
    private boolean enableEtl;
    private boolean enablePpm;
    private boolean enableSimilaritySearch;
    private boolean enableUsersList;

    // Switch for custom calendar
    private boolean enableCalendar;

    // Switch for BPMN Diff
    private boolean bpmndiffEnable;

    // Switch for conformance checking
    private boolean enableConformanceCheck;

    // Switch for publish model
    private boolean enableModelPublish;

    // Switch for PD on new data layer
    private boolean enableNewPD;

    // Switch to Conformance Checker on new data layer
    private boolean enableNewConformanceCheck;

    private boolean enableLogAnimationNewDataLayer;

    // Maximum upload size
    private long maxUploadSize;

    // Email for issue reporting

    private Version version = new Version();

    @Data
    public class Version {
	private String number;
	private String edition;
    }

    @Data
    public class Keycloak {
	private boolean enabled;

    }

    public boolean isCommunity() {
	return version.getEdition().toLowerCase().contains(COMMUNITY_TAG);
    }

    public String getSiteEditor() {
	return site.getEditor();
    }

    public String getVersionNumber() {
	return version.getNumber();
    }

    public String getVersionEdition() {
	return version.getEdition();
    }

    public String getLogsDir() {
	return logs.getDir();
    }

    public String getStoragePath() {
	return storage.getPath();
    }

}
