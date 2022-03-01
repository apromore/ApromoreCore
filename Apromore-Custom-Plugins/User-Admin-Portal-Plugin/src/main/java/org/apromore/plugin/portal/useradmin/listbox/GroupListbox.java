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

package org.apromore.plugin.portal.useradmin.listbox;

import java.util.Set;
import org.apromore.dao.model.Group;
import org.apromore.plugin.portal.useradmin.common.SearchableListbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;

public class GroupListbox extends SearchableListbox {

    public GroupListbox(Listbox listbox, ListModelList sourceListModel, String title) {
        super(listbox, sourceListModel, title);
    }

    @Override
    public String getValue(int index) {
        return ((Group) this.getSourceListModel().get(index)).getName().toLowerCase();
    }

    @Override
    public Set<Group> getSelection() {
        return (Set<Group>) getListModel().getSelection();
    }
}
