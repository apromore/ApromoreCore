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

import com.apql.Apql.controller.QueryController;
import com.apql.Apql.controller.ViewController;
import com.apql.Apql.highlight.Highlight;
import com.apql.Apql.popup.FolderLabel;
import com.apql.Apql.popup.PopupFrame;
import com.apql.Apql.popup.PopupPanel;
import com.apql.Apql.popup.ProcessLabel;
import com.apql.Apql.tree.FolderProcessTree;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

/**
 * Created by corno on 29/07/2014.
 */
public class PopupListener implements MouseListener {
    private static final long serialVersionUID = 8029796883876262902L;
    private JTextPane textPane;
    private StyledDocument doc;
    private final SimpleAttributeSet keyWord = new SimpleAttributeSet();
    private final SimpleAttributeSet normalWord = new SimpleAttributeSet();
    private QueryController queryController = QueryController.getQueryController();
    private ViewController viewController = ViewController.getController();
    private PopupFrame popup;
    private String SEP;
    private PopupPanel panel;
    private JLabel lastChoose;

    public PopupListener(PopupPanel panel){
        this.textPane = queryController.getTextPane();
        this.doc = textPane.getStyledDocument();
        StyleConstants.setForeground(keyWord, Color.RED);
        StyleConstants.setBold(keyWord, true);
        StyleConstants.setForeground(normalWord, Color.BLACK);
        this.panel=panel;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        JLabel label=(JLabel)e.getSource();
        if(e.getClickCount() == 1){
            this.panel.setVisible(false);
            label.setBackground(new Color(171, 205, 239));
            if(lastChoose==null)
                lastChoose=label;
            else if(!lastChoose.equals(label)){
                lastChoose.setBackground(Color.white);
                lastChoose=label;
            }
            this.panel.repaint();
            this.panel.setVisible(true);
        }else if (e.getClickCount() == 2 && !e.isConsumed()) {
            e.consume();
            if(!(label instanceof ProcessLabel)&& !(label instanceof FolderLabel)){
                String itemSelected=label.getText().trim();
                addWordToText(itemSelected);

                popup = viewController.getPopup();
                popup.setVisible(false);
                viewController.setPopupOpen(false);
                viewController.setPopup(null);
            }else if(label instanceof FolderLabel){
                FolderLabel folderLabel=(FolderLabel)label;

                if(folderLabel.getText().equals("Home"))
                    addWordToText("\""+folderLabel.getText()+"/");
                else
                    addWordToText(folderLabel.getText()+"/");
                queryController.setCaretPosition(textPane.getCaretPosition());
                textPane.setCaretPosition(queryController.getCaretPosition());

                popup = viewController.getPopup();
                popup.setVisible(false);

                List<JLabel> results = ((FolderProcessTree)viewController.getFolderProcessTree()).getFoldersProcesses(folderLabel.getFolderId()+"");

                panel.removeResults();
                panel.setLayout(new GridLayout(results.size(), 1));
                panel.addResults(results);
                popup = new PopupFrame();
                if(results.size() < 5) {
                    popup.setPreferredSize(new Dimension(200, 40 * results.size()));
                    popup.setMinimumSize(new Dimension(200, 40 * results.size()));
                    popup.setMaximumSize(new Dimension(200, 40 * results.size()));
                }else{
                    popup.setPreferredSize(new Dimension(200, 150));
                    popup.setMinimumSize(new Dimension(200, 150));
                    popup.setMaximumSize(new Dimension(200, 150));
                }

                JScrollPane pane=new JScrollPane(panel);
                popup.add(pane);
                viewController.setPopup(popup);
//                popup.setLocation(textPane.getCaret().getMagicCaretPosition());
                popup.pack();
                popup.setVisible(true);

            }else if(label instanceof ProcessLabel){
                ProcessLabel process=(ProcessLabel)label;

                popup = viewController.getPopup();
                popup.setVisible(false);
                if(process.getNativeType()!=null) {
                    if (queryController.getVersion().equals(ViewController.ALLVERSIONS)) {
                        addWordToText(process.getName() + "{ALLVERSION}\"");
                    } else if (queryController.getVersion().equals(ViewController.LATESTVERSION)) {
                        addWordToText(process.getName() + "{LATESTVERSION}\"");
                    } else if (queryController.getVersion().equals(ViewController.CHOOSEVERSION)) {
                        addWordToText(process.getName() + "{LATESTVERSION}\"");
                    }
                }else{
                    addWordToText(process.getName()+"\"");
                }
                viewController.setPopupOpen(false);
                viewController.setPopup(null);
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

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    private void addWordToText(String itemSelected){
        int caretPosition = textPane.getCaretPosition();
        String text = textPane.getText();
        System.out.println("TEXT: "+text);
        String res = "";
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) != 13)
                res += text.charAt(i);
        }
        text = res;
        if (caretPosition == 0) {
            try {
                this.doc.insertString(0, itemSelected, keyWord);
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        } else {
            String beforeCaret = text.substring(0, caretPosition);

            int i = caretPosition - 1;
            for (; i >= 0; i--) {
                if (beforeCaret.charAt(i) == ' '
                        || beforeCaret.charAt(i) == '\n' || beforeCaret.charAt(i)=='/' || beforeCaret.charAt(i)=='"') {
                    break;
                }
            }
            if (i != 0) {
                beforeCaret = beforeCaret.substring(0, i + 1);
            }

            String afterCaret = text.substring(caretPosition);
            String result=null;
            result = beforeCaret + itemSelected + afterCaret;
            System.out.println("BEFORE: "+beforeCaret);
            System.out.println("item: "+itemSelected);
            System.out.println("AFTER: "+afterCaret);
            textPane.setText("");
            queryController.getQueueHistory().addHistory(result);
            Highlight.getHighlight().highlight(result);

        }


    }
}
