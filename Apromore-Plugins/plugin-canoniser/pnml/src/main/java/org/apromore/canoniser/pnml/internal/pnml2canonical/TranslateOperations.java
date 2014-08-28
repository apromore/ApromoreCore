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

import org.apromore.pnml.TransitionToolspecificType;
import org.apromore.pnml.TransitionType;

public class TranslateOperations {
    DataHandler data;
    long ids;

    public void setValues(DataHandler data, long ids) {
        this.data = data;
        this.ids = ids;
    }

    public void translateOperation(TransitionToolspecificType tran,
                                   TransitionType tra) {

        TranslateJoins tj = new TranslateJoins();
        TranslateSplits ts = new TranslateSplits();
        TranslateSplitJoins tsj = new TranslateSplitJoins();
        data.put_id_map(tra.getId(), String.valueOf(ids));

        if (tran == null) {

            if (data.gettargetvalues().contains(tra.getId())
                    && !data.getsourcevalues().contains(tra.getId())) {

                ts.setValues(data, ids);
                ts.translateAndSplits(tra);
                ids = ts.getIds();
            } else if (data.getsourcevalues().contains(tra.getId())
                    && !data.gettargetvalues().contains(tra.getId())) {
                tj.setValues(data, ids);
                tj.translateAndJoins(tra);
                ids = tj.getIds();
            } else if (data.getsourcevalues().contains(tra.getId())
                    && data.gettargetvalues().contains(tra.getId())) {
                tsj.setValues(data, ids);
                tsj.translateAndSplitJoins(tra);
                ids = tsj.getIds();

            }
        } else {

            if (tran.getOperator().getType() == 101) {
                ts.setValues(data, ids);
                ts.translateAndSplits(tra);
                ids = ts.getIds();
            } else if (tran.getOperator().getType() == 102) {
                tj.setValues(data, ids);
                tj.translateAndJoins(tra);
                ids = tj.getIds();
            } else if (tran.getOperator().getType() == 104) {
                ts.setValues(data, ids);
                ts.translateXorSplits(tra);
                ids = ts.getIds();
            } else if (tran.getOperator().getType() == 105) {
                tj.setValues(data, ids);
                tj.translateXorJoins(tra);
                ids = tj.getIds();
            } else if (tran.getOperator().getType() == 106) {
                tsj.setValues(data, ids);
                tsj.translateXorSplitJoins(tra);
                ids = tsj.getIds();
            } else if (tran.getOperator().getType() == 107) {
                tsj.setValues(data, ids);
                tsj.translateAndSplitJoins(tra);
                ids = tsj.getIds();
            } else if (tran.getOperator().getType() == 108) {
                tsj.setValues(data, ids);
                tsj.translateAndJoinXorSplit(tra);
                ids = tsj.getIds();
            } else if (tran.getOperator().getType() == 109) {
                tsj.setValues(data, ids);
                tsj.translateXorJoinAndSplit(tra);
                ids = tsj.getIds();
            }

        }
    }

    public long getIds() {
        return ids;
    }

}
