/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2016 Queensland University of Technology.
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

import org.apromore.plugin.Plugin;

import java.io.IOException;
import java.util.Locale;

/**
 * Plug-in interface for a Editor functionality
 */
public interface EditorPlugin extends Plugin {

    /**
     * Label of the plug-in, i.e., the name under which it's functionality is accessible
     *
     * @param locale (optional locale)
     * @return
     */
    String getLabel(Locale locale);
    

    /**
     * Provides the relative path from the application server root (e.g., "myplugin/js/source.js") to the JavaScript code
     * of this editor plug-in plug-in as plain String
     *
     * @return
     * @throws IOException
     */
    String getJavaScriptURI();

    /**
     * Provides the name space of the Signavio plug-in in the JavaScript.
     *
     * @return
     */
    String getJavaScriptPackage();

}
