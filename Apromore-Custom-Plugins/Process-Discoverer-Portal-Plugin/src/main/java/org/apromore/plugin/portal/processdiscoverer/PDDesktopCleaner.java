package org.apromore.plugin.portal.processdiscoverer;

import org.apromore.plugin.portal.processdiscoverer.vis.ProcessVisualizer;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.processdiscoverer.ProcessDiscoverer;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.util.DesktopCleanup;

/**
 * This class is registered with ZK as a DesktopCleanup and will be called when the desktop timeouts.
 * It is in charged of cleaning up memory resources used by this plugin.
 * Note that ZK is assumed to automatically destroy all ZK-specific objects (e.g. session, desktop, execution, etc.)
 * 
 * @author Bruce Nguyen
 *
 */
public class PDDesktopCleaner implements DesktopCleanup {
    @Override
    public void cleanup(Desktop desktop) throws Exception {
        System.out.println("PD cleanup starts for desktopID = " + desktop.getId());
        
        // Clean up this plugin
        ((ProcessDiscoverer)desktop.getAttribute("processDiscoverer")).cleanUp();
        ((ProcessVisualizer)desktop.getAttribute("processVisualizer")).cleanUp();

        // Clean up the Portal session as it doesn't control plugin session
        UserSessionManager.removeEditSession(desktop.getAttribute("pluginSessionId").toString());

        System.out.println("PD cleanup is done for desktopID = " + desktop.getId());
    }
}
