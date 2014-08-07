/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of “Apromore”.
 *
 * “Apromore” is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * “Apromore” is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package com.processconfiguration.configurator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.yawlfoundation.yawlschema.ConfigurationType;
import org.yawlfoundation.yawlschema.ExternalNetElementType;
import org.yawlfoundation.yawlschema.ExternalTaskFactsType;
import org.yawlfoundation.yawlschema.InputPortConfigType;
import org.yawlfoundation.yawlschema.InputPortValueType;
import org.yawlfoundation.yawlschema.JoinConfigType;
import org.yawlfoundation.yawlschema.NetFactsType;
import org.yawlfoundation.yawlschema.NetFactsType.ProcessControlElements;
import org.yawlfoundation.yawlschema.NofiConfigType;
import org.yawlfoundation.yawlschema.OutputPortConfigType;
import org.yawlfoundation.yawlschema.OutputPortValueType;
import org.yawlfoundation.yawlschema.RemConfigType;
import org.yawlfoundation.yawlschema.SpecificationSetFactsType;
import org.yawlfoundation.yawlschema.SplitConfigType;
import org.yawlfoundation.yawlschema.YAWLSpecificationFactsType;

import com.processconfiguration.bddc.ExecBDDC;
import com.processconfiguration.cmap.CEpcType;
import com.processconfiguration.cmap.CMAP;
import com.processconfiguration.cmap.CYawlType;
import com.processconfiguration.cmap.JoinPortType;
import com.processconfiguration.cmap.NofiType;
import com.processconfiguration.cmap.RemType;
import com.processconfiguration.cmap.SplitPortType;
import com.processconfiguration.dcl.DCL;
import com.processconfiguration.dcl.FactType;
import com.processconfiguration.individualizer.IndividualizerEPC;
import com.processconfiguration.individualizer.IndividualizerYAWL;

import epml.TypeAND;
import epml.TypeCAnd;
import epml.TypeCFunction;
import epml.TypeCOR;
import epml.TypeCXOR;
import epml.TypeDirectory;
import epml.TypeEPC;
import epml.TypeEPML;
import epml.TypeFunction;
import epml.TypeOR;
import epml.TypeXOR;

public class Configurator {

	static TypeEPML cepcModel = null;
	static SpecificationSetFactsType cyawlModel = null;
	static CMAP cMAP = null;
	static CEpcType cepcMap = null;
	static CYawlType cyawlMap = null;
	static DCL conf = null;
	private static boolean[] f;
	static Map<String, String> ns = null;
	static TypeEPC cepc;
	static ProcessControlElements cyawl;
	File fInModel = null;
	File fInMap = null;
	File fInConf = null;
	String fOutName = null;
	static List<ExternalTaskFactsType> tasks = null;
	private static ExecBDDC bddc = null;
	TreeMap<String, Boolean> FactsMap;

	public Configurator(File fInModel, File fInMap, File fInConf) {

		this.fInModel = fInModel;
		this.fInMap = fInMap;
		this.fInConf = fInConf;
		this.cMAP = cMAP;
		commit();
	}

	public File commit() {
		readConfiguration(fInConf);
		if (fInModel.toString().endsWith(".epml"))
			return cepc();
		else if (fInModel.toString().endsWith(".yawl"))
			return cyawl();
		else {
			System.err.println("Model format not recognized.");
			return null;
		}
	}

	private File cyawl() {
		String type1 = null, type2 = null;
		int countV;
		try {

			JAXBContext jc = JAXBContext
					.newInstance("org.yawlfoundation.yawlschema");
			Unmarshaller u = jc.createUnmarshaller();
			JAXBElement yawlElement = (JAXBElement) u.unmarshal(fInModel); // creates
																			// the
																			// root
																			// element
																			// from
																			// XML
																			// file
			cyawlModel = (SpecificationSetFactsType) yawlElement.getValue();

			cyawl = retrieveMainCYAWL();
			tasks = getTasks();
			JAXBContext jaxbcontext = JAXBContext
					.newInstance("com.processconfiguration.cmap");
			Unmarshaller unmarshaller = jaxbcontext.createUnmarshaller();
			cMAP = (CMAP) unmarshaller.unmarshal(fInMap);
			cyawlMap = null;
                        for (Object object: cMAP.getCBpmnOrCEpcOrCYawl()) {
                                if (object instanceof CYawlType) {
                                        cyawlMap = (CYawlType) object;
                                        break;
                                }
                        }

			createSets();
			bddc = new ExecBDDC(FactsMap);

			// for all the splits ports. Note that each port can have exactly
			// one value. The second is retrieved automatically
			for (SplitPortType currentVP : cyawlMap.getSplits().getPort()) {
				for (com.processconfiguration.cmap.SplitPortType.Value currentValue : currentVP
						.getValue()) {
					if (verifyCondition(currentValue.getCondition())) {// verifies
																		// if
																		// the
																		// current
																		// condition
																		// holds
																		// given
																		// the
																		// configuration
																		// f
						updateCYAWLModel("split", currentVP.getSourceId(),
								currentVP.getTargetId(),
								OutputPortValueType.fromValue(currentValue
										.getType()));// updates the C-YAWL
														// model. Note: for
														// C-YAWL type is
														// split|join|rem|nofi|decomp
														// while the
														// getChildElement is
														// hard-coded with
														// "task" as type
						break;// if the first holds, then we don't need to check
								// the other values
					} else
						updateCYAWLModel("split", currentVP.getSourceId(),
								currentVP.getTargetId(),
								OutputPortValueType
										.fromValue(getOtherTypeAB(currentValue
												.getType())));
				}
			}

			// for all the joins ports. Note that each port can have exactly two
			// values. The is retrieved automatically
			for (JoinPortType currentVP : cyawlMap.getJoins().getPort()) {
				countV = 0;
				for (com.processconfiguration.cmap.JoinPortType.Value currentValue : currentVP
						.getValue()) {
					if (verifyCondition(currentValue.getCondition())) {// verifies
																		// if
																		// the
																		// current
																		// condition
																		// holds
																		// given
																		// the
																		// configuration
																		// f
						updateCYAWLModel("join", currentVP.getTargetId(),
								currentVP.getSourceId(),
								InputPortValueType.fromValue(currentValue
										.getType()));// /updates the C-YAWL
														// model
						break;// if the first holds, then we don't need to check
								// the other values
					} else if (countV == 0)
						type1 = currentValue.getType();
					else if (countV == 1)
						type2 = currentValue.getType();
					countV++;
				}
				if (countV == 2)// this means no condition has evaluated to
								// true, so the remaining type has to be used
								// instead
					updateCYAWLModel("join", currentVP.getTargetId(),
							currentVP.getSourceId(),
							InputPortValueType.fromValue(getOtherTypeABH(type1,
									type2)));
			}

			// for all the rem elements within rems. Note that each rem can have
			// exactly one type value between activated and blocked. The other
			// is retrieved automatically
			for (RemType currentVP : cyawlMap.getRems().getRem()) {
				for (com.processconfiguration.cmap.RemType.Value currentValue : currentVP
						.getValue()) {
					if (verifyCondition(currentValue.getCondition())) {// verifies
																		// if
																		// the
																		// current
																		// condition
																		// holds
																		// given
																		// the
																		// configuration
																		// f
						updateCYAWLModel("rem", currentVP.getTaskId(), null,
								OutputPortValueType.fromValue(currentValue
										.getType()));// updates the C-EPC model
						break;
					} else
						updateCYAWLModel("rem", currentVP.getTaskId(), null,
								OutputPortValueType
										.fromValue(getOtherTypeAB(currentValue
												.getType())));
				}
			}

			// for all the nofi elements in nofis.
			for (NofiType currentVP : cyawlMap.getNofis().getNofi()) {
				for (com.processconfiguration.cmap.NofiType.Value currentValue : currentVP
						.getValue()) {
					if (verifyCondition(currentValue.getCondition())) {// verifies
																		// if
																		// the
																		// current
																		// condition
																		// holds
																		// given
																		// the
																		// configuration
																		// f
						updateCYAWLModel("nofi", currentVP.getTaskId(), null,
								currentValue);// /updates the C-YAWL model
						break;// if the first holds, then we don't need to check
								// the other values
					}
				}
			}

			// close file
			//fOutName = fInModel.getPath().substring(0,fInModel.getPath().length() - 5)+ "_configured.yawl";
			File fOutModel = File.createTempFile("temp", ".yawl");
			
			Marshaller marshaller = jc.createMarshaller();
			marshaller.marshal(yawlElement, fOutModel);
			//marshaller.marshal(yawlElement, new FileOutputStream(fOutName));

			IndividualizerYAWL iyawl = new IndividualizerYAWL(fOutModel);
			if (iyawl.commit() == null) {
				return null;
			}

			return iyawl.commit();
			//return 0;// positive termination

		} catch (NoSuchElementException e) {
			e.printStackTrace();
			System.err.println("Mapping format not correct.");
			return null;//return 1;// negative termination: the mapping doesn't contain an
						// element <c-yawl>
		} catch (Exception e) {
			e.printStackTrace();
			return null;//return 1;// negative termination: some problem has occurred TODO:
						// verify all the cases
		}

	}

	// this method returns 'blocked' if the input is 'activated' and vice versa
	private String getOtherTypeAB(String type) {
		if (type.equals("activated"))
			return "blocked";
		else
			return "activated";
	}

	public List<ExternalTaskFactsType> getTasks() {
		List<ExternalTaskFactsType> list = new ArrayList<ExternalTaskFactsType>();
		int size = cyawl.getTaskOrCondition().size();
		for (int index = 0; index < size; index++) {
			if (cyawl.getTaskOrCondition().get(index) instanceof ExternalTaskFactsType) {
				list.add((ExternalTaskFactsType) cyawl.getTaskOrCondition()
						.get(index));
			}
		}
		return list;

	}

	// this method returns 'hidden' if the inputs are 'activated' and 'blocked',
	// or 'blocked' if the inputs are 'activated' and 'hidden', otherwise
	// 'activated'
	private String getOtherTypeABH(String type1, String type2) {
		if ((type1.equals("activated") || type1.equals("blocked"))
				& (type2.equals("blocked") || type2.equals("activated")))
			return "hidden";
		else if ((type1.equals("activated") || type1.equals("hidden"))
				& (type2.equals("hidden") || type2.equals("activated")))
			return "blocked";
		else
			return "activated";
	}

	// this method retrieves and returns element ProcessControlElements from the
	// main YAWL element specificationSet
	private ProcessControlElements retrieveMainCYAWL() {

		YAWLSpecificationFactsType s = cyawlModel.getSpecification().get(0);
		int size = s.getDecomposition().size();
		for (int index = 0; index < size; index++) {
			if (s.getDecomposition().get(index) != null) {
				NetFactsType value = (NetFactsType) s.getDecomposition().get(
						index);
				if (value.isIsRootNet()) {
					return value.getProcessControlElements();// we require there
																// is always a
																// decomposition
																// with
																// attribute
																// isRootNet='true'
				}
			}
		}
		return null;
	}

	// this method updates the input C-YAWL model by performing the
	// configuration (it adds the <configuration> tag in a <task> tag with the
	// proper values)
	private static void updateCYAWLModel(String configType, String taskId,
			String flows, Object type) {
		ConfigurationType configuration;
		OutputPortConfigType sPort;
		InputPortConfigType jPort;
		ExternalNetElementType flow;
		List<OutputPortConfigType> sPorts;
		List<InputPortConfigType> jPorts;
		List<String> flowsList;
		boolean noConf = false, noSplit = false, noSPort = false, noJPort = false, noJoin = false, noRem = false, noNofi = false;

		try {
			ExternalTaskFactsType task = getTask(taskId);// retrieve the proper
															// task, to which
															// the configuration
															// has to be applied
			if (task == null) {
				System.err.println("Task does not exist:".concat(taskId));
			} else {

				if ((configuration = task.getConfiguration()) == null) {
					noConf = true;
					configuration = new ConfigurationType();
				}

				if (configType.equals("split")) {
					SplitConfigType split;
					if ((split = configuration.getSplit()) == null) {
						noSplit = true;
						split = new SplitConfigType();
						try {
							OutputPortValueType temp;
							temp = task.getDefaultConfiguration().getSplit()
									.getValue();
							split.setValue(temp);
						} catch (NullPointerException e) {
							split.setValue(OutputPortValueType.ACTIVATED);// if
																			// no
																			// defaultConfiguration,
																			// then
																			// the
																			// "most common"
																			// value
																			// is
																			// set
																			// to
																			// 'activated'
						}
					}

					flowsList = retrieveFlows(flows);
					if ((sPorts = split.getPort()).size() != 0) {// there exists
																	// at least
																	// a port
																	// already

						if ((sPort = checkOutputPort(sPorts, flowsList)) == null) {// if
																					// ==
																					// null
																					// means
																					// the
																					// requested
																					// port
																					// doesn't
																					// exist
							noSPort = true;
							sPort = new OutputPortConfigType();
						}
					} else {
						noSPort = true;
						sPort = new OutputPortConfigType();
					}

					// modify port according to the mapping
					sPort.setValue((OutputPortValueType) type);
					if (noSPort) {// set flowDestinations within the port
						for (String flowId : flowsList) {
							flow = new ExternalNetElementType();
							flow.setId(flowId);
							sPort.getFlowDestination().add(flow);
						}
						split.getPort().add(sPort);
					}

					if (noSplit)
						configuration.setSplit(split);
				}

				else if (configType.equals("join")) {
					JoinConfigType join;
					if ((join = configuration.getJoin()) == null) {
						noJoin = true;
						join = new JoinConfigType();
						try {
							InputPortValueType temp;
							temp = task.getDefaultConfiguration().getJoin()
									.getValue();
							join.setValue(temp);
						} catch (NullPointerException e) {
							join.setValue(InputPortValueType.ACTIVATED);// if no
																		// defaultConfiguration,
																		// then
																		// the
																		// "most common"
																		// value
																		// is
																		// set
																		// to
																		// 'activated'
						}
					}

					flowsList = retrieveFlows(flows);
					if ((jPorts = join.getPort()).size() != 0) {// there exists
																// at least a
																// port already

						if ((jPort = checkInputPort(jPorts, flowsList)) == null) {// if
																					// ==
																					// null
																					// means
																					// the
																					// requested
																					// port
																					// doesn't
																					// exist
							noJPort = true;
							jPort = new InputPortConfigType();
						}
					} else {
						noJPort = true;
						jPort = new InputPortConfigType();
					}

					// modify port according to the mapping
					jPort.setValue((InputPortValueType) type);
					if (noJPort) {// set flowDestinations within the port
						for (String flowId : flowsList) {
							flow = new ExternalNetElementType();
							flow.setId(flowId);
							jPort.getFlowSource().add(flow);
						}
						join.getPort().add(jPort);
					}

					if (noJoin)
						configuration.setJoin(join);
				}

				else if (configType.equals("rem")) {
					RemConfigType rem;
					if ((rem = configuration.getRem()) == null) {
						noRem = true;
						rem = new RemConfigType();
					}
					rem.setValue((OutputPortValueType) type);

					if (noRem)
						configuration.setRem(rem);
				}

				else if (configType.equals("nofi")) {
					NofiConfigType nofi;
					if ((nofi = configuration.getNofi()) == null) {
						noNofi = true;
						nofi = new NofiConfigType();
					}
					org.yawlfoundation.yawlschema.NofiConfigType value = (org.yawlfoundation.yawlschema.NofiConfigType) type;
					nofi.setMinIncrease(value.getMinIncrease());
					nofi.setMaxDecrease(value.getMaxDecrease());
					nofi.setThresIncrease(value.getThresIncrease());
					nofi.setCreationMode(value.getCreationMode());

					if (noNofi)
						configuration.setNofi(nofi);
				}

				else
					System.err
							.println("Configuration type is none of 'split', 'join', 'rem', 'nofi'.");

				if (noConf)
					task.setConfiguration(configuration);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static List<String> retrieveFlows(String flows) {
		StringTokenizer flowsTokenize = new StringTokenizer(flows);
		ArrayList<String> flowsList = new ArrayList<String>();
		while (flowsTokenize.hasMoreTokens()) {
			flowsList.add(flowsTokenize.nextToken());
		}
		return flowsList;
	}

	// checks whether an output port is already contained in the <split> child
	// of the <configuration> tag of a given task
	private static OutputPortConfigType checkOutputPort(
			List<OutputPortConfigType> ports, List<String> flowsList) {
		int count;
		for (OutputPortConfigType currentPort : ports) {
			count = 0;
			if (currentPort.getFlowDestination().size() == flowsList.size()) {// first
																				// checks
																				// if
																				// the
																				// number
																				// of
																				// flows
																				// corresponds,
																				// to
																				// avoid
																				// situations
																				// such
																				// as
																				// port
																				// ABC
																				// that
																				// comprises
																				// AC
				for (ExternalNetElementType currentFlow : currentPort
						.getFlowDestination()) {
					for (String flowId : flowsList) {
						if (flowId.equals(currentFlow.getId())) {
							count++;
							break;
						}
					}
				}
				if (count == flowsList.size())
					return currentPort;
			}
		}
		return null;
	}

	// checks whether an input port is already contained in the <join> child of
	// the <configuration> tag of a given task
	private static InputPortConfigType checkInputPort(
			List<InputPortConfigType> ports, List<String> flowsList) {
		int count;
		for (InputPortConfigType currentPort : ports) {
			count = 0;
			if (currentPort.getFlowSource().size() == flowsList.size()) {// first
																			// checks
																			// if
																			// the
																			// number
																			// of
																			// flows
																			// corresponds,
																			// to
																			// avoid
																			// situations
																			// such
																			// as
																			// port
																			// ABC
																			// that
																			// comprises
																			// AC
				for (ExternalNetElementType currentFlow : currentPort
						.getFlowSource()) {
					for (String flowId : flowsList) {
						if (flowId.equals(currentFlow.getId())) {
							count++;
							break;
						}
					}
				}
				if (count == flowsList.size())
					return currentPort;
			}
		}
		return null;
	}

	// get a task within a processControlElements according the given taskId
	private static ExternalTaskFactsType getTask(String taskId) {
		for (ExternalTaskFactsType currentTask : tasks) {
			if (currentTask.getId().equals(taskId))
				return currentTask;
		}

		return null;
	}

	private File cepc() {
		try {

			JAXBContext jc = JAXBContext.newInstance("epml");
			Unmarshaller u = jc.createUnmarshaller();
			JAXBElement epcElement = (JAXBElement) u.unmarshal(fInModel);
			cepcModel = (TypeEPML) epcElement.getValue();

			cepc = retrieveMainCEPC();
			JAXBContext jaxbcontext = JAXBContext
					.newInstance("com.processconfiguration.cmap");
			Unmarshaller unmarshaller = jaxbcontext.createUnmarshaller();
			cMAP = (CMAP) unmarshaller.unmarshal(fInMap);
			cepcMap = null;
			for (Object object: cMAP.getCBpmnOrCEpcOrCYawl()) {
				if (object instanceof CEpcType) {
					cepcMap = (CEpcType) object;
					break;
				}
			}

			createSets();
			bddc = new ExecBDDC(FactsMap);

			// NOTE: even if all the values but the last evaluate false, the
			// mapper still checks if the last evaluates to true
			for (Object object: cepcMap.getCOROrCXOROrCAND()) {
			    if (object instanceof com.processconfiguration.cmap.CORType) {
				com.processconfiguration.cmap.CORType currentVP = (com.processconfiguration.cmap.CORType) object;
				for (com.processconfiguration.cmap.CORType.Value currentValue : currentVP
						.getValue()) {
					if (verifyCondition(currentValue.getCondition())) {// verifies
																		// if
																		// the
																		// current
																		// condition
																		// holds
																		// given
																		// the
																		// configuration
																		// f
						for (TypeOR currentOR : cepc.getOR()) {
							if (currentVP.getId().intValue() == currentOR
									.getId().intValue()) {
								TypeCOR.Configuration currentConfiguration = new TypeCOR.Configuration();
								currentConfiguration.setValue(currentValue
										.getType());
								if (currentValue.getType().equals("seq")) {
									currentConfiguration.setGoto(currentValue
											.getGoto());
								}
								currentOR.getConfigurableConnector()
										.setConfiguration(currentConfiguration);
							}
						}
						break;// if the first holds, then we don't need to check
								// the other values
					}
				}
			    } else if (object instanceof com.processconfiguration.cmap.CXORType) {
				com.processconfiguration.cmap.CXORType currentVP = (com.processconfiguration.cmap.CXORType) object;
				for (com.processconfiguration.cmap.CXORType.Value currentValue : currentVP
						.getValue()) {
					if (verifyCondition(currentValue.getCondition())) {// verifies
																		// if
																		// the
																		// current
																		// condition
																		// holds
																		// given
																		// the
																		// configuration
																		// f
						for (TypeXOR currentXOR : cepc.getXOR()) {
							if (currentXOR.getId().intValue() == currentVP
									.getId().intValue()) {
								TypeCXOR.Configuration currentConfiguration = new TypeCXOR.Configuration();
								currentConfiguration.setValue(currentValue
										.getType());
								if (currentValue.getType().equals("seq")) {
									currentConfiguration.setGoto(currentValue
											.getGoto());
								}
								currentXOR.getConfigurableConnector()
										.setConfiguration(currentConfiguration);
							}
						}
						break;// if the first holds, then we don't need to check
								// the other values
					}
				}
			    } else if (object instanceof com.processconfiguration.cmap.CANDType) {
                                com.processconfiguration.cmap.CANDType currentVP = (com.processconfiguration.cmap.CANDType) object;
				for (com.processconfiguration.cmap.CANDType.Value currentValue : currentVP
						.getValue()) {
					if (verifyCondition(currentValue.getCondition())) {// verifies
																		// if
																		// the
																		// current
																		// condition
																		// holds
																		// given
																		// the
																		// configuration
																		// f
						for (TypeAND currentAND : cepc.getAND()) {
							if (currentAND.getId().intValue() == currentVP
									.getId().intValue()) {
								TypeCAnd.Configuration currentConfiguration = new TypeCAnd.Configuration();
								currentConfiguration.setValue(currentValue
										.getType());
								if (currentValue.getType().equals("seq")) {
									currentConfiguration.setGoto(currentValue
											.getGoto());
								}
								currentAND.getConfigurableConnector()
										.setConfiguration(currentConfiguration);
							}
						}
						break;// if the first holds, then we don't need to check
								// the other values
					}
				}
			    } else if (object instanceof com.processconfiguration.cmap.CFunctionType) {
                                com.processconfiguration.cmap.CFunctionType currentVP = (com.processconfiguration.cmap.CFunctionType) object;
				for (com.processconfiguration.cmap.CFunctionType.Value currentValue : currentVP
						.getValue()) {
					if (verifyCondition(currentValue.getCondition())) {// verifies
																		// if
																		// the
																		// current
																		// condition
																		// holds
																		// given
																		// the
																		// configuration
																		// f
						for (TypeFunction currentFunction : cepc.getFunctions()) {
							if (currentVP.getId().intValue() == currentFunction
									.getId().intValue()) {
								TypeCFunction.Configuration currentConfiguration = new TypeCFunction.Configuration();
								currentConfiguration.setValue(currentValue
										.getType());
								currentFunction.getConfigurableFunction()
										.setConfiguration(currentConfiguration);
								currentFunction.toString();
							}
						}
						break;// if the first holds, then we don't need to check
								// the other values
					}
				}
			    } else {
				throw new AssertionError("Unsupported CEpcType: " + object.getClass().getName());
			    }
			}
			// close file
			//fOutName = fInModel.getPath().substring(0,fInModel.getPath().length() - 5)+ "_configured.epml";
			File fOutModel = File.createTempFile("temp", ".epml");		

			Marshaller marshaller = jc.createMarshaller();
			marshaller.marshal(epcElement, fOutModel);
			//marshaller.marshal(epcElement, new FileOutputStream(fOutName));
			
			IndividualizerEPC iepc = new IndividualizerEPC(fOutModel);
			if (iepc.commit() == null) {
				return null;
			}

			//return 0;// positive termination
			return iepc.commit();

		} catch (NoSuchElementException e) {
			e.printStackTrace();
			System.err.println("Mapping format not correct.");
			return null;//return 1;// negative termination: the mapping doesn't contain an
						// element <c-epc>
		} catch (Exception e) {
			e.printStackTrace();
			return null;//return 1;// negative termination: some problem has occurred TODO:
						// verify all the cases
		}
	}

	// this method retrieves the main epc and returns it as TypeEPC element
	private static TypeEPC retrieveMainCEPC() {
		// TODO: verify directory type and epc type properly. For the time
		// being, we assume 1 directory containing 1 epc (the most common case)
		List<TypeDirectory> c = cepcModel.getDirectory();
		TypeEPC rc = (TypeEPC) c.get(0).getEpcOrDirectory().get(0);
		return rc;
		// TODO: we have to check getEPC() whether it contain model list or not.
		// If it is not, we then retrieve model list from getEpcOrDirectory()
		// TODO: we assume there is only 1 directory and only 1 epc in that
		// directory
	}

	// this method evaluates the condition for a value of a given variation
	// point, against the facts setting f (i.e. the configuration)
	// TODO: implement a reduction of the string in
	// (...and...)...or...(...and...) expression, so that then it can be easily
	// parsed
	// TODO: the reduction should also reduce the XOR if present
	// TODO: implement order thru parenthesis

	private boolean verifyCondition(String condition) {
		return bddc.isViolated(condition);
	}

	private void createSets() {
		FactsMap = new TreeMap<String, Boolean>();
		for (FactType f : conf.getFact()) {
			FactsMap.put(f.getId(), f.isValue());
		}
	}

	private static void readConfiguration(File fInConf) {
		try {
			JAXBContext jc = JAXBContext
					.newInstance("com.processconfiguration.dcl");
			Unmarshaller u = jc.createUnmarshaller();
			conf = (DCL) u.unmarshal(fInConf); // creates the root element from
												// XML file

			f = new boolean[conf.getFact().size()];

			for (FactType cf : conf.getFact()) {
				f[Integer.parseInt(cf.getId().substring(1)) - 1] = cf.isValue();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
