package de.hpi.bpmn2_0.factory.node;

/**
 * Copyright (c) 2009
 * Philipp Giese, Sven Wagner-Boysen
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import javax.xml.namespace.QName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.oryxeditor.server.diagram.generic.GenericShape;

import de.hpi.bpmn2_0.annotations.Property;
import de.hpi.bpmn2_0.annotations.StencilId;
import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.factory.AbstractActivityFactory;
import de.hpi.bpmn2_0.model.FormalExpression;
import de.hpi.bpmn2_0.model.activity.Activity;
import de.hpi.bpmn2_0.model.activity.CallActivity;
import de.hpi.bpmn2_0.model.activity.Task;
import de.hpi.bpmn2_0.model.activity.misc.BusinessRuleTaskImplementation;
import de.hpi.bpmn2_0.model.activity.misc.Operation;
import de.hpi.bpmn2_0.model.activity.misc.ServiceImplementation;
import de.hpi.bpmn2_0.model.activity.misc.UserTaskImplementation;
import de.hpi.bpmn2_0.model.activity.resource.ActivityResource;
import de.hpi.bpmn2_0.model.activity.resource.HumanPerformer;
import de.hpi.bpmn2_0.model.activity.resource.Performer;
import de.hpi.bpmn2_0.model.activity.resource.PotentialOwner;
import de.hpi.bpmn2_0.model.activity.resource.Resource;
import de.hpi.bpmn2_0.model.activity.resource.ResourceAssignmentExpression;
import de.hpi.bpmn2_0.model.activity.type.BusinessRuleTask;
import de.hpi.bpmn2_0.model.activity.type.ManualTask;
import de.hpi.bpmn2_0.model.activity.type.ReceiveTask;
import de.hpi.bpmn2_0.model.activity.type.ScriptTask;
import de.hpi.bpmn2_0.model.activity.type.SendTask;
import de.hpi.bpmn2_0.model.activity.type.ServiceTask;
import de.hpi.bpmn2_0.model.activity.type.UserTask;
import de.hpi.bpmn2_0.model.callable.GlobalTask;
import de.hpi.bpmn2_0.model.data_object.Message;

/**
 * Concrete class to create any kind of task objects from a {@link GenericShape} with
 * the stencil id "http://b3mn.org/stencilset/bpmn2.0#Task"
 * 
 * @author Philipp Giese
 * @author Sven Wagner-Boysen
 * 
 */
@StencilId("Task")
public class TaskFactory extends AbstractActivityFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.hpi.bpmn2_0.factory.AbstractBpmnFactory#createProcessElement(org.
	 * oryxeditor.server.diagram.Shape)
	 */
	// @Override
	protected Activity createProcessElement(GenericShape shape)
			throws BpmnConverterException {
		try {
			Task task = (Task) this.invokeCreatorMethodAfterProperty(shape);
			this.setStandardAttributes(task, shape);
			
			CallActivity ca = this.handleCallActivity(task, shape);
			if(ca != null) {
				return ca;
			}
			
			return task;
		} catch (Exception e) {
			throw new BpmnConverterException(
					"Error while creating the process element of "
							+ shape.getStencilId(), e);
		}
	}

	@Property(name = "tasktype", value = "None")
	public Task createTask(GenericShape shape) {
		Task task = new Task();

		task.setId(shape.getResourceId());
		task.setName(shape.getProperty("name"));

		return task;
	}

	@Property(name = "tasktype", value = "User")
	public UserTask createUserTask(GenericShape shape) {
		UserTask task = new UserTask();

		task.setId(shape.getResourceId());
		task.setName(shape.getProperty("name"));

		/* Set implementation property */
		String implementation = shape.getProperty("implementation");
		if (implementation != null) {
			task.setImplementation(UserTaskImplementation
					.fromValue(implementation));
		}

		/* Set ActivityResources */
		String resourcesProperty = shape.getProperty("resources");
		if (resourcesProperty != null) {
			this.setActivityResources(task, resourcesProperty);
		}

		return task;
	}

	@Property(name = "tasktype", value = "Receive")
	public ReceiveTask createReceiveTask(GenericShape shape) {
		ReceiveTask task = new ReceiveTask();

		task.setId(shape.getResourceId());
		task.setName(shape.getProperty("name"));
		
		/* Implementation */
		String implementation = shape.getProperty("implementation");
		if(implementation != null && !(implementation.length() == 0))
			task.setImplementation(ServiceImplementation.fromValue(implementation));
		
		/* Define Operation of the service task */
		String operationString = shape.getProperty("operationref");
		if(operationString != null && !(operationString.length() == 0)) {
			task.setOperationRef(new QName(operationString));
		}
		
		/* Message */
		String messageString = shape.getProperty("messageref");
		if(messageString != null && !(messageString.length() == 0)) {
			task.setMessageRef(new QName(messageString));
		}
		
		/* Handle initiate flag */
		String instantiate = shape.getProperty("instantiate");
		if(instantiate != null && instantiate.equalsIgnoreCase("true"))
			task.setInstantiate(true);
		else
			task.setInstantiate(false);
		
		return task;
	}

	@Property(name = "tasktype", value = "Send")
	public SendTask createSendTask(GenericShape shape) {
		SendTask task = new SendTask();

		task.setId(shape.getResourceId());
		task.setName(shape.getProperty("name"));
		
		/* Implementation */
		String implementation = shape.getProperty("implementation");
		if(implementation != null && !(implementation.length() == 0))
			task.setImplementation(ServiceImplementation.fromValue(implementation));
		
		/* Define Operation of the service task */
		String operationString = shape.getProperty("operationref");
		if(operationString != null && !(operationString.length() == 0)) {
			Operation operation = new Operation();
			operation.setId(operationString);
			task.setOperationRef(operation);
		}
		
		/* Message */
		String messageString = shape.getProperty("messageref");
		if(messageString != null && !(messageString.length() == 0)) {
			Message message = new Message();
			message.setId(messageString);
			task.setMessageRef(message);
		}

		return task;
	}

	@Property(name = "tasktype", value = "Script")
	public ScriptTask createScriptTask(GenericShape shape) {
		ScriptTask task = new ScriptTask();

		task.setId(shape.getResourceId());
		task.setName(shape.getProperty("name"));

		String scriptFormat = shape.getProperty("scriptformat");
		if (scriptFormat != null) {
			task.setScriptFormat(scriptFormat);
		}

		String script = shape.getProperty("script");
		if (script != null) {
			task.setScript(script);
		}

		return task;
	}

	@Property(name = "tasktype", value = "Business Rule")
	public BusinessRuleTask createBusinessRuleTask(GenericShape shape) {
		BusinessRuleTask task = new BusinessRuleTask();

		task.setId(shape.getResourceId());
		task.setName(shape.getProperty("name"));
		
		/* Implementation */
		String implementation = shape.getProperty("implementation");
		if(implementation != null && !(implementation.length() == 0))
			task.setImplementation(BusinessRuleTaskImplementation.fromValue(implementation));

		return task;
	}

	@Property(name = "tasktype", value = "Service")
	public ServiceTask createServiceTask(GenericShape shape) {
		ServiceTask task = new ServiceTask();

		task.setId(shape.getResourceId());
		task.setName(shape.getProperty("name"));
		
		String implementation = shape.getProperty("implementation");
		if(implementation != null && !(implementation.length() == 0))
			task.setImplementation(ServiceImplementation.fromValue(implementation));
		
		/* Define Operation of the service task */
		String operationString = shape.getProperty("operationref");
		if(operationString != null && !(operationString.length() == 0)) {
			Operation operation = new Operation();
			operation.setId(operationString);
			task.setOperationRef(new QName(operationString));
		}

		return task;
	}

	@Property(name = "tasktype", value = "Manual")
	public ManualTask createManualTask(GenericShape shape) {
		ManualTask task = new ManualTask();

		task.setId(shape.getResourceId());
		task.setName(shape.getProperty("name"));

		return task;
	}

	/**
	 * Retrieves the values from the complex type property 'resources' and
	 * builds ups the resources objects.
	 * 
	 * @param task
	 *            The {@link Task} object
	 * @param resourcesProperty
	 *            The resources property String.
	 */
	private void setActivityResources(Task task, String resourcesProperty) {
		try {
			JSONObject resources = new JSONObject(resourcesProperty);
			JSONArray items = resources.getJSONArray("items");
			for (int i = 0; i < items.length(); i++) {
				JSONObject resource = items.getJSONObject(i);
				String type = resource.getString("resource_type");
				ActivityResource actResource = null;
				if (type.equalsIgnoreCase("performer")) {
					actResource = new Performer();

				} else if (type.equalsIgnoreCase("humanperformer")) {
					actResource = new HumanPerformer();
				} else if (type.equalsIgnoreCase("potentialowner")) {
					actResource = new PotentialOwner();
				}

				if (actResource != null) {
					/* Set ResourceRef */
					Resource resourceRef = new Resource(resource
							.getString("resource"));
					actResource.setResourceRef(resourceRef);

					/* Set Resource Assignment Expression */
					ResourceAssignmentExpression resAsgExpr = new ResourceAssignmentExpression();
					FormalExpression fExpr = new FormalExpression(resource.getString("resourceassignmentexpr"));
					
					String language = resource.getString("language");
					if(language != null && !(language.length() == 0)) {
						fExpr.setLanguage(language);
					}
					
					String evaluationType = resource.getString("evaluatestotype");
					if(evaluationType != null && !(evaluationType.length() == 0)) {
						fExpr.setEvaluatesToTypeRef(evaluationType);
					}
					
					resAsgExpr.setExpression(fExpr);
					actResource.setResourceAssignmentExpression(resAsgExpr);
					
					/* Assign ActivityResource */
					task.getActivityResource().add(actResource);
				}

			}
		} catch (JSONException e) {
			// ignore resources property
		}
	}
	
	/**
	 * In case the "callacitivity" property is set to true, the Task t gets 
	 * converted to an {@link CallActivity} referencing a {@link GlobalTask} 
	 * depending on the original task type.
	 *  
	 * @param t
	 * @param s
	 * @return
	 */
	private CallActivity handleCallActivity(Task t, GenericShape s) {
		if(s.getProperty("callacitivity") == null || !s.getProperty("callacitivity").equalsIgnoreCase("true")) {
			return null;
		}
		
		GlobalTask gt = t.getAsGlobalTask();
		
		CallActivity ca = new CallActivity(t);
		ca.setCalledElement(gt);
		
		return ca;
	}
	
 //	private Operation createOperation(GenericShape shape) {
//		Operation operation = new Operation();
//		operation.setId(OryxUUID.generate());
//		operation.setName(shape.getProperty("operationname"));
//		
//		/* Handle in and out messages */
////		operation.setInMessageRef(this.createMessage(prefix, shape))
//		
//		return operation;
//	}
	
//	private Message createMessage(String prefix, GenericShape shape) {
//		Message msg = new Message();
//		msg.setName(shape.getProperty(prefix + "messagename"));
//		
//		return msg;
//	}
}
