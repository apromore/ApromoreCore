/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2013 - 2016 Reina Uba.
 * Copyright (C) 2016 - 2017 Queensland University of Technology.
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

package org.apromore.similaritysearch.common.similarity;

import org.apromore.similaritysearch.common.Settings;
import org.apromore.similaritysearch.common.stemmer.PorterStemmer;
import org.apromore.similaritysearch.common.stemmer.SnowballStemmer;

import java.util.ArrayList;
import java.util.Set;
import java.util.StringTokenizer;


public class LabelEditDistance {

    private static PorterStemmer porterStemmer = new PorterStemmer();

    public static double edTokensWithStemming(String a, String b, String delimeter, SnowballStemmer stemmer, boolean stem) {

        if(a != null && b != null && a.equals(b)) return 1;
        else if(a == null && b == null) return 1;

        ArrayList<String> aTokensInit = new ArrayList<String>();
        ArrayList<String> bTokensInit = new ArrayList<String>();

        ArrayList<String> aTokens = new ArrayList<String>();
        ArrayList<String> bTokens = new ArrayList<String>();

        StringTokenizer tokensA = new StringTokenizer(a, delimeter);
        while (tokensA.hasMoreTokens()) {
            String aToken = tokensA.nextToken();
            aTokensInit.add(aToken.toLowerCase());
        }

        StringTokenizer tokensB = new StringTokenizer(b, delimeter);
        while (tokensB.hasMoreTokens()) {
            String bToken = tokensB.nextToken();
            bTokensInit.add(bToken.toLowerCase());
        }
        if (aTokensInit.contains("not") && !bTokensInit.contains("not")
                || !aTokensInit.contains("not") && bTokensInit.contains("not")) {
            return 0;
        }

        if (stem) {
            aTokens = removeStopWordsAndStem(aTokensInit, stemmer);
            bTokens = removeStopWordsAndStem(bTokensInit, stemmer);

            if (aTokens.size() == 0) {
                aTokens = removeStopWordsAndStem1(aTokensInit, stemmer);
            }
            if (aTokens.size() == 0) {
                aTokens = aTokensInit;
            }

            if (bTokens.size() == 0) {
                bTokens = removeStopWordsAndStem1(bTokensInit, stemmer);
            }
            if (bTokens.size() == 0) {
                bTokens = bTokensInit;
            }
        }

        int dimFunc = aTokens.size() > bTokens.size() ? aTokens.size() : bTokens.size();

        double costFunc[][] = new double[dimFunc][dimFunc];

        for (int i = 0; i < aTokens.size(); i++) {
            for (int j = 0; j < bTokens.size(); j++) {

                // find the score using edit distance
                double edScore = 0;

                int ed = ed(aTokens.get(i), bTokens.get(j));
                edScore = ed == 0 ? 1 :
                        (1 - ed / (Double.valueOf(Math.max(aTokens.get(i).length(), bTokens.get(j).length()))));
                costFunc[i][j] = edScore > 0 ? (-1) * edScore : edScore;
            }
        }
        double costFuncCopy[][] = new double[dimFunc][dimFunc];

        for (int i = 0; i < costFuncCopy.length; i++) {
            for (int j = 0; j < costFuncCopy[0].length; j++) {
                costFuncCopy[i][j] = costFunc[i][j];
            }
        }

        double mappedWeightFunc = 0;

        if(costFunc.length > 0) {
            int[][] result = new HungarianAlgorithm(costFuncCopy).execute();

            for (int i = 0; i < result.length; i++) {
                mappedWeightFunc += (-1) * costFunc[result[i][0]][result[i][1]];
            }

            // TOTAL mappingscore
            double mappingScore = 0;
            double mappedWeight = mappedWeightFunc;


            if (mappedWeight == 0) {
                mappingScore = 0;
            } else {
                mappingScore = mappedWeight * 2 / (aTokens.size() + bTokens.size());
            }
            return mappingScore;
        }else {
            return 0;
        }
    }


    private static ArrayList<String> removeStopWordsAndStem(ArrayList<String> toRemove, SnowballStemmer stemmer) {

        ArrayList<String> result = new ArrayList<String>();
        Set<String> stopWords = stemmer.getStopWords();
        int repeat = 1;

        for (String s : toRemove) {
            s = s.toLowerCase();
            if (s.length() > 2 && (!stemmer.hasStopWords() || stemmer.hasStopWords() && !stopWords.contains(s))) {
                String stemmedString;
                if(porterStemmer == null) {
                    stemmer.setCurrent(s);
                    for (int i = repeat; i != 0; i--) {
                        stemmer.stem();
                    }
                    stemmedString = stemmer.getCurrent();
                }else {
                    porterStemmer.add(s);
                    porterStemmer.stem();
                    stemmedString = porterStemmer.toString();
                }
                result.add(stemmedString);
            }
        }
        return result;
    }

    private static ArrayList<String> removeStopWordsAndStem1(ArrayList<String> toRemove, SnowballStemmer stemmer) {

        ArrayList<String> result = new ArrayList<String>();
        int repeat = 1;

        for (String s : toRemove) {
            s = s.toLowerCase();
            String stemmedString;
			if ( s.length() > 2) {
                if(porterStemmer == null) {
                    stemmer.setCurrent(s);
                    for (int i = repeat; i != 0; i--) {
                        stemmer.stem();
                    }
                    stemmedString = stemmer.getCurrent();
                }else {
                    porterStemmer.add(s);
                    porterStemmer.stem();
                    stemmedString = porterStemmer.toString();
                }
                result.add(stemmedString);
			}
        }
        return result;
    }

    public static int ed(String a, String b) {
        int[][] ed = new int[a.length() + 1][b.length() + 1];

        for (int i = 0; i < a.length() + 1; i++) {
            ed[i][0] = i;
        }

        for (int j = 1; j < b.length() + 1; j++) {

            ed[0][j] = j;

            for (int i = 1; i < a.length() + 1; i++) {

                ed[i][j] = Math.min(ed[i - 1][j] + 1,
                        Math.min(ed[i][j - 1] + 1,
                                ed[i - 1][j - 1] + (a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1)));
            }
        }
        return ed[a.length()][b.length()];
    }

    public static void main(String[] a) {
        LabelEditDistance.edTokensWithStemming("Determine caller s relationship to policy",
                "Determine if customer wants to continue with claim",
                Settings.STRING_DELIMETER,
                Settings.getEnglishStemmer(), true);
    }
}
