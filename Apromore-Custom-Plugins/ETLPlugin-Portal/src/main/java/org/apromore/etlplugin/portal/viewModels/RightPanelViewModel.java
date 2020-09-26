/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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
package org.apromore.etlplugin.portal.viewModels;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.select.annotation.VariableResolver;

/**
 * ViewModel for the right panel.
 */
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class RightPanelViewModel {

    private Boolean joinTab;
    private Boolean transformTab;

    /**
     * Initialise.
     */
    @Init
    public void init() {
        joinTab = true;
        transformTab = false;
    }

    /**
     * When the table clicks.
     *
     * @param tabPanelId Id of the selected panel
     */
    @GlobalCommand
    @NotifyChange({"joinTab","transformTab"})
    public void newTabSelected(@BindingParam("tabPanelId")String tabPanelId) {
        if (tabPanelId.equals("dataJoin")) {
            joinTab = true;
            transformTab = false;
        } else {
            joinTab = false;
            transformTab = true;
        }
    }

    /**
     * Get join tab boolean.
     *
     * @return join tab visibility value
     */
    public Boolean getJoinTab() {
        return joinTab;
    }

    /**
     * Get transform tab boolean.
     *
     * @return transform tab visibility value
     */
    public Boolean getTransformTab() {
        return transformTab;
    }

}
