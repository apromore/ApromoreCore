package org.apromore.canoniser.yawl;

import java.io.OutputStream;

import org.apromore.plugin.impl.DefaultPluginResult;

public class YAWLCanoniserResult extends DefaultPluginResult {

    private OutputStream yawlOrgData;

    public OutputStream getYawlOrgData() {
        return yawlOrgData;
    }

    public void setYawlOrgData(OutputStream yawlOrgData) {
        this.yawlOrgData = yawlOrgData;
    }
    
}
