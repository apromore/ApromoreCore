/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2011 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.portal.dialogController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apromore.model.ProcessSummaryType;
import org.apromore.model.SummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.portal.exception.ExceptionFormats;
import org.zkoss.zk.ui.SuspendNotAllowedException;

public class EditListProcessesController extends BaseController {

    private MainController mainC;
    private List<EditOneProcessController> toEditList;
    private List<EditOneProcessController> editedList;

    public EditListProcessesController(MainController mainC, MenuController menuC, Map<SummaryType, List<VersionSummaryType>> processVersions)
            throws SuspendNotAllowedException, InterruptedException, ExceptionFormats {
        this.mainC = mainC;
        this.toEditList = new ArrayList<>();
        this.editedList = new ArrayList<>();
        Set<SummaryType> keys = processVersions.keySet();
        for (SummaryType key : keys) {
            if(key instanceof ProcessSummaryType) {
                ProcessSummaryType process = (ProcessSummaryType) key;
                for (Integer i = 0; i < processVersions.get(process).size(); i++) {
                    VersionSummaryType version = processVersions.get(process).get(i);
                    EditOneProcessController editOneProcess = new EditOneProcessController(this.mainC, this, process, version);
                    this.toEditList.add(editOneProcess);
                }
            }
        }
    }

    public List<EditOneProcessController> getEditedList() {
        if (editedList == null) {
            editedList = new ArrayList<>();
        }
        return this.editedList;
    }


    public List<EditOneProcessController> getToEditList() {
        if (toEditList == null) {
            toEditList = new ArrayList<>();
        }
        return toEditList;
    }

    public void deleteFromToBeEdited(EditOneProcessController editOneProcess) throws Exception {
        this.toEditList.remove(editOneProcess);
        if (this.toEditList.size() == 0) {
            reportEditProcess();
        }
    }

    private void reportEditProcess() throws Exception {
        String report = "Modification of " + this.editedList.size();
        if (this.editedList.size() == 0) {
            report += " process.";
        } else {
            if (this.editedList.size() == 1) {
                report += " process completed.";
            } else if (this.editedList.size() > 1) {
                report += " processes completed.";
            }
            this.mainC.reloadSummaries();
        }
        this.mainC.displayMessage(report);
    }

    public void cancelAll() {
        for (EditOneProcessController aToEditList : this.toEditList) {
            if (aToEditList.getEditOneProcessWindow() != null) {
                aToEditList.getEditOneProcessWindow().detach();
            }
        }
        this.toEditList.clear();
    }
}
