package com.apql.Apql.highlight;

/**
 * Created by corno on 28/07/2014.
 */

import com.apql.Apql.popup.ProcessLabel;

import java.awt.Component;

import javax.swing.*;

public class ProcessListRenderer extends  DefaultListCellRenderer {
    protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
    private final ImageIcon folderImg = new ImageIcon(getClass().getResource("/icons/folder24.png"));
    private final ImageIcon modelImg = new ImageIcon(getClass().getResource("/icons/bpmn_22x22.png"));

    @Override
    public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean selected,
            boolean expanded) {

        ProcessLabel label=(ProcessLabel)value;
        return label;
    }
}
