/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
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

package org.apromore.portal.dialogController.renderer;

import java.util.HashSet;

import org.apromore.plugin.property.RequestParameterType;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.dialogController.dto.VersionDetailType;
import org.apromore.portal.model.VersionSummaryType;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class VersionSummaryItemRenderer implements ListitemRenderer {

    private MainController mainController;


    public VersionSummaryItemRenderer(MainController main) {
        this.mainController = main;
    }

    /*
     * (non-Javadoc)
     * @see org.zkoss.zul.ListitemRenderer#render(org.zkoss.zul.Listitem, java.lang.Object, int)
     */
    @Override
    public void render(Listitem listItem, Object obj, int index) {
    	VersionDetailType versionDetailType=(VersionDetailType) obj;
    	if(versionDetailType.getProcess()!=null) {
    		renderVersionSummary(listItem, versionDetailType);
    	}else {
		   listItem.appendChild(wrapIntoListCell(new Label("-")));
		   if(versionDetailType.getVersion().getLastUpdate()!=null && !versionDetailType.getVersion().getLastUpdate().isEmpty()) 
		   {
			   listItem.appendChild(wrapIntoListCell(new Label(versionDetailType.getVersion().getLastUpdate())));
		   }else {
			   listItem.appendChild(wrapIntoListCell(new Label(versionDetailType.getVersion().getCreationDate())));
		   }
    	}
    }

    private void renderVersionSummary(final Listitem listItem, final VersionDetailType data) {
        listItem.appendChild(renderVersionVersion(data.getVersion()));
        listItem.appendChild(renderVersionLastUpdate(data.getVersion()));

        listItem.addEventListener(Events.ON_DOUBLE_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                mainController.editProcess2(data.getProcess(), data.getVersion(), getNativeType(data.getProcess().getOriginalNativeType()),
                    new HashSet<RequestParameterType<?>>(), false);
            }

            /* Sometimes we have merged models with no native type, we should give them a default so they can be edited. */
            private String getNativeType(String origNativeType) {
                String nativeType = origNativeType;
                if (origNativeType == null || origNativeType.isEmpty()) {
                    nativeType = "BPMN 2.0";
                }
                return nativeType;
            }
        });
    }

    private Component renderVersionVersion(VersionSummaryType version) {
        return wrapIntoListCell(new Label(version.getVersionNumber()));
    }

    private Component renderVersionLastUpdate(VersionSummaryType version) {
        if (version.getLastUpdate() == null || version.getLastUpdate().isEmpty()) {
            return wrapIntoListCell(new Label(version.getCreationDate()));
        } else {
            return wrapIntoListCell(new Label(version.getLastUpdate()));
        }
    }

    private Listcell wrapIntoListCell(Component cp) {
        Listcell lc = new Listcell();
        lc.appendChild(cp);
        return lc;
    }

}
