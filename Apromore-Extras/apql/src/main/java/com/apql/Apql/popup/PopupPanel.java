package com.apql.Apql.popup;

import com.apql.Apql.controller.ViewController;
import com.apql.Apql.listener.PopupListener;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Created by corno on 29/07/2014.
 */
public class PopupPanel extends JPanel {
    private int rows;
    private int columns;
    private GridLayout grid;
    private ViewController viewController = ViewController.getController();

    public PopupPanel(int rows, int columns){
        this.rows=rows;
        this.columns=columns;
        this.grid=new GridLayout(rows,1);
        setLayout(grid);
    }

    public void addResults(List<JLabel> results){
        PopupListener listener=new PopupListener(this);
        setVisible(false);
        grid=new GridLayout(results.size(),columns);
        setLayout(grid);
        for(JLabel result : results){
            result.addMouseListener(listener);
//            Box row=Box.createHorizontalBox();
//            row.setPreferredSize(new Dimension(200,30));
            result.setMinimumSize(new Dimension(200,30));
            result.setPreferredSize(new Dimension(200,30));
            result.setMaximumSize(new Dimension(200,30));
            result.setOpaque(true);
            result.setBackground(Color.WHITE);
//            row.add(result);
            add(result);
        }
        repaint();
        setVisible(true);
    }

    public void addResults(String[] results){
        PopupListener listener=new PopupListener(this);
        setVisible(false);
        grid=new GridLayout(results.length,columns);
        setLayout(grid);
        JLabel label;
        for(String result : results){
//            Box row=Box.createHorizontalBox();
//            row.setPreferredSize(new Dimension(200,30));
//            if(results.length > 1)
                label=new KeywordLabel(result, viewController.getImageIcon(ViewController.ICONKEY));
//            else
//                label = new JLabel(result);
            label.setMinimumSize(new Dimension(200,30));
            label.setPreferredSize(new Dimension(200,30));
            label.setMaximumSize(new Dimension(200,30));
            label.setOpaque(true);
            label.setBackground(Color.WHITE);
            label.setToolTipText(result);
            label.addMouseListener(listener);
//            row.add(label);
            add(label);
        }
        repaint();
        setVisible(true);
    }

    public int resultsNumber(){
        return rows;
    }

    public void removeResults(){
        for(Component c : getComponents())
            remove(c);
    }

}
