package org.apromore.plugin.portal.sample;

import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.Level;
import org.apromore.plugin.portal.PortalContext;
import org.springframework.stereotype.Component;
import org.zkoss.zul.Window;

import java.io.IOException;
import java.util.Locale;

/**
 * An example Portal Plugin, which display an Hello World dialog
 */
@Component("plugin")
public class SamplePlugin extends DefaultPortalPlugin {

    @Override
    public String getLabel(Locale locale) {
        return "Example";
    }

    @Override
    public void execute(PortalContext context) {
        // Show a message on the portal
        context.getMessageHandler().displayInfo("Executed example plug-in!");
        try {
            // Create a window based on the ZUL file, which is controlled by SampleController
            // Please note that it is important to pass a ClassLoader of a class within the plug-in bundle!
            Window window = (Window) context.getUI().createComponent(getClass().getClassLoader(), "zul/sample.zul", null, null);
            window.doModal();
        } catch (IOException e) {
            context.getMessageHandler().displayError("Could not load component ", e);
        }
    }

}