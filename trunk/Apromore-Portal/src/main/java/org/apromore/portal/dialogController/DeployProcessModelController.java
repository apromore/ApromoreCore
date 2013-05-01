package org.apromore.portal.dialogController;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apromore.model.PluginInfo;
import org.apromore.model.PluginMessages;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.plugin.property.ParameterType;
import org.apromore.plugin.property.RequestParameterType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 * Deploy Process Window
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt</a>
 *
 */
public class DeployProcessModelController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeployProcessModelController.class);

    private static final long serialVersionUID = 7136271898811065214L;

    private final Button okButton;
    private final Button cancelButton;
    private final Textbox nativeTypeBox;
    private final Grid propertiesGrid;
    private final Window deployProcessW;

    private final PluginPropertiesHelper propertiesHelper;

    private final Entry<ProcessSummaryType, List<VersionSummaryType>> selectedProcess;
    private String selectedDeploymentPLugin;

    private final MainController mainController;

    public DeployProcessModelController(final MainController mainController, final Entry<ProcessSummaryType, List<VersionSummaryType>> process) throws InterruptedException {
        this.mainController = mainController;
        this.selectedProcess = process;

        deployProcessW = (Window) Executions.createComponents("macros/deployProcess.zul", null, null);

        okButton = (Button) deployProcessW.getFellow("deployProcessButton");
        cancelButton = (Button) deployProcessW.getFellow("cancelButton");

        nativeTypeBox = (Textbox) deployProcessW.getFellow("nativeTypeBox");
        nativeTypeBox.setValue(selectedProcess.getKey().getOriginalNativeType());

        propertiesGrid = (Grid) deployProcessW.getFellow("deploymentPropertiesGrid");
        propertiesHelper = new PluginPropertiesHelper(getService(), propertiesGrid);

        readDeploymentPluginInfos(nativeTypeBox.getValue());

        okButton.addEventListener("onClick", new EventListener() {

            @Override
            public void onEvent(final Event event) throws InterruptedException {
                deployProcess(event);
            }
        });

       cancelButton.addEventListener("onClick", new EventListener() {

            @Override
            public void onEvent(final Event event) throws Exception {
                closeWindow();
            }
        });

        try {
            deployProcessW.doModal();
        } catch (SuspendNotAllowedException | InterruptedException e) {
            LOGGER.error("Error showing Deploy Process window", e);
        }
    }

    protected Set<RequestParameterType<?>> convertProperties() {
        return propertiesHelper.readPluginProperties(ParameterType.DEFAULT_CATEGORY);
    }

    private void readDeploymentPluginInfos(final String nativeType) throws InterruptedException {
        try {
            final Set<PluginInfo> deploymentPluginInfos = getService().readDeploymentPluginInfo(nativeType);

            if (deploymentPluginInfos.size() >= 1) {

                List<String> canoniserNames = new ArrayList<String>();
                for (PluginInfo cInfo: deploymentPluginInfos) {
                    canoniserNames.add(cInfo.getName() + ":" + cInfo.getVersion());
                }

                Row canoniserSelectionRow = (Row) this.deployProcessW.getFellow("deploymentSelectionRow");
                SelectDynamicListController canoniserCB = new SelectDynamicListController(canoniserNames);
                canoniserCB.setAutodrop(true);
                canoniserCB.setWidth("85%");
                canoniserCB.setHeight("100%");
                canoniserCB.setAttribute("hflex", "1");
                canoniserCB.setSelectedIndex(0);
                canoniserSelectionRow.appendChild(canoniserCB);

                canoniserCB.addEventListener("onSelect", new EventListener() {

                    @Override
                    public void onEvent(final Event event) throws Exception {
                        if (event instanceof SelectEvent) {
                            Comboitem cbItem = (Comboitem) ((SelectEvent) event).getSelectedItems().iterator().next();
                            selectedDeploymentPLugin = cbItem.getLabel();
                            for (PluginInfo info: deploymentPluginInfos) {
                                if (info.getName().equals(selectedDeploymentPLugin)) {
                                    propertiesHelper.showPluginProperties(info, null);
                                }
                            }
                        }
                    }
                });

                PluginInfo deploymentPluginInfo = deploymentPluginInfos.iterator().next();
                propertiesHelper.showPluginProperties(deploymentPluginInfo, null);
                selectedDeploymentPLugin = deploymentPluginInfo.getName() + ":"+ deploymentPluginInfo.getVersion();

            } else {
                closeWindow();
                Messagebox.show(MessageFormat.format("Import failed (No Deployment Plugin found for native type {0})", nativeType), "Attention", Messagebox.OK, Messagebox.ERROR);
            }
        } catch (Exception e) {
            closeWindow();
            Messagebox.show("Reading Deployment Plugin info failed (" + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }

    private void closeWindow() {
        deployProcessW.detach();
    }

    protected String getSelectedPluginVersion() {
        if (selectedDeploymentPLugin != null) {
            return selectedDeploymentPLugin.split(":")[1];
        } else {
            return null;
        }
    }

    protected String getSelectedPluginName() {
        if (selectedDeploymentPLugin != null) {
            return selectedDeploymentPLugin.split(":")[0];
        } else {
            return null;
        }
    }

    private void deployProcess(final Event event) throws InterruptedException {
        Double lastVersion = selectedProcess.getKey().getLastVersion();
        String name = selectedProcess.getKey().getName();
        String branch = "MAIN";
        Clients.showBusy(deployProcessW, "Deploying process...");
        PluginMessages deploymentMessages;
        try {
            deploymentMessages = getService().deployProcess(branch, name, lastVersion, nativeTypeBox.getValue(), getSelectedPluginName(), getSelectedPluginVersion(), convertProperties());
            deployProcessW.detach();
            Clients.clearBusy(deployProcessW);
            mainController.showPluginMessages(deploymentMessages);
        } catch (Exception e) {
            Clients.clearBusy(deployProcessW);
            closeWindow();
            Messagebox.show("Deploy process failed (" + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }

}
