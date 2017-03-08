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
package org.apromore.prodrift.util;

import java.util.ArrayList;

import com.aliasi.classify.ScoredPrecisionRecallEvaluation;



//import java.util.ArrayList;
//
//import com.aliasi.classify.ScoredPrecisionRecallEvaluation;
//
//import ee.ut.eventstr.test.AlphaBasedPosetReaderTest;
//
//import java.lang.Math;


 
/**
 * Generates and saves a precision-recall curve. Uses a cross-validation
 * with NaiveBayes to make the curve.
 *
 * @author FracPete
 * @author Eibe Frank
 */
public class PrecisionRecallCurve {
	
	private LinePlot plotting;
	
	private double typicalThreshold;
	private int numberOfDriftGoldStandard;
	
	public PrecisionRecallCurve(double typicalThreshold, int numberOfDriftGoldStandard) {
		
		this.typicalThreshold = typicalThreshold;
		this.numberOfDriftGoldStandard = numberOfDriftGoldStandard;
		plotting = new LinePlot(this.getClass().getName(), "PR Curve","Recall","Precision"); 
		
	}
 
  
	private double[][]  computePRCurve(int winsize, ArrayList<Double> pValVector){
		int driftStep= (pValVector.size()-1+2*winsize) /this.numberOfDriftGoldStandard+1;		
		  
		ScoredPrecisionRecallEvaluation predictions = new ScoredPrecisionRecallEvaluation();
		  for (int i = 0; i < pValVector.size(); i++) {
			if ((i+winsize)%driftStep==0) 
			  predictions.addCase(false, pValVector.get(i));
			else
				predictions.addCase(true, pValVector.get(i));
		}
		    
		   
		double[][] tempRes = predictions.prCurve(false);
		return tempRes;
	}
	
	
	public void AddPRCurve(String title, int winsize, ArrayList<Double> pValVector) {
		double[][] tempRes = computePRCurve(winsize, pValVector);
		 
		int prCurve = plotting.AddCurve(title);
		 for (int i = 0; i < tempRes.length; i++) {
			  plotting.addEleVal(prCurve,tempRes[i][0], tempRes[i][1]);
		} 
	}
	
	public void AddPRLogCurve(String title, int winsize, ArrayList<Double> pValVector) {
		
		double[][] tempRes = computePRCurve(winsize, pValVector);
		 
		int prCurve = plotting.AddCurve(title);
		 for (int i = 0; i < tempRes.length; i++) {
			 double x = tempRes[i][0];
			 double y = tempRes[i][1];
			 if (x != 0 && y!=0)
			  plotting.addEleVal(prCurve,-Math.log10(x),-Math.log10(y) );
		} 
	}
	
	public void AddPRLogInvCurve(String title, int winsize, ArrayList<Double> pValVector) {
		
		double[][] tempRes = computePRCurve(winsize, pValVector);
		 
		int prCurve = plotting.AddCurve(title);
		 for (int i = 0; i < tempRes.length; i++) {
			 double x = 1-tempRes[i][0];
			 double y = 1-tempRes[i][1];
			 if (x != 0 && y!=0)
			  plotting.addEleVal(prCurve,-Math.log10(x),-Math.log10(y) );
		} 
	}
	
	public void plot() {
		plotting.plot();
	}
	
	/**
   * takes two arguments: dataset in ARFF format (expects class to
   * be last attribute) and name of file with output
   */
  public static void main(String[] args) throws Exception {
 
    // load data
//    Instances data = new Instances(new BufferedReader(new FileReader(args[0])));
//    data.setClassIndex(data.numAttributes() - 1);
 
    // train classifier
//    Classifier cl = new NaiveBayes();
//    Evaluation eval = new Evaluation(data);
//    eval.crossValidateModel(cl, data, 10, new Random(1));
// 
	  
	  ScoredPrecisionRecallEvaluation predictions = new ScoredPrecisionRecallEvaluation();
    
//	  ArrayList<Prediction> predictions = new ArrayList<Prediction>();
	  predictions.addCase(true,0.8);
	  predictions.addCase(true,0.8);
	  predictions.addCase(true,0.6);
	  predictions.addCase(true,0.7);
	  predictions.addCase(true,0.6);
	  predictions.addCase(true,0.7);
	  predictions.addCase(true,0.6);
	  predictions.addCase(true,0.2);
	  predictions.addCase(true,0.2);
	  predictions.addCase(true,0.2);
	  predictions.addCase(true,0.2);
	  predictions.addCase(true,0.2);
	  predictions.addCase(false,0.15);
	  predictions.addCase(false,0.15);
	  predictions.addCase(true,0.15);
	  predictions.addCase(true,0.6);
	  predictions.addCase(true,0.7);
	  predictions.addCase(true,0.6);
	  predictions.addCase(true,0.7);
	  predictions.addCase(true,0.6);
	  predictions.addCase(true,0.5);
	  predictions.addCase(true,0.6);
	  predictions.addCase(true,0.5);
	  predictions.addCase(true,0.6);
	  predictions.addCase(true,0.7);
	  
	 // generate curve
//    ThresholdCurve tc = new ThresholdCurve();
////    int classIndex = 0;
//    Instances result = tc.getCurve(predictions);
	  LinePlot plotting = new LinePlot("PrecisionRecallCurve", "PR Curve","Recall","Precision"); 
	   
	  double[][] tempRes = predictions.prCurve(false);
	 
	  int prCurve = plotting.AddCurve("PR Curve");
	  for (int i = 0; i < tempRes.length; i++) {
		  plotting.addEleVal(prCurve,tempRes[i][0], tempRes[i][1]);
	  }
	  plotting.plot();
	  
	  
	  
  }
}