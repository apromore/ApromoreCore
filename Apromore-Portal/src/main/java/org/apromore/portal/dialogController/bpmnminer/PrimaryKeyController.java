/*
 * Copyright © 2009-2015 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.portal.dialogController.bpmnminer;

import org.apromore.portal.dialogController.BPMNMinerController;
import org.apromore.portal.dialogController.BaseController;
import org.processmining.plugins.bpmn.miner.preprocessing.functionaldependencies.DiscoverERmodel;
import org.processmining.plugins.bpmn.miner.preprocessing.functionaldependencies.DiscoverERmodel.PrimaryKeyData;
import org.processmining.plugins.bpmn.miner.preprocessing.functionaldependencies.NoEntityException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.*;

import java.util.*;

/**
 * Created by conforti on 9/04/15.
 */
public class PrimaryKeyController extends BaseController {

    private static final long serialVersionUID = 1L;
    private BPMNMinerController bpmnMinerController;
    private Window primaryKeyW;

    private Button cancelButton;
    private Button nextButton;

    private Rows rows;

    private List<PrimaryKeyData> data;
    private int chosenKeysIndex[];
    private Map<Set<String>, Set<String>> group;

    public PrimaryKeyController(BPMNMinerController bpmnMinerController, List<PrimaryKeyData> pKeyData) throws NoEntityException {
        this.bpmnMinerController = bpmnMinerController;

        this.primaryKeyW = (Window) Executions.createComponents("macros/bpmnminer/selectPrimaryKey.zul", null, null);
        this.primaryKeyW.setTitle("Select Primary Keys");

        this.cancelButton = (Button) this.primaryKeyW.getFellow("cancelButton");
        this.nextButton = (Button) this.primaryKeyW.getFellow("okButton");

        this.cancelButton.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                cancel();
            }
        });
        this.nextButton.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                next();
            }
        });

        this.rows = (Rows) this.primaryKeyW.getFellow("rows");

        this.data = pKeyData;
        chosenKeysIndex = new int[data.size()];
        int dataIndex = 0;
        // create a new property item for each event type
        for (PrimaryKeyData currentData : data) {

            chosenKeysIndex[dataIndex] = 0;

            // build a list of all keys (sets of attribute names)
            String[] keyList = new String[currentData.primaryKeys.length]; //change if user can select any attributes for identifiers
            for (int i = 0; i < currentData.primaryKeys.length; i++) {
                HashSet<String> attr = currentData.primaryKeys[i];
                keyList[i] = DiscoverERmodel.keyToString(attr);
            }

            Row row = new Row();
            row.setParent(rows);

            Label activity = new Label(currentData.name);
            activity.setParent(row);

            if(keyList.length > 1) {
                Selectbox selectbox = new Selectbox();
                selectbox.setModel(new ListModelArray<Object>(keyList));
                selectbox.setId("" + dataIndex);
                selectbox.setParent(row);
            }else {
                Label key = new Label(Arrays.toString(keyList));
                key.setParent(row);
            }

            dataIndex++;
        }

        this.primaryKeyW.doModal();

    }

    protected void cancel() {
        this.primaryKeyW.detach();
    }

    protected void next() {
        try {
            updateChosenKeysIndex();
            getSelection();
            this.primaryKeyW.detach();
            this.bpmnMinerController.setSelectedPrimaryKeys(group);
        }catch (ArrayIndexOutOfBoundsException aob) {
            Messagebox.show("Select a primary key for each activity");
        }

    }

    protected void updateChosenKeysIndex() {
        List<Component> rowsList = rows.getChildren();
        for(Component component : rowsList) {
            Component child = component.getLastChild();
            if(child instanceof Selectbox) {
                Selectbox selectbox = (Selectbox) child;
                int index = Integer.parseInt(selectbox.getId());
                chosenKeysIndex[index] = selectbox.getSelectedIndex();
            }
        }
    }

    protected void getSelection() {
        group = new HashMap<Set<String>, Set<String>>();

        for (int dataIndex = 0; dataIndex < data.size(); dataIndex++) {
            Set<String> primaryKey = data.get(dataIndex).primaryKeys[chosenKeysIndex[dataIndex]];
            Set<String> set;
            if ((set = group.get(primaryKey)) == null) {
                set = new HashSet<String>();
                group.put(primaryKey, set);
            }
            set.add(data.get(dataIndex).name);
        }
    }
}
