package ${package}.provider;

import ${package}.${name};

/**
 * The Provider interface for your Plugins. Plugins are discovered by Apromore through a technology independant Provider interface.
 * This way we decouple the Plugins and their discovery mechanism from the used technology.
 *
 * @author <a href="mailto:${yourMail}">${yourName}</a>
 *
 */
public interface ${name}Provider {
    
	${name} findExamplePlugin(String name);
	
}