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

import java.util.*;

import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Gateway;

/**
 * Created by Adriano on 1/11/2016.
 */
public class OracleItem implements Comparable {
    private Set<Integer> past;
    private Set<Integer> future;

    private String oracle;
    private String oraclePast;
    private String oracleFuture;

    private Set<OracleItem> xorBrothers;
    private Set<OracleItem> andBrothers;

    public OracleItem() {
        past = new HashSet<>();
        future = new HashSet<>();
        xorBrothers = new HashSet<>();
        andBrothers = new HashSet<>();
    }

    public void fillPast(int p) { past.add(p); }
    public void fillFuture(int f) { future.add(f); }

    public void fillPast(Collection<Integer> past) { this.past.addAll(past); }
    public void fillFuture(Collection<Integer> future) { this.future.addAll(future); }

    public Set<OracleItem> getXorBrothers() { return xorBrothers; }
    public Set<OracleItem> getAndBrothers() { return andBrothers; }

    public void engrave() {
//        this method should be called when we want to finalize an Oracle item
//        that means, it is ready to be successively used
//        it transform the Oracle item into its final string of type:
//        past|future where past = :x:y:z: and future = :j:k:l:
//        therefore: Oracle item = :x:y:z:|:j:k:l:

        int i;
        int size;
        ArrayList<Integer> past = new ArrayList<>(this.past);
        ArrayList<Integer> future = new ArrayList<>(this.future);

        ArrayList<Integer> present = new ArrayList<>();
        present.addAll(this.past);
        present.addAll(this.future);

        i=0;
        Collections.sort(past);
        size = past.size();
        oraclePast = ":";
        while( i < size ) oraclePast += ":" + past.get(i++) + ":";
        oraclePast += ":";

        i=0;
        Collections.sort(future);
        size = future.size();
        oracleFuture = ":";
        while( i < size ) oracleFuture += ":" + future.get(i++) + ":";
        oracleFuture += ":";

//        this is an extra string used to merge AND Oracle items
//        it contains all the ordered elements of the past and the future
        i=0;
        Collections.sort(present);
        size = present.size();
        oracle = ":";
        while( i < size ) oracle += ":" + present.get(i++) + ":";
        oracle += ":";
    }

    public boolean isXOR(OracleItem oItem) { return oItem.oracleFuture.equals(oracleFuture); }
    public static OracleItem mergeXORs(Set<OracleItem> xorBrothers) {
        /*
        * merging two or more XOR oracle items means:
        * 1. create a new oracle item that contains all the oracle items to be merged as xorBrothers
        * 2. its future will be the same shared future of all the xorBrothers in input (see also isXOR)
        * 3. its past will be the union of the pasts of all the xorBrothers in input
        *
        * eg: inputs = { (:A:|:C:D:), (:B:|:C:D:) } output = (:A:B:|:C:D:)
        */

        OracleItem oiUnion = new OracleItem();
        oiUnion.xorBrothers.addAll(xorBrothers);

        for( OracleItem xor : xorBrothers ) {
            oiUnion.future.addAll(xor.future);
            break;
        }

        for( OracleItem xor : xorBrothers ) oiUnion.past.addAll(xor.past);

        oiUnion.engrave();
        return oiUnion;
    }

    public boolean isAND(OracleItem oItem) { return oItem.oracle.equals(oracle); }
    public static OracleItem mergeANDs(Set<OracleItem> andBrothers) {
        /*
        * merging two or more AND oracle items means:
        * 1. create a new oracle item that contains all the oracle items to be merged as andBrothers
        * 2. its future will be only the part of shared future of all the andBrothers in input
        * 3. its past will be the union of the pasts of all the andBrothers in input
        *
        * eg: inputs = { (:A:|:B:C:D:), (:B:|:A:C:D:) } output = (:A:B:|:C:D:)
        */

        OracleItem oiUnion = new OracleItem();
        oiUnion.andBrothers.addAll(andBrothers);

        for( OracleItem and : andBrothers ) oiUnion.future.addAll(and.future);
        for( OracleItem and : andBrothers ) oiUnion.future.retainAll(and.future);

        for( OracleItem and : andBrothers ) oiUnion.past.addAll(and.past);

        oiUnion.engrave();
        return oiUnion;
    }


    public int getANDDistance(OracleItem oi) {
        HashSet<Integer> union = new HashSet<>();
        HashSet<Integer> intersection = new HashSet<>();
        int distance;

        union.addAll(oi.past);
        union.addAll(oi.future);

        intersection.addAll(this.past);
        intersection.addAll(this.future);
        intersection.retainAll(union);

        union.addAll(this.past);
        union.addAll(this.future);

        distance = union.size() - intersection.size();
        return distance;
    }

    public static OracleItem forcedMergeANDs(Set<OracleItem> andBrothers) {
        /*
        * forcing the merging of two or more AND oracle items means:
        * 1. create a new oracle item that contains all the oracle items to be merged as andBrothers
        * 2. its future will be the union of the future minus the union of the past of all the andBrothers
        * 3. its past will be the union of the pasts of all the andBrothers in input
        *
        * eg: inputs = { (:A:|:B:C:D:), (:B:|:A:C:) } output = (:A:B:|:C:D:)
        * note: in this example we are missing the activity 'D' in the future of the second input,
        *       but we proceed as it was there too, that is why we are 'forcing' the merging:
        *       somehow the concurrency between 'B' and 'D' was not caught OR
        *       the concurrency between 'A' and 'D' was noise
        */

        OracleItem oiUnion = new OracleItem();
        oiUnion.andBrothers.addAll(andBrothers);

        for( OracleItem and : andBrothers ) oiUnion.future.addAll(and.future);
        for( OracleItem and : andBrothers ) oiUnion.past.addAll(and.past);
        oiUnion.future.removeAll(oiUnion.past);

        oiUnion.engrave();
        return oiUnion;
    }


    public Gateway.GatewayType getGateType(){
//        note: we CANNOT have both xorBrothers and andBrothers filled
        if( !xorBrothers.isEmpty() ) return Gateway.GatewayType.DATABASED;
        if( !andBrothers.isEmpty() ) return Gateway.GatewayType.PARALLEL;
        return null;
    }

    public Integer getNodeCode() {
        if( past.size() == 1 ) return (new ArrayList<>(past)).get(0);
        else return null;
    }

    @Override
    public String toString() { return (oraclePast + "|" + oracleFuture); }

    @Override
    public boolean equals(Object o) {
        if( o instanceof OracleItem ) {
            return o.toString().equals(this.toString());
        } else return false;
    }

    @Override
    public int compareTo(Object o) {
        if( o instanceof OracleItem ) {
            return o.toString().compareTo(this.toString());
        } else return -1;
    }
}
