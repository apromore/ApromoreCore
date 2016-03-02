package org.apromore.plugin.portal;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;

import org.zkoss.zk.ui.Component;

import org.apromore.model.FolderType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.UserType;

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

    /**
     * Request to add a <var>process</var> to the table.
     *
     * @param process
     */
    void displayNewProcess(ProcessSummaryType process);

    /**
     * @return the current folder displayed by the portal screen
     */
    FolderType getCurrentFolder();

    /**
     * @return the authenticated user
     */
    UserType getCurrentUser();
}
