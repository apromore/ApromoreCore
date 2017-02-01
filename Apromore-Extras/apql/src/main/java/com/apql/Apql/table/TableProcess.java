/*
 * Copyright © 2009-2017 The Apromore Initiative.
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

package com.apql.Apql.table;

import com.apql.Apql.controller.QueryController;
import com.apql.Apql.tree.DraggableNodeProcess;
import org.apromore.model.VersionSummaryType;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.text.ParseException;
import java.util.*;
import java.util.List;

/**
 * Created by corno on 8/08/2014.
 */
public class TableProcess extends JPanel implements Observer, Serializable {
    private JPanel panelHeader;
    private JScrollPane scrollHeader;
    private JScrollPane scrollRows;
    private List<TableRow> rows;
    private int nColumns;
    private JPanel panelRows;
    private TableHeader header;
    private transient QueryController queryController=QueryController.getQueryController();
    private int mode=0;
    private int lastMode=-1;
    private boolean selectRow=true;
    private BorderLayout layout;

    public TableProcess(){
        super();
        layout = new BorderLayout();
        setLayout(layout);
        setBackground(Color.WHITE);
        rows=new LinkedList<>();

    }

    public TableProcess(boolean selectRow){
        super();
        layout = new BorderLayout();
        setLayout(layout);
        setBackground(Color.WHITE);
        rows=new LinkedList<>();
        this.selectRow=selectRow;
    }

    public void setHeader(int widthHeader, int heightHeader, String... header){
        this.nColumns=header.length;
        panelHeader=new JPanel();
        panelHeader.setMinimumSize(new Dimension(widthHeader * header.length, 35));
        panelHeader.setPreferredSize(new Dimension(widthHeader*header.length, 35));
        panelHeader.setMaximumSize(new Dimension(widthHeader*header.length, 35));
        this.header=new TableHeader(this,widthHeader,heightHeader, header);
        panelHeader.add(this.header);
        panelHeader.revalidate();
        panelHeader.repaint();

        panelRows=new JPanel();
        scrollRows=new JScrollPane(panelRows);
        scrollRows.setVisible(false);
        scrollHeader=new JScrollPane(panelHeader);
        scrollHeader.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        this.add(scrollHeader, BorderLayout.NORTH);
        this.add(scrollRows, BorderLayout.CENTER);

        this.revalidate();
        this.repaint();
    }

    public void clearSelection(){
        if(!rows.isEmpty()) {
            boolean whiteBackground = true;
            for (TableRow row : rows) {
                if (whiteBackground) {
                    row.setBackground(Color.white.brighter());
                    whiteBackground = false;
                } else {
                    row.setBackground(Color.LIGHT_GRAY);
                    whiteBackground = true;
                }
            }
            this.revalidate();
            this.repaint();
        }
    }

    public void setRows(int widthHeader, int heightHeader, DraggableNodeProcess dnp){
        panelRows=new JPanel();
        rows.clear();
        panelRows.setLayout(new GridLayout(dnp.getVersions().size(),1));
        TableRow row;
        boolean whiteBackground=true;
        if(dnp!=null) {
            for (VersionSummaryType vst : dnp.getVersions()) {
                Color backgroundColor = whiteBackground ? Color.white.brighter() : Color.lightGray;
                System.out.println(whiteBackground ? "WHITE" : "BLACK");
                whiteBackground = !whiteBackground;
                row = new TableRow(this, widthHeader, heightHeader, backgroundColor, dnp.getPathNode(), dnp.getId(), vst.getName(), vst.getVersionNumber(), vst.getLastUpdate());
                this.rows.add(row);
                panelRows.add(row);
            }
        }
        this.remove(scrollRows);
        scrollRows = new JScrollPane(panelRows);

        this.add(scrollRows, BorderLayout.CENTER);

        this.revalidate();
        this.repaint();
    }

    public void setRows(int widthHeader, int heightHeader,Set<String> idNets){
        panelRows=new JPanel();
        panelRows.setLayout(new GridLayout(idNets.size(),1));
        TableRow row;
        rows.clear();
        boolean whiteBackground=true;
        StringTokenizer st;
        for(String id : idNets){
            DraggableNodeProcess dnp;
            try {
                dnp = queryController.getLocation(id);
            } catch (ParseException e) {
                throw new RuntimeException("I'm too lazy to deal with tightening up Luigi's stringly-typed stuff right now", e);
            }
            System.out.println("DRAGGABLE: "+dnp+" "+idNets);
            st=new StringTokenizer(id,"/");
            Color backgroundColor = whiteBackground ? Color.white.brighter() : Color.lightGray;
            whiteBackground = !whiteBackground;
            row=new TableRow(this,widthHeader,heightHeader,backgroundColor,dnp.getPathNode(),null,dnp.getName(),st.nextToken(),dnp.getOriginalLanguage(),st.nextToken(),st.nextToken());
            this.rows.add(row);
            panelRows.add(row);
        }

        this.remove(scrollRows);
        scrollRows = new JScrollPane(panelRows);
        this.add(scrollRows, BorderLayout.CENTER);
        this.revalidate();
        this.repaint();
    }

    public List<TableRow> getRows(){
        return rows;
    }

    public void cleanRows(){
        this.remove(scrollRows);
        panelRows=new JPanel();
        panelRows.setLayout(new GridLayout(0,1));
        rows.clear();
        scrollRows = new JScrollPane(panelRows);
        if(header!=null)
            header.clearSort();
        this.add(scrollRows, BorderLayout.CENTER);
        this.revalidate();
        this.repaint();
    }

    public void sortTable(int column){
        if(scrollRows==null)
            return;
        scrollRows.setVisible(false);
        panelRows = new JPanel();
        panelRows.setLayout(new GridLayout(rows.size(), 1));
        boolean white=true;
        if(lastMode!=column) {
            lastMode=column;
            this.mode = column;
            Collections.sort(rows);
        } else {
            Collections.reverse(rows);
        }

        for (TableRow tr : rows) {
            Color background = white ? Color.WHITE : Color.LIGHT_GRAY;
            white = !white;
            tr.setBackground(background);
            for(JLabel label : tr.getLabelsRow()){
                label.setBackground(background);
                label.revalidate();
                label.repaint();
            }
            tr.revalidate();
            tr.repaint();
            panelRows.add(tr);
        }

        scrollRows = new JScrollPane(panelRows);
        this.add(scrollRows, BorderLayout.CENTER);
        scrollRows.setVisible(true);
        panelRows.revalidate();
        panelRows.repaint();
        this.revalidate();
        this.repaint();
    }

    public void setOrderMode(int mode){
        this.mode=mode;
    }

    public boolean isSelectRow(){
        return selectRow;
    }

    public int getOrderMode(){
        return this.mode;
    }

    private void buildLabel(int widthHeader, int heightHeader, JLabel label){
        label.setHorizontalAlignment(JLabel.LEFT);
        label.setPreferredSize(new Dimension(widthHeader, heightHeader));
        label.setMaximumSize(new Dimension(widthHeader, heightHeader));
        label.setMinimumSize(new Dimension(widthHeader, heightHeader));
    }

    @Override
    public void update(int... widthColumn) {
        for(TableRow row : rows){
            List<JLabel> columnsRow=row.getLabelsRow();
            row.setVisible(false);
            for(int i=0; i< columnsRow.size(); i++){
                JLabel column=columnsRow.get(i);
                column.setPreferredSize(new Dimension(widthColumn[i], column.getHeight()));
                column.revalidate();
                column.repaint();
            }
            row.setVisible(true);
        }
    }
}
