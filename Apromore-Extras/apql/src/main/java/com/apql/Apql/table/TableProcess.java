package com.apql.Apql.table;

import com.apql.Apql.controller.QueryController;
import com.apql.Apql.tree.DraggableNodeProcess;
import org.apromore.model.VersionSummaryType;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
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
    private SpringLayout layout;

    public TableProcess(){
        super();
        layout = new SpringLayout();
        setLayout(layout);
        setBackground(Color.WHITE);
        rows=new LinkedList<>();

    }

    public TableProcess(boolean selectRow){
        super();
        layout = new SpringLayout();
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

        this.add(scrollHeader);
        this.add(scrollRows);

        layout.putConstraint(SpringLayout.NORTH,scrollHeader,0,SpringLayout.NORTH,this);
        layout.putConstraint(SpringLayout.NORTH,scrollRows,0,SpringLayout.SOUTH,scrollHeader);

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
        if(dnp!=null)
            for (VersionSummaryType vst : dnp.getVersions()) {
                if (whiteBackground) {
                    System.out.println("WHITE");
                    row = new TableRow(this, widthHeader, heightHeader, Color.white.brighter(), dnp.getPathNode(), dnp.getId(), vst.getName(), vst.getVersionNumber(), vst.getLastUpdate());
                    whiteBackground = false;
                } else {
                    System.out.println("BLACK");
                    row = new TableRow(this, widthHeader, heightHeader, Color.lightGray, dnp.getPathNode(), dnp.getId(), vst.getName(), vst.getVersionNumber(), vst.getLastUpdate());
                    whiteBackground = true;
                }
                this.rows.add(row);
                panelRows.add(row);
            }
        this.remove(scrollRows);
        scrollRows = new JScrollPane(panelRows);

        this.add(scrollRows);

        layout.putConstraint(SpringLayout.NORTH,scrollRows,0,SpringLayout.SOUTH,scrollHeader);

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
            DraggableNodeProcess dnp = queryController.getLocation(id);
            System.out.println("DRAGGABLE: "+dnp+" "+idNets);
            st=new StringTokenizer(id,"/");
            if(whiteBackground){
                row=new TableRow(this,widthHeader,heightHeader,Color.white.brighter(),dnp.getPathNode(),null,dnp.getName(),st.nextToken(),dnp.getOriginalLanguage(),st.nextToken(),st.nextToken());
                whiteBackground=false;
            }else{
                row=new TableRow(this,widthHeader,heightHeader,Color.lightGray,dnp.getPathNode(),null,dnp.getName(),st.nextToken(),dnp.getOriginalLanguage(),st.nextToken(),st.nextToken());
                whiteBackground=true;
            }
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

            for (TableRow tr : rows) {
                if (white) {
                    tr.setBackground(Color.WHITE);
                    for(JLabel label : tr.getLabelsRow()){
                        label.setBackground(Color.WHITE);
                        label.revalidate();
                        label.repaint();
                    }
                    white = false;
                } else {
                    tr.setBackground(Color.LIGHT_GRAY);
                    for(JLabel label : tr.getLabelsRow()){
                        label.setBackground(Color.LIGHT_GRAY);
                        label.revalidate();
                        label.repaint();
                    }
                    white = true;
                }
                tr.revalidate();
                tr.repaint();
                panelRows.add(tr);
            }

        }else{
            Collections.reverse(rows);
            for (TableRow tr : rows) {
                if (white) {
                    tr.setBackground(Color.WHITE);
                    for(JLabel label : tr.getLabelsRow()){
                        label.setBackground(Color.WHITE);
                        label.revalidate();
                        label.repaint();
                    }
                    white = false;
                } else {
                    tr.setBackground(Color.LIGHT_GRAY);
                    for(JLabel label : tr.getLabelsRow()){
                        label.setBackground(Color.LIGHT_GRAY);
                        label.revalidate();
                        label.repaint();
                    }
                    white = true;
                }
                tr.revalidate();
                tr.repaint();
                panelRows.add(tr);
            }
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
