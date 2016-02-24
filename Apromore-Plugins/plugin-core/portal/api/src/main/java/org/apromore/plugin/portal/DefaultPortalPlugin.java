package org.apromore.plugin.portal;

import org.apromore.plugin.DefaultParameterAwarePlugin;

import java.util.Locale;

/**
 * Default implementation for a parameter-aware PortalPlugin. Subclass this class to create a new PortalPlugin rather than implementing the interface directly.
 * Override all methods that you want to customize. By default the plugin returns the label default and does nothing.
 */
public class DefaultPortalPlugin extends DefaultParameterAwarePlugin implements PortalPlugin {

    @Override
    public String getLabel(Locale locale) {
        return "default";
    }

    @Override
    public void execute(PortalContext context) {
    }

}