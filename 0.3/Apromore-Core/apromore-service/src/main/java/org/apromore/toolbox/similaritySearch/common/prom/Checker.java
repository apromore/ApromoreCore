package org.apromore.toolbox.similaritySearch.common.prom;

import java.io.*;
import java.util.*;

public class Checker {

	private static Map<String, Long> word2id = null;
	private static Map<String, Long> morph2wordid = null;
	private static Map<Long, Set<Long>> wordid2synidset = null;
	private static Set<String> stopWords = null;

	public static double EQ_SCORE = 1.0;
	public static double SYN_SCORE = 0.75;

	private static final Porter stemmingAlgorithm = new Porter();

	private static Checker instance;
	private static String mapdir;
	private static boolean dictionaryLoaded = false;

	/**
	 * Initializer the checker. This operation may take some time, because a dictionary will
	 * be read into memory.
	 *
	 * @param mapdir	directory in which the dictionary is located (should end with /).
	 */
	@SuppressWarnings("static-access")
	private Checker(String mapdir) {
		this.mapdir = mapdir;
		instance=this;
	}
	
	@SuppressWarnings("unchecked")
	private void loadDictionary(){		
		word2id = (Map<String, Long>) loadMapFromObject(mapdir +
		"word2id.map");
		morph2wordid = (Map<String, Long>) loadMapFromObject(mapdir +
		"morph2wordid.map");
		wordid2synidset = (Map<Long, Set<Long>>) loadMapFromObject(
				mapdir + "wordid2synidset.map");
		stopWords = loadStringSetFromText(mapdir + "englishST.txt");
		dictionaryLoaded = true;
	}

	public static void setMapDir(String mapDir) {
		mapdir = mapDir;
	}

	public static Checker getInstance() {
		if (instance == null) {
			instance = new Checker(mapdir);
		}
		return instance;
	}

	private Object loadMapFromObject(String file) {
		Object result = null;
		try {
			FileInputStream fos = new FileInputStream(file);
			ObjectInputStream oos = new ObjectInputStream(fos);
			result = oos.readObject();
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private Set<String> loadStringSetFromText(String file) {
		Set<String> result = new HashSet<String>();
		try {
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);

			boolean hasRead = true;
			while (hasRead) {
				String read = br.readLine();
				hasRead = (read != null);
				if (hasRead) {
					result.add(read);
				}
			}

			br.close();
			isr.close();
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private Long word2id(String word) {
		return word2id.get(word);
	}

	private Long morph2wordid(String word) {
		return morph2wordid.get(word);
	}

	private Set<Long> wordid2synidset(long wordid) {
		Set<Long> result = wordid2synidset.get(wordid);
		if (result != null) {
			return result;
		} else {
			return new HashSet<Long>();
		}
	}

	/**
	 * Normalizes a word by:
	 * - removing preceeding and succeeding space, tab, enters
	 * - changing it to lowercase letters
	 * - removing the special characters: , : / \ [ ] ( )
	 * - replacing - with space
	 *
	 * @param word
	 * @return trimmmed word
	 */
	private String normalizeWord(String word) {
		return word.trim().toLowerCase().replaceAll(",|:|/|\\[|\\]|\\(|\\)", "").replaceAll("-",
		" ");
	}

	/**
	 * Gets the unique ID of a word by performing the following operations:
	 * - search word, if found return ID
	 * - if not found: word stemming, search again, if found return ID
	 * - if still not found search word in morphisms, if found return ID of the basic form of the word
	 *
	 * @return the ID of the found word, word stem or word morphism; 0 if nothing found
	 */
	private long getWordID(String word) {
		if (word.length() == 0) {
			return 0;
		}
		;

		Long result = word2id(word);

		if (result == null) {
			String stemmedWord = stemmingAlgorithm.stripAffixes(word);
			result = word2id(stemmedWord);
		}

		if (result == null) {
			result = morph2wordid(word);
		}

		if (result != null) {
			return result;
		} else {
			return 0;
		}
	}

	private Vector<String> splitString(String src) {
		Vector<String> result = new Vector<String>();

		int fromIndex = 0;
		int foundIndex;
		do {
			foundIndex = src.indexOf(' ', fromIndex);
			int toIndex = (foundIndex == -1) ? (src.length()) : (foundIndex);
			if (fromIndex != toIndex) {
				String trimmedWord = normalizeWord(src.substring(fromIndex, toIndex));
				if (!stopWords.contains(trimmedWord)) {
					result.add(trimmedWord);
				}
			}
			fromIndex = toIndex + 1;
		} while (foundIndex != -1);

		return result;
	}

	/**
	 * Returns the semantic equivalence score.
	 *
	 * This is a number between 0 and 1 to express the similarity between two strings,
	 * where 0 represents no similarity and 1 represents perfect similarity.
	 * The result is computed as follows:
	 * 1. split-up labels into words
	 * 2. trim words, words to lower-case, remove weird characters such as , ( ) [ ] (see normalizeWord)
	 * 3. remove 'stop-words', e.g.: 'for', 'an', 'on'
	 * 4. word 'stemming', e.g.: accounts -> account, accounting -> account, ...
	 * 5. take each word from the shortest label (in number of words) and compute the 'semantic-match score':
	 *    semantic match score = 1 if there is an identical word in the other label
	 *    semantic match score = 0.75 if there is a synonym in the other label
	 *    semantic match score = 0 otherwise
	 * 6. add the semantic match scores for each word in the shortest label and divide by the number of words in the longest label
	 * 7. this is the semantic equivalence score for the labels
	 *
	 * @param label1
	 * @param label2
	 * @return semantic equivalence score (value between 0 and 1) between labels
	 */
	public double semanticEquivalenceScore(String label1, String label2) {
		synchronized (this){
			if (!dictionaryLoaded){
				loadDictionary();
			}
		}
		Vector<String> wds1 = splitString(label1.replace("\r\n", " ").replace('\n', ' '));
		Vector<String> wds2 = splitString(label2.replace("\r\n", " ").replace('\n', ' '));

		Vector<String> longestVector;
		Vector<String> shortestVector;
		if (wds1.size() > wds2.size()) {
			longestVector = wds1;
			shortestVector = wds2;
		} else {
			longestVector = wds2;
			shortestVector = wds1;
		}

		Vector<Set<Long>> lwordsyns = new Vector<Set<Long>>();
		Vector<Long> lwordids = new Vector<Long>();
		for (Iterator<String> i = longestVector.iterator(); i.hasNext(); ) {
			String word1 = i.next();
			long wordid1 = getWordID(word1);
			lwordids.add(wordid1);
			lwordsyns.add(wordid2synidset(wordid1));
		}
		Vector<Set<Long>> swordsyns = new Vector<Set<Long>>();
		Vector<Long> swordids = new Vector<Long>();
		for (Iterator<String> i = shortestVector.iterator(); i.hasNext(); ) {
			String word2 = i.next();
			long wordid2 = getWordID(word2);
			swordids.add(wordid2);
			swordsyns.add(wordid2synidset(wordid2));
		}

		double score = 0.0;

		for (int i = 0; i < shortestVector.size(); i++) {
			String word1 = shortestVector.elementAt(i);
			double currscore = 0.0;
			for (int j = 0; j < longestVector.size(); j++) {
				String word2 = longestVector.elementAt(j);
				if (word1.equals(word2)) {
					currscore = EQ_SCORE;
					break;
				}
				if (swordsyns.elementAt(i).contains(lwordids.elementAt(j))) {
					currscore = SYN_SCORE;
				} else if (lwordsyns.elementAt(j).contains(swordids.elementAt(i))) {
					currscore = SYN_SCORE;
				}
			}
			score += currscore;
		}
		return score / longestVector.size();
	}


	/**
	 * Returns the syntactic equivalence score.
	 *
	 * This is a number between 0 and 1 to express the similarity between two strings,
	 * where 0 represents no similarity and 1 represents perfect similarity.
	 * The result is computed as the ratio between the number of operations required
	 * to go from one string to the other (by inserting, removing or replacing characters)
	 * and the maximum number of operations required to do so (which equals the sum of the stringlengths).
	 * The labels are normalized first: remove enters and weird characters, all characters to lowercase (see normalizeWord).
	 *
	 * @param label1
	 * @param label2
	 * @return syntactic equivalence score (value between 0 and 1) between the labels
	 *
	 */
	public double syntacticEquivalenceScore(String label1, String label2) {
        int n = label1.length(); // length of s
		int m = label2.length(); // length of t

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
			t_j = label2.charAt(j - 1);
			d[0] = j;

			Object s_i = null; // ith object of s
			for (i = 1; i <= n; i++) {
				s_i = label1.charAt(i - 1);
				cost = s_i.equals(t_j) ? (short) 0 : (short) 1;
				// minimum of cell to the left+1, to the top+1, diagonally left and up +cost
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

		// equivalence score = 1 - (costcount / max_costcount)
		// where max_costcount = sum of string lengths
		return 1 - (costcount * 1.0) / (label1.length() * 1.0 + label2.length() * 1.0);
	}

}
