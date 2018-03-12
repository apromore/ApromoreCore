package plugin.bpmn.to.maude.handlers;

import plugin.bpmn.to.maude.notation.*;
import plugin.bpmn.to.maude.getService.GetReqEditor;
import plugin.bpmn.to.maude.handlers.PostMultipleParameters;

import java.io.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;



/**
 * Created by Fabrizio Fornari on 18/12/2017.
 */
@SuppressWarnings("serial")
public class MaudeOperationEditor {

	public String originalModel;
	public String ParsedModel;

	boolean operation_done = false;

	private static ArrayList<String> poolList;
	private static ArrayList<String> taskList;
	private static ArrayList<String> inputMsgList;
	private static ArrayList<String> outputMsgList;

	private static String violatingTraceCounterexample;


	public static String doMaudeOperation(final String modelToParse, final String parsedModel, final String propertyToVerify, 
	String param, String poolName1, String poolName2, String taskName1 , String taskName2 , String msgName
	) throws IOException {

		//System.out.println("\nBefore MaudeOperationEditor \n");
		poolList=new  ArrayList<String>();
		taskList=new  ArrayList<String>();
		inputMsgList=new  ArrayList<String>();
		outputMsgList=new  ArrayList<String>();

		//Obtain PoolList and TaskList
		//obtainPoolAndTaskList(parsedModel, poolList, taskList);
		////System.out.println("\nAfter obtainPoolAndTaskList");
		PostMultipleParameters resultTrue= new PostMultipleParameters();

		obtainPoolAndTaskListAndMsgList(parsedModel, poolList, taskList, inputMsgList, outputMsgList);


		String poolListString = "";
		String taskListString = "";
		String inputMsgListString = "";
		String outputMsgListString = "";

		if(propertyToVerify.equals("getParsedModel")){

			try{
				for(int str = 0; str< poolList.size(); str++){
					poolListString+=poolList.get(str) + "$$$pool$$$";
				}
				poolListString= poolListString.substring(0, poolListString.length() - 10);

				
				for(int str = 0; str< taskList.size(); str++){
					taskListString+=taskList.get(str) + "$$$task$$$";
				}
				taskListString= taskListString.substring(0, taskListString.length() - 10);
				

				//////JOptionPane.showMessageDialog(null, "\ninputMsgList.size()\n"+inputMsgList.size());
				for(int str = 0; str< inputMsgList.size(); str++){
					//////JOptionPane.showMessageDialog(null, "\ninputMsgList.get(str)\n"+inputMsgList.get(str));
					inputMsgListString+=inputMsgList.get(str) + "$$$inputMsg$$$";
				}
				inputMsgListString= inputMsgListString.substring(0, inputMsgListString.length() - 14);
			

				for(int str = 0; str< outputMsgList.size(); str++){
					outputMsgListString+=outputMsgList.get(str) + "$$$outputMsg$$$";
				}
				outputMsgListString= outputMsgListString.substring(0, outputMsgListString.length() - 15);

			} catch (Exception e1msg) {
				//////JOptionPane.showMessageDialog(null, "\ngetParsedModel\n"+e1msg.getMessage());
				
				// editorPane.setText(result.getResult());
				//return result;
			}

			//////JOptionPane.showMessageDialog(null, "\ngetParsedModel\n"+resultTrue.getResult()+"$$$Separator$$$"+null+"$$$Separator$$$"+poolListString+"$$$Separator$$$"+taskListString+"$$$Separator$$$"+inputMsgListString+"$$$Separator$$$"+outputMsgListString);
			return resultTrue.getResult()+"$$$Separator$$$"+null+"$$$Separator$$$"+poolListString+"$$$Separator$$$"+taskListString+"$$$Separator$$$"+inputMsgListString+"$$$Separator$$$"+outputMsgListString;
		}

		if(param.equals("false")){
				 switch (propertyToVerify) {
		         case "Can a Process Start?":
						resultTrue = aProcessStart(modelToParse , parsedModel);
									  //////JOptionPane.showMessageDialog(null, "\npropertyToVerify\n"+propertyToVerify);

		             break;
		         case "Can a Process End?":
				 		//////JOptionPane.showMessageDialog(null, "\npropertyToVerify\n"+propertyToVerify);
						resultTrue = aProcessEnd(modelToParse , parsedModel);
						//////JOptionPane.showMessageDialog(null, "\nresultTrue\n"+resultTrue.getResult());
						//////JOptionPane.showMessageDialog(null, "\nresultTrue\n"+resultTrue.getViolatingTrace());

		        	 break;
		         case "Can All the Process End?":
				 		resultTrue = allProcessesEnd(modelToParse , parsedModel);
		        	 break;
		         case "No Dead Activities":
				 		resultTrue = noDeadActivities(modelToParse , parsedModel);
		             break;
		         case "Option to Complete":
				 		resultTrue = optionToComplete(modelToParse , parsedModel);
		             break;
		         case "Proper Completion [Control Flow Tokens Only]":
				 		resultTrue = properCompletionCF(modelToParse , parsedModel);
		        	 break;
		         case "Proper Completion [Message Flow Tokens Only]":
				 		resultTrue = properCompletionMF(modelToParse , parsedModel);
		             break;
		         case "Proper Completion [Control Flow and Message Flow Tokens]":
				 		resultTrue =  properCompletionCFMF(modelToParse , parsedModel);
		             break;
		         case "Safeness":
				 		resultTrue = safeness(modelToParse , parsedModel);
		             break;
		         default:
		             throw new IllegalArgumentException("Invalid property: " + propertyToVerify);
			 }
			}

			////JOptionPane.showMessageDialog(null,"propertyToVerify" + propertyToVerify);
			if(param.equals("true")){
				switch (propertyToVerify) {
						case "aBPoolSndMsg":
							resultTrue =  aBPoolSndMsg(modelToParse , parsedModel, poolName1, msgName);
							break;
						case "aBPoolRcvMsg":
							resultTrue =  aBPoolRcvMsg(modelToParse , parsedModel, poolName1, msgName);
							break;
						case "Task1ImpliesTask2":
							resultTrue =  Task1ImpliesTask2(modelToParse , parsedModel, taskName1, taskName2);
							break;
						case "Can the Task be Enabled?":
							resultTrue =  aTaskEnabledParameterized(modelToParse , parsedModel, taskName1);
							break;
						case "Can the Task Run?":
							resultTrue =  aTaskRunningParameterized(modelToParse , parsedModel, taskName1);
				 	    	break;
						case "Can the Task Complete?":
							resultTrue =  aTaskCompleteParameterized(modelToParse , parsedModel, taskName1);
							break;
						case "Can the Process Start?":
							resultTrue =  aProcessStartParam(modelToParse , parsedModel, poolName1);
							break;
						case "Can the Process End?":
							resultTrue =  aProcessEndParam(modelToParse , parsedModel, poolName1);
				 	    	break;
						case "No Dead Activities":
							resultTrue =  noDeadActivitiesParam(modelToParse , parsedModel, poolName1);
							break;
						case "Option to Complete":
							resultTrue =  optionToCompleteParam(modelToParse , parsedModel, poolName1);
							break;
						case "Proper Completion [Control Flow Tokens Only]":
							resultTrue =  properCompletionCFParam(modelToParse , parsedModel, poolName1);
							break;
						case "Proper Completion [Message Flow Tokens Only]":
							resultTrue =  properCompletionMFParam(modelToParse , parsedModel, poolName1);
							break;
						case "Proper Completion [Control Flow and Message Flow Tokens]":
							resultTrue =  properCompletionCFMFParam(modelToParse , parsedModel, poolName1);
							break;
						case "Safeness":
							resultTrue =  safenessParam(modelToParse , parsedModel, poolName1);
							break;
						default:
							throw new IllegalArgumentException("Invalid property: " + propertyToVerify+" for Pool Named: "+poolName1);
					}
			}

	String resultWellFormatted=resultTrue.getResult();
	////JOptionPane.showMessageDialog(null, "\nresultWellFormatted\n"+idToChange);
	
	if(resultTrue.getResult()!=null){
		if(resultTrue.getResult().contains("true")||resultTrue.getResult().contains("True")){
			resultWellFormatted = "True, the property is verified. Time required: "+resultTrue.getPropertyVerificationTime()+"ms";
		}
		if(resultTrue.getResult().contains("All the Pools respect ")){
			resultWellFormatted = resultWellFormatted+"Time required: "+resultTrue.getPropertyVerificationTime()+"ms";
		}
		
		if(resultTrue.getResult().contains("false")||resultTrue.getResult().contains("False")){
			resultWellFormatted = "False, the property is not verified. Time required: "+resultTrue.getPropertyVerificationTime()+"ms";
		}	

		if(resultTrue.getResult().contains("does not respect")){
			resultWellFormatted = resultWellFormatted+"Time required: "+resultTrue.getPropertyVerificationTime()+"ms";
		}	
	}else{
		 	resultWellFormatted = "False, the property is not verified. Time required: "+resultTrue.getPropertyVerificationTime()+"ms";
	}

	String idToChange=null;

	if(resultTrue.getViolatingTrace()!=null){
		Proc finalprocess = counterExampleReturnTest(resultTrue.getViolatingTrace());
		String realViolatingTrace = realViolatingTrace(finalprocess);
		idToChange = realViolatingTrace;
	}
	return resultWellFormatted+"$$$Separator$$$"+idToChange+"$$$Separator$$$"+poolListString+"$$$Separator$$$"+taskListString+"$$$Separator$$$"+inputMsgListString+"$$$Separator$$$"+outputMsgListString;



}

// //aProcessStart
	public static PostMultipleParameters aProcessStart(final String modelToParse, final String parsedModel){
		//operation_done=true;
		//editorPane.setText("");
		PostMultipleParameters result = new PostMultipleParameters();
		try {

			String originalModelUrl = modelToParse;
			PostMultipleParameters postMultiple = new PostMultipleParameters();
			postMultiple.setOriginalModel(originalModelUrl);
			postMultiple.setParsedModel(parsedModel);
			postMultiple.setProperty("aBPoolstarts");
			result=GetReqEditor.PostReq_BProve_Maude_WebService_Property(postMultiple);
		} catch (Exception e1) {
			result.setResult("\nERROR\n");
			// editorPane.setText(result.getResult());
			//return result;
		}

		return result;
	}



	public static PostMultipleParameters aProcessStartParam(final String modelToParse, final String parsedModel,final String poolName){
		//operation_done=true;
		//editorPane.setText("");
		PostMultipleParameters result = new PostMultipleParameters();
		try {

			String originalModelUrl = modelToParse;
			PostMultipleParameters postMultiple = new PostMultipleParameters();
			postMultiple.setOriginalModel(originalModelUrl);
			postMultiple.setParsedModel(parsedModel);
			postMultiple.setProperty("aBPoolstartsParameterized");
			postMultiple.setPoolName(poolName);
			result=GetReqEditor.PostReq_BProve_Maude_WebService_Property(postMultiple);
		} catch (Exception e1) {
			result.setResult("\nERROR\n");
			// editorPane.setText(result.getResult());
			//return result;
		}

		return result;
	}

	public static PostMultipleParameters aBPoolSndMsg(final String modelToParse, final String parsedModel,final String poolName, final String msgName){
		//operation_done=true;
		//editorPane.setText("");
		PostMultipleParameters result = new PostMultipleParameters();
		try {

			String originalModelUrl = modelToParse;
			PostMultipleParameters postMultiple = new PostMultipleParameters();
			postMultiple.setOriginalModel(originalModelUrl);
			postMultiple.setParsedModel(parsedModel);
			postMultiple.setProperty("aBPoolSndMsg");
			postMultiple.setPoolName(poolName);
			postMultiple.setSndMsgName(msgName);
			//////JOptionPane.showMessageDialog(null, "\nmsgName\n"+msgName);
			result=GetReqEditor.PostReq_BProve_Maude_WebService_Property(postMultiple);
		} catch (Exception e1) {
			result.setResult("\nERROR\n");
			// editorPane.setText(result.getResult());
			//return result;
		}

		return result;
	}

	public static PostMultipleParameters aBPoolRcvMsg(final String modelToParse, final String parsedModel,final String poolName, final String msgName){
		//operation_done=true;
		//editorPane.setText("");
		PostMultipleParameters result = new PostMultipleParameters();
		try {

			String originalModelUrl = modelToParse;
			PostMultipleParameters postMultiple = new PostMultipleParameters();
			postMultiple.setOriginalModel(originalModelUrl);
			postMultiple.setParsedModel(parsedModel);
			postMultiple.setProperty("aBPoolRcvMsg");
			postMultiple.setPoolName(poolName);
			postMultiple.setRcvMsgName(msgName);
			result=GetReqEditor.PostReq_BProve_Maude_WebService_Property(postMultiple);
		} catch (Exception e1) {
			result.setResult("\nERROR\n");
			// editorPane.setText(result.getResult());
			//return result;
		}

		return result;
	}

// //Task1ImpliesTask2
public static PostMultipleParameters Task1ImpliesTask2(final String modelToParse, final String parsedModel, final String taskName1, final String taskName2){
	// operation_done=true;
	// editorPane.setText("");
		
		PostMultipleParameters result = new PostMultipleParameters();

		try {
			String originalModelUrl = modelToParse;
			PostMultipleParameters postMultiple = new PostMultipleParameters();
			postMultiple.setOriginalModel(originalModelUrl);
			postMultiple.setParsedModel(parsedModel);
			postMultiple.setTaskName1(taskName1);
			postMultiple.setTaskName2(taskName2);
			postMultiple.setProperty("Task1ImpliesTask2");
			postMultiple.printPostMultipleParameters();
			result=GetReqEditor.PostReq_BProve_Maude_WebService_Property(postMultiple);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return result;
	}


//
// //aTaskEnabledParameterized
public static PostMultipleParameters aTaskEnabledParameterized(final String modelToParse, final String parsedModel, final String taskName1){
	// operation_done=true;
	// editorPane.setText("");
		
		PostMultipleParameters result = new PostMultipleParameters();

		try {
			String originalModelUrl = modelToParse;
			PostMultipleParameters postMultiple = new PostMultipleParameters();
			postMultiple.setOriginalModel(originalModelUrl);
			postMultiple.setParsedModel(parsedModel);
			postMultiple.setTaskName1(taskName1);
			postMultiple.setProperty("aTaskEnabledParameterized");
			result=GetReqEditor.PostReq_BProve_Maude_WebService_Property(postMultiple);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return result;
	}

	// //aTaskRunningParameterized
public static PostMultipleParameters aTaskRunningParameterized(final String modelToParse, final String parsedModel, final String taskName1){
	// operation_done=true;
	// editorPane.setText("");
		
		PostMultipleParameters result = new PostMultipleParameters();

		try {
			String originalModelUrl = modelToParse;
			PostMultipleParameters postMultiple = new PostMultipleParameters();
			postMultiple.setOriginalModel(originalModelUrl);
			postMultiple.setParsedModel(parsedModel);
			postMultiple.setTaskName1(taskName1);
			postMultiple.setProperty("aTaskRunningParameterized");
			result=GetReqEditor.PostReq_BProve_Maude_WebService_Property(postMultiple);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return result;
	}

	// //aProcessEnd
public static PostMultipleParameters aTaskCompleteParameterized(final String modelToParse, final String parsedModel, final String taskName1){
	// operation_done=true;
	// editorPane.setText("");

		PostMultipleParameters result = new PostMultipleParameters();

		try {
			String originalModelUrl = modelToParse;
			PostMultipleParameters postMultiple = new PostMultipleParameters();
			postMultiple.setOriginalModel(originalModelUrl);
			postMultiple.setParsedModel(parsedModel);
			postMultiple.setTaskName1(taskName1);
			postMultiple.setProperty("aTaskCompleteParameterized");
			result=GetReqEditor.PostReq_BProve_Maude_WebService_Property(postMultiple);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return result;
	}



// //aProcessEnd
	public static PostMultipleParameters aProcessEnd(final String modelToParse, final String parsedModel){
	// operation_done=true;
	// editorPane.setText("");

		PostMultipleParameters result = new PostMultipleParameters();

		try {
			String originalModelUrl = modelToParse;
			PostMultipleParameters postMultiple = new PostMultipleParameters();
			postMultiple.setOriginalModel(originalModelUrl);
			postMultiple.setParsedModel(parsedModel);
			postMultiple.setProperty("aBPoolends");
			result=GetReqEditor.PostReq_BProve_Maude_WebService_Property(postMultiple);

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return result;
	}

	public static PostMultipleParameters aProcessEndParam(final String modelToParse, final String parsedModel, String poolName){
		// operation_done=true;
		// editorPane.setText("");

			PostMultipleParameters result = new PostMultipleParameters();
	
			try {
				String originalModelUrl = modelToParse;
				PostMultipleParameters postMultiple = new PostMultipleParameters();
				postMultiple.setOriginalModel(originalModelUrl);
				postMultiple.setParsedModel(parsedModel);
				postMultiple.setProperty("aBPoolendsParameterized");
				postMultiple.setPoolName(poolName);
				
				result=GetReqEditor.PostReq_BProve_Maude_WebService_Property(postMultiple);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			return result;
		}

//allProcessesEnd
	public static PostMultipleParameters allProcessesEnd(final String modelToParse, final String parsedModel){
	// operation_done=true;
	// editorPane.setText("");

	PostMultipleParameters result = new PostMultipleParameters();
	try {
		//result = GetReqEditor.PostallBPoolend(modelToParse , parsedModel);
		String originalModelUrl = modelToParse;
		PostMultipleParameters postMultiple = new PostMultipleParameters();
		postMultiple.setOriginalModel(originalModelUrl);
		postMultiple.setParsedModel(parsedModel);
		postMultiple.setProperty("allBPoolend");
		result=GetReqEditor.PostReq_BProve_Maude_WebService_Property(postMultiple);
	} catch (Exception e1) {
		e1.printStackTrace();
	}
	return result;
}


// //noDeadActivities
	public static PostMultipleParameters noDeadActivities(final String modelToParse, final String parsedModel){
		// operation_done=true;
		// editorPane.setText("");
		PostMultipleParameters result = new PostMultipleParameters();
		try {
			//result = GetReqEditor.PostNoDeadActivities(modelToParse , parsedModel);
			String originalModelUrl = modelToParse;
			PostMultipleParameters postMultiple = new PostMultipleParameters();
			postMultiple.setOriginalModel(originalModelUrl);
			postMultiple.setParsedModel(parsedModel);
			postMultiple.setProperty("noDeadActivities");
			result=GetReqEditor.PostReq_BProve_Maude_WebService_Property(postMultiple);
		} catch (Exception e1) {
			e1.printStackTrace();
			result.setResult("\nERROR\n");
			// editorPane.setText(result.getResult());
			// editorPane.setForeground(Color.black);
		}
		return result;
	}
//

	//OptionToComplete
	public static PostMultipleParameters optionToComplete(final String modelToParse, final String parsedModel){
			// operation_done=true;
			// editorPane.setText("");
			PostMultipleParameters result = new PostMultipleParameters();

			try {
				//result = GetReqEditor.PostOptionToComplete(modelToParse , parsedModel);
				String originalModelUrl = modelToParse;
				PostMultipleParameters postMultiple = new PostMultipleParameters();
				postMultiple.setOriginalModel(originalModelUrl);
				postMultiple.setParsedModel(parsedModel);
				postMultiple.setProperty("optionToComplete");
				result=GetReqEditor.PostReq_BProve_Maude_WebService_Property(postMultiple);

			} catch (Exception e1) {
				e1.printStackTrace();
				result.setResult("\nERROR\n");
				// editorPane.setText(result.getResult());
				// editorPane.setForeground(Color.black);
			}
			return result;
		}

	//properCompletionCF
		public static PostMultipleParameters properCompletionCF(final String modelToParse, final String parsedModel){
			//operation_done=true;

			PostMultipleParameters result=null;

			try {
				String originalModelUrl = modelToParse;
				PostMultipleParameters postMultiple = new PostMultipleParameters();
				postMultiple.setOriginalModel(originalModelUrl);
				postMultiple.setParsedModel(parsedModel);
				postMultiple.setProperty("properCompletionCF");
				result=GetReqEditor.PostReq_BProve_Maude_WebService_Property(postMultiple);
			} catch (Exception e1) {
				e1.printStackTrace();
				result.setResult("\nERROR\n");
				// editorPane.setText(result.getResult());
				// editorPane.setForeground(Color.black);
			}
			return result;
		}


		//properCompletionMF
		public static PostMultipleParameters properCompletionMF(final String modelToParse, final String parsedModel){
			//operation_done=true;
			long time = 0;
			//editorPane.setText("");
			PostMultipleParameters result=null;

			try {
				//result = GetReqEditor.PostProperCompletion(modelToParse , parsedModel);
				String originalModelUrl = modelToParse;
				PostMultipleParameters postMultiple = new PostMultipleParameters();
				postMultiple.setOriginalModel(originalModelUrl);
				postMultiple.setParsedModel(parsedModel);
				postMultiple.setProperty("properCompletionMF");
				result=GetReqEditor.PostReq_BProve_Maude_WebService_Property(postMultiple);
			} catch (Exception e1) {
				e1.printStackTrace();
				result.setResult("\nERROR\n");
				// editorPane.setText(result.getResult());
				// editorPane.setForeground(Color.black);
			}
			return result;
		}

		//properCompletionCFMF
		public static PostMultipleParameters properCompletionCFMF(final String modelToParse, final String parsedModel){
			// operation_done=true;
			// editorPane.setText("");
			PostMultipleParameters result=null;

			try {
				//result = GetReqEditor.PostProperCompletion(modelToParse , parsedModel);
				String originalModelUrl = modelToParse;
				PostMultipleParameters postMultiple = new PostMultipleParameters();
				postMultiple.setOriginalModel(originalModelUrl);
				postMultiple.setParsedModel(parsedModel);
				postMultiple.setProperty("properCompletionCFMF");
				result=GetReqEditor.PostReq_BProve_Maude_WebService_Property(postMultiple);
			} catch (Exception e1) {
				e1.printStackTrace();
				result.setResult("\nERROR\n");
				// editorPane.setText(result.getResult());
				// editorPane.setForeground(Color.black);
			}
			return result;
		}
//

	//safeness
		public static PostMultipleParameters safeness(final String modelToParse, final String parsedModel){

			// operation_done=true;
			// editorPane.setText("");

			PostMultipleParameters result = new PostMultipleParameters();
			try {
				String originalModelUrl = modelToParse;
				PostMultipleParameters postMultiple = new PostMultipleParameters();
				postMultiple.setOriginalModel(originalModelUrl);
				postMultiple.setParsedModel(parsedModel);
				postMultiple.setProperty("safeness");
				result=GetReqEditor.PostReq_BProve_Maude_WebService_Property(postMultiple);
				//////System.out.println("\nresult: "+result.getResult());
			} catch (Exception e1) {
				e1.printStackTrace();
				result.setResult("\nERROR\n");
				// editorPane.setText(result.getResult());
				// editorPane.setForeground(Color.black);
			}
			return result;
		}

// //NEW PARAMETERIZED PROPERTIES
//noDeadActivitiesParam
		public static PostMultipleParameters  noDeadActivitiesParam(final String modelToParse, final String parsedModel, String poolName){
			// operation_done=true;
			// editorPane.setText("");
			PostMultipleParameters result = new PostMultipleParameters();
			try {
				//result = GetReqEditor.PostNoDeadActivities(modelToParse , parsedModel);
				String originalModelUrl = modelToParse;
				PostMultipleParameters postMultiple = new PostMultipleParameters();
				postMultiple.setOriginalModel(originalModelUrl);
				postMultiple.setParsedModel(parsedModel);
				postMultiple.setPoolName(poolName);
				postMultiple.setProperty("noDeadActivitiesParam");
				result=GetReqEditor.PostReq_BProve_Maude_WebService_Property(postMultiple);
			} catch (Exception e1) {
				e1.printStackTrace();
				result.setResult("\nERROR\n");
				// editorPane.setText(result.getResult());
				// editorPane.setForeground(Color.black);
			}
			return result;
		}
//

//OptionToCompleteParam
public static PostMultipleParameters  optionToCompleteParam(final String modelToParse, final String parsedModel, String poolName){
		// operation_done=true;
		// editorPane.setText("");
		PostMultipleParameters result = new PostMultipleParameters();

		try {
			//result = GetReqEditor.PostOptionToComplete(modelToParse , parsedModel);
			String originalModelUrl = modelToParse;
			PostMultipleParameters postMultiple = new PostMultipleParameters();
			postMultiple.setOriginalModel(originalModelUrl);
			postMultiple.setParsedModel(parsedModel);
			postMultiple.setPoolName(poolName);
			postMultiple.setProperty("optionToCompleteParam");
			result=GetReqEditor.PostReq_BProve_Maude_WebService_Property(postMultiple);

		} catch (Exception e1) {
			e1.printStackTrace();
			result.setResult("\nERROR\n");
			// editorPane.setText(result.getResult());
			// editorPane.setForeground(Color.black);
		}
		return result;
	}

//properCompletionCFParam
	public static PostMultipleParameters  properCompletionCFParam(final String modelToParse, final String parsedModel, String poolName){
		// operation_done=true;
		// editorPane.setText("");
		PostMultipleParameters result=null;

		try {
			String originalModelUrl = modelToParse;
			PostMultipleParameters postMultiple = new PostMultipleParameters();
			postMultiple.setOriginalModel(originalModelUrl);
			postMultiple.setParsedModel(parsedModel);
			postMultiple.setPoolName(poolName);
			postMultiple.setProperty("properCompletionCFParam");
			result=GetReqEditor.PostReq_BProve_Maude_WebService_Property(postMultiple);
		} catch (Exception e1) {
			e1.printStackTrace();
			result.setResult("\nERROR\n");
			// editorPane.setText(result.getResult());
			// editorPane.setForeground(Color.black);
		}
		return result;
	}

//properCompletionMFParam
	public static PostMultipleParameters  properCompletionMFParam(final String modelToParse, final String parsedModel, String poolName){
		//operation_done=true;
		//editorPane.setText("");
		PostMultipleParameters result=null;

		try {
			//result = GetReqEditor.PostProperCompletion(modelToParse , parsedModel);
			String originalModelUrl = modelToParse;
			PostMultipleParameters postMultiple = new PostMultipleParameters();
			postMultiple.setOriginalModel(originalModelUrl);
			postMultiple.setParsedModel(parsedModel);
			postMultiple.setPoolName(poolName);
			postMultiple.setProperty("properCompletionMFParam");
			result=GetReqEditor.PostReq_BProve_Maude_WebService_Property(postMultiple);
		} catch (Exception e1) {
			e1.printStackTrace();
			result.setResult("\nERROR\n");
			// editorPane.setText(result.getResult());
			// editorPane.setForeground(Color.black);
		}
		return result;
	}
//

//properCompletionCFMFParam
	public static PostMultipleParameters  properCompletionCFMFParam(final String modelToParse, final String parsedModel, String poolName){
		//operation_done=true;
		//editorPane.setText("");
		PostMultipleParameters result=null;

		try {
			//result = GetReqEditor.PostProperCompletion(modelToParse , parsedModel);
			String originalModelUrl = modelToParse;
			PostMultipleParameters postMultiple = new PostMultipleParameters();
			postMultiple.setOriginalModel(originalModelUrl);
			postMultiple.setParsedModel(parsedModel);
			postMultiple.setPoolName(poolName);
			postMultiple.setProperty("properCompletionCFMFParam");
			result=GetReqEditor.PostReq_BProve_Maude_WebService_Property(postMultiple);
		} catch (Exception e1) {
			e1.printStackTrace();
			result.setResult("\nERROR\n");
			// editorPane.setText(result.getResult());
			// editorPane.setForeground(Color.black);
		}
		return result;
	}

//safenessParam
	public static PostMultipleParameters  safenessParam(final String modelToParse, final String parsedModel, String poolName){
		// operation_done=true;
		// editorPane.setText("");

		PostMultipleParameters result = new PostMultipleParameters();
		try {
			String originalModelUrl = modelToParse;
			PostMultipleParameters postMultiple = new PostMultipleParameters();
			postMultiple.setOriginalModel(originalModelUrl);
			postMultiple.setParsedModel(parsedModel);
			postMultiple.setPoolName(poolName);
			postMultiple.setProperty("safenessParam");
			result=GetReqEditor.PostReq_BProve_Maude_WebService_Property(postMultiple);
			//////System.out.println("\nresult: "+result.getResult());
		} catch (Exception e1) {
			e1.printStackTrace();
			result.setResult("\nERROR\n");
			// editorPane.setText(result.getResult());
			// editorPane.setForeground(Color.black);
		}
		return result;
	}

//	obtainTaskList
	public static void obtainPoolAndTaskList(String parsedModel, ArrayList<String> poolListNames, ArrayList<String> tasksNames){

		//////System.out.println("\nparsedModel: "+parsedModel);
		//////System.out.println("\nIn obtainPoolAndTaskList");
		Collaboration C = new Collaboration();
		ArrayList<String> poolList = C.SplitPool(parsedModel);
		ArrayList<String> procElements = new ArrayList<String>();
		//////System.out.println("\npoolList.size(): "+poolList.size());
		for(int i = 0; i<poolList.size(); i++)
		{
			//////System.out.println("\nPoolN: "+i);
			String poolName;
			try{

			    //Pool pool1 = new Pool(poolList.get(i));

			    Pool pool1 = new Pool();

			    poolName=pool1.extractName(poolList.get(i));
			    poolListNames.add(poolName);
			    //DEVO ESTRARRE TUTTI I TASK, QUELLI SEMPLICI, I SEND, E I RECEIVE ...
			    // POI NE METTO IN UNA LISTA DI STRING I NOMI E POI POSSO PROCEDERE CON LA STESURA
			    //DEL MAUDE COMMAND
	//TasksNames
			    //////System.out.println("\npoolName: "+poolName);
			    //poolName=pool1.getOrganizationName();
			    //////System.out.println("\n\nPool getOrganizationName: "+poolName);
			    Proc ProcessElements = new Proc();
			    procElements=ProcessElements.extractProcElement(poolList.get(i));
			    //////System.out.println("\n\nEstraggo i processElements ");
			    //////System.out.println("\nBefore  ProcessElements.analizeProc(procElements)");
			    ProcessElements.analizeProc(procElements);
			    //////System.out.println("\nAfter  ProcessElements.analizeProc(procElements)");
			    //////System.out.println("\n\nGenero gli oggetti corrispondenti agli elementi ");
			    ArrayList<Task> tasks=ProcessElements.getTasks();//);
			    //////System.out.println("\n\nEstraggo i Task ");
			    for(Iterator<Task> iTask = tasks.iterator(); iTask.hasNext(); ) {
			    	tasksNames.add(iTask.next().name);
			    }

	//SndTasksNames
			    ArrayList<SendTask> sndTasks=ProcessElements.getSendTask();//);
			    //poolListNames.add(poolName);
			    for(Iterator<SendTask> iTask = sndTasks.iterator(); iTask.hasNext(); ) {
			    	tasksNames.add(iTask.next().name);
			    }

	//ReceiveTasksNames
			    ArrayList<ReceiveTask> rcvTasks=ProcessElements.getReceiveTask();//);
			    //poolListNames.add(poolName);
			    for(Iterator<ReceiveTask> iTask = rcvTasks.iterator(); iTask.hasNext(); ) {
			    	tasksNames.add(iTask.next().name);
			    }

	//Print TaskName List
			    //////System.out.println("\n Stampo Nomi dei Task\n");
			    //for(Iterator<String> iTask = tasksNames.iterator(); iTask.hasNext(); ) {
			    //	////System.out.println("\n TaskName: "+ iTask.next());
			    //}

			}catch(Exception e){
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				sw.toString();
				//////System.out.println("\n Exception: "+ sw);
			}

		}


	}

	public static void obtainPoolAndTaskListAndMsgList(String parsedModel, ArrayList<String> poolListNames, ArrayList<String> tasksNames, ArrayList<String> inputMsgString, ArrayList<String> outputMsgString){
		
		//////System.out.println("\nparsedModel: "+parsedModel);	
		//////System.out.println("\nIn obtainPoolAndTaskList");
		Collaboration C = new Collaboration();
		ArrayList<String> poolList = C.SplitPool(parsedModel);
		ArrayList<String> procElements = new ArrayList<String>();	
		ArrayList<Msg> inputMsg = new ArrayList<Msg>();
		ArrayList<Msg> outputMsg = new ArrayList<Msg>();
		//////System.out.println("\npoolList.size(): "+poolList.size());
		for(int i = 0; i<poolList.size(); i++)
		{			
			//////System.out.println("\nPoolN: "+i);
			String poolName;
			try{
				
			    //Pool pool1 = new Pool(poolList.get(i));
			    
			    Pool pool1 = new Pool();
			    
			    poolName=pool1.extractName(poolList.get(i));
			    poolListNames.add(poolName);
				
				//////JOptionPane.showMessageDialog(null, "\nEstraggo i messaggi\n");

			    //Estraggo i messaggi
				inputMsg=pool1.extractInputMsg(poolList.get(i));
				//////JOptionPane.showMessageDialog(null, "\nEstraggo i messaggi: \n"+inputMsg.get(0).getMsgName());
				outputMsg=pool1.extractOutputMsg(poolList.get(i));
				//////JOptionPane.showMessageDialog(null, "\nEstraggo i messaggi: \n"+outputMsg.get(0).getMsgName());
			    for(int i2=0; i2<inputMsg.size();i2++){
					inputMsgString.add(inputMsg.get(i2).getMsgName());
					//////JOptionPane.showMessageDialog(null, "\nEstraggo i messaggi: \n"+inputMsg.get(i2).getMsgName());
			    	////System.out.println("\ninputMsg.get(i).getMsgName()\n: index: "+i2+"msg: "+inputMsg.get(i2).getMsgName());
			    }
			    
			    for(int i3=0; i3<outputMsg.size();i3++){
					outputMsgString.add(outputMsg.get(i3).getMsgName());
					//////JOptionPane.showMessageDialog(null, "\nEstraggo i messaggi: \n"+outputMsg.get(i3).getMsgName());
			    	////System.out.println("\noutputMsg.get(i).getMsgName()\n: index: "+i3+"msg: "+outputMsg.get(i3).getMsgName());
			    }
			    
			    
			    
			    //DEVO ESTRARRE TUTTI I TASK, QUELLI SEMPLICI, I SEND, E I RECEIVE ... 
			    // POI NE METTO IN UNA LISTA DI STRING I NOMI E POI POSSO PROCEDERE CON LA STESURA
			    //DEL MAUDE COMMAND
	//TasksNames
			    //////System.out.println("\npoolName: "+poolName);
			    //poolName=pool1.getOrganizationName();
			    //////System.out.println("\n\nPool getOrganizationName: "+poolName);
			    Proc ProcessElements = new Proc();
			    procElements=ProcessElements.extractProcElement(poolList.get(i));
			    //////System.out.println("\n\nEstraggo i processElements ");
			    //////System.out.println("\nBefore  ProcessElements.analizeProc(procElements)");
			    ProcessElements.analizeProc(procElements);
			    //////System.out.println("\nAfter  ProcessElements.analizeProc(procElements)");
			    //////System.out.println("\n\nGenero gli oggetti corrispondenti agli elementi ");
			    ArrayList<Task> tasks=ProcessElements.getTasks();//);
			    //////System.out.println("\n\nEstraggo i Task ");
			    for(Iterator<Task> iTask = tasks.iterator(); iTask.hasNext(); ) {
			    	tasksNames.add(iTask.next().name);
			    }

	//SndTasksNames
			    ArrayList<SendTask> sndTasks=ProcessElements.getSendTask();//);
			    //poolListNames.add(poolName);
			    for(Iterator<SendTask> iTask = sndTasks.iterator(); iTask.hasNext(); ) {
			    	tasksNames.add(iTask.next().name);
			    }
			    
	//ReceiveTasksNames
			    ArrayList<ReceiveTask> rcvTasks=ProcessElements.getReceiveTask();//);
			    //poolListNames.add(poolName);
			    for(Iterator<ReceiveTask> iTask = rcvTasks.iterator(); iTask.hasNext(); ) {
			    	tasksNames.add(iTask.next().name);
			    }

	//Print TaskName List
			    //////System.out.println("\n Stampo Nomi dei Task\n");
			    //for(Iterator<String> iTask = tasksNames.iterator(); iTask.hasNext(); ) {
			    //	////System.out.println("\n TaskName: "+ iTask.next());
			    //}
			    
			}catch(Exception e){
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				sw.toString();
				//////System.out.println("\n Exception: "+ sw);				
			}
			
		}	

	}

	private static String trace(String violatingTrace0) {
			String idws="";
			try{
				String violatingTrace = violatingTrace0;
				//JOptionPane.showMessageDialog(null,"guazza" + violatingTrace);
        		String id="array";

				String violatingTraceString = new String(violatingTrace);
				StringTokenizer st = new StringTokenizer(violatingTraceString, "\n");
				String bloc[]=new String[st.countTokens()];
				int i=0;
				while (st.hasMoreElements()) {
					//////JOptionPane.showMessageDialog(null, st.nextElement());
					bloc[i]=(String) st.nextElement();
					i++;
				}

				for(int j=0; j<bloc.length;j++)
				{
					if(bloc[j].contains("Name")){
						String str1 = bloc[j];
						StringTokenizer st1 = new StringTokenizer(str1, ":");
						String arring[]=new String[st1.countTokens()];
						int i1=0;
						while (st1.hasMoreElements()) {
							//////JOptionPane.showMessageDialog(null, st.nextElement());
							arring[i1]=(String) st1.nextElement();
							i1++;
						}
						String passage="nothing";
						if(arring[1].contains("sid-"))
						{
							passage=arring[1];
							if(!id.contains(passage))
							{id=(id+","+passage); }
						}
					}
				}
				//////JOptionPane.showMessageDialog(null,id);
				idws= id.replace(" ","");
				//////JOptionPane.showMessageDialog(null,idws);
				//settraceResult(idws);
				//////JOptionPane.showMessageDialog(null,"traceResult\n"+idws);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return idws;
    }

	private static Proc counterExampleReturnTest(byte[] violatingTraceByte){
		ByteArrayInputStream bis = new ByteArrayInputStream(violatingTraceByte);
		ObjectInput in = null;
		Proc finalprocess=null;
		try {
			
		
			in = new ObjectInputStream(bis);
			Object o = in.readObject(); 
			finalprocess = (Proc)o;
		
			in.close();
		} catch (Exception e) {
			//TODO: handle exception
			//JOptionPane.showMessageDialog(null,e.getMessage());
		}	
		
		return finalprocess;

}

private static String  realViolatingTrace(Proc finalProcess){
	String realViolatingTrace="";

	String sourceIDs="";
	String targetIDs="";

	try {
		
	
		if(finalProcess.Start != null)
		{
			for(int i = 0; i<finalProcess.Start.size(); i++)
			{
				targetIDs+=finalProcess.Start.get(i).Edge.EdgeName+",";
				////JOptionPane.showMessageDialog(null, "targetIDs"+targetIDs);
			}			
		}
		if(finalProcess.StartRcvMsg != null)
		{
			for(int i = 0; i<finalProcess.StartRcvMsg.size(); i++)
			{
				targetIDs+=finalProcess.StartRcvMsg.get(i).Edge.EdgeName+",";
			}			
		}
		if(finalProcess.AndJoin != null)
		{
			for(int i = 0; i<finalProcess.AndJoin.size(); i++)
			{
				targetIDs+=finalProcess.AndJoin.get(i).Edge.EdgeName+",";
			}			
		}		
		if(finalProcess.AndSplit != null)
		{
			for(int i = 0; i<finalProcess.AndSplit.size(); i++)
			{
				sourceIDs+=finalProcess.AndSplit.get(i).Edge.EdgeName+",";
			}			
		}
		if(finalProcess.EndSndMsg != null)
		{
			for(int i = 0; i<finalProcess.EndSndMsg.size(); i++)
			{
				sourceIDs+=finalProcess.EndSndMsg.get(i).Edge.EdgeName+",";
			}			
		}
		if(finalProcess.EventBasedgat != null)
		{
			for(int i = 0; i<finalProcess.EventBasedgat.size(); i++)
			{
				sourceIDs+=finalProcess.EventBasedgat.get(i).Edge.EdgeName+",";
			}			
		}
		if(finalProcess.InterRcv != null)
		{
			for(int i = 0; i<finalProcess.InterRcv.size(); i++)
			{
				targetIDs+=finalProcess.InterRcv.get(i).OutputEdge.EdgeName+",";
			}			
		}
		if(finalProcess.InterSnd != null)
		{
			for(int i = 0; i<finalProcess.InterSnd.size(); i++)
			{
				targetIDs+=finalProcess.InterSnd.get(i).OutputEdge.EdgeName+",";
			}			
		}
		if(finalProcess.OrSplit != null)
		{
			for(int i = 0; i<finalProcess.OrSplit.size(); i++)
			{
				sourceIDs+=finalProcess.OrSplit.get(i).Edge.EdgeName+",";
			}			
		}
		if(finalProcess.ReceiveTask != null)
		{
			for(int i = 0; i<finalProcess.ReceiveTask.size(); i++)
			{
				sourceIDs+=finalProcess.ReceiveTask.get(i).InputEdge.EdgeName+",";
			}			
		}
		if(finalProcess.SendTask != null)
		{
			for(int i = 0; i<finalProcess.SendTask.size(); i++)
			{
				sourceIDs+=finalProcess.SendTask.get(i).InputEdge.EdgeName+",";
			}			
		}
				
		if(finalProcess.Task != null)
		{
			for(int i = 0; i<finalProcess.Task.size(); i++)
			{
				sourceIDs+=finalProcess.Task.get(i).InputEdge.EdgeName+",";
			}			
		}
		if(finalProcess.Terminate != null)
		{
			for(int i = 0; i<finalProcess.Terminate.size(); i++)
			{
				sourceIDs+=finalProcess.Terminate.get(i).Edge.EdgeName+",";
			}			
		}
		if(finalProcess.XorJoin != null)
		{
			for(int i = 0; i<finalProcess.XorJoin.size(); i++)
			{
				targetIDs+=finalProcess.XorJoin.get(i).Edge.EdgeName+",";
			}			
		}
		if(finalProcess.XorSplit != null)
		{
			for(int i = 0; i<finalProcess.XorSplit.size(); i++)
			{
				sourceIDs+=finalProcess.XorSplit.get(i).Edge.EdgeName+",";
			}			
		}
		if(finalProcess.End != null)
		{
			for(int i = 0; i<finalProcess.End.size(); i++)
			{
				sourceIDs+=finalProcess.End.get(i).Edge.EdgeName+",";
			}			
		}
	} catch (Exception e) {
		//TODO: handle exception
	}
	sourceIDs= sourceIDs.substring(0, sourceIDs.length() - 1);
	targetIDs= targetIDs.substring(0, targetIDs.length() - 1);
		realViolatingTrace="££sourceIDs££"+sourceIDs+"££targetIDs££"+targetIDs;
	return realViolatingTrace;

}


	private static String counterExampleReturn(byte[] violatingTraceByte){
			Proc p = null;
			String conversion="";
			try{
					ByteArrayInputStream in = new ByteArrayInputStream(violatingTraceByte);
					ObjectInputStream ois = new ObjectInputStream(in);
					p = ( Proc ) ois.readObject();
			}catch(Exception e){//////JOptionPane.showMessageDialog(null, "\nerror return\n"+e);
					}
			try{
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					PrintStream ps = new PrintStream(baos);
					PrintStream old = System.out;
					System.setOut(ps);
					p.printProcess();
					//System.out.flush();
					System.setOut(old);
					conversion = baos.toString();
					//////JOptionPane.showMessageDialog(null, "result of string conversion \n"+conversion);

			}catch(Exception e){//////JOptionPane.showMessageDialog(null, "\nerror conversion\n"+e);
					}
			return conversion;
	}

}
