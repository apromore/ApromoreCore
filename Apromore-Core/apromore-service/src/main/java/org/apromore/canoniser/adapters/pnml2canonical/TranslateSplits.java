package org.apromore.canoniser.adapters.pnml2canonical;

import org.apromore.cpf.ANDSplitType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.MessageType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.TimerType;
import org.apromore.cpf.XORSplitType;
import org.apromore.pnml.TransitionType;

public class TranslateSplits {
    DataHandler data;
    long ids;

    public void setValues(DataHandler data, long ids) {
        this.data = data;
        this.ids = ids;
    }

    public void translateAndSplits(TransitionType tra) {
        ANDSplitType andsplit = new ANDSplitType();
        TaskType task = new TaskType();
        MessageType msg = new MessageType();
        TimerType time = new TimerType();
        EdgeType split = new EdgeType();
        EdgeType triggeredge = new EdgeType();
        TranslateTrigger tt = new TranslateTrigger();
        tt.setValues(data, ids);
        String trigger = tt.translateOperationTrigger(tra);
        if (trigger.equals("none") || trigger.equals("res")) {
            data.put_objectmap(String.valueOf(ids), task);
        }
        andsplit.setId(String.valueOf(ids++));
        if (tra.getName() != null) {
            andsplit.setName(tra.getName().getText());
        }
        if (trigger.equals("none")) {
            task.setId(String.valueOf(ids++));
            if (tra.getName() != null) {
                task.setName(tra.getName().getText());
            }
            split.setId(String.valueOf(ids++));
            split.setSourceId(task.getId());
        } else if (trigger.equals("res")) {
            task.setId(String.valueOf(ids++));
            if (tra.getName() != null) {
                task.setName(tra.getName().getText());
            }
            // task.getResourceTypeRef().add(new ResourceTypeRefType());
            split.setId(String.valueOf(ids++));
            split.setSourceId(task.getId());
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
            triggeredge.setSourceId(msg.getId());
            triggeredge.setTargetId(task.getId());
            split.setId(String.valueOf(ids++));
            split.setSourceId(task.getId());
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
            triggeredge.setSourceId(time.getId());
            triggeredge.setTargetId(task.getId());
            split.setId(String.valueOf(ids++));
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

    public void translateXorSplits(TransitionType tra) {
        XORSplitType xorsplit = new XORSplitType();
        TaskType task = new TaskType();
        MessageType msg = new MessageType();
        TimerType time = new TimerType();
        EdgeType split = new EdgeType();
        EdgeType triggeredge = new EdgeType();
        TranslateTrigger tt = new TranslateTrigger();
        tt.setValues(data, ids);
        String trigger = tt.translateOperationTrigger(tra);
        if (trigger.equals("none") || trigger.equals("res")) {
            data.put_objectmap(String.valueOf(ids), task);
        }
        xorsplit.setId(String.valueOf(ids++));
        if (tra.getName() != null) {
            xorsplit.setName(tra.getName().getText());
        }
        if (trigger.equals("none")) {
            task.setId(String.valueOf(ids++));
            if (tra.getName() != null) {
                task.setName(tra.getName().getText());
            }
            split.setId(String.valueOf(ids++));
            split.setSourceId(task.getId());
        } else if (trigger.equals("res")) {
            task.setId(String.valueOf(ids++));
            if (tra.getName() != null) {
                task.setName(tra.getName().getText());
            }
            // task.getResourceTypeRef().add(new ResourceTypeRefType());
            split.setId(String.valueOf(ids++));
            split.setSourceId(task.getId());
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
            triggeredge.setSourceId(msg.getId());
            triggeredge.setTargetId(task.getId());
            split.setId(String.valueOf(ids++));
            split.setSourceId(task.getId());
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
            triggeredge.setSourceId(time.getId());
            triggeredge.setTargetId(task.getId());
            split.setId(String.valueOf(ids++));
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

    public long getIds() {
        return ids;
    }
}
