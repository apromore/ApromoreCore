/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2016 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * Copyright (C) 2020, Apromore Pty Ltd.
 *
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

package org.apromore.portal.dialogController;

// Java 2 Standard packages
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Third party packages
import org.apromore.plugin.editor.EditorPlugin;
import org.apromore.portal.context.EditorPluginResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;

/**
 * The Signavio Controller. This controls opening the signavio editor in apromore.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class LogVisualizerApromoreController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogVisualizerApromoreController.class.getCanonicalName());

    public LogVisualizerApromoreController() {
        super();

        String id = Executions.getCurrent().getParameter("id");
        String data1 = (String) Executions.getCurrent().getSession().getAttribute("SIGNAVIO_SESSION" + id);

        try {
            this.setTitle("Dummy title");

            Map<String, Object> param = new HashMap<>();
            param.put("jsonData",     data1.replace("\n", " ").replace("'", "\\u0027").trim());
            param.put("url",          getURL("BPMN 2.0"));
            param.put("importPath",   getImportPath("BPMN 2.0"));
            param.put("editor",       config.getSiteEditor());
            param.put("doAutoLayout", "false");

            List<EditorPlugin> editorPlugins = EditorPluginResolver.resolve();
            param.put("plugins", editorPlugins);

            Executions.getCurrent().pushArg(param);

        } catch (Exception e) {
            LOGGER.error("",e);
            e.printStackTrace();
        }
    }
}
