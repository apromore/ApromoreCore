/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2015 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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

package org.apromore.portal;

// Java 2 Standard Edition
import java.io.Serializable;

// Third party packages
import org.slf4j.LoggerFactory;

public class ConfigBean implements Serializable {

    private static final long serialVersionUID = 117L;
    private static final String COMMUNITY_TAG = "community";

    private String  siteEditor;
    private String  siteExternalHost;
    private int     siteExternalPort;
    private String  siteFilestore;
    private String  siteManager;
    private String  sitePortal;
    private String  majorVersionNumber;
    private String  minorVersionNumber;
    private String  versionEdition;
    private String  versionBuildDate;
    private boolean  enablePublish;
    private boolean  enableTC;
    private boolean  enableFullUserReg;

    public ConfigBean() {}

    public ConfigBean(String siteEditor, String siteExternalHost, int siteExternalPort, String siteFilestore, String siteManager, String sitePortal, String majorVersionNumber, String minorVersionNumber, String versionEdition, String versionBuildDate, boolean enablePublish, boolean enableTC, boolean enableFullUserReg) {

        LoggerFactory.getLogger(getClass()).info("Portal configured with:" +
            " site.editor=" + siteEditor +
            " site.externalHost=" + siteExternalHost +
            " site.externalPort=" + siteExternalPort +
            " site.filestore=" + siteFilestore +
            " site.manager=" + siteManager +
            " site.portal=" + sitePortal +
            " majorversion.number=" + majorVersionNumber +
            " minorversion.number=" + minorVersionNumber +
            " version.edition=" + versionEdition +
            " version.builddate=" + versionBuildDate);

        this.siteEditor         = siteEditor;
        this.siteExternalHost   = siteExternalHost;
        this.siteExternalPort   = siteExternalPort;
        this.siteFilestore      = siteFilestore;
        this.siteManager        = siteManager;
        this.sitePortal         = sitePortal;
        this.majorVersionNumber = majorVersionNumber;
        this.minorVersionNumber = minorVersionNumber;
        this.versionEdition     = versionEdition;
        this.versionBuildDate   = versionBuildDate;
        this.enablePublish      = enablePublish;
        this.enableTC           = enableTC;
        this.enableFullUserReg  = enableFullUserReg;
    }

    public String getSiteEditor()           { return siteEditor; }
    public String getSiteExternalHost()     { return siteExternalHost; }
    public int    getSiteExternalPort()     { return siteExternalPort; }
    public String getSiteFilestore()        { return siteFilestore; }
    public String getSiteManager()          { return siteManager; }
    public String getSitePortal()           { return sitePortal; }
    public String getMajorVersionNumber()   { return majorVersionNumber; }
    public String getMinorVersionNumber()   { return minorVersionNumber; }
    public String getVersionEdition()       { return versionEdition; }
    public String getVersionBuildDate()     { return versionBuildDate; }
    public boolean getEnablePublish()       { return enablePublish; }
    public boolean getEnableTC()            { return enableTC; }
    public boolean getEnableFullUserReg()   { return enableFullUserReg; }

    public boolean isCommunity() {
        return versionEdition.toLowerCase().contains(COMMUNITY_TAG);
    }
}
