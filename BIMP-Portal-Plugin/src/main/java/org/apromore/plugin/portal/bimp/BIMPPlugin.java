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

package org.apromore.plugin.portal.bimp;

// Java 2 Standard Edition
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

// Java 2 Enterprise Edition
import javax.inject.Inject;

// Third party packages
import org.springframework.stereotype.Component;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Messagebox;

// Local packages
import org.apromore.exception.RepositoryException;
import org.apromore.helper.Version;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.SummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.service.ProcessService;

@Component("plugin")
public class BIMPPlugin extends DefaultPortalPlugin {

    private String label = "Simulate with BIMP";
    private String groupLabel = "Analyze";

    @Inject private ProcessService processService;

    @Override
    public String getLabel(Locale locale) {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String getGroupLabel(Locale locale) {
        return groupLabel;
    }

    public void setGroupLabel(String groupLabel) {
        this.groupLabel = groupLabel;
    }

    @Override
    public void execute(PortalContext portalContext) {

        Map<SummaryType, List<VersionSummaryType>> elements = portalContext.getSelection().getSelectedProcessModelVersions();

        if (elements.entrySet().size() != 1) {
            Messagebox.show("Select exactly one BPMN model", "Attention", Messagebox.OK, Messagebox.ERROR);
            return;
        }

        Map.Entry<SummaryType, List<VersionSummaryType>> entry = elements.entrySet().iterator().next();

        if (!(entry.getKey() instanceof ProcessSummaryType) || entry.getValue().size() != 1) {
            Messagebox.show("Select exactly one BPMN model", "Attention", Messagebox.OK, Messagebox.ERROR);
            return;
        }

        ProcessSummaryType process = (ProcessSummaryType) entry.getKey();
        VersionSummaryType vst = entry.getValue().get(0);

        // Fetch the BPMN serialization of the model
        int procID = process.getId();
        String procName = process.getName();
        String branch = vst.getName();
        Version version = new Version(vst.getVersionNumber());
        try {
            String bpmn = processService.getBPMNRepresentation(procName, procID, branch, version);
            
            // Go to the BIMP page
            Clients.evalJavaScript(
                "var form = document.createElement('form'); " +
                "form.action = 'http://bimp.cs.ut.ee/simulator'; " +
                "form.method = 'POST'; " +

                "var input = document.createElement('input'); " +
                "input.type = 'text'; " +
                "input.name = 'file'; " +
                "input.value = '" + bpmn.replaceAll("'", "\\' ").replaceAll("\n", " ") + "'; " +
                "form.appendChild(input); " +

                "form.submit();");

        } catch (RepositoryException e) {
            Messagebox.show("Unable to read " + procName, "Attention", Messagebox.OK, Messagebox.ERROR);
            e.printStackTrace();
        }
    }
}
