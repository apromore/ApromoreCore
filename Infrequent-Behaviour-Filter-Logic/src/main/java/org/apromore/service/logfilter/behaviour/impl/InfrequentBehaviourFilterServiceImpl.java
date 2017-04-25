/*
 * Copyright © 2009-2017 The Apromore Initiative.
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

package org.apromore.service.logfilter.behaviour.impl;

import com.raffaeleconforti.noisefiltering.event.InfrequentBehaviourFilter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apromore.plugin.DefaultParameterAwarePlugin;
import org.apromore.service.logfilter.behaviour.InfrequentBehaviourFilterService;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

/**
 * Created by Raffaele Conforti on 18/04/2016.
 */
@Service
public class  InfrequentBehaviourFilterServiceImpl extends DefaultParameterAwarePlugin implements InfrequentBehaviourFilterService {

    private final static String LPSOLVE55 = "lpsolve55.dll";
    private final static String LPSOLVE55J = "lpsolve55j.dll";
    private final static String LIBLPSOLVE55 = "liblpsolve55.jnilib";
    private final static String LIBLPSOLVE55J = "liblpsolve55j.jnilib";

//    static {
//        try {
//            if(System.getProperty("os.name").startsWith("Windows")) {
//                System.loadLibrary(LPSOLVE55);
//                System.loadLibrary(LPSOLVE55J);
//            }else {
//                System.loadLibrary(LIBLPSOLVE55);
//                System.loadLibrary(LIBLPSOLVE55J);
//            }
//        } catch (UnsatisfiedLinkError e) {
//            loadFromJar();
//        }
//    }

    private static void loadFromJar() {
        // we need to put both DLLs to temp dir
        String path = "lib/";
        if(System.getProperty("os.name").startsWith("Windows")) {
            loadLibWin(path, LPSOLVE55);
            loadLibWin(path, LPSOLVE55J);
        }else {
            loadLibMac(path, LIBLPSOLVE55);
            loadLibMac(path, LIBLPSOLVE55J);
        }
    }

    private static void loadLibWin(String path, String name) {
        try {
            // have to use a stream
            InputStream in = InfrequentBehaviourFilter.class.getResourceAsStream("/" + name);
            // always write to different location
            File fileOut = new File(name);
            OutputStream out = FileUtils.openOutputStream(fileOut);
            IOUtils.copy(in, out);
            in.close();
            out.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load required DLL", e);
        }
    }

    private static void loadLibMac(String path, String name) {
        try {
            // have to use a stream
            InputStream in = InfrequentBehaviourFilter.class.getResourceAsStream("/" + name);
            // always write to different location
            File fileOut = new File(name);
            OutputStream out = FileUtils.openOutputStream(fileOut);
            IOUtils.copy(in, out);
            in.close();
            out.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load required JNILIB", e);
        }
    }

    @Override
    public XLog filterLog(XLog log) {
        if(System.getProperty("os.name").startsWith("Windows")) {
            if(!(new File(LPSOLVE55)).exists()) {
                loadFromJar();
            }
        }else {
            if(!(new File(LIBLPSOLVE55)).exists()) {
                loadFromJar();
            }
        }

        XEventClassifier classifier = new XEventNameClassifier();
        InfrequentBehaviourFilter filter = new InfrequentBehaviourFilter(classifier);
        return filter.filterLog(log);
    }

}
