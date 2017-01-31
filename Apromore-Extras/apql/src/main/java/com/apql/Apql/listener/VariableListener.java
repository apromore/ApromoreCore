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
import com.apql.Apql.highlight.SquigglePainter;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;

public class VariableListener implements KeyListener {
    private JTextPane textPane;
    private QueryController controller;
    private SquigglePainter red = new SquigglePainter(Color.RED);
    private SquigglePainter blue = new SquigglePainter(Color.BLUE);
    private static final  String variableNamePattern = "([a-z]+[_]?[a-z0-9_]*|[_][a-z0-9_]*)";
    private static final  String variablesListPattern = "((\")[a-zA-Z0-9]+(\",))*[\"][a-zA-Z0-9]+[\"]";
    private LinkedList<String> lista;
    private HashSet<String> hash;

    public VariableListener() {
        this.controller = QueryController.getQueryController();
        this.textPane = controller.getVariablePane();
    }

    @Override
    public void keyPressed(KeyEvent arg0) {

    }

    @Override
    public void keyReleased(KeyEvent arg0) {
        lista = new LinkedList<String>();
        hash = new HashSet<String>();
    
        fullLists();
        
        int startPos = 0;
        int endPos = 0;
        textPane.getHighlighter().removeAllHighlights();//remove all precedent highlights from textPane
        
        
        for (int i = 0; i < lista.size(); i++) {
            String variable=lista.get(i);
            if (variable.equals(" ") || variable.equals("\n") || variable.equals("\t") || variable.equals("\b")) {
                startPos = endPos;
                endPos += variable.length();
                continue;
            }
            startPos = endPos;
            endPos += variable.length();

            Pattern pattern = Pattern.compile(variableNamePattern + "=[{]"
                            + variablesListPattern + "([}];)",
                    Pattern.UNICODE_CHARACTER_CLASS);
            Matcher matcher = pattern.matcher(variable);
            
            try {
                if (!matcher.find()) {
                    textPane.getHighlighter().addHighlight(startPos, endPos,
                            red);
                } else {
                    String name = variable.substring(0, variable.indexOf("="));
                    if (!hash.contains(name)) {
                        textPane.getHighlighter().addHighlight(startPos,
                                endPos - 1, blue);
                        continue;
                    }
                    hash.remove(name);
                }
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
        textPane.repaint();//apply the highlight change
    }

    @Override
    public void keyTyped(KeyEvent arg0) {

    }
    
    private void fullLists(){
        String text = textPane.getText();
        String result = "";
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) != 13)
                result += text.charAt(i);
        }
        StringTokenizer st = new StringTokenizer(result, "; \b\n\t", true);
        String var = "";
        String semicolon = "";
        while (st.hasMoreTokens()) {
            var = st.nextToken();
            if (var.equals(" ") || var.equals("\n") || var.equals("\t") || var.equals("\b")) {
                lista.addLast(var);
                continue;
            }

            if (st.hasMoreTokens())
                semicolon = st.nextToken();
            else
                semicolon = "";
            lista.addLast(var + semicolon);

            int startPos = text.indexOf(var);
            int endPos = (int) (text.indexOf(var) + var.length());
            int equalPos = var.indexOf("=") + startPos;

            String nameVar = null;
            if (equalPos > startPos) {
                nameVar = text.substring(startPos, equalPos);
                hash.add(nameVar);
            }
        }
    }

}