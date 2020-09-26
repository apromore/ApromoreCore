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
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zul.Window;

import java.util.HashMap;

/**
 * Model for the expand window.
 */
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class InputExpandedExcerptViewModel {

    HashMap<String, String> map = new HashMap<>();

    /**
     * Initialise.
     */
    @Init
    public void init() {

    }

    /**
     * Create a modal to add rules to a given column.
     * @param filename the column the rules will be added to
     */
    @Command
    public void openExpandedWindow(@BindingParam("filename") String filename) {
        map.put("filename", filename);
        Window inputExpandedModal = (Window) Executions.createComponents(
                "/views/input-expanded-modal.zul", null, map);
        inputExpandedModal.setMode("modal");
        inputExpandedModal.doModal();
    }

}
