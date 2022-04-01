/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2016 - 2017 Queensland University of Technology.
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

package org.apromore.plugin.editor;

import org.apromore.manager.client.ManagerService;
import org.apromore.plugin.DefaultPlugin;
import org.apromore.plugin.editor.EditorPlugin;

import java.util.Locale;

/**
 * Default implementation for a Editor plug-in. Subclass this class to create a new Editor plug-in rather than implementing the interface directly.
 * Override all methods that you want to customize. At least you should provide the URI to the JS file of the editor plug-in.
 */
public class DefaultEditorPlugin extends DefaultPlugin implements EditorPlugin {
	protected String editorCode = "signavio"; //no changes are needed for existing Signavio plugins

    @Override
    public String getLabel(Locale locale) {
        return "default";
    }

    @Override
    public String getJavaScriptURI() {
        return "";
    }

    @Override
    public String getJavaScriptPackage() {
        return "";
    }

}
