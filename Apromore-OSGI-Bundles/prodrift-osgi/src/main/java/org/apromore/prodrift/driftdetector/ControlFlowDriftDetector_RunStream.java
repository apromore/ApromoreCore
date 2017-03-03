/*
 * Copyright  2009-2017 The Apromore Initiative.
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
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.math3.stat.inference.ChiSquareTest;
import org.apache.commons.math3.stat.inference.GTest;
import org.apromore.prodrift.config.DriftConfig;
import org.apromore.prodrift.config.StatisticTestConfig;
import org.apromore.prodrift.config.WindowConfig;
import org.apromore.prodrift.main.Main;
import org.apromore.prodrift.mining.log.local.AlphaRelations;
import org.apromore.prodrift.mining.log.local.PartiallyOrderedRun;
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
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.jfree.chart.JFreeChart;

import jsc.contingencytables.ContingencyTable;
import jsc.independentsamples.MannWhitneyTest;
import jsc.independentsamples.SmirnovTest;



public class ControlFlowDriftDetector_RunStream implements ControlFlowDriftDetector{

	

	private DriftConfig driftConfig = DriftConfig.TRACE; 
	private WindowConfig winConfig = WindowConfig.ADWIN;
	private StatisticTestConfig statisticTest = StatisticTestConfig.QS;
	
	private String logFileName = "";
	private String fileName_trace  = "./%s.mxml";
	
	private Path logPath = null;
	
	private String windowHeader = "";
	
	public static int numberOfDriftGoldStandard = 9; //beeing the number of event logs that i have concatinated	
	
	private double typicalThreshold = 0.05;
	private int initialwinSize = 100; // it has to be less than log.size/2
	private double winSizeReal = 100;
	
	private XFactory factory = XFactoryRegistry.instance().currentDefault();
	
	
	private XLog log;
	private PrecisionRecallCurve PRcurves = new PrecisionRecallCurve(typicalThreshold, numberOfDriftGoldStandard);
	private MeanDelayCurve MDcurves = new MeanDelayCurve(typicalThreshold, numberOfDriftGoldStandard);
	private confusionMat T_confMat, R_confMat;
	private double T_meandelay = 0, R_meandelay = 0;
	private long OverallTime = 0;

	private String driftSPOutput = "";
	private String ratioDis ="";

	private int ReduceWhenDimensionsAbove = 1000000;
	private int minExpectedFrequency = 0;
	private float exclusionRate = 1.00f;
	
	boolean isCPNToolsLog = true;

	
	boolean TESTGRAD = false;
	private double windowFilterFactor = 0.25;
	List<Integer> startOfGradDrifts = null;
	List<Integer> endOfGradDrifts = null;
	List<Integer> LastReadGradDrifts = null;
	
	
	public ControlFlowDriftDetector_RunStream(XLog xlog, int winsize, boolean isAdwin,
			String logFileName, boolean TESTGRAD) {

		Main.isStandAlone = true;
		
		XLog xl = xlog;
		xl = XLogManager.orderByTraceCompletionTimeStamp(xl);
		log = xl;
		
		this.initialwinSize = winsize;
		this.winSizeReal = winsize;
		this.driftConfig = DriftConfig.RUN;
		this.statisticTest = StatisticTestConfig.QS;
		this.winConfig = isAdwin ? WindowConfig.ADWIN : WindowConfig.FWIN;
		this.TESTGRAD = TESTGRAD;
		
		this.isCPNToolsLog = false;
		
		String RelationType = "";
		if(this.driftConfig == DriftConfig.RUN)
			RelationType = "Runs";
		else
			RelationType = this.driftConfig.toString();
		
		String gradualStr = TESTGRAD ? ", Detect gradual drift = yes" : ", Detect gradual drift = no";
		this.windowHeader = "P-value Diagram"+" (" + logFileName +  ", Initial window size = "+ initialwinSize + ", Window type = " + winConfig.toString() + ", Relation Type = " + RelationType + gradualStr + ")";
		
	}
	
	public ControlFlowDriftDetector_RunStream(XLog xlog, int winsize, boolean isAdwin, boolean withGradualDrift) {

		Main.isStandAlone = true;
		
		XLog xl = xlog;
		xl = XLogManager.orderByTraceCompletionTimeStamp(xl);
		log = xl;
		
		this.initialwinSize = winsize;
		this.winSizeReal = winsize;
		this.driftConfig = DriftConfig.RUN;
		this.statisticTest = StatisticTestConfig.QS;
		this.winConfig = isAdwin ? WindowConfig.ADWIN : WindowConfig.FWIN;
		this.TESTGRAD = withGradualDrift;
		
		this.isCPNToolsLog = false;
		
		String RelationType = "";
		if(this.driftConfig == DriftConfig.RUN)
			RelationType = "Runs";
		else
			RelationType = this.driftConfig.toString();
		
		String gradualStr = withGradualDrift ? ", Detect gradual drift = yes" : ", Detect gradual drift = no";
		this.windowHeader = "P-value Diagram"+" (Initial window size = "+ initialwinSize + ", Window type = " + winConfig.toString() + ", Relation Type = " + RelationType + gradualStr + ")";
		
	}
	
	public ControlFlowDriftDetector_RunStream(XLog logFIle, int winsize, DriftConfig Dcfg, StatisticTestConfig statTest, WindowConfig winCfg,
			Path logPath, boolean isCPNToolsLog, boolean TESTGRAD) {
//		this.logFile =  logFilePath;
		this.initialwinSize = winsize;
		this.winSizeReal = winsize;
		this.driftConfig = DriftConfig.RUN;
		this.statisticTest = statTest;
		this.winConfig = winCfg;
		log = logFIle;
		this.logPath = logPath;
		this.isCPNToolsLog = isCPNToolsLog;
		
		this.TESTGRAD = TESTGRAD;
		
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
		
		int gradDriftIndex = 0;
		for(int i = 0; i < MDcurves.getDriftPoints().size(); i++)
		{
			
			if(startOfGradDrifts.size() > gradDriftIndex && MDcurves.getEndOfTransitionPoints().get(i+1).intValue() == startOfGradDrifts.get(gradDriftIndex).intValue() &&
					MDcurves.getStartOfTransitionPoints().get(i+1).intValue() == endOfGradDrifts.get(gradDriftIndex).intValue())
			{
				
				XTrace trace_st = log.get(startOfGradDrifts.get(gradDriftIndex).intValue());
				Date driftTime_st = XLogManager.getEventTime(trace_st.get(trace_st.size() - 1)).getValue();
				
				XTrace trace_end = log.get(endOfGradDrifts.get(gradDriftIndex).intValue());
				Date driftTime_end = XLogManager.getEventTime(trace_end.get(trace_end.size() - 1)).getValue();
				
				pddres.getDriftStatements().add("Gradual drift detected between trace: " + startOfGradDrifts.get(gradDriftIndex).intValue() + " (" + driftTime_st + ") and trace: " + endOfGradDrifts.get(gradDriftIndex).intValue() + " (" + driftTime_end + ").");
				pddres.getIsGradual().add(true);
				
				gradDriftIndex++;
				i++;
				
			}else
			{
				
				pddres.getDriftStatements().add("Sudden drift detected at trace: " + MDcurves.getDriftPoints().get(i).intValue() + " (" + MDcurves.getDriftDates().get(i).toString() + ") after reading " + MDcurves.getLastReadTrace().get(i).intValue() + " traces.");
				pddres.getIsGradual().add(false);
				
			}
			
			
		}
		
		return pddres;
		
	}
	
	
	public JFreeChart findDrifts() {
		
		MDcurves.getDriftPoints().clear();
		MDcurves.getLastReadTrace().clear();
		MDcurves.getStartOfTransitionPoints().clear();
		MDcurves.getEndOfTransitionPoints().clear();
		MDcurves.getpValuesAtDrifts().clear();
		MDcurves.getDriftDates().clear();
		
		startOfGradDrifts = new ArrayList<>();
		endOfGradDrifts = new ArrayList<>();
		LastReadGradDrifts = new ArrayList<>();
		
		XLog eventStream = LogStreamer.logStreamer(log, null);
		
		List<DriftPoint> DriftPointsList = new ArrayList<>();
		
		if(isCPNToolsLog)
			XLogManager.printActualDriftPoints(eventStream, DriftPointsList);
		
		int winSize = initialwinSize;
		LinePlot plotting = new LinePlot(this.getClass().getName(), windowHeader, "Completed traces", "P-value"); //		
		
		
		long time = System.nanoTime();

		//splitLogIntoChunks(log,winSize);
		
		AlphaRelations alphaRelations = new AlphaRelations(log);
		PartiallyOrderedRun strHandler = new PartiallyOrderedRun(alphaRelations, logFileName, log);
		
		double pValue=0;
		
		switch (driftConfig) {
		
			case RUN:	
				driftSPOutput += "\nRUNS, ";
				int runCurveIndex = plotting.AddCurve("P-value curve");
				ArrayList<Double> runpValVector = new ArrayList<>();
				ArrayList<Integer> winSizeVector = new ArrayList<>();
				strHandler.runsToDisctrib();
				
//				System.out.println("# of Distinct Runs: " + strHandler.getDistinctRuns().size());
//				System.out.println("# of Runs: " + log.size());
//				System.out.println("Distinct Runs/Runs ratio: " + (float)strHandler.getDistinctRuns().size()/(float)log.size());
//				strHandler.tracesToDisctrib();
//				System.out.println("Distinct trace/traces ratio: " + (float)strHandler.getExistingTraces().size()/(float)log.size());
				
//				System.out.println();
				int logIdex;
				for (int i = 0; i < 2*winSize; i++) driftSPOutput += "1, ";
				for (logIdex = 0; logIdex < 2 * winSize - 1; logIdex++){//2*win-1 is the initial offset
					runpValVector.add(1.0);
					winSizeVector.add(winSize);
				} 
				
				ArrayList<Integer> numOfDistinctRuns = new ArrayList<>();
								
				// for BPM 2015 ADWIN
				long rfm[][] = buildRunsFrequencyMatrix(log, strHandler, winSize, logIdex);
				double oldWinSizeReal = winSize;
				int oldColumnCount = rfm[0].length;
				
				
				for (; logIdex < log.size(); logIdex++) {
					
					switch (statisticTest) {
						case KS:
							double [] spl1 = PartiallyOrderedRun.copyFromIntArray(strHandler.getPastRunsSampleValuesInt(logIdex,winSize));
							double [] spl2 = PartiallyOrderedRun.copyFromIntArray(strHandler.getPastRunsSampleValuesInt(logIdex-winSize,winSize));
							SmirnovTest KSTest = new SmirnovTest(spl1, spl2);
							pValue = KSTest.getSP();
							break;
							
						case MW:
							spl1 = PartiallyOrderedRun.copyFromIntArray(strHandler.getPastRunsSampleValuesInt(logIdex,winSize));
							spl2 = PartiallyOrderedRun.copyFromIntArray(strHandler.getPastRunsSampleValuesInt(logIdex-winSize,winSize));
							MannWhitneyTest MWTest = new MannWhitneyTest(spl1, spl2);
							pValue = MWTest.getSP();
							break;
							
						case QS:
						{
							//----ADWIN
							if (this.winConfig == WindowConfig.ADWIN)
							{

//								winSize = ADWin_RUN_ChiSQ(log, strHandler, winSize, logIdex);
								
								double []result = ADWin_RUN_ChiSQ_BPM2015(log, strHandler, winSize, oldWinSizeReal, oldColumnCount, logIdex);
								winSize = (int)result[0];
								oldWinSizeReal = result[1];
								oldColumnCount = (int)result[2];
								
							}
							
							long runsFreqMatrix[][] = buildRunsFrequencyMatrix(log, strHandler, winSize, logIdex);
//							System.out.println("Log Index: " + logIdex + ", Win Size: " + winSize + ", Distinct Runs: " + runsFreqMatrix[1].length);
							numOfDistinctRuns.add(runsFreqMatrix[1].length);
							
							
							
//							Utils.printArray(runsFreqMatrix[0]);
//							System.out.println();
//							Utils.printArray(runsFreqMatrix[1]);
//							System.out.println();
//							System.out.println("***************************************************");
							
							
//							XLogManager.printTrace(log.get(logIdex));
							
							if(runsFreqMatrix[0].length > ReduceWhenDimensionsAbove)
								runsFreqMatrix = Utils.reduceDimensionality_cuttingNonValueAddingColumns(runsFreqMatrix);
							
							if(runsFreqMatrix[1].length > 1)
							{
								
//								int runsFreqMatrix2[][] = new int[runsFreqMatrix.length][runsFreqMatrix[0].length];
//								for (int i = 0; i < runsFreqMatrix.length; i++) {
//									for (int j = 0; j < runsFreqMatrix[0].length; j++) {
//										runsFreqMatrix2[i][j] = (int)runsFreqMatrix[i][j];
//									}
//								}
								
								ChiSquareTest cst = new ChiSquareTest();	
								try{
									
									pValue = cst.chiSquareTestDataSetsComparison(runsFreqMatrix[1], runsFreqMatrix[0]);
									
								}catch(Exception ex)
								{
									pValue = 1;
								}
								
								
								
								
							}else
							{
								
								pValue = 1;
								
							}
							break;
							
						}
						
						case GTest:
						{
							//----ADWIN
							if (this.winConfig == WindowConfig.ADWIN)
							{

								winSize = ADWin_RUN_GTest(log, strHandler, winSize, logIdex);
								
							}
							
							long runsFreqMatrix[][] = buildRunsFrequencyMatrix(log, strHandler, winSize, logIdex);
							System.out.println(logIdex + "," + winSize + "," + runsFreqMatrix[1].length);
							numOfDistinctRuns.add(runsFreqMatrix[1].length);
							
							if(runsFreqMatrix[0].length > ReduceWhenDimensionsAbove)
								runsFreqMatrix = Utils.reduceDimensionality_cuttingNonValueAddingColumns(runsFreqMatrix);
							System.out.println(logIdex + "," + winSize + "," + runsFreqMatrix[1].length);
							
							if(runsFreqMatrix[1].length >= 1)
							{
								
//								int runsFreqMatrix2[][] = new int[runsFreqMatrix.length][runsFreqMatrix[0].length];
//								for (int i = 0; i < runsFreqMatrix.length; i++) {
//									for (int j = 0; j < runsFreqMatrix[0].length; j++) {
//										runsFreqMatrix2[i][j] = (int)runsFreqMatrix[i][j];
//									}
//								}
								GTest gt = new GTest();
								pValue = gt.gTestDataSetsComparison(runsFreqMatrix[1], runsFreqMatrix[0]);
								
							}else
							{
								
								pValue = 1;
								
							}
							break;
							
						}
						
						default:
							break;
							
					}
					ratioDis += winSize + "," + pValue + "\n";
					
					
	//				plotting.addEleVal(runCurveIndex, logIdex+windowInitialSize, pValue);//now we use addRaw instead
					runpValVector.add(pValue);
					winSizeVector.add(winSize);
					driftSPOutput += pValue + ", ";
	//				if (pValue<=0.05) System.out.println("run-based drift at:"+(logIdex+winSize));
	//				XTrace trace = log.get(logIdex);
	//				strHandler.addTrace(trace);
				}
				
//				List<List<Integer>> listOfLists = new ArrayList<List<Integer>>();
//				listOfLists.add(numOfDistinctRuns);
//				Utils.writeToFile("numOfDistinctRuns.csv", listOfLists);
	
				plotting.addRaw(runCurveIndex, runpValVector);
	//			IOUtils.toFile(model + "_SingleRuns.csv", driftSPOutput);
	//			PRcurves.AddPRCurve("Runs-PR-"+winSize, winSize, runpValVector);
	//			PRcurves.AddPRLogCurve("Runs-PR-"+winSize, winSize,runpValVector);
	//			PRcurves.AddPRLogInvCurve("Runs-PR-"+winSize, winSize, runpValVector);
	//			MDcurves.AddMDCurve("Runs-MD-"+winSize,winSize,runpValVector);
				MDcurves.retreiveDrift_RunStream(log, eventStream, log.size(), logPath, runpValVector, 
						winSizeVector, typicalThreshold, DriftPointsList, isCPNToolsLog);
				R_confMat = MDcurves.getConfMat(log.size());
				R_meandelay = R_confMat.getMeanDelay();
	//			System.out.println("THE FSCORE IS "+R_confMat.getFScore() + " -- Precision " + R_confMat.getPrecision()  + " -- Recall " + R_confMat.getRecall());
	//			System.out.println("MeanDelay " + R_meandelay);
	//			IOUtils.toFile("ratioDis.csv", ratioDis);
				
//				String logPathString = logPath.toString();
//				
//				String pValuesPath = logPathString.substring(0, logPathString.lastIndexOf('.')) + "_results_" + driftConfig + "_pValueVector.csv";
//				List<List<Double>> listOfLists_pv = new ArrayList<List<Double>>();
//				listOfLists_pv.add(runpValVector);
//				Utils.writeToFile_Double(pValuesPath, listOfLists_pv);
				
				
				
				
				break;
				
			default:
				break;
		}
		
		
		if (TESTGRAD) 
		{
			
			findGradualDrifts(strHandler, MDcurves.getStartOfTransitionPoints(), MDcurves.getEndOfTransitionPoints(), startOfGradDrifts, endOfGradDrifts, LastReadGradDrifts);
			
		}
		
		
		
//		IOUtils.toFile(model + "_SP_traces_runs.csv", driftSPOutput);

//		IOUtils.toFile(model + "_type_traces_runs.csv", strHandler.sampleTracesOutput);

//		for (int ind = 0; ind < eventLogList.size(); ind++) {
//			XLog LogChunk = factory.createLog();
//			LogChunk = (XLog)eventLogList.get(ind).clone();  
//			
//			
//			PartiallyOrderedRun.DRCount.add(ind, new HashMap<Integer,Integer>());
//
//			for (int i = 0; i < LogChunk.size(); i++) {
//				XTrace trace = LogChunk.get(i);
//				strHandler.addTrace(trace,ind);
//				
//				
//			}
//			
//			//IOUtils.toFile(ind + model + "_prefix.dot", runs.toDot());
//			//IOUtils.toFile(ind + model + "_DistinctRuns.dot", runs.DRtoDot());
//		
//		}
		
//		IOUtils.toFile(logFile + "_DistinctRuns.dot", strHandler.DRtoDot());		
		//IOUtils.toFile(model + "_Runs_freq.csv", strHandler.DRtoMatrixFile());
		
		
		
//		runs.mergePrefix();
//		IOUtils.toFile(model + "_merged.dot", runs.toDot());
//
//		
//		PrimeEventStructure<Integer> pes = runs.getPrimeEventStructure();
//		pes.pack();
//		dumpPES(model, pes);
		
		OverallTime = System.nanoTime() - time;
//		System.out.println("Overall time: " + (System.nanoTime() - time) / 1000000000.0);
	    plotting.addThreshold(typicalThreshold);
		
	    
	    JFreeChart lineChart = plotting.plotSuddenAndGradual(MDcurves.getDriftPoints(), MDcurves.getStartOfTransitionPoints(), MDcurves.getEndOfTransitionPoints(), startOfGradDrifts, endOfGradDrifts, typicalThreshold, log.size());

	    
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
	
	
	public void findGradualDrifts(PartiallyOrderedRun strHandler, List<BigInteger> startOfTransitionPoints,
			List<BigInteger> endOfTransitionPoints, List<Integer> startOfGradDrifts,
			List<Integer> endOfGradDrifts, List<Integer> LastReadGradDrifts)
	{
		
		//gradual drift detection
		
		for (int indexSuddenDrift = 0; indexSuddenDrift < startOfTransitionPoints.size()-2; indexSuddenDrift++) {
			
			int[] beforeGrad = strHandler.getRunsSampleValuesInt(
					endOfTransitionPoints.get(indexSuddenDrift).intValue(), 
					startOfTransitionPoints.get(indexSuddenDrift).intValue()
					- endOfTransitionPoints.get(indexSuddenDrift).intValue());
			
			int[] insideGrad = strHandler.getRunsSampleValuesInt(
					endOfTransitionPoints.get(indexSuddenDrift+1).intValue(), 
					startOfTransitionPoints.get(indexSuddenDrift+1).intValue()
					- endOfTransitionPoints.get(indexSuddenDrift+1).intValue());
			
			int[] afterGrad = strHandler.getRunsSampleValuesInt(
					endOfTransitionPoints.get(indexSuddenDrift+2).intValue(), 
					startOfTransitionPoints.get(indexSuddenDrift+2).intValue()
					- endOfTransitionPoints.get(indexSuddenDrift+2).intValue());
			
			double gradPValue = getGradualPValue(strHandler.getDistinctRuns().size(), endOfTransitionPoints.get(indexSuddenDrift+1).intValue(), beforeGrad, insideGrad, afterGrad);
			
			if(!Main.isStandAlone)System.out.println("Gradual drift = "
				+ ((gradPValue > typicalThreshold) ? "YES" : "NO")
				+ " p-value " + gradPValue 
				+ " between " + endOfTransitionPoints.get(indexSuddenDrift+1)
				+ " and " + startOfTransitionPoints.get(indexSuddenDrift+1)
				+ " after " + (startOfTransitionPoints.get(indexSuddenDrift+1).intValue() + initialwinSize * windowFilterFactor));
			
			if (gradPValue > typicalThreshold) {
				startOfGradDrifts.add(endOfTransitionPoints.get(indexSuddenDrift+1).intValue());
				endOfGradDrifts.add(startOfTransitionPoints.get(indexSuddenDrift+1).intValue());
				LastReadGradDrifts.add(startOfTransitionPoints.get(indexSuddenDrift+1).intValue() + initialwinSize);
			}					
			
//			Grad_confMat = MDcurves.getGradConfMat(log.size(), startOfGradDrifts, endOfGradDrifts, LastReadGradDrifts);
	
		}
		
		//end gradual drift
		
	}
	
	
	private double getGradualPValue(
			int numberDistinctRuns, int logIdex, int[] beforeGrad,
			int[] insideGrad, int[] afterGrad) {
		
		int[][] gradRunsFreqMatrixPreTemp = new int[3][numberDistinctRuns];
		for (int i = 0; i < beforeGrad.length; i++) {
			gradRunsFreqMatrixPreTemp[0][beforeGrad[i]] += 1;
		}
		for (int i = 0; i < insideGrad.length; i++) {
			gradRunsFreqMatrixPreTemp[1][insideGrad[i]] += 1;
		}
		for (int i = 0; i < afterGrad.length; i++) {
			gradRunsFreqMatrixPreTemp[2][afterGrad[i]] += 1;
		}
		
		
		for (int i = 0; i < gradRunsFreqMatrixPreTemp[0].length; i++) {
			gradRunsFreqMatrixPreTemp[0][i] = Math.round(100*(float) gradRunsFreqMatrixPreTemp[0][i]/beforeGrad.length) ;
			gradRunsFreqMatrixPreTemp[1][i] = Math.round(100*(float) gradRunsFreqMatrixPreTemp[1][i]/insideGrad.length) ;
			gradRunsFreqMatrixPreTemp[2][i] = Math.round(100*(float) gradRunsFreqMatrixPreTemp[2][i]/afterGrad.length) ; 
		}
		
		
		int[][] gradRunsFreqMatrixTemp = new int[2][numberDistinctRuns];
//		for (int i = 0; i < beforeGrad.length; i++) {
//			gradRunsFreqMatrixTemp[0][beforeGrad[i]] += 1;
//		}
//		for (int i = 0; i < insideGrad.length; i++) {
//			gradRunsFreqMatrixTemp[1][insideGrad[i]] += 1;
//		}
//		for (int i = 0; i < afterGrad.length; i++) {
//			gradRunsFreqMatrixTemp[0][afterGrad[i]] += 1;
//		}
		for (int i = 0; i < gradRunsFreqMatrixPreTemp[0].length; i++) {
			gradRunsFreqMatrixTemp[0][i] = gradRunsFreqMatrixPreTemp[0][i]+gradRunsFreqMatrixPreTemp[2][i];
			gradRunsFreqMatrixTemp[1][i] = gradRunsFreqMatrixPreTemp[1][i];
		}
		
		
		int gradCountNonDoubleZero = 0;
		for (int i = 0; i < gradRunsFreqMatrixTemp[0].length; i++) {
			if (gradRunsFreqMatrixTemp[0][i] != 0
					|| gradRunsFreqMatrixTemp[1][i] != 0)
				gradCountNonDoubleZero++;
		}
		int gradRunsFreqMatrix[][] = new int[2][gradCountNonDoubleZero];
		int index = 0;
		for (int i = 0; i < gradRunsFreqMatrixTemp[0].length; i++) {
			if (gradRunsFreqMatrixTemp[0][i] != 0
					|| gradRunsFreqMatrixTemp[1][i] != 0) {
				gradRunsFreqMatrix[0][index] = gradRunsFreqMatrixTemp[0][i];
				gradRunsFreqMatrix[1][index] = gradRunsFreqMatrixTemp[1][i];
				index++;
			}
		}
		String outputGradMatrix = "";
		for (int j = 0; j < gradRunsFreqMatrix.length; j++) {
			for (int i = 0; i < gradRunsFreqMatrix[0].length; i++) {
				outputGradMatrix = outputGradMatrix
						+ gradRunsFreqMatrix[j][i] + ",";
			}
			outputGradMatrix += "\n";
		}
//		IOUtils.toFile("gradMatrix.csv", outputGradMatrix);
		ContingencyTable contingencyTable = new ContingencyTable(
				gradRunsFreqMatrix);
		if (contingencyTable.getColumnCount() < 2)
			System.out.println("COLUMN COUNT UNDER 2 at  " + logIdex);
		jsc.contingencytables.ChiSquaredTest QSTest = new jsc.contingencytables.ChiSquaredTest(
				contingencyTable);
		return QSTest.getSP();
	}
	
	public double[] ADWin_RUN_ChiSQ_BPM2015(XLog log, PartiallyOrderedRun strHandler, int oldWinSize, double oldWinSizeReal, int oldColumnCount, int logIdex)
	{
		
		long runsFreqMatrix[][] = buildRunsFrequencyMatrix(log, strHandler, oldWinSize, logIdex);
		int newColumnCount = runsFreqMatrix[0].length;
		
		double variationRatio = (double)newColumnCount / (double) oldColumnCount;
		double NewWinSizeReal = Math.max(Math.min(oldWinSizeReal * variationRatio , 1000000), 20);
		int newWinSize = Math.min((int) Math.round(NewWinSizeReal), (logIdex + 1) / 2);
		
		double result[] = new double[3];
		result[0] = newWinSize;
		result[1] = NewWinSizeReal;
		result[2] = newColumnCount;
		
		return result;
		
		
	}
	
	public int ADWin_RUN_ChiSQ(XLog log, PartiallyOrderedRun strHandler, int winSize, int logIdex)
	{
		
		boolean isWinsizeTooLarge = true;
		long runsFreqMatrix[][];
		int oldwinSize = winSize;
		do
		{
			
			runsFreqMatrix = buildRunsFrequencyMatrix(log, strHandler, winSize, logIdex);
//			if(runsFreqMatrix[0].length > ReduceWhenDimensionsAbove)
//				runsFreqMatrix = Utils.reduceDimensionality_cuttingNonValueAddingColumns(runsFreqMatrix);
			int sumOfneededFreq = checkQSquareFrequencyRequirment(runsFreqMatrix[1]);
			
			if(sumOfneededFreq == 0)
			{
				
				if(isWinsizeTooLarge)
					winSize--;
				else
					break;
				
			}else{
				
				isWinsizeTooLarge = false;
				oldwinSize = winSize;
				if (logIdex < 2 * (winSize + sumOfneededFreq))
				{
					
					winSize = (logIdex + 1) / 2;
					break;
					
				}else
				{
					
					winSize += sumOfneededFreq;
					
				}
				
			}
			
		}while(winSize < runsFreqMatrix[1].length * 20);
		
		if(winSize > runsFreqMatrix[1].length * 20)
			winSize = oldwinSize;
		
		return winSize;
		
	}
	
	public int ADWin_RUN_GTest(XLog log, PartiallyOrderedRun strHandler, int winSize, int logIdex)
	{
		
		boolean isWinsizeTooLarge = true;
		long runsFreqMatrix[][];
		int oldwinSize = winSize;
		do
		{
			
			runsFreqMatrix = buildRunsFrequencyMatrix(log, strHandler, winSize, logIdex);
			if(runsFreqMatrix[0].length > ReduceWhenDimensionsAbove)
				runsFreqMatrix = Utils.reduceDimensionality_cuttingNonValueAddingColumns(runsFreqMatrix);
			int sumOfneededFreq = checkQSquareFrequencyRequirment(runsFreqMatrix[1]);
			
			if(sumOfneededFreq == 0)
			{
				
				if(isWinsizeTooLarge)
					winSize--;
				else
					break;
				
			}else{
				
				isWinsizeTooLarge = false;
				oldwinSize = winSize;
				if (logIdex < 2 * (winSize + sumOfneededFreq))
				{
					
					winSize = (logIdex + 1) / 2;
					break;
					
				}else
				{

					winSize += sumOfneededFreq;
					
				}
				
			}
			
		}while(winSize < runsFreqMatrix[1].length * 20);
		
		if(winSize > runsFreqMatrix[1].length * 20)
			winSize = oldwinSize;
		
		return winSize;
		
	}
	
	public long [][] buildRunsFrequencyMatrix(XLog log, PartiallyOrderedRun strHandler, int winSize, int logIdex)
	{
		
		int countNonDoubleZero = 0;
		
		int [] splInt1 = strHandler.getPastRunsSampleValuesInt(logIdex,winSize);
		int [] splInt2 = strHandler.getPastRunsSampleValuesInt(logIdex-winSize,winSize);

		int newrunsFreqMatrixTemp[][] = new int[2][strHandler.getDistinctRuns().size()];
		for (int i = 0; i < splInt1.length; i++) {
			newrunsFreqMatrixTemp[0][splInt1[i]]+=1;
			newrunsFreqMatrixTemp[1][splInt2[i]]+=1;
		}

		for (int i = 0; i < newrunsFreqMatrixTemp[0].length; i++) {
			if (newrunsFreqMatrixTemp[0][i]!=0 || newrunsFreqMatrixTemp[1][i]!=0 ) 
				countNonDoubleZero++;

		}
		
		long runsFreqMatrix[][] = new long[2][countNonDoubleZero];
		int index=0;
		for (int i = 0; i < newrunsFreqMatrixTemp[0].length; i++) {
			if (newrunsFreqMatrixTemp[0][i]!=0 || newrunsFreqMatrixTemp[1][i]!=0 ){
				runsFreqMatrix[0][index]=newrunsFreqMatrixTemp[0][i];
				runsFreqMatrix[1][index]=newrunsFreqMatrixTemp[1][i];
				index++;
			}
		}
		
//		List<List<Integer>> listOfLists = new ArrayList<List<Integer>>();
//		listOfLists.add(countNonDetZeroList);
//		Utils.writeToFile("countNonDoubleZero.csv", listOfLists);
		
		return runsFreqMatrix;
		
	}
	
	public int checkQSquareFrequencyRequirment(long runsFreqWindow[])
	{
		// All expected counts are > 1 and no more than 20% of expected counts are less than 5 (According to wikipedia)
		int sumOfneededFreq = 0;
		Arrays.sort(runsFreqWindow);
		int excludedColumnsCount = (int)Math.floor(((float)runsFreqWindow.length) * exclusionRate);
		
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
	
	public void printTopRun(int[][] runsFreqMatrix) {
		// TODO Auto-generated method stub
		
	}
	
	public void winSizeTest() {
		int logsize = log.size();
		System.out.println("log size "+logsize);
		int step_winsize = logsize / 200;
		System.out.println("step size" + step_winsize);
		for (int i = 1; i < 6; i++) {
			initialwinSize = step_winsize * i;
			System.out.println("excution for window size =" + initialwinSize);
			this.findDrifts();
		}
	}

	public void dumpPES(String model, PrimeEventStructure<Integer> pes)
			throws FileNotFoundException {
		System.out.println("Done with pes");
	    PrintStream out = null;
	    out = new PrintStream("target/"+model+".pes.tex");
	    pes.toLatex(out);
	    out.close();
	    System.out.println("Done with toLatex");
	}
	
	public static float getRunsVariablity(XLog log) {
			
			
			
			AlphaRelations alphaRelations = new AlphaRelations(log);
			PartiallyOrderedRun strHandler = new PartiallyOrderedRun(alphaRelations, "", log);
			
			strHandler.runsToDisctrib();
			
			int numOfDistinctRuns = strHandler.getDistinctRuns().size();
			int numOfRuns = log.size();
			
			return (float)numOfDistinctRuns/(float)numOfRuns;
			
	}
	
	public static float getTraceVariablity(XLog log) {
		
		AlphaRelations alphaRelations = new AlphaRelations(log);
		PartiallyOrderedRun strHandler = new PartiallyOrderedRun(alphaRelations, "", log);
		
		strHandler.tracesToDisctrib();
		
		int numOfDistinctTraces = strHandler.getExistingTraces().size();
		int numOfTraces = log.size();
		
		return (float)numOfDistinctTraces/(float)numOfTraces;
		
}
	

	
//	public static void changePatternSimpleTestChangeWise() throws FileNotFoundException {
//		String[] logtype = new String[]{"re","cf","lp","pl","cb","cm","cp","cd","pm","rp","sw","fr","IOR","IRO","OIR","ORI","RIO","ROI"};
////		String[] logtype = new String[]{"IOR","IRO","OIR","ORI","RIO","ROI"};
////		String[] logtype = new String[]{"OIR"};
//		String[] loglengthType = new String[]{"2.5k","5k","7.5k","10k"};
//		int[] logsize = new int []{2500,5000,7500,10000};
//		int[] winsize = new int []{25,50,75,100,125,150};
//		WindowConfig[] config = new WindowConfig[]{WindowConfig.FWIN, WindowConfig.ADWIN};
//		String res = "";
////		double percentage = 0.01;
//		for (int t = 0; t < logtype.length; t++) {
//			res += "\n\n"+ logtype[t]+ "\nsize, R_fscore, R_prec, R_recall, T_delay, R_delay, Avg-runTime(ï¿½s), , WinConfig, winsize \n";
//			for (int l = 0; l < loglengthType.length; l++) {
//				res+="\n";
//				for (int c = 0; c < config.length; c++) {				
//					int w = 0;
//					//if (config[c] == WindowConfig.ADWIN) w = 3;
//					for (; w < winsize.length; w++) {
//						String logname = logtype[t]+loglengthType[l];
//		//				int winsize = (int) (logsize[i] * percentage);
//						System.out.println("--- A test for "+logname+" "+winsize[w]);
//						ControlFlowDriftDetector driftTest0 = new ControlFlowDriftDetector(XLogManager.readLog(new FileInputStream(logname), logname),winsize[w], DriftConfig.RUN, StatisticTestConfig.QS, config[c], logPath);
////						int distinctruns =  driftTest0.test(); //can be an output to the excel
//		//				res += logname + "," + driftTest0.T_confMat.getFScore() + "," + driftTest0.T_confMat.getPrecision() + "," + driftTest0.T_confMat.getRecall() 
//		//						+ "," + driftTest0.R_confMat.getFScore() + "," + driftTest0.R_confMat.getPrecision() + "," + driftTest0.R_confMat.getRecall() 
//		//						+ "," + driftTest0.T_meandelay + "," + driftTest0.R_meandelay 
//		//						+ "," + driftTest0.OverallTime/1000000/logsize[i] + "\n";
//						res += loglengthType[l] + "," + driftTest0.R_confMat.getFScore() + "," + driftTest0.R_confMat.getPrecision() + "," + driftTest0.R_confMat.getRecall() 
//								+ "," + driftTest0.T_meandelay + "," + driftTest0.R_meandelay 
//								+ "," + (double)driftTest0.OverallTime/1000/logsize[l] + ", ," + config[c] + "," + winsize[w] + "\n";
//					}
//				}
//			}
//		}
//		
//		IOUtils.toFile("test.csv", res);
//	}
//	
//	public void changePatternSimpleTestLengthWise() throws FileNotFoundException {
//		String[] logtype = new String[]{"br","bp","bc","bl"};
//		String[] loglengthType = new String[]{"5k","10k","15k","20k"};
//		int[] logsize = new int []{5000,10000,15000,20000};
//		String res = "w50 \nlogname, T_fscore, T_prec, T_recall, R_fscore, R_prec, R_recall, T_delay, R_delay, Avg-runTime(ms) \n";
//		for (int j = 0; j < logtype.length; j++) {
//			for (int i = 0; i < loglengthType.length; i++) {
//				String logname = logtype[j]+loglengthType[i];
//				int winsize = 50;
//				ControlFlowDriftDetector driftTest0 = new ControlFlowDriftDetector(XLogManager.readLog(new FileInputStream(logname), logname),winsize, DriftConfig.TRACE, StatisticTestConfig.QS, WindowConfig.ADWIN, logPath);
//				driftTest0.findDrifts();
//				res += logname + "," + driftTest0.T_confMat.getFScore() + "," + driftTest0.T_confMat.getPrecision() + "," + driftTest0.T_confMat.getRecall() 
//						+ "," + driftTest0.R_confMat.getFScore() + "," + driftTest0.R_confMat.getPrecision() + "," + driftTest0.R_confMat.getRecall() 
//						+ "," + driftTest0.T_meandelay + "," + driftTest0.R_meandelay 
//						+ "," + driftTest0.OverallTime/1000000/logsize[i] + "\n";
//			}
//		}
//		IOUtils.toFile("test.csv", res);
//	}
//	
//	public void changePatternTest() throws FileNotFoundException {
//		String[] logtype = new String[]{"br","bp","bc","bl"};
//		String[] loglengthType = new String[]{"5k","10k","15k","20k"};
//		int[] logsize = new int []{5000,10000,15000,20000};
//		String res = "0.1% \nlogname, T_fscore, T_prec, T_recall, R_fscore, R_prec, R_recall, T_delay, R_delay, Avg-runTime(ms) \n";
//		for (int i = 0; i < loglengthType.length; i++) {
//			for (int j = 0; j < logtype.length; j++) {
//				String logname = logtype[j]+loglengthType[i];
//				int winsize = (int) (logsize[i] * 0.001);
//				ControlFlowDriftDetector driftTest0 = new ControlFlowDriftDetector(XLogManager.readLog(new FileInputStream(logname), logname), winsize, DriftConfig.TRACE, StatisticTestConfig.QS, WindowConfig.ADWIN, logPath);
//				driftTest0.findDrifts();
//				res += logname + "," + driftTest0.T_confMat.getFScore() + "," + driftTest0.T_confMat.getPrecision() + "," + driftTest0.T_confMat.getRecall() 
//						+ "," + driftTest0.R_confMat.getFScore() + "," + driftTest0.R_confMat.getPrecision() + "," + driftTest0.R_confMat.getRecall() 
//						+ "," + driftTest0.T_meandelay + "," + driftTest0.R_meandelay 
//						+ "," + driftTest0.OverallTime/1000000/logsize[i] + "\n";
//			}
//		}
//		
//		res += "0.5%\nlogname, T_fscore, T_prec, T_recall, R_fscore, R_prec, R_recall, T_delay, R_delay, Avg-runTime(ms) \n";
//		for (int i = 0; i < loglengthType.length; i++) {
//			for (int j = 0; j < logtype.length; j++) {
//				String logname = logtype[j]+loglengthType[i];
//				int winsize = (int) (logsize[i] * 0.005);
//				ControlFlowDriftDetector driftTest0 = new ControlFlowDriftDetector(XLogManager.readLog(new FileInputStream(logname), logname), winsize, DriftConfig.TRACE, StatisticTestConfig.QS, WindowConfig.ADWIN, logPath);
//				driftTest0.findDrifts();
//				res += logname + "," + driftTest0.T_confMat.getFScore() + "," + driftTest0.T_confMat.getPrecision() + "," + driftTest0.T_confMat.getRecall() 
//						+ "," + driftTest0.R_confMat.getFScore() + "," + driftTest0.R_confMat.getPrecision() + "," + driftTest0.R_confMat.getRecall() 
//						+ "," + driftTest0.T_meandelay + "," + driftTest0.R_meandelay 
//						+ "," + driftTest0.OverallTime/1000000/logsize[i] + "\n";
//			}
//		}
//		
//		res += "1%\nlogname, T_fscore, T_prec, T_recall, R_fscore, R_prec, R_recall, T_delay, R_delay, Avg-runTime(ms) \n";
//		for (int i = 0; i < loglengthType.length; i++) {
//			for (int j = 0; j < logtype.length; j++) {
//				String logname = logtype[j]+loglengthType[i];
//				int winsize = (int) (logsize[i] * 0.01);
//				ControlFlowDriftDetector driftTest0 = new ControlFlowDriftDetector(XLogManager.readLog(new FileInputStream(logname), logname), winsize, DriftConfig.TRACE, StatisticTestConfig.QS, WindowConfig.ADWIN, logPath);
//				driftTest0.findDrifts();
//				res += logname + "," + driftTest0.T_confMat.getFScore() + "," + driftTest0.T_confMat.getPrecision() + "," + driftTest0.T_confMat.getRecall() 
//						+ "," + driftTest0.R_confMat.getFScore() + "," + driftTest0.R_confMat.getPrecision() + "," + driftTest0.R_confMat.getRecall() 
//						+ "," + driftTest0.T_meandelay + "," + driftTest0.R_meandelay 
//						+ "," + driftTest0.OverallTime/1000000/logsize[i] + "\n";
//			}
//		}
//		
//		res += "w=10\nlogname, T_fscore, T_prec, T_recall, R_fscore, R_prec, R_recall, T_delay, R_delay, Avg-runTime(ms) \n";
//		for (int i = 0; i < loglengthType.length; i++) {
//			for (int j = 0; j < logtype.length; j++) {
//				String logname = logtype[j]+loglengthType[i];
//				int winsize = 10;
//				ControlFlowDriftDetector driftTest0 = new ControlFlowDriftDetector(XLogManager.readLog(new FileInputStream(logname), logname), winsize, DriftConfig.TRACE, StatisticTestConfig.QS, WindowConfig.ADWIN, logPath);
//				driftTest0.findDrifts();
//				res += logname + "," + driftTest0.T_confMat.getFScore() + "," + driftTest0.T_confMat.getPrecision() + "," + driftTest0.T_confMat.getRecall() 
//						+ "," + driftTest0.R_confMat.getFScore() + "," + driftTest0.R_confMat.getPrecision() + "," + driftTest0.R_confMat.getRecall() 
//						+ "," + driftTest0.T_meandelay + "," + driftTest0.R_meandelay 
//						+ "," + driftTest0.OverallTime/1000000/logsize[i] + "\n";
//			}
//		}
//		res += "w=50\nlogname, T_fscore, T_prec, T_recall, R_fscore, R_prec, R_recall, T_delay, R_delay, Avg-runTime(ms) \n";
//		for (int i = 0; i < loglengthType.length; i++) {
//			for (int j = 0; j < logtype.length; j++) {
//				String logname = logtype[j]+loglengthType[i];
//				int winsize = 50;
//				ControlFlowDriftDetector driftTest0 = new ControlFlowDriftDetector(XLogManager.readLog(new FileInputStream(logname), logname), winsize, DriftConfig.TRACE, StatisticTestConfig.QS, WindowConfig.ADWIN, logPath);
//				driftTest0.findDrifts();
//				res += logname + "," + driftTest0.T_confMat.getFScore() + "," + driftTest0.T_confMat.getPrecision() + "," + driftTest0.T_confMat.getRecall() 
//						+ "," + driftTest0.R_confMat.getFScore() + "," + driftTest0.R_confMat.getPrecision() + "," + driftTest0.R_confMat.getRecall() 
//						+ "," + driftTest0.T_meandelay + "," + driftTest0.R_meandelay 
//						+ "," + driftTest0.OverallTime/1000000/logsize[i] + "\n";
//			}
//		}
//		res += "w=100\nlogname, T_fscore, T_prec, T_recall, R_fscore, R_prec, R_recall, T_delay, R_delay, Avg-runTime(ms) \n";
//		for (int i = 0; i < loglengthType.length; i++) {
//			for (int j = 0; j < logtype.length; j++) {
//				String logname = logtype[j]+loglengthType[i];
//				int winsize = 100;
//				ControlFlowDriftDetector driftTest0 = new ControlFlowDriftDetector(XLogManager.readLog(new FileInputStream(logname), logname), winsize, DriftConfig.TRACE, StatisticTestConfig.QS, WindowConfig.ADWIN, logPath);
//				driftTest0.findDrifts();
//				res += logname + "," + driftTest0.T_confMat.getFScore() + "," + driftTest0.T_confMat.getPrecision() + "," + driftTest0.T_confMat.getRecall() 
//						+ "," + driftTest0.R_confMat.getFScore() + "," + driftTest0.R_confMat.getPrecision() + "," + driftTest0.R_confMat.getRecall() 
//						+ "," + driftTest0.T_meandelay + "," + driftTest0.R_meandelay 
//						+ "," + driftTest0.OverallTime/1000000/logsize[i] + "\n";
//			}
//		}
//		IOUtils.toFile("test.csv", res);
//	}
	
	
	
	
}