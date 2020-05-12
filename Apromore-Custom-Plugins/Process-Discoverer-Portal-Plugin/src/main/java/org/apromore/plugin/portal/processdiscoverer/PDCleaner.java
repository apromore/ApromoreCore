package org.apromore.plugin.portal.processdiscoverer;

import org.apromore.plugin.portal.processdiscoverer.vis.ProcessVisualizer;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.processdiscoverer.ProcessDiscoverer;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.util.SessionCleanup;

public class PDCleaner implements SessionCleanup {
    @Override
    public void cleanup(Session sess) throws Exception {
        System.out.println("PD Session cleanup starts.");
        ((ProcessDiscoverer)sess.getAttribute("processDiscoverer")).cleanUp();
        ((ProcessVisualizer)sess.getAttribute("processVisualizer")).cleanUp();
        UserSessionManager.removeEditSession(sess.getAttribute("userSessionId").toString());
        System.out.println("PD Session cleanup is done!");
    }
}
