package org.apromore.plugin.portal;

import org.zkoss.zk.ui.Component;

import java.io.IOException;
import java.util.Map;

/**
 * Provides access to the UI of the portal. This class MUST be used by plug-ins to create UI elements to ensure proper class loading.
 * So far, it only allows to create generic ZK components.
 * In the future it may be extended with functionality that allows to mutate certain parts of the portal.
 */
public interface PortalUI {

    /**
     * Creates a ZK component using the provided ClassLoader to lookup the ZUL file specified as URI.
     *
     * @param bundleClassLoader provide the ClassLoader of a class within your plug-in bundle (e.g., plugin.getClass().getClassLoader())
     * @param uri the path to the ZUL file(e.g., test.zul for a file test.zul in the main/resources dir)
     * @param parent optional parent of the component, if NULL then it will be a top-level component
     * @param arguments optional arguments
     * @return the corresponding ZK component
     * @throws IOException in case loading the ZUL file failed
     */
    Component createComponent(ClassLoader bundleClassLoader, String uri, Component parent, Map<?, ?> arguments) throws IOException;

}
