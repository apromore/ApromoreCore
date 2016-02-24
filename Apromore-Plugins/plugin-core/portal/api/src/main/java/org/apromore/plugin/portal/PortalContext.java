package org.apromore.plugin.portal;

import org.zkoss.zk.ui.Component;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;

/**
 * Communication interface that provides access to the Apromore portal
 */
public interface PortalContext {

    /**
     * @return a MessageHandler that can be used to display messages on the portal
     */
    MessageHandler getMessageHandler();

    /**
     * @return a PortalSelection object with information on the currently selected objects (process models) in the portal
     */
    PortalSelection getSelection();

    /**
     * @return a PortalUI object that MUST be used to generate new UI elements using the ZK library.
     */
    PortalUI getUI();

}