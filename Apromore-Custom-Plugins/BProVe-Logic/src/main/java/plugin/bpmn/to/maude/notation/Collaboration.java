package plugin.bpmn.to.maude.notation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;


public class Collaboration implements java.io.Serializable{
	
	ArrayList<Pool> arrayPool;
	
	public Collaboration()
	{
		
	}
	
	public Collaboration(String stringcoll)
	{
		//System.out.println("\nINPUT COSTRUTTORE COLLABORATION: "+stringcoll);
		this.arrayPool = new ArrayList<Pool>();
		ArrayList<String> pool = SplitPool(stringcoll);
		
		//for(String a: pool)
			//System.out.println("SPLIT POOL: "+a);
				
		for(int i = 0; i<pool.size(); i++)
			{
				//System.out.println("\nContenuto Array<String> pool: "+pool.get(2));
				Pool pool1 = new Pool(pool.get(i));
				this.arrayPool.add(pool1);
				
			}		
	}
	
	//StringBuilder
	public String Inizializza(String result) 
    {
		result =result.replaceAll("\n","");
		//System.out.println("RESULT: "+result);
		
		return result;
		
	}
	
	//SPLIT INPUT STRING INTO TOKEN AND CREATE ARRAY
	public ArrayList<String> extractCollaboration(String input)
	
	{
		//System.out.println("STRING INPUTEXTRACT: "+input);
		ArrayList<String> collaboration = new ArrayList<String>();
		
		String stringprocelem = null;
		int scanIndex = 0;
		int substrStartIndex = 0;
		while(scanIndex<input.length())
			{
			substrStartIndex = input.indexOf("{", scanIndex);
			int parCount = 1;
			if(substrStartIndex == -1)
				break;
			scanIndex = substrStartIndex+5;
						
			while(parCount > 0 && scanIndex < input.length()) {
				if(input.charAt(scanIndex) == '{')
					parCount++;
				else if(input.charAt(scanIndex) == '}')
					parCount--;
				scanIndex++;				
				}			
			stringprocelem = input.substring(substrStartIndex, scanIndex);
			collaboration.add(stringprocelem);
			}
		
			
			
		//VECCHIA COLLABORATION	
		/*StringTokenizer st = new StringTokenizer(input, "{");
		
		while (st.hasMoreTokens())
 	    	{
 	    	collaboration.add(st.nextToken());
 	    	}
		collaboration.remove(0);*/
			
		return collaboration;
 	   
	}
	
	//SPLIT STRING INTO POOL
	public ArrayList<String> SplitPool(String pool)
		{
		String[] strMsg = pool.split("pool\\(");
		ArrayList<String> Pool = new ArrayList<String>(Arrays.asList(strMsg));//CONVERT ARRAY TO ARRAYLIST
		Pool.remove(0);
			
		return Pool;
		}
	
	public ArrayList<Proc> compareCollaboration(Collaboration collab1, Collaboration collab2)
	{
		ArrayList<Proc> processlist = new ArrayList<Proc>();
		Proc process1 = new Proc();
		Pool pool1 = new Pool();
		for(int i=0; i<collab1.arrayPool.size(); i++)
		{
			process1 = pool1.comparePoolperProcess(collab1.arrayPool.get(i), collab2.arrayPool.get(i));
			//process1.printProcess();
			if(process1.empty == false)
			{
			processlist.add(process1);
			}
		}
		
		
		return processlist;
			
	}
	
}
