package org.apromore.plugin.deployment;

import org.apromore.plugin.MessageAwarePlugin;
import org.apromore.plugin.PropertyAwarePlugin;

/**
 * A Plugin API template extending {@see MessageAwarePlugin} and {@see PropertyAwarePlugin}. 
 * If you don't need these extended functionality you may consider to just extend from {@see Plugin}.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt</a>
 *
 */
public interface DeploymentPlugin extends MessageAwarePlugin, PropertyAwarePlugin {
    
	void doSomething();
	
}