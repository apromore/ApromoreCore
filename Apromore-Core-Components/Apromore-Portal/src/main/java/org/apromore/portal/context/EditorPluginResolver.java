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

package org.apromore.portal.context;

import org.apromore.plugin.editor.EditorPlugin;
import org.zkoss.zkplus.spring.SpringUtil;

import java.util.List;

/**
 * Looking up editor plug-ins
 */
public class EditorPluginResolver {

    public static List<EditorPlugin> resolve(String beanId) {
        Object editorPlugins = SpringUtil.getBean(beanId);
        if (editorPlugins != null) {
            return (List<EditorPlugin>) editorPlugins;
        } else {
            throw new RuntimeException("Could not get list of editor plug-ins for beanId='" + beanId + "'");
        }
    }
    
    public static List<EditorPlugin> resolve() {
    	return EditorPluginResolver.resolve("editorPlugins");
    }

}
