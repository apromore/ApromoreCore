/*
 * Copyright � 2009-2018 The Apromore Initiative.
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
package org.apromore.prodrift.logabstraction.extension;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apromore.prodrift.config.BehaviorRelation;
import org.apromore.prodrift.util.Utils;
import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.framework.util.Pair;

public class BasicLogRelationsExt {

	private final XLog log;
	private final XLogInfo logSummary;
	private final XEventClasses eventClasses;
	private final Map<Pair<XEventClass, XEventClass>, Double> causalRelations = new HashMap<Pair<XEventClass, XEventClass>, Double>();
	private final Map<Pair<XEventClass, XEventClass>, Double> parallelRelations = new HashMap<Pair<XEventClass, XEventClass>, Double>();
	private final Map<XEventClass, Integer> startClasses = new HashMap<XEventClass, Integer>();
	private final Map<XEventClass, Integer> endClasses = new HashMap<XEventClass, Integer>();
	private final Map<XEventClass, Integer> selfLoopRelations = new HashMap<XEventClass, Integer>();
	private final Map<Pair<XEventClass, XEventClass>, RelationCardinality> dfRelations = new HashMap<Pair<XEventClass, XEventClass>, RelationCardinality>(); // Directly follows relations
	private final Map<Pair<XEventClass, XEventClass>, Set<XTrace>> existDirect = new HashMap<Pair<XEventClass, XEventClass>, Set<XTrace>>();
	private final Map<Pair<XEventClass, XEventClass>, RelationCardinality> twoLoopRelations = new HashMap<Pair<XEventClass, XEventClass>, RelationCardinality>();

	float noiseThresh = 0.05f; // minimum freq of an edge in the DF graph (compared to the maximum edge freq)
	float loopThresh = 0.3f; // minimum ratio between the loop freq to loop back freq (# of ABA to # of BA)

//	public BasicLogRelationsExt(XLog log) {
//		super(log);
//		// TODO Auto-generated constructor stub
//	}

	public BasicLogRelationsExt(XLog log, XLogInfo summary, float noiseThresh) {
		this.log = log;
		this.logSummary = summary;
		eventClasses = summary.getEventClasses();
		this.noiseThresh = noiseThresh;
		initialize(Collections.<Pair<XEventClass, XEventClass>>emptySet());
	}

	private void initialize(Collection<Pair<XEventClass, XEventClass>> startEndEventTypes) {

		fillDirectSuccessionMatrices(log);

		// direct contains the direct successions (AB patterns)
		// twoloop contains ABA patterns
		// start and end are filled

//		makeBasicRelations(progress, shortLoops);

	}

	/**
	 * Makes basic relations, if required extended with short loops.
	 *
	 */
	public void makeBasicRelations(BasicLogRelationsExt alphaRelation_other) {

		filterDFgraph(alphaRelation_other);

		selfLoopRelations.clear();
		causalRelations.clear();
		parallelRelations.clear();

		for (Pair<XEventClass, XEventClass> pair : dfRelations.keySet())
		{
			RelationCardinality rc_pair = dfRelations.get(pair);
			assert (rc_pair.getCardinality() > 0);

			if(!rc_pair.isNoise())
			{
				Pair<XEventClass, XEventClass> opposed = new Pair<XEventClass, XEventClass>(pair.getSecond(),
						pair.getFirst());
				RelationCardinality rc_opposed = dfRelations.get(opposed);
				if (rc_opposed != null && !rc_opposed.isNoise()) {
					assert (rc_opposed.getCardinality() > 0);
					// two loop or parallel relation
					RelationCardinality rc_loop = twoLoopRelations.get(pair);
					RelationCardinality rc_loop_opposed = twoLoopRelations.get(opposed);
					if ((rc_loop != null && !rc_loop.isNoise()) || (rc_loop_opposed != null && !rc_loop_opposed.isNoise())) {
						// two causal dependencies
						causalRelations.put(pair, 1.0);
						causalRelations.put(opposed, 1.0);
					} else {
//						if(pair.getFirst() == null || pair.getSecond() == null)
//							System.out.println();
						if (pair.getFirst().equals(pair.getSecond())) {
							selfLoopRelations.put(pair.getFirst(), rc_pair.getCardinality());
						}
						parallelRelations.put(pair, 1.0);
						parallelRelations.put(opposed, 1.0);
					}
				} else {
					// causal relation
					causalRelations.put(pair, 1.0);
				}
			}

		}

	}

	private void filterDFgraph(BasicLogRelationsExt alphaRelation_other)
	{
		int []dfCards = new int[dfRelations.size()];
		int index = 0;
		for(RelationCardinality rc : dfRelations.values())
		{
			dfCards[index++] = rc.getCardinality();
		}

		int maxCardinality = Utils.getMax(dfCards);

		dfCards = new int[alphaRelation_other.getDfRelations().size()];
		index = 0;
		for(RelationCardinality rc : alphaRelation_other.getDfRelations().values())
		{
			dfCards[index++] = rc.getCardinality();
		}

		int maxCardinality_other = Utils.getMax(dfCards);

		// post-processing to remove noise
		List<Pair<XEventClass, XEventClass>> pairList = new ArrayList<>(dfRelations.keySet());
		for(int i = 0; i < pairList.size(); i++)
		{
			Pair<XEventClass, XEventClass> pair = pairList.get(i);
			RelationCardinality rc = dfRelations.get(pair);
			rc.setNoise(false);

			RelationCardinality rc_other = alphaRelation_other.getDfRelations().get(pair);

			if(rc.getCardinality() < maxCardinality * noiseThresh
					&& (rc_other == null || rc_other.getCardinality() < maxCardinality_other * noiseThresh))
			{
				rc.setNoise(true);
			}
		}


		// remove noisy loops
		pairList = new ArrayList<>(twoLoopRelations.keySet());
		for(int i = 0; i < pairList.size(); i++)
		{
			Pair<XEventClass, XEventClass> pair = pairList.get(i);
			RelationCardinality rc_loop = twoLoopRelations.get(pair);
			rc_loop.setNoise(false);

			RelationCardinality rc_df = dfRelations.get(pair);

			Pair<XEventClass, XEventClass> opposed = new Pair<XEventClass, XEventClass>(pair.getSecond(),
					pair.getFirst());

			RelationCardinality rc_df_opposed = dfRelations.get(opposed);

			if(rc_df.isNoise() || rc_df_opposed.isNoise())
			{
				rc_loop.setNoise(true);
			}else
			{
				int loopFreq = rc_loop.getCardinality();
				int loopBackFreq = rc_df_opposed.getCardinality();

				if(loopFreq < loopBackFreq * loopThresh)
				{
					RelationCardinality loopFreq_other = alphaRelation_other.getTwoLoopRelations().get(pair);
					if(loopFreq_other != null)
					{
						Pair<XEventClass, XEventClass> opposed_other = new Pair<XEventClass, XEventClass>(pair.getSecond(),
								pair.getFirst());

						int loopBackFreq_other = alphaRelation_other.getDfRelations().get(opposed_other).getCardinality();

						if(loopFreq_other.getCardinality() < loopBackFreq_other * loopThresh)
						{
							rc_loop.setNoise(true);
						}
					}else
					{
						rc_loop.setNoise(true);
					}
				}
			}
		}
	}

	/**
	 * Makes direct succession relations, as well as two-loop relations, i.e.
	 * searches through the log for AB patterns and ABA patterns
	 *
	 * @param log
	 */
	private void fillDirectSuccessionMatrices(XLog log) {
		int n;
		for (XTrace trace : log) {
			if (!trace.isEmpty()) {
				XEventClass firstEvent = eventClasses.getClassOf(trace.get(0));
				// Count initial events
				n = startClasses.containsKey(firstEvent) ? startClasses.get(firstEvent) : 0;
				startClasses.put(firstEvent, n + 1);

				for (int i = 0; i < trace.size() - 1; i++) {

					XEventClass fromEvent = eventClasses.getClassOf(trace.get(i));
					XEventClass toEvent = eventClasses.getClassOf(trace.get(i + 1));
					Pair<XEventClass, XEventClass> pair = new Pair<XEventClass, XEventClass>(fromEvent, toEvent);
					// check for 2-loop
					if ((i < trace.size() - 2) && !fromEvent.equals(toEvent)) {
						if (fromEvent.equals(eventClasses.getClassOf(trace.get(i + 2)))) {
							// Pattern is ABA
							RelationCardinality rc_loop = twoLoopRelations.get(pair);
							if(rc_loop == null)
							{
								rc_loop = new RelationCardinality(0, false);
								twoLoopRelations.put(pair, rc_loop);
							}
							rc_loop.setCardinality(rc_loop.getCardinality() + 1);
						}
					}
					// update direct successions dependencies
					RelationCardinality rc = dfRelations.get(pair);
					if(rc == null)
					{
						rc = new RelationCardinality(0, false);
						dfRelations.put(pair, rc);
					}
					rc.setCardinality(rc.getCardinality() + 1);

					Set<XTrace> traces = existDirect.containsKey(pair) ? existDirect.get(pair) : new HashSet<XTrace>();
					traces.add(trace);
					existDirect.put(pair, traces);

				}
				XEventClass lastEvent = eventClasses.getClassOf(trace.get(trace.size() - 1));
				n = endClasses.containsKey(lastEvent) ? endClasses.get(lastEvent) : 0;
				endClasses.put(lastEvent, n + 1);
			}
		}

	}


	public /*Entry<Pair<XEventClass, XEventClass>, BehaviorRelation>*/ boolean updateDirectSuccessionMatrices(XTrace trace, XEvent event, boolean add, boolean front) {
		int n;

		Entry<Pair<XEventClass, XEventClass>, BehaviorRelation> output = null;

		if(add)
		{
			eventClasses.register(event);
			eventClasses.harmonizeIndices();
			int traceSize = trace.size();
			if(traceSize == 0)
			{
				// update the first event
				XEventClass firstEvent = eventClasses.getClassOf(event);
				// Count initial events
				n = startClasses.containsKey(firstEvent) ? startClasses.get(firstEvent) : 0;
				startClasses.put(firstEvent, n + 1);

				// update the last event
				XEventClass lastEvent = eventClasses.getClassOf(event);
				n = endClasses.containsKey(lastEvent) ? endClasses.get(lastEvent) : 0;
				endClasses.put(lastEvent, n + 1);

				// add the event to the trace
				trace.add(event);

//				output = new AbstractMap.SimpleEntry<Pair<XEventClass, XEventClass>, BehaviorRelation>(null, null);

				return /*output*/false;
			}

			// add an event
			if(front)
			{
				// add an event to the front of the trace
				XEventClass fromEvent = eventClasses.getClassOf(trace.get(traceSize - 1));
				XEventClass toEvent = eventClasses.getClassOf(event);

//					if(fromEvent.getId().compareToIgnoreCase("END") == 0 ||
//							toEvent.getId().compareToIgnoreCase("START") == 0)
//						System.out.println();

				Pair<XEventClass, XEventClass> pair = new Pair<XEventClass, XEventClass>(fromEvent, toEvent);

				// update direct successions dependencies////////////////////////////////
				RelationCardinality rc = dfRelations.get(pair);
				if(rc == null)
				{
					rc = new RelationCardinality(0, false);
					dfRelations.put(pair, rc);
				}
				rc.setCardinality(rc.getCardinality() + 1);

//				int []dfCards = new int[dfRelations.size()];
//				int index = 0;
//				for(RelationCardinality rcc : dfRelations.values())
//				{
//					dfCards[index++] = rcc.getCardinality();
//				}
//
//				int maxCardinality = Utils.getMax(dfCards);
//
//				if(rc.getCardinality() < maxCardinality * noiseThresh)
//					rc.setNoise(true);
//				else
//					rc.setNoise(false);
				////////////////////////////////


//				if(!rc.isNoise())
//				{
				// check for 2-loop
				if ((trace.size() - 2 >= 0) && !fromEvent.equals(toEvent))
				{
					XEventClass fromfromEvent = eventClasses.getClassOf(trace.get(traceSize - 2));
					if (fromfromEvent.equals(toEvent))
					{
						// Pattern is ABA
						Pair<XEventClass, XEventClass> opposed = new Pair<XEventClass, XEventClass>(pair.getSecond(),
								pair.getFirst());

						RelationCardinality rc_loop_opposed = twoLoopRelations.get(opposed);
						if(rc_loop_opposed == null)
						{
							rc_loop_opposed = new RelationCardinality(0, false);
							twoLoopRelations.put(opposed, rc_loop_opposed);
						}
						rc_loop_opposed.setCardinality(rc_loop_opposed.getCardinality() + 1);


//							int loopBackFreq = rc.getCardinality();
//							if(loopFreq >= loopBackFreq * loopThresh)
//								twoLoopRelations.put(opposed, loopFreq);
//							else
//								twoLoopRelations.remove(opposed);
					}
				}
//				}

				Set<XTrace> traces = existDirect.containsKey(pair) ? existDirect.get(pair) : new HashSet<XTrace>();
				traces.add(trace);
				existDirect.put(pair, traces);

				// update the last event
				XEventClass lastEvent = toEvent;
				n = endClasses.containsKey(lastEvent) ? endClasses.get(lastEvent) : 0;
				endClasses.put(lastEvent, n + 1);

				// add the event to the trace
				trace.add(event);

				// update basic relations based on the new pair
//				BehaviorRelation br = updateBasicRelations(pair, true);

//				output = new AbstractMap.SimpleEntry<Pair<XEventClass, XEventClass>, BehaviorRelation>(pair, br);

			}else
			{
				// add an event to the back of the trace

				XEventClass fromEvent = eventClasses.getClassOf(event);
				XEventClass toEvent = eventClasses.getClassOf(trace.get(0));
				Pair<XEventClass, XEventClass> pair = new Pair<XEventClass, XEventClass>(fromEvent, toEvent);

				// update direct successions dependencies////////////////////////////////
				RelationCardinality rc = dfRelations.get(pair);
				if(rc == null)
				{
					rc = new RelationCardinality(0, false);
					dfRelations.put(pair, rc);
				}
				rc.setCardinality(rc.getCardinality() + 1);

//				int []dfCards = new int[dfRelations.size()];
//				int index = 0;
//				for(RelationCardinality rcc : dfRelations.values())
//				{
//					dfCards[index++] = rcc.getCardinality();
//				}
//
//				int maxCardinality = Utils.getMax(dfCards);
//
//				if(rc.getCardinality() < maxCardinality * noiseThresh)
//					rc.setNoise(true);
//				else
//					rc.setNoise(false);
				////////////////////////////////

//				if(!rc.isNoise())
//				{
				// check for 2-loop
				if ((trace.size() - 2 >= 0) && !fromEvent.equals(toEvent))
				{
					XEventClass totoEvent = eventClasses.getClassOf(trace.get(1));
					if (fromEvent.equals(totoEvent))
					{
						// Pattern is ABA
						Pair<XEventClass, XEventClass> opposed = new Pair<XEventClass, XEventClass>(pair.getSecond(),
								pair.getFirst());

						RelationCardinality rc_loop = twoLoopRelations.get(pair);
						if(rc_loop == null)
						{
							rc_loop = new RelationCardinality(0, false);
							twoLoopRelations.put(pair, rc_loop);
						}
						rc_loop.setCardinality(rc_loop.getCardinality() + 1);

//							int loopBackFreq = dfRelations.get(opposed).getCardinality();
//							if(loopFreq >= loopBackFreq * loopThresh)
//								twoLoopRelations.put(pair, loopFreq);
//							else
//								twoLoopRelations.remove(pair);
					}
				}
//				}


				Set<XTrace> traces = existDirect.containsKey(pair) ? existDirect.get(pair) : new HashSet<XTrace>();
				traces.add(trace);
				existDirect.put(pair, traces);

				// update the first event
				XEventClass firstEvent = eventClasses.getClassOf(event);
				// Count initial events
				n = startClasses.containsKey(firstEvent) ? startClasses.get(firstEvent) : 0;
				startClasses.put(firstEvent, n + 1);

				// add the event to the trace
				trace.add(0, event);

				// update basic relations based on the new pair
//				BehaviorRelation br = updateBasicRelations(pair, true);
//
//				output = new AbstractMap.SimpleEntry<Pair<XEventClass, XEventClass>, BehaviorRelation>(pair, br);

			}

		}else
		{
			// remove an event

			int traceSize = trace.size();
			if(traceSize == 1)
			{
				// update the first event
				XEventClass firstEvent = eventClasses.getClassOf(event);
				// Count initial events
				n = startClasses.containsKey(firstEvent) ? startClasses.get(firstEvent) : 0;
				if(n - 1 <= 0)
					startClasses.remove(firstEvent);
				else
					startClasses.put(firstEvent, n - 1);

				// update the last event
				XEventClass lastEvent = eventClasses.getClassOf(event);
				n = endClasses.containsKey(lastEvent) ? endClasses.get(lastEvent) : 0;
				if(n - 1 <= 0)
					endClasses.remove(lastEvent);
				else
					endClasses.put(lastEvent, n - 1);

				// remove the event from the trace
				trace.remove(event);

//				output = new AbstractMap.SimpleEntry<Pair<XEventClass, XEventClass>, BehaviorRelation>(null, null);

				return /*output*/false;
			}

			if(front)
			{
				// remove an event from the front of the trace

				XEventClass fromEvent = eventClasses.getClassOf(trace.get(traceSize - 2));
				XEventClass toEvent = eventClasses.getClassOf(event);
				Pair<XEventClass, XEventClass> pair = new Pair<XEventClass, XEventClass>(fromEvent, toEvent);

				// update direct successions dependencies////////////////////////////////
				RelationCardinality rc = dfRelations.get(pair);
				if(rc.getCardinality() - 1 <= 0)
					dfRelations.remove(pair);
				else
					rc.setCardinality(rc.getCardinality() - 1);

				/*int []dfCards = new int[dfRelations.size()];
				int index = 0;
				for(RelationCardinality rcc : dfRelations.values())
				{
					dfCards[index++] = rcc.getCardinality();
				}

				int maxCardinality = Utils.getMax(dfCards);

				if(rc.getCardinality() < maxCardinality * noiseThresh)
					rc.setNoise(true);
				else
					rc.setNoise(false);*/
				////////////////////////////////

				Pair<XEventClass, XEventClass> opposed = new Pair<XEventClass, XEventClass>(pair.getSecond(),
						pair.getFirst());
				// check for 2-loop
				if ((trace.size() - 3 >= 0) && !fromEvent.equals(toEvent))
				{
					XEventClass fromfromEvent = eventClasses.getClassOf(trace.get(traceSize - 3));
					if (fromfromEvent.equals(toEvent))
					{
						// Pattern is ABA
						RelationCardinality rc_loop_opposed = twoLoopRelations.get(opposed);
//						if(rc_loop_opposed == null)
//						{
//							rc_loop_opposed = new RelationCardinality(0, false);
//							twoLoopRelations.put(opposed, rc_loop_opposed);
//						}
						rc_loop_opposed.setCardinality(rc_loop_opposed.getCardinality() - 1);

//						int loopBackFreq = rc.getCardinality();
						if(rc_loop_opposed.getCardinality() <= 0 /*&& loopFreq >= loopBackFreq * loopThresh*/)
							twoLoopRelations.remove(opposed);

					}
				}

				/*if(rc.isNoise())
				{
					twoLoopRelations.remove(pair);
					twoLoopRelations.remove(opposed);
				}*/

				// remove the event from the trace
				trace.remove(event);

				if(existDirect.containsKey(pair))
				{

					Set<XTrace> traces = existDirect.get(pair);
					if(!hasPair(trace, pair))
						traces.remove(trace);

					if(traces.size() == 0)
						existDirect.remove(pair);
					else
						existDirect.put(pair, traces);

				}

				// update the last event
				XEventClass lastEvent = toEvent;
				n = endClasses.containsKey(lastEvent) ? endClasses.get(lastEvent) : 0;
				if(n - 1 <= 0)
					endClasses.remove(lastEvent);
				else
					endClasses.put(lastEvent, n - 1);

				if(trace.size() != 0)
				{

					lastEvent = eventClasses.getClassOf(trace.get(trace.size() - 1));
					n = endClasses.containsKey(lastEvent) ? endClasses.get(lastEvent) : 0;
					endClasses.put(lastEvent, n + 1);

				}


				// update basic relations based on the removed pair
//				BehaviorRelation br = updateBasicRelations(pair, true);
//
//				output = new AbstractMap.SimpleEntry<Pair<XEventClass, XEventClass>, BehaviorRelation>(pair, br);


			}else
			{

				// remove an event from the back of the trace

				XEventClass fromEvent = eventClasses.getClassOf(event);
				XEventClass toEvent = eventClasses.getClassOf(trace.get(1));
				Pair<XEventClass, XEventClass> pair = new Pair<XEventClass, XEventClass>(fromEvent, toEvent);

				// update direct successions dependencies////////////////////////////////
				RelationCardinality rc = dfRelations.get(pair);
				if(rc.getCardinality() - 1 <= 0)
					dfRelations.remove(pair);
				else
					rc.setCardinality(rc.getCardinality() - 1);

				/*int []dfCards = new int[dfRelations.size()];
				int index = 0;
				for(RelationCardinality rcc : dfRelations.values())
				{
					dfCards[index++] = rcc.getCardinality();
				}

				int maxCardinality = Utils.getMax(dfCards);

				if(rc.getCardinality() < maxCardinality * noiseThresh)
					rc.setNoise(true);
				else
					rc.setNoise(false);*/
				////////////////////////////////

				Pair<XEventClass, XEventClass> opposed = new Pair<XEventClass, XEventClass>(pair.getSecond(),
						pair.getFirst());
				// check for 2-loop
				if ((trace.size() - 3 >= 0) && !fromEvent.equals(toEvent))
				{
					XEventClass totoEvent = eventClasses.getClassOf(trace.get(2));
					if (fromEvent.equals(totoEvent))
					{
						// Pattern is ABA
						RelationCardinality rc_loop = twoLoopRelations.get(pair);
						rc_loop.setCardinality(rc_loop.getCardinality() - 1);

//						int loopBackFreq = dfRelations.get(opposed).getCardinality();
						if(rc_loop.getCardinality() <= 0 /*&& loopFreq >= loopBackFreq * loopThresh*/)
							twoLoopRelations.remove(pair);
					}
				}

				/*if(rc.isNoise())
				{
					twoLoopRelations.remove(pair);
					twoLoopRelations.remove(opposed);
				}*/

				// remove the event from the trace
				trace.remove(event);

				if(existDirect.containsKey(pair))
				{

					Set<XTrace> traces = existDirect.get(pair);
					if(!hasPair(trace, pair))
						traces.remove(trace);

					if(traces.size() == 0)
						existDirect.remove(pair);
					else
						existDirect.put(pair, traces);

				}

				// update the first event
				XEventClass firstEvent = eventClasses.getClassOf(event);
				// Count initial events
				n = startClasses.containsKey(firstEvent) ? startClasses.get(firstEvent) : 0;
				if(n - 1 <= 0)
					startClasses.remove(firstEvent);
				else
					startClasses.put(firstEvent, n - 1);

				if(trace.size() != 0)
				{

					firstEvent = eventClasses.getClassOf(trace.get(0));
					n = startClasses.containsKey(firstEvent) ? startClasses.get(firstEvent) : 0;
					startClasses.put(firstEvent, n + 1);

				}

				// update basic relations based on the removed pair
//				BehaviorRelation br = updateBasicRelations(pair, true);
//
//				output = new AbstractMap.SimpleEntry<Pair<XEventClass, XEventClass>, BehaviorRelation>(pair, br);

			}

			unRegister(event);

		}

		return /*output*/true;

	}


	public BehaviorRelation updateBasicRelations(Pair<XEventClass, XEventClass> pair, boolean shortLoops)
	{

		BehaviorRelation br = null;

		Pair<XEventClass, XEventClass> opposed = new Pair<XEventClass, XEventClass>(pair.getSecond(),
				pair.getFirst());
		RelationCardinality rc_opposed = dfRelations.get(opposed);

		// remove the current relation
		parallelRelations.remove(pair);
		parallelRelations.remove(opposed);
		causalRelations.remove(pair);
		causalRelations.remove(opposed);
		selfLoopRelations.remove(pair.getFirst());

		RelationCardinality rc_pair = dfRelations.get(pair);
		if(rc_pair != null && !rc_pair.isNoise())
		{
			if (rc_opposed != null && !rc_opposed.isNoise())
			{
				// two loop or parallel relation
				RelationCardinality rc_loop = twoLoopRelations.get(pair);
				RelationCardinality rc_loop_opposed = twoLoopRelations.get(opposed);
				if ((rc_loop != null && !rc_loop.isNoise()) || (rc_loop_opposed != null && !rc_loop_opposed.isNoise())) {

					// two causal dependencies
					if(twoLoopRelations.containsKey(pair) && twoLoopRelations.containsKey(opposed))
					{

						br = BehaviorRelation.Length_Two_Loop_bi;

					}else if(twoLoopRelations.containsKey(pair))
					{

						br = BehaviorRelation.Length_Two_Loop_ABA;

					}else if(twoLoopRelations.containsKey(opposed))
					{

						br = BehaviorRelation.Length_Two_Loop_BAB;

					}
					causalRelations.put(pair, 1.0);
					causalRelations.put(opposed, 1.0);

				} else
				{
//					if(pair.getFirst() == null || pair.getSecond() == null)
//						System.out.println();
					Integer n = rc_pair.getCardinality();
					if (shortLoops && (pair.getFirst().equals(pair.getSecond())))
					{

						selfLoopRelations.put(pair.getFirst(), n);
						br = BehaviorRelation.Length_One_Loop;

					}else
					{

						parallelRelations.put(pair, 1.0);
						parallelRelations.put(opposed, 1.0);
						br = BehaviorRelation.CONCURRENCY;

					}

				}
			} else {
				// causal relation
				causalRelations.put(pair, 1.0);
				br = BehaviorRelation.Causal;

			}
		}else
		{
			if (rc_opposed != null && !rc_opposed.isNoise())
			{
				causalRelations.put(opposed, 1.0);
				br = BehaviorRelation.INV_Causal;
			}
		}

		return br;

	}


	private boolean hasPair(XTrace trace, Pair<XEventClass, XEventClass> pair)
	{

		for(int i = 0; i < trace.size() - 1; i++)
		{

			if(eventClasses.getClassOf(trace.get(i)).equals(pair.getFirst()) && eventClasses.getClassOf(trace.get(i+1)).equals(pair.getSecond()))
				return true;

		}

		return false;

	}

	public void unRegister(XEvent event) {
		unRegister(eventClasses.getClassifier().getClassIdentity(event));
	}

	public synchronized void unRegister(String classId) {
		XEventClass eventClass = eventClasses.getByIdentity(classId);
		if (eventClass != null && classId != null) {
			if(eventClass.size() == 1)
				eventClasses.getClasses().remove(classId);
			else
				decrementSize(eventClass);
		}

	}

	public void decrementSize(XEventClass eventClass)
	{
		eventClass.setSize(eventClass.size() - 1);
	}

	public class RelationCardinality
	{
		private int cardinality = 0;
		private boolean isNoise = false;

		public RelationCardinality(int cardinality, boolean isNoise) {
			this.cardinality = cardinality;
			this.isNoise = isNoise;
		}

		public int getCardinality() {
			return cardinality;
		}

		public void setCardinality(int cardinality) {
			this.cardinality = cardinality;
		}

		public boolean isNoise() {
			return isNoise;
		}

		public void setNoise(boolean isNoise) {
			this.isNoise = isNoise;
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return String.valueOf(cardinality);
		}

	}

	public float getNoiseThresh() {
		return noiseThresh;
	}

	public void setNoiseThresh(float noiseThresh) {
		this.noiseThresh = noiseThresh;
	}

	public float getLoopThresh() {
		return loopThresh;
	}

	public void setLoopThresh(float loopThresh) {
		this.loopThresh = loopThresh;
	}

	public XLog getLog() {
		return log;
	}

	public XLogInfo getLogSummary() {
		return logSummary;
	}

	public XEventClasses getEventClasses() {
		return eventClasses;
	}

	public Map<Pair<XEventClass, XEventClass>, Double> getCausalRelations() {
		return causalRelations;
	}

	public Map<Pair<XEventClass, XEventClass>, Double> getParallelRelations() {
		return parallelRelations;
	}

	public Map<XEventClass, Integer> getStartClasses() {
		return startClasses;
	}

	public Map<XEventClass, Integer> getEndClasses() {
		return endClasses;
	}

	public Map<XEventClass, Integer> getSelfLoopRelations() {
		return selfLoopRelations;
	}

	public Map<Pair<XEventClass, XEventClass>, RelationCardinality> getDfRelations() {
		return dfRelations;
	}

	public Map<Pair<XEventClass, XEventClass>, Set<XTrace>> getExistDirect() {
		return existDirect;
	}

	public Map<Pair<XEventClass, XEventClass>, RelationCardinality> getTwoLoopRelations() {
		return twoLoopRelations;
	}



}
