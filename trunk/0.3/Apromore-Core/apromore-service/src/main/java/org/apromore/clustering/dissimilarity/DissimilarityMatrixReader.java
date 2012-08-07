package org.apromore.clustering.dissimilarity;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.StringTokenizer;

import org.apache.commons.collections.map.MultiKeyMap;
import org.apromore.clustering.containment.ContainmentRelation;

public class DissimilarityMatrixReader implements DissimilarityMatrix {
    MultiKeyMap dissimmap = new MultiKeyMap();
    ContainmentRelation crel;

    public DissimilarityMatrixReader(Reader reader, ContainmentRelation crel, double threshold) throws Exception {
        BufferedReader in = new BufferedReader(reader);
        this.crel = crel;
        int lnumber = 1;
        String line;
        while ((line = in.readLine()) != null) {
            StringTokenizer tokenizer = new StringTokenizer(line, ",");
            String f = tokenizer.nextToken();
            String s = tokenizer.nextToken();
            String t = tokenizer.nextToken();

            try {
                String first = f;
                String second = s;
                double diss = Double.valueOf(t);

                if (diss <= threshold) {
                    Integer ifirst = crel.getFragmentIndex(first);
                    Integer isecond = crel.getFragmentIndex(second);
                    dissimmap.put(ifirst, isecond, diss);
                }
            } catch (NumberFormatException e) {
                System.out.printf("Error in line: %d, '%s'\n", lnumber, line);
            }
            lnumber++;
        }
    }

    public Double getDissimilarity(Integer frag1, Integer frag2) {
        Double result = (Double) dissimmap.get(frag1, frag2);
        if (result == null) {
            result = (Double) dissimmap.get(frag2, frag1);
        }
        return result;
    }
}
