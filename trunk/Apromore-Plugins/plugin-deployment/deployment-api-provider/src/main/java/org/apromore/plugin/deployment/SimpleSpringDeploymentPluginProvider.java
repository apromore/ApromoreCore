package org.apromore.plugin.deployment;

import java.util.ArrayList;
import java.util.List;

import org.apromore.plugin.deployment.DeploymentPlugin;
import org.apromore.plugin.provider.PluginProviderHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SimpleSpringDeploymentPluginProvider extends DeploymentPluginProviderImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleSpringDeploymentPluginProvider.class);

    public SimpleSpringDeploymentPluginProvider() {
        super();
        List<DeploymentPlugin> myDeploymentPluginList = new ArrayList<DeploymentPlugin>();
        Class<?>[] classes = PluginProviderHelper.getAllClassesImplementingInterfaceUsingSpring(DeploymentPlugin.class);
        for (int i = 0; i < classes.length; i++) {
            Class<?> myDeploymentPluginClass = classes[i];
            try {
                Object obj = myDeploymentPluginClass.newInstance();
                if (obj instanceof DeploymentPlugin) {
                    myDeploymentPluginList.add((DeploymentPlugin) obj);
                }
            } catch (InstantiationException | IllegalAccessException e) {
                LOGGER.warn("Could not instantiate DeploymentPlugin: "+myDeploymentPluginClass.getName());
            }
        }
        setInternalDeploymentPluginList(myDeploymentPluginList);
    }


}
