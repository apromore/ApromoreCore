/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
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
  private String logsDir = "../Event-Logs-Repository";
  private String numOfEvent;
  private String numOfTrace;
  // Fallback Storage path
  private String storagePath = "FILE::../Event-Logs-Repository";

  private static final long serialVersionUID = 117L;
  private static final String COMMUNITY_TAG = "community";

  private boolean sanitizationEnabled = false;
  private Site site = new Site();

  @Data
  public class Site {
    private String editor;
    private String externalHost;
    private int externalPort;
    private String filestore;
    private String fullProtocolHostPortUrl;
    private String host;
    private String logvisualizer;
    private int port;
    private String pql;
    private String manager;
    private String portal;
    private boolean useKeycloakSso;
    private SecurityMs securityMs;

  }

  @Data
  public class SecurityMs {
    private String host;
    private String port;
    private String httpLogoutUrl;
    private String httpsLogoutUrl;
  }

  // LDAP

  private Ldap ldap = new Ldap();

  @Data
  public class Ldap {
    private String providerURL;
    private String userContext;
    private String usernameAttribute;
    private String emailAttribute;
    private String firstNameAttribute;
    private String lastNameAttribute;
  }


  // Switches to enable features
  private boolean enablePublish;
  private boolean enableTC;
  private boolean enablePP;
  private boolean enableUserReg;
  private boolean enableFullUserReg;
  private boolean enableSubscription;
  private boolean enableEtl;

  // Switch for custom calendar
  private boolean enableCalendar;


  // Maximum upload size
  private long maxUploadSize;

  private boolean useKeycloakSso;

  // Email for issue reporting
  private String contactEmail;


  private Version version = new Version();

  @Data
  public class Version {
    private String number;
    private String edition;
  }


  public boolean isCommunity() {
    return version.getEdition().toLowerCase().contains(COMMUNITY_TAG);
  }

  public String getLdapUserContext() {
    return ldap.getUserContext();
  }

  public String getLdapLastNameAttribute() {
    return ldap.getLastNameAttribute();
  }

  public String getLdapUsernameAttribute() {
    return ldap.getUsernameAttribute();
  }

  public String getLdapFirstNameAttribute() {
    return ldap.getFirstNameAttribute();
  }

  public String getLdapEmailAttribute() {
    return ldap.getEmailAttribute();
  }

  public String getLdapProviderURL() {
    return ldap.getProviderURL();
  }

  public String getSecurityMsHttpLogoutUrl() {
    return site.getSecurityMs().getHttpLogoutUrl();
  }

  public String getSecurityMsHttpsLogoutUrl() {
    return site.getSecurityMs().getHttpsLogoutUrl();
  }

  public String getSiteEditor() {
    return site.getEditor();
  }

  public String getSiteExternalHost() {
    return site.getExternalHost();
  }

  public int getSiteExternalPort() {
    return site.getExternalPort();
  }

  public String getMajorVersionNumber() {
    return version.getNumber().split("\\.")[0];
  }


  public String getMinorVersionNumber() {
    return version.getNumber().split("\\.")[1];
  }


  public String getVersionEdition() {
    return version.getEdition();
  }


}
