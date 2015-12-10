/*
 * Copyright © 2009-2015 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.canoniser.pnml.internal.pnml2canonical;

import java.util.List;

import org.apromore.cpf.ResourceTypeRefType;
import org.apromore.cpf.TaskType;
import org.apromore.pnml.TransitionToolspecificType;
import org.apromore.pnml.TransitionType;

public abstract class TranslateTransition {

    static public void translateTask(TransitionType tran, DataHandler data) {
        data.put_id_map(tran.getId(), String.valueOf(data.getIds()));
        boolean translated = TranslateTrigger.translateTrigger(tran, data);

        if (!translated) {
            TaskType task = new TaskType();

            data.put_objectmap(String.valueOf(data.getIds()), task);
            task.setId(String.valueOf(data.nextId()));
            if (tran.getName() != null) {
                task.setName(tran.getName().getText());
            }
            task.setOriginalID(tran.getId());
            if (tran.getToolspecific() != null) {
                List<TransitionToolspecificType> pnmlTransitionToolspecific = tran
                        .getToolspecific();
                for (Object obj : pnmlTransitionToolspecific) {
                    if (obj instanceof TransitionToolspecificType) {

                        TransitionToolspecificType transitionToolspecific = (TransitionToolspecificType) obj;
                        if (transitionToolspecific.getTrigger() != null
                                && transitionToolspecific.getTrigger()
                                .getType() == 200) {
                            if (transitionToolspecific.getTransitionResource() == null) {
                                task.getResourceTypeRef().add(new ResourceTypeRefType());
                            }
                        }
                        if (transitionToolspecific.isSubprocess() != null) {
                            task.setSubnetId(task.getId());
                        }
                    }
                }
            }
            data.getNet().getNode().add(task);

        }
    }

}
