package com.apql.Apql;

import com.apql.Apql.controller.QueryController;
import com.apql.Apql.controller.ViewController;
import com.apql.Apql.highlight.ButtonAction;
import com.apql.Apql.listener.WordListener;
import com.apql.Apql.popup.PopupFrame;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import javax.swing.text.Keymap;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

//import java.awt.dnd.*;

public class QueryText extends JTextPane {

    private static final long serialVersionUID = -7719436763743034599L;
    private QueryController queryController=QueryController.getQueryController();
    private ViewController viewController = ViewController.getController();
    public QueryText() {
        setPreferredSize(new Dimension(450, 210));
        QueryController.getQueryController().settextPane(this);
        addKeyListener(new WordListener());
        Keymap parent = this.getKeymap();
        Keymap newmap = JTextComponent.addKeymap("KeymapExampleMap", parent);

        KeyStroke suggest        = KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, InputEvent.CTRL_MASK);
        KeyStroke copy           = KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK);
        KeyStroke paste          = KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK);
        KeyStroke cut            = KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK);
        KeyStroke selectAll      = KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK);
        KeyStroke expandCollapse = KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SLASH, InputEvent.CTRL_MASK);
        KeyStroke undo           = KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK);
        KeyStroke redo           = KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_MASK);

        Action actionSuggest        = new ButtonAction.CTRLSpace();
        Action actionCopy           = new ButtonAction.CTRLC(this);
        Action actionCut            = new ButtonAction.CTRLX(this);
        Action actionPaste          = new ButtonAction.CTRLV(this);
        Action actionSelect         = new ButtonAction.CTRLA(this);
        Action actionExpandCollapse = new ButtonAction.CTRLBack();
        Action actionUndo           = new ButtonAction.CTRLZ();
        Action actionRedo           = new ButtonAction.CTRLY();

        newmap.addActionForKeyStroke(suggest, actionSuggest);
        newmap.addActionForKeyStroke(copy, actionCopy);
        newmap.addActionForKeyStroke(paste, actionPaste);
        newmap.addActionForKeyStroke(cut, actionCut);
        newmap.addActionForKeyStroke(selectAll, actionSelect);
        newmap.addActionForKeyStroke(expandCollapse, actionExpandCollapse);
        newmap.addActionForKeyStroke(undo, actionUndo);
        newmap.addActionForKeyStroke(redo, actionRedo);

        this.setKeymap(newmap);

        addMouseListener(new MouseListener() {

            public void mouseReleased(MouseEvent e) { }

            public void mousePressed(MouseEvent e) { }

            public void mouseExited(MouseEvent e) { }

            public void mouseEntered(MouseEvent e) { }

            public void mouseClicked(MouseEvent e) {
                int position=queryController.getTextPane().getCaretPosition();
                queryController.setCaretPosition(position);
                System.out.println("position: "+position);
                PopupFrame pf = ViewController.getController().getPopup();
                if (pf != null) {
                    ViewController.getController().setPopupOpen(false);
                    pf.setVisible(false);
                    pf = null;
                    ViewController.getController().setPopup(null);
                }
            }
        });
        setVisible(true);
    }
}
