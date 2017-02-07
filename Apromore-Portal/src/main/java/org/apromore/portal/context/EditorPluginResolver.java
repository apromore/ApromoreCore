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

package org.apromore.portal.context;

import org.apromore.plugin.editor.EditorPlugin;
import org.zkoss.spring.SpringUtil;

import java.util.List;

/**
 * Looking up editor plug-ins
 */
public class EditorPluginResolver {

    public static List<EditorPlugin> resolve() {
        Object editorPlugins = SpringUtil.getBean("editorPlugins");
        if (editorPlugins != null) {
            return (List<EditorPlugin>) editorPlugins;
        } else {
            throw new RuntimeException("Could not get list of editor plug-ins!");
        }
    }

}
