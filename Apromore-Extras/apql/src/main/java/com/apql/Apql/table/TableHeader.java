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
