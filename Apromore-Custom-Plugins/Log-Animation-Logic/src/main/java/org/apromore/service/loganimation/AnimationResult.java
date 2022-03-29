/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package org.apromore.service.loganimation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apromore.service.loganimation.replay.AnimationLog;
import org.json.JSONObject;

import de.hpi.bpmn2_0.model.Definitions;

public class AnimationResult {
    private final List<AnimationLog> logs;
    private final Definitions model;
    private final JSONObject jsonSetupData;
    
    public AnimationResult(List<AnimationLog> logs, Definitions model, JSONObject jsonSetupData) {
        this.logs = logs;
        this.model = model;
        this.jsonSetupData = jsonSetupData;
    }
    
    private AnimationResult() {
        logs = new ArrayList<>();
        model = null;
        jsonSetupData = null;
    }
    
    public List<AnimationLog> getAnimationLogs() {
        return Collections.unmodifiableList(logs);
    }
    
    public Definitions getModel() {
        return this.model;
    }
    
    public JSONObject getSetupJSON() {
        return this.jsonSetupData;
    }
}
