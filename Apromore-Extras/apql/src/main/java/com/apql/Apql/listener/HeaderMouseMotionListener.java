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
