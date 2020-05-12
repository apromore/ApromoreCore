/*-
 * #%L
 * This file is part of "Apromore Core".
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

package org.apromore.plugin.portal.processdiscoverer.plugins;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.apromore.logman.attribute.graph.MeasureType;
import org.apromore.model.EditSessionType;
import org.apromore.model.LogSummaryType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.SummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.processdiscoverer.impl.factory.PDCustomFactory;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.dialogController.dto.ApromoreSession;
import org.zkoss.zul.Messagebox;

public class PDAbstractPlugin extends DefaultPortalPlugin {

    private String label = "Discover process map / BPMN model";
    private String groupLabel = "Discover";
    protected String sessionId = "";
    
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
    public void execute(PortalContext context) {
    	
    }

    protected boolean prepare(PortalContext context, MeasureType visType) {
        try {
            Map<SummaryType, List<VersionSummaryType>> elements = context.getSelection().getSelectedProcessModelVersions();
            if (elements.size() != 1) {
                Messagebox.show("Please select exactly one log!", "Wrong Log Selection", Messagebox.OK, Messagebox.INFORMATION);
                return false;
            }
            SummaryType selection = elements.keySet().iterator().next();
            if (!(selection instanceof LogSummaryType)) {
            	Messagebox.show("Please select a log!", "Wrong Selection", Messagebox.OK, Messagebox.INFORMATION);
                return false;
            }
        	
        	String username = context.getCurrentUser().getUsername();
            ProcessSummaryType process = new ProcessSummaryType();
            process.setDomain("Process Discoverer");
            process.setName("Process Discoverer");
            process.setId(1);
            process.setMakePublic(true);
            VersionSummaryType version = new VersionSummaryType();
            version.setName("Process Discoverer");
            version.setVersionNumber("1.0");
            
            EditSessionType editSession1 = createEditSession(username, process, version);
            ApromoreSession session = new ApromoreSession(editSession1, null,
            												(MainController)context.getMainController(), 
            												process, version, null, null, 
            												new HashSet<RequestParameterType<?>>());
            session.put("context", context);
            session.put("visType", visType);
            session.put("selection", selection);
            session.put("pdFactory", new PDCustomFactory());
            
            sessionId = UUID.randomUUID().toString();
            UserSessionManager.setEditSession(sessionId, session);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return true;

    }
    
    public String getSessionId() {
    	return sessionId;
    }
    
    private static EditSessionType createEditSession(final String username, final ProcessSummaryType process, final VersionSummaryType version) {
        EditSessionType editSession = new EditSessionType();
        editSession.setDomain(process.getDomain());
        editSession.setNativeType("BPMN 2.0");
        editSession.setProcessId(process.getId());
        editSession.setProcessName(process.getName());
        editSession.setUsername(username);
        editSession.setPublicModel(process.isMakePublic());
        editSession.setOriginalBranchName(version.getName());
        editSession.setOriginalVersionNumber(version.getVersionNumber());
        editSession.setCurrentVersionNumber(version.getVersionNumber());
        editSession.setMaxVersionNumber(process.getLastVersion());
        editSession.setCreationDate(version.getCreationDate());
        editSession.setLastUpdate(version.getLastUpdate());
        editSession.setWithAnnotation(false);

        return editSession;
    }
}
