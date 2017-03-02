package org.apromore.service.perfmining.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.joda.time.DateTime;
import org.joda.time.Seconds;

/**
 * A stage means a lifycle stage of a case It is also a queuing system,
 * consisting of a Queue and a Service Facility
 * 
 * @author Bruce Nguyen
 */
public class Stage {
	private final List<FlowCell> flowCells = new ArrayList<FlowCell>();

	private List<Integer> queueArrivalCounts = new ArrayList<Integer>(); // start enqueuing
	private final List<Integer> serviceArrivalCounts = new ArrayList<Integer>(); //cumulative arrival case count, i.e. start servicing
	private final List<Integer> serviceDepartureCounts = new ArrayList<Integer>(); //cumulative departure case count
	private final List<Integer> serviceDeclinedCounts = new ArrayList<Integer>(); //cumulative decline case count
	private final List<Integer> serviceCancelledCounts = new ArrayList<Integer>(); //cumulative cancel case count
	private final List<Integer> serviceExitCounts = new ArrayList<Integer>(); //cumulative exit case count
	private final Map<String, List<Integer>> serviceExitSubCounts = new HashMap<String, List<Integer>>();
	private final List<Integer> servicePassedCounts = new ArrayList<Integer>(); //cumulative arrival case count	
	private final List<Long> caseCumulativeTime = new ArrayList<Long>(); //number of cumulative seconds for all cases within this stage 
	private final List<Long> resCumulativeTime = new ArrayList<Long>(); //number of cumulative seconds for all res within this stage
	//	private List<Long> serviceResourceTime = new ArrayList<Long>(); //cumulative resource work time in this stage
	//	private List<Long> serviceTotalTime = new ArrayList<Long>(); //cumulative stage time of this stage

	private final Map<Integer, Integer> serviceTimeMap = new HashMap<Integer, Integer>();
	private final Map<Integer, Integer> queueTimeMap = new HashMap<Integer, Integer>();
	private String name = "";
	private SPF bpf = null;

	public Stage(String name, SPF bpf) {
		this.name = name;
		this.bpf = bpf;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getBlockCount() {
		return flowCells.size();
	}

	public List<Integer> getServiceArrivalCounts() {
		return serviceArrivalCounts;
	}

	public List<Integer> getServiceDepartureCounts() {
		return serviceDepartureCounts;
	}

	public List<Integer> getServiceExitCounts() {
		if (serviceExitCounts.isEmpty()) {
			for (int i = 0; i < bpf.getTimeSeries().size(); i++) {
				int count = 0;
				for (List<Integer> subCounts : serviceExitSubCounts.values()) {
					count += subCounts.get(i);
				}
				serviceExitCounts.add(count);
			}
		}
		return serviceExitCounts;
	}

	public Map<String, List<Integer>> getServiceExitSubCounts() {
		return serviceExitSubCounts;
	}

	public List<Integer> getServicePassedCounts() {
		if (servicePassedCounts.isEmpty()) {
			for (int i = 0; i < bpf.getTimeSeries().size(); i++) {
				servicePassedCounts.add(serviceDepartureCounts.get(i) - getServiceExitCounts().get(i));
			}
		}
		return servicePassedCounts;
	}

	public List<Long> getCaseCumulativeTime() {
		return caseCumulativeTime;
	}

	public List<Long> getResCumulativeTime() {
		return resCumulativeTime;
	}

	//	public List<Long> getServiceResourceTime() {
	//		return this.serviceResourceTime;
	//	}
	//	
	//	public List<Long> getServiceTotalTime() {
	//		return this.serviceTotalTime;
	//	}

	public List<Integer> getQueueArrivalCounts() {
		return queueArrivalCounts;
	}

	public void setQueueArrivalCounts(List<Integer> enqueueCountMap) {
		queueArrivalCounts = enqueueCountMap;
	}

	public List<Integer> getQueueDepartureCounts() {
		return serviceArrivalCounts;
	}

	/**
	 * Compute stage block characteristics at a point in time
	 * 
	 * @param timePoint
	 *            : the current point in time of the BPF
	 * @return a stage block is added
	 */
	public void computeFlowCellCharacteristics(int index) {
		DateTime timePoint = bpf.getTimeSeries().get(index);
		FlowCell cell = new FlowCell(timePoint);
		int preIndex = index - 1;

		if (timePoint.equals(bpf.getStartTimePoint())) {

			cell.setCharacteristic(SPF.CHAR_QUEUE_ARRIVAL_RATE, 0.0);
			cell.setCharacteristic(SPF.CHAR_QUEUE_DEPARTURE_RATE, 0.0);
			cell.setCharacteristic(SPF.CHAR_QUEUE_CIP, 0.0);
			cell.setCharacteristic(SPF.CHAR_QUEUE_TIS, 0.0);

			cell.setCharacteristic(SPF.CHAR_SERVICE_ARRIVAL_RATE, 0.0);
			cell.setCharacteristic(SPF.CHAR_SERVICE_DEPARTURE_RATE, 0.0);
			cell.setCharacteristic(SPF.CHAR_SERVICE_PASSED_RATE, 0.0);
			cell.setCharacteristic(SPF.CHAR_SERVICE_EXIT_RATE, 0.0);
			cell.setCharacteristic(SPF.CHAR_SERVICE_CIP, 0.0);
			cell.setCharacteristic(SPF.CHAR_SERVICE_TIS, 0.0);
			cell.setCharacteristic(SPF.CHAR_SERVICE_FLOW_EFFICIENCY, 0.0);
		} else {

			cell.setCharacteristic(SPF.CHAR_QUEUE_ARRIVAL_RATE, 1.0
					* (queueArrivalCounts.get(index) - queueArrivalCounts.get(preIndex))
					/ (bpf.getConfig().getTimeStep() / 3600));
			cell.setCharacteristic(SPF.CHAR_QUEUE_DEPARTURE_RATE, 1.0
					* (serviceArrivalCounts.get(index) - serviceArrivalCounts.get(preIndex))
					/ (bpf.getConfig().getTimeStep() / 3600));
			cell.setCharacteristic(SPF.CHAR_QUEUE_CIP,
					1.0 * (queueArrivalCounts.get(index) - serviceArrivalCounts.get(preIndex)));
			int foundIndex = searchDepartingPointAtQueue(index);
			cell.setCharacteristic(SPF.CHAR_QUEUE_TIS, 1.0 * (Seconds.secondsBetween(bpf.getTimeSeries().get(index),
					bpf.getTimeSeries().get(foundIndex)).getSeconds()) / 3600);
			queueTimeMap.put(index, foundIndex);

			cell.setCharacteristic(SPF.CHAR_SERVICE_ARRIVAL_RATE, 1.0
					* (serviceArrivalCounts.get(index) - serviceArrivalCounts.get(preIndex))
					/ (bpf.getConfig().getTimeStep() / 3600));
			cell.setCharacteristic(SPF.CHAR_SERVICE_DEPARTURE_RATE, 1.0
					* (serviceDepartureCounts.get(index) - serviceDepartureCounts.get(preIndex))
					/ (bpf.getConfig().getTimeStep() / 3600));
			cell.setCharacteristic(SPF.CHAR_SERVICE_PASSED_RATE, 1.0
					* (getServicePassedCounts().get(index) - getServicePassedCounts().get(preIndex))
					/ (bpf.getConfig().getTimeStep() / 3600));
			cell.setCharacteristic(SPF.CHAR_SERVICE_CIP,
					1.0 * (serviceArrivalCounts.get(index) - serviceDepartureCounts.get(preIndex)));
			cell.setCharacteristic(SPF.CHAR_SERVICE_EXIT_RATE, 1.0
					* (getServiceExitCounts().get(index) - getServiceExitCounts().get(preIndex))
					/ (bpf.getConfig().getTimeStep() / 3600));

			for (String exitType : serviceExitSubCounts.keySet()) {
				cell.setCharacteristic(
						SPF.CHAR_SERVICE_EXIT_RATE_TYPE + exitType,
						1.0
								* (serviceExitSubCounts.get(exitType).get(index) - serviceExitSubCounts.get(exitType)
										.get(preIndex)) / (bpf.getConfig().getTimeStep() / 3600));
			}

			if (caseCumulativeTime.get(index) - caseCumulativeTime.get(preIndex) != 0) {
				cell.setCharacteristic(
						SPF.CHAR_SERVICE_FLOW_EFFICIENCY,
						1.0 * (resCumulativeTime.get(index) - resCumulativeTime.get(preIndex))
								/ (caseCumulativeTime.get(index) - caseCumulativeTime.get(preIndex)));
			} else {
				cell.setCharacteristic(SPF.CHAR_SERVICE_FLOW_EFFICIENCY, 0.0);
			}

			//			if (serviceTotalTime.get(index) != 0 && bpf.getConfig().getCheckStartCompleteEvents()) {
			//				cell.setCharacteristic(BPF.CHAR_SERVICE_FLOW_EFFICIENCY, 1.0*serviceResourceTime.get(index)/serviceTotalTime.get(index));
			//			} else {
			//				cell.setCharacteristic(BPF.CHAR_SERVICE_FLOW_EFFICIENCY, 0.0);
			//			}
			//Compute Time in Stage
			foundIndex = searchDepartingPointAtService(index);
			cell.setCharacteristic(SPF.CHAR_SERVICE_TIS, 1.0 * (Seconds.secondsBetween(bpf.getTimeSeries().get(index),
					bpf.getTimeSeries().get(foundIndex)).getSeconds()) / 3600);
			serviceTimeMap.put(index, foundIndex);
		}

		flowCells.add(cell);
	}

	//	public void computeQueueCharacteristics(int index) {
	//		DateTime timePoint = bpf.getTimeSeries().get(index);
	//		FlowCell cell = new FlowCell(timePoint);
	//		int preIndex = index - 1;
	//		
	//		if (timePoint.equals(bpf.getStartTimePoint())) {
	//			cell.setCharacteristic(BPF.CHAR_QUEUE_ARRIVAL_RATE, 0.0);
	//			cell.setCharacteristic(BPF.CHAR_QUEUE_DEPARTURE_RATE, 0.0);
	//			cell.setCharacteristic(BPF.CHAR_QUEUE_CIP, 0.0);
	//			cell.setCharacteristic(BPF.CHAR_QUEUE_TIS, 0.0);
	//		}
	//		else {
	//			cell.setCharacteristic(BPF.CHAR_QUEUE_ARRIVAL_RATE, 1.0*(queueArrivalCounts.get(index) - queueArrivalCounts.get(preIndex))/(bpf.getConfig().getTimeStep()/3600));
	//			cell.setCharacteristic(BPF.CHAR_QUEUE_DEPARTURE_RATE, 1.0*(serviceArrivalCounts.get(index) - serviceArrivalCounts.get(preIndex))/(bpf.getConfig().getTimeStep()/3600));
	//			cell.setCharacteristic(BPF.CHAR_QUEUE_CIP, 1.0*(queueArrivalCounts.get(index) - serviceArrivalCounts.get(preIndex)));
	//
	//			//Compute Time in Stage
	//			int foundIndex = searchDepartingPointAtQueue(index);
	//			cell.setCharacteristic(BPF.CHAR_QUEUE_TIS, 1.0*(Seconds.secondsBetween(bpf.getTimeSeries().get(index), bpf.getTimeSeries().get(foundIndex)).getSeconds())/3600);
	//			this.queueTimeMap.put(index, foundIndex);		
	//		}
	//		
	//		this.flowCells.add(cell);
	//	}	

	/**
	 * Compute the point on arrival path that has the same cumulative count as
	 * the departure path at the input time point. If cannot find the point,
	 * then return the last time point of the BPF The distance is computed to
	 * the last time point of the BPF
	 * 
	 * @param timePoint
	 * @return: a DateTime point or the end point of BPF if not found
	 */
	private int searchDepartingPointAtService(int index) {
		//		DateTime startPoint = null;
		//		DateTime preCyclePoint = timePoint.minusSeconds((bpf.getConfig().getTimeStep()));
		//		DateTime lastPoint = bpf.getLastSeriesPoint();
		int preIndex = index - 1;
		int start;

		//		if (preCyclePoint.isEqual(bpf.getEndTimePoint())) { // if previous point cannot be found then this point cannot be found
		if (preIndex == 0) {
			return preIndex;
		} else if (serviceTimeMap.containsKey(preIndex)) {
			start = serviceTimeMap.get(preIndex);
		} else {
			start = index;
		}

		int foundIndex = -1;
		for (int i = start; i < bpf.getTimeSeries().size(); i++) {
			if ((serviceDepartureCounts.get(i) <= serviceArrivalCounts.get(index))) {
				foundIndex = i;
			} else {
				break; //break to select the closest point in time
			}
		}
		if (foundIndex == -1) {
			foundIndex = bpf.getTimeSeries().size();
		}
		return foundIndex;
	}

	private int searchDepartingPointAtQueue(int index) {
		int preIndex = index - 1;
		int start;

		if (preIndex == 0) {
			return preIndex;
		} else if (queueTimeMap.containsKey(preIndex)) {
			start = queueTimeMap.get(preIndex);
		} else {
			start = index;
		}

		int foundIndex = -1;
		for (int i = start; i < bpf.getTimeSeries().size(); i++) {
			if ((serviceArrivalCounts.get(i) <= queueArrivalCounts.get(index))) {
				foundIndex = i;
			} else {
				break; //break to select the closest point in time
			}
		}
		if (foundIndex == -1) {
			foundIndex = bpf.getTimeSeries().size();
		}
		return foundIndex;
	}

	public void computeCharacteristics() throws Exception {
		if (serviceArrivalCounts.isEmpty()) {
			throw new Exception("Cannot compute characteristics of stage '" + name + "' since stage is empty!");
		}

		int counter = 0;
		for (int i = 0; i < bpf.getTimeSeries().size(); i++) {
			counter++;
			if (counter % 1000 == 0) {
			}
			computeFlowCellCharacteristics(i);
		}
	}

	public List<FlowCell> getFlowCells() {
		return flowCells;
	}

	/**
	 * @param timestamp
	 *            : milliseconds from 1.1.1970
	 * @return: StageBlock
	 */
	public FlowCell getFlowCellByTime(long timestamp) {
		for (int i = 0; i < flowCells.size(); i++) {
			if (bpf.getTimeSeries().get(i).getMillis() == timestamp) {
				return flowCells.get(i);
			}
		}
		return null;
	}

	public double getMean(String characteristic, DateTime start, DateTime end) {
		List<Double> values = new ArrayList<Double>();
		System.out.println(name);
		for (int i = 0; i < flowCells.size(); i++) {
			DateTime timePoint = bpf.getTimeSeries().get(i);
			if ((timePoint.isAfter(start) || timePoint.isEqual(start))
					&& (timePoint.isBefore(end) || timePoint.isEqual(end))) {
				values.add(flowCells.get(i).getCharacteristic(characteristic));
			}
		}

		double[] valuesA = new double[values.size()];
		int i = 0;
		for (Double value : values) {
			valuesA[i] = value;
			i++;
		}

		Mean meanCalc = new Mean();
		double result = meanCalc.evaluate(valuesA, 0, values.size());
		if (Double.isNaN(result)) {
			result = 0.0;
		}
		return result;
	}

	public double getMedian(String characteristic, DateTime start, DateTime end) {
		List<Double> values = new ArrayList<Double>();
		for (int i = 0; i < flowCells.size(); i++) {
			DateTime timePoint = bpf.getTimeSeries().get(i);
			if ((timePoint.isAfter(start) || timePoint.isEqual(start))
					&& (timePoint.isBefore(end) || timePoint.isEqual(end))) {
				values.add(flowCells.get(i).getCharacteristic(characteristic));
			}
		}

		double[] valuesA = new double[values.size()];
		int i = 0;
		for (Double value : values) {
			valuesA[i] = value;
			i++;
		}

		Median calc = new Median();
		double result = calc.evaluate(valuesA, 0, values.size());
		if (Double.isNaN(result)) {
			result = 0.0;
		}
		return result;
	}

	//	public double getMeanFlowEfficiency(DateTime start, DateTime end) throws ClassNotFoundException, SQLException {
	//		
	//		if (!bpf.getConfig().getCheckStartCompleteEvents()) return 0.0;
	//		
	//		int startIndex = -1, endIndex = -1;
	//		for (int i=0;i<bpf.getTimeSeries().size();i++) {
	//			DateTime timePoint = bpf.getTimeSeries().get(i);
	//			if ((timePoint.isAfter(start) || timePoint.isEqual(start)) && startIndex == -1) {
	//				startIndex = i;
	//			}
	//			if (timePoint.isBefore(end) || timePoint.isEqual(end)) {
	//				endIndex = i;
	//			}
	//			else {
	//				break;
	//			}
	//		}
	//		
	//		if (startIndex != -1 && endIndex != -1) {
	//			if ((serviceTotalTime.get(endIndex) - serviceTotalTime.get(startIndex)) != 0) {
	//				return 1.0*(serviceResourceTime.get(endIndex) - serviceResourceTime.get(startIndex))/
	//							(serviceTotalTime.get(endIndex) - serviceTotalTime.get(startIndex));
	//			}
	//			else {
	//				return 0.0;
	//			}
	//		}
	//		else {
	//			return 0.0;
	//		}
	//	}

	public void clear() {
		flowCells.clear();
		serviceArrivalCounts.clear();
		serviceDepartureCounts.clear();
		serviceDeclinedCounts.clear();
		serviceCancelledCounts.clear();
		serviceExitCounts.clear();
		serviceTimeMap.clear();
	}
}
