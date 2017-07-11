package nl.rug.ds.bpm.verification.comparator;

import java.util.Comparator;

public class StringComparator implements Comparator<String>
{
	@Override
	public int compare(String a, String b)
	{
		return a.compareTo(b);
	}
}
