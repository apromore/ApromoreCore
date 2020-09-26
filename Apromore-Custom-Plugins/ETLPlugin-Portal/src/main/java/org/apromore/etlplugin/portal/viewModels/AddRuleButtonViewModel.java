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

import org.apromore.etlplugin.portal.models.templateTableModel.Column;
import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zul.Window;

import java.util.HashMap;

/**
 * A view model for an add rule button.
 * @author janeh
 */
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class AddRuleButtonViewModel {
    HashMap<String, Column> map = new HashMap<>();

    /**
     * Initialize the view model.
     * @param view the context view
     */
    @Init
    public void init(@ContextParam(ContextType.VIEW) Component view) {
        Selectors.wireComponents(view, this, false);
    }

    /**
     * Create a modal to add rules to a given column.
     * @param colName the column the rules will be added to
     */
    @Command
    public void openWindow(@BindingParam("colName") Column colName) {
        map.put("colName", colName);
        Window addRulesModal = (Window) Executions.createComponents(
                "/views/add-rules-modal.zul", null, map);
        addRulesModal.setMode("modal");
        addRulesModal.doModal();
    }

}
