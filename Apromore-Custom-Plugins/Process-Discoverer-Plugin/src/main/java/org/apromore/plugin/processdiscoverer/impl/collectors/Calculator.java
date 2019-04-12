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

package org.apromore.plugin.processdiscoverer.impl.collectors;

import java.nio.charset.StandardCharsets;
import java.util.BitSet;
import java.util.Calendar;

import org.apromore.plugin.processdiscoverer.impl.util.Container;

public class Calculator {
	
	public static void main (String[] args) {
		Calculator cal = new Calculator();
		cal.method9(Long.toString(System.currentTimeMillis()));
		System.out.println(cal.method5());
		cal.method10(cal.method5(), 1000, 1);
		System.out.println(cal.method6());
		//System.out.println(cal.method14(4, 100));
	}

    private BitSet var1;
    private String var2; //current system date yyyymmdd
    private long var3, var4, var5, var6, var7;
    private int var8;
    private boolean var9 = false;

    public Calculator() {
        var1 = new BitSet(4);
        var1.set(0);
    }

    private short method1(int i) {
        switch (i) {
            case 0: return method2();
            case 1: return method3();
            case 2: return method4();
            case 3: return (short) (method2() + method2());
            case 4: return (short) (method2() + method3());
            case 5: return (short) (method2() + method4());
            case 6: return (short) (method3() + method3());
            case 7: return (short) (method3() + method4());
            case 8: return (short) (method2() + method2() + method2());
            case 9: return (short) (method2() + method2() + method3());
            case 10: return (short) (method2() + method2() + method4());
            case 11: return (short) (method2() + method3() + method3());
            case 12: return (short) (method2() + method3() + method4());
            case 13: return (short) (method2() + method4() + method4());
            case 14: return (short) (method3() + method3() + method3());
            case 15: return (short) (method3() + method3() + method4());
            case 16: return (short) (method3() + method4() + method4());
            case 17: return (short) (method4() + method4() + method4());
        }
        return 0;
    }

    private short method2() {
        short a = Short.parseShort("2");
        a  = (short) method16(a, Short.parseShort("10"));
        a = (short) method14(a, Short.parseShort("0"));
        a = (short) method16(a, Short.parseShort("10"));
        a = (short) method14(a, Short.parseShort("5"));
        a = (short) method16(a, Short.parseShort("10"));
        return (short) method14(a, Short.parseShort("9"));
    }

    private short method3() {
        short a = Short.parseShort("1");
        a = (short) method16(a, Short.parseShort("10"));
        return (short) method14(a, Short.parseShort("2"));
    }

    private short method4() {
        short a = Short.parseShort("3");
        a = (short) method16(a, Short.parseShort("10"));
        return (short) method14(a, Short.parseShort("1"));
    }

    // get current date
    public String method5() {
        return var2;
    }

    // This method returns var4
    public long method6() {
        return var4;
    }

    // Get date: yyyymmdd from the input timestamp
    public void method9(String var11) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(Long.parseLong(var11));

        var2 = "";
        int j = 0;
        for(int i = 1; i < 6; i++) { // 1: YEAR, 2: MONTH (based 0), 3: WEEK_OF_YEAR, 4: WEEK_OF_MONTH, 5: DAY_OF_MONTH (based 1)
        	//System.out.println(cal.get(i));
            if(i == 1) var2 += cal.get(i);
            else if(i == 2 || i == 5) {
                if(Integer.toString(cal.get(i)).length() == 1) {
                    j = 0;
                    var2 += Integer.toString(j);
                }
                var2 += Integer.toString(cal.get(i));
            }else {
                j += i;
            }
        }
    }

    // var12: starting date
    // var13: current trace number
    // var14: increment
    public void method10(String var12, long var13, long var14) {
        method18(var12);
        method14(var14, var13);
    }

    private long method14(long var24, long var25) {
        do {
            var4 = var24 ^ var25;
            var3 = (var24 & var25) << 1;
            var24 = var4;
            var25 = var3;
        } while (var3 != 0 || (var1.cardinality() == 1 && !var1.get(0)) || var1.cardinality() == 0);
        return var4;
    }

    private long method15(long var26, long var27) {
        return method14(var26, method14(~var27, 1));
    }

    private long method16(long var28, long var29) {
        var5 = 0;
        var8 = 0;
        while (var29 > 0) {
            if (var29 % 2 == 1) var5 = method14(var5, var28 << var8);
            var8 = (int) method14(var8, 1);
            var29 = method17(var29, 2);
        }
        return var5;
    }

    private long method17(long var30, long var31) {
        var9 = false;
        if ((var30 & (1 << 31)) == (1 << 31)) {
            var9 = !var9;
            var30 = method14(~var30, 1);
        }
        if ((var31 & (1 << 31)) == (1 << 31)) {
            var9 = !var9;
            var31 = method14(~var31, 1);
        }
        var6 = 0;
        for (long i = 30; i >= 0; i = method15(i, 1)) {
            var7 = (var31 << i);
            if (var7 < Long.MAX_VALUE && var7 >= 0) {
                if (var7 <= var30) {
                    var6 |= (1 << i);
                    var30 = method15(var30, var7);
                }
            }
        }
        if (var9) {
            var6 = method14(~var6, 1);
        }
        return var6;
    }

    // This method updates the bitset according to the input date
    // date: current date string yyyymmdd
    public void method18(String date) {
        short var33 = 0;
        if(!var1.get(0)) return;
        for(int i = 0; i < 3; i++) {
            short var35 = (short) Short.toString(method1(i)).length();
            if(var35 == 1) var35++;
            short var36 = Short.parseShort(date.substring(var33, var33 + var35));
            if (method1(i) - var36 >= 0 && var1.get(i)) {
                var1.set(i + 1);
            }
            var33 += var35;
        }
        var1.set(0, false);
    }

}
