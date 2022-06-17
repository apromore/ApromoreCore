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

package org.apromore.plugin.portal.useradmin.listbox;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class TristateItemRenderer implements ListitemRenderer {

    static final int UNCHECKED = TristateModel.UNCHECKED;
    static final int CHECKED = TristateModel.CHECKED;
    static final int INDETERMINATE = TristateModel.INDETERMINATE;

    public TristateListbox list;
    public boolean forceTwoState = false;
    public boolean disabled = false;
    private boolean multiSelected = false;
    private Listbox listbox;

    public void setList(TristateListbox list) {
        this.list = list;
    }

    public void setListbox(Listbox listbox) {
        this.listbox = listbox;
    }

    public void setForceTwoState(boolean forceTwoState) {
        this.forceTwoState = forceTwoState;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public void render(Listitem listItem, Object obj, int index) {
        TristateModel model = (TristateModel) obj;

        Checkbox checkbox = new Checkbox();
        checkbox.setValue(model);
        checkbox.setDisabled(model.isDisabled());

        updateCheckbox(checkbox);
        Listcell cbCell = new Listcell();
        cbCell.appendChild(checkbox);
        listItem.appendChild(cbCell);
        listItem.appendChild(new Listcell(model.getLabel()));
        listItem.setDisabled(model.isDisabled());

        checkbox.addEventListener(Events.ON_CHECK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                Checkbox checkbox = (Checkbox) event.getTarget();
                rotateState(checkbox);
            }
        });

        if (!model.isDisabled()) {
            listItem.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
                @Override
                public void onEvent(Event event) throws Exception {
                    Listitem listitem = (Listitem) event.getTarget();
                    Checkbox checkbox = (Checkbox) listitem.getChildren().get(0).getFirstChild();
                    rotateState(checkbox);
                }
            });
        }
    }

    public void updateCheckbox(Checkbox checkbox) {
        TristateModel model = checkbox.getValue();

        if (model.getState() == INDETERMINATE) {
            checkbox.setIndeterminate(true);
            checkbox.setChecked(false);
        } else {
            checkbox.setIndeterminate(false);
            if (model.getState() == CHECKED) {
                checkbox.setChecked(true);
            } else {
                checkbox.setChecked(false);
            }
        }
    }

    public void rotateState(Checkbox checkbox) {
        TristateModel model = checkbox.getValue();
        updateExistingSelection(checkbox);
        Listitem listitem = (Listitem) checkbox.getParent().getParent();
        int index = listitem.getIndex();

        if (disabled) {
            model.setState(UNCHECKED);
            checkbox.setIndeterminate(false);
            checkbox.setChecked(false);
        } else if (forceTwoState) {
            if (model.getState() == INDETERMINATE) {
                model.setState(UNCHECKED);
                checkbox.setIndeterminate(false);
                checkbox.setChecked(false);
            } else if (model.getState() == UNCHECKED) {
                model.setState(CHECKED);
                checkbox.setIndeterminate(false);
                checkbox.setChecked(true);
            } else {
                model.setState(UNCHECKED);
                checkbox.setIndeterminate(false);
                checkbox.setChecked(false);
            }
        } else {
            boolean twoStateOnly = model.isTwoStateOnly();
            if (model.getState() == INDETERMINATE) {
                model.setState(UNCHECKED);
                checkbox.setIndeterminate(false);
                checkbox.setChecked(false);
            } else if (model.getState() == UNCHECKED) {
                model.setState(CHECKED);
                checkbox.setIndeterminate(false);
                checkbox.setChecked(true);
            } else { // CHECKED
                if (twoStateOnly) {
                    model.setState(UNCHECKED);
                    checkbox.setIndeterminate(false);
                    checkbox.setChecked(false);
                } else {
                    model.setState(INDETERMINATE);
                    checkbox.setIndeterminate(true);
                    checkbox.setChecked(false);
                }
            }
        }
        list.getListModel().set(index, model); // trigger change
    }

    /**
     * Update other list items if there is a limit to whether multiple items can be selected.
     * If the selected checkbox is not coSelectable, all other non-coSelectable checkboxes must be unchecked.
     *
     * @param selectedCheckbox the checkbox of the selected list item.
     */
    public void updateExistingSelection(Checkbox selectedCheckbox) {
        TristateModel selectedModel = selectedCheckbox.getValue();

        boolean isExclusionRuleApplyOnMultiple = multiSelected && selectedModel.getState() == INDETERMINATE;

        if (!selectedModel.isCoSelectable()) {
            for (Listitem item : listbox.getItems()) {
                Checkbox listItemCheckbox = (Checkbox) item.getChildren().get(0).getFirstChild();
                TristateModel listItemModel = listItemCheckbox.getValue();
                if (isExclusionRuleApplyOnMultiple && !listItemCheckbox.getUuid().equals(selectedCheckbox.getUuid())) {
                    continue;
                }

                if (!listItemModel.isCoSelectable() && !selectedCheckbox.equals(listItemCheckbox)) {
                    listItemModel.setState(UNCHECKED);
                    listItemCheckbox.setIndeterminate(false);
                    listItemCheckbox.setChecked(false);
                }
            }
        }
    }

    public void setMultiUserSelected(boolean multiSelected) {
        this.multiSelected = multiSelected;
    }
}
