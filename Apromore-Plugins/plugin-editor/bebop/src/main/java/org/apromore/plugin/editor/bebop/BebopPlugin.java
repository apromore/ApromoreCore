/*
 * Copyright © 2009-2017 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.plugin.editor.bebop;

import org.apromore.plugin.editor.DefaultEditorPlugin;
import org.springframework.stereotype.Component;

/**
 * Example for a Apromore Editor plug-in that provide two functionalities:
 *  - A custom servlet
 *  - A custom JavaScript file
 */
@Component("plugin")
public class BebopPlugin extends DefaultEditorPlugin {

    @Override
    public String getJavaScriptURI() {
        return "/bebop/bebop.js"; //TODO automatically get root dir via BundleContext
    }

    @Override
    public String getJavaScriptPackage() {
        return "ORYX.Plugins.Bebop";
    }

}
