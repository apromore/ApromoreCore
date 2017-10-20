/*
 * Copyright � 2009-2017 The Apromore Initiative.
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
package org.apromore.prodrift.driftdetector;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.nio.file.Path;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.math3.stat.inference.ChiSquareTest;
import org.apache.commons.math3.stat.inference.GTest;
import org.apromore.prodrift.config.BehaviorRelation;
import org.apromore.prodrift.config.CharacterizationConfig;
import org.apromore.prodrift.config.DriftConfig;
import org.apromore.prodrift.config.FeatureExtractionConfig;
import org.apromore.prodrift.config.InductiveMinerConfig;
import org.apromore.prodrift.config.NoiseFilterConfig;
import org.apromore.prodrift.config.RelationFrequency;
import org.apromore.prodrift.config.StatisticTestConfig;
import org.apromore.prodrift.config.WindowConfig;
import org.apromore.prodrift.driftcharacterization.ControlFlowDriftCharacterizer;
import org.apromore.prodrift.driftcharacterization.PairRelation;
import org.apromore.prodrift.logabstraction.extension.BasicLogRelationsExt;
import org.apromore.prodrift.logabstraction.extension.BasicLogRelationsExt.RelationCardinality;
import org.apromore.prodrift.main.Main;
import org.apromore.prodrift.mining.log.local.PrimeEventStructure;
import org.apromore.prodrift.model.DriftPoint;
import org.apromore.prodrift.model.ProDriftDetectionResult;
import org.apromore.prodrift.util.LinePlot;
import org.apromore.prodrift.util.LogStreamer;
import org.apromore.prodrift.util.MeanDelayCurve;
import org.apromore.prodrift.util.PrecisionRecallCurve;
import org.apromore.prodrift.util.Utils;
import org.apromore.prodrift.util.XLogManager;
import org.apromore.prodrift.util.confusionMat;
import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.info.impl.XLogInfoImpl;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XLogImpl;
import org.deckfour.xes.model.impl.XTraceImpl;
import org.jfree.chart.JFreeChart;
import org.processmining.framework.util.Pair;

import com.google.common.collect.Sets;



public class ControlFlowDriftDetector_EventStream implements ControlFlowDriftDetector{



	private DriftConfig driftConfig = DriftConfig.AlphaRelation;
	private WindowConfig winConfig = WindowConfig.ADWIN;
	private StatisticTestConfig statisticTest = StatisticTestConfig.QS;
	private InductiveMinerConfig IMConfig = InductiveMinerConfig.IMpt;
	private FeatureExtractionConfig FEConfig = FeatureExtractionConfig.WB;
	private NoiseFilterConfig noiseFilterConfig = NoiseFilterConfig.RemoveNoise_sum;

	private String logFileName = "";

	private Path logPath = null;

	public static int numberOfDriftGoldStandard = 9; //being the number of event logs that i have concatinated

	private double typicalThreshold = 0.05;
	private int initialwinSize = 100; // it has to be less than log.size/2
	private double winSizeReal = 100;

	private XFactory factory = XFactoryRegistry.instance().currentDefault();

	private Set<String> startActivities = new HashSet();
	private Set<String> endActivities = new HashSet();

	private XLog log;
	XLog eventStream = null;
	private PrecisionRecallCurve PRcurves = new PrecisionRecallCurve(typicalThreshold, numberOfDriftGoldStandard);
	private MeanDelayCurve MDcurves = new MeanDelayCurve(typicalThreshold, numberOfDriftGoldStandard);
	private confusionMat T_confMat, R_confMat;
	private double T_meandelay = 0, R_meandelay = 0;
	private long OverallTime = 0;

	private int numOfActivities = 0;
	int winSizeDividedBy = 10;
	int activityLimit = 35;

	private String driftSPOutput = "";
	private String ratioDis ="";

	private int ReduceWhenDimensionsAbove = 0;
	private int minExpectedFrequency = 5;

	private float oscilationFactor = 0.5f;

	private float relationNoiseThresh = 0.05f;

	private float relationStrengthThreshhold = -10f; // set it to less than -1 if you want to go with regular Alpha+

	private float relationsStrengthRelativityThreshhold = 0.5f;

	public static int winSizeCoefficient = 5;

	private String windowHeader = "";

	private boolean withConflict = false;

	private boolean isCPNToolsLog = true;

	private boolean withCharacterization = false;

	private boolean considerChangeSignificance = true;

	private boolean withFragment = true;

	private boolean withPartialMatching = false;

	private CharacterizationConfig charConfig = CharacterizationConfig.K_samplePermutationTest;

	private int charBufferSize = 100000; // must be at least 2*minCharDataPoints,
	//but better be as big as possible(preferably 2*(2*winsize + minCharDataPoints))

	private int minCharDataPoints = 500;
	int topCharzedDrifts = 1;
	int cutTopRelationsPercentage = 100;
	int charWindowMoveInterval = 1;

	Map<BigInteger, List<String>> characterizationMap = new HashMap<>();

	private String logNameShort;

	private List<Integer> bigDiffCounts = new ArrayList<>();


	public ControlFlowDriftDetector_EventStream(XLog logFIle, int winsize, boolean isAdwin,
												float noiseFilterPercentage, boolean withConflict, String logFileName, boolean withCharacterization, int cummulativeChange) {

		Main.isStandAlone = true;
//		XLog xl = XLogManager.readLog(is, logFileName);
		log = logFIle;

		List<Set<String>> startAndEndActivities = XLogManager.getStartAndEndActivities(log);
//		List<Set<String>> startAndEndActivities = XLogManager.addStartAndEndActivities(log);

		startActivities = startAndEndActivities.get(0);
		endActivities = startAndEndActivities.get(1);

		StringBuilder sb = new StringBuilder();
		eventStream = LogStreamer.logStreamer(log, sb, logFileName);

		numOfActivities = Integer.parseInt(sb.toString());

		this.initialwinSize = winsize;
		this.winSizeReal = winsize;
		this.driftConfig = DriftConfig.AlphaRelation;
		this.statisticTest = StatisticTestConfig.QS;
		this.winConfig = isAdwin ? WindowConfig.ADWIN : WindowConfig.FWIN;
		this.IMConfig = InductiveMinerConfig.IM;
		this.FEConfig = FeatureExtractionConfig.WBANB;
		this.noiseFilterConfig = relationNoiseThresh == 0f ? NoiseFilterConfig.NoFilter : NoiseFilterConfig.RemoveNoise;
		this.relationNoiseThresh = this.noiseFilterConfig == NoiseFilterConfig.NoFilter ? 0f : (noiseFilterPercentage / 100.0f);
		this.logFileName = logFileName;
		this.withConflict = withConflict;
		this.isCPNToolsLog = false;

		this.withCharacterization = withCharacterization;
		this.charConfig = CharacterizationConfig.K_samplePermutationTest;
		this.minCharDataPoints = 500;
		this.topCharzedDrifts = Integer.MAX_VALUE;
		this.cutTopRelationsPercentage = cummulativeChange; // top x%
		this.charBufferSize = 100000; // data points
		this.considerChangeSignificance = true;
		this.withFragment = false;
		this.withPartialMatching = false;

		String RelationType = "";
		if(this.driftConfig == DriftConfig.AlphaRelation)
			RelationType = "Alpha+";
		else
			RelationType = this.driftConfig.toString();
		this.windowHeader = "P-value Diagram"+" (" + logFileName +  ", Initial window size = "+ initialwinSize + ", Window type = " + winConfig.toString() + ", Relation BlockType = " + RelationType + ", Relation noise filter percentage = " + relationNoiseThresh * 100 + "%" + ")";

	}

	public ControlFlowDriftDetector_EventStream(XLog logFile, int winsize, boolean isAdwin, boolean withCharacterization) {

		Main.isStandAlone = true;
//		XLog xl = XLogManager.readLog(is, logFileName);
		log = logFile;

		List<Set<String>> startAndEndActivities = XLogManager.getStartAndEndActivities(log);
//		List<Set<String>> startAndEndActivities = XLogManager.addStartAndEndActivities(log);

		startActivities = startAndEndActivities.get(0);
		endActivities = startAndEndActivities.get(1);

		StringBuilder sb = new StringBuilder();
		eventStream = LogStreamer.logStreamer(log, sb, logFileName);

		numOfActivities = Integer.parseInt(sb.toString());

		this.initialwinSize = winsize;
		this.winSizeReal = winsize;
		this.driftConfig = DriftConfig.AlphaRelation;
		this.statisticTest = StatisticTestConfig.QS;
		this.winConfig = isAdwin ? WindowConfig.ADWIN : WindowConfig.FWIN;
		this.IMConfig = InductiveMinerConfig.IM;
		this.FEConfig = FeatureExtractionConfig.WBANB;

		boolean isStandard = true;
		if(numOfActivities < activityLimit)
			isStandard = true;
		else
			isStandard = false;

		this.noiseFilterConfig = NoiseFilterConfig.NoFilter;
		this.relationNoiseThresh = 0f;
		this.withConflict = true;
		if(!isStandard)
		{
			this.noiseFilterConfig = NoiseFilterConfig.RemoveNoise_sum;
			this.relationNoiseThresh = 3.5f / 100.0f;
			this.withConflict = false;
		}

		this.logFileName = logFileName;
		this.isCPNToolsLog = false;

		this.withCharacterization = withCharacterization;
		this.charConfig = CharacterizationConfig.K_samplePermutationTest;
		this.minCharDataPoints = 500;
		this.topCharzedDrifts = Integer.MAX_VALUE;
		this.cutTopRelationsPercentage = 98; // top x%
		this.charBufferSize = 100000; // data points
		this.considerChangeSignificance = true;
		this.withFragment = false;
		this.withPartialMatching = false;

		String RelationType = "";
		if(this.driftConfig == DriftConfig.AlphaRelation)
			RelationType = "Alpha+";
		else
			RelationType = this.driftConfig.toString();
		this.windowHeader = "P-value Diagram"+" (Initial window size = "+ initialwinSize + ", Window type = " + winConfig.toString() + ", Relation BlockType = " + RelationType + ")";

	}


	public ControlFlowDriftDetector_EventStream(XLog logFIle, String logFileName, int winsize, DriftConfig Dcfg,
												StatisticTestConfig statTest, WindowConfig winCfg, InductiveMinerConfig IMConfig, FeatureExtractionConfig FEConfig,
												NoiseFilterConfig noiseFilterConfig, Path logPath, float relationNoiseThresh,
												boolean withConflict, boolean isCPNToolsLog, boolean withCharacterization, boolean considerChangeSignificance,
												CharacterizationConfig charConfig, int minCharDataPoints, int topCharzedDrifts, int cutTopRelationsPercentage,
												int charBufferSize, String logNameShort, boolean withFragment, boolean withPartialMatching) {
//		this.logFile =  logFilePath;
		this.initialwinSize = winsize;
		this.winSizeReal = winsize;
		this.driftConfig = Dcfg;
		this.statisticTest = statTest;
		this.winConfig = winCfg;
		this.IMConfig = IMConfig;



		this.FEConfig = FEConfig;
		this.noiseFilterConfig = noiseFilterConfig;
		this.relationNoiseThresh = relationNoiseThresh;
		if(this.noiseFilterConfig == NoiseFilterConfig.NoFilter)
			this.relationNoiseThresh = 0;
		log = logFIle;

		List<Set<String>> startAndEndActivities = XLogManager.getStartAndEndActivities(log);
//		List<Set<String>> startAndEndActivities = XLogManager.addStartAndEndActivities(log);

		startActivities = startAndEndActivities.get(0);
		endActivities = startAndEndActivities.get(1);

		StringBuilder sb = new StringBuilder();
		eventStream = LogStreamer.logStreamer(log, sb, logFileName);

		numOfActivities = Integer.parseInt(sb.toString());

		this.logFileName = logFileName;
		this.logPath = logPath;
		String relationStrength = "";
		if(this.driftConfig == DriftConfig.AlphaRelation)
			relationStrength = ", relation strength threshhold = " + relationStrengthThreshhold;
		this.windowHeader = "P-value Diagram"+" (" + logFileName +  ", Initial window size = "+ initialwinSize + ", Window type = " + winConfig.toString() + ", Relation BlockType = " + driftConfig + ", Relation noise filter threshhold = " + relationNoiseThresh * 100 + "%" + relationStrength + ")";
		this.withConflict = withConflict;
		this.isCPNToolsLog = isCPNToolsLog;
		this.withCharacterization = withCharacterization;
		this.charConfig = charConfig;
		this.minCharDataPoints = minCharDataPoints;
		this.topCharzedDrifts = topCharzedDrifts;
		this.cutTopRelationsPercentage = cutTopRelationsPercentage;
		this.charBufferSize = charBufferSize;
		this.logNameShort = logNameShort;

		this.considerChangeSignificance = considerChangeSignificance;
		this.withFragment = withFragment;
		this.withPartialMatching = withPartialMatching;



	}


	public ProDriftDetectionResult ControlFlowDriftDetectorStart()
	{

//		changePatternTest();
//		changePatternSimpleTestChangeWise();



//		driftTest0.winSizeTest();
//		driftTest0.PRcurves.plot();
//		driftTest0.MDcurves.plot();
//		System.out.println("***END MAIN***");

		JFreeChart lineChart= this.findDrifts();

		Image img = null;
		BufferedImage objBufferedImage= lineChart.createBufferedImage(1024,600);
		img = objBufferedImage.getScaledInstance(-1, -1, Image.SCALE_DEFAULT);

		ProDriftDetectionResult pddres = new ProDriftDetectionResult();
		pddres.setLineChart(lineChart);
		pddres.setpValuesDiagram(img);
		pddres.setDriftPoints(MDcurves.getDriftPoints());
		pddres.setLastReadTrace(MDcurves.getLastReadTrace());
		pddres.setStartOfTransitionPoints(MDcurves.getStartOfTransitionPoints());
		pddres.setEndOfTransitionPoints(MDcurves.getEndOfTransitionPoints());
		pddres.setDriftDates(MDcurves.getDriftDates());

		if(characterizationMap != null)
		{
			pddres.setCharacterizationMap(characterizationMap);
		}

		for(int i = 0; i < MDcurves.getDriftPoints().size(); i++)
		{

			int firstUnderCurve = MDcurves.getDriftPoints().get(i).intValue();
			pddres.getDriftStatements().add("Drift detected at event: " + firstUnderCurve + " (" + MDcurves.getDriftDates().get(i).toString() + ") after reading " + MDcurves.getLastReadTrace().get(i).intValue() + " events.");
			pddres.getIsGradual().add(false);

			List<String> charStats = pddres.getCharacterizationMap().get(MDcurves.getDriftPoints().get(i));
			if(charStats != null)
				for(String cs: charStats)
					if(!Main.isStandAlone)System.out.println(cs);

		}



		return pddres;

	}

	public void ControlFlowDriftDetectorStop()
	{

		try
		{
			Thread.currentThread().stop();
		}catch(Exception ex)
		{
		}

	}


	public JFreeChart findDrifts() {

		MDcurves.getDriftPoints().clear();
		MDcurves.getLastReadTrace().clear();
		MDcurves.getStartOfTransitionPoints().clear();
		MDcurves.getEndOfTransitionPoints().clear();
		MDcurves.getpValuesAtDrifts().clear();
		MDcurves.getDriftDates().clear();

//		for(XTrace trace : eventStream)
//			for(XEvent event : trace)
//				System.out.println(XLogManager.getTraceID(trace) + ": " + XLogManager.getEventName(event) + ": " + XLogManager.getEventTime(event));

		List<DriftPoint> DriftPointsList = new ArrayList<>();

		if(!Main.isStandAlone)System.out.println("Events: " + eventStream.size());
		if(!Main.isStandAlone)System.out.println("Activities: " + numOfActivities);
		if(!Main.isStandAlone && isCPNToolsLog)
			XLogManager.printActualDriftPoints(eventStream, DriftPointsList);

//		if(!Main.isStandAlone)
//		{
//			System.out.println("Start activities:");
//			for(String act : startActivities)
//			{
//				System.out.println(act);
//			}
//			System.out.println();
//			System.out.println("End activities:");
//			for(String act : endActivities)
//			{
//				System.out.println(act);
//			}
//		}

		LinePlot plotting = null;
		long time = System.nanoTime();

		switch (driftConfig) {

			case DirectFollow:
			{

				plotting = findDrifts_DirectFollow(eventStream, DriftPointsList);
				break;

			}
			case Follow:
			{

				plotting = findDrifts_Follow(eventStream, DriftPointsList);
				break;

			}
			case AlphaRelation:
			{

				plotting = findDrifts_AlphaRelations(eventStream, DriftPointsList);
				break;

			}
			case AbelBehaviouralProfile:
			{

//				plotting = findDrifts_AbelBehaviouralProfile(eventStream, DriftPointsList);
				break;

			}
			case BlockStructure:
			{

//				plotting = findDrifts_BlockStructure(eventStream, DriftPointsList);
				break;

			}

			default:
				break;
		}

		OverallTime = System.nanoTime() - time;
		if(!Main.isStandAlone)System.out.println("Overall time(sec): " + (System.nanoTime() - time) / 1000000000.0);

		JFreeChart lineChart = null;
		if(plotting != null)
		{
			plotting.addThreshold(typicalThreshold);
			lineChart = plotting.plot(MDcurves.getDriftPoints(), this.typicalThreshold, eventStream.size());
		}




//		ByteArrayOutputStream bas = new ByteArrayOutputStream();
//		        try {
//		            ImageIO.write(objBufferedImage, "png", bas);
//		        } catch (IOException e) {
//		            e.printStackTrace();
//		        }
//
//		byte[] byteArray=bas.toByteArray();
//
//		InputStream in = new ByteArrayInputStream(byteArray);
//		BufferedImage image;
//		try {
////			image = ImageIO.read(in);
//			File outputfile = new File("./image.png");
//			ImageIO.write(objBufferedImage, "png", outputfile);
//
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//





		return lineChart;
	}

	public LinePlot findDrifts_Follow(XLog eventStream, List<DriftPoint> DriftPointsList)
	{


		int winSize = initialwinSize;
		LinePlot plotting = new LinePlot(this.getClass().getName(), windowHeader, "Events of log", "P-value"); //

		double pValue=0;

		int curveIndex = plotting.AddCurve("P-value curve");
		ArrayList<Double> DC_pValVector = new ArrayList<>();
		ArrayList<Integer> winSizeVector = new ArrayList<>();


		int eventIndex;
		for (eventIndex = 0; eventIndex < 2 * winSize - 1; eventIndex++){//2*win-1 is the initial offset
			DC_pValVector.add(1.0);
			winSizeVector.add(winSize);
		}

		HashMap<String, Integer> TraceId_TraceIndex_InSubLog_Map_detWin = new HashMap<>();
		XLog detectionWindowSubLog = buildSubLogFromEventStream(eventStream, TraceId_TraceIndex_InSubLog_Map_detWin,
				(eventIndex + 1) - winSize, (eventIndex + 1));

		HashMap<String, Integer> TraceId_TraceIndex_InSubLog_Map_refWin = new HashMap<>();
		XLog referenceWindowSubLog = buildSubLogFromEventStream(eventStream, TraceId_TraceIndex_InSubLog_Map_refWin,
				(eventIndex + 1) - 2 * winSize, (eventIndex + 1) - winSize);

		HashMap<String, Integer> Activity_freq_detWin = new HashMap<>();
		HashMap<String, Set<String>> Activity_TraceSet_detWin = new HashMap<>();
		buildActivityFrequency(detectionWindowSubLog, Activity_freq_detWin, Activity_TraceSet_detWin);

		HashMap<String, Integer> Activity_freq_refWin = new HashMap<>();
		HashMap<String, Set<String>> Activity_TraceSet_refWin = new HashMap<>();
		buildActivityFrequency(referenceWindowSubLog, Activity_freq_refWin, Activity_TraceSet_refWin);

		HashMap<String, Integer> Relation_Freq_detWin = new HashMap<>();
		buildFollowRelationFrequency(detectionWindowSubLog, Relation_Freq_detWin);

		HashMap<String, Integer> Relation_Freq_refWin = new HashMap<>();
		buildFollowRelationFrequency(referenceWindowSubLog, Relation_Freq_refWin);

		Integer totalRelationFreq_bothWins[] = new Integer[2];
		totalRelationFreq_bothWins[0] = new Integer(0);
		totalRelationFreq_bothWins[1] = new Integer(0);

//		System.out.println(eventStream.size());
//		for(XTrace trace : eventStream)
//		{
//
//			System.out.println(XLogManager.getTraceID(trace) + ":" + XLogManager.getEventName(trace.get(0)));
//
//		}
		for (; ;) {
//			long t1 = System.currentTimeMillis();

			totalRelationFreq_bothWins[0] = 0;
			totalRelationFreq_bothWins[1] = 0;

			if(eventIndex % 1 == 0)
			{
				switch (statisticTest) {

					case GTest:
					{
						//----ADWIN
						if (this.winConfig == WindowConfig.ADWIN)
						{

							//						winSize = ADWin_DC_GTest(log, strHandler, winSize, eventIndex);

						}


						long Follow_FreqMatrix[][] = build_Follow_FrequencyMatrix(Relation_Freq_detWin, Relation_Freq_refWin,
								totalRelationFreq_bothWins);
						//					System.out.println(eventIndex + "," + winSize + "," + DC_FreqMatrix[1].length);
						//					System.out.println("build_AbelBehaviouralProfile time: " + (System.currentTimeMillis() - t1));

//						System.out.print("before: " + Follow_FreqMatrix[0].length + "  ");

						if (this.noiseFilterConfig == NoiseFilterConfig.RemoveNoise_sum)
							Follow_FreqMatrix = removeNoise_sum(Follow_FreqMatrix, totalRelationFreq_bothWins[0], totalRelationFreq_bothWins[1]);
						else if (this.noiseFilterConfig == NoiseFilterConfig.Aggregation)
							Follow_FreqMatrix = aggregateNoise(Follow_FreqMatrix, totalRelationFreq_bothWins[0], totalRelationFreq_bothWins[1]);
						else if (this.noiseFilterConfig == NoiseFilterConfig.RemoveInsignificantChanges)
							Follow_FreqMatrix = filterInsignificantChanges(Follow_FreqMatrix, totalRelationFreq_bothWins[0], totalRelationFreq_bothWins[1]);

//						if(Follow_FreqMatrix[0].length > ReduceWhenDimensionsAbove)
//							Follow_FreqMatrix = Utils.reduceDimensionality_cuttingColumnsWithFreqBelowThreshold(Follow_FreqMatrix, minExpectedFrequency);
						//					System.out.println(eventIndex + "," + winSize + "," + DC_FreqMatrix[1].length);
//						System.out.println("after: " + Follow_FreqMatrix[0].length);

						if(Follow_FreqMatrix[1].length > 1)
						{

//							System.out.println(DC_FreqMatrix[1].length);
							GTest gt = new GTest();
							pValue = gt.gTestDataSetsComparison(Follow_FreqMatrix[1], Follow_FreqMatrix[0]);

						}else
						{

							pValue = 1;

						}
						break;

					}

					default:
						break;

				}

				DC_pValVector.add(pValue);
				winSizeVector.add(winSize);
			}

			eventIndex++;
			if(eventIndex < eventStream.size())
			{


				// update data structure related to detection window
				updateActivityFreqMap_addEvent(Activity_freq_detWin, eventStream.get(eventIndex));
				updateActivityFreqMap_removeEvent(detectionWindowSubLog, TraceId_TraceIndex_InSubLog_Map_detWin, Activity_freq_detWin, eventStream.get(eventIndex - winSize));

				updateActivityTraceSetMap_addEvent(eventStream.get(eventIndex), Activity_TraceSet_detWin);
				updateFollowRelationFreqMap_addEvent(detectionWindowSubLog, Relation_Freq_detWin, TraceId_TraceIndex_InSubLog_Map_detWin, eventStream.get(eventIndex), true);
				updateSubLog_addEvent(detectionWindowSubLog, TraceId_TraceIndex_InSubLog_Map_detWin, eventStream.get(eventIndex), true);

				updateActivityTraceSetMap_removeEvent(detectionWindowSubLog, TraceId_TraceIndex_InSubLog_Map_detWin, eventStream.get(eventIndex  - winSize), Activity_TraceSet_detWin);
				updateFollowRelationFreqMap_removeEvent(detectionWindowSubLog, Relation_Freq_detWin, TraceId_TraceIndex_InSubLog_Map_detWin, eventStream.get(eventIndex  - winSize), true);
				updateSubLog_removeEvent(detectionWindowSubLog, TraceId_TraceIndex_InSubLog_Map_detWin, eventStream.get(eventIndex  - winSize));

				// update data structure related to reference window
				updateActivityFreqMap_addEvent(Activity_freq_refWin, eventStream.get(eventIndex - winSize));
				updateActivityFreqMap_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, Activity_freq_refWin, eventStream.get(eventIndex - 2 * winSize));

				updateActivityTraceSetMap_addEvent(eventStream.get(eventIndex - winSize), Activity_TraceSet_refWin);
				updateFollowRelationFreqMap_addEvent(referenceWindowSubLog, Relation_Freq_refWin, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(eventIndex - winSize), true);
				updateSubLog_addEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(eventIndex - winSize), true);

				updateActivityTraceSetMap_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(eventIndex - 2 * winSize), Activity_TraceSet_refWin);
				updateFollowRelationFreqMap_removeEvent(referenceWindowSubLog, Relation_Freq_refWin, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(eventIndex - 2 * winSize), false);
				updateSubLog_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(eventIndex - 2 * winSize));

				if (this.winConfig == WindowConfig.ADWIN)
				{

					winSize = ADWin_Follow_ver1(eventStream, eventIndex, winSize,
							detectionWindowSubLog, TraceId_TraceIndex_InSubLog_Map_detWin,
							referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin,
							Activity_freq_detWin, Activity_freq_refWin,
							Relation_Freq_detWin, Relation_Freq_refWin,
							Activity_TraceSet_detWin, Activity_TraceSet_refWin);

//					System.out.println(winSize);

				}

			}else
				break;


//			System.out.println("Total time: " + (System.currentTimeMillis() - t1));
		}

		List<Double> smoothPValues = DC_pValVector;

		// smoothing
//		smoothPValues = Utils.SMASmoothing(DC_pValVector, winSizeVector, smfDivideBy);
////		int curveNum = plotting.AddCurve("P-value curve smooth");


		plotting.addRaw(curveIndex, smoothPValues);

		MDcurves.retreiveDrift_EventStream(eventStream, eventStream.size(), logPath, smoothPValues,
				winSizeVector, typicalThreshold, DriftPointsList, driftConfig.toString(),
				0.5f, isCPNToolsLog, null);

		R_confMat = MDcurves.getConfMat(log.size());
		R_meandelay = R_confMat.getMeanDelay();


		String logPathString = logPath.toString();
		String winsPath = logPathString.substring(0, logPathString.lastIndexOf('.')) + "_results_" + driftConfig + "_winVector.csv";
		List<List<Integer>> listOfLists = new ArrayList<List<Integer>>();
		listOfLists.add(winSizeVector);
		Utils.writeToFile_Integer(winsPath, listOfLists);

		return plotting;

	}


	public LinePlot findDrifts_DirectFollow(XLog eventStream, List<DriftPoint> DriftPointsList)
	{


		int winSize = initialwinSize;
		LinePlot plotting = new LinePlot(this.getClass().getName(), windowHeader, "Events of log", "P-value"); //

		double pValue=0;

		int curveIndex = plotting.AddCurve("P-value curve");
		ArrayList<Double> DC_pValVector = new ArrayList<>();
		ArrayList<Integer> winSizeVector = new ArrayList<>();


		int eventIndex;
		for (eventIndex = 0; eventIndex < 2 * winSize - 1; eventIndex++){//2*win-1 is the initial offset
			DC_pValVector.add(1.0);
			winSizeVector.add(winSize);
		}

		HashMap<String, Integer> TraceId_TraceIndex_InSubLog_Map_detWin = new HashMap<>();
		XLog detectionWindowSubLog = buildSubLogFromEventStream(eventStream, TraceId_TraceIndex_InSubLog_Map_detWin,
				(eventIndex + 1) - winSize, (eventIndex + 1));

		HashMap<String, Integer> TraceId_TraceIndex_InSubLog_Map_refWin = new HashMap<>();
		XLog referenceWindowSubLog = buildSubLogFromEventStream(eventStream, TraceId_TraceIndex_InSubLog_Map_refWin,
				(eventIndex + 1) - 2 * winSize, (eventIndex + 1) - winSize);


		HashMap<String, Integer> Activity_freq_detWin = new HashMap<>();
		HashMap<String, Set<String>> Activity_TraceSet_detWin = new HashMap<>();
		buildActivityFrequency(detectionWindowSubLog, Activity_freq_detWin, Activity_TraceSet_detWin);

		HashMap<String, Integer> Activity_freq_refWin = new HashMap<>();
		HashMap<String, Set<String>> Activity_TraceSet_refWin = new HashMap<>();
		buildActivityFrequency(referenceWindowSubLog, Activity_freq_refWin, Activity_TraceSet_refWin);

		HashMap<String, Integer> DF_Relation_Freq_detWin = new HashMap<>();
		buildDirectFollowRelationFrequency(detectionWindowSubLog, DF_Relation_Freq_detWin);

		HashMap<String, Integer> DF_Relation_Freq_refWin = new HashMap<>();
		buildDirectFollowRelationFrequency(referenceWindowSubLog, DF_Relation_Freq_refWin);

		Integer totalRelationFreq_bothWins[] = new Integer[2];
		totalRelationFreq_bothWins[0] = new Integer(0);
		totalRelationFreq_bothWins[1] = new Integer(0);

//		System.out.println(eventStream.size());
//		for(XTrace trace : eventStream)
//		{
//
//			System.out.println(XLogManager.getTraceID(trace) + ":" + XLogManager.getEventName(trace.get(0)));
//
//		}
		for (; ;) {
//			long t1 = System.currentTimeMillis();

			totalRelationFreq_bothWins[0] = 0;
			totalRelationFreq_bothWins[1] = 0;

			if(eventIndex % 1 == 0)
			{
				//----ADWIN
				if (this.winConfig == WindowConfig.ADWIN)
				{

//						winSize = ADWin_DC_GTest(log, strHandler, winSize, eventIndex);

				}

				long DC_FreqMatrix[][] = build_DirectFollow_FrequencyMatrix(DF_Relation_Freq_detWin, DF_Relation_Freq_refWin,
						totalRelationFreq_bothWins);
//					System.out.println(eventIndex + "," + winSize + "," + DC_FreqMatrix[1].length);
//					System.out.println("build_AbelBehaviouralProfile time: " + (System.currentTimeMillis() - t1));
//						System.out.print("before: " + DC_FreqMatrix[0].length + "  ");

				if (this.noiseFilterConfig == NoiseFilterConfig.RemoveNoise_sum)
					DC_FreqMatrix = removeNoise_sum(DC_FreqMatrix, totalRelationFreq_bothWins[0], totalRelationFreq_bothWins[1]);
				if (this.noiseFilterConfig == NoiseFilterConfig.RemoveNoise)
					DC_FreqMatrix = removeNoise_max(DC_FreqMatrix, null);
				else if (this.noiseFilterConfig == NoiseFilterConfig.Aggregation)
					DC_FreqMatrix = aggregateNoise(DC_FreqMatrix, totalRelationFreq_bothWins[0], totalRelationFreq_bothWins[1]);
				else if (this.noiseFilterConfig == NoiseFilterConfig.RemoveInsignificantChanges)
					DC_FreqMatrix = filterInsignificantChanges(DC_FreqMatrix, totalRelationFreq_bothWins[0], totalRelationFreq_bothWins[1]);

//						if(DC_FreqMatrix[0].length > ReduceWhenDimensionsAbove)
//							DC_FreqMatrix = Utils.reduceDimensionality_cuttingColumnsWithFreqBelowThreshold(DC_FreqMatrix, minExpectedFrequency);
//					System.out.println(eventIndex + "," + winSize + "," + DC_FreqMatrix[1].length);
//						System.out.println("after: " + DC_FreqMatrix[0].length);

				if(DC_FreqMatrix[1].length > 1)
				{

					try{

						if(statisticTest == StatisticTestConfig.GTest)
						{

							GTest gt = new GTest();
							pValue = gt.gTestDataSetsComparison(DC_FreqMatrix[1], DC_FreqMatrix[0]);

						}else if(statisticTest == StatisticTestConfig.QS)
						{

							ChiSquareTest cst = new ChiSquareTest();
							pValue = cst.chiSquareTest(DC_FreqMatrix);

						}

					}catch(Exception ex)
					{
						pValue = 1;
					}

				}else
				{

					pValue = 1;

				}

				DC_pValVector.add(pValue);
				winSizeVector.add(winSize);
			}

			eventIndex++;
			if(eventIndex < eventStream.size())
			{


				// update data structure related to detection window
				updateActivityFreqMap_addEvent(Activity_freq_detWin, eventStream.get(eventIndex));
				updateActivityFreqMap_removeEvent(detectionWindowSubLog, TraceId_TraceIndex_InSubLog_Map_detWin, Activity_freq_detWin, eventStream.get(eventIndex - winSize));

				updateActivityTraceSetMap_addEvent(eventStream.get(eventIndex), Activity_TraceSet_detWin);
				updateDirectFollowRelationFreqMap_addEvent(detectionWindowSubLog, DF_Relation_Freq_detWin, TraceId_TraceIndex_InSubLog_Map_detWin, eventStream.get(eventIndex), true);
				updateSubLog_addEvent(detectionWindowSubLog, TraceId_TraceIndex_InSubLog_Map_detWin, eventStream.get(eventIndex), true);

				updateActivityTraceSetMap_removeEvent(detectionWindowSubLog, TraceId_TraceIndex_InSubLog_Map_detWin, eventStream.get(eventIndex  - winSize), Activity_TraceSet_detWin);
				updateDirectFollowRelationFreqMap_removeEvent(detectionWindowSubLog, DF_Relation_Freq_detWin, TraceId_TraceIndex_InSubLog_Map_detWin, eventStream.get(eventIndex - winSize));
				updateSubLog_removeEvent(detectionWindowSubLog, TraceId_TraceIndex_InSubLog_Map_detWin, eventStream.get(eventIndex - winSize));

				// update data structure related to reference window
				updateActivityFreqMap_addEvent(Activity_freq_refWin, eventStream.get(eventIndex - winSize));
				updateActivityFreqMap_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, Activity_freq_refWin, eventStream.get(eventIndex - 2 * winSize));

				updateActivityTraceSetMap_addEvent(eventStream.get(eventIndex - winSize), Activity_TraceSet_refWin);
				updateDirectFollowRelationFreqMap_addEvent(referenceWindowSubLog, DF_Relation_Freq_refWin, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(eventIndex - winSize), true);
				updateSubLog_addEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(eventIndex - winSize), true);

				updateActivityTraceSetMap_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(eventIndex - 2 * winSize), Activity_TraceSet_refWin);
				updateDirectFollowRelationFreqMap_removeEvent(referenceWindowSubLog, DF_Relation_Freq_refWin, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(eventIndex - 2 * winSize));
				updateSubLog_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(eventIndex - 2 * winSize));

				if (this.winConfig == WindowConfig.ADWIN)
				{

					winSize = ADWin_DirectFollow_ver1(eventStream, eventIndex, winSize,
							detectionWindowSubLog, TraceId_TraceIndex_InSubLog_Map_detWin,
							referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin,
							Activity_freq_detWin, Activity_freq_refWin,
							DF_Relation_Freq_detWin, DF_Relation_Freq_refWin,
							Activity_TraceSet_detWin, Activity_TraceSet_refWin);

//					System.out.println(winSize);

				}

			}else
				break;


//			System.out.println("Total time: " + (System.currentTimeMillis() - t1));
		}

//		System.out.println("Average bigDiffCount = " + Utils.getAverage(bigDiffCounts.toArray()));

		List<Double> smoothPValues = DC_pValVector;

		// smoothing
//		smoothPValues = Utils.SMASmoothing(DC_pValVector, winSizeVector, smfDivideBy);
////		int curveNum = plotting.AddCurve("P-value curve smooth");


		plotting.addRaw(curveIndex, smoothPValues);

		MDcurves.retreiveDrift_EventStream(eventStream, eventStream.size(), logPath, smoothPValues,
				winSizeVector, typicalThreshold, DriftPointsList, driftConfig.toString(),
				0.5f, isCPNToolsLog, null);

		R_confMat = MDcurves.getConfMat(log.size());
		R_meandelay = R_confMat.getMeanDelay();


		String logPathString = logPath.toString();
		String winsPath = logPathString.substring(0, logPathString.lastIndexOf('.')) + "_results_" + driftConfig + "_winVector.csv";
		List<List<Integer>> listOfLists = new ArrayList<List<Integer>>();
		listOfLists.add(winSizeVector);
		Utils.writeToFile_Integer(winsPath, listOfLists);

		return plotting;

	}

	public LinePlot findDrifts_AlphaRelations(XLog eventStream, List<DriftPoint> DriftPointsList)
	{

		ControlFlowDriftCharacterizer characterizer = new ControlFlowDriftCharacterizer(logPath, logNameShort, minCharDataPoints, topCharzedDrifts, cutTopRelationsPercentage, considerChangeSignificance, withFragment, withPartialMatching);

		int winSize = initialwinSize;
		LinePlot plotting = new LinePlot(this.getClass().getName(), windowHeader, "Events of log", "P-value"); //

		double pValue=0;

		int curveIndex = plotting.AddCurve("P-value curve");
		ArrayList<Double> Alpha_pValVector = new ArrayList<>();
		ArrayList<Integer> winSizeVector = new ArrayList<>();


//		ArrayList<inLong> timePerformance = new ArrayList<>();


		int eventIndex;
		for (eventIndex = 0; eventIndex < 2 * winSize - 1; eventIndex++){//2*win-1 is the initial offset
			Alpha_pValVector.add(1.0);
			winSizeVector.add(winSize);
		}

		HashMap<String, Integer> TraceId_TraceIndex_InSubLog_Map_detWin = new HashMap<>();
		XLog detectionWindowSubLog = buildSubLogFromEventStream(eventStream, TraceId_TraceIndex_InSubLog_Map_detWin,
				(eventIndex + 1) - winSize, (eventIndex + 1));

		HashMap<String, Integer> TraceId_TraceIndex_InSubLog_Map_refWin = new HashMap<>();
		XLog referenceWindowSubLog = buildSubLogFromEventStream(eventStream, TraceId_TraceIndex_InSubLog_Map_refWin,
				(eventIndex + 1) - 2 * winSize, (eventIndex + 1) - winSize);

		HashMap<String, Integer> Relation_Freq_detWin = new HashMap<>();
		buildFollowRelationFrequency(detectionWindowSubLog, Relation_Freq_detWin);

		HashMap<String, Integer> Relation_Freq_refWin = new HashMap<>();
		buildFollowRelationFrequency(referenceWindowSubLog, Relation_Freq_refWin);

		HashMap<String, Integer> Activity_freq_detWin = new HashMap<>();
		HashMap<String, Set<String>> Activity_TraceSet_detWin = new HashMap<>();
		buildActivityFrequency(detectionWindowSubLog, Activity_freq_detWin, Activity_TraceSet_detWin);

		HashMap<String, Integer> Activity_freq_refWin = new HashMap<>();
		HashMap<String, Set<String>> Activity_TraceSet_refWin = new HashMap<>();
		buildActivityFrequency(referenceWindowSubLog, Activity_freq_refWin, Activity_TraceSet_refWin);

		HashMap<String, Integer> DF_Relation_Freq_detWin = new HashMap<>();
		buildDirectFollowRelationFrequency(detectionWindowSubLog, DF_Relation_Freq_detWin);

		HashMap<String, Integer> DF_Relation_Freq_refWin = new HashMap<>();
		buildDirectFollowRelationFrequency(referenceWindowSubLog, DF_Relation_Freq_refWin);

		Integer totalRelationFreq_bothWins[] = new Integer[2];
		totalRelationFreq_bothWins[0] = new Integer(0);
		totalRelationFreq_bothWins[1] = new Integer(0);

		Map<Pair<XEventClass, XEventClass>, RelationFrequency> pair_Relation_Map1 = null;
		Map<Pair<XEventClass, XEventClass>, RelationFrequency> pair_Relation_Map2 = null;

		BasicLogRelationsExt alphaRelations_det = null;
		XLogInfo summary = XLogInfoFactory.createLogInfo(detectionWindowSubLog, XLogInfoImpl.NAME_CLASSIFIER);
		alphaRelations_det = new BasicLogRelationsExt(detectionWindowSubLog, summary, relationNoiseThresh);

		BasicLogRelationsExt alphaRelations_ref = null;
		summary = XLogInfoFactory.createLogInfo(referenceWindowSubLog, XLogInfoImpl.NAME_CLASSIFIER);
		alphaRelations_ref = new BasicLogRelationsExt(referenceWindowSubLog, summary, relationNoiseThresh);

//		System.out.println(eventStream.size());
//		for(XTrace trace : eventStream)
//		{
//
//			System.out.println(XLogManager.getTraceID(trace) + ":" + XLogManager.getEventName(trace.get(0)));
//
//		}

		// Drift characterization
		Map<PairRelation, Integer> Relation_repFreq_map = new HashMap<PairRelation, Integer>();
		List<Map<PairRelation, Integer>> RelationFreqMap_Queue = new ArrayList();
		List<Integer> outputQueue = new LinkedList<Integer>();
		int numOfonesInOutputQueue = 0;
		int numOfzerosInOutputQueue = 0;
		boolean isBelowThreshold = false;
		int underCurveLength = 0;
		boolean isDriftCharacterized = true;
		int winSize_fbt = -1;
		int eventIndex_fbt = -1;
		int driftPoint = -1;
		boolean isNewDriftDetected = false;
		int numOfCharacterizedDrifts = 0;

		int subLogB_stIndex = (eventIndex + 1) - winSize, subLogB_endIndex = (eventIndex + 1);
		int subLogA_stIndex = 0, subLogA_endIndex = 0;
//		int minSubLogSize = winSize + minCharDataPoints;

		int characterizationCounter = 0;

		List<Long> time = new ArrayList<>();
		for (; ;) {
			long t1 = System.currentTimeMillis();

			totalRelationFreq_bothWins[0] = 0;
			totalRelationFreq_bothWins[1] = 0;

//					long Alpha_FreqMatrix1[][] = null;
			long Alpha_FreqMatrix2[][] = null;

//					pair_Relation_Map1 = new HashMap<>();
//					Alpha_FreqMatrix1 = build_AlphaRelations_FrequencyMatrix(detectionWindowSubLog, referenceWindowSubLog,
//							Relation_Freq_detWin, Relation_Freq_refWin, DF_Relation_Freq_detWin, DF_Relation_Freq_refWin, Activity_freq_detWin, Activity_freq_refWin,
//							Activity_TraceSet_detWin, Activity_TraceSet_refWin, totalRelationFreq_bothWins, pair_Relation_Map1,
//							alphaRelations_det, alphaRelations_ref);


//			if(pair_Relation_Map2 == null){

			pair_Relation_Map2 = new HashMap<>();
			Alpha_FreqMatrix2 = build_AlphaRelations_FrequencyMatrix(detectionWindowSubLog, referenceWindowSubLog,
					Relation_Freq_detWin, Relation_Freq_refWin, DF_Relation_Freq_detWin, DF_Relation_Freq_refWin, Activity_freq_detWin, Activity_freq_refWin,
					Activity_TraceSet_detWin, Activity_TraceSet_refWin, totalRelationFreq_bothWins, pair_Relation_Map2,
					alphaRelations_det, alphaRelations_ref,
					Relation_repFreq_map, RelationFreqMap_Queue, isNewDriftDetected, eventIndex);

//			}
			Map<Integer, Entry<Pair<XEventClass, XEventClass>, RelationFrequency>> index_relation = new HashMap<>();
//					else
//					{
//						long t2 = System.currentTimeMillis();
//				Alpha_FreqMatrix2 = update_AlphaRelations_FrequencyMatrix(pair_Relation_Map2, totalRelationFreq_bothWins,
//						Relation_repFreq_map, RelationFreqMap_Queue, isNewDriftDetected, eventIndex, index_relation);
//						System.out.println("update_AlphaRelations_FrequencyMatrix: " + (System.currentTimeMillis() - t2));

//					}


//				if(eventIndex == 14934)
//					System.out.println();

//					comparePair_Relation_Maps(pair_Relation_Map1, pair_Relation_Map2, eventIndex, eventStream.get(eventIndex).get(0));


//					System.out.println("build_AlphaRelations_FrequencyMatrix: " + (System.currentTimeMillis() - t1));
//					System.out.println(eventIndex + "," + winSize + "," + DC_FreqMatrix[1].length);
//					System.out.println("build_AbelBehaviouralProfile time: " + (System.currentTimeMillis() - t1));
//						System.out.println("before: " + Alpha_FreqMatrix[0].length + "  ");


//						for (int i = 0; i < Alpha_FreqMatrix2.length; i++) {
//
//							for (int k = 0; k < Alpha_FreqMatrix2[i].length; k++) {
//								System.out.print(Alpha_FreqMatrix2[i][k]+", ");
//							}
//
//							System.out.println();
//						}
//
//						System.out.println("/////////////////////////////////////");

//						System.out.println("totalRelationFreq[0] = " + totalRelationFreq_bothWins[0]);
//						System.out.println("distinct activities = " + Activity_freq_detWin.size());

//			System.out.println("before: " + Alpha_FreqMatrix2[0].length);

//				if(eventIndex >= 16199)
//					System.out.println();
//			if (this.noiseFilterConfig == NoiseFilterConfig.RemoveNoise_sum)
//				Alpha_FreqMatrix2 = removeNoise_sum(Alpha_FreqMatrix2, totalRelationFreq_bothWins[0], totalRelationFreq_bothWins[1]);
//			if (this.noiseFilterConfig == NoiseFilterConfig.RemoveNoise_max)
//				Alpha_FreqMatrix2 = removeNoise_max(Alpha_FreqMatrix2, index_relation);
//			else if (this.noiseFilterConfig == NoiseFilterConfig.RemoveNoise_bottomRelations)
//				Alpha_FreqMatrix2 = removeNoise_bottomRelations(Alpha_FreqMatrix2, totalRelationFreq_bothWins[0], totalRelationFreq_bothWins[1]);
//			else if (this.noiseFilterConfig == NoiseFilterConfig.Aggregation)
//				Alpha_FreqMatrix2 = aggregateNoise(Alpha_FreqMatrix2, totalRelationFreq_bothWins[0], totalRelationFreq_bothWins[1]);
//			else if (this.noiseFilterConfig == NoiseFilterConfig.RemoveInsignificantChanges)
//				Alpha_FreqMatrix2 = filterInsignificantChanges(Alpha_FreqMatrix2, totalRelationFreq_bothWins[0], totalRelationFreq_bothWins[1]);
//			else if (this.noiseFilterConfig == NoiseFilterConfig.RemoveFrequencyDrift)
//				Alpha_FreqMatrix2 = removeFrequencyDrift(Alpha_FreqMatrix2, totalRelationFreq_bothWins[0], totalRelationFreq_bothWins[1]);

//			System.out.println("after: " + Alpha_FreqMatrix2[0].length);

//					if(Alpha_FreqMatrix2[0].length > ReduceWhenDimensionsAbove)
//						Alpha_FreqMatrix2 = Utils.reduceDimensionality_cuttingColumnsWithFreqBelowThreshold(Alpha_FreqMatrix2, minExpectedFrequency);
//					System.out.println(eventIndex + "," + winSize + "," + Alpha_FreqMatrix2[1].length);
//						System.out.println("after: " + Alpha_FreqMatrix2[0].length);

//					System.out.println("winSize = " + winSize);
//					for (int i = 0; i < Alpha_FreqMatrix2.length; i++) {
//
//						for (int k = 0; k < Alpha_FreqMatrix2[i].length; k++) {
//							System.out.print(Alpha_FreqMatrix2[i][k]+", ");
//						}
//
//						System.out.println();
//					}
//
//					System.out.println("/////////////////////////////////////");

			Utils.removeZeroFreqColumns(Alpha_FreqMatrix2);

			if(Alpha_FreqMatrix2[0].length > 1)
			{

//						long t2 = System.currentTimeMillis();

				try{

					if(statisticTest == StatisticTestConfig.GTest)
					{

						GTest gt = new GTest();
						pValue = gt.gTestDataSetsComparison(Alpha_FreqMatrix2[1], Alpha_FreqMatrix2[0]);

					}else if(statisticTest == StatisticTestConfig.QS)
					{

						ChiSquareTest cst = new ChiSquareTest();
						pValue = cst.chiSquareTest(Alpha_FreqMatrix2);

					}

				}catch(Exception ex)
				{
					pValue = 1;
				}

//						System.out.println("Gtest: " + (System.currentTimeMillis() - t2));
			}else
			{

				pValue = 1;

			}
//			System.out.println(pValue);
//			if(Double.isNaN(pValue))
//				pValue = 0;

			if(withCharacterization)
			{
				if(charConfig == CharacterizationConfig.K_samplePermutationTest)
				{

					if(!isNewDriftDetected)
					{

						if(eventIndex % charWindowMoveInterval == 0)
						{
							if(outputQueue.size() == charBufferSize)
							{

								int discardedOutput = outputQueue.remove(0);
								if(discardedOutput == 1)
									numOfonesInOutputQueue--;
								else
									numOfzerosInOutputQueue--;

							}


							if(isDriftCharacterized)
							{

								outputQueue.add(0);
								numOfzerosInOutputQueue++;

//								subLogB_endIndex = eventIndex;

							}else
							{

								outputQueue.add(1);
								numOfonesInOutputQueue++;

//								subLogA_endIndex = eventIndex;

							}
						}


						if(/*numOfzerosInOutputQueue >= numOfonesInOutputQueue*/
								numOfonesInOutputQueue >= minCharDataPoints)
						{


							// do characterization

							// first remove the events that are potentially from the behavior after the next potential drift
//							int sublogAsize = subLogA_endIndex - subLogA_stIndex;
//							int diff = sublogAsize - winSize;
//							int cutSublogAby = diff > minSubLogSize ? (winSize) :
//								sublogAsize - minSubLogSize;
//							if(cutSublogAby > 0)
//							{
//
//								subLogA_endIndex -= cutSublogAby;
//
//							}
//
//							int sublogBsize = subLogB_endIndex - subLogB_stIndex;
//							sublogAsize = subLogA_endIndex - subLogA_stIndex;
//
//							int diff_sublogs = Math.abs(sublogBsize - sublogAsize);
//							if(sublogBsize > sublogAsize)
//							{
//
//								subLogB_endIndex -= diff_sublogs;
//
//							}else
//							{
//
//								subLogA_endIndex -= diff_sublogs;
//
//							}

							subLogA_endIndex = eventIndex;
							subLogA_stIndex = subLogA_endIndex - winSize - numOfonesInOutputQueue;

							XLog subLogB = XLogManager.getSubLog_eventBased(eventStream, subLogB_stIndex, subLogB_endIndex);
							XLog subLogA = XLogManager.getSubLog_eventBased(eventStream, subLogA_stIndex, subLogA_endIndex);

							if(Main.isLogGenerationForNick)
							{
								subLogB = XLogManager.getCompleteTracesSubLogFromSubTraceSubLog(subLogB, new String[]{Main.startActivity1, Main.startActivity2}, new String[]{Main.endActivity1});
								subLogA = XLogManager.getCompleteTracesSubLogFromSubTraceSubLog(subLogA, new String[]{Main.startActivity1, Main.startActivity2}, new String[]{Main.endActivity1});

								String mxmlLogPath = logPath.toString().substring(0, logPath.toString().lastIndexOf("\\")+1) + "_sublogB_" + numOfCharacterizedDrifts + ".mxml";
								ByteArrayOutputStream baos = XLogManager.saveLogInMemory(subLogB, mxmlLogPath);
								mxmlLogPath += ".gz";
								XLogManager.GzipLogAndSaveInDisk(baos, mxmlLogPath);

								mxmlLogPath = logPath.toString().substring(0, logPath.toString().lastIndexOf("\\")+1) + "_sublogA_" + numOfCharacterizedDrifts + ".mxml";
								baos = XLogManager.saveLogInMemory(subLogA, mxmlLogPath);
								mxmlLogPath += ".gz";
								XLogManager.GzipLogAndSaveInDisk(baos, mxmlLogPath);
							}

							List<String> charStatements = null;

							if(!Main.isLogGenerationForNick && charConfig == CharacterizationConfig.LogisticRegression)
								charStatements = characterizer.characterizeDrift_LogisticRegression(Relation_repFreq_map, RelationFreqMap_Queue, outputQueue,
										subLogB, subLogA);
							else if(!Main.isLogGenerationForNick && charConfig == CharacterizationConfig.K_samplePermutationTest)
								charStatements = characterizer.characterizeDrift_KSpermutationTest(Relation_repFreq_map, RelationFreqMap_Queue, outputQueue,
										subLogB, subLogA, startActivities, endActivities, IMConfig);
//							else
//								return null;

							characterizationMap.put(BigInteger.valueOf(driftPoint), charStatements);
							numOfCharacterizedDrifts++;

							resetCharacterizationDataStructures(Relation_repFreq_map, RelationFreqMap_Queue, outputQueue);
							isDriftCharacterized = true;
							numOfzerosInOutputQueue = numOfonesInOutputQueue;
							numOfonesInOutputQueue = 0;

							subLogB_stIndex = subLogA_stIndex;
							subLogB_endIndex = subLogA_endIndex;

						}

					}

					if(pValue > typicalThreshold)
					{

//						if(isNewDriftDetected)
//						{
//
////							minSubLogSize = winSize + minCharDataPoints;
//							subLogA_stIndex = eventIndex - winSize;
//							subLogA_endIndex = eventIndex;
//
//						}


						isBelowThreshold = false;
						winSize_fbt = -1;
						eventIndex_fbt = -1;
						underCurveLength = 0;
						isNewDriftDetected = false;

					}else
					{

						isBelowThreshold = true;
						if(winSize_fbt == -1)
						{
							winSize_fbt = winSize;
							eventIndex_fbt = eventIndex;
						}
						underCurveLength++;


						int ucLengthRequirement = (int)(oscilationFactor * winSize_fbt);
						if(!isNewDriftDetected && underCurveLength >= ucLengthRequirement)
						{

							if(!isDriftCharacterized)
							{

								// do characterization

								// first remove the events that are potentially from the behavior after next drift
//								int sublogAsize = subLogA_endIndex - subLogA_stIndex;
//								int diff = sublogAsize - winSize;
//								int cutSublogAby = diff > minSubLogSize ? (winSize) :
//									sublogAsize - minSubLogSize;
//								if(cutSublogAby > 0)
//								{
//
//									subLogA_endIndex -= cutSublogAby;
//
//								}
//
//								int sublogBsize = subLogB_endIndex - subLogB_stIndex;
//								sublogAsize = subLogA_endIndex - subLogA_stIndex;
//
//								int diff_sublogs = Math.abs(sublogBsize - sublogAsize);
//								if(sublogBsize > sublogAsize)
//								{
//
//									subLogB_endIndex -= diff_sublogs;
//
//								}else
//								{
//
//									subLogA_endIndex -= diff_sublogs;
//
//								}

								subLogA_endIndex = eventIndex;

								int diff2 = numOfonesInOutputQueue - winSize_fbt - ucLengthRequirement;
								int toDeleteTopCount = diff2 > minCharDataPoints ? (winSize_fbt + ucLengthRequirement) :
										numOfonesInOutputQueue - minCharDataPoints;
								if(toDeleteTopCount > 0)
								{

									removeTopEntitiesInCharacterizationQueues(Relation_repFreq_map,
											RelationFreqMap_Queue, outputQueue, toDeleteTopCount);

									numOfonesInOutputQueue -= toDeleteTopCount;

									subLogA_endIndex -= toDeleteTopCount;

								}

								subLogA_stIndex = subLogA_endIndex - winSize - numOfonesInOutputQueue;


								XLog subLogB = XLogManager.getSubLog_eventBased(eventStream, subLogB_stIndex, subLogB_endIndex);
								XLog subLogA = XLogManager.getSubLog_eventBased(eventStream, subLogA_stIndex, subLogA_endIndex);
								if(Main.isLogGenerationForNick)
								{
									subLogB = XLogManager.getCompleteTracesSubLogFromSubTraceSubLog(subLogB, new String[]{Main.startActivity1, Main.startActivity2}, new String[]{Main.endActivity1});
									subLogA = XLogManager.getCompleteTracesSubLogFromSubTraceSubLog(subLogA, new String[]{Main.startActivity1, Main.startActivity2}, new String[]{Main.endActivity1});

									String mxmlLogPath = logPath.toString().substring(0, logPath.toString().lastIndexOf("\\")+1) + "sublogB_" + numOfCharacterizedDrifts + ".mxml";
									ByteArrayOutputStream baos = XLogManager.saveLogInMemory(subLogB, mxmlLogPath);
									mxmlLogPath += ".gz";
									XLogManager.GzipLogAndSaveInDisk(baos, mxmlLogPath);

									mxmlLogPath = logPath.toString().substring(0, logPath.toString().lastIndexOf("\\")+1) + "sublogA_" + numOfCharacterizedDrifts + ".mxml";
									baos = XLogManager.saveLogInMemory(subLogA, mxmlLogPath);
									mxmlLogPath += ".gz";
									XLogManager.GzipLogAndSaveInDisk(baos, mxmlLogPath);
								}

								List<String> charStatements = null;

								if(!Main.isLogGenerationForNick && charConfig == CharacterizationConfig.LogisticRegression)
									charStatements = characterizer.characterizeDrift_LogisticRegression(Relation_repFreq_map, RelationFreqMap_Queue, outputQueue,
											subLogB, subLogA);
								else if(!Main.isLogGenerationForNick && charConfig == CharacterizationConfig.K_samplePermutationTest)
									charStatements = characterizer.characterizeDrift_KSpermutationTest(Relation_repFreq_map, RelationFreqMap_Queue, outputQueue,
											subLogB, subLogA, startActivities, endActivities, IMConfig);
//								else
//									return null;

								characterizationMap.put(BigInteger.valueOf(driftPoint), charStatements);
								numOfCharacterizedDrifts++;

								resetCharacterizationDataStructures(Relation_repFreq_map, RelationFreqMap_Queue, outputQueue);
								isDriftCharacterized = true;
								numOfzerosInOutputQueue = numOfonesInOutputQueue;
								numOfonesInOutputQueue = 0;

								subLogB_stIndex = subLogA_stIndex;
								subLogB_endIndex = subLogA_endIndex;

							}else
							{

								driftPoint = eventIndex_fbt;
								// remove the events that are potentially from the behavior after drift
//								int sublogBsize = subLogB_endIndex - subLogB_stIndex;
//								int diff = sublogBsize - winSize_fbt - ucLengthRequirement;
//								int cutSublogBby = diff > minSubLogSize ? (winSize_fbt + ucLengthRequirement) :
//									sublogBsize - minSubLogSize;
//								if(cutSublogBby > 0)
//								{
//
//									subLogB_endIndex -= cutSublogBby;
//
//								}
								subLogB_endIndex = eventIndex;

								int diff2 = numOfzerosInOutputQueue - winSize_fbt - ucLengthRequirement;
								int toDeleteTopCount = diff2 > minCharDataPoints ? (winSize_fbt + ucLengthRequirement) :
										numOfzerosInOutputQueue - minCharDataPoints;
								if(toDeleteTopCount > 0)
								{

									removeTopEntitiesInCharacterizationQueues(Relation_repFreq_map,
											RelationFreqMap_Queue, outputQueue, toDeleteTopCount);

									numOfzerosInOutputQueue -= toDeleteTopCount;

									subLogB_endIndex -= toDeleteTopCount;

								}

								int toDeleteBottomCount = numOfzerosInOutputQueue - minCharDataPoints;
								if(toDeleteBottomCount > 0)
								{

									removeBottomEntitiesInCharacterizationQueues(Relation_repFreq_map,
											RelationFreqMap_Queue, outputQueue, toDeleteBottomCount);

									numOfzerosInOutputQueue -= toDeleteBottomCount;

								}

								subLogB_stIndex = subLogB_endIndex - winSize_fbt - numOfzerosInOutputQueue;

							}
							isDriftCharacterized = false;
							isNewDriftDetected = true;

						}

					}
				}
			}

			Alpha_pValVector.add(pValue);
			winSizeVector.add(winSize);

			eventIndex++;
			if(eventIndex < eventStream.size() /*&& eventIndex < 100000*/)
			{

				// update data structure related to detection window
				updateActivityFreqMap_addEvent(Activity_freq_detWin, eventStream.get(eventIndex));
				updateActivityFreqMap_removeEvent(detectionWindowSubLog, TraceId_TraceIndex_InSubLog_Map_detWin, Activity_freq_detWin, eventStream.get(eventIndex - winSize));

				updateActivityTraceSetMap_addEvent(eventStream.get(eventIndex), Activity_TraceSet_detWin);
				updateFollowRelationFreqMap_addEvent(detectionWindowSubLog, Relation_Freq_detWin, TraceId_TraceIndex_InSubLog_Map_detWin, eventStream.get(eventIndex), true);
				updateDirectFollowRelationFreqMap_addEvent(detectionWindowSubLog, DF_Relation_Freq_detWin, TraceId_TraceIndex_InSubLog_Map_detWin, eventStream.get(eventIndex), true);
				updateAlphaRelations_addEvent(detectionWindowSubLog, TraceId_TraceIndex_InSubLog_Map_detWin, eventStream.get(eventIndex), true, true, alphaRelations_det, Activity_TraceSet_detWin);
//				updatePairRelationMap_addEvent(detectionWindowSubLog, TraceId_TraceIndex_InSubLog_Map_detWin, eventStream.get(eventIndex), true, true, pair_Relation_Map2, alphaRelations_det, Relation_Freq_detWin, Activity_TraceSet_detWin);

//								updateSubLog_addEvent(detectionWindowSubLog, TraceId_TraceIndex_InSubLog_Map_detWin, eventStream.get(eventIndex), true);

				updateActivityTraceSetMap_removeEvent(detectionWindowSubLog, TraceId_TraceIndex_InSubLog_Map_detWin, eventStream.get(eventIndex  - winSize), Activity_TraceSet_detWin);
				updateFollowRelationFreqMap_removeEvent(detectionWindowSubLog, Relation_Freq_detWin, TraceId_TraceIndex_InSubLog_Map_detWin, eventStream.get(eventIndex  - winSize), true);
				updateDirectFollowRelationFreqMap_removeEvent(detectionWindowSubLog, DF_Relation_Freq_detWin, TraceId_TraceIndex_InSubLog_Map_detWin, eventStream.get(eventIndex  - winSize));
				updateAlphaRelations_removeEvent(detectionWindowSubLog, TraceId_TraceIndex_InSubLog_Map_detWin, eventStream.get(eventIndex  - winSize), false, true, alphaRelations_det, Activity_TraceSet_detWin);
//				updatePairRelationMap_removeEvent(detectionWindowSubLog, TraceId_TraceIndex_InSubLog_Map_detWin, eventStream.get(eventIndex  - winSize), false, true, pair_Relation_Map2, alphaRelations_det, Relation_Freq_detWin, Activity_TraceSet_detWin);

//								updateSubLog_removeEvent(detectionWindowSubLog, TraceId_TraceIndex_InSubLog_Map_detWin, eventStream.get(eventIndex  - winSize));

				// update data structure related to reference window
				updateActivityFreqMap_addEvent(Activity_freq_refWin, eventStream.get(eventIndex - winSize));
				updateActivityFreqMap_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, Activity_freq_refWin, eventStream.get(eventIndex - 2 * winSize));

				updateActivityTraceSetMap_addEvent(eventStream.get(eventIndex - winSize), Activity_TraceSet_refWin);
				updateFollowRelationFreqMap_addEvent(referenceWindowSubLog, Relation_Freq_refWin, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(eventIndex - winSize), true);
				updateDirectFollowRelationFreqMap_addEvent(referenceWindowSubLog, DF_Relation_Freq_refWin, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(eventIndex - winSize), true);
				updateAlphaRelations_addEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(eventIndex - winSize), true, false, alphaRelations_ref, Activity_TraceSet_refWin);
//				updatePairRelationMap_addEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(eventIndex - winSize), true, false, pair_Relation_Map2, alphaRelations_ref, Relation_Freq_refWin, Activity_TraceSet_refWin);

//								updateSubLog_addEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(eventIndex - winSize), true);

				updateActivityTraceSetMap_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(eventIndex  - 2 * winSize), Activity_TraceSet_refWin);
				updateFollowRelationFreqMap_removeEvent(referenceWindowSubLog, Relation_Freq_refWin, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(eventIndex - 2 * winSize), false);
				updateDirectFollowRelationFreqMap_removeEvent(referenceWindowSubLog, DF_Relation_Freq_refWin, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(eventIndex - 2 * winSize));
				updateAlphaRelations_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(eventIndex - 2 * winSize), false, false, alphaRelations_ref, Activity_TraceSet_refWin);
//				updatePairRelationMap_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(eventIndex - 2 * winSize), false, false, pair_Relation_Map2, alphaRelations_ref, Relation_Freq_refWin, Activity_TraceSet_refWin);

//								updateSubLog_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(eventIndex - 2 * winSize));


				if (this.winConfig == WindowConfig.ADWIN)
				{

//					long t3 = System.currentTimeMillis();

					int oldWinSize = winSize;

					winSize = ADWin_AlphaRelations_ver1(eventStream, eventIndex, winSize,
							detectionWindowSubLog, referenceWindowSubLog,
							TraceId_TraceIndex_InSubLog_Map_detWin, TraceId_TraceIndex_InSubLog_Map_refWin,
							Activity_freq_detWin, Activity_freq_refWin,
							Activity_TraceSet_detWin, Activity_TraceSet_refWin);

//		            if(winSize > oldWinSize)
//		            {
					int winSizeLimit = eventStream.size() / winSizeDividedBy;
//		            	winSizeLimit = winSizeLimit > oldWinSize ? winSizeLimit : oldWinSize;
					if(winSize > winSizeLimit)
						winSize = winSizeLimit;
//		            }

//		            System.out.println(winSize);


					UpdateDataStructures_WithNewWinSize_AlphaRelations(eventStream, eventIndex, winSize, oldWinSize,
							detectionWindowSubLog, TraceId_TraceIndex_InSubLog_Map_detWin,
							referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin,
							Relation_Freq_detWin, Relation_Freq_refWin,
							DF_Relation_Freq_detWin, DF_Relation_Freq_refWin,
							Activity_freq_detWin, Activity_freq_refWin,
							Activity_TraceSet_detWin, Activity_TraceSet_refWin,
							pair_Relation_Map2,
							alphaRelations_det, alphaRelations_ref);

//					System.out.println("ADWin_AlphaRelations_ver1: " + (System.currentTimeMillis() - t3));

//					System.out.println(winSize);

				}

				if(withCharacterization)
					charBufferSize = 2 * (2 * winSize + minCharDataPoints);

//				System.out.println(System.currentTimeMillis() - t1);

			}else
				break;

//			System.out.println(eventIndex);

//			long t2 = (System.currentTimeMillis() - t1);
//			System.out.println("Total time: " + t2);
//			time.add(t2);

//			System.out.println(eventIndex + ": Average time: " + Utils.getAverage(time.toArray()));

//			if(in > 100 && in < 200)
//			{
//
//				timePerformance.add((System.currentTimeMillis() - t1));
//
//			}
//
//			if(in == 250)
//			{
//
//				String logPathString = logPath.toString();
//				String winsPath = logPathString.substring(0, logPathString.lastIndexOf('.')) + "_results_" + driftConfig + "_timePerformance.csv";
//				List<List<Long>> listOfLists = new ArrayList<List<Long>>();
//				listOfLists.add(timePerformance);
//				Utils.writeToFile_Long(winsPath, listOfLists);
//
//			}

		}

//		System.out.println("Average bigDiffCount = " + Utils.getAverage(bigDiffCounts.toArray()));

		List<Double> smoothPValues = Alpha_pValVector;

		// smoothing
//		smoothPValues = Utils.SMASmoothing(Alpha_pValVector, winSizeVector, 0.5f);
////		int curveNum = plotting.AddCurve("P-value curve smooth");


		plotting.addRaw(curveIndex, smoothPValues);


//		MDcurves.retreiveDrift_EventStream(eventStream, eventStream.size(), logPath, smoothPValues,
//				winSizeVector, typicalThreshold, Utils.copyDriftPointsList(DriftPointsList), driftConfig.toString(),
//				0.25f, isCPNToolsLog);

		MDcurves.retreiveDrift_EventStream(eventStream, eventStream.size(), logPath, smoothPValues,
				winSizeVector, typicalThreshold, Utils.copyDriftPointsList(DriftPointsList), driftConfig.toString(),
				oscilationFactor, isCPNToolsLog, characterizationMap);

//		MDcurves.retreiveDrift_EventStream(eventStream, eventStream.size(), logPath, smoothPValues,
//				winSizeVector, typicalThreshold, Utils.copyDriftPointsList(DriftPointsList), driftConfig.toString(),
//				0.75f, isCPNToolsLog);
//
//		MDcurves.retreiveDrift_EventStream(eventStream, eventStream.size(), logPath, smoothPValues,
//				winSizeVector, typicalThreshold, Utils.copyDriftPointsList(DriftPointsList), driftConfig.toString(),
//				1.00f, isCPNToolsLog);


		R_confMat = MDcurves.getConfMat(log.size());
		R_meandelay = R_confMat.getMeanDelay();


//		String logPathString = logPath.toString();
//		String winsPath = logPathString.substring(0, logPathString.lastIndexOf('.')) + "_results_" + driftConfig + "_winVector.csv";
//		List<List<Integer>> listOfLists = new ArrayList<List<Integer>>();
//		listOfLists.add(winSizeVector);
//		Utils.writeToFile_Integer(winsPath, listOfLists);
//
//
//		String pValuesPath = logPathString.substring(0, logPathString.lastIndexOf('.')) + "_results_" + driftConfig + "_pValueVector.csv";
//		List<List<Double>> listOfLists_pv = new ArrayList<List<Double>>();
//		listOfLists_pv.add(smoothPValues);
//		Utils.writeToFile_Double(pValuesPath, listOfLists_pv);

		return plotting;

	}

	/*public LinePlot findDrifts_BlockStructure(XLog eventStream, List<DriftPoint> DriftPointsList)
	{

		int winSize = initialwinSize;
		LinePlot plotting = new LinePlot(this.getClass().getName(), windowHeader, "Events of log", "P-value"); //

		double pValue=0;

		int curveIndex = plotting.AddCurve("P-value curve");
		ArrayList<Double> DC_pValVector = new ArrayList<>();
		ArrayList<Integer> winSizeVector = new ArrayList<>();


		int eventIndex;
		for (eventIndex = 0; eventIndex < 2 * winSize - 1; eventIndex++){//2*win-1 is the initial offset
			DC_pValVector.add(1.0);
			winSizeVector.add(winSize);
		}

		HashMap<String, Integer> TraceId_TraceIndex_InSubLog_Map_detWin = new HashMap<>();
		XLog detectionWindowSubLog = buildSubLogFromEventStream(eventStream, TraceId_TraceIndex_InSubLog_Map_detWin,
				(eventIndex + 1) - winSize, (eventIndex + 1));

		XLogManager.labelTraceCompletionStatus(detectionWindowSubLog, startActivities, endActivities);

		HashMap<String, Integer> TraceId_TraceIndex_InSubLog_Map_refWin = new HashMap<>();
		XLog referenceWindowSubLog = buildSubLogFromEventStream(eventStream, TraceId_TraceIndex_InSubLog_Map_refWin,
				(eventIndex + 1) - 2 * winSize, (eventIndex + 1) - winSize);

		XLogManager.labelTraceCompletionStatus(referenceWindowSubLog, startActivities, endActivities);

		HashMap<String, Integer> Activity_freq_detWin = new HashMap<>();
		HashMap<String, Set<String>> Activity_TraceSet_detWin = new HashMap<>();
		buildActivityFrequency(detectionWindowSubLog, Activity_freq_detWin, Activity_TraceSet_detWin);

		HashMap<String, Integer> Activity_freq_refWin = new HashMap<>();
		HashMap<String, Set<String>> Activity_TraceSet_refWin = new HashMap<>();
		buildActivityFrequency(referenceWindowSubLog, Activity_freq_refWin, Activity_TraceSet_refWin);

//		XLogInfo2 logSummary_det = XLogInfoFactory2.createLogInfo(detectionWindowSubLog, XLogInfoImpl.NAME_CLASSIFIER);
//		Dfg dfg_detWin = buildDfg(detectionWindowSubLog, logSummary_det, startActivities, endActivities, Activity_freq_detWin);

//		XLogInfo2 logSummary_ref = XLogInfoFactory2.createLogInfo(referenceWindowSubLog, XLogInfoImpl.NAME_CLASSIFIER);
//		Dfg dfg_refWin = buildDfg(referenceWindowSubLog, logSummary_ref, startActivities, endActivities, Activity_freq_refWin);

//		ProcessTree pt_det = IMProcessTree.mineProcessTree(detectionWindowSubLog, miningParameters);
//		ProcessTree pt_ref = IMProcessTree.mineProcessTree(referenceWindowSubLog, miningParameters);

		System.out.println(eventStream.size());
//		for(XTrace trace : eventStream)
//		{
//
//			System.out.println(XLogManager.getTraceID(trace) + ":" + XLogManager.getEventName(trace.get(0)));
//
//		}
		for (; ;) {

			long t1 = System.currentTimeMillis();
			if(eventIndex % 50 == 0)
			{

				ProcessTree pt_det = IMProcessTree.mineProcessTree(detectionWindowSubLog, miningParameters);
				ProcessTree pt_ref = IMProcessTree.mineProcessTree(referenceWindowSubLog, miningParameters);

				long BS_FreqMatrix[][] = build_BlockStructure_FrequencyMatrix(pt_det, pt_ref,
						Activity_freq_detWin, Activity_freq_refWin);

//				System.out.println(System.currentTimeMillis() - t1);

//					System.out.println(eventIndex + "," + winSize + "," + DC_FreqMatrix[1].length);
//					System.out.println("build_AbelBehaviouralProfile time: " + (System.currentTimeMillis() - t1));

//				if(DC_FreqMatrix[0].length > ReduceWhenDimensionsAbove)
//					DC_FreqMatrix = Utils.reduceDimensionality_cuttingColumnsWithFreqBelowThreshold(DC_FreqMatrix, minExpectedFrequency);
//					System.out.println(eventIndex + "," + winSize + "," + DC_FreqMatrix[1].length);

				if(statisticTest == StatisticTestConfig.GTest)
				{

					GTest gt = new GTest();
					pValue = gt.gTestDataSetsComparison(BS_FreqMatrix[1], BS_FreqMatrix[0]);

				}else if(statisticTest == StatisticTestConfig.QS)
				{

					ChiSquareTest cst = new ChiSquareTest();
					pValue = cst.chiSquareTest(BS_FreqMatrix);

				}

				DC_pValVector.add(pValue);
				winSizeVector.add(winSize);

			}else
			{
				DC_pValVector.add(pValue);
				winSizeVector.add(winSize);
			}

			eventIndex++;
			if(eventIndex < eventStream.size() && eventIndex < 20000)
			{

				// update data structure related to detection window
				updateActivityFreqMap_addEvent(Activity_freq_detWin, eventStream.get(eventIndex));
				updateActivityTraceSetMap_addEvent(eventStream.get(eventIndex), Activity_TraceSet_detWin);
//				boolean dfgChanged_detAdd = updateDfg_addEvent(dfg_detWin, logSummary_det, detectionWindowSubLog, TraceId_TraceIndex_InSubLog_Map_detWin, startActivities, endActivities, eventStream.get(eventIndex), true);
				XTrace trace = updateSubLog_addEvent(detectionWindowSubLog, TraceId_TraceIndex_InSubLog_Map_detWin, eventStream.get(eventIndex), true);

				XLogManager.labelTraceCompletionStatus(trace, startActivities, endActivities);

				updateActivityFreqMap_removeEvent(detectionWindowSubLog, TraceId_TraceIndex_InSubLog_Map_detWin, Activity_freq_detWin, eventStream.get(eventIndex - winSize));
				updateActivityTraceSetMap_removeEvent(detectionWindowSubLog, TraceId_TraceIndex_InSubLog_Map_detWin, eventStream.get(eventIndex  - winSize), Activity_TraceSet_detWin);
//				boolean dfgChanged_detRemove = updateDfg_removeEvent(dfg_detWin, logSummary_det, detectionWindowSubLog, TraceId_TraceIndex_InSubLog_Map_detWin, startActivities, endActivities, eventStream.get(eventIndex - winSize));
				trace = updateSubLog_removeEvent(detectionWindowSubLog, TraceId_TraceIndex_InSubLog_Map_detWin, eventStream.get(eventIndex - winSize));

				if(trace.size() > 0)
					XLogManager.labelTraceCompletionStatus(trace, startActivities, endActivities);

//				if(dfgChanged_detAdd || dfgChanged_detRemove)
//					pt_det = IMProcessTree.mineProcessTree(detectionWindowSubLog, miningParameters);

				// update data structure related to reference window
				updateActivityFreqMap_addEvent(Activity_freq_refWin, eventStream.get(eventIndex - winSize));
				updateActivityTraceSetMap_addEvent(eventStream.get(eventIndex - winSize), Activity_TraceSet_refWin);
//				boolean dfgChanged_refAdd = updateDfg_addEvent(dfg_refWin, logSummary_ref, referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, startActivities, endActivities, eventStream.get(eventIndex - winSize), true);
				trace = updateSubLog_addEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(eventIndex - winSize), true);

				XLogManager.labelTraceCompletionStatus(trace, startActivities, endActivities);

				updateActivityFreqMap_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, Activity_freq_refWin, eventStream.get(eventIndex - 2 * winSize));
				updateActivityTraceSetMap_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(eventIndex  - 2 * winSize), Activity_TraceSet_refWin);
//				boolean dfgChanged_refRemove = updateDfg_removeEvent(dfg_refWin, logSummary_ref, referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, startActivities, endActivities, eventStream.get(eventIndex - 2 * winSize));
				trace = updateSubLog_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(eventIndex - 2 * winSize));

				if(trace.size() > 0)
					XLogManager.labelTraceCompletionStatus(trace, startActivities, endActivities);
//				if(dfgChanged_refAdd || dfgChanged_refRemove)
//					pt_ref = IMProcessTree.mineProcessTree(referenceWindowSubLog, miningParameters);

				if (this.winConfig == WindowConfig.ADWIN)
				{

					List<Boolean> dfgChanged = new ArrayList<>();
					winSize = ADWin_BlockStructure_ver1(eventStream, eventIndex, winSize,
							detectionWindowSubLog, TraceId_TraceIndex_InSubLog_Map_detWin,
							referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin,
							Activity_freq_detWin, Activity_freq_refWin,
							Activity_TraceSet_detWin, Activity_TraceSet_refWin,
							dfg_detWin, dfg_refWin, logSummary_det, logSummary_ref,
							startActivities, endActivities, dfgChanged);

//					if(dfgChanged.get(0))
//						pt_det = IMProcessTree.mineProcessTree(detectionWindowSubLog, miningParameters);
//					if(dfgChanged.get(1))
//						pt_ref = IMProcessTree.mineProcessTree(referenceWindowSubLog, miningParameters);
//					System.out.println(winSize);

				}

			}else
				break;

//			System.out.println(eventIndex);
//			System.out.println(System.currentTimeMillis() - t1);

		}

		List<Double> smoothPValues = DC_pValVector;

		// smoothing
//		smoothPValues = Utils.SMASmoothing(DC_pValVector, winSizeVector, smfDivideBy);
////		int curveNum = plotting.AddCurve("P-value curve smooth");


		plotting.addRaw(curveIndex, smoothPValues);


		MDcurves.retreiveDrift_EventStream(eventStream, eventStream.size(), logPath, smoothPValues,
				winSizeVector, typicalThreshold, Utils.copyDriftPointsList(DriftPointsList), driftConfig.toString(),
				oscilationFactor, isCPNToolsLog, characterizationMap);


		R_confMat = MDcurves.getConfMat(log.size());
		R_meandelay = R_confMat.getMeanDelay();


		String logPathString = logPath.toString();
		String winsPath = logPathString.substring(0, logPathString.lastIndexOf('.')) + "_results_" + driftConfig + "_winVector.csv";
		List<List<Integer>> listOfLists = new ArrayList<List<Integer>>();
		listOfLists.add(winSizeVector);
		Utils.writeToFile_Integer(winsPath, listOfLists);

		return plotting;

	}

	*/
	// remove top x entities from the top(tail) of the queues
	private void removeTopEntitiesInCharacterizationQueues(Map<PairRelation, Integer> Relation_repFreq_map,
														   List<Map<PairRelation, Integer>> RelationFreqMap_Queue, List<Integer> outputQueue, int topEntities)
	{

		if(topEntities > outputQueue.size())
			return;

		for(int i = 0; i < topEntities; i++)
		{

			outputQueue.remove(outputQueue.size() - 1);
			Map<PairRelation, Integer> RelationFreqMap_first = RelationFreqMap_Queue.remove(RelationFreqMap_Queue.size() - 1);
			for (PairRelation relationKey : RelationFreqMap_first.keySet())
			{

				Integer relationRepFreq = Relation_repFreq_map.get(relationKey);
				if(relationRepFreq.intValue() == 1)
					Relation_repFreq_map.remove(relationKey);
				else
					Relation_repFreq_map.put(relationKey, relationRepFreq.intValue() - 1);

			}

		}

	}

	// remove bottom x entities from the bottom(head) of the queues
	private void removeBottomEntitiesInCharacterizationQueues(Map<PairRelation, Integer> Relation_repFreq_map,
															  List<Map<PairRelation, Integer>> RelationFreqMap_Queue, List<Integer> outputQueue, int bottomEntities)
	{

		if(bottomEntities > outputQueue.size())
			return;

		for(int i = 0; i < bottomEntities; i++)
		{

			outputQueue.remove(0);
			Map<PairRelation, Integer> RelationFreqMap_first = RelationFreqMap_Queue.remove(0);
			for (PairRelation relationKey : RelationFreqMap_first.keySet())
			{

				Integer relationRepFreq = Relation_repFreq_map.get(relationKey);
				if(relationRepFreq.intValue() == 1)
					Relation_repFreq_map.remove(relationKey);
				else
					Relation_repFreq_map.put(relationKey, relationRepFreq.intValue() - 1);

			}

		}

	}

	public void resetCharacterizationDataStructures(Map<PairRelation, Integer> Relation_repFreq_map,
													List<Map<PairRelation, Integer>> RelationFreqMap_Queue, List<Integer> outputQueue)
	{

		int queueSize = outputQueue.size();
		for(int i = 0; i < queueSize; i++)
		{

			Integer output = outputQueue.get(0);
			if(output == 1)
			{

				outputQueue.remove(0);
				outputQueue.add(0);
				Map<PairRelation, Integer> RelationFreqMap_first = RelationFreqMap_Queue.remove(0);
				RelationFreqMap_Queue.add(RelationFreqMap_first);

			}else
			{

				outputQueue.remove(0);
				Map<PairRelation, Integer> RelationFreqMap_first = RelationFreqMap_Queue.remove(0);
				for (PairRelation relationKey : RelationFreqMap_first.keySet())
				{

					Integer relationRepFreq = Relation_repFreq_map.get(relationKey);
					if(relationRepFreq.intValue() == 1)
						Relation_repFreq_map.remove(relationKey);
					else
						Relation_repFreq_map.put(relationKey, relationRepFreq.intValue() - 1);

				}

			}

		}

	}

//	public LinePlot findDrifts_AbelBehaviouralProfile(XLog eventStream, List<DriftPoint> DriftPointsList)
//	{
//
//
//		int winSize = initialwinSize;
//		LinePlot plotting = new LinePlot(this.getClass().getName(), windowHeader, "Events of log", "P-value"); //
//
//		double pValue=0;
//
//		int curveIndex = plotting.AddCurve("P-value curve");
//		ArrayList<Double> DC_pValVector = new ArrayList<>();
//		ArrayList<Integer> winSizeVector = new ArrayList<>();
//
//
//		int eventIndex;
//		for (eventIndex = 0; eventIndex < 2 * winSize - 1; eventIndex++){//2*win-1 is the initial offset
//			DC_pValVector.add(1.0);
//			winSizeVector.add(winSize);
//		}
//
//		HashMap<String, Integer> TraceId_TraceIndex_InSubLog_Map_detWin = new HashMap<>();
//		XLog detectionWindowSubLog = buildSubLogFromEventStream(eventStream, TraceId_TraceIndex_InSubLog_Map_detWin,
//				(eventIndex + 1) - winSize, (eventIndex + 1));
//
//		HashMap<String, Integer> TraceId_TraceIndex_InSubLog_Map_refWin = new HashMap<>();
//		XLog referenceWindowSubLog = buildSubLogFromEventStream(eventStream, TraceId_TraceIndex_InSubLog_Map_refWin,
//				(eventIndex + 1) - 2 * winSize, (eventIndex + 1) - winSize);
//
////		System.out.println(eventStream.size());
////		for(XTrace trace : eventStream)
////		{
////
////			System.out.println(XLogManager.getTraceID(trace) + ":" + XLogManager.getEventName(trace.get(0)));
////
////		}
//		for (; ;) {
////			long t1 = System.currentTimeMillis();
//
//			if(eventIndex % 1 == 0)
//			{
//				switch (statisticTest) {
//
//					case GTest:
//					{
//						//----ADWIN
//						if (this.winConfig == WindowConfig.ADWIN)
//						{
//
//	//						winSize = ADWin_DC_GTest(log, strHandler, winSize, eventIndex);
//
//						}
//
//
//						long DC_FreqMatrix[][] = build_AbelBehaviouralProfile_FrequencyMatrix(detectionWindowSubLog, referenceWindowSubLog);
//	//					System.out.println(eventIndex + "," + winSize + "," + DC_FreqMatrix[1].length);
//	//					System.out.println("build_AbelBehaviouralProfile time: " + (System.currentTimeMillis() - t1));
//
//						if(DC_FreqMatrix[0].length > ReduceWhenDimensionsAbove)
//							DC_FreqMatrix = Utils.reduceDimensionality_cuttingColumnsWithFreqBelowThreshold(DC_FreqMatrix, minExpectedFrequency);
//	//					System.out.println(eventIndex + "," + winSize + "," + DC_FreqMatrix[1].length);
//
//						if(DC_FreqMatrix[1].length > 1)
//						{
//
////							System.out.println(DC_FreqMatrix[1].length);
//							GTest gt = new GTest();
//							pValue = gt.gTestDataSetsComparison(DC_FreqMatrix[1], DC_FreqMatrix[0]);
//
//						}else
//						{
//
//							pValue = 1;
//
//						}
//						break;
//
//					}
//
//					default:
//						break;
//
//				}
//
//				DC_pValVector.add(pValue);
//				winSizeVector.add(winSize);
//			}
//
//			eventIndex++;
//			if(eventIndex < eventStream.size())
//			{
//
//				updateSubLog_addEvent(detectionWindowSubLog, TraceId_TraceIndex_InSubLog_Map_detWin, eventStream.get(eventIndex), true);
//				updateSubLog_removeEvent(detectionWindowSubLog, TraceId_TraceIndex_InSubLog_Map_detWin, eventStream.get((eventIndex + 1) - winSize));
//				updateSubLog_addEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get((eventIndex + 1) - winSize), true);
//				updateSubLog_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get((eventIndex + 1) - 2 * winSize));
//
//			}else
//				break;
//
//
////			System.out.println("Total time: " + (System.currentTimeMillis() - t1));
//		}
//
//		plotting.addRaw(curveIndex, DC_pValVector);
//
//		MDcurves.retreiveDrift_EventStream(eventStream, eventStream.size(), logPath, DC_pValVector,
//				winSizeVector, typicalThreshold, Utils.copyDriftPointsList(DriftPointsList), driftConfig.toString(),
//				0.5f, isCPNToolsLog);
//
//
//		R_confMat = MDcurves.getConfMat(log.size());
//		R_meandelay = R_confMat.getMeanDelay();
//
//
//		String logPathString = logPath.toString();
//		String winsPath = logPathString.substring(0, logPathString.lastIndexOf('.')) + "_results_" + driftConfig + "_winVector.csv";
//		List<List<Integer>> listOfLists = new ArrayList<List<Integer>>();
//		listOfLists.add(winSizeVector);
//		Utils.writeToFile_Integer(winsPath, listOfLists);
//
//		return plotting;
//
//	}
//

	public int ADWin_Follow_ver1(XLog eventStream, int eventIndex, int winSize,
								 XLog detectionWindowSubLog, HashMap<String, Integer> TraceId_TraceIndex_InSubLog_Map_detWin,
								 XLog referenceWindowSubLog, HashMap<String, Integer> TraceId_TraceIndex_InSubLog_Map_refWin,
								 HashMap<String, Integer> Activity_freq_detWin, HashMap<String, Integer> Activity_freq_refWin,
								 HashMap<String, Integer> Relation_Freq_detWin, HashMap<String, Integer> Relation_Freq_refWin,
								 HashMap<String, Set<String>> Activity_TraceSet_detWin, HashMap<String, Set<String>> Activity_TraceSet_refWin)
	{

		int numOfDistinctActivities = Math.max(Activity_freq_detWin.size(), Activity_freq_refWin.size());
		int suitableWindowSize = numOfDistinctActivities * numOfDistinctActivities * winSizeCoefficient;

		int diff = Math.min(suitableWindowSize - winSize, (eventIndex - 2*winSize) / 2);

		boolean hasWindowsExpanded = false, hasWindowsShrunk = false;

		while(diff != 0 && (!hasWindowsExpanded || !hasWindowsShrunk))
		{
//			System.out.println(diff);

			if(diff > 0)
			{

				hasWindowsExpanded = true;

				// expand detection window and push reference window
				int detWinStartIndex__ = (eventIndex - winSize);
				int i = detWinStartIndex__;
				for(; i > detWinStartIndex__ - diff; i--)
				{

					// add events to the data structure related to detection window
					updateActivityFreqMap_addEvent(Activity_freq_detWin, eventStream.get(i));
					updateActivityTraceSetMap_addEvent(eventStream.get(i), Activity_TraceSet_detWin);
					updateFollowRelationFreqMap_addEvent(detectionWindowSubLog, Relation_Freq_detWin, TraceId_TraceIndex_InSubLog_Map_detWin, eventStream.get(i), false);
					updateSubLog_addEvent(detectionWindowSubLog, TraceId_TraceIndex_InSubLog_Map_detWin, eventStream.get(i), false);

					// update data structure related to reference window
					updateActivityFreqMap_addEvent(Activity_freq_refWin, eventStream.get(i - winSize));
					updateActivityTraceSetMap_addEvent(eventStream.get(i - winSize), Activity_TraceSet_refWin);
					updateFollowRelationFreqMap_addEvent(referenceWindowSubLog, Relation_Freq_refWin, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(i - winSize), false);
					updateSubLog_addEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(i - winSize), false);

					updateActivityFreqMap_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, Activity_freq_refWin, eventStream.get(i));
					updateActivityTraceSetMap_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(i), Activity_TraceSet_refWin);
					updateFollowRelationFreqMap_removeEvent(referenceWindowSubLog, Relation_Freq_refWin, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(i), false);
					updateSubLog_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(i));

				}

				// expand reference window
				int index = i - winSize;
				for(int j = index; j > (index - diff); j--)
				{

					// add events to the data structure related to reference window
					updateActivityFreqMap_addEvent(Activity_freq_refWin, eventStream.get(j));
					updateActivityTraceSetMap_addEvent(eventStream.get(j), Activity_TraceSet_refWin);
					updateFollowRelationFreqMap_addEvent(referenceWindowSubLog, Relation_Freq_refWin, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(j), false);
					updateSubLog_addEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(j), false);

				}

			}else
			{

				hasWindowsShrunk = true;

				// shrink detection window and pull reference window
				int detWinStartIndex = (eventIndex - winSize + 1);
				int i = detWinStartIndex;
				for(; i < detWinStartIndex - diff; i++)
				{

					// remove events from the data structure related to detection window
					updateActivityFreqMap_removeEvent(detectionWindowSubLog, TraceId_TraceIndex_InSubLog_Map_detWin, Activity_freq_detWin, eventStream.get(i));
					updateActivityTraceSetMap_removeEvent(detectionWindowSubLog, TraceId_TraceIndex_InSubLog_Map_detWin, eventStream.get(i), Activity_TraceSet_detWin);
					updateFollowRelationFreqMap_removeEvent(detectionWindowSubLog, Relation_Freq_detWin, TraceId_TraceIndex_InSubLog_Map_detWin, eventStream.get(i), true);
					updateSubLog_removeEvent(detectionWindowSubLog, TraceId_TraceIndex_InSubLog_Map_detWin, eventStream.get(i));

					// update data structure related to reference window
					updateActivityFreqMap_addEvent(Activity_freq_refWin, eventStream.get(i));
					updateActivityTraceSetMap_addEvent(eventStream.get(i), Activity_TraceSet_refWin);
					updateFollowRelationFreqMap_addEvent(referenceWindowSubLog, Relation_Freq_refWin, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(i), true);
					updateSubLog_addEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(i), true);

					updateActivityFreqMap_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, Activity_freq_refWin, eventStream.get(i - winSize));
					updateActivityTraceSetMap_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(i - winSize), Activity_TraceSet_refWin);
					updateFollowRelationFreqMap_removeEvent(referenceWindowSubLog, Relation_Freq_refWin, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(i - winSize), false);
					updateSubLog_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(i - winSize));

				}

				// shrink reference window
				int index = i - winSize;
				for(int j = index; j < (index - diff); j++)
				{

					// remove events from the data structure related to reference window
					updateActivityFreqMap_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, Activity_freq_refWin, eventStream.get(j));
					updateActivityTraceSetMap_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(j), Activity_TraceSet_refWin);
					updateFollowRelationFreqMap_removeEvent(referenceWindowSubLog, Relation_Freq_refWin, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(j), false);
					updateSubLog_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(j));

				}

			}

			winSize += diff;
			numOfDistinctActivities = Math.max(Activity_freq_detWin.size(), Activity_freq_refWin.size());
			suitableWindowSize = numOfDistinctActivities * numOfDistinctActivities * winSizeCoefficient;
			diff = Math.min(suitableWindowSize - winSize, (eventIndex - 2*winSize) / 2);
		}

		return winSize;

	}



	public int ADWin_DirectFollow_ver1(XLog eventStream, int eventIndex, int winSize,
									   XLog detectionWindowSubLog, HashMap<String, Integer> TraceId_TraceIndex_InSubLog_Map_detWin,
									   XLog referenceWindowSubLog, HashMap<String, Integer> TraceId_TraceIndex_InSubLog_Map_refWin,
									   HashMap<String, Integer> Activity_freq_detWin, HashMap<String, Integer> Activity_freq_refWin,
									   HashMap<String, Integer> DF_Relation_Freq_detWin, HashMap<String, Integer> DF_Relation_Freq_refWin,
									   HashMap<String, Set<String>> Activity_TraceSet_detWin, HashMap<String, Set<String>> Activity_TraceSet_refWin)
	{

		int numOfDistinctActivities = Math.max(Activity_freq_detWin.size(), Activity_freq_refWin.size());
		int suitableWindowSize = numOfDistinctActivities * numOfDistinctActivities * winSizeCoefficient;

		int diff = Math.min(suitableWindowSize - winSize, (eventIndex - 2*winSize) / 2);

		boolean hasWindowsExpanded = false, hasWindowsShrunk = false;

		while(diff != 0 && (!hasWindowsExpanded || !hasWindowsShrunk))
		{
//			System.out.println(diff);

			if(diff > 0)
			{

				hasWindowsExpanded = true;

				// expand detection window and push reference window
				int detWinStartIndex__ = (eventIndex - winSize);
				int i = detWinStartIndex__;
				for(; i > detWinStartIndex__ - diff; i--)
				{

					// add events to the data structure related to detection window
					updateActivityFreqMap_addEvent(Activity_freq_detWin, eventStream.get(i));
					updateActivityTraceSetMap_addEvent(eventStream.get(i), Activity_TraceSet_detWin);
					updateDirectFollowRelationFreqMap_addEvent(detectionWindowSubLog, DF_Relation_Freq_detWin, TraceId_TraceIndex_InSubLog_Map_detWin, eventStream.get(i), false);
					updateSubLog_addEvent(detectionWindowSubLog, TraceId_TraceIndex_InSubLog_Map_detWin, eventStream.get(i), false);

					// update data structure related to reference window
					updateActivityFreqMap_addEvent(Activity_freq_refWin, eventStream.get(i - winSize));
					updateActivityTraceSetMap_addEvent(eventStream.get(i - winSize), Activity_TraceSet_refWin);
					updateDirectFollowRelationFreqMap_addEvent(referenceWindowSubLog, DF_Relation_Freq_refWin, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(i - winSize), false);
					updateSubLog_addEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(i - winSize), false);

					updateActivityFreqMap_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, Activity_freq_refWin, eventStream.get(i));
					updateActivityTraceSetMap_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(i), Activity_TraceSet_refWin);
					updateDirectFollowRelationFreqMap_removeEvent(referenceWindowSubLog, DF_Relation_Freq_refWin, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(i));
					updateSubLog_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(i));

				}

				// expand reference window
				int index = i - winSize;
				for(int j = index; j > (index - diff); j--)
				{

					// add events to the data structure related to reference window
					updateActivityFreqMap_addEvent(Activity_freq_refWin, eventStream.get(j));
					updateActivityTraceSetMap_addEvent(eventStream.get(j), Activity_TraceSet_refWin);
					updateDirectFollowRelationFreqMap_addEvent(referenceWindowSubLog, DF_Relation_Freq_refWin, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(j), false);
					updateSubLog_addEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(j), false);

				}

			}else
			{

				hasWindowsShrunk = true;

				// shrink detection window and pull reference window
				int detWinStartIndex = (eventIndex - winSize + 1);
				int i = detWinStartIndex;
				for(; i < detWinStartIndex - diff; i++)
				{

					// remove events from the data structure related to detection window
					updateActivityFreqMap_removeEvent(detectionWindowSubLog, TraceId_TraceIndex_InSubLog_Map_detWin, Activity_freq_detWin, eventStream.get(i));
					updateActivityTraceSetMap_removeEvent(detectionWindowSubLog, TraceId_TraceIndex_InSubLog_Map_detWin, eventStream.get(i), Activity_TraceSet_detWin);
					updateDirectFollowRelationFreqMap_removeEvent(detectionWindowSubLog, DF_Relation_Freq_detWin, TraceId_TraceIndex_InSubLog_Map_detWin, eventStream.get(i));
					updateSubLog_removeEvent(detectionWindowSubLog, TraceId_TraceIndex_InSubLog_Map_detWin, eventStream.get(i));

					// update data structure related to reference window
					updateActivityFreqMap_addEvent(Activity_freq_refWin, eventStream.get(i));
					updateActivityTraceSetMap_addEvent(eventStream.get(i), Activity_TraceSet_refWin);
					updateDirectFollowRelationFreqMap_addEvent(referenceWindowSubLog, DF_Relation_Freq_refWin, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(i), true);
					updateSubLog_addEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(i), true);

					updateActivityFreqMap_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, Activity_freq_refWin, eventStream.get(i - winSize));
					updateActivityTraceSetMap_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(i - winSize), Activity_TraceSet_refWin);
					updateDirectFollowRelationFreqMap_removeEvent(referenceWindowSubLog, DF_Relation_Freq_refWin, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(i - winSize));
					updateSubLog_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(i - winSize));

				}

				// shrink reference window
				int index = i - winSize;
				for(int j = index; j < (index - diff); j++)
				{

					// remove events from the data structure related to reference window
					updateActivityFreqMap_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, Activity_freq_refWin, eventStream.get(j));
					updateActivityTraceSetMap_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(j), Activity_TraceSet_refWin);
					updateDirectFollowRelationFreqMap_removeEvent(referenceWindowSubLog, DF_Relation_Freq_refWin, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(j));
					updateSubLog_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(j));

				}

			}

			winSize += diff;
			numOfDistinctActivities = Math.max(Activity_freq_detWin.size(), Activity_freq_refWin.size());
			suitableWindowSize = numOfDistinctActivities * numOfDistinctActivities * winSizeCoefficient;
			diff = Math.min(suitableWindowSize - winSize, (eventIndex - 2*winSize) / 2);
		}

		return winSize;

	}


	// adaptive window size based on n^2 * winSizeCoefficient
	public int ADWin_BlockStructure_ver1(XLog eventStream,
										 int eventIndex,
										 int winSize,
										 XLog detectionWindowSubLog,
										 HashMap<String, Integer> TraceId_TraceIndex_InSubLog_Map_detWin,
										 XLog referenceWindowSubLog,
										 HashMap<String, Integer> TraceId_TraceIndex_InSubLog_Map_refWin,
										 HashMap<String, Integer> Activity_freq_detWin,
										 HashMap<String, Integer> Activity_freq_refWin,
										 HashMap<String, Set<String>> Activity_TraceSet_detWin,
										 HashMap<String, Set<String>> Activity_TraceSet_refWin/*,
			Dfg dfg_detWin,
			Dfg dfg_refWin,
			XLogInfo2 logSummary_det,
			XLogInfo2 logSummary_ref,
			Set<String> startActivities,
			Set<String> endActivities,
			List<Boolean> dfgChanged*/)
	{

		int numOfDistinctActivities = Math.max(Activity_freq_detWin.size(), Activity_freq_refWin.size());
		int suitableWindowSize = numOfDistinctActivities * numOfDistinctActivities * winSizeCoefficient;

		int diff = Math.min(suitableWindowSize - winSize, (eventIndex - 2*winSize) / 2);

		boolean hasWindowsExpanded = false, hasWindowsShrunk = false;

//		boolean dfgChanged_det = false, dfgChanged_ref = false;

		while(diff != 0 && (!hasWindowsExpanded || !hasWindowsShrunk))
		{
//			System.out.println(diff);

			if(diff > 0)
			{

				hasWindowsExpanded = true;

				// expand detection window and push reference window
				int detWinStartIndex__ = (eventIndex - winSize);
				int i = detWinStartIndex__;
				for(; i > detWinStartIndex__ - diff; i--)
				{

					// add events to the data structure related to detection window
					updateActivityFreqMap_addEvent(Activity_freq_detWin, eventStream.get(i));
					updateActivityTraceSetMap_addEvent(eventStream.get(i), Activity_TraceSet_detWin);
//					boolean dfgChanged_detAdd = updateDfg_addEvent(dfg_detWin, logSummary_det, detectionWindowSubLog, TraceId_TraceIndex_InSubLog_Map_detWin, startActivities, endActivities, eventStream.get(i), false);
					updateSubLog_addEvent(detectionWindowSubLog, TraceId_TraceIndex_InSubLog_Map_detWin, eventStream.get(i), false);

					// update data structure related to reference window
					updateActivityFreqMap_addEvent(Activity_freq_refWin, eventStream.get(i - winSize));
					updateActivityTraceSetMap_addEvent(eventStream.get(i - winSize), Activity_TraceSet_refWin);
//					boolean dfgChanged_refAdd = updateDfg_addEvent(dfg_refWin, logSummary_ref, referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, startActivities, endActivities, eventStream.get(i - winSize), false);
					updateSubLog_addEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(i - winSize), false);

					updateActivityFreqMap_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, Activity_freq_refWin, eventStream.get(i));
					updateActivityTraceSetMap_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(i), Activity_TraceSet_refWin);
//					boolean dfgChanged_refRemove = updateDfg_removeEvent(dfg_refWin, logSummary_ref, referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, startActivities, endActivities, eventStream.get(i));
					updateSubLog_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(i));

//					if(dfgChanged_detAdd)
//						dfgChanged_det = true;
//
//					if(dfgChanged_refAdd || dfgChanged_refRemove)
//						dfgChanged_ref = true;

				}

				// expand reference window
				int index = i - winSize;
				for(int j = index; j > (index - diff); j--)
				{

					// add events to the data structure related to reference window
					updateActivityFreqMap_addEvent(Activity_freq_refWin, eventStream.get(j));
					updateActivityTraceSetMap_addEvent(eventStream.get(j), Activity_TraceSet_refWin);
//					boolean dfgChanged_refAdd = updateDfg_addEvent(dfg_refWin, logSummary_ref, referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, startActivities, endActivities, eventStream.get(j), false);
					updateSubLog_addEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(j), false);

//					if(dfgChanged_refAdd)
//						dfgChanged_ref = true;
				}

			}else
			{

				hasWindowsShrunk = true;

				// shrink detection window and pull reference window
				int detWinStartIndex = (eventIndex - winSize + 1);
				int i = detWinStartIndex;
				for(; i < detWinStartIndex - diff; i++)
				{

					// remove events from the data structure related to detection window
					updateActivityFreqMap_removeEvent(detectionWindowSubLog, TraceId_TraceIndex_InSubLog_Map_detWin, Activity_freq_detWin, eventStream.get(i));
					updateActivityTraceSetMap_removeEvent(detectionWindowSubLog, TraceId_TraceIndex_InSubLog_Map_detWin, eventStream.get(i), Activity_TraceSet_detWin);
//					boolean dfgChanged_detRemove = updateDfg_removeEvent(dfg_detWin, logSummary_det, detectionWindowSubLog, TraceId_TraceIndex_InSubLog_Map_detWin, startActivities, endActivities, eventStream.get(i));
					updateSubLog_removeEvent(detectionWindowSubLog, TraceId_TraceIndex_InSubLog_Map_detWin, eventStream.get(i));

					// update data structure related to reference window
					updateActivityFreqMap_addEvent(Activity_freq_refWin, eventStream.get(i));
					updateActivityTraceSetMap_addEvent(eventStream.get(i), Activity_TraceSet_refWin);
//					boolean dfgChanged_refAdd = updateDfg_addEvent(dfg_refWin, logSummary_ref, referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, startActivities, endActivities, eventStream.get(i), true);
					updateSubLog_addEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(i), true);

					updateActivityFreqMap_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, Activity_freq_refWin, eventStream.get(i - winSize));
					updateActivityTraceSetMap_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(i - winSize), Activity_TraceSet_refWin);
//					boolean dfgChanged_refRemove = updateDfg_removeEvent(dfg_refWin, logSummary_ref, referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, startActivities, endActivities, eventStream.get(i - winSize));
					updateSubLog_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(i - winSize));

//					if(dfgChanged_detRemove)
//						dfgChanged_det = true;
//
//					if(dfgChanged_refAdd || dfgChanged_refRemove)
//						dfgChanged_ref = true;
				}

				// shrink reference window
				int index = i - winSize;
				for(int j = index; j < (index - diff); j++)
				{

					// remove events from the data structure related to reference window
					updateActivityFreqMap_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, Activity_freq_refWin, eventStream.get(j));
					updateActivityTraceSetMap_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(j), Activity_TraceSet_refWin);
//					boolean dfgChanged_refRemove = updateDfg_removeEvent(dfg_refWin, logSummary_ref, referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, startActivities, endActivities, eventStream.get(j));
					updateSubLog_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(j));

//					if(dfgChanged_refRemove)
//						dfgChanged_ref = true;
				}

			}

			winSize += diff;
			numOfDistinctActivities = Math.max(Activity_freq_detWin.size(), Activity_freq_refWin.size());
			suitableWindowSize = numOfDistinctActivities * numOfDistinctActivities * winSizeCoefficient;
			diff = Math.min(suitableWindowSize - winSize, (eventIndex - 2*winSize) / 2);
		}

//		if(dfgChanged == null)
//			dfgChanged = new ArrayList<>();
//
//		dfgChanged.add(new Boolean(dfgChanged_det));
//		dfgChanged.add(new Boolean(dfgChanged_ref));

		return winSize;

	}


	// adaptive window size based on (n+1)^2 * winSizeCoefficient
	public int ADWin_BlockStructure_ver2(XLog eventStream, int eventIndex, int winSize,
										 XLog detectionWindowSubLog, HashMap<String, Integer> TraceId_TraceIndex_InSubLog_Map_detWin,
										 XLog referenceWindowSubLog, HashMap<String, Integer> TraceId_TraceIndex_InSubLog_Map_refWin,
										 HashMap<String, Integer> Activity_freq_detWin, HashMap<String, Integer> Activity_freq_refWin,
										 HashMap<String, Set<String>> Activity_TraceSet_detWin, HashMap<String, Set<String>> Activity_TraceSet_refWin)
	{

		int numOfDistinctActivities = Activity_freq_refWin.size();
		int suitableWindowSize = (numOfDistinctActivities + 1) * (numOfDistinctActivities + 1) * winSizeCoefficient;

		int diff = Math.min(suitableWindowSize - winSize, (eventIndex - 2*winSize) / 2);

		boolean hasWindowsExpanded = false, hasWindowsShrunk = false;

		while(diff != 0 && (!hasWindowsExpanded || !hasWindowsShrunk))
		{
//			System.out.println(diff);

			if(diff > 0)
			{

				hasWindowsExpanded = true;

				// expand detection window and push reference window
				int detWinStartIndex__ = (eventIndex - winSize);
				int i = detWinStartIndex__;
				for(; i > detWinStartIndex__ - diff; i--)
				{

					// add events to the data structure related to detection window
					updateActivityFreqMap_addEvent(Activity_freq_detWin, eventStream.get(i));
					updateActivityTraceSetMap_addEvent(eventStream.get(i), Activity_TraceSet_detWin);
					updateSubLog_addEvent(detectionWindowSubLog, TraceId_TraceIndex_InSubLog_Map_detWin, eventStream.get(i), false);

					// update data structure related to reference window
					updateActivityFreqMap_addEvent(Activity_freq_refWin, eventStream.get(i - winSize));
					updateActivityTraceSetMap_addEvent(eventStream.get(i - winSize), Activity_TraceSet_refWin);
					updateSubLog_addEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(i - winSize), false);

					updateActivityFreqMap_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, Activity_freq_refWin, eventStream.get(i));
					updateActivityTraceSetMap_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(i), Activity_TraceSet_refWin);
					updateSubLog_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(i));

				}

				// expand reference window
				int index = i - winSize;
				for(int j = index; j > (index - diff); j--)
				{

					// add events to the data structure related to reference window
					updateActivityFreqMap_addEvent(Activity_freq_refWin, eventStream.get(j));
					updateActivityTraceSetMap_addEvent(eventStream.get(j), Activity_TraceSet_refWin);
					updateSubLog_addEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(j), false);

				}

			}else
			{

				hasWindowsShrunk = true;

				// shrink detection window and pull reference window
				int detWinStartIndex = (eventIndex - winSize + 1);
				int i = detWinStartIndex;
				for(; i < detWinStartIndex - diff; i++)
				{

					// remove events from the data structure related to detection window
					updateActivityFreqMap_removeEvent(detectionWindowSubLog, TraceId_TraceIndex_InSubLog_Map_detWin, Activity_freq_detWin, eventStream.get(i));
					updateActivityTraceSetMap_removeEvent(detectionWindowSubLog, TraceId_TraceIndex_InSubLog_Map_detWin, eventStream.get(i), Activity_TraceSet_detWin);
					updateSubLog_removeEvent(detectionWindowSubLog, TraceId_TraceIndex_InSubLog_Map_detWin, eventStream.get(i));

					// update data structure related to reference window
					updateActivityFreqMap_addEvent(Activity_freq_refWin, eventStream.get(i));
					updateActivityTraceSetMap_addEvent(eventStream.get(i), Activity_TraceSet_refWin);
					updateSubLog_addEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(i), true);

					updateActivityFreqMap_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, Activity_freq_refWin, eventStream.get(i - winSize));
					updateActivityTraceSetMap_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(i - winSize), Activity_TraceSet_refWin);
					updateSubLog_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(i - winSize));

				}

				// shrink reference window
				int index = i - winSize;
				for(int j = index; j < (index - diff); j++)
				{

					// remove events from the data structure related to reference window
					updateActivityFreqMap_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, Activity_freq_refWin, eventStream.get(j));
					updateActivityTraceSetMap_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(j), Activity_TraceSet_refWin);
					updateSubLog_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(j));

				}

			}

			winSize += diff;
			numOfDistinctActivities = Activity_freq_refWin.size();
			suitableWindowSize = (numOfDistinctActivities + 1) * (numOfDistinctActivities + 1) * winSizeCoefficient;
			diff = Math.min(suitableWindowSize - winSize, (eventIndex - 2*winSize) / 2);
		}

		return winSize;

	}

	// adaptive window size based on n^2 * winSizeCoefficient
	public int ADWin_AlphaRelations_ver1(XLog eventStream, int eventIndex, int winSize,
										 XLog detectionWindowSubLog, XLog referenceWindowSubLog,
										 HashMap<String, Integer> TraceId_TraceIndex_InSubLog_Map_detWin, HashMap<String, Integer> TraceId_TraceIndex_InSubLog_Map_refWin,
										 HashMap<String, Integer> Activity_freq_detWin, HashMap<String, Integer> Activity_freq_refWin,
										 HashMap<String, Set<String>> Activity_TraceSet_detWin, HashMap<String, Set<String>> Activity_TraceSet_refWin)
	{

		int numOfDistinctActivities = Math.max(Activity_freq_detWin.size(), Activity_freq_refWin.size());
		int suitableWindowSize = numOfDistinctActivities * numOfDistinctActivities * winSizeCoefficient;

		int diff = Math.min(suitableWindowSize - winSize, (eventIndex - 2*winSize) / 2);

		boolean hasWindowsExpanded = false, hasWindowsShrunk = false;

		while(diff != 0 && (!hasWindowsExpanded || !hasWindowsShrunk))
		{
//			System.out.println(diff);

			if(diff > 0)
			{

				hasWindowsExpanded = true;

				// expand detection window and push reference window
				int detWinStartIndex__ = (eventIndex - winSize);
				int i = detWinStartIndex__;
				for(; i > detWinStartIndex__ - diff; i--)
				{

					// add events to the data structure related to detection window
					updateActivityFreqMap_addEvent(Activity_freq_detWin, eventStream.get(i));

					// update data structure related to reference window
					updateActivityFreqMap_addEvent(Activity_freq_refWin, eventStream.get(i - winSize));
					updateActivityFreqMap_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, Activity_freq_refWin, eventStream.get(i));

				}

				// expand reference window
				int index = i - winSize;
				for(int j = index; j > (index - diff); j--)
				{

					// add events to the data structure related to reference window
					updateActivityFreqMap_addEvent(Activity_freq_refWin, eventStream.get(j));

				}

			}else
			{

				hasWindowsShrunk = true;

				// shrink detection window and pull reference window
				int detWinStartIndex = (eventIndex - winSize + 1);
				int i = detWinStartIndex;
				for(; i < detWinStartIndex - diff; i++)
				{

					// remove events from the data structure related to detection window
					updateActivityFreqMap_removeEvent(detectionWindowSubLog, TraceId_TraceIndex_InSubLog_Map_detWin, Activity_freq_detWin, eventStream.get(i));

					// update data structure related to reference window
					updateActivityFreqMap_addEvent(Activity_freq_refWin, eventStream.get(i));
					updateActivityFreqMap_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, Activity_freq_refWin, eventStream.get(i - winSize));

				}

				// shrink reference window
				int index = i - winSize;
				for(int j = index; j < (index - diff); j++)
				{

					// remove events from the data structure related to reference window
					updateActivityFreqMap_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, Activity_freq_refWin, eventStream.get(j));

				}

			}

			winSize += diff;
			numOfDistinctActivities = Math.max(Activity_freq_detWin.size(), Activity_freq_refWin.size());
			suitableWindowSize = numOfDistinctActivities * numOfDistinctActivities * winSizeCoefficient;
			diff = Math.min(suitableWindowSize - winSize, (eventIndex - 2*winSize) / 2);
		}

		return winSize;

	}

	public static XLog buildSubLogFromEventStream(XLog eventStream, HashMap<String, Integer> TraceId_TraceIndex_InSubLog_Map, int fromIndex, int toIndex)
	{

		XLog subLog = new XLogImpl(eventStream.getAttributes());

		for(int i = fromIndex; i < toIndex; i++)
		{

			XTrace curTrace = eventStream.get(i);
			String traceID = XLogManager.getTraceID(curTrace);
			XEvent curEvent = curTrace.get(0);
			Integer TraceIndex_InSubLog = TraceId_TraceIndex_InSubLog_Map.get(traceID);
			if(TraceIndex_InSubLog == null)
			{

				XTrace t1 = new XTraceImpl((XAttributeMap)curTrace.getAttributes().clone());
				t1.add(curEvent);
				subLog.add(t1);
				TraceIndex_InSubLog = subLog.size() - 1;
				TraceId_TraceIndex_InSubLog_Map.put(traceID, TraceIndex_InSubLog);

			}else
			{

				XTrace t1 = subLog.get(TraceIndex_InSubLog);
				t1.add(curEvent);

			}
		}

		return subLog;

	}

	/*public void updatePairRelationMap_addEvent(XLog subLog, HashMap<String, Integer> TraceId_TraceIndex_InSubLog_Map, XTrace _event, boolean front, boolean isDet,
			Map<Pair<XEventClass, XEventClass>, RelationFrequency> pair_Relation_Map, BasicLogRelationsExt alphaRelations, HashMap<String, Integer> relation_freq,
			HashMap<String, Set<String>> Activity_TraceSet)
	{


		String traceID = XLogManager.getTraceID(_event);
		XEvent event = _event.get(0);
		Integer TraceIndex_InSubLog = TraceId_TraceIndex_InSubLog_Map.get(traceID);
		XTrace t1 = null;
		if(TraceIndex_InSubLog == null)
		{

			t1 = new XTraceImpl((XAttributeMap)_event.getAttributes().clone());

			subLog.add(t1);
			TraceIndex_InSubLog = subLog.size() - 1;
			TraceId_TraceIndex_InSubLog_Map.put(traceID, TraceIndex_InSubLog);

		}else
		{

			t1 = subLog.get(TraceIndex_InSubLog);

		}

		Entry<Pair<XEventClass, XEventClass>, BehaviorRelation> pairRelation = alphaRelations.updateDirectSuccessionMatrices(t1, event, true, front);

//		if(pairRelation.getKey()!= null && (pairRelation.getKey().getFirst().getId().compareToIgnoreCase("immunopathologisch onderzoek") == 0 ||
//				pairRelation.getKey().getSecond().getId().compareToIgnoreCase("immunopathologisch onderzoek") == 0))
//			System.out.println();

		if(pairRelation.getValue() != null)
		{

			switch(pairRelation.getValue())
			{

				case Length_Two_Loop_bi:
				{

					Pair<XEventClass, XEventClass> pair = pairRelation.getKey();
					Pair<XEventClass, XEventClass> opposed = new Pair<XEventClass, XEventClass>(pair.getSecond(),
							pair.getFirst());

					Integer loopLengthTwoFreq_pair = alphaRelations.getTwoLoopRelations().get(pair);
					Integer loopLengthTwoFreq_opposed = alphaRelations.getTwoLoopRelations().get(opposed);

					RelationFrequency rf_pair = pair_Relation_Map.get(pair);
					if(rf_pair == null)
					{

						rf_pair = new RelationFrequency();
						pair_Relation_Map.put(pair, rf_pair);

					}

					RelationFrequency rf_opposed = pair_Relation_Map.get(opposed);
					if(rf_opposed == null)
					{

						rf_opposed = new RelationFrequency();
						pair_Relation_Map.put(opposed, rf_opposed);

					}

					if(isDet)
					{

						rf_pair.setRelationType_detWin(BehaviorRelation.Length_Two_Loop);
						rf_pair.setDetWinFreq(loopLengthTwoFreq_pair);

						rf_opposed.setRelationType_detWin(BehaviorRelation.Length_Two_Loop);
						rf_opposed.setDetWinFreq(loopLengthTwoFreq_opposed);

					}else
					{

						rf_pair.setRelationType_refWin(BehaviorRelation.Length_Two_Loop);
						rf_pair.setRefWinFreq(loopLengthTwoFreq_pair);

						rf_opposed.setRelationType_refWin(BehaviorRelation.Length_Two_Loop);
						rf_opposed.setRefWinFreq(loopLengthTwoFreq_opposed);

					}

					break;

				}

				case Length_Two_Loop_ABA:
				{

					Pair<XEventClass, XEventClass> pair = pairRelation.getKey();
					Pair<XEventClass, XEventClass> opposed = new Pair<XEventClass, XEventClass>(pair.getSecond(),
							pair.getFirst());

					Integer loopLengthTwoFreq_pair = alphaRelations.getTwoLoopRelations().get(pair);
					Integer causalFreq_opposed = alphaRelations.getDfRelations().get(opposed).getCardinality();

					RelationFrequency rf_pair = pair_Relation_Map.get(pair);
					if(rf_pair == null)
					{

						rf_pair = new RelationFrequency();
						pair_Relation_Map.put(pair, rf_pair);

					}

					RelationFrequency rf_opposed = pair_Relation_Map.get(opposed);
					if(rf_opposed == null)
					{

						rf_opposed = new RelationFrequency();
						pair_Relation_Map.put(opposed, rf_opposed);

					}

					if(isDet)
					{

						rf_pair.setRelationType_detWin(BehaviorRelation.Length_Two_Loop);
						rf_pair.setDetWinFreq(loopLengthTwoFreq_pair);

						rf_opposed.setRelationType_detWin(BehaviorRelation.Causal);
						rf_opposed.setDetWinFreq(causalFreq_opposed);

					}else
					{

						rf_pair.setRelationType_refWin(BehaviorRelation.Length_Two_Loop);
						rf_pair.setRefWinFreq(loopLengthTwoFreq_pair);

						rf_opposed.setRelationType_refWin(BehaviorRelation.Causal);
						rf_opposed.setRefWinFreq(causalFreq_opposed);

					}

					break;

				}

				case Length_Two_Loop_BAB:
				{

					Pair<XEventClass, XEventClass> pair = pairRelation.getKey();
					Pair<XEventClass, XEventClass> opposed = new Pair<XEventClass, XEventClass>(pair.getSecond(),
							pair.getFirst());

					Integer causalFreq_pair = alphaRelations.getDfRelations().get(pair).getCardinality();
					Integer loopLengthTwoFreq_opposed = alphaRelations.getTwoLoopRelations().get(opposed);

					RelationFrequency rf_pair = pair_Relation_Map.get(pair);
					if(rf_pair == null)
					{

						rf_pair = new RelationFrequency();
						pair_Relation_Map.put(pair, rf_pair);

					}

					RelationFrequency rf_opposed = pair_Relation_Map.get(opposed);
					if(rf_opposed == null)
					{

						rf_opposed = new RelationFrequency();
						pair_Relation_Map.put(opposed, rf_opposed);

					}

					if(isDet)
					{

						rf_pair.setRelationType_detWin(BehaviorRelation.Causal);
						rf_pair.setDetWinFreq(causalFreq_pair);

						rf_opposed.setRelationType_detWin(BehaviorRelation.Length_Two_Loop);
						rf_opposed.setDetWinFreq(loopLengthTwoFreq_opposed);

					}else
					{

						rf_pair.setRelationType_refWin(BehaviorRelation.Causal);
						rf_pair.setRefWinFreq(causalFreq_pair);

						rf_opposed.setRelationType_refWin(BehaviorRelation.Length_Two_Loop);
						rf_opposed.setRefWinFreq(loopLengthTwoFreq_opposed);

					}

					break;

				}

				case Length_One_Loop:
				{

					Pair<XEventClass, XEventClass> pair = pairRelation.getKey();

					Integer loopLengthOneFreq_pair = alphaRelations.getSelfLoopRelations().get(pair.getFirst());

					RelationFrequency rf_pair = pair_Relation_Map.get(pair);
					if(rf_pair == null)
					{

						rf_pair = new RelationFrequency();
						pair_Relation_Map.put(pair, rf_pair);

					}

					if(isDet)
					{

						rf_pair.setRelationType_detWin(BehaviorRelation.Length_One_Loop);
						rf_pair.setDetWinFreq(loopLengthOneFreq_pair);

					}else
					{

						rf_pair.setRelationType_refWin(BehaviorRelation.Length_One_Loop);
						rf_pair.setRefWinFreq(loopLengthOneFreq_pair);

					}

					break;

				}

				case CONCURRENCY:
				{

					Pair<XEventClass, XEventClass> pair = pairRelation.getKey();
					Pair<XEventClass, XEventClass> opposed = new Pair<XEventClass, XEventClass>(pair.getSecond(),
							pair.getFirst());

					Integer freq1 = alphaRelations.getDfRelations().get(pair).getCardinality();
					Integer freq2 = alphaRelations.getDfRelations().get(opposed).getCardinality();
					int cuncurrencyFreq_pair = Math.min(freq1, freq2);

					String a_Name = pair.getFirst().getId();
					String b_Name = pair.getSecond().getId();

					if(a_Name.compareTo(b_Name) <= 0)
					{

						RelationFrequency rf_pair = pair_Relation_Map.get(pair);
						if(rf_pair == null)
						{

							rf_pair = new RelationFrequency();
							pair_Relation_Map.put(pair, rf_pair);

						}

						if(isDet)
						{

							rf_pair.setRelationType_detWin(BehaviorRelation.CONCURRENCY);
							rf_pair.setDetWinFreq(cuncurrencyFreq_pair);

							RelationFrequency rf_opposed = pair_Relation_Map.get(opposed);
							if(rf_opposed != null)
							{

								rf_opposed.setRelationType_detWin(null);
								rf_opposed.setDetWinFreq(0);

							}

						}else
						{

							rf_pair.setRelationType_refWin(BehaviorRelation.CONCURRENCY);
							rf_pair.setRefWinFreq(cuncurrencyFreq_pair);

							RelationFrequency rf_opposed = pair_Relation_Map.get(opposed);
							if(rf_opposed != null)
							{

								rf_opposed.setRelationType_refWin(null);
								rf_opposed.setRefWinFreq(0);

							}

						}

					}else
					{

						RelationFrequency rf_opposed = pair_Relation_Map.get(opposed);
						if(rf_opposed == null)
						{

							rf_opposed = new RelationFrequency();
							pair_Relation_Map.put(opposed, rf_opposed);

						}

						if(isDet)
						{

							rf_opposed.setRelationType_detWin(BehaviorRelation.CONCURRENCY);
							rf_opposed.setDetWinFreq(cuncurrencyFreq_pair);

							RelationFrequency rf_pair = pair_Relation_Map.get(pair);
							if(rf_pair != null)
							{

								rf_pair.setRelationType_detWin(null);
								rf_pair.setDetWinFreq(0);

							}

						}else
						{

							rf_opposed.setRelationType_refWin(BehaviorRelation.CONCURRENCY);
							rf_opposed.setRefWinFreq(cuncurrencyFreq_pair);

							RelationFrequency rf_pair = pair_Relation_Map.get(pair);
							if(rf_pair != null)
							{

								rf_pair.setRelationType_refWin(null);
								rf_pair.setRefWinFreq(0);

							}

						}

					}

					break;

				}

				case Causal:
				{

					Pair<XEventClass, XEventClass> pair = pairRelation.getKey();
					Pair<XEventClass, XEventClass> opposed = new Pair<XEventClass, XEventClass>(pair.getSecond(),
							pair.getFirst());

					Integer causalFreq_pair = alphaRelations.getDfRelations().get(pair).getCardinality();

					RelationFrequency rf_pair = pair_Relation_Map.get(pair);
					if(rf_pair == null)
					{

						rf_pair = new RelationFrequency();
						pair_Relation_Map.put(pair, rf_pair);

					}

					if(isDet)
					{

						rf_pair.setRelationType_detWin(BehaviorRelation.Causal);
						rf_pair.setDetWinFreq(causalFreq_pair);

						RelationFrequency rf_opposed = pair_Relation_Map.get(opposed);
						if(rf_opposed != null)
						{

							rf_opposed.setRelationType_detWin(null);
							rf_opposed.setDetWinFreq(0);

						}

					}else
					{

						rf_pair.setRelationType_refWin(BehaviorRelation.Causal);
						rf_pair.setRefWinFreq(causalFreq_pair);

						RelationFrequency rf_opposed = pair_Relation_Map.get(opposed);
						if(rf_opposed != null)
						{

							rf_opposed.setRelationType_refWin(null);
							rf_opposed.setRefWinFreq(0);

						}

					}

					break;

				}

			}

		}

		if(withConflict)
		{

			// Update conflict relations
			XEventClass eventClass = alphaRelations.getEventClasses().getClassOf(event);

			for(XEventClass evClass : alphaRelations.getEventClasses().getClasses())
			{

				if(eventClass != evClass)
				{

					Pair<XEventClass, XEventClass> pair = new Pair<XEventClass, XEventClass>(eventClass, evClass);
					Pair<XEventClass, XEventClass> opposed = new Pair<XEventClass, XEventClass>(evClass, eventClass);

//					if(pair.getFirst().getId().compareToIgnoreCase("immunopathologisch onderzoek") == 0 &&
//							pair.getSecond().getId().compareToIgnoreCase("ligdagen - alle spec.beh.kinderg.-reval.") == 0)
//						System.out.println();
//
//					if(opposed.getFirst().getId().compareToIgnoreCase("immunopathologisch onderzoek") == 0 &&
//							opposed.getSecond().getId().compareToIgnoreCase("ligdagen - alle spec.beh.kinderg.-reval.") == 0)
//						System.out.println();

					if(isDet)
					{

						boolean isIndependence = false;
						if(!pair_Relation_Map.containsKey(pair) && !pair_Relation_Map.containsKey(opposed))
							isIndependence = true;
						else if(pair_Relation_Map.containsKey(pair))
						{
							if((pair_Relation_Map.get(pair).getRelationType_detWin() == BehaviorRelation.Independence ||
								pair_Relation_Map.get(pair).getRelationType_detWin() == null))
							{
									if((pair_Relation_Map.containsKey(opposed)))
									{
										if((pair_Relation_Map.get(opposed).getRelationType_detWin() == BehaviorRelation.Independence ||
										pair_Relation_Map.get(opposed).getRelationType_detWin() == null))
											isIndependence = true;
									}else
										isIndependence = true;
							}
						}else
						{
							if((pair_Relation_Map.get(opposed).getRelationType_detWin() == BehaviorRelation.Independence ||
								pair_Relation_Map.get(opposed).getRelationType_detWin() == null))
								isIndependence = true;

						}


						if(isIndependence)
						{

							String a_Name = pair.getFirst().getId();
							String b_Name = pair.getSecond().getId();

							Integer a_Follow_b_freq = relation_freq.get(a_Name + "_" + BehaviorRelation.FOLLOW + "_" + b_Name) != null
									? relation_freq.get(a_Name + "_" + BehaviorRelation.FOLLOW + "_" + b_Name) : 0;
							Integer b_Follow_a_freq = relation_freq.get(b_Name + "_" + BehaviorRelation.FOLLOW + "_" + a_Name) != null
									? relation_freq.get(b_Name + "_" + BehaviorRelation.FOLLOW + "_" + a_Name) : 0;

							Set<String> a_traceSet = Activity_TraceSet.get(a_Name);
							int a_traceSetSize = a_traceSet != null ? a_traceSet.size() : 0;

							Set<String> b_traceSet = Activity_TraceSet.get(b_Name);
							int b_traceSetSize = b_traceSet != null ? b_traceSet.size() : 0;

							int intersectionSize = (a_traceSet != null && b_traceSet != null) ? Utils.countIntersection(a_traceSet, b_traceSet) : 0;

							Integer a_Independence_b_freq = (a_traceSetSize + b_traceSetSize - 2 * intersectionSize)
									+ (a_Follow_b_freq + b_Follow_a_freq); // freq (a#b) = (# of traces containing only a) + (# of traces containing only b) + (# of follow relations between a and b)

//							Integer a_Independence_b_freq = (a_Follow_b_freq + b_Follow_a_freq); // freq (a#b) = (# of follow relations between a and b)


							String relation = "";
							if(a_Name.compareTo(b_Name) <= 0)
							{

								RelationFrequency rf_pair = pair_Relation_Map.get(pair);
								if(rf_pair == null)
								{

									rf_pair = new RelationFrequency();
									pair_Relation_Map.put(pair, rf_pair);

								}

								rf_pair.setRelationType_detWin(BehaviorRelation.Independence);
								rf_pair.setDetWinFreq(a_Independence_b_freq);

								RelationFrequency rf_opposed = pair_Relation_Map.get(opposed);
								if(rf_opposed != null)
								{

									rf_opposed.setRelationType_detWin(null);
									rf_opposed.setDetWinFreq(0);

								}

							}else
							{

								RelationFrequency rf_opposed = pair_Relation_Map.get(opposed);
								if(rf_opposed == null)
								{

									rf_opposed = new RelationFrequency();
									pair_Relation_Map.put(opposed, rf_opposed);

								}

								rf_opposed.setRelationType_detWin(BehaviorRelation.Independence);
								rf_opposed.setDetWinFreq(a_Independence_b_freq);

								RelationFrequency rf_pair = pair_Relation_Map.get(pair);
								if(rf_pair != null)
								{

									rf_pair.setRelationType_detWin(null);
									rf_pair.setDetWinFreq(0);

								}

							}

						}

					}else
					{

						boolean isIndependence = false;
						if(!pair_Relation_Map.containsKey(pair) && !pair_Relation_Map.containsKey(opposed))
							isIndependence = true;
						else if(pair_Relation_Map.containsKey(pair))
						{
							if((pair_Relation_Map.get(pair).getRelationType_refWin() == BehaviorRelation.Independence ||
								pair_Relation_Map.get(pair).getRelationType_refWin() == null))
							{
									if((pair_Relation_Map.containsKey(opposed)))
									{
										if((pair_Relation_Map.get(opposed).getRelationType_refWin() == BehaviorRelation.Independence ||
										pair_Relation_Map.get(opposed).getRelationType_refWin() == null))
											isIndependence = true;
									}else
										isIndependence = true;
							}
						}else
						{
							if((pair_Relation_Map.get(opposed).getRelationType_refWin() == BehaviorRelation.Independence ||
								pair_Relation_Map.get(opposed).getRelationType_refWin() == null))
								isIndependence = true;

						}


						if(isIndependence)
						{

							String a_Name = pair.getFirst().getId();
							String b_Name = pair.getSecond().getId();

							Integer a_Follow_b_freq = relation_freq.get(a_Name + "_" + BehaviorRelation.FOLLOW + "_" + b_Name) != null
									? relation_freq.get(a_Name + "_" + BehaviorRelation.FOLLOW + "_" + b_Name) : 0;
							Integer b_Follow_a_freq = relation_freq.get(b_Name + "_" + BehaviorRelation.FOLLOW + "_" + a_Name) != null
									? relation_freq.get(b_Name + "_" + BehaviorRelation.FOLLOW + "_" + a_Name) : 0;

							Set<String> a_traceSet = Activity_TraceSet.get(a_Name);
							int a_traceSetSize = a_traceSet != null ? a_traceSet.size() : 0;

							Set<String> b_traceSet = Activity_TraceSet.get(b_Name);
							int b_traceSetSize = b_traceSet != null ? b_traceSet.size() : 0;

							int intersectionSize = (a_traceSet != null && b_traceSet != null) ? Utils.countIntersection(a_traceSet, b_traceSet) : 0;

							Integer a_Independence_b_freq = (a_traceSetSize + b_traceSetSize - 2 * intersectionSize)
									+ (a_Follow_b_freq + b_Follow_a_freq); // freq (a#b) = (# of traces containing only a) + (# of traces containing only b) + (# of follow relations between a and b)

//							Integer a_Independence_b_freq = (a_Follow_b_freq + b_Follow_a_freq); // freq (a#b) = (# of follow relations between a and b)


							String relation = "";
							if(a_Name.compareTo(b_Name) <= 0)
							{

								RelationFrequency rf_pair = pair_Relation_Map.get(pair);
								if(rf_pair == null)
								{

									rf_pair = new RelationFrequency();
									pair_Relation_Map.put(pair, rf_pair);

								}

								rf_pair.setRelationType_refWin(BehaviorRelation.Independence);
								rf_pair.setRefWinFreq(a_Independence_b_freq);

								RelationFrequency rf_opposed = pair_Relation_Map.get(opposed);
								if(rf_opposed != null)
								{

									rf_opposed.setRelationType_refWin(null);
									rf_opposed.setRefWinFreq(0);

								}

							}else
							{

								RelationFrequency rf_opposed = pair_Relation_Map.get(opposed);
								if(rf_opposed == null)
								{

									rf_opposed = new RelationFrequency();
									pair_Relation_Map.put(opposed, rf_opposed);

								}

								rf_opposed.setRelationType_refWin(BehaviorRelation.Independence);
								rf_opposed.setRefWinFreq(a_Independence_b_freq);

								RelationFrequency rf_pair = pair_Relation_Map.get(pair);
								if(rf_pair != null)
								{

									rf_pair.setRelationType_refWin(null);
									rf_pair.setRefWinFreq(0);

								}

							}

						}

					}

				}

			}

		}

	}
	*/

	/*public void updatePairRelationMap_removeEvent(XLog subLog, HashMap<String, Integer> TraceId_TraceIndex_InSubLog_Map, XTrace _event, boolean front, boolean isDet,
			Map<Pair<XEventClass, XEventClass>, RelationFrequency> pair_Relation_Map, BasicLogRelationsExt alphaRelations, HashMap<String, Integer> relation_freq,
			HashMap<String, Set<String>> Activity_TraceSet)
	{


		String traceID = XLogManager.getTraceID(_event);
		XEvent event = _event.get(0);
		XEventClass eventClass = alphaRelations.getEventClasses().getClassOf(event);
		Integer TraceIndex_InSubLog = TraceId_TraceIndex_InSubLog_Map.get(traceID);

		XTrace t1 = subLog.get(TraceIndex_InSubLog);

		Entry<Pair<XEventClass, XEventClass>, BehaviorRelation> pairRelation = alphaRelations.updateDirectSuccessionMatrices(t1, event, false, front);

		if(t1.size() == 0)
		{

			for(int i = subLog.indexOf(t1) + 1; i < subLog.size(); i++)
			{

				String traceId = XLogManager.getTraceID(subLog.get(i));
				TraceId_TraceIndex_InSubLog_Map.put(traceId, TraceId_TraceIndex_InSubLog_Map.get(traceId) - 1);

			}

			subLog.remove(t1);
			TraceId_TraceIndex_InSubLog_Map.remove(traceID);

		}

//		if(pairRelation.getKey()!= null && (pairRelation.getKey().getFirst().getId().compareToIgnoreCase("END") == 0 ||
//				pairRelation.getKey().getSecond().getId().compareToIgnoreCase("END") == 0))
//			System.out.println();
//
//		if(pairRelation.getKey()!= null && (pairRelation.getKey().getFirst().getId().compareToIgnoreCase("immunopathologisch onderzoek") == 0 ||
//				pairRelation.getKey().getSecond().getId().compareToIgnoreCase("immunopathologisch onderzoek") == 0))
//			System.out.println();

		if(pairRelation.getValue() != null)
		{

			switch(pairRelation.getValue())
			{

				case Length_Two_Loop_bi:
				{

					Pair<XEventClass, XEventClass> pair = pairRelation.getKey();
					Pair<XEventClass, XEventClass> opposed = new Pair<XEventClass, XEventClass>(pair.getSecond(),
							pair.getFirst());

					Integer loopLengthTwoFreq_pair = alphaRelations.getTwoLoopRelations().get(pair);
					Integer loopLengthTwoFreq_opposed = alphaRelations.getTwoLoopRelations().get(opposed);

					RelationFrequency rf_pair = pair_Relation_Map.get(pair);
					if(rf_pair == null)
					{

						rf_pair = new RelationFrequency();
						pair_Relation_Map.put(pair, rf_pair);

					}

					RelationFrequency rf_opposed = pair_Relation_Map.get(opposed);
					if(rf_opposed == null)
					{

						rf_opposed = new RelationFrequency();
						pair_Relation_Map.put(opposed, rf_opposed);

					}

					if(isDet)
					{

						rf_pair.setRelationType_detWin(BehaviorRelation.Length_Two_Loop);
						rf_pair.setDetWinFreq(loopLengthTwoFreq_pair);

						rf_opposed.setRelationType_detWin(BehaviorRelation.Length_Two_Loop);
						rf_opposed.setDetWinFreq(loopLengthTwoFreq_opposed);

					}else
					{

						rf_pair.setRelationType_refWin(BehaviorRelation.Length_Two_Loop);
						rf_pair.setRefWinFreq(loopLengthTwoFreq_pair);

						rf_opposed.setRelationType_refWin(BehaviorRelation.Length_Two_Loop);
						rf_opposed.setRefWinFreq(loopLengthTwoFreq_opposed);

					}

					break;

				}

				case Length_Two_Loop_ABA:
				{

					Pair<XEventClass, XEventClass> pair = pairRelation.getKey();
					Pair<XEventClass, XEventClass> opposed = new Pair<XEventClass, XEventClass>(pair.getSecond(),
							pair.getFirst());

					Integer loopLengthTwoFreq_pair = alphaRelations.getTwoLoopRelations().get(pair);
					Integer causalFreq_opposed = alphaRelations.getDfRelations().get(opposed).getCardinality();

					RelationFrequency rf_pair = pair_Relation_Map.get(pair);
					if(rf_pair == null)
					{

						rf_pair = new RelationFrequency();
						pair_Relation_Map.put(pair, rf_pair);

					}

					RelationFrequency rf_opposed = pair_Relation_Map.get(opposed);
					if(rf_opposed == null)
					{

						rf_opposed = new RelationFrequency();
						pair_Relation_Map.put(opposed, rf_opposed);

					}

					if(isDet)
					{

						rf_pair.setRelationType_detWin(BehaviorRelation.Length_Two_Loop);
						rf_pair.setDetWinFreq(loopLengthTwoFreq_pair);

						rf_opposed.setRelationType_detWin(BehaviorRelation.Causal);
						rf_opposed.setDetWinFreq(causalFreq_opposed);

					}else
					{

						rf_pair.setRelationType_refWin(BehaviorRelation.Length_Two_Loop);
						rf_pair.setRefWinFreq(loopLengthTwoFreq_pair);

						rf_opposed.setRelationType_refWin(BehaviorRelation.Causal);
						rf_opposed.setRefWinFreq(causalFreq_opposed);

					}

					break;

				}

				case Length_Two_Loop_BAB:
				{

					Pair<XEventClass, XEventClass> pair = pairRelation.getKey();
					Pair<XEventClass, XEventClass> opposed = new Pair<XEventClass, XEventClass>(pair.getSecond(),
							pair.getFirst());

					Integer causalFreq_pair = alphaRelations.getDfRelations().get(pair).getCardinality();
					Integer loopLengthTwoFreq_opposed = alphaRelations.getTwoLoopRelations().get(opposed);

					RelationFrequency rf_pair = pair_Relation_Map.get(pair);
					if(rf_pair == null)
					{

						rf_pair = new RelationFrequency();
						pair_Relation_Map.put(pair, rf_pair);

					}

					RelationFrequency rf_opposed = pair_Relation_Map.get(opposed);
					if(rf_opposed == null)
					{

						rf_opposed = new RelationFrequency();
						pair_Relation_Map.put(opposed, rf_opposed);

					}

					if(isDet)
					{

						rf_pair.setRelationType_detWin(BehaviorRelation.Causal);
						rf_pair.setDetWinFreq(causalFreq_pair);

						rf_opposed.setRelationType_detWin(BehaviorRelation.Length_Two_Loop);
						rf_opposed.setDetWinFreq(loopLengthTwoFreq_opposed);

					}else
					{

						rf_pair.setRelationType_refWin(BehaviorRelation.Causal);
						rf_pair.setRefWinFreq(causalFreq_pair);

						rf_opposed.setRelationType_refWin(BehaviorRelation.Length_Two_Loop);
						rf_opposed.setRefWinFreq(loopLengthTwoFreq_opposed);

					}

					break;

				}

				case Length_One_Loop:
				{

					Pair<XEventClass, XEventClass> pair = pairRelation.getKey();

					Integer loopLengthOneFreq_pair = alphaRelations.getSelfLoopRelations().get(pair.getFirst());

					RelationFrequency rf_pair = pair_Relation_Map.get(pair);
					if(rf_pair == null)
					{

						rf_pair = new RelationFrequency();
						pair_Relation_Map.put(pair, rf_pair);

					}

					if(isDet)
					{

						rf_pair.setRelationType_detWin(BehaviorRelation.Length_One_Loop);
						rf_pair.setDetWinFreq(loopLengthOneFreq_pair);

					}else
					{

						rf_pair.setRelationType_refWin(BehaviorRelation.Length_One_Loop);
						rf_pair.setRefWinFreq(loopLengthOneFreq_pair);

					}

					break;

				}

				case CONCURRENCY:
				{

					Pair<XEventClass, XEventClass> pair = pairRelation.getKey();
					Pair<XEventClass, XEventClass> opposed = new Pair<XEventClass, XEventClass>(pair.getSecond(),
							pair.getFirst());

					Integer freq1 = alphaRelations.getDfRelations().get(pair).getCardinality();
					Integer freq2 = alphaRelations.getDfRelations().get(opposed).getCardinality();
					int cuncurrencyFreq_pair = Math.min(freq1, freq2);

					String a_Name = pair.getFirst().getId();
					String b_Name = pair.getSecond().getId();

					if(a_Name.compareTo(b_Name) <= 0)
					{

						RelationFrequency rf_pair = pair_Relation_Map.get(pair);
						if(rf_pair == null)
						{

							rf_pair = new RelationFrequency();
							pair_Relation_Map.put(pair, rf_pair);

						}

						if(isDet)
						{

							rf_pair.setRelationType_detWin(BehaviorRelation.CONCURRENCY);
							rf_pair.setDetWinFreq(cuncurrencyFreq_pair);

							RelationFrequency rf_opposed = pair_Relation_Map.get(opposed);
							if(rf_opposed != null)
							{

								rf_opposed.setRelationType_detWin(null);
								rf_opposed.setDetWinFreq(0);

							}

						}else
						{

							rf_pair.setRelationType_refWin(BehaviorRelation.CONCURRENCY);
							rf_pair.setRefWinFreq(cuncurrencyFreq_pair);

							RelationFrequency rf_opposed = pair_Relation_Map.get(opposed);
							if(rf_opposed != null)
							{

								rf_opposed.setRelationType_refWin(null);
								rf_opposed.setRefWinFreq(0);

							}

						}

					}else
					{

						RelationFrequency rf_opposed = pair_Relation_Map.get(opposed);
						if(rf_opposed == null)
						{

							rf_opposed = new RelationFrequency();
							pair_Relation_Map.put(opposed, rf_opposed);

						}

						if(isDet)
						{

							rf_opposed.setRelationType_detWin(BehaviorRelation.CONCURRENCY);
							rf_opposed.setDetWinFreq(cuncurrencyFreq_pair);

							RelationFrequency rf_pair = pair_Relation_Map.get(pair);
							if(rf_pair != null)
							{

								rf_pair.setRelationType_detWin(null);
								rf_pair.setDetWinFreq(0);

							}

						}else
						{

							rf_opposed.setRelationType_refWin(BehaviorRelation.CONCURRENCY);
							rf_opposed.setRefWinFreq(cuncurrencyFreq_pair);

							RelationFrequency rf_pair = pair_Relation_Map.get(pair);
							if(rf_pair != null)
							{

								rf_pair.setRelationType_refWin(null);
								rf_pair.setRefWinFreq(0);

							}

						}

					}

					break;

				}

				case Causal:
				{

					Pair<XEventClass, XEventClass> pair = pairRelation.getKey();
					Pair<XEventClass, XEventClass> opposed = new Pair<XEventClass, XEventClass>(pair.getSecond(),
							pair.getFirst());

					Integer causalFreq_pair = alphaRelations.getDfRelations().get(pair).getCardinality();

					RelationFrequency rf_pair = pair_Relation_Map.get(pair);
					if(rf_pair == null)
					{

						rf_pair = new RelationFrequency();
						pair_Relation_Map.put(pair, rf_pair);

					}

					if(isDet)
					{

						rf_pair.setRelationType_detWin(BehaviorRelation.Causal);
						rf_pair.setDetWinFreq(causalFreq_pair);

						RelationFrequency rf_opposed = pair_Relation_Map.get(opposed);
						if(rf_opposed != null)
						{

							rf_opposed.setRelationType_detWin(null);
							rf_opposed.setDetWinFreq(0);

						}

					}else
					{

						rf_pair.setRelationType_refWin(BehaviorRelation.Causal);
						rf_pair.setRefWinFreq(causalFreq_pair);

						RelationFrequency rf_opposed = pair_Relation_Map.get(opposed);
						if(rf_opposed != null)
						{

							rf_opposed.setRelationType_refWin(null);
							rf_opposed.setRefWinFreq(0);

						}

					}

					break;

				}

				case INV_Causal:
				{

					Pair<XEventClass, XEventClass> pair = pairRelation.getKey();
					Pair<XEventClass, XEventClass> opposed = new Pair<XEventClass, XEventClass>(pair.getSecond(),
							pair.getFirst());

					Integer causalFreq_opposed = alphaRelations.getDfRelations().get(opposed).getCardinality();

					RelationFrequency rf_opposed = pair_Relation_Map.get(opposed);
					if(rf_opposed == null)
					{

						rf_opposed = new RelationFrequency();
						pair_Relation_Map.put(opposed, rf_opposed);

					}

					if(isDet)
					{

						rf_opposed.setRelationType_detWin(BehaviorRelation.Causal);
						rf_opposed.setDetWinFreq(causalFreq_opposed);

						RelationFrequency rf_pair = pair_Relation_Map.get(pair);
						if(rf_pair != null)
						{

							rf_pair.setRelationType_detWin(null);
							rf_pair.setDetWinFreq(0);

						}

					}else
					{

						rf_opposed.setRelationType_refWin(BehaviorRelation.Causal);
						rf_opposed.setRefWinFreq(causalFreq_opposed);

						RelationFrequency rf_pair = pair_Relation_Map.get(pair);
						if(rf_pair != null)
						{

							rf_pair.setRelationType_refWin(null);
							rf_pair.setRefWinFreq(0);

						}

					}

					break;

				}

			}

		}else
		{

			// null : there is no relationship for pair and opposed
			Pair<XEventClass, XEventClass> pair = pairRelation.getKey();
			RelationFrequency rf_pair = pair_Relation_Map.get(pair);
			if(rf_pair != null)
			{

				if(isDet)
				{

					rf_pair.setRelationType_detWin(null);
					rf_pair.setDetWinFreq(0);

				}else
				{

					rf_pair.setRelationType_refWin(null);
					rf_pair.setRefWinFreq(0);

				}

			}

		}

		if(withConflict)
		{

			// Update conflict relations
			XEventClass eventClass_afterUpdate = alphaRelations.getEventClasses().getClassOf(event);

			if(eventClass_afterUpdate != null)
			{

				for(XEventClass evClass : alphaRelations.getEventClasses().getClasses())
				{

					Pair<XEventClass, XEventClass> pair = new Pair<XEventClass, XEventClass>(eventClass, evClass);
					Pair<XEventClass, XEventClass> opposed = new Pair<XEventClass, XEventClass>(evClass, eventClass);

					if(eventClass != evClass)
					{

						if(isDet)
						{

							boolean isIndependence = false;
							if(!pair_Relation_Map.containsKey(pair) && !pair_Relation_Map.containsKey(opposed))
								isIndependence = true;
							else if(pair_Relation_Map.containsKey(pair))
							{
								if((pair_Relation_Map.get(pair).getRelationType_detWin() == BehaviorRelation.Independence ||
									pair_Relation_Map.get(pair).getRelationType_detWin() == null))
								{
										if((pair_Relation_Map.containsKey(opposed)))
										{
											if((pair_Relation_Map.get(opposed).getRelationType_detWin() == BehaviorRelation.Independence ||
											pair_Relation_Map.get(opposed).getRelationType_detWin() == null))
												isIndependence = true;
										}else
											isIndependence = true;
								}
							}else
							{
								if((pair_Relation_Map.get(opposed).getRelationType_detWin() == BehaviorRelation.Independence ||
									pair_Relation_Map.get(opposed).getRelationType_detWin() == null))
									isIndependence = true;

							}


							if(isIndependence)
							{

								String a_Name = pair.getFirst().getId();
								String b_Name = pair.getSecond().getId();

								Integer a_Follow_b_freq = relation_freq.get(a_Name + "_" + BehaviorRelation.FOLLOW + "_" + b_Name) != null
										? relation_freq.get(a_Name + "_" + BehaviorRelation.FOLLOW + "_" + b_Name) : 0;
								Integer b_Follow_a_freq = relation_freq.get(b_Name + "_" + BehaviorRelation.FOLLOW + "_" + a_Name) != null
										? relation_freq.get(b_Name + "_" + BehaviorRelation.FOLLOW + "_" + a_Name) : 0;

								Set<String> a_traceSet = Activity_TraceSet.get(a_Name);
								int a_traceSetSize = a_traceSet != null ? a_traceSet.size() : 0;

								Set<String> b_traceSet = Activity_TraceSet.get(b_Name);
								int b_traceSetSize = b_traceSet != null ? b_traceSet.size() : 0;

								int intersectionSize = (a_traceSet != null && b_traceSet != null) ? Utils.countIntersection(a_traceSet, b_traceSet) : 0;

								Integer a_Independence_b_freq = (a_traceSetSize + b_traceSetSize - 2 * intersectionSize)
										+ (a_Follow_b_freq + b_Follow_a_freq); // freq (a#b) = (# of traces containing only a) + (# of traces containing only b) + (# of follow relations between a and b)

//								Integer a_Independence_b_freq = (a_Follow_b_freq + b_Follow_a_freq); // freq (a#b) = (# of follow relations between a and b)


								String relation = "";
								if(a_Name.compareTo(b_Name) <= 0)
								{

									RelationFrequency rf_pair = pair_Relation_Map.get(pair);
									if(rf_pair == null)
									{

										rf_pair = new RelationFrequency();
										pair_Relation_Map.put(pair, rf_pair);

									}

									rf_pair.setRelationType_detWin(BehaviorRelation.Independence);
									rf_pair.setDetWinFreq(a_Independence_b_freq);

									RelationFrequency rf_opposed = pair_Relation_Map.get(opposed);
									if(rf_opposed != null)
									{

										rf_opposed.setRelationType_detWin(null);
										rf_opposed.setDetWinFreq(0);

									}

								}else
								{

									RelationFrequency rf_opposed = pair_Relation_Map.get(opposed);
									if(rf_opposed == null)
									{

										rf_opposed = new RelationFrequency();
										pair_Relation_Map.put(opposed, rf_opposed);

									}

									rf_opposed.setRelationType_detWin(BehaviorRelation.Independence);
									rf_opposed.setDetWinFreq(a_Independence_b_freq);

									RelationFrequency rf_pair = pair_Relation_Map.get(pair);
									if(rf_pair != null)
									{

										rf_pair.setRelationType_detWin(null);
										rf_pair.setDetWinFreq(0);

									}

								}

							}

						}else
						{

							boolean isIndependence = false;
							if(!pair_Relation_Map.containsKey(pair) && !pair_Relation_Map.containsKey(opposed))
								isIndependence = true;
							else if(pair_Relation_Map.containsKey(pair))
							{
								if((pair_Relation_Map.get(pair).getRelationType_refWin() == BehaviorRelation.Independence ||
									pair_Relation_Map.get(pair).getRelationType_refWin() == null))
								{
										if((pair_Relation_Map.containsKey(opposed)))
										{
											if((pair_Relation_Map.get(opposed).getRelationType_refWin() == BehaviorRelation.Independence ||
											pair_Relation_Map.get(opposed).getRelationType_refWin() == null))
												isIndependence = true;
										}else
											isIndependence = true;
								}
							}else
							{
								if((pair_Relation_Map.get(opposed).getRelationType_refWin() == BehaviorRelation.Independence ||
									pair_Relation_Map.get(opposed).getRelationType_refWin() == null))
									isIndependence = true;

							}


							if(isIndependence)
							{

								String a_Name = pair.getFirst().getId();
								String b_Name = pair.getSecond().getId();

								Integer a_Follow_b_freq = relation_freq.get(a_Name + "_" + BehaviorRelation.FOLLOW + "_" + b_Name) != null
										? relation_freq.get(a_Name + "_" + BehaviorRelation.FOLLOW + "_" + b_Name) : 0;
								Integer b_Follow_a_freq = relation_freq.get(b_Name + "_" + BehaviorRelation.FOLLOW + "_" + a_Name) != null
										? relation_freq.get(b_Name + "_" + BehaviorRelation.FOLLOW + "_" + a_Name) : 0;

								Set<String> a_traceSet = Activity_TraceSet.get(a_Name);
								int a_traceSetSize = a_traceSet != null ? a_traceSet.size() : 0;

								Set<String> b_traceSet = Activity_TraceSet.get(b_Name);
								int b_traceSetSize = b_traceSet != null ? b_traceSet.size() : 0;

								int intersectionSize = (a_traceSet != null && b_traceSet != null) ? Utils.countIntersection(a_traceSet, b_traceSet) : 0;

								Integer a_Independence_b_freq = (a_traceSetSize + b_traceSetSize - 2 * intersectionSize)
										+ (a_Follow_b_freq + b_Follow_a_freq); // freq (a#b) = (# of traces containing only a) + (# of traces containing only b) + (# of follow relations between a and b)

//								Integer a_Independence_b_freq = (a_Follow_b_freq + b_Follow_a_freq); // freq (a#b) = (# of follow relations between a and b)


								String relation = "";
								if(a_Name.compareTo(b_Name) <= 0)
								{

									RelationFrequency rf_pair = pair_Relation_Map.get(pair);
									if(rf_pair == null)
									{

										rf_pair = new RelationFrequency();
										pair_Relation_Map.put(pair, rf_pair);

									}

									rf_pair.setRelationType_refWin(BehaviorRelation.Independence);
									rf_pair.setRefWinFreq(a_Independence_b_freq);

									RelationFrequency rf_opposed = pair_Relation_Map.get(opposed);
									if(rf_opposed != null)
									{

										rf_opposed.setRelationType_refWin(null);
										rf_opposed.setRefWinFreq(0);

									}

								}else
								{

									RelationFrequency rf_opposed = pair_Relation_Map.get(opposed);
									if(rf_opposed == null)
									{

										rf_opposed = new RelationFrequency();
										pair_Relation_Map.put(opposed, rf_opposed);

									}

									rf_opposed.setRelationType_refWin(BehaviorRelation.Independence);
									rf_opposed.setRefWinFreq(a_Independence_b_freq);

									RelationFrequency rf_pair = pair_Relation_Map.get(pair);
									if(rf_pair != null)
									{

										rf_pair.setRelationType_refWin(null);
										rf_pair.setRefWinFreq(0);

									}

								}

							}

						}

					}

				}

			}else
			{

				for(XEventClass evClass : alphaRelations.getEventClasses().getClasses())
				{

					Pair<XEventClass, XEventClass> pair = new Pair<XEventClass, XEventClass>(eventClass, evClass);
					Pair<XEventClass, XEventClass> opposed = new Pair<XEventClass, XEventClass>(evClass, eventClass);

					RelationFrequency rf_pair = pair_Relation_Map.get(pair);
					if(rf_pair != null)
					{

						if(isDet)
						{

							rf_pair.setRelationType_detWin(null);
							rf_pair.setDetWinFreq(0);

						}else
						{

							rf_pair.setRelationType_refWin(null);
							rf_pair.setRefWinFreq(0);

						}

					}

					RelationFrequency rf_opposed = pair_Relation_Map.get(opposed);
					if(rf_opposed != null)
					{

						if(isDet)
						{

							rf_opposed.setRelationType_detWin(null);
							rf_opposed.setDetWinFreq(0);

						}else
						{

							rf_opposed.setRelationType_refWin(null);
							rf_opposed.setRefWinFreq(0);

						}

					}

				}

			}

		}

	}
	*/

	public void UpdateDataStructures_WithNewWinSize_AlphaRelations(XLog eventStream, int eventIndex, int newWinSize, int oldWinSize,
																   XLog detectionWindowSubLog, HashMap<String, Integer> TraceId_TraceIndex_InSubLog_Map_detWin,
																   XLog referenceWindowSubLog, HashMap<String, Integer> TraceId_TraceIndex_InSubLog_Map_refWin,
																   HashMap<String, Integer> Relation_Freq_detWin, HashMap<String, Integer> Relation_Freq_refWin,
																   HashMap<String, Integer> DF_Relation_Freq_detWin, HashMap<String, Integer> DF_Relation_Freq_refWin,
																   HashMap<String, Integer> Activity_freq_detWin, HashMap<String, Integer> Activity_freq_refWin,
																   HashMap<String, Set<String>> Activity_TraceSet_detWin, HashMap<String, Set<String>> Activity_TraceSet_refWin,
																   Map<Pair<XEventClass, XEventClass>, RelationFrequency> pair_Relation_Map,
																   BasicLogRelationsExt alphaRelations_det, BasicLogRelationsExt alphaRelations_ref)
	{

		int diff = newWinSize - oldWinSize;

		if(diff > 0)
		{

			// expand detection window and push reference window
			int detWinStartIndex__ = (eventIndex - oldWinSize);
			int i = detWinStartIndex__;
			for(; i > detWinStartIndex__ - diff; i--)
			{

				// add events to the data structure related to detection window
				updateActivityTraceSetMap_addEvent(eventStream.get(i), Activity_TraceSet_detWin);
				updateFollowRelationFreqMap_addEvent(detectionWindowSubLog, Relation_Freq_detWin, TraceId_TraceIndex_InSubLog_Map_detWin, eventStream.get(i), false);
				updateDirectFollowRelationFreqMap_addEvent(detectionWindowSubLog, DF_Relation_Freq_detWin, TraceId_TraceIndex_InSubLog_Map_detWin, eventStream.get(i), false);
				updateAlphaRelations_addEvent(detectionWindowSubLog, TraceId_TraceIndex_InSubLog_Map_detWin, eventStream.get(i), false, true, alphaRelations_det, Activity_TraceSet_detWin);
				//				updatePairRelationMap_addEvent(detectionWindowSubLog, TraceId_TraceIndex_InSubLog_Map_detWin, eventStream.get(i), false, true, pair_Relation_Map, alphaRelations_det, Relation_Freq_detWin, Activity_TraceSet_detWin);
				//				updateSubLog_addEvent(detectionWindowSubLog, TraceId_TraceIndex_InSubLog_Map_detWin, eventStream.get(i), false);

				// update data structure related to reference window
				updateActivityTraceSetMap_addEvent(eventStream.get(i - oldWinSize), Activity_TraceSet_refWin);
				updateFollowRelationFreqMap_addEvent(referenceWindowSubLog, Relation_Freq_refWin, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(i - oldWinSize), false);
				updateDirectFollowRelationFreqMap_addEvent(referenceWindowSubLog, DF_Relation_Freq_refWin, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(i - oldWinSize), false);
				updateAlphaRelations_addEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(i - oldWinSize), false, false, alphaRelations_ref, Activity_TraceSet_refWin);
				//				updatePairRelationMap_addEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(i - oldWinSize), false, false, pair_Relation_Map, alphaRelations_ref, Relation_Freq_refWin, Activity_TraceSet_refWin);
				//				updateSubLog_addEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(i - oldWinSize), false);

				updateActivityTraceSetMap_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(i), Activity_TraceSet_refWin);
				updateFollowRelationFreqMap_removeEvent(referenceWindowSubLog, Relation_Freq_refWin, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(i), false);
				updateDirectFollowRelationFreqMap_removeEvent(referenceWindowSubLog, DF_Relation_Freq_refWin, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(i));
				updateAlphaRelations_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(i), true, false, alphaRelations_ref, Activity_TraceSet_refWin);
				//				updatePairRelationMap_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(i), true, false, pair_Relation_Map, alphaRelations_ref, Relation_Freq_refWin, Activity_TraceSet_refWin);
				//				updateSubLog_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(i));

			}

			// expand reference window
			int index = i - oldWinSize;
			for(int j = index; j > (index - diff); j--)
			{

				// add events to the data structure related to reference window
				updateActivityTraceSetMap_addEvent(eventStream.get(j), Activity_TraceSet_refWin);
				updateFollowRelationFreqMap_addEvent(referenceWindowSubLog, Relation_Freq_refWin, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(j), false);
				updateDirectFollowRelationFreqMap_addEvent(referenceWindowSubLog, DF_Relation_Freq_refWin, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(j), false);
				updateAlphaRelations_addEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(j), false, false, alphaRelations_ref, Activity_TraceSet_refWin);
				//				updatePairRelationMap_addEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(j), false, false, pair_Relation_Map, alphaRelations_ref, Relation_Freq_refWin, Activity_TraceSet_refWin);
				//				updateSubLog_addEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(j), false);

			}

		}else if(diff < 0)
		{

			// shrink detection window and pull reference window
			int detWinStartIndex = (eventIndex - oldWinSize + 1);
			int i = detWinStartIndex;
			for(; i < detWinStartIndex - diff; i++)
			{

				// remove events from the data structure related to detection window
				updateActivityTraceSetMap_removeEvent(detectionWindowSubLog, TraceId_TraceIndex_InSubLog_Map_detWin, eventStream.get(i), Activity_TraceSet_detWin);
				updateFollowRelationFreqMap_removeEvent(detectionWindowSubLog, Relation_Freq_detWin, TraceId_TraceIndex_InSubLog_Map_detWin, eventStream.get(i), true);
				updateDirectFollowRelationFreqMap_removeEvent(detectionWindowSubLog, DF_Relation_Freq_detWin, TraceId_TraceIndex_InSubLog_Map_detWin, eventStream.get(i));
				updateAlphaRelations_removeEvent(detectionWindowSubLog, TraceId_TraceIndex_InSubLog_Map_detWin, eventStream.get(i), false, true, alphaRelations_det, Activity_TraceSet_detWin);
				//				updatePairRelationMap_removeEvent(detectionWindowSubLog, TraceId_TraceIndex_InSubLog_Map_detWin, eventStream.get(i), false, true, pair_Relation_Map, alphaRelations_det, Relation_Freq_detWin, Activity_TraceSet_detWin);
				//				updateSubLog_removeEvent(detectionWindowSubLog, TraceId_TraceIndex_InSubLog_Map_detWin, eventStream.get(i));

				// update data structure related to reference window
				updateActivityTraceSetMap_addEvent(eventStream.get(i), Activity_TraceSet_refWin);
				updateFollowRelationFreqMap_addEvent(referenceWindowSubLog, Relation_Freq_refWin, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(i), true);
				updateDirectFollowRelationFreqMap_addEvent(referenceWindowSubLog, DF_Relation_Freq_refWin, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(i), true);
				updateAlphaRelations_addEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(i), true, false, alphaRelations_ref, Activity_TraceSet_refWin);
				//				updatePairRelationMap_addEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(i), true, false, pair_Relation_Map, alphaRelations_ref, Relation_Freq_refWin, Activity_TraceSet_refWin);
				//				updateSubLog_addEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(i), true);

				updateActivityTraceSetMap_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(i - oldWinSize), Activity_TraceSet_refWin);
				updateFollowRelationFreqMap_removeEvent(referenceWindowSubLog, Relation_Freq_refWin, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(i - oldWinSize), false);
				updateDirectFollowRelationFreqMap_removeEvent(referenceWindowSubLog, DF_Relation_Freq_refWin, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(i - oldWinSize));
				updateAlphaRelations_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(i - oldWinSize), false, false,  alphaRelations_ref, Activity_TraceSet_refWin);
				//				updatePairRelationMap_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(i - oldWinSize), false, false, pair_Relation_Map, alphaRelations_ref, Relation_Freq_refWin, Activity_TraceSet_refWin);
				//				updateSubLog_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(i - oldWinSize));

			}

			// shrink reference window
			int index = i - oldWinSize;
			for(int j = index; j < (index - diff); j++)
			{

				// remove events from the data structure related to reference window
				updateActivityTraceSetMap_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(j), Activity_TraceSet_refWin);
				updateFollowRelationFreqMap_removeEvent(referenceWindowSubLog, Relation_Freq_refWin, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(j), false);
				updateDirectFollowRelationFreqMap_removeEvent(referenceWindowSubLog, DF_Relation_Freq_refWin, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(j));
				updateAlphaRelations_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(j), false, false, alphaRelations_ref, Activity_TraceSet_refWin);
				//				updatePairRelationMap_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(j), false, false, pair_Relation_Map, alphaRelations_ref, Relation_Freq_refWin, Activity_TraceSet_refWin);
				//				updateSubLog_removeEvent(referenceWindowSubLog, TraceId_TraceIndex_InSubLog_Map_refWin, eventStream.get(j));

			}

		}


	}

	public void updateAlphaRelations_addEvent(XLog subLog, HashMap<String, Integer> TraceId_TraceIndex_InSubLog_Map, XTrace _event, boolean front, boolean isDet,
											  BasicLogRelationsExt alphaRelations, HashMap<String, Set<String>> Activity_TraceSet)
	{


		String traceID = XLogManager.getTraceID(_event);
		XEvent event = _event.get(0);
		Integer TraceIndex_InSubLog = TraceId_TraceIndex_InSubLog_Map.get(traceID);
		XTrace t1 = null;
		if(TraceIndex_InSubLog == null)
		{

			t1 = new XTraceImpl((XAttributeMap)_event.getAttributes().clone());

			subLog.add(t1);
			TraceIndex_InSubLog = subLog.size() - 1;
			TraceId_TraceIndex_InSubLog_Map.put(traceID, TraceIndex_InSubLog);

		}else
		{

			t1 = subLog.get(TraceIndex_InSubLog);

		}

		alphaRelations.updateDirectSuccessionMatrices(t1, event, true, front);

	}

	public void updateAlphaRelations_removeEvent(XLog subLog, HashMap<String, Integer> TraceId_TraceIndex_InSubLog_Map, XTrace _event, boolean front, boolean isDet,
												 BasicLogRelationsExt alphaRelations, HashMap<String, Set<String>> Activity_TraceSet)
	{


		String traceID = XLogManager.getTraceID(_event);
		XEvent event = _event.get(0);
		XEventClass eventClass = alphaRelations.getEventClasses().getClassOf(event);
		Integer TraceIndex_InSubLog = TraceId_TraceIndex_InSubLog_Map.get(traceID);

		XTrace t1 = subLog.get(TraceIndex_InSubLog);

		alphaRelations.updateDirectSuccessionMatrices(t1, event, false, front);

		if(t1.size() == 0)
		{

			for(int i = subLog.indexOf(t1) + 1; i < subLog.size(); i++)
			{

				String traceId = XLogManager.getTraceID(subLog.get(i));
				TraceId_TraceIndex_InSubLog_Map.put(traceId, TraceId_TraceIndex_InSubLog_Map.get(traceId) - 1);

			}

			subLog.remove(t1);
			TraceId_TraceIndex_InSubLog_Map.remove(traceID);

		}
	}
	public XTrace updateSubLog_addEvent(XLog subLog, HashMap<String, Integer> TraceId_TraceIndex_InSubLog_Map, XTrace _event, boolean append)
	{

//		XLogInfo summary = subLog.getInfo(XLogInfoImpl.NAME_CLASSIFIER);
//		if(summary != null)
//		{
//
//			for(XEventClasses classes : summary.getEventClassesMap().values()) {
//				classes.register(event);
//			}
//
//		}


		String traceID = XLogManager.getTraceID(_event);
		XEvent event = _event.get(0);
		Integer TraceIndex_InSubLog = TraceId_TraceIndex_InSubLog_Map.get(traceID);
		XTrace t1 = null;
		if(TraceIndex_InSubLog == null)
		{

			t1 = new XTraceImpl((XAttributeMap)_event.getAttributes().clone());
			if(append)
				t1.add(event);
			else
				t1.add(0, event);
			subLog.add(t1);
			TraceIndex_InSubLog = subLog.size() - 1;
			TraceId_TraceIndex_InSubLog_Map.put(traceID, TraceIndex_InSubLog);

		}else
		{

			t1 = subLog.get(TraceIndex_InSubLog);
			if(append)
				t1.add(event);
			else
				t1.add(0, event);

		}
		return t1;

	}

	public XTrace updateSubLog_removeEvent(XLog subLog, HashMap<String, Integer> TraceId_TraceIndex_InSubLog_Map, XTrace _event)
	{

		String traceID = XLogManager.getTraceID(_event);
		XEvent event = _event.get(0);
		Integer TraceIndex_InSubLog = TraceId_TraceIndex_InSubLog_Map.get(traceID);
		XTrace t1 = subLog.get(TraceIndex_InSubLog);

		t1.remove(event);
		if(t1.size() == 0)
		{


			for(int i = subLog.indexOf(t1) + 1; i < subLog.size(); i++)
			{

				String traceId = XLogManager.getTraceID(subLog.get(i));
				TraceId_TraceIndex_InSubLog_Map.put(traceId, TraceId_TraceIndex_InSubLog_Map.get(traceId) - 1);

			}

			subLog.remove(t1);
			TraceId_TraceIndex_InSubLog_Map.remove(traceID);

		}

		return t1;

	}

	public long [][] build_DirectFollow_FrequencyMatrix(HashMap<String, Integer> Relation_Freq_detWin, HashMap<String, Integer> Relation_Freq_refWin,
														Integer totalRelationFreq_bothWins[])
	{

		HashSet<String> distinctRelations = new HashSet<>();
		for(String rAndf : Relation_Freq_detWin.keySet())
		{

			distinctRelations.add(rAndf);

		}

		for(String rAndf : Relation_Freq_refWin.keySet())
		{

			distinctRelations.add(rAndf);

		}
//		for(String distinctRelation: distinctRelations)
//		{
//
//			System.out.println(distinctRelation);
//
//		}

//		System.out.println(distinctRelations.size());

		int countNonDoubleZero = 0;
		int distinctRelationsSize = distinctRelations.size();
		int relationsFreqMatrixTemp[][] = new int[2][distinctRelationsSize];

		Iterator<String> iter = distinctRelations.iterator();
		int j = 0;
		int bigDeffCounter = 0;
		while(iter.hasNext()) {

			String curRelation = iter.next();

			Integer freq_detWin = Relation_Freq_detWin.get(curRelation);
			if(freq_detWin != null)
				relationsFreqMatrixTemp[0][j] = freq_detWin;
			else
				relationsFreqMatrixTemp[0][j] = 0;

			Integer freq_refWin = Relation_Freq_refWin.get(curRelation);
			if(freq_refWin != null)
				relationsFreqMatrixTemp[1][j] = freq_refWin;
			else
				relationsFreqMatrixTemp[1][j] = 0;

			if(Math.abs(relationsFreqMatrixTemp[0][j] - relationsFreqMatrixTemp[1][j]) > 20)
			{

				bigDeffCounter++;
//				System.out.println(curRelation + ": " + relationsFreqMatrixTemp[1][j] + " -> " + relationsFreqMatrixTemp[0][j]);

			}

			j++;

		}

		bigDiffCounts.add(bigDeffCounter);

		for (int i = 0; i < relationsFreqMatrixTemp[0].length; i++) {
			if (relationsFreqMatrixTemp[0][i]!=0 || relationsFreqMatrixTemp[1][i]!=0 )
				countNonDoubleZero++;

		}

		long relationsFreqMatrix[][] = new long[2][countNonDoubleZero];
		int index=0;
		for (int i = 0; i < relationsFreqMatrixTemp[0].length; i++)
		{
			if (relationsFreqMatrixTemp[0][i]!=0 || relationsFreqMatrixTemp[1][i]!=0 )
			{

				relationsFreqMatrix[0][index]=relationsFreqMatrixTemp[0][i];
				totalRelationFreq_bothWins[0] += relationsFreqMatrixTemp[0][i];

				relationsFreqMatrix[1][index]=relationsFreqMatrixTemp[1][i];
				totalRelationFreq_bothWins[1] += relationsFreqMatrixTemp[1][i];
				index++;

			}
		}

//		List<List<Integer>> listOfLists = new ArrayList<List<Integer>>();
//		listOfLists.add(countNonDetZeroList);
//		Utils.writeToFile("countNonDoubleZero.csv", listOfLists);
		relationsFreqMatrixTemp = null;
		distinctRelations.clear();

		return relationsFreqMatrix;

	}

	public void buildDirectFollowRelationFrequency(XLog subLog, HashMap<String, Integer> Relation_Freq)
	{

		for(int i = 0; i < subLog.size(); i++)
		{

			XTrace trace = subLog.get(i);
			String traceID = XLogManager.getTraceID(trace);

			for(int j = 0; j < trace.size(); j++)
			{

				XEvent curEvent = trace.get(j);
				if(j - 1 >= 0)
				{

					XEvent prevEvent = trace.get(j - 1);
					String relation = XLogManager.getEventName(prevEvent) + "_" + BehaviorRelation.D_FOLLOW + "_" + XLogManager.getEventName(curEvent);
					if(Relation_Freq.containsKey(relation))
					{

						Relation_Freq.put(relation, Relation_Freq.get(relation) + 1);

					}else
					{

						Relation_Freq.put(relation, 1);

					}

				}

			}

		}

	}

	public void updateDirectFollowRelationFreqMap_addEvent(XLog subLog, HashMap<String, Integer> Relation_Freq, HashMap<String, Integer> TraceId_TraceIndex_InSubLog_Map, XTrace _event, boolean append)
	{

		String traceID = XLogManager.getTraceID(_event);
		XEvent event = _event.get(0);
		Integer TraceIndex_InSubLog = TraceId_TraceIndex_InSubLog_Map.get(traceID);

		if(TraceIndex_InSubLog != null)
		{

			XTrace trace = subLog.get(TraceIndex_InSubLog);

			String relation = "";
			if(append)
			{

				XEvent prevEvent = trace.get(trace.size() - 1);
				relation = XLogManager.getEventName(prevEvent) + "_" + BehaviorRelation.D_FOLLOW + "_" + XLogManager.getEventName(event);

			}else
			{

				XEvent nextEvent = trace.get(0);
				relation = XLogManager.getEventName(event) + "_" + BehaviorRelation.D_FOLLOW + "_" + XLogManager.getEventName(nextEvent);

			}


			if(Relation_Freq.containsKey(relation))
			{

				Relation_Freq.put(relation, Relation_Freq.get(relation) + 1);

			}else
			{

				Relation_Freq.put(relation, 1);

			}

		}

	}

	public void updateDirectFollowRelationFreqMap_removeEvent(XLog subLog, HashMap<String,
			Integer> Relation_Freq, HashMap<String, Integer> TraceId_TraceIndex_InSubLog_Map, XTrace _event)
	{

		String traceID = XLogManager.getTraceID(_event);
		XEvent event = _event.get(0);
		Integer TraceIndex_InSubLog = TraceId_TraceIndex_InSubLog_Map.get(traceID);
		XTrace t1 = subLog.get(TraceIndex_InSubLog);
		int eventIndex = t1.indexOf(event);

		if(eventIndex + 1 < t1.size())
		{

			String relation = XLogManager.getEventName(event) + "_" + BehaviorRelation.D_FOLLOW + "_" + XLogManager.getEventName(t1.get(eventIndex + 1));
			int freq = Relation_Freq.get(relation);
			if(freq == 1)
				Relation_Freq.remove(relation);
			else
				Relation_Freq.put(relation, freq - 1);

		}
		if(eventIndex > 0)
		{

			String relation = XLogManager.getEventName(t1.get(eventIndex - 1)) + "_" + BehaviorRelation.D_FOLLOW + "_" + XLogManager.getEventName(event);
			int freq = Relation_Freq.get(relation);
			if(freq == 1)
				Relation_Freq.remove(relation);
			else
				Relation_Freq.put(relation, freq - 1);

		}

	}

	/*
	public static Dfg buildDfg(XLog subLog,
			XLogInfo2 logSummary,
			Set<String> startActivities,
			Set<String> endActivities,
			HashMap<String, Integer> activity_freq)
	{

		Dfg dfg = new DfgImpl();

		for(XTrace trace : subLog)
		{
			XEventClass prevEventClass = null;
			for(XEvent event : trace)
			{
				XEventClass curEventClass = logSummary.getEventClasses().getClassOf(event);
				if(prevEventClass != null)
				{
					dfg.addDirectlyFollowsEdge(prevEventClass, curEventClass, 1);

				}
				prevEventClass = curEventClass;

			}
		}

		for(XEventClass evClass : dfg.getActivities())
		{
			if(startActivities.contains(evClass.getId()))
			{
				dfg.addStartActivity(evClass, activity_freq.get(evClass.getId()));
			}
			if(endActivities.contains(evClass.getId()))
			{
				dfg.addEndActivity(evClass, activity_freq.get(evClass.getId()));
			}
		}

		return dfg;

	}

	public boolean updateDfg_addEvent(
			Dfg dfg,
			XLogInfo2 logSummary,
			XLog subLog,
			HashMap<String, Integer> TraceId_TraceIndex_InSubLog_Map,
			Set<String> startActivities,
			Set<String> endActivities,
			XTrace _event,
			boolean append)
	{
		boolean dfgChanged = false;

		String traceID = XLogManager.getTraceID(_event);
		XEvent event = _event.get(0);
		Integer TraceIndex_InSubLog = TraceId_TraceIndex_InSubLog_Map.get(traceID);

		if(logSummary.getEventClasses().getClassOf(event) == null)
			logSummary.getEventClasses().register(event);

		XEventClass eventClass = logSummary.getEventClasses().getClassOf(event);

		if(TraceIndex_InSubLog != null)
		{

			XTrace trace = subLog.get(TraceIndex_InSubLog);

			String relation = "";
			if(append)
			{

				XEvent prevEvent = trace.get(trace.size() - 1);
				XEventClass prevEventClass = logSummary.getEventClasses().getClassOf(prevEvent);

				if(!dfg.containsDirectlyFollowsEdge(prevEventClass, eventClass))
					dfgChanged = true;

				dfg.addDirectlyFollowsEdge(prevEventClass, eventClass, 1);

			}else
			{

				XEvent nextEvent = trace.get(0);
				XEventClass nextEventClass = logSummary.getEventClasses().getClassOf(nextEvent);

				if(!dfg.containsDirectlyFollowsEdge(eventClass, nextEventClass))
					dfgChanged = true;

				dfg.addDirectlyFollowsEdge(eventClass, nextEventClass, 1);

			}

		}

		if(startActivities.contains(eventClass.getId()))
		{
			dfg.addStartActivity(eventClass, 1);
		}
		if(endActivities.contains(eventClass.getId()))
		{
			dfg.addEndActivity(eventClass, 1);
		}

		return dfgChanged;

	}

	public boolean updateDfg_removeEvent(Dfg dfg,
			XLogInfo2 logSummary,
			XLog subLog,
			HashMap<String, Integer> TraceId_TraceIndex_InSubLog_Map,
			Set<String> startActivities,
			Set<String> endActivities,
			XTrace _event)
	{
		boolean dfgChanged = false;

		String traceID = XLogManager.getTraceID(_event);
		XEvent event = _event.get(0);
		Integer TraceIndex_InSubLog = TraceId_TraceIndex_InSubLog_Map.get(traceID);
		XTrace t1 = subLog.get(TraceIndex_InSubLog);
		int eventIndex = t1.indexOf(event);

		XEventClass eventClass = logSummary.getEventClasses().getClassOf(event);

		if(eventIndex + 1 < t1.size())
		{
			XEvent nextEvent = t1.get(eventIndex + 1);
			XEventClass nextEventClass = logSummary.getEventClasses().getClassOf(nextEvent);

			dfg.addDirectlyFollowsEdge(eventClass, nextEventClass, -1);

			if(!dfg.containsDirectlyFollowsEdge(eventClass, nextEventClass))
				dfgChanged = true;
		}
		if(eventIndex > 0)
		{
			XEvent prevEvent = t1.get(eventIndex - 1);
			XEventClass prevEventClass = logSummary.getEventClasses().getClassOf(prevEvent);

			dfg.addDirectlyFollowsEdge(prevEventClass, eventClass, -1);

			if(!dfg.containsDirectlyFollowsEdge(prevEventClass, eventClass))
				dfgChanged = true;
		}

		if(startActivities.contains(eventClass.getId()))
		{
			dfg.addStartActivity(eventClass, -1);
		}
		if(endActivities.contains(eventClass.getId()))
		{
			dfg.addEndActivity(eventClass, -1);
		}

		return dfgChanged;

	}
	*/
	public void buildFollowRelationFrequency(XLog subLog, HashMap<String, Integer> Relation_Freq)
	{

		for(int i = 0; i < subLog.size(); i++)
		{

			XTrace trace = subLog.get(i);
			String traceID = XLogManager.getTraceID(trace);

			for(int j = 0; j < trace.size(); j++)
			{

				XEvent curEvent = trace.get(j);
				for(int k = j - 1; k >= 0; k--)
				{

					XEvent prevEvent = trace.get(k);
					String relation = XLogManager.getEventName(prevEvent) + "_" + BehaviorRelation.FOLLOW + "_" + XLogManager.getEventName(curEvent);
					if(Relation_Freq.containsKey(relation))
					{

						Relation_Freq.put(relation, Relation_Freq.get(relation) + 1);

					}else
					{

						Relation_Freq.put(relation, 1);

					}

				}

			}

		}

	}

	public void updateFollowRelationFreqMap_addEvent(XLog subLog, HashMap<String, Integer> Relation_Freq, HashMap<String, Integer> TraceId_TraceIndex_InSubLog_Map, XTrace _event, boolean append)
	{

		String traceID = XLogManager.getTraceID(_event);
		XEvent event = _event.get(0);
		Integer TraceIndex_InSubLog = TraceId_TraceIndex_InSubLog_Map.get(traceID);

		if(TraceIndex_InSubLog != null)
		{

			XTrace trace = subLog.get(TraceIndex_InSubLog);
			for(int j = 0; j < trace.size(); j++)
			{

				String relation = "";
				if(append)
				{

					XEvent prevEvent = trace.get(j);
					relation = XLogManager.getEventName(prevEvent) + "_" + BehaviorRelation.FOLLOW + "_" + XLogManager.getEventName(event);

				}else
				{

					XEvent nextEvent = trace.get(j);
					relation = XLogManager.getEventName(event) + "_" + BehaviorRelation.FOLLOW + "_" + XLogManager.getEventName(nextEvent);

				}
				if(Relation_Freq.containsKey(relation))
				{

					Relation_Freq.put(relation, Relation_Freq.get(relation) + 1);

				}else
				{

					Relation_Freq.put(relation, 1);

				}
			}

		}

	}

	public void updateFollowRelationFreqMap_removeEvent(XLog subLog, HashMap<String,
			Integer> Relation_Freq, HashMap<String, Integer> TraceId_TraceIndex_InSubLog_Map, XTrace _event, boolean detWin)
	{

		String traceID = XLogManager.getTraceID(_event);
		XEvent event = _event.get(0);
		Integer TraceIndex_InSubLog = TraceId_TraceIndex_InSubLog_Map.get(traceID);
		XTrace t1 = subLog.get(TraceIndex_InSubLog);
		int eventIndex = t1.indexOf(event);

		for(int i = eventIndex + 1; i < t1.size(); i++)
		{

			String relation = XLogManager.getEventName(event) + "_" + BehaviorRelation.FOLLOW + "_" + XLogManager.getEventName(t1.get(i));
			int freq = 0;

			try{
				freq = Relation_Freq.get(relation);
			}catch(Exception ex)
			{
				System.out.println();
			}

			if(freq == 1)
				Relation_Freq.remove(relation);
			else
				Relation_Freq.put(relation, freq - 1);

		}

		for(int i = eventIndex - 1; i >= 0; i--)
		{

			String relation = XLogManager.getEventName(t1.get(i)) + "_" + BehaviorRelation.FOLLOW + "_" + XLogManager.getEventName(event);
			int freq = 0;

			freq = Relation_Freq.get(relation);

			if(freq == 1)
				Relation_Freq.remove(relation);
			else
				Relation_Freq.put(relation, freq - 1);

		}

	}

	public long [][] build_Follow_FrequencyMatrix(HashMap<String, Integer> Relation_Freq_detWin, HashMap<String, Integer> Relation_Freq_refWin,
												  Integer[] totalRelationFreq_bothWins)
	{

		HashSet<String> distinctRelations = new HashSet<>();
		for(String rAndf : Relation_Freq_detWin.keySet())
		{

			distinctRelations.add(rAndf);

		}

		for(String rAndf : Relation_Freq_refWin.keySet())
		{

			distinctRelations.add(rAndf);

		}
//		for(String distinctRelation: distinctRelations)
//		{
//
//			System.out.println(distinctRelation);
//
//		}

		int countNonDoubleZero = 0;
		int distinctRelationsSize = distinctRelations.size();
		int relationsFreqMatrixTemp[][] = new int[2][distinctRelationsSize];

		Iterator<String> iter = distinctRelations.iterator();
		int j = 0;
		while(iter.hasNext()) {

			String curRelation = iter.next();

			Integer freq_detWin = Relation_Freq_detWin.get(curRelation);
			if(freq_detWin != null)
				relationsFreqMatrixTemp[0][j] = freq_detWin;
			else
				relationsFreqMatrixTemp[0][j] = 0;

			Integer freq_refWin = Relation_Freq_refWin.get(curRelation);
			if(freq_refWin != null)
				relationsFreqMatrixTemp[1][j] = freq_refWin;
			else
				relationsFreqMatrixTemp[1][j] = 0;

			j++;

		}

		for (int i = 0; i < relationsFreqMatrixTemp[0].length; i++) {
			if (relationsFreqMatrixTemp[0][i]!=0 || relationsFreqMatrixTemp[1][i]!=0 )
				countNonDoubleZero++;

		}

		long relationsFreqMatrix[][] = new long[2][countNonDoubleZero];
		int index=0;
		for (int i = 0; i < relationsFreqMatrixTemp[0].length; i++)
		{
			if (relationsFreqMatrixTemp[0][i]!=0 || relationsFreqMatrixTemp[1][i]!=0 )
			{

				relationsFreqMatrix[0][index] = relationsFreqMatrixTemp[0][i];
				totalRelationFreq_bothWins[0] += relationsFreqMatrixTemp[0][i];

				relationsFreqMatrix[1][index] = relationsFreqMatrixTemp[1][i];
				totalRelationFreq_bothWins[1] += relationsFreqMatrixTemp[1][i];
				index++;

			}
		}

//		List<List<Integer>> listOfLists = new ArrayList<List<Integer>>();
//		listOfLists.add(countNonDetZeroList);
//		Utils.writeToFile("countNonDoubleZero.csv", listOfLists);
		relationsFreqMatrixTemp = null;
		distinctRelations.clear();

		return relationsFreqMatrix;

	}

	public long [][] build_AlphaRelations_FrequencyMatrix(XLog subLog_detWin, XLog subLog_refWin,
														  HashMap<String, Integer> relation_freq_detWin, HashMap<String, Integer> relation_freq_refWin,
														  HashMap<String, Integer> DF_relation_freq_detWin, HashMap<String, Integer> DF_relation_freq_refWin,
														  HashMap<String, Integer> Activity_freq_detWin, HashMap<String, Integer> Activity_freq_refWin,
														  HashMap<String, Set<String>> Activity_TraceSet_detWin, HashMap<String, Set<String>> Activity_TraceSet_refWin,
														  Integer totalRelationFreq_bothWins[], Map<Pair<XEventClass, XEventClass>, RelationFrequency> pair_Relation_Map,
														  BasicLogRelationsExt alphaRelations_det, BasicLogRelationsExt alphaRelations_ref,
														  Map<PairRelation, Integer> Relation_repFreq_map, List<Map<PairRelation, Integer>> RelationFreqMap_Queue,
														  boolean isNewDriftDetected, int eventIndex)
	{

//		List<String> traceIDs = getTraces_with_b_butNot_a(subLog_detWin);
//		for(String traceID : traceIDs)
//		{
//
//			XLogManager.removeTrace(subLog_detWin, traceID);
//
//		}
//
//		traceIDs = getTraces_with_b_butNot_a(subLog_refWin);
//		for(String traceID : traceIDs)
//		{
//
//			XLogManager.removeTrace(subLog_refWin, traceID);
//
//		}

		alphaRelations_det.makeBasicRelations(alphaRelations_ref);
		alphaRelations_ref.makeBasicRelations(alphaRelations_det);

		long t1 = System.currentTimeMillis();
		Map<String, Integer> relationsAndfrequncies_detWin = getAlphaRelationsWithFrequency(subLog_detWin,
				relation_freq_detWin, DF_relation_freq_detWin, Activity_freq_detWin, Activity_TraceSet_detWin, pair_Relation_Map, true, alphaRelations_det);
		Map<String, Integer> relationsAndfrequncies_refWin = getAlphaRelationsWithFrequency(subLog_refWin,
				relation_freq_refWin, DF_relation_freq_refWin, Activity_freq_refWin, Activity_TraceSet_refWin, pair_Relation_Map, false, alphaRelations_ref);
//		System.out.println("getAlphaRelationsWithFrequency: " + (System.currentTimeMillis() - t1));


		List<Integer> frequencies_det = new ArrayList<>();
		List<Integer> frequencies_ref = new ArrayList<>();

		List<Pair<XEventClass, XEventClass>> toBeRemovedPairs = new ArrayList<>();

		PairRelation pr;
		Map<PairRelation, Integer> RelationFreqMap = new HashMap<>();

		for (Entry<Pair<XEventClass, XEventClass>, RelationFrequency> entry : pair_Relation_Map.entrySet())
		{

			pr = null;
			int freq = 0;
			Pair<XEventClass, XEventClass> pair = entry.getKey();
			RelationFrequency rf = entry.getValue();

			if(rf.getRelationType_detWin() == null && rf.getRelationType_refWin() == null)
			{

				toBeRemovedPairs.add(pair);

			}else
			{

				if(rf.getRelationType_detWin() == rf.getRelationType_refWin())
				{

					frequencies_det.add(rf.getDetWinFreq());
					frequencies_ref.add(rf.getRefWinFreq());

//					index_relation.put(frequencies_det.size() - 1, entry);

					pr = new PairRelation();
					pr.setPair(pair);
					pr.setBehaviourRelation(rf.getRelationType_detWin());
					freq = rf.getDetWinFreq();

//					pr = new PairRelation();
//					pr.setPair(pair);
//					pr.setBehaviourRelation(rf.getRelationType_refWin());
//					freq = rf.getRefWinFreq();

				}else
				{

					if(rf.getRelationType_detWin() != null)
					{

						frequencies_det.add(rf.getDetWinFreq());
						frequencies_ref.add(0);

//						index_relation.put(frequencies_det.size() - 1, entry);

						pr = new PairRelation();
						pr.setPair(pair);
						pr.setBehaviourRelation(rf.getRelationType_detWin());
						freq = rf.getDetWinFreq();

					}

					if(rf.getRelationType_refWin() != null)
					{

						frequencies_det.add(0);
						frequencies_ref.add(rf.getRefWinFreq());

//						index_relation.put(frequencies_det.size() - 1, entry);

//						pr = new PairRelation();
//						pr.setPair(pair);
//						pr.setBehaviourRelation(rf.getRelationType_refWin());
//						freq = rf.getRefWinFreq();

					}

				}

			}

			if(withCharacterization && charConfig == CharacterizationConfig.K_samplePermutationTest && (eventIndex % charWindowMoveInterval) == 0 && !isNewDriftDetected && pr != null && pr.getBehaviourRelation() != BehaviorRelation.Independence)
			{

				Integer relationRepFreq = Relation_repFreq_map.get(pr);
				if(relationRepFreq == null)
				{

					relationRepFreq = new Integer(0);
					Relation_repFreq_map.put(pr, relationRepFreq);

				}

				Relation_repFreq_map.put(pr, relationRepFreq.intValue() + 1);
				RelationFreqMap.put(pr, freq);

			}

		}

		if(withCharacterization && charConfig == CharacterizationConfig.K_samplePermutationTest && (eventIndex % charWindowMoveInterval) == 0 && !isNewDriftDetected)
		{

			if(RelationFreqMap_Queue.size() == charBufferSize)
			{

				Map<PairRelation, Integer> RelationFreqMap_first = RelationFreqMap_Queue.remove(0);
				for (PairRelation relationKey : RelationFreqMap_first.keySet())
				{

					Integer relationRepFreq = Relation_repFreq_map.get(relationKey);
					if(relationRepFreq.intValue() == 1)
						Relation_repFreq_map.remove(relationKey);
					else
						Relation_repFreq_map.put(relationKey, relationRepFreq.intValue() - 1);

				}

			}

			RelationFreqMap_Queue.add(RelationFreqMap);

		}

		for(Pair<XEventClass, XEventClass> pair : toBeRemovedPairs)
			pair_Relation_Map.remove(pair);

		long relationsFreqMatrix[][] = new long[2][frequencies_det.size()];

		for (int i = 0; i < frequencies_det.size(); i++)
		{

			int freq = frequencies_det.get(i);
			relationsFreqMatrix[0][i] = freq;
			totalRelationFreq_bothWins[0] += freq;
//			totalRelationFreq_bothWins[0] = totalRelationFreq_bothWins[0] < freq ? freq : totalRelationFreq_bothWins[0];

			freq = frequencies_ref.get(i);
			relationsFreqMatrix[1][i] = freq;
			totalRelationFreq_bothWins[1] += freq;
//			totalRelationFreq_bothWins[1] = totalRelationFreq_bothWins[1] < freq ? freq : totalRelationFreq_bothWins[1];

		}


//		int max = 0;
//		int min = 0;
//		int j = 0;
//		if(totalRelationFreq_bothWins[0] >  totalRelationFreq_bothWins[1])
//		{
//			max = totalRelationFreq_bothWins[0];
//			min = totalRelationFreq_bothWins[1];
//			j = 1;
//		}else if(totalRelationFreq_bothWins[0] <  totalRelationFreq_bothWins[1])
//		{
//			max = totalRelationFreq_bothWins[1];
//			min = totalRelationFreq_bothWins[0];
//			j = 0;
//		}
//
//		totalRelationFreq_bothWins[j] = 0;
//
//		if(totalRelationFreq_bothWins[0] !=  totalRelationFreq_bothWins[1])
//			for (int i = 0; i < relationsFreqMatrix[j].length; i++)
//			{
//
//				relationsFreqMatrix[j][i] = Math.round(((float)(relationsFreqMatrix[j][i] * max) / (float)min));
//
//				totalRelationFreq_bothWins[j] += (int)relationsFreqMatrix[j][i];
//
//
//			}

		return relationsFreqMatrix;
	}


	public long [][] update_AlphaRelations_FrequencyMatrix(Map<Pair<XEventClass, XEventClass>, RelationFrequency> pair_Relation_Map,
														   Integer totalRelationFreq_bothWins[], Map<PairRelation, Integer> Relation_repFreq_map, List<Map<PairRelation, Integer>> RelationFreqMap_Queue,
														   boolean isNewDriftDetected, int eventIndex, Map<Integer, Entry<Pair<XEventClass, XEventClass>, RelationFrequency>> index_relation)
	{

		List<Integer> frequencies_det = new ArrayList<>();
		List<Integer> frequencies_ref = new ArrayList<>();

		List<Pair<XEventClass, XEventClass>> toBeRemovedPairs = new ArrayList<>();

		PairRelation pr;
		Map<PairRelation, Integer> RelationFreqMap = new HashMap<>();

		for (Entry<Pair<XEventClass, XEventClass>, RelationFrequency> entry : pair_Relation_Map.entrySet())
		{

			pr = null;
			int freq = 0;
			Pair<XEventClass, XEventClass> pair = entry.getKey();
			RelationFrequency rf = entry.getValue();

			if(rf.getRelationType_detWin() == null && rf.getRelationType_refWin() == null)
			{

				toBeRemovedPairs.add(pair);

			}else
			{

				if(rf.getRelationType_detWin() == rf.getRelationType_refWin())
				{

					frequencies_det.add(rf.getDetWinFreq());
					frequencies_ref.add(rf.getRefWinFreq());

					index_relation.put(frequencies_det.size() - 1, entry);

					pr = new PairRelation();
					pr.setPair(pair);
					pr.setBehaviourRelation(rf.getRelationType_detWin());
					freq = rf.getDetWinFreq();

//					pr = new PairRelation();
//					pr.setPair(pair);
//					pr.setBehaviourRelation(rf.getRelationType_refWin());
//					freq = rf.getRefWinFreq();

				}else
				{

					if(rf.getRelationType_detWin() != null)
					{

						frequencies_det.add(rf.getDetWinFreq());
						frequencies_ref.add(0);

						index_relation.put(frequencies_det.size() - 1, entry);

						pr = new PairRelation();
						pr.setPair(pair);
						pr.setBehaviourRelation(rf.getRelationType_detWin());
						freq = rf.getDetWinFreq();

					}

					if(rf.getRelationType_refWin() != null)
					{

						frequencies_det.add(0);
						frequencies_ref.add(rf.getRefWinFreq());

						index_relation.put(frequencies_det.size() - 1, entry);

//						pr = new PairRelation();
//						pr.setPair(pair);
//						pr.setBehaviourRelation(rf.getRelationType_refWin());
//						freq = rf.getRefWinFreq();

					}

				}

			}

			if(withCharacterization && charConfig == CharacterizationConfig.K_samplePermutationTest && (eventIndex % charWindowMoveInterval) == 0 && !isNewDriftDetected && pr != null && pr.getBehaviourRelation() != BehaviorRelation.Independence)
			{

				Integer relationRepFreq = Relation_repFreq_map.get(pr);
				if(relationRepFreq == null)
				{

					relationRepFreq = new Integer(0);
					Relation_repFreq_map.put(pr, relationRepFreq);

				}

				Relation_repFreq_map.put(pr, relationRepFreq.intValue() + 1);
				RelationFreqMap.put(pr, freq);

			}

		}

		if(withCharacterization && charConfig == CharacterizationConfig.K_samplePermutationTest && (eventIndex % charWindowMoveInterval) == 0 && !isNewDriftDetected)
		{

			if(RelationFreqMap_Queue.size() == charBufferSize)
			{

				Map<PairRelation, Integer> RelationFreqMap_first = RelationFreqMap_Queue.remove(0);
				for (PairRelation relationKey : RelationFreqMap_first.keySet())
				{

					Integer relationRepFreq = Relation_repFreq_map.get(relationKey);
					if(relationRepFreq.intValue() == 1)
						Relation_repFreq_map.remove(relationKey);
					else
						Relation_repFreq_map.put(relationKey, relationRepFreq.intValue() - 1);

				}

			}

			RelationFreqMap_Queue.add(RelationFreqMap);

		}

		for(Pair<XEventClass, XEventClass> pair : toBeRemovedPairs)
			pair_Relation_Map.remove(pair);

		long relationsFreqMatrix[][] = new long[2][frequencies_det.size()];

		for (int i = 0; i < frequencies_det.size(); i++)
		{

			int freq = frequencies_det.get(i);
			relationsFreqMatrix[0][i] = freq;
			totalRelationFreq_bothWins[0] += freq;
//			totalRelationFreq_bothWins[0] = totalRelationFreq_bothWins[0] < freq ? freq : totalRelationFreq_bothWins[0];

			freq = frequencies_ref.get(i);
			relationsFreqMatrix[1][i] = freq;
			totalRelationFreq_bothWins[1] += freq;
//			totalRelationFreq_bothWins[1] = totalRelationFreq_bothWins[1] < freq ? freq : totalRelationFreq_bothWins[1];

		}


//		int max = 0;
//		int min = 0;
//		int j = 0;
//		if(totalRelationFreq_bothWins[0] >  totalRelationFreq_bothWins[1])
//		{
//			max = totalRelationFreq_bothWins[0];
//			min = totalRelationFreq_bothWins[1];
//			j = 1;
//		}else if(totalRelationFreq_bothWins[0] <  totalRelationFreq_bothWins[1])
//		{
//			max = totalRelationFreq_bothWins[1];
//			min = totalRelationFreq_bothWins[0];
//			j = 0;
//		}
//
//		totalRelationFreq_bothWins[j] = 0;
//
//		if(totalRelationFreq_bothWins[0] !=  totalRelationFreq_bothWins[1])
//			for (int i = 0; i < relationsFreqMatrix[j].length; i++)
//			{
//
//				relationsFreqMatrix[j][i] = Math.round(((float)(relationsFreqMatrix[j][i] * max) / (float)min));
//
//				totalRelationFreq_bothWins[j] += (int)relationsFreqMatrix[j][i];
//
//
//			}

		return relationsFreqMatrix;

	}


	public void comparePair_Relation_Maps(Map<Pair<XEventClass, XEventClass>, RelationFrequency> pair_Relation_Map1,
										  Map<Pair<XEventClass, XEventClass>, RelationFrequency> pair_Relation_Map2, int eventIndex, XEvent event)
	{

		Iterator<Pair<XEventClass, XEventClass>> it = pair_Relation_Map2.keySet().iterator();
		List<Pair<XEventClass, XEventClass>> toBeRemovedPairs = new ArrayList<>();
		while (it.hasNext())
		{

			Pair<XEventClass, XEventClass> pair = it.next();
			RelationFrequency rf = pair_Relation_Map2.get(pair);
			if(rf.getRelationType_detWin() == null && rf.getRelationType_refWin() == null)
			{

				toBeRemovedPairs.add(pair);

			}

		}

		for(Pair<XEventClass, XEventClass> pair : toBeRemovedPairs)
			pair_Relation_Map2.remove(pair);


		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();

		it = pair_Relation_Map1.keySet().iterator();
		while (it.hasNext())
		{

			Pair<XEventClass, XEventClass> pair = it.next();

//			if(pair.getFirst().getId().compareToIgnoreCase("vervolgconsult poliklinisch") == 0 &&
//					pair.getSecond().getId().compareToIgnoreCase("administratief tarief       - eerste pol") == 0)
//				System.out.println();

			RelationFrequency rf1 = pair_Relation_Map1.get(pair);
			RelationFrequency rf2 = pair_Relation_Map2.get(pair);
			if(rf2 != null)
			{

				if(rf1.getRelationType_detWin() != rf2.getRelationType_detWin())
				{

					if(rf1.getRelationType_detWin() != null) sb1.append("\n").append("Det: ").append(pair.getFirst().getId()).append(" " + rf1.getRelationType_detWin() + " ").append(pair.getSecond().getId()).append(": " + rf1.getRefWinFreq());
					if(rf2.getRelationType_detWin() != null) sb2.append("\n").append("Det: ").append(pair.getFirst().getId()).append(" " + rf2.getRelationType_detWin() + " ").append(pair.getSecond().getId()).append(": " + rf2.getRefWinFreq());

				}else if(rf1.getDetWinFreq() != rf2.getDetWinFreq())
				{

					if(rf1.getRelationType_detWin() != null) sb1.append("\n").append("Det: ").append(pair.getFirst().getId()).append(" " + rf1.getRelationType_detWin() + " ").append(pair.getSecond().getId()).append(": " + rf1.getDetWinFreq());
					if(rf2.getRelationType_detWin() != null) sb2.append("\n").append("Det: ").append(pair.getFirst().getId()).append(" " + rf2.getRelationType_detWin() + " ").append(pair.getSecond().getId()).append(": " + rf2.getDetWinFreq());

				}

				if(rf1.getRelationType_refWin() != rf2.getRelationType_refWin())
				{

					if(rf1.getRelationType_refWin() != null) sb1.append("\n").append("Ref: ").append(pair.getFirst().getId()).append(" " + rf1.getRelationType_refWin() + " ").append(pair.getSecond().getId()).append(": " + rf1.getRefWinFreq());
					if(rf2.getRelationType_refWin() != null) sb2.append("\n").append("Ref: ").append(pair.getFirst().getId()).append(" " + rf2.getRelationType_refWin() + " ").append(pair.getSecond().getId()).append(": " + rf2.getRefWinFreq());

				}else if(rf1.getRefWinFreq() != rf2.getRefWinFreq())
				{

					if(rf1.getRelationType_refWin() != null) sb1.append("\n").append("Ref: ").append(pair.getFirst().getId()).append(" " + rf1.getRelationType_refWin() + " ").append(pair.getSecond().getId()).append(": " + rf1.getRefWinFreq());
					if(rf2.getRelationType_refWin() != null) sb2.append("\n").append("Ref: ").append(pair.getFirst().getId()).append(" " + rf2.getRelationType_refWin() + " ").append(pair.getSecond().getId()).append(": " + rf2.getRefWinFreq());

				}

			}else
			{

				if(rf1.getRelationType_detWin() != null) sb1.append("\n").append("Det: ").append(pair.getFirst().getId()).append(" " + rf1.getRelationType_detWin() + " ").append(pair.getSecond().getId()).append(": " + rf1.getRefWinFreq());
				if(rf1.getRelationType_refWin() != null) sb1.append("\n").append("Ref: ").append(pair.getFirst().getId()).append(" " + rf1.getRelationType_refWin() + " ").append(pair.getSecond().getId()).append(": " + rf1.getRefWinFreq());

			}

		}


		it = pair_Relation_Map2.keySet().iterator();
		while (it.hasNext())
		{

			Pair<XEventClass, XEventClass> pair = it.next();
			RelationFrequency rf1 = pair_Relation_Map1.get(pair);
			RelationFrequency rf2 = pair_Relation_Map2.get(pair);
			if(rf1 == null)
			{

				if(rf2.getRelationType_detWin() != null) sb2.append("\n").append("Det: ").append(pair.getFirst().getId()).append(" " + rf2.getRelationType_detWin() + " ").append(pair.getSecond().getId()).append(": " + rf2.getRefWinFreq());
				if(rf2.getRelationType_refWin() != null) sb2.append("\n").append("Ref: ").append(pair.getFirst().getId()).append(" " + rf2.getRelationType_refWin() + " ").append(pair.getSecond().getId()).append(": " + rf2.getRefWinFreq());


			}

		}

		if(sb1.length() != 0 || sb2.length() != 0)
		{

			BufferedReader reader = null;
			List<String> lines = new ArrayList<String>();

			try {

				reader = new BufferedReader(new FileReader(new File("./compare.txt")));
				String currentLine;

				while ((currentLine = reader.readLine()) != null) {

					lines.add(currentLine);

				}

				reader.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			BufferedWriter writer = null;

			try {

				lines.add("eventIndex: " + eventIndex);
				lines.add("eventName: " + XLogManager.getEventName(event) + "\n");
				lines.add("pair_Relation_Map1: ");
				lines.add(sb1.toString() + "\n");

				lines.add("pair_Relation_Map2: ");
				lines.add(sb2.toString());

				System.out.println("**********************************************");
				lines.add("**********************************************");

				writer = new BufferedWriter(new FileWriter(new File("./compare.txt")));
				for(int i = 0; i < lines.size(); i++)
				{

					writer.write(lines.get(i) + "\n");

				}

				writer.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				try {
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}


	}

	public Map<String, Integer> getAlphaRelationsWithFrequency(XLog log, HashMap<String, Integer> relation_freq,
															   HashMap<String, Integer> DF_relation_freq, HashMap<String, Integer> Activity_freq, HashMap<String, Set<String>> Activity_TraceSet,
															   Map<Pair<XEventClass, XEventClass>, RelationFrequency> pair_Relation_Map, boolean isDet, BasicLogRelationsExt alphaRelations)
	{

		Map<String, Integer> relationsAndfrequncies = new HashMap<>();
//
		long t1 = System.currentTimeMillis();
//		System.out.println("createLogInfo: " + (System.currentTimeMillis() - t1));
		t1 = System.currentTimeMillis();
//		System.out.println("BasicLogRelations: " + (System.currentTimeMillis() - t1));


		List<XEventClass> XEventClassList = new ArrayList<>();
		for(XEventClass eventClass : alphaRelations.getEventClasses().getClasses())
		{

			if(eventClass != null && !XEventClassList.contains(eventClass))
				XEventClassList.add(eventClass);

		}

		t1 = System.currentTimeMillis();
		for (int i = 0; i < XEventClassList.size(); i++)
		{

			XEventClass firstActivity = XEventClassList.get(i);

			for (int j = i; j < XEventClassList.size(); j++)
			{

				XEventClass secActivity = XEventClassList.get(j);

				getAlphaRelationFrequency(log, relationsAndfrequncies, alphaRelations, relation_freq, DF_relation_freq, Activity_freq, Activity_TraceSet, firstActivity, secActivity, pair_Relation_Map, isDet);

//				getAlphaRelationFrequency_withStrength(log, relationsAndfrequncies, alphaRelations, relation_freq, DF_relation_freq, Activity_freq, Activity_TraceSet, firstActivity, secActivity);

			}

		}
//		System.out.println("getAlphaRelationFrequency: " + (System.currentTimeMillis() - t1));

//		Object[] activitiesNames = Activity_freq.keySet().toArray();
//
//		for (int i = 0; i < activitiesNames.length; i++)
//		{
//
//			String firstActivity = (String) activitiesNames[i];
//
//			for (int j = i; j < activitiesNames.length; j++)
//			{
//
//				String secActivity = (String) activitiesNames[j];
//
////				getAlphaRelationFrequency_BasecOnHighestRelationStrength(log, relationsAndfrequncies, relation_freq, DF_relation_freq, Activity_freq, Activity_TraceSet, firstActivity, secActivity);
//
//				getAlphaRelationFrequency_BasecOnRelationStrength_withThreshholdAndStrength(log, relationsAndfrequncies, relation_freq, DF_relation_freq, Activity_freq, Activity_TraceSet, firstActivity, secActivity);
//
//			}
//
//		}


		return relationsAndfrequncies;

	}

	public void getAlphaRelationFrequency(XLog subLog, Map<String, Integer> relationsAndfrequncies, BasicLogRelationsExt alphaRelations,
										  HashMap<String, Integer> relation_freq, HashMap<String, Integer> DF_relation_freq, HashMap<String, Integer> Activity_freq,	HashMap<String, Set<String>> Activity_TraceSet,
										  XEventClass a, XEventClass b, Map<Pair<XEventClass, XEventClass>, RelationFrequency> pair_Relation_Map, boolean isDet)
	{

		String a_Name = a.getId();

		String b_Name = b.getId();



//		Integer a_DF_b_freq = DF_relation_freq.get(a_Name + "_" + BehaviorRelation.D_FOLLOW + "_" + b_Name);
//		a_DF_b_freq = a_DF_b_freq != null ? a_DF_b_freq : 0;
//
//		Integer b_DF_a_freq = DF_relation_freq.get(b_Name + "_" + BehaviorRelation.D_FOLLOW + "_" + a_Name);
//		b_DF_a_freq = b_DF_a_freq != null ? b_DF_a_freq : 0;

		String relation = null;

		Integer loopLengthOneFreq = alphaRelations.getSelfLoopRelations().get(a);
		if(a == b && loopLengthOneFreq != null && loopLengthOneFreq >= 0)
		{
			// Length-one-loop relation a,a

			relation = a_Name + "_" + BehaviorRelation.Length_One_Loop + "_" + b_Name;

			relationsAndfrequncies.put(relation, loopLengthOneFreq);

			addPairRelationEntity(pair_Relation_Map, new Pair<XEventClass, XEventClass>(a, b), BehaviorRelation.Length_One_Loop, loopLengthOneFreq, isDet);

		}else
		{

			RelationCardinality loopLengthTwo_rc = alphaRelations.getTwoLoopRelations().get(new Pair<XEventClass, XEventClass>(a, b));
			if(loopLengthTwo_rc != null && !loopLengthTwo_rc.isNoise())
			{
				// Length-two-loop relation a,b

				relation = a_Name + "_" + BehaviorRelation.Length_Two_Loop + "_" + b_Name;
				relationsAndfrequncies.put(relation, loopLengthTwo_rc.getCardinality());

				addPairRelationEntity(pair_Relation_Map, new Pair<XEventClass, XEventClass>(a, b), BehaviorRelation.Length_Two_Loop, loopLengthTwo_rc.getCardinality(), isDet);

				// Find the relation of the opposite (b,a)
				loopLengthTwo_rc = alphaRelations.getTwoLoopRelations().get(new Pair<XEventClass, XEventClass>(b, a));
				if(loopLengthTwo_rc != null && !loopLengthTwo_rc.isNoise())
				{
					// Length-two-loop relation b,a

					relation = b_Name + "_" + BehaviorRelation.Length_Two_Loop + "_" + a_Name;

					relationsAndfrequncies.put(relation, loopLengthTwo_rc.getCardinality());

					addPairRelationEntity(pair_Relation_Map, new Pair<XEventClass, XEventClass>(b, a), BehaviorRelation.Length_Two_Loop, loopLengthTwo_rc.getCardinality(), isDet);

				}else
				{
					if(alphaRelations.getCausalRelations().containsKey(new Pair<XEventClass, XEventClass>(b, a)))
					{
						// Causal relation b -> a

						Integer freq = alphaRelations.getDfRelations().get(new Pair<XEventClass, XEventClass>(b, a)).getCardinality();

						relation = b_Name + "_" + BehaviorRelation.Causal + "_" + a_Name;
						relationsAndfrequncies.put(relation, freq);

						addPairRelationEntity(pair_Relation_Map, new Pair<XEventClass, XEventClass>(b, a), BehaviorRelation.Causal, freq, isDet);

					}

				}

			}else
			{
				loopLengthTwo_rc = alphaRelations.getTwoLoopRelations().get(new Pair<XEventClass, XEventClass>(b, a));
				if(loopLengthTwo_rc != null && !loopLengthTwo_rc.isNoise())
				{
					// Length-two-loop relation a,b

					relation = b_Name + "_" + BehaviorRelation.Length_Two_Loop + "_" + a_Name;
					relationsAndfrequncies.put(relation, loopLengthTwo_rc.getCardinality());

					addPairRelationEntity(pair_Relation_Map, new Pair<XEventClass, XEventClass>(b, a), BehaviorRelation.Length_Two_Loop, loopLengthTwo_rc.getCardinality(), isDet);


					// Causal relation a -> b
					Double relationStrength = alphaRelations.getCausalRelations().get(new Pair<XEventClass, XEventClass>(a, b));
					if(relationStrength != null && relationStrength >= 0)
					{
						// Causal relation a -> b

						Integer freq = alphaRelations.getDfRelations().get(new Pair<XEventClass, XEventClass>(a, b)).getCardinality();

						relation = a_Name + "_" + BehaviorRelation.Causal + "_" + b_Name;
						relationsAndfrequncies.put(relation, freq);

						addPairRelationEntity(pair_Relation_Map, new Pair<XEventClass, XEventClass>(a, b), BehaviorRelation.Causal, freq, isDet);

					}

				}else
				{

					Double relationStrength = alphaRelations.getParallelRelations().get(new Pair<XEventClass, XEventClass>(a, b));
					if(relationStrength != null && relationStrength >= 0)
					{
						// Parallel relation a||b

						Integer freq1 = alphaRelations.getDfRelations().get(new Pair<XEventClass, XEventClass>(a, b)).getCardinality();
						Integer freq2 = alphaRelations.getDfRelations().get(new Pair<XEventClass, XEventClass>(b, a)).getCardinality();
						int freq = Math.min(freq1, freq2); // old version = min

						relation = "";
						if(a_Name.compareTo(b_Name) <= 0)
						{

							relation = a_Name + "_" + BehaviorRelation.CONCURRENCY + "_" + b_Name;
							addPairRelationEntity(pair_Relation_Map, new Pair<XEventClass, XEventClass>(a, b), BehaviorRelation.CONCURRENCY, freq, isDet);

						}else
						{

							relation = b_Name + "_" + BehaviorRelation.CONCURRENCY + "_" + a_Name;
							addPairRelationEntity(pair_Relation_Map, new Pair<XEventClass, XEventClass>(b, a), BehaviorRelation.CONCURRENCY, freq, isDet);

						}

						relationsAndfrequncies.put(relation, freq);

					}else
					{

						relationStrength = alphaRelations.getCausalRelations().get(new Pair<XEventClass, XEventClass>(a, b));
						if(relationStrength != null && relationStrength >= 0)
						{
							// Causal relation a -> b

							Integer freq = alphaRelations.getDfRelations().get(new Pair<XEventClass, XEventClass>(a, b)).getCardinality();

							relation = a_Name + "_" + BehaviorRelation.Causal + "_" + b_Name;
							relationsAndfrequncies.put(relation, freq);

							addPairRelationEntity(pair_Relation_Map, new Pair<XEventClass, XEventClass>(a, b), BehaviorRelation.Causal, freq, isDet);

						}else{

							relationStrength = alphaRelations.getCausalRelations().get(new Pair<XEventClass, XEventClass>(b, a));
							if(relationStrength != null && relationStrength >= 0)
							{
								// Causal relation b -> a

								Integer freq = alphaRelations.getDfRelations().get(new Pair<XEventClass, XEventClass>(b, a)).getCardinality();

								relation = b_Name + "_" + BehaviorRelation.Causal + "_" + a_Name;
								relationsAndfrequncies.put(relation, freq);

								addPairRelationEntity(pair_Relation_Map, new Pair<XEventClass, XEventClass>(b, a), BehaviorRelation.Causal, freq, isDet);

							}
							else
							{
								if(withConflict)
								{

									if(a != b){
										// Conflict relation a # b

										Integer a_Follow_b_freq = relation_freq.get(a_Name + "_" + BehaviorRelation.FOLLOW + "_" + b_Name) != null
												? relation_freq.get(a_Name + "_" + BehaviorRelation.FOLLOW + "_" + b_Name) : 0;
										Integer b_Follow_a_freq = relation_freq.get(b_Name + "_" + BehaviorRelation.FOLLOW + "_" + a_Name) != null
												? relation_freq.get(b_Name + "_" + BehaviorRelation.FOLLOW + "_" + a_Name) : 0;

										Set<String> a_traceSet = Activity_TraceSet.get(a_Name);
										int a_traceSetSize = a_traceSet != null ? a_traceSet.size() : 0;

										Set<String> b_traceSet = Activity_TraceSet.get(b_Name);
										int b_traceSetSize = b_traceSet != null ? b_traceSet.size() : 0;

										int intersectionSize = (a_traceSet != null && b_traceSet != null) ? Utils.countIntersection(a_traceSet, b_traceSet) : 0;

										Integer a_Independence_b_freq = (a_traceSetSize + b_traceSetSize - 2 * intersectionSize)
												+ (a_Follow_b_freq + b_Follow_a_freq); // freq (a#b) = (# of traces containing only a) + (# of traces containing only b) + (# of follow relations between a and b)

										//								Integer a_Independence_b_freq = (a_Follow_b_freq + b_Follow_a_freq); // freq (a#b) = (# of follow relations between a and b)

										relation = "";
										if(a_Name.compareTo(b_Name) <= 0)
										{

											relation = a_Name + "_" + BehaviorRelation.Independence + "_" + b_Name;
											addPairRelationEntity(pair_Relation_Map, new Pair<XEventClass, XEventClass>(a, b), BehaviorRelation.Independence, a_Independence_b_freq, isDet);

										}else
										{

											relation = b_Name + "_" + BehaviorRelation.Independence + "_" + a_Name;
											addPairRelationEntity(pair_Relation_Map, new Pair<XEventClass, XEventClass>(b, a), BehaviorRelation.Independence, a_Independence_b_freq, isDet);

										}

										relationsAndfrequncies.put(relation, a_Independence_b_freq);

									}

								}

							}
						}

					}

				}

			}
		}


	}


	public void addPairRelationEntity(Map<Pair<XEventClass, XEventClass>, RelationFrequency> pair_Relation_Map, Pair<XEventClass, XEventClass> pair,
									  BehaviorRelation relationType, int freq, boolean isDet)
	{

		RelationFrequency rf = pair_Relation_Map.get(pair);
		if(rf == null)
		{

			rf = new RelationFrequency();
			if(isDet)
			{

				rf.setRelationType_detWin(relationType);
				rf.setDetWinFreq(freq);

			}else
			{

				rf.setRelationType_refWin(relationType);
				rf.setRefWinFreq(freq);

			}

			pair_Relation_Map.put(pair, rf);

		}else
		{

			if(isDet)
			{

				rf.setRelationType_detWin(relationType);
				rf.setDetWinFreq(freq);

			}else
			{

				rf.setRelationType_refWin(relationType);
				rf.setRefWinFreq(freq);

			}

		}


	}

	public void getAlphaRelationFrequency_withStrength(XLog subLog, Map<String, Integer> relationsAndfrequncies, BasicLogRelationsExt alphaRelations,
													   HashMap<String, Integer> relation_freq, HashMap<String, Integer> DF_relation_freq, HashMap<String, Integer> Activity_freq,	HashMap<String, Set<String>> Activity_TraceSet,
													   XEventClass a, XEventClass b)
	{

		String a_Name = a.getId();

		String b_Name = b.getId();

		Integer a_DF_b_freq = DF_relation_freq.get(a_Name + "_" + BehaviorRelation.D_FOLLOW + "_" + b_Name);
		a_DF_b_freq = a_DF_b_freq != null ? a_DF_b_freq : 0;

		Integer b_DF_a_freq = DF_relation_freq.get(b_Name + "_" + BehaviorRelation.D_FOLLOW + "_" + a_Name);
		b_DF_a_freq = b_DF_a_freq != null ? b_DF_a_freq : 0;

		float a_b_causal_strength = (float)(a_DF_b_freq - b_DF_a_freq) / (float)(a_DF_b_freq + b_DF_a_freq + 1);
		float b_a_causal_strength = (float)(b_DF_a_freq - a_DF_b_freq) / (float)(b_DF_a_freq + a_DF_b_freq + 1);

		int maxDF = Math.max(a_DF_b_freq, b_DF_a_freq);
		float a_b_parallel_strength = (float)(maxDF - Math.abs(a_DF_b_freq - b_DF_a_freq)) /
				(float)(maxDF + Math.abs(a_DF_b_freq - b_DF_a_freq) + 1);

		Integer a_Follow_b_freq = relation_freq.get(a_Name + "_" + BehaviorRelation.FOLLOW + "_" + b_Name) != null
				? relation_freq.get(a_Name + "_" + BehaviorRelation.FOLLOW + "_" + b_Name) : 0;
		Integer b_Follow_a_freq = relation_freq.get(b_Name + "_" + BehaviorRelation.FOLLOW + "_" + a_Name) != null
				? relation_freq.get(b_Name + "_" + BehaviorRelation.FOLLOW + "_" + a_Name) : 0;

		Set<String> a_traceSet = Activity_TraceSet.get(a_Name);
		Set<String> b_traceSet = Activity_TraceSet.get(b_Name);

		int intersectionSize = Utils.countIntersection(a_traceSet, b_traceSet);

		Integer a_Independence_b_freq = (a_traceSet.size() + b_traceSet.size() - 2 * intersectionSize)
				+ (a_Follow_b_freq + b_Follow_a_freq) - (a_DF_b_freq + b_DF_a_freq); // freq (a#b) = (# of traces containing only a) + (# of traces containing only b) + (# of follow relations between a and b) - (# of direct follow relations between a and b)

		float a_b_Independence_strength = (float)(a_Independence_b_freq - (a_DF_b_freq + b_DF_a_freq)) /
				(float)(a_Independence_b_freq + (a_DF_b_freq + b_DF_a_freq) + 1);

		Integer loopLengthOneFreq = alphaRelations.getSelfLoopRelations().get(a);
		if(a == b && loopLengthOneFreq != null && loopLengthOneFreq >= 0)
		{
			// Length-one-loop relation a,a

			String relation = a_Name + "_" + BehaviorRelation.Length_One_Loop + "_" + b_Name;

			relationsAndfrequncies.put(relation, loopLengthOneFreq);

		}else
		{

			Integer loopLengthTwoFreq = alphaRelations.getTwoLoopRelations().get(new Pair<XEventClass, XEventClass>(a, b)).getCardinality();
			if(loopLengthTwoFreq != null && loopLengthTwoFreq >= 0)
			{
				// Length-two-loop relation a,b

				String relation = a_Name + "_" + BehaviorRelation.Length_Two_Loop + "_" + b_Name;
				relationsAndfrequncies.put(relation, loopLengthTwoFreq);

				// Find the relation of the opposite (b,a)
				loopLengthTwoFreq = alphaRelations.getTwoLoopRelations().get(new Pair<XEventClass, XEventClass>(b, a)).getCardinality();
				if(loopLengthTwoFreq != null && loopLengthTwoFreq >= 0)
				{
					// Length-two-loop relation b,a

					relation = b_Name + "_" + BehaviorRelation.Length_Two_Loop + "_" + a_Name;

					relationsAndfrequncies.put(relation, loopLengthTwoFreq);

				}else
				{

					Integer freq = alphaRelations.getDfRelations().get(new Pair<XEventClass, XEventClass>(b, a)).getCardinality();
					if(freq != null && freq >= 0 && b_a_causal_strength >= relationStrengthThreshhold)
					{
						// Causal relation b -> a

						relation = b_Name + "_" + BehaviorRelation.Causal + "_" + a_Name;
						relationsAndfrequncies.put(relation, b_DF_a_freq);

					}

				}

			}else
			{

				Double relationStrength = alphaRelations.getParallelRelations().get(new Pair<XEventClass, XEventClass>(a, b));
				if(relationStrength != null && relationStrength >= 0 && a_b_parallel_strength >= relationStrengthThreshhold)
				{
					// Parallel relation a||b

					String relation = "";
					if(a_Name.compareTo(b_Name) <= 0)
						relation = a_Name + "_" + BehaviorRelation.CONCURRENCY + "_" + b_Name;
					else
						relation = b_Name + "_" + BehaviorRelation.CONCURRENCY + "_" + a_Name;

					relationsAndfrequncies.put(relation, Math.min(a_DF_b_freq, b_DF_a_freq));

				}else
				{

					Integer freq = alphaRelations.getDfRelations().get(new Pair<XEventClass, XEventClass>(a, b)).getCardinality();
					if(freq != null && freq >= 0 && a_b_causal_strength >= relationStrengthThreshhold)
					{
						// Causal relation a -> b

						String relation = a_Name + "_" + BehaviorRelation.Causal + "_" + b_Name;
						relationsAndfrequncies.put(relation, a_DF_b_freq);

						freq = alphaRelations.getDfRelations().get(new Pair<XEventClass, XEventClass>(b, a)).getCardinality();
						if(freq != null && freq >= 0 && b_a_causal_strength >= relationStrengthThreshhold)
						{
							// Causal relation b -> a

							relation = b_Name + "_" + BehaviorRelation.Causal + "_" + a_Name;
							relationsAndfrequncies.put(relation, b_DF_a_freq);

						}

					}else
					{

						freq = alphaRelations.getDfRelations().get(new Pair<XEventClass, XEventClass>(b, a)).getCardinality();
						if(freq != null && freq >= 0 && b_a_causal_strength >= relationStrengthThreshhold)
						{
							// Causal relation b -> a

							String relation = b_Name + "_" + BehaviorRelation.Causal + "_" + a_Name;
							relationsAndfrequncies.put(relation, b_DF_a_freq);

						}else
						{

							if(a != b  && a_b_Independence_strength >= relationStrengthThreshhold){
								// Conflict relation a # b

								//								Integer freq1 = relation_freq.get(a_Name + "_" + BehaviorRelation.FOLLOW + "_" + b_Name) != null
								//										? relation_freq.get(a_Name + "_" + BehaviorRelation.FOLLOW + "_" + b_Name) : 0;
								//								Integer freq2 = relation_freq.get(b_Name + "_" + BehaviorRelation.FOLLOW + "_" + a_Name) != null
								//										? relation_freq.get(b_Name + "_" + BehaviorRelation.FOLLOW + "_" + a_Name) : 0;


								String relation = "";
								if(a_Name.compareTo(b_Name) <= 0)
									relation = a_Name + "_" + BehaviorRelation.Independence + "_" + b_Name;
								else
									relation = b_Name + "_" + BehaviorRelation.Independence + "_" + a_Name;

								relationsAndfrequncies.put(relation, a_Independence_b_freq);

							}

						}
					}

				}

			}

		}

	}

	public void getAlphaRelationFrequency_BasecOnHighestRelationStrength(XLog subLog, Map<String, Integer> relationsAndfrequncies,
																		 HashMap<String, Integer> relation_freq, HashMap<String, Integer> DF_relation_freq, HashMap<String, Integer> Activity_freq,	HashMap<String, Set<String>> Activity_TraceSet,
																		 String a, String b)
	{

		String a_Name = a;

		String b_Name = b;

		Integer a_DF_b_freq = DF_relation_freq.get(a_Name + "_" + BehaviorRelation.D_FOLLOW + "_" + b_Name);
		a_DF_b_freq = a_DF_b_freq != null ? a_DF_b_freq : 0;

		Integer b_DF_a_freq = DF_relation_freq.get(b_Name + "_" + BehaviorRelation.D_FOLLOW + "_" + a_Name);
		b_DF_a_freq = b_DF_a_freq != null ? b_DF_a_freq : 0;

		float a_b_causal_strength = (float)(a_DF_b_freq - b_DF_a_freq) / (float)(a_DF_b_freq + b_DF_a_freq + 1);

		float b_a_causal_strength = (float)(b_DF_a_freq - a_DF_b_freq) / (float)(b_DF_a_freq + a_DF_b_freq + 1);

		float a_b_parallel_strength = (float)(Math.max(a_DF_b_freq, b_DF_a_freq) - Math.abs(a_DF_b_freq - b_DF_a_freq)) /
				(float)(Math.max(a_DF_b_freq, b_DF_a_freq) + Math.abs(a_DF_b_freq - b_DF_a_freq) + 1);



		Integer a_Follow_b_freq = relation_freq.get(a_Name + "_" + BehaviorRelation.FOLLOW + "_" + b_Name);
		a_Follow_b_freq = a_Follow_b_freq != null ? a_Follow_b_freq : 0;

		Integer b_Follow_a_freq = relation_freq.get(b_Name + "_" + BehaviorRelation.FOLLOW + "_" + a_Name);
		b_Follow_a_freq = b_Follow_a_freq != null ? b_Follow_a_freq : 0;

		Set<String> a_traceSet = Activity_TraceSet.get(a_Name);
		Set<String> b_traceSet = Activity_TraceSet.get(b_Name);

//		Integer a_Independence_b_freq = a.size() + b.size() - 2 * Sets.intersection(a_traceSet, b_traceSet).size()
//				+ (a_Follow_b_freq + b_Follow_a_freq) - (a_DF_b_freq + b_DF_a_freq); // freq (a#b) = (# of traces containing only a) + (# of traces containing only b) + (# of follow relations between a and b) - (# of direct follow relations between a and b)
//
//		float a_b_Independence_strength = (float)(a_Independence_b_freq - (a_DF_b_freq + b_DF_a_freq)) /
//				(float)(a_Independence_b_freq + (a_DF_b_freq + b_DF_a_freq) + 1);

		Set<String> a_intersection_b = Sets.intersection(a_traceSet, b_traceSet);

		Integer a_Conflict_b_freq = a_traceSet.size() + b_traceSet.size() - 2 * a_intersection_b.size(); // freq (a#b) = (# of traces containing only a) + (# of traces containing only b)

		float a_b_Conflict_strength = (float)(a_Conflict_b_freq - a_intersection_b.size()) /
				(float)(a_Conflict_b_freq + a_intersection_b.size() + 1);

		float maxStrength = Math.max(a_b_causal_strength, Math.max(b_a_causal_strength,  Math.max(a_b_parallel_strength, a_b_Conflict_strength)));

		if(maxStrength > 0 && maxStrength == a_b_parallel_strength)
		{

			// Parallel relation a||b
			String relation = "";
			if(a_Name.compareTo(b_Name) <= 0)
				relation = a_Name + "_" + BehaviorRelation.CONCURRENCY + "_" + b_Name;
			else
				relation = b_Name + "_" + BehaviorRelation.CONCURRENCY + "_" + a_Name;

			relationsAndfrequncies.put(relation, Math.min(a_DF_b_freq, b_DF_a_freq));

		}else if(maxStrength > 0 && maxStrength == a_b_causal_strength)
		{

			// Causal relation a -> b
			String relation = a_Name + "_" + BehaviorRelation.Causal + "_" + b_Name;
			relationsAndfrequncies.put(relation, a_DF_b_freq);

		}else if(maxStrength > 0 && maxStrength == b_a_causal_strength)
		{

			// Causal relation b -> a
			String  relation = b_Name + "_" + BehaviorRelation.Causal + "_" + a_Name;
			relationsAndfrequncies.put(relation, b_DF_a_freq);

		}
		else if(maxStrength > 0 && maxStrength == a_b_Conflict_strength)
		{

			// Conflict relation a # b
			if(a != b){
				String relation = "";
				if(a_Name.compareTo(b_Name) <= 0)
					relation = a_Name + "_" + BehaviorRelation.CONFLICT + "_" + b_Name;
				else
					relation = b_Name + "_" + BehaviorRelation.CONFLICT + "_" + a_Name;

				relationsAndfrequncies.put(relation, a_Conflict_b_freq);

			}

		}

	}

	public void getAlphaRelationFrequency_BasecOnRelationStrength_withThreshhold(XLog subLog, Map<String, Integer> relationsAndfrequncies,
																				 HashMap<String, Integer> relation_freq, HashMap<String, Integer> DF_relation_freq, HashMap<String, Integer> Activity_freq,	HashMap<String, Set<String>> Activity_TraceSet,
																				 String a, String b)
	{

		String a_Name = a;

		String b_Name = b;

		Integer a_DF_b_freq = DF_relation_freq.get(a_Name + "_" + BehaviorRelation.D_FOLLOW + "_" + b_Name);
		a_DF_b_freq = a_DF_b_freq != null ? a_DF_b_freq : 0;

		Integer b_DF_a_freq = DF_relation_freq.get(b_Name + "_" + BehaviorRelation.D_FOLLOW + "_" + a_Name);
		b_DF_a_freq = b_DF_a_freq != null ? b_DF_a_freq : 0;

		float a_b_causal_strength = (float)(a_DF_b_freq - b_DF_a_freq) / (float)(a_DF_b_freq + b_DF_a_freq + 1);

		float b_a_causal_strength = (float)(b_DF_a_freq - a_DF_b_freq) / (float)(b_DF_a_freq + a_DF_b_freq + 1);

		float a_b_parallel_strength = (float)(Math.max(a_DF_b_freq, b_DF_a_freq) - Math.abs(a_DF_b_freq - b_DF_a_freq)) /
				(float)(Math.max(a_DF_b_freq, b_DF_a_freq) + Math.abs(a_DF_b_freq - b_DF_a_freq) + 1);



//		Integer a_Follow_b_freq = relation_freq.get(a_Name + "_" + BehaviorRelation.FOLLOW + "_" + b_Name) != null
//				? relation_freq.get(a_Name + "_" + BehaviorRelation.FOLLOW + "_" + b_Name) : 0;
//		Integer b_Follow_a_freq = relation_freq.get(b_Name + "_" + BehaviorRelation.FOLLOW + "_" + a_Name) != null
//				? relation_freq.get(b_Name + "_" + BehaviorRelation.FOLLOW + "_" + a_Name) : 0;
//		Integer a_Independence_b_freq = a_traceSet.size() + b_traceSet.size() - 2 * a_intersection_b.size()
//		+ (a_Follow_b_freq + b_Follow_a_freq) - (a_DF_b_freq + b_DF_a_freq); // freq (a#b) = (# of traces containing only a) + (# of traces containing only b) + (# of follow relations between a and b) - (# of direct follow relations between a and b)
//
//		float a_b_Independence_strength = (float)(a_Independence_b_freq - (a_DF_b_freq + b_DF_a_freq)) /
//		(float)(a_Independence_b_freq + (a_DF_b_freq + b_DF_a_freq) + 1);
//
		Set<String> a_traceSet = Activity_TraceSet.get(a_Name);
		Set<String> b_traceSet = Activity_TraceSet.get(b_Name);

		Set<String> a_intersection_b = Sets.intersection(a_traceSet, b_traceSet);

		Integer a_Conflict_b_freq = a_traceSet.size() + b_traceSet.size() - 2 * a_intersection_b.size(); // freq (a#b) = (# of traces containing only a) + (# of traces containing only b)

		float a_b_Conflict_strength = (float)(a_Conflict_b_freq - a_intersection_b.size()) /
				(float)(a_Conflict_b_freq + a_intersection_b.size() + 1);


		int noOfStrongRelations = 0;

		if(a_b_causal_strength >= relationStrengthThreshhold)
			noOfStrongRelations++;
		if(b_a_causal_strength >= relationStrengthThreshhold)
			noOfStrongRelations++;
		if(a_b_parallel_strength >= relationStrengthThreshhold)
			noOfStrongRelations++;
		if(a_b_Conflict_strength >= relationStrengthThreshhold)
			noOfStrongRelations++;

		if(noOfStrongRelations == 0)
			return; // we cannot decide between multiple weak relations

		if(a_b_parallel_strength >= relationStrengthThreshhold)
		{

			// Parallel relation a||b
			String relation = "";
			if(a_Name.compareTo(b_Name) <= 0)
				relation = a_Name + "_" + BehaviorRelation.CONCURRENCY + "_" + b_Name;
			else
				relation = b_Name + "_" + BehaviorRelation.CONCURRENCY + "_" + a_Name;

			relationsAndfrequncies.put(relation, Math.min(a_DF_b_freq, b_DF_a_freq));

		}else if(a_b_causal_strength >= relationStrengthThreshhold)
		{

			// Causal relation a -> b
			String relation = a_Name + "_" + BehaviorRelation.Causal + "_" + b_Name;
			relationsAndfrequncies.put(relation, a_DF_b_freq);

		}else if(b_a_causal_strength >= relationStrengthThreshhold)
		{

			// Causal relation b -> a
			String  relation = b_Name + "_" + BehaviorRelation.Causal + "_" + a_Name;
			relationsAndfrequncies.put(relation, b_DF_a_freq);

		}
		else if(a_b_Conflict_strength >= relationStrengthThreshhold)
		{

			// Conflict relation a # b
			if(a != b){
				String relation = "";
				if(a_Name.compareTo(b_Name) <= 0)
					relation = a_Name + "_" + BehaviorRelation.Independence + "_" + b_Name;
				else
					relation = b_Name + "_" + BehaviorRelation.Independence + "_" + a_Name;

				relationsAndfrequncies.put(relation, a_Conflict_b_freq);

			}

		}

	}


	public void getAlphaRelationFrequency_BasecOnRelationStrength_withThreshholdAndStrength(XLog subLog, Map<String, Integer> relationsAndfrequncies,
																							HashMap<String, Integer> relation_freq, HashMap<String, Integer> DF_relation_freq, HashMap<String, Integer> Activity_freq,	HashMap<String, Set<String>> Activity_TraceSet,
																							String a, String b)
	{

		String a_Name = a;

		String b_Name = b;

		List<Float> strenghList = new ArrayList<>();

		Integer a_DF_b_freq = DF_relation_freq.get(a_Name + "_" + BehaviorRelation.D_FOLLOW + "_" + b_Name);
		a_DF_b_freq = a_DF_b_freq != null ? a_DF_b_freq : 0;

		Integer b_DF_a_freq = DF_relation_freq.get(b_Name + "_" + BehaviorRelation.D_FOLLOW + "_" + a_Name);
		b_DF_a_freq = b_DF_a_freq != null ? b_DF_a_freq : 0;

		float a_b_causal_strength = (float)(a_DF_b_freq - b_DF_a_freq) / (float)(a_DF_b_freq + b_DF_a_freq + 1);
		strenghList.add(a_b_causal_strength);

		float b_a_causal_strength = (float)(b_DF_a_freq - a_DF_b_freq) / (float)(b_DF_a_freq + a_DF_b_freq + 1);
		strenghList.add(b_a_causal_strength);

		int maxDF = Math.max(a_DF_b_freq, b_DF_a_freq);
		float a_b_parallel_strength = (float)(maxDF - Math.abs(a_DF_b_freq - b_DF_a_freq)) /
				(float)(maxDF + Math.abs(a_DF_b_freq - b_DF_a_freq) + 1);
		strenghList.add(a_b_parallel_strength);



		Set<String> a_traceSet = Activity_TraceSet.get(a_Name);
		Set<String> b_traceSet = Activity_TraceSet.get(b_Name);

		Set<String> a_intersection_b = Sets.intersection(a_traceSet, b_traceSet);

		Integer a_Conflict_b_freq = a_traceSet.size() + b_traceSet.size() - 2 * a_intersection_b.size(); // freq (a#b) = (# of traces containing only a) + (# of traces containing only b)

		float a_b_Conflict_strength = (float)(a_Conflict_b_freq - a_intersection_b.size()) /
				(float)(a_Conflict_b_freq + a_intersection_b.size() + 1);
		strenghList.add(a_b_Conflict_strength);


		int noOfStrongRelations = 0;

		if(a_b_causal_strength >= relationStrengthThreshhold)
			noOfStrongRelations++;
		if(b_a_causal_strength >= relationStrengthThreshhold)
			noOfStrongRelations++;
		if(a_b_parallel_strength >= relationStrengthThreshhold)
			noOfStrongRelations++;
		if(a_b_Conflict_strength >= relationStrengthThreshhold)
			noOfStrongRelations++;

//		float maxStrength = Math.max(a_b_causal_strength, Math.max(b_a_causal_strength,  Math.max(a_b_parallel_strength, a_b_Conflict_strength)));

		if(noOfStrongRelations == 0)
			return; // we cannot decide between multiple weak relations

		Collections.sort(strenghList);
		float maxStrength = strenghList.get(strenghList.size() - 1);

		if(noOfStrongRelations > 1)
		{

			float secMaxStrength = strenghList.get(strenghList.size() - 2);
			if((maxStrength - secMaxStrength) / maxStrength < relationsStrengthRelativityThreshhold)
				return; // the strongest relation is not much superior than second strongest relation

		}

		if(maxStrength == a_b_parallel_strength)
		{

			// Parallel relation a||b
			String relation = "";
			if(a_Name.compareTo(b_Name) <= 0)
				relation = a_Name + "_" + BehaviorRelation.CONCURRENCY + "_" + b_Name;
			else
				relation = b_Name + "_" + BehaviorRelation.CONCURRENCY + "_" + a_Name;

			relationsAndfrequncies.put(relation, maxDF);

		}else if(maxStrength == a_b_causal_strength)
		{

			// Causal relation a -> b
			String relation = a_Name + "_" + BehaviorRelation.Causal + "_" + b_Name;
			relationsAndfrequncies.put(relation, a_DF_b_freq);

		}else if(maxStrength == b_a_causal_strength)
		{

			// Causal relation b -> a
			String  relation = b_Name + "_" + BehaviorRelation.Causal + "_" + a_Name;
			relationsAndfrequncies.put(relation, b_DF_a_freq);

		}
		else if(maxStrength == a_b_Conflict_strength)
		{

			// Conflict relation a # b
			if(a != b){
				String relation = "";
				if(a_Name.compareTo(b_Name) <= 0)
					relation = a_Name + "_" + BehaviorRelation.CONFLICT + "_" + b_Name;
				else
					relation = b_Name + "_" + BehaviorRelation.CONFLICT + "_" + a_Name;

				relationsAndfrequncies.put(relation, a_Conflict_b_freq);

			}

		}

	}




//	public long [][] build_AbelBehaviouralProfile_FrequencyMatrix(XLog subLog_detWin, XLog subLog_refWin)
//	{
//
////		List<String> traceIDs = getTraces_with_b_butNot_a(subLog_detWin);
////		for(String traceID : traceIDs)
////		{
////
////			XLogManager.removeTrace(subLog_detWin, traceID);
////
////		}
////
////		traceIDs = getTraces_with_b_butNot_a(subLog_refWin);
////		for(String traceID : traceIDs)
////		{
////
////			XLogManager.removeTrace(subLog_refWin, traceID);
////
////		}
//
////		long t1 = System.currentTimeMillis();
//		Map<String, Integer> relationsAndfrequncies_detWin = getAbelBehaviouralProfileWithFrequency(subLog_detWin, logFileName);
//		Map<String, Integer> relationsAndfrequncies_refWin = getAbelBehaviouralProfileWithFrequency(subLog_refWin, logFileName);
////		System.out.println("getAbelBehaviouralProfileWithFrequency: " + (System.currentTimeMillis() - t1));
//
//		HashSet<String> distinctRelations = new HashSet<>();
//		for(String rAndf : relationsAndfrequncies_detWin.keySet())
//		{
//
//			distinctRelations.add(rAndf);
//
//		}
//
//		for(String rAndf : relationsAndfrequncies_refWin.keySet())
//		{
//
//			distinctRelations.add(rAndf);
//
//		}
////		for(String distinctRelation: distinctRelations)
////		{
////
////			System.out.println(distinctRelation);
////
////		}
//
//		int countNonDoubleZero = 0;
//		int distinctRelationsSize = distinctRelations.size();
//		int relationsFreqMatrixTemp[][] = new int[2][distinctRelationsSize];
//
//		Iterator<String> iter = distinctRelations.iterator();
//		int j = 0;
//		while(iter.hasNext()) {
//
//			String curRelation = iter.next();
//
//			Integer freq_detWin = relationsAndfrequncies_detWin.get(curRelation);
//			if(freq_detWin != null)
//				relationsFreqMatrixTemp[0][j] = freq_detWin;
//			else
//				relationsFreqMatrixTemp[0][j] = 0;
//
//			Integer freq_refWin = relationsAndfrequncies_refWin.get(curRelation);
//			if(freq_refWin != null)
//				relationsFreqMatrixTemp[1][j] = freq_refWin;
//			else
//				relationsFreqMatrixTemp[1][j] = 0;
//
//			j++;
//
//		}
//
//		for (int i = 0; i < relationsFreqMatrixTemp[0].length; i++) {
//			if (relationsFreqMatrixTemp[0][i]!=0 || relationsFreqMatrixTemp[1][i]!=0 )
//				countNonDoubleZero++;
//
//		}
//
//		long relationsFreqMatrix[][] = new long[2][countNonDoubleZero];
//		int index=0;
//		for (int i = 0; i < relationsFreqMatrixTemp[0].length; i++) {
//			if (relationsFreqMatrixTemp[0][i]!=0 || relationsFreqMatrixTemp[1][i]!=0 ){
//				relationsFreqMatrix[0][index]=relationsFreqMatrixTemp[0][i];
//				relationsFreqMatrix[1][index]=relationsFreqMatrixTemp[1][i];
//				index++;
//			}
//		}
//
////		List<List<Integer>> listOfLists = new ArrayList<List<Integer>>();
////		listOfLists.add(countNonDetZeroList);
////		Utils.writeToFile("countNonDoubleZero.csv", listOfLists);
//
//		return relationsFreqMatrix;
//
//	}
//

	//	public Map<String, Integer> getAbelBehaviouralProfileWithFrequency(XLog log, String logFileName)
//	{
//
//		OldFrequencyAwarePrimeEventStructure<Integer> fpes = null;
//		try {
//			fpes = Main_FPES.buildFRAPES(log, logFileName.substring(0, logFileName.indexOf('.')));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//        Map<String, Integer> relationsAndfrequncies = fpes.getRelationsAndfrequncies();
////
////        Set<String> keys = relationsAndfrequncies.keySet();
////
////        Iterator<String> keyIter = keys.iterator();
////        while(keyIter.hasNext())
////        {
////
////        	String key = keyIter.next();
////        	System.out.println(key + ", " + relationsAndfrequncies.get(key));
////
////        }
//
//        return relationsAndfrequncies;
//
//	}
//
	/*
	public long [][] build_BlockStructure_FrequencyMatrix(ProcessTree pt_det, ProcessTree pt_ref,
			HashMap<String, Integer> activity_freq_detWin, HashMap<String, Integer> activity_freq_refWin)
	{

//		List<String> traceIDs = getTraces_with_b_butNot_a(subLog_detWin);
//		for(String traceID : traceIDs)
//		{
//
//			XLogManager.removeTrace(subLog_detWin, traceID);
//
//		}
//
//		traceIDs = getTraces_with_b_butNot_a(subLog_refWin);
//		for(String traceID : traceIDs)
//		{
//
//			XLogManager.removeTrace(subLog_refWin, traceID);
//
//		}

//		long t1 = System.currentTimeMillis();
		List<Map<String, Integer>> relationAndFrequencies_bothWins = getBlockStructuresWithFrequency(pt_det, pt_ref,
				activity_freq_detWin, activity_freq_refWin);

		Map<String, Integer> relationsAndfrequncies_detWin = relationAndFrequencies_bothWins.get(0);
		Map<String, Integer> relationsAndfrequncies_refWin = relationAndFrequencies_bothWins.get(1);
//		System.out.println("getBlockStructureWithFrequency: " + (System.currentTimeMillis() - t1));

		HashSet<String> distinctRelations = new HashSet<>();
		for(String rAndf : relationsAndfrequncies_detWin.keySet())
		{

			distinctRelations.add(rAndf);

		}

		for(String rAndf : relationsAndfrequncies_refWin.keySet())
		{

			distinctRelations.add(rAndf);

		}
//		for(String distinctRelation: distinctRelations)
//		{
//
//			System.out.println(distinctRelation);
//
//		}

//		System.out.println(distinctRelations.size());

		int countNonDoubleZero = 0;
		int distinctRelationsSize = distinctRelations.size();
		int relationsFreqMatrixTemp[][] = new int[2][distinctRelationsSize];

		Iterator<String> iter = distinctRelations.iterator();
		int j = 0;
		while(iter.hasNext()) {

			String curRelation = iter.next();

			Integer freq_detWin = relationsAndfrequncies_detWin.get(curRelation);
			if(freq_detWin != null)
				relationsFreqMatrixTemp[0][j] = freq_detWin;
			else
				relationsFreqMatrixTemp[0][j] = 0;

			Integer freq_refWin = relationsAndfrequncies_refWin.get(curRelation);
			if(freq_refWin != null)
				relationsFreqMatrixTemp[1][j] = freq_refWin;
			else
				relationsFreqMatrixTemp[1][j] = 0;

			j++;

		}

		for (int i = 0; i < relationsFreqMatrixTemp[0].length; i++) {
			if (relationsFreqMatrixTemp[0][i]!=0 || relationsFreqMatrixTemp[1][i]!=0 )
				countNonDoubleZero++;

		}

		long relationsFreqMatrix[][] = new long[2][countNonDoubleZero];
		int index=0;
		for (int i = 0; i < relationsFreqMatrixTemp[0].length; i++) {
			if (relationsFreqMatrixTemp[0][i]!=0 || relationsFreqMatrixTemp[1][i]!=0 ){
				relationsFreqMatrix[0][index]=relationsFreqMatrixTemp[0][i];
				relationsFreqMatrix[1][index]=relationsFreqMatrixTemp[1][i];
				index++;
			}
		}

//		List<List<Integer>> listOfLists = new ArrayList<List<Integer>>();
//		listOfLists.add(countNonDetZeroList);
//		Utils.writeToFile("countNonDoubleZero.csv", listOfLists);

		return relationsFreqMatrix;

	}


	public List<Map<String, Integer>> getBlockStructuresWithFrequency(ProcessTree pt_det, ProcessTree pt_ref,
			HashMap<String, Integer> activity_freq_detWin, HashMap<String, Integer> activity_freq_refWin)
	{

//		long t1 = System.currentTimeMillis();

		List<Map<String, Integer>> relationAndFrequencies_bothWins = null;
		if(IMConfig == InductiveMinerConfig.IM)
		{

			// inductive miner
//			IMMiningDialog dialog = new IMMiningDialog(subLog_detWin);
//			pt_detWin = IMProcessTree.mineProcessTree(subLog_detWin, dialog.getMiningParameters());
//			dialog = new IMMiningDialog(subLog_refWin);
//			pt_refWin = IMProcessTree.mineProcessTree(subLog_refWin, dialog.getMiningParameters());

		}else if(IMConfig == InductiveMinerConfig.IMnoise)
		{

			// inductive miner with noise filter
//			pt_detWin = IMProcessTree.mineProcessTree_infrequent(subLog_detWin);
//			pt_refWin = IMProcessTree.mineProcessTree_infrequent(subLog_refWin);

		}else if(IMConfig == InductiveMinerConfig.IMincomplete)
		{

			// inductive miner with incomplete log
//			pt_detWin = IMProcessTree.mineProcessTree_incomplete(subLog_detWin);
//			pt_refWin = IMProcessTree.mineProcessTree_incomplete(subLog_refWin);

		}


//		System.out.println("IM Processing: " + (System.currentTimeMillis() - t1));



//		long t1 = System.currentTimeMillis();

//		Main.visitPTree(pt_detWin.getRoot(), "");
//		System.out.println();
//		Main.visitPTree(pt_refWin.getRoot(), "");


		if(FEConfig == FeatureExtractionConfig.BB)
		{

			relationAndFrequencies_bothWins = buildBlockStructureFromTree_BB(pt_det, pt_ref,
					activity_freq_detWin, activity_freq_refWin);

		}else if(FEConfig == FeatureExtractionConfig.WB)
		{

			relationAndFrequencies_bothWins = buildBlockStructureFromTree_WB(pt_det, pt_ref,
					activity_freq_detWin, activity_freq_refWin);

		}else if(FEConfig == FeatureExtractionConfig.Hybrid)
		{

			relationAndFrequencies_bothWins = buildBlockStructureFromTree_Hybrid(pt_det, pt_ref,
					activity_freq_detWin, activity_freq_refWin);

		}else if(FEConfig == FeatureExtractionConfig.WBA)
		{

			relationAndFrequencies_bothWins = buildBlockStructureFromTree_WBA(pt_det, pt_ref,
					activity_freq_detWin, activity_freq_refWin);

		}else if(FEConfig == FeatureExtractionConfig.WBANB)
		{

			relationAndFrequencies_bothWins = buildBlockStructureFromTree_WBANB(pt_det, pt_ref,
					activity_freq_detWin, activity_freq_refWin);

		}

//		System.out.println("Extract features from ProcessTree: " + (System.currentTimeMillis() - t1));

        return relationAndFrequencies_bothWins;

	}

	// White Box on only Activities with No Block name(WBANB)
	private List<Map<String, Integer>> buildBlockStructureFromTree_WBANB(ProcessTree pt_detWin, ProcessTree pt_refWin,
			HashMap<String, Integer> activity_freq_detWin, HashMap<String, Integer> activity_freq_refWin)
	{

		List<BlockStructure> blockStructures_det = new ArrayList();
		List<BlockStructure> blockStructures_ref = new ArrayList();

		extractBlocks_WBANB(pt_detWin.getRoot(), blockStructures_det, activity_freq_detWin);
		extractBlocks_WBANB(pt_refWin.getRoot(), blockStructures_ref, activity_freq_refWin);

		Map<String, Integer> blockStructuresWithFreq_det = new HashMap<>();
		Map<String, Integer> blockStructuresWithFreq_ref = new HashMap<>();

		for(BlockStructure bs : blockStructures_det)
		{

			blockStructuresWithFreq_det.put(bs.getContentString(), bs.getFreq());

		}
		for(BlockStructure bs : blockStructures_ref)
		{

			blockStructuresWithFreq_ref.put(bs.getContentString(), bs.getFreq());

		}

		List<Map<String, Integer>> relationAndFrequencies_bothWins = new ArrayList<>();
		relationAndFrequencies_bothWins.add(blockStructuresWithFreq_det);
		relationAndFrequencies_bothWins.add(blockStructuresWithFreq_ref);

		return relationAndFrequencies_bothWins;

	}

	private Entry<BlockStructure, Integer> extractBlocks_WBANB(Node n, List<BlockStructure> blockStructures, HashMap<String, Integer> activity_freq)
	{

		String nodeName = null;
		int nodeFreq = 0;
		BlockStructure blockStructure = new BlockStructure();
		if (n instanceof Block)
		{
			// n is an internal node (a Block)
			Block b = (Block) n;

			nodeName = getBlockName(b);

			StringBuilder blockContent = new StringBuilder();
			blockContent.append(nodeName + "(");

			blockStructure.setBlockName(nodeName);

			List<Integer> blockChildrenFreqs = new ArrayList<>();
			List<String> childrenNames = new ArrayList<>();
			// visit children of n
			for (Node c : b.getChildren()) {
				Entry<BlockStructure, Integer> childNodeName_freq = extractBlocks_WBANB(c, blockStructures, activity_freq);
				BlockStructure childBlockStructure = childNodeName_freq.getKey();

				if(!childBlockStructure.ContainsBlockStructure())
				{

					String childName = childBlockStructure.getBlockName();
					childrenNames.add(childName);
					if(childName.compareToIgnoreCase("tau") != 0)
						blockStructure.getLeaves().add(childBlockStructure);

				}else
				{

					StringBuilder blockStructureContent = new StringBuilder();
//					blockStructureContent.append("(");
					for (int k = 0; k < childBlockStructure.getLeaves().size(); k++)
					{

						BlockStructure leaf = childBlockStructure.getLeaves().get(k);
						blockStructure.getLeaves().add(leaf);
						if(k < (childBlockStructure.getLeaves().size() - 1))
							blockStructureContent.append(leaf + ", ");
						else
							blockStructureContent.append(leaf);

					}
//					blockStructureContent.append(")");
					childrenNames.add(blockStructureContent.toString());

				}
				blockStructure.getChildren().add(childBlockStructure);
				blockChildrenFreqs.add(childNodeName_freq.getValue());

			}

			Collections.sort(blockStructure.getLeaves());

			if (b instanceof Xor || b instanceof And || b instanceof Or)
				Collections.sort(childrenNames);

			for (int k = 0; k < childrenNames.size(); k++)
			{

				String childName = childrenNames.get(k);
				if(k < (childrenNames.size() - 1))
					blockContent.append(childName + ", ");
				else
					blockContent.append(childName);

			}

			blockContent.append(")");

			blockStructure.setContainsBlockStructure(true);

			blockStructure.setContentString(blockContent.toString());

			nodeFreq = getBlockFreq(b, blockChildrenFreqs);
			blockStructure.setFreq(nodeFreq);

			blockStructures.add(blockStructure);

		}

		else {
			// n is a leaf (a Task)
			nodeName = n.getName();
			if(activity_freq.containsKey(nodeName))
				nodeFreq = activity_freq.get(nodeName);
			else
				nodeFreq = 0;

			blockStructure.setFreq(nodeFreq);
			blockStructure.setBlockName(nodeName);
			blockStructure.setContentString(nodeName);
			blockStructure.setContainsBlockStructure(false);

		}

		Entry<BlockStructure, Integer> node_freq = new SimpleEntry(blockStructure, nodeFreq);

		return node_freq;

	}

	// White Box on only Activities(WBA)
		private List<Map<String, Integer>> buildBlockStructureFromTree_WBA(ProcessTree pt_detWin, ProcessTree pt_refWin,
				HashMap<String, Integer> activity_freq_detWin, HashMap<String, Integer> activity_freq_refWin)
		{

			List<BlockStructure> blockStructures_det = new ArrayList();
			List<BlockStructure> blockStructures_ref = new ArrayList();

			extractBlocks_WBA(pt_detWin.getRoot(), blockStructures_det, activity_freq_detWin);
			extractBlocks_WBA(pt_refWin.getRoot(), blockStructures_ref, activity_freq_refWin);

			Map<String, Integer> blockStructuresWithFreq_det = new HashMap<>();
			Map<String, Integer> blockStructuresWithFreq_ref = new HashMap<>();

			for(BlockStructure bs : blockStructures_det)
			{

				blockStructuresWithFreq_det.put(bs.getContentString(), bs.getFreq());

			}
			for(BlockStructure bs : blockStructures_ref)
			{

				blockStructuresWithFreq_ref.put(bs.getContentString(), bs.getFreq());

			}

			List<Map<String, Integer>> relationAndFrequencies_bothWins = new ArrayList<>();
			relationAndFrequencies_bothWins.add(blockStructuresWithFreq_det);
			relationAndFrequencies_bothWins.add(blockStructuresWithFreq_ref);

			return relationAndFrequencies_bothWins;

		}

		private Entry<BlockStructure, Integer> extractBlocks_WBA(Node n, List<BlockStructure> blockStructures, HashMap<String, Integer> activity_freq)
		{

			String nodeName = null;
			int nodeFreq = 0;
			BlockStructure blockStructure = new BlockStructure();
			if (n instanceof Block)
			{
				// n is an internal node (a Block)
				Block b = (Block) n;

				nodeName = getBlockName(b);

				StringBuilder blockContent = new StringBuilder();
				blockContent.append(nodeName + "(");

				blockStructure.setBlockName(nodeName);

				List<Integer> blockChildrenFreqs = new ArrayList<>();
				List<String> childrenNames = new ArrayList<>();
				// visit children of n
				for (Node c : b.getChildren()) {
					Entry<BlockStructure, Integer> childNodeName_freq = extractBlocks_WBA(c, blockStructures, activity_freq);
					BlockStructure childBlockStructure = childNodeName_freq.getKey();

					if(!childBlockStructure.ContainsBlockStructure())
					{

						String childName = childBlockStructure.getBlockName();
						childrenNames.add(childName);
						if(childName.compareToIgnoreCase("tau") != 0)
							blockStructure.getLeaves().add(childBlockStructure);

					}else
					{

						StringBuilder blockStructureContent = new StringBuilder();
						blockStructureContent.append(childBlockStructure.getBlockName() + "(");

						for (int k = 0; k < childBlockStructure.getLeaves().size(); k++)
						{

							BlockStructure leaf = childBlockStructure.getLeaves().get(k);
							blockStructure.getLeaves().add(leaf);
							if(k < (childBlockStructure.getLeaves().size() - 1))
								blockStructureContent.append(leaf + ", ");
							else
								blockStructureContent.append(leaf);

						}
						blockStructureContent.append(")");
						childrenNames.add(blockStructureContent.toString());

						blockStructure.setContainsBlockStructure(true);

					}
					blockStructure.getChildren().add(childBlockStructure);
					blockChildrenFreqs.add(childNodeName_freq.getValue());

				}

				Collections.sort(blockStructure.getLeaves());

				if (b instanceof Xor || b instanceof And || b instanceof Or)
					Collections.sort(childrenNames);

				for (int k = 0; k < childrenNames.size(); k++)
				{

					String childName = childrenNames.get(k);
					if(k < (childrenNames.size() - 1))
						blockContent.append(childName + ", ");
					else
						blockContent.append(childName);

				}

				blockContent.append(")");

				blockStructure.setContentString(blockContent.toString());

				nodeFreq = getBlockFreq(b, blockChildrenFreqs);
				blockStructure.setFreq(nodeFreq);

				blockStructures.add(blockStructure);

				Entry<BlockStructure, Integer> node_freq = new SimpleEntry(blockStructure, nodeFreq);

				return node_freq;

			}

			else {
				// n is a leaf (a Task)
				nodeName = n.getName();
				if(activity_freq.containsKey(nodeName))
					nodeFreq = activity_freq.get(nodeName);
				else
					nodeFreq = 0;

				blockStructure.setFreq(nodeFreq);
				blockStructure.setSize(1);
				blockStructure.setBlockName(nodeName);
				blockStructure.setContentString(nodeName);
				blockStructure.setContainsBlockStructure(false);

			}

			Entry<BlockStructure, Integer> node_freq = new SimpleEntry(blockStructure, nodeFreq);

			return node_freq;

		}

	// Hybrid approach(BB + WB)
	private List<Map<String, Integer>> buildBlockStructureFromTree_Hybrid(ProcessTree pt_detWin, ProcessTree pt_refWin,
			HashMap<String, Integer> activity_freq_detWin, HashMap<String, Integer> activity_freq_refWin)
	{

		List<BlockStructure> blockStructures_det = new ArrayList();
		List<BlockStructure> blockStructures_ref = new ArrayList();

		extractBlocks_Hybrid(pt_detWin.getRoot(), blockStructures_det, activity_freq_detWin);
		extractBlocks_Hybrid(pt_refWin.getRoot(), blockStructures_ref, activity_freq_refWin);

		compareBlockStructureLists(blockStructures_det, blockStructures_det); // look for bs similarities inside detection window
		compareBlockStructureLists(blockStructures_ref, blockStructures_ref); // look for bs similarities inside detection window
		compareBlockStructureLists(blockStructures_det, blockStructures_ref); // look for bs similarities between detection window and reference window

		Map<String, Integer> blockStructuresWithFreq_det = new HashMap<>();
		Map<String, Integer> blockStructuresWithFreq_ref = new HashMap<>();

		for(BlockStructure bs : blockStructures_det)
		{

			if(bs.ContainsBlockStructure())
				bs.unfoldBlockStructures();

			blockStructuresWithFreq_det.put(bs.getContentString(), bs.getFreq());

		}
		for(BlockStructure bs : blockStructures_ref)
		{

			if(bs.ContainsBlockStructure())
				bs.unfoldBlockStructures();

			blockStructuresWithFreq_ref.put(bs.getContentString(), bs.getFreq());

		}

		List<Map<String, Integer>> relationAndFrequencies_bothWins = new ArrayList<>();
		relationAndFrequencies_bothWins.add(blockStructuresWithFreq_det);
		relationAndFrequencies_bothWins.add(blockStructuresWithFreq_ref);

		return relationAndFrequencies_bothWins;

	}
//
	private Entry<BlockStructure, Integer> extractBlocks_Hybrid(Node n, List<BlockStructure> blockStructures, HashMap<String, Integer> activity_freq)
	{

		String nodeName = null;
		int nodeFreq = 0;
		BlockStructure blockStructure = new BlockStructure();
		if (n instanceof Block)
		{
			// n is an internal node (a Block)
			Block b = (Block) n;

			nodeName = getBlockName(b);

			StringBuilder blockContent = new StringBuilder();
			blockContent.append(nodeName + "(");

			blockStructure.setBlockName(nodeName);

			List<Integer> blockChildrenFreqs = new ArrayList<>();
			List<String> childrenNames = new ArrayList<>();
			// visit children of n
			for (Node c : b.getChildren()) {
				Entry<BlockStructure, Integer> childNodeName_freq = extractBlocks_Hybrid(c, blockStructures, activity_freq);
				BlockStructure childBlockStructure = childNodeName_freq.getKey();

				if(!childBlockStructure.ContainsBlockStructure())
				{

					String childName = childBlockStructure.getBlockName();
					childrenNames.add(childName);
					if(childName.compareToIgnoreCase("tau") != 0)
						blockStructure.getLeaves().add(childBlockStructure);

				}else
				{
					for (BlockStructure leaf: childBlockStructure.getLeaves())
					{

						blockStructure.getLeaves().add(leaf);

					}
					String childName = childBlockStructure.getBlockName();
					childrenNames.add(childName);

					boolean thereis = blockStructure.setToUnfoldBlockStructuresWithName(childName);
					if(thereis)
						childBlockStructure.setShouldBeUnfoldedInParent(true);

				}
				blockStructure.getChildren().add(childBlockStructure);
				blockChildrenFreqs.add(childNodeName_freq.getValue());

			}

			Collections.sort(blockStructure.getLeaves());

			if (b instanceof Xor || b instanceof And || b instanceof Or)
				Collections.sort(childrenNames);

			for (int k = 0; k < childrenNames.size(); k++)
			{

				String childName = childrenNames.get(k);
				if(k < (childrenNames.size() - 1))
					blockContent.append(childName + ", ");
				else
					blockContent.append(childName);

			}

			blockContent.append(")");

			blockStructure.setContainsBlockStructure(true);

			blockStructure.setContentString(blockContent.toString());

			nodeFreq = getBlockFreq(b, blockChildrenFreqs);
			blockStructure.setFreq(nodeFreq);

			blockStructures.add(blockStructure);

			Entry<BlockStructure, Integer> node_freq = new SimpleEntry(blockStructure, nodeFreq);

			return node_freq;

		}

		else {
			// n is a leaf (a Task)
			nodeName = n.getName();
			if(activity_freq.containsKey(nodeName))
				nodeFreq = activity_freq.get(nodeName);
			else
				nodeFreq = 0;

			blockStructure.setFreq(nodeFreq);
			blockStructure.setSize(1);
			blockStructure.setBlockName(nodeName);
			blockStructure.setContentString(nodeName);
			blockStructure.setContainsBlockStructure(false);

		}

		Entry<BlockStructure, Integer> node_freq = new SimpleEntry(blockStructure, nodeFreq);

		return node_freq;

	}

	private void compareBlockStructureLists(List<BlockStructure> bs1, List<BlockStructure> bs2)
	{

		for(BlockStructure bs_1 : bs1)
		{

			if(bs_1.ContainsBlockStructure())
			{

				for(BlockStructure bs_2 : bs2)
				{

					if(bs_1 != bs_2)
					{
						if(bs_2.ContainsBlockStructure())
						{

							if(!bs_1.AreAllBSsSetTobeUnfolded() || !bs_2.AreAllBSsSetTobeUnfolded())
							{
								if(bs_1.getContentString().compareToIgnoreCase(bs_2.getContentString()) == 0)
								{

									bs_1.setToUnfoldAllBlockStructures();
									bs_2.setToUnfoldAllBlockStructures();

								}

							}

						}

					}

				}

			}

		}

	}


	// White Box(WB) approach
	private List<Map<String, Integer>> buildBlockStructureFromTree_WB(ProcessTree pt_detWin, ProcessTree pt_refWin,
			HashMap<String, Integer> activity_freq_detWin, HashMap<String, Integer> activity_freq_refWin)
	{

		Map<String, Integer> blockStructuresWithFreq_det = new HashMap<>();
		Map<String, Integer> blockStructuresWithFreq_ref = new HashMap<>();

		extractBlocks_WB(pt_detWin.getRoot(), blockStructuresWithFreq_det, activity_freq_detWin);
		extractBlocks_WB(pt_refWin.getRoot(), blockStructuresWithFreq_ref, activity_freq_refWin);

		List<Map<String, Integer>> relationAndFrequencies_bothWins = new ArrayList<>();
		relationAndFrequencies_bothWins.add(blockStructuresWithFreq_det);
		relationAndFrequencies_bothWins.add(blockStructuresWithFreq_ref);

		return relationAndFrequencies_bothWins;

	}
	private HashMap<String, Integer> extractBlocks_WB(Node n, Map<String, Integer> blockStructuresWithFreq, HashMap<String, Integer> activity_freq)
	{

		String nodeContent = null;
		int nodeFreq = 0;
		if (n instanceof Block)
		{
			// n is an internal node (a Block)
			Block b = (Block) n;

			String nodeName = getBlockName(b);

			StringBuilder blockContent = new StringBuilder();
			blockContent.append(nodeName + "(");

			List<Integer> blockChildrenFreqs = new ArrayList<>();
			List<String> childrenNames = new ArrayList<>();
			// visit children of n
			for (Node c : b.getChildren()) {

				HashMap<String, Integer> childNodeName_freq = extractBlocks_WB(c, blockStructuresWithFreq, activity_freq);
				String childName = (String)childNodeName_freq.keySet().toArray()[0];
				childrenNames.add(childName);
				blockChildrenFreqs.add(childNodeName_freq.get(childName));

			}

			if (b instanceof Xor || b instanceof And || b instanceof Or)
				Collections.sort(childrenNames);

			for (int k = 0; k < childrenNames.size(); k++)
			{

				String childName = childrenNames.get(k);
				if(k < (childrenNames.size() - 1))
					blockContent.append(childName + ", ");
				else
					blockContent.append(childName);

			}

			blockContent.append(")");

			nodeFreq = getBlockFreq(b, blockChildrenFreqs);

			blockStructuresWithFreq.put(blockContent.toString(), nodeFreq);

			nodeContent = blockContent.toString();

		}

		else {
			// n is a leaf (a Task)
			nodeContent = n.getName();
			if(activity_freq.containsKey(nodeContent))
				nodeFreq = activity_freq.get(nodeContent);
			else
				nodeFreq = 0;

		}

		HashMap<String, Integer> node_freq = new HashMap<>();
		node_freq.put(nodeContent, nodeFreq);

		return node_freq;

	}


	// Black Box(BB) approach
		private List<Map<String, Integer>> buildBlockStructureFromTree_BB(ProcessTree pt_detWin, ProcessTree pt_refWin,
				HashMap<String, Integer> activity_freq_detWin, HashMap<String, Integer> activity_freq_refWin)
		{

			Map<String, Integer> blockStructuresWithFreq_det = new HashMap<>();
			Map<String, Integer> blockStructuresWithFreq_ref = new HashMap<>();

			extractBlocks_BB(pt_detWin.getRoot(), blockStructuresWithFreq_det, activity_freq_detWin);
			extractBlocks_BB(pt_refWin.getRoot(), blockStructuresWithFreq_ref, activity_freq_refWin);

			List<Map<String, Integer>> relationAndFrequencies_bothWins = new ArrayList<>();
			relationAndFrequencies_bothWins.add(blockStructuresWithFreq_det);
			relationAndFrequencies_bothWins.add(blockStructuresWithFreq_ref);

			return relationAndFrequencies_bothWins;

		}
		private HashMap<String, Integer> extractBlocks_BB(Node n, Map<String, Integer> blockStructuresWithFreq, HashMap<String, Integer> activity_freq)
		{

			String nodeName = null;
			int nodeFreq = 0;
			if (n instanceof Block)
			{
				// n is an internal node (a Block)
				Block b = (Block) n;

				nodeName = getBlockName(b);

				StringBuilder blockContent = new StringBuilder();
				blockContent.append(nodeName + "(");

				List<Integer> blockChildrenFreqs = new ArrayList<>();
				List<String> childrenNames = new ArrayList<>();
				// visit children of n
				for (Node c : b.getChildren()) {

					HashMap<String, Integer> childNodeName_freq = extractBlocks_BB(c, blockStructuresWithFreq, activity_freq);
					String childName = (String)childNodeName_freq.keySet().toArray()[0];
					childrenNames.add(childName);
					blockChildrenFreqs.add(childNodeName_freq.get(childName));

				}

				if (b instanceof Xor || b instanceof And || b instanceof Or)
					Collections.sort(childrenNames);

				for (int k = 0; k < childrenNames.size(); k++)
				{

					String childName = childrenNames.get(k);
					if(k < (childrenNames.size() - 1))
						blockContent.append(childName + ", ");
					else
						blockContent.append(childName);

				}

				blockContent.append(")");

				nodeFreq = getBlockFreq(b, blockChildrenFreqs);

				blockStructuresWithFreq.put(blockContent.toString(), nodeFreq);

			}

			else {
				// n is a leaf (a Task)
				nodeName = n.getName();
				if(activity_freq.containsKey(nodeName))
					nodeFreq = activity_freq.get(nodeName);
				else
					nodeFreq = 0;

			}

			HashMap<String, Integer> node_freq = new HashMap<>();
			node_freq.put(nodeName, nodeFreq);

			return node_freq;

		}


	public String getBlockName(Block b)
	{

		// figure out the operator type of this block
		if (b instanceof And) {
			return "AND_block";
		}else if (b instanceof Xor) {
			return "XOR_block";
		}else if (b instanceof Or) {
			return "Or_block";
		}else if (b instanceof Seq) {
			return "Seq_block";
		}else if (b instanceof XorLoop) {
			return "XorLoop_block";
		}else if (b instanceof Def) {
			return "Def_block";
		}else if (b instanceof DefLoop) {
			return "DefLoop_block";
		}else if (b instanceof PlaceHolder) {
			return "PlaceHolder_block";
		}else{
			return "OTHER_block";
		}

	}

	public int getBlockFreq(Block b, List<Integer> blockChildrenFreqs)
	{

		if (b instanceof And) {

			int freq = Utils.getMax(blockChildrenFreqs); // freq of an AND gate equals to the freq of either of its children
			return freq;

		}else if (b instanceof Xor) {

			int freq = Utils.getSum(blockChildrenFreqs); // freq of an XOR gate equals to the sum of freqs of its children
			return freq;

		}else if (b instanceof Or) {

			int freq = Utils.getSum(blockChildrenFreqs); // freq of an OR gate equals to the sum of freqs of its children
			return freq;

		}else if (b instanceof Seq) {

			int freq = Utils.getMax(blockChildrenFreqs); // freq of a Seq gate equals to the freq of either of its children
			return freq;

		}else if (b instanceof XorLoop) {

			int freq = blockChildrenFreqs.get(0); // freq of an XORLoop gate equals to the freq of loop back
			return freq;

		}else if (b instanceof Def) {

			int freq = Utils.getMax(blockChildrenFreqs);
			return freq;

		}else if (b instanceof DefLoop) {

			int freq = Utils.getMax(blockChildrenFreqs);
			return freq;

		}else if (b instanceof PlaceHolder) {

			int freq = Utils.getMax(blockChildrenFreqs);
			return freq;

		}else{

			int freq = Utils.getMax(blockChildrenFreqs);
			return freq;

		}

	}
	*/
	public static void buildActivityFrequency(XLog subLog, HashMap<String, Integer> Activity_Freq, HashMap<String, Set<String>> Activity_TraceSet)
	{

		for(int i = 0; i < subLog.size(); i++)
		{

			XTrace trace = subLog.get(i);
			String traceID = XLogManager.getTraceID(trace);

			for(int j = 0; j < trace.size(); j++)
			{

				XEvent curEvent = trace.get(j);
				String eventName = XLogManager.getEventName(curEvent);

				if(Activity_Freq.containsKey(eventName))
				{

					Activity_Freq.put(eventName, Activity_Freq.get(eventName) + 1);
					Set<String> traceSet = Activity_TraceSet.get(eventName);
					traceSet.add(traceID);

				}else
				{

					Activity_Freq.put(eventName, 1);
					Set<String> traceSet = new TreeSet<>();
					traceSet.add(traceID);
					Activity_TraceSet.put(eventName, traceSet);

				}

			}

		}


	}

	public void updateActivityFreqMap_addEvent(HashMap<String, Integer> Activity_Freq, XTrace _event)
	{

		XEvent curEvent = _event.get(0);
		String eventName = XLogManager.getEventName(curEvent);
		String traceID = XLogManager.getTraceID(_event);

		if(Activity_Freq.containsKey(eventName))
		{

			Activity_Freq.put(eventName, Activity_Freq.get(eventName) + 1);

		}else
		{

			Activity_Freq.put(eventName, 1);

		}

	}

	public void updateActivityFreqMap_removeEvent(XLog subLog, HashMap<String, Integer> TraceId_TraceIndex_InSubLog_Map, HashMap<String, Integer> Activity_Freq, XTrace _event)
	{

		XEvent curEvent = _event.get(0);
		String eventName = XLogManager.getEventName(curEvent);


		if(Activity_Freq.containsKey(eventName))
		{

			int freq = Activity_Freq.get(eventName);
			if(freq == 1)
			{

				Activity_Freq.remove(eventName);

			}
			else
			{

				Activity_Freq.put(eventName, freq - 1);

			}


		}

	}

	public void updateActivityTraceSetMap_addEvent(XTrace _event, HashMap<String, Set<String>> Activity_TraceSet)
	{

		XEvent curEvent = _event.get(0);
		String eventName = XLogManager.getEventName(curEvent);
		String traceID = XLogManager.getTraceID(_event);

		if(Activity_TraceSet.containsKey(eventName))
		{

			Set<String> traceSet = Activity_TraceSet.get(eventName);
			traceSet.add(traceID);

		}else
		{

			Set<String> traceSet = new TreeSet<>();
			traceSet.add(traceID);
			Activity_TraceSet.put(eventName, traceSet);

		}

	}

	public void updateActivityTraceSetMap_removeEvent(XLog subLog, HashMap<String, Integer> TraceId_TraceIndex_InSubLog_Map, XTrace _event, HashMap<String, Set<String>>Activity_TraceSet)
	{

		XEvent curEvent = _event.get(0);
		String eventName = XLogManager.getEventName(curEvent);

		String traceID = XLogManager.getTraceID(_event);
		Integer TraceIndex_InSubLog = TraceId_TraceIndex_InSubLog_Map.get(traceID);

		Set<String> traceSet = Activity_TraceSet.get(eventName);
		if(org.apromore.prodrift.logmodifier.XLogManager.countEvent(subLog.get(TraceIndex_InSubLog), eventName) == 1)
			traceSet.remove(traceID);

		if(traceSet.size() == 0)
			Activity_TraceSet.remove(eventName);

	}

	// To aggregate noise columns in frequency matrix
	public long[][] aggregateNoise(long [][]freqMatrix, Integer totalRelationFreq_detWin, Integer totalRelationFreq_refWin)
	{

		int noiseColumns = 0;
		for (int i = 0; i < freqMatrix[1].length; i++) {

			if (!fulfillsFrequencyRequirement(totalRelationFreq_detWin.intValue(), freqMatrix[0][i], relationNoiseThresh) &&
					!fulfillsFrequencyRequirement(totalRelationFreq_refWin.intValue(), freqMatrix[1][i], relationNoiseThresh))
			{

				noiseColumns++;

			}

		}

		long noiseAggregate_detWin = 0;
		long noiseAggregate_refWin = 0;

		long [][] freqMatrixAfterNoiseAggregation = new long[2][freqMatrix[1].length - noiseColumns + 1];
		int index = 0;
		for (int i = 0; i < freqMatrix[1].length; i++) {

			if (fulfillsFrequencyRequirement(totalRelationFreq_detWin.intValue(), freqMatrix[0][i], relationNoiseThresh) ||
					fulfillsFrequencyRequirement(totalRelationFreq_refWin.intValue(), freqMatrix[1][i], relationNoiseThresh))
			{

				freqMatrixAfterNoiseAggregation[0][index] = freqMatrix[0][i];
				freqMatrixAfterNoiseAggregation[1][index] = freqMatrix[1][i];
				index++;

			}else
			{

				noiseAggregate_detWin += freqMatrix[0][i];
				noiseAggregate_refWin += freqMatrix[1][i];

			}

		}

		freqMatrixAfterNoiseAggregation[0][index] = noiseAggregate_detWin;
		freqMatrixAfterNoiseAggregation[1][index] = noiseAggregate_refWin;

		return freqMatrixAfterNoiseAggregation;

	}

	// To remove noise from frequency matrix
	public long[][] removeNoise_sum(long [][]freqMatrix, Integer totalRelationFreq_detWin, Integer totalRelationFreq_refWin)
	{

//		long maxFreq0 = 0, maxFreq1 = 0;
//
//		for (int i = 0; i < freqMatrix[1].length; i++) {
//
//			if (freqMatrix[0][i] > maxFreq0)
//				maxFreq0 = freqMatrix[0][i];
//			if (freqMatrix[1][i] > maxFreq1)
//				maxFreq1 = freqMatrix[1][i];
//
//		}

		int noiseColumns = 0;
		for (int i = 0; i < freqMatrix[1].length; i++) {

			if (!fulfillsFrequencyRequirement(totalRelationFreq_detWin.intValue(), freqMatrix[0][i], relationNoiseThresh) &&
					!fulfillsFrequencyRequirement(totalRelationFreq_refWin.intValue(), freqMatrix[1][i], relationNoiseThresh))
			{

				noiseColumns++;

			}

		}

		long [][] freqMatrixAfterNoiseRemoval = new long[2][freqMatrix[1].length - noiseColumns];
		int index = 0;
		for (int i = 0; i < freqMatrix[1].length; i++) {

			if (fulfillsFrequencyRequirement(totalRelationFreq_detWin.intValue(), freqMatrix[0][i], relationNoiseThresh) ||
					fulfillsFrequencyRequirement(totalRelationFreq_refWin.intValue(), freqMatrix[1][i], relationNoiseThresh))
			{

				freqMatrixAfterNoiseRemoval[0][index] = freqMatrix[0][i];
				freqMatrixAfterNoiseRemoval[1][index] = freqMatrix[1][i];
				index++;

			}

		}

		return freqMatrixAfterNoiseRemoval;

	}

	// To remove noise from frequency matrix
	public long[][] removeNoise_max(long [][]freqMatrix, Map<Integer, Entry<Pair<XEventClass, XEventClass>, RelationFrequency>> index_relation)
	{
		Map<Integer, Entry<Pair<XEventClass, XEventClass>, RelationFrequency>> index_relation_new = new HashMap<>();

		long maxFreq0 = 0, maxFreq1 = 0;
//
		for (int i = 0; i < freqMatrix[1].length; i++) {

			if (freqMatrix[0][i] > maxFreq0)
				maxFreq0 = freqMatrix[0][i];
			if (freqMatrix[1][i] > maxFreq1)
				maxFreq1 = freqMatrix[1][i];

		}

		int noiseColumns = 0;
		for (int i = 0; i < freqMatrix[1].length; i++) {

			if (!fulfillsFrequencyRequirement(maxFreq0, freqMatrix[0][i], relationNoiseThresh) &&
					!fulfillsFrequencyRequirement(maxFreq1, freqMatrix[1][i], relationNoiseThresh))
			{

				noiseColumns++;

			}

		}

		long [][] freqMatrixAfterNoiseRemoval = new long[2][freqMatrix[1].length - noiseColumns];
		int index = 0;
		for (int i = 0; i < freqMatrix[1].length; i++) {

			if (fulfillsFrequencyRequirement(maxFreq0, freqMatrix[0][i], relationNoiseThresh) ||
					fulfillsFrequencyRequirement(maxFreq1, freqMatrix[1][i], relationNoiseThresh))
			{

				freqMatrixAfterNoiseRemoval[0][index] = freqMatrix[0][i];
				freqMatrixAfterNoiseRemoval[1][index] = freqMatrix[1][i];
				index++;

				if(index_relation != null)
					index_relation_new.put(index - 1, index_relation.get(i));

			}

		}
		if(index_relation != null)
		{	index_relation.clear();
			index_relation.putAll(index_relation_new);
		}

		return freqMatrixAfterNoiseRemoval;

	}

	public long[][] removeNoise_bottomRelations(long [][]freqMatrix, Integer totalRelationFreq_detWin, Integer totalRelationFreq_refWin)
	{

//		long maxFreq0 = 0, maxFreq1 = 0;
//
//		for (int i = 0; i < freqMatrix[1].length; i++) {
//
//			if (freqMatrix[0][i] > maxFreq0)
//				maxFreq0 = freqMatrix[0][i];
//			if (freqMatrix[1][i] > maxFreq1)
//				maxFreq1 = freqMatrix[1][i];
//
//		}

		freqMatrix = Utils.rotate90(freqMatrix);

		java.util.Arrays.sort(freqMatrix, new Comparator<long[]>() {
			@Override
			public int compare(final long[] entry1, final long[] entry2) {
				final Long sum1 = entry1[0] + entry1[1];
				final Long sum2 = entry2[0] + entry2[1];
				return sum2.compareTo(sum1);
			}
		});

		int nRemovedRows = (int)(freqMatrix.length * relationNoiseThresh);

		nRemovedRows = nRemovedRows >= freqMatrix.length ? freqMatrix.length - 1: nRemovedRows;

		long [][] freqMatrixAfterNoiseRemoval = new long[freqMatrix.length - nRemovedRows][2];
		int index = 0;
		for (int i = 0; i < freqMatrixAfterNoiseRemoval.length; i++) {
			freqMatrixAfterNoiseRemoval[i][0] = freqMatrix[i][0];
			freqMatrixAfterNoiseRemoval[i][1] = freqMatrix[i][1];
		}

		freqMatrixAfterNoiseRemoval = Utils.rotate90(freqMatrixAfterNoiseRemoval);

		return freqMatrixAfterNoiseRemoval;

	}

	public long[][] removeFrequencyDrift(long [][]freqMatrix, Integer totalRelationFreq_detWin, Integer totalRelationFreq_refWin)
	{

//		long maxFreq0 = 0, maxFreq1 = 0;
//
//		for (int i = 0; i < freqMatrix[1].length; i++) {
//
//			if (freqMatrix[0][i] > maxFreq0)
//				maxFreq0 = freqMatrix[0][i];
//			if (freqMatrix[1][i] > maxFreq1)
//				maxFreq1 = freqMatrix[1][i];
//
//		}


		long [][] freqMatrixAfterFreqDriftRemoval = new long[2][freqMatrix[1].length];
		for (int i = 0; i < freqMatrix[1].length; i++) {

			if (freqMatrix[0][i] != 0 && freqMatrix[1][i] != 0)
			{

				freqMatrixAfterFreqDriftRemoval[0][i] = freqMatrix[0][i];
				freqMatrixAfterFreqDriftRemoval[1][i] = freqMatrix[0][i];

			}else
			{
				freqMatrixAfterFreqDriftRemoval[0][i] = freqMatrix[0][i];
				freqMatrixAfterFreqDriftRemoval[1][i] = freqMatrix[1][i];
			}

		}

		return freqMatrixAfterFreqDriftRemoval;

	}

	public boolean fulfillsFrequencyRequirement(long baseSize, long relationFreq, float ratio)
	{

		if(((float)relationFreq / (float)baseSize) >= ratio)
			return true;
		else
			return false;

	}

	public long[][] filterInsignificantChanges(long [][]freqMatrix, Integer totalRelationFreq_detWin, Integer totalRelationFreq_refWin)
	{

		int significantColumns = 0;
		for (int i = 0; i < freqMatrix[1].length; i++) {

			if (freqMatrix[0][i] >= freqMatrix[1][i] * 2 || freqMatrix[1][i] >= freqMatrix[0][i] * 2)
			{

				significantColumns++;

			}

		}

		long [][] freqMatrixAfterInsignificantRemoval = new long[2][significantColumns];
		int index = 0;
		for (int i = 0; i < freqMatrix[1].length; i++) {

			if (freqMatrix[0][i] >= freqMatrix[1][i] * 2 || freqMatrix[1][i] >= freqMatrix[0][i] * 2)
			{

				freqMatrixAfterInsignificantRemoval[0][index] = freqMatrix[0][i];
				freqMatrixAfterInsignificantRemoval[1][index] = freqMatrix[1][i];
				index++;

			}

		}

		return freqMatrixAfterInsignificantRemoval;

	}

	public int checkQSquareFrequencyRequirment(long runsFreqWindow[])
	{
		// All expected counts are > 1 and no more than 20% of expected counts are less than 5 (According to wikipedia)
		int sumOfneededFreq = 0;
		Arrays.sort(runsFreqWindow);
		int excludedColumnsCount = (int)Math.floor(((float)runsFreqWindow.length) * relationNoiseThresh);

		for(int i = runsFreqWindow.length - 1; i >= excludedColumnsCount; i--)
		{
			if(runsFreqWindow[i] < minExpectedFrequency)
			{

				sumOfneededFreq += minExpectedFrequency - runsFreqWindow[i];

			}

		}

		for(int i = excludedColumnsCount - 1; i >= 0; i--)
		{
			if(runsFreqWindow[i] < 1)
			{

				sumOfneededFreq += 1 - runsFreqWindow[i];

			}

		}

		return sumOfneededFreq;

	}


//	@Test
//	public void test() throws Exception {
//		XLog log = XLogReader.openLog(String.format(fileName_trace, model));
//		AlphaRelations alphaRelations = new AlphaRelations(log);
//
//	    long time = System.nanoTime();
//		PartiallyOrderedRun runs = new PartiallyOrderedRun(alphaRelations, model);
//
//		for (int i = 0; i < log.size(); i++) {
//			XTrace trace = log.get(i);
//			runs.addTrace(trace);
//
//		}
//
//		IOUtils.toFile(model + "_prefix.dot", runs.toDot());
//		IOUtils.toFile(model + "_DistinctRuns.dot", runs.DRtoDot());
//		runs.mergePrefix();
//		IOUtils.toFile(model + "_merged.dot", runs.toDot());
//
//
//		PrimeEventStructure<Integer> pes = runs.getPrimeEventStructure();
//		pes.pack();
//		dumpPES(model, pes);
//
//	    System.out.println("Overall time: " + (System.nanoTime() - time) / 1000000000.0);
//	}


	public List<String> getTraces_with_b_butNot_a(XLog sublog)
	{

		List<String> traceIDs = new ArrayList<>();
		for(int i = 0; i < sublog.size(); i++)
		{

			XTrace trace = sublog.get(i);
			String traceID = XLogManager.getTraceID(trace);
			boolean has_b = false;
			boolean has_a = false;

			for(int j = 0; j < trace.size(); j++)
			{

				XEvent curEvent = trace.get(j);
				if(XLogManager.getEventName(curEvent).compareToIgnoreCase("b") == 0)
					has_b = true;
				if(XLogManager.getEventName(curEvent).compareToIgnoreCase("a") == 0)
					has_a = true;

			}
			if(has_b && !has_a)
			{

				traceIDs.add(traceID);

			}

		}

		return traceIDs;

	}

	public void printTopRun(int[][] runsFreqMatrix) {
		// TODO Auto-generated method stub

	}

//	public void winSizeTest() {
//		int logsize = log.size();
//		System.out.println("log size "+logsize);
//		int step_winsize = logsize / 200;
//		System.out.println("step size" + step_winsize);
//		for (int i = 1; i < 6; i++) {
//			initialwinSize = step_winsize * i;
//			System.out.println("excution for window size =" + initialwinSize);
//			this.findDrifts();
//		}
//	}

	public void dumpPES(String model, PrimeEventStructure<Integer> pes)
			throws FileNotFoundException {
		System.out.println("Done with pes");
		PrintStream out = null;
		out = new PrintStream("target/"+model+".pes.tex");
		pes.toLatex(out);
		out.close();
		System.out.println("Done with toLatex");
	}










}
