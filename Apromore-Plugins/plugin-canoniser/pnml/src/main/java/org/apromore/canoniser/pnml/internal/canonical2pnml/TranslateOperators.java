/*
 * Copyright © 2009-2016 The Apromore Initiative.
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

import java.math.BigInteger;
import java.util.logging.Logger;

import org.apromore.cpf.ANDJoinType;
import org.apromore.cpf.ANDSplitType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ORJoinType;
import org.apromore.cpf.ORSplitType;
import org.apromore.cpf.XORJoinType;
import org.apromore.cpf.XORSplitType;
import org.apromore.pnml.NodeNameType;
import org.apromore.pnml.OperatorType;
import org.apromore.pnml.TransitionToolspecificType;
import org.apromore.pnml.TransitionType;

public class TranslateOperators {

    static final private Logger LOGGER = Logger.getLogger(TranslateNet.class.getCanonicalName());

    DataHandler data;
    long ids;

    public void setValues(DataHandler data, long ids) {
        this.data = data;
        this.ids = ids;
    }

    public void translate(NodeType node) {
        if (data.get_specialoperators().containsKey("andsplitjoin-" + node.getName())) {
            TranslateANDSplitJoin tasj = new TranslateANDSplitJoin();
            tasj.setValues(data, ids);
            tasj.translate(node);
            ids = tasj.getIds();
        } else if (data.get_specialoperators().containsKey("xorsplitjoin-" + node.getName())) {
            TranslateXORSplitJoin txsj = new TranslateXORSplitJoin();
            txsj.setValues(data, ids);
            txsj.translate(node);
            ids = txsj.getIds();
        } else if (data.get_specialoperators().containsKey("andjoinxorsplit-" + node.getName())) {
            TranslateANDJoinXORSplit tajxs = new TranslateANDJoinXORSplit();
            tajxs.setValues(data, ids);
            tajxs.translate(node);
            ids = tajxs.getIds();
        } else if (data.get_specialoperators().containsKey("xorjoinandsplit-" + node.getName())) {
            TranslateXORJoinANDSplit txjas = new TranslateXORJoinANDSplit();
            txjas.setValues(data, ids);
            txjas.translate(node);
            ids = txjas.getIds();
        } else {
            TransitionType tran = new TransitionType();
            TransitionToolspecificType trantool = new TransitionToolspecificType();
            OperatorType op = new OperatorType();
            /*  We lose the name of the gate when converting it to a silent transition -- since "silent" means no name
            NodeNameType test = null;
            if (node.getName() != null) {
                test = new NodeNameType();
                test.setText(node.getName());
            }
            */
            data.put_id_map(node.getId(), String.valueOf(ids));
            tran.setId(String.valueOf(ids++));
            //tran.setName(test);
            tran.setGraphics(TranslateNode.newGraphicsNodeType(TranslateNode.dummyPosition(), TranslateNode.blindTransitionDefaultDimension()));

            if (node instanceof ANDJoinType && node.getOriginalID() != null && node.getOriginalID().contains("op")) {
                trantool.setTool("WoPeD");
                trantool.setVersion("1.0");
                String splitid[] = node.getOriginalID().split("_");
                op.setId(splitid[0]);
                op.setType(102);
                trantool.setOrientation(3);
                trantool.setOperator(op);
            } else if (node instanceof ANDSplitType && node.getOriginalID() != null && node.getOriginalID().contains("op")) {
                trantool.setTool("WoPeD");
                trantool.setVersion("1.0");
                String splitid[] = node.getOriginalID().split("_");
                op.setId(splitid[0]);
                op.setType(101);
                trantool.setOrientation(1);
                trantool.setOperator(op);
            } else if (node instanceof XORJoinType) {
                if (node.getOriginalID() != null) {
                    trantool.setTool("WoPeD");
                    trantool.setVersion("1.0");
                    String splitid[] = node.getOriginalID().split("_");
                    op.setId(splitid[0]);
                    op.setType(105);
                    trantool.setOrientation(3);
                    trantool.setOperator(op);
                }
                data.put_dupjoinMap(node.getId(), tran);
                data.addxorconnectors(node);
            } else if (node instanceof XORSplitType) {
                if (node.getOriginalID() != null) {
                    trantool.setTool("WoPeD");
                    trantool.setVersion("1.0");
                    String splitid[] = node.getOriginalID().split("_");
                    op.setId(splitid[0]);
                    trantool.setOrientation(1);
                    op.setType(104);
                    trantool.setOperator(op);
                }
                data.put_dupsplitMap(node.getId(), tran);
                data.addxorconnectors(node);
            } else if (node instanceof ORJoinType) {  // Petri Nets can't model OR routing, so there's no correct way to do this  :(
                LOGGER.warning("Changing CPF node " + node.getId() + " from OR join to XOR during PNML decanonization");
                data.put_dupjoinMap(node.getId(), tran);
                data.addxorconnectors(node);
            } else if (node instanceof ORSplitType) {
                LOGGER.warning("Changing CPF node " + node.getId() + " from OR split to XOR during PNML decanonization");
                data.put_dupsplitMap(node.getId(), tran);
                data.addxorconnectors(node);
            }

            if (trantool.getTool() != null && tran.getName() != null) {
                if (data.get_triggermap().containsKey(tran.getName().getText())) {
                    trantool.setTrigger((TransitionToolspecificType.Trigger) data.get_triggermap_value(tran.getName().getText()));
                }
                if (data.get_resourcepositionmap().containsKey(tran.getName().getText())) {
                    trantool.setTransitionResource((TransitionToolspecificType.TransitionResource) data
                            .get_resourcepositionmap_value(tran.getName().getText()));
                }
                tran.getToolspecific().add(trantool);
            }

            data.getNet().getTransition().add(tran);
            data.put_pnmlRefMap(tran.getId(), tran);
            data.getStartNodeMap().put(node, tran);
            data.getEndNodeMap().put(node, tran);
            data.put_originalid_map(BigInteger.valueOf(Long.valueOf(tran.getId())), node.getOriginalID());
        }
    }

    public long getIds() {
        return ids;
    }

}
