package org.apromore.plugin.portal.processpublisher;

import org.apromore.commons.config.ConfigBean;
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zkoss.util.resource.Labels;

import java.util.Locale;

/**
 * The process publisher plugin is responsible for creating and revoking links to view models in view-only mode.
 *
 * @author Jane Hoh.
 */
@Component
public class ProcessPublisherPlugin extends DefaultPortalPlugin {

    @Autowired
    ConfigBean config;

    @Override
    public String getLabel(final Locale locale) {
        return Labels.getLabel("plugin_process_publish_text","Publish model");
    }

    @Override
    public Availability getAvailability() {
        return config.isEnableModelPublish() ? Availability.AVAILABLE : Availability.UNAVAILABLE;
    }
}
