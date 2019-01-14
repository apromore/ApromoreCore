package plugin.bpmn.to.maude.notation;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Terminate extends End implements java.io.Serializable{
	
	 //Status Edge
	String Status;
	
	
	public Terminate()
	{
		
	}
	
	public Terminate(String stringterminate) {
		ArrayList<String> TerminateToken = new ArrayList<String>();
		
		TerminateToken = TokenTerminateEdge(stringterminate);
		this.Status = TerminateToken.get(0);
		
		Pattern pattern = Pattern.compile("^\"|^\\s*+\"");
		Matcher matcher = pattern.matcher(TerminateToken.get(1));	
		
		if (matcher.find()) 
		{
			int startindex = matcher.group().length()-1;
			int endindex = TerminateToken.get(1).lastIndexOf(")");
			String substring = TerminateToken.get(1).substring(startindex, endindex);
			this.Edge = new Edge(substring);
		}
	}
	
	public ArrayList<String> TokenTerminateEdge(String terminate)
	{
		ArrayList<String> TokenTerminateEdge = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(terminate, ",");
		while (st.hasMoreTokens())
	    	{
				TokenTerminateEdge.add(st.nextToken());
	    	}
		return TokenTerminateEdge;	
	}
	
	public boolean compareTerminate(Terminate terminate1, Terminate terminate2)
	{
		if(terminate1.Status.equals(terminate2.Status))
		{
			return true;
		}else
		{
			return false;
		}
	}
	
	public void printTerminate()
	{
		//Status Edge
		//System.out.println("\nStart: ");
		//System.out.println("Status: "+this.Status);
		this.Edge.printEdge();		
	}

}
