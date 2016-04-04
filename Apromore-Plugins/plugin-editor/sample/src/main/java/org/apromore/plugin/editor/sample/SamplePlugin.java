package org.apromore.plugin.editor.sample;

import org.apromore.plugin.editor.DefaultEditorPlugin;
import org.springframework.stereotype.Component;

/**
 * Example for a Apromore Editor plug-in that provide two functionalities:
 *  - A custom servlet
 *  - A custom JavaScript file
 */
@Component("plugin")
public class SamplePlugin extends DefaultEditorPlugin {

    @Override
    public String getJavaScriptURI() {
        return "/sample/sample.js"; //TODO automatically get root dir via BundleContext
    }

    @Override
    public String getJavaScriptPackage() {
        return "ORYX.Plugins.Sample";
    }

}
