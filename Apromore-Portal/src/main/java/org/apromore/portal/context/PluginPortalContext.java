package org.apromore.portal.context;

import org.apache.commons.io.IOUtils;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.plugin.portal.*;
import org.apromore.portal.dialogController.MainController;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of the PortalContext that is use by portal plug-ins to communicate with the portal.
 */
public class PluginPortalContext implements PortalContext {

    /**
     * Implementation of the PortalUI communication interface
     */
    private final static class PortalUIImpl implements PortalUI {

        @Override
        public Component createComponent(ClassLoader bundleClassLoader, String uri, Component parent, Map<?, ?> arguments) throws IOException {
            InputStream is = bundleClassLoader.getResourceAsStream(uri);
            return Executions.createComponentsDirectly(IOUtils.toString(is, "UTF-8"), "zul", parent, arguments);
        }

    }

    /**
     * Implementation of the MessageHandler communication interface
     */
    private class PortalMessageHandler implements MessageHandler {

        @Override
        public void displayInfo(String message) {
            displayMessage(org.apromore.plugin.portal.Level.INFO, message);
        }

        @Override
        public void displayError(String message, Exception exception) {
            displayMessage(Level.ERROR, message + " caused by " + exception.toString());
        }

        @Override
        public void displayMessage(org.apromore.plugin.portal.Level level, String message) {
            //TODO implement different colors for different message levels
            mainController.displayMessage(message);
        }

    }

    private final MainController mainController;
    private final MessageHandler messageHandler;
    private final PortalUI portalUI;

    /**
     * Create a new PluginPortalContext
     *
     * @param mainController
     */
    public PluginPortalContext(MainController mainController) {
        this.mainController = mainController;
        this.messageHandler = new PortalMessageHandler();
        this.portalUI = new PortalUIImpl();
    }

    @Override
    public MessageHandler getMessageHandler() {
        return new PortalMessageHandler();
    }

    @Override
    public PortalSelection getSelection() {
        return new PortalSelection() {
            @Override
            public Map<ProcessSummaryType, List<VersionSummaryType>> getSelectedProcessModelVersions() {
                return mainController.getSelectedProcessVersions();
            }

            @Override
            public Set<ProcessSummaryType> getSelectedProcessModels() {
                return mainController.getSelectedProcesses();
            }

        };
    }

    @Override
    public PortalUI getUI() {
        return new PortalUIImpl();
    }

}
