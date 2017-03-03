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
package org.apromore.prodrift.logabstraction.extension;

import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.apromore.prodrift.config.BehaviorRelation;
import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.framework.plugin.Progress;
import org.processmining.framework.providedobjects.SubstitutionType;
import org.processmining.framework.util.Pair;
import org.processmining.plugins.log.logabstraction.BasicLogRelations;
import org.processmining.plugins.log.logabstraction.LogRelations;

@SubstitutionType(substitutedType = LogRelations.class)
@Deprecated
public class BasicLogRelationsExt extends BasicLogRelations {

	public BasicLogRelationsExt(XLog log) {
		super(log);
		// TODO Auto-generated constructor stub
	}

	public BasicLogRelationsExt(XLog log, XLogInfo summary) {
		super(log, summary);
	}

	public BasicLogRelationsExt(XLog log, Progress progress) {
		super(log, progress);
	}

	public BasicLogRelationsExt(XLog log, XLogInfo summary, Progress progress) {
		super(log, summary, progress);
	}

	public Entry<Pair<XEventClass, XEventClass>, BehaviorRelation> updateDirectSuccessionMatrices(XTrace trace, XEvent event, boolean add, boolean front) {
		int n;
		
		Entry<Pair<XEventClass, XEventClass>, BehaviorRelation> output = null;
		
		if(add)
		{
			getEventClasses().register(event);
			getEventClasses().harmonizeIndices();
			// add an event
			if(front)
			{
				// add an event to the front of the trace
				int traceSize = trace.size();
				if(traceSize > 0)
				{
					
					XEventClass fromEvent = getEventClasses().getClassOf(trace.get(traceSize - 1));
					XEventClass toEvent = getEventClasses().getClassOf(event);
					
//					if(fromEvent.getId().compareToIgnoreCase("END") == 0 || 
//							toEvent.getId().compareToIgnoreCase("START") == 0)
//						System.out.println();
					
					Pair<XEventClass, XEventClass> pair = new Pair<XEventClass, XEventClass>(fromEvent, toEvent);
					
					// check for 2-loop
					if ((trace.size() - 2 >= 0) && !fromEvent.equals(toEvent)) 
					{
						XEventClass fromfromEvent = getEventClasses().getClassOf(trace.get(traceSize - 2));
						if (fromfromEvent.equals(toEvent)) 
						{
							// Pattern is ABA
							Pair<XEventClass, XEventClass> opposed = new Pair<XEventClass, XEventClass>(pair.getSecond(),
									pair.getFirst());
							n = getLengthTwoLoops().containsKey(opposed) ? getLengthTwoLoops().get(opposed) : 0;
							getLengthTwoLoops().put(opposed, n + 1);
						}
					}
					
					// update direct successions dependencies
					n = getDirectFollowsDependencies().containsKey(pair) ? getDirectFollowsDependencies().get(pair) : 0;
					getDirectFollowsDependencies().put(pair, n + 1);
					Set<XTrace> traces = getCountDirect().containsKey(pair) ? getCountDirect().get(pair) : new HashSet<XTrace>();
					traces.add(trace);
					getCountDirect().put(pair, traces);
					
					// update the last event
					XEventClass lastEvent = toEvent;
					n = getEndTraceInfo().containsKey(lastEvent) ? getEndTraceInfo().get(lastEvent) : 0;
					getEndTraceInfo().put(lastEvent, n + 1);
					
					// add the event to the trace
					trace.add(event);
					
					// update basic relations based on the new pair
					BehaviorRelation br = updateBasicRelations(pair, true);
					
					output = new AbstractMap.SimpleEntry<Pair<XEventClass, XEventClass>, BehaviorRelation>(pair, br);
					
					
				}else
				{
					// update the first event
					XEventClass firstEvent = getEventClasses().getClassOf(event);
					// Count initial events
					n = getStartTraceInfo().containsKey(firstEvent) ? getStartTraceInfo().get(firstEvent) : 0;
					getStartTraceInfo().put(firstEvent, n + 1);
					
					// update the last event
					XEventClass lastEvent = getEventClasses().getClassOf(event);
					n = getEndTraceInfo().containsKey(lastEvent) ? getEndTraceInfo().get(lastEvent) : 0;
					getEndTraceInfo().put(lastEvent, n + 1);
					
					// add the event to the trace
					trace.add(event);
					
					output = new AbstractMap.SimpleEntry<Pair<XEventClass, XEventClass>, BehaviorRelation>(null, null);
					
				}
				
			}else
			{
				// add an event to the back of the trace
				int traceSize = trace.size();
				if(traceSize > 0)
				{
					
					XEventClass fromEvent = getEventClasses().getClassOf(event);
					XEventClass toEvent = getEventClasses().getClassOf(trace.get(0));
					Pair<XEventClass, XEventClass> pair = new Pair<XEventClass, XEventClass>(fromEvent, toEvent);
					
					// check for 2-loop
					if ((trace.size() - 2 >= 0) && !fromEvent.equals(toEvent)) 
					{
						XEventClass totoEvent = getEventClasses().getClassOf(trace.get(1));
						if (fromEvent.equals(totoEvent)) 
						{
							// Pattern is ABA
							n = getLengthTwoLoops().containsKey(pair) ? getLengthTwoLoops().get(pair) : 0;
							getLengthTwoLoops().put(pair, n + 1);
						}
					}
					
					// update direct successions dependencies
					n = getDirectFollowsDependencies().containsKey(pair) ? getDirectFollowsDependencies().get(pair) : 0;
					getDirectFollowsDependencies().put(pair, n + 1);
					Set<XTrace> traces = getCountDirect().containsKey(pair) ? getCountDirect().get(pair) : new HashSet<XTrace>();
					traces.add(trace);
					getCountDirect().put(pair, traces);
					
					// update the first event
					XEventClass firstEvent = getEventClasses().getClassOf(event);
					// Count initial events
					n = getStartTraceInfo().containsKey(firstEvent) ? getStartTraceInfo().get(firstEvent) : 0;
					getStartTraceInfo().put(firstEvent, n + 1);
					
					// add the event to the trace
					trace.add(0, event);
					
					// update basic relations based on the new pair
					BehaviorRelation br = updateBasicRelations(pair, true);
					
					output = new AbstractMap.SimpleEntry<Pair<XEventClass, XEventClass>, BehaviorRelation>(pair, br);
					
					
				}else
				{
					// update the first event
					XEventClass firstEvent = getEventClasses().getClassOf(event);
					// Count initial events
					n = getStartTraceInfo().containsKey(firstEvent) ? getStartTraceInfo().get(firstEvent) : 0;
					getStartTraceInfo().put(firstEvent, n + 1);
					
					// update the last event
					XEventClass lastEvent = getEventClasses().getClassOf(event);
					n = getEndTraceInfo().containsKey(lastEvent) ? getEndTraceInfo().get(lastEvent) : 0;
					getEndTraceInfo().put(lastEvent, n + 1);
					
					// add the event to the trace
					trace.add(0, event);
					
					output = new AbstractMap.SimpleEntry<Pair<XEventClass, XEventClass>, BehaviorRelation>(null, null);
					
				}
			}
			
		}else
		{
			// remove an event
			
			if(front)
			{
				
				// remove an event from the front of the trace
				int traceSize = trace.size();
				if(traceSize > 1)
				{
					
					XEventClass fromEvent = getEventClasses().getClassOf(trace.get(traceSize - 2));
					XEventClass toEvent = getEventClasses().getClassOf(event);
					Pair<XEventClass, XEventClass> pair = new Pair<XEventClass, XEventClass>(fromEvent, toEvent);
					
					// check for 2-loop
					if ((trace.size() - 3 >= 0) && !fromEvent.equals(toEvent)) 
					{
						XEventClass fromfromEvent = getEventClasses().getClassOf(trace.get(traceSize - 3));
						if (fromfromEvent.equals(toEvent)) 
						{
							// Pattern is ABA
							Pair<XEventClass, XEventClass> opposed = new Pair<XEventClass, XEventClass>(pair.getSecond(),
									pair.getFirst());
							n = getLengthTwoLoops().containsKey(opposed) ? getLengthTwoLoops().get(opposed) : 0;
							if(n - 1 <= 0)
								getLengthTwoLoops().remove(opposed);
							else
								getLengthTwoLoops().put(opposed, n - 1);
						}
					}
					
					// update direct successions dependencies
					n = getDirectFollowsDependencies().containsKey(pair) ? getDirectFollowsDependencies().get(pair) : 0;
					if(n - 1 <= 0)
						getDirectFollowsDependencies().remove(pair);
					else
						getDirectFollowsDependencies().put(pair, n - 1);
					
					// remove the event from the trace
					trace.remove(event);
					
					if(getCountDirect().containsKey(pair))
					{
						
						Set<XTrace> traces = getCountDirect().get(pair);
						if(!hasPair(trace, pair))
							traces.remove(trace);
							
						if(traces.size() == 0)
							getCountDirect().remove(pair);
						else
							getCountDirect().put(pair, traces);
						
					}
					
					// update the last event
					XEventClass lastEvent = toEvent;
					n = getEndTraceInfo().containsKey(lastEvent) ? getEndTraceInfo().get(lastEvent) : 0;
					if(n - 1 <= 0)
						getEndTraceInfo().remove(lastEvent);
					else
						getEndTraceInfo().put(lastEvent, n - 1);
					
					if(trace.size() != 0)
					{
						
						lastEvent = getEventClasses().getClassOf(trace.get(trace.size() - 1));
						n = getEndTraceInfo().containsKey(lastEvent) ? getEndTraceInfo().get(lastEvent) : 0;
						getEndTraceInfo().put(lastEvent, n + 1);
						
					}
					
					
					// update basic relations based on the removed pair
					BehaviorRelation br = updateBasicRelations(pair, true);
					
					output = new AbstractMap.SimpleEntry<Pair<XEventClass, XEventClass>, BehaviorRelation>(pair, br);
					
					
				}else
				{
					// update the first event
					XEventClass firstEvent = getEventClasses().getClassOf(event);
					// Count initial events
					n = getStartTraceInfo().containsKey(firstEvent) ? getStartTraceInfo().get(firstEvent) : 0;
					if(n - 1 <= 0)
						getStartTraceInfo().remove(firstEvent);
					else
						getStartTraceInfo().put(firstEvent, n - 1);
					
					// update the last event
					XEventClass lastEvent = getEventClasses().getClassOf(event);
					n = getEndTraceInfo().containsKey(lastEvent) ? getEndTraceInfo().get(lastEvent) : 0;
					if(n - 1 <= 0)
						getEndTraceInfo().remove(lastEvent);
					else
						getEndTraceInfo().put(lastEvent, n - 1);
					
					// remove the event from the trace
					trace.remove(event);
					
					output = new AbstractMap.SimpleEntry<Pair<XEventClass, XEventClass>, BehaviorRelation>(null, null);
					
				}
				
			}else
			{
				
				// remove an event from the back of the trace
				int traceSize = trace.size();
				if(traceSize > 1)
				{
					
					XEventClass fromEvent = getEventClasses().getClassOf(event);
					XEventClass toEvent = getEventClasses().getClassOf(trace.get(1));
					Pair<XEventClass, XEventClass> pair = new Pair<XEventClass, XEventClass>(fromEvent, toEvent);
					
					// check for 2-loop
					try{
						
						if ((trace.size() - 3 >= 0) && !fromEvent.equals(toEvent)) 
						{
							XEventClass totoEvent = getEventClasses().getClassOf(trace.get(2));
							if (fromEvent.equals(totoEvent)) 
							{
								// Pattern is ABA
								n = getLengthTwoLoops().containsKey(pair) ? getLengthTwoLoops().get(pair) : 0;
								if(n - 1 <= 0)
									getLengthTwoLoops().remove(pair);
								else
									getLengthTwoLoops().put(pair, n - 1);
							}
						}
						
					}catch (Exception e) {
						System.out.println();
					}
					
					
					// update direct successions dependencies
					n = getDirectFollowsDependencies().containsKey(pair) ? getDirectFollowsDependencies().get(pair) : 0;
					if(n - 1 <= 0)
						getDirectFollowsDependencies().remove(pair);
					else
						getDirectFollowsDependencies().put(pair, n - 1);
					
					// remove the event from the trace
					trace.remove(event);
					
					if(getCountDirect().containsKey(pair))
					{
						
						Set<XTrace> traces = getCountDirect().get(pair);
						if(!hasPair(trace, pair))
							traces.remove(trace);
							
						if(traces.size() == 0)
							getCountDirect().remove(pair);
						else
							getCountDirect().put(pair, traces);
						
					}
					
					// update the first event
					XEventClass firstEvent = getEventClasses().getClassOf(event);
					// Count initial events
					n = getStartTraceInfo().containsKey(firstEvent) ? getStartTraceInfo().get(firstEvent) : 0;
					if(n - 1 <= 0)
						getStartTraceInfo().remove(firstEvent);
					else
						getStartTraceInfo().put(firstEvent, n - 1);
					
					if(trace.size() != 0)
					{
						
						firstEvent = getEventClasses().getClassOf(trace.get(0));
						n = getStartTraceInfo().containsKey(firstEvent) ? getStartTraceInfo().get(firstEvent) : 0;
						getStartTraceInfo().put(firstEvent, n + 1);
						
					}
					
					// update basic relations based on the removed pair
					BehaviorRelation br = updateBasicRelations(pair, true);
					
					output = new AbstractMap.SimpleEntry<Pair<XEventClass, XEventClass>, BehaviorRelation>(pair, br);
					
					
				}else
				{
					// update the first event
					XEventClass firstEvent = getEventClasses().getClassOf(event);
					// Count initial events
					n = getStartTraceInfo().containsKey(firstEvent) ? getStartTraceInfo().get(firstEvent) : 0;
					if(n - 1 <= 0)
						getStartTraceInfo().remove(firstEvent);
					else
						getStartTraceInfo().put(firstEvent, n - 1);
					
					// update the last event
					XEventClass lastEvent = getEventClasses().getClassOf(event);
					n = getEndTraceInfo().containsKey(lastEvent) ? getEndTraceInfo().get(lastEvent) : 0;
					if(n - 1 <= 0)
						getEndTraceInfo().remove(lastEvent);
					else
						getEndTraceInfo().put(lastEvent, n - 1);
					
					// remove the event from the trace
					trace.remove(event);
					
					output = new AbstractMap.SimpleEntry<Pair<XEventClass, XEventClass>, BehaviorRelation>(null, null);
					
				}
				
			}
			
			unRegister(event);
			
		}
		
		return output;
		
	}
	
	
	public BehaviorRelation updateBasicRelations(Pair<XEventClass, XEventClass> pair, boolean shortLoops)
	{
		
		BehaviorRelation br = BehaviorRelation.Causal;
		
		Pair<XEventClass, XEventClass> opposed = new Pair<XEventClass, XEventClass>(pair.getSecond(),
				pair.getFirst());
		
		// remove the current relation
		getParallelRelations().remove(pair);
		getParallelRelations().remove(opposed);
		getCausalDependencies().remove(pair);
		getCausalDependencies().remove(opposed);
		getLengthOneLoops().remove(pair.getFirst());
		
		if (getDirectFollowsDependencies().containsKey(opposed)) 
		{
			// two loop or parallel relation
			if (shortLoops && (getLengthTwoLoops().containsKey(pair) || getLengthTwoLoops().containsKey(opposed))) 
			{
				// two causal dependencies
				if(getLengthTwoLoops().containsKey(pair) && getLengthTwoLoops().containsKey(opposed))
				{
					
					br = BehaviorRelation.Length_Two_Loop_bi;
					
				}else if(getLengthTwoLoops().containsKey(pair))
				{
					
					br = BehaviorRelation.Length_Two_Loop_ABA;
					
				}else if(getLengthTwoLoops().containsKey(opposed))
				{
					
					br = BehaviorRelation.Length_Two_Loop_BAB;
					
				}
				getCausalDependencies().put(pair, 1.0);
				getCausalDependencies().put(opposed, 1.0);
				
			} else 
			{
//				if(pair.getFirst() == null || pair.getSecond() == null)
//					System.out.println();
				Integer n = getDirectFollowsDependencies().get(pair);
				if (shortLoops && (pair.getFirst().equals(pair.getSecond()))) 
				{
					
					getLengthOneLoops().put(pair.getFirst(), n);
					br = BehaviorRelation.Length_One_Loop;
					
				}else if(n == null)
				{
					// in case of removing an event
					getParallelRelations().remove(pair);
					getParallelRelations().remove(opposed);
					
					getCausalDependencies().put(opposed, 1.0);
					br = BehaviorRelation.INV_Causal;
					
				}else
				{
					
					getParallelRelations().put(pair, 1.0);
					getParallelRelations().put(opposed, 1.0);
					br = BehaviorRelation.CONCURRENCY;
					
				}
				
			}
		} else {
			// causal relation
			Integer n = getDirectFollowsDependencies().get(pair);
			if(n == null)
			{
				
				getCausalDependencies().remove(pair);  // in case of removing an event
				br = null;
				
			}else
			{
				
				getCausalDependencies().put(pair, 1.0);
				br = BehaviorRelation.Causal;
				
			}
			
		}
		
		return br;
		
	}
	
	
	private boolean hasPair(XTrace trace, Pair<XEventClass, XEventClass> pair)
	{
		
		for(int i = 0; i < trace.size() - 1; i++)
		{
			
			if(getEventClasses().getClassOf(trace.get(i)).equals(pair.getFirst()) && getEventClasses().getClassOf(trace.get(i+1)).equals(pair.getSecond()))
				return true;
			
		}
		
		return false;
		
	}
	
	public void unRegister(XEvent event) {
		unRegister(getEventClasses().getClassifier().getClassIdentity(event));
	}
	
	public synchronized void unRegister(String classId) {
		XEventClass eventClass = getEventClasses().getByIdentity(classId);
		if (eventClass != null && classId != null) {
			if(eventClass.size() == 1)
				getEventClasses().getClasses().remove(classId);
			else
				decrementSize(eventClass);
		}
		
	}
	
	public void decrementSize(XEventClass eventClass)
	{
		eventClass.setSize(eventClass.size() - 1);
	}

}
