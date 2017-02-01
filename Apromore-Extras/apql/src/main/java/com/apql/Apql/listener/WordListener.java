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

package com.apql.Apql.listener;

import com.apql.Apql.highlight.ButtonAction;
import com.apql.Apql.popup.PopupFrame;
import com.apql.Apql.controller.ContextController;
import com.apql.Apql.controller.QueryController;
import com.apql.Apql.controller.ViewController;
import com.apql.Apql.highlight.Highlight;
import com.apql.Apql.highlight.Keywords;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


import javax.swing.*;
import javax.swing.text.*;

public class WordListener implements KeyListener {

	private JTextPane textPane;
	private StyledDocument doc;
	private final SimpleAttributeSet keyWord = new SimpleAttributeSet();
	private final SimpleAttributeSet normalWord = new SimpleAttributeSet();

	private QueryController queryController = QueryController.getQueryController();
    private ViewController viewController = ViewController.getController();

	public WordListener() {
		this.textPane = queryController.getTextPane();
		this.doc = textPane.getStyledDocument();
		StyleConstants.setForeground(keyWord, Color.RED);
		StyleConstants.setBold(keyWord, true);
		StyleConstants.setForeground(normalWord, Color.BLACK);

	}

	public void keyPressed(KeyEvent arg0) {
	}

	public void keyReleased(KeyEvent arg0) {
//        System.out.println("CARET POS: "+textPane.getCaretPosition()+" "+textPane.getText());
//        System.out.println(arg0.getKeyCode());
        queryController.setCaretPosition(textPane.getCaretPosition());
        if(queryController.keepLocationInQuery().isEmpty()){
            ButtonAction.CTRLBack.setExpand(true);
        }

        if (arg0.getKeyCode() == 8) {
		}
		if (arg0.getKeyCode() == 10) {
			//enter

		}
        if(arg0.isControlDown() && arg0.getKeyChar() != 'z'  && arg0.getKeyCode() == 90){

        }else if(arg0.isControlDown() && arg0.getKeyChar() != 'y'  && arg0.getKeyCode() == 89){

        }else if (arg0.getKeyCode() >= 37 && arg0.getKeyCode() <= 40) {
			//arrow

		} else if (arg0.getKeyCode() == 17 && !viewController.isPopupOpen()) {

		} else if (viewController.isPopupOpen()) {
            String word=findWord();
            PopupFrame popup = viewController.getPopup();
            popup.setVisible(false);
            if(!Keywords.contains(word)) {
                popup = new PopupFrame();
                JScrollPane pane = ContextController.getContextController().findContext();
                popup.setPreferredSize(new Dimension(200,(int)pane.getMinimumSize().getHeight()));
                popup.setMaximumSize(new Dimension(200,(int)pane.getMinimumSize().getHeight()));
                popup.setMinimumSize(new Dimension(200,(int)pane.getMinimumSize().getHeight()));
                popup.add(pane);
                viewController.setPopup(popup);
                popup.pack();
                popup.setVisible(true);
            }else{
                String str = textPane.getText();
                queryController.getQueueHistory().addHistory(str);
                Highlight.getHighlight().highlight(str);
                textPane.setCaretPosition(queryController.getCaretPosition());
                viewController.setPopupOpen(false);
            }
		} else {
            String str = textPane.getText();
            queryController.getQueueHistory().addHistory(str);
            Highlight.getHighlight().highlight(str);
            textPane.setCaretPosition(queryController.getCaretPosition());
		}
	}

	public void keyTyped(KeyEvent arg0) {

	}

    private String findWord() {
        int caretPos = QueryController.getQueryController().getCaretPosition();
        String text = textPane.getText();
        String word = "";
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) != 13) {
                res.append(text.charAt(i));
            }
        }
        text = res.toString();
        if (caretPos == 0 || text.charAt(caretPos - 1) == ' '
                || text.charAt(caretPos - 1) == '\n'
                || text.charAt(caretPos - 1) == '\t') {
        } else {
            String beforeCaret = text.substring(0, caretPos);
            for (int i = caretPos - 1; i >= 0; i--) {
                if (beforeCaret.charAt(i) == ' '
                        || beforeCaret.charAt(i) == '\n'
                        || beforeCaret.charAt(i) == '\t') {
                    break;
                } else {
                    word = beforeCaret.charAt(i) + word;
                }
            }
        }
        return word;
    }

}
