/*
 * Copyright © 2009-2018 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package au.edu.qut.processmining.miners.splitminer.oracle;

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
        int counter = 0;

        while( oracleItems.size() != 1 ) {
//            System.out.println("DEBUG - oracle items: " + oracleItems.size());
//            for( OracleItem oi : oracleItems ) System.out.println("DEBUG - oracle item: " + oi);
            merged = false;

//            firstly we try to merge XORs,
//            because XORs have priority on ANDs for the merging technique
            while(true) {
                toMerge = new HashSet<>();
                for( OracleItem oi : oracleItems ) {
//                    if we found oracle items to be merged we add the Oracle item we were analysing
//                    to the toMerge set and we proceed with the merging
                    for( OracleItem oii : oracleItems ) if( (oi != oii) && oi.isXOR(oii) ) toMerge.add(oii);

//                    if we found some Oracle item that can be merged we add also the one we used for the comparisons
                    if( toMerge.size() != 0 ) {
                        toMerge.add(oi);
                        break;
                    }
                }

//                merging time: XORs
                if( toMerge.size() != 0 ) {
                    merged = true;
                    matryoshka = OracleItem.mergeXORs(toMerge);
//                    System.out.println("DEBUG - merging XORs ...");
                    oracleItems.removeAll(toMerge);
                    oracleItems.add(matryoshka);
                } else break;
            }

//            after we merged all the possible XORs Oracle items,
//            we try to merge the ANDs Oracle items, using the same technique
            for( OracleItem oi : oracleItems ) {
                toMerge = new HashSet<>();
                for( OracleItem oii : oracleItems ) if( (oi != oii) && oi.isAND(oii) ) toMerge.add(oii);
                if( toMerge.size() != 0 ) {
                    toMerge.add(oi);
                    break;
                }
            }

//            merging time: ANDs
            if( toMerge.size() != 0 ) {
                merged = true;
                matryoshka = OracleItem.mergeANDs(toMerge);
//                System.out.println("DEBUG - merging ANDs ...");
                oracleItems.removeAll(toMerge);
                oracleItems.add(matryoshka);
            }

//            it can happens that we did not merge anything, but there are still Oracle items
//            this can happen because the concurrency relationships are not complete
//            in such case, we have to force the merging
            if( !merged ) {
                int tmpDistance;
                int minDistance = Integer.MAX_VALUE;
//                System.out.println("WARNING - impossible merging oracle items, extending the concurrency relationships");

//                if we have to force a merging, we try to merge the minimum distance couple of Oracle items
//                that is: we are trying to introduce the less possible changes in the concurrency relationships
//                we prefer to assume that a concurrency relationship was missing
//                rather than a concurrency relationship was an error
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

//                System.out.println("WARNING - forcing AND merge ...");
                counter++;
                oracleItems.removeAll(toMerge);
                oracleItems.add(matryoshka);
            }
        }

        if( counter != 0 ) System.out.println("DEBUG - forced AND merging: " + counter);
//        System.out.println("DEBUG - matryoshka: " + matryoshka);
        return matryoshka;
    }


}
