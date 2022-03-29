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

package org.apromore.plugin.portal.processdiscoverer.eventlisteners;

import org.apromore.plugin.portal.processdiscoverer.InteractiveMode;
import org.apromore.plugin.portal.processdiscoverer.PDController;
import org.apromore.plugin.portal.processdiscoverer.components.AbstractController;
import org.zkoss.zk.ui.event.Event;

public class AnimationController extends AbstractController {
    public AnimationController(PDController controller) {
        super(controller);
    }
    
    @Override
    public void onEvent(Event event) throws Exception {
        if (!parent.prepareCriticalServices()) {
            return;
        }
        
        // Toggle between model and animation views
        if (parent.getInteractiveMode() == InteractiveMode.MODEL_MODE) {
            parent.switchToAnimationView();
        }
        else {
            parent.switchToModelView();
        }
    }
}
