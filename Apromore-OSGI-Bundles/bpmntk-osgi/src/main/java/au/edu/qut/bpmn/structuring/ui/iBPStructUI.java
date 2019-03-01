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

package au.edu.qut.bpmn.structuring.ui;

import org.deckfour.uitopia.api.event.TaskListener;
import org.processmining.contexts.uitopia.UIPluginContext;

import java.util.concurrent.CancellationException;

/**
 * Created by Adriano on 29/02/2016.
 */
public class iBPStructUI {

    public iBPStructUIResult showGUI(UIPluginContext context) {

        iBPStructSettings ibpsParam = new iBPStructSettings();
        TaskListener.InteractionResult guiResult = context.showWizard("Select iBPStruct strategy", true, true, ibpsParam);

        if( guiResult == TaskListener.InteractionResult.CANCEL ) {
            context.getFutureResult(0).cancel(true);
            throw new CancellationException("The wizard has been cancelled.");
        }

        return ibpsParam.getSelections();
    }

}
