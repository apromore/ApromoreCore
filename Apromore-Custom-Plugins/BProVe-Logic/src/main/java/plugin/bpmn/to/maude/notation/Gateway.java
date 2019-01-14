package plugin.bpmn.to.maude.notation;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Gateway extends ProcElement  implements java.io.Serializable{
	
	ArrayList<Edge> EdgeSet;
	public Edge Edge;	
	GatewayType Gtype;
	
	public enum GatewayType{Join, Split};
	
	public Gateway()
	{
		
	}
	
	public ArrayList<String> TokenGateway(String gateway)
	{
		ArrayList<String> SplitTokenGateway = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(gateway, ",");
		while (st.hasMoreTokens())
	    	{
				SplitTokenGateway.add(st.nextToken());
	    	}
		return SplitTokenGateway;	
	}
	
	public String extractEdges(String TokenGateway)
	{
			String edges = null;
			Pattern patternextractEdges = Pattern.compile("^(edges\\()|^\\s*+(edges\\()");
			Matcher matcherextractEdges = patternextractEdges.matcher(TokenGateway);
			if (matcherextractEdges.find())
			{
				edges = TokenGateway.substring(matcherextractEdges.group().length());				
			}
									
			return edges;					
	}			
}