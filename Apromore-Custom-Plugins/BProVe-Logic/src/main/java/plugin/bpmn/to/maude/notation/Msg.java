package plugin.bpmn.to.maude.notation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@SuppressWarnings("serial")
public class Msg  implements java.io.Serializable {
	
	String MsgName;
	
	String MsgToken;
	
	public Msg()
	{
		this.MsgName = null;
		this.MsgToken = null;
	}
	
	public String getMsgName() {
		return MsgName;
	}

	public void setMsgName(String msgName) {
		MsgName = msgName;
	}

	public String getMsgToken() {
		return MsgToken;
	}

	public void setMsgToken(String msgToken) {
		MsgToken = msgToken;
	}

	
	public Msg(String stringMsg)
	{
		ArrayList<String> ArrayMsg = PointDivision(stringMsg);
		//for(String a: ArrayMsg)
			////System.out.println("COSTRUTTORE MSG: "+a);
		//this.MsgName = ArrayMsg.get(0).replace("\"", "");
		this.MsgName = ArrayMsg.get(0);
		StringTokenizer st2 = new StringTokenizer(this.MsgName, "\"");

		ArrayList<String> appoggio = new ArrayList<String>();
		while (st2.hasMoreElements()) {
			appoggio.add((String) st2.nextElement());
		}
		this.MsgName=appoggio.get(0);
		////System.out.println("0 this.MsgName: "+this.MsgName);
		
		this.MsgName = ArrayMsg.get(0).replace("\"", "");
		this.MsgName = this.MsgName.substring(1, this.MsgName.length()-1);
		this.MsgToken = ArrayMsg.get(1).replaceAll("\\D+","");
				
	}
	
	public ArrayList<String> PointDivision(String msg)
	{
		ArrayList<String> PointDivision = new ArrayList<String>();
		
		StringTokenizer st = new StringTokenizer(msg, ".");
		while (st.hasMoreTokens())
		    	{
		    		PointDivision.add(st.nextToken());
		    	}
			
		return PointDivision;
	}	
	
	public ArrayList<Msg> SplitMsg(String msg)
	{	
		ArrayList<Msg> ArrayMsg = new ArrayList<Msg>();
		
		String[] strMsg = msg.split("andmsg");
		ArrayList<String> Msg = new ArrayList<String>(Arrays.asList(strMsg));
		
		//for(String a: Msg)
			////System.out.println(a);
		for(int i=0; i<Msg.size(); i++)
			{
			////System.out.println("SPLITMSG"+i+":"+Msg.get(i));
				
			Pattern patternmsg = Pattern.compile("^\\s*+(emptyMsgSet)+\\s*");
			Matcher matchermsg = patternmsg.matcher(Msg.get(i));			
			
				if(matchermsg.find())
				{
					////System.out.println("RIMUOVO!");
					Msg.remove(i);
				}else{
					////System.out.println("NON RIMUOVO!");
					StringTokenizer st = new StringTokenizer(Msg.get(i), ".");
					Msg msg1 = new Msg(Msg.get(i));
					ArrayMsg.add(msg1);
					while (st.hasMoreTokens())
					{	
					st.nextToken();
					}
				}
			}
		
			return ArrayMsg;	
		}
	
	public void printMsg()
	{
		//System.out.println("MsgName: "+this.MsgName);
		//System.out.println("MsgToken: "+this.MsgToken);		
	}
	
	public void printMsgList(ArrayList <Msg> MsgList)
	{
		//System.out.println("\nMsg: ");
		for(int i=0; i<MsgList.size(); i++)
		{
			MsgList.get(i).printMsg();
		}
	}
	
}
