package plugin.bpmn.to.maude.notation;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;


public class Start extends Event implements java.io.Serializable{
	
	//Status Edge
	String Status;
	public Edge Edge;
	
	public Start()
	{
		
	}
	
	public Start(String stringstart)
	{
		ArrayList<String> StartToken = new ArrayList<String>();
		
		StartToken = TokenStartEdge(stringstart);
		this.Status = StartToken.get(0);
		
		Pattern pattern = Pattern.compile("^\"|^\\s*+\"");
		Matcher matcher = pattern.matcher(StartToken.get(1));	
		
		if (matcher.find()) 
		{
			int startindex = matcher.group().length()-1;
			int endindex = StartToken.get(1).lastIndexOf(")");
			String substring = StartToken.get(1).substring(startindex, endindex);
			this.Edge = new Edge(substring);
		}
		
		/*this.InputToken = StartToken.get(0);
		
		Pattern pattern = Pattern.compile("^\"|^[ ]+\"");
		Matcher matcher = pattern.matcher(StartToken.get(1));	
		
		if (matcher.find()) 
		{
			int startindex = matcher.group().length();
			int endindex = StartToken.get(1).lastIndexOf("\"");
			String substring = StartToken.get(1).substring(startindex, endindex);
			////System.out.println("SUBSTRING STARTRCVMSG: "+substring);
			////System.out.println("STARTINDEX: "+startindex);
			this.name = substring;
		}*/		
		
	}
	
	public ArrayList<String> TokenStartEdge(String start)
	{
		ArrayList<String> TokenStartEdge = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(start, ",");
		while (st.hasMoreTokens())
	    	{
				TokenStartEdge.add(st.nextToken());
	    	}
		return TokenStartEdge;	
	}
	
	/*public ArrayList<String> pointDivision(String msgstring )
		{
			ArrayList<String> Msg = new ArrayList<String>();
				StringTokenizer st = new StringTokenizer(msgstring, ".");
				while (st.hasMoreTokens())
					{
						Msg.add(st.nextToken());
					}
		
			return Msg;	
		}*/
	
	public boolean compareStart(Start start1, Start start2)
	{
		if(start1.Edge.EdgeName.equals(start2.Edge.EdgeName) 
				&& !start1.Status.equals(start2.Status))
		{
			return true;
		}else
		{
			return false;
		}
	}
	
	/*public boolean compareStartName (Start start1, Start start2)
	{
		if(start1.name.equals(start2.name))
		{
			return true;
		}else
		{
			return false;
		}
	}*/
	
	
	public void printStart()
	{
		//Status Edge
		//System.out.println("\nStart: ");
		//System.out.println("Status: "+this.Status);
		this.Edge.printEdge();		
	}
			
}

