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

package org.apromore.processdiscoverer.util;

import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 05/08/2018.
 */
public class Convertor {

    private static final DecimalFormat var1 = new DecimalFormat(Container.var1[123]);

    public static String convertFrom(String var2) {
        double var3 = Double.parseDouble(var2);
        double var4 = var3 / 1000.0;
        double var5 = var4 / 60.0;
        double var6 = var5 / 60.0;
        double var7 = var6 / 24.0;
        double var8 = var7 / 7.0;
        double var9 = var7 / 30.0;
        double var10 = var7 / 365.0;

        if(var10 > 1) {
            return var1.format(var10) + " yrs";
        }else if(var9 > 1) {
            return var1.format(var9) + " mths";
        }else if(var8 > 1) {
            return var1.format(var8) + " wks";
        }else if(var7 > 1) {
            return var1.format(var7) + " d";
        }else if(var6 > 1) {
            return var1.format(var6) + " hrs";
        }else if(var5 > 1) {
            return var1.format(var5) + " mins";
        }else if(var4 > 1) {
            return var1.format(var4) + " secs";
        }else if(var3 > 1){
            return var1.format(var3) + " millis";
        }else {
            return "instant"; 
        }
    }

    public static String[] convertFrom(Double var11) {
        double var12 = var11 / 1000.0;
        double var13 = var12 / 60.0;
        double var14 = var13 / 60.0;
        double var15 = var14 / 24.0;
        double var16 = var15 / 7.0;
        double var17 = var15 / 30.0;
        double var18 = var15 / 365.0;

        if(var18 > 1) return new String[] {var1.format(var18), "0"};
        else if(var17 > 1) return new String[] {var1.format(var17), "1"};
        else if(var16 > 1) return new String[] {var1.format(var16), "2"};
        else if(var15 > 1) return new String[] {var1.format(var15), "3"};
        else if(var14 > 1) return new String[] {var1.format(var14), "4"};
        else if(var13 > 1) return new String[] {var1.format(var13), "5"};
        else return new String[] {var1.format(var12), "6"};
    }

}
