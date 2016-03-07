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

package com.apql.Apql;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.JFrame;

public class QueryFrame extends JFrame {

	private static final long serialVersionUID = 7476203503736619817L;

	public QueryFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension p = Toolkit.getDefaultToolkit().getScreenSize();
		int width = (int) (p.getWidth() - 700) / 2;
		int height = (int) (p.getHeight() - 500) / 2;
		setLocation(new Point(width, height));
		setMinimumSize(new Dimension(700, 500));
	}

}
