package org.apromore.plugin.editor.ibpstruct;

import org.apromore.plugin.editor.DefaultEditorPlugin;
import org.springframework.stereotype.Component;

/**
 * Example for a Apromore Editor plug-in that provide two functionalities:
 *  - A custom servlet
 *  - A custom JavaScript file
 */
@Component("plugin")
public class IBPStructPlugin extends DefaultEditorPlugin {

    @Override
    public String getJavaScriptURI() {
        return "/ibpstruct/ibpstruct.js"; //TODO automatically get root dir via BundleContext
    }

    @Override
    public String getJavaScriptPackage() {
        return "ORYX.Plugins.IBPStruct";
    }

}
