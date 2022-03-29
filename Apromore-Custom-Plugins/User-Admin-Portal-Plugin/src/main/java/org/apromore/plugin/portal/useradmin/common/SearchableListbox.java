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

package org.apromore.plugin.portal.useradmin.common;

import java.util.Set;
import org.zkoss.zk.ui.event.CheckEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zul.Auxhead;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;

/**
 * Based on Chii's SearchableCaseIdListbox.java
 */
public class SearchableListbox {

    public ListModelList sourceListModel;
    public ListModelList listModel;

    private Listbox listbox;
    private Listhead listhead;
    // private Auxhead auxead;
    private String title = "";

    public Listheader listheader;

    public Textbox searchInput;
    private Button searchBtn;
    private Button searchBtnClear;
    private Checkbox searchToggle;
    public Label searchCount;

    public SearchableListbox(Listbox listbox, ListModelList sourceListModel, String title) {
        this.title = title;
        this.listbox = listbox;
        this.sourceListModel = sourceListModel;
        this.listModel = new ListModelList<>();
        this.listModel.setMultiple(true);

        // These selector must be custom and defined via sclass
        this.listheader = (Listheader) listbox.query(".z-listheader");
        this.searchToggle = (Checkbox) listbox.query(".ap-listbox-search-toggle");
        this.searchInput = (Textbox) listbox.query(".ap-listbox-search-input");
        this.searchBtn = (Button) listbox.query(".ap-listbox-search-btn");
        this.searchBtnClear = (Button) listbox.query(".ap-listbox-search-clear");
        this.searchCount = (Label) listbox.query(".ap-listbox-search-count");
        this.initEvents();
        this.initData();
        this.listbox.setModel(this.listModel);
        this.listbox.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                Object o = event.getTarget();
            }
        });
        this.updateCounts();
    }

    public ListModelList getSourceListmodel() {
        return sourceListModel;
    }

    private void initEvents() {

        searchToggle.addEventListener(Events.ON_CHECK, new EventListener<CheckEvent>() {
            @Override
            public void onEvent(CheckEvent event) throws Exception {
                if (event.isChecked()) {
                    showSearchDrawer(true);
                } else {
                    showSearchDrawer(false);
                    searchInput.setValue("");
                }
            }
        });

        searchInput.addEventListener(Events.ON_OK, new EventListener<Event>() {
            @Override
            public void onEvent(Event e) throws Exception {
                doSearch(searchInput.getValue());
            }
        });

        searchBtn.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event e) throws Exception {
                doSearch(searchInput.getValue());
            }
        });

        searchBtnClear.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event e) throws Exception {
                searchInput.setValue("");
                reset();
            }
        });

        listbox.addEventListener(Events.ON_SELECT, new EventListener<SelectEvent>() {
            @Override
            public void onEvent(SelectEvent e) throws Exception {
                updateCounts();
            }
        });
    }

    public void updateCounts() {
        if (searchInput.getValue().equals("")) {
            searchCount.setValue("");
        } else {
            searchCount.setValue("(" + listModel.size() + "/" + sourceListModel.size() + ")");
        }
        listheader.setLabel(title + " (" + listbox.getSelectedCount() + "/" + sourceListModel.size() + ")");
    }

    public void doSearch(String input) {
        if (!input.equals("")) {
            unselectAll();
            listModel.clear();
            for (int i = 0; i < sourceListModel.size(); i++) {
                String value = getValue(i);
                if (value.contains(input.toLowerCase())) {
                    listModel.add(sourceListModel.get(i));
                }
            }
            updateCounts();
        }
    }

    public String getValue(int index) {
        return "" + index;
    }

    public ListModelList getSourceListModel() {
        return sourceListModel;
    }

    public void setSourceListModel(ListModelList sourceListModel) {
        this.sourceListModel = sourceListModel;
    }

    public ListModelList getListModel() {
        return this.listModel;
    }

    public void initData() {
        listModel.clear();
        for (int i = 0; i < sourceListModel.size(); i++) {
            listModel.add(sourceListModel.get(i));
        }
        listbox.setModel(listModel);
    }

    public int getSelectionCount() {
        return listbox.getSelectedCount();
    }

    public boolean isSingleFileSelected() {
        return getSelectionCount() == 1;
    }

    public Set getSelection() {
        return listModel.getSelection();
    }

    public void selectAll() {
        if (listbox != null && listModel != null && !listbox.getItems().isEmpty()) {
            listbox.selectAll();
            listbox.getItemAtIndex(0).setFocus(true);
            listModel.clearSelection();
            getListModel().getInnerList().forEach(li -> listModel.addToSelection(li));
            updateCounts();
        }
    }

    public void unselectAll() {
        if (listbox != null && listModel != null) {
            listbox.clearSelection();
            listModel.clearSelection();
            updateCounts();
        }
    }

    public Set<Listitem> getSelectedItems() {
        return listbox.getSelectedItems();
    }

    public void showSearchDrawer(boolean visible) {
        Auxhead auxhead = (Auxhead) listbox.query(".ap-auxhead");
        auxhead.setVisible(visible);
    }

    public void reset() {
        initData();
        unselectAll();
        showSearchDrawer(false);
        searchToggle.setChecked(false);
        searchInput.setValue("");
        updateCounts();
    }

}
