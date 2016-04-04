package org.apromore.portal.context;

import org.apromore.plugin.editor.EditorPlugin;
import org.apromore.plugin.portal.PortalPlugin;
import org.zkoss.spring.SpringUtil;

import java.util.List;

/**
 * Looking up editor plug-ins
 */
public class EditorPluginResolver {

    public static List<EditorPlugin> resolve() {
        Object editorPlugins = SpringUtil.getBean("editorPlugins");
        if (editorPlugins != null) {
            return (List<EditorPlugin>) editorPlugins;
        } else {
            throw new RuntimeException("Could not get list of editor plug-ins!");
        }
    }

}
