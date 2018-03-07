
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
package org.apromore.prodrift.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apromore.prodrift.main.Main;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYShapeAnnotation;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class LinePlot extends ApplicationFrame{
	
	private XYSeriesCollection dataset = new XYSeriesCollection();
	private ArrayList<XYSeries> curves = new ArrayList<XYSeries>();
	private String title,XLb,YLb;
	
	private boolean addThr = false;
	
	
	private XYSeries thrsLine = new XYSeries("Threshhold");
	
	public LinePlot(String windowTitle, String tle,String Xlabel, String Ylabel) {
		super(windowTitle);
		title = tle;
		XLb = Xlabel;
		YLb = Ylabel;
	}
	
	//create new XYseries and return its index (beeing the last one) 
	public int AddCurve(String curveName) {
		XYSeries curvetoAdd = new XYSeries(curveName);
		curves.add(curvetoAdd);
		return curves.size()-1;
	}

	//add a raw of y values where x is incremented every time by 1
	public void addRaw(int curveIndex, List<Double> pValVector) {
		for (int i = 0; i < pValVector.size(); i++) {
			addEleVal(curveIndex,i,pValVector.get(i));
		}
	}

	public void addEleVal(int curveIndex, double xval, double yval) {
		curves.get(curveIndex).add(xval,yval);
	}
	
	
	public void addThreshold (double trValue)
	{
		addThr = true;
		thrsLine.add(curves.get(0).getMinX(), trValue);
		thrsLine.add(curves.get(0).getMaxX(), trValue);
	}
	

	private XYDataset createDataset() {
		
		for (int i = 0; i < curves.size(); i++) {
			dataset.addSeries(curves.get(i));
		}
		
		if (addThr) dataset.addSeries(thrsLine);

		return dataset;
	}
	
	public JFreeChart plot() {
		JFreeChart lineChart = ChartFactory.createXYLineChart(title,XLb, YLb, createDataset(),PlotOrientation.VERTICAL ,
		         true , true , false);
			

		if(!Main.isStandAlone)
		{
			ChartPanel chartPanel = new ChartPanel(lineChart);
			chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
			setContentPane(chartPanel);
			this.pack();
			RefineryUtilities.centerFrameOnScreen(this);
			this.setVisible(true);
		}
		
		return lineChart;
		
	}

	public JFreeChart plot(List<BigInteger> driftPoints, double threshold, int logSize) {
		JFreeChart lineChart = ChartFactory.createXYLineChart(title,XLb, YLb, createDataset(),PlotOrientation.VERTICAL ,
		         true , true , false);
		
		for(int i = 0; i < driftPoints.size(); i++)
		{
			
			int widthHalf = (logSize / 200) > 1 ? (logSize / 200) : 1;
			((XYPlot) lineChart.getPlot()).addAnnotation(new XYShapeAnnotation(new Ellipse2D.Double(((double)driftPoints.get(i).intValue()) - widthHalf, 
					threshold - 0.01, 2*widthHalf, 0.02)));
			
		}
		

		if(!Main.isStandAlone)
		{
			ChartPanel chartPanel = new ChartPanel(lineChart);
			chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
			setContentPane(chartPanel);
			this.pack();
			RefineryUtilities.centerFrameOnScreen(this);
			this.setVisible(true);
		}
		
		return lineChart;
		
	}
	
	public JFreeChart plotSuddenAndGradual(List<BigInteger> driftPoints, List<BigInteger> startOfTransitionPoints,
			List<BigInteger> endOfTransitionPoints, List<Integer> startOfGradDrifts,
			List<Integer> endOfGradDrifts, double threshold, int logSize) {
		
		createDataset();
		JFreeChart lineChart = ChartFactory.createXYLineChart(title,XLb, YLb, dataset, PlotOrientation.VERTICAL ,
		         true , true , false);
		
		int gradDriftIndex = 0;
		for(int i = 0; i < driftPoints.size(); i++)
		{
			
			if(startOfGradDrifts.size() > gradDriftIndex && endOfTransitionPoints.get(i+1).intValue() == startOfGradDrifts.get(gradDriftIndex).intValue() &&
					startOfTransitionPoints.get(i+1).intValue() == endOfGradDrifts.get(gradDriftIndex).intValue())
			{
				
				
				((XYPlot) lineChart.getPlot()).addAnnotation(new XYShapeAnnotation(new java.awt.geom.Rectangle2D.Double(startOfGradDrifts.get(gradDriftIndex).doubleValue(), threshold - 0.005, 
						endOfGradDrifts.get(gradDriftIndex).doubleValue() - startOfGradDrifts.get(gradDriftIndex).doubleValue(), 0.01), null, null, Color.black));
				
//				((XYPlot) lineChart.getPlot()).addAnnotation(new XYShapeAnnotation(new java.awt.geom.Rectangle2D.Double(200.0, threshold, 
//						1000.0, 0.5), null, null, Color.yellow));
				
				
				gradDriftIndex++;
				i++;
				
			}else
			{
				
//				((XYPlot) lineChart.getPlot()).addAnnotation(new XYShapeAnnotation(new Ellipse2D.Double(((double)driftPoints.get(i).intValue()) - 10, 
//						threshold - 0.005, 20, 0.01)));
				
				int widthHalf = (logSize / 200) > 1 ? (logSize / 200) : 1;
				((XYPlot) lineChart.getPlot()).addAnnotation(new XYShapeAnnotation(new Ellipse2D.Double(((double)driftPoints.get(i).intValue()) - widthHalf, 
						threshold - 0.01, 2*widthHalf, 0.02)));
				
			}
			
			
		}
		

		if(!Main.isStandAlone)
		{
			ChartPanel chartPanel = new ChartPanel(lineChart);
			chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
			setContentPane(chartPanel);
			this.pack();
			RefineryUtilities.centerFrameOnScreen(this);
			this.setVisible(true);
		}
		
		return lineChart;
		
	}

	public XYSeriesCollection getDataset() {
		return dataset;
	}

	public void setDataset(XYSeriesCollection dataset) {
		this.dataset = dataset;
	}

	public ArrayList<XYSeries> getCurves() {
		return curves;
	}

	public void setCurves(ArrayList<XYSeries> curves) {
		this.curves = curves;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getXLb() {
		return XLb;
	}

	public void setXLb(String xLb) {
		XLb = xLb;
	}

	public String getYLb() {
		return YLb;
	}

	public void setYLb(String yLb) {
		YLb = yLb;
	}

	public boolean isAddThr() {
		return addThr;
	}

	public void setAddThr(boolean addThr) {
		this.addThr = addThr;
	}

	public XYSeries getThrsLine() {
		return thrsLine;
	}

	public void setThrsLine(XYSeries thrsLine) {
		this.thrsLine = thrsLine;
	}
	
//	//Method to plot the graph with a MARKER (vertical line on the drift point)
//	public JFreeChart plot(List<BigInteger> driftPoints, double threshold) {
//		JFreeChart lineChart = ChartFactory.createXYLineChart(title,XLb, YLb, createDataset(),PlotOrientation.VERTICAL ,
//		         true , true , false);
//		XYPlot plot = (XYPlot) lineChart.getPlot();
//		for(int i = 0; i < driftPoints.size(); i++)
//		{
//			
////			((XYPlot) lineChart.getPlot()).addAnnotation(new XYShapeAnnotation(new Ellipse2D.Double(((double)driftPoints.get(i).intValue()) - 10, 
////					threshold - 0.005, 20, 0.01)));
//
//			XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true,false);
//			renderer.setSeriesPaint(0, Color.BLUE);
//			renderer.setSeriesStroke(0, new BasicStroke(0.8f));
//			
//			plot.setRenderer(renderer);
//			Marker verticalLine = new ValueMarker(driftPoints.get(i).intValue(),Color.YELLOW,new BasicStroke(1));
//			plot.addDomainMarker(verticalLine);
//			
//		}
//		
//
//		ChartPanel chartPanel = new ChartPanel(lineChart);
//		chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
//		setContentPane(chartPanel);
//		this.pack();
//		RefineryUtilities.centerFrameOnScreen(this);
//		this.setVisible(true);
//		
//		return lineChart;
//		
//	}
	
	
}
