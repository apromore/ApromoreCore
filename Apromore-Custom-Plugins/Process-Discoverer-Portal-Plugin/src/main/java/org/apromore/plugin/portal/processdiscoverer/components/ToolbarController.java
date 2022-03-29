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
package org.apromore.plugin.portal.processdiscoverer.components;

import org.apromore.exception.UserNotFoundException;
import org.apromore.plugin.portal.processdiscoverer.PDController;
import org.apromore.plugin.portal.processdiscoverer.actions.FilterAction;
import org.apromore.plugin.portal.processdiscoverer.actions.FilterActionOnClearFilter;
import org.apromore.plugin.portal.processdiscoverer.data.ContextData;
import org.apromore.plugin.portal.processdiscoverer.data.UserOptionsData;
import org.apromore.service.AuthorizationService;
import org.apromore.util.AccessType;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Span;
import org.zkoss.zul.Textbox;

public class ToolbarController extends AbstractController {
    private final String LAYOUT_HIERARCHY = "layoutHierarchy";
    private final String LAYOUT_DAGRE_TB = "layoutDagreTopBottom";
    private Button layoutHierarchy;
    private Button layoutDagreTopBottom;

    private Button filter;
    private Button filterClear;
    private Button filterUndo;
    private Button filterRedo;
    private Button animate;
    private Button fitScreen;
    private Button share;
    private Button calendar;
    private Button cost;

    private Button exportFilteredLog;
    private Button downloadPDF;
    private Button downloadPNG;
    private Button downloadJSON;
    private Button exportBPMN;
    
    private Textbox searchText;
    private Div searchNode;
    private Button shortcutButton;
    private Div rightToolbar;
    
    private UserOptionsData userOptions;

    private boolean isReadOnly;
    
    public ToolbarController(PDController parent) {
        super(parent);
        userOptions = parent.getUserOptions();
    }
    
    @Override
    public void initializeControls(Object data) throws Exception {

        isReadOnly = isReadOnly();
        
        Component toolbar = parent.getFellow("toolbar");
        filter = (Button) toolbar.getFellow("filter");
        filterClear = (Button) toolbar.getFellow("filterClear");
        filterClear.setDisabled(true);
        filterUndo = (Button) toolbar.getFellow("filterUndo");
        filterRedo = (Button) toolbar.getFellow("filterRedo");
        animate = (Button) toolbar.getFellow("animate");
        fitScreen = (Button) toolbar.getFellow("fitScreen");
        share = (Button) toolbar.getFellow("share");
        cost = (Button) toolbar.getFellow("cost");

        Span calendarSep = (Span) toolbar.getFellow("calendarSep");
        calendar = (Button) toolbar.getFellow("calendar");
        calendarSep.setVisible(parent.getContextData().isCalendarEnabled());
        calendar.setVisible(parent.getContextData().isCalendarEnabled());

        exportFilteredLog = (Button) toolbar.getFellow("exportUnfitted");
        exportFilteredLog.setVisible(!isReadOnly);

        downloadPDF = (Button) toolbar.getFellow("downloadPDF");
        downloadPNG = (Button) toolbar.getFellow("downloadPNG");
        downloadJSON = (Button) toolbar.getFellow("downloadJSON");

        exportBPMN = (Button) toolbar.getFellow("exportBPMN");
        exportBPMN.setVisible(!isReadOnly);
        exportBPMN.setDisabled(true);

        layoutHierarchy = (Button) toolbar.getFellow(LAYOUT_HIERARCHY);
        layoutDagreTopBottom = (Button) toolbar.getFellow(LAYOUT_DAGRE_TB);

        searchText = (Textbox) toolbar.getFellow("searchText");
        searchNode = (Div) toolbar.getFellow("searchNode");
        shortcutButton = (Button) toolbar.getFellow("shortcutButton");
        rightToolbar = (Div) toolbar.getFellow("rightToolbar");
    }

    public void updateUndoRedoButtons(boolean undoState, boolean redoState) {
        filterUndo.setDisabled(!undoState);
        filterRedo.setDisabled(!redoState);
    }

    @Override
    public void initializeEventListeners(Object data) throws Exception {
        // Layout
        layoutHierarchy.addEventListener(Events.ON_CLICK, e -> changeLayout(LAYOUT_HIERARCHY));
        layoutDagreTopBottom.addEventListener(Events.ON_CLICK, e -> changeLayout(LAYOUT_DAGRE_TB));

        fitScreen.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                parent.fitVisualizationToWindow();
            }
        });

        filter.addEventListener("onInvoke", e -> parent.openLogFilter(e));
        filter.addEventListener("onInvokeExt", e -> parent.openLogFilter(e));
        filter.addEventListener(Events.ON_CLICK, e -> parent.openLogFilter(e));
        filterClear.addEventListener(Events.ON_CLICK, e -> onClearFilter());
        filterUndo.addEventListener(Events.ON_CLICK, e -> parent.getActionManager().undoAction());
        filterRedo.addEventListener(Events.ON_CLICK, e -> parent.getActionManager().redoAction());
        
        animate.addEventListener(Events.ON_CLICK, e -> parent.openAnimation(e));
        exportFilteredLog.addEventListener("onExport", e -> parent.openLogExport(e));
        exportBPMN.addEventListener(Events.ON_CLICK, e -> parent.openBPMNExport(e));
        calendar.addEventListener(Events.ON_CLICK, e -> parent.openCalendar());
        cost.addEventListener(Events.ON_CLICK, e -> parent.openCost());

        downloadPDF.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                parent.exportPDF();
            }
        });
        
        downloadPNG.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                parent.exportPNG();
            }
        });
        
        downloadJSON.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                parent.exportJSON();
            }
        });

        share.addEventListener(Events.ON_CLICK, event -> {
            parent.openSharingWindow();
        });
    }
    
    @Override
    public void setDisabled(boolean disabled) {
        layoutHierarchy.setDisabled(disabled);
        layoutDagreTopBottom.setDisabled(disabled);
        filter.setDisabled(disabled);
        filterClear.setDisabled(disabled || parent.getProcessAnalyst().isCurrentFilterCriteriaEmpty());
        shortcutButton.setDisabled(disabled);
        rightToolbar.setClientDataAttribute("disabled", disabled ? "on" : "off" );
    }

    public void setDisabledSearch(boolean disabled) {
        searchNode.setClientDataAttribute("disabled", disabled ? "on" : "off" );
        searchText.setDisabled(disabled);
        searchText.setValue("");
    }

    public void setDisabledAnimation(boolean disabled) {
        animate.setDisabled(disabled || !userOptions.getMainAttributeKey().equals(parent.getConfigData().getDefaultAttribute()));
    }

    public void toogleAnimateBtn(boolean state) {
        parent.toggleComponentSclass(animate, state, "ap-btn-anim-off", "ap-btn-anim-on");
        animate.setTooltiptext(state ? parent.getLabel("stopAnimation_text") : parent.getLabel("animate_text"));
    }

    public void setDisabledFilterClear(boolean disabled) {
        filterClear.setDisabled(disabled);
    }

    public void setDisabledModelExport(boolean disabled) {
        exportBPMN.setDisabled(disabled);
    }
    
    @Override
    public void updateUI(Object data) throws Exception {
        //
    }

    private void changeLayout(final String compId) throws Exception {
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
    
    private void onClearFilter() {
        Messagebox.show(
            parent.getLabel("filterClear_message"),
            parent.getLabel("filterClear_title"),
            Messagebox.OK | Messagebox.CANCEL,
            Messagebox.QUESTION,
            e -> proceedClearFilter(e)
        );
    }

    private void proceedClearFilter(Event evt) {
        if (evt.getName().equals("onOK")) {
            try {
                FilterAction action = new FilterActionOnClearFilter(parent, parent.getProcessAnalyst());
                action.setPreActionFilterCriteria(parent.getProcessAnalyst().copyCurrentFilterCriteria());
                parent.getActionManager().executeAction(action);
            } catch (Exception e) {
                Messagebox.show(
                    parent.getLabel("filterError_message"),
                    parent.getLabel("filterError_title"),
                    Messagebox.OK, Messagebox.ERROR
                );
            }
        }
    }

    private boolean isReadOnly() throws UserNotFoundException {
        ContextData contextData = parent.getContextData();
        AuthorizationService authorizationService = parent.getAuthorizationService();

        AccessType accessType = authorizationService.getLogAccessTypeByUser(contextData.getLogId(),
                contextData.getUsername());

        return !contextData.isEditEnabled() || AccessType.VIEWER == accessType || AccessType.RESTRICTED == accessType;
    }

}
