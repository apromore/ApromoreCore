/*
 * Copyright  2009-2018 The Apromore Initiative.
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
package org.apromore.prodrift.main;


import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apromore.prodrift.config.DriftDetectionSensitivity;
import org.apromore.prodrift.driftcharacterization.CharacterizationAccuracyResult;
import org.apromore.prodrift.driftdetector.ControlFlowDriftDetector_EventStream;
import org.apromore.prodrift.model.ProDriftDetectionResult;
import org.apromore.prodrift.util.LogStreamer;
import org.apromore.prodrift.util.XLogManager;
import org.deckfour.xes.model.XLog;


public class Main {

	public static List<String> logNameList = new ArrayList<>();

	public static Map<String, CharacterizationAccuracyResult> CharacterizationAccuracyMap = new LinkedHashMap<>();

	public static boolean completeCharacterizationExperiment = false;
	public static boolean completeGoodnessOfKSPTExperiment = false;

	public static boolean isLogGenerationForNick = false;
	public static String startActivity1 = "START";
	public static String startActivity2 = "START";
	public static String endActivity1 = "END";

	public static boolean isStandAlone = true;

	public static void main(final String[] args) throws Exception
	{
		Path path = Paths.get("./Log.mxml.gz");
		XLog xl = XLogManager.readLog(new FileInputStream(path.toString()), path.getFileName().toString());
		int winSize = -1;
		boolean isAdwin = false;
		float noiseFilterPercentage = 10.0f;
		boolean withConflict = false;
		String logFileName = "Log.mxml.gz";
		boolean withCharacterization = true;
		int cummulativeChange = 98;

		StringBuilder sb = new StringBuilder();
		StringBuilder winSizeStr = new StringBuilder();
		XLog eventStream = LogStreamer.logStreamer(xl, sb, winSizeStr, logFileName);
		int activityCount = Integer.parseInt(sb.toString());

		if(winSize == -1)
		{
			int winSize_t = Integer.parseInt(winSizeStr.toString());
			if(winSize_t < 100)
				winSize = Math.max(winSize_t, activityCount * activityCount * 5);
			else
				winSize = winSize_t;
		}

		DriftDetectionSensitivity ddSensitivity = DriftDetectionSensitivity.Low;

		ControlFlowDriftDetector_EventStream driftDertector = new ControlFlowDriftDetector_EventStream(xl, eventStream, winSize, activityCount, isAdwin, noiseFilterPercentage, ddSensitivity, withConflict, logFileName, withCharacterization, cummulativeChange);

		ProDriftDetectionResult result = driftDertector.ControlFlowDriftDetectorStart();

		System.out.println();

		java.util.List<BigInteger> startOfTransitionPoints = result.getStartOfTransitionPoints();
		java.util.List<BigInteger> endOfTransitionPoints = result.getEndOfTransitionPoints();


		List<ByteArrayOutputStream> eventLogList = null;
		try {

			eventLogList = XLogManager.getSubLogs(xl, logFileName, startOfTransitionPoints, endOfTransitionPoints, true);

		}catch (Exception ex)
		{
			ex.printStackTrace();
			return;
		}
//
	}

}
	 