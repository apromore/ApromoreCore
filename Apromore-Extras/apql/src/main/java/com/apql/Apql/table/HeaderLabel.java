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

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

/**
 * Created by corno on 11/08/2014.
 */
public class HeaderLabel extends JLabel implements MouseListener{
    private String text;
    private int column;
    private TableProcess table;
    private Color startColor=Color.BLUE;
    private Color endColor=Color.WHITE;
    private ImageIcon sortup = new ImageIcon(getClass().getResource("/icons/sortup.png"));
    private ImageIcon sortdown = new ImageIcon(getClass().getResource("/icons/sortdown.png"));
    private List<JLabel> headers;

    public HeaderLabel(String text, int column,TableProcess table,List<JLabel> headers){
        super(text);
        this.text=text;
        this.column=column;
        this.table=table;
        this.headers=headers;
        setOpaque(true);
        setHorizontalAlignment(JLabel.LEFT);
        setBorder(BorderFactory.createLineBorder(new Color(192,192,192), 1));
        setFont(new Font("Arial", Font.BOLD, 12));
        addMouseListener(this);
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        table.sortTable(column);
        for(JLabel label : headers){
            if(label.equals(this)) {
                if (this.getIcon() == null) {
                    setIcon(sortup);
                } else if (this.getIcon().equals(sortup)) {
                    setIcon(sortdown);
                } else if (this.getIcon().equals(sortdown)) {
                    setIcon(sortup);
                }
            }else{
                label.setIcon(null);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
//        if(e.getX() < ((JLabel)e.getSource()).getWidth() - 3 )
//            ((JLabel)e.getSource()).setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    @Override
    public void mouseExited(MouseEvent e) {
        ((JLabel)e.getSource()).setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    @Override
    public void paint(Graphics g){
        int width = getWidth();
        int height = getHeight();

        GradientPaint paint = new GradientPaint( 0, 0, startColor, width, height, endColor, true );
        Graphics2D g2d = ( Graphics2D )g;
        Paint oldPaint = g2d.getPaint();
        g2d.setPaint( paint );
        g2d.fillRect( 0, 0, width, height );
        g2d.setPaint( oldPaint );

        super.paint( g );
    }
}
