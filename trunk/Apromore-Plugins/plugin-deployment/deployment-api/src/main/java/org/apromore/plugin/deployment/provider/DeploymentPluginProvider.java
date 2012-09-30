package org.apromore.plugin.deployment.provider;

import org.apromore.plugin.deployment.DeploymentPlugin;

/**
 * The Provider interface for your Plugins. 
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt</a>
 *
 */
public interface DeploymentPluginProvider {
    
	DeploymentPlugin findExamplePlugin(String name);
	
}