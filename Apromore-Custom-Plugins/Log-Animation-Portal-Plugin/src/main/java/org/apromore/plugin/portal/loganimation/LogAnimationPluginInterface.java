package org.apromore.plugin.portal.loganimation;

import org.apromore.plugin.portal.PortalContext;
import org.deckfour.xes.model.XLog;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 27/10/17.
 */
public interface LogAnimationPluginInterface {

//    void execute(PortalContext portalContext, ProcessSummaryType processSummaryType, VersionSummaryType versionSummaryType, LogSummaryType logSummaryType);
//    void execute(PortalContext portalContext, String JSONData, String layout, LogSummaryType logSummaryType);
    void execute(PortalContext portalContext, String JSONData, String layout, XLog eventlog, boolean maintain_gateways, String logName);

}
