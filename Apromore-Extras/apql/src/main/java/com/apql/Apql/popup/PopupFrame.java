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

package com.apql.Apql.popup;

import com.apql.Apql.controller.ContextController;
import com.apql.Apql.controller.QueryController;
import com.apql.Apql.controller.ViewController;
import com.apql.Apql.highlight.Keywords;
import com.apql.Apql.listener.PopupListener;


import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.LinkedList;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.StyledDocument;

public class PopupFrame extends JWindow implements WindowListener{

    private static final long serialVersionUID = -899598309445075428L;


    private JTextPane textPane;

    private QueryController queryController=QueryController.getQueryController();

    public PopupFrame() {
        this.textPane = queryController.getTextPane();

        setMinimumSize(new Dimension(200, 150));
        Point textPaneP = textPane.getLocationOnScreen();
        Point caretP = textPane.getCaret().getMagicCaretPosition();
        System.out.println("caretPFRAME: "+textPane.getCaretPosition());

        if (caretP == null) {
            Point position=queryController.getPopupPosition();
            if(position!=null)
                setLocation((int) (textPaneP.getX() + position.getX() + 5), (int) (textPaneP.getY() + position.getY() + 15));
            else
                setLocation((int) (textPaneP.getX()), (int) (textPaneP.getY() + 15));
        }else {
            queryController.setPopupPosition(caretP);
            setLocation((int) (textPaneP.getX() + caretP.getX() + 5), (int) (textPaneP.getY()
                    + caretP.getY() + 15));
        }

        Border raisedbevel = BorderFactory.createRaisedBevelBorder();
        Border loweredbevel = BorderFactory.createLoweredBevelBorder();
        Border compound = BorderFactory.createCompoundBorder(
                raisedbevel, loweredbevel);
        this.getRootPane().setBorder(compound);
        setAutoRequestFocus(false);

    }
//
//    private JScrollPane buildListModel(){
//
//        return null;
//    }

//    private String findWord() {
//        int caretPos = QueryController.getQueryController().getCaretPosition();
//        String text = textPane.getText();
//        String word = "";
//        StringBuilder res = new StringBuilder();
//        for (int i = 0; i < text.length(); i++) {
//            if (text.charAt(i) != 13)
//                res.append(text.charAt(i));
//        }
//        text = res.toString();
//        if (caretPos == 0 || text.charAt(caretPos - 1) == ' '
//                || text.charAt(caretPos - 1) == '\n'
//                || text.charAt(caretPos - 1) == '\t') {
//        } else {
//            String beforeCaret = text.substring(0, caretPos);
//            for (int i = caretPos - 1; i >= 0; i--) {
//                if (beforeCaret.charAt(i) == ' '
//                        || beforeCaret.charAt(i) == '\n'
//                        || beforeCaret.charAt(i) == '\t') {
//                    break;
//                } else {
//                    word = beforeCaret.charAt(i) + word;
//                }
//            }
//        }
//        return word;
//    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {

    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}
