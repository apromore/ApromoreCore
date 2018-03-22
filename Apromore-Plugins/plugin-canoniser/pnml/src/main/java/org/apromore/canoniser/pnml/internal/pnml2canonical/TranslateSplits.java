/*
 * Copyright © 2009-2018 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.canoniser.pnml.internal.pnml2canonical;

import org.apromore.cpf.ANDSplitType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.MessageType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.TimerType;
import org.apromore.cpf.XORSplitType;
import org.apromore.pnml.TransitionType;

public abstract class TranslateSplits {

    public static void translateAndSplits(TransitionType tra, DataHandler data) {
        ANDSplitType andsplit = new ANDSplitType();
        TaskType task = new TaskType();
        MessageType msg = new MessageType();
        TimerType time = new TimerType();
        EdgeType split = new EdgeType();
        EdgeType triggeredge = new EdgeType();

        String trigger = TranslateTrigger.translateOperationTrigger(tra);
        if (trigger.equals("none") || trigger.equals("res")) {
            data.put_objectmap(String.valueOf(data.getIds()), task);
        }
        andsplit.setId(String.valueOf(data.nextId()));
        if (tra.getName() != null) {
            andsplit.setName(tra.getName().getText());
        }
        if (trigger.equals("none")) {
            task.setId(String.valueOf(data.nextId()));
            if (tra.getName() != null) {
                task.setName(tra.getName().getText());
            }
            split.setId(String.valueOf(data.nextId()));
            split.setSourceId(task.getId());
        } else if (trigger.equals("res")) {
            task.setId(String.valueOf(data.nextId()));
            if (tra.getName() != null) {
                task.setName(tra.getName().getText());
            }
            // task.getResourceTypeRef().add(new ResourceTypeRefType());
            split.setId(String.valueOf(data.nextId()));
            split.setSourceId(task.getId());
        } else if (trigger.equals("message")) {
            msg.setId(String.valueOf(data.nextId()));
            if (tra.getName() != null) {
                msg.setName(tra.getName().getText());
            }
            task.setId(String.valueOf(data.nextId()));
            if (tra.getName() != null) {
                task.setName(tra.getName().getText());
            }
            triggeredge.setId(String.valueOf(data.nextId()));
            triggeredge.setSourceId(msg.getId());
            triggeredge.setTargetId(task.getId());
            split.setId(String.valueOf(data.nextId()));
            split.setSourceId(task.getId());
        } else if (trigger.equals("time")) {
            time.setId(String.valueOf(data.nextId()));
            if (tra.getName() != null) {
                time.setName(tra.getName().getText());
            }
            task.setId(String.valueOf(data.nextId()));
            if (tra.getName() != null) {
                task.setName(tra.getName().getText());
            }
            triggeredge.setId(String.valueOf(data.nextId()));
            triggeredge.setSourceId(time.getId());
            triggeredge.setTargetId(task.getId());
            split.setId(String.valueOf(data.nextId()));
            split.setSourceId(task.getId());
        }

        split.setTargetId(andsplit.getId());
        if (trigger.equals("none") || trigger.equals("res")) {
            data.getNet().getNode().add(task);
            data.put_andsplitmap(String.valueOf(tra.getId()),
                    String.valueOf(task.getId()));
        } else if (trigger.equals("message")) {
            data.getNet().getNode().add(msg);
            data.getNet().getEdge().add(triggeredge);
            data.getNet().getNode().add(task);
            data.put_andsplitmap(String.valueOf(tra.getId()),
                    String.valueOf(task.getId()));
            data.put_andsplitmap(String.valueOf(tra.getId()),
                    String.valueOf(msg.getId()));
        } else if (trigger.equals("time")) {
            data.getNet().getNode().add(time);
            data.getNet().getEdge().add(triggeredge);
            data.getNet().getNode().add(task);
            data.put_andsplitmap(String.valueOf(tra.getId()),
                    String.valueOf(task.getId()));
            data.put_andsplitmap(String.valueOf(tra.getId()),
                    String.valueOf(time.getId()));
        }
        data.getNet().getEdge().add(split);
        andsplit.setOriginalID(tra.getId());
        data.getNet().getNode().add(andsplit);

    }

    public static void translateXorSplits(TransitionType tra, DataHandler data) {
        XORSplitType xorsplit = new XORSplitType();
        TaskType task = new TaskType();
        MessageType msg = new MessageType();
        TimerType time = new TimerType();
        EdgeType split = new EdgeType();
        EdgeType triggeredge = new EdgeType();
        //TranslateTrigger tt = new TranslateTrigger();
        //tt.setValues(data, ids);
        String trigger = TranslateTrigger.translateOperationTrigger(tra);
        if (trigger.equals("none") || trigger.equals("res")) {
            data.put_objectmap(String.valueOf(data.getIds()), task);
        }
        xorsplit.setId(String.valueOf(data.nextId()));
        if (tra.getName() != null) {
            xorsplit.setName(tra.getName().getText());
        }
        if (trigger.equals("none")) {
            task.setId(String.valueOf(data.nextId()));
            if (tra.getName() != null) {
                task.setName(tra.getName().getText());
            }
            split.setId(String.valueOf(data.nextId()));
            split.setSourceId(task.getId());
        } else if (trigger.equals("res")) {
            task.setId(String.valueOf(data.nextId()));
            if (tra.getName() != null) {
                task.setName(tra.getName().getText());
            }
            // task.getResourceTypeRef().add(new ResourceTypeRefType());
            split.setId(String.valueOf(data.nextId()));
            split.setSourceId(task.getId());
        } else if (trigger.equals("message")) {
            msg.setId(String.valueOf(data.nextId()));
            if (tra.getName() != null) {
                msg.setName(tra.getName().getText());
            }
            task.setId(String.valueOf(data.nextId()));
            if (tra.getName() != null) {
                task.setName(tra.getName().getText());
            }
            triggeredge.setId(String.valueOf(data.nextId()));
            triggeredge.setSourceId(msg.getId());
            triggeredge.setTargetId(task.getId());
            split.setId(String.valueOf(data.nextId()));
            split.setSourceId(task.getId());
        } else if (trigger.equals("time")) {
            time.setId(String.valueOf(data.nextId()));
            if (tra.getName() != null) {
                time.setName(tra.getName().getText());
            }
            task.setId(String.valueOf(data.nextId()));
            if (tra.getName() != null) {
                task.setName(tra.getName().getText());
            }
            triggeredge.setId(String.valueOf(data.nextId()));
            triggeredge.setSourceId(time.getId());
            triggeredge.setTargetId(task.getId());
            split.setId(String.valueOf(data.nextId()));
            split.setSourceId(task.getId());
        }

        split.setTargetId(xorsplit.getId());
        if (trigger.equals("none") || trigger.equals("res")) {
            data.getNet().getNode().add(task);
            data.put_andsplitmap(String.valueOf(tra.getId()),
                    String.valueOf(task.getId()));
        } else if (trigger.equals("message")) {
            data.getNet().getNode().add(msg);
            data.getNet().getEdge().add(triggeredge);
            data.getNet().getNode().add(task);
            data.put_andsplitmap(String.valueOf(tra.getId()),
                    String.valueOf(task.getId()));
            data.put_andsplitmap(String.valueOf(tra.getId()),
                    String.valueOf(msg.getId()));
        } else if (trigger.equals("time")) {
            data.getNet().getNode().add(time);
            data.getNet().getEdge().add(triggeredge);
            data.getNet().getNode().add(task);
            data.put_andsplitmap(String.valueOf(tra.getId()),
                    String.valueOf(task.getId()));
            data.put_andsplitmap(String.valueOf(tra.getId()),
                    String.valueOf(time.getId()));
        }
        data.getNet().getEdge().add(split);
        xorsplit.setOriginalID(tra.getId());
        data.getNet().getNode().add(xorsplit);

    }

}
