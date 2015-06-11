package com.apql.Apql;

import com.apql.Apql.controller.QueryController;
import com.apql.Apql.controller.ViewController;
import com.apql.Apql.highlight.ButtonAction;
import com.apql.Apql.listener.VariableListener;

import java.awt.Dimension;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import javax.swing.text.Keymap;

public class VariableText extends JTextPane {

	private static final long serialVersionUID = 9125334227933777654L;

	public VariableText() {
		setPreferredSize(new Dimension(600, 210));
		QueryController.getQueryController().setVariablePane(this);
		addKeyListener(new VariableListener());
        Keymap parent = this.getKeymap();
        Keymap newmap = JTextComponent.addKeymap("KeymapExampleMap", parent);
        KeyStroke copy = KeyStroke
                .getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK);
        KeyStroke paste = KeyStroke
                .getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK);
        KeyStroke cut = KeyStroke
                .getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK);
        KeyStroke selectAll = KeyStroke
                .getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK);
        Action actionCopy=new ButtonAction.CTRLC(this);
        Action actionCut=new ButtonAction.CTRLX(this);
        Action actionPaste=new ButtonAction.CTRLV(this);
        Action actionSelect=new ButtonAction.CTRLA(this);
        newmap.addActionForKeyStroke(copy, actionCopy);
        newmap.addActionForKeyStroke(paste, actionPaste);
        newmap.addActionForKeyStroke(cut, actionCut);
        newmap.addActionForKeyStroke(selectAll, actionSelect);
        this.setKeymap(newmap);
		setVisible(true);
	}

}
