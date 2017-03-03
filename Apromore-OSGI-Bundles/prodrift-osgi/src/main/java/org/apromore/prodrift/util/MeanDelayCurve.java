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
package org.apromore.prodrift.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apromore.prodrift.driftdetector.ControlFlowDriftDetector_EventStream;
import org.apromore.prodrift.driftdetector.ControlFlowDriftDetector_RunStream;
import org.apromore.prodrift.main.Main;
import org.apromore.prodrift.model.DriftPoint;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;


public class MeanDelayCurve {

	private LinePlot plotMD;
	
	private List<BigInteger> driftPoints = new ArrayList<>();
	private List<BigInteger> startOfTransitionPoints = new ArrayList<>();
	private List<BigInteger> endOfTransitionPoints = new ArrayList<>();
	private List<BigInteger> lastReadTrace = new ArrayList<>();
	private List<Double> pValuesAtDrifts = new ArrayList<>();
	private List<Date> driftDates = new ArrayList<>();
	
	private double typicalThreshold;
	private int numberOfDriftGoldStandard;
	
	public MeanDelayCurve(double typicalThreshold, int numberOfDriftGoldStandard) {
		
		this.typicalThreshold = typicalThreshold;
		this.numberOfDriftGoldStandard = numberOfDriftGoldStandard;
		plotMD = new LinePlot(this.getClass().getName(), "Mean Delay", "Threshold", "Delay");
		
	}

	public void plot() {
		plotMD.plot();
	}
	

	public void AddMDCurve(String title, int winsize , ArrayList<Double> pValVector) {
		int curveIndex = plotMD.AddCurve(title);
		
		double[] ThresholdList = new double[]{0.01,0.02,0.03,0.04,0.05,0.06,0.07,0.08,0.09,0.1,0.11,0.12,0.13,0.14,0.15,0.16,0.17,0.18,0.19};
		boolean[] FiredDrift = new boolean[]{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false};
		ArrayList<ArrayList<Double>> delayList = new ArrayList<>();


		
		for (int tsIt = 0; tsIt < ThresholdList.length; tsIt++) {

			plotMD.addEleVal(curveIndex, ThresholdList[tsIt], getMeanDelay(winsize, pValVector, ThresholdList[tsIt]));
		}
	}
	
	public double getMeanDelay(int winsize , ArrayList<Double> pValVector, double Threshold){
		ArrayList<Boolean> Driftlabels = new ArrayList<>();
		
		int driftStep= (pValVector.size()-1+2*winsize) /(this.numberOfDriftGoldStandard+1);

		for (int i = winsize; i < pValVector.size()+winsize; i++) {
			if ((i+winsize)%driftStep== 0) 
				Driftlabels.add(false);
			else 
				Driftlabels.add(true);
		}	
		
		boolean FiredDrift = false;
		
		int countDrift = 0;


		
		int driftPoint = 0;
		double totalDelay = 0;
		for (int drIt = 0; drIt < pValVector.size(); drIt++) {
			if (!Driftlabels.get(drIt)) {//actual drift
				driftPoint = drIt;
				FiredDrift=true;//start search for next detected drift (pvalue<threshold)
			}
			if (pValVector.get(drIt) <= Threshold && FiredDrift){
				System.out.println("Mean delay:" + (drIt-driftPoint));
				totalDelay+=(drIt-driftPoint);
				countDrift++;
				FiredDrift=false;//found
			}
		}
		return (double) totalDelay/countDrift;
	}
	
	
	public confusionMat getConfMat(int logSize) {
		int tP=0,fP=0,fN=0;
		double totalDelay = 0;
		
		ArrayList<BigInteger> gold = GoldStandard(logSize);
		int driftIndex = 0, goldIndex = 0;
		
		while ((driftIndex < driftPoints.size()) && (goldIndex < gold.size())){
			if (  driftPoints.get(driftIndex).compareTo(gold.get(goldIndex)) < 0){
				fP++;
				driftIndex++;
				continue;
			}
			
			if (  driftPoints.get(driftIndex).compareTo(gold.get(goldIndex)) >= 0){
				if (driftPoints.get(driftIndex).compareTo( (goldIndex + 1 == gold.size()  )? BigInteger.valueOf(logSize):gold.get(goldIndex+1)) < 0){
					tP++;
					totalDelay+=(driftPoints.get(driftIndex).subtract(gold.get(goldIndex))).doubleValue();
					driftIndex++;
				}
				goldIndex++;
			}
			
		}
		
		while (driftIndex < driftPoints.size()){
			fP++;
			driftIndex++;
		} 
		
		fN = this.numberOfDriftGoldStandard - tP;
		
		return new confusionMat(tP, fP, fN, (double) totalDelay/tP);
	}
	

	private ArrayList<BigInteger> GoldStandard(int logSize){
		ArrayList<BigInteger> goldStandardDrifts = new ArrayList<>();
		for (int i = 1; i < this.numberOfDriftGoldStandard+1; i++) {
			goldStandardDrifts.add(BigInteger.valueOf(i*logSize/(this.numberOfDriftGoldStandard+1)));
		}
		return goldStandardDrifts;
	}
	
//	private int getGoldStdDrift(ArrayList<Integer> gS, int position){
//		int i=0;
//		while (i<gS.size()){
//			
//		}
//		return null;
//	}
	
	public void retreiveDrift(XLog xl, int logSize, ArrayList<Double> pValVector, ArrayList<Integer> winSizeVector, 
			double threshold) {
		
		endOfTransitionPoints.add(BigInteger.valueOf(0));
		
		List<Integer> winSizesAtTransition = new ArrayList<Integer>();
		int driftIndex = 0;
		
		for (int i = 0; i < pValVector.size(); i++) {

			int underCurveLength = 0;
			int winSizeAtDrift = -1;
			
			winSizesAtTransition.clear();
			
			while (i < pValVector.size() && pValVector.get(i) <= threshold){
				underCurveLength++;
//				if(winSizeAtDrift == -1){
//					winSizeAtDrift = winSizeVector.get(i);
//					System.out.println(winSizeAtDrift);
//				}
				winSizesAtTransition.add(winSizeVector.get(i));
				i++;
			}
			 
			int winSize = 0;
			winSize = Utils.getMax(winSizesAtTransition.toArray());
			
			int underCurveDriftThreshhold = winSize / 2;
			
			if (underCurveLength > 0 && underCurveLength >= underCurveDriftThreshhold) {
				if(!Main.isStandAlone)System.out.println("(" + (++driftIndex) + ")" + "Window size = " + winSize);
				int FirstUnderCurve = i - underCurveLength;
				int driftPoint = FirstUnderCurve;
				int statisticalTestPosition = FirstUnderCurve - winSizeVector.get(driftPoint);
				driftPoints.add(BigInteger.valueOf( FirstUnderCurve ));
				startOfTransitionPoints.add(BigInteger.valueOf( FirstUnderCurve ));
				endOfTransitionPoints.add(BigInteger.valueOf(i));
				lastReadTrace.add(BigInteger.valueOf(driftPoint));
				pValuesAtDrifts.add(pValVector.get(FirstUnderCurve));
				if(!Main.isStandAlone)System.out.println("A drift has been detected at "+ FirstUnderCurve);
				XTrace traceAtDrift = xl.get(FirstUnderCurve);
				Date driftTime = XLogManager.getEventTime(traceAtDrift.get(traceAtDrift.size() - 1)).getValue();
				if(!Main.isStandAlone)System.out.println("A drift has been detected at the time "+ driftTime);
				
				
			}
		}
		
		
		startOfTransitionPoints.add(BigInteger.valueOf(logSize));
		
		List<List<Integer>> listOfLists = new ArrayList<List<Integer>>();
		listOfLists.add(winSizeVector);
		Utils.writeToFile_Integer("winSizeResource.csv", listOfLists);
	}
	
	public void retreiveDrift_RunStream(XLog xl, XLog eventStream, int logSize, Path logPath, ArrayList<Double> pValVector, ArrayList<Integer> winSizeVector, 
			double threshold, List<DriftPoint> DriftPointsList, boolean isCPNLogs) {
		
		endOfTransitionPoints.add(BigInteger.valueOf(0));
		
		List<Integer> winSizesAtTransition = new ArrayList<Integer>();
		int driftIndex = 0;
		
		for (int i = 0; i < pValVector.size(); i++) {

			int underCurveLength = 0;
			int winSizeAtDrift = -1;
			
			winSizesAtTransition.clear();
			
			while (i < pValVector.size() && pValVector.get(i) <= threshold){
				underCurveLength++;
//				if(winSizeAtDrift == -1){
//					winSizeAtDrift = winSizeVector.get(i);
//					System.out.println(winSizeAtDrift);
//				}
				winSizesAtTransition.add(winSizeVector.get(i));
				i++;
			}
			 
			int winSize = 0;
			winSize = Utils.getMax(winSizesAtTransition.toArray());
			
			int underCurveDriftThreshhold = winSize / 3;
			
			if (underCurveLength > 0 && underCurveLength >= underCurveDriftThreshhold) {
				if(!Main.isStandAlone)System.out.println("(" + (++driftIndex) + ")" /*+ "Window size = " + winSize*/);
				int FirstUnderCurve = i - underCurveLength;
				int driftPoint = FirstUnderCurve;
				int statisticalTestPosition = FirstUnderCurve - winSizeVector.get(driftPoint);
				driftPoints.add(BigInteger.valueOf( FirstUnderCurve ));
				startOfTransitionPoints.add(BigInteger.valueOf( FirstUnderCurve ));
				endOfTransitionPoints.add(BigInteger.valueOf(i));
				lastReadTrace.add(BigInteger.valueOf(driftPoint + underCurveDriftThreshhold));
				pValuesAtDrifts.add(pValVector.get(FirstUnderCurve));
				if(!Main.isStandAlone)System.out.println("A drift has been detected at run: "+ FirstUnderCurve);
				XTrace traceAtDrift = xl.get(FirstUnderCurve);
				Date driftTime = XLogManager.getEventTime(traceAtDrift.get(traceAtDrift.size() - 1)).getValue();
				driftDates.add(driftTime);
				if(!Main.isStandAlone)System.out.println("The drift has been detected at the time: "+ driftTime);
				
				
				// On event stream logs (CPN logs)
				if(!Main.isStandAlone)
				{
					if(isCPNLogs)
					{
						
						int driftPoint_event = XLogManager.getEventIndex(eventStream, traceAtDrift.get(traceAtDrift.size() - 1));
						DriftPoint dp = getCorrespondingDriftPoint(driftPoint_event, DriftPointsList);
						if(dp != null)
						{
							
							if(dp.getTruePositive() == 0)
							{
								
								dp.setTruePositive(1);
								dp.setDriftPointDetected(driftPoint_event);
								dp.setDriftTimeDetected(driftTime.getTime());
								
								XTrace lastReadTraceForDriftDetection = xl.get(driftPoint + underCurveDriftThreshhold);
								int lastReadEventIndex = XLogManager.getEventIndex(eventStream, lastReadTraceForDriftDetection.get(lastReadTraceForDriftDetection.size() - 1));
								dp.setDetectionDelay(lastReadEventIndex - dp.getDriftPointActual());
								
							}else
							{
								
								dp.setFalsePositive(dp.getFalsePositive() + 1);
								
							}
							
						}else
						{
							
							DriftPointsList.get(0).setFalsePositive(DriftPointsList.get(0).getFalsePositive() + 1);
							
						}
						
					}	
				}
			}
		}
		
		if(!Main.isStandAlone)
		{
			if(isCPNLogs)
			{
				
				BufferedWriter writer;
				try {

					String logPathString = logPath.toString();
					String resultsPath = logPathString.substring(0, logPathString.lastIndexOf('.')) + "_resutls_RunStream.csv";
					writer = new BufferedWriter(new FileWriter(new File(resultsPath)));
					
					writer.write("driftPointActual" + ",driftTimeActual" + ",IntraTraceDriftAreaStartPoint" + ",IntraTraceDriftAreaEndPoint" +
							",driftPointDetected" + ",driftTimeDetected" + ",truePositive" +
							",falsePositive" + ",detectionDelay(events)");
					writer.write("\n");
					
					double truePositiveSum = 0, falsePositiveSum = 0;
					int detectionDelaySum = 0;
					for(DriftPoint dp : DriftPointsList)
					{
						
						truePositiveSum += dp.getTruePositive();
						falsePositiveSum += dp.getFalsePositive();
						detectionDelaySum += dp.getDetectionDelay();
						
						dp.writeToFile(writer);
						writer.write("\n");
						
					}
					writer.write("\n");
					
					double precision = 0;
					if(truePositiveSum != 0 || falsePositiveSum != 0)
						precision = truePositiveSum / (truePositiveSum + falsePositiveSum);
					
					double recall = truePositiveSum / (double)ControlFlowDriftDetector_RunStream.numberOfDriftGoldStandard;
					
					double F_score = 0;
					if(precision != 0 || recall != 0)
						F_score = 2 * precision * recall / (precision + recall);
					
					int meanDelay = 0;
					if(truePositiveSum != 0)
						meanDelay = detectionDelaySum / (int)truePositiveSum;
					
					writer.write("Precision" + "," + precision + "," + "Recall" + "," + recall + "," + "F-score" + "," + F_score
							 + "," + "Mean delay" + "," + meanDelay);
					writer.write("\n");
					
			        writer.close();
				
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		
				
			}
		}
		
		
		startOfTransitionPoints.add(BigInteger.valueOf(logSize));
		
//		List<List<Integer>> listOfLists = new ArrayList<List<Integer>>();
//		listOfLists.add(winSizeVector);
//		Utils.writeToFile_Integer("winSize_runs.csv", listOfLists);
	}

	
	public void retreiveDrift_EventStream(XLog xl, int logSize, Path logPath, 
			List<Double> pValVector, ArrayList<Integer> winSizeVector, 
			double threshold, List<DriftPoint> DriftPointsList, 
			String relationType, float oscilationRatio, boolean isCPNLogs, 
			Map<BigInteger, List<String>> characterizationMap) 
	{
		
		endOfTransitionPoints.add(BigInteger.valueOf(0));
		
		List<Integer> winSizesAtTransition = new ArrayList<Integer>();
		int driftIndex = 0;
		for (int i = 0; i < pValVector.size(); i++) {

			int underCurveLength = 0;
			int winSizeAtDrift = -1;
			
			winSizesAtTransition.clear();
			
			while (i < pValVector.size() && pValVector.get(i) <= threshold){
				underCurveLength++;
//				if(winSizeAtDrift == -1){
//					winSizeAtDrift = winSizeVector.get(i);
//					System.out.println(winSizeAtDrift);
//				}
				winSizesAtTransition.add(winSizeVector.get(i));
				i++;
			}
			 
			int winSize = 0;
			winSize = Utils.getMax(winSizesAtTransition.toArray());
//			winSize = winSizesAtTransition.get(0); // win size when drops below threshold
			
			int underCurveDriftThreshhold = (int)(winSize * oscilationRatio);
			
			if (underCurveLength > 0 && underCurveLength >= underCurveDriftThreshhold) {
				if(!Main.isStandAlone) System.out.println("(" + (++driftIndex) + ")"/* + "Window size = " + winSize*/);
				int FirstUnderCurve = i - underCurveLength;
				int driftPoint = FirstUnderCurve;
				int statisticalTestPosition = FirstUnderCurve - winSizeVector.get(driftPoint);
				driftPoints.add(BigInteger.valueOf( driftPoint ));
				startOfTransitionPoints.add(BigInteger.valueOf( FirstUnderCurve ));
				endOfTransitionPoints.add(BigInteger.valueOf(i));
				lastReadTrace.add(BigInteger.valueOf(driftPoint + underCurveDriftThreshhold));
				pValuesAtDrifts.add(pValVector.get(FirstUnderCurve));
				if(!Main.isStandAlone) System.out.println("A drift has been detected at event: "+ FirstUnderCurve);
				XTrace traceAtDrift = xl.get(FirstUnderCurve);
				Date driftTime = XLogManager.getEventTime(traceAtDrift.get(traceAtDrift.size() - 1)).getValue();
				driftDates.add(driftTime);
				if(!Main.isStandAlone) System.out.println("The drift has been detected at the time: "+ driftTime);
				
				if(!Main.isStandAlone)
				{
					if(characterizationMap != null)
					{
						System.out.println("************** Drift Characterization *******************");
						List<String> charStatementsList = characterizationMap.get(BigInteger.valueOf(FirstUnderCurve));
						int cIndex = 1;
						if(charStatementsList != null)
						{
							for(String charStatement : charStatementsList)
							{
								System.out.println(cIndex++ + ". " + charStatement);
							}
						}
						
					}
					System.out.println("----------------------------------------------------------");
					
					if(isCPNLogs)
					{
						
						DriftPoint dp = getCorrespondingDriftPoint(driftPoint, DriftPointsList);
						if(dp != null)
						{
							
							if(dp.getTruePositive() == 0)
							{
								
								dp.setTruePositive(1);
								dp.setDriftPointDetected(driftPoint);
								dp.setDriftTimeDetected(driftTime.getTime());
								dp.setDetectionDelay(dp.getDriftPointDetected() + underCurveDriftThreshhold - dp.getDriftPointActual());
								
							}else
							{
								
								dp.setFalsePositive(dp.getFalsePositive() + 1);
								
							}
							
						}else
						{
							
							DriftPointsList.get(0).setFalsePositive(DriftPointsList.get(0).getFalsePositive() + 1);
							
						}
						
					}
				}
				
				
			}
		}
		
		if(!Main.isStandAlone)
		{
			String resultsPath = null;
			if(logPath != null)
			{
				
				String logPathString = logPath.toString();
				resultsPath = logPathString.substring(0, logPathString.lastIndexOf('.')) + "_results_" + relationType + "_" + oscilationRatio + ".csv";
				
			}
			
			
			if(isCPNLogs)
			{
				
				BufferedWriter writer;
				try {

					writer = new BufferedWriter(new FileWriter(new File(resultsPath)));
					
					writer.write("driftPointActual" + ",driftTimeActual" + ",IntraTraceDriftAreaStartPoint" + ",IntraTraceDriftAreaEndPoint" +
							",driftPointDetected" + ",driftTimeDetected" + ",truePositive" +
							",falsePositive" + ",detectionDelay(events)");
					writer.write("\n");
					
					double truePositiveSum = 0, falsePositiveSum = 0;
					int detectionDelaySum = 0;
					for(DriftPoint dp : DriftPointsList)
					{
						
						truePositiveSum += dp.getTruePositive();
						falsePositiveSum += dp.getFalsePositive();
						detectionDelaySum += dp.getDetectionDelay();
						
						dp.writeToFile(writer);
						writer.write("\n");
						
					}
					writer.write("\n");
					
					double precision = 0;
					if(truePositiveSum != 0 || falsePositiveSum != 0)
						precision = truePositiveSum / (truePositiveSum + falsePositiveSum);
					
					double recall = truePositiveSum / (double)ControlFlowDriftDetector_EventStream.numberOfDriftGoldStandard;
					
					double F_score = 0;
					if(precision != 0 || recall != 0)
						F_score = 2 * precision * recall / (precision + recall);
					
					int meanDelay = 0;
					if(truePositiveSum != 0)
						meanDelay = detectionDelaySum / (int)truePositiveSum;
					
					writer.write("Precision" + "," + precision + "," + "Recall" + "," + recall + "," + "F-score" + "," + F_score
							 + "," + "Mean delay" + "," + meanDelay);
					
			        writer.close();
				
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
				
			}
		}
		
		startOfTransitionPoints.add(BigInteger.valueOf(logSize));
		
//		List<List<Integer>> listOfLists = new ArrayList<List<Integer>>();
//		listOfLists.add(winSizeVector);
//		Utils.writeToFile(resultsPath.substring(0, resultsPath.lastIndexOf('.')) + "_winVector.csv", listOfLists);
	}

	
	private DriftPoint getCorrespondingDriftPoint(int detectedDriftIndex, List<DriftPoint> DriftPointsList)
	{
		
		for(int i = DriftPointsList.size() - 1; i >= 0; i--)
		{
			
			DriftPoint dp = DriftPointsList.get(i);
			if(detectedDriftIndex >= dp.getDriftPointActual())
				return dp;
			
		}
		
		return null;
		
	}
	
	public double getBasicPrecisionNOTWORKING(int winsize , ArrayList<Double> pValVector, double threshold) {
		int tP=0,fP=0,fN=0;
		
		boolean FiredDrift = false;
		boolean computingLenght = false;
		int driftLenght=0;
		
		ArrayList<Boolean> Driftlabels = new ArrayList<>();	
		int driftStep= (pValVector.size()-1+2*winsize) /(this.numberOfDriftGoldStandard+1);
		for (int i = winsize; i < pValVector.size()+winsize; i++) {
			if ((i+winsize)%driftStep== 0) 
				Driftlabels.add(false);
			else 
				Driftlabels.add(true);
		}
		
		int pVVIt=0;
		for ( pVVIt = 0; pVVIt < pValVector.size(); pVVIt++) {
			boolean found = false;
			while (pValVector.get(pVVIt) <= threshold && pVVIt<pValVector.size()) {found = true;
			pVVIt++;}
			if (found) {tP++;
			found = false;}
		}
		
		int tN=0;
		return (double)tP/this.numberOfDriftGoldStandard;
	}
	
	
	public void AddMDCurve_depricated(String title, int winsize , ArrayList<Double> pValVector) {
		int curveIndex = plotMD.AddCurve(title);
		
		double[] ThresholdList = new double[]{0.01,0.02,0.03,0.04,0.05,0.06,0.07,0.08,0.09,0.1,0.11,0.12,0.13,0.14,0.15,0.16,0.17,0.18,0.19};
		boolean[] FiredDrift = new boolean[]{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false};
		ArrayList<ArrayList<Double>> delayList = new ArrayList<>();
		for (int i = 0; i < ThresholdList.length; i++) delayList.add(new ArrayList<Double>());//initialize the list
		ArrayList<Boolean> Driftlabels = new ArrayList<>();
		
		int driftStep= (pValVector.size()-1+2*winsize) /(this.numberOfDriftGoldStandard+1);
//		Driftlabels.add(true);
		for (int i = winsize; i < pValVector.size()+winsize; i++) {
			if ((i+winsize)%driftStep== 0) 
				Driftlabels.add(false);
			else 
				Driftlabels.add(true);
		}	
//		Driftlabels.add(true);

		
		for (int tsIt = 0; tsIt < ThresholdList.length; tsIt++) {
			int driftPoint = 0;
			double totalDelay = 0;
			for (int drIt = 0; drIt < pValVector.size(); drIt++) {
				if (!Driftlabels.get(drIt)) {//actual drift
					driftPoint = drIt;
					FiredDrift[tsIt]=true;//start search for next detected drift (pvalue<threshold)
				}
				if (pValVector.get(drIt) <= ThresholdList[tsIt] && FiredDrift[tsIt]){
					delayList.get(tsIt).add((double)drIt-driftPoint);
					totalDelay+=(drIt-driftPoint);
					FiredDrift[tsIt]=false;//found
				}
			}
			plotMD.addEleVal(curveIndex, ThresholdList[tsIt], totalDelay/delayList.get(tsIt).size());
		}
	}
	
	public List<BigInteger> getEndOfTransitionPoints() {
		return endOfTransitionPoints;
	}

	public void setEndOfTransitionPoints(List<BigInteger> endOfTransitionPoints) {
		this.endOfTransitionPoints = endOfTransitionPoints;
	}

	public List<BigInteger> getStartOfTransitionPoints() {
		return startOfTransitionPoints;
	}

	public void setStartOfTransitionPoints(List<BigInteger> startOfTransitionPoints) {
		this.startOfTransitionPoints = startOfTransitionPoints;
	}

	public List<Double> getpValuesAtDrifts() {
		return pValuesAtDrifts;
	}

	public void setpValuesAtDrifts(List<Double> pValuesAtDrifts) {
		this.pValuesAtDrifts = pValuesAtDrifts;
	}

	public List<BigInteger> getDriftPoints() {
		return driftPoints;
	}

	public void setDriftPoints(List<BigInteger> driftPoints) {
		this.driftPoints = driftPoints;
	}

	public List<BigInteger> getLastReadTrace() {
		return lastReadTrace;
	}

	public void setLastReadTrace(List<BigInteger> lastReadTrace) {
		this.lastReadTrace = lastReadTrace;
	}

	public List<Date> getDriftDates() {
		return driftDates;
	}

	public void setDriftDates(List<Date> driftDates) {
		this.driftDates = driftDates;
	}
	
	
//	public confusionMat getGradConfMat(int logSize, List<Integer> startOfGradDrifts, List<Integer> endOfGradDrifts, List<Integer> lastReadGradDrifts) {
//
//		int tP=0,fP=0,fN=0;
//		double totalDelay = 0;
//		
//		ArrayList<BigInteger> gold = GoldStandard(logSize);
//		int driftIndex = 0, goldIndex = 0;
//		
//		while ((driftIndex < startOfGradDrifts.size()) && (goldIndex < gold.size())){
//
//			
//			if ( startOfGradDrifts.get(driftIndex) <= gold.get(goldIndex).intValue() && gold.get(goldIndex).intValue() <= endOfGradDrifts.get(driftIndex) ) {	
//					tP++;
//					totalDelay+=(BigInteger.valueOf(lastReadGradDrifts.get(driftIndex)).subtract(gold.get(goldIndex))).doubleValue()-250; // last read - (gold standard + 250) = last read - gold standard - 250  
//					driftIndex++;	
//					goldIndex++;
//			}
//			else 
//				if ( startOfGradDrifts.get(driftIndex) > gold.get(goldIndex).intValue() ) {
//					if ( ((goldIndex + 1 == gold.size()  )? logSize:gold.get(goldIndex+1).intValue()) > endOfGradDrifts.get(driftIndex) ) {
//						fP++;
//						goldIndex++;
//						driftIndex++;
//						}
//					else  goldIndex++;
//				}
//				else{
//					fP++;
//					driftIndex++;
//				}	
//		}
//		
//		while (driftIndex < startOfGradDrifts.size()){
//			fP++;
//			driftIndex++;
//		} 
//		
//		fN = AlphaBasedPosetReaderTest.numberOfDriftGoldStandar - tP;
//		
//		return new confusionMat(tP, fP, fN, (double) totalDelay/tP);
//		
//	}
	
//	public static void main(String[] args) {
//		
//		ArrayList<Double> Pvalue = new ArrayList<Double>();
//		for (int i = 0; i < 100; i++) {
//			Pvalue.add((double) new java.util.Random().nextFloat());
//		}
//		MeanDelayCurve md= new MeanDelayCurve();
//		md.AddMDCurve("test",10,Pvalue);
//		md.plot();
//			
//	}

}
