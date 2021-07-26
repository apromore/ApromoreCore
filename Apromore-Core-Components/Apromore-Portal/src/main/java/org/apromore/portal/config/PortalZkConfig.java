/**
 * #%L This file is part of "Apromore Enterprise Edition". %% Copyright (C) 2019 - 2021 Apromore Pty
 * Ltd. All Rights Reserved. %% NOTICE: All information contained herein is, and remains the
 * property of Apromore Pty Ltd and its suppliers, if any. The intellectual and technical concepts
 * contained herein are proprietary to Apromore Pty Ltd and its suppliers and may be covered by U.S.
 * and Foreign Patents, patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material is strictly forbidden unless
 * prior written permission is obtained from Apromore Pty Ltd. #L%
 */
package org.apromore.portal.config;

import javax.annotation.PostConstruct;
import org.apromore.portal.LoggingZKListener;
import org.apromore.zk.ApromoreDesktopInit;
import org.springframework.context.annotation.Configuration;
import org.zkoss.lang.Library;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WebApps;
import org.zkoss.zk.ui.util.Statistic;
import org.zkoss.zk.ui.util.URIInfo;

@Configuration
public class PortalZkConfig {

  @PostConstruct
  public void initZk() {
    WebApps.getCurrent().setAppName("Apromore");
    WebApps.getCurrent().getConfiguration().setDebugJS(true);



    Library.setProperty("org.zkoss.theme.preferred", "chiaroscuro_c");
    // Library.setProperty("org.zkoss.theme.preferred", "sapphire");
    Library.setProperty("org.zkoss.theme.atlantic.useGoogleFont.disabled", "true");
    Library.setProperty("org.zkoss.zk.ui.WebApp.name", "Apromore");
    Library.setProperty("org.zkoss.zul.grid.rod", "true");
    Library.setProperty("org.zkoss.zul.listbox.rod", "true");
    Library.setProperty("org.zkoss.web.classWebResource.cache", "false");
    Library.setProperty("org.zkoss.zul.progressbox.position", "center");
    Library.setProperty("org.zkoss.util.label.web.charset", "UTF-8");
    // Library.setProperty("org.zkoss.util.label.web.location", "./zk-label.properties");
    Labels.register(new LabelLocatorService(WebApps.getCurrent().getServletContext(), "zk-label"));
    Labels.register(new LabelLocatorService(WebApps.getCurrent().getServletContext(), "brand"));
    Labels.register(new LabelLocatorService(WebApps.getCurrent().getServletContext(), "ui"));

    WebApps.getCurrent().getConfiguration().setTimeoutURI("ajax", "/", URIInfo.SEND_REDIRECT);
    try {
      WebApps.getCurrent().getConfiguration().addListener(Statistic.class);
      WebApps.getCurrent().getConfiguration().addListener(ApromoreDesktopInit.class);
      WebApps.getCurrent().getConfiguration().addListener(LoggingZKListener.class);
      WebApps.getCurrent().getConfiguration().addListener(LoggingZKListener.class);


    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    WebApps.getCurrent().getConfiguration().setClientErrorReload("ajaz", 301, "/", null);
    WebApps.getCurrent().getConfiguration().setClientErrorReload("ajaz", 410, "/", "server-push");
    WebApps.getCurrent().getConfiguration().addErrorPage("ajaz", Throwable.class, "/pages/401.zul");


  }



}
