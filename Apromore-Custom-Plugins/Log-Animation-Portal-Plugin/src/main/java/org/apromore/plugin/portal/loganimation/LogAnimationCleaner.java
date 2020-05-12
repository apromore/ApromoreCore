package org.apromore.plugin.portal.loganimation;

import org.apromore.portal.common.UserSessionManager;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.util.DesktopCleanup;

/**
 * This class is registered with ZK as a DesktopCleanup and will be called when the session timeouts.
 * It is in charged of cleaning up memory resources used by this plugin.
 * Note that ZK is assumed to automatically destroy all ZK-specific objects (e.g. session, desktop, execution, etc.)
 * 
 * @author Bruce Nguyen
 *
 */
public class LogAnimationCleaner implements DesktopCleanup {
    @Override
    public void cleanup(Desktop desktop) throws Exception {
        System.out.println("LogAnimation cleanup starts for desktopID = " + desktop.getId());
        
        // Clean up this plugin
        // Log animation logic has no tricky resources to be cleaned up, auto cleaned by the GC

        // Clean up the Portal session as it doesn't control plugin session
        UserSessionManager.removeEditSession(desktop.getAttribute("pluginSessionId").toString());
        
        System.out.println("LogAnimation cleanup is done for desktopID = " + desktop.getId());
    }
}
