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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apromore.plugin.portal.useradmin.common.SearchableListbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;

public class TristateListbox<T> extends SearchableListbox {

    private Map<String, Integer> keyToIndexMap;
    private Map<String, Integer> keyToStateCache;
    private Set<T> addedObjects = new HashSet<T>();
    private Set<T> removedObjects = new HashSet<T>();

    public boolean isMulti = false;

    public TristateListbox(Listbox listbox, ListModelList sourceListModel, String title) {
        super(listbox, sourceListModel, title);
        this.listheader.setSort("none");
    }

    public void setMulti(boolean isMulti) {
        this.isMulti = isMulti;
    }

    public void updateCache() {
        for (int i = 0; i < listModel.size(); i++) {
            TristateModel model = (TristateModel) (listModel.get(i));
            Integer state = model.getState();
            keyToStateCache.put(model.getKey(), state);
        }
    }

    @Override
    public void updateCounts() {
        if (searchInput.getValue().equals("")) {
            searchCount.setValue("");
        } else {
            searchCount.setValue("(" + listModel.size() + " / " + sourceListModel.size() + ")");
        }
    }

    @Override
    public void doSearch(String input) {
        keyToIndexMap = new HashMap<String, Integer>();
        // TO DO: Allow cache search
        // keyToStateCache = new HashMap<String, Integer>();

        if (!input.equals("")) {
            // Save current selection
            // keyToStateCache.clear();
            // updateCache();

            unselectAll();
            listModel.clear();
            int j = 0;
            for (int i = 0; i < sourceListModel.size(); i++) {
                TristateModel model = (TristateModel) (sourceListModel.get(i));
                String value = model.getLabel().toLowerCase();
                if (value.contains(input.toLowerCase())) {
                    listModel.add(model);
                    keyToIndexMap.put(model.getKey(), j);
                    j++;
                }
            }
            updateCounts();
        }
    }

    @Override
    public void initData() {
        listModel.clear();
        keyToIndexMap = new HashMap<String, Integer>();
        for (int i = 0; i < sourceListModel.size(); i++) {
            TristateModel model = (TristateModel) (sourceListModel.get(i));
            model.setState(TristateModel.UNCHECKED);
            listModel.add(model);
            keyToIndexMap.put(model.getKey(), i);
        }
    }

    public Map<String, Integer> getKeyToIndexMap() {
        return keyToIndexMap;
    }

    public void calcSelection() {
        addedObjects = new HashSet<T>();
        removedObjects = new HashSet<T>();
        for (int i = 0; i < listModel.size(); i++) {
            TristateModel model = (TristateModel) (listModel.get(i));
            T obj = (T) model.getObj();
            int state = model.getState();
            if (state == TristateModel.UNCHECKED) {
                removedObjects.add(obj);
            } else if (state == TristateModel.CHECKED) {
                addedObjects.add(obj);
            }
        }
    }

    public Set<T> getAddedObjects() {
        return addedObjects;
    }

    public Set<T> getRemovedObjects() {
        return removedObjects;
    }

}
