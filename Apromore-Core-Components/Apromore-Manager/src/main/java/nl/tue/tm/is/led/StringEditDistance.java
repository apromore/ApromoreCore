/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 University of Tartu
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package nl.tue.tm.is.led;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Reina Uba
 * @author Bruce Nguyen
 *  - 23 April 2020: Switch off wordNet and TokenizedLabelCacheStrings which are not used in Apromore.
 *  - 23 April 2020: Add clearWordcache() - the missing method being used in Apromore-Manager
 */
public class StringEditDistance {
	
	//private static TokenizedLabelCacheStrings cache = null;
	//public static Map<String, Double> wordnetCache = new HashMap<String, Double>();
	public static Map<String, Double> wordCache = new HashMap<String, Double>();

	
//	private static JiangAndConrath jcn = null;
//	
//	public static void addCache(TokenizedLabelCacheStrings cache1) {
//		cache = cache1;
//	}
//	
//	public static boolean hasCache() {
//		return cache != null;
//	}
	
//	public static TokenizedLabel getLabelFromCache(String label){
//		// there should be only one tokenized label for one label
////		System.out.println("Get label from cache "+ label);
//		return cache.get(label);
//	}
	
	public static int editDistance(String label1, String label2) {
		String s = label1;
		String t = label2;

		int n = s.length(); // length of s
		int m = t.length(); // length of t

		if (n == 0) {
			return m;
		} else if (m == 0) {
			return n;
		}
		int MAX_N = m + n;

		short[] swap; // placeholder to assist in swapping p and d

		// indexes into strings s and t
		short i; // iterates through s
		short j; // iterates through t

		Object t_j = null; // jth object of t

		short cost; // cost

		short[] d = new short[MAX_N + 1];
		short[] p = new short[MAX_N + 1];

		for (i = 0; i <= n; i++) {
			p[i] = i;
		}

		for (j = 1; j <= m; j++) {
			t_j = t.charAt(j - 1);
			d[0] = j;

			Object s_i = null; // ith object of s
			for (i = 1; i <= n; i++) {
				s_i = s.charAt(i - 1);
				cost = s_i.equals(t_j) ? (short) 0 : (short) 1;
				// minimum of cell to the left+1, to the top+1, diagonally left
				// and up +cost
				d[i] = (short) Math.min(Math.min(d[i - 1] + 1, p[i] + 1), p[i - 1] + cost);
			}

			// copy current distance counts to 'previous row' distance counts
			swap = p;
			p = d;
			d = swap;
		}

		// our last action in the above loop was to switch d and p, so p now
		// actually has the most recent cost counts
		int costcount = p[n];
		
//		System.out.println(costcount + " "+ Math.max(label1.length(), label2.length()));
		// equivalence score = 1 - (costcount / max_costcount)
		// where max_costcount = sum of string lengths
		return costcount;
		//return 1 - (costcount * 1.0) / (s.length() * 1.0 + t.length() * 1.0);
	}
	// 1 similar ; 0 - different
	public static double similarity(String label1, String label2) {
//		if (true) 
//			return 0.5;
//		
		if ((label1.length() == 0) && (label2.length() == 0)){
			return 1.0;
		}
		Double res = wordCache.get(label1+";"+label2);
		if (res != null) {
			return res;
		}
		
//		if (StringEditDistance.hasWordnet()) {
//			TokenizedLabel t1 = StringEditDistance.getLabelFromCache(label1);
//			TokenizedLabel t2 = StringEditDistance.getLabelFromCache(label2);
//			double sim = 0;
//			PrintStream out = System.out;
//			PrintStream tmpStream = new PrintStream(new ByteArrayOutputStream());
//			try {
//				System.setOut(tmpStream);
//				sim = t1.similarityWordnet(t2, jcn);
//
//			} finally {
//				System.setOut(out);
//				tmpStream.close();
//				tmpStream = null;
//			}
//			wordCache.put(label1+";"+label2, /*1.0 - */sim);
//			return /*1.0 - */sim;
//		}
//		
//		if (StringEditDistance.hasCache() && 
//				label1 != null && label1.length() != 0 && 
//				label2 != null && label2.length() != 0) { 
//			TokenizedLabel t1 = StringEditDistance.getLabelFromCache(label1);
//			TokenizedLabel t2 = StringEditDistance.getLabelFromCache(label2);
//			res = /*1.0 - */t1.similarity(t2);
//			wordCache.put(label1+";"+label2, res);
//			return res;
//		}
		res = 1 - (editDistance(label1, label2)*1.0)/(Math.max(label1.length(), label2.length())*1.0);
		wordCache.put(label1+";"+label2, res);
		return res;  
	}

//	public static void addWordnet(JiangAndConrath jcn1) {
//		jcn = jcn1;
//	}
//	
//	public static JiangAndConrath getWordnet() {
//		return jcn;
//	}
//	
//	public static boolean hasWordnet() {
//		return jcn != null;
//	}
	
	public static void clearWordCache() { 
	    wordCache.clear(); 
	}
}
