package org.apromore.canoniser.yawl;

import java.io.OutputStream;

import org.apromore.plugin.impl.PluginResultImpl;

public class YAWLCanoniserResult extends PluginResultImpl {

    private OutputStream yawlOrgData;

    public OutputStream getYawlOrgData() {
        return yawlOrgData;
    }

    public void setYawlOrgData(OutputStream yawlOrgData) {
        this.yawlOrgData = yawlOrgData;
    }
    
}
