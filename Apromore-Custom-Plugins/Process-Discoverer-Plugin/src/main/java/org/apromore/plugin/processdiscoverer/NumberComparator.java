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

package org.apromore.plugin.processdiscoverer;

import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;

import java.util.Comparator;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 05/08/2018.
 */
public class NumberComparator implements Comparator<Object> {

    private boolean var1;
    private int var2;

    public NumberComparator(boolean var3, int var4) {
        this.var1 = var3;
        this.var2 = var4;
    }

    @Override
    public int compare(Object o1, Object o2) {
        Double contributor1 = Double.parseDouble(((Listcell) ((Listitem) o1).getChildren().get(var2)).getLabel());
        Double contributor2 = Double.parseDouble(((Listcell) ((Listitem) o2).getChildren().get(var2)).getLabel());
        return contributor1.compareTo(contributor2) * (var1 ? 1 : -1);
    }

}