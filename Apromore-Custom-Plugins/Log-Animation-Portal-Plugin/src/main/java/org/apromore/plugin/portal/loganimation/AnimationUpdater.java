/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2017 Queensland University of Technology.
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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

package org.apromore.plugin.portal.loganimation;

import java.util.Set;
import java.util.StringTokenizer;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 16/11/17.
 */
public class AnimationUpdater {

    public String updateAnimationData(String animationData, Set<String> removedFlowIDs) {
        StringTokenizer st = new StringTokenizer(animationData, "}", true);
        StringBuilder sb = new StringBuilder();

        boolean remove = false;
        while(st.hasMoreElements()) {
            String token = st.nextToken();
            if(!token.equals("}")) remove = false;

            for(String flowID : removedFlowIDs) {
                if(token.contains(flowID)) {
                    remove = true;
                    break;
                }
            }
            if(!remove) {
                sb.append(token);
            }
        }
        return animationData;
    }

}
