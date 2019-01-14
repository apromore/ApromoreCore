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

package org.apromore.service.logfilter.activity.impl;

import org.apromore.plugin.DefaultParameterAwarePlugin;
import org.apromore.service.logfilter.activity.ActivityFilterService;
import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.impl.XLogInfoImpl;
import org.deckfour.xes.model.XLog;
import org.springframework.stereotype.Service;

/**
 * Created by Raffaele Conforti on 18/04/2016.
 */
@Service
public class ActivityFilterServiceImpl extends DefaultParameterAwarePlugin implements ActivityFilterService {

    @Override
    public String[] getLifecycleClasses(XLog log) {
        XEventClassifier classifier = XLogInfoImpl.LIFECYCLE_TRANSITION_CLASSIFIER;
        XLogInfo logInfo = XLogInfoImpl.create(log, classifier);
        XEventClasses eventClasses = logInfo.getEventClasses(classifier);
        String[] classes = new String[eventClasses.size()];
        int i = 0;
        for(XEventClass eventClass : eventClasses.getClasses()) {
            classes[i] = eventClass.toString();
            i++;
        }
        return classes;
    }

    @Override
    public XLog filterLog(XLog log, String[] classes_to_remove, int percentage) {
        LogFilter filter = new LogFilter();
        return filter.filter(log, classes_to_remove, percentage);
    }

}
