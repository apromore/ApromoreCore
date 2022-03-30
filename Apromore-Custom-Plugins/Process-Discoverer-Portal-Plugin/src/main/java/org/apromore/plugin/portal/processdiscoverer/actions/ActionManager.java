/*-
 * #%L
 * This file is part of "Apromore Enterprise Edition".
 * %%
 * Copyright (C) 2019 - 2022 Apromore Pty Ltd. All Rights Reserved.
 * %%
 * NOTICE:  All information contained herein is, and remains the
 * property of Apromore Pty Ltd and its suppliers, if any.
 * The intellectual and technical concepts contained herein are
 * proprietary to Apromore Pty Ltd and its suppliers and may
 * be covered by U.S. and Foreign Patents, patents in process,
 * and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this
 * material is strictly forbidden unless prior written permission
 * is obtained from Apromore Pty Ltd.
 * #L%
 */
package org.apromore.plugin.portal.processdiscoverer.actions;

import org.apromore.plugin.portal.processdiscoverer.PDController;
import org.zkoss.zul.Messagebox;

public class ActionManager {
    private PDController pdController;
    private ActionHistory actionHistory = new ActionHistory();
    
    public ActionManager(PDController pdController) {
        this.pdController = pdController;
    }
    
    /** For real action that perform changes */
    public void executeAction(Action action) {
        if (action.execute()) {
            actionHistory.undoPush(action);
            // Redo actions are those actions that have been undoed.
            // When an action is redoed (re-executed), it assumes that the undo stack is the
            // same as
            // before it is pushed to undo
            // Thus, whenever a NEW action is pushed to the undo stack, all current redoable
            // actions must
            // be clear to ensure consistent state.
            actionHistory.clearRedo();
            if (action instanceof FilterAction)
                pdController.updateUI(false);
        }
    }

    /**
     * For actions that don't change anything but need to be bundled for undo/redo
     * Some actions fall into this category, such as do filtering via opening a
     * LogFilter window These actions can't be executed directly via executeAction()
     * method, but they can be stored to support undo/redo
     */
    public void storeAction(Action action) {
        actionHistory.undoPush(action);
        if (action instanceof FilterAction)
            pdController.updateUI(false);
    }

    public void undoAction() {
        Action action = actionHistory.undoPop();
        if (action != null) {
            try {
                action.undo();
            } catch (Exception e) {
                // LOGGER.error("Error when undoing filter action. Error message: " +
                // e.getMessage());
                Messagebox.show(pdController.getLabel("undoError_message"));
            }
            actionHistory.redoPush(action);
            if (action instanceof FilterAction)
                pdController.updateUI(false);
        }
    }

    // Re-execute
    public void redoAction() {
        Action action = actionHistory.redoPop();
        if (action != null) {
            if (action.execute()) {
                actionHistory.undoPush(action);
                if (action instanceof FilterAction)
                    pdController.updateUI(false);
            }
        }
    }
    
    public boolean canUndo() {
        return !actionHistory.isUndoEmpty();
    }
    
    public boolean canRedo() {
        return !actionHistory.isRedoEmpty();
    }
}
