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
import org.processmining.plugins.bpmn.miner.preprocessing.functionaldependencies.NoEntityException;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import java.util.List;

/**
 * Created by conforti on 9/04/15.
 */
public class CandidatesEntitiesController extends BaseController {

    private static final long serialVersionUID = 1L;
    private BPMNMinerController bpmnMinerController;
    private Window candidateEntitiesW;
    private Listbox candidates;
    private Button cancelButton;
    private Button nextButton;

    private List<String> listCandidates;
    private boolean[] selected;

    public CandidatesEntitiesController(BPMNMinerController bpmnMinerController, List<String> listCandidates) {
        this.bpmnMinerController = bpmnMinerController;
        this.selected = new boolean[listCandidates.size()];
        this.listCandidates = listCandidates;

        this.candidateEntitiesW = (Window) Executions.createComponents("macros/bpmnminer/candidateEntities.zul", null, null);
        this.candidateEntitiesW.setTitle("Select Candidate Entities");

        this.candidates = (Listbox) this.candidateEntitiesW.getFellow("list");
        for(String candidate : listCandidates) {
            Listitem listItem = new Listitem();
            listItem.setLabel(candidate);
            this.candidates.appendChild(listItem);
            listItem.setSelected(true);
        }

        this.cancelButton = (Button) this.candidateEntitiesW.getFellow("entityCancelButton");
        this.nextButton = (Button) this.candidateEntitiesW.getFellow("entityNextButton");

        updateActions();

        this.candidates.addEventListener("onSelect", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                updateActions();
            }
        });
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

        if(listCandidates != null && listCandidates.size() > 0) {
            this.candidateEntitiesW.doModal();
        }else {
            next();
        }
    }

    protected void cancel() {
        this.candidateEntitiesW.detach();
    }

    protected void next() {
        this.candidateEntitiesW.detach();
        try {
            this.bpmnMinerController.setSelectedCandidatesEntities(listCandidates, selected);
        } catch (NoEntityException e) {
            this.bpmnMinerController.noEntityException();
        }
    }

    protected void updateActions() {
        for(int i = 0; i < selected.length; i++) {
            boolean done = false;
            for(Listitem listItem : candidates.getSelectedItems()) {
                if(listCandidates.get(i).equals(listItem.getLabel())) {
                    selected[i] = true;
                    done = true;
                    break;
                }
            }
            if(!done) selected[i] = false;
        }
    }
}
