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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.apromore.prodrift.config.DriftConfig;
import org.apromore.prodrift.driftdetector.ControlFlowDriftDetector;
import org.apromore.prodrift.driftdetector.ControlFlowDriftDetector_EventStream;
import org.apromore.prodrift.driftdetector.ControlFlowDriftDetector_RunStream;
import org.apromore.prodrift.model.ProDriftDetectionResult;
import org.apromore.prodrift.util.XLogManager;
import org.deckfour.xes.model.XLog;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class Test {

	
	public static void main(String args[]) throws FileNotFoundException
	{
		
		Path path = Paths.get("./Loan_baseline_conditional_move.mxml");
		XLog xl = XLogManager.readLog(new FileInputStream(path.toString()), path.getFileName().toString());
		
		DriftConfig cf = DriftConfig.AlphaRelation;
		ControlFlowDriftDetector cfdd = null;
		if(cf == DriftConfig.AlphaRelation)
		{
			int initialWinSize = 3000; // # of events within a window
			boolean useAdwin = true; // Adaptive window or Fixed window
			boolean withCharacterization = true; // Characterize detected drifts
			cfdd = new ControlFlowDriftDetector_EventStream(xl, initialWinSize, useAdwin, withCharacterization);
		}
		else if(cf == DriftConfig.RUN)
		{
			int initialWinSize = 100; // # of runs within a window
			boolean useAdwin = true; // Adaptive window or Fixed window
			boolean detectGradualDrift = true; // Support gradual drift detection
			cfdd = new ControlFlowDriftDetector_RunStream(xl, initialWinSize, useAdwin, detectGradualDrift);
		}
		
		ProDriftDetectionResult result = cfdd.ControlFlowDriftDetectorStart();
		
		// visualize p-value diagram
		result.getpValuesDiagram();		
		JFreeChart lineChart = result.getLineChart();
		ChartPanel chartPanel = new ChartPanel(lineChart);
		chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));		
		ApplicationFrame af = new ApplicationFrame("");
		af.setContentPane(chartPanel);
		af.pack();
		RefineryUtilities.centerFrameOnScreen(af);
		af.setVisible(true);
		
		
		// print drift detection and characterization results
		List<BigInteger> driftPoints = result.getDriftPoints();
        Map<BigInteger, List<String>> characterizationMap = result.getCharacterizationMap();

        for(int i = 0; i < result.getDriftStatements().size(); i++)
        {


            BigInteger driftPoint = driftPoints.get(i);
            System.out.println("(" + (i+1) + ") " + result.getDriftStatements().get(i));
            List<String> charStatementsList = characterizationMap.get(driftPoint);

            if(charStatementsList != null){
                int ind = 0;
                System.out.println("************** Drift Characterization *******************");
				for(String str : charStatementsList)
					System.out.println("(" + (++ind) + ") " + str);
            }
            
            System.out.println();
            System.out.println();

        }
	}
}
