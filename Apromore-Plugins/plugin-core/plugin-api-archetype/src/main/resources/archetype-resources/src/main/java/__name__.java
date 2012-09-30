package ${package};

import org.apromore.plugin.MessageAwarePlugin;
import org.apromore.plugin.PropertyAwarePlugin;

/**
 * A Plugin API template extending {@see MessageAwarePlugin} and {@see PropertyAwarePlugin}. 
 * If you don't need these extended functionality you may consider to just extend from {@see Plugin}.
 *
 * @author <a href="mailto:${yourMail}">${yourName}</a>
 *
 */
public interface ${name} extends MessageAwarePlugin, PropertyAwarePlugin {
    
	void doSomething();
	
}