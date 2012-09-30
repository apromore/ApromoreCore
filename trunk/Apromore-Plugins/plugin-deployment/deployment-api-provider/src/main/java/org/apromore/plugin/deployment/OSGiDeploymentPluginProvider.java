package org.apromore.plugin.deployment;

import java.util.List;

import org.apromore.plugin.deployment.DeploymentPlugin;
import org.springframework.stereotype.Service;

@Service("osgiDeploymentPluginProviderImpl")
public class OSGiDeploymentPluginProvider extends DeploymentPluginProviderImpl {

    public List<DeploymentPlugin> getDeploymentPluginList() {
        return getInternalDeploymentPluginList();
    }

    public void setDeploymentPluginList(final List<DeploymentPlugin> newDeploymentPluginList) {
        setInternalDeploymentPluginList(newDeploymentPluginList);
    }

}
