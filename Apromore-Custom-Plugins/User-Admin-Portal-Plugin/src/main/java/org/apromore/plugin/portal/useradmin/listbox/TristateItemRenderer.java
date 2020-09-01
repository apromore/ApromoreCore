/*-
 * #%L
 * This file is part of "Apromore Core".
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
package org.apromore.plugin.portal.useradmin.listbox;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.EventListener;

import org.zkoss.zul.Label;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import org.apromore.plugin.portal.useradmin.listbox.TristateModel;

public class TristateItemRenderer implements ListitemRenderer {

    final static int UNCHECKED = TristateModel.UNCHECKED;
    final static int CHECKED = TristateModel.CHECKED;
    final static int INDETERMINATE = TristateModel.INDETERMINATE;

    @Override
    public void render(Listitem listItem, Object obj, int index) {
        TristateModel model = (TristateModel) obj;

        Checkbox checkbox = new Checkbox();
        checkbox.setValue(model);

        updateCheckbox(checkbox);
        Listcell cbCell = new Listcell();
        cbCell.appendChild(checkbox);
        listItem.appendChild(cbCell);
        listItem.appendChild(new Listcell(model.getLabel()));

        checkbox.addEventListener(Events.ON_CHECK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                Checkbox checkbox = (Checkbox)event.getTarget();
                rotateState(checkbox);
            }
        });

        listItem.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                Listitem listitem = (Listitem)event.getTarget();
                Checkbox checkbox = (Checkbox)listitem.getChildren().get(1).getFirstChild();
                rotateState(checkbox);
            }
        });
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

        if (model.getState() == INDETERMINATE) {
            model.setState(UNCHECKED);
            checkbox.setIndeterminate(false);
            checkbox.setChecked(false);
        } else if (model.getState() == UNCHECKED) {
            model.setState(CHECKED);
            checkbox.setIndeterminate(false);
            checkbox.setChecked(true);
        } else {
            model.setState(INDETERMINATE);
            checkbox.setIndeterminate(true);
            checkbox.setChecked(false);
        }
    }
}