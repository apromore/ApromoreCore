package plugin.bpmn.to.maude.notation;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class InterRcv extends MsgEvent  implements java.io.Serializable{
	
	//Status Edge Edge Msg
	String Status;
	Edge InputEdge;
	public Edge OutputEdge;
	Msg msg;
		
	public InterRcv()
	{
		
	}
	
	public InterRcv(String status, Edge input, Edge output, Msg msg)
	{
		this.Status = status;
		this.InputEdge = input;
		this.OutputEdge = output;
		this.msg = msg;
	}
	
	
	public InterRcv(String interrcv) {
		
		ArrayList<String> isnd = new ArrayList<String>();
		isnd = TokenMsgEvent(interrcv);
		this.Status = isnd.get(0);
		this.InputEdge = new Edge(isnd.get(1));
		this.OutputEdge = new Edge(isnd.get(2));
		this.msg = new Msg(isnd.get(3));
	}
	
	public boolean compareInterRcv(InterRcv interrcv1, InterRcv interrcv2)
	{
		if(!interrcv1.Status.equals(interrcv2.Status) ||
				!interrcv1.OutputEdge.EdgeToken.equals(interrcv2.OutputEdge.EdgeToken)  )
		{
			return true;
		}else
		{
			return false;
		}
	}
	
	/*public boolean compareMsgCatchEventName (InterRcv msgcatchevent1, InterRcv msgcatchevent2)
	{
		if(msgcatchevent1.name.equals(msgcatchevent2.name))
		{
			return true;
		}else
		{
			return false;
		}
	}*/
	
	public void printInterRcv()
	{
		//Status Edge Edge Msg
		//System.out.println("\nInterRcv: ");
		//System.out.println("Status: "+this.Status);
		if(this.InputEdge != null)
		{
			this.InputEdge.printEdge();
		}
		this.OutputEdge.printEdge();
		this.msg.printMsg();		
	}
	

}
