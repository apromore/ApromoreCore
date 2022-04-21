/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apromore.commons.datetime.Constants;
import org.apromore.commons.datetime.DateTimeUtils;
import org.apromore.dao.model.Folder;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.portal.common.VersionSummaryTypes;
import org.apromore.portal.dialogController.dto.VersionDetailType;
import org.apromore.portal.dialogController.renderer.VersionSummaryItemRenderer;
import org.apromore.portal.model.LogSummaryType;
import org.apromore.portal.model.ProcessSummaryType;
import org.apromore.portal.model.VersionSummaryType;
import org.apromore.portal.util.VersionSummaryComparator;
import org.slf4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SortEvent;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;

import static org.apromore.common.Constants.TRUNK_NAME;

public class ProcessVersionDetailController extends BaseDetailController {

    private static final long serialVersionUID = 3661234712204860492L;
    private static final Logger LOGGER =
    	      PortalLoggerFactory.getLogger(ProcessVersionDetailController.class);


    private final Listbox listBox;
    private ProcessSummaryType data;

    public ProcessVersionDetailController(MainController mainController) {
        super(mainController);

        listBox = ((Listbox) Executions.createComponents("~./macros/detail/processVersionsDetail.zul", getMainController(), null));
        listBox.setItemRenderer(new VersionSummaryItemRenderer(mainController));
        ListModelList<VersionDetailType> model = new ListModelList<>();
        model.setMultiple(true);
        listBox.setModel(model);

        appendChild(listBox);
    }

    public void displayProcessVersions(ProcessSummaryType data) {
        getListModel().clearSelection();
        getListModel().clear();
        this.data=data;
        Listheader columnName = (Listheader) this.listBox.getFellow("version");
        columnName.setSortAscending(new VersionSummaryComparator(true, VersionSummaryTypes.BY_VERSION));
        columnName.setSortDescending(new VersionSummaryComparator(false, VersionSummaryTypes.BY_VERSION));
        columnName.addEventListener(Events.ON_SORT, this::forwardSortEvent);

        Listheader columnId = (Listheader) this.listBox.getFellow("lastUpdate");
        columnId.setSortAscending(new VersionSummaryComparator(true, VersionSummaryTypes.BY_UPDATE_DATE));
        columnId.setSortDescending(new VersionSummaryComparator(false, VersionSummaryTypes.BY_UPDATE_DATE));
        columnId.addEventListener(Events.ON_SORT, this::forwardSortEvent);
        redrawList(new VersionSummaryComparator(true, VersionSummaryTypes.BY_VERSION),true);
    }

    private void forwardSortEvent(Event evt) {
        try {
            SortEvent event=(SortEvent)evt;
            Listheader header = (Listheader) event.getTarget();
            VersionSummaryComparator comparator = event.isAscending() ? (VersionSummaryComparator) header.getSortAscending() :
                (VersionSummaryComparator) header.getSortDescending();
            redrawList(comparator,false);
        } catch (Exception ex) {
            LOGGER.error("Error in sorting", ex);
        }
    }

    private void redrawList(VersionSummaryComparator comparator, boolean defaultSelected) {
        if (data == null) {
            return;
        }
        getListModel().clear();
        List<VersionDetailType> details = new ArrayList<>();
        List<VersionSummaryType> versionSummaries = data.getVersionSummaries();
        versionSummaries.sort(comparator);
        for (VersionSummaryType version : versionSummaries) {
            if (version.getName().equals(TRUNK_NAME)) {
                String lastUpdate = version.getLastUpdate();

                if (lastUpdate != null) {
                    lastUpdate = DateTimeUtils.normalize(lastUpdate);
                }
                version.setLastUpdate(lastUpdate);
                details.add(new VersionDetailType(data, version));
            }
        }
        getListModel().addAll(details);
        if (!details.isEmpty() && defaultSelected) {
            getListModel().addToSelection(details.get(details.size() - 1));
        }
    }

    public void displayLogVersions(LogSummaryType data) {
        try {
            getListModel().clearSelection();
            getListModel().clear();
            VersionSummaryType versionSummary = new VersionSummaryType();
            String createdDate = data.getCreateDate();
            if (createdDate != null) {
                createdDate = DateTimeUtils.normalize(createdDate);
            }
            versionSummary.setCreationDate(createdDate);
            getListModel().add(new VersionDetailType(null, versionSummary));
            this.data = null;
        } catch (Exception ex) {
            LOGGER.error("Error occured in assigning Log version", ex);
        }
    }

    public void displayFolderVersions(Folder data) {
        try {
            getListModel().clearSelection();
            getListModel().clear();
            VersionSummaryType versionSummary = new VersionSummaryType();
            DateFormat dateFormat = new SimpleDateFormat(Constants.DATE_TIME_FORMAT_HUMANIZED);
            if (data.getDateModified() != null) {
                versionSummary.setCreationDate(dateFormat.format(data.getDateModified()));
            } else {
                versionSummary.setCreationDate(dateFormat.format(data.getDateCreated()));
            }
            getListModel().add(new VersionDetailType(null, versionSummary));
            this.data = null;
        } catch (Exception ex) {
            LOGGER.error("Error occured in assigning Folder version", ex);
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
