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
package org.apromore.plugin.portal.file;

import java.util.Locale;
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.slf4j.Logger;

import org.apromore.portal.dialogController.ImportController;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.exception.DialogException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Messagebox;

public class UploadFilePlugin extends DefaultPortalPlugin {

    private static Logger LOGGER = PortalLoggerFactory.getLogger(UploadFilePlugin.class);

    private String label = "Upload";
    private String groupLabel = "File";
    private MainController mainC;

    // PortalPlugin overrides

    @Override
    public String getItemCode(Locale locale) { return label; }

    @Override
    public String getGroup(Locale locale) {
        return "File";
    }

    @Override
    public String getLabel(Locale locale) {
        return Labels.getLabel("plugin_file_upload_text",label);
    }

    @Override
    public String getGroupLabel(Locale locale) {
        return Labels.getLabel("plugin_file_title_text", groupLabel);
    }


    @Override
    public String getIconPath() {
        return "upload.svg";
    }

    @Override
    public void execute(PortalContext portalContext) {
        MainController mainC = (MainController) portalContext.getMainController();

        mainC.eraseMessage();
        try {
            new ImportController(mainC);

        } catch (DialogException e) {
            Messagebox.show(e.getMessage(), "Apromore", Messagebox.OK, Messagebox.ERROR);
        }
    }
}
