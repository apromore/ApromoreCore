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
package org.apromore.plugin.portal.processdiscoverer.actions;

import java.util.Stack;

/**
 * Generic Action history for undo and redo
 */
public class ActionHistory {
    private Stack<Action> undoStack;
    private Stack<Action> redoStack;

    public ActionHistory() {
        undoStack = new Stack<Action>();
        redoStack = new Stack<Action>();
    }
    
    public boolean isUndoEmpty() {
        return undoStack.isEmpty();
    }

    public boolean isRedoEmpty() {
        return redoStack.isEmpty();
    }

    public void undoPush(Action action) {
        undoStack.push(action);
    }
    
    public Action undoPop() {
        return !undoStack.isEmpty() ? undoStack.pop() : null;
    }

    public void redoPush(Action action) {
        redoStack.push(action);
    }
    
    public Action redoPop() {
        return !redoStack.isEmpty() ? redoStack.pop() : null;
    }
    
    public void clearRedo() {
        redoStack.clear();
    }
}
