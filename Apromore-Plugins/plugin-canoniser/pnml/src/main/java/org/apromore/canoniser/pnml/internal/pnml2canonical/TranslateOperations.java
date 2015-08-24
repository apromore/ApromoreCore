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

public abstract class TranslateOperations {

    static public void translateOperation(TransitionToolspecificType tran,
                                   TransitionType tra,
                                   DataHandler data) {

        data.put_id_map(tra.getId(), String.valueOf(data.getIds()));

        if (tran == null) {
            final boolean isSource = data.getsourcevalues().contains(tra.getId());
            final boolean isTarget = data.gettargetvalues().contains(tra.getId());

            if (!isSource && isTarget) {
                TranslateSplits.translateAndSplits(tra, data);
            } else if (isSource && !isTarget) {
                TranslateJoins.translateAndJoins(tra, data);
            } else if (isSource && isTarget) {
                TranslateSplitJoins.translateAndSplitJoins(tra, data);
            } else {
                throw new IllegalArgumentException("Unable to translate " + tra.getId() + " as a routing operation because it neither joins nor splits");
            }
        } else {
            switch (tran.getOperator().getType()) {
            case 101: TranslateSplits.translateAndSplits(tra, data); break;
            case 102: TranslateJoins.translateAndJoins(tra, data); break;
            case 104: TranslateSplits.translateXorSplits(tra, data); break;
            case 105: TranslateJoins.translateXorJoins(tra, data); break;
            case 106: TranslateSplitJoins.translateXorSplitJoins(tra, data); break;
            case 107: TranslateSplitJoins.translateAndSplitJoins(tra, data); break;
            case 108: TranslateSplitJoins.translateAndJoinXorSplit(tra, data); break;
            case 109: TranslateSplitJoins.translateXorJoinAndSplit(tra, data); break;
            }
        }
    }
}
