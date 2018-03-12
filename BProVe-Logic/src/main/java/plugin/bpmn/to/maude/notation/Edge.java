package plugin.bpmn.to.maude.notation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

public class Edge implements java.io.Serializable{
	
	public String EdgeName;
	String EdgeToken;
	
	public Edge()
	{
		this.EdgeName = "";
		this.EdgeToken = "";		
	}
	
	public Edge(String stringEdge)
	{
		ArrayList<String> ArrayEdge = PointDivision(stringEdge);
		
		Pattern patterEdgeName = Pattern.compile("^\"+[A-Za-z0-9-_:]+\" |^\\s*+\"+[A-Za-z0-9-_:]+\"");
		Matcher matcherEdgeName = patterEdgeName.matcher(ArrayEdge.get(0));
		
		if(matcherEdgeName.find())
		{
			String replaceEdgeName = matcherEdgeName.group().replaceAll("\\s+","");
		
			this.EdgeName = replaceEdgeName.substring(1, replaceEdgeName.length()-1);
			this.EdgeToken = ArrayEdge.get(1).replaceAll("\\D+","");
		}
	}
	
	public ArrayList<String> PointDivision(String EdgeTaskEdge)
	{
		ArrayList<String> PointDivision = new ArrayList<String>();
		
		StringTokenizer st = new StringTokenizer(EdgeTaskEdge, ".");
		while (st.hasMoreTokens())
		    	{
		    		PointDivision.add(st.nextToken());
		    	}
			
		return PointDivision;
	}
	
	public ArrayList<Edge> SplitEdges(String edge)
	{	
		
		//System.out.println("\nEdge: "+edge);
		String[] strEdge = edge.split("and");
		ArrayList<String> Edges = new ArrayList<String>(Arrays.asList(strEdge));
		ArrayList<Edge> ArrayEdge = new ArrayList<Edge>();
		for(int i=0; i<Edges.size(); i++)
			{
				StringTokenizer st = new StringTokenizer(Edges.get(i), ".");
				Edge edge1 = new Edge(Edges.get(i));
				ArrayEdge.add(edge1);
				while (st.hasMoreTokens())
					{	
					st.nextToken();
					}
			}
			return ArrayEdge;	
		}
	
	
	public void printEdge()
	{
		//System.out.println("EdgeName: "+this.EdgeName);
		//System.out.println("EdgeToken: "+this.EdgeToken);
	}
	
	public void printEdgeList(ArrayList <Edge> EdgeList)
	{
		for(int i=0; i<EdgeList.size(); i++)
		{
			EdgeList.get(i).printEdge();
		}
	}
}
