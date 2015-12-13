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

package com.apql.Apql.highlight;

/**
 * Created by corno on 11/07/2014.
 */
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import javax.swing.text.View;

public class SquigglePainter extends DefaultHighlighter.DefaultHighlightPainter {
    public SquigglePainter(Color color) {
        super(color);
    }

    /**
     * Paints a portion of a highlight.
     *
     * @param g
     *            the graphics context
     * @param offs0
     *            the starting model offset >= 0
     * @param offs1
     *            the ending model offset >= offs1
     * @param bounds
     *            the bounding box of the view, which is not necessarily the
     *            region to paint.
     * @param c
     *            the editor
     * @param view
     *            View painting for
     * @return region drawing occured in
     */
    public Shape paintLayer(Graphics g, int offs0, int offs1, Shape bounds,
                            JTextComponent c, View view) {
        Rectangle r = getDrawingArea(offs0, offs1, bounds, view);

        if (r == null)
            return null;

        // Do your custom painting

        Color color = getColor();
        g.setColor(color == null ? c.getSelectionColor() : color);

        // Draw the squiggles

        int squiggle = 2;
        int twoSquiggles = squiggle * 2;
        int y = r.y + r.height - squiggle;

        for (int x = r.x; x <= r.x + r.width - twoSquiggles; x += twoSquiggles) {
            g.drawArc(x, y, squiggle, squiggle, 0, 180);
            g.drawArc(x + squiggle, y, squiggle, squiggle, 180, 181);
        }

        // Return the drawing area

        return r;
    }

    private Rectangle getDrawingArea(int offs0, int offs1, Shape bounds,
                                     View view) {
        // Contained in view, can just use bounds.

        if (offs0 == view.getStartOffset() && offs1 == view.getEndOffset()) {
            Rectangle alloc;

            if (bounds instanceof Rectangle) {
                alloc = (Rectangle) bounds;
            } else {
                alloc = bounds.getBounds();
            }

            return alloc;
        } else {
            // Should only render part of View.
            try {
                // --- determine locations ---
                Shape shape = view.modelToView(offs0, Position.Bias.Forward,
                        offs1, Position.Bias.Backward, bounds);
                Rectangle r = (shape instanceof Rectangle) ? (Rectangle) shape
                        : shape.getBounds();

                return r;
            } catch (BadLocationException e) {
                // can't render
            }
        }

        // Can't render

        return null;
    }
}