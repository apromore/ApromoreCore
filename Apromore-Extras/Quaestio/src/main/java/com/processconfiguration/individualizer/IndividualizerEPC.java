/*
 * Copyright © 2009-2018 The Apromore Initiative.
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
package com.processconfiguration.individualizer;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import epml.TEpcElement;
import epml.TypeAND;
import epml.TypeArc;
import epml.TypeDirectory;
import epml.TypeEPC;
import epml.TypeEPML;
import epml.TypeEvent;
import epml.TypeFlow;
import epml.TypeFunction;
import epml.TypeGraphics;
import epml.TypeOR;
import epml.TypePosition;
import epml.TypeXOR;

public class IndividualizerEPC {

	TypeEPML model = null;
	Map<String, String> ns = null;
	TypeEPC epc;
	File fInModel = null;
	File fOutModel = null;
	//String fOutName = null;
	int maxID;
	static final int xOff = 10;
	static int yOff;
	List<String> deadArcs = null;
	JAXBContext jc;
	Unmarshaller u;
	HashMap<Integer, TypeArc> ArcsMap;
	HashMap<Integer, TypeFunction> FunctionsMap;
	HashMap<Integer, TypeEvent> EventsMap;
	HashMap<Integer, TypeOR> OrsMap;
	HashMap<Integer, TypeXOR> XorsMap;
	HashMap<Integer, TypeAND> AndsMap;

	public IndividualizerEPC(File fInModel) {
		this.fInModel = fInModel;
		commit();
	}

	public File commit() {// public int commit() {
		try {
			String value;
			int id;
			deadArcs = new ArrayList<String>();
			JAXBContext jc = JAXBContext.newInstance("epml");
			Unmarshaller u = jc.createUnmarshaller();
			JAXBElement epcElement = (JAXBElement) u.unmarshal(fInModel);
			model = (TypeEPML) epcElement.getValue();
			epc = retrieveMainEPC();

			retrieveMaxId();
			createSets();

			// for all the configurable functions
			for (Iterator<TypeFunction> iFunc = epc.getFunctions().iterator(); iFunc
					.hasNext();) {// for (TypeFunction cFunc :
									// epc.getFunctions()) {
				TypeFunction cFunc = iFunc.next();
				// if configurable
				if (cFunc.getConfigurableFunction() != null
						&& cFunc.getConfigurableFunction().getConfiguration() != null) {
					id = cFunc.getId().intValue();
					value = cFunc.getConfigurableFunction().getConfiguration()
							.getValue();
					if (value.equals("off")) {
						fixRemoval(id, -1, null);
						// remove element by using iterator
						if (FunctionsMap.get(id) != null) {
							iFunc.remove();
						}
					} else if (value.equals("opt")) {
						fixFOpt(cFunc);
					}
					cFunc.setConfigurableFunction(null);
				}
			}
			// ALL THE SPLITS
			// for all the configurable or. TODO: perhaps all the connectors
			// could be grouped
			for (Iterator<TypeOR> iOR = epc.getOR().iterator(); iOR.hasNext();) {
				TypeOR cOR = iOR.next();
				if (performConfiguration(cOR, "or", true)) {
					iOR.remove();// remove element after configuration
				}
			}

			// for all the configurable xor
			for (Iterator<TypeXOR> iXOR = epc.getXOR().iterator(); iXOR
					.hasNext();) {
				TypeXOR cXOR = iXOR.next();
				if (performConfiguration(cXOR, "xor", true)) {
					iXOR.remove();
				}
			}
			// for all the configurable and: TODO: just 'and' or 'seq'
			for (Iterator<TypeAND> iAND = epc.getAND().iterator(); iAND
					.hasNext();) {
				TypeAND cAND = iAND.next();
				if (performConfiguration(cAND, "and", true)) {
					iAND.remove();
				}
			}

			// ALL THE JOINS
			for (Iterator<TypeOR> iOR = epc.getOR().iterator(); iOR.hasNext();) {
				TypeOR cOR = iOR.next();
				if (performConfiguration(cOR, "or", false)) {
					iOR.remove();
				}
			}

			for (Iterator<TypeXOR> iXOR = epc.getXOR().iterator(); iXOR
					.hasNext();) {
				TypeXOR cXOR = iXOR.next();
				if (performConfiguration(cXOR, "xor", false)) {
					iXOR.remove();
				}
			}

			for (Iterator<TypeAND> iAND = epc.getAND().iterator(); iAND
					.hasNext();) {
				TypeAND cAND = iAND.next();
				if (performConfiguration(cAND, "and", false)) {
					iAND.remove();
				}
			}

			removeDeadArcs();

			// closeFile();
			fOutModel = new File("temp.epml");
			fOutModel.createNewFile();

			Marshaller marshaller = jc.createMarshaller();
			marshaller.marshal(epcElement, fOutModel);

			// fOutModel.deleteOnExit();

			// return 0;// positive termination
			return fOutModel;

		} catch (Exception e) {
			e.printStackTrace();
			return fOutModel;
			// return 1;// negative termination: some problem has occurred TODO:
			// verify all the cases and read the model type: choose
			// the algorithm accordingly
		}
	}

	private void createSets() {
		// create ArcMap
		ArcsMap = new HashMap<Integer, TypeArc>();
		for (TypeArc arc : epc.getArcs()) {
			ArcsMap.put(arc.getId().intValue(), arc);
		}
		// create FunctionMap
		FunctionsMap = new HashMap<Integer, TypeFunction>();
		for (TypeFunction function : epc.getFunctions()) {
			FunctionsMap.put(function.getId().intValue(), function);
		}
		// create EventMap
		EventsMap = new HashMap<Integer, TypeEvent>();
		for (TypeEvent event : epc.getEvents()) {
			EventsMap.put(event.getId().intValue(), event);
		}
		// create OrMap
		OrsMap = new HashMap<Integer, TypeOR>();
		for (TypeOR or : epc.getOR()) {
			OrsMap.put(or.getId().intValue(), or);
		}
		// create XorMap
		XorsMap = new HashMap<Integer, TypeXOR>();
		for (TypeXOR xor : epc.getXOR()) {
			XorsMap.put(xor.getId().intValue(), xor);
		}
		// create AndMap
		AndsMap = new HashMap<Integer, TypeAND>();
		for (TypeAND and : epc.getAND()) {
			AndsMap.put(and.getId().intValue(), and);
		}
	}

	private void removeDeadArcs() {
		for (String arcString : deadArcs) {
			if (ArcsMap.get(Integer.parseInt(arcString)) != null) {
				for (Iterator<TypeArc> iArc = epc.getArcs().iterator(); iArc
						.hasNext();) {
					if (iArc.next().getId().intValue() == Integer
							.parseInt(arcString)) {
						iArc.remove();
					}
				}
			}
		}

	}

	// for each connector: first do all the splits, then all the joins, finally
	// remove all the disconnected arcs, that now are only traversed
	private boolean performConfiguration(TEpcElement connector, String type,
			boolean firstSplits) throws Exception {
		List<TypeArc> l = null;
		if (connector instanceof TypeOR) {
			TypeOR confC = (TypeOR) connector;
			// if configurable
			if (confC.getConfigurableConnector() != null
					&& confC.getConfigurableConnector().getConfiguration() != null) {
				int id = confC.getId().intValue();
				if (firstSplits && (l = getSplitArcs(id)).size() > 1) {
					String value = confC.getConfigurableConnector()
							.getConfiguration().getValue();
					if (!value.equals("seq")) {
						generateNewConnector(connector, value);
						// remove element
						if (OrsMap.get(id) != null) {
							return true;// return true to remove element in the
										// main loop
						}
					} else {// sequence
						int goTo = confC.getConfigurableConnector()
								.getConfiguration().getGoto().intValue();
						// connect to the sequence
						fixRemoval(confC.getId().intValue(), goTo, "split");
						for (TypeArc cArc : l) {// for each outgoing arc of the
												// split
							if (cArc.getId().intValue() != goTo) {// if not the
																	// chosen
																	// sequence
								removeNextNodes(cArc, true, null);// remove the
																	// sequence
							}
						}
						// remove element
						if (OrsMap.get(id) != null) {
							return true;// return true to remove element in the
										// main loop
						}
					}
				} else if (!firstSplits && (l = getJoinArcs(id)).size() > 1) {
					String value = confC.getConfigurableConnector()
							.getConfiguration().getValue();
					if (!value.equals("seq")) {
						generateNewConnector(connector, value);
						// remove element
						if (OrsMap.get(id) != null) {
							return true;// return true to remove element in the
										// main loop
						}
					} else {
						int goTo = confC.getConfigurableConnector()
								.getConfiguration().getGoto().intValue();
						// connect from the sequence
						fixRemoval(confC.getId().intValue(), goTo, "join");
						for (TypeArc cArc : l) {// for each outgoing arc of the
												// join
							if (cArc.getId().intValue() != goTo) {// if not the
																	// chosen
																	// sequence
								removeNextNodes(cArc, false, null);// remove the
																	// sequence
							}
						}
						// remove element
						if (OrsMap.get(id) != null) {
							return true;// return true to remove element in the
										// main loop
						}
					}
				}
			}
		} else if (connector instanceof TypeXOR) {
			TypeXOR confC = (TypeXOR) connector;
			// if configurable
			if (confC.getConfigurableConnector() != null
					&& confC.getConfigurableConnector().getConfiguration() != null) {
				int id = confC.getId().intValue();
				if (firstSplits && (l = getSplitArcs(id)).size() > 1) {
					String value = confC.getConfigurableConnector()
							.getConfiguration().getValue();
					if (!value.equals("seq")) {
						generateNewConnector(connector, value);
						if (XorsMap.get(id) != null) {
							return true;// return true to remove element in the
										// main loop
						}
					} else {// sequence
						int goTo = confC.getConfigurableConnector()
								.getConfiguration().getGoto().intValue();
						// connect to the sequence
						fixRemoval(confC.getId().intValue(), goTo, "split");
						for (TypeArc cArc : l) {// for each outgoing arc of the
												// split
							if (cArc.getId().intValue() != goTo) {// if not the
																	// chosen
																	// sequence
								removeNextNodes(cArc, true, null);// remove the
																	// sequence
							}
						}
						if (XorsMap.get(id) != null) {
							return true;// return true to remove element in the
										// main loop
						}
					}
				} else if (!firstSplits && (l = getJoinArcs(id)).size() > 1) {
					String value = confC.getConfigurableConnector()
							.getConfiguration().getValue();
					if (!value.equals("seq")) {
						generateNewConnector(connector, value);
						if (XorsMap.get(id) != null) {
							return true;// return true to remove element in the
										// main loop
						}
					} else {
						int goTo = confC.getConfigurableConnector()
								.getConfiguration().getGoto().intValue();
						// connect from the sequence
						fixRemoval(confC.getId().intValue(), goTo, "join");
						for (TypeArc cArc : l) {// for each outgoing arc of the
												// join
							if (cArc.getId().intValue() != goTo) {// if not the
																	// chosen
																	// sequence
								removeNextNodes(cArc, false, null);// remove the
																	// sequence
							}
						}
						if (XorsMap.get(id) != null) {
							return true;// return true to remove element in the
										// main loop
						}
					}
				}
			}
		} else if (connector instanceof TypeAND) {
			TypeAND confC = (TypeAND) connector;
			// if configurable
			if (confC.getConfigurableConnector() != null
					&& confC.getConfigurableConnector().getConfiguration() != null) {
				int id = confC.getId().intValue();
				if (firstSplits && (l = getSplitArcs(id)).size() > 1) {
					String value = confC.getConfigurableConnector()
							.getConfiguration().getValue();
					if (!value.equals("seq")) {
						generateNewConnector(connector, value);
						if (AndsMap.get(id) != null) {
							return true;// return true to remove element in the
										// main loop
						}
					} else {// sequence
						int goTo = confC.getConfigurableConnector()
								.getConfiguration().getGoto().intValue();
						// connect to the sequence
						fixRemoval(confC.getId().intValue(), goTo, "split");
						for (TypeArc cArc : l) {// for each outgoing arc of the
												// split
							if (cArc.getId().intValue() != goTo) {// if not the
																	// chosen
																	// sequence
								removeNextNodes(cArc, true, null);// remove the
																	// sequence
							}
						}
						if (AndsMap.get(id) != null) {
							return true;// return true to remove element in the
										// main loop
						}
					}
				} else if (!firstSplits && (l = getJoinArcs(id)).size() > 1) {
					String value = confC.getConfigurableConnector()
							.getConfiguration().getValue();
					if (!value.equals("seq")) {
						generateNewConnector(connector, value);
						if (AndsMap.get(id) != null) {
							return true;// return true to remove element in the
										// main loop
						}
					} else {
						int goTo = confC.getConfigurableConnector()
								.getConfiguration().getGoto().intValue();
						// connect from the sequence
						fixRemoval(confC.getId().intValue(), goTo, "join");
						for (TypeArc cArc : l) {// for each outgoing arc of the
												// join
							if (cArc.getId().intValue() != goTo) {// if not the
																	// chosen
																	// sequence
								removeNextNodes(cArc, false, null);// remove the
																	// sequence
							}
						}
						if (AndsMap.get(id) != null) {
							return true;// return true to remove element in the
										// main loop
						}
					}
				}
			}
		}
		return false;
	}// TODO:Reduction rule: for each connector, if isSplit==false and
		// isJoin==false, then reduce: delete connector, connect source arc
		// properly and delete target arc

	private void removeNextNodes(TypeArc next, boolean searchJoin,
			TEpcElement nNext) throws Exception {
		if (nNext == null && next != null) {// TypeArc is passed to this method
			int id = next.getId().intValue();
			if (searchJoin) {// search for any select which its id equal
								// arc.getFlow().getTarget()
				if (FunctionsMap.get(next.getFlow().getTarget().intValue()) != null) {
					nNext = FunctionsMap.get(next.getFlow().getTarget()
							.intValue());
				} else if (EventsMap.get(next.getFlow().getTarget().intValue()) != null) {
					nNext = EventsMap
							.get(next.getFlow().getTarget().intValue());
				} else if (OrsMap.get(next.getFlow().getTarget().intValue()) != null) {
					nNext = OrsMap.get(next.getFlow().getTarget().intValue());
				} else if (XorsMap.get(next.getFlow().getTarget().intValue()) != null) {
					nNext = XorsMap.get(next.getFlow().getTarget().intValue());
				} else if (AndsMap.get(next.getFlow().getTarget().intValue()) != null) {
					nNext = AndsMap.get(next.getFlow().getTarget().intValue());
				}
				if (nNext == null)
					return;
			} else {
				if (FunctionsMap.get(next.getFlow().getSource().intValue()) != null) {
					nNext = FunctionsMap.get(next.getFlow().getSource()
							.intValue());
				} else if (EventsMap.get(next.getFlow().getSource().intValue()) != null) {
					nNext = EventsMap
							.get(next.getFlow().getSource().intValue());
				} else if (OrsMap.get(next.getFlow().getSource().intValue()) != null) {
					nNext = OrsMap.get(next.getFlow().getSource().intValue());
				} else if (XorsMap.get(next.getFlow().getSource().intValue()) != null) {
					nNext = XorsMap.get(next.getFlow().getSource().intValue());
				} else if (AndsMap.get(next.getFlow().getSource().intValue()) != null) {
					nNext = AndsMap.get(next.getFlow().getSource().intValue());
				}
				if (nNext == null)
					return;// next==null
			}
			deadArcs.add("" + id);
			removeNextNodes(null, searchJoin, nNext);
			return;
		} else {// Other types
				// first check type of nNext
			if (nNext instanceof TypeOR) {// TypeOR
				int id = nNext.getId().intValue();
				if (searchJoin) {// we're looking for a join to stop
					if (getJoinArcs(id).size() > 1)
						return;// 0=>found join
					for (TypeArc cArc : epc.getArcs()) {
						if (cArc.getFlow().getSource().intValue() == id) {
							next = cArc;// find next arc element
							break;
						}
					}
					for (Iterator<TypeOR> iOR = epc.getOR().iterator(); iOR
							.hasNext();) {
						if (iOR.next().getId().intValue() == id
								&& OrsMap.get(id) != null) {
							iOR.remove();// remove or
							break;
						}
					}
					removeNextNodes(next, searchJoin, null);
				} else {
					if (getSplitArcs(id).size() > 1)
						return;// 0=>found split
					else {
						for (TypeArc cArc : epc.getArcs()) {
							if (cArc.getFlow().getTarget().intValue() == id) {
								next = cArc;// find next arc element
								break;
							}
						}
						for (Iterator<TypeOR> iOR = epc.getOR().iterator(); iOR
								.hasNext();) {
							if (iOR.next().getId().intValue() == id
									&& OrsMap.get(id) != null) {// if(iOR.next().getId().intValue()==id
																// &&
																// getIndex(nIndex)!=-1){
								iOR.remove();// remove or
								break;
							}
						}
						removeNextNodes(next, searchJoin, null);
					}
				}
			} else if (nNext instanceof TypeXOR) {// TypeXOR
				int id = nNext.getId().intValue();
				if (searchJoin) {// we're looking for a join to stop
					if (getJoinArcs(id).size() > 1)
						return;// 0=>found join
					for (TypeArc cArc : epc.getArcs()) {
						if (cArc.getFlow().getSource().intValue() == id) {
							next = cArc;// find next arc element
							break;
						}
					}
					for (Iterator<TypeXOR> iXOR = epc.getXOR().iterator(); iXOR
							.hasNext();) {
						if (iXOR.next().getId().intValue() == id
								&& XorsMap.get(id) != null) {// if(iXOR.next().getId().intValue()==id
																// &&
																// getIndex(nIndex)!=-1){
							iXOR.remove();// remove or
							break;
						}
					}
					removeNextNodes(next, searchJoin, null);
				} else {
					if (getSplitArcs(id).size() > 1)
						return;// 0=>found split
					else {
						for (TypeArc cArc : epc.getArcs()) {
							if (cArc.getFlow().getTarget().intValue() == id) {
								next = cArc;// find next arc element
								break;
							}
						}
						for (Iterator<TypeXOR> iXOR = epc.getXOR().iterator(); iXOR
								.hasNext();) {
							if (iXOR.next().getId().intValue() == id
									&& XorsMap.get(id) != null) {// if(iXOR.next().getId().intValue()==id
																	// &&
																	// getIndex(nIndex)!=-1){
								iXOR.remove();// remove or
								break;
							}
						}
						removeNextNodes(next, searchJoin, null);
					}
				}
			} else if (nNext instanceof TypeAND) {// TypeAND
				int id = nNext.getId().intValue();
				if (searchJoin) {// we're looking for a join to stop
					if (getJoinArcs(id).size() > 1)
						return;// 0=>found join
					for (TypeArc cArc : epc.getArcs()) {
						if (cArc.getFlow().getSource().intValue() == id) {
							next = cArc;// find next arc element
							break;
						}
					}
					for (Iterator<TypeAND> iAND = epc.getAND().iterator(); iAND
							.hasNext();) {
						if (iAND.next().getId().intValue() == id
								&& AndsMap.get(id) != null) {// if(iAND.next().getId().intValue()==id
																// &&
																// getIndex(nIndex)!=-1){
							iAND.remove();// remove or
							break;
						}
					}
					removeNextNodes(next, searchJoin, null);
				} else {
					if (getSplitArcs(id).size() > 1)
						return;// 0=>found split
					else {
						for (TypeArc cArc : epc.getArcs()) {
							if (cArc.getFlow().getTarget().intValue() == id) {
								next = cArc;// find next arc element
								break;
							}
						}
						for (Iterator<TypeAND> iAND = epc.getAND().iterator(); iAND
								.hasNext();) {
							if (iAND.next().getId().intValue() == id
									&& AndsMap.get(id) != null) {// if(iAND.next().getId().intValue()==id
																	// &&
																	// getIndex(nIndex)!=-1){
								iAND.remove();// remove or
								break;
							}
						}
						removeNextNodes(next, searchJoin, null);
					}
				}
			} else if (nNext instanceof TypeFunction) {// TypeFunction
				int id = nNext.getId().intValue();
				for (Iterator<TypeFunction> iFunc = epc.getFunctions()
						.iterator(); iFunc.hasNext();) {
					if (iFunc.next().getId().intValue() == id
							&& FunctionsMap.get(id) != null) {// if(iFunc.next().getId().intValue()==id
																// &&
																// getIndex(nIndex)!=-1){
						iFunc.remove();// remove function
						break;
					}
				}
				if (searchJoin) {
					for (TypeArc cArc : epc.getArcs()) {
						if (cArc.getFlow().getSource().intValue() == id) {
							next = cArc;
							removeNextNodes(next, searchJoin, null);
						}
					}
					return;
				} else {
					for (TypeArc cArc : epc.getArcs()) {
						if (cArc.getFlow().getTarget().intValue() == id) {
							next = cArc;
							removeNextNodes(next, searchJoin, null);
						}
					}
					return;
				}
			} else if (nNext instanceof TypeEvent) {// TypeEvent
				int id = nNext.getId().intValue();
				for (Iterator<TypeEvent> iEvent = epc.getEvents().iterator(); iEvent
						.hasNext();) {
					if (iEvent.next().getId().intValue() == id
							&& EventsMap.get(id) != null) {// if(iEvent.next().getId().intValue()==id
															// &&
															// getIndex(nIndex)!=-1){
						iEvent.remove();// remove function
						break;
					}
				}
				if (searchJoin) {
					for (TypeArc cArc : epc.getArcs()) {
						if (cArc.getFlow().getSource().intValue() == id) {
							next = cArc;
							removeNextNodes(next, searchJoin, null);
						} else {
							return;
						}
					}
				} else {
					for (TypeArc cArc : epc.getArcs()) {
						if (cArc.getFlow().getTarget().intValue() == id) {
							next = cArc;
							removeNextNodes(next, searchJoin, null);
						} else {
							return;
						}
					}
				}
			}
		}
		return;
	}

	private List<TypeArc> getSplitArcs(int id) throws Exception {
		List<TypeArc> ls = new ArrayList<TypeArc>();
		int i = 0;
		for (TypeArc cArc : epc.getArcs()) {
			if (cArc.getFlow().getSource().intValue() == id) {
				ls.add(i, cArc);
				i++;
			}
		}
		return ls;
	}

	private List<TypeArc> getJoinArcs(int id) throws Exception {
		List<TypeArc> ls = new ArrayList<TypeArc>();
		int i = 0;
		for (TypeArc cArc : epc.getArcs()) {
			if (cArc.getFlow().getTarget().intValue() == id) {
				ls.add(i, cArc);
				i++;
			}
		}
		return ls;
	}

	private void generateNewConnector(TEpcElement currentCE, String value)
			throws Exception {// private void copyFields(TEpcElement currentCE,
								// TEpcElement newCE, String value) throws
								// Exception {
		BigDecimal xCE = currentCE.getGraphics().getPosition().getX();
		BigDecimal yCE = currentCE.getGraphics().getPosition().getY();
		BigDecimal wCE = currentCE.getGraphics().getPosition().getWidth();
		BigDecimal hCE = currentCE.getGraphics().getPosition().getHeight();
		if (value.equals("or")) {
			TypeOR newOR = new TypeOR();
			TypePosition p = new TypePosition();
			TypeGraphics g = new TypeGraphics();
			p.setHeight(hCE);
			p.setWidth(wCE);
			p.setX(xCE);
			p.setY(yCE);
			g.setPosition(p);
			newOR.setGraphics(g);
			newOR.setId(currentCE.getId());
			epc.getOR().add((TypeOR) newOR);
		} else if (value.equals("xor")) {
			TypeXOR newXOR = new TypeXOR();
			TypePosition p = new TypePosition();
			TypeGraphics g = new TypeGraphics();
			p.setHeight(hCE);
			p.setWidth(wCE);
			p.setX(xCE);
			p.setY(yCE);
			g.setPosition(p);
			newXOR.setGraphics(g);
			newXOR.setId(currentCE.getId());
			epc.getXOR().add((TypeXOR) newXOR);
		} else if (value.equals("and")) {
			TypeAND newAND = new TypeAND();
			TypePosition p = new TypePosition();
			TypeGraphics g = new TypeGraphics();
			p.setHeight(hCE);
			p.setWidth(wCE);
			p.setX(xCE);
			p.setY(yCE);
			g.setPosition(p);
			newAND.setGraphics(g);
			newAND.setId(currentCE.getId());
			epc.getAND().add((TypeAND) newAND);
		}
	}

	private void retrieveMaxId() {
		maxID = (epc.getFunctions().size() + epc.getEvents().size()
				+ epc.getArcs().size() + epc.getOR().size()
				+ epc.getXOR().size() + epc.getAND().size()) + 1;
	}

	// this method is used when a variation point needs to be removed: fixes the
	// incoming (target) and outgoing (source) arcs
	private void fixRemoval(int id, int goTo, String type) throws Exception {
		TypeArc sArc, tArc;
		if (type == "split") {
			tArc = lookUpArcT(id);
			sArc = new TypeArc();
			if (ArcsMap.get(goTo) != null) {
				sArc = ArcsMap.get(goTo);
			}
		} else if (type == "join") {
			tArc = new TypeArc();
			if (ArcsMap.get(goTo) != null) {
				tArc = ArcsMap.get(goTo);
			}
			sArc = lookUpArcS(id);
		} else {// type==sequence
			tArc = lookUpArcT(id);
			sArc = lookUpArcS(id);
		}
		tArc.getFlow().setTarget(sArc.getFlow().getTarget());
		trackGoto(sArc.getId().intValue(), tArc.getId().intValue());
		for (Iterator<TypeArc> iArc = epc.getArcs().iterator(); iArc.hasNext();) {
			TypeArc cArc = iArc.next();
			if (cArc.getId().intValue() == sArc.getId().intValue()
					&& ArcsMap.get(sArc.getId().intValue()) != null) {
				iArc.remove();
				return;
			}
		}
	}

	private void trackGoto(int sId, int tId) throws Exception {
		// find goto under configuration for all connector or, xor, and
		for (TypeOR cOr : epc.getOR()) {
			if (cOr.getConfigurableConnector() != null
					&& cOr.getConfigurableConnector().getConfiguration() != null) {
				if (cOr.getConfigurableConnector().getConfiguration().getGoto() != null) {
					if (cOr.getConfigurableConnector().getConfiguration()
							.getGoto().intValue() == sId) {
						cOr.getConfigurableConnector().getConfiguration()
								.setGoto(BigInteger.valueOf(tId));
						return;
					}
				}
			}
		}
		for (TypeXOR cXor : epc.getXOR()) {
			if (cXor.getConfigurableConnector() != null
					&& cXor.getConfigurableConnector().getConfiguration() != null) {
				if (cXor.getConfigurableConnector().getConfiguration()
						.getGoto() != null) {
					if (cXor.getConfigurableConnector().getConfiguration()
							.getGoto().intValue() == sId) {
						cXor.getConfigurableConnector().getConfiguration()
								.setGoto(BigInteger.valueOf(tId));
						return;
					}
				}
			}
		}
		for (TypeAND cAnd : epc.getAND()) {
			if (cAnd.getConfigurableConnector() != null
					&& cAnd.getConfigurableConnector().getConfiguration() != null) {
				if (cAnd.getConfigurableConnector().getConfiguration()
						.getGoto() != null) {
					if (cAnd.getConfigurableConnector().getConfiguration()
							.getGoto().intValue() == sId) {
						cAnd.getConfigurableConnector().getConfiguration()
								.setGoto(BigInteger.valueOf(tId));
						return;
					}
				}
			}
		}
	}

	private void fixFOpt(TypeFunction f) throws Exception {
		// xor
		int idF = f.getId().intValue();
		int xF = f.getGraphics().getPosition().getX().intValueExact();
		int yF = f.getGraphics().getPosition().getY().intValueExact();
		int wF = f.getGraphics().getPosition().getWidth().intValueExact();
		int hF = f.getGraphics().getPosition().getHeight().intValueExact();
		TypeArc sArc, tArc;
		tArc = lookUpArcT(idF);
		sArc = lookUpArcS(idF);
		int yTArc = ((TypeGraphics) tArc.getGraphics()).getPosition().getY()
				.intValueExact();
		int ySArc = ((TypeGraphics) sArc.getGraphics()).getPosition().getY()
				.intValueExact();
		// generate xor1
		TypeXOR XOR1 = new TypeXOR();
		TypeGraphics gXOR1 = new TypeGraphics();
		TypePosition pXOR1 = new TypePosition();
		XOR1.setId(BigInteger.valueOf(maxID++));
		pXOR1.setHeight(BigDecimal.valueOf(31));
		pXOR1.setWidth(BigDecimal.valueOf(31));
		pXOR1.setX(BigDecimal.valueOf(xF + xOff + wF));
		pXOR1.setY(BigDecimal.valueOf((yF - yTArc - hF) + yTArc));
		gXOR1.setPosition(pXOR1);
		XOR1.setGraphics(gXOR1);
		epc.getXOR().add(XOR1);
		tArc.getFlow().setTarget(XOR1.getId());
		// generate xor2
		TypeXOR XOR2 = new TypeXOR();
		TypeGraphics gXOR2 = new TypeGraphics();
		TypePosition pXOR2 = new TypePosition();
		XOR2.setId(BigInteger.valueOf(maxID++));
		pXOR2.setHeight(BigDecimal.valueOf(31));
		pXOR2.setWidth(BigDecimal.valueOf(31));
		pXOR2.setX(BigDecimal.valueOf(xF + xOff + wF));
		pXOR2.setY(BigDecimal.valueOf(((ySArc - yF) / 4) + yF + hF));
		gXOR2.setPosition(pXOR2);
		XOR2.setGraphics(gXOR2);
		epc.getXOR().add(XOR2);
		sArc.getFlow().setSource(XOR2.getId());

		// arc
		int xXor1 = XOR1.getGraphics().getPosition().getX().intValueExact();
		int yXor1 = XOR1.getGraphics().getPosition().getY().intValueExact();
		int xXor2 = XOR2.getGraphics().getPosition().getX().intValueExact();
		int yXor2 = XOR2.getGraphics().getPosition().getY().intValueExact();
		// generate arc
		TypeArc inArc = new TypeArc();
		TypeFlow inFlow = new TypeFlow();
		TypeGraphics inG = new TypeGraphics();
		TypePosition inPs = new TypePosition();
		TypePosition inPt = new TypePosition();
		inPs.setX(BigDecimal.valueOf(xXor1));
		inPs.setY(BigDecimal.valueOf(yXor1 + 31));
		inPt.setX(BigDecimal.valueOf(xF + hF));
		inPt.setY(BigDecimal.valueOf(yF));
		inG.setPosition(inPs);
		inG.setPosition(inPt);
		inFlow.setSource(XOR1.getId());
		inFlow.setTarget(f.getId());
		inArc.setFlow(inFlow);
		inArc.setGraphics(inG);
		inArc.setId(BigInteger.valueOf(maxID++));
		epc.getArcs().add(inArc);

		// generate betweenArc
		TypeArc betweenArc = new TypeArc();
		TypeFlow betweenFlow = new TypeFlow();
		betweenFlow.setSource(XOR1.getId());
		betweenFlow.setTarget(XOR2.getId());
		betweenArc.setFlow(betweenFlow);
		betweenArc.setId(BigInteger.valueOf(maxID++));
		epc.getArcs().add(betweenArc);

		// generate outArc
		TypeArc outArc = new TypeArc();
		TypeFlow outFlow = new TypeFlow();
		TypeGraphics outG = new TypeGraphics();
		TypePosition outPs = new TypePosition();
		TypePosition outPt = new TypePosition();
		outPs.setX(BigDecimal.valueOf(xF + hF));
		outPs.setY(BigDecimal.valueOf(yF + hF));
		outPt.setX(BigDecimal.valueOf(xXor2));
		outPt.setY(BigDecimal.valueOf(yXor2));
		outG.setPosition(outPs);
		outG.setPosition(outPt);
		outFlow.setSource(f.getId());
		outFlow.setTarget(XOR2.getId());
		outArc.setFlow(outFlow);
		outArc.setGraphics(outG);
		outArc.setId(BigInteger.valueOf(maxID++));
		epc.getArcs().add(outArc);
	}

	private TypeArc lookUpArcS(int id) {// TODO: either with XMLUtils thru XPath
		for (TypeArc arc : epc.getArcs()) {
			if (arc.getFlow().getSource().intValue() == id)
				return arc;
		}
		return null;
	}

	private TypeArc lookUpArcT(int id) {
		for (TypeArc arc : epc.getArcs()) {
			if (arc.getFlow().getTarget().intValue() == id)
				return arc;
		}
		return null;
	}

	// this method retrieves the main epc and returns it as ITypeEPC element
	private TypeEPC retrieveMainEPC() {
		// TODO: verify directory type and epc type properly. For the time
		// being, we assume 1 directory containing 1 epc (the most common case)
		List<TypeDirectory> c = model.getDirectory();
		TypeEPC rc = (TypeEPC) c.get(0).getEpcOrDirectory().get(0);
		return rc;
	}

}
