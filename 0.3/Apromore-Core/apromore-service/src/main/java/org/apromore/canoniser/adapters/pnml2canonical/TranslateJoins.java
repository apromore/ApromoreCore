package org.apromore.canoniser.adapters.pnml2canonical;

import org.apromore.cpf.ANDJoinType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.MessageType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.TimerType;
import org.apromore.cpf.XORJoinType;
import org.apromore.pnml.TransitionType;

public class TranslateJoins {
    DataHandler data;
    long ids;

    public void setValues(DataHandler data, long ids) {
        this.data = data;
        this.ids = ids;
    }

    public void translateAndJoins(TransitionType tra) {
        ANDJoinType andjoin = new ANDJoinType();
        TaskType task = new TaskType();
        MessageType msg = new MessageType();
        TimerType time = new TimerType();
        EdgeType join = new EdgeType();
        EdgeType triggeredge = new EdgeType();
        TranslateTrigger tt = new TranslateTrigger();
        tt.setValues(data, ids);
        String trigger = tt.translateOperationTrigger(tra);
        if (trigger.equals("none") || trigger.equals("res")) {
            data.put_objectmap(String.valueOf(ids), task);
        }
        andjoin.setId(String.valueOf(ids++));
        if (tra.getName() != null) {
            andjoin.setName(tra.getName().getText());
        }
        if (trigger.equals("none")) {
            task.setId(String.valueOf(ids++));
            if (tra.getName() != null) {
                task.setName(tra.getName().getText());
            }
            join.setId(String.valueOf(ids++));
            join.setTargetId(task.getId());
        } else if (trigger.equals("res")) {
            task.setId(String.valueOf(ids++));
            if (tra.getName() != null) {
                task.setName(tra.getName().getText());
            }
            // task.getResourceTypeRef().add(new ResourceTypeRefType());
            join.setId(String.valueOf(ids++));
            join.setTargetId(task.getId());
        } else if (trigger.equals("message")) {
            msg.setId(String.valueOf(ids++));
            if (tra.getName() != null) {
                msg.setName(tra.getName().getText());
            }
            task.setId(String.valueOf(ids++));
            if (tra.getName() != null) {
                task.setName(tra.getName().getText());
            }
            triggeredge.setId(String.valueOf(ids++));
            triggeredge.setSourceId(task.getId());
            triggeredge.setTargetId(msg.getId());
            join.setId(String.valueOf(ids++));
            join.setTargetId(task.getId());
        } else if (trigger.equals("time")) {
            time.setId(String.valueOf(ids++));
            if (tra.getName() != null) {
                time.setName(tra.getName().getText());
            }
            task.setId(String.valueOf(ids++));
            if (tra.getName() != null) {
                task.setName(tra.getName().getText());
            }
            triggeredge.setId(String.valueOf(ids++));
            triggeredge.setSourceId(task.getId());
            triggeredge.setTargetId(time.getId());
            join.setId(String.valueOf(ids++));
            join.setTargetId(task.getId());
        }

        join.setSourceId(andjoin.getId());
        andjoin.setOriginalID(tra.getId());
        data.getNet().getNode().add(andjoin);
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

    public void translateXorJoins(TransitionType tra) {
        XORJoinType xorjoin = new XORJoinType();
        TaskType task = new TaskType();
        MessageType msg = new MessageType();
        TimerType time = new TimerType();
        EdgeType join = new EdgeType();
        EdgeType triggeredge = new EdgeType();
        TranslateTrigger tt = new TranslateTrigger();
        tt.setValues(data, ids);
        String trigger = tt.translateOperationTrigger(tra);
        if (trigger.equals("none") || trigger.equals("res")) {
            data.put_objectmap(String.valueOf(ids), task);
        }
        xorjoin.setId(String.valueOf(ids++));
        if (tra.getName() != null) {
            xorjoin.setName(tra.getName().getText());
        }
        if (trigger.equals("none")) {
            task.setId(String.valueOf(ids++));
            if (tra.getName() != null) {
                task.setName(tra.getName().getText());
            }
            join.setId(String.valueOf(ids++));
            join.setTargetId(task.getId());
        } else if (trigger.equals("res")) {
            task.setId(String.valueOf(ids++));
            if (tra.getName() != null) {
                task.setName(tra.getName().getText());
            }
            // task.getResourceTypeRef().add(new ResourceTypeRefType());
            join.setId(String.valueOf(ids++));
            join.setTargetId(task.getId());
        } else if (trigger.equals("message")) {
            msg.setId(String.valueOf(ids++));
            if (tra.getName() != null) {
                msg.setName(tra.getName().getText());
            }
            task.setId(String.valueOf(ids++));
            if (tra.getName() != null) {
                task.setName(tra.getName().getText());
            }
            triggeredge.setId(String.valueOf(ids++));
            triggeredge.setSourceId(task.getId());
            triggeredge.setTargetId(msg.getId());
            join.setId(String.valueOf(ids++));
            join.setTargetId(task.getId());
        } else if (trigger.equals("time")) {
            time.setId(String.valueOf(ids++));
            if (tra.getName() != null) {
                time.setName(tra.getName().getText());
            }
            task.setId(String.valueOf(ids++));
            if (tra.getName() != null) {
                task.setName(tra.getName().getText());
            }
            triggeredge.setId(String.valueOf(ids++));
            triggeredge.setSourceId(task.getId());
            triggeredge.setTargetId(time.getId());
            join.setId(String.valueOf(ids++));
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

    public long getIds() {
        return ids;
    }

}
