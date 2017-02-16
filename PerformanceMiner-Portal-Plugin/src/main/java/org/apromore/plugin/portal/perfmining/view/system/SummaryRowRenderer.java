/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apromore.plugin.portal.perfmining.view.system;

import org.zkoss.zul.*;

/**
 *
 * @author Administrator
 */
public class SummaryRowRenderer implements RowRenderer {
    
    public void render(final Row row, final java.lang.Object data) {
        String[] ary = (String[]) data;
        new Label(ary[0]).setParent(row);
        new Label(ary[1]).setParent(row);
        new Label(ary[2]).setParent(row);
        new Label(ary[3]).setParent(row);
        new Label(ary[4]).setParent(row);
        new Label(ary[5]).setParent(row);
        new Label(ary[6]).setParent(row);
    }
  
    public void render(final Row row, final java.lang.Object data, int test) {
        String[] ary = (String[]) data;
        new Label(ary[0]).setParent(row);
        new Label(ary[1]).setParent(row);
        new Label(ary[2]).setParent(row);
        new Label(ary[3]).setParent(row);
        new Label(ary[4]).setParent(row);
        new Label(ary[5]).setParent(row);
        new Label(ary[6]).setParent(row);
    }  
}
