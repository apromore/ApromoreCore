/*-
 * #%L
 * This file is part of "Apromore Core".
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

package org.apromore.apmlog.util;

import org.zkoss.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

/**
 *
 * This class is compatible with ZK MVVM implementation
 *
 * Share the runtime instance of this class (within browser tab) by:
 *
 * Executions.getCurrent().getDesktop().setAttribute("numberFormatStyle", this.numberFormatStyle);
 *
 * Get the shared runtime instance (within browser tab) by:
 *
 * NumberFormatStyle nfs =
 *                 (NumberFormatStyle) Executions.getCurrent().getDesktop().getAttribute("numberFormatStyle");
 *
 *
 *
 * @author Chii Chang (2022-02-01)
 */
public class NumberFormatStyle {

    private boolean thousandsSeparatorEnabled = true;
    private boolean commaDotStyle = true;
    private int decimalPlace = 2;

    private static final String THOUSANDS_SEPARATOR_ENABLED = "thousandsSeparatorEnabled";
    private static final String COMMA_DOT_STYLE = "commaDotStyle";
    private static final String DECIMAL_PLACE = "decimalPlace";


    public NumberFormatStyle() {

    }

    public NumberFormatStyle(boolean thousandsSeparatorEnabled, boolean commaDotStyle, int decimalPlace) {
        this.thousandsSeparatorEnabled = thousandsSeparatorEnabled;
        this.commaDotStyle = commaDotStyle;
        this.decimalPlace = decimalPlace;
    }

    public static NumberFormatStyle of(boolean thousandsSeparatorEnabled, boolean commaDotStyle, int decimalPlace) {
        return new NumberFormatStyle(thousandsSeparatorEnabled, commaDotStyle, decimalPlace);
    }

    public boolean isThousandsSeparatorEnabled() {
        return thousandsSeparatorEnabled;
    }

    public boolean isCommaDotStyle() {
        return commaDotStyle;
    }

    public int getDecimalPlace() {
        return decimalPlace;
    }

    public boolean isDotCommaStyle() {
        return !commaDotStyle;
    }

    public void setDotCommaStyle(boolean dotComma) {
        commaDotStyle = !dotComma;
    }

    public NumberFormatStyle setThousandsSeparatorEnabled(boolean thousandsSeparatorEnabled) {
        this.thousandsSeparatorEnabled = thousandsSeparatorEnabled;
        return this;
    }

    public NumberFormatStyle setCommaDotStyle(boolean commaDotStyle) {
        this.commaDotStyle = commaDotStyle;
        return this;
    }

    public NumberFormatStyle setDecimalPlace(int place) {
        this.decimalPlace = Math.max(place, 0);
        return this;
    }

    public DecimalFormat getDecimalFormatForFixed(int decimalPlace) {
        DecimalFormat df = getDecimalFormat();
        df.setMaximumFractionDigits(decimalPlace);
        return df;
    }

    public DecimalFormat getDecimalFormat() {
        NumberFormat nf = NumberFormat.getNumberInstance(commaDotStyle ? Locale.US : Locale.GERMAN);
        nf.setMaximumFractionDigits(decimalPlace);

        DecimalFormat formatter = (DecimalFormat) nf;

        if (thousandsSeparatorEnabled) {
            DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
            symbols.setGroupingSeparator(commaDotStyle ? ',' : '.');
            formatter.setDecimalFormatSymbols(symbols);
        } else {
            formatter.setGroupingUsed(false);
        }

        return  formatter;
    }

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(THOUSANDS_SEPARATOR_ENABLED, String.valueOf(thousandsSeparatorEnabled));
        jsonObject.put(COMMA_DOT_STYLE, String.valueOf(commaDotStyle));
        jsonObject.put(DECIMAL_PLACE, String.valueOf(decimalPlace));

        return jsonObject;
    }

    public static NumberFormatStyle from(JSONObject jsonObject) {
        boolean separatorEnabled = true;
        boolean commaDot = true;
        int decimal = 2;

        if (jsonObject.containsKey(THOUSANDS_SEPARATOR_ENABLED)) {
            separatorEnabled = Boolean.parseBoolean(jsonObject.get(THOUSANDS_SEPARATOR_ENABLED).toString());
        }

        if (jsonObject.containsKey(COMMA_DOT_STYLE)) {
            commaDot = Boolean.parseBoolean(jsonObject.get(COMMA_DOT_STYLE).toString());
        }

        if (jsonObject.containsKey(DECIMAL_PLACE)) {
            decimal = Integer.parseInt(jsonObject.get(DECIMAL_PLACE).toString());
        }

        return new NumberFormatStyle(separatorEnabled, commaDot, decimal);
    }
}
