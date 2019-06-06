/*
 * Copyright © 2019 The University of Melbourne.
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

package org.apromore.processdiscoverer.dfg.collectors;

import java.util.Calendar;

public class Calculator {

//    private BitSet var1;
    private String currentDate; 
    private long current;

    public Calculator() {
//        var1 = new BitSet(4);
//        var1.set(0);
    }

    // get current date
    public String getCurrentDate() {
        return currentDate;
    }

    public long getCurrent() {
        return current;
    }

    // Get date: yyyymmdd from the input timestamp
    // Fix error of month (base 0 instead of base 1 in the Calendar) 
    public void setCurrentDate(String timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(Long.parseLong(timestamp));

        currentDate = "";
        int j = 0;
        // 1: YEAR, 2: MONTH (base 0), 3: WEEK_OF_YEAR, 4: WEEK_OF_MONTH, 5: DAY_OF_MONTH (based 1)
        for(int i = 1; i < 6; i++) { 
            if(i == 1) currentDate += cal.get(i);
            else if(i == 2 || i == 5) {
                if(Integer.toString(cal.get(i)).length() == 1) {
                    j = 0;
                    currentDate += Integer.toString(j);
                }
                currentDate += (i == 2) ? Integer.toString(cal.get(i) + 1) : Integer.toString(cal.get(i));
            }else {
                j += i;
            }
        }
    }

    // seed: starting date (unused)
    // current: current number
    // increment: increment
    public void increment(String seed, long current, long increment) {
        //method18(seed);
        add(increment, current);
    }

    private long add(long a, long b) {
    	long var3;
        do {
        	current = a ^ b;
            var3 = (a & b) << 1;
            a = current;
            b = var3;
        } while (var3 != 0); //(var1.cardinality() == 1 && !var1.get(0)) || var1.cardinality() == 0);
        return current;
    }
    
//    public void method18(String date) {
//        short var33 = 0;
//        if(!var1.get(0)) return;
//        for(int i = 0; i < 3; i++) {
//            short var35 = (short) Short.toString(method1(i)).length();
//            if(var35 == 1) var35++;
//            short var36 = Short.parseShort(date.substring(var33, var33 + var35));
//            if (method1(i) - var36 >= 0 && var1.get(i)) {
//                var1.set(i + 1);
//            }
//            var33 += var35;
//        }
//        var1.set(0, false);
//    }

}
