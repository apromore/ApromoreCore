/*
 * Copyright © 2009-2015 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package com.apql.Apql.table;

import com.apql.Apql.listener.HeaderMouseMotionListener;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by corno on 10/08/2014.
 */
public class TableHeader extends JPanel{
    private int defaultWidth;
    private  int defaultHeight;
    private List<JLabel> columns;
    private transient HeaderMouseMotionListener listener;

    public TableHeader(TableProcess table, int defaultWidth, int defaultHeight,String... headers){
        this.defaultWidth=defaultWidth;
        this.defaultHeight=defaultHeight;
        this.listener=new HeaderMouseMotionListener();
        this.columns=new LinkedList<>();
        setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        HeaderLabel header;
        int col=0;
        for(String str : headers){
            header=new HeaderLabel(" "+str,col,table,columns);
            header.setPreferredSize(new Dimension(defaultWidth, defaultHeight));
            header.setMaximumSize(new Dimension(defaultWidth, defaultHeight));
            header.setMinimumSize(new Dimension(defaultWidth, defaultHeight));
//            header.setBorder(BorderFactory.createLoweredBevelBorder());
            header.addMouseMotionListener(listener);
            columns.add(header);
            add(header);
            col++;
        }
    }

    public void clearSort(){
       if(columns!=null && !columns.isEmpty()){
           for(JLabel column : columns)
               column.setIcon(null);
       }
    }
}
