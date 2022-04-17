/*-
 * #%L
 * This file is part of "Apromore Core".
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
package org.apromore.portal.controller;

import org.apromore.portal.common.ArtifactOrderTypes;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.dialogController.ProcessListboxController;
import org.apromore.portal.util.ArtifactsComparator;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Menuseparator;

public class SortMenuController {
    private ArtifactOrderTypes currentSelectedSortingMenuItem;
    private boolean currentSelectedAscending = true;
    private static final String ICON_CHECKED = "z-icon-check";
    private MainController mainController;
    private Menupopup orderListItemPopup;

    public SortMenuController(MainController mainController, Menupopup orderListItemPopup) {
        this.mainController = mainController;
        this.orderListItemPopup = orderListItemPopup;

    }

    private void createSortMenuFieldItems() {
        orderListItemPopup.appendChild(createSortMenuItem(
            ArtifactOrderTypes.BY_TYPE,
            Labels.getLabel("portal_sort_by_type"),
            createSortMenuEventListener(ArtifactOrderTypes.BY_TYPE)
        ));
        orderListItemPopup.appendChild(createSortMenuItem(
            ArtifactOrderTypes.BY_NAME,
            Labels.getLabel("portal_sort_by_name"),
            createSortMenuEventListener(ArtifactOrderTypes.BY_NAME)
        ));
        orderListItemPopup.appendChild(createSortMenuItem(
            ArtifactOrderTypes.BY_CREATED_DATE,
            Labels.getLabel("portal_sort_by_creation"),
            createSortMenuEventListener(ArtifactOrderTypes.BY_CREATED_DATE)));
        orderListItemPopup.appendChild(createSortMenuItem(
            ArtifactOrderTypes.BY_LAST_VERSION,
            Labels.getLabel("portal_sort_by_last_version"),
            createSortMenuEventListener(ArtifactOrderTypes.BY_LAST_VERSION)));
        orderListItemPopup.appendChild(createSortMenuItem(
            ArtifactOrderTypes.BY_UPDATE_DATE,
            Labels.getLabel("portal_sort_by_last_update"),
            createSortMenuEventListener(ArtifactOrderTypes.BY_UPDATE_DATE)));
        orderListItemPopup.appendChild(createSortMenuItem(
            ArtifactOrderTypes.BY_OWNER,
            Labels.getLabel("portal_sort_by_owner"),
            createSortMenuEventListener(ArtifactOrderTypes.BY_OWNER)));
        orderListItemPopup.appendChild(new Menuseparator());
        orderListItemPopup.appendChild(createSortMenuItem("ASCENDING",
            Labels.getLabel("portal_sort_order_ascending"),
            createSortDirectionEventListener(true)));
        orderListItemPopup.appendChild(createSortMenuItem("DESCENDING",
            Labels.getLabel("portal_sort_order_descending"),
            createSortDirectionEventListener(false)));

    }

    private Menuitem createSortMenuItem(String attribute, String label, EventListener<Event> eventListener) {

        Menuitem item = new Menuitem();
        item.setLabel(label);
        ArtifactsComparator comparator =
            (ArtifactsComparator) Executions.getCurrent().getDesktop().getAttribute("ARTIFACT_COMPARATOR");
        if (comparator != null && comparator.isAsc() && attribute.equals("ASCENDING")) {
            item.setIconSclass(ICON_CHECKED);
            currentSelectedAscending = true;
        }
        if (comparator != null && !comparator.isAsc() && attribute.equals("DESCENDING")) {
            item.setIconSclass(ICON_CHECKED);
            currentSelectedAscending = false;
        }
        item.addEventListener(Events.ON_CLICK, eventListener);
        return item;
    }

    private Menuitem createSortMenuItem(ArtifactOrderTypes artifactOrderTypes, String label, EventListener<Event> eventListener) {
        Menuitem item = new Menuitem();
        item.setLabel(label);
        ArtifactsComparator comparator =
            (ArtifactsComparator) Executions.getCurrent().getDesktop().getAttribute("ARTIFACT_COMPARATOR");
        if (comparator != null && comparator.getArtifactOrder().equals(artifactOrderTypes)) {
            item.setIconSclass(ICON_CHECKED);
            currentSelectedSortingMenuItem = artifactOrderTypes;
        }
        item.addEventListener(Events.ON_CLICK, eventListener);
        return item;
    }

    private EventListener<Event> createSortMenuEventListener(ArtifactOrderTypes artifactOrderTypes) {
        return event -> sortList(new ArtifactsComparator(currentSelectedAscending, artifactOrderTypes));
    }

    private EventListener<Event> createSortDirectionEventListener(boolean isAscending) {
        return event -> sortList(new ArtifactsComparator(isAscending, currentSelectedSortingMenuItem));
    }

    private void sortList(ArtifactsComparator comparator) {
        ((ProcessListboxController) mainController.getBaseListboxController()).redrawList(comparator);
    }

    public void showSortMenu(Event e) {
        this.orderListItemPopup.getChildren().clear();
        createSortMenuFieldItems();
        orderListItemPopup.open(e.getTarget(), "at_pointer");
    }
}
