/**
 * Copyright (c) 2009, Ole Eckermann, Stefan Krumnow & Signavio GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.signavio.warehouse.business.util.jpdl4;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringWriter;

public class Bounds {
    private int ulx = 0;
    private int uly = 0;
    private int height = 80;
    private int width = 100;

    public void setUlx(int ulx) {
        this.ulx = ulx;
    }

    public void setUly(int uly) {
        this.uly = uly;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getUlx() {
        return ulx;
    }

    public int getUly() {
        return uly;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public Bounds() {

    }

    public Bounds(JSONObject bounds) {
        try {
            JSONObject upperLeft = bounds.getJSONObject("upperLeft");
            JSONObject lowerRight = bounds.getJSONObject("lowerRight");
            this.ulx = upperLeft.getInt("x");
            this.uly = upperLeft.getInt("y");
            this.width = lowerRight.getInt("x") - ulx;
            this.height = lowerRight.getInt("y") - uly;
        } catch (JSONException e) {
        }
    }

    public Bounds(String[] bounds) {
        if (bounds.length == 4) {
            this.ulx = Integer.parseInt(bounds[0]);
            this.uly = Integer.parseInt(bounds[1]);
            this.width = Integer.parseInt(bounds[2]);
            this.height = Integer.parseInt(bounds[3]);
        }
    }

    public String toJpdl() {
        StringWriter jpdl = new StringWriter();
        jpdl.write(" g=\"");
        jpdl.write(ulx + ",");
        jpdl.write(uly + ",");
        jpdl.write(width + ",");
        jpdl.write(height + "\"");
        return jpdl.toString();
    }

    public JSONObject toJson() throws JSONException {

        JSONObject lowerRight = new JSONObject();
        lowerRight.put("x", ulx + width);
        lowerRight.put("y", uly + height);

        JSONObject upperLeft = new JSONObject();
        upperLeft.put("x", ulx);
        upperLeft.put("y", uly);

        JSONObject bounds = new JSONObject();
        bounds.put("lowerRight", lowerRight);
        bounds.put("upperLeft", upperLeft);

        return bounds;
    }

}
