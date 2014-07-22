/*******************************************************************************
 * Copyright © 2006-2011, www.processconfiguration.com
 *   
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *   
 * Contributors:
 *      Marcello La Rosa - initial API and implementation, subsequent revisions
 *      Florian Gottschalk - individualizer for YAWL
 *      Possakorn Pitayarojanakul - integration with Configurator and Individualizer
 ******************************************************************************/
package com.processconfiguration.individualizer;

import java.io.File;
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

import org.yawlfoundation.yawlschema.ConfigurationType;
import org.yawlfoundation.yawlschema.ControlTypeCodeType;
import org.yawlfoundation.yawlschema.CreationModeCodeType;
import org.yawlfoundation.yawlschema.CreationModeConfigType;
import org.yawlfoundation.yawlschema.DecompositionType;
import org.yawlfoundation.yawlschema.ExternalConditionFactsType;
import org.yawlfoundation.yawlschema.ExternalNetElementFactsType;
import org.yawlfoundation.yawlschema.ExternalNetElementType;
import org.yawlfoundation.yawlschema.ExternalTaskFactsType;
import org.yawlfoundation.yawlschema.FlowsIntoType;
import org.yawlfoundation.yawlschema.InputPortConfigType;
import org.yawlfoundation.yawlschema.JoinConfigType;
import org.yawlfoundation.yawlschema.LayoutContainerFactsType;
import org.yawlfoundation.yawlschema.LayoutDecoratorFactsType;
import org.yawlfoundation.yawlschema.LayoutFactsType;
import org.yawlfoundation.yawlschema.LayoutNetFactsType;
import org.yawlfoundation.yawlschema.MultipleInstanceExternalTaskFactsType;
import org.yawlfoundation.yawlschema.NetFactsType;
import org.yawlfoundation.yawlschema.NetFactsType.ProcessControlElements;
import org.yawlfoundation.yawlschema.NofiConfigType;
import org.yawlfoundation.yawlschema.OutputConditionFactsType;
import org.yawlfoundation.yawlschema.OutputPortConfigType;
import org.yawlfoundation.yawlschema.PredicateType;
import org.yawlfoundation.yawlschema.RemovesTokensFromFlowType;
import org.yawlfoundation.yawlschema.SpecificationSetFactsType;
import org.yawlfoundation.yawlschema.SplitConfigType;
import org.yawlfoundation.yawlschema.YAWLSpecificationFactsType;

public class IndividualizerYAWL {
	public final static Boolean DEBUG = false;
	public final static Boolean DEBUGCLEANUP = true;

	List<String> configuredNets = new ArrayList<String>();
	List<String> requiredDecompositions = new ArrayList<String>();

	SpecificationSetFactsType model = null;
	LayoutFactsType layout = null;
	String specificationID = null;
	String netID = null;
	YAWLSpecificationFactsType s = null;
	Map<String, String> ns = null;
	// NetFactsType rootNet;
	File fInModel = null;
	File fOutModel = null;
	String fOutName = null;
	int maxID;
	private ConfigurationRemovals removedElements = new ConfigurationRemovals();
	private JAXBContext jc;
	private Unmarshaller u;
	private JAXBElement yawlElement;

	class ProcessControlElementsExtended {
		ProcessControlElements processControlElements;

		public ProcessControlElementsExtended(
				ProcessControlElements processControlElements) {
			this.processControlElements = processControlElements;
		}

		public OutputConditionFactsType getOutputCondition() {
			return processControlElements.getOutputCondition();
		}

		public void setOutputCondition(OutputConditionFactsType value) {
			processControlElements.setOutputCondition(value);
		}

		public ExternalConditionFactsType getInputCondition() {
			return processControlElements.getInputCondition();
		}

		public void setInputCondition(ExternalConditionFactsType value) {
			processControlElements.setInputCondition(value);
		}

		public List<ExternalNetElementFactsType> getTaskOrCondition() {
			return processControlElements.getTaskOrCondition();
		}

		public ExternalNetElementType getExternalNetElementFacts(String id) {
			int size = processControlElements.getTaskOrCondition().size();
			for (int index = 0; index < size; index++) {
				if (processControlElements.getTaskOrCondition().get(index)
						.getId().equals(id)) {
					return processControlElements.getTaskOrCondition().get(
							index);
				}
			}
			if (processControlElements.getInputCondition().getId().equals(id)) {
				return processControlElements.getInputCondition();
			}
			if (processControlElements.getOutputCondition().getId().equals(id)) {
				return processControlElements.getOutputCondition();
			}
			return null;
		}

		public List<ExternalTaskFactsType> getTask() {
			List<ExternalTaskFactsType> list = new ArrayList<ExternalTaskFactsType>();
			int size = processControlElements.getTaskOrCondition().size();
			for (int index = 0; index < size; index++) {
				if (processControlElements.getTaskOrCondition().get(index) instanceof ExternalTaskFactsType) {
					list.add((ExternalTaskFactsType) processControlElements
							.getTaskOrCondition().get(index));
				}
			}
			return list;

		}

		public ExternalTaskFactsType getTask(String id) {
			if (getExternalNetElementFacts(id) instanceof ExternalTaskFactsType) {
				return (ExternalTaskFactsType) getExternalNetElementFacts(id);
			}
			return null;
		}

		public List<ExternalConditionFactsType> getCondition() {
			List<ExternalConditionFactsType> list = new ArrayList<ExternalConditionFactsType>();
			int size = processControlElements.getTaskOrCondition().size();
			for (int index = 0; index < size; index++) {
				if (processControlElements.getTaskOrCondition().get(index) instanceof ExternalConditionFactsType) {
					list.add((ExternalConditionFactsType) processControlElements
							.getTaskOrCondition().get(index));
				}
			}
			return list;
		}

		public ExternalConditionFactsType getCondition(String id) {
			if (getExternalNetElementFacts(id) instanceof ExternalConditionFactsType) {
				return (ExternalConditionFactsType) getExternalNetElementFacts(id);
			}
			return null;
		}
	}

	public IndividualizerYAWL(File fInModel) {
		try {
			createContext();
			this.fInModel = fInModel;
			JAXBElement yawlElement = (JAXBElement) u.unmarshal(fInModel); // creates
																			// the
																			// root
																			// element
																			// from
																			// XML
																			// file
			this.yawlElement = yawlElement;
			model = (SpecificationSetFactsType) yawlElement.getValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public IndividualizerYAWL(JAXBElement yawlElement) {
		try {
			createContext();
			this.yawlElement = yawlElement;
			model = (SpecificationSetFactsType) yawlElement.getValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public IndividualizerYAWL(SpecificationSetFactsType model) {
		try {
			createContext();
			this.model = model;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean configure(NetFactsType net) {
		ProcessControlElementsExtended elements = new ProcessControlElementsExtended(
				net.getProcessControlElements());
		List<ExternalTaskFactsType> tasks = elements.getTask();
		for (ExternalTaskFactsType task : tasks) {
			configureRem(task);
			configureNofi(task);
			configureSplit(task, elements);
			configureJoin(task, elements);
			elements.setOutputCondition(elements.getOutputCondition());

			if (task.getConfiguration() != null) {
				task.setConfiguration(null);
			}
			if (task.getDefaultConfiguration() != null) {
				task.setDefaultConfiguration(null);
			}

		}
		boolean tasksRemoved = true;
		boolean conditionsRemoved = true;
		while (tasksRemoved || conditionsRemoved) {
			tasksRemoved = false;
			conditionsRemoved = false;
			tasksRemoved = removeTasks(elements);
			conditionsRemoved = removeConditions(elements);
		}

		List<String> decompositions = getTaskDecompositions((ProcessControlElementsExtended) elements);
		requiredDecompositions.addAll(decompositions);
		// YAWLSpecificationFactsType s=model.getSpecification().get(0);
		for (String decomposition : decompositions) {
			for (DecompositionType d : s.getDecomposition()) {
				if (d.getId().equals(decomposition)) {
					if ((d instanceof NetFactsType)
							&& ((NetFactsType) d).getProcessControlElements() != null) {
						if (!configuredNets.contains(d.getId())) {
							configuredNets.add(d.getId());
							netID = d.getId();
							configure((NetFactsType) d);
						}

					}

				}
			}
		}

		return true;
	}

	private void createContext() {
		try {
			jc = JAXBContext.newInstance("org.yawlfoundation.yawlschema");
			u = jc.createUnmarshaller();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private FlowsIntoType getFlowsInto(ExternalNetElementFactsType element,
			String id) {
		int size = element.getFlowsInto().size();
		for (int index = 0; index < size; index++) {
			if (element.getFlowsInto().get(index).getNextElementRef().getId()
					.equals(id)) {
				return element.getFlowsInto().get(index);
			}
		}
		return null;
	}

	private void removeFlowsInto(ExternalNetElementFactsType element, String id) {
		int size = element.getFlowsInto().size();
		for (int index = size; 0 < index; index--) {
			if (element.getFlowsInto().get(index - 1).getNextElementRef()
					.getId().equals(id)) {
				removedElements.addRemovedFlow(specificationID, netID,
						element.getId(), id);
				removeFlow(element.getFlowsInto(), index - 1);
			}
		}
	}

	/**
	 * Commits the individualized model to the output file fOutname. Only
	 * executable if object is initiated using a file or a JAXB element.
	 * 
	 * @return int 0 if succesfull, 1 if failed
	 */
	public File commit() {// public int commit(String fOutName) {
		if (yawlElement == null) {
			return null;// return 1;
		} else {
			try {
				s = model.getSpecification().get(0);
				specificationID = s.getUri();
				layout = model.getLayout();
				// model =
				// SpecificationSetFactsTypeFactory.createFacade(fInModel);
				requiredDecompositions.add(retrieveRootNet().getId());
				netID = retrieveRootNet().getId();
				configure(retrieveRootNet());

				// Remove not required decompositions:
				int noDecompositions = s.getDecomposition().size();
				for (int i = noDecompositions; i > 0; i--) {
					if (!requiredDecompositions.contains(s.getDecomposition()
							.get(i - 1).getId())) {
						s.getDecomposition().remove(i - 1);
					}
				}
				
				// closeFile();
				fOutModel = new File("temp.yawl");
				fOutModel.createNewFile();

				Marshaller marshaller = jc.createMarshaller();
				marshaller.marshal(yawlElement, fOutModel);
				fOutModel.deleteOnExit();
				// return 0;// positive termination
				return fOutModel;

			} catch (Exception e) {
				e.printStackTrace();
				return null;
				// return 1;// negative termination: some problem has occurred
				// TODO: verify all the cases and read the model
				// type: choose the algorithm accordingly
			}
		}
	}

	/**
	 * Creates a list of those elements that must be removed from the YAWL
	 * specification according to the configuration. The original input object
	 * will be destroyed.
	 * 
	 * @return ConfigurationRemovals
	 */
	public ConfigurationRemovals preview() {
		try {

			s = model.getSpecification().get(0);
			specificationID = s.getUri();
			layout = model.getLayout();
			requiredDecompositions.add(retrieveRootNet().getId());
			netID = retrieveRootNet().getId();
			configure(retrieveRootNet());

			// Remove not required decompositions:
			int noDecompositions = s.getDecomposition().size();
			for (int i = noDecompositions; i > 0; i--) {
				if (!requiredDecompositions.contains(s.getDecomposition()
						.get(i - 1).getId())) {
					s.getDecomposition().remove(i - 1);
				}
			}

			return removedElements;// positive termination

		} catch (Exception e) {
			e.printStackTrace();
			return null;// negative termination: some problem has occurred TODO:
						// verify all the cases and read the model type: choose
						// the algorithm accordingly
		}
	}

	/**
	 * Gets the task decompositions for the Process Control Elements elements.
	 * 
	 * @param elements
	 *            the elements
	 * 
	 * @return the task decompositions for the Process Control Elements elements
	 */
	private List<String> getTaskDecompositions(
			ProcessControlElementsExtended elements) {
		List<String> decompositions = new ArrayList<String>();
		for (ExternalTaskFactsType task : elements.getTask()) {
			if (task.getDecomposesTo() != null) {
				decompositions.add(task.getDecomposesTo().getId());
				if (DEBUG)
					System.out.println(task.getDecomposesTo().getId());
			}
		}
		return decompositions;
	}

	/**
	 * Method removeTasks
	 * 
	 * removes all tasks from the net that are not on a path between input and
	 * output port.
	 * 
	 * @param elements
	 *            the elements
	 * 
	 * @return true, if removes tasks
	 */

	private boolean removeTasks(ProcessControlElementsExtended elements) {
		List<String> onPathElements = new ArrayList<String>();
		boolean elementsRemoved = false;
		onPathElements = tasksOnPath(elements);
		if (DEBUG)
			System.out.println(onPathElements.toString());
		for (ExternalTaskFactsType task : elements.getTask()) {
			int size = task.getRemovesTokens().size();
			for (int index = size; 0 < index; index--) {
				if (!onPathElements.contains(task.getRemovesTokens()
						.get(index - 1).getId())) {
					task.getRemovesTokens().remove(index - 1);
				}
			}

			if (task.getFlowsInto() != null
					&& task.getFlowsInto().size() == 1
					&& task.getSplit().getCode()
							.equals(ControlTypeCodeType.XOR)) {
				task.getFlowsInto().get(0).setIsDefaultFlow("");
				task.getFlowsInto().get(0).setPredicate(null);
			}

			if (!onPathElements.contains(task.getId())) {
				if (DEBUG)
					System.out.println("Remove task:");
				if (DEBUG)
					System.out.println(task.getId());
				removeTask(elements, task);
				elementsRemoved = true;
			}
		}

		return elementsRemoved;
	}

	/**
	 * Removes all conditions from the net that are not on a path between input
	 * and output port..
	 * 
	 * @param elements
	 *            the process elements
	 * 
	 * @return true, if conditions were removed
	 */
	private boolean removeConditions(ProcessControlElementsExtended elements) {
		boolean elementsRemoved = false;
		List<String> onPathElements = tasksOnPath(elements);
		for (ExternalConditionFactsType condition : elements.getCondition()) {

			if (!onPathElements.contains(condition.getId())) {
				if (condition.getFlowsInto().size() != 0) {
					for (FlowsIntoType element : condition.getFlowsInto()) {
						if (elements.getTask(element.getNextElementRef()
								.getId()) == null) {
						} else {
							if (elements
									.getTask(
											element.getNextElementRef().getId())
									.getJoin().getCode().equals("and")) {
								elementsRemoved = true;
								removeTask(elements, elements.getTask(element
										.getNextElementRef().getId()));
								if (elements
										.getCondition("001__deadConditioninput") != null) {
									onPathElements
											.add("001__deadConditioninput");
								}
								if (elements
										.getCondition("001__deadConditionoutput") != null) {
									onPathElements
											.add("001__deadConditionoutput");
								}
							}
						}
					}
				}
				for (String prevElement : getElementsWithInputFlows(
						condition.getId(), elements)) {
					List<FlowsIntoType> flows = ((ExternalNetElementFactsType) elements
							.getExternalNetElementFacts(prevElement))
							.getFlowsInto();
					for (int i = 0; i < flows.size(); i++) {
						if (flows.get(i).getNextElementRef().getId()
								.equals(condition.getId())) {
							removedElements.addRemovedFlow(specificationID,
									netID, prevElement, flows.get(i)
											.getNextElementRef().getId());
							flows.remove(i);
						}
					}
				}
				for (int i = 0; i < elements.getTaskOrCondition().size(); i++) {
					if (elements.getTaskOrCondition().get(i).getId()
							.equals(condition.getId())) {
						removedElements.addRemovedCondition(specificationID,
								netID, condition.getId());
						elements.getTaskOrCondition().remove(i);

					}
				}

				elementsRemoved = true;

			}

		}

		return elementsRemoved;
	}

	/**
	 * Removes the task from the process control elements of the YAWL net. If
	 * necessary, preceding or succeeding tasks are connected to dead input or
	 * output conditions to ensure process correctness.
	 * 
	 * @param elements
	 *            the YAWL net elements
	 * @param task
	 *            the task that should be removed
	 */
	private void removeTask(ProcessControlElementsExtended elements,
			ExternalTaskFactsType task) {
		for (FlowsIntoType flow : task.getFlowsInto()) {
			if (!(elements.getExternalNetElementFacts(flow.getNextElementRef()
					.getId()) instanceof ExternalConditionFactsType)) {
				ExternalConditionFactsType deadCondition = addDeadCondition(
						elements, "input");
				deadCondition.getFlowsInto().add(flow);
				removedElements.addRemovedFlow(specificationID, netID,
						task.getId(), flow.getNextElementRef().getId());
			}
		}
		for (String prevElement : getElementsWithInputFlows(task.getId(),
				elements)) {
			if (!(elements.getExternalNetElementFacts(prevElement) instanceof ExternalConditionFactsType)) {
				ExternalConditionFactsType deadCondition = addDeadCondition(
						elements, "output");
				removedElements.addRemovedFlow(specificationID, netID,
						prevElement, task.getId());
				getFlowsInto(
						(ExternalNetElementFactsType) elements
								.getExternalNetElementFacts(prevElement),
						(task.getId())).setNextElementRef(deadCondition);
			} else {
				removeFlowsInto(
						(ExternalNetElementFactsType) elements
								.getExternalNetElementFacts(prevElement),
						task.getId());
			}
		}
		for (int index = 0; index < elements.getTaskOrCondition().size(); index++) {
			if (elements.getTaskOrCondition().get(index).getId()
					.equals(task.getId())) {
				removedElements.addRemovedTask(specificationID, netID,
						task.getId());
				elements.getTaskOrCondition().remove(index);
			}
		}
	}

	/**
	 * Gets the rem configuration for the task.
	 * 
	 * @param task
	 *            the task
	 * 
	 * @return the rem configuration
	 */
	private String getRemConfiguration(ExternalTaskFactsType task) {
		ConfigurationType configuration = task.getConfiguration();
		if (configuration == null || configuration.getRem() == null) {
			ConfigurationType defaultConfiguration = task
					.getDefaultConfiguration();
			if (defaultConfiguration == null
					|| defaultConfiguration.getRem() == null) {
				return "activated";
			} else {
				return defaultConfiguration.getRem().getValue().value();
			}
		} else {
			return configuration.getRem().getValue().value();
		}
	}

	/**
	 * Gets the nofi configuration for the task.
	 * 
	 * @param task
	 *            the task
	 * 
	 * @return the nofi configuration
	 */
	private NofiConfigType getNofiConfiguration(ExternalTaskFactsType task) {
		ConfigurationType configuration = task.getConfiguration();
		if (configuration == null || configuration.getNofi() == null) {
			ConfigurationType defaultConfiguration = task
					.getDefaultConfiguration();
			if (defaultConfiguration == null
					|| defaultConfiguration.getNofi() == null) {
				try {

					NofiConfigType nofi = new NofiConfigType();
					nofi.setMaxDecrease(new BigInteger("0"));
					nofi.setMinIncrease(new BigInteger("0"));
					nofi.setThresIncrease(new BigInteger("0"));
					nofi.setCreationMode(CreationModeConfigType.KEEP);
					return nofi;
				} catch (Exception e) {
					e.printStackTrace();
					return null;// negative termination: some problem has
								// occurred TODO: verify all the cases and read
								// the model type: choose the algorithm
								// accordingly
				}
			} else {
				return defaultConfiguration.getNofi();
			}
		} else {
			return configuration.getNofi();
		}
	}

	/**
	 * Gets the output port configuration for the port of the task.
	 * 
	 * @param task
	 *            the task
	 * @param port
	 *            the port
	 * 
	 * @return the output port configuration
	 */
	private String getOutputPortConfiguration(ExternalTaskFactsType task,
			List<String> port) {
		ConfigurationType configuration = task.getConfiguration();
		List<String> configPort = new ArrayList<String>();
		if (configuration == null || configuration.getSplit() == null) {
			ConfigurationType defaultConfiguration = task
					.getDefaultConfiguration();
			if (defaultConfiguration == null
					|| defaultConfiguration.getSplit() == null) {
				return "activated";
			} else {
				SplitConfigType splitConfig = defaultConfiguration.getSplit();
				List<OutputPortConfigType> ports = splitConfig.getPort();
				for (OutputPortConfigType portconfig : ports) {
					for (int i = 0; i < portconfig.getFlowDestination().size(); i++) {
						configPort.add(portconfig.getFlowDestination().get(i)
								.getId());
					}
					if (configPort.containsAll(port)
							&& configPort.size() == port.size()) {
						return portconfig.getValue().value();
					}
					configPort.clear();
				}
				// YAWL 2.2 (spuriously?) allows a configuration/split/@value attribute, but null is the expected value
                		return splitConfig.getValue() == null ? "activated" : splitConfig.getValue().value();
			}
		} else {
			SplitConfigType splitConfig = configuration.getSplit();
			List<OutputPortConfigType> ports = splitConfig.getPort();

			for (OutputPortConfigType portconfig : ports) {
				for (int i = 0; i < portconfig.getFlowDestination().size(); i++) {
					configPort.add(portconfig.getFlowDestination().get(i)
							.getId());
				}
				if (configPort.containsAll(port)
						&& configPort.size() == port.size()) {

					return portconfig.getValue().value();
				}
				configPort.clear();
			}
			// YAWL 2.2 (spuriously?) allows a configuration/split/@value attribute, but null is the expected value
                	return splitConfig.getValue() == null ? "activated" : splitConfig.getValue().value();
		}
	}

	/**
	 * Gets the input port configuration for the port of the task.
	 * 
	 * @param task
	 *            the task
	 * @param port
	 *            the port
	 * 
	 * @return the input port configuration
	 */
	private String getInputPortConfiguration(ExternalTaskFactsType task,
			List<String> port) {
		ConfigurationType configuration = task.getConfiguration();
		List<String> configPort = new ArrayList<String>();
		if (configuration == null || configuration.getJoin() == null) {
			ConfigurationType defaultConfiguration = task
					.getDefaultConfiguration();
			if (defaultConfiguration == null
					|| defaultConfiguration.getJoin() == null) {
				return "activated";
			} else {
				JoinConfigType joinConfig = defaultConfiguration.getJoin();
				List<InputPortConfigType> ports = joinConfig.getPort();
				// if (DEBUG) System.out.println(ports.toString());
				for (InputPortConfigType portconfig : ports) {
					for (int i = 0; i < portconfig.getFlowSource().size(); i++) {
						configPort.add(portconfig.getFlowSource().get(i)
								.getId());
					}
					if (configPort.containsAll(port)
							&& configPort.size() == port.size()) {
						return portconfig.getValue().value();
					}
					configPort.clear();
				}
				// YAWL 2.2 (spuriously?) allows a configuration/join/@value attribute, but null is the expected value
                		return joinConfig.getValue() == null ? "activated" : joinConfig.getValue().value();
			}
		} else {
			JoinConfigType joinConfig = configuration.getJoin();
			List<InputPortConfigType> ports = joinConfig.getPort();
			for (InputPortConfigType portconfig : ports) {
				for (int i = 0; i < portconfig.getFlowSource().size(); i++) {
					configPort.add(portconfig.getFlowSource().get(i).getId());
				}
				if (configPort.containsAll(port)
						&& configPort.size() == port.size()) {
					return portconfig.getValue().value();
				}
				configPort.clear();
			}
			// YAWL 2.2 (spuriously?) allows a configuration/join/@value attribute, but null is the expected value
                	return joinConfig.getValue() == null ? "activated" : joinConfig.getValue().value();
		}
	}

	/**
	 * Configure rem for the task task. Removes all links to tasks if rem is
	 * configured as blocked.
	 * 
	 * @param task
	 *            the task
	 */
	private void configureRem(ExternalTaskFactsType task) {
		if (getRemConfiguration(task).equals("blocked")) {
			removedElements.addBlockedCancelationRegion(specificationID, netID,
					task.getId());
			for (int i = task.getRemovesTokens().size(); i > 0; i--) {
				task.getRemovesTokens().remove(i - 1);
			}
			for (int i = task.getRemovesTokensFromFlow().size(); i > 0; i--) {
				task.getRemovesTokensFromFlow().remove(i - 1);
			}
		}
	}

	/**
	 * Configure nofi. Changes the multiple instance parameters according to the
	 * configuration parameters of the task task.
	 * 
	 * @param task
	 *            the task
	 */
	private void configureNofi(ExternalTaskFactsType task) {
		if (task instanceof MultipleInstanceExternalTaskFactsType) {
			MultipleInstanceExternalTaskFactsType miTask = (MultipleInstanceExternalTaskFactsType) task;
			NofiConfigType configuration = getNofiConfiguration(task);
			if (miTask.getMinimum() != null) {
				int min = (Integer.parseInt(miTask.getMinimum()) + configuration
						.getMinIncrease().intValue());
				miTask.setMinimum("" + min);
			}
			if (miTask.getMaximum() != null) {
				int max = (Integer.parseInt(miTask.getMaximum()) - configuration
						.getMaxDecrease().intValue());
				miTask.setMaximum(max + "");
			}
			if (miTask.getThreshold() != null) {
				int thres = (Integer.parseInt(miTask.getThreshold()) + configuration
						.getThresIncrease().intValue());
				miTask.setThreshold(thres + "");
			}
			if (miTask.getCreationMode() != null
					&& configuration.getCreationMode().equals("restrict")) {
				miTask.getCreationMode().setCode(CreationModeCodeType.STATIC);
			}
		}
	}

	/**
	 * Method buildPorts
	 * 
	 * builds recursively an Array of all ports from the List of Flows
	 * 
	 * @param List
	 *            <String> todoElements List of Flows
	 * 
	 * 
	 */
	private List<List<String>> buildPorts(List<String> todoElements) {

		List<String> newTodo = new ArrayList<String>();
		List<List<String>> ownElements = new ArrayList<List<String>>();
		List<List<String>> newElements = new ArrayList<List<String>>();

		boolean addElement = true;
		if (todoElements.size() == 1) {
			ownElements.add(todoElements);
		} else {
			for (String element : todoElements) {
				newTodo.addAll(todoElements);
				newTodo.remove(element);
				newElements = buildPorts(newTodo);
				for (List<String> newElement : newElements) {
					for (List<String> ownElement : ownElements) {
						if (ownElement.containsAll(newElement)
								&& ownElement.size() == newElement.size()) {
							addElement = false;
							break;
						}
					}
					if (addElement) {
						ownElements.add(newElement);
						List<String> combinedElement = new ArrayList<String>();
						combinedElement.add(element);
						combinedElement.addAll(newElement);
						addElement = true;
						for (List<String> ownElement : ownElements) {
							if (ownElement.containsAll(combinedElement)
									&& ownElement.size() == combinedElement
											.size()) {
								addElement = false;
								break;
							}
						}
						if (addElement) {
							ownElements.add(combinedElement);
						}

					}

					addElement = true;
				}
				newTodo = new ArrayList<String>();

			}
		}
		newElements.clear();
		return ownElements;
	}

	/**
	 * Method removeFlow
	 * 
	 * removes the flow into a next element and resets the ordering and a
	 * default flow
	 * 
	 * @param List
	 *            <Flowsintotype> list of all flows from the task
	 * @param int index the index of the flow from the list that should be
	 *        removed
	 * 
	 * 
	 */

	private void removeFlow(List<FlowsIntoType> flows, int index) {
		// Element that should be removed is currently default flow:
		if (flows.get(index).getIsDefaultFlow() != null) {
			if (flows.size() > 1) {
				int biggestOrdering = 0;
				int newDefaultFlow = 0;
				// Find element with highest ordering to become new default flow
				for (int i = 0; i < flows.size(); i++) {
					PredicateType predicateFlow = flows.get(i).getPredicate();
					if (!flows.get(i).getNextElementRef().getId()
							.equals("001__deadConditionoutput")
							&& predicateFlow != null
							&& predicateFlow.getOrdering() != null) {
						if (predicateFlow.getOrdering().intValue() > biggestOrdering) {
							newDefaultFlow = i;
						}
					}
				}
				// Remove predicate from the new default flow and set it
				flows.get(newDefaultFlow).setIsDefaultFlow("");
				flows.get(newDefaultFlow).setPredicate(null);
			}
		} else {
			// Not default flow that is removed: Lower predicate orderings for
			// Predicates with numbers higher than the removed one
			if (flows.size() > 1) {
				PredicateType predicate = flows.get(index).getPredicate();
				if (predicate != null && predicate.getOrdering() != null) {
					int orderingRemoved = predicate.getOrdering().intValue();
					for (FlowsIntoType flow : flows) {
						PredicateType predicateFlow = flow.getPredicate();
						if (predicateFlow != null
								&& predicateFlow.getOrdering() != null) {
							if (predicateFlow.getOrdering().intValue() > orderingRemoved) {
								predicateFlow.setOrdering(BigInteger
										.valueOf(predicateFlow.getOrdering()
												.intValue() - 1));
							}
						}
					}

				}
			}

		}
		// Remove the flow
		flows.remove(index);

	}

	/**
	 * Configure split.
	 * 
	 * @param task
	 *            the task that should be configured
	 * @param elements
	 *            the elements of the YAWL net
	 */
	private void configureSplit(ExternalTaskFactsType task,
			ProcessControlElementsExtended elements) {
		String behavior = task.getSplit().getCode().value();
		List<String> flows = new ArrayList<String>();
		if (behavior.equals("xor")) {
			// XOR-Split:
			for (int i = task.getFlowsInto().size(); i > 0; i--) {
				flows.add(task.getFlowsInto().get(i - 1).getNextElementRef()
						.getId());
				if (getOutputPortConfiguration(task, flows).equals("blocked")) {
					removedElements.addRemovedFlow(specificationID, netID,
							task.getId(), task.getFlowsInto().get(i - 1)
									.getNextElementRef().getId());
					removeFlow(task.getFlowsInto(), i - 1);
				}
				flows.clear();
			}

		} else if (behavior.equals("and")) {
			// AND-Split:
			for (int i = 0; i < task.getFlowsInto().size(); i++) {
				flows.add(task.getFlowsInto().get(i).getNextElementRef()
						.getId());
			}
			if (getOutputPortConfiguration(task, flows).equals("blocked")) {
				for (int i = task.getFlowsInto().size(); i > 0; i--) {
					removedElements.addRemovedFlow(specificationID, netID,
							task.getId(), task.getFlowsInto().get(i - 1)
									.getNextElementRef().getId());
				}
				task.getFlowsInto().clear();
			}
			flows.clear();

		} else if (behavior.equals("or")) {
			// OR-Split
			boolean switchToXOR = true;
			// collect all inflows
			Map<String, List<List<String>>> activated = new HashMap<String, List<List<String>>>();
			Map<String, List<List<String>>> blocked = new HashMap<String, List<List<String>>>();
			Map<String, String> predicate = new HashMap<String, String>();

			for (int i = task.getFlowsInto().size(); i > 0; i--) {
				flows.add(task.getFlowsInto().get(i - 1).getNextElementRef()
						.getId());
				List<List<String>> activatedPorts = new ArrayList<List<String>>();
				List<List<String>> blockedPorts = new ArrayList<List<String>>();
				activated.put(task.getFlowsInto().get(i - 1)
						.getNextElementRef().getId(), activatedPorts);
				blocked.put(task.getFlowsInto().get(i - 1).getNextElementRef()
						.getId(), blockedPorts);
				predicate.put(task.getFlowsInto().get(i - 1)
						.getNextElementRef().getId(),
						task.getFlowsInto().get(i - 1).getPredicate()
								.getValue());
			}
			// build list of Ports
			List<List<String>> ports = buildPorts(flows);
			List<String> keepFlows = new ArrayList<String>();

			for (List<String> port : ports) {
				if (getOutputPortConfiguration(task, port).equals("activated")) {
					keepFlows.addAll(port);
					for (String flow : port) {
						activated.get(flow).add(port);
					}

					// TODO: Check if Update of Data Perspective is correct for
					// >2 Flows
					// If all ports have only one outgoing flow, then switch
					// split behavior to XOR
					if (port.size() > 1) {
						switchToXOR = false;
					}
				} else {
					for (String flow : port) {
						blocked.get(flow).add(port);
					}

				}
			}
			// if (DEBUG)
			// System.out.println("Keep Flows: ".concat(keepFlows.toString()));
			for (int i = 0; i < flows.size(); i++) {
				if (!keepFlows.contains(flows.get(i))) {
					// if (DEBUG) System.out.println("Remove Flow:");
					// if (DEBUG) System.out.println(flows.get(i));
					ExternalNetElementType element = elements
							.getExternalNetElementFacts(flows.get(i));
					if (!(element instanceof ExternalConditionFactsType)) {
						ExternalConditionFactsType deadSource = addDeadCondition(
								elements, "input");
						deadSource.getFlowsInto().add(
								getFlowsInto(task, flows.get(i)));
					}
					removeFlowsInto(task, flows.get(i));
				} else {
					if (!blocked.get(flows.get(i)).isEmpty()) {
						String newPredicate = "((";
						for (List<String> port : activated.get(flows.get(i))) {
							for (String flow : port) {
								newPredicate = newPredicate.concat(
										predicate.get(flow)).concat(" and ");
							}
							newPredicate = newPredicate.substring(0,
									newPredicate.length() - 5).concat(") or (");
						}
						newPredicate = newPredicate.substring(0,
								newPredicate.length() - 5)
								.concat(") and not((");
						for (List<String> port : blocked.get(flows.get(i))) {
							boolean superPortExists = false;
							List<String> activatedSuperPort = new ArrayList<String>();
							for (List<String> activatedPort : activated
									.get(flows.get(i))) {
								if (activatedPort.containsAll(port)
										&& activatedPort.size() > activatedSuperPort
												.size()) {
									activatedSuperPort = activatedPort;
								}
								superPortExists = true;
							}
							for (String flow : port) {
								newPredicate = newPredicate.concat(
										predicate.get(flow)).concat(" and ");
							}
							if (superPortExists) {
								for (String flow : activatedSuperPort) {
									if (!port.contains(flow)) {
										newPredicate = newPredicate
												.concat("not(")
												.concat(predicate.get(flow))
												.concat(") and ");

									}
								}

							}
							newPredicate = newPredicate.substring(0,
									newPredicate.length() - 5).concat(") or (");
						}

						newPredicate = newPredicate.substring(0,
								newPredicate.length() - 5).concat(")");
						FlowsIntoType flow = getFlowsInto(task, flows.get(i));
						if (flow.getPredicate() != null) {
							flow.getPredicate().setValue(newPredicate);
						} else {
							// TODO: check why predicate does not exist...
							PredicateType p = new PredicateType();
							p.setValue(newPredicate);
							flow.setPredicate(p);
						}
					}
				}
			}
			// Switch task from OR to AND
			if (task.getFlowsInto().size() == 1) {
				task.getSplit().setCode(ControlTypeCodeType.AND);
				removedElements.addChangedDecoration(specificationID, netID,
						task.getId(), "split", "AND");
				LayoutNetFactsType net = findLayoutNet(netID);
				Iterator<Object> containers = net.getFrameOrScaleOrVertex()
						.iterator();
				while (containers.hasNext()) {
					Object container = containers.next();
					if (container instanceof LayoutContainerFactsType
							&& ((LayoutContainerFactsType) container).getId()
									.equals(task.getId())) {
						Iterator<Object> decorators = ((LayoutContainerFactsType) container)
								.getVertexOrLabelOrDecorator().iterator();
						while (decorators.hasNext()) {
							Object decorator = decorators.next();
							if (decorator instanceof LayoutDecoratorFactsType
									&& ((LayoutDecoratorFactsType) decorator)
											.getType().endsWith("split")) {
								((LayoutDecoratorFactsType) decorator)
										.setType("AND_split");
							}
						}
					}
				}
			}
			// Switch task from OR to XOR
			if (switchToXOR) {
				if (task.getSplit().getCode().equals(ControlTypeCodeType.OR)) {
					task.getSplit().setCode(ControlTypeCodeType.XOR);
					removedElements.addChangedDecoration(specificationID,
							netID, task.getId(), "split", "XOR");
					// we need to define an order for the predicate evaluation:
					// predicates that just evaluate to true are put at the end
					// of the ordering, all others remain in the same order as
					// they appeared so far
					LayoutNetFactsType net = findLayoutNet(netID);
					Iterator<Object> containers = net.getFrameOrScaleOrVertex()
							.iterator();
					while (containers.hasNext()) {
						Object container = containers.next();
						if (container instanceof LayoutContainerFactsType
								&& ((LayoutContainerFactsType) container)
										.getId().equals(task.getId())) {
							Iterator<Object> decorators = ((LayoutContainerFactsType) container)
									.getVertexOrLabelOrDecorator().iterator();
							while (decorators.hasNext()) {
								Object decorator = decorators.next();
								if (decorator instanceof LayoutDecoratorFactsType
										&& ((LayoutDecoratorFactsType) decorator)
												.getType().endsWith("split")) {
									((LayoutDecoratorFactsType) decorator)
											.setType("XOR_split");
								}
							}
						}
					}

					List<FlowsIntoType> flowsInto = task.getFlowsInto();
					int last = flowsInto.size() - 2;
					int first = 0;
					boolean defaultset = false;
					for (int index = 0; index < flowsInto.size(); index++) {
						if (flowsInto.get(index).getIsDefaultFlow() != null) {
							defaultset = true;
						}
					}
					for (int index = 0; index < flowsInto.size(); index++) {
						if (flowsInto.get(index).getPredicate() != null
								&& flowsInto.get(index).getIsDefaultFlow() != null) {
							flowsInto.get(index).setPredicate(null);
						}
						if (flowsInto.get(index).getPredicate() != null) {
							if (flowsInto.get(index).getPredicate().getValue()
									.equals("true()")) {
								if (!defaultset) {
									flowsInto.get(index).setIsDefaultFlow("");
									defaultset = true;
								} else {
									flowsInto
											.get(index)
											.getPredicate()
											.setOrdering(
													BigInteger.valueOf(last));
									last--;
								}
							} else {
								flowsInto.get(index).getPredicate()
										.setOrdering(BigInteger.valueOf(first));
								first++;
							}
						}
					}
					// If still not set then just take the last element in the
					// list as default.
					if (!defaultset) {
						flowsInto.get(first - 1).setIsDefaultFlow("");
						flowsInto.get(first - 1).setPredicate(null);
						defaultset = true;
					}

				}
			}

		}

	}

	private List<String> getElementsWithInputFlows(String taskID,
			ProcessControlElementsExtended processElements) {
		List<String> inputFlows = new ArrayList<String>();
		List<ExternalNetElementFactsType> elements = new ArrayList<ExternalNetElementFactsType>();
		elements.addAll(processElements.getTask());
		elements.addAll(processElements.getCondition());
		elements.add(processElements.getInputCondition());
		for (ExternalNetElementFactsType element : elements) {
			for (int i = 0; i < element.getFlowsInto().size(); i++) {
				if (element.getFlowsInto().get(i).getNextElementRef().getId()
						.equals(taskID)) {
					inputFlows.add(element.getId());
					break;
				}
			}
		}
		return inputFlows;
	}

	private ExternalConditionFactsType addDeadCondition(
			ProcessControlElementsExtended elements, String type) {
		try {
			if (elements.getCondition("001__deadCondition".concat(type)) == null) {
				ExternalConditionFactsType deadCondition = new ExternalConditionFactsType();
				deadCondition.setId("001__deadCondition".concat(type));
				deadCondition
						.setName("Dead ".concat(type).concat(" Condition"));
				elements.getTaskOrCondition().add(deadCondition);
				return deadCondition;
			} else {
				return elements.getCondition("001__deadCondition".concat(type));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;// negative termination: some problem has occurred TODO:
						// verify all the cases and read the model type: choose
						// the algorithm accordingly
		}
	}

	private ExternalTaskFactsType addTauTask(
			ProcessControlElementsExtended elements, ExternalTaskFactsType task) {
		try {
			// tau-Task does not exist yet:
			if (elements.getTask("tau-".concat(task.getId())) == null) {
				ExternalTaskFactsType tauTask = new ExternalTaskFactsType();
				tauTask.setId("tau-".concat(task.getId()));
				tauTask.setName("tau of ".concat(task.getName().toString()));
				removedElements.addHiddenTask(specificationID, netID,
						task.getId());
				for (FlowsIntoType flow : task.getFlowsInto()) {
					tauTask.getFlowsInto().add(flow);
				}
				tauTask.setJoin(task.getJoin());
				tauTask.setSplit(task.getSplit());
				if (task.getStartingMappings() != null)
					tauTask.setStartingMappings(task.getStartingMappings());
				if (task.getCompletedMappings() != null)
					tauTask.setCompletedMappings(task.getCompletedMappings());
				if (task.getEnablementMappings() != null)
					tauTask.setEnablementMappings(task.getEnablementMappings());
				for (ExternalNetElementType vRemovesToken : task
						.getRemovesTokens()) {
					tauTask.getRemovesTokens().add(vRemovesToken);
				}
				for (RemovesTokensFromFlowType vRemovesTokensFromFlow : task
						.getRemovesTokensFromFlow()) {
					tauTask.getRemovesTokensFromFlow().add(
							vRemovesTokensFromFlow);
				}
				elements.getTaskOrCondition().add(tauTask);
				return tauTask;
			} else {
				// tau-Task exists already
				return elements.getTask("tau-".concat(task.getId()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;// negative termination: some problem has occurred TODO:
						// verify all the cases and read the model type: choose
						// the algorithm accordingly
		}
	}

	private void configureJoin(ExternalTaskFactsType task,
			ProcessControlElementsExtended elements) {
		String behavior = task.getJoin().getCode().value();
		List<String> flows = new ArrayList<String>();
		flows = getElementsWithInputFlows(task.getId(), elements);
		if (behavior.equals("xor")) {

			for (int i = 0; i < flows.size(); i++) {
				List<String> flowList = new ArrayList<String>();
				flowList.add(flows.get(i));
				String test = getInputPortConfiguration(task, flowList);
				if (test.equals("blocked")) {
					ExternalNetElementType element = elements
							.getExternalNetElementFacts(flows.get(i));
					if (element instanceof ExternalConditionFactsType) {
						// condtion-task connection remove flow
						removeFlowsInto((ExternalNetElementFactsType) element,
								task.getId());
					} else {
						// direct task-task connection reconnect 1st task to
						// artificial dead place
						String deadTarget = addDeadCondition(elements, "output")
								.getId();
						getFlowsInto((ExternalNetElementFactsType) element,
								task.getId()).getNextElementRef().setId(
								deadTarget);
					}
				} else if (getInputPortConfiguration(task, flowList).equals(
						"hidden")) {
					ExternalTaskFactsType tauTask = addTauTask(elements, task);
					ExternalNetElementType element = elements
							.getExternalNetElementFacts(flows.get(i));
					getFlowsInto((ExternalNetElementFactsType) element,
							task.getId()).getNextElementRef().setId(
							tauTask.getId());
					if (element instanceof ExternalTaskFactsType) {
						ExternalTaskFactsType elementT = (ExternalTaskFactsType) element;
						if (elementT.getConfiguration() != null
								&& elementT.getConfiguration().getSplit() != null
								&& elementT.getConfiguration().getSplit()
										.getPort() != null) {
							for (OutputPortConfigType port : elementT
									.getConfiguration().getSplit().getPort()) {
								for (ExternalNetElementType flowDest : port
										.getFlowDestination()) {
									if (flowDest.getId().equals(task.getId())) {
										flowDest.setId(tauTask.getId());
									}
								}
							}
						}
					}
					for (FlowsIntoType flow : tauTask.getFlowsInto()) {
						ExternalNetElementType elementTarget = elements
								.getExternalNetElementFacts(flow
										.getNextElementRef().getId());
						if (elementTarget instanceof ExternalTaskFactsType) {
							ExternalTaskFactsType elementT = (ExternalTaskFactsType) elementTarget;
							if (elementT.getConfiguration() != null
									&& elementT.getConfiguration().getJoin() != null
									&& elementT.getConfiguration().getJoin()
											.getPort() != null) {
								for (InputPortConfigType port : elementT
										.getConfiguration().getJoin().getPort()) {
									for (ExternalNetElementType flowSource : port
											.getFlowSource()) {
										if (flowSource.getId().equals(
												task.getId())) {
											flowSource.setId(tauTask.getId());
										}
									}
								}
							}
						}

					}
				}
				flowList.clear();
			}
		} else {
			if (getInputPortConfiguration(task, flows).equals("blocked")) {
				// forall incoming arcs:
				for (int i = 0; i < flows.size(); i++) {
					ExternalNetElementType element = elements
							.getExternalNetElementFacts(flows.get(i));
					if (element instanceof ExternalConditionFactsType) {
						if (DEBUG)
							System.out.println("RemoveFlow:");
						removeFlowsInto((ExternalNetElementFactsType) element,
								task.getId());
					} else {
						String deadTarget = addDeadCondition(elements, "output")
								.getId();
						getFlowsInto((ExternalNetElementFactsType) element,
								task.getId()).getNextElementRef().setId(
								deadTarget);
					}
				}
			} else if (getInputPortConfiguration(task, flows).equals("hidden")) {
				// TODO: simplify code: tau task is task without decomposition
				for (int i = 0; i < flows.size(); i++) {
					ExternalTaskFactsType tauTask = addTauTask(elements, task);
					ExternalNetElementType element = elements
							.getExternalNetElementFacts(flows.get(i));
					getFlowsInto((ExternalNetElementFactsType) element,
							task.getId()).getNextElementRef().setId(
							tauTask.getId());
				}
			}
		}

	}

	private List<String> tasksOnPath(ProcessControlElementsExtended elements) {
		List<String> onPathElements = new ArrayList<String>();
		onPathElements.add(elements.getOutputCondition().getId());
		onPathElements.add(elements.getInputCondition().getId());

		List<String> reachableFromI = new ArrayList<String>();
		tasksOnPathFromI(elements.getInputCondition(), elements, reachableFromI);

		List<String> onPathElementsToO = new ArrayList<String>();
		onPathElementsToO.add(elements.getOutputCondition().getId());

		Iterator<ExternalTaskFactsType> tasks = elements.getTask().iterator();
		while (tasks.hasNext()) {
			ExternalTaskFactsType task = tasks.next();
			List<String> visitedElements = new ArrayList<String>();
			if (tasksOnPathToO(task, elements, visitedElements,
					onPathElementsToO) && reachableFromI.contains(task.getId())) {
				onPathElements.add(task.getId());
			}

		}
		Iterator<ExternalConditionFactsType> conditions = elements
				.getCondition().iterator();
		while (conditions.hasNext()) {
			ExternalConditionFactsType condition = conditions.next();
			List<String> visitedElements = new ArrayList<String>();
			if (tasksOnPathToO(condition, elements, visitedElements,
					onPathElementsToO)
					&& reachableFromI.contains(condition.getId())) {
				onPathElements.add(condition.getId());
			}

		}

		return onPathElements;
	}

	private boolean tasksOnPathToO(ExternalNetElementFactsType start,
			ProcessControlElementsExtended elements,
			List<String> visitedElements, List<String> onPathElements) {
		if (onPathElements.contains(start.getId())) {
			return true;
		} else if (start.getFlowsInto().size() == 0) {
			return false;
		}
		boolean onPath = false;
		List<FlowsIntoType> flows = start.getFlowsInto();
		for (FlowsIntoType flow : flows) {
			if (elements.getExternalNetElementFacts(flow.getNextElementRef()
					.getId()) != null) {
				ExternalNetElementType nextElement = elements
						.getExternalNetElementFacts(flow.getNextElementRef()
								.getId());
				if (!visitedElements.contains(nextElement.getId())) {
					visitedElements.add(nextElement.getId());
					if (!(!(nextElement instanceof OutputConditionFactsType) && !tasksOnPathToO(
							(ExternalNetElementFactsType) nextElement,
							elements, visitedElements, onPathElements))) {
						onPathElements.add(start.getId());
						onPath = true;
					}

				} else {
					if (onPathElements.contains(nextElement.getId())) {
						onPathElements.add(start.getId());
						onPath = true;
					}
				}
			}
		}
		return onPath;
	}

	private void tasksOnPathFromI(ExternalNetElementFactsType start,
			ProcessControlElementsExtended elements,
			List<String> visitedElements) {
		List<FlowsIntoType> flows = start.getFlowsInto();
		for (FlowsIntoType flow : flows) {
			ExternalNetElementType nextElement = elements
					.getExternalNetElementFacts(flow.getNextElementRef()
							.getId());
			if (!visitedElements.contains(nextElement.getId())) {
				visitedElements.add(nextElement.getId());
				if (!(nextElement instanceof OutputConditionFactsType)) {
					tasksOnPathFromI((ExternalNetElementFactsType) nextElement,
							elements, visitedElements);
				}
			}
		}

	}

	private LayoutNetFactsType findLayoutNet(String id) {
		Iterator<LayoutFactsType.Specification> specifications = layout
				.getSpecification().iterator();
		while (specifications.hasNext()) {
			LayoutFactsType.Specification specification = specifications.next();
			if (specification.getId().equals(specificationID)) {
				Iterator<LayoutNetFactsType> nets = specification.getNet()
						.iterator();
				while (nets.hasNext()) {
					LayoutNetFactsType net = nets.next();
					if (net.getId().equals(id)) {
						return net;
					}
				}
			}
		}
		return null;

	}

	// this method retrieves the main yawl specification and returns it as
	// NetFactsType element
	private NetFactsType retrieveRootNet() {
		YAWLSpecificationFactsType s = model.getSpecification().get(0);

		int size = s.getDecomposition().size();
		for (int index = 0; index < size; index++) {
			if (s.getDecomposition().get(index) != null) {
				NetFactsType value = (NetFactsType) s.getDecomposition().get(
						index);
				if (value.isIsRootNet()) {
					specificationID = s.getUri();
					netID = value.getId();
					return value;
				}
			}
		}
		return null;
	}

}
