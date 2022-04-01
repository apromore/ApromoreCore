/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2011 - 2017 Queensland University of Technology.
 * Copyright (C) 2012 Felix Mannhardt.
 * Copyright (C) 2015 Adriano Augusto.
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

package org.apromore.portal.dialogController;

import org.apromore.portal.common.Constants;
import org.apromore.portal.common.UserSessionManager;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zul.Menubar;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class MenuController extends BaseMenuController {

    @Override
    public void doAfterCompose(Menubar menubar) {
        super.doAfterCompose(menubar);
        // Recreate the menubar when the authenticated user changes
        EventQueues.lookup(Constants.EVENT_QUEUE_REFRESH_SCREEN, EventQueues.SESSION, true)
                .subscribe(event -> {
                    if (Constants.EVENT_QUEUE_SESSION_ATTRIBUTES.equals(event.getName())) {
                        if (UserSessionManager.USER.equals(event.getData())) {
                            loadMenu(menubar, "main-menu");
                        }
                    }
                });
        loadMenu(menubar,"main-menu");
    }

}
