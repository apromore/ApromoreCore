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
package org.apromore.prodrift.driftcharacterization;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apromore.prodrift.config.BehaviorRelation;
import org.apromore.prodrift.config.FrequencyChange;
import org.apromore.prodrift.config.FrequencyChangeType;
import org.apromore.prodrift.driftdetector.ControlFlowDriftDetector_EventStream;
import org.apromore.prodrift.im.BlockStructure;
import org.apromore.prodrift.main.Main;
import org.apromore.prodrift.util.Utils;
import org.apromore.prodrift.util.XLogManager;
import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.framework.util.Pair;
//import org.processmining.plugins.InductiveMiner.mining.MiningParameters;
//import org.processmining.plugins.InductiveMiner.plugins.IMProcessTree;
//import org.processmining.processtree.ProcessTree;
//import org.rosuda.JRI.Rengine;

import com.aliasi.matrix.DenseVector;
import com.aliasi.matrix.Vector;
import com.aliasi.stats.AnnealingSchedule;
import com.aliasi.stats.LogisticRegression;
import com.aliasi.stats.RegressionPrior;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

public class ControlFlowDriftCharacterizer {

	
	private Map<String, ChangePattern> changePatternsMap = new LinkedHashMap<>();

	private boolean isRfileFilled = false;
	
	private Path logPath = null;
	
	private String logNameShort = "";
	
	private float charTopCoeffsRatio = 1f; // top x%
	
//	private Rengine engineR;
	
	private String logisticRegressionFile;
	private String K_samplePermutationTestFile;
	
	private String goodnessOfKSPTfile;
	private String charAccuracyKSPTfile;
	
	private boolean resetFilesRelations = true;
	private boolean resetFilesGoodness = true;
	private boolean resetFilesAccuracy = true;
	
	private int driftCounter = 0;
	
	private int minCharDataPoints = 500;
	private int topCharzedDrifts = 1;
	private int cutTopRelationsPercentage = 100;
	
	private boolean considerChangeSignificance = true;
	private boolean withFragment = true;
	private boolean withPartialMatching = false;
	
	public ControlFlowDriftCharacterizer(Path logPath, 
			String logNameShort, 
			int minCharDataPoints, 
			int topCharzedDrifts, 
			int cutTopRelationsPercentage, 
			boolean considerChangeSignificance, 
			boolean withFragment, 
			boolean withPartialMatching) {
		
		this.logPath = logPath;
		this.logNameShort = logNameShort;
		this.minCharDataPoints = minCharDataPoints;
		this.topCharzedDrifts = topCharzedDrifts;
		this.cutTopRelationsPercentage = cutTopRelationsPercentage;
		if(!Main.isStandAlone)
		{
			logisticRegressionFile = logPath.toString().substring(0, logPath.toString().lastIndexOf('.')) + "_characterizationResult_LR_" + minCharDataPoints + ".csv";
			K_samplePermutationTestFile = logPath.toString().substring(0, logPath.toString().lastIndexOf('.')) + "_RelationOrdering_KSPT_" + minCharDataPoints + ".csv";
			goodnessOfKSPTfile = logPath.toString().substring(0, logPath.toString().lastIndexOf('.')) + "_GoodnessOfKSPT_" + minCharDataPoints + ".csv";
			charAccuracyKSPTfile = logPath.toString().substring(0, logPath.toString().lastIndexOf('.')) + "_charAccuracyKSPT_" + minCharDataPoints + "_" + cutTopRelationsPercentage + ".csv";
		}
		this.considerChangeSignificance = considerChangeSignificance;
		this.withFragment = withFragment;
		this.withPartialMatching = withPartialMatching;
		
		populateChangePatternMap(changePatternsMap);
		
	}



	public List<String> characterizeDrift_LogisticRegression(Map<PairRelation, Integer> Relation_repFreq_map, 
			List<Map<PairRelation, Integer>> RelationFreqMap_Queue, List<Integer> outputQueue, 
			XLog subLogB, XLog subLogA)
	{
		List<String> charStatements = new ArrayList<>();
		int dimensionCount = 1 + 1;
		Set<PairRelation> relationKeySet = Relation_repFreq_map.keySet();
		Vector[] INPUTS = new Vector[RelationFreqMap_Queue.size()];
		int[] OUTPUTS = new int[outputQueue.size()];
		int inputIndex = 0;
		
		Iterator<Integer> outputIter = outputQueue.iterator();
		Map<PairRelation, RelationImpact> relationImpact_Map = new HashMap<PairRelation, RelationImpact>();
		
		
		for(Map<PairRelation, Integer> relationFreqMap : RelationFreqMap_Queue)
		{
			
			int output = outputIter.hasNext() ? outputIter.next().intValue() : 1;
			OUTPUTS[inputIndex] = output;
			
			DenseVector dv = new DenseVector(dimensionCount);
			dv.setValue(0, 1); // intercept
			int index = 1;
			for(PairRelation relation : relationKeySet)
			{
				
				Integer freq = relationFreqMap.get(relation);
				if(freq != null)
					dv.setValue(index, freq.intValue());
				else
					dv.setValue(index, 0);
				
				RelationImpact ri = relationImpact_Map.get(relation);
				if(ri == null)
				{
					
					ri = new RelationImpact();
					relationImpact_Map.put(relation, ri);
					
				}
				
				int fr = (int) dv.value(index);
				if(output == 0)
				{
					
					ri.setSumFreqBeforeDrift(ri.getSumFreqBeforeDrift() + fr);
					ri.setRepFreqBeforeDrift(ri.getRepFreqBeforeDrift() + 1);
					
					if(fr > ri.getMaxFreqBeforeDrift())
						ri.setMaxFreqBeforeDrift(fr);
					
					if(fr < ri.getMinFreqBeforeDrift())
						ri.setMinFreqBeforeDrift(fr);
					
				}else
				{
					
					ri.setSumFreqAfterDrift(ri.getSumFreqAfterDrift() + fr);
					ri.setRepFreqAfterDrift(ri.getRepFreqAfterDrift() + 1);
					
					if(fr > ri.getMaxFreqAfterDrift())
						ri.setMaxFreqAfterDrift(fr);
					
					if(fr < ri.getMinFreqAfterDrift())
						ri.setMinFreqAfterDrift(fr);
					
				}
				

				index++;
				
				break;
				
			}
			
			INPUTS[inputIndex++] = dv;
			
		}
		
//		if(!isRfileFilled)
//		{
//			isRfileFilled = true;
//			
//			try {
//				
//				FileWriter fw_R = new FileWriter("./LR_data.csv", false);
//
//				fw_R.write("Intercept,");
//				for(PairRelation relation : relationKeySet)
//				{
//					
//					fw_R.write(relation.toString() + ",");
//					
//				}
//				fw_R.write("output");
//				fw_R.write("\n");
//				
//				for (int i = 0; i < OUTPUTS.length; i++) 
//				{
//					
//					Vector dv = INPUTS[i];
//					for (int j = 0; j < relationKeySet.size() + 1; j++) 
//					{
//						
//						fw_R.write(dv.value(j) + ",");
//						
//					}
//					if(OUTPUTS[i] == 1)
//						fw_R.write("1");
//					else
//						fw_R.write("0");
//					fw_R.write("\n");
//					
//				}
//				
//		        fw_R.close();
//		        
////				ChiSquareTest chit = new ChiSquareTest();	
////				System.out.println(chit.chiSquareDataSetsComparison(covariate, output));
////				double testStat = 0.0;
////				for(int i = 0; i < covariate.length; i++)
////					testStat += Utils.chiSquareValue(output[i], covariate[i]);
//				
////				System.out.println("testStat = " + testStat);
//			
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//		}
		
		long ti = System.currentTimeMillis();
		LogisticRegression regression = LogisticRegression.estimate(INPUTS,
                                      OUTPUTS,
                                      RegressionPrior.noninformative(),
                                      AnnealingSchedule.inverse(.05, 100),
                                      null, // reporter with no feedback
                                      0.000000001, // min improve
                                      1, // min epochs
                                      100); // max epochs(max time each instance is visited)
		
		System.out.println("LogisticRegression took(s) : " + (System.currentTimeMillis() - ti)/1000);
		
//		try {
			
			Vector[] betas = regression.weightVectors();
			
			int sumOfCoefficients = 0;
			Iterator<PairRelation> iter = relationKeySet.iterator();
	        for (int outcome = 0; outcome < betas.length; ++outcome) 
	        {
	        	
	            for (int i = 1; i < betas[outcome].numDimensions(); ++i)
	            {
	            	
	            	PairRelation relation = iter.hasNext() ? iter.next() : null;
	            	int relationCoefficient = (int)Math.abs(betas[outcome].value(i));
	            	RelationImpact ri = relationImpact_Map.get(relation);
	            	ri.setCoefficient((float)relationCoefficient);
	            	ri.setCoefficient_ori((int)betas[outcome].value(i));
	            	
	            	sumOfCoefficients += relationCoefficient;
	            	
	            }
	            
	        }
	        
//	        Map<PairRelation, RelationImpact> relationImpactMap_sorted = sortByComparator_byCoefficient(relationImpact_Map);
//	        Set<PairRelation> keySet = relationImpactMap_sorted.keySet();
//	        Iterator<PairRelation> it = keySet.iterator();
//	        
//	        int index = 0;
//	        while(it.hasNext())
//	        {
//	        	
//	        	PairRelation relation = it.next();
//            	RelationImpact ri = relationImpactMap_sorted.get(relation);
//            	float coefficient_normalized = (float)ri.getCoefficient() / (float)sumOfCoefficients;
//            	ri.setCoefficient(coefficient_normalized);
//            	ri.setAvgFreqBeforeDrift(ri.getSumFreqBeforeDrift()/ri.getRepFreqBeforeDrift());
//            	ri.setAvgFreqAfterDrift(ri.getSumFreqAfterDrift()/ri.getRepFreqAfterDrift());
//            	ri.setRank(++index);
//            	
//	        }
//	        
//	        FileWriter fw = null;
//	        if(resetFilesRelations)
//	        {
//	        	fw = new FileWriter(logisticRegressionFile, false);
//	        	resetFilesRelations = false;
//	        }else
//	        	fw = new FileWriter(logisticRegressionFile, true);
//
//	        it = keySet.iterator();
//	        float sumOfTopCoefficients = 0f;
//	        
//	        while (it.hasNext()) {
//            	
//	        	PairRelation relation = it.next();
//            	RelationImpact ri = relationImpactMap_sorted.get(relation);
//            	sumOfTopCoefficients += ri.getCoefficient();
//            	if(sumOfTopCoefficients <= charTopCoeffsRatio || sumOfTopCoefficients == ri.getCoefficient())
//            		ri.setAmongTopRelations(true);
//            	
//            	fw.write(relation.toString() + "  " + ri.toString());
//            	fw.write("\n");
//            	
//            }
//	        
//	        discoverChangePatterns(relationImpactMap_sorted, "LR");
	        
	        
//	        XLogInfo summary = XLogInfoFactory.createLogInfo(subLogB, XLogInfoImpl.NAME_CLASSIFIER);
//			LogRelations logRelations = new BasicLogRelations(subLogB, summary);
//			FakePluginContext context = new FakePluginContext();
//			AlphaMiner alphaMiner = new AlphaMiner();
//			Object []result = alphaMiner.doMining(context, summary, logRelations);
//			Petrinet subLogB_net = (Petrinet) result[0];
//			
//			HashMap<String, Integer> subLogB_dfRelationFreq = new HashMap<>();
//			buildDirectFollowRelationFrequency(subLogB, subLogB_dfRelationFreq);
//			
//			
//			summary = XLogInfoFactory.createLogInfo(subLogA, XLogInfoImpl.NAME_CLASSIFIER);
//			logRelations = new BasicLogRelations(subLogA, summary);
//			context = new FakePluginContext();
//			alphaMiner = new AlphaMiner();
//			result = alphaMiner.doMining(context, summary, logRelations);
//			Petrinet subLogA_net = (Petrinet) result[0];
//	        
//			HashMap<String, Integer> subLogA_dfRelationFreq = new HashMap<>();
//			buildDirectFollowRelationFrequency(subLogA, subLogA_dfRelationFreq);
	        
//	        fw.write("\n");
//	        fw.write("***********************************************");
//	        fw.write("\n");
//			
//	        fw.close();
		
//		} catch (IOException /*| InterruptedException | ExecutionException*/ e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	        
	    return charStatements;
		
	}
	
	
	
	public List<String> characterizeDrift_KSpermutationTest(
			Map<PairRelation, Integer> Relation_repFreq_map, 
			List<Map<PairRelation, Integer>> RelationFreqMap_Queue, 
			List<Integer> outputQueue, 
			XLog subLogB, 
			XLog subLogA, 
			Set<String> startActivities, 
			Set<String> endActivities/*,
			MiningParameters miningParameters*/)
	{
		
		driftCounter++;
		List<String> charStatements = new ArrayList<>();
		
		int dimensionCount = Relation_repFreq_map.size() + 1;
		Set<PairRelation> relationKeySet = Relation_repFreq_map.keySet();
		Vector[] INPUTS = new Vector[RelationFreqMap_Queue.size()];
		int[] OUTPUTS = new int[outputQueue.size()];
		int inputIndex = 0;
		
		Iterator<Integer> outputIter = outputQueue.iterator();
		Map<PairRelation, RelationImpact> relationImpact_Map = new HashMap<PairRelation, RelationImpact>();
		
		
		for(Map<PairRelation, Integer> relationFreqMap : RelationFreqMap_Queue)
		{
			
			int output = outputIter.hasNext() ? outputIter.next().intValue() : 1;
			OUTPUTS[inputIndex] = output;
			
			DenseVector dv = new DenseVector(dimensionCount);
			dv.setValue(0, 1); // intercept
			int index = 1;
			for(PairRelation relation : relationKeySet)
			{
				
				Integer freq = relationFreqMap.get(relation);
				if(freq != null)
					dv.setValue(index, freq.intValue());
				else
					dv.setValue(index, 0);
				
				RelationImpact ri = relationImpact_Map.get(relation);
				if(ri == null)
				{
					
					ri = new RelationImpact();
					relationImpact_Map.put(relation, ri);
					
				}
				
				int fr = (int) dv.value(index);
				if(output == 0)
				{
					
					ri.setSumFreqBeforeDrift(ri.getSumFreqBeforeDrift() + fr);
					ri.setRepFreqBeforeDrift(ri.getRepFreqBeforeDrift() + 1);
					
					if(fr > ri.getMaxFreqBeforeDrift())
						ri.setMaxFreqBeforeDrift(fr);
					
					if(fr < ri.getMinFreqBeforeDrift())
						ri.setMinFreqBeforeDrift(fr);
					
				}else
				{
					
					ri.setSumFreqAfterDrift(ri.getSumFreqAfterDrift() + fr);
					ri.setRepFreqAfterDrift(ri.getRepFreqAfterDrift() + 1);
					
					if(fr > ri.getMaxFreqAfterDrift())
						ri.setMaxFreqAfterDrift(fr);
					
					if(fr < ri.getMinFreqAfterDrift())
						ri.setMinFreqAfterDrift(fr);
					
				}
				

				index++;
				
			}
			
			INPUTS[inputIndex++] = dv;
			
		}
		
		if(!isRfileFilled)
		{
			isRfileFilled = true;
			
			try {
				
				FileWriter fw_R = new FileWriter("./LR_data.csv", false);

				fw_R.write("Intercept,");
				for(PairRelation relation : relationKeySet)
				{
					
					fw_R.write(relation.toString() + ",");
					
				}
				fw_R.write("output");
				fw_R.write("\n");
				
				for (int i = 0; i < OUTPUTS.length; i++) 
				{
					
					Vector dv = INPUTS[i];
					for (int j = 0; j < relationKeySet.size() + 1; j++) 
					{
						
						fw_R.write(dv.value(j) + ",");
						
					}
					if(OUTPUTS[i] == 1)
						fw_R.write("1");
					else
						fw_R.write("0");
					fw_R.write("\n");
					
				}
				
		        fw_R.close();
		        
//				ChiSquareTest chit = new ChiSquareTest();	
//				System.out.println(chit.chiSquareDataSetsComparison(covariate, output));
//				double testStat = 0.0;
//				for(int i = 0; i < covariate.length; i++)
//					testStat += Utils.chiSquareValue(output[i], covariate[i]);
				
//				System.out.println("testStat = " + testStat);
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
		long t1 = System.currentTimeMillis();
		int relationFreqs[] = new int[outputQueue.size()];
		int Output[] = new int[outputQueue.size()];
		float sumOfTestStats = 0f;
		for(PairRelation relation : relationKeySet)
		{
			
			RelationImpact ri = relationImpact_Map.get(relation);
			if(ri == null)
			{
				
				ri = new RelationImpact();
				relationImpact_Map.put(relation, ri);
				
			}
			outputIter = outputQueue.iterator();
			int index = 0;
			for(Map<PairRelation, Integer> relationFreqMap : RelationFreqMap_Queue)
			{
				
				int output = outputIter.hasNext() ? outputIter.next().intValue() : 1;
				Integer freq = relationFreqMap.get(relation);
				int fr = 0;
				if(freq != null)
					fr = freq.intValue();
				
				relationFreqs[index] = fr;
				Output[index] = output;
				index++;
						
				if(output == 0)
				{
					
					ri.setSumFreqBeforeDrift(ri.getSumFreqBeforeDrift() + fr);
					ri.setRepFreqBeforeDrift(ri.getRepFreqBeforeDrift() + 1);
					
					if(fr > ri.getMaxFreqBeforeDrift())
						ri.setMaxFreqBeforeDrift(fr);
					
					if(fr < ri.getMinFreqBeforeDrift())
						ri.setMinFreqBeforeDrift(fr);
					
				}else
				{
					
					ri.setSumFreqAfterDrift(ri.getSumFreqAfterDrift() + fr);
					ri.setRepFreqAfterDrift(ri.getRepFreqAfterDrift() + 1);
					
					if(fr > ri.getMaxFreqAfterDrift())
						ri.setMaxFreqAfterDrift(fr);
					
					if(fr < ri.getMinFreqAfterDrift())
						ri.setMinFreqAfterDrift(fr);
					
				}
				
			}
			
//			StringBuffer testStat = new StringBuffer();
//			StringBuffer p_value = new StringBuffer();
//			doKSpermutationTestInR(relationFreqs, Output, testStat, p_value);
//			ri.setTestStatistic(Float.valueOf(testStat.toString()).isNaN() ? 0 : Float.valueOf(testStat.toString()));
//			ri.setTestStatistic_ori(Float.valueOf(testStat.toString()).isNaN() ? 0 : Float.valueOf(testStat.toString()));
//			ri.setP_value(Float.valueOf(p_value.toString()).isNaN() ? 1 : Float.valueOf(p_value.toString()));
			
			sumOfTestStats += ri.getTestStatistic();
			
		}
		
		Set<PairRelation> keySet = relationImpact_Map.keySet();
        Iterator<PairRelation> it = keySet.iterator();
        
        float sumOfRelChangeMags = 0;
        if(considerChangeSignificance)
        {
	        while(it.hasNext())
	        {
	        	PairRelation relation = it.next();
	        	RelationImpact ri = relationImpact_Map.get(relation);
	        	
	        	ri.setAvgFreqBeforeDrift(ri.getSumFreqBeforeDrift()/ri.getRepFreqBeforeDrift());
	        	ri.setAvgFreqAfterDrift(ri.getSumFreqAfterDrift()/ri.getRepFreqAfterDrift());
	        	
	        	float freqChangeMagnitude = 0;
	        	if(ri.getAvgFreqAfterDrift() != 0 || ri.getAvgFreqBeforeDrift() != 0)
	        		freqChangeMagnitude = (float)((ri.getAvgFreqAfterDrift() - ri.getAvgFreqBeforeDrift()) *
	        			(ri.getAvgFreqAfterDrift() - ri.getAvgFreqBeforeDrift())) /
	        				(float)Math.max(ri.getAvgFreqAfterDrift(), ri.getAvgFreqBeforeDrift());
	        	
	        	ri.setRelChangeMag(freqChangeMagnitude);
//	        	ri.setRelChangeMag(freqChangeMagnitude * ri.getTestStatistic());
	        	
	        	sumOfRelChangeMags += ri.getRelChangeMag();
	        }
        }
		
//		System.out.println("K-sample Permutation Test: " + (System.currentTimeMillis() - t1));
			
//		Map<PairRelation, RelationImpact> relationImpactMap_sorted = sortByComparator_byTestStat(relationImpact_Map);
		Map<PairRelation, RelationImpact> relationImpactMap_sorted = sortByComparator_byRelChangeMag(relationImpact_Map);
        
		keySet = relationImpactMap_sorted.keySet();
        it = keySet.iterator();
        
        float sumOfRelChangeMags_normalized = 0;
        float sumOfTestStats_normalized = 0;
        int index = 0;
        while(it.hasNext())
        {
        	
        	PairRelation relation = it.next();
        	RelationImpact ri = relationImpactMap_sorted.get(relation);
//        	float testStat_normalized = ri.getTestStatistic() / sumOfTestStats;
//        	ri.setTestStatistic(testStat_normalized);
        	float relChangeMags_normalized = ri.getRelChangeMag() / sumOfRelChangeMags;
        	ri.setRelChangeMag(relChangeMags_normalized);
        	ri.setAvgFreqBeforeDrift(ri.getSumFreqBeforeDrift()/ri.getRepFreqBeforeDrift());
        	ri.setAvgFreqAfterDrift(ri.getSumFreqAfterDrift()/ri.getRepFreqAfterDrift());
        	ri.setRank(++index);
        	
//        	sumOfTestStats_normalized += testStat_normalized;
        	sumOfRelChangeMags_normalized += relChangeMags_normalized;
        	
        }
        
        Multimap<PairRelation, RelationImpact> sortedMap_cut = LinkedListMultimap.create();
		
		try {
			
			FileWriter fw = null;
			if(!Main.isStandAlone)
			{
		        if(resetFilesRelations)
		        {
		        	fw = new FileWriter(K_samplePermutationTestFile, false);
		        	resetFilesRelations = false;
		        }else
		        	fw = new FileWriter(K_samplePermutationTestFile, true);
			}
		
	        it = keySet.iterator();
//	        int numOfRelations = relationImpactMap_sorted.size();
//	        int numOfTopRelations = 0;
	        float sumOfTopRelationTS = 0;
	        float sumOfTopRelationRCM = 0;
	        boolean topRelationsSelected = false;
	        
//	        System.out.println(sumOfRelChangeMags_normalized);
	        while (it.hasNext()) {
            	
	        	PairRelation relation = it.next();
            	RelationImpact ri = relationImpactMap_sorted.get(relation);
//            	if(ri.getP_value() < 0.05 && numOfTopRelations <= Math.ceil(numOfRelations * cutTopRelationsPercentage/100.0))
//            	{
//            	if(ri.getP_value() < 0.05 && numOfTopRelations <= cutTopRelationsPercentage)
//            	{
//            		ri.setAmongTopRelations(true);
//            		sortedMap_cut.put(relation, ri);
//            		numOfTopRelations++;
//	        	}
            	
//            	if(!topRelationsSelected && ri.getP_value() < 0.05)
//            	{
//            		if(sumOfTopRelationTS <= ((float)cutTopRelationsPercentage/100.0) * sumOfTestStats_normalized)
//            		{
//            			ri.setAmongTopRelations(true);
//            			sortedMap_cut.put(relation, ri);
//            			sumOfTopRelationTS += ri.getTestStatistic();
//            		}else
//            		{
//            			topRelationsSelected = true;
//            		}
//            	}
            	
            	if(!topRelationsSelected && ri.getP_value() < 0.05)
            	{
            		if(sumOfTopRelationRCM <= ((float)cutTopRelationsPercentage/100.0) * sumOfRelChangeMags_normalized)
            		{
            			ri.setAmongTopRelations(true);
            			sortedMap_cut.put(relation, ri);
            			sumOfTopRelationRCM += ri.getRelChangeMag();
            		}else
            		{
            			topRelationsSelected = true;
            		}
            	}
            	
            	if(!Main.isStandAlone)
    			{
            		fw.write(relation.toString() + "  " + ri.toString2());
            		fw.write("\n");
    			}
            	
            }
	        
	        it = keySet.iterator();
	        
	        
	        
	        if(!Main.isStandAlone)
			{
	        	fw.write("\n");
	        	fw.write("***********************************************");
	        	fw.write("\n");
			
	        	fw.close();
			}
	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if(Main.completeGoodnessOfKSPTExperiment)
        	testGoodnessOfKSPT(sortedMap_cut, goodnessOfKSPTfile);
        else
        	discoverChangePatterns(sortedMap_cut, charStatements, "KSPT");
		
		
		return charStatements;
		
	}
	
	private boolean containsEquivRelationEntry(Multimap<PairRelation, RelationImpact> map, 
			Entry<PairRelation, RelationImpact> b)
	{
		if(map.containsKey(b.getKey()))
		{
			Iterator<RelationImpact> it = map.get(b.getKey()).iterator();
			while(it.hasNext())
			{
				RelationImpact ri = it.next();
				if(getRelatinFreqChange(ri) == getRelatinFreqChange(b.getValue())
						&& getRelatinFreqChangeType(ri) == getRelatinFreqChangeType(b.getValue()))
					return true;
			}
			
		}
		
		return false;
	}
	
	
//	private void doKSpermutationTestInR(int[] relationFreqs, int[] Output,
//			StringBuffer testStat, StringBuffer p_value) {
//		
//		if(Rengine.getMainEngine() == null)
//			engineR = new Rengine(new String[] { "--no-save" }, false, null);
//		else
//			engineR = Rengine.getMainEngine();
//		 // Create an R vector in the form of a string.
//        String covariate = Utils.convertArrayToRvector(relationFreqs);
//        String output = Utils.convertArrayToRvector(Output);
//        
//        engineR.eval("covariate=" + covariate);
//        engineR.eval("output=" + output);
//        
//        engineR.eval("library(perm)");
//        engineR.eval("perm = permKS(covariate ~ output)");
//        
//        testStat.append(engineR.eval("perm$statistic").asDouble());
//        p_value.append(engineR.eval("perm$p.value").asDouble());
//		
//	}

	
	private void discoverChangePatterns(
			Multimap<PairRelation, RelationImpact> relationImpactMap_sorted, 
			List<String> charStatements,
			String charactrizationMethod)
	{
		
		long t1 = System.currentTimeMillis();
		
		Iterator<Entry<String, ChangePattern>> it = changePatternsMap.entrySet().iterator();
		Multimap<String, Entry<List<Entry<PairRelation, RelationImpact>>, InformationGain>> allPermsMap_sortedByDCG = LinkedListMultimap.create();
		
		Map<Entry<FrequencyChangeType, FrequencyChange>, List<Entry<PairRelation, RelationImpact>>> relationsGrpdByType = new HashMap<>();
		groupRelatinsByType(relationImpactMap_sorted, relationsGrpdByType);
		
		while(it.hasNext())
		{
			Entry<String, ChangePattern> entry = it.next();
			String changePatternName = entry.getKey();
			ChangePattern cp = entry.getValue();
			
//			System.out.println(changePatternName);
//			long t2 = System.currentTimeMillis();	        	
			Set<Entry<List<Entry<PairRelation, RelationImpact>>, InformationGain>> cpmpSet = getChangePatternMatchingPerms(relationImpactMap_sorted, relationsGrpdByType, cp).entrySet();
//			System.out.println(changePatternName + ": " + (System.currentTimeMillis() - t2));
//			System.out.println("***************************");
			Iterator<Entry<List<Entry<PairRelation, RelationImpact>>, InformationGain>> cpmpSet_it = cpmpSet.iterator();
	        while(cpmpSet_it.hasNext())
	        {
	        	allPermsMap_sortedByDCG.put(changePatternName, cpmpSet_it.next());
	        }
	        
		}
		
		allPermsMap_sortedByDCG = sortByComparator_byDCG3(allPermsMap_sortedByDCG);
		
		Multimap<String, Entry<List<Entry<PairRelation, RelationImpact>>, InformationGain>> 
			allPermsMap_sortedByNDCG = sortByComparator_byNDCG3(allPermsMap_sortedByDCG);
		
		Set<Entry<PairRelation, RelationImpact>> usedRelations = new HashSet();
		List<Entry<String, Map<String, XEventClass>>> charzedDrifts = new ArrayList();
		
		
		String changePatternName = "";
		float changePatternNDCG = 0f;
		
		Iterator<Entry<String, Entry<List<Entry<PairRelation, RelationImpact>>, InformationGain>>> perms_it = allPermsMap_sortedByNDCG.entries().iterator();
		Entry<String, Entry<List<Entry<PairRelation, RelationImpact>>, InformationGain>> nextChangePattern;
		Multimap<String, Map<String, XEventClass>> lengthTwoLoops = LinkedListMultimap.create();
		int numOfCharzedDrifts = 0;
		boolean charPrint = false;
		while(numOfCharzedDrifts < topCharzedDrifts && perms_it.hasNext())
		{
			nextChangePattern = perms_it.next();
			if(!haveSharedRelations(usedRelations, nextChangePattern.getValue().getKey()))
			{
				changePatternName = nextChangePattern.getKey();
		        changePatternNDCG = nextChangePattern.getValue().getValue().getnDCG();
		        List<Entry<PairRelation, RelationImpact>> permutation = nextChangePattern.getValue().getKey();
		        
		        if(!isItASubPermutation(permutation, allPermsMap_sortedByDCG))
		        {
		        	ChangePattern cp = changePatternsMap.get(changePatternName);
			        Map<String, XEventClass> label_EventClass_Map = assignActivitiesToVariables(permutation, cp);
			        if(changePatternName.contains("Insert"))
			        	if(isItDuplicate(label_EventClass_Map.get("y"), relationImpactMap_sorted))
			        		changePatternName = "Duplicate";
			        
			        String driftStatement = createDriftStatement(changePatternName, label_EventClass_Map);
			        Entry<String, Map<String, XEventClass>> charzedDrift = new AbstractMap.SimpleEntry(driftStatement, label_EventClass_Map);
			        
			        if(changePatternName.contains("LoopLengthTwo"))
			        {
			        	if(!islengthTwoLoopRepeated(lengthTwoLoops, changePatternName, label_EventClass_Map))
			        	{
			        		lengthTwoLoops.put(changePatternName, label_EventClass_Map);
			        		charzedDrifts.add(charzedDrift);
			        		charStatements.add(charzedDrift.getKey());
			        	}
			        }else{
			        	charzedDrifts.add(charzedDrift);
			        	charStatements.add(charzedDrift.getKey());
			        }
			        	
			        
			        if(!charPrint)
			        {
			        	
			        	if(!Main.isStandAlone)System.out.println("Caracterization statements:");
			        	charPrint = true;
			        	
			        }
			        if(!Main.isStandAlone)System.out.println("--" + charzedDrift.getKey()/* + ": " + changePatternNDCG */);
			        if(!Main.isStandAlone)System.out.println();
			        
			        List<Entry<PairRelation, RelationImpact>> ch_perm = nextChangePattern.getValue().getKey();
			        usedRelations.addAll(ch_perm);
			        
			        numOfCharzedDrifts++;
		        }
		        
			}
		}
		
		// deal with leftover relations //
//		Set<Entry<PairRelation, RelationImpact>> leftOverRelations = relationImpactMap_sorted.entrySet();
//		leftOverRelations.removeAll(usedRelations);
//		
//		Iterator<Entry<PairRelation, RelationImpact>> iter = leftOverRelations.iterator();
//		while(iter.hasNext())
//		{
//			Entry<PairRelation, RelationImpact> ent = iter.next();
//			System.out.println("Frequency of the relation " + ent.getKey().toString() +
//					" has changed from " + ent.getValue().getAvgFreqBeforeDrift() + 
//					" to " + ent.getValue().getAvgFreqAfterDrift());
//		}
		
		long t2 = System.currentTimeMillis();
//		System.out.println("Change pattern discovery took(ms): " + (t2 - t1));
//	    System.out.println();
		
        /////////
        
		if(!Main.isStandAlone)
		{
	        if(Main.completeCharacterizationExperiment)
	        {
	        	CharacterizationAccuracyResult cres = Main.CharacterizationAccuracyMap.get(logNameShort);
				cres.setNumOfCharacterizedDrifts(cres.getNumOfCharacterizedDrifts() + 1);
				cres.setProcessingTime_ms(cres.getProcessingTime_ms() + (t2 - t1));
				
	        	for (int i = 0; i < charzedDrifts.size(); i++) 
	            {
	        		Entry<String, Map<String, XEventClass>> charzedDrift = charzedDrifts.get(i);
	        		changePatternName = charzedDrift.getKey().substring(0, charzedDrift.getKey().indexOf(':'));
	        		Map<String, XEventClass> label_EventClass_Map = charzedDrift.getValue();
	        		
	        		if(changePatternName.length() != 0)
	    			{
	    				String chPShortName = getChangePatternShortName(changePatternName);
	    	        	if(logNameShort.compareToIgnoreCase(chPShortName) == 0 &&
	    	        			isItTruePositive(logNameShort, cres.getMainActivityNamesList(), label_EventClass_Map))
	    		        {
	    	    			cres.setTP(cres.getTP() + 1);
	    		        }else
	    		        {
	    		        	CharacterizationAccuracyResult cres2 = Main.CharacterizationAccuracyMap.get(chPShortName);
	    	    			cres2.setFP(cres2.getFP() + 1);
	    		        }
	    			}
	            }
				
	        }
		}
       
        
        
//       	try {
//       		
//       		FileWriter fw = null;
//            if(resetFilesAccuracy)
//            {
//            	fw = new FileWriter(charAccuracyKSPTfile, false);
//            	resetFilesAccuracy = false;
//            }else
//            	fw = new FileWriter(charAccuracyKSPTfile, true);
//			
//            for (int i = 0; i < charzedDrifts.size(); i++) 
//            {
//            	Entry<String, Map<String, XEventClass>> charzedDrift = charzedDrifts.get(i);
//            	fw.write(charzedDrift.getKey() + ", , time," + (t2-t1) + "\n");
//			}
//            fw.write("\n");
//			
//			fw.close();
//			
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}
	
	private boolean isItDuplicate(XEventClass eventClass, Multimap<PairRelation, RelationImpact> relationImpactMap)
	{
		Iterator<Entry<PairRelation, RelationImpact>> it = relationImpactMap.entries().iterator();
		while(it.hasNext())
		{
			Entry<PairRelation, RelationImpact> entry = it.next();
			PairRelation pr = entry.getKey();
			RelationImpact ri = entry.getValue();
			if(eventClass.equals(pr.getPair().getFirst()) || eventClass.equals(pr.getPair().getSecond()))
				if(ri.getAvgFreqBeforeDrift() > 0)
					return true;
		}
		
		return false;
	}
	
	private boolean islengthTwoLoopRepeated(Multimap<String, Map<String, XEventClass>> lengthTwoLoops, 
			String changePatternName, Map<String, XEventClass> relationImpactMap)
	{
		Iterator<Map<String, XEventClass>> it = lengthTwoLoops.get(changePatternName).iterator();
		
		while(it.hasNext())
		{
			Map<String, XEventClass> map = it.next();
			
			if(map.get("x").equals(relationImpactMap.get("y")) && map.get("y").equals(relationImpactMap.get("x")))
				return true;
		}
		
		return false;
	}
	
	private List<Entry<PairRelation, RelationImpact>> groupRelatinsByType(Multimap<PairRelation, RelationImpact> relationImpactMap, Map<Entry<FrequencyChangeType, FrequencyChange>, 
			List<Entry<PairRelation, RelationImpact>>> relationsGrpdByType)
	{
		
		List<Entry<PairRelation, RelationImpact>> ABSdec = new ArrayList<Entry<PairRelation, RelationImpact>>();
		List<Entry<PairRelation, RelationImpact>> ABSinc = new ArrayList<Entry<PairRelation, RelationImpact>>();
		List<Entry<PairRelation, RelationImpact>> RELdec = new ArrayList<Entry<PairRelation, RelationImpact>>();
		List<Entry<PairRelation, RelationImpact>> RELinc = new ArrayList<Entry<PairRelation, RelationImpact>>();
		List<Entry<PairRelation, RelationImpact>> ABSorRELdec = new ArrayList<Entry<PairRelation, RelationImpact>>();
		List<Entry<PairRelation, RelationImpact>> ABSorRELinc = new ArrayList<Entry<PairRelation, RelationImpact>>();
		
		List<Entry<PairRelation, RelationImpact>> filteredRelationImpactList = new ArrayList<>();
        Iterator<Entry<PairRelation, RelationImpact>> it = relationImpactMap.entries().iterator();
		while(it.hasNext())
		{
			
			Entry<PairRelation, RelationImpact> entry = it.next();
			PairRelation pr = entry.getKey();
			RelationImpact ri = entry.getValue();
			
			if(ri.getAvgFreqBeforeDrift() > ri.getAvgFreqAfterDrift())
			{
				if(ri.getAvgFreqAfterDrift() == 0)
				{
					ABSdec.add(entry);
				}else
				{
					RELdec.add(entry);
				}
				ABSorRELdec.add(entry);
			}else if(ri.getAvgFreqBeforeDrift() < ri.getAvgFreqAfterDrift())
			{
				if(ri.getAvgFreqBeforeDrift() == 0)
				{
					ABSinc.add(entry);
				}else
				{
					RELinc.add(entry);
				}
				ABSorRELinc.add(entry);
			}
		}
		
		relationsGrpdByType.put(new AbstractMap.SimpleEntry(FrequencyChangeType.ABS, FrequencyChange.Decrease), ABSdec);
		relationsGrpdByType.put(new AbstractMap.SimpleEntry(FrequencyChangeType.ABS, FrequencyChange.Increase), ABSinc);
		relationsGrpdByType.put(new AbstractMap.SimpleEntry(FrequencyChangeType.REL, FrequencyChange.Decrease), RELdec);
		relationsGrpdByType.put(new AbstractMap.SimpleEntry(FrequencyChangeType.REL, FrequencyChange.Increase), RELinc);
		relationsGrpdByType.put(new AbstractMap.SimpleEntry(FrequencyChangeType.ABSorREL, FrequencyChange.Decrease), ABSorRELdec);
		relationsGrpdByType.put(new AbstractMap.SimpleEntry(FrequencyChangeType.ABSorREL, FrequencyChange.Increase), ABSorRELinc);
		
		
		return filteredRelationImpactList;
		
	}
	
	private FrequencyChangeType getRelatinFreqChange(RelationImpact relationImpact)
	{
		if((relationImpact.getAvgFreqBeforeDrift() > relationImpact.getAvgFreqAfterDrift() 
				&& relationImpact.getAvgFreqAfterDrift() == 0) 
				|| (relationImpact.getAvgFreqBeforeDrift() < relationImpact.getAvgFreqAfterDrift())
				&& relationImpact.getAvgFreqBeforeDrift() == 0)
		{
			return FrequencyChangeType.ABS;
		}
		
		return null;
	}
	
	private FrequencyChange getRelatinFreqChangeType(RelationImpact relationImpact)
	{
		if(relationImpact.getAvgFreqBeforeDrift() > relationImpact.getAvgFreqAfterDrift())
		{
			return FrequencyChange.Decrease;
		}else if(relationImpact.getAvgFreqBeforeDrift() < relationImpact.getAvgFreqAfterDrift())
		{
			return FrequencyChange.Increase;
		}
		
		return null;
	}
		
	private Map<List<Entry<PairRelation, RelationImpact>>, InformationGain> getChangePatternMatchingPerms(Multimap<PairRelation, RelationImpact> relationImpactMap_sorted, 
			Map<Entry<FrequencyChangeType, FrequencyChange>, List<Entry<PairRelation, RelationImpact>>> relationsGrpdByType,
			ChangePattern cp)
	{
		
		Map<List<Entry<PairRelation, RelationImpact>>, InformationGain> CPMP = getChangePatternMatchingPermutations(relationImpactMap_sorted, relationsGrpdByType, cp);
		
		return CPMP;
		
	}
	
//	// Serial insert: [-: 1)x->z(ABS)] [+: 2)x->y(ABS), 3)y->z(ABS)]
//	private Map<List<Entry<PairRelation, RelationImpact>>, Float> getSerialInsertDriftRelevance(Map<PairRelation, RelationImpact> relationImpactMap_sorted)
//	{
//		
//		List<Object[]> relPatsList = new ArrayList<Object[]>();
//		relPatsList.add(new Object[]{"x", "z", BehaviorRelation.Causal, true, FrequencyChangeType.ABS}); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
//		relPatsList.add(new Object[]{"x", "y", BehaviorRelation.Causal, false, FrequencyChangeType.ABS}); 
//		relPatsList.add(new Object[]{"y", "z", BehaviorRelation.Causal, false, FrequencyChangeType.ABS});
//		float relPats_relevances[]  = {1f, 1f, 1f};
//		
//		Map<List<Entry<PairRelation, RelationImpact>>, Float> CPMP = getChangePatternMatchingPermutations(relationImpactMap_sorted, relPatsList, relPats_relevances);
//		
//		return CPMP;
//		
//	}
//
//	// Serial removal: [-: 1)x->y(ABS), 2)y->z(ABS)] [+: 3)x->z(ABS)]
//	private Map<List<Entry<PairRelation, RelationImpact>>, Float> getSerialRemovalDriftRelevance(Map<PairRelation, RelationImpact> relationImpactMap_sorted)
//	{
//		
//		List<Object[]> relPatsList = new ArrayList<Object[]>();
//		relPatsList.add(new Object[]{"x", "y", BehaviorRelation.Causal, true, FrequencyChangeType.ABS}); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
//		relPatsList.add(new Object[]{"y", "z", BehaviorRelation.Causal, true, FrequencyChangeType.ABS});
//		relPatsList.add(new Object[]{"x", "z", BehaviorRelation.Causal, false, FrequencyChangeType.ABS});
//		float relPats_relevances[]  = {1f, 1f, 1f};
//		
//		Map<List<Entry<PairRelation, RelationImpact>>, Float> CPMP = getChangePatternMatchingPermutations(relationImpactMap_sorted, relPatsList, relPats_relevances);
//		
//		return CPMP;
//		
//	}
//	
//	// Parallel insert: [-: 1)w->x(REL), 2)x->z(REL)] [+: 3)w->y(ABS), 4)y->z(ABS), 5)x||y(ABS)]
//	private Map<List<Entry<PairRelation, RelationImpact>>, Float> getParallelInsertDriftRelevance(Map<PairRelation, RelationImpact> relationImpactMap_sorted)
//	{
//		
//		List<Object[]> relPatsList = new ArrayList<Object[]>();
//		relPatsList.add(new Object[]{"w", "x", BehaviorRelation.Causal, true, FrequencyChangeType.REL}); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
//		relPatsList.add(new Object[]{"x", "z", BehaviorRelation.Causal, true, FrequencyChangeType.REL}); 
//		relPatsList.add(new Object[]{"w", "y", BehaviorRelation.Causal, false, FrequencyChangeType.ABS});
//		relPatsList.add(new Object[]{"y", "z", BehaviorRelation.Causal, false, FrequencyChangeType.ABS});
//		relPatsList.add(new Object[]{"x", "y", BehaviorRelation.CONCURRENCY, false, FrequencyChangeType.ABS});
//		float relPats_relevances[]  = {1f, 1f, 1f, 1f, 1f};
//		
//		Map<List<Entry<PairRelation, RelationImpact>>, Float> CPMP = getChangePatternMatchingPermutations(relationImpactMap_sorted, relPatsList, relPats_relevances);
//		
//		return CPMP;
//		
//	}
//	
//	// Parallel removal: [-: 3)w->y(ABS), 4)y->z(ABS), 5)x||y(ABS)] [+: 1)w->x(REL), 2)x->z(REL)]
//	private Map<List<Entry<PairRelation, RelationImpact>>, Float> getParallelRemovalDriftRelevance(Map<PairRelation, RelationImpact> relationImpactMap_sorted)
//	{
//		
//		List<Object[]> relPatsList = new ArrayList<Object[]>();
//		relPatsList.add(new Object[]{"w", "y", BehaviorRelation.Causal, true, FrequencyChangeType.ABS});
//		relPatsList.add(new Object[]{"y", "z", BehaviorRelation.Causal, true, FrequencyChangeType.ABS});
//		relPatsList.add(new Object[]{"x", "y", BehaviorRelation.CONCURRENCY, true, FrequencyChangeType.ABS});
//		relPatsList.add(new Object[]{"w", "x", BehaviorRelation.Causal, false, FrequencyChangeType.REL}); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
//		relPatsList.add(new Object[]{"x", "z", BehaviorRelation.Causal, false, FrequencyChangeType.REL}); 
//		float relPats_relevances[]  = {1f, 1f, 1f, 1f, 1f};
//		
//		Map<List<Entry<PairRelation, RelationImpact>>, Float> CPMP = getChangePatternMatchingPermutations(relationImpactMap_sorted, relPatsList, relPats_relevances);
//		
//		return CPMP;
//		
//	}
//	
//	// Conditional insert: [-: 1)x->z(REL)] [+: 2)x->y(ABS), 3)y->z(ABS)]
//	private Map<List<Entry<PairRelation, RelationImpact>>, Float> getConditionalInsertDriftRelevance(Map<PairRelation, RelationImpact> relationImpactMap_sorted)
//	{
//		
//		List<Object[]> relPatsList = new ArrayList<Object[]>();
//		relPatsList.add(new Object[]{"x", "z", BehaviorRelation.Causal, true, FrequencyChangeType.REL}); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
//		relPatsList.add(new Object[]{"x", "y", BehaviorRelation.Causal, false, FrequencyChangeType.ABS}); 
//		relPatsList.add(new Object[]{"y", "z", BehaviorRelation.Causal, false, FrequencyChangeType.ABS});
//		float relPats_relevances[]  = {1f, 1f, 1f};
//		
//		Map<List<Entry<PairRelation, RelationImpact>>, Float> CPMP = getChangePatternMatchingPermutations(relationImpactMap_sorted, relPatsList, relPats_relevances);
//		
//		return CPMP;
//		
//	}
//	
//	// Conditional removal: [-: 2)x->y(ABS), 3)y->z(ABS)] [+: 1)x->z(REL)]
//	private Map<List<Entry<PairRelation, RelationImpact>>, Float> getConditionalRemovalDriftRelevance(Map<PairRelation, RelationImpact> relationImpactMap_sorted)
//	{
//		
//		List<Object[]> relPatsList = new ArrayList<Object[]>();
//		relPatsList.add(new Object[]{"x", "y", BehaviorRelation.Causal, true, FrequencyChangeType.ABS}); 
//		relPatsList.add(new Object[]{"y", "z", BehaviorRelation.Causal, true, FrequencyChangeType.ABS});
//		relPatsList.add(new Object[]{"x", "z", BehaviorRelation.Causal, false, FrequencyChangeType.REL}); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
//		float relPats_relevances[]  = {1f, 1f, 1f};
//		
//		Map<List<Entry<PairRelation, RelationImpact>>, Float> CPMP = getChangePatternMatchingPermutations(relationImpactMap_sorted, relPatsList, relPats_relevances);
//		
//		return CPMP;
//		
//	}
//	
//	// Substitute: [-: 1)x->y(ABS), 2)y->z(ABS)] [+: 3)x->w(ABS), 3)w->z(ABS)]
//	private Map<List<Entry<PairRelation, RelationImpact>>, Float> getSubstituteDriftRelevance(Map<PairRelation, RelationImpact> relationImpactMap_sorted)
//	{
//		
//		List<Object[]> relPatsList = new ArrayList<Object[]>();
//		relPatsList.add(new Object[]{"x", "y", BehaviorRelation.Causal, true, FrequencyChangeType.ABS}); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
//		relPatsList.add(new Object[]{"y", "z", BehaviorRelation.Causal, true, FrequencyChangeType.ABS}); 
//		relPatsList.add(new Object[]{"x", "w", BehaviorRelation.Causal, false, FrequencyChangeType.ABS});
//		relPatsList.add(new Object[]{"w", "z", BehaviorRelation.Causal, false, FrequencyChangeType.ABS});
//		float relPats_relevances[]  = {1f, 1f, 1f, 1f};
//		
//		Map<List<Entry<PairRelation, RelationImpact>>, Float> CPMP = getChangePatternMatchingPermutations(relationImpactMap_sorted, relPatsList, relPats_relevances);
//		
//		return CPMP;
//		
//	}
//	
//	// Swap: [-: 1)t->u(ABS), 2)u->v(ABS), 3)w->x(ABS), 4)x->y(ABS)] [+: 5)t->x(ABS), 6)x->v(ABS), 7)w->u(ABS), 8)u->y(ABS)]
//	private Map<List<Entry<PairRelation, RelationImpact>>, Float> getSwapDriftRelevance(Map<PairRelation, RelationImpact> relationImpactMap_sorted)
//	{
//		
//		List<Object[]> relPatsList = new ArrayList<Object[]>();
//		relPatsList.add(new Object[]{"t", "u", BehaviorRelation.Causal, true, FrequencyChangeType.ABS}); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
//		relPatsList.add(new Object[]{"u", "v", BehaviorRelation.Causal, true, FrequencyChangeType.ABS}); 
//		relPatsList.add(new Object[]{"w", "x", BehaviorRelation.Causal, true, FrequencyChangeType.ABS});
//		relPatsList.add(new Object[]{"x", "y", BehaviorRelation.Causal, true, FrequencyChangeType.ABS});
//		relPatsList.add(new Object[]{"t", "x", BehaviorRelation.Causal, false, FrequencyChangeType.ABS});
//		relPatsList.add(new Object[]{"x", "v", BehaviorRelation.Causal, false, FrequencyChangeType.ABS});
//		relPatsList.add(new Object[]{"w", "u", BehaviorRelation.Causal, false, FrequencyChangeType.ABS});
//		relPatsList.add(new Object[]{"u", "y", BehaviorRelation.Causal, false, FrequencyChangeType.ABS});
//		float relPats_relevances[]  = {1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f};
//		
//		Map<List<Entry<PairRelation, RelationImpact>>, Float> CPMP = getChangePatternMatchingPermutations(relationImpactMap_sorted, relPatsList, relPats_relevances);
//		
//		return CPMP;
//		
//	}
//		
//	// Conditional Move: [-: 1)v->w(ABS), 2)w->x(ABS), 3)u->y(REL), 4)y->z(REL)] [+: 5)v->x(ABS), 6)u->w(ABS), 7)w->z(ABS)]
//	private Map<List<Entry<PairRelation, RelationImpact>>, Float> getConditionalMoveDriftRelevance(Map<PairRelation, RelationImpact> relationImpactMap_sorted)
//	{
//		
//		List<Object[]> relPatsList = new ArrayList<Object[]>();
//		relPatsList.add(new Object[]{"v", "w", BehaviorRelation.Causal, true, FrequencyChangeType.ABS}); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
//		relPatsList.add(new Object[]{"w", "x", BehaviorRelation.Causal, true, FrequencyChangeType.ABS}); 
//		relPatsList.add(new Object[]{"u", "y", BehaviorRelation.Causal, true, FrequencyChangeType.REL});
//		relPatsList.add(new Object[]{"y", "z", BehaviorRelation.Causal, true, FrequencyChangeType.REL});
//		relPatsList.add(new Object[]{"v", "x", BehaviorRelation.Causal, false, FrequencyChangeType.ABS});
//		relPatsList.add(new Object[]{"u", "w", BehaviorRelation.Causal, false, FrequencyChangeType.ABS});
//		relPatsList.add(new Object[]{"w", "z", BehaviorRelation.Causal, false, FrequencyChangeType.ABS});
//		float relPats_relevances[]  = {1f, 1f, 1f, 1f, 1f, 1f, 1f};
//		
//		Map<List<Entry<PairRelation, RelationImpact>>, Float> CPMP = getChangePatternMatchingPermutations(relationImpactMap_sorted, relPatsList, relPats_relevances);
//		
//		return CPMP;
//		
//	}
//	
//	// Conditional Move Inverse: [-: 5)v->x(ABS), 6)u->w(ABS), 7)w->z(ABS)] [+: 1)v->w(ABS), 2)w->x(ABS), 3)u->y(REL), 4)y->z(REL)]
//	private Map<List<Entry<PairRelation, RelationImpact>>, Float> getConditionalMoveInverseDriftRelevance(Map<PairRelation, RelationImpact> relationImpactMap_sorted)
//	{
//		
//		List<Object[]> relPatsList = new ArrayList<Object[]>();
//		relPatsList.add(new Object[]{"v", "x", BehaviorRelation.Causal, true, FrequencyChangeType.ABS});
//		relPatsList.add(new Object[]{"u", "w", BehaviorRelation.Causal, true, FrequencyChangeType.ABS});
//		relPatsList.add(new Object[]{"w", "z", BehaviorRelation.Causal, true, FrequencyChangeType.ABS});
//		relPatsList.add(new Object[]{"v", "w", BehaviorRelation.Causal, false, FrequencyChangeType.ABS}); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
//		relPatsList.add(new Object[]{"w", "x", BehaviorRelation.Causal, false, FrequencyChangeType.ABS}); 
//		relPatsList.add(new Object[]{"u", "y", BehaviorRelation.Causal, false, FrequencyChangeType.REL});
//		relPatsList.add(new Object[]{"y", "z", BehaviorRelation.Causal, false, FrequencyChangeType.REL});
//		float relPats_relevances[]  = {1f, 1f, 1f, 1f, 1f, 1f, 1f};
//		
//		Map<List<Entry<PairRelation, RelationImpact>>, Float> CPMP = getChangePatternMatchingPermutations(relationImpactMap_sorted, relPatsList, relPats_relevances);
//		
//		return CPMP;
//		
//	}
//	
//	// Parallel Move: [-: 1)v->w(ABS), 2)w->x(ABS), 3)u->y(REL), 4)y->z(REL)] [+: 5)v->x(ABS), 6)u->w(ABS), 7)w->z(ABS), 8)w||y(ABS)]
//	private Map<List<Entry<PairRelation, RelationImpact>>, Float> getParallelMoveDriftRelevance(Map<PairRelation, RelationImpact> relationImpactMap_sorted)
//	{
//		
//		List<Object[]> relPatsList = new ArrayList<Object[]>();
//		relPatsList.add(new Object[]{"v", "w", BehaviorRelation.Causal, true, FrequencyChangeType.ABS}); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
//		relPatsList.add(new Object[]{"w", "x", BehaviorRelation.Causal, true, FrequencyChangeType.ABS}); 
//		relPatsList.add(new Object[]{"u", "y", BehaviorRelation.Causal, true, FrequencyChangeType.REL});
//		relPatsList.add(new Object[]{"y", "z", BehaviorRelation.Causal, true, FrequencyChangeType.REL});
//		relPatsList.add(new Object[]{"v", "x", BehaviorRelation.Causal, false, FrequencyChangeType.ABS});
//		relPatsList.add(new Object[]{"u", "w", BehaviorRelation.Causal, false, FrequencyChangeType.ABS});
//		relPatsList.add(new Object[]{"w", "z", BehaviorRelation.Causal, false, FrequencyChangeType.ABS});
//		relPatsList.add(new Object[]{"w", "y", BehaviorRelation.CONCURRENCY, false, FrequencyChangeType.ABS});
//		float relPats_relevances[]  = {1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f};
//		
//		Map<List<Entry<PairRelation, RelationImpact>>, Float> CPMP = getChangePatternMatchingPermutations(relationImpactMap_sorted, relPatsList, relPats_relevances);
//		
//		return CPMP;
//		
//	}
//	
//	// Parallel Move Inverse: [-: 5)v->x(ABS), 6)u->w(ABS), 7)w->z(ABS), 8)w||y(ABS)] [+: 1)v->w(ABS), 2)w->x(ABS), 3)u->y(REL), 4)y->z(REL)]
//	private Map<List<Entry<PairRelation, RelationImpact>>, Float> getParallelMoveInverseDriftRelevance(Map<PairRelation, RelationImpact> relationImpactMap_sorted)
//	{
//		
//		List<Object[]> relPatsList = new ArrayList<Object[]>();
//		relPatsList.add(new Object[]{"v", "x", BehaviorRelation.Causal, true, FrequencyChangeType.ABS}); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
//		relPatsList.add(new Object[]{"u", "w", BehaviorRelation.Causal, true, FrequencyChangeType.ABS});
//		relPatsList.add(new Object[]{"w", "z", BehaviorRelation.Causal, true, FrequencyChangeType.ABS});
//		relPatsList.add(new Object[]{"w", "y", BehaviorRelation.CONCURRENCY, true, FrequencyChangeType.ABS});
//		relPatsList.add(new Object[]{"v", "w", BehaviorRelation.Causal, false, FrequencyChangeType.ABS}); 
//		relPatsList.add(new Object[]{"w", "x", BehaviorRelation.Causal, false, FrequencyChangeType.ABS}); 
//		relPatsList.add(new Object[]{"u", "y", BehaviorRelation.Causal, false, FrequencyChangeType.REL});
//		relPatsList.add(new Object[]{"y", "z", BehaviorRelation.Causal, false, FrequencyChangeType.REL});
//		float relPats_relevances[]  = {1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f};
//		
//		Map<List<Entry<PairRelation, RelationImpact>>, Float> CPMP = getChangePatternMatchingPermutations(relationImpactMap_sorted, relPatsList, relPats_relevances);
//		
//		return CPMP;
//		
//	}
//	
//	// Serial Move: [-: 1)w->x(ABS), 2)x->y(ABS), 3)t->v(ABS)] [+: 4)w->y(ABS), 5)t->x(ABS), 6)x->v(ABS)]
//	private Map<List<Entry<PairRelation, RelationImpact>>, Float> getSerialMoveDriftRelevance(Map<PairRelation, RelationImpact> relationImpactMap_sorted)
//	{
//		
//		List<Object[]> relPatsList = new ArrayList<Object[]>();
//		relPatsList.add(new Object[]{"w", "x", BehaviorRelation.Causal, true, FrequencyChangeType.ABS}); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
//		relPatsList.add(new Object[]{"x", "y", BehaviorRelation.Causal, true, FrequencyChangeType.ABS}); 
//		relPatsList.add(new Object[]{"t", "v", BehaviorRelation.Causal, true, FrequencyChangeType.ABS});
//		relPatsList.add(new Object[]{"w", "y", BehaviorRelation.Causal, false, FrequencyChangeType.ABS});
//		relPatsList.add(new Object[]{"t", "x", BehaviorRelation.Causal, false, FrequencyChangeType.ABS});
//		relPatsList.add(new Object[]{"x", "v", BehaviorRelation.Causal, false, FrequencyChangeType.ABS});
//		float relPats_relevances[]  = {1f, 1f, 1f, 1f, 1f, 1f};
//		
//		Map<List<Entry<PairRelation, RelationImpact>>, Float> CPMP = getChangePatternMatchingPermutations(relationImpactMap_sorted, relPatsList, relPats_relevances);
//		
//		return CPMP;
//		
//	}
//	
//	// Conditional to sequence: [-: 1)w->y(ABS), 2)x->z(ABS)] [+: 3)x->y(ABS), 4)w->x(REL), 5)y->z(REL)]
//	private Map<List<Entry<PairRelation, RelationImpact>>, Float> getConditionalToSequenceDriftRelevance(Map<PairRelation, RelationImpact> relationImpactMap_sorted)
//	{
//		
//		List<Object[]> relPatsList = new ArrayList<Object[]>();
//		relPatsList.add(new Object[]{"w", "y", BehaviorRelation.Causal, true, FrequencyChangeType.ABS}); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
//		relPatsList.add(new Object[]{"x", "z", BehaviorRelation.Causal, true, FrequencyChangeType.ABS}); 
//		relPatsList.add(new Object[]{"x", "y", BehaviorRelation.Causal, false, FrequencyChangeType.ABS});
//		relPatsList.add(new Object[]{"w", "x", BehaviorRelation.Causal, false, FrequencyChangeType.REL});
//		relPatsList.add(new Object[]{"y", "z", BehaviorRelation.Causal, false, FrequencyChangeType.REL});
//		float relPats_relevances[]  = {1f, 1f, 1f, 1f, 1f};
//		
//		Map<List<Entry<PairRelation, RelationImpact>>, Float> CPMP = getChangePatternMatchingPermutations(relationImpactMap_sorted, relPatsList, relPats_relevances);
//		
//		return CPMP;
//		
//	}
//	
//	// Sequence to Conditional: [-: 3)x->y(ABS), 4)w->x(REL), 5)y->z(REL)] [+: 1)w->y(ABS), 2)x->z(ABS)]
//	private Map<List<Entry<PairRelation, RelationImpact>>, Float> getSequenceToConditionalDriftRelevance(Map<PairRelation, RelationImpact> relationImpactMap_sorted)
//	{
//		
//		List<Object[]> relPatsList = new ArrayList<Object[]>();
//		relPatsList.add(new Object[]{"x", "y", BehaviorRelation.Causal, true, FrequencyChangeType.ABS});
//		relPatsList.add(new Object[]{"w", "x", BehaviorRelation.Causal, true, FrequencyChangeType.REL});
//		relPatsList.add(new Object[]{"y", "z", BehaviorRelation.Causal, true, FrequencyChangeType.REL});
//		relPatsList.add(new Object[]{"w", "y", BehaviorRelation.Causal, false, FrequencyChangeType.ABS}); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
//		relPatsList.add(new Object[]{"x", "z", BehaviorRelation.Causal, false, FrequencyChangeType.ABS}); 
//		float relPats_relevances[]  = {1f, 1f, 1f, 1f, 1f};
//		
//		Map<List<Entry<PairRelation, RelationImpact>>, Float> CPMP = getChangePatternMatchingPermutations(relationImpactMap_sorted, relPatsList, relPats_relevances);
//		
//		return CPMP;
//		
//	}
//	
//	// Parallel to sequence: [-: 1)w->y(ABS), 2)x->z(ABS), 3)x||y(ABS)] [+: 4)x->y(REL), 4)w->x(REL), 5)y->z(REL)]
//	private Map<List<Entry<PairRelation, RelationImpact>>, Float> getParallelToSequenceDriftRelevance(Map<PairRelation, RelationImpact> relationImpactMap_sorted)
//	{
//		
//		List<Object[]> relPatsList = new ArrayList<Object[]>();
//		relPatsList.add(new Object[]{"w", "y", BehaviorRelation.Causal, true, FrequencyChangeType.ABS}); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
//		relPatsList.add(new Object[]{"x", "z", BehaviorRelation.Causal, true, FrequencyChangeType.ABS}); 
//		relPatsList.add(new Object[]{"x", "y", BehaviorRelation.CONCURRENCY, true, FrequencyChangeType.ABS});
//		relPatsList.add(new Object[]{"x", "y", BehaviorRelation.Causal, false, FrequencyChangeType.ABS});
//		relPatsList.add(new Object[]{"w", "x", BehaviorRelation.Causal, false, FrequencyChangeType.REL});
//		relPatsList.add(new Object[]{"y", "z", BehaviorRelation.Causal, false, FrequencyChangeType.REL});
//		float relPats_relevances[]  = {1f, 1f, 1f, 1f, 1f, 1f};
//		
//		Map<List<Entry<PairRelation, RelationImpact>>, Float> CPMP = getChangePatternMatchingPermutations(relationImpactMap_sorted, relPatsList, relPats_relevances);
//		
//		return CPMP;
//		
//	}
//	
//	// Sequence to Parallel: [-: 4)x->y(A), 4)w->x(REL), 5)y->z(REL)] [+: 1)w->y(ABS), 2)x->z(ABS), 3)x||y(ABS)]
//	private Map<List<Entry<PairRelation, RelationImpact>>, Float> getSequenceToParallelDriftRelevance(Map<PairRelation, RelationImpact> relationImpactMap_sorted)
//	{
//		
//		List<Object[]> relPatsList = new ArrayList<Object[]>();
//		relPatsList.add(new Object[]{"x", "y", BehaviorRelation.Causal, true, FrequencyChangeType.ABS});
//		relPatsList.add(new Object[]{"w", "x", BehaviorRelation.Causal, true, FrequencyChangeType.REL});
//		relPatsList.add(new Object[]{"y", "z", BehaviorRelation.Causal, true, FrequencyChangeType.REL});
//		relPatsList.add(new Object[]{"w", "y", BehaviorRelation.Causal, false, FrequencyChangeType.ABS}); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
//		relPatsList.add(new Object[]{"x", "z", BehaviorRelation.Causal, false, FrequencyChangeType.ABS}); 
//		relPatsList.add(new Object[]{"x", "y", BehaviorRelation.CONCURRENCY, false, FrequencyChangeType.ABS});
//		float relPats_relevances[]  = {1f, 1f, 1f, 1f, 1f, 1f};
//		
//		Map<List<Entry<PairRelation, RelationImpact>>, Float> CPMP = getChangePatternMatchingPermutations(relationImpactMap_sorted, relPatsList, relPats_relevances);
//		
//		return CPMP;
//		
//	}
//	
//	// Loop lenght one: [+: 1)x@x(ABS)]
//	private Map<List<Entry<PairRelation, RelationImpact>>, Float> getLoopLengthOneDriftRelevance(Map<PairRelation, RelationImpact> relationImpactMap_sorted)
//	{
//		
//		List<Object[]> relPatsList = new ArrayList<Object[]>();
//		relPatsList.add(new Object[]{"x", "x", BehaviorRelation.Length_One_Loop, false, FrequencyChangeType.ABS}); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
//		float relPats_relevances[]  = {3};
//		
//		Map<List<Entry<PairRelation, RelationImpact>>, Float> CPMP = getChangePatternMatchingPermutations(relationImpactMap_sorted, relPatsList, relPats_relevances);
//		
//		return CPMP;
//		
//	}
//	
//	// Loop lenght one inverse: [-: 1)x@x(ABS)]
//	private Map<List<Entry<PairRelation, RelationImpact>>, Float> getLoopLengthOneInverseDriftRelevance(Map<PairRelation, RelationImpact> relationImpactMap_sorted)
//	{
//		
//		List<Object[]> relPatsList = new ArrayList<Object[]>();
//		relPatsList.add(new Object[]{"x", "x", BehaviorRelation.Length_One_Loop, true, FrequencyChangeType.ABS}); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
//		float relPats_relevances[]  = {3};
//		
//		Map<List<Entry<PairRelation, RelationImpact>>, Float> CPMP = getChangePatternMatchingPermutations(relationImpactMap_sorted, relPatsList, relPats_relevances);
//		
//		return CPMP;
//		
//	}
//	
//	// Loop lenght two: [+: 1)x@y(ABS)]
//	private Map<List<Entry<PairRelation, RelationImpact>>, Float> getLoopLengthTwoDriftRelevance(Map<PairRelation, RelationImpact> relationImpactMap_sorted)
//	{
//		
//		List<Object[]> relPatsList = new ArrayList<Object[]>();
//		relPatsList.add(new Object[]{"x", "y", BehaviorRelation.Length_Two_Loop, false, FrequencyChangeType.ABS}); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
//		float relPats_relevances[]  = {100};
//		
//		Map<List<Entry<PairRelation, RelationImpact>>, Float> CPMP = getChangePatternMatchingPermutations(relationImpactMap_sorted, relPatsList, relPats_relevances);
//		
//		return CPMP;
//		
//	}
//	
//	// Loop lenght two Inverse: [-: 1)x@y(ABS)]
//	private Map<List<Entry<PairRelation, RelationImpact>>, Float> getLoopLengthTwoInverseDriftRelevance(Map<PairRelation, RelationImpact> relationImpactMap_sorted)
//	{
//		
//		List<Object[]> relPatsList = new ArrayList<Object[]>();
//		relPatsList.add(new Object[]{"x", "y", BehaviorRelation.Length_Two_Loop, true, FrequencyChangeType.ABS}); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
//		float relPats_relevances[]  = {100};
//		
//		Map<List<Entry<PairRelation, RelationImpact>>, Float> CPMP = getChangePatternMatchingPermutations(relationImpactMap_sorted, relPatsList, relPats_relevances);
//		
//		return CPMP;
//		
//	}
//	
//	// Loop lenght three or more: [+: 1)x->y(REL), 2)y->z(REL), 3)z->x(ABS)]
//	private Map<List<Entry<PairRelation, RelationImpact>>, Float> getLoopLengthThreeDriftRelevance(Map<PairRelation, RelationImpact> relationImpactMap_sorted)
//	{
//		
//		List<Object[]> relPatsList = new ArrayList<Object[]>();
//		relPatsList.add(new Object[]{"x", "y", BehaviorRelation.Causal, false, FrequencyChangeType.REL}); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
//		relPatsList.add(new Object[]{"y", "z", BehaviorRelation.Causal, false, FrequencyChangeType.REL}); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
//		relPatsList.add(new Object[]{"z", "x", BehaviorRelation.Causal, false, FrequencyChangeType.ABS}); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
//		float relPats_relevances[]  = {1f, 1f, 1f};
//		
//		Map<List<Entry<PairRelation, RelationImpact>>, Float> CPMP = getChangePatternMatchingPermutations(relationImpactMap_sorted, relPatsList, relPats_relevances);
//		
//		return CPMP;
//		
//	}
//	
//	// Loop lenght three or more: [-: 1)x->y(REL), 2)y->z(REL), 3)z->x(ABS)]
//	private Map<List<Entry<PairRelation, RelationImpact>>, Float> getLoopLengthThreeInverseDriftRelevance(Map<PairRelation, RelationImpact> relationImpactMap_sorted)
//	{
//		
//		List<Object[]> relPatsList = new ArrayList<Object[]>();
//		relPatsList.add(new Object[]{"x", "y", BehaviorRelation.Causal, true, FrequencyChangeType.REL}); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
//		relPatsList.add(new Object[]{"y", "z", BehaviorRelation.Causal, true, FrequencyChangeType.REL}); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
//		relPatsList.add(new Object[]{"z", "x", BehaviorRelation.Causal, true, FrequencyChangeType.ABS}); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
//		float relPats_relevances[]  = {1f, 1f, 1f};
//		
//		Map<List<Entry<PairRelation, RelationImpact>>, Float> CPMP = getChangePatternMatchingPermutations(relationImpactMap_sorted, relPatsList, relPats_relevances);
//		
//		return CPMP;
//		
//	}
//	
//	// Synchronize Add: [-: 1)y||z(ABS), 2)w->y(ABSorREL), 3)z->u(ABSorREL)] [+: 4)z->y(ABS), 5)y->u(ABSorREL), 6)w->z(ABSorREL)]
//	private Map<List<Entry<PairRelation, RelationImpact>>, Float> getSynchronizeAddDriftRelevance(Map<PairRelation, RelationImpact> relationImpactMap_sorted)
//	{
//		
//		List<Object[]> relPatsList = new ArrayList<Object[]>();
//		relPatsList.add(new Object[]{"y", "z", BehaviorRelation.CONCURRENCY, true, FrequencyChangeType.ABS}); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
//		relPatsList.add(new Object[]{"w", "y", BehaviorRelation.Causal, true, FrequencyChangeType.ABSorREL});
//		relPatsList.add(new Object[]{"z", "u", BehaviorRelation.Causal, true, FrequencyChangeType.ABSorREL});
//		relPatsList.add(new Object[]{"z", "y", BehaviorRelation.Causal, false, FrequencyChangeType.ABS});
//		relPatsList.add(new Object[]{"y", "u", BehaviorRelation.Causal, false, FrequencyChangeType.ABSorREL});
//		relPatsList.add(new Object[]{"w", "z", BehaviorRelation.Causal, false, FrequencyChangeType.ABSorREL});
//		float relPats_relevances[]  = {1f, 1f, 1f, 1f, 1f, 1f};
//		
//		Map<List<Entry<PairRelation, RelationImpact>>, Float> CPMP = getChangePatternMatchingPermutations(relationImpactMap_sorted, relPatsList, relPats_relevances);
//		
//		return CPMP;
//		
//	}
//	
//	// Synchronize Removal: [-: 4)z->y(ABS), 5)y->u(ABSorREL), 6)w->z(ABSorREL)] [+: 1)y||z(ABS), 2)w->y(ABSorREL), 3)z->u(ABSorREL)]
//	private Map<List<Entry<PairRelation, RelationImpact>>, Float> getSynchronizeRemovalDriftRelevance(Map<PairRelation, RelationImpact> relationImpactMap_sorted)
//	{
//		
//		List<Object[]> relPatsList = new ArrayList<Object[]>();
//		relPatsList.add(new Object[]{"z", "y", BehaviorRelation.Causal, true, FrequencyChangeType.ABS});
//		relPatsList.add(new Object[]{"y", "u", BehaviorRelation.Causal, true, FrequencyChangeType.ABSorREL});
//		relPatsList.add(new Object[]{"w", "z", BehaviorRelation.Causal, true, FrequencyChangeType.ABSorREL});
//		relPatsList.add(new Object[]{"y", "z", BehaviorRelation.CONCURRENCY, false, FrequencyChangeType.ABS}); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
//		relPatsList.add(new Object[]{"w", "y", BehaviorRelation.Causal, false, FrequencyChangeType.ABSorREL});
//		relPatsList.add(new Object[]{"z", "u", BehaviorRelation.Causal, false, FrequencyChangeType.ABSorREL});
//		float relPats_relevances[]  = {1f, 1f, 1f, 1f, 1f, 1f};
//		
//		Map<List<Entry<PairRelation, RelationImpact>>, Float> CPMP = getChangePatternMatchingPermutations(relationImpactMap_sorted, relPatsList, relPats_relevances);
//		
//		return CPMP;
//		
//	}
//	
//	// Skip: [-: 1)x->y(REL), 2)y->z(REL)] [+: 3)x->z(ABS)]
//	private Map<List<Entry<PairRelation, RelationImpact>>, Float> getSkipDriftRelevance(Map<PairRelation, RelationImpact> relationImpactMap_sorted)
//	{
//		
//		List<Object[]> relPatsList = new ArrayList<Object[]>();
//		relPatsList.add(new Object[]{"x", "y", BehaviorRelation.Causal, true, FrequencyChangeType.REL}); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
//		relPatsList.add(new Object[]{"y", "z", BehaviorRelation.Causal, true, FrequencyChangeType.REL});
//		relPatsList.add(new Object[]{"x", "z", BehaviorRelation.Causal, false, FrequencyChangeType.ABS});
//		float relPats_relevances[]  = {1f, 1f, 1f};
//		
//		Map<List<Entry<PairRelation, RelationImpact>>, Float> CPMP = getChangePatternMatchingPermutations(relationImpactMap_sorted, relPatsList, relPats_relevances);
//		
//		return CPMP;
//		
//	}
//	
//	// Skip Inverse: [-: 3)x->z(ABS)] [+: 1)x->y(REL), 2)y->z(REL)]
//	private Map<List<Entry<PairRelation, RelationImpact>>, Float> getSkipInverseDriftRelevance(Map<PairRelation, RelationImpact> relationImpactMap_sorted)
//	{
//		
//		List<Object[]> relPatsList = new ArrayList<Object[]>();
//		
//		relPatsList.add(new Object[]{"x", "z", BehaviorRelation.Causal, true, FrequencyChangeType.ABS});
//		relPatsList.add(new Object[]{"x", "y", BehaviorRelation.Causal, false, FrequencyChangeType.REL}); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
//		relPatsList.add(new Object[]{"y", "z", BehaviorRelation.Causal, false, FrequencyChangeType.REL});
//		float relPats_relevances[]  = {1f, 1f, 1f};
//		
//		Map<List<Entry<PairRelation, RelationImpact>>, Float> CPMP = getChangePatternMatchingPermutations(relationImpactMap_sorted, relPatsList, relPats_relevances);
//		
//		return CPMP;
//		
//	}
//	
//	// Frequency: [-: 1)w->x(REL), 2)x->z(REL)] [+: 3)w->y(REL), 3)y->u(REL)]
//	private Map<List<Entry<PairRelation, RelationImpact>>, Float> getFrequencyDriftRelevance(Map<PairRelation, RelationImpact> relationImpactMap_sorted)
//	{
//		
//		List<Object[]> relPatsList = new ArrayList<Object[]>();
//		relPatsList.add(new Object[]{"w", "x", BehaviorRelation.Causal, true, FrequencyChangeType.REL}); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
//		relPatsList.add(new Object[]{"x", "z", BehaviorRelation.Causal, true, FrequencyChangeType.REL});
//		relPatsList.add(new Object[]{"w", "y", BehaviorRelation.Causal, false, FrequencyChangeType.REL});
//		relPatsList.add(new Object[]{"y", "u", BehaviorRelation.Causal, false, FrequencyChangeType.REL});
//		float relPats_relevances[]  = {1f, 1f, 1f, 1f};
//		
//		Map<List<Entry<PairRelation, RelationImpact>>, Float> CPMP = getChangePatternMatchingPermutations(relationImpactMap_sorted, relPatsList, relPats_relevances);
//		
//		return CPMP;
//		
//	}
		
	private Map<List<Entry<PairRelation, RelationImpact>>, InformationGain> getChangePatternMatchingPermutations(Multimap<PairRelation, 
			RelationImpact> relationImpactMap_sorted, 
			Map<Entry<FrequencyChangeType, FrequencyChange>, List<Entry<PairRelation, RelationImpact>>> relationsGrpdByType,
			ChangePattern cp)
	{
		
		List<List<Entry<PairRelation, RelationImpact>>> listOfRelationsLists = new ArrayList<>();
		
		for (int i = 0; i < cp.getRelationChanges().size(); i++) 
		{
			
			RelationChange relPat = cp.getRelationChanges().get(i);
			List<Entry<PairRelation, RelationImpact>> relationList = 
					retrieveRelationsWithAttributes(relationImpactMap_sorted, relationsGrpdByType, 
							relPat.getFreqChange(), relPat.getFreqChangeType(), relPat.getBehaviorRelation());
			listOfRelationsLists.add(relationList);
			
		}
		
		int numOfRlationPatterns = cp.getRelationChanges().size();
		float IDCG = cp.getRelationChanges().get(0).getImportance();
		for (int i = 1; i < numOfRlationPatterns; i++) 
		{
			
			IDCG += cp.getRelationChanges().get(i).getImportance()/Utils.log2(i+1);
			
		}
		List<List<Entry<PairRelation, RelationImpact>>> listOfPermutations = getListOfPermutations(listOfRelationsLists, cp);
//		listOfPermutations = filterUnconformingPermutations(listOfPermutations, relationChanges);
		Map<List<Entry<PairRelation, RelationImpact>>, InformationGain> permutaionsDCGs = calculatePermutationsDCGs(listOfPermutations, cp.getRelationChanges());
		Map<List<Entry<PairRelation, RelationImpact>>, InformationGain> permutaionsDCGs_sorted = sortByComparator_byDCG(permutaionsDCGs);
		
		
		Iterator<Entry<List<Entry<PairRelation, RelationImpact>>, InformationGain>> it = permutaionsDCGs_sorted.entrySet().iterator();
		
		while(it.hasNext())
		{
			
			Entry<List<Entry<PairRelation, RelationImpact>>, InformationGain> entry = it.next();
			entry.getValue().setnDCG(entry.getValue().getDCG() / IDCG);
			
		}
		
		return permutaionsDCGs_sorted;
		
	}
	
	private List<Entry<PairRelation, RelationImpact>> retrieveRelationsWithAttributes(Multimap<PairRelation, RelationImpact> relationImpactMap, 
			Map<Entry<FrequencyChangeType, FrequencyChange>, List<Entry<PairRelation, RelationImpact>>> relationsGrpdByType, 
			FrequencyChange freqChange, FrequencyChangeType freqChangeType, BehaviorRelation br)
	{
		
		List<Entry<PairRelation, RelationImpact>> filteredRelationImpactList = new ArrayList<>();
        Iterator<Entry<PairRelation, RelationImpact>> it = relationsGrpdByType.get(new AbstractMap.SimpleEntry(freqChangeType, freqChange)).iterator();
		while(it.hasNext())
		{
			
			Entry<PairRelation, RelationImpact> entry = it.next();
			PairRelation pr = entry.getKey();
			RelationImpact ri = entry.getValue();
			if(pr.getBehaviourRelation() == br)
			{
				filteredRelationImpactList.add(entry);
				if(br == BehaviorRelation.CONCURRENCY)
				{
					PairRelation pr_opposed = new PairRelation();
					pr_opposed.setPair(new Pair<XEventClass, XEventClass>(pr.getPair().getSecond(), pr.getPair().getFirst()));
					pr_opposed.setBehaviourRelation(br);
					Entry<PairRelation, RelationImpact> entry_opposed = new AbstractMap.SimpleEntry(pr_opposed, ri);
					filteredRelationImpactList.add(entry_opposed);
				}
			}
		}
		
		return filteredRelationImpactList;
		
	}
	
	private List<List<Entry<PairRelation, RelationImpact>>> getListOfPermutations(
			List<List<Entry<PairRelation, RelationImpact>>> listOfRelationsLists, 
			ChangePattern cp)
	{
		
		List<List<Entry<PairRelation, RelationImpact>>> listOfPermutations = new ArrayList<>();
		int numOfRelationsLists = listOfRelationsLists.size();
		
		if(withPartialMatching)
		{
			for (int i = 0; i < numOfRelationsLists; i++) 
				listOfRelationsLists.get(i).add(new AbstractMap.SimpleEntry<PairRelation, RelationImpact>(null, null));
		}
		
		long numOfPermutations = 1;
		for (int i = 0; i < numOfRelationsLists; i++) numOfPermutations *= listOfRelationsLists.get(i).size();
		
		int []indices = new int[numOfRelationsLists];
		for (int i = 0; i < indices.length; i++) indices[i] = 0;
		
//		System.out.println(numOfPermutations);
		Map<String, XEventClass> label_EventClass_Map = new HashMap<>();
		
		boolean isThereNewPerm = numOfPermutations > 0 ? true : false;
		List<Entry<PairRelation, RelationImpact>> permutation = new ArrayList<Entry<PairRelation,RelationImpact>>();
		boolean isPermutationValid = true;
		for (int j = 0; j < numOfRelationsLists && isThereNewPerm; j++) 
		{
			Entry<PairRelation, RelationImpact> relationEntry = listOfRelationsLists.get(j).get(indices[j]);
			RelationChange relationPattern = cp.getRelationChanges().get(j);
			isPermutationValid = true;
			
			if(!isItaConformingPartialPermutation(relationEntry, relationPattern, label_EventClass_Map))
			{
				isPermutationValid = false;
			}else
			{
				permutation.add(relationEntry);
				if(j == numOfRelationsLists - 1)
				{
					listOfPermutations.add(permutation);
					List<Entry<PairRelation, RelationImpact>> permutation2 = new ArrayList<Entry<PairRelation,RelationImpact>>();
					for(Entry<PairRelation,RelationImpact> entry : permutation) permutation2.add(entry);
					permutation = permutation2;
				}
			}
			
			if(!isPermutationValid || j == numOfRelationsLists - 1)
			{
				int shortenPermBy = 0;
				if(isPermutationValid) shortenPermBy++;
				for (int k = numOfRelationsLists - 1; k >= 0; k--)
				{
					if(k > j)
						indices[k] = 0;
					else
					{
						j--;
						if(++indices[k] == listOfRelationsLists.get(k).size())
						{
							if(k == 0)
								isThereNewPerm = false;
							else
							{
								indices[k] = 0;
								shortenPermBy++;
							}
						}else
							break;
					}
				}
				for (int i = 0; i < shortenPermBy; i++) 
				{
					permutation.remove(permutation.size() - 1);
					label_EventClass_Map = null;
					label_EventClass_Map = assignActivitiesToVariables(permutation, cp);
				}
			}
			
		}
		
//			if(isItaConformingPermutation(permutation, relationChanges))
//			listOfPermutations.add(permutation);
		
//			if(arePermutationEntriesDistinct(permutation))
//				listOfPermutations.add(permutation);
		
		return listOfPermutations;
		
	}
	
	
	/*private List<List<Entry<PairRelation, RelationImpact>>> getListOfAnyLengthSeqs(List<Entry<PairRelation, RelationImpact>> listOFCausalRelations)
	{
		List<List<Entry<PairRelation, RelationImpact>>> listOfAnyLengthSeqs = new ArrayList<>();
		
		int []indices = new int[listOFCausalRelations.size()];
		for (int i = 0; i < indices.length; i++) indices[i] = 0;
		
		boolean isThereNewSeq = listOFCausalRelations.size() > 0 ? true : false;
		boolean isSeqValid = true;
		List<Entry<PairRelation, RelationImpact>> seq = new ArrayList<Map.Entry<PairRelation,RelationImpact>>();
		for (int j = 0; j < listOFCausalRelations.size() && isThereNewSeq; j++) 
		{
			Entry<PairRelation, RelationImpact> relationEntry = listOFCausalRelations.get(indices[j]);
			isSeqValid = true;
			if(!isItaSeq(seq, relationEntry.getKey()))
			{
				isSeqValid = false;
			}else
			{
				seq.add(relationEntry);
				listOfAnyLengthSeqs.add(seq);
			}
			
			if(!isSeqValid || j == listOFCausalRelations.size()-1)
			{
				int shortenSeqBy = 0;
				for (int k = listOFCausalRelations.size() - 1; k >= 0; k--)
				{
					if(k > j)
						indices[k] = 0;
					else
					{
						j--;
						if(isSeqValid) shortenSeqBy++;
						if(++indices[k] == listOFCausalRelations.size())
						{
							shortenSeqBy++;
							if(k == 0)
								isThereNewSeq = false;
							else
								indices[k] = 0;
						}else
							break;
					}
				}
				for (int i = 0; i < shortenSeqBy; i++) 
				{
					seq.remove(seq.size() - 1);
				}
			}
			
		}
		
		return listOfAnyLengthSeqs;
	}
	*/
	/*private List<List<Entry<PairRelation, RelationImpact>>> getListOfPermutations_partialMatching(List<List<Entry<PairRelation, RelationImpact>>> listOfRelationsLists)
	{
		
		List<List<Entry<PairRelation, RelationImpact>>> listOfPermutations = new ArrayList<>();
		int numOfRelationsLists = listOfRelationsLists.size();
		
		for (int i = 0; i < numOfRelationsLists; i++) 
			listOfRelationsLists.get(i).add(new AbstractMap.SimpleEntry<PairRelation, RelationImpact>(null, null));
		
		int numOfPermutations = 1;
		for (int i = 0; i < numOfRelationsLists; i++) numOfPermutations *= listOfRelationsLists.get(i).size();
		
		int []indices = new int[numOfRelationsLists];
		for (int i = 0; i < indices.length; i++) indices[i] = 0;
		
		for (int i = 0; i < numOfPermutations; i++) 
		{
			
			List<Entry<PairRelation, RelationImpact>> permutaion = new ArrayList<Map.Entry<PairRelation,RelationImpact>>();
			for (int j = 0; j < numOfRelationsLists; j++) permutaion.add(listOfRelationsLists.get(j).get(indices[j]));
			
//			if(arePermutationEntriesDistinct(permutaion))
//				listOfPermutations.add(permutaion);
			
			for (int j = numOfRelationsLists - 1; j >= 0; j--) 
				if(++indices[j] == listOfRelationsLists.get(j).size())
					indices[j] = 0;
				else
					break;
			
		}
		
		return listOfPermutations;
		
	}
	*/
	
	private boolean arePermutationEntriesDistinct(List<Entry<PairRelation, RelationImpact>> permutaion)
	{
		
		for (int i = 0; i < permutaion.size(); i++) 
		{
			
			Entry<PairRelation, RelationImpact> curEntry = permutaion.get(i);
			if(curEntry.getKey() != null)
				for (int j = i - 1; j >= 0; j--) 
				{
					
					Entry<PairRelation, RelationImpact> prevEntry = permutaion.get(j);
					if(curEntry.getKey().equals(prevEntry.getKey()))
						return false;
					
				}
			
		}
		
		return true;
		
	}
	
	private List<List<Entry<PairRelation, RelationImpact>>> filterUnconformingPermutations(List<List<Entry<PairRelation, RelationImpact>>> listOfPermutations,
			List<RelationChange> relationChanges)
	{
		
		Map<String, XEventClass> label_EventClass_Map = new HashMap<String, XEventClass>();
		for (int i = 0; i < listOfPermutations.size(); i++) 
		{
			
			List<Entry<PairRelation, RelationImpact>> permutation = listOfPermutations.get(i);
			label_EventClass_Map.clear();
			for (int j = 0; j < permutation.size(); j++) 
			{
				
				PairRelation pr = permutation.get(j).getKey();
				if(pr != null)
				{
					RelationChange relationPattern = relationChanges.get(j);
//					boolean mustBeRemoved = false;
//					if((BehaviorRelation)relationPattern[2] == BehaviorRelation.CONCURRENCY ||
//							(BehaviorRelation)relationPattern[2] == BehaviorRelation.Length_Two_Loop ||
//							(BehaviorRelation)relationPattern[2] == BehaviorRelation.Independence)
//					{
//						
//						if(label_EventClass_Map.containsKey((String)relationPattern[0]))
//						{
//							if((label_EventClass_Map.get((String)relationPattern[0]).equals(pr.getPair().getFirst())))
//							{
//								if(label_EventClass_Map.containsKey((String)relationPattern[1]))
//								{
//									if(!label_EventClass_Map.get((String)relationPattern[1]).equals(pr.getPair().getSecond()))
//									{
//										mustBeRemoved = true;
//									}
//								}else
//									label_EventClass_Map.put((String)relationPattern[1], pr.getPair().getSecond());
//							}else if((label_EventClass_Map.get((String)relationPattern[0]).equals(pr.getPair().getSecond())))
//							{
//								if(label_EventClass_Map.containsKey((String)relationPattern[1]))
//								{
//									if(!label_EventClass_Map.get((String)relationPattern[1]).equals(pr.getPair().getFirst()))
//									{
//										mustBeRemoved = true;
//									}
//								}else
//								{
//									label_EventClass_Map.put((String)relationPattern[1], pr.getPair().getFirst());
//							
//								}
//							}
//						}else if(label_EventClass_Map.containsKey((String)relationPattern[1]))
//						{
//							if((label_EventClass_Map.get((String)relationPattern[1]).equals(pr.getPair().getFirst())))
//							{
//								label_EventClass_Map.put((String)relationPattern[0], pr.getPair().getSecond());
//							}else if((label_EventClass_Map.get((String)relationPattern[1]).equals(pr.getPair().getSecond())))
//							{
//								label_EventClass_Map.put((String)relationPattern[0], pr.getPair().getFirst());
//							}else
//							{
//								mustBeRemoved = true;
//							}
//						}else
//						{
//							label_EventClass_Map.put((String)relationPattern[0], pr.getPair().getFirst());
//							label_EventClass_Map.put((String)relationPattern[1], pr.getPair().getSecond());
//						}
//						
//					}
					
					
					if(label_EventClass_Map.containsKey(relationPattern.getFirstActivity()))
					{
						if((!label_EventClass_Map.get(relationPattern.getFirstActivity()).equals(pr.getPair().getFirst())))
						{
							listOfPermutations.remove(i);
							i--;
							break;
						}
					}else
						label_EventClass_Map.put(relationPattern.getFirstActivity(), pr.getPair().getFirst());
					
					if(label_EventClass_Map.containsKey(relationPattern.getSecondActivity()))
					{
						if(!label_EventClass_Map.get(relationPattern.getSecondActivity()).equals(pr.getPair().getSecond()))
						{
							
							listOfPermutations.remove(i);
							i--;
							break;
							
						}
					}else
						label_EventClass_Map.put(relationPattern.getSecondActivity(), pr.getPair().getSecond());
				}
				
			}
			
		}
		
		return listOfPermutations;
				
	}
	
	
	private boolean isItaConformingPermutation(List<Entry<PairRelation, RelationImpact>> permutation,
			List<RelationChange> relationChanges)
	{
		
		Map<String, XEventClass> label_EventClass_Map = new HashMap<String, XEventClass>();
		
			
		label_EventClass_Map.clear();
		for (int j = 0; j < permutation.size(); j++) 
		{
			
			PairRelation pr = permutation.get(j).getKey();
			if(pr != null)
			{
				RelationChange relationPattern = relationChanges.get(j);
				
				if(label_EventClass_Map.containsKey(relationPattern.getFirstActivity()))
				{
					if((!label_EventClass_Map.get(relationPattern.getFirstActivity()).equals(pr.getPair().getFirst())))
					{
						return false;
					}
				}else
					label_EventClass_Map.put(relationPattern.getFirstActivity(), pr.getPair().getFirst());
				
				if(label_EventClass_Map.containsKey(relationPattern.getSecondActivity()))
				{
					if(!label_EventClass_Map.get(relationPattern.getSecondActivity()).equals(pr.getPair().getSecond()))
					{
						
						return false;
						
					}
				}else
					label_EventClass_Map.put(relationPattern.getSecondActivity(), pr.getPair().getSecond());
			}
			
		}
			
		
		return true;
				
	}
	
	
	private boolean isItaSeq(
			List<Entry<PairRelation, RelationImpact>> seq, 
			PairRelation pr)
	{
		if(seq.size() == 0)
			return true;
		
		PairRelation lastPRinSeq = seq.get(seq.size() - 1).getKey();
		if(lastPRinSeq.getPair().getSecond().equals(pr.getPair().getFirst()))
			return true;
		
		return false;
				
	}
	
	private boolean isItaConformingPartialPermutation(Entry<PairRelation, RelationImpact> relationEntry,
			RelationChange relationPattern, Map<String, XEventClass> label_EventClass_Map)
	{
		
		PairRelation pr = relationEntry.getKey();
		if(pr != null)
		{
			
			if(label_EventClass_Map.containsKey(relationPattern.getFirstActivity()))
			{
				if((!label_EventClass_Map.get(relationPattern.getFirstActivity()).equals(pr.getPair().getFirst())))
				{
					return false;
				}
			}
			
			if(label_EventClass_Map.containsKey(relationPattern.getSecondActivity()))
			{
				if(!label_EventClass_Map.get(relationPattern.getSecondActivity()).equals(pr.getPair().getSecond()))
				{
					
					return false;
					
				}
			}
			
			label_EventClass_Map.put(relationPattern.getFirstActivity(), pr.getPair().getFirst());
			label_EventClass_Map.put(relationPattern.getSecondActivity(), pr.getPair().getSecond());
				
		}
			
		return true;
				
	}
	
	
	private Map<List<Entry<PairRelation, RelationImpact>>, InformationGain> calculatePermutationsDCGs(List<List<Entry<PairRelation, RelationImpact>>> listOfPermutations,
			List<RelationChange> relationChanges)
	{
		
		Map<List<Entry<PairRelation, RelationImpact>>, InformationGain> permutaionsDCGs = new HashMap<List<Entry<PairRelation,RelationImpact>>, InformationGain>();
		
		for (int i = 0; i < listOfPermutations.size(); i++) 
		{
			
			List<Entry<PairRelation, RelationImpact>> permutation = listOfPermutations.get(i);
			float DCG = 0;
			for (int j = 0; j < permutation.size(); j++) 
			{
				
				RelationImpact ri = permutation.get(j).getValue();
				if(ri != null)
				{
					
					if(ri.getRank() != 1)
						DCG += relationChanges.get(j).getImportance()/Utils.log2(ri.getRank());
					else
						DCG += relationChanges.get(j).getImportance();
					
				}
				
			}
			permutaionsDCGs.put(permutation, new InformationGain(DCG, 0f));
			
		}
		
		return permutaionsDCGs;
		
	}
	
	
	private Map<String, XEventClass> assignActivitiesToVariables(List<Entry<PairRelation, RelationImpact>> permutation,
			ChangePattern cp)
	{
		
		Map<String, XEventClass> label_EventClass_Map = new HashMap<String, XEventClass>();
			
		label_EventClass_Map.clear();
		for (int j = 0; j < permutation.size(); j++) 
		{
			
			PairRelation pr = permutation.get(j).getKey();
			if(pr != null)
			{
				RelationChange relationPattern = cp.getRelationChanges().get(j);
				
				if(!label_EventClass_Map.containsKey(relationPattern.getFirstActivity()))
					label_EventClass_Map.put(relationPattern.getFirstActivity(), pr.getPair().getFirst());
				
				if(!label_EventClass_Map.containsKey(relationPattern.getSecondActivity()))
					label_EventClass_Map.put(relationPattern.getSecondActivity(), pr.getPair().getSecond());
			}
			
		}
			
		
		return label_EventClass_Map;
				
	}
	

	private static Map<PairRelation, RelationImpact> sortByComparator_byCoefficient(Map<PairRelation, RelationImpact> unsortMap) {
		 
		// Convert Map to List
		List<Entry<PairRelation, RelationImpact>> list =
			new LinkedList<Entry<PairRelation, RelationImpact>>(unsortMap.entrySet());
 
		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<Entry<PairRelation, RelationImpact>>() {
			public int compare(Entry<PairRelation, RelationImpact> o1,
                                           Entry<PairRelation, RelationImpact> o2) {
				return (o2.getValue().getCoefficient()).compareTo(o1.getValue().getCoefficient());
			}
		});
 
		// Convert sorted map back to a Map
		Map<PairRelation, RelationImpact> sortedMap = new LinkedHashMap<PairRelation, RelationImpact>();
		for (Iterator<Entry<PairRelation, RelationImpact>> it = list.iterator(); it.hasNext();) {
			Entry<PairRelation, RelationImpact> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
	
	private static Map<PairRelation, RelationImpact> sortByComparator_byTestStat(Map<PairRelation, RelationImpact> unsortMap) {
		 
		// Convert Map to List
		List<Entry<PairRelation, RelationImpact>> list =
			new LinkedList<Entry<PairRelation, RelationImpact>>(unsortMap.entrySet());
 
		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<Entry<PairRelation, RelationImpact>>() {
			public int compare(Entry<PairRelation, RelationImpact> o1,
                                           Entry<PairRelation, RelationImpact> o2) {
				return (o2.getValue().getTestStatistic()).compareTo(o1.getValue().getTestStatistic());
			}
		});
 
		// Convert sorted map back to a Map
		Map<PairRelation, RelationImpact> sortedMap = new LinkedHashMap<PairRelation, RelationImpact>();
		for (Iterator<Entry<PairRelation, RelationImpact>> it = list.iterator(); it.hasNext();) {
			Entry<PairRelation, RelationImpact> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
	
	private static Map<PairRelation, RelationImpact> sortByComparator_byRelChangeMag(Map<PairRelation, RelationImpact> unsortMap) {
		 
		// Convert Map to List
		List<Entry<PairRelation, RelationImpact>> list =
			new LinkedList<Entry<PairRelation, RelationImpact>>(unsortMap.entrySet());
 
		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<Entry<PairRelation, RelationImpact>>() {
			public int compare(Entry<PairRelation, RelationImpact> o1,
                                           Entry<PairRelation, RelationImpact> o2) {
				return (o2.getValue().getRelChangeMag()).compareTo(o1.getValue().getRelChangeMag());
			}
		});
 
		// Convert sorted map back to a Map
		Map<PairRelation, RelationImpact> sortedMap = new LinkedHashMap<PairRelation, RelationImpact>();
		for (Iterator<Entry<PairRelation, RelationImpact>> it = list.iterator(); it.hasNext();) {
			Entry<PairRelation, RelationImpact> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
	
	private static Map<List<Entry<PairRelation, RelationImpact>>, InformationGain> sortByComparator_byDCG(Map<List<Entry<PairRelation, RelationImpact>>, InformationGain> unsortMap) {
		 
		// Convert Map to List
		List<Entry<List<Entry<PairRelation, RelationImpact>>, InformationGain>> list =
			new LinkedList<Entry<List<Entry<PairRelation, RelationImpact>>, InformationGain>>(unsortMap.entrySet());
 
		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<Entry<List<Entry<PairRelation, RelationImpact>>, InformationGain>>() {
			public int compare(Entry<List<Entry<PairRelation, RelationImpact>>, InformationGain> o1,
                                           Entry<List<Entry<PairRelation, RelationImpact>>, InformationGain> o2) {
				return o2.getValue().getDCG().compareTo(o1.getValue().getDCG());
			}
		});
 
		// Convert sorted map back to a Map
		Map<List<Entry<PairRelation, RelationImpact>>, InformationGain> sortedMap = new LinkedHashMap<List<Entry<PairRelation, RelationImpact>>, InformationGain>();
		for (Iterator<Entry<List<Entry<PairRelation, RelationImpact>>, InformationGain>> it = list.iterator(); it.hasNext();) {
			Entry<List<Entry<PairRelation, RelationImpact>>, InformationGain> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
	
	private static Map<String, Float> sortByComparator_byDCG2(Map<String, Float> unsortMap) {
		 
		// Convert Map to List
		List<Entry<String, Float>> list =
			new LinkedList<Entry<String, Float>>(unsortMap.entrySet());
 
		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<Entry<String, Float>>() {
			public int compare(Entry<String, Float> o1,
                                           Entry<String, Float> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});
 
		// Convert sorted map back to a Map
		Map<String, Float> sortedMap = new LinkedHashMap<String, Float>();
		for (Iterator<Entry<String, Float>> it = list.iterator(); it.hasNext();) {
			Entry<String, Float> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
	
	private static Multimap<String, Entry<List<Entry<PairRelation, RelationImpact>>, InformationGain>> sortByComparator_byDCG3(Multimap<String, Entry<List<Entry<PairRelation, RelationImpact>>, InformationGain>> unsortMap) {
		 
		// Convert Map to List
		List<Entry<String, Entry<List<Entry<PairRelation, RelationImpact>>, InformationGain>>> list =
			new LinkedList<Entry<String, Entry<List<Entry<PairRelation, RelationImpact>>, InformationGain>>>(unsortMap.entries());
 
		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<Entry<String, Entry<List<Entry<PairRelation, RelationImpact>>, InformationGain>>>() {
			public int compare(Entry<String, Entry<List<Entry<PairRelation, RelationImpact>>, InformationGain>> o1,
					Entry<String, Entry<List<Entry<PairRelation, RelationImpact>>, InformationGain>> o2) {
				return o2.getValue().getValue().getDCG().compareTo(o1.getValue().getValue().getDCG());
			}
		});
 
		// Convert sorted map back to a Map
		Multimap<String, Entry<List<Entry<PairRelation, RelationImpact>>, InformationGain>> sortedMap = LinkedListMultimap.create();
		for (Iterator<Entry<String, Entry<List<Entry<PairRelation, RelationImpact>>, InformationGain>>> it = list.iterator(); it.hasNext();) {
			Entry<String, Entry<List<Entry<PairRelation, RelationImpact>>, InformationGain>> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		
		return sortedMap;
	}
	
	private static Multimap<String, Entry<List<Entry<PairRelation, RelationImpact>>, InformationGain>> sortByComparator_byNDCG3(Multimap<String, Entry<List<Entry<PairRelation, RelationImpact>>, InformationGain>> unsortMap) {
		 
		// Convert Map to List
		List<Entry<String, Entry<List<Entry<PairRelation, RelationImpact>>, InformationGain>>> list =
			new LinkedList<Entry<String, Entry<List<Entry<PairRelation, RelationImpact>>, InformationGain>>>(unsortMap.entries());
 
		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<Entry<String, Entry<List<Entry<PairRelation, RelationImpact>>, InformationGain>>>() {
			public int compare(Entry<String, Entry<List<Entry<PairRelation, RelationImpact>>, InformationGain>> o1,
					Entry<String, Entry<List<Entry<PairRelation, RelationImpact>>, InformationGain>> o2) {
				return o2.getValue().getValue().getnDCG().compareTo(o1.getValue().getValue().getnDCG());
			}
		});
 
		// Convert sorted map back to a Map
		Multimap<String, Entry<List<Entry<PairRelation, RelationImpact>>, InformationGain>> sortedMap = LinkedListMultimap.create();
		for (Iterator<Entry<String, Entry<List<Entry<PairRelation, RelationImpact>>, InformationGain>>> it = list.iterator(); it.hasNext();) {
			Entry<String, Entry<List<Entry<PairRelation, RelationImpact>>, InformationGain>> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		
		return sortedMap;
	}
	
	private void buildDirectFollowRelationFrequency(XLog subLog, HashMap<String, Integer> Relation_Freq)
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
	

	private void testGoodnessOfKSPT(Multimap<PairRelation, RelationImpact> relationImpactMap_sorted, 
			String goodnessFile)
	{
		
		long t1 = System.currentTimeMillis();
		
		Map<String, Float> changePattern_nDCG_Map = new HashMap<String, Float>();
		
		Map<Entry<FrequencyChangeType, FrequencyChange>, List<Entry<PairRelation, RelationImpact>>> relationsGrpdByType = new HashMap<>();
		groupRelatinsByType(relationImpactMap_sorted, relationsGrpdByType);
		
		Entry<List<Entry<PairRelation, RelationImpact>>, InformationGain> topPerm = null;
        
		if(goodnessOfKSPTfile.contains("SerialRemoval"))
		{
			topPerm = getChangePatternMatchingPerms(relationImpactMap_sorted, relationsGrpdByType, changePatternsMap.get("serialInsert")).entrySet().iterator().next();
	        changePattern_nDCG_Map.put("serialInsert", topPerm.getValue().getnDCG());
	        
	        topPerm = getChangePatternMatchingPerms(relationImpactMap_sorted, relationsGrpdByType, changePatternsMap.get("serialRemoval")).entrySet().iterator().next();
	        changePattern_nDCG_Map.put("serialRemoval", topPerm.getValue().getnDCG());
		}
        
		if(goodnessOfKSPTfile.contains("ParallelRemoval"))
		{
			topPerm = getChangePatternMatchingPerms(relationImpactMap_sorted, relationsGrpdByType, changePatternsMap.get("ParallelInsert")).entrySet().iterator().next();
	        changePattern_nDCG_Map.put("ParallelInsert", topPerm.getValue().getnDCG());
	        
	        topPerm = getChangePatternMatchingPerms(relationImpactMap_sorted, relationsGrpdByType, changePatternsMap.get("ParallelRemoval")).entrySet().iterator().next();
	        changePattern_nDCG_Map.put("ParallelRemoval", topPerm.getValue().getnDCG());
		}
        
		if(goodnessOfKSPTfile.contains("ConditionalRemoval"))
		{
			topPerm = getChangePatternMatchingPerms(relationImpactMap_sorted, relationsGrpdByType, changePatternsMap.get("ConditionalInsert")).entrySet().iterator().next();
	        changePattern_nDCG_Map.put("ConditionalInsert", topPerm.getValue().getnDCG());
	        
	        topPerm = getChangePatternMatchingPerms(relationImpactMap_sorted, relationsGrpdByType, changePatternsMap.get("ConditionalRemoval")).entrySet().iterator().next();
	        changePattern_nDCG_Map.put("ConditionalRemoval", topPerm.getValue().getnDCG());
		}
        
		if(goodnessOfKSPTfile.contains("Substitute"))
		{
			topPerm = getChangePatternMatchingPerms(relationImpactMap_sorted, relationsGrpdByType, changePatternsMap.get("Substitute")).entrySet().iterator().next();
	        changePattern_nDCG_Map.put("Substitute", topPerm.getValue().getnDCG());
		}
        
		if(goodnessOfKSPTfile.contains("Swap"))
		{
			topPerm = getChangePatternMatchingPerms(relationImpactMap_sorted, relationsGrpdByType, changePatternsMap.get("Swap")).entrySet().iterator().next();
	        changePattern_nDCG_Map.put("Swap", topPerm.getValue().getnDCG());
		}
        
		if(goodnessOfKSPTfile.contains("ConditionalMove"))
		{
			 topPerm = getChangePatternMatchingPerms(relationImpactMap_sorted, relationsGrpdByType, changePatternsMap.get("ConditionalMove")).entrySet().iterator().next();
		        changePattern_nDCG_Map.put("ConditionalMove", topPerm.getValue().getnDCG());
		        
		        topPerm = getChangePatternMatchingPerms(relationImpactMap_sorted, relationsGrpdByType, changePatternsMap.get("ConditionalMoveInverse")).entrySet().iterator().next();
		        changePattern_nDCG_Map.put("ConditionalMoveInverse", topPerm.getValue().getnDCG());
		}
        
		if(goodnessOfKSPTfile.contains("ParallelMove"))
		{
			topPerm = getChangePatternMatchingPerms(relationImpactMap_sorted, relationsGrpdByType, changePatternsMap.get("ParallelMove")).entrySet().iterator().next();
	        changePattern_nDCG_Map.put("ParallelMove", topPerm.getValue().getnDCG());
	        
	        topPerm = getChangePatternMatchingPerms(relationImpactMap_sorted, relationsGrpdByType, changePatternsMap.get("ParallelMoveInverse")).entrySet().iterator().next();
	        changePattern_nDCG_Map.put("ParallelMoveInverse", topPerm.getValue().getnDCG());
		}
        
		if(goodnessOfKSPTfile.contains("SerialMove"))
		{
			topPerm = getChangePatternMatchingPerms(relationImpactMap_sorted, relationsGrpdByType, changePatternsMap.get("SerialMove")).entrySet().iterator().next();
	        changePattern_nDCG_Map.put("SerialMove", topPerm.getValue().getnDCG());
		}
        
		if(goodnessOfKSPTfile.contains("ConditionalToSequence"))
		{
			topPerm = getChangePatternMatchingPerms(relationImpactMap_sorted, relationsGrpdByType, changePatternsMap.get("ConditionalToSequence")).entrySet().iterator().next();
	        changePattern_nDCG_Map.put("ConditionalToSequence", topPerm.getValue().getnDCG());
	        
	        topPerm = getChangePatternMatchingPerms(relationImpactMap_sorted, relationsGrpdByType, changePatternsMap.get("SequenceToConditional")).entrySet().iterator().next();
	        changePattern_nDCG_Map.put("SequenceToConditional", topPerm.getValue().getnDCG());
		}
        
		if(goodnessOfKSPTfile.contains("ParallelToSequence"))
		{
			topPerm = getChangePatternMatchingPerms(relationImpactMap_sorted, relationsGrpdByType, changePatternsMap.get("ParallelToSequence")).entrySet().iterator().next();
	        changePattern_nDCG_Map.put("ParallelToSequence", topPerm.getValue().getnDCG());
	        
	        topPerm = getChangePatternMatchingPerms(relationImpactMap_sorted, relationsGrpdByType, changePatternsMap.get("SequenceToParallel")).entrySet().iterator().next();
	        changePattern_nDCG_Map.put("SequenceToParallel", topPerm.getValue().getnDCG());
		}
        
		if(goodnessOfKSPTfile.contains("LoopLengthOne"))
		{
			topPerm = getChangePatternMatchingPerms(relationImpactMap_sorted, relationsGrpdByType, changePatternsMap.get("LoopLengthOne")).entrySet().iterator().next();
	        changePattern_nDCG_Map.put("LoopLengthOne", topPerm.getValue().getnDCG());
	        
	        topPerm = getChangePatternMatchingPerms(relationImpactMap_sorted, relationsGrpdByType, changePatternsMap.get("LoopLengthOneInverse")).entrySet().iterator().next();
	        changePattern_nDCG_Map.put("LoopLengthOneInverse", topPerm.getValue().getnDCG());
		}
        
		if(goodnessOfKSPTfile.contains("LoopLengthTwo"))
		{
			topPerm = getChangePatternMatchingPerms(relationImpactMap_sorted, relationsGrpdByType, changePatternsMap.get("LoopLengthTwo")).entrySet().iterator().next();
	        changePattern_nDCG_Map.put("LoopLengthTwo", topPerm.getValue().getnDCG());
	        
	        topPerm = getChangePatternMatchingPerms(relationImpactMap_sorted, relationsGrpdByType, changePatternsMap.get("LoopLengthTwoInverse")).entrySet().iterator().next();
	        changePattern_nDCG_Map.put("LoopLengthTwoInverse", topPerm.getValue().getnDCG());
		}
        
		if(goodnessOfKSPTfile.contains("LoopLengthThree"))
		{
			topPerm = getChangePatternMatchingPerms(relationImpactMap_sorted, relationsGrpdByType, changePatternsMap.get("LoopLengthThree")).entrySet().iterator().next();
	        changePattern_nDCG_Map.put("LoopLengthThree", topPerm.getValue().getnDCG());
	        
	        topPerm = getChangePatternMatchingPerms(relationImpactMap_sorted, relationsGrpdByType, changePatternsMap.get("LoopLengthThreeInverse")).entrySet().iterator().next();
	        changePattern_nDCG_Map.put("LoopLengthThreeInverse", topPerm.getValue().getnDCG());
		}
        
		if(goodnessOfKSPTfile.contains("Synchronize"))
		{
			topPerm = getChangePatternMatchingPerms(relationImpactMap_sorted, relationsGrpdByType, changePatternsMap.get("SynchronizeAdd")).entrySet().iterator().next();
	        changePattern_nDCG_Map.put("SynchronizeAdd", topPerm.getValue().getnDCG());
	        
	        topPerm = getChangePatternMatchingPerms(relationImpactMap_sorted, relationsGrpdByType, changePatternsMap.get("SynchronizeRemoval")).entrySet().iterator().next();
	        changePattern_nDCG_Map.put("SynchronizeRemoval", topPerm.getValue().getnDCG());
		}
        
		if(goodnessOfKSPTfile.contains("Skip"))
		{
			topPerm = getChangePatternMatchingPerms(relationImpactMap_sorted, relationsGrpdByType, changePatternsMap.get("Skip")).entrySet().iterator().next();
	        changePattern_nDCG_Map.put("Skip", topPerm.getValue().getnDCG());
	        
	        topPerm = getChangePatternMatchingPerms(relationImpactMap_sorted, relationsGrpdByType, changePatternsMap.get("SkipInverse")).entrySet().iterator().next();
	        changePattern_nDCG_Map.put("SkipInverse", topPerm.getValue().getnDCG());
		}
        
		if(goodnessOfKSPTfile.contains("Frequency"))
		{
			topPerm = getChangePatternMatchingPerms(relationImpactMap_sorted, relationsGrpdByType, changePatternsMap.get("Frequency")).entrySet().iterator().next();
	        changePattern_nDCG_Map.put("Frequency", topPerm.getValue().getnDCG());
		}
        
        
        
        Map<String, Float> changePattern_DCG_Map_sorted = sortByComparator_byDCG2(changePattern_nDCG_Map);
        
        Entry<String, Float> topChangePattern = changePattern_DCG_Map_sorted.entrySet().iterator().next();
        System.out.println(topChangePattern.getKey() + ": " + topChangePattern.getValue());
        
        System.out.println("Change pattern discovery took(ms): " + (System.currentTimeMillis() - t1));
        System.out.println();
        


        try {
        	FileWriter fw = null;
            if(resetFilesGoodness)
            {
            	fw = new FileWriter(goodnessFile, false);
            	resetFilesGoodness = false;
            }else
            	fw = new FileWriter(goodnessFile, true);
            
            fw.write(topChangePattern.getKey() + "," + topChangePattern.getValue() + "\n");
			
			fw.close();
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}
	

	private String getChangePatternShortName(String discoveredChange)
	{
		
		if(discoveredChange.contains("serialInsert") 
				|| discoveredChange.contains("serialRemoval"))
		{
			return "SerialRemoval";
		}
        
		if(discoveredChange.contains("ParallelInsert") 
				|| discoveredChange.contains("ParallelRemoval"))
		{
			return "ParallelRemoval";
		}
        
		if(discoveredChange.contains("ConditionalInsert") 
				|| discoveredChange.contains("ConditionalRemoval"))
		{
	        return "ConditionalRemoval";
		}
		
		if(discoveredChange.contains("Duplicate") )
		{
			return "Duplicate";
		}
        
		if(discoveredChange.contains("Substitute") )
		{
			return "Substitute";
		}
        
		if(discoveredChange.contains("Swap"))
		{
			return "Swap";
		}
        
		if(discoveredChange.contains("ConditionalMove") 
				|| discoveredChange.contains("ConditionalMoveInverse"))
		{
			return "ConditionalMove";
		}
        
		if(discoveredChange.contains("ParallelMove") 
				|| discoveredChange.contains("ParallelMoveInverse"))
		{
			return "ParallelMove";
		}
        
		if(discoveredChange.contains("SerialMove"))
		{
			return "SerialMove";
		}
        
		if(discoveredChange.contains("ConditionalToSequence") 
				|| discoveredChange.contains("SequenceToConditional"))
		{
			return "ConditionalToSequence";
		}
        
		if(discoveredChange.contains("ParallelToSequence") 
				|| discoveredChange.contains("SequenceToParallel"))
		{
			return "ParallelToSequence";
		}
        
		if(discoveredChange.contains("LoopLengthOne") 
				|| discoveredChange.contains("LoopLengthOneInverse"))
		{
			return "LoopLengthOne";
		}
        
		if(discoveredChange.contains("LoopLengthTwo") 
				|| discoveredChange.contains("LoopLengthTwoInverse"))
		{
			return "LoopLengthTwo";
		}
        
		if(discoveredChange.contains("LoopLengthMore") 
				|| discoveredChange.contains("LoopLengthMoreInverse"))
		{
			return "LoopLengthMore";
		}
        
		if(discoveredChange.contains("SynchronizeAdd") 
				|| discoveredChange.contains("SynchronizeRemoval"))
		{
			return "Synchronize";
		}
        
		if(discoveredChange.contains("Skip") 
				|| discoveredChange.contains("SkipInverse"))
		{
			return "Skip";
		}
        
		if(discoveredChange.contains("Frequency"))
		{
			return "Frequency";
		}
        
		return null;
        
	}
	
	private boolean containsRelationList(List<Entry<PairRelation, RelationImpact>> list1, List<Entry<PairRelation, RelationImpact>> list2)
	{
		
		for (int i = 0; i < list2.size(); i++) 
		{
			if(!list1.contains(list2.get(i)))
				return false;
		}
		
		return true;
		
	}
	
	private boolean isItASubPermutation(List<Entry<PairRelation, RelationImpact>> permutation, 
    		Multimap<String, Entry<List<Entry<PairRelation, RelationImpact>>, InformationGain>> allPermsMap_sortedByDCG)
	{
		
		Iterator<Entry<String, Entry<List<Entry<PairRelation, RelationImpact>>, InformationGain>>> perms_it = allPermsMap_sortedByDCG.entries().iterator();
		Entry<String, Entry<List<Entry<PairRelation, RelationImpact>>, InformationGain>> nextChangePattern;
		while(perms_it.hasNext())
		{
			nextChangePattern = perms_it.next();
			List<Entry<PairRelation, RelationImpact>> perm = nextChangePattern.getValue().getKey();
			if(permutation.equals(perm))
				return false;
			if(containsRelationList(perm, permutation))
				return true;
		}
		
		return false;
		
	}
	
	private boolean haveSharedRelations(Set<Entry<PairRelation, RelationImpact>> set1, List<Entry<PairRelation, RelationImpact>> list2)
	{
		
		for (int i = 0; i < list2.size(); i++) 
		{
			if(set1.contains(list2.get(i)))
				return true;
		}
		
		return false;
		
	}
	
	private boolean isItTruePositive(String changePaternShortName, List<String> mainActivityNamesList, Map<String, XEventClass> label_EventClass_Map)
	{
		
		String driftStatement;
		
		if(changePaternShortName.compareToIgnoreCase("SerialRemoval") == 0)
		{
			return mainActivityNamesList.contains(label_EventClass_Map.get("y").toString());
		}
        
		if(changePaternShortName.compareToIgnoreCase("ParallelRemoval") == 0)
		{
			return mainActivityNamesList.contains(label_EventClass_Map.get("y").toString());
		}
        
		if(changePaternShortName.compareToIgnoreCase("ConditionalRemoval") == 0)
		{
			return mainActivityNamesList.contains(label_EventClass_Map.get("y").toString());
		}
				
		if(changePaternShortName.compareToIgnoreCase("Duplicate") == 0)
		{
			return mainActivityNamesList.contains(label_EventClass_Map.get("y").toString());
		}
        
		if(changePaternShortName.compareToIgnoreCase("Substitute") == 0)
		{
			return mainActivityNamesList.contains(label_EventClass_Map.get("y").toString());
		}
        
		if(changePaternShortName.compareToIgnoreCase("Swap") == 0)
		{
			return mainActivityNamesList.contains(label_EventClass_Map.get("x").toString());
		}
        
		if(changePaternShortName.compareToIgnoreCase("ConditionalMove") == 0)
		{
			return mainActivityNamesList.contains(label_EventClass_Map.get("w").toString());
		}
		        
		if(changePaternShortName.compareToIgnoreCase("ParallelMove") == 0)
		{
			return mainActivityNamesList.contains(label_EventClass_Map.get("w").toString());
		}
		        
		if(changePaternShortName.compareToIgnoreCase("SerialMove") == 0)
		{
			return mainActivityNamesList.contains(label_EventClass_Map.get("x").toString());
		}
        
		if(changePaternShortName.compareToIgnoreCase("ConditionalToSequence") == 0)
		{
			return mainActivityNamesList.contains(label_EventClass_Map.get("x").toString());
		}
        
		if(changePaternShortName.compareToIgnoreCase("ParallelToSequence") == 0)
		{
			return mainActivityNamesList.contains(label_EventClass_Map.get("x").toString());
		}
		        
		if(changePaternShortName.compareToIgnoreCase("LoopLengthOne") == 0)
		{
			return mainActivityNamesList.contains(label_EventClass_Map.get("x").toString());
		}
		        
		if(changePaternShortName.compareToIgnoreCase("LoopLengthTwo") == 0)
		{
			return mainActivityNamesList.contains(label_EventClass_Map.get("x").toString());
		}
		
		if(changePaternShortName.compareToIgnoreCase("LoopLengthMore") == 0)
		{
			return mainActivityNamesList.contains(label_EventClass_Map.get("x").toString());
		}
		
		if(changePaternShortName.compareToIgnoreCase("Synchronize") == 0)
		{
			return mainActivityNamesList.contains(label_EventClass_Map.get("z").toString());
		}
		
		if(changePaternShortName.compareToIgnoreCase("Skip") == 0)
		{
			return mainActivityNamesList.contains(label_EventClass_Map.get("y").toString());
		}
        
		if(changePaternShortName.compareToIgnoreCase("Frequency") == 0)
		{
			return mainActivityNamesList.contains(label_EventClass_Map.get("w").toString());
		}
		
		return false;
	}
	
	private void populateChangePatternMap(Map<String, ChangePattern> changePatternsMap)
	{
		
		// Serial insert: [-: 1)x->z(ABS)] [+: 2)x->y(ABS), 3)y->z(ABS)]
		List<RelationChange> relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("x", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f)); // (Lactivity, Ractivity, BR, isMinus, iSabsChange, importance)
		relPatsList.add(new RelationChange("x", "y", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f)); 
		relPatsList.add(new RelationChange("y", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		changePatternsMap.put("serialInsert", new ChangePattern("serialInsert", relPatsList));
		
		// Serial removal: [-: 1)x->y(ABS), 2)y->z(ABS)] [+: 3)x->z(ABS)]
		relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("x", "y", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f)); // (Lactivity, Ractivity, BR, isMinus, iSabsChange, importance)
		relPatsList.add(new RelationChange("y", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f)); 
		relPatsList.add(new RelationChange("x", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		changePatternsMap.put("serialRemoval", new ChangePattern("serialRemoval", relPatsList));
		
		// Parallel insert: [-: 1)w->x(REL), 2)x->z(REL)] [+: 3)w->y(ABS), 4)y->z(ABS), 5)x||y(ABS)]
		relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("w", "x", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.REL, 1f)); // (Lactivity, Ractivity, BR, isMinus, iSabsChange, importance)
		relPatsList.add(new RelationChange("x", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.REL, 1f)); 
		relPatsList.add(new RelationChange("w", "y", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("y", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("x", "y", BehaviorRelation.CONCURRENCY, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		changePatternsMap.put("ParallelInsert", new ChangePattern("ParallelInsert", relPatsList));
		
		// Parallel removal: [-: 3)w->y(ABS), 4)y->z(ABS), 5)x||y(ABS)] [+: 1)w->x(REL), 2)x->z(REL)]
		relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("w", "y", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("y", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("x", "y", BehaviorRelation.CONCURRENCY, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "x", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.REL, 1f)); // (Lactivity, Ractivity, BR, isMinus, iSabsChange, importance)
		relPatsList.add(new RelationChange("x", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.REL, 1f)); 
		changePatternsMap.put("ParallelRemoval", new ChangePattern("ParallelRemoval", relPatsList));
		
		// Conditional insert: [-: 1)x->u(REL)] [+: 2)x->y(ABS), 3)y->z(ABS)]
		relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("x", "u", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.REL, 1f));
		relPatsList.add(new RelationChange("x", "y", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("y", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		changePatternsMap.put("ConditionalInsert", new ChangePattern("ConditionalInsert", relPatsList));
		
		// Conditional removal: [-: 2)x->y(ABS), 3)y->z(ABS)] [+: 1)x->u(REL)]
		relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("x", "y", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("y", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("x", "u", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.REL, 1f));
		changePatternsMap.put("ConditionalRemoval", new ChangePattern("ConditionalRemoval", relPatsList));
		
		// Substitute: [-: 1)x->y(ABS), 2)y->z(ABS)] [+: 3)x->w(ABS), 3)w->z(ABS)]
		relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("x", "y", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("y", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("x", "w", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		changePatternsMap.put("Substitute", new ChangePattern("Substitute", relPatsList));
		
		// Swap: [-: 1)t->u(ABS), 2)u->v(ABS), 3)w->x(ABS), 4)x->y(ABS)] [+: 5)t->x(ABS), 6)x->v(ABS), 7)w->u(ABS), 8)u->y(ABS)]
		relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("t", "u", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u", "v", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("t", "x", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("x", "v", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "x", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("x", "y", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u", "y", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		changePatternsMap.put("Swap", new ChangePattern("Swap", relPatsList));
		
		// Conditional Move: [-: 1)v->w(ABS), 2)w->x(ABS), 3)u->y(REL), 4)y->z(REL)] [+: 5)v->x(ABS), 6)u->w(ABS), 7)w->z(ABS)]
		relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("v", "w", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "x", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("v", "x", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u", "w", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u", "y", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.REL, 1f));
//		relPatsList.add(new RelationChange("y", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.REL, 1f));
		relPatsList.add(new RelationChange("w", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		changePatternsMap.put("ConditionalMove", new ChangePattern("ConditionalMove", relPatsList));
		
		// Conditional Move Inverse: [-: 5)v->x(ABS), 6)u->w(ABS), 7)w->z(ABS)] [+: 1)v->w(ABS), 2)w->x(ABS), 3)u->y(REL), 4)y->z(REL)]
		relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("v", "x", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("v", "w", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "x", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u", "w", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u", "y", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.REL, 1f));
//		relPatsList.add(new RelationChange("y", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.REL, 1f));
		changePatternsMap.put("ConditionalMoveInverse", new ChangePattern("ConditionalMoveInverse", relPatsList));
		
		// Parallel Move: [-: 1)v->w(ABS), 2)w->x(ABS), 3)u->y(REL), 4)y->z(REL)] [+: 5)v->x(ABS), 6)u->w(ABS), 7)w->z(ABS), 8)w||y(ABS)]
		relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("v", "w", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "x", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("v", "x", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u", "w", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u", "y", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.REL, 1f));
		relPatsList.add(new RelationChange("w", "y", BehaviorRelation.CONCURRENCY, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("y", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.REL, 1f));
		relPatsList.add(new RelationChange("w", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		changePatternsMap.put("ParallelMove", new ChangePattern("ParallelMove", relPatsList));
		
		// Parallel Move Inverse: [-: 5)v->x(ABS), 6)u->w(ABS), 7)w->z(ABS), 8)w||y(ABS)] [+: 1)v->w(ABS), 2)w->x(ABS), 3)u->y(REL), 4)y->z(REL)]
		relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("v", "x", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u", "w", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("v", "w", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "x", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "y", BehaviorRelation.CONCURRENCY, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u", "y", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.REL, 1f));
		relPatsList.add(new RelationChange("y", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.REL, 1f));
		changePatternsMap.put("ParallelMoveInverse", new ChangePattern("ParallelMoveInverse", relPatsList));
		
		// Serial Move: [-: 1)w->x(ABS), 2)x->y(ABS), 3)t->v(ABS)] [+: 4)w->y(ABS), 5)t->x(ABS), 6)x->v(ABS)]
		relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("w", "x", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("x", "y", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "y", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("t", "v", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("t", "x", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("x", "v", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		changePatternsMap.put("SerialMove", new ChangePattern("SerialMove", relPatsList));
		
		// Conditional to sequence: [-: 1)w->y(ABS), 2)x->z(ABS)] [+: 3)x->y(ABS), 4)w->x(REL), 5)y->z(REL)]
		relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("w", "y", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("x", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "x", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.REL, 1f));
		relPatsList.add(new RelationChange("x", "y", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("y", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.REL, 1f));
		changePatternsMap.put("ConditionalToSequence", new ChangePattern("ConditionalToSequence", relPatsList));
		
		// Conditional to sequence3: [-: 1)w->y(ABS), 2)x->z(ABS)] [+: 3)x->y(ABS), 4)w->x(REL), 5)y->z(REL)]
		relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("w", "y", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u1", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("x", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "x", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.REL, 1f));
		relPatsList.add(new RelationChange("x", "u1", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "y", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("y", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.REL, 1f));
		changePatternsMap.put("ConditionalToSequence3", new ChangePattern("ConditionalToSequence3", relPatsList));
		
		// Conditional to sequence4: [-: 1)w->y(ABS), 2)x->z(ABS)] [+: 3)x->y(ABS), 4)w->x(REL), 5)y->z(REL)]
		relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("w", "y", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u1", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u2", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("x", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u2", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "x", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.REL, 1f));
		relPatsList.add(new RelationChange("x", "u1", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "u2", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u2", "y", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("y", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.REL, 1f));
		changePatternsMap.put("ConditionalToSequence4", new ChangePattern("ConditionalToSequence4", relPatsList));

		// Conditional to sequence5: [-: 1)w->y(ABS), 2)x->z(ABS)] [+: 3)x->y(ABS), 4)w->x(REL), 5)y->z(REL)]
		relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("w", "y", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u1", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u2", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u3", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("x", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u2", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u3", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "x", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.REL, 1f));
		relPatsList.add(new RelationChange("x", "u1", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "u2", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u2", "u3", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u3", "y", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("y", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.REL, 1f));
		changePatternsMap.put("ConditionalToSequence5", new ChangePattern("ConditionalToSequence5", relPatsList));
		/*
		// Conditional to sequence6: [-: 1)w->y(ABS), 2)x->z(ABS)] [+: 3)x->y(ABS), 4)w->x(REL), 5)y->z(REL)]
		relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("w", "y", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u1", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u2", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u3", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u4", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("x", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u2", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u3", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u4", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "x", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.REL, 1f));
		relPatsList.add(new RelationChange("x", "u1", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "u2", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u2", "u3", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u3", "u4", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u4", "y", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("y", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.REL, 1f));
		changePatternsMap.put("ConditionalToSequence6", new ChangePattern("ConditionalToSequence6", relPatsList));

		// Conditional to sequence7: [-: 1)w->y(ABS), 2)x->z(ABS)] [+: 3)x->y(ABS), 4)w->x(REL), 5)y->z(REL)]
		relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("w", "y", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u1", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u2", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u3", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u4", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u5", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("x", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u2", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u3", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u4", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u5", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "x", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.REL, 1f));
		relPatsList.add(new RelationChange("x", "u1", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "u2", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u2", "u3", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u3", "u4", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u4", "u5", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u5", "y", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("y", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.REL, 1f));
		changePatternsMap.put("ConditionalToSequence7", new ChangePattern("ConditionalToSequence7", relPatsList));

		// Conditional to sequence8: [-: 1)w->y(ABS), 2)x->z(ABS)] [+: 3)x->y(ABS), 4)w->x(REL), 5)y->z(REL)]
		relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("w", "y", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u1", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u2", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u3", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u4", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u5", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u6", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("x", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u2", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u3", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u4", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u5", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u6", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "x", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.REL, 1f));
		relPatsList.add(new RelationChange("x", "u1", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "u2", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u2", "u3", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u3", "u4", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u4", "u5", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u5", "u6", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u6", "y", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("y", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.REL, 1f));
		changePatternsMap.put("ConditionalToSequence8", new ChangePattern("ConditionalToSequence8", relPatsList));
								*/
		// Sequence to Conditional: [-: 3)x->y(ABS), 4)w->x(REL), 5)y->z(REL)] [+: 1)w->y(ABS), 2)x->z(ABS)]
		relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("x", "y", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "x", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.REL, 1f));
		relPatsList.add(new RelationChange("w", "y", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("y", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.REL, 1f));
		relPatsList.add(new RelationChange("x", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		changePatternsMap.put("SequenceToConditional", new ChangePattern("SequenceToConditional", relPatsList));

		// Sequence to Conditional3: [-: 3)x->y(ABS), 4)w->x(REL), 5)y->z(REL)] [+: 1)w->y(ABS), 2)x->z(ABS)]
		relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("x", "u1", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "y", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "x", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.REL, 1f));
		relPatsList.add(new RelationChange("w", "y", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u1", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("y", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.REL, 1f));
		relPatsList.add(new RelationChange("x", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		changePatternsMap.put("SequenceToConditional3", new ChangePattern("SequenceToConditional3", relPatsList));

		// Sequence to Conditional4: [-: 3)x->y(ABS), 4)w->x(REL), 5)y->z(REL)] [+: 1)w->y(ABS), 2)x->z(ABS)]
		relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("x", "u1", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "u2", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u2", "y", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "x", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.REL, 1f));
		relPatsList.add(new RelationChange("w", "y", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u1", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u2", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("y", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.REL, 1f));
		relPatsList.add(new RelationChange("x", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u2", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		changePatternsMap.put("SequenceToConditional4", new ChangePattern("SequenceToConditional4", relPatsList));

		// Sequence to Conditional5: [-: 3)x->y(ABS), 4)w->x(REL), 5)y->z(REL)] [+: 1)w->y(ABS), 2)x->z(ABS)]
		relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("x", "u1", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "u2", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u2", "u3", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u3", "y", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "x", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.REL, 1f));
		relPatsList.add(new RelationChange("w", "y", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u1", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u2", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u3", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("y", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.REL, 1f));
		relPatsList.add(new RelationChange("x", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u2", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u3", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		changePatternsMap.put("SequenceToConditional5", new ChangePattern("SequenceToConditional5", relPatsList));
		/*
		// Sequence to Conditional6: [-: 3)x->y(ABS), 4)w->x(REL), 5)y->z(REL)] [+: 1)w->y(ABS), 2)x->z(ABS)]
		relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("x", "u1", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "u2", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u2", "u3", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u3", "u4", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u4", "y", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "x", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.REL, 1f));
		relPatsList.add(new RelationChange("w", "y", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u1", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u2", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u3", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u4", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("y", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.REL, 1f));
		relPatsList.add(new RelationChange("x", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u2", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u3", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u4", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		changePatternsMap.put("SequenceToConditional6", new ChangePattern("SequenceToConditional6", relPatsList));

		// Sequence to Conditional7: [-: 3)x->y(ABS), 4)w->x(REL), 5)y->z(REL)] [+: 1)w->y(ABS), 2)x->z(ABS)]
		relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("x", "u1", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "u2", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u2", "u3", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u3", "u4", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u4", "u5", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u5", "y", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "x", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.REL, 1f));
		relPatsList.add(new RelationChange("w", "y", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u1", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u2", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u3", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u4", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u5", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("y", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.REL, 1f));
		relPatsList.add(new RelationChange("x", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u2", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u3", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u4", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u5", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		changePatternsMap.put("SequenceToConditional7", new ChangePattern("SequenceToConditional7", relPatsList));

		// Sequence to Conditional8: [-: 3)x->y(ABS), 4)w->x(REL), 5)y->z(REL)] [+: 1)w->y(ABS), 2)x->z(ABS)]
		relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("x", "u1", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "u2", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u2", "u3", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u3", "u4", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u4", "u5", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u5", "u6", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u6", "y", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "x", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.REL, 1f));
		relPatsList.add(new RelationChange("w", "y", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u1", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u2", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u3", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u4", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u5", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u6", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("y", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.REL, 1f));
		relPatsList.add(new RelationChange("x", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u2", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u3", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u4", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u5", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u6", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		changePatternsMap.put("SequenceToConditional8", new ChangePattern("SequenceToConditional8", relPatsList));
		*/
		// Parallel to sequence: [-: 1)w->y(ABS), 2)x->z(ABS), 3)x||y(ABS)] [+: 4)x->y(REL), 4)w->x(REL), 5)y->z(REL)]
		relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("x", "y", BehaviorRelation.CONCURRENCY, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("x", "y", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "y", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "x", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.REL, 1f));
		relPatsList.add(new RelationChange("x", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("y", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.REL, 1f));
		changePatternsMap.put("ParallelToSequence", new ChangePattern("ParallelToSequence", relPatsList));

		// Parallel to sequence3: [-: 1)w->y(ABS), 2)x->z(ABS), 3)x||y(ABS)] [+: 4)x->y(REL), 4)w->x(REL), 5)y->z(REL)]
		relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("x", "u1", BehaviorRelation.CONCURRENCY, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("x", "u1", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "y", BehaviorRelation.CONCURRENCY, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "y", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "y", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u1", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "x", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.REL, 1f));
		relPatsList.add(new RelationChange("x", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("y", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.REL, 1f));
		changePatternsMap.put("ParallelToSequence3", new ChangePattern("ParallelToSequence3", relPatsList));

		// Parallel to sequence4: [-: 1)w->y(ABS), 2)x->z(ABS), 3)x||y(ABS)] [+: 4)x->y(REL), 4)w->x(REL), 5)y->z(REL)]
		relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("x", "u1", BehaviorRelation.CONCURRENCY, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("x", "u1", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "u2", BehaviorRelation.CONCURRENCY, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "u2", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u2", "y", BehaviorRelation.CONCURRENCY, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u2", "y", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "y", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u1", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u2", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "x", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.REL, 1f));
		relPatsList.add(new RelationChange("x", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u2", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("y", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.REL, 1f));
		changePatternsMap.put("ParallelToSequence4", new ChangePattern("ParallelToSequence4", relPatsList));

		// Parallel to sequence5: [-: 1)w->y(ABS), 2)x->z(ABS), 3)x||y(ABS)] [+: 4)x->y(REL), 4)w->x(REL), 5)y->z(REL)]
		relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("x", "u1", BehaviorRelation.CONCURRENCY, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("x", "u1", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "u2", BehaviorRelation.CONCURRENCY, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "u2", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u2", "u3", BehaviorRelation.CONCURRENCY, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u2", "u3", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u3", "y", BehaviorRelation.CONCURRENCY, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u3", "y", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "y", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u1", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u2", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u3", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "x", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.REL, 1f));
		relPatsList.add(new RelationChange("x", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u2", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u3", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("y", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.REL, 1f));
		changePatternsMap.put("ParallelToSequence5", new ChangePattern("ParallelToSequence5", relPatsList));
		/*
		// Parallel to sequence6: [-: 1)w->y(ABS), 2)x->z(ABS), 3)x||y(ABS)] [+: 4)x->y(REL), 4)w->x(REL), 5)y->z(REL)]
		relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("x", "u1", BehaviorRelation.CONCURRENCY, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("x", "u1", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "u2", BehaviorRelation.CONCURRENCY, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "u2", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u2", "u3", BehaviorRelation.CONCURRENCY, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u2", "u3", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u3", "u4", BehaviorRelation.CONCURRENCY, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u3", "u4", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u4", "y", BehaviorRelation.CONCURRENCY, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u4", "y", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "y", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u1", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u2", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u3", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u4", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "x", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.REL, 1f));
		relPatsList.add(new RelationChange("x", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u2", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u3", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u4", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("y", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.REL, 1f));
		changePatternsMap.put("ParallelToSequence6", new ChangePattern("ParallelToSequence6", relPatsList));

		// Parallel to sequence7: [-: 1)w->y(ABS), 2)x->z(ABS), 3)x||y(ABS)] [+: 4)x->y(REL), 4)w->x(REL), 5)y->z(REL)]
		relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("x", "u1", BehaviorRelation.CONCURRENCY, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("x", "u1", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "u2", BehaviorRelation.CONCURRENCY, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "u2", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u2", "u3", BehaviorRelation.CONCURRENCY, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u2", "u3", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u3", "u4", BehaviorRelation.CONCURRENCY, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u3", "u4", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u4", "u5", BehaviorRelation.CONCURRENCY, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u4", "u5", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u5", "y", BehaviorRelation.CONCURRENCY, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u5", "y", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "y", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u1", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u2", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u3", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u4", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u5", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "x", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.REL, 1f));
		relPatsList.add(new RelationChange("x", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u2", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u3", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u4", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u5", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("y", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.REL, 1f));
		changePatternsMap.put("ParallelToSequence7", new ChangePattern("ParallelToSequence7", relPatsList));

		// Parallel to sequence8: [-: 1)w->y(ABS), 2)x->z(ABS), 3)x||y(ABS)] [+: 4)x->y(REL), 4)w->x(REL), 5)y->z(REL)]
		relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("x", "u1", BehaviorRelation.CONCURRENCY, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("x", "u1", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "u2", BehaviorRelation.CONCURRENCY, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "u2", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u2", "u3", BehaviorRelation.CONCURRENCY, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u2", "u3", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u3", "u4", BehaviorRelation.CONCURRENCY, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u3", "u4", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u4", "u5", BehaviorRelation.CONCURRENCY, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u4", "u5", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u5", "u6", BehaviorRelation.CONCURRENCY, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u5", "u6", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u6", "y", BehaviorRelation.CONCURRENCY, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u6", "y", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "y", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u1", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u2", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u3", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u4", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u5", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u6", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "x", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.REL, 1f));
		relPatsList.add(new RelationChange("x", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u2", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u3", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u4", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u5", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u6", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("y", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.REL, 1f));
		changePatternsMap.put("ParallelToSequence8", new ChangePattern("ParallelToSequence8", relPatsList));
		*/
		// Sequence to Parallel: [-: 4)x->y(ABS), 4)w->x(REL), 5)y->z(REL)] [+: 1)w->y(ABS), 2)x->z(ABS), 3)x||y(ABS)]
		relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("x", "y", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("x", "y", BehaviorRelation.CONCURRENCY, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "x", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.REL, 1f));
		relPatsList.add(new RelationChange("w", "y", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("y", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.REL, 1f));
		relPatsList.add(new RelationChange("x", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		changePatternsMap.put("SequenceToParallel", new ChangePattern("SequenceToParallel", relPatsList));

		// Sequence to Parallel3: [-: 4)x->y(ABS), 4)w->x(REL), 5)y->z(REL)] [+: 1)w->y(ABS), 2)x->z(ABS), 3)x||y(ABS)]
		relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("x", "u1", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("x", "u1", BehaviorRelation.CONCURRENCY, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "y", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "y", BehaviorRelation.CONCURRENCY, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "x", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.REL, 1f));
		relPatsList.add(new RelationChange("w", "y", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u1", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("y", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.REL, 1f));
		relPatsList.add(new RelationChange("x", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		changePatternsMap.put("SequenceToParallel3", new ChangePattern("SequenceToParallel3", relPatsList));

		// Sequence to Parallel4: [-: 4)x->y(ABS), 4)w->x(REL), 5)y->z(REL)] [+: 1)w->y(ABS), 2)x->z(ABS), 3)x||y(ABS)]
		relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("x", "u1", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("x", "u1", BehaviorRelation.CONCURRENCY, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "u2", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "u2", BehaviorRelation.CONCURRENCY, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u2", "y", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u2", "y", BehaviorRelation.CONCURRENCY, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "x", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.REL, 1f));
		relPatsList.add(new RelationChange("w", "y", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u1", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u2", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("y", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.REL, 1f));
		relPatsList.add(new RelationChange("x", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u2", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		changePatternsMap.put("SequenceToParallel4", new ChangePattern("SequenceToParallel4", relPatsList));

		// Sequence to Parallel5: [-: 4)x->y(ABS), 4)w->x(REL), 5)y->z(REL)] [+: 1)w->y(ABS), 2)x->z(ABS), 3)x||y(ABS)]
		relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("x", "u1", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("x", "u1", BehaviorRelation.CONCURRENCY, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "u2", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "u2", BehaviorRelation.CONCURRENCY, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u2", "u3", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u2", "u3", BehaviorRelation.CONCURRENCY, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u3", "y", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u3", "y", BehaviorRelation.CONCURRENCY, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "x", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.REL, 1f));
		relPatsList.add(new RelationChange("w", "y", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u1", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u2", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u3", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("y", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.REL, 1f));
		relPatsList.add(new RelationChange("x", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u2", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u3", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		changePatternsMap.put("SequenceToParallel5", new ChangePattern("SequenceToParallel5", relPatsList));
		/*
		// Sequence to Parallel6: [-: 4)x->y(ABS), 4)w->x(REL), 5)y->z(REL)] [+: 1)w->y(ABS), 2)x->z(ABS), 3)x||y(ABS)]
		relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("x", "u1", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("x", "u1", BehaviorRelation.CONCURRENCY, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "u2", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "u2", BehaviorRelation.CONCURRENCY, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u2", "u3", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u2", "u3", BehaviorRelation.CONCURRENCY, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u3", "u4", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u3", "u4", BehaviorRelation.CONCURRENCY, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u4", "y", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u4", "y", BehaviorRelation.CONCURRENCY, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "x", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.REL, 1f));
		relPatsList.add(new RelationChange("w", "y", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u1", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u2", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u3", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u4", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("y", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.REL, 1f));
		relPatsList.add(new RelationChange("x", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u2", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u3", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u4", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		changePatternsMap.put("SequenceToParallel6", new ChangePattern("SequenceToParallel6", relPatsList));

		// Sequence to Parallel7: [-: 4)x->y(ABS), 4)w->x(REL), 5)y->z(REL)] [+: 1)w->y(ABS), 2)x->z(ABS), 3)x||y(ABS)]
		relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("x", "u1", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("x", "u1", BehaviorRelation.CONCURRENCY, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "u2", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "u2", BehaviorRelation.CONCURRENCY, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u2", "u3", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u2", "u3", BehaviorRelation.CONCURRENCY, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u3", "u4", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u3", "u4", BehaviorRelation.CONCURRENCY, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u4", "u5", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u4", "u5", BehaviorRelation.CONCURRENCY, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u5", "y", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u5", "y", BehaviorRelation.CONCURRENCY, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "x", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.REL, 1f));
		relPatsList.add(new RelationChange("w", "y", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u1", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u2", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u3", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u4", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u5", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("y", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.REL, 1f));
		relPatsList.add(new RelationChange("x", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u2", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u3", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u4", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u5", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		changePatternsMap.put("SequenceToParallel7", new ChangePattern("SequenceToParallel7", relPatsList));

		// Sequence to Parallel8: [-: 4)x->y(ABS), 4)w->x(REL), 5)y->z(REL)] [+: 1)w->y(ABS), 2)x->z(ABS), 3)x||y(ABS)]
		relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("x", "u1", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("x", "u1", BehaviorRelation.CONCURRENCY, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "u2", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "u2", BehaviorRelation.CONCURRENCY, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u2", "u3", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u2", "u3", BehaviorRelation.CONCURRENCY, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u3", "u4", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u3", "u4", BehaviorRelation.CONCURRENCY, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u4", "u5", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u4", "u5", BehaviorRelation.CONCURRENCY, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u5", "u6", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u5", "u6", BehaviorRelation.CONCURRENCY, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u6", "y", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u6", "y", BehaviorRelation.CONCURRENCY, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "x", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.REL, 1f));
		relPatsList.add(new RelationChange("w", "y", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u1", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u2", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u3", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u4", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u5", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("w", "u6", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("y", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.REL, 1f));
		relPatsList.add(new RelationChange("x", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u1", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u2", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u3", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u4", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u5", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("u6", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		changePatternsMap.put("SequenceToParallel8", new ChangePattern("SequenceToParallel8", relPatsList));
		*/
		// Loop length one: [+: 1)x@x(ABS)]
		relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("x", "x", BehaviorRelation.Length_One_Loop, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f)); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
		changePatternsMap.put("LoopLengthOne", new ChangePattern("LoopLengthOne", relPatsList));
		
		// Loop length one inverse: [-: 1)x@x(ABS)]
		relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("x", "x", BehaviorRelation.Length_One_Loop, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f)); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
		changePatternsMap.put("LoopLengthOneInverse", new ChangePattern("LoopLengthOneInverse", relPatsList));
		
		// Loop length two: [+: 1)x@y(ABS)]
		relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("x", "y", BehaviorRelation.Length_Two_Loop, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f)); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
		changePatternsMap.put("LoopLengthTwo", new ChangePattern("LoopLengthTwo", relPatsList));
		
		// Loop length two Inverse: [-: 1)x@y(ABS)]
		relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("x", "y", BehaviorRelation.Length_Two_Loop, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f)); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
		changePatternsMap.put("LoopLengthTwoInverse", new ChangePattern("LoopLengthTwoInverse", relPatsList));
		
		// Loop length three: [+: 1)x->y(REL), 2)y->z(REL), 3)z->x(ABS)]
		relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("x", "u1", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.REL, 1f)); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
		relPatsList.add(new RelationChange("u1", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.REL, 1f)); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
		relPatsList.add(new RelationChange("z", "x", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f)); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
		changePatternsMap.put("LoopLengthMore3", new ChangePattern("LoopLengthMore3", relPatsList));
		
		// Loop length three: [-: 1)x->y(REL), 2)y->z(REL), 3)z->x(ABS)]
		relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("x", "u1", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.REL, 1f)); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
		relPatsList.add(new RelationChange("u1", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.REL, 1f)); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
		relPatsList.add(new RelationChange("z", "x", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f)); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
		changePatternsMap.put("LoopLengthMoreInverse3", new ChangePattern("LoopLengthMoreInverse3", relPatsList));
		
		// Loop length four: [+: 1)x->y(REL), 2)y->z(REL), 3)z->x(ABS)]
		relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("x", "u1", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.REL, 1f)); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
		relPatsList.add(new RelationChange("u1", "u2", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.REL, 1f)); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
		relPatsList.add(new RelationChange("u2", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.REL, 1f)); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
		relPatsList.add(new RelationChange("z", "x", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f)); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
		changePatternsMap.put("LoopLengthMore4", new ChangePattern("LoopLengthMore4", relPatsList));
		
		// Loop length four: [-: 1)x->y(REL), 2)y->z(REL), 3)z->x(ABS)]
		relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("x", "u1", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.REL, 1f)); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
		relPatsList.add(new RelationChange("u1", "u2", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.REL, 1f)); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
		relPatsList.add(new RelationChange("u2", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.REL, 1f)); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
		relPatsList.add(new RelationChange("z", "x", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f)); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
		changePatternsMap.put("LoopLengthMoreInverse4", new ChangePattern("LoopLengthMoreInverse4", relPatsList));

		// Loop length five: [+: 1)x->y(REL), 2)y->z(REL), 3)z->x(ABS)]
		relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("x", "u1", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.REL, 1f)); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
		relPatsList.add(new RelationChange("u1", "u2", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.REL, 1f)); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
		relPatsList.add(new RelationChange("u2", "u3", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.REL, 1f)); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
		relPatsList.add(new RelationChange("u3", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.REL, 1f)); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
		relPatsList.add(new RelationChange("z", "x", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f)); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
		changePatternsMap.put("LoopLengthMore5", new ChangePattern("LoopLengthMore5", relPatsList));
		
		// Loop length five: [-: 1)x->y(REL), 2)y->z(REL), 3)z->x(ABS)]
		relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("x", "u1", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.REL, 1f)); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
		relPatsList.add(new RelationChange("u1", "u2", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.REL, 1f)); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
		relPatsList.add(new RelationChange("u2", "u3", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.REL, 1f)); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
		relPatsList.add(new RelationChange("u3", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.REL, 1f)); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
		relPatsList.add(new RelationChange("z", "x", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f)); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
		changePatternsMap.put("LoopLengthMoreInverse5", new ChangePattern("LoopLengthMoreInverse5", relPatsList));
		
		// Synchronize Add: [-: 1)y||z(ABS), 2)w->y(ABSorREL), 3)z->u(ABSorREL)] [+: 4)z->y(ABS), 5)y->u(ABSorREL), 6)w->z(ABSorREL)]
		relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("y", "z", BehaviorRelation.CONCURRENCY, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("z", "y", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("z", "u", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABSorREL, 1f));
		relPatsList.add(new RelationChange("y", "u", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABSorREL, 1f));
		relPatsList.add(new RelationChange("w", "y", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABSorREL, 1f));
		relPatsList.add(new RelationChange("w", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABSorREL, 1f));
		changePatternsMap.put("SynchronizeAdd", new ChangePattern("SynchronizeAdd", relPatsList));
		
		// Synchronize Removal: [-: 4)z->y(ABS), 5)y->u(ABSorREL), 6)w->z(ABSorREL)] [+: 1)y||z(ABS), 2)w->y(ABSorREL), 3)z->u(ABSorREL)]
		relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("z", "y", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("y", "z", BehaviorRelation.CONCURRENCY, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f));
		relPatsList.add(new RelationChange("z", "u", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABSorREL, 1f));
		relPatsList.add(new RelationChange("y", "u", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABSorREL, 1f));
		relPatsList.add(new RelationChange("w", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABSorREL, 1f));
		relPatsList.add(new RelationChange("w", "y", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABSorREL, 1f));
		changePatternsMap.put("SynchronizeRemoval", new ChangePattern("SynchronizeRemoval", relPatsList));
		
		// Skip: [-: 1)x->y(REL), 2)y->z(REL)] [+: 3)x->z(ABS)]
		relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("x", "y", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.REL, 1f)); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
		relPatsList.add(new RelationChange("y", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.REL, 1f)); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
		relPatsList.add(new RelationChange("x", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.ABS, 1f)); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
		changePatternsMap.put("Skip", new ChangePattern("Skip", relPatsList));
		
		// Skip Inverse: [-: 3)x->z(ABS)] [+: 1)x->y(REL), 2)y->z(REL)]
		relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("x", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.ABS, 1f)); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
		relPatsList.add(new RelationChange("x", "y", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.REL, 1f)); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
		relPatsList.add(new RelationChange("y", "z", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.REL, 1f)); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
		changePatternsMap.put("SkipInverse", new ChangePattern("SkipInverse", relPatsList));
		
		// Frequency: [-: 1)w->x(REL), 2)x->z(REL)] [+: 3)w->y(REL), 3)y->u(REL)]
		relPatsList = new ArrayList<RelationChange>();
		relPatsList.add(new RelationChange("w", "x", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.REL, 1f)); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
		relPatsList.add(new RelationChange("x", "z", BehaviorRelation.Causal, FrequencyChange.Decrease, FrequencyChangeType.REL, 1f)); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
		relPatsList.add(new RelationChange("w", "y", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.REL, 1f)); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
		relPatsList.add(new RelationChange("y", "u", BehaviorRelation.Causal, FrequencyChange.Increase, FrequencyChangeType.REL, 1f)); // (Lactivity, Ractivity, BR, isMinus, iSabsChange)
		changePatternsMap.put("Frequency", new ChangePattern("Frequency", relPatsList));
				
	}
	
	
	private String createDriftStatement(String changePaternName, Map<String, XEventClass> label_EventClass_Map)
	{
		
		String driftStatement;
		
		if(changePaternName.compareToIgnoreCase("serialInsert") == 0)
		{
			return "Serial insert: after drift, activity \"" + label_EventClass_Map.get("y") + "\" is inserted between activities \"" + 
					label_EventClass_Map.get("x") + "\" and \"" + label_EventClass_Map.get("z") +"\".";
		}
		
		if(changePaternName.compareToIgnoreCase("serialRemoval") == 0)
		{
			return "Serial removal: after drift, activity \"" + label_EventClass_Map.get("y") + "\" is removed from between activities \"" + 
					label_EventClass_Map.get("x") + "\" and \"" + label_EventClass_Map.get("z") +"\".";
		}
        
		if(changePaternName.compareToIgnoreCase("ParallelInsert") == 0)
		{
			return "Parallel insert: after drift, activity \"" + label_EventClass_Map.get("y") + "\" is inserted between activities \"" + 
					label_EventClass_Map.get("w") + "\" and \"" + label_EventClass_Map.get("z") + 
					"\", and in parallel with activity \"" + label_EventClass_Map.get("x") + ".";
		}
		
		if(changePaternName.compareToIgnoreCase("ParallelRemoval") == 0)
		{
			return "Parallel removal: after drift, activity \"" + label_EventClass_Map.get("y") + "\" which was in parallel with activity \"" + label_EventClass_Map.get("x") + 
					"\" is removed from between activities \"" + label_EventClass_Map.get("w") + "\" and \"" + 
					label_EventClass_Map.get("z") + ".";
		}
        
		if(changePaternName.compareToIgnoreCase("ConditionalInsert") == 0)
		{
			return "Conditional insert: after drift, activity \"" + label_EventClass_Map.get("y") + "\" is inserted between activities \"" + 
					label_EventClass_Map.get("x") + "\" and \"" + label_EventClass_Map.get("z") + 
					"\" in a conditional branch.";
		}
		
		if(changePaternName.compareToIgnoreCase("ConditionalRemoval") == 0)
		{
			return "Conditional removal: after drift, activity \"" + label_EventClass_Map.get("y") + "\" which was in a conditional branch between activities \"" + 
					label_EventClass_Map.get("x") + "\" and \"" + label_EventClass_Map.get("z") + 
					"\" is removed.";
		}
		
		if(changePaternName.compareToIgnoreCase("Duplicate") == 0)
		{
			return "Duplicate: after drift, activity \"" + label_EventClass_Map.get("y") + "\" is duplicated between activities \"" + 
					label_EventClass_Map.get("x") + "\" and \"" + label_EventClass_Map.get("z") +"\".";
		}
        
		if(changePaternName.compareToIgnoreCase("Substitute") == 0)
		{
			return "Substitute: after drift, activity \"" + label_EventClass_Map.get("y") + "\" which was between activities \"" + label_EventClass_Map.get("x")
					+ "\" and \"" +  label_EventClass_Map.get("z") +"\" is substituted by activity \"" + 
					label_EventClass_Map.get("w") + "\".";
		}
        
		if(changePaternName.compareToIgnoreCase("Swap") == 0)
		{
			return "Swap: after drift, activity \"" + label_EventClass_Map.get("u") + "\" is swapped with activity \"" + 
					label_EventClass_Map.get("x") + "\".";
		}
        
		if(changePaternName.compareToIgnoreCase("ConditionalMove") == 0)
		{
			return "Conditional move: after drift, activity \"" + label_EventClass_Map.get("w") + "\" which was between activities \"" +
					label_EventClass_Map.get("v") + "\" and \"" + label_EventClass_Map.get("x") + 
					"\" is moved to a conditional branch between activities \"" + 
					label_EventClass_Map.get("u") + "\" and \"" + label_EventClass_Map.get("z") + 
					"\".";
		}
		
		if(changePaternName.compareToIgnoreCase("ConditionalMoveInverse") == 0)
		{
			return "Conditional move (inverse): after drift, activity \"" + label_EventClass_Map.get("w") + "\", which was in a conditional branch between activities \""+ 
					label_EventClass_Map.get("u") + "\" and \"" + label_EventClass_Map.get("z") + "\", is moved to between activities \"" + 
					label_EventClass_Map.get("v") + "\" and \"" + label_EventClass_Map.get("x") + "\".";
		}
        
		if(changePaternName.compareToIgnoreCase("ParallelMove") == 0)
		{
			return "Parallel move: after drift, activity \"" + label_EventClass_Map.get("w") + "\", which was between activities \"" + 
					label_EventClass_Map.get("v") + "\" and \"" + label_EventClass_Map.get("x") + "\", " +
					"is moved to between activities \"" + 
					label_EventClass_Map.get("u") + "\" and \"" + label_EventClass_Map.get("z") + 
					"\", and in parallel with activity \"" + label_EventClass_Map.get("y") + "\".";
		}
		
		if(changePaternName.compareToIgnoreCase("ParallelMoveInverse") == 0)
		{
			return "Parallel move (inverse): after drift, activity \"" + label_EventClass_Map.get("w") + "\", which was between activities \"" + 
					label_EventClass_Map.get("u") + "\" and \"" + label_EventClass_Map.get("z") + "\", and " +
					"in parallel with activity \""+ 
					label_EventClass_Map.get("y") + "\", is moved to between activities \"" + 
					label_EventClass_Map.get("v") + "\" and \"" + label_EventClass_Map.get("x") + "\".";
		}
        
		if(changePaternName.compareToIgnoreCase("SerialMove") == 0)
		{
			return "Serial move: after drift, activity \"" + label_EventClass_Map.get("x") + "\", which was between activities \"" + 
					label_EventClass_Map.get("w") + "\" and \"" + label_EventClass_Map.get("y") +
					"\" is moved to between activities \"" + 
					label_EventClass_Map.get("t") + "\" and \"" + label_EventClass_Map.get("v") + "\".";
		}
        
		if(changePaternName.contains("ConditionalToSequence"))
		{
			StringBuffer strBuf = new StringBuffer();
			for (int i = 1; i <= 6; i++) 
				if(label_EventClass_Map.containsKey(String.valueOf("u"+i)))
					strBuf.append("\"").append(label_EventClass_Map.get(String.valueOf("u"+i))).append("\", ");
			
			return "Conditional to sequence: after drift, activities \"" + label_EventClass_Map.get("x") + "\", " + strBuf.toString()
					+ "and \"" + 
					label_EventClass_Map.get("y") + "\" are sequential, while before drift they were conditional.";
		}
		
		if(changePaternName.contains("SequenceToConditional"))
		{
			StringBuffer strBuf = new StringBuffer();
			for (int i = 1; i <= 6; i++) 
				if(label_EventClass_Map.containsKey(String.valueOf("u"+i)))
					strBuf.append("\"").append(label_EventClass_Map.get(String.valueOf("u"+i))).append("\", ");
			
			return "sequence to conditional: after drift, activities \"" + label_EventClass_Map.get("x") + "\", " + strBuf.toString()
					+ "and \"" + 
					label_EventClass_Map.get("y") + "\" are conditional, while before drift they were sequential.";
		}
        
		if(changePaternName.contains("ParallelToSequence"))
		{
			StringBuffer strBuf = new StringBuffer();
			for (int i = 1; i <= 6; i++) 
				if(label_EventClass_Map.containsKey(String.valueOf("u"+i)))
					strBuf.append("\"").append(label_EventClass_Map.get(String.valueOf("u"+i))).append("\", ");
			
			return "Parallel to sequence: after drift, activities \"" + label_EventClass_Map.get("x") + "\", " + strBuf.toString()
					+ "and \"" + 
					label_EventClass_Map.get("y") + "\" are sequential, while before drift they were parallel.";
		}
		
		if(changePaternName.contains("SequenceToParallel"))
		{
			StringBuffer strBuf = new StringBuffer();
			for (int i = 1; i <= 6; i++) 
				if(label_EventClass_Map.containsKey(String.valueOf("u"+i)))
					strBuf.append("\"").append(label_EventClass_Map.get(String.valueOf("u"+i))).append("\", ");
			
			return "Sequence to parallel: after drift, activities \"" + label_EventClass_Map.get("x") + "\", " + strBuf.toString()
					+ "and \"" + 
					label_EventClass_Map.get("y") + "\" are parallel, but before drift they were sequential.";
		}
        
		if(changePaternName.compareToIgnoreCase("LoopLengthOne") == 0)
		{
			return "Loop (added): after drift, a loop is added over activity \"" + label_EventClass_Map.get("x") + "\".";
		}
		
		if(changePaternName.compareToIgnoreCase("LoopLengthOneInverse") == 0)
		{
			return "Loop (removed): before drift, there was a loop over activity \"" + label_EventClass_Map.get("x") + 
					"\" before drift, while after drift there is not.";
		}
        
		if(changePaternName.compareToIgnoreCase("LoopLengthTwo") == 0)
		{
			return "Loop (added): after drift, a loop is added over activities \"" + label_EventClass_Map.get("x") + "\" and \"" +
					 label_EventClass_Map.get("y") + "\".";
		}
		
		if(changePaternName.compareToIgnoreCase("LoopLengthTwoInverse") == 0)
		{
			return "Loop (removed): before drift, there was a loop over activities \"" + label_EventClass_Map.get("x") + "\" and \"" +
					 label_EventClass_Map.get("y") + "\", while after drift there is not.";
		}
        
		if(changePaternName.contains("LoopLengthMore") && !changePaternName.contains("Inverse"))
		{
			StringBuffer strBuf = new StringBuffer();
			for (int i = 1; i <= 3; i++) 
				if(label_EventClass_Map.containsKey(String.valueOf("u"+i)))
					strBuf.append("\"").append(label_EventClass_Map.get(String.valueOf("u"+i))).append("\", ");
			
			return "Loop (added): after drift, a loop is added over activities \"" + label_EventClass_Map.get("x") + "\", " + strBuf.toString()
					+ "and \"" + label_EventClass_Map.get("z") + "\".";
		}
		
		if(changePaternName.contains("LoopLengthMoreInverse"))
		{
			StringBuffer strBuf = new StringBuffer();
			for (int i = 1; i <= 3; i++) 
				if(label_EventClass_Map.containsKey(String.valueOf("u"+i)))
					strBuf.append("\"").append(label_EventClass_Map.get(String.valueOf("u"+i))).append("\", ");
			
			return "Loop (removed): before drift, there was a loop over activities \"" + label_EventClass_Map.get("x") + "\", " + strBuf.toString()
					+ "and \"" + label_EventClass_Map.get("z") + "\", while after drift there is not.";
		}
        
		if(changePaternName.compareToIgnoreCase("SynchronizeAdd") == 0)
		{
			return "Synchronization: after drift, activities \"" + label_EventClass_Map.get("z") + "\" and \"" + label_EventClass_Map.get("y") +
					"\" are synchronized, while before drift they were parallel.";
		}
		
		if(changePaternName.compareToIgnoreCase("SynchronizeRemoval") == 0)
		{
			return "Synchronization (inverse): after drift, synchronization between activities \"" + label_EventClass_Map.get("z") + "\" and \"" + label_EventClass_Map.get("y") +
					"\" is removed.";
		}
        
		if(changePaternName.compareToIgnoreCase("Skip") == 0)
		{
			return "Skip: after drift, activity \"" + label_EventClass_Map.get("y") + "\" is sometimes skipped, while before drift it was always executed.";
		}
		
		if(changePaternName.compareToIgnoreCase("SkipInverse") == 0)
		{
			return "Skip (inverse): after drift, activity \"" + label_EventClass_Map.get("y") + "\" is always executed, while before drift it was sometimes skipped.";
		}
        
		if(changePaternName.compareToIgnoreCase("Frequency") == 0)
		{
			return "Changing branching frequency: after activity \"" + label_EventClass_Map.get("w") + "\" branch of activities \"" + label_EventClass_Map.get("y") + "\" and \"" + label_EventClass_Map.get("u") + 
					"\" is more frequently executed after drift,"
					+ " while branch of activity \"" + label_EventClass_Map.get("x") + "\" and \"" + label_EventClass_Map.get("z") + "\" is less frequently executed.";
		}
		
		return "";
	}
	
	
	
}
