/*
 * Copyright © 2009-2016 The Apromore Initiative.
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

package com.apql.Apql.listener;

/**
 * Created by corno on 8/08/2014.
 */
import com.apql.Apql.table.HeaderLabel;
import com.apql.Apql.table.Observable;
import com.apql.Apql.table.TableProcess;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.Serializable;

public class HeaderMouseMotionListener implements MouseMotionListener,Observable, Serializable {

    @Override
    public void mouseDragged(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e) && e.getSource() instanceof JLabel && e.getX() > ((JLabel)e.getSource()).getWidth()-15) {

            HeaderLabel label=(HeaderLabel)e.getSource();
            Component[] sibling=label.getParent().getComponents();
            int widthLabel=label.getWidth();
            int mousePositionx=e.getX();
            int diffX = widthLabel - mousePositionx;
            if(mousePositionx > 10 && mousePositionx < 190){
                label.setPreferredSize(new Dimension(widthLabel - diffX, label.getHeight()));
                label.setMaximumSize(new Dimension(widthLabel - diffX, label.getHeight()));
                label.setMinimumSize(new Dimension(widthLabel - diffX, label.getHeight()));

                for(int i=0; i<sibling.length; i++){
                    if(sibling[i].equals(label) && (i<sibling.length-2 || i==0)){
                        sibling[i+1].setPreferredSize(new Dimension(sibling[i+1].getWidth() + diffX, label.getHeight()));
                        sibling[i+1].setMinimumSize(new Dimension(sibling[i+1].getWidth() + diffX, label.getHeight()));
                        sibling[i+1].setMaximumSize(new Dimension(sibling[i+1].getWidth() + diffX, label.getHeight()));
                        break;
                    }else if(sibling[i].equals(label) && i<sibling.length-1){
                        sibling[i+1].setPreferredSize(new Dimension(sibling[i+1].getWidth() + diffX, label.getHeight()));
                        sibling[i+1].setMinimumSize(new Dimension(sibling[i+1].getWidth() + diffX, label.getHeight()));
                        sibling[i+1].setMaximumSize(new Dimension(sibling[i+1].getWidth() + diffX, label.getHeight()));
                        break;
                    }
                }
            }
            int[] widthComponent=new int[sibling.length];
            for(int i=0; i<sibling.length; i++){
                JLabel l=(JLabel)sibling[i];
                widthComponent[i]=l.getWidth();
            }
            notifyObservers(label, widthComponent);
            label.revalidate();
            label.repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if(e.getSource() instanceof JLabel && (e.getX() > ((JLabel)e.getSource()).getWidth()-3 || e.getX() < 3)){
            JLabel label=(JLabel)e.getSource();
            Component[] sibling = label.getParent().getComponents();
            if(label.equals(sibling[sibling.length-1]))
                return;
            label.setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
        }else if(e.getSource() instanceof JLabel && (e.getX() < ((JLabel)e.getSource()).getWidth()-4 || e.getX() > 4)){
            JLabel label=(JLabel)e.getSource();
            label.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
    }

    @Override
    public void notifyObservers(Component parent,int... widthColumn) {
        Component tb=parent;
        while(tb!=null && !(tb instanceof TableProcess)){
            tb=tb.getParent();
        }
        TableProcess table=(TableProcess)tb;
        table.update(widthColumn);
    }
}
