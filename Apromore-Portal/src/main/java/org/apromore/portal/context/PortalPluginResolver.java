package org.apromore.portal.context;

import org.apromore.plugin.portal.PortalPlugin;
import org.zkoss.spring.SpringUtil;

import java.util.List;

/**
 * Looking up portal plug-ins
 */
public class PortalPluginResolver {

    public static List<PortalPlugin> resolve() {
        Object portalPlugins = SpringUtil.getBean("portalPlugins");
        if (portalPlugins != null) {
            return (List<PortalPlugin>) portalPlugins;
        } else {
            throw new RuntimeException("Could not get list of portal plug-ins!");
        }
    }

}
