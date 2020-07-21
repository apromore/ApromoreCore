/*-
 * #%L
 * This file is part of "Apromore Enterprise Edition".
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
/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne. All Rights Reserved.
 *
 */
package org.apromore.plugin.portal.useradmin.common;

import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zul.*;

import java.util.*;

/**
 * Based on Chii's SearchableCaseIdListbox.java
 */
public class SearchableListbox {

    private Listbox listbox;
    private ListModelList sourceListmodel;
    private ListModelList listmodel;
    private Listhead listhead;
    // private Auxhead auxead;

    private Listheader listheader;
    private Checkbox selectAll;

    private Textbox searchInput;
    private Button searchBtn;
    private Button searchBtnClear;
    private Checkbox searchToggle;
    private Label searchCount;

    public SearchableListbox(Listbox listbox, ListModelList sourceListmodel) {
        this.listbox = listbox;
        this.sourceListmodel = sourceListmodel;
        this.listmodel = new ListModelList<>();
        this.listmodel.setMultiple(true);

        // These selector must be custom and defined via sclass
        this.listheader = (Listheader)listbox.query(".z-listheader");
        this.listheader.setSort("auto");
        this.searchToggle = (Checkbox)listbox.query(".ap-listbox-search-toggle");
        this.searchInput = (Textbox)listbox.query(".ap-listbox-search-input");
        this.searchBtn = (Button)listbox.query(".ap-listbox-search-btn");
        this.searchBtnClear = (Button)listbox.query(".ap-listbox-search-clear");
        this.searchCount = (Label) listbox.query(".ap-listbox-search-count");
        this.initEvents();
        this.initData();
        this.listbox.setModel(this.listmodel);
    }

    public ListModelList getSourceListmodel() {
        return sourceListmodel;
    }

    private void initEvents() {

//        selectAll.addEventListener(Events.ON_CHECK, new EventListener<CheckEvent>() {
//            @Override
//            public void onEvent(CheckEvent event) throws Exception {
//                if (event.isChecked()) {
//                    listbox.selectAll();
//                } else {
//                    listbox.clearSelection();
//                }
//                updateCounts();
//            }
//        });

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
                if (e.getSelectedItems().size() > 0) {
                    if (searchToggle.isChecked()) {
                        if (listbox.getSelectedCount() == listbox.getItems().size()) {
                            // selectAll.setChecked(true);
                        }
                    } else {
                        if (listbox.getSelectedCount() == listmodel.size()) {
                            // selectAll.setChecked(true);
                        }
                    }
                }

                if (e.getUnselectedItems().size() > 0) {
                    // selectAll.setChecked(false);
                }
                updateCounts();
            }
        });
    }

    private void updateCounts() {
        if (searchInput.getValue().equals("")) {
            searchCount.setValue("");
        } else {
            searchCount.setValue("(" + listmodel.size() + " / " + sourceListmodel.size() + ")");
        }
        listheader.setLabel("(" + listbox.getSelectedCount() + " / " + listbox.getItemCount() + ")");
    }

    private void doSearch(String input) {
        if (!input.equals("")) {
            unselectAll();
            listmodel.clear();
            for (int i = 0; i < sourceListmodel.size(); i++) {
                String value = getValue(i);
                if (value.contains(input.toLowerCase())) {
                    listmodel.add(sourceListmodel.get(i));
                }
            }
            updateCounts();
        }
    }

    public String getValue(int index) {
        return "" + index;
    }

    public void setSourceListmodel(ListModelList sourceListmodel) {
        this.sourceListmodel = sourceListmodel;
    }

    public ListModelList getListmodel() {
        return this.listmodel;
    }

    private void initData() {
        listmodel.clear();
        for (int i = 0; i < sourceListmodel.size(); i++) {
            listmodel.add(sourceListmodel.get(i));
        }
    }

    public Set getSelection () {
        return listmodel.getSelection();
    }

    public void selectAll() {
        listbox.selectAll();
        listbox.getItemAtIndex(0).setFocus(true);
    }

    public void unselectAll() {
        listbox.clearSelection();
    }

    public Set<Listitem> getSelectedItems() {
        return listbox.getSelectedItems();
    }

    public void showSearchDrawer(boolean visible) {
        Auxhead auxhead = (Auxhead)listbox.query(".ap-auxhead");
        auxhead.setVisible(visible);
    }

    public void reset() {
        initData();
        unselectAll();
        showSearchDrawer(false);
        searchToggle.setChecked(false);
        searchInput.setValue("");
    }
}
