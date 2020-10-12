/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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

import org.apromore.portal.dialogController.dto.VersionDetailType;
import org.apromore.portal.dialogController.renderer.VersionSummaryItemRenderer;
import org.apromore.portal.model.ProcessSummaryType;
import org.apromore.portal.model.VersionSummaryType;
import org.apromore.commons.datetime.DateTimeUtil;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.South;

public class ProcessVersionDetailController extends BaseDetailController {

    private static final long serialVersionUID = 3661234712204860492L;

    private final Listbox listBox;

    public ProcessVersionDetailController(MainController mainController) {
        super(mainController);

        listBox = ((Listbox) Executions.createComponents("macros/detail/processVersionsDetail.zul", getMainController(), null));
        listBox.setItemRenderer(new VersionSummaryItemRenderer(mainController));
        ListModelList<VersionDetailType> model = new ListModelList<>();
        model.setMultiple(true);
        listBox.setModel(model);

        appendChild(listBox);
    }

    @SuppressWarnings("unchecked")
    public void displayProcessVersions(ProcessSummaryType data) {
        getListModel().clearSelection();
        getListModel().clear();
        List<VersionSummaryType> versionSummaries = data.getVersionSummaries();
        List<VersionDetailType> details = new ArrayList<>();
        for (VersionSummaryType version : data.getVersionSummaries()) {
            String lastUpdate = version.getLastUpdate();

            if (lastUpdate != null) {
                lastUpdate = DateTimeUtil.normalize(lastUpdate);
            }
            version.setLastUpdate(lastUpdate);
            details.add(new VersionDetailType(data, version));
        }
        getListModel().addAll(details);
        if (!details.isEmpty()) {
            getListModel().addToSelection(details.get(details.size() - 1));
        }
    }

    protected ListModelList<VersionDetailType> getListModel() {
        return (ListModelList<VersionDetailType>)(Object) listBox.getListModel();
    }

    public void clearProcessVersions() {
        getListModel().clear();
    }

    public VersionSummaryType getSelectedVersion() {
        if (getListModel().getSelection().size() == 1) {
            Object obj = getListModel().getSelection().iterator().next();
            if (obj instanceof VersionSummaryType) {
                return (VersionSummaryType) obj;
            } else if (obj instanceof VersionDetailType) {
                return ((VersionDetailType) obj).getVersion();
            }
            return null;
        } else {
            return null;
        }
    }
}
