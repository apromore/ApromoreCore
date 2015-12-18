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

package org.apromore.canoniser.pnml.internal.canonical2pnml;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.apromore.cpf.MessageType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ResourceTypeRefType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.TimerType;
import org.apromore.pnml.ArcType;
import org.apromore.pnml.DimensionType;
import org.apromore.pnml.GraphicsNodeType;
import org.apromore.pnml.GraphicsSimpleType;
import org.apromore.pnml.NodeNameType;
import org.apromore.pnml.OrganizationUnitType;
import org.apromore.pnml.PlaceType;
import org.apromore.pnml.PositionType;
import org.apromore.pnml.RoleType;
import org.apromore.pnml.TransitionResourceType;
import org.apromore.pnml.TransitionToolspecificType;
import org.apromore.pnml.TransitionType;
import org.apromore.pnml.TriggerType;

public class TranslateNode {

    DataHandler data;
    long ids;

    public void setValues(DataHandler data, long ids) {
        this.data = data;
        this.ids = ids;
    }

    public void translateTask(TaskType task) {
        TransitionToolspecificType trantool = new TransitionToolspecificType();
        TransitionToolspecificType ttt = new TransitionToolspecificType();
        TransitionType tran = new TransitionType();
        boolean isSub = false;
        data.put_id_map(task.getId(), String.valueOf(ids));
        tran.setId(String.valueOf(ids++));
        tran.setGraphics(newGraphicsNodeType(dummyPosition(), transitionDefaultDimension()));

        if (task.getName() != null) {
            NodeNameType test = new NodeNameType();
            test.setText(task.getName());
            tran.setName(test);
        }

        if (task.getSubnetId() != null) {
            isSub = true;
            data.setSubnet(tran);
        }

        if (data.get_triggermap().containsKey(tran.getName().getText()) && data.get_triggermap_value(tran.getName().getText()) instanceof TransitionToolspecificType.Trigger) {
            trantool.setTool("WoPeD");
            trantool.setVersion("1.0");
            trantool.setTrigger((TransitionToolspecificType.Trigger) data.get_triggermap_value(tran.getName().getText()));
            if (isSub) {
                trantool.setSubprocess(true);
            }
            tran.getToolspecific().add(trantool);
        }

        if (task.getResourceTypeRef().size() > 0) {
            ttt.setTool("WoPeD");
            ttt.setVersion("1.0");
            TriggerType tt = new TransitionToolspecificType.Trigger();
            GraphicsSimpleType gt = new GraphicsSimpleType();
            DimensionType dt = new DimensionType();
            dt.setX(BigDecimal.valueOf(Long.valueOf(24)));
            dt.setY(BigDecimal.valueOf(Long.valueOf(22)));
            gt.setDimension(dt);
            tt.setId("");
            tt.setType(200);
            tt.setGraphics(gt);
            data.put_triggermap(task.getName(), tt);
            ttt.setTrigger((TransitionToolspecificType.Trigger) tt);
            for (Object resource : task.getResourceTypeRef()) {
                if (resource instanceof ResourceTypeRefType) {
                    if (((ResourceTypeRefType) resource).getResourceTypeId() != null) {
                        if (data.get_resourcemap().containsKey(
                                String.valueOf(((ResourceTypeRefType) resource).getResourceTypeId()))) {
                            ResourceTypeType res = data
                                    .get_resourcemap_value(String
                                            .valueOf(((ResourceTypeRefType) resource).getResourceTypeId()));
                            String[] split = res.getName().split("-");
                            split[0] = split[0].substring(0);
                            split[1] = split[1].substring(0);

                            OrganizationUnitType ui = new OrganizationUnitType();
                            ui.setName(split[0]);
                            RoleType ro = new RoleType();
                            ro.setName(split[1]);
                            TransitionResourceType tres = new TransitionResourceType();
                            tres.setOrganizationalUnitName(ui);
                            tres.setRoleName(ro);
                            GraphicsSimpleType gtres = new GraphicsSimpleType();
                            DimensionType dtres = new DimensionType();
                            dtres.setX(BigDecimal.valueOf(Long.valueOf(60)));
                            dtres.setY(BigDecimal.valueOf(Long.valueOf(22)));
                            gtres.setDimension(dt);
                            tres.setGraphics(gtres);
                            ttt.setTransitionResource((TransitionToolspecificType.TransitionResource) tres);
                            data.put_resourcepositionmap(task.getName(), tres);
                            if (!data.getunit().contains(split[0])) {
                                data.addunit(split[0]);
                            }
                            if (!data.getroles().contains(split[1])) {
                                data.addroles(split[1]);
                            }
                        }
                    }
                }
            }

            if (isSub) {
                trantool.setSubprocess(true);
            }
            tran.getToolspecific().add(ttt);
        }

        if (trantool.getTool() == null && ttt.getTool() == null && isSub) {
            TransitionToolspecificType sub = new TransitionToolspecificType();
            sub.setTool("WoPeD");
            sub.setVersion("1.0");
            sub.setSubprocess(true);
            tran.getToolspecific().add(sub);
        }

        data.getNet().getTransition().add(tran);
        data.put_pnmlRefMap(tran.getId(), tran);

        data.put_originalid_map(BigInteger.valueOf(Long.valueOf(tran.getId())), task.getOriginalID());

        if (data.isCpfTaskPnmlTransition()) {

            // Tell TranslateArc where to attach incoming and outgoing arcs
            data.getStartNodeMap().put(task, tran);
            data.getEndNodeMap().put(task, tran);
            
        } else {
            // The transition we've created only starts the task; we also need
            // to add a place to represent that the task is running and a second
            // transition to represent the task ending.
            //
            // [tran] --> (running) --> [end]

            // Create the place for the state in which the task is running
            PlaceType running = new PlaceType();
            running.setId(String.valueOf(ids++));
            running.setGraphics(newGraphicsNodeType(dummyPosition(), placeDefaultDimension()));
            data.getNet().getPlace().add(running);

            // Create the arc from the start transition to the running place
            ArcType startToRunning = new ArcType();
            startToRunning.setId(String.valueOf(ids++));
            startToRunning.setSource(tran);
            startToRunning.setTarget(running);
            data.getNet().getArc().add(startToRunning);

            // Create the transition that stops the task running
            TransitionType end = new TransitionType();
            end.setId(String.valueOf(ids++));
            end.setName(running.getName());
            end.setGraphics(newGraphicsNodeType(dummyPosition(), transitionDefaultDimension()));
            data.getNet().getTransition().add(end);

            // Create the arc from the running place to the end transition
            ArcType runningToEnd = new ArcType();
            runningToEnd.setId(String.valueOf(ids++));
            runningToEnd.setSource(running);
            runningToEnd.setTarget(end);
            data.getNet().getArc().add(runningToEnd);

            // Name the nodes (English naming convention)
            String taskName = task.getName();
            if (taskName != null) {
                // Start transition named "<Task> start"
                NodeNameType name = new NodeNameType();
                name.setText(taskName + "_start");
                tran.setName(name);

                // Running place named "<Task>"
                name = new NodeNameType();
                name.setText(taskName);
                running.setName(name);

                // End transition named "<Task> end"
                name = new NodeNameType();
                name.setText(taskName + "_end");
                end.setName(name);
            }

            // Tell TranslateArc where to attach incoming and outgoing arcs
            data.getStartNodeMap().put(task, tran);
            data.getRunningPlaceMap().put(task, running);
            data.getEndNodeMap().put(task, end);
        }
    }

    /*
    static private final BigDecimal TWO = new BigDecimal(2);

    static private PositionType midpoint(final PositionType a, final PositionType b) {
        PositionType p = new PositionType();
        p.setX(a.getX().add(b.getX()).divide(TWO));
        p.setY(a.getX().add(b.getX()).divide(TWO));
        return p;
    }
    */

    static PositionType dummyPosition() {
        PositionType dummyPosition = new PositionType();
        dummyPosition.setX(BigDecimal.valueOf(100));
        dummyPosition.setY(BigDecimal.valueOf(400));
        return dummyPosition;
    }

    static DimensionType placeDefaultDimension() {
        DimensionType d = new DimensionType();
        d.setX(BigDecimal.valueOf(30));
        d.setY(BigDecimal.valueOf(30));
        return d;
    }

    static DimensionType transitionDefaultDimension() {
        DimensionType d = new DimensionType();
        d.setX(BigDecimal.valueOf(50));
        d.setY(BigDecimal.valueOf(50));
        return d;
    }

    static DimensionType blindTransitionDefaultDimension() {
        DimensionType d = new DimensionType();
        d.setX(BigDecimal.valueOf(10));
        d.setY(BigDecimal.valueOf(50));
        return d;
    }

    static GraphicsNodeType newGraphicsNodeType(final PositionType position, final DimensionType dimension) {
        GraphicsNodeType graphics = new GraphicsNodeType();
        graphics.setPosition(position);
        graphics.setDimension(dimension);
        return graphics;
    }


    public void translateEvent(NodeType node) {
        if (node instanceof MessageType || node instanceof TimerType) {
            TransitionType tran = new TransitionType();
            data.put_id_map(node.getId(), String.valueOf(ids));
            tran.setId(String.valueOf(ids++));
            tran.setGraphics(newGraphicsNodeType(dummyPosition(), transitionDefaultDimension()));

            if (node.getName() != null) {
                NodeNameType test = new NodeNameType();
                test.setText(node.getName());
                tran.setName(test);
            }

            TransitionToolspecificType ttt = new TransitionToolspecificType();
            ttt.setTool("WoPeD");
            ttt.setVersion("1.0");
            TransitionToolspecificType.Trigger tt = new TransitionToolspecificType.Trigger();
            GraphicsSimpleType gt = new GraphicsSimpleType();
            DimensionType dt = new DimensionType();
            dt.setX(BigDecimal.valueOf(Long.valueOf(24)));
            dt.setY(BigDecimal.valueOf(Long.valueOf(22)));
            gt.setDimension(dt);
            tt.setId("");
            if (node instanceof MessageType) {
                tt.setType(201);
            } else {
                assert node instanceof TimerType;
                tt.setType(202);
            }
            tt.setGraphics(gt);
            data.put_triggermap(node.getName(), tt);
            ttt.setTrigger(tt);
            tran.getToolspecific().add(ttt);

            // Create a place to wait for the timer to expire
            String waitingPlaceNameText = null;
            if (node instanceof MessageType) {
                waitingPlaceNameText = "msg_wait";
            } else {
                assert node instanceof TimerType;
                waitingPlaceNameText = "time_wait";
            }
            assert waitingPlaceNameText != null;
            if (node.getName() != null) {
                waitingPlaceNameText = node.getName() + "_" + waitingPlaceNameText;
            }

            NodeNameType waitingPlaceName = new NodeNameType();
            waitingPlaceName.setText(waitingPlaceNameText);

            PlaceType waitingPlace = new PlaceType();
            waitingPlace.setId(String.valueOf(ids++));
            waitingPlace.setName(waitingPlaceName);
            waitingPlace.setGraphics(newGraphicsNodeType(dummyPosition(), placeDefaultDimension()));
            data.getNet().getPlace().add(waitingPlace);

            // Create arc from waitingPlace to tran
            ArcType arc = new ArcType();
            arc.setId(String.valueOf(ids++));
            arc.setSource(waitingPlace);
            arc.setTarget(tran);
            data.getNet().getArc().add(arc);

            data.getNet().getTransition().add(tran);
            data.put_pnmlRefMap(tran.getId(), tran);
            data.getStartNodeMap().put(node, waitingPlace);
            data.getRunningPlaceMap().put(node, waitingPlace);
            data.getEndNodeMap().put(node, tran);
            data.put_originalid_map(BigInteger.valueOf(Long.valueOf(tran.getId())), node.getOriginalID());

        } else {
            PlaceType place = new PlaceType();
            data.put_id_map(node.getId(), String.valueOf(ids));
            place.setId(String.valueOf(ids++));
            place.setGraphics(newGraphicsNodeType(dummyPosition(), placeDefaultDimension()));

            if (node.getName() != null) {
                NodeNameType test = new NodeNameType();
                test.setText(node.getName());
                place.setName(test);
            }

            data.getNet().getPlace().add(place);
            data.put_pnmlRefMap(place.getId(), place);

            data.getStartNodeMap().put(node, place);
            data.getRunningPlaceMap().put(node, place);
            data.getEndNodeMap().put(node, place);

            data.put_originalid_map(BigInteger.valueOf(Long.valueOf(place.getId())), node.getOriginalID());
        }
    }

    public void translateState(NodeType node) {
        PlaceType place = new PlaceType();
        data.put_id_map(node.getId(), String.valueOf(ids));
        place.setId(String.valueOf(ids++));
        place.setGraphics(newGraphicsNodeType(dummyPosition(), placeDefaultDimension()));

        if (node.getName() != null) {
            NodeNameType test = new NodeNameType();
            test.setText(node.getName());
            place.setName(test);
        }

        data.getNet().getPlace().add(place);
        data.put_pnmlRefMap(place.getId(), place);

        data.getStartNodeMap().put(node, place);
        data.getRunningPlaceMap().put(node, place);
        data.getEndNodeMap().put(node, place);

        data.put_originalid_map(BigInteger.valueOf(Long.valueOf(place.getId())), node.getOriginalID());
    }

    public long getIds() {
        return ids;
    }

}
