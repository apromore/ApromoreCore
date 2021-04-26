/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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
package org.apromore.plugin.portal.processdiscoverer.components;

import org.apromore.plugin.portal.processdiscoverer.PDController;
import org.apromore.plugin.portal.processdiscoverer.data.UserOptionsData;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

public class ToolbarController extends AbstractController {
    private final String LAYOUT_HIERARCHY = "layoutHierarchy";
    private final String LAYOUT_DAGRE_TB = "layoutDagreTopBottom";
    private Checkbox layoutHierarchy;
    private Checkbox layoutDagreTopBottom;

    private Button filter;
    private Button filterClear;
    private Button animate;
    private Button fitScreen;
    private Button share;

    private Button exportFilteredLog;
    private Button downloadPDF;
    private Button downloadPNG;
    private Button downloadJSON;
    private Button exportBPMN;
    
    private Textbox searchText;
    private Button shortcutButton;
    
    private UserOptionsData userOptions;
    
    public ToolbarController(PDController parent) {
        super(parent);
        userOptions = parent.getUserOptions();
    }
    
    @Override
    public void initializeControls(Object data) throws Exception {
        
        Component toolbar = parent.getFellow("toolbar");
        filter = (Button) toolbar.getFellow("filter");
        filterClear = (Button) toolbar.getFellow("filterClear");
        filterClear.setDisabled(true);
        animate = (Button) toolbar.getFellow("animate");
        fitScreen = (Button) toolbar.getFellow("fitScreen");
        share = (Button) toolbar.getFellow("share");

        exportFilteredLog = (Button) toolbar.getFellow("exportUnfitted");
        downloadPDF = (Button) toolbar.getFellow("downloadPDF");
        downloadPNG = (Button) toolbar.getFellow("downloadPNG");
        downloadJSON = (Button) toolbar.getFellow("downloadJSON");
        exportBPMN = (Button) toolbar.getFellow("exportBPMN");

        layoutHierarchy = (Checkbox) toolbar.getFellow(LAYOUT_HIERARCHY);
        layoutHierarchy.setChecked(userOptions.getLayoutHierarchy());
        layoutDagreTopBottom = (Checkbox) toolbar.getFellow(LAYOUT_DAGRE_TB);
        layoutDagreTopBottom.setChecked(userOptions.getLayoutDagre());

        searchText = (Textbox) toolbar.getFellow("searchText");
        shortcutButton = (Button) toolbar.getFellow("shortcutButton");
    }
    
    @Override
    public void initializeEventListeners(Object data) throws Exception {
        // Layout
        EventListener<Event> layoutListener = new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                String compId = ((Checkbox) event.getTarget()).getId();
                switch (compId) {
                case LAYOUT_HIERARCHY:
                    userOptions.setLayoutHierarchy(true);
                    userOptions.setLayoutDagre(false);
                    break;
                case LAYOUT_DAGRE_TB:
                    userOptions.setLayoutHierarchy(false);
                    userOptions.setLayoutDagre(true);
                    break;
                }
                parent.changeLayout();
            }
        };
        
        layoutHierarchy.addEventListener("onClick", layoutListener);
        
        layoutDagreTopBottom.addEventListener("onClick", layoutListener);

        fitScreen.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                parent.fitVisualizationToWindow();
            }
        });

        filterClear.addEventListener("onClick", e -> onClearFilter());
        
        filter.addEventListener("onInvoke", e -> parent.openLogFilter(e));
        filter.addEventListener("onInvokeExt", e -> parent.openLogFilter(e));
        filter.addEventListener(Events.ON_CLICK, e -> parent.openLogFilter(e));
        
        animate.addEventListener("onClick", e -> parent.openAnimation(e));

        exportFilteredLog.addEventListener("onExport", e -> parent.openLogExport(e));
        
        exportBPMN.addEventListener("onClick", e -> parent.openBPMNExport(e));
        
        downloadPDF.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                parent.exportPDF();
            }
        });
        
        downloadPNG.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                parent.exportPNG();
            }
        });
        
        downloadJSON.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                parent.exportJSON();
            }
        });

        share.addEventListener("onClick", event -> {
            parent.openSharingWindow();
        });
    }
    
    @Override
    public void setDisabled(boolean disabled) {
        layoutHierarchy.setDisabled(disabled);
        layoutDagreTopBottom.setDisabled(disabled);
        filter.setDisabled(disabled);
        filterClear.setDisabled(disabled || parent.getLogData().isCurrentFilterCriteriaEmpty());
        searchText.setDisabled(disabled);
        shortcutButton.setDisabled(disabled);
    }

    public void setDisabledAnimation(boolean disabled) {
        animate.setDisabled(disabled || !userOptions.getMainAttributeKey().equals(parent.getConfigData().getDefaultAttribute()));
    }

    public void toogleAnimateBtn(boolean state) {
        parent.toggleComponentSclass(animate, state, "ap-btn-anim-off", "ap-btn-anim-on");
    }

    public void setDisabledFilterClear(boolean disabled) {
        filterClear.setDisabled(disabled);
    }
    
    @Override
    public void updateUI(Object data) throws Exception {
        //
    }
    
    private void onClearFilter() {
        Messagebox.show(
                "Are you sure you want to clear all filters?",
                "Filter log",
                Messagebox.OK | Messagebox.CANCEL,
                Messagebox.QUESTION,
                e -> proceedClearFilter(e)
        );
    }

    private void proceedClearFilter(Event evt) {
        if (evt.getName().equals("onOK")) {
            try {
                clearFilter();
            } catch (Exception e) {
                Messagebox.show("Unable to clear the filter", "Filter error",
                        Messagebox.OK, Messagebox.ERROR);
            }
        }
    }

    private void clearFilter() throws Exception {
        parent.clearFilter();
    }
}
