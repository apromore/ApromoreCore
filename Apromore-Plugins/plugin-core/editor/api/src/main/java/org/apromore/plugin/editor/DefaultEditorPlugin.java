package org.apromore.plugin.editor;

import org.apromore.manager.client.ManagerService;
import org.apromore.plugin.DefaultPlugin;
import org.apromore.plugin.editor.EditorPlugin;

import java.util.Locale;

/**
 * Default implementation for a Editor plug-in. Subclass this class to create a new Editor plug-in rather than implementing the interface directly.
 * Override all methods that you want to customize. At least you should provide the URI to the JS file of the editor plug-in.
 */
public class DefaultEditorPlugin extends DefaultPlugin implements EditorPlugin {

    @Override
    public String getLabel(Locale locale) {
        return "default";
    }

    @Override
    public String getJavaScriptURI() {
        return "";
    }

    @Override
    public String getJavaScriptPackage() {
        return "";
    }

}