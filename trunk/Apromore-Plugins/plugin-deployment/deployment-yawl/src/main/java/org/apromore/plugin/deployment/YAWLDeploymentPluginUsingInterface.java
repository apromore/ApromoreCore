package org.apromore.plugin.deployment;

import java.util.List;
import java.util.Set;

import org.apromore.plugin.message.PluginMessage;
import org.apromore.plugin.property.PropertyType;


/**
 * <p>
 * Implementation of your new Plugin API. This class just implements the plain Interface without extending the abstract default class.
 * Depending on your needs you should have a look at the default implementation.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt</a>
 *
 */
public class YAWLDeploymentPluginUsingInterface implements DeploymentPlugin {

	@Override
    public void doSomething() {
		//TODO Implement
	}

    @Override
    public List<PluginMessage> getPluginMessages() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getVersion() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getAuthor() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<PropertyType> getAvailableProperties() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<PropertyType> getMandatoryProperties() {
        // TODO Auto-generated method stub
        return null;
    }

}