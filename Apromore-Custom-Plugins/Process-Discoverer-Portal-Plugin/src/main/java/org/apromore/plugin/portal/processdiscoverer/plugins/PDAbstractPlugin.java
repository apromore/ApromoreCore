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

package org.apromore.plugin.portal.processdiscoverer.plugins;

import lombok.NonNull;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.logman.attribute.graph.MeasureType;
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.processdiscoverer.PDController;
import org.apromore.plugin.portal.processdiscoverer.impl.factory.PDCustomFactory;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.dialogController.dto.ApromoreSession;
import org.apromore.portal.model.EditSessionType;
import org.apromore.portal.model.LogSummaryType;
import org.apromore.portal.model.ProcessSummaryType;
import org.apromore.portal.model.SummaryType;
import org.apromore.portal.model.VersionSummaryType;
import org.zkoss.web.Attributes;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Messagebox;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;

public class PDAbstractPlugin extends DefaultPortalPlugin {

    private String label = "Discover process map / BPMN model";
    protected String sessionId = "";
    
    @Override
    public String getLabel(Locale locale) {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public void execute(PortalContext context) {
    	
    }
    
    @Override
    public String getIconPath() {
        return "discover_model.svg";
    }

    public ResourceBundle getLabels() {
        return ResourceBundle.getBundle("pd",
            (Locale) Sessions.getCurrent().getAttribute(Attributes.PREFERRED_LOCALE),
            PDController.class.getClassLoader());
    }

    public String getLabel(String key) {
        String label = getLabels().getString(key);
        if (label == null) {
            label = "";
        }
        return label;
    }

    protected boolean preparePluginSession(@NonNull PortalContext context,
                                           @NonNull MeasureType visType,
                                           @NonNull List<LogFilterRule> logFilters) {
        try {
            Map<SummaryType, List<VersionSummaryType>> elements = context.getSelection().getSelectedProcessModelVersions();
            if (elements.size() != 1) {
                Messagebox.show(getLabel("selectOneLog_message"), "Apromore", Messagebox.OK, Messagebox.INFORMATION);
                return false;
            }
            SummaryType selection = elements.keySet().iterator().next();
            if (!(selection instanceof LogSummaryType)) {
            	Messagebox.show(getLabel("selectALog_message"), "Apromore", Messagebox.OK, Messagebox.INFORMATION);
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
            if (!logFilters.isEmpty()) session.put("logFilters", logFilters);
            
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
