/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.portal.dialogController;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apromore.canoniser.Canoniser;
import org.apromore.helper.Version;
import org.apromore.model.EditSessionType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.portal.common.Constants;
import org.apromore.portal.common.UserSessionManager;

import org.apromore.portal.exception.ExceptionFormats;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zul.Button;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class SaveAsDialogController extends BaseController {

    private EventQueue<Event> qe = EventQueues.lookup(Constants.EVENT_QUEUE_REFRESH_SCREEN, EventQueues.SESSION, true);

    private static final BigDecimal VERSION_INCREMENT = new BigDecimal("0.1");

    private Window saveAsW;
    private Textbox modelName;
    private Textbox versionNumber;
    private Textbox branchName;
    private ProcessSummaryType process;
    private VersionSummaryType version;
    private PluginPropertiesHelper pluginPropertiesHelper;
    private EditSessionType editSession;
    private boolean save;
    private String modelData;
    private String originalVersionNumber;

    public SaveAsDialogController(ProcessSummaryType process, VersionSummaryType version, EditSessionType editSession,
            boolean isNormalSave, String data) throws SuspendNotAllowedException, InterruptedException, ExceptionFormats {
        this.process = process;
        this.version = version;
        this.editSession = editSession;
        this.save = isNormalSave;
        this.saveAsW = (Window) Executions.createComponents("saveAsDialog.zul", null, null);
        this.modelData = data;
        this.originalVersionNumber = this.editSession.getCurrentVersionNumber();
        if (isNormalSave) {
            this.saveAsW.setTitle("Save");
        } else {
            this.saveAsW.setTitle("Save As");
        }

        Rows rows = (Rows) this.saveAsW.getFirstChild().getFirstChild().getFirstChild().getNextSibling();
        Row modelNameR = (Row) rows.getChildren().get(0);
        Row versionNumberR = (Row) rows.getChildren().get(1);
        Row branchNameR = (Row) rows.getChildren().get(2);
        Row buttonGroupR = (Row) rows.getChildren().get(3);
        this.modelName = (Textbox) modelNameR.getFirstChild().getNextSibling();
        this.versionNumber = (Textbox) versionNumberR.getFirstChild().getNextSibling();
        this.branchName = (Textbox) branchNameR.getFirstChild().getNextSibling();

        pluginPropertiesHelper = new PluginPropertiesHelper(getService(), (Grid) this.saveAsW.getFellow("saveAsGrid"));
        Button saveB = (Button) buttonGroupR.getFirstChild().getFirstChild();
        Button cancelB = (Button) saveB.getNextSibling();
        this.modelName.setText(this.editSession.getProcessName());

        saveB.addEventListener("onClick",
                new EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        saveModel(save);
                    }
                });
        this.saveAsW.addEventListener("onOK",
                new EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        saveModel(save);
                    }
                });
        cancelB.addEventListener("onClick",
                new EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        cancel();
                    }
                });

        if (isNormalSave) {
            String branchName = null;
            BigDecimal versionNumber;
            BigDecimal currentVersion = new BigDecimal(editSession.getCurrentVersionNumber());
            BigDecimal maxVersion = new BigDecimal(editSession.getMaxVersionNumber());

            versionNumber = createNewVersionNumber(currentVersion);
            if (maxVersion.compareTo(currentVersion) > 0) {
                branchName = createNewBranchName(this.editSession.getOriginalBranchName());
            } else {
                branchName = this.editSession.getOriginalBranchName();
            }

            this.modelName.setReadonly(true);
            this.branchName.setText(branchName);
            if (version.isEmpty()) {
                this.versionNumber.setText("1.0");
            } else {
                this.versionNumber.setText(String.format("%1.1f", versionNumber));
            }
        } else {
            this.branchName.setText("MAIN");
            this.branchName.setReadonly(true);
            this.versionNumber.setText("1.0");
        }

        this.saveAsW.doModal();
    }


    protected void cancel() throws Exception {
        closePopup();
    }

    private void closePopup() {
        this.saveAsW.detach();
    }

    protected void saveModel(boolean isNormalSave) throws Exception {
        String userName = UserSessionManager.getCurrentUser().getUsername();
        String nativeType = this.editSession.getNativeType();
        String versionName = this.version.getName();
        String domain = this.process.getDomain();
        String processName = this.modelName.getText();
        Integer processId = this.process.getId();
        String created = this.version.getCreationDate();
        String branch = this.branchName.getText();
        boolean makePublic = this.process.isMakePublic();
        String versionNo = versionNumber.getText();

        if (branch == null || branch.equals("")) {
            branch = "MAIN";
        }

        // TODO: If Save As, the default branch should be MAIN and has to be handled at the server end
        InputStream is = new ByteArrayInputStream(this.modelData.getBytes());
        try {
            if (validateFields()) {
                if (!isNormalSave) {
                    Integer folderId = 0;
                    if (UserSessionManager.getCurrentFolder() != null) {
                        folderId = UserSessionManager.getCurrentFolder().getId();
                    }
                    getService().importProcess(userName, folderId, nativeType, processName, versionNo, is, domain, null, created, null,
                            makePublic, pluginPropertiesHelper.readPluginProperties(Canoniser.CANONISE_PARAMETER));
                } else {
                    getService().updateProcess(editSession.hashCode(), userName, nativeType, processId, domain, process.getName(),
                            editSession.getOriginalBranchName(), branch, versionNo, originalVersionNumber, versionName, is);
                }
                Messagebox.show("Saved Successfully!", "Save", Messagebox.OK, Messagebox.INFORMATION);
                qe.publish(new Event(Constants.EVENT_MESSAGE_SAVE, null, Boolean.TRUE));
                closePopup();
            }
        } catch (Exception e) {
            Messagebox.show("Unable to Save Model : Error: \n" + e.getMessage());
        }
    }


    private boolean validateFields() {
        boolean valid = true;
        String message = "";
        String title = "Missing Fields";

        Version newVersion = new Version(versionNumber.getText());
        Version curVersion = new Version(editSession.getCurrentVersionNumber());
        try {
            if (this.save) {
                if (newVersion.compareTo(curVersion) < 0) {
                    valid = false;
                    message = message + "New Version number has to be greater than " + this.editSession.getCurrentVersionNumber();
                    title = "Wrong Version Number";
                }
                if (this.branchName.getText().equals("") || this.branchName.getText() == null) {
                    valid = false;
                    message = message + "Branch Name cannot be empty";
                    title = "Branch Name Empty";
                }
            } else {
                if (this.modelName.getText().equals("") || this.modelName.getText() == null) {
                    valid = false;
                    message = message + "Model Name cannot be empty";
                    title = "Model Name Empty";
                }
                if (this.modelName.getText().equals(this.editSession.getProcessName())) {
                    valid = false;
                    message = message + "Model Name has to be different from " + this.editSession.getProcessName();
                    title = "Same Model Name";
                }
            }
            if (this.versionNumber.getText().equals("") || this.versionNumber.getText() == null) {
                valid = false;
                message = message + "Version Number cannot be empty";
                title = "Version Number Empty";
            }
            if (!message.equals("")) {
                Messagebox.show(message, title, Messagebox.OK, Messagebox.INFORMATION);
            }
        } catch (Exception e) {
            valid = false;
        }
        return valid;
    }

    private String createNewBranchName(String currBranchName) {
        String branchName;
        if (currBranchName.equalsIgnoreCase("Main") || !currBranchName.matches("B[0-9]+")) {
            branchName = "B1";
        } else {
            Integer branchVersionNumber = 0;
            Matcher matcher = Pattern.compile("\\d+").matcher(currBranchName);
            if (matcher.find()) {
                branchVersionNumber = Integer.valueOf(matcher.group());
            }
            branchName = "B" + branchVersionNumber + 1;
        }
        return branchName;
    }

    private BigDecimal createNewVersionNumber(BigDecimal currentVersion) {
        return (currentVersion).add(VERSION_INCREMENT);
    }

}