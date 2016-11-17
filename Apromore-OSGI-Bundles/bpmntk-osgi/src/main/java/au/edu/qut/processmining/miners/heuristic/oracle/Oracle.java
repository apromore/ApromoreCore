package au.edu.qut.processmining.miners.heuristic.oracle;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Adriano on 2/11/2016.
 */
public class Oracle {

    public Oracle(){}

    public OracleItem getFinalOracleItem(Set<OracleItem> oracleItems) {
        OracleItem matryoshka = null;
        Set<OracleItem> toMerge;
        boolean merged;
        boolean forced = true;

        while( oracleItems.size() != 1 ) {
//            System.out.println("DEBUG - oracle items: " + oracleItems.size());
            for( OracleItem oi : oracleItems ) System.out.println("DEBUG - oracle item: " + oi);
            merged = false;

            /* firstly we try to merge XORs (all the possible XORs as possible, because XORs have priority on AND for the merging technique) */
            while(true) {
                toMerge = new HashSet<>();
                for( OracleItem oi : oracleItems ) {
                    for( OracleItem oii : oracleItems ) if( (oi != oii) && oi.isXOR(oii) ) toMerge.add(oii);
                    /* if we found oracle items to be merged we add the oracle item we were analysing to the toMerge set and we proceed with the merging */
                    if( toMerge.size() != 0 ) {
                        toMerge.add(oi);
                        break;
                    }
                }

                if( toMerge.size() != 0 ) {
                    merged = true;
                    matryoshka = OracleItem.mergeXORs(toMerge);
                    System.out.println("DEBUG - merging XORs ...");
                    for( OracleItem oi : toMerge ) {
                        System.out.println("DEBUG - XOR: " + oi);
                        oracleItems.remove(oi);
                    }
                    oracleItems.add(matryoshka);
                } else break;
            }

            for( OracleItem oi : oracleItems ) {
                toMerge = new HashSet<>();
                for( OracleItem oii : oracleItems ) if( (oi != oii) && oi.isAND(oii) ) toMerge.add(oii);
                if( toMerge.size() != 0 ) {
                    toMerge.add(oi);
                    break;
                }
            }

            if( toMerge.size() != 0 ) {
                merged = true;
                matryoshka = OracleItem.mergeANDs(toMerge);
                System.out.println("DEBUG - merging ANDs ...");
                for( OracleItem oi : toMerge ) {
                    System.out.println("DEBUG - AND: " + oi);
                    oracleItems.remove(oi);
                }
                oracleItems.add(matryoshka);
            }

            if( !merged ) {
                int tmpDistance;
                int minDistance = Integer.MAX_VALUE;
                System.out.println("WARNING - impossible merging oracle items, extending the concurrency relationships");

                for( OracleItem oi : oracleItems )
                    for( OracleItem oii : oracleItems ) {
                        if( oi == oii ) continue;
                        tmpDistance = oi.getANDDistance(oii);
                        if( tmpDistance < minDistance ) {
                            minDistance = tmpDistance;
                            toMerge = new HashSet<>();
                            toMerge.add(oi);
                            toMerge.add(oii);
                        }
                    }

                if(forced) matryoshka = OracleItem.forcedMergeANDs(toMerge);
                else matryoshka = OracleItem.mergeANDs(toMerge);

                System.out.println("WARNING - forcing AND merge ...");
                for( OracleItem oi : toMerge ) {
                    System.out.println("WARNING - f-AND: " + oi);
                    oracleItems.remove(oi);
                }
                oracleItems.add(matryoshka);
            }
        }

        System.out.println("DEBUG - matryoshka: " + matryoshka);
        return matryoshka;
    }


}
