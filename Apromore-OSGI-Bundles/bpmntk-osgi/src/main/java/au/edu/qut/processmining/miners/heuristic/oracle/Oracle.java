package au.edu.qut.processmining.miners.heuristic.oracle;

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

        while( oracleItems.size() != 1 ) {
            for( OracleItem oi : oracleItems ) System.out.println("DEBUG - oracle item: " + oi);

            /* firstly we try to merge XORs (all the possible XORs as possible, because XORs have priority on AND for the merging technique) */
            while(true) {
                System.out.println("DEBUG - oracle items: " + oracleItems.size());
                toMerge = new HashSet<>();
                for( OracleItem oi : oracleItems ) {
                    for( OracleItem oii : oracleItems ) if( (oi != oii) && oi.isXOR(oii) ) toMerge.add(oii);
                    /* if we found oracle items to be merged we add the oracle item we were analysing to the toMerge set and we proceed with the merging */
                    if( toMerge.size() != 0 ) {
                        System.out.println("DEBUG - merging XORs ...");
                        toMerge.add(oi);
                        break;
                    }
                }

                System.out.println("DEBUG - merge items: " + toMerge.size());
                if( toMerge.size() != 0 ) {
                    matryoshka = OracleItem.mergeXORs(toMerge);
                    for( OracleItem oi : toMerge ) {
                        System.out.println("DEBUG - xor: " + oi);
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
                matryoshka = OracleItem.mergeANDs(toMerge);
                System.out.println("DEBUG - merging ANDs ...");
                for( OracleItem oi : toMerge ) {
                    System.out.println("DEBUG - and: " + oi);
                    oracleItems.remove(oi);
                }
                oracleItems.add(matryoshka);
            }
        }

        System.out.println("DEBUG - matryoshka: " + matryoshka);
        return matryoshka;
    }


}
