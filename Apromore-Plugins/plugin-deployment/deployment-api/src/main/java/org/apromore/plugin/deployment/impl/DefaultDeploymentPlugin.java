package org.apromore.plugin.deployment.impl;

import org.apromore.plugin.impl.DefaultPropertyAwarePlugin;
import org.apromore.plugin.deployment.DeploymentPlugin;

/**
 * <p>
 * Default implementation of your new Plugin API. It extends from {@see DefaultPropertyAwarePlugin} which provides
 * the basic default implementation for handling Messages and Properties.
 *
 * <p>
 * Note that you don't have to inherit all these default implementations and interfaces. For a simple Plugin API
 * it may be sufficient to extend the {@see Plugin} interface.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt</a>
 *
 */
public class DefaultDeploymentPlugin extends DefaultPropertyAwarePlugin implements DeploymentPlugin {
    
	public void doSomething() {		
	}
	
}