package plugin.bpmn.to.maude.handlers;

@SuppressWarnings("serial")
public class PostMultipleParameters implements java.io.Serializable {
	
	//{originalModel}/{parsedModel}/{property}/{poolName}/{taskName1}/{taskName2}/{sndMsgName}/{rcvMsgName}
	
	String originalModel;
	String parsedModel;
	
	//For element counting
	int numberOfElement;
	int numberOfPool;
	int numberOfStart;
	int numberOfStartMsg;
	int numberOfEnd;
	int numberOfEndMsg;
	int numberOfTerminate;
	int numberOfTask;
	int numberOfTaskSnd;
	int numberOfTaskRcv;
	int numberOfIntermidiateThrowEvent;
	int numberOfIntermidiateCatchEvent;
	int numberOfGatewayXorSplit;
	int numberOfGatewayXorJoin;
	int numberOfGatewayAndSplit;
	int numberOfGatewayAndJoin;
	int numberOfGatewayOrSplit;
	int numberOfGatewayEventBased;

	//For property specification
	String property;
	String poolName;
	String taskName1;
	String taskName2;
	String sndMsgName;
	String rcvMsgName;
	
	//For recording the required Time
	String propertyVerificationTime;
	String parsingTime;
	
	//For result management
	String counterexample;
	byte[] violatingTrace;

	String result;
	String state;
//	String[] states;
	
	String command;
	
	public PostMultipleParameters(){
		this.originalModel=null;
		this.parsedModel=null;
		this.property=null;
		this.poolName=null;
		this.taskName1=null;
		this.taskName2=null;
		this.sndMsgName=null;
		this.rcvMsgName=null;
		this.numberOfElement=0;
		this.numberOfPool=0;
		this.numberOfStart=0;
		this.numberOfStartMsg=0;
		this.numberOfEnd=0;
		this.numberOfEndMsg=0;
		this.numberOfTerminate=0;
		this.numberOfTask=0;
		this.numberOfTaskSnd=0;
		this.numberOfTaskRcv=0;
		this.numberOfIntermidiateThrowEvent=0;
		this.numberOfIntermidiateCatchEvent=0;
		this.numberOfGatewayXorSplit=0;
		this.numberOfGatewayXorJoin=0;
		this.numberOfGatewayAndSplit=0;
		this.numberOfGatewayAndJoin=0;
		this.numberOfGatewayOrSplit=0;
		this.numberOfGatewayEventBased=0;
		this.propertyVerificationTime=null;
		this.parsingTime=null;
		this.counterexample=null;
		this.violatingTrace=null;
		this.result=null;
		this.state=null;
//		this.states=null;
		this.command=null;
	}
	
	public PostMultipleParameters(
			String originalModel, 
			String parsedModel, 
			String property, 
			String poolName, 
			String taskName1, 
			String taskName2, 
			String sndMsgName, 
			String rcvMsgName, 	
			int numberOfElement,
			int numberOfPool,
			int numberOfStart,
			int numberOfStartMsg,
			int numberOfEnd,
			int numberOfEndMsg,
			int numberOfTerminate,
			int numberOfTask,
			int numberOfTaskSnd,
			int numberOfTaskRcv,
			int numberOfIntermidiateThrowEvent,
			int numberOfIntermidiateCatchEvent,
			int numberOfGatewayXorSplit,
			int numberOfGatewayXorJoin,
			int numberOfGatewayAndSplit,
			int numberOfGatewayAndJoin,
			int numberOfGatewayOrSplit,
			int numberOfGatewayEventBased, 
			String propertyVerificationTime,
			String parsingTime,
			String counterexample,
			byte[] violatingTrace,
			String result,
			String state,
//			String[] states
			String command
			
	){
		this.originalModel=originalModel;
		this.parsedModel=parsedModel;
		this.property=property;
		this.poolName=poolName;
		this.taskName1=taskName1;
		this.taskName2=taskName2;
		this.sndMsgName=sndMsgName;
		this.rcvMsgName=rcvMsgName;
		this.numberOfElement=numberOfElement;
		this.numberOfPool=numberOfPool;
		this.numberOfStart=numberOfStart;
		this.numberOfStartMsg=numberOfStartMsg;
		this.numberOfEnd=numberOfEnd;
		this.numberOfEndMsg=numberOfEndMsg;
		this.numberOfTerminate=numberOfTerminate;
		this.numberOfTask=numberOfTask;
		this.numberOfTaskSnd=numberOfTaskSnd;
		this.numberOfTaskRcv=numberOfTaskRcv;
		this.numberOfIntermidiateThrowEvent=numberOfIntermidiateThrowEvent;
		this.numberOfIntermidiateCatchEvent=numberOfIntermidiateCatchEvent;
		this.numberOfGatewayXorSplit=numberOfGatewayXorSplit;
		this.numberOfGatewayXorJoin=numberOfGatewayXorJoin;
		this.numberOfGatewayAndSplit=numberOfGatewayAndSplit;
		this.numberOfGatewayAndJoin=numberOfGatewayAndJoin;
		this.numberOfGatewayOrSplit=numberOfGatewayOrSplit;
		this.numberOfGatewayEventBased=numberOfGatewayEventBased;
		this.propertyVerificationTime=propertyVerificationTime;
		this.parsingTime=parsingTime;
		this.counterexample=counterexample;
		this.result=result;
		this.violatingTrace=violatingTrace;
		this.state=state;
//		this.states=states;
		this.command=command;
	}
	
	public void printPostMultipleParameters(){
		System.out.println("\n originalModel: "+originalModel);
		System.out.println("\n parsedModel: "+parsedModel);
		System.out.println("\n property: "+property);
		System.out.println("\n poolName: "+poolName);
		System.out.println("\n taskName1: "+taskName1);
		System.out.println("\n taskName2: "+taskName2);
		System.out.println("\n sndMsgName: "+sndMsgName);
		System.out.println("\n rcvMsgName: "+rcvMsgName);
		System.out.println("\n numberOfElement: "+numberOfElement);
		System.out.println("\n numberOfPool: "+numberOfPool);
		System.out.println("\n numberOfStart: "+numberOfStart);
		System.out.println("\n numberOfStart: "+numberOfStart);
		System.out.println("\n numberOfStartMsg: "+numberOfStartMsg);
		System.out.println("\n numberOfEnd: "+numberOfEnd);
		System.out.println("\n numberOfEndMsg: "+numberOfEndMsg);
		System.out.println("\n numberOfTerminate: "+numberOfTerminate);
		System.out.println("\n numberOfTask: "+numberOfTask);
		System.out.println("\n numberOfTaskSnd: "+numberOfTaskSnd);
		System.out.println("\n numberOfTaskRcv: "+numberOfTaskRcv);
		System.out.println("\n numberOfIntermidiateThrowEvent: "+numberOfIntermidiateThrowEvent);
		System.out.println("\n numberOfIntermidiateCatchEvent: "+numberOfIntermidiateCatchEvent);
		System.out.println("\n numberOfGatewayXorSplit: "+numberOfGatewayXorSplit);
		System.out.println("\n numberOfGatewayXorJoin: "+numberOfGatewayXorJoin);
		System.out.println("\n numberOfGatewayAndSplit: "+numberOfGatewayAndSplit);
		System.out.println("\n numberOfGatewayAndJoin: "+numberOfGatewayAndJoin);
		System.out.println("\n numberOfGatewayOrSplit: "+numberOfGatewayOrSplit);
		System.out.println("\n numberOfGatewayEventBased: "+numberOfGatewayEventBased);
		System.out.println("\n propertyVerificationTime: "+propertyVerificationTime);
		System.out.println("\n parsingTime: "+parsingTime);
		System.out.println("\n counterexample: "+counterexample);
		System.out.println("\n violatingTrace: "+violatingTrace);
		System.out.println("\n result: "+result);
		System.out.println("\n state: "+state);
//		System.out.println("\n states: "+states.toString());
		System.out.println("\n command: "+command);

	}
	
	public String toStringPostMultipleParameters(){
		return this.originalModel + this.parsedModel + this.property + this.poolName + this.taskName1 + this.taskName2 + this.sndMsgName + this.rcvMsgName +this.propertyVerificationTime + this.parsingTime + this.counterexample + this.violatingTrace + this.result + this.state + this.command;
	}
	
	//Get
	public String getOriginalModel(){
		return this.originalModel;
	}
	public String getParsedModel(){
		return this.parsedModel;
	}
	public String getProperty(){
		return this.property;
	}
	public String getPoolName(){
		return this.poolName;
	}
	public String getTaskName1(){
		return this.taskName1;
	}
	public String getTaskName2(){
		return this.taskName2;
	}
	public String getSndMsgName(){
		return this.sndMsgName;
	}
	public String getRcvMsgName(){
		return this.rcvMsgName;
	}
	public int getNumberOfElement(){
		return this.numberOfElement;
	}
	public int getNumberOfPool(){
		return this.numberOfPool;
	}
	public int getNumberOfStart(){
		return this.numberOfStart;
	}
	public int getNumberOfStartMsg(){
		return this.numberOfStartMsg;
	}
	public int getNumberOfEnd(){
		return this.numberOfEnd;
	}
	public int getNumberOfEndMsg(){
		return this.numberOfEndMsg;
	}
	public int getNumberOfTerminate(){
		return this.numberOfTerminate;
	}
	public int getNumberOfTask(){
		return this.numberOfTask;
	}
	public int getNumberOfTaskSnd(){
		return this.numberOfTaskSnd;
	}
	public int getNumberOfTaskRcv(){
		return this.numberOfTaskRcv;
	}
	public int getNumberOfIntermidiateThrowEvent(){
		return this.numberOfIntermidiateThrowEvent;
	}
	public int getNumberOfIntermidiateCatchEvent(){
		return this.numberOfIntermidiateCatchEvent;
	}
	public int getNumberOfGatewayXorSplit(){
		return this.numberOfGatewayXorSplit;
	}
	public int getNumberOfGatewayXorJoin(){
		return this.numberOfGatewayXorJoin;
	}
	public int getNumberOfGatewayAndSplit(){
		return this.numberOfGatewayAndSplit;
	}
	public int getNumberOfGatewayAndJoin(){
		return this.numberOfGatewayAndJoin;
	}
	public int getNumberOfGatewayOrSplit(){
		return this.numberOfGatewayOrSplit;
	}
	public int getNumberOfGatewayEventBased(){
		return this.numberOfGatewayEventBased;
	}
	
	public String getPropertyVerificationTime(){
		return this.propertyVerificationTime;
	}
	
	public String getParsingTime(){
		return this.parsingTime;
	}
	
	public String getCounterexample() {
		return counterexample;
	}
	
	public byte[] getViolatingTrace() {
		return violatingTrace;
	}

	public String getResult() {
		return result;
	}
	
	public String getState() {
		return state;
	}
	
//	public String[] getStates() {
//		return states;
//	}
	
	public String getCommand() {
		return command;
	}
	
	
	//Set
	public void setOriginalModel( String originalModel){
		this.originalModel=originalModel;
		return;
	}
	public void setParsedModel( String parsedModel){
		this.parsedModel=parsedModel;
		return;
	}
	public void setProperty(String property){
		this.property=property;
		return;
	}
	public void setPoolName(String poolName){
		this.poolName=poolName;
		return;
	}
	public void setTaskName1(String taskName1){
		this.taskName1=taskName1;
		return;
	}
	public void setTaskName2(String taskName2){
		this.taskName2=taskName2;
		return;
	}
	public void setSndMsgName(String sndMsgName){
		this.sndMsgName=sndMsgName;
		return;
	}
	public void setRcvMsgName(String rcvMsgName){
		this.rcvMsgName=rcvMsgName;
		return;
	}
	public void setNumberOfElement(int numberOfElement){
		this.numberOfElement=numberOfElement;
		return;
	}
	public void setNumberOfPool(int numberOfPool){
		this.numberOfPool=numberOfPool;
		return;
	}
	public void setNumberOfStart(int numberOfStart){
		this.numberOfStart=numberOfStart;
		return;
	}
	public void setNumberOfStartMsg(int numberOfStartMsg){
		this.numberOfStartMsg=numberOfStartMsg;
		return;
	}
	public void setNumberOfEnd(int numberOfEnd){
		this.numberOfEnd=numberOfEnd;
		return;
	}
	public void setNumberOfEndMsg(int numberOfEndMsg){
		this.numberOfEndMsg=numberOfEndMsg;
		return;
	}
	public void setNumberOfTerminate(int numberOfTerminate){
		this.numberOfTerminate=numberOfTerminate;
		return;
	}
	public void setNumberOfTask(int numberOfTask){
		this.numberOfTask=numberOfTask;
		return;
	}
	public void setNumberOfTaskSnd(int numberOfTaskSnd){
		this.numberOfTaskSnd=numberOfTaskSnd;
		return;
	}
	public void setNumberOfTaskRcv(int numberOfTaskRcv){
		this.numberOfTaskRcv=numberOfTaskRcv;
		return;
	}
	public void setNumberOfIntermidiateThrowEvent(int numberOfIntermidiateThrowEvent){
		this.numberOfIntermidiateThrowEvent=numberOfIntermidiateThrowEvent;
		return;
	}
	public void setNumberOfIntermidiateCatchEvent(int numberOfIntermidiateCatchEvent){
		this.numberOfIntermidiateCatchEvent=numberOfIntermidiateCatchEvent;
		return;
	}
	public void setNumberOfGatewayXorSplit(int numberOfGatewayXorSplit){
		this.numberOfGatewayXorSplit=numberOfGatewayXorSplit;
		return;
	}
	public void setNumberOfGatewayXorJoin(int numberOfGatewayXorJoin){
		this.numberOfGatewayXorJoin=numberOfGatewayXorJoin;
		return;
	}
	public void setNumberOfGatewayAndSplit(int numberOfGatewayAndSplit){
		this.numberOfGatewayAndSplit=numberOfGatewayAndSplit;
		return;
	}
	public void setNumberOfGatewayAndJoin(int numberOfGatewayAndJoin){
		this.numberOfGatewayAndJoin=numberOfGatewayAndJoin;
		return;
	}
	public void setNumberOfGatewayOrSplit(int numberOfGatewayOrSplit){
		this.numberOfGatewayOrSplit=numberOfGatewayOrSplit;
		return;
	}
	public void setNumberOfGatewayEventBased(int numberOfGatewayEventBased){
		this.numberOfGatewayEventBased=numberOfGatewayEventBased;
		return;
	}
	
	public void setPropertyVerificationTime(String propertyVerificationTime){
		this.propertyVerificationTime=propertyVerificationTime;
		return; 
	}
	
	public void setParsingTime(String parsingTime){
		this.parsingTime=parsingTime;
		return;
	}
	
	public void setCounterexample(String counterexample) {
		this.counterexample = counterexample;
	}

	public void setViolatingTrace(byte[] violatingTrace) {
		this.violatingTrace = violatingTrace;
	}
	
	public void setResult(String result) {
		this.result = result;
	}
	
	public void setState(String state) {
		this.state = state;
	}
	
	public void setCommand(String command) {
		this.command = command;
	}
		
	
//	public void setStates(String[] states) {
//		this.states = states;
//	}

}

