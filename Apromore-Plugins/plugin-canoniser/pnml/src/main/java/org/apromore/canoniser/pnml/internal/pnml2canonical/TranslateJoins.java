/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.canoniser.pnml.internal.pnml2canonical;

import org.apromore.cpf.ANDJoinType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.MessageType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.TimerType;
import org.apromore.cpf.XORJoinType;
import org.apromore.pnml.TransitionType;

public abstract class TranslateJoins {

    public static void translateAndJoins(TransitionType tra, DataHandler data) {
        ANDJoinType andjoin = new ANDJoinType();
        TaskType task = new TaskType();
        MessageType msg = new MessageType();
        TimerType time = new TimerType();
        EdgeType joinEdge = new EdgeType();
        EdgeType triggeredge = new EdgeType();
        String trigger = TranslateTrigger.translateOperationTrigger(tra);
        if (trigger.equals("none") || trigger.equals("res")) {
            data.put_objectmap(String.valueOf(data.getIds()), task);
        }
        andjoin.setId(String.valueOf(data.nextId()));
        if (tra.getName() != null) {
            andjoin.setName(tra.getName().getText());
        }
        switch (trigger) {
        case "none":
            task.setId(String.valueOf(data.nextId()));
            if (tra.getName() != null) {
                task.setName(tra.getName().getText());
            }
            joinEdge.setId(String.valueOf(data.nextId()));
            joinEdge.setTargetId(task.getId());
            break;

        case "res":
            task.setId(String.valueOf(data.nextId()));
            if (tra.getName() != null) {
                task.setName(tra.getName().getText());
            }
            // task.getResourceTypeRef().add(new ResourceTypeRefType());
            joinEdge.setId(String.valueOf(data.nextId()));
            joinEdge.setTargetId(task.getId());
            break;

        case "message":
            msg.setId(String.valueOf(data.nextId()));
            if (tra.getName() != null) {
                msg.setName(tra.getName().getText());
            }
            task.setId(String.valueOf(data.nextId()));
            if (tra.getName() != null) {
                task.setName(tra.getName().getText());
            }
            triggeredge.setId(String.valueOf(data.nextId()));
            triggeredge.setSourceId(task.getId());
            triggeredge.setTargetId(msg.getId());
            joinEdge.setId(String.valueOf(data.nextId()));
            joinEdge.setTargetId(task.getId());
            break;

        case "time":
            time.setId(String.valueOf(data.nextId()));
            if (tra.getName() != null) {
                time.setName(tra.getName().getText());
            }
            task.setId(String.valueOf(data.nextId()));
            if (tra.getName() != null) {
                task.setName(tra.getName().getText());
            }
            triggeredge.setId(String.valueOf(data.nextId()));
            triggeredge.setSourceId(task.getId());
            triggeredge.setTargetId(time.getId());
            joinEdge.setId(String.valueOf(data.nextId()));
            joinEdge.setTargetId(task.getId());
            break;
        }

        joinEdge.setSourceId(andjoin.getId());
        andjoin.setOriginalID(tra.getId());
        data.getNet().getNode().add(andjoin);
        data.getNet().getEdge().add(joinEdge);
        if (trigger.equals("none") || trigger.equals("res")) {
            data.getNet().getNode().add(task);
            data.put_andjoinmap(String.valueOf(tra.getId()), String.valueOf(task.getId()));
        } else if (trigger.equals("message")) {
            data.getNet().getNode().add(task);
            data.getNet().getEdge().add(triggeredge);
            data.getNet().getNode().add(msg);
            data.put_andjoinmap(String.valueOf(tra.getId()), String.valueOf(task.getId()));
            data.put_andjoinmap(String.valueOf(tra.getId()), String.valueOf(msg.getId()));
        } else if (trigger.equals("time")) {
            data.getNet().getNode().add(task);
            data.getNet().getEdge().add(triggeredge);
            data.getNet().getNode().add(time);
            data.put_andjoinmap(String.valueOf(tra.getId()), String.valueOf(task.getId()));
            data.put_andjoinmap(String.valueOf(tra.getId()), String.valueOf(time.getId()));
        }

    }

    public static void translateXorJoins(TransitionType tra, DataHandler data) {
        XORJoinType xorjoin = new XORJoinType();
        TaskType task = new TaskType();
        MessageType msg = new MessageType();
        TimerType time = new TimerType();
        EdgeType join = new EdgeType();
        EdgeType triggeredge = new EdgeType();
        //TranslateTrigger tt = new TranslateTrigger();
        //tt.setValues(data, ids);
        String trigger = TranslateTrigger.translateOperationTrigger(tra);
        if (trigger.equals("none") || trigger.equals("res")) {
            data.put_objectmap(String.valueOf(data.getIds()), task);
        }
        xorjoin.setId(String.valueOf(data.nextId()));
        if (tra.getName() != null) {
            xorjoin.setName(tra.getName().getText());
        }
        if (trigger.equals("none")) {
            task.setId(String.valueOf(data.nextId()));
            if (tra.getName() != null) {
                task.setName(tra.getName().getText());
            }
            join.setId(String.valueOf(data.nextId()));
            join.setTargetId(task.getId());
        } else if (trigger.equals("res")) {
            task.setId(String.valueOf(data.nextId()));
            if (tra.getName() != null) {
                task.setName(tra.getName().getText());
            }
            // task.getResourceTypeRef().add(new ResourceTypeRefType());
            join.setId(String.valueOf(data.nextId()));
            join.setTargetId(task.getId());
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
            triggeredge.setSourceId(task.getId());
            triggeredge.setTargetId(msg.getId());
            join.setId(String.valueOf(data.nextId()));
            join.setTargetId(task.getId());
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
            triggeredge.setSourceId(task.getId());
            triggeredge.setTargetId(time.getId());
            join.setId(String.valueOf(data.nextId()));
            join.setTargetId(task.getId());
        }

        join.setSourceId(xorjoin.getId());
        xorjoin.setOriginalID(tra.getId());
        data.getNet().getNode().add(xorjoin);
        data.getNet().getEdge().add(join);
        if (trigger.equals("none") || trigger.equals("res")) {
            data.getNet().getNode().add(task);
            data.put_andjoinmap(String.valueOf(tra.getId()), String.valueOf(task.getId()));
        } else if (trigger.equals("message")) {
            data.getNet().getNode().add(task);
            data.getNet().getEdge().add(triggeredge);
            data.getNet().getNode().add(msg);
            data.put_andjoinmap(String.valueOf(tra.getId()), String.valueOf(task.getId()));
            data.put_andjoinmap(String.valueOf(tra.getId()), String.valueOf(msg.getId()));
        } else if (trigger.equals("time")) {
            data.getNet().getNode().add(task);
            data.getNet().getEdge().add(triggeredge);
            data.getNet().getNode().add(time);
            data.put_andjoinmap(String.valueOf(tra.getId()), String.valueOf(task.getId()));
            data.put_andjoinmap(String.valueOf(tra.getId()), String.valueOf(time.getId()));
        }
    }

}
